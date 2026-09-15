# Herbalist's Craft — content reference

This document is the reference for everything the mod ships: the herbs, the medicines, the
workstations, the progression ladder, the integrations and every configuration option. It is
generated from the same content files the mod is built from, so the numbers here are the numbers
in the jar.

- **Minecraft:** 1.21.1 · **NeoForge:** 21.1.250 or newer 21.1.x · **Java:** 21
- **Content:** 49 herbs, 37 medicines, 14 properties, 5 item forms, 5 toxicity levels
- **Workstations:** Mortar & Pestle, Herbal Mill, Herbalist's Table, Drying Rack
- **Optional mods:** Tough As Nails (temperature, hydration, seasons), Serene Seasons (seasons)

## The loop

Explore → harvest wild herbs and seeds → process them → learn their properties → discover recipes →
brew medicine → use it well.

Every step is a real mechanic rather than a menu: wild herbs only grow where their biome,
temperature, light level and season say they should; processing happens at workstations that
animate, make noise and show progress; properties are *learned* by handling plants, and stay
learned on the player forever.

## Progression ladder

| # | Step | What unlocks it |
| --- | --- | --- |
| 1 | Beginner | Punch grass and find your first wild herb |
| 2 | Wild Gathering | Harvesting teaches the herb's properties and its season |
| 3 | Mortar & Pestle | Craft the mortar, grind a herb into powder |
| 4 | Basic Medicines | Brew the first tonics at the Herbalist's Table |
| 5 | Journal | Craft the Herbalist's Journal, read what you know |
| 6 | Herbal Mill | Batch processing, up to three herbs per cycle |
| 7 | Seasonal Farming | Grow herbs deliberately, plant the right season |
| 8 | Advanced Extracts | Concentrated extracts for tier 3 medicines |
| 9 | Herbalist's Table | The full base + herb + extract + catalyst workstation |
| 10 | Coatings | Toxic, bleeding, weakening oils for weapons |
| 11 | Advanced Medicine | Tier 4 medicines, expensive and risky |
| 12 | Ancient Knowledge | Recipes from the Ancient Apothecary |

## Workstations

| Workstation | Slots | Behaviour |
| --- | --- | --- |
| **Mortar & Pestle** | 1 in, 1 out | Hand grinding with a pestle animation, custom grind sound and green medicinal particles. Any herb becomes its powder; nothing about it needs a crafting table. |
| **Herbal Mill** | 3 inputs + additive, 1 output | Batch processing with a real GUI: animated progress bar, slot separation, output preview and milling sound. Halves the work of grinding three herbs by hand. |
| **Herbalist's Table** | Base + Herb + Extract + Catalyst → Result | Three recipes per medicine plus free-form experimentation. The panel shows the recipe, the properties it will produce and whether the player knows it. |
| **Drying Rack** | 1 slot | Fresh herb in, dried herb out after a configurable drying time. The herb is drawn on the rack while it dries, then it pops off as a finished item. |

All four work on the server; the client only draws what the server tells it, and every screen
reads its geometry from the generated `GuiLayout` so slots, previews and tooltips cannot drift
apart.

## Herbs

Wild herbs grow through worldgen, not through constant scanning: each herb is a configured
feature placed by a biome modifier, so Minecraft's own chunk generation decides when to look.

