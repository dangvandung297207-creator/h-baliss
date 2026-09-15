package com.herbalistscraft.mixing;

import com.herbalistscraft.herb.HerbProperty;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/** What a base, a herb, an extract and a catalyst compute to at the Herbalist's Table. */
public record MixingResult(
        ResourceLocation output,
        String outputName,
        float potency,
        Map<HerbProperty, Float> properties,
        boolean known,
        boolean failure,
        boolean experimental,
        List<Component> lines) {

    public MixingResult {
        properties = Map.copyOf(properties);
    }

    public static MixingResult failure(List<Component> lines) {
        return new MixingResult(ResourceLocation.fromNamespaceAndPath("herbalistscraft", "failed_mixture"),
                "failed_mixture", 0.0F, new EnumMap<>(HerbProperty.class), false, true, false, lines);
    }

    public static MixingResult experimental(List<Component> lines) {
        return new MixingResult(ResourceLocation.fromNamespaceAndPath("herbalistscraft", "experimental_tonic"),
                "experimental_tonic", 0.5F, new EnumMap<>(HerbProperty.class), false, false, true, lines);
    }
}
