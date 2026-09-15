package com.herbalistscraft.registry;

import com.herbalistscraft.HerbalistsCraft;
import com.herbalistscraft.world.HerbPatchFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The single worldgen feature that plants wild herb patches. */
public final class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, HerbalistsCraft.MODID);

    public static final DeferredHolder<Feature<?>, HerbPatchFeature> HERB_PATCH =
            FEATURES.register("herb_patch", HerbPatchFeature::new);

    private ModFeatures() {}
}
