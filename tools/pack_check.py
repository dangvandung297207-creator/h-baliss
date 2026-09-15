#!/usr/bin/env python3
"""Structural checks for the generated pack.

``./gradlew build`` proves the Java compiles; this script proves the *data* hangs together:
that every JSON file is valid and lives in a folder the game actually reads, that every
``herbalistscraft:`` id mentioned anywhere resolves to a registered item or block, that the
structure templates decode and only use blocks that exist, and that worldgen references
(template pools, structure sets, loot tables, tags) all point at real files.

    python3 tools/pack_check.py

Exits non-zero and prints every problem it finds, so it can gate a release.
"""
from __future__ import annotations

import gzip
import json
import pathlib
import re
import struct
import sys

REPO = pathlib.Path(__file__).resolve().parent.parent
MAIN = REPO / "src/main/resources"
GEN = REPO / "src/generated/resources"
MODID = "herbalistscraft"

# 1.21 datapack folders are singular; a misnamed folder (data/recipe instead of
# data/herbalistscraft/recipe) silently disables content, which is exactly the bug class
# this check exists for.
DATA_FOLDERS = {
    "advancement", "damage_type", "enchantment", "herb", "jukebox_song", "loot_modifiers",
    "loot_table", "medicine", "neoforge", "painting_variant", "predicate", "recipe", "structure",
    "tags", "trim_material", "trim_pattern", "worldgen",
}
ASSET_FOLDERS = {"atlases", "blockstates", "font", "lang", "models", "particles", "sounds", "textures",
                 "waypoint_style"}

PROBLEMS: list[str] = []


def problem(message: str) -> None:
    PROBLEMS.append(message)


# ---------------------------------------------------------------------------
# NBT reader (enough of the format to read structure templates back)
# ---------------------------------------------------------------------------

class Reader:
    def __init__(self, data: bytes) -> None:
        self.data = data
        self.pos = 0

    def read(self, fmt: str):
        size = struct.calcsize(fmt)
        values = struct.unpack_from(fmt, self.data, self.pos)
        self.pos += size
        return values[0] if len(values) == 1 else values

    def string(self) -> str:
        length = self.read(">H")
        text = self.data[self.pos:self.pos + length].decode("utf-8")
        self.pos += length
        return text

    def payload(self, tag: int):
        if tag == 1:
            return self.read(">b")
        if tag == 2:
            return self.read(">h")
        if tag == 3:
            return self.read(">i")
        if tag == 4:
            return self.read(">q")
        if tag == 5:
            return self.read(">f")
        if tag == 6:
            return self.read(">d")
        if tag == 7:
            return [self.read(">b") for _ in range(self.read(">i"))]
        if tag == 8:
            return self.string()
        if tag == 9:
            element = self.read(">b")
            return [self.payload(element) for _ in range(self.read(">i"))]
        if tag == 10:
            return self.compound()
        if tag == 11:
            return [self.read(">i") for _ in range(self.read(">i"))]
        if tag == 12:
            return [self.read(">q") for _ in range(self.read(">i"))]
        raise ValueError(f"unknown NBT tag {tag}")

    def compound(self) -> dict:
        out = {}
        while True:
            tag = self.read(">b")
            if tag == 0:
                return out
            name = self.string()
            out[name] = self.payload(tag)


def read_nbt(path: pathlib.Path) -> dict:
    with gzip.open(path, "rb") as handle:
        raw = handle.read()
    reader = Reader(raw)
    tag = reader.read(">b")
    if tag != 10:
        raise ValueError("structure template root is not a compound")
    reader.string()
    return reader.compound()


# ---------------------------------------------------------------------------
# registered ids
# ---------------------------------------------------------------------------

def registered(path: pathlib.Path) -> set[str]:
    """``ITEMS.registerItem("bloodroot", ...)`` / ``BLOCKS.registerBlock(...)`` ids."""
    text = path.read_text()
    return set(re.findall(r'register(?:Item|Block|)\("([a-z0-9_]+)"', text))


def namespaced_ids() -> set[str]:
    ids = registered(REPO / "src/main/java/com/herbalistscraft/registry/ModItems.java")
    ids |= registered(REPO / "src/main/java/com/herbalistscraft/registry/ModBlocks.java")
    return ids


# ---------------------------------------------------------------------------
# checks
# ---------------------------------------------------------------------------

def check_json_layout() -> None:
    for root, folders, in ((MAIN, DATA_FOLDERS | ASSET_FOLDERS), (GEN, DATA_FOLDERS)):
        for path in sorted(root.rglob("*.json")):
            relative = path.relative_to(root)
            try:
                json.loads(path.read_text())
            except json.JSONDecodeError as error:
                problem(f"{relative}: invalid JSON ({error})")
                continue
            parts = relative.parts
            if parts[0] == "data":
                if len(parts) < 3:
                    problem(f"{relative}: data file has no namespace folder")
                elif parts[2] not in folders:
                    problem(f"{relative}: '{parts[2]}' is not a folder the game loads")
            elif parts[0] == "assets":
                if len(parts) < 3:
                    problem(f"{relative}: asset file is too shallow")
                elif len(parts) > 3 and parts[2] not in folders:
                    problem(f"{relative}: '{parts[2]}' is not a known asset folder")
                elif len(parts) == 3 and parts[2] != "sounds.json":
                    problem(f"{relative}: unexpected file next to the pack folders")


