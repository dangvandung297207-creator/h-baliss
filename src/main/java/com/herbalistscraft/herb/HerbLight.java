package com.herbalistscraft.herb;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

/** Light level a herb needs; the crop enforces it in its tick. */
public enum HerbLight implements StringRepresentable {
    DARK(0, 6),
    DIM(4, 11),
    BRIGHT(9, 15);

    public static final Codec<HerbLight> CODEC = StringRepresentable.fromEnum(HerbLight::values);

    private final int min;
    private final int max;

    HerbLight(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int min() {
        return min;
    }

    public int max() {
        return max;
    }

    public boolean accepts(int light) {
        return light >= min && light <= max;
    }

    @Override
    public String getSerializedName() {
        return name();
    }
}
