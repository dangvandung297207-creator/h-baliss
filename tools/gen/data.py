"""Generates every server-side data file: datapack registries, recipes, tags, worldgen,
loot tables, global loot modifiers and advancements."""
from __future__ import annotations

import json
import pathlib

from .biome_groups import BIOME_GROUPS, PLACEMENT_STYLES, RARITY_MULTIPLIER, STRUCTURE_BIOMES

MODID = "herbalistscraft"
DATA_VERSION = 3955  # Minecraft 1.21.1

FORM_ITEMS = {
    "fresh": "{herb}",
    "seed": "{herb}_seeds",
    "dried": "dried_{herb}",
    "powder": "{herb}_powder",
    "extract": "{herb}_extract",
}

TOXICITY_POINTS = {"NONE": 0, "LOW": 6, "MEDIUM": 14, "HIGH": 26, "EXTREME": 45}


def _write(path: pathlib.Path, data) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, indent=2) + "\n")


def form_item(herb_id: str, form: str) -> str:
    return f"{MODID}:" + FORM_ITEMS.get(form, "{herb}").format(herb=herb_id)


# ---------------------------------------------------------------------------
# datapack registries
# ---------------------------------------------------------------------------

def _herb_definitions(data: pathlib.Path, herbs: list, lang: dict, log=print) -> None:
    root = data / "herbalistscraft/herb"
    for herb in herbs:
        definition = {
            "category": herb["category"],
            "morphology": herb.get("morphology", "LEAF"),
            "lore": herb.get("lore", ""),
            "properties": herb["properties"],
            "toxicity": herb.get("toxicity", "NONE"),
            "rarity": herb.get("rarity", "COMMON"),
            "seasons": herb.get("seasons", ["SPRING", "SUMMER", "AUTUMN", "WINTER"]),
            "biome_groups": herb.get("biomes", []),
            "placement": herb.get("placement", "SURFACE"),
            "soil": herb.get("soil", "DIRT"),
            "light": herb.get("light", "BRIGHT"),
            "growth": {
                "speed": herb.get("growthSpeed", 1.0),
                "yield": {"min": herb.get("yield", [1, 2])[0], "max": herb.get("yield", [1, 2])[1]},
                "seed_return": {"min": herb.get("seedReturn", [1, 1])[0], "max": herb.get("seedReturn", [1, 1])[1]},
                "regrow_chance": herb.get("regrowChance", 0.5),
                "fresh_days": herb.get("freshDays", 4),
            },
            "forms": herb.get("forms", ["fresh", "seed"]),
            "uses": herb.get("uses", []),
            "soil_tag": f"#{MODID}:herb_soils/{herb.get('soil', 'DIRT').lower()}",
            "block": f"{MODID}:{herb['id']}_crop",
            "seed_item": f"{MODID}:{herb['id']}_seeds" if "seed" in herb.get("forms", []) else "",
        }
        _write(root / f"{herb['id']}.json", definition)
    log(f"  datapack: {len(herbs)} herb definitions")


def _medicine_definitions(data: pathlib.Path, medicines: list, log=print) -> None:
    root = data / "herbalistscraft/medicine"
    for med in medicines:
        craft = med.get("craft", {})
        definition = {
            "kind": med["kind"],
            "tier": med.get("tier", 1),
            "rarity": med.get("rarity", "COMMON"),
            "color": med.get("color", "#c0c0c0"),
            "toxicity": med.get("toxicity", "NONE"),
            "toxin_points": TOXICITY_POINTS.get(med.get("toxicity", "NONE"), 0),
            "cooldown": med.get("cooldown", 200),
            "description": med.get("description", ""),
            "discovery": med.get("discovery", "EXPERIMENT"),
            "effects": [_effect(e) for e in med.get("effects", [])],
            "side_effects": [_effect(e, default_type="VANILLA") for e in med.get("sideEffects", [])],
            "item": f"{MODID}:{med['id']}",
            "herb_count": sum(1 for key in ("herb", "extract") if craft.get(key)),
        }
        if "coating" in med:
            coating = med["coating"]
            definition["coating"] = {
                "charges": coating.get("charges", 8),
                "duration_ticks": coating.get("durationTicks", 2400),
                "potency": coating.get("potency", 0.7),
                "on_hit": [_effect(e, default_type="VANILLA") for e in coating.get("onHit", [])],
            }
        _write(root / f"{med['id']}.json", definition)
    log(f"  datapack: {len(medicines)} medicine definitions")


