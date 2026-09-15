"""Generates every client-side asset: item models, block models, blockstates, lang,
particle definitions and the textures behind all of them."""
from __future__ import annotations

import json
import pathlib

from . import art
from .png import Image, parse_color, write_png

MODID = "herbalistscraft"

UI_ICON_DIR = "textures/gui/icons"


def _write_json(path: pathlib.Path, data) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, indent=2) + "\n")


def _item_model(path: pathlib.Path, texture: str, layers: int = 1) -> None:
    textures = {f"layer{i}": f"{MODID}:item/{texture}" if i == 0 else f"{MODID}:item/{texture}_{i}" for i in range(layers)}
    _write_json(path, {"parent": "minecraft:item/generated", "textures": textures})



def _cube_model(textures: dict, elements: list, ambient_occlusion: bool = True) -> dict:
    """A block model built from explicit element boxes (never a plain cube)."""
    particle = textures.get("side") or next(iter(textures.values()))
    return {
        "parent": "minecraft:block/block",
        "textures": {"particle": f"{MODID}:block/{particle}",
                     **{key: f"{MODID}:block/{name}" for key, name in textures.items()}},
        "elements": elements,
        "ambientocclusion": ambient_occlusion,
    }


# ---------------------------------------------------------------------------
# block models
# ---------------------------------------------------------------------------

def mortar_model() -> dict:
    def box(frm, to, tex, sides=None):
        faces = {}
        for name in ("down", "up", "north", "south", "west", "east"):
            faces[name] = {"texture": f"#{sides.get(name, tex) if sides else tex}", "uv": [0, 0, 16, 16]}
        return {"from": list(frm), "to": list(to), "faces": faces}

    elements = [
        box([3, 0, 3], [13, 2, 13], "side", {"up": "top"}),
        box([2, 2, 2], [14, 3, 14], "rim", {"up": "top", "down": "side"}),
        box([4, 3, 4], [12, 5, 12], "pestle", {"up": "top"}),
        {
            "from": [7, 5, 7], "to": [9, 13, 9],
            "rotation": {"origin": [8, 5, 8], "axis": "z", "angle": -22.5, "rescale": True},
            "faces": {name: {"texture": "#pestle", "uv": [0, 0, 16, 16]} for name in
                      ("down", "up", "north", "south", "west", "east")},
        },
    ]
    return _cube_model({"side": "mortar_side", "top": "mortar_top", "rim": "mortar_rim", "pestle": "pestle"}, elements)


def mill_model() -> dict:
    def box(frm, to, tex, sides=None):
        faces = {}
        for name in ("down", "up", "north", "south", "west", "east"):
            faces[name] = {"texture": f"#{sides.get(name, tex) if sides else tex}", "uv": [0, 0, 16, 16]}
        return {"from": list(frm), "to": list(to), "faces": faces}

    elements = [
        box([1, 0, 1], [15, 11, 15], "side", {"up": "top", "down": "side"}),
        box([0, 11, 2], [16, 12, 14], "planks"),
        box([2, 12, 4], [14, 13, 12], "planks"),
        box([2, 13, 4], [14, 14, 12], "planks"),
        {
            "from": [13, 6, 6], "to": [16, 8, 10],
            "rotation": {"origin": [14, 7, 8], "axis": "z", "angle": 15, "rescale": True},
            "faces": {name: {"texture": "#planks", "uv": [0, 0, 16, 16]} for name in
                      ("down", "up", "north", "south", "west", "east")},
        },
    ]
    return _cube_model({"side": "mill_side", "front": "mill_front", "top": "mill_top", "planks": "mill_planks"}, elements)


