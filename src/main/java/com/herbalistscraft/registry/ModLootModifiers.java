package com.herbalistscraft.registry;

import com.herbalistscraft.HerbalistsCraft;
import com.herbalistscraft.loot.HerbLootModifier;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/** One global loot modifier serializer, used by six modifier files to reach vanilla loot tables. */
public final class ModLootModifiers {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, HerbalistsCraft.MODID);

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<HerbLootModifier>> HERB_LOOT =
            LOOT_MODIFIERS.register("herb_loot", () -> HerbLootModifier.CODEC);

    private ModLootModifiers() {}
}