def _effect(effect: dict, default_type: str | None = None) -> dict:
    out: dict = {}
    etype = effect.get("type", default_type)
    if etype:
        out["type"] = etype
    for key, target in (("effect", "effect"), ("duration", "duration"), ("amplifier", "amplifier"),
                        ("amount", "amount"), ("chance", "chance"), ("warmth", "warmth"), ("chill", "chill")):
        if key in effect:
            out[target] = effect[key]
    if "effects" in effect:
        out["targets"] = effect["effects"]
    out.setdefault("duration", 0)
    out.setdefault("amplifier", 0)
    out.setdefault("amount", 0)
    out.setdefault("chance", 1.0)
    return out


# ---------------------------------------------------------------------------
# recipes
# ---------------------------------------------------------------------------

def _recipe(path: pathlib.Path, name: str, body: dict) -> None:
    _write(path / f"{name}.json", body)


def _recipes(data: pathlib.Path, herbs: list, medicines: list, log=print) -> None:
    root = data / MODID / "recipe"
    by_id = {h["id"]: h for h in herbs}
    made = 0

    # ---- mortar: fresh or dried herb -> powder ------------------------------
    for herb in herbs:
        forms = herb.get("forms", [])
        if "powder" not in forms:
            continue
        if "fresh" in forms:
            _recipe(root / "mortar", f"{herb['id']}_powder_from_fresh", {
                "type": f"{MODID}:mortar",
                "ingredient": {"item": f"{MODID}:{herb['id']}"},
                "result": {"id": f"{MODID}:{herb['id']}_powder", "count": 1},
                "grind_time": 60,
                "quality": 0.9,
            })
            made += 1
        if "dried" in forms:
            _recipe(root / "mortar", f"{herb['id']}_powder_from_dried", {
                "type": f"{MODID}:mortar",
                "ingredient": {"item": f"{MODID}:dried_{herb['id']}"},
                "result": {"id": f"{MODID}:{herb['id']}_powder", "count": 2},
                "grind_time": 80,
                "quality": 1.15,
            })
            made += 1

    # ---- mill ---------------------------------------------------------------
    for herb in herbs:
        forms = herb.get("forms", [])
        if "fresh" in forms and "dried" in forms:
            _recipe(root / "mill", f"dry_{herb['id']}", {
                "type": f"{MODID}:mill",
                "ingredient": {"item": f"{MODID}:{herb['id']}"},
                "result": {"id": f"{MODID}:dried_{herb['id']}", "count": 1},
                "processing_time": 240,
            })
            made += 1
        if "powder" in forms and "extract" in forms:
            _recipe(root / "mill", f"{herb['id']}_extract", {
                "type": f"{MODID}:mill",
                "ingredient": {"item": f"{MODID}:{herb['id']}_powder"},
                "additive": {"item": f"{MODID}:water_vial"},
                "result": {"id": f"{MODID}:{herb['id']}_extract", "count": 2},
                "processing_time": 320,
            })
            made += 1

    mill_recipes = [
        ("herbal_oil_from_seeds", {"tag": f"{MODID}:mill_seeds"}, None, f"{MODID}:herbal_oil", 1, 200),
        ("herbal_alcohol", {"item": "minecraft:wheat"}, {"item": "minecraft:sugar"}, f"{MODID}:herbal_alcohol", 1, 220),
        ("purity_salt", {"item": "minecraft:sugar"}, {"item": "minecraft:charcoal"}, f"{MODID}:purity_salt", 2, 160),
        ("ember_ash", {"item": "minecraft:blaze_powder"}, None, f"{MODID}:ember_ash", 2, 160),
        ("frost_crystal", {"item": "minecraft:snowball"}, {"item": "minecraft:amethyst_shard"}, f"{MODID}:frost_crystal", 1, 200),
        ("life_essence", {"item": "minecraft:glowstone_dust"}, {"item": f"{MODID}:vitalis_root_powder"}, f"{MODID}:life_essence", 1, 280),
        ("shadow_ichor", {"item": "minecraft:fermented_spider_eye"}, {"item": f"{MODID}:deadly_cap_powder"}, f"{MODID}:shadow_ichor", 1, 280),
        ("salve_base", {"item": "minecraft:honeycomb"}, {"item": f"{MODID}:herbal_oil"}, f"{MODID}:salve_base", 1, 220),
        ("moonwater", {"item": f"{MODID}:water_vial"}, {"item": f"{MODID}:moonflower_powder"}, f"{MODID}:moonwater", 1, 320),
    ]
    for name, ingredient, additive, result, count, time in mill_recipes:
        body = {
            "type": f"{MODID}:mill",
            "ingredient": ingredient,
            "result": {"id": result, "count": count},
            "processing_time": time,
        }
        if additive:
            body["additive"] = additive
        _recipe(root / "mill", name, body)
        made += 1

    # ---- the Herbalist's Table ---------------------------------------------
    for med in medicines:
        craft = med.get("craft", {})
        if not craft:
            continue
        herb_id = craft.get("herb")
        form = craft.get("form", "powder")
        body = {
            "type": f"{MODID}:table",
            "base": {"item": f"{MODID}:{craft['base']}"},
            "herb": {"item": form_item(herb_id, form)},
            "result": {"id": f"{MODID}:{med['id']}", "count": 1},
            "brew_time": craft.get("time", 200),
            "medicine": f"{MODID}:{med['id']}",
        }
        if craft.get("extract"):
            body["extract"] = {"item": form_item(craft["extract"], "extract")}
        if craft.get("catalyst"):
            body["catalyst"] = {"item": f"{MODID}:{craft['catalyst']}"}
        _recipe(root / "table", med["id"], body)
        made += 1

    # ---- vanilla crafting: containers, tools, journal -----------------------
    crafting = [
        ("glass_vial", {"type": "minecraft:crafting_shaped", "pattern": ["G G", " G "],
                        "key": {"G": {"item": "minecraft:glass"}},
                        "result": {"id": f"{MODID}:glass_vial", "count": 3}}),
        ("clay_cup", {"type": "minecraft:crafting_shaped", "pattern": ["C C", " C "],
                      "key": {"C": {"item": "minecraft:clay_ball"}},
                      "result": {"id": f"{MODID}:clay_cup", "count": 2}}),
        ("herbalists_journal", {"type": "minecraft:crafting_shapeless",
                                "ingredients": [{"item": "minecraft:book"}, {"item": "minecraft:leather"},
                                                {"item": f"{MODID}:golden_chamomile"}],
                                "result": {"id": f"{MODID}:herbalists_journal", "count": 1}}),
        ("pruning_shears", {"type": "minecraft:crafting_shaped", "pattern": [" I", "I "],
                            "key": {"I": {"item": "minecraft:iron_ingot"}},
                            "result": {"id": f"{MODID}:pruning_shears", "count": 1}}),
        ("seed_pouch", {"type": "minecraft:crafting_shapeless",
                        "ingredients": [{"item": "minecraft:leather"}, {"item": "minecraft:string"},
                                        {"tag": f"{MODID}:herb_seeds"}],
                        "result": {"id": f"{MODID}:seed_pouch", "count": 1}}),
        ("mortar_and_pestle", {"type": "minecraft:crafting_shaped", "pattern": ["S S", " S "],
                               "key": {"S": {"item": "minecraft:stone"}},
                               "result": {"id": f"{MODID}:mortar_and_pestle", "count": 1}}),
        ("herbal_mill", {"type": "minecraft:crafting_shaped", "pattern": ["PPP", "PSP", "PPP"],
                         "key": {"P": {"tag": "minecraft:planks"}, "S": {"item": "minecraft:stone"}},
                         "result": {"id": f"{MODID}:herbal_mill", "count": 1}}),
        ("herbalists_table", {"type": "minecraft:crafting_shaped", "pattern": ["PPP", "LML", "L L"],
                              "key": {"P": {"tag": "minecraft:planks"}, "L": {"tag": "minecraft:logs"},
                                      "M": {"item": f"{MODID}:mortar_and_pestle"}},
                              "result": {"id": f"{MODID}:herbalists_table", "count": 1}}),
        ("drying_rack", {"type": "minecraft:crafting_shaped", "pattern": ["SSS", "S S", "S S"],
                         "key": {"S": {"item": "minecraft:stick"}},
                         "result": {"id": f"{MODID}:drying_rack", "count": 1}}),
    ]
    for name, body in crafting:
        _recipe(root, name, body)
        made += 1

    log(f"  recipes: {made} recipe files")
    return made