def table_model() -> dict:
    def box(frm, to, tex):
        return {"from": list(frm), "to": list(to),
                "faces": {name: {"texture": f"#{tex}", "uv": [0, 0, 16, 16]} for name in
                          ("down", "up", "north", "south", "west", "east")}}

    def faced(frm, to, mapping):
        faces = {}
        for name in ("down", "up", "north", "south", "west", "east"):
            faces[name] = {"texture": f"#{mapping.get(name, 'side')}", "uv": [0, 0, 16, 16]}
        return {"from": list(frm), "to": list(to), "faces": faces}

    elements = [
        faced([0, 12, 0], [16, 14, 16], {"up": "top", "down": "side"}),
        box([1, 2, 1], [3, 12, 3], "planks"),
        box([13, 2, 1], [15, 12, 3], "planks"),
        box([1, 2, 13], [3, 12, 15], "planks"),
        box([13, 2, 13], [15, 12, 15], "planks"),
        box([2, 4, 2], [14, 5, 14], "planks"),
        box([3, 5, 3], [6, 9, 6], "mortar_side"),
        box([10, 5, 10], [13, 8, 13], "glass"),
    ]
    return _cube_model({"top": "table_top", "side": "table_side", "planks": "mill_planks",
                        "mortar_side": "mortar_side", "glass": "vial_glass"}, elements)


def rack_model() -> dict:
    def box(frm, to, tex):
        return {"from": list(frm), "to": list(to),
                "faces": {name: {"texture": f"#{tex}", "uv": [0, 0, 16, 16]} for name in
                          ("down", "up", "north", "south", "west", "east")}}

    elements = [
        box([1, 0, 1], [3, 14, 3], "wood"),
        box([13, 0, 1], [15, 14, 3], "wood"),
        box([1, 0, 13], [3, 14, 15], "wood"),
        box([13, 0, 13], [15, 14, 15], "wood"),
        box([1, 12, 1], [15, 13, 3], "wood"),
        box([1, 12, 13], [15, 13, 15], "wood"),
        box([0, 11, 0], [16, 12, 16], "wood"),
        box([2, 6, 2], [14, 7, 14], "wood"),
    ]
    return _cube_model({"wood": "rack_wood"}, elements)


# ---------------------------------------------------------------------------
# textures
# ---------------------------------------------------------------------------

def _block_textures(out: pathlib.Path) -> None:
    block = out / "textures/block"
    write_png(block / "mortar_side.png", art.mortar_side_texture())
    write_png(block / "mortar_top.png", art.mortar_top_texture())
    write_png(block / "mortar_rim.png", art.stone_texture((156, 152, 144, 255), "rim"))
    write_png(block / "pestle.png", art.wood_texture((126, 94, 60, 255), "pestle"))
    write_png(block / "mill_side.png", art.mill_side_texture())
    write_png(block / "mill_front.png", art.mill_front_texture())
    write_png(block / "mill_top.png", art.mill_top_texture())
    write_png(block / "mill_planks.png", art.wood_texture((148, 110, 68, 255), "mill_planks"))
    write_png(block / "table_top.png", art.table_top_texture())
    write_png(block / "table_side.png", art.table_side_texture())
    write_png(block / "rack_wood.png", art.wood_texture((150, 114, 70, 255), "rack"))
    write_png(block / "vial_glass.png", art.vial_sprite(None).shade(1.15))
    # liquids used by the mill/table in block models and by fluid-less bottles
    for name, color in (("liquid_water", "#5f8fc9"), ("liquid_oil", "#d8b94a"), ("liquid_green", "#6f9e4c")):
        write_png(block / f"{name}.png", art.powder_pile_sprite(color).scaled_center(1.3))


