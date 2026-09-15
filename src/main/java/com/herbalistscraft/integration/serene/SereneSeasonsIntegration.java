package com.herbalistscraft.integration.serene;

import com.herbalistscraft.Config;
import com.herbalistscraft.HerbalistsCraft;
import com.herbalistscraft.herb.HerbSeason;
import java.util.Locale;
import java.util.Optional;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;

/**
 * Serene Seasons support, isolated here and reached reflectively so the mod links and runs
 * whether or not Serene Seasons is installed. Only used when the player asks for it through
 * {@code integration.serene_seasons}.
 */
public final class SereneSeasonsIntegration {
    public static final String MODID = "sereneseasons";
    private static Boolean loaded;

    private SereneSeasonsIntegration() {}

    public static boolean isLoaded() {
        if (loaded == null) {
            loaded = ModList.get().isLoaded(MODID);
            if (Config.debugLogging()) {
                HerbalistsCraft.LOGGER.info("Serene Seasons detected: {}", loaded);
            }
        }
        return loaded;
    }

    public static boolean active() {
        return isLoaded() && Config.sereneSeasons();
    }

    /** The season Serene Seasons reports for a level, if that API is where we expect it. */
    public static Optional<HerbSeason> season(Level level) {
        if (!active()) {
            return Optional.empty();
        }
        try {
            Class<?> helper = Class.forName("sereneseasons.api.season.SeasonHelper");
            Object state = helper.getMethod("getSeasonState", Level.class).invoke(null, level);
            if (state == null) {
                return Optional.empty();
            }
            Object subSeason = state.getClass().getMethod("getSubSeason").invoke(state);
            String name = String.valueOf(subSeason == null ? state : subSeason).toUpperCase(Locale.ROOT);
            for (HerbSeason candidate : HerbSeason.values()) {
                if (name.contains(candidate.name())) {
                    return Optional.of(candidate);
                }
            }
        } catch (ReflectiveOperationException | LinkageError ignored) {
            // Serene Seasons present but the API moved: internal seasons take over.
        }
        return Optional.empty();
    }
}
