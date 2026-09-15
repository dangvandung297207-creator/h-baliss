package com.herbalistscraft.medicine;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

/**
 * One thing a medicine does. The same record covers instant numbers, timed vanilla effects,
 * the mod's own effects, temperature and hydration nudges and cures.
 */
public record MedicineEffect(
        EffectType type,
        Optional<ResourceLocation> effect,
        int duration,
        int amplifier,
        float amount,
        float chance,
        float warmth,
        float chill,
        List<ResourceLocation> targets) {

    public static final Codec<MedicineEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EffectType.CODEC.optionalFieldOf("type", EffectType.VANILLA).forGetter(MedicineEffect::type),
            ResourceLocation.CODEC.optionalFieldOf("effect").forGetter(MedicineEffect::effect),
            Codec.INT.optionalFieldOf("duration", 0).forGetter(MedicineEffect::duration),
            Codec.INT.optionalFieldOf("amplifier", 0).forGetter(MedicineEffect::amplifier),
            Codec.FLOAT.optionalFieldOf("amount", 0.0F).forGetter(MedicineEffect::amount),
            Codec.floatRange(0.0F, 1.0F).optionalFieldOf("chance", 1.0F).forGetter(MedicineEffect::chance),
            Codec.FLOAT.optionalFieldOf("warmth", 0.0F).forGetter(MedicineEffect::warmth),
            Codec.FLOAT.optionalFieldOf("chill", 0.0F).forGetter(MedicineEffect::chill),
            ResourceLocation.CODEC.listOf().optionalFieldOf("targets", List.of()).forGetter(MedicineEffect::targets)
    ).apply(instance, MedicineEffect::new));

    public static MedicineEffect instant(EffectType type, float amount) {
        return new MedicineEffect(type, Optional.empty(), 0, 0, amount, 1.0F, 0.0F, 0.0F, List.of());
    }

    public static MedicineEffect vanilla(ResourceLocation effect, int duration, int amplifier, float chance) {
        return new MedicineEffect(EffectType.VANILLA, Optional.of(effect), duration, amplifier, 0.0F,
                chance, 0.0F, 0.0F, List.of());
    }

    public static MedicineEffect herbal(String effect, int duration, int amplifier) {
        return new MedicineEffect(EffectType.HERBAL,
                Optional.of(ResourceLocation.fromNamespaceAndPath("herbalistscraft", effect)), duration, amplifier,
                0.0F, 1.0F, 0.0F, 0.0F, List.of());
    }

    public static MedicineEffect temperature(float warmth, float chill) {
        return new MedicineEffect(EffectType.TEMP, Optional.empty(), 0, 0, 0.0F, 1.0F, warmth, chill, List.of());
    }

    public static MedicineEffect toxin(float amount) {
        return new MedicineEffect(EffectType.TOXIN, Optional.empty(), 0, 0, amount, 1.0F, 0.0F, 0.0F, List.of());
    }

    public static MedicineEffect toxinClear(float amount) {
        return new MedicineEffect(EffectType.TOXIN_CLEAR, Optional.empty(), 0, 0, amount, 1.0F, 0.0F, 0.0F, List.of());
    }

    public static MedicineEffect cure(List<ResourceLocation> targets) {
        return new MedicineEffect(EffectType.CURE, Optional.empty(), 0, 0, 0.0F, 1.0F, 0.0F, 0.0F, targets);
    }

    public static MedicineEffect flag(EffectType type) {
        return new MedicineEffect(type, Optional.empty(), 0, 0, 0.0F, 1.0F, 0.0F, 0.0F, List.of());
    }
}