def _gui_textures(out: pathlib.Path, properties: dict) -> None:
    gui = out / "textures/gui"
    write_png(gui / "mill.png", art.gui_mill())
    write_png(gui / "table.png", art.gui_table())
    write_png(gui / "journal.png", art.gui_journal())
    write_png(gui / f"{UI_ICON_DIR}/arrow.png", art.arrow_icon())
    write_png(gui / f"{UI_ICON_DIR}/check.png", art.check_icon())
    write_png(gui / f"{UI_ICON_DIR}/lock.png", art.lock_icon())
    for rarity in ("COMMON", "UNCOMMON", "RARE", "VERY_RARE", "LEGENDARY"):
        write_png(gui / f"{UI_ICON_DIR}/rarity_{rarity.lower()}.png", art.rarity_badge(rarity))
    for tab in ("herbs", "properties", "recipes", "research", "seasons", "biomes"):
        write_png(gui / f"{UI_ICON_DIR}/tab_{tab}.png", art.tab_icon(tab))
    for key, info in properties.items():
        write_png(gui / f"{UI_ICON_DIR}/property_{info['icon']}.png", art.property_icon(info["icon"], info["color"]))


def _particle_textures(out: pathlib.Path) -> list:
    names = ["herbal_spark", "frost_spark", "ember_spark", "toxic_smoke", "dried_leaf"]
    for name in names:
        write_png(out / f"textures/particle/{name}.png", art.particle_sprite(name))
        _write_json(out / f"particles/{name}.json", {"textures": [f"{MODID}:{name}"]})
    return names


# ---------------------------------------------------------------------------
# lang
# ---------------------------------------------------------------------------

def _herb_name(herb: dict) -> str:
    return herb["name"]