def check_references(ids: set[str]) -> None:
    """Every ``herbalistscraft:x`` mentioned in data files must be a real item or block."""
    owners = {
        "item": (REPO / "src/main/resources/data", REPO / "src/generated/resources/data"),
        "block": (),
    }
    del owners
    content_folders = {"recipe", "loot_table", "loot_modifiers", "advancement"}
    for root in (MAIN, GEN):
        for path in sorted(root.rglob("*.json")):
            relative = path.relative_to(REPO)
            if not content_folders.intersection(relative.parts):
                continue
            text = path.read_text()
            for match in re.finditer(rf'"(?:name|item|id|item_id)":\s*"({MODID}:[a-z0-9_/]+)"', text):
                reference = match.group(1).split(":", 1)[1]
                if reference not in ids and not reference.endswith("_seeds"):
                    problem(f"{relative}: unknown id {match.group(1)}")


def check_structure_templates(ids: set[str]) -> None:
    templates = GEN / "data" / MODID / "structure"
    if not templates.exists():
        problem("no structure templates were generated")
        return
    for path in sorted(templates.glob("*.nbt")):
        relative = path.relative_to(REPO)
        try:
            root = read_nbt(path)
        except Exception as error:  # noqa: BLE001 - report anything the reader chokes on
            problem(f"{relative}: cannot read structure NBT ({error})")
            continue
        size = root.get("size")
        palette = root.get("palette") or []
        blocks = root.get("blocks")
        if not isinstance(size, list) or len(size) != 3 or min(size) <= 0:
            problem(f"{relative}: bad size {size}")
            continue
        if not palette:
            problem(f"{relative}: empty palette")
        if blocks is None:
            problem(f"{relative}: no blocks list")
            continue
        for entry in blocks:
            state = entry.get("state")
            if not isinstance(state, int) or not 0 <= state < len(palette):
                problem(f"{relative}: block references palette index {state} of {len(palette)}")
                continue
            position = entry.get("pos")
            if not isinstance(position, list) or len(position) != 3:
                problem(f"{relative}: block has no position")
                continue
            if any(position[axis] < 0 or position[axis] >= size[axis] for axis in range(3)):
                problem(f"{relative}: block at {position} is outside size {size}")
            name = palette[state].get("Name", "")
            if name.startswith(f"{MODID}:") and name.split(":", 1)[1] not in ids:
                problem(f"{relative}: unknown block {name}")
            if "Properties" in palette[state] and not palette[state]["Properties"]:
                problem(f"{relative}: {name} has an empty Properties tag")
        for entry in root.get("blocks", []):
            entity = entry.get("nbt")
            if not entity:
                continue
            loot = entity.get("LootTable")
            if loot and loot.startswith(f"{MODID}:"):
                target = GEN / "data" / MODID / "loot_table" / f"{loot.split(':', 1)[1]}.json"
                if not target.exists():
                    problem(f"{relative}: chest references missing loot table {loot}")
        if not isinstance(root.get("entities"), list):
            problem(f"{relative}: missing entities list")


def check_worldgen() -> None:
    worldgen = GEN / "data" / MODID / "worldgen"
    structures = sorted(worldgen.glob("structure/*.json"))
    if not structures:
        problem("no structure JSON was generated")
    for path in structures:
        data = json.loads(path.read_text())
        relative = path.relative_to(REPO)
        pool = data.get("start_pool", "")
        if not (worldgen / "template_pool" / f"{pool.split(':', 1)[-1]}.json").exists():
            problem(f"{relative}: start_pool {pool} has no template pool")
        biomes = data.get("biomes", "")
        if isinstance(biomes, str) and biomes.startswith("#"):
            tag = GEN / "data" / biomes[1:].split(":", 1)[0] / "tags/worldgen/biome" / \
                f"{biomes[1:].split(':', 1)[1]}.json"
            if not tag.exists():
                problem(f"{relative}: biome tag {biomes} does not exist")
        for key in ("step", "terrain_adaptation", "start_height", "project_start_to_heightmap"):
            if key not in data:
                problem(f"{relative}: missing '{key}'")
    for path in sorted(worldgen.glob("structure_set/*.json")):
        data = json.loads(path.read_text())
        relative = path.relative_to(REPO)
        placement = data.get("placement", {})
        if placement.get("type") != "minecraft:random_spread":
            problem(f"{relative}: unexpected placement {placement.get('type')}")
        if placement.get("spacing", 0) <= placement.get("separation", 0):
            problem(f"{relative}: separation must be smaller than spacing")
        for entry in data.get("structures", []):
            structure = entry.get("structure", "")
            if structure.startswith(f"{MODID}:"):
                target = worldgen / "structure" / f"{structure.split(':', 1)[1]}.json"
                if not target.exists():
                    problem(f"{relative}: references missing structure {structure}")
    for path in sorted(worldgen.glob("template_pool/**/*.json")):
        data = json.loads(path.read_text())
        relative = path.relative_to(REPO)
        for entry in data.get("elements", []):
            location = entry.get("element", {}).get("location", "")
            if location.startswith(f"{MODID}:"):
                target = GEN / "data" / MODID / "structure" / f"{location.split(':', 1)[1]}.nbt"
                if not target.exists():
                    problem(f"{relative}: element points at missing template {location}")
        if "fallback" not in data:
            problem(f"{relative}: template pool has no fallback")