| Herb | Category | Properties | Toxicity | Rarity | Seasons | Biomes | Placement |
| --- | --- | --- | --- | --- | --- | --- | --- |
| **Frostmint** (`frostmint`) | Cooling | COOLING, HYDRATION | None | Common | Win/Aut | taiga, snowy | Surface |
| **Glacier Lotus** (`glacier_lotus`) | Cooling | COLD_PROTECTION, COOLING, REGENERATION | Low | Very_Rare | Win | snowy | Wetland |
| **Iceleaf** (`iceleaf`) | Cooling | COLD_PROTECTION, COOLING | Low | Rare | Win | snowy, mountains | High_Elevation |
| **Moonfrost Berry** (`moonfrost_berry`) | Cooling | COOLING, HYDRATION | None | Uncommon | Win | taiga, snowy | Surface |
| **Snowbell** (`snowbell`) | Cooling | COOLING, SEDATIVE | Low | Uncommon | Win | snowy | Surface |
| **Winter Sage** (`winter_sage`) | Cooling | ANTISEPTIC, COLD_PROTECTION, COOLING, RESPIRATORY | None | Common | Win/Aut | taiga, mountains | Surface |
| **Bloodroot** (`bloodroot`) | Healing | HEALING, HEMOSTATIC | Low | Common | Spr/Aut | plains, forest | Surface |
| **Comfrey** (`comfrey`) | Healing | HEALING, HEMOSTATIC | Low | Common | Spr/Sum | forest, swamp | Wetland |
| **Heartleaf** (`heartleaf`) | Healing | HEALING, REGENERATION | None | Uncommon | Spr | forest | Shaded |
| **Red Clover** (`red_clover`) | Healing | ANTISEPTIC, HEALING, HYDRATION | None | Common | Spr/Sum | plains | Surface |
| **Silverleaf** (`silverleaf`) | Healing | ANTISEPTIC, COOLING, HEALING | None | Uncommon | Spr/Aut | forest | Shaded |
| **Vitalis Root** (`vitalis_root`) | Healing | ENERGY, HEALING, REGENERATION | Low | Rare | Sum | jungle | Shaded |
| **Yarrow** (`yarrow`) | Healing | ANTISEPTIC, HEMOSTATIC | Low | Common | Sum | plains, mountains | Surface |
| **Bitterroot** (`bitterroot`) | Medicinal | ANTISEPTIC, RESPIRATORY, TOXIC | Medium | Common | Sum | desert, badlands | Surface |
| **Cleansage** (`cleansage`) | Medicinal | ANTISEPTIC, RESPIRATORY | None | Common | Spr/Sum | plains, badlands | Surface |
| **Feverfew** (`feverfew`) | Medicinal | COOLING, HEALING, SEDATIVE | Low | Uncommon | Sum | plains, mountains | Surface |
| **Golden Chamomile** (`golden_chamomile`) | Medicinal | HEALING, HYDRATION, SEDATIVE | None | Common | Spr | plains | Surface |
| **Lungwort** (`lungwort`) | Medicinal | HEALING, RESPIRATORY | None | Uncommon | Spr | forest | Shaded |
| **Marshroot** (`marshroot`) | Medicinal | ANTISEPTIC, HYDRATION, RESPIRATORY | Low | Common | Sum | swamp | Wetland |
| **Spiritwood** (`spiritwood`) | Medicinal | ANTISEPTIC, ENERGY, REGENERATION | Low | Rare | Aut/Win | forest, taiga | Shaded |
| **Witch Hazel** (`witch_hazel`) | Medicinal | ANTISEPTIC, HEALING, HEMOSTATIC | None | Uncommon | Aut | forest, swamp | Shaded |
| **Ashleaf** (`ashleaf`) | Protective | FIRE_PROTECTION, WARMING | Low | Common | Sum/Aut | nether | Nether |
| **Dragonscale Herb** (`dragonscale_herb`) | Protective | FIRE_PROTECTION, HEALING, WARMING | Medium | Very_Rare | Sum/Aut | mountains, nether | High_Elevation |
| **Ironbark** (`ironbark`) | Protective | ANTISEPTIC, FIRE_PROTECTION | Low | Uncommon | Aut/Win | mountains, jungle | High_Elevation |
| **Sunpetal** (`sunpetal`) | Protective | FIRE_PROTECTION, HEALING, WARMING | None | Uncommon | Sum | savanna, desert | Surface |
| **Wardroot** (`wardroot`) | Protective | ANTISEPTIC, COLD_PROTECTION, WARMING | Low | Rare | Win | taiga, mountains | High_Elevation |
| **Dreamcap** (`dreamcap`) | Sedative | REGENERATION, SEDATIVE | Medium | Uncommon | Aut/Win | cave | Underground |
| **Lavender** (`lavender`) | Sedative | ANTISEPTIC, HYDRATION, SEDATIVE | None | Common | Sum | plains, forest | Surface |
| **Moonflower** (`moonflower`) | Sedative | COOLING, SEDATIVE | Medium | Uncommon | Aut | dark_forest | Shaded |
| **Sleepwort** (`sleepwort`) | Sedative | SEDATIVE, TOXIC | High | Rare | Aut | swamp | Wetland |
| **Velvet Bell** (`velvet_bell`) | Sedative | HEALING, SEDATIVE | Low | Uncommon | Win | taiga | Shaded |
| **Dawnbloom** (`dawnbloom`) | Stimulant | ENERGY, STIMULANT, WARMING | Low | Uncommon | Spr | jungle | Shaded |
| **Stormherb** (`stormherb`) | Stimulant | ENERGY, STIMULANT | Medium | Uncommon | Sum/Aut | mountains | High_Elevation |
| **Swiftleaf** (`swiftleaf`) | Stimulant | ENERGY, STIMULANT | Low | Common | Sum | plains, savanna | Surface |
| **Thunder Thistle** (`thunder_thistle`) | Stimulant | ENERGY, FIRE_PROTECTION, STIMULANT | Medium | Rare | Sum | mountains, savanna | High_Elevation |
| **Wild Ginseng** (`wild_ginseng`) | Stimulant | ENERGY, REGENERATION, STIMULANT | Low | Rare | Aut | forest, jungle | Shaded |
| **Blackthorn** (`blackthorn`) | Toxic | HEMOSTATIC, TOXIC | Medium | Uncommon | Aut | dark_forest | Shaded |
| **Deadly Cap** (`deadly_cap`) | Toxic | SEDATIVE, TOXIC | Extreme | Rare | Aut | dark_forest, cave | Underground |
| **Grave Moss** (`grave_moss`) | Toxic | COOLING, REGENERATION, TOXIC | Medium | Rare | Aut/Win | cave | Underground |
| **Hemlock** (`hemlock`) | Toxic | SEDATIVE, TOXIC | Extreme | Uncommon | Spr | swamp | Wetland |
| **Nightshade** (`nightshade`) | Toxic | SEDATIVE, TOXIC | High | Common | Sum | swamp, dark_forest | Shaded |
| **Viperweed** (`viperweed`) | Toxic | HEMOSTATIC, TOXIC | High | Uncommon | Sum | swamp | Wetland |
| **Wolfsbane** (`wolfsbane`) | Toxic | RESPIRATORY, SEDATIVE, TOXIC | Extreme | Rare | Win | mountains, taiga | High_Elevation |
| **Blaze Pepper** (`blaze_pepper`) | Warming | ENERGY, STIMULANT, WARMING | Medium | Rare | Sum/Aut | nether | Nether |
| **Cinder Bloom** (`cinder_bloom`) | Warming | FIRE_PROTECTION, WARMING | Low | Rare | Sum | nether | Nether |
| **Cinnamon Bark** (`cinnamon_bark`) | Warming | ENERGY, RESPIRATORY, WARMING | None | Uncommon | Aut | jungle | Shaded |
| **Emberroot** (`emberroot`) | Warming | FIRE_PROTECTION, WARMING | Low | Common | Aut/Win | nether | Nether |
| **Fire Sage** (`fire_sage`) | Warming | FIRE_PROTECTION, WARMING | Low | Uncommon | Sum | badlands, savanna | Surface |
| **Sunleaf** (`sunleaf`) | Warming | ENERGY, WARMING | None | Common | Sum | desert, badlands | Surface |

