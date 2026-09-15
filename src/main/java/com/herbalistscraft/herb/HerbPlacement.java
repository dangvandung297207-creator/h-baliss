package com.herbalistscraft.herb;

import com.herbalistscraft.HerbalistsCraft;
import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

/** Where in the world a herb generates; mirrors the placement styles in the worldgen tables. */
public enum HerbPlacement implements StringRepresentable {
    SURFACE,
    WETLAND,
    SHADED,
    UNDERGROUND,
    HIGH_ELEVATION,
    NETHER;

    public static final Codec<HerbPlacement> CODEC = StringRepresentable.fromEnum(HerbPlacement::values);

    @Override
    public String getSerializedName() {
        return name();
    }

    public String translationKey() {
        return "placement." + HerbalistsCraft.MODID + "." + name();
    }

    public Component displayName() {
        return Component.translatable(translationKey());
    }
}
