package com.herbalistscraft.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

/** Configuration of a wild herb patch: which herb, and how far it spreads. */
public record HerbPatchConfiguration(String herb, int tries, int xzSpread, int ySpread)
        implements FeatureConfiguration {
    public static final Codec<HerbPatchConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("herb").forGetter(HerbPatchConfiguration::herb),
            Codec.intRange(1, 64).optionalFieldOf("tries", 3).forGetter(HerbPatchConfiguration::tries),
            Codec.intRange(1, 16).optionalFieldOf("xz_spread", 4).forGetter(HerbPatchConfiguration::xzSpread),
            Codec.intRange(0, 8).optionalFieldOf("y_spread", 1).forGetter(HerbPatchConfiguration::ySpread)
    ).apply(instance, HerbPatchConfiguration::new));
}