### Item forms

Each herb yields a fresh plant and its seeds, and can be processed further:

| Form | Item | How it is made | Notes |
| --- | --- | --- | --- |
| Fresh | `<herb>` | Harvesting the plant | Strongest properties, spoils in days |
| Seed | `<herb>_seeds` | Harvesting or the seed pouch | Plant it, or trade it to the herbalist |
| Dried | `dried_<herb>` | Drying Rack | Weaker but keeps for a very long time |
| Powder | `<herb>_powder` | Mortar & Pestle, or Herbal Mill | Standard brewing input |
| Extract | `<herb>_extract` | Herbal Mill with an additive | Concentrated; halves the herb needed |

Every herb has at least the forms its own medicines need, so a recipe can never ask for a
`<herb>_extract` that does not exist.

## Medicines

| Medicine | Kind | Tier | Toxicity | Cooldown | Description |
| --- | --- | --- | --- | --- | --- |
| **Toxic Oil** (`toxic_oil`) | Oil | 1 | None | 3s | Nightshade steeped in seed oil. Crude, cheap, and it works on anything with a blade. |
| **Bleeding Oil** (`bleeding_oil`) | Oil | 2 | None | 3s | Blackthorn and yarrow, perversely combined. Cuts that keep bleeding. |
| **Poison Extract** (`poison_extract`) | Oil | 2 | None | 3s | Viperweed and deadly cap drawn into oil. Potent enough that the maker wears gloves. |
| **Slowing Oil** (`slowing_oil`) | Oil | 2 | None | 3s | Hemlock and sleepwort. A numbing coat that makes every step an effort. |
| **Weakening Oil** (`weakening_oil`) | Oil | 3 | None | 3s | Wolfsbane, ground into grease. The struck limb forgets its strength. |
| **Venom Oil** (`venom_oil`) | Oil | 4 | None | 3s | Viperweed drawn a second time through shadow ichor. Almost nothing survives the wound. |
| **Antiseptic Salve** (`antiseptic_salve`) | Salve | 1 | None | 10s | A green balm of cleansage and silverleaf. The field dressing of every herbalist. |
| **Healing Salve** (`healing_salve`) | Salve | 1 | Low | 12s | Bloodroot and comfrey in a wax base. Ordinary, reliable, always worth carrying. |
| **Burn Salve** (`burn_salve`) | Salve | 2 | None | 15s | Witch hazel and lavender for cooked skin. Extinguishes the burn and cools what is left. |
| **Ember Salve** (`ember_salve`) | Salve | 2 | Low | 30s | Emberroot and fire sage worked into wax. It does not make the wearer fireproof, only harder to burn. |
| **Frost Salve** (`frost_salve`) | Salve | 2 | None | 30s | A greasy balm of iceleaf and glacier lotus. Rubbed into the skin it turns the cold aside. |
| **Ward Salve** (`ward_salve`) | Salve | 3 | Low | 45s | Ironbark and wardroot, ground fine. Old herbalists paint it on before bad weather and bad roads. |
| **Sweet Berry Tea** (`berry_tea`) | Tea | 1 | None | 6s | Moonfrost berries and red clover. Sweet, cold, and endlessly drinkable. |
| **Bitter Tea** (`bitter_tea`) | Tea | 1 | Low | 10s | Bitterroot and marshroot. Unpleasant, clearing, and it grows where nothing else will. |
| **Cooling Tea** (`cooling_tea`) | Tea | 1 | None | 10s | Frostmint steeped with feverfew. For the long walk under a pitiless sun. |
| **Herbal Tea** (`herbal_tea`) | Tea | 1 | None | 6s | Chamomile, clover and a little lavender. The first cup every herbalist learns. |
| **Sun Tea** (`sun_tea`) | Tea | 1 | None | 7s | Sunleaf and its own petals, steeped until the cup glows faintly. A traveller's friend on cold roads. |
| **Warming Tea** (`warming_tea`) | Tea | 1 | None | 10s | Emberroot and sunpetal. The cup that keeps the cold from settling into the bones. |
| **Focus Tea** (`focus_tea`) | Tea | 2 | Low | 20s | A mild dawnbloom cup for long nights of work. No jitters, no brilliance, just clarity. |
| **Recovery Tea** (`recovery_tea`) | Tea | 2 | None | 15s | Feverfew and velvet bell. What you drink the morning after a bad night. |
| **Sleep Tea** (`sleep_tea`) | Tea | 2 | Low | 30s | Moonflower, snowbell and a pinch of frost crystal. Sleep comes quickly and stays. |
| **Hush Tea** (`hush_tea`) | Tea | 3 | Medium | 45s | Sleepwort, taken carefully. The marsh keeps its own recipes and shares them grudgingly. |
| **Minor Healing Tonic** (`minor_healing_tonic`) | Tonic | 1 | Low | 5s | Bitter root steeped in spring water. Closes small wounds at the cost of an empty belly. |
| **Antidote Tonic** (`antidote_tonic`) | Tonic | 2 | None | 12s | Marshroot boiled with purifying salt. Strips venom from the blood. |
| **Antiseptic Tonic** (`antiseptic_tonic`) | Tonic | 2 | Low | 10s | Cleansage and silverleaf. Drives rot out of a wound from the inside. |
| **Healing Tonic** (`healing_tonic`) | Tonic | 2 | Low | 10s | Comfrey and bloodroot in equal measure. Knits flesh, thickens blood. |
| **Hemostatic Tonic** (`hemostatic_tonic`) | Tonic | 2 | Low | 8s | Yarrow and blackthorn. It stops bleeding, and it stops it hard. |
| **Respiratory Tonic** (`respiratory_tonic`) | Tonic | 2 | Low | 12s | Cinnamon bark and lungwort. It keeps the breath coming and holds back the dark water. |
| **Stamina Tonic** (`stamina_tonic`) | Tonic | 2 | Medium | 30s | Swiftleaf, ginseng and a bite of blaze pepper. Fast legs, and a hunger that follows. |
| **Detox Tonic** (`detox_tonic`) | Tonic | 3 | None | 15s | Spiritwood and grave moss in strong spirits. It purges the toxin meter, and then some. |
| **Ember Tonic** (`ember_tonic`) | Tonic | 3 | Medium | 24s | Thunder thistle and ashleaf. The drinker's blood runs hot against flame and frost alike. |
| **Focus Tonic** (`focus_tonic`) | Tonic | 3 | Medium | 30s | Dawnbloom drawn through stormherb. The world sharpens; sleep can wait. |
| **Frost Lung Tonic** (`frost_lung_tonic`) | Tonic | 3 | Low | 16s | Winter sage and ice-bearing leaves. The cold reaches the chest and stops there. |
| **Recuperation Tonic** (`recuperation_tonic`) | Tonic | 3 | Medium | 40s | Dreamcap and grave moss. Deep, healing sleep for the badly hurt - and a heavy head after. |
| **Strong Healing Tonic** (`strong_healing_tonic`) | Tonic | 3 | Medium | 20s | Vitalis root distilled through heartleaf. A battlefield medicine, and a costly one. |
| **Dragonscale Draught** (`dragonscale_draught`) | Tonic | 4 | High | 120s | Dragonscale herb burnt with cinder bloom. Heavy, bitter, and worth the burns. |
| **Vitalis Tonic** (`vitalis_tonic`) | Tonic | 4 | High | 60s | An old apothecary formula. For a time the drinker simply has more life in them. |

