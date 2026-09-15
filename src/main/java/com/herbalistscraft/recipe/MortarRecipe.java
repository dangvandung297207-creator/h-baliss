package com.herbalistscraft.recipe;

import com.herbalistscraft.registry.ModRecipes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

/**
 * Grinding a herb in the mortar: one ingredient in, one powder out. Works with a bare hand,
 * which is why the very first step of the ladder happens at the mortar and not at a crafting table.
 */
public class MortarRecipe implements Recipe<SingleRecipeInput> {
    private final Ingredient ingredient;
    private final ItemStack result;
    private final int grindTime;
    private final float quality;

    public MortarRecipe(Ingredient ingredient, ItemStack result, int grindTime, float quality) {
        this.ingredient = ingredient;
        this.result = result;
        this.grindTime = grindTime;
        this.quality = quality;
    }

    public Ingredient ingredient() {
        return ingredient;
    }

    public int grindTime() {
        return grindTime;
    }

    public float quality() {
        return quality;
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return ingredient.test(input.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
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
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(ingredient);
        return list;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.MORTAR_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.MORTAR_TYPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    /** Serializer: codec for datapack loading, stream codec for the client recipe sync. */
    public static class Serializer implements RecipeSerializer<MortarRecipe> {
        private static final MapCodec<MortarRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(MortarRecipe::ingredient),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                com.mojang.serialization.Codec.INT.optionalFieldOf("grind_time", 60).forGetter(MortarRecipe::grindTime),
                com.mojang.serialization.Codec.FLOAT.optionalFieldOf("quality", 1.0F).forGetter(MortarRecipe::quality)
        ).apply(instance, MortarRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, MortarRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, MortarRecipe::ingredient,
                ItemStack.STREAM_CODEC, recipe -> recipe.result,
                net.minecraft.network.codec.ByteBufCodecs.VAR_INT, MortarRecipe::grindTime,
                net.minecraft.network.codec.ByteBufCodecs.FLOAT, MortarRecipe::quality,
                MortarRecipe::new);

        @Override
        public MapCodec<MortarRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MortarRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    public static List<MortarRecipe> all(Level level) {
        return level.getRecipeManager().getAllRecipesFor(ModRecipes.MORTAR_TYPE.get());
    }

    public static MortarRecipe find(Level level, ItemStack stack) {
        for (MortarRecipe recipe : all(level)) {
            if (recipe.ingredient().test(stack)) {
                return recipe;
            }
        }
        return null;
    }
}
