package com.herbalistscraft.recipe;

import com.herbalistscraft.registry.ModRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * The Herbalist's Table: base + herb + optional extract + optional catalyst becomes a medicine.
 * Everything the table needs is in the recipe file, so new medicines are pure data.
 */
public class TableRecipe implements Recipe<TableRecipe.Input> {
    private final Ingredient base;
    private final Ingredient herb;
    private final Optional<Ingredient> extract;
    private final Optional<Ingredient> catalyst;
    private final ItemStack result;
    private final int brewTime;
    private final Optional<ResourceLocation> medicine;

    public TableRecipe(Ingredient base, Ingredient herb, Optional<Ingredient> extract, Optional<Ingredient> catalyst,
                       ItemStack result, int brewTime, Optional<ResourceLocation> medicine) {
        this.base = base;
        this.herb = herb;
        this.extract = extract;
        this.catalyst = catalyst;
        this.result = result;
        this.brewTime = brewTime;
        this.medicine = medicine;
    }

    public Ingredient base() {
        return base;
    }

    public Ingredient herb() {
        return herb;
    }

    public Optional<Ingredient> extract() {
        return extract;
    }

    public Optional<Ingredient> catalyst() {
        return catalyst;
    }

    public int brewTime() {
        return brewTime;
    }

    public Optional<ResourceLocation> medicine() {
        return medicine;
    }

    @Override
    public boolean matches(Input input, Level level) {
        if (!base.test(input.base()) || !herb.test(input.herb())) {
            return false;
        }
        if (extract.isPresent() && !input.extract().map(extract.get()::test).orElse(false)) {
            return false;
        }
        if (extract.isEmpty() && input.extract().isPresent()) {
            return false;
        }
        if (catalyst.isPresent() && !input.catalyst().map(catalyst.get()::test).orElse(false)) {
            return false;
        }
        return catalyst.isPresent() || input.catalyst().isEmpty();
    }

    @Override
    public ItemStack assemble(Input input, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.TABLE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.TABLE_TYPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    /** The five-slot table input. */
    public record Input(ItemStack base, ItemStack herb, Optional<ItemStack> extract, Optional<ItemStack> catalyst)
            implements RecipeInput {
        @Override
        public ItemStack getItem(int index) {
            return switch (index) {
                case 0 -> base;
                case 1 -> herb;
                case 2 -> extract.orElse(ItemStack.EMPTY);
                case 3 -> catalyst.orElse(ItemStack.EMPTY);
                default -> ItemStack.EMPTY;
            };
        }

        @Override
        public int size() {
            return 4;
        }

        @Override
        public boolean isEmpty() {
            return base.isEmpty() && herb.isEmpty();
        }
    }

    public static class Serializer implements RecipeSerializer<TableRecipe> {
        private static final MapCodec<TableRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("base").forGetter(TableRecipe::base),
                Ingredient.CODEC_NONEMPTY.fieldOf("herb").forGetter(TableRecipe::herb),
                Ingredient.CODEC.optionalFieldOf("extract").forGetter(TableRecipe::extract),
                Ingredient.CODEC.optionalFieldOf("catalyst").forGetter(TableRecipe::catalyst),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                Codec.INT.optionalFieldOf("brew_time", 200).forGetter(TableRecipe::brewTime),
                ResourceLocation.CODEC.optionalFieldOf("medicine").forGetter(TableRecipe::medicine)
        ).apply(instance, TableRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, TableRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, TableRecipe::base,
                Ingredient.CONTENTS_STREAM_CODEC, TableRecipe::herb,
                Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC, TableRecipe::extract,
                Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC, TableRecipe::catalyst,
                ItemStack.STREAM_CODEC, recipe -> recipe.result,
                ByteBufCodecs.VAR_INT, TableRecipe::brewTime,
                TableRecipe::new);

        @Override
        public MapCodec<TableRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TableRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    public static List<TableRecipe> all(Level level) {
        return level.getRecipeManager().getAllRecipesFor(ModRecipes.TABLE_TYPE.get());
    }

    public static TableRecipe find(Level level, ItemStack base, ItemStack herb, ItemStack extract, ItemStack catalyst) {
        Input input = new Input(base, herb, optional(extract), optional(catalyst));
        for (TableRecipe recipe : all(level)) {
            if (recipe.matches(input, level)) {
                return recipe;
            }
        }
        return null;
    }

    private static Optional<ItemStack> optional(ItemStack stack) {
        return stack.isEmpty() ? Optional.empty() : Optional.of(stack);
    }
}