### Recipe shape

Every medicine is brewed at the Herbalist's Table from a **base**, a **herb** (in a specific
form), often an **extract**, and optionally a **catalyst**. The catalyst decides what the
medicine does *best*; the herb decides what it is. A tonic measured in vials, a tea in a clay cup,
a salve in a jar, an oil in a flask.

## Experimentation

Unknown combinations are not dead ends. Put any base, herb and optional extract or catalyst into
the table and press brew:

| Situation | Result |
| --- | --- |
| Matches a recipe the player has discovered | That medicine, always |
| Matches a recipe the player has *not* discovered | The medicine, plus the discovery |
| No recipe, valid ingredients | An experimental medicine whose properties are computed from the herbs used |
| Spoiled, inert or contradictory ingredients | `failed_mixture` — a wasted brew, no item spam |
| A toxic combination | A medicine with real side effects; toxicity is added, never cancelled for free |

The result is a normal item carrying a `MixtureData` component, so there is **no** runtime item
registration and no unbounded item growth.

## Bases and catalysts

| Base | Effect on the brew |
| --- | --- |
| Water Vial | Neutral, cheap, the baseline every recipe is balanced against |
| Herbal Alcohol | +15% potency, extracts things water cannot |
| Spring Water Cup | Tea base: milder, hydrating |
| Salve Base | Ointment: applied by hand, works on the skin, not the stomach |
| Herbal Oil | Weapon coating base; toxins are stronger, healing is weaker |
| Moonwater | +30% potency, rare, found in ancient places |

