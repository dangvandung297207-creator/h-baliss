package com.herbalistscraft.herb;

import com.herbalistscraft.HerbalistsCraft;
import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

/** Broad families used for creative tab ordering, journal grouping and palette families. */
public enum HerbCategory implements StringRepresentable {
    HEALING,
    COOLING,
    WARMING,
    TOXIC,
    MEDICINAL,
    STIMULANT,
    SEDATIVE,
    PROTECTIVE;

    public static final Codec<HerbCategory> CODEC = StringRepresentable.fromEnum(HerbCategory::values);
    public static final HerbCategory[] VALUES = values();

    @Override
    public String getSerializedName() {
        return name();
    }

    public String translationKey() {
        return "category." + HerbalistsCraft.MODID + "." + name();
    }

    public Component displayName() {
        return Component.translatable(translationKey());
    }
}
