package com.herbalistscraft.medicine;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

/** The vocabulary the mixing calculator and the medicine files speak. */
public enum EffectType implements StringRepresentable {
    /** A vanilla mob effect, by id. */
    VANILLA,
    /** One of the mod's own effects, by name. */
    HERBAL,
    /** Instant healing, in half hearts. */
    INSTANT_HEALTH,
    /** Positive amount feeds, negative amount starves. */
    HUNGER,
    /** Hydration, meaningful when Tough As Nails is installed. */
    HYDRATE,
    /** Warms (positive warmth) or chills (positive chill) the body. */
    TEMP,
    /** Blunts the next temperature swing, in degrees. */
    TEMPERATURE_RELIEF,
    /** Adds toxin points to the accumulated load. */
    TOXIN,
    /** Flushes toxin points. */
    TOXIN_CLEAR,
    /** Stops bleeding. */
    CLEAR_BLEEDING,
    /** Removes the listed vanilla effects. */
    CURE,
    /** Puts the player out if they are burning. */
    EXTINGUISH;

    public static final Codec<EffectType> CODEC = StringRepresentable.fromEnum(EffectType::values);

    @Override
    public String getSerializedName() {
        return name().toLowerCase(java.util.Locale.ROOT);
    }
}