| Catalyst | Steers the brew towards |
| --- | --- |
| Purity Salt | Strips out poison; the safe choice |
| Life Essence | Healing and regeneration |
| Frost Crystal | Cooling, suppresses warming |
| Ember Ash | Warming, suppresses cooling |
| Shadow Ichor | Adds toxicity, deepens side effects |

## Toxicity

| Level | Toxin points | Typical source |
| --- | --- | --- |
| None | 0 | Water, teas, most salves |
| Low | 6 | Common tonics |
| Medium | 14 | Strong tonics, most oils |
| High | 26 | Tier 3–4 medicines and poisons |
| Extreme | 45 | Ancient and deliberately poisonous brews |

Toxicity accumulates in the player and decays over time (configurable). Crossing the thresholds
brings nausea, weakness, poison, slowness, dizziness, hunger and a temporary reduction of maximum
health — fair, capped and configurable, never a permanent punishment.

## Freshness

A fresh herb is stronger but has a shelf life measured in days. Dried herbs lose some potency and
keep for a very long time. The tooltip shows which state a stack is in, and the config decides how
long fresh herbs last and how much of their strength they lose when they age.

## Discovery and the Journal

Recipes start as `??? / Unknown Recipe`. They are learned by brewing, by reading journal pages
found in the world, by trading with the herbalist, by finding structures, or by experimenting.
Discovery is permanent per player.

