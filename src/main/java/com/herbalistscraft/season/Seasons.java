package com.herbalistscraft.season;

import com.herbalistscraft.Config;
import com.herbalistscraft.herb.HerbSeason;
import com.herbalistscraft.integration.serene.SereneSeasonsIntegration;
import com.herbalistscraft.integration.tan.TanIntegration;
import java.util.Optional;
import net.minecraft.world.level.Level;

/**
 * Season and temperature lookups. Optional mods are consulted through classes that are only
 * loaded when the mod is actually present, so the mod always runs standalone.
 */
public final class Seasons {
    private Seasons() {}

    /** The season in effect for a level: an optional provider if configured, else internal seasons. */
    public static HerbSeason current(Level level) {
        if (level == null) {
            return HerbSeason.SPRING;
        }
        Optional<HerbSeason> external = external(level);
        if (external.isPresent()) {
            return external.get();
        }
        return internal(level);
    }

    /** Day-based internal season: one season every {@code season.internal_length_days}. */
    public static HerbSeason internal(Level level) {
        int length = Math.max(1, Config.seasonLengthDays());
        long day = level.getDayTime() / 24000L;
        long index = (day / length) % 4L;
        return switch ((int) index) {
            case 0 -> HerbSeason.SPRING;
            case 1 -> HerbSeason.SUMMER;
            case 2 -> HerbSeason.AUTUMN;
            default -> HerbSeason.WINTER;
        };
    }

    private static Optional<HerbSeason> external(Level level) {
        Config.SeasonSource source = Config.seasonSource();
        if (source == Config.SeasonSource.INTERNAL) {
            return Optional.empty();
        }
        if (source == Config.SeasonSource.AUTO || source == Config.SeasonSource.TOUGH_AS_NAILS) {
            if (TanIntegration.isLoaded() && Config.toughAsNailsSeasons()) {
                Optional<HerbSeason> tan = TanIntegration.season(level);
                if (tan.isPresent()) {
                    return tan;
                }
            }
        }
        if (source == Config.SeasonSource.AUTO || source == Config.SeasonSource.SERENE_SEASONS) {
            Optional<HerbSeason> serene = SereneSeasonsIntegration.season(level);
            if (serene.isPresent()) {
                return serene;
            }
        }
        return Optional.empty();
    }
}
