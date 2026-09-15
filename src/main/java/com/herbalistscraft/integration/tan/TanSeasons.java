package com.herbalistscraft.integration.tan;

import com.herbalistscraft.herb.HerbSeason;
import java.util.Optional;
import net.minecraft.world.level.Level;

/**
 * Compiled against Tough As Nails only in spirit: every access is reflective so the mod links
 * and runs whether or not TAN is installed. Loaded lazily, never on the hot path.
 */
final class TanSeasons {
    private TanSeasons() {}

    static Optional<HerbSeason> current(Level level) {
        try {
            Class<?> helper = Class.forName("toughasnails.season.SeasonHelper");
            Object season = helper.getMethod("getSeason", Level.class).invoke(null, level);
            if (season == null) {
                return Optional.empty();
            }
            String name = String.valueOf(season).toUpperCase(java.util.Locale.ROOT);
            for (HerbSeason candidate : HerbSeason.values()) {
                if (name.contains(candidate.name())) {
                    return Optional.of(candidate);
                }
            }
        } catch (ReflectiveOperationException | LinkageError ignored) {
            // TAN present but the season API moved: internal seasons take over.
        }
        return Optional.empty();
    }
}
