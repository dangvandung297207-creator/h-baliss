package com.herbalistscraft.world;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;

/**
 * Maps a position to the biome groups the content toolchain uses (plains, forest, swamp ...).
 * The mapping is built once per biome holder and cached, so nothing scans per tick.
 */
public final class BiomeGroups {
    private static final List<String> GROUPS = List.of(
            "plains", "forest", "dark_forest", "jungle", "swamp", "taiga", "snowy", "desert", "savanna",
            "badlands", "mountains", "beach", "river", "nether", "cave", "end");

    private BiomeGroups() {}

    public static List<String> groupsFor(ServerLevel level, BlockPos pos) {
        Holder<Biome> biome = level.getBiome(pos);
        ResourceLocation id = level.registryAccess().registryOrThrow(Registries.BIOME).getKey(biome.value());
        List<String> groups = new ArrayList<>();
        if (id != null) {
            String path = id.getPath();
            for (String group : GROUPS) {
                if (matches(group, path)) {
                    groups.add(group);
                }
            }
        }
        if (groups.isEmpty()) {
            groups.add("cave");
        }
        return groups;
    }

    private static boolean matches(String group, String path) {
        return switch (group) {
            case "plains" -> path.contains("plains") || path.contains("meadow") || path.contains("field");
            case "forest" -> path.contains("forest") && !path.contains("dark");
            case "dark_forest" -> path.contains("dark_forest");
            case "jungle" -> path.contains("jungle") || path.contains("bamboo");
            case "swamp" -> path.contains("swamp") || path.contains("mangrove");
            case "taiga" -> path.contains("taiga") || path.contains("old_growth_pine");
            case "snowy" -> path.contains("snow") || path.contains("frozen") || path.contains("ice");
            case "desert" -> path.contains("desert");
            case "savanna" -> path.contains("savanna");
            case "badlands" -> path.contains("badlands") || path.contains("mesa");
            case "mountains" -> path.contains("mountain") || path.contains("peak") || path.contains("slope")
                    || path.contains("stony");
            case "beach" -> path.contains("beach") || path.contains("shore");
            case "river" -> path.contains("river");
            case "nether" -> path.contains("nether") || path.contains("warped") || path.contains("crimson")
                    || path.contains("soul_sand");
            case "end" -> path.contains("end");
            case "cave" -> path.contains("lush_caves") || path.contains("dripstone") || path.contains("deep_dark");
            default -> false;
        };
    }
}
