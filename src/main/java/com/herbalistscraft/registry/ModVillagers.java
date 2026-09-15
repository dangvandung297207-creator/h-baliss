package com.herbalistscraft.registry;

import com.google.common.collect.ImmutableSet;
import com.herbalistscraft.HerbalistsCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The Herbalist profession and the workstation that anchors it. */
public final class ModVillagers {
    public static final DeferredRegister<PoiType> POI_TYPES =
            DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, HerbalistsCraft.MODID);
    public static final DeferredRegister<VillagerProfession> PROFESSIONS =
            DeferredRegister.create(Registries.VILLAGER_PROFESSION, HerbalistsCraft.MODID);

    public static final DeferredHolder<PoiType, PoiType> HERBALIST_TABLE = POI_TYPES.register("herbalist_table",
            () -> new PoiType(ImmutableSet.copyOf(ModBlocks.HERBALISTS_TABLE.get().getStateDefinition().getPossibleStates()),
                    1, 1));

    public static final DeferredHolder<VillagerProfession, VillagerProfession> HERBALIST = PROFESSIONS.register(
            "herbalist", () -> new VillagerProfession("herbalist",
                    poi -> poi.is(HERBALIST_TABLE.getKey()),
                    ImmutableSet.<net.minecraft.world.item.Item>of(),
                    ImmutableSet.<net.minecraft.world.level.block.Block>of(ModBlocks.HERBALISTS_TABLE.get()),
                    ModSounds.MILL.get()));

    private ModVillagers() {}
}
