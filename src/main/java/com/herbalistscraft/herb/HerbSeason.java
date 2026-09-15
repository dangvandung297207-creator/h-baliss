package com.herbalistscraft.herb;

import com.herbalistscraft.HerbalistsCraft;
import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

/** The four seasons, with the growth multipliers the design calls for. */
public enum HerbSeason implements StringRepresentable {
    SPRING(1.0F, 1.0F),
    SUMMER(0.85F, 0.9F),
    AUTUMN(1.15F, 1.1F),
    WINTER(0.35F, 0.4F);

    public static final Codec<HerbSeason> CODEC = StringRepresentable.fromEnum(HerbSeason::values);

    private final float growthMultiplier;
    private final float yieldMultiplier;

    HerbSeason(float growthMultiplier, float yieldMultiplier) {
        this.growthMultiplier = growthMultiplier;
        this.yieldMultiplier = yieldMultiplier;
    }

    public float growthMultiplier() {
        return growthMultiplier;
    }

    public float yieldMultiplier() {
        return yieldMultiplier;
    }

    public String translationKey() {
        return "season." + HerbalistsCraft.MODID + "." + name();
    }

    public Component displayName() {
        return Component.translatable(translationKey());
    }

    @Override
    public String getSerializedName() {
        return name();
    }
}
