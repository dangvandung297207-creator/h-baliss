package com.herbalistscraft.herb;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

/** Shape of the plant; drives the silhouette of the sprite and the crop stages. */
public enum HerbMorphology implements StringRepresentable {
    ROOT,
    LEAF,
    FLOWER,
    GRASS,
    BERRY,
    MUSHROOM,
    BARK,
    VINE,
    BULB,
    MOSS;

    public static final Codec<HerbMorphology> CODEC = StringRepresentable.fromEnum(HerbMorphology::values);

    @Override
    public String getSerializedName() {
        return name();
    }
}