# ---------------------------------------------------------------------------
# tags
# ---------------------------------------------------------------------------

def _tags(root: pathlib.Path, herbs: list, log=print) -> None:
    # Everything below is machine-generated; keep it out of src/main/resources.
    root = root / "src/generated/resources"
    tags = root / "data" / MODID / "tags"
    item_values = {
        "herbs": [f"{MODID}:{h['id']}" for h in herbs],
        "dried_herbs": [f"{MODID}:dried_{h['id']}" for h in herbs if "dried" in h.get("forms", [])],
        "herb_powders": [f"{MODID}:{h['id']}_powder" for h in herbs if "powder" in h.get("forms", [])],
        "herb_extracts": [f"{MODID}:{h['id']}_extract" for h in herbs if "extract" in h.get("forms", [])],
        "herb_seeds": [f"{MODID}:{h['id']}_seeds" for h in herbs if "seed" in h.get("forms", [])],
        "mill_seeds": [
            f"{MODID}:{h['id']}_seeds" for h in herbs if "seed" in h.get("forms", [])
        ] + ["minecraft:wheat_seeds", "minecraft:melon_seeds", "minecraft:pumpkin_seeds", "minecraft:beetroot_seeds"],
        "herbal_bases": [f"{MODID}:{b}" for b in ("water_vial", "spring_water_cup", "herbal_oil",
                                                  "herbal_alcohol", "salve_base", "moonwater")],
        "catalysts": [f"{MODID}:{c}" for c in ("purity_salt", "ember_ash", "frost_crystal",
                                               "life_essence", "shadow_ichor")],
        "coatable_weapons": ["#minecraft:swords", "#minecraft:axes", "#minecraft:hoes",
                             "minecraft:trident", "minecraft:arrow", "#minecraft:pickaxes", "#minecraft:shovels"],
        "herbal_materials": ["#herbalistscraft:herbs", "#herbalistscraft:herb_powders",
                             "#herbalistscraft:herb_extracts", "#herbalistscraft:dried_herbs"],
    }
    for name, values in item_values.items():
        _write(tags / "item" / f"{name}.json", {"replace": False, "values": values})

    # toxic herbs are useful to other mods and to our own logic
    toxic = [f"{MODID}:{h['id']}" for h in herbs if h.get("toxicity", "NONE") in ("HIGH", "EXTREME")]
    _write(tags / "item" / "toxic_herbs.json", {"replace": False, "values": toxic})
    healing = [f"{MODID}:{h['id']}" for h in herbs if h["properties"].get("HEALING")]
    _write(tags / "item" / "healing_herbs.json", {"replace": False, "values": healing})
    warming = [f"{MODID}:{h['id']}" for h in herbs if h["properties"].get("WARMING")]
    _write(tags / "item" / "warming_herbs.json", {"replace": False, "values": warming})
    cooling = [f"{MODID}:{h['id']}" for h in herbs if h["properties"].get("COOLING")]
    _write(tags / "item" / "cooling_herbs.json", {"replace": False, "values": cooling})

    # block tags: crops and the soils each soil type accepts
    _write(tags / "block" / "herb_crops.json", {"replace": False,
           "values": [f"{MODID}:{h['id']}_crop" for h in herbs]})
    soils = {
        "dirt": ["minecraft:dirt", "minecraft:grass_block", "minecraft:coarse_dirt", "minecraft:rooted_dirt",
                 "minecraft:podzol", "minecraft:mycelium"],
        "farmland": ["minecraft:farmland"],
        "sand": ["minecraft:sand", "minecraft:red_sand", "minecraft:gravel"],
        "moss": ["minecraft:moss_block", "minecraft:moss_carpet", "minecraft:clay", "minecraft:mud",
                 "minecraft:muddy_mangrove_roots", "minecraft:sculk", "minecraft:deepslate"],
        "netherrack": ["minecraft:netherrack", "minecraft:blackstone", "minecraft:basalt", "minecraft:magma_block"],
        "soul_soil": ["minecraft:soul_soil", "minecraft:soul_sand", "minecraft:warped_nylium", "minecraft:crimson_nylium"],
        "water_edge": ["minecraft:sand", "minecraft:dirt", "minecraft:clay", "minecraft:gravel",
                       "minecraft:mud", "minecraft:moss_block", "minecraft:stone"],
    }
    for name, values in soils.items():
        _write(tags / "block" / "herb_soils" / f"{name}.json", {"replace": False, "values": values})

    # biome group tags + per-herb tags that reference them
    for group, values in BIOME_GROUPS.items():
        _write(tags / "worldgen/biome/herb_biomes" / f"{group}.json", {"replace": False, "values": values})
    for herb in herbs:
        groups = herb.get("biomes", [])
        if not groups:
            continue
        values = [f"#{MODID}:herb_biomes/{g}" for g in groups]
        _write(tags / "worldgen/biome/herb_biomes" / f"{herb['id']}.json", {"replace": False, "values": values})

    for name, values in STRUCTURE_BIOMES.items():
        _write(tags / "worldgen/biome" / f"has_structure/{name}.json", {"replace": False, "values": values})

    log(f"  tags: biome groups, herbs, soils and item tags written")