def generate(root: pathlib.Path, herbs: list, medicines: list, items: list, experimental: list,
             lang: dict, log=print) -> dict:
    res = root / "src/main/resources"
    assets = res / "assets" / MODID
    item_tex = assets / "textures/item"
    entries: dict[str, str] = {}
    counts = {"item_textures": 0, "item_models": 0, "blockstates": 0, "block_models": 0}

    # ---- herb items: fresh, seeds, dried, powder, extract --------------------
    herb_items: dict[str, dict] = {}
    for herb in herbs:
        forms = herb.get("forms", ["fresh", "seed"])
        sprite = art.herb_sprite(herb)
        drawings = {
            "fresh": sprite,
            "seed": art.seed_sprite(herb),
            "dried": art.dried_sprite(herb),
            "powder": art.powder_sprite(herb),
            "extract": art.extract_sprite(herb),
        }
        for form in forms:
            item_id = {"fresh": herb["id"], "seed": f"{herb['id']}_seeds", "dried": f"dried_{herb['id']}",
                       "powder": f"{herb['id']}_powder", "extract": f"{herb['id']}_extract"}[form]
            write_png(item_tex / f"{item_id}.png", drawings[form])
            _item_model(assets / "models/item" / f"{item_id}.json", item_id)
            suffix = {"fresh": "", "seed": " Seeds", "dried": "Dried ", "powder": " Powder", "extract": " Extract"}[form]
            if form == "seed":
                entries[f"item.{MODID}.{item_id}"] = f"{herb['name']} Seeds"
            elif form == "dried":
                entries[f"item.{MODID}.{item_id}"] = f"Dried {herb['name']}"
            elif form == "powder":
                entries[f"item.{MODID}.{item_id}"] = f"{herb['name']} Powder"
            elif form == "extract":
                entries[f"item.{MODID}.{item_id}"] = f"{herb['name']} Extract"
            else:
                entries[f"item.{MODID}.{item_id}"] = herb["name"]
            herb_items[f"{herb['id']}:{form}"] = {"id": item_id, "sprite": item_id}
            counts["item_textures"] += 1
            counts["item_models"] += 1

        # growth stage textures (crops are drawn as crosses, one texture per stage)
        for stage in range(4):
            write_png(assets / "textures/block" / f"{herb['id']}_stage{stage}.png",
                      art.crop_stage_sprite(herb, stage))
        entries[f"block.{MODID}.{herb['id']}_crop"] = f"{herb['name']}"
        counts["item_textures"] += 1

    # ---- block textures ------------------------------------------------------
    _block_textures(assets)

    # ---- workstation block models + blockstates ------------------------------
    block_models = {
        "mortar_and_pestle": mortar_model(),
        "herbal_mill": mill_model(),
        "herbalists_table": table_model(),
        "drying_rack": rack_model(),
    }
    for name, model in block_models.items():
        _write_json(assets / "models/block" / f"{name}.json", model)
        counts["block_models"] += 1

    for name in ("mortar_and_pestle", "herbalists_table", "drying_rack"):
        _write_json(assets / "blockstates" / f"{name}.json",
                    {"variants": {"": {"model": f"{MODID}:block/{name}"}}})
        counts["blockstates"] += 1
    facing_variants = {}
    for facing, y in (("north", 0), ("east", 90), ("south", 180), ("west", 270)):
        facing_variants[f"facing={facing}"] = {"model": f"{MODID}:block/herbal_mill", "y": y}
    _write_json(assets / "blockstates/herbal_mill.json", {"variants": facing_variants})
    counts["blockstates"] += 1

    for herb in herbs:
        blockstate = {"variants": {f"age={stage}": {"model": f"{MODID}:block/{herb['id']}_stage{stage}"}
                                   for stage in range(4)}}
        _write_json(assets / "blockstates" / f"{herb['id']}_crop.json", blockstate)
        counts["blockstates"] += 1
        for stage in range(4):
            _write_json(assets / "models/block" / f"{herb['id']}_stage{stage}.json", {
                "parent": "minecraft:block/cross",
                "textures": {"cross": f"{MODID}:block/{herb['id']}_stage{stage}"},
            })
            counts["block_models"] += 1

    # ---- block items reuse their block model --------------------------------
    for item in items:
        if item.get("kind") == "BLOCK":
            _item_model_block(assets / "models/item" / f"{item['id']}.json", item["block"])
            counts["item_models"] += 1

    # ---- other items ---------------------------------------------------------
    for item in items + experimental:
        art_kind = item.get("art", "vial_empty")
        color = item.get("color")
        if item.get("kind") == "BLOCK":
            continue
        if art_kind == "vial_empty":
            img = art.vial_sprite(None)
        elif art_kind == "vial_filled":
            img = art.vial_sprite(parse_color(color or "#8f7f5f"))
        elif art_kind == "cup_empty":
            img = art.cup_sprite(None)
        elif art_kind == "cup_filled":
            img = art.cup_sprite(parse_color(color or "#8f7f5f"))
        elif art_kind == "jar_filled":
            img = art.jar_sprite(parse_color(color or "#eadcbe"))
        elif art_kind == "powder_pile":
            img = art.powder_pile_sprite(color or "#f2f2ea")
        elif art_kind == "crystal_shard":
            img = art.crystal_sprite(color or "#a8dcf0")
        elif art_kind == "page":
            img = art.page_sprite(False)
        elif art_kind == "page_ancient":
            img = art.page_sprite(True)
        elif art_kind == "shears":
            img = art.shears_sprite()
        elif art_kind == "pouch":
            img = art.pouch_sprite()
        elif art_kind == "journal":
            img = art.journal_sprite()
        elif art_kind == "mortar":
            img = art.mortar_icon()
        elif art_kind in ("mill", "table", "rack"):
            img = art.workstation_icon(art_kind)
        else:
            img = art.powder_pile_sprite(color or "#c0c0c0")
        write_png(item_tex / f"{item['id']}.png", img)
        _item_model(assets / "models/item" / f"{item['id']}.json", item["id"])
        entries[f"item.{MODID}.{item['id']}"] = item["name"]
        counts["item_textures"] += 1
        counts["item_models"] += 1

    # ---- medicines -----------------------------------------------------------
    for med in medicines:
        color = parse_color(med["color"])
        kind = med["kind"]
        if kind == "TONIC":
            img = art.vial_sprite(color)
        elif kind == "TEA":
            img = art.cup_sprite(color)
        elif kind == "SALVE":
            img = art.jar_sprite(color)
        else:
            img = art.oil_sprite(color)
        write_png(item_tex / f"{med['id']}.png", img)
        _item_model(assets / "models/item" / f"{med['id']}.json", med["id"])
        entries[f"item.{MODID}.{med['id']}"] = med["name"]
        counts["item_textures"] += 1
        counts["item_models"] += 1

    # ---- gui + particles -----------------------------------------------------
    _gui_textures(assets, lang["properties"])
    particles = _particle_textures(assets)

    # ---- lang ----------------------------------------------------------------
    for herb in herbs:
        entries.setdefault(f"item.{MODID}.{herb['id']}", herb["name"])
    lang_entries = dict(entries)
    lang_entries.update(_flatten_lang(lang, herbs, medicines))
    _write_json(assets / "lang/en_us.json", dict(sorted(lang_entries.items())))

    # ---- sounds manifest -----------------------------------------------------
    sound_names = ["grind", "mill", "pour", "dry", "salve", "discovery", "sip"]
    sounds = {}
    for name in sound_names:
        sounds[name] = {"category": "block" if name in ("grind", "mill", "pour", "dry") else "player",
                        "subtitle": f"subtitles.{MODID}.{name}",
                        "sounds": [{"name": f"{MODID}:{name}", "stream": False, "attenuation_distance": 16}]}
    _write_json(assets / "sounds.json", sounds)
    for name in sound_names:
        lang_entries[f"subtitles.{MODID}.{name}"] = lang["sounds"][name] if name in lang["sounds"] else name
    _write_json(assets / "lang/en_us.json", dict(sorted(lang_entries.items())))

    log(f"  assets: {counts['item_textures']} item textures, {counts['block_models']} block models, "
        f"{counts['blockstates']} blockstates, {len(particles)} particles, {len(lang_entries)} lang entries")
    return {"herb_items": herb_items, "counts": counts}


