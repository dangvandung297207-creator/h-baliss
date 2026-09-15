package com.herbalistscraft.registry;

import com.herbalistscraft.HerbalistsCraft;
import com.herbalistscraft.medicine.CoatingData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Item data components: harvest day and weapon coating. */
public final class ModDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, HerbalistsCraft.MODID);

    /** Game day the stack was harvested on; fresh herbs lose potency as the days pass. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> HARVEST_DAY =
            DATA_COMPONENTS.registerComponentType("harvest_day",
                    builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT)
                            .networkSynchronized(ByteBufCodecs.VAR_INT));

    /** The oil smeared onto a weapon, with the hits it has left. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CoatingData>> COATING =
            DATA_COMPONENTS.registerComponentType("coating",
                    builder -> builder.persistent(CoatingData.CODEC)
                            .networkSynchronized(CoatingData.STREAM_CODEC));

    /** What an unnamed experimental brew contains, computed at the table. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<
            com.herbalistscraft.mixing.MixtureData>> MIXTURE =
            DATA_COMPONENTS.registerComponentType("mixture",
                    builder -> builder.persistent(com.herbalistscraft.mixing.MixtureData.CODEC)
                            .networkSynchronized(com.herbalistscraft.mixing.MixtureData.STREAM_CODEC));

    private ModDataComponents() {}

}
