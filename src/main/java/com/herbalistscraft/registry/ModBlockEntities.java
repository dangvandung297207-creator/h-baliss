package com.herbalistscraft.registry;

import com.herbalistscraft.HerbalistsCraft;
import com.herbalistscraft.block.entity.DryingRackBlockEntity;
import com.herbalistscraft.block.entity.HerbalMillBlockEntity;
import com.herbalistscraft.block.entity.HerbalistTableBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Block entities for the three workstations that hold state. */
public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, HerbalistsCraft.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HerbalMillBlockEntity>> HERBAL_MILL =
            BLOCK_ENTITIES.register("herbal_mill", () -> BlockEntityType.Builder
                    .of(HerbalMillBlockEntity::new, ModBlocks.HERBAL_MILL.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HerbalistTableBlockEntity>> HERBALISTS_TABLE =
            BLOCK_ENTITIES.register("herbalists_table", () -> BlockEntityType.Builder
                    .of(HerbalistTableBlockEntity::new, ModBlocks.HERBALISTS_TABLE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DryingRackBlockEntity>> DRYING_RACK =
            BLOCK_ENTITIES.register("drying_rack", () -> BlockEntityType.Builder
                    .of(DryingRackBlockEntity::new, ModBlocks.DRYING_RACK.get()).build(null));

    private ModBlockEntities() {}
}