def _item_model_block(path: pathlib.Path, block: str) -> None:
    _write_json(path, {"parent": f"{MODID}:block/{block}"})


def _flatten_lang(lang: dict, herbs: list, medicines: list) -> dict:
    out: dict[str, str] = {}
    for key, info in lang["properties"].items():
        out[f"property.{MODID}.{key}"] = info["name"]
        out[f"property.{MODID}.{key}.hint"] = info["hint"]
    for key, name in lang["categories"].items():
        out[f"category.{MODID}.{key}"] = name
    for key, name in lang["kinds"].items():
        out[f"kind.{MODID}.{key}"] = name
    for key, name in lang["rarity"].items():
        out[f"rarity.{MODID}.{key}"] = name
    for key, name in lang["toxicity"].items():
        out[f"toxicity.{MODID}.{key}"] = name
    for key, name in lang["seasons"].items():
        out[f"season.{MODID}.{key}"] = name
    for key, name in lang["biome_groups"].items():
        out[f"biome_group.{MODID}.{key}"] = name
    for key, name in lang["placements"].items():
        out[f"placement.{MODID}.{key}"] = name
    for key, name in lang["forms"].items():
        out[f"form.{MODID}.{key}"] = name
    for key, name in lang["freshness"].items():
        out[f"freshness.{MODID}.{key}"] = name
    for key, name in lang["effects"].items():
        out[f"effect.{MODID}.{key}"] = name
    for section in ("gui", "tooltip", "message", "advancement", "key", "command", "config"):
        out[f"{section}.{MODID}"] = lang[section] if isinstance(lang[section], str) else ""
        _flatten_section(out, section, lang[section])
    for med in medicines:
        out[f"medicine.{MODID}.{med['id']}.description"] = med.get("description", "")
    for herb in herbs:
        out[f"herb.{MODID}.{herb['id']}.lore"] = herb.get("lore", "")
    return out


def _flatten_section(out: dict, prefix: str, data) -> None:
    if isinstance(data, dict):
        for key, value in data.items():
            if isinstance(value, dict):
                _flatten_section(out, f"{prefix}.{key}", value)
            elif isinstance(value, str):
                out[f"{prefix}.{MODID}.{key}"] = value
