package com.herbalistscraft.herb;

import com.herbalistscraft.HerbalistsCraft;
import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

/** Toxicity ladder used by herbs, medicines and the accumulated toxin load. */
public enum Toxicity implements StringRepresentable {
    NONE(0, "none"),
    LOW(1, "low"),
    MEDIUM(2, "medium"),
    HIGH(3, "high"),
    EXTREME(4, "extreme");

    public static final Codec<Toxicity> CODEC = StringRepresentable.fromEnum(Toxicity::values);

    private final int severity;
    private final String key;

    Toxicity(int severity, String key) {
        this.severity = severity;
        this.key = key;
    }

    public int severity() {
        return severity;
    }

    public String translationKey() {
        return "toxicity." + HerbalistsCraft.MODID + "." + name();
    }

    public Component displayName() {
        return Component.translatable(translationKey());
    }

    @Override
    public String getSerializedName() {
        return key;
    }

    public static Toxicity bySeverity(int severity) {
        for (Toxicity value : values()) {
            if (value.severity == severity) {
                return value;
            }
        }
        return EXTREME;
    }
}
