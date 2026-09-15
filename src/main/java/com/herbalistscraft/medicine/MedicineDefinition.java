package com.herbalistscraft.medicine;

import com.herbalistscraft.herb.HerbRarity;
import com.herbalistscraft.herb.Toxicity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

/**
 * A medicine as defined by the server datapack. The item, the tooltip, the effects and the
 * discovery rules all read from this record, so nothing about a medicine lives in Java.
 */
public record MedicineDefinition(
        MedicineKind kind,
        int tier,
        HerbRarity rarity,
        String color,
        Toxicity toxicity,
        int toxinPoints,
        int cooldown,
        String description,
        DiscoveryMethod discovery,
        List<MedicineEffect> effects,
        List<MedicineEffect> sideEffects,
        Optional<Coating> coating) {

    public static final Codec<MedicineDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MedicineKind.CODEC.fieldOf("kind").forGetter(MedicineDefinition::kind),
            Codec.intRange(1, 4).optionalFieldOf("tier", 1).forGetter(MedicineDefinition::tier),
            HerbRarity.CODEC.optionalFieldOf("rarity", HerbRarity.COMMON).forGetter(MedicineDefinition::rarity),
            Codec.STRING.optionalFieldOf("color", "#c0c0c0").forGetter(MedicineDefinition::color),
            Toxicity.CODEC.optionalFieldOf("toxicity", Toxicity.NONE).forGetter(MedicineDefinition::toxicity),
            Codec.INT.optionalFieldOf("toxin_points", 0).forGetter(MedicineDefinition::toxinPoints),
            Codec.INT.optionalFieldOf("cooldown", 200).forGetter(MedicineDefinition::cooldown),
            Codec.STRING.optionalFieldOf("description", "").forGetter(MedicineDefinition::description),
            DiscoveryMethod.CODEC.optionalFieldOf("discovery", DiscoveryMethod.EXPERIMENT)
                    .forGetter(MedicineDefinition::discovery),
            MedicineEffect.CODEC.listOf().optionalFieldOf("effects", List.of()).forGetter(MedicineDefinition::effects),
            MedicineEffect.CODEC.listOf().optionalFieldOf("side_effects", List.of())
                    .forGetter(MedicineDefinition::sideEffects),
            Coating.CODEC.optionalFieldOf("coating").forGetter(MedicineDefinition::coating)
    ).apply(instance, MedicineDefinition::new));

    /** How a recipe is learned. */
    public enum DiscoveryMethod implements StringRepresentable {
        STARTER,
        EXPERIMENT,
        LOOT,
        TRADE,
        JOURNAL;

        public static final Codec<DiscoveryMethod> CODEC = StringRepresentable.fromEnum(DiscoveryMethod::values);

        @Override
        public String getSerializedName() {
            return name();
        }
    }

    /** Weapon-coating behaviour for oils. */
    public record Coating(int charges, int durationTicks, float potency, List<MedicineEffect> onHit) {
        public static final Codec<Coating> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.intRange(1, 64).optionalFieldOf("charges", 8).forGetter(Coating::charges),
                Codec.INT.optionalFieldOf("duration_ticks", 2400).forGetter(Coating::durationTicks),
                Codec.floatRange(0.1F, 2.0F).optionalFieldOf("potency", 0.7F).forGetter(Coating::potency),
                MedicineEffect.CODEC.listOf().optionalFieldOf("on_hit", List.of()).forGetter(Coating::onHit)
        ).apply(instance, Coating::new));
    }

    public boolean isCoating() {
        return coating.isPresent();
    }

    public Component descriptionComponent(String id) {
        return Component.translatable("medicine.herbalistscraft." + id + ".description");
    }
}
