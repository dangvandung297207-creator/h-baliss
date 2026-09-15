package com.herbalistscraft.herb;

import com.herbalistscraft.HerbalistsCraft;
import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

/** The fourteen medicinal properties a herb can carry. */
public enum HerbProperty implements StringRepresentable {
    HEALING("healing", "#d94a4a"),
    HEMOSTATIC("hemostatic", "#a3342f"),
    COOLING("cooling", "#63a8c9"),
    WARMING("warming", "#d9813f"),
    ANTISEPTIC("antiseptic", "#7fc7a0"),
    STIMULANT("stimulant", "#e0b23f"),
    SEDATIVE("sedative", "#8b7fc7"),
    TOXIC("toxic", "#6d8a3a"),
    FIRE_PROTECTION("fire_protection", "#e06a2a"),
    COLD_PROTECTION("cold_protection", "#8fd2e8"),
    HYDRATION("hydration", "#4f9fd9"),
    ENERGY("energy", "#e8d24a"),
    RESPIRATORY("respiratory", "#9fd8d8"),
    REGENERATION("regeneration", "#e07ab0");

    public static final Codec<HerbProperty> CODEC = StringRepresentable.fromEnum(HerbProperty::values);
    public static final HerbProperty[] VALUES = values();

    private final String name;
    private final int color;

    HerbProperty(String name, String color) {
        this.name = name;
        this.color = 0xFF000000 | Integer.parseInt(color.substring(1), 16);
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    /** ARGB colour used by the journal, the tooltips and the property icons. */
    public int color() {
        return color;
    }

    public String translationKey() {
        return "property." + HerbalistsCraft.MODID + "." + name();
    }

    public Component displayName() {
        return Component.translatable(translationKey());
    }

    public Component hint() {
        return Component.translatable(translationKey() + ".hint");
    }

    /** Icon file stem, matching the generated textures/gui/icons/property_&lt;icon&gt;.png files. */
    public String icon() {
        return name;
    }

    /** Lookup by enum name, used when reading computed mixtures back from a data component. */
    public static java.util.Optional<HerbProperty> byName(String name) {
        for (HerbProperty property : VALUES) {
            if (property.name().equals(name)) {
                return java.util.Optional.of(property);
            }
        }
        return java.util.Optional.empty();
    }
}