# ---------------------------------------------------------------------------
# worldgen
# ---------------------------------------------------------------------------

def _worldgen(root: pathlib.Path, herbs: list, log=print) -> None:
    # Everything below is machine-generated; keep it out of src/main/resources.
    root = root / "src/generated/resources"
    cfg = root / "data" / MODID / "worldgen/configured_feature"
    placed = root / "data" / MODID / "worldgen/placed_feature"
    modifiers = root / "data" / MODID / "neoforge/biome_modifier"
    for herb in herbs:
        style = PLACEMENT_STYLES.get(herb.get("placement", "SURFACE"), PLACEMENT_STYLES["SURFACE"])
        rarity = RARITY_MULTIPLIER.get(herb.get("rarity", "COMMON"), 1.0)
        count = max(1, round(style["count"] * rarity))

        _write(cfg / f"herb_{herb['id']}.json", {
            "type": f"{MODID}:herb_patch",
            "config": {
                "herb": f"{MODID}:{herb['id']}_crop",
                "tries": style["tries"],
                "xz_spread": style["spread"],
                "y_spread": 1 if "heightmap" in style else 3,
            },
        })

        placement = [{"type": "minecraft:count", "count": count}, {"type": "minecraft:in_square"}]
        if "height_range" in style:
            low, high = style["height_range"]
            placement.append({
                "type": "minecraft:height_range",
                "height": {"type": "minecraft:uniform",
                           "min_inclusive": {"absolute": low},
                           "max_inclusive": {"absolute": high}},
            })
            placement.append({"type": "minecraft:block_predicate_filter",
                              "predicate": {"type": "minecraft:matching_blocks", "blocks": "minecraft:air"}})
        else:
            placement.append({"type": "minecraft:heightmap", "heightmap": style["heightmap"]})
        placement.append({"type": "minecraft:biome"})
        _write(placed / f"herb_{herb['id']}.json", {
            "feature": f"{MODID}:herb_{herb['id']}",
            "placement": placement,
        })

        _write(modifiers / f"add_herb_{herb['id']}.json", [{
            "type": "neoforge:add_features",
            "biomes": f"#{MODID}:herb_biomes/{herb['id']}",
            "features": f"{MODID}:herb_{herb['id']}",
            "step": "vegetal_decoration",
        }])

    log(f"  worldgen: {len(herbs)} configured features, placed features and biome modifiers")


