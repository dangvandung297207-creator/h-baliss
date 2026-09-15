package com.herbalistscraft.integration.tan;

import com.herbalistscraft.Config;
import com.herbalistscraft.HerbalistsCraft;
import com.herbalistscraft.herb.HerbSeason;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;

/**
 * Tough As Nails lives entirely behind this gate: temperature, hydration and seasons.
 * Nothing here is required, and no TAN class is touched unless the mod is loaded.
 */
public final class TanIntegration {
    public static final String MODID = "toughasnails";
    private static Boolean loaded;

    private TanIntegration() {}

    public static boolean isLoaded() {
        if (loaded == null) {
            loaded = ModList.get().isLoaded(MODID);
            if (Config.debugLogging()) {
                HerbalistsCraft.LOGGER.info("Tough As Nails detected: {}", loaded);
            }
        }
        return loaded;
    }

    public static boolean active() {
        return isLoaded() && Config.toughAsNails();
    }

    /** Seasons, if TAN exposes them; empty when TAN has no season cycle or is absent. */
    public static Optional<HerbSeason> season(Level level) {
        if (!active()) {
            return Optional.empty();
        }
        try {
            return TanSeasons.current(level);
        } catch (Throwable error) {
            HerbalistsCraft.LOGGER.warn("Tough As Nails season lookup failed, falling back to internal seasons", error);
            return Optional.empty();
        }
    }

    /** Warms or cools the player, in TAN temperature units. Returns the amount actually applied. */
    public static float adjustTemperature(Player player, float amount) {
        if (!active() || amount == 0.0F) {
            return 0.0F;
        }
        try {
            return TanTemperature.adjust(player, amount);
        } catch (Throwable error) {
            HerbalistsCraft.LOGGER.warn("Tough As Nails temperature adjustment failed", error);
            return 0.0F;
        }
    }

    /** Restores thirst, in TAN hydration units. Returns the amount actually applied. */
    public static float hydrate(Player player, float amount) {
        if (!active() || amount == 0.0F) {
            return 0.0F;
        }
        try {
            return TanTemperature.hydrate(player, amount);
        } catch (Throwable error) {
            HerbalistsCraft.LOGGER.warn("Tough As Nails hydration adjustment failed", error);
            return 0.0F;
        }
    }
}
