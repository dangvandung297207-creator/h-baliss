"""Biome groups used by herb worldgen.

Each group becomes a biome tag (``data/herbalistscraft/tags/worldgen/biome/herb_biomes/<group>.json``)
built from vanilla biome ids and vanilla biome tags, and every herb's ``biomes`` list is
turned into a NeoForge biome modifier that adds that herb's placed feature to those biomes.
Keeping the mapping in one place is what makes "no biome contains every herb" easy to audit.
"""

BIOME_GROUPS = {
    "plains": ["minecraft:plains", "minecraft:sunflower_plains", "minecraft:meadow"],
    "forest": ["#minecraft:is_forest"],
    "dark_forest": ["minecraft:dark_forest"],
    "jungle": ["#minecraft:is_jungle"],
    "swamp": ["minecraft:swamp", "minecraft:mangrove_swamp"],
    "taiga": ["#minecraft:is_taiga"],
    "snowy": [
        "minecraft:snowy_plains", "minecraft:snowy_beach", "minecraft:snowy_slopes",
        "minecraft:snowy_taiga", "minecraft:ice_spikes", "minecraft:frozen_river",
        "minecraft:frozen_ocean", "minecraft:grove",
    ],
    "desert": ["minecraft:desert"],
    "savanna": ["#minecraft:is_savanna"],
    "badlands": ["#minecraft:is_badlands"],
    "mountains": ["#minecraft:is_mountain", "#minecraft:is_hill", "minecraft:stony_shore", "minecraft:windswept_savanna"],
    "beach": ["#minecraft:is_beach", "minecraft:stony_shore"],
    "river": ["#minecraft:is_river", "minecraft:river", "minecraft:frozen_river"],
    "nether": ["#minecraft:is_nether"],
    "cave": ["#minecraft:is_overworld"],
    "end": ["#minecraft:is_end"],
}

# Structure biome tags (structures are rarer than herbs and pick a smaller subset).
STRUCTURE_BIOMES = {
    "herbalist_hut": ["#minecraft:is_overworld"],
    "abandoned_hut": ["#minecraft:is_overworld"],
    "ancient_apothecary": ["#minecraft:is_forest", "#minecraft:is_taiga", "#minecraft:is_jungle",
                           "#minecraft:is_mountain", "minecraft:swamp"],
}

# Where the worldgen places each placement style. ``heightmap``/``height_range`` values are
# consumed by tools/gen/data.py to build the placed feature JSON.
PLACEMENT_STYLES = {
    "SURFACE": {"count": 4, "rarity": 1, "heightmap": "WORLD_SURFACE_WG", "tries": 4, "spread": 5},
    "WETLAND": {"count": 3, "rarity": 2, "heightmap": "WORLD_SURFACE_WG", "tries": 3, "spread": 4},
    "SHADED": {"count": 3, "rarity": 2, "heightmap": "WORLD_SURFACE_WG", "tries": 3, "spread": 4},
    "HIGH_ELEVATION": {"count": 3, "rarity": 3, "heightmap": "WORLD_SURFACE_WG", "tries": 2, "spread": 3, "min_y": 96},
    "UNDERGROUND": {"count": 12, "rarity": 4, "height_range": [0, 56], "tries": 2, "spread": 3},
    "NETHER": {"count": 5, "rarity": 2, "heightmap": "WORLD_SURFACE_WG", "tries": 3, "spread": 4},
}

RARITY_MULTIPLIER = {
    "COMMON": 1.0,
    "UNCOMMON": 0.75,
    "RARE": 0.55,
    "VERY_RARE": 0.35,
    "LEGENDARY": 0.2,
}

TOXICITY_RARITY_BONUS = {"NONE": 0, "LOW": 0, "MEDIUM": 0, "HIGH": 1, "EXTREME": 2}