# ---------------------------------------------------------------------------
# loot
# ---------------------------------------------------------------------------

def _loot(root: pathlib.Path, herbs: list, log=print) -> None:
    # Everything below is machine-generated; keep it out of src/main/resources.
    root = root / "src/generated/resources"
    tables = root / "data" / MODID / "loot_table"
    by_rarity: dict[str, list] = {}
    for herb in herbs:
        by_rarity.setdefault(herb.get("rarity", "COMMON"), []).append(herb)
    seeds = [f"{MODID}:{h['id']}_seeds" for h in herbs if "seed" in h.get("forms", [])]
    fresh = [f"{MODID}:{h['id']}" for h in herbs]
    dried = [f"{MODID}:dried_{h['id']}" for h in herbs if "dried" in h.get("forms", [])]
    powders = [f"{MODID}:{h['id']}_powder" for h in herbs if "powder" in h.get("forms", [])]
    extracts = [f"{MODID}:{h['id']}_extract" for h in herbs if "extract" in h.get("forms", [])]
    rare_herbs = [f"{MODID}:{h['id']}" for h in herbs if h.get("rarity") in ("RARE", "VERY_RARE")]

    def stack(item, min_c=1, max_c=1, weight=1):
        return {"type": "minecraft:item", "weight": weight, "name": item,
                "functions": [{"function": "minecraft:set_count",
                               "count": {"type": "minecraft:uniform", "min": min_c, "max": max_c}}]}

    def pool(entries, rolls=1):
        return {"rolls": rolls, "entries": entries}

    hut = {"type": "minecraft:chest", "pools": [
        pool([stack(s, 1, 3, 6) for s in seeds] + [stack(s, 1, 2, 4) for s in fresh]),
        pool([stack(s, 1, 2, 5) for s in dried] + [stack(s, 1, 2, 3) for s in powders]),
        pool([stack(f"{MODID}:glass_vial", 1, 4, 5), stack(f"{MODID}:clay_cup", 1, 3, 4),
              stack(f"{MODID}:seed_pouch", 1, 1, 1), stack(f"{MODID}:pruning_shears", 1, 1, 1),
              stack("minecraft:bread", 1, 3, 2)], rolls=2),
        pool([stack(f"{MODID}:journal_page_herbal", 1, 1, 3), stack(f"{MODID}:journal_page_medicinal", 1, 1, 2),
              stack(f"{MODID}:purity_salt", 1, 2, 2)], rolls=1),
    ]}
    abandoned = {"type": "minecraft:chest", "pools": [
        pool([stack(s, 1, 2, 5) for s in dried] + [stack(f"{MODID}:{h}", 1, 1, 3) for h in
                                                   ("nightshade", "viperweed", "hemlock", "deadly_cap", "wolfsbane")]),
        pool([stack(f"{MODID}:journal_page_toxic", 1, 1, 3), stack(f"{MODID}:journal_page_medicinal", 1, 1, 2),
              stack(f"{MODID}:shadow_ichor", 1, 1, 2), stack(f"{MODID}:toxic_oil", 1, 1, 2)], rolls=2),
        pool([stack("minecraft:bone", 1, 4, 3), stack("minecraft:rotten_flesh", 1, 3, 2),
              stack("minecraft:web", 1, 2, 2), stack("minecraft:coal", 1, 5, 3)], rolls=2),
    ]}
    apothecary = {"type": "minecraft:chest", "pools": [
        pool([stack(s, 1, 1, 2) for s in rare_herbs] + [stack(s, 1, 2, 3) for s in extracts]),
        pool([stack(f"{MODID}:life_essence", 1, 2, 3), stack(f"{MODID}:moonwater", 1, 1, 2),
              stack(f"{MODID}:shadow_ichor", 1, 2, 3), stack(f"{MODID}:frost_crystal", 1, 3, 3),
              stack(f"{MODID}:ember_ash", 1, 3, 3)]),
        pool([stack(f"{MODID}:journal_page_ancient", 1, 1, 4), stack(f"{MODID}:journal_page_toxic", 1, 1, 2),
              stack(f"{MODID}:journal_page_medicinal", 1, 2, 2)]),
        pool([stack(f"{MODID}:dragonscale_draught", 1, 1, 1), stack(f"{MODID}:venom_oil", 1, 1, 1),
              stack(f"{MODID}:vitalis_tonic", 1, 2, 2), stack(f"{MODID}:focus_tonic", 1, 2, 2)], rolls=2),
        pool([stack("minecraft:gold_ingot", 1, 4, 2), stack("minecraft:lapis_lazuli", 2, 6, 3),
              stack("minecraft:emerald", 1, 3, 2), stack("minecraft:glowstone_dust", 2, 6, 3)]),
    ]}
    _write(tables / "chests/herbalist_hut.json", hut)
    _write(tables / "chests/abandoned_herbalist_hut.json", abandoned)
    _write(tables / "chests/ancient_apothecary.json", apothecary)

    # global loot modifiers: herbal materials sprinkled into vanilla dungeon loot
    def entry(item, weight, min_c=1, max_c=1):
        return {"item": item, "weight": weight, "min_count": min_c, "max_count": max_c}

    modifier_sets = {
        "village_herbs": ([
            "minecraft:chests/village/village_plains_house", "minecraft:chests/village/village_taiga_house",
            "minecraft:chests/village/village_savanna_house", "minecraft:chests/village/village_desert_house",
            "minecraft:chests/village/village_snowy_house", "minecraft:chests/village/village_temple",
            "minecraft:chests/village/village_toolsmith", "minecraft:chests/village/village_mason",
        ], [
            entry("herbalistscraft:journal_page_herbal", 3), entry("herbalistscraft:glass_vial", 4, 1, 3),
            entry("herbalistscraft:golden_chamomile", 3), entry("herbalistscraft:cleansage", 3),
            entry("herbalistscraft:marshroot", 3), entry("herbalistscraft:bitterroot", 2),
            entry("herbalistscraft:lavender", 3), entry("herbalistscraft:seed_pouch", 1),
        ]),
        "dungeon_herbs": ([
            "minecraft:chests/simple_dungeon", "minecraft:chests/abandoned_mineshaft",
            "minecraft:chests/stronghold_corridor", "minecraft:chests/stronghold_crossing",
            "minecraft:chests/stronghold_library", "minecraft:chests/shipwreck_supply",
            "minecraft:chests/pillager_outpost", "minecraft:chests/underwater_ruin_big",
            "minecraft:chests/underwater_ruin_small",
        ], [
            entry("herbalistscraft:journal_page_medicinal", 3), entry("herbalistscraft:dried_bloodroot", 3, 1, 2),
            entry("herbalistscraft:healing_tonic", 2), entry("herbalistscraft:antiseptic_salve", 2),
            entry("herbalistscraft:comfrey", 3), entry("herbalistscraft:yarrow", 3),
            entry("herbalistscraft:pruning_shears", 1),
        ]),
        "temple_herbs": ([
            "minecraft:chests/jungle_temple", "minecraft:chests/jungle_temple_dispenser",
            "minecraft:chests/desert_pyramid", "minecraft:chests/woodland_mansion",
            "minecraft:chests/igloo_chest", "minecraft:chests/buried_treasure",
        ], [
            entry("herbalistscraft:journal_page_ancient", 2), entry("herbalistscraft:wild_ginseng", 3),
            entry("herbalistscraft:vitalis_root", 3), entry("herbalistscraft:sunleaf", 3),
            entry("herbalistscraft:life_essence", 2), entry("herbalistscraft:spiritwood", 2),
            entry("herbalistscraft:glacier_lotus", 1),
        ]),
        "swamp_witch_herbs": ([
            "minecraft:chests/spawn_bonus_chest", "minecraft:chests/shipwreck_treasure",
        ], [
            entry("herbalistscraft:journal_page_toxic", 3), entry("herbalistscraft:nightshade", 4),
            entry("herbalistscraft:toxic_oil", 3), entry("herbalistscraft:shadow_ichor", 2),
            entry("herbalistscraft:hemlock", 3), entry("herbalistscraft:sleepwort", 2),
        ]),
        "ancient_city_herbs": ([
            "minecraft:chests/ancient_city", "minecraft:chests/ancient_city_ice_box",
            "minecraft:chests/end_city_treasure",
        ], [
            entry("herbalistscraft:journal_page_ancient", 3), entry("herbalistscraft:moonwater", 2),
            entry("herbalistscraft:grave_moss", 3), entry("herbalistscraft:venom_oil", 1),
            entry("herbalistscraft:vitalis_tonic", 1), entry("herbalistscraft:dragonscale_herb", 1),
        ]),
        "nether_herbs": ([
            "minecraft:chests/nether_bridge", "minecraft:chests/bastion_treasure",
            "minecraft:chests/bastion_other", "minecraft:chests/bastion_bridge",
            "minecraft:chests/bastion_hoglin_stable", "minecraft:chests/ruined_portal",
        ], [
            entry("herbalistscraft:emberroot", 3), entry("herbalistscraft:cinder_bloom", 2),
            entry("herbalistscraft:ashleaf", 3), entry("herbalistscraft:ember_ash", 3, 1, 2),
            entry("herbalistscraft:dragonscale_herb", 1), entry("herbalistscraft:blaze_pepper", 2),
        ]),
    }
    modifiers = root / "data" / MODID / "loot_modifiers"
    manifest_entries = []
    for name, (tables_list, entries) in modifier_sets.items():
        any_of = {"condition": "minecraft:any_of",
                  "terms": [{"condition": "neoforge:loot_table_id", "loot_table_id": table}
                            for table in tables_list]}
        _write(modifiers / f"{name}.json", {
            "type": f"{MODID}:herb_loot",
            "conditions": [any_of],
            "entries": entries,
            "rolls": 1,
            "chance": 0.6,
        })
        manifest_entries.append(f"{MODID}:{name}")
    _write(root / "data/neoforge/loot_modifiers/global_loot_modifiers.json",
           {"entries": manifest_entries, "replace": False})
    log(f"  loot: 3 chest tables and {len(manifest_entries)} global loot modifiers")


