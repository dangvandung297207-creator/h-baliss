package com.herbalistscraft.registry;

import com.herbalistscraft.HerbalistsCraft;
import com.herbalistscraft.recipe.MillRecipe;
import com.herbalistscraft.recipe.MortarRecipe;
import com.herbalistscraft.recipe.TableRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The three bespoke recipe types. The vanilla brewing stand is untouched. */
public final class ModRecipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, HerbalistsCraft.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, HerbalistsCraft.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<MortarRecipe>> MORTAR_TYPE =
            RECIPE_TYPES.register("mortar", () -> RecipeType.simple(
                    ResourceLocation.fromNamespaceAndPath(HerbalistsCraft.MODID, "mortar")));
    public static final DeferredHolder<RecipeType<?>, RecipeType<MillRecipe>> MILL_TYPE =
            RECIPE_TYPES.register("mill", () -> RecipeType.simple(
                    ResourceLocation.fromNamespaceAndPath(HerbalistsCraft.MODID, "mill")));
    public static final DeferredHolder<RecipeType<?>, RecipeType<TableRecipe>> TABLE_TYPE =
            RECIPE_TYPES.register("table", () -> RecipeType.simple(
                    ResourceLocation.fromNamespaceAndPath(HerbalistsCraft.MODID, "table")));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MortarRecipe>> MORTAR_SERIALIZER =
            RECIPE_SERIALIZERS.register("mortar", MortarRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MillRecipe>> MILL_SERIALIZER =
            RECIPE_SERIALIZERS.register("mill", MillRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<TableRecipe>> TABLE_SERIALIZER =
            RECIPE_SERIALIZERS.register("table", TableRecipe.Serializer::new);

    private ModRecipes() {}
}