The Herbalist's Journal is a custom item with a custom screen — not a written book. It has six
sections: Herb Encyclopedia, Properties, Recipes, Research, Seasonal Guide and Biome Guide. Every
row is filtered by what the player actually knows, and unknown entries are shown as
`??? / Unknown Property` rather than hidden.

## The Herbalist villager

A full villager profession with the Herbalist's Table as its job site, traded with at Novice →
Apprentice → Journeyman → Expert → Master. Trades change with the villager's biome, so a taiga
herbalist sells what grows in the taiga. Turning the profession off in the config keeps villagers
from taking the job.

## Structures

| Structure | What is inside |
| --- | --- |
| **Herbalist Hut** | A working hut: table, mill, mortar, two drying racks, chest, bookshelves, and a fenced herb garden outside with a water channel and a compost heap |
| **Abandoned Herbalist Hut** | The same hut fallen in — holes in the roof, cobwebs, an overgrown garden, and loot that tells the story of what went wrong |
| **Ancient Apothecary** | A deepslate ruin with a working altar, four pillars, an altar garden of rare herbs and chests of advanced recipes |

They are ordinary `minecraft:jigsaw` structures with normal `random_spread` placement, so they
generate in existing worlds and in datapacks that replace biome tags. Their frequency is data:
edit `data/herbalistscraft/worldgen/structure_set/<name>.json` (spacing and separation) in a
datapack to taste.

## Loot

Herbal materials are added to the structures where they belong — village houses, dungeons, temples,
witch huts, ancient cities, the Nether — through global loot modifiers. Each modifier targets a
sensible list of vanilla tables, rolls once, and adds a small weighted handful rather than
flooding the table. Journal pages carrying recipes are the rarest entry, so exploration stays the
best way to find new recipes.

## Advancements

