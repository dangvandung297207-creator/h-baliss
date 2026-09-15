# Herbalist's Craft

A herbalism, medicine and survival mod for **Minecraft 1.21.1 / NeoForge**. Forty-nine herbs grow
where their biome, light level and season allow, and everything you do with them — gather, dry,
grind, mill, extract, brew, poison — is a mechanic you can watch happen.

> Explore → harvest → process → learn → discover → brew → use it well.

## What is in it

- **49 herbs**, each with a plant, seeds, textures, models, biome and season preferences, growth
  speed, rarity, yield, properties, toxicity and lore — all data-driven, no per-herb classes.
- **37 medicines** across four forms: tonics, teas, salves and weapon oils, from a cheap Minor
  Healing Tonic up to tier 4 brews that ask for rare ingredients, processing and knowledge.
- **Four workstations**, none of them a crafting table: the **Mortar & Pestle** (grind by hand),
  the **Herbal Mill** (batch processing with its own GUI and progress animation), the
  **Herbalist's Table** (base + herb + extract + catalyst, with a recipe, property and knowledge
  panel) and the **Drying Rack** (herbs visibly dry in place).
- **Experimentation**: unknown combinations produce a deterministic medicine computed from what
  went in — never a placeholder, never a new item registered at runtime.
- **A real knowledge system**: properties and recipes start unknown, are learned by handling
  plants, reading journal pages, trading, exploring or experimenting, and are remembered forever.
- **The Herbalist's Journal**: a custom item with its own screen covering the Herb Encyclopedia,
  Properties, Recipes, Research, the Seasonal Guide and the Biome Guide.
- **Toxicity and freshness**: strong medicine has a price; fresh herbs are potent but perishable,
  dried ones keep and are milder.
- **A Herbalist villager**, biome-dependent trades from Novice to Master, and three structures —
  the Herbalist Hut, the Abandoned Herbalist Hut and the Ancient Apothecary — with loot that
  rewards exploration without flooding vanilla chests.
- **Optional integrations**: Tough As Nails (temperature, hydration, seasons) and Serene Seasons,
  both detected at runtime through an isolated reflective gate. The mod runs fine without either.

## Installing

1. Install [NeoForge 21.1.250 or newer 21.1.x](https://neoforged.net/) for Minecraft 1.21.1.
2. Drop `herbalistscraft-1.0.0.jar` into your `mods/` folder.
3. Optional: add Tough As Nails for temperature, hydration and seasons.

Grab the jar from the [releases page](../../releases). Packs are welcome to modify the shipped
data files.

## Building

```bash
./gradlew build          # jar in build/libs/
./gradlew runClient      # development client
python3 tools/generate.py   # regenerate content from tools/content/*.json
python3 tools/pack_check.py # validate ids, models, lang, worldgen and structure templates
```

See [docs/BUILD.md](docs/BUILD.md) for the full build and release workflow.

## Documentation

- [docs/HERBALISTS_CRAFT.md](docs/HERBALISTS_CRAFT.md) — every herb, medicine, workstation,
  config option, structure, advancement and command.
- [docs/BUILD.md](docs/BUILD.md) — building, regenerating the content, releasing.

## Layout

| Path | What lives there |
| --- | --- |
| `src/main/java/com/herbalistscraft/` | Hand-written code: workstations, medicine, mixing, knowledge, worldgen, client |
| `src/main/java/com/herbalistscraft/registry/` | Generated registration: herbs, medicines, items, blocks, GUI layout |
| `src/main/resources/` | Authored and generated assets, and the herb/medicine datapack registries |
| `src/generated/resources/` | Tags, worldgen, loot, advancements and the structure templates |
| `tools/` | The content files and the Python generator that turns them into everything above |

## Licence

All rights reserved. See [LICENSE](LICENSE).
