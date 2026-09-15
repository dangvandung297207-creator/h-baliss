package com.herbalistscraft.herb;

import com.herbalistscraft.HerbalistsCraft;
import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

/** How hard a herb is to find, and how long it takes to grow. */
public enum HerbRarity implements StringRepresentable {
    COMMON(1.0F, 1.0F),
    UNCOMMON(1.25F, 0.75F),
    RARE(1.6F, 0.55F),
    VERY_RARE(2.1F, 0.35F);

    public static final Codec<HerbRarity> CODEC = StringRepresentable.fromEnum(HerbRarity::values);

    private final float growthPenalty;
    private final float spawnWeight;

    HerbRarity(float growthPenalty, float spawnWeight) {
        this.growthPenalty = growthPenalty;
        this.spawnWeight = spawnWeight;
    }

    /** Rarer herbs take longer per growth stage. */
    public float growthPenalty() {
        return growthPenalty;
    }

    public float spawnWeight() {
        return spawnWeight;
    }

    @Override
    public String getSerializedName() {
        return name();
    }

    public String translationKey() {
        return "rarity." + HerbalistsCraft.MODID + "." + name();
    }

    public Component displayName() {
        return Component.translatable(translationKey());
    }

    public String badgeIcon() {
        return "rarity_" + getSerializedName();
    }
}