First Leaf, A Bitter Beginning, Drying Time, Millwright, Field Medicine, Poisoner's Art, Botanical
Scholar (learn 20 herbs), Master Herbalist, Deep Knowledge, Four Seasons, Wanderer, Toxin Survivor,
Trading Herbs and Ancient Knowledge. They are awarded from code, so they can depend on real
player state rather than a datapack trigger.

## Commands

| Command | Reports |
| --- | --- |
| `/herbalist knowledge` | How many herbs, properties and recipes the player knows |
| `/herbalist season` | The season currently in effect and where it comes from |
| `/herbalist toxin` | Current toxin level and how quickly it is decaying |

## Configuration

Config lives in the standard NeoForge files (`config/herbalistscraft-common.toml` and
`-client.toml`).

| Option | Default | What it changes |
| --- | --- | --- |
| `worldgen.enable_wild_herbs` | true | Whether wild herb patches generate at all |
| `worldgen.herb_spawn_rate` | 1.0 | Multiplier on every herb patch attempt |
| `worldgen.rare_herb_spawn_rate` | 1.0 | Extra multiplier for rare and very rare herbs |
| `growth.plant_growth_speed` | 1.0 | Growth speed of planted herbs |
| `growth.regrowth` | true | Plants regrow after harvest instead of dying |
| `growth.seasonal_growth` | true | Herbs respond to seasons |
| `season.provider` | AUTO | Where seasons come from: internal calendar, TAN, Serene Seasons or AUTO |
| `season.internal_length_days` | 24 | Days per season for the internal calendar |
| `season.affects_worldgen` | true | Whether the season affects which herbs generate |
| `medicine.effectiveness` | 1.0 | Potency of every medicine |
| `medicine.cooldown_multiplier` | 1.0 | Medicine cooldowns |
| `medicine.freshness_effect` | true | Whether freshness changes potency |
| `toxicity.enabled` / `multiplier` / `decay_rate` | true / 1.0 / 2 | Toxin accumulation, strength and decay |
| `freshness.enabled` / `days_fresh` / `days_aging` / `drying_ticks` | true / 3 / 3 / 2400 | Shelf life and drying speed |
| `crafting.enable_experimentation` | true | Free-form brewing at the table |
| `crafting.coating_charges` / `coating_duration` | 1.0 / 1.0 | Weapon coating charges and how long they last |
| `crafting.experiment_toxin_scale` | 1.0 | How toxic accidental experiments are |
| `villagers.enabled` / `biome_trades` | true / true | Herbalist villagers and biome-specific trades |
| `integration.tough_as_nails` / `tough_as_nails_seasons` | true / true | Use TAN temperature, hydration and seasons |
| `integration.serene_seasons` | true | Use Serene Seasons for seasons |
| `integration.debug_logging` | false | Log which optional mods were detected |
| `client.show_property_icons` | true | Property icons in tooltips |
| `client.show_hud_indicators` | true | Toxin and temperature indicators on the HUD |
| `client.journal_animations` | true | Page-turn animation in the journal |

## Integrations

Tough As Nails and Serene Seasons are **optional and detected at runtime**, and every access goes
through a small reflective gate in `integration/`, so the mod loads with neither of them present
and simply falls back to its own internal season calendar. Nothing in the integration can grant
invulnerability, and no integration is required for any medicine to work.

## For pack authors

- Herb definitions: `data/herbalistscraft/herb/<id>.json` — category, morphology, properties,
  toxicity, rarity, seasons, biome groups, placement style, soil, light, growth and yield.
- Medicine definitions: `data/herbalistscraft/medicine/<id>.json` — kind, tier, rarity, colour,
  toxicity, cooldown, effects, side effects, description and discovery source.
- Recipes: `data/herbalistscraft/recipe/{mortar,mill,table}/*.json` use the mod's own recipe types.
- The whole pack is generated by `python3 tools/generate.py` from `tools/content/*.json`, and
  `python3 tools/pack_check.py` validates ids, models, textures, language keys, worldgen links and
  the structure templates.

