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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/** Batch processing at the Herbal Mill: one main ingredient, an optional additive, a result. */
public class MillRecipe implements Recipe<MillRecipe.Input> {
    private final Ingredient ingredient;
    private final Optional<Ingredient> additive;
    private final ItemStack result;
    private final int processingTime;

    public MillRecipe(Ingredient ingredient, Optional<Ingredient> additive, ItemStack result, int processingTime) {
        this.ingredient = ingredient;
        this.additive = additive;
        this.result = result;
        this.processingTime = processingTime;
    }

    public Ingredient ingredient() {
        return ingredient;
    }

    public Optional<Ingredient> additive() {
        return additive;
    }

    public ItemStack result() {
        return result;
    }

    public int processingTime() {
        return processingTime;
    }

    @Override
    public boolean matches(Input input, Level level) {
        if (!ingredient.test(input.main())) {
            return false;
        }
        if (additive.isEmpty()) {
            return true;
        }
        return input.additive().map(additive.get()::test).orElse(false);
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
        return ModRecipes.MILL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.MILL_TYPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    /** Two-slot input: the herb and an optional additive. */
    public record Input(ItemStack main, Optional<ItemStack> additive) implements RecipeInput {
        @Override
        public ItemStack getItem(int index) {
            return index == 0 ? main : additive.orElse(ItemStack.EMPTY);
        }

        @Override
        public int size() {
            return 2;
        }

        @Override
        public boolean isEmpty() {
            return main.isEmpty();
        }
    }

    public static class Serializer implements RecipeSerializer<MillRecipe> {
        private static final MapCodec<MillRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(MillRecipe::ingredient),
                Ingredient.CODEC.optionalFieldOf("additive").forGetter(MillRecipe::additive),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(MillRecipe::result),
                Codec.INT.optionalFieldOf("processing_time", 200).forGetter(MillRecipe::processingTime)
        ).apply(instance, MillRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, MillRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, MillRecipe::ingredient,
                ByteBufCodecs.optional(Ingredient.CONTENTS_STREAM_CODEC), MillRecipe::additive,
                ItemStack.STREAM_CODEC, MillRecipe::result,
                ByteBufCodecs.VAR_INT, MillRecipe::processingTime,
                MillRecipe::new);

        @Override
        public MapCodec<MillRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MillRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    public static List<MillRecipe> all(Level level) {
        return level.getRecipeManager().getAllRecipesFor(ModRecipes.MILL_TYPE.get())
                .stream()
                .map(net.minecraft.world.item.crafting.RecipeHolder::value)
                .toList();
    }

    public static MillRecipe find(Level level, ItemStack main, ItemStack additive) {
        Input input = new Input(main, additive.isEmpty() ? Optional.empty() : Optional.of(additive));
        for (MillRecipe recipe : all(level)) {
            if (recipe.matches(input, level)) {
                return recipe;
            }
        }
        return null;
    }
}
