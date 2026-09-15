package com.herbalistscraft.mixing;

import com.herbalistscraft.herb.HerbProperty;
import com.herbalistscraft.medicine.EffectType;
import com.herbalistscraft.medicine.MedicineEffect;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

/**
 * The bridge from herb properties to the effects a drink has. Used for experimental mixtures,
 * where no hand-written recipe exists but the properties still have to mean something.
 */
public final class PropertyEffects {
    private PropertyEffects() {}

    private static ResourceLocation vanilla(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }

    public static List<MedicineEffect> effects(Map<HerbProperty, Float> properties, float potency) {
        List<MedicineEffect> effects = new ArrayList<>();
        for (Map.Entry<HerbProperty, Float> entry : properties.entrySet()) {
            float scale = Math.max(0.0F, entry.getValue()) * Math.max(0.05F, potency);
            if (scale <= 0.0F) {
                continue;
            }
            switch (entry.getKey()) {
                case HEALING -> effects.add(MedicineEffect.instant(EffectType.INSTANT_HEALTH,
                        Math.min(12.0F, 4.0F * scale)));
                case REGENERATION -> {
                    effects.add(MedicineEffect.vanilla(vanilla("regeneration"), (int) (120 * scale), 0, 1.0F));
                    effects.add(MedicineEffect.herbal("vitality", (int) (200 * scale), 0));
                }
                case HEMOSTATIC -> effects.add(MedicineEffect.flag(EffectType.CLEAR_BLEEDING));
                case COOLING -> {
                    effects.add(MedicineEffect.temperature(0.0F, scale));
                    effects.add(MedicineEffect.herbal("cold_ward", (int) (600 * scale), 0));
                }
                case WARMING -> {
                    effects.add(MedicineEffect.temperature(scale, 0.0F));
                    effects.add(MedicineEffect.herbal("heat_ward", (int) (600 * scale), 0));
                }
                case ANTISEPTIC -> effects.add(MedicineEffect.herbal("antiseptic", (int) (400 * scale), 0));
                case STIMULANT -> {
                    effects.add(MedicineEffect.vanilla(vanilla("speed"), (int) (400 * scale), 0, 1.0F));
                    effects.add(MedicineEffect.herbal("vigor", (int) (200 * scale), 0));
                }
                case SEDATIVE -> effects.add(MedicineEffect.herbal("sedation", (int) (400 * scale), 0));
                case TOXIC -> effects.add(MedicineEffect.toxin(8.0F * scale));
                case FIRE_PROTECTION -> effects.add(MedicineEffect.herbal("fire_ward", (int) (400 * scale), 0));
                case COLD_PROTECTION -> effects.add(MedicineEffect.herbal("cold_ward", (int) (1200 * scale), 0));
                case HYDRATION -> effects.add(MedicineEffect.instant(EffectType.HYDRATE, 4.0F * scale));
                case ENERGY -> {
                    effects.add(MedicineEffect.instant(EffectType.HUNGER, -4.0F * scale));
                    effects.add(MedicineEffect.vanilla(vanilla("speed"), (int) (300 * scale), 0, 1.0F));
                }
                case RESPIRATORY -> {
                    effects.add(MedicineEffect.herbal("respiratory", (int) (400 * scale), 0));
                    effects.add(MedicineEffect.cure(List.of(vanilla("poison"))));
                }
            }
        }
        return effects;
    }

    /** The one-line flavour the table shows for a property, in the order the properties are declared. */
    public static String summary(Map<HerbProperty, Float> properties) {
        if (properties.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (HerbProperty property : HerbProperty.VALUES) {
            Float value = properties.get(property);
            if (value == null || value <= 0.0F) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(" + ");
            }
            builder.append(property.displayName().getString());
        }
        return builder.toString();
    }
}