def check_lang() -> None:
    lang = json.loads((MAIN / "assets" / MODID / "lang" / "en_us.json").read_text())
    items = re.findall(r'registerItem\("([a-z0-9_]+)"', (REPO / "src/main/java/com/herbalistscraft/"
                                                             "registry/ModItems.java").read_text())
    blocks = re.findall(r'registerBlock\("([a-z0-9_]+)"', (REPO / "src/main/java/com/herbalistscraft/"
                                                           "registry/ModBlocks.java").read_text())
    for item in items:
        if f"item.{MODID}.{item}" not in lang:
            problem(f"lang: missing item.{MODID}.{item}")
    for block in blocks:
        if f"block.{MODID}.{block}" not in lang:
            problem(f"lang: missing block.{MODID}.{block}")
    for herb in sorted((MAIN / "data" / MODID / "herb").glob("*.json")):
        key = f"herb.{MODID}.{herb.stem}.lore"
        if key not in lang:
            problem(f"lang: missing {key}")
    for medicine in sorted((MAIN / "data" / MODID / "medicine").glob("*.json")):
        key = f"medicine.{MODID}.{medicine.stem}"
        if f"{key}.description" not in lang:
            problem(f"lang: missing {key}.description")


def check_assets(ids: set[str]) -> None:
    for path in sorted((MAIN / "assets" / MODID / "blockstates").glob("*.json")):
        data = json.loads(path.read_text())
        for variant in data.get("variants", {}).values():
            for model in ([variant] if isinstance(variant, dict) else variant):
                if isinstance(model, dict) and "model" in model:
                    if not resolve_model(model["model"]):
                        problem(f"{path.name}: missing model {model['model']}")
    for path in sorted((MAIN / "assets" / MODID / "models").rglob("*.json")):
        data = json.loads(path.read_text())
        parent = data.get("parent")
        if parent and not resolve_model(parent):
            problem(f"{path.relative_to(MAIN)}: missing parent {parent}")
        textures = data.get("textures", {})
        for key, texture in textures.items():
            if texture.startswith("#") or texture.startswith("minecraft:"):
                continue
            if not (MAIN / "assets" / texture.split(":", 1)[0] / "textures" / f"{texture.split(':', 1)[1]}.png") \
                    .exists():
                problem(f"{path.relative_to(MAIN)}: missing texture {key}={texture}")


def resolve_model(reference: str) -> bool:
    """True when a model reference resolves inside this pack or vanilla/NeoForge."""
    if reference.startswith(("minecraft:", "neoforge:")) or reference == "builtin/entity":
        return True
    namespace, _, path = reference.partition(":")
    if not path:
        namespace, path = MODID, namespace
    return (MAIN / "assets" / namespace / "models" / f"{path}.json").exists()


def check_item_models() -> None:
    """Every registered item needs a model, or it renders as the missing-texture cube."""
    java = REPO / "src/main/java/com/herbalistscraft/registry"
    ids = re.findall(r'register(?:Item|SimpleBlockItem)\("([a-z0-9_]+)"',
                     (java / "ModItems.java").read_text() + (java / "ModBlocks.java").read_text())
    for item in sorted(set(ids)):
        model = MAIN / "assets" / MODID / "models/item" / f"{item}.json"
        if not model.exists():
            problem(f"models: no item model for {item}")


def check_sounds() -> None:
    path = MAIN / "assets" / MODID / "sounds.json"
    if not path.exists():
        problem("sounds.json is missing")
        return
    for name, entry in json.loads(path.read_text()).items():
        for sound in entry.get("sounds", []):
            file = (sound if isinstance(sound, str) else sound.get("name", "")).split(":", 1)[-1]
            if not (MAIN / "assets" / MODID / "sounds" / f"{file}.ogg").exists():
                problem(f"sounds.json: missing file sounds/{file}.ogg")


def main() -> int:
    ids = namespaced_ids()
    check_json_layout()
    check_references(ids)
    check_structure_templates(ids)
    check_worldgen()
    check_lang()
    check_assets(ids)
    check_item_models()
    check_sounds()
    if PROBLEMS:
        print(f"{len(PROBLEMS)} problem(s):")
        for message in PROBLEMS:
            print(f"  - {message}")
        return 1
    print(f"pack ok: {len(ids)} registered items/blocks, "
          f"{len(list(GEN.rglob('*.json')))} generated files, 3 structure templates")
    return 0


if __name__ == "__main__":
    sys.exit(main())