# ---------------------------------------------------------------------------
# advancements
# ---------------------------------------------------------------------------

def _advancements(root: pathlib.Path, herbs: list, medicines: list, lang: dict, log=print) -> None:
    # Everything below is machine-generated; keep it out of src/main/resources.
    root = root / "src/generated/resources"
    """All Herbalist's Craft advancements use the `minecraft:impossible` criterion and are
    awarded from code by AdvancementGrants, which owns the real (data-driven) conditions."""
    adv = root / "data" / MODID / "advancement"
    common_herbs = [h for h in herbs if h.get("rarity") == "COMMON"]
    tier4 = [m for m in medicines if m.get("tier", 1) >= 4]
    criteria = {"granted": {"trigger": "minecraft:impossible"}}

    def frame(name, parent, icon, frame_type="task", hidden=False, background=None):
        display = {
            "icon": {"id": icon},
            "title": {"translate": f"advancement.{MODID}.{name}.title"},
            "description": {"translate": f"advancement.{MODID}.{name}.description"},
            "frame": frame_type,
            "show_toast": True,
            "announce_to_chat": True,
            "hidden": hidden,
        }
        if name == "root":
            display["background"] = background or "minecraft:textures/gui/advancements/backgrounds/husbandry.png"
        _write(adv / f"{name}.json", {"parent": parent, "criteria": criteria, "display": display})

    frame("root", "minecraft:husbandry/root", f"{MODID}:herbalists_journal")
    frame("first_leaf", f"{MODID}:root", f"{MODID}:bloodroot")
    frame("bitter_beginning", f"{MODID}:first_leaf", f"{MODID}:mortar_and_pestle")
    frame("drying_time", f"{MODID}:bitter_beginning", f"{MODID}:drying_rack")
    frame("millwright", f"{MODID}:drying_time", f"{MODID}:herbal_mill")
    frame("field_medicine", f"{MODID}:bitter_beginning", f"{MODID}:minor_healing_tonic")
    frame("poisoners_art", f"{MODID}:field_medicine", f"{MODID}:toxic_oil", "goal")
    frame("botanical_scholar", f"{MODID}:field_medicine", f"{MODID}:herbalists_journal", "goal")
    frame("master_herbalist", f"{MODID}:botanical_scholar", f"{MODID}:golden_chamomile", "challenge")
    frame("deep_knowledge", f"{MODID}:botanical_scholar", f"{MODID}:strong_healing_tonic", "goal")
    frame("four_seasons", f"{MODID}:botanical_scholar", f"{MODID}:sun_tea", "goal")
    frame("wanderer", f"{MODID}:botanical_scholar", f"{MODID}:moonfrost_berry", "goal")
    frame("toxin_survivor", f"{MODID}:field_medicine", f"{MODID}:detox_tonic", "goal")
    frame("trading_herbs", f"{MODID}:first_leaf", f"{MODID}:seed_pouch")
    frame("ancient_knowledge", f"{MODID}:deep_knowledge", f"{MODID}:dragonscale_draught", "challenge")

    log(f"  advancements: 15 advancement files (root, first_leaf, bitter_beginning, drying_time, millwright, "
        f"field_medicine, poisoners_art, botanical_scholar [{len(common_herbs)} common herbs], master_herbalist, "
        f"deep_knowledge, four_seasons, wanderer, toxin_survivor, trading_herbs, ancient_knowledge [{len(tier4)} tier 4])")


# ---------------------------------------------------------------------------

def generate(root: pathlib.Path, herbs: list, medicines: list, lang: dict, log=print) -> dict:
    data = root / "src/main/resources/data"
    _herb_definitions(data, herbs, lang, log)
    _medicine_definitions(data, medicines, log)
    recipes = _recipes(data, herbs, medicines, log)
    _structures(root, log)
    _tags(root, herbs, log)
    _worldgen(root, herbs, log)
    _loot(root, herbs, log)
    _advancements(root, herbs, medicines, lang, log)
    return {"recipes": recipes, "data_version": DATA_VERSION}


def _structures(root: pathlib.Path, log=print) -> None:
    from . import structures

    structures.generate(root, log=log)
