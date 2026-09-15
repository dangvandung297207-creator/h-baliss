package com.herbalistscraft.herb;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;

/**
 * A herb as defined by the server datapack ({@code data/<namespace>/herbalistscraft/herb/<id>.json}).
 * Codecs are shared by the datapack loader and the network sync, so clients always agree with the server.
 */
public record HerbDefinition(
        HerbCategory category,
        HerbMorphology morphology,
        String lore,
        Map<HerbProperty, Float> properties,
        Toxicity toxicity,
        HerbRarity rarity,
        List<HerbSeason> seasons,
        List<String> biomeGroups,
        HerbPlacement placement,
        HerbSoil soil,
        HerbLight light,
        Growth growth,
        List<HerbForm> forms,
        List<String> uses) {

    public static final Codec<HerbDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            HerbCategory.CODEC.fieldOf("category").forGetter(HerbDefinition::category),
            HerbMorphology.CODEC.optionalFieldOf("morphology", HerbMorphology.LEAF).forGetter(HerbDefinition::morphology),
            Codec.STRING.optionalFieldOf("lore", "").forGetter(HerbDefinition::lore),
            Codec.unboundedMap(HerbProperty.CODEC, Codec.FLOAT).fieldOf("properties").forGetter(HerbDefinition::properties),
            Toxicity.CODEC.optionalFieldOf("toxicity", Toxicity.NONE).forGetter(HerbDefinition::toxicity),
            HerbRarity.CODEC.optionalFieldOf("rarity", HerbRarity.COMMON).forGetter(HerbDefinition::rarity),
            HerbSeason.CODEC.listOf().optionalFieldOf("seasons", List.of()).forGetter(HerbDefinition::seasons),
            Codec.STRING.listOf().optionalFieldOf("biome_groups", List.of()).forGetter(HerbDefinition::biomeGroups),
            HerbPlacement.CODEC.optionalFieldOf("placement", HerbPlacement.SURFACE).forGetter(HerbDefinition::placement),
            HerbSoil.CODEC.optionalFieldOf("soil", HerbSoil.DIRT).forGetter(HerbDefinition::soil),
            HerbLight.CODEC.optionalFieldOf("light", HerbLight.BRIGHT).forGetter(HerbDefinition::light),
            Growth.CODEC.forGetter(HerbDefinition::growth),
            HerbForm.CODEC.listOf().optionalFieldOf("forms", List.of(HerbForm.FRESH, HerbForm.SEED))
                    .forGetter(HerbDefinition::forms),
            Codec.STRING.listOf().optionalFieldOf("uses", List.of()).forGetter(HerbDefinition::uses)
    ).apply(instance, HerbDefinition::new));

    /** Growth behaviour: speed, harvest size, seed return, regrowth and shelf life.
     *  Written flat by the toolchain ({@code growth_speed}, {@code yield}, {@code seed_return},
     *  {@code regrow_chance}, {@code fresh_days}) so a herb file reads like a herb, not like a tree. */
    public record Growth(float speed, Range produce, Range seeds, float regrowChance, int freshDays) {
        public static final Codec<Growth> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.optionalFieldOf("growth_speed", 1.0F).forGetter(Growth::speed),
                Range.CODEC.optionalFieldOf("yield", new Range(1, 1)).forGetter(Growth::produce),
                Range.CODEC.optionalFieldOf("seed_return", new Range(1, 1)).forGetter(Growth::seeds),
                Codec.floatRange(0.0F, 1.0F).optionalFieldOf("regrow_chance", 0.0F).forGetter(Growth::regrowChance),
                Codec.intRange(1, 60).optionalFieldOf("fresh_days", 4).forGetter(Growth::freshDays)
        ).apply(instance, Growth::new));

    }

    /** An inclusive integer range, as written by the content toolchain. */
    public record Range(int min, int max) {
        public static final Codec<Range> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("min").forGetter(Range::min),
                Codec.INT.fieldOf("max").forGetter(Range::max)
        ).apply(instance, Range::new));

        public int roll(RandomSource random) {
            return min >= max ? min : min + random.nextInt(max - min + 1);
        }
    }

    public float potency(HerbProperty property) {
        return properties.getOrDefault(property, 0.0F);
    }

    public boolean has(HerbProperty property) {
        return potency(property) > 0.0F;
    }

    public boolean hasForm(HerbForm form) {
        return forms.contains(form);
    }

    public boolean growsIn(HerbSeason season) {
        return seasons.isEmpty() || seasons.contains(season);
    }

    /** Growth multiplier for a season, honouring the "thrives / dormant" rules. */
    public float seasonGrowth(HerbSeason season) {
        if (seasons.isEmpty()) {
            return season.growthMultiplier();
        }
        if (seasons.contains(season)) {
            return season.growthMultiplier() * 1.35F;
        }
        if (season == HerbSeason.WINTER && !seasons.contains(HerbSeason.WINTER)) {
            return 0.0F;
        }
        return season.growthMultiplier() * 0.45F;
    }

    public String loreKey(String herbId) {
        return "herb.herbalistscraft." + herbId + ".lore";
    }

    public Component loreComponent(String herbId) {
        return Component.translatable(loreKey(herbId));
    }
}
