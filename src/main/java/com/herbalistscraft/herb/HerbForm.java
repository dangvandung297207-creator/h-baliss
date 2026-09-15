package com.herbalistscraft.herb;

import com.herbalistscraft.HerbalistsCraft;
import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

/** The processing forms a herb can exist in. Fresh is strongest but spoils; dried keeps for ages. */
public enum HerbForm implements StringRepresentable {
    FRESH("fresh", 1.0F, 64),
    SEED("seed", 1.0F, 64),
    DRIED("dried", 0.8F, 64),
    POWDER("powder", 1.0F, 64),
    EXTRACT("extract", 1.5F, 16);

    public static final Codec<HerbForm> CODEC = StringRepresentable.fromEnum(HerbForm::values);

    private final String name;
    private final float potency;
    private final int stackSize;

    HerbForm(String name, float potency, int stackSize) {
        this.name = name;
        this.potency = potency;
        this.stackSize = stackSize;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    /** Potency multiplier applied when the herb is worked into a medicine. */
    public float potency() {
        return potency;
    }

    public int stackSize() {
        return stackSize;
    }

    /** True when this form keeps its strength forever instead of carrying a freshness stamp. */
    public boolean isStable() {
        return this == DRIED || this == POWDER || this == EXTRACT;
    }

    public String translationKey() {
        return "form." + HerbalistsCraft.MODID + "." + name;
    }

    public Component displayName() {
        return Component.translatable(translationKey());
    }
}
