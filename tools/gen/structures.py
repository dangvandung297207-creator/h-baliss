"""Builds the three Herbalist's Craft structures.

Every structure is a vanilla ``minecraft:jigsaw`` structure with exactly one template, so the
whole thing is data: the ``.nbt`` template is written by :mod:`tools.gen.nbt` from the
blueprints below, and the ``structure``/``structure_set``/``template_pool`` JSON is plain
worldgen data. No custom structure classes, no custom placement — normal worldgen only.

The blueprints are dictionaries of ``(x, y, z) -> block state``. Positions start at ``(0, 0, 0)``
and the game centres a jigsaw start piece horizontally on its origin, so a blueprint is written
as if the structure stood on a 0-based grid.
"""
from __future__ import annotations

import json
import pathlib
import random

from . import nbt
from .data import DATA_VERSION, MODID

# ---------------------------------------------------------------------------
# blueprint plumbing
# ---------------------------------------------------------------------------


class Blueprint:
    """A sparse grid of block states plus the block entities that go with them."""

    def __init__(self) -> None:
        self.blocks: dict[tuple[int, int, int], str] = {}
        self.block_entities: dict[tuple[int, int, int], dict] = {}

    # -- authoring helpers ---------------------------------------------------

    def set(self, x: int, y: int, z: int, state: str | None) -> None:
        """Place ``state`` at a position; ``None`` removes the block (leaves air)."""
        key = (int(x), int(y), int(z))
        if state is None:
            self.blocks.pop(key, None)
            self.block_entities.pop(key, None)
        else:
            self.blocks[key] = state

    def fill(self, start, end, state: str | None, keep: bool = False) -> None:
        """Fill the box spanned by ``start`` and ``end`` (both inclusive)."""
        for x in range(min(start[0], end[0]), max(start[0], end[0]) + 1):
            for y in range(min(start[1], end[1]), max(start[1], end[1]) + 1):
                for z in range(min(start[2], end[2]), max(start[2], end[2]) + 1):
                    if keep and self.blocks.get((x, y, z)) is not None:
                        continue
                    self.set(x, y, z, state)

    def shell(self, start, end, state: str | None, floor: bool = False) -> None:
        """Fill only the outer walls (and optionally the floor) of a box."""
        x0, y0, z0 = start
        x1, y1, z1 = end
        for x in range(x0, x1 + 1):
            for y in range(y0, y1 + 1):
                for z in range(z0, z1 + 1):
                    if x in (x0, x1) or z in (z0, z1) or (floor and y == y0):
                        self.set(x, y, z, state)

    def chest(self, x: int, y: int, z: int, loot_table: str, facing: str = "north") -> None:
        """A chest that rolls its contents from a loot table (the usual structure loot hook)."""
        self.set(x, y, z, f"minecraft:chest[facing={facing}]")
        self.block_entities[(x, y, z)] = {"id": "minecraft:chest", "LootTable": loot_table}

    def scatter(self, positions, state: str) -> None:
        for position in positions:
            self.set(position[0], position[1], position[2], state)

    # -- output --------------------------------------------------------------

    def size(self) -> list[int]:
        if not self.blocks:
            raise ValueError("empty blueprint")
        xs = [key[0] for key in self.blocks]
        ys = [key[1] for key in self.blocks]
        zs = [key[2] for key in self.blocks]
        return [max(xs) + 1, max(ys) + 1, max(zs) + 1]

    def to_nbt(self, name: str) -> dict:
        """Serialise into the vanilla structure-template NBT layout."""
        palette: list[str] = []
        for state in sorted(self.blocks.values()):
            if state not in palette:
                palette.append(state)
        index = {state: position for position, state in enumerate(palette)}

        palette_tag = [parse_state(state) for state in palette]
        blocks_tag = []
        for position in sorted(self.blocks):
            state = self.blocks[position]
            entry = {"state": index[state], "pos": nbt.int_list(position)}
            entity = self.block_entities.get(position)
            if entity is not None:
                entry["nbt"] = dict(entity)
            blocks_tag.append(entry)

        return {
            "DataVersion": DATA_VERSION,
            "size": nbt.int_list(self.size()),
            "palette": nbt.compound_list(palette_tag),
            "blocks": nbt.compound_list(blocks_tag),
            "entities": nbt.List(nbt.TAG_COMPOUND, []),
        }


def parse_state(state: str) -> dict:
    """``"minecraft:oak_log[axis=y]"`` -> ``{"Name": ..., "Properties": {...}}``."""
    if "[" not in state:
        return {"Name": state}
    name, _, rest = state.partition("[")
    body = rest.rstrip("]")
    properties = {}
    for pair in body.split(","):
        key, _, value = pair.partition("=")
        properties[key.strip()] = value.strip()
    return {"Name": name, "Properties": properties}


# ---------------------------------------------------------------------------
# shared pieces
# ---------------------------------------------------------------------------

HERB_GARDEN = ["bloodroot", "heartleaf", "yarrow", "comfrey", "golden_chamomile", "lavender", "sunleaf"]

ROOF_STAIRS = {
    "north": "minecraft:oak_stairs[facing=north,half=bottom,shape=straight]",
    "south": "minecraft:oak_stairs[facing=south,half=bottom,shape=straight]",
    "east": "minecraft:oak_stairs[facing=east,half=bottom,shape=straight]",
    "west": "minecraft:oak_stairs[facing=west,half=bottom,shape=straight]",
}
ROOF_SLAB = "minecraft:oak_slab[type=bottom,waterlogged=false]"


def gable_roof(blueprint: Blueprint, x0: int, z0: int, x1: int, z1: int, y: int, levels: int = 3) -> None:
    """A stepped roof: each level shrinks by one block and its rim is made of stairs."""
    for level in range(levels):
        lx0, lz0, lx1, lz1 = x0 + level, z0 + level, x1 - level, z1 - level
        if lx0 > lx1 or lz0 > lz1:
            break
        for x in range(lx0, lx1 + 1):
            for z in range(lz0, lz1 + 1):
                edge_x = x in (lx0, lx1)
                edge_z = z in (lz0, lz1)
                if not edge_x and not edge_z:
                    blueprint.set(x, y + level, z, ROOF_SLAB)
                elif edge_x and not edge_z:
                    blueprint.set(x, y + level, z, ROOF_STAIRS["west" if x == lx0 else "east"])
                elif edge_z and not edge_x:
                    blueprint.set(x, y + level, z, ROOF_STAIRS["north" if z == lz0 else "south"])
                else:
                    blueprint.set(x, y + level, z, ROOF_STAIRS["north"])


def garden_bed(blueprint: Blueprint, x0: int, z0: int, width: int, length: int, herbs, age: int,
               growth: float = 1.0, rng: random.Random | None = None) -> None:
    """A fenced bed of farmland with a water channel down the middle.

    ``age`` is the crop stage the herbs are planted at; ``growth`` is the chance that any given
    plant is there at all (used to make abandoned gardens look overgrown and patchy).
    """
    rng = rng or random.Random(0)
    centre = x0 + width // 2
    for x in range(x0, x0 + width):
        for z in range(z0, z0 + length):
            blueprint.set(x, 0, z, "minecraft:farmland[moisture=7]" if x != centre
                          else "minecraft:water[level=0]")
    for x in range(x0, x0 + width):
        for z in range(z0, z0 + length):
            if x == centre:
                continue
            if rng.random() > growth:
                continue
            herb = herbs[(x * 3 + z) % len(herbs)]
            stage = age if rng.random() < 0.7 else max(0, age - 1)
            blueprint.set(x, 1, z, f"{MODID}:{herb}_crop[age={stage}]")
    # fence the bed, leaving the corner nearest the hut open as a gate
    for x in range(x0 - 1, x0 + width + 1):
        for z in (z0 - 1, z0 + length):
            blueprint.set(x, 1, z, "minecraft:oak_fence")
    for z in range(z0 - 1, z0 + length + 1):
        for x in (x0 - 1, x0 + width):
            blueprint.set(x, 1, z, "minecraft:oak_fence")
    # a gate on the hut side so the garden can be walked into
    blueprint.set(x0 - 1, 1, z0 + length // 2, None)
    blueprint.set(x0 - 1, 0, z0 + length // 2, "minecraft:gravel")


def hut_shell(blueprint: Blueprint, x0: int, z0: int, size: int = 9, wall_height: int = 3) -> None:
    """Walls, floor, windows and a doorway of the hut, shared by the cared-for and ruined hut."""
    x1, z1 = x0 + size - 1, z0 + size - 1
    blueprint.fill((x0, 0, z0), (x1, 0, z1), "minecraft:oak_planks")
    blueprint.shell((x0, 0, z0), (x1, 0, z1), "minecraft:cobblestone", floor=True)
    blueprint.shell((x0, 1, z0), (x1, wall_height, z1), "minecraft:oak_planks")
    for x, z in ((x0, z0), (x1, z0), (x0, z1), (x1, z1)):
        blueprint.fill((x, 1, z), (x, wall_height, z), "minecraft:oak_log[axis=y]")
    # windows, one row up
    for x in (x0 + 2, x0 + 4, x0 + 6):
        blueprint.set(x, 2, z0, "minecraft:glass_pane")
    for x in (x0 + 2, x0 + 6):
        blueprint.set(x, 2, z1, "minecraft:glass_pane")
    blueprint.set(x1, 2, z0 + 4, "minecraft:glass_pane")
    blueprint.set(x0, 2, z0 + 4, "minecraft:glass_pane")
    # doorway on the south wall
    door = x0 + size // 2
    blueprint.set(door, 1, z1, "minecraft:oak_door[facing=north,half=lower,hinge=left]")
    blueprint.set(door, 2, z1, "minecraft:oak_door[facing=north,half=upper,hinge=left]")
    # gravel path out of the door
    for step in range(1, 4):
        blueprint.set(door, 0, z1 + step, "minecraft:gravel")
    return door


# ---------------------------------------------------------------------------
# the three structures
# ---------------------------------------------------------------------------

def build_herbalist_hut() -> Blueprint:
    blueprint = Blueprint()
    x0, z0, size = 1, 1, 9
    door = hut_shell(blueprint, x0, z0, size)
    gable_roof(blueprint, x0, z0, x0 + size - 1, z0 + size - 1, 4, levels=3)

    # interior: a working herbalist's workshop
    blueprint.set(3, 1, 3, f"{MODID}:herbalists_table")
    blueprint.set(7, 1, 3, f"{MODID}:herbal_mill[facing=south]")
    blueprint.set(3, 1, 7, f"{MODID}:mortar_and_pestle")
    blueprint.set(7, 1, 7, f"{MODID}:drying_rack")
    blueprint.set(7, 1, 6, f"{MODID}:drying_rack")
    blueprint.set(2, 1, 5, "minecraft:barrel[facing=up]")
    blueprint.set(8, 1, 5, "minecraft:bookshelf")
    blueprint.set(8, 1, 4, "minecraft:bookshelf")
    blueprint.set(5, 1, 5, "minecraft:lantern[hanging=false]")
    blueprint.chest(4, 1, 6, f"{MODID}:chests/herbalist_hut", facing="east")
    blueprint.set(5, 1, 8, "minecraft:flower_pot")
    blueprint.set(4, 1, 8, "minecraft:flower_pot")

    # the garden beside the hut, plus a compost heap and a second drying rack in the open
    garden_bed(blueprint, 11, 2, 4, 8, HERB_GARDEN, age=3, rng=random.Random(11))
    blueprint.set(11, 1, 1, "minecraft:composter")
    blueprint.set(13, 1, 1, f"{MODID}:drying_rack")
    blueprint.set(14, 1, 1, "minecraft:oak_fence")
    blueprint.set(door, 0, 10, "minecraft:gravel")
    blueprint.set(door - 1, 0, 10, "minecraft:coarse_dirt")
    blueprint.set(door + 1, 0, 10, "minecraft:coarse_dirt")
    return blueprint


def build_abandoned_hut() -> Blueprint:
    blueprint = Blueprint()
    rng = random.Random(4477)
    x0, z0, size = 1, 1, 9
    door = hut_shell(blueprint, x0, z0, size)
    x1, z1 = x0 + size - 1, z0 + size - 1
    gable_roof(blueprint, x0, z0, x1, z1, 4, levels=3)

    # the roof has fallen in and the walls are breached
    for x in range(x0, x1 + 1):
        for z in range(z0, z1 + 1):
            for y in (4, 5, 6):
                if (x, y, z) in blueprint.blocks and rng.random() < 0.28:
                    blueprint.set(x, y, z, None)
    for x, z in ((x0, z0 + 3), (x1 - 1, z0 + 1), (x0 + 2, z1), (x1, z1 - 2), (x0, z0 + 6)):
        blueprint.set(x, 3, z, None)
        if rng.random() < 0.6:
            blueprint.set(x, 2, z, None)
    blueprint.set(door, 1, z1, None)
    blueprint.set(door, 2, z1, None)

    # a workshop that was left in a hurry
    blueprint.set(3, 1, 3, f"{MODID}:herbalists_table")
    blueprint.set(7, 1, 3, f"{MODID}:herbal_mill[facing=south]")
    blueprint.set(3, 1, 7, f"{MODID}:mortar_and_pestle")
    blueprint.set(7, 1, 6, f"{MODID}:drying_rack")
    blueprint.chest(5, 1, 2, f"{MODID}:chests/abandoned_herbalist_hut", facing="south")
    blueprint.chest(6, 1, 7, f"{MODID}:chests/abandoned_herbalist_hut", facing="north")
    blueprint.set(2, 1, 6, "minecraft:crafting_table")
    blueprint.set(8, 1, 4, "minecraft:flower_pot")
    blueprint.set(4, 1, 6, "minecraft:cobweb")
    blueprint.set(8, 2, 8, "minecraft:cobweb")
    blueprint.set(2, 3, 2, "minecraft:cobweb")
    blueprint.set(6, 1, 5, "minecraft:coarse_dirt")
    blueprint.set(4, 1, 4, "minecraft:dead_bush")
    blueprint.set(8, 1, 1, "minecraft:coarse_dirt")

    # the garden kept growing without anyone to tend it
    garden_bed(blueprint, 11, 2, 4, 8, HERB_GARDEN, age=2, growth=0.55, rng=random.Random(99))
    for x, z in ((11, 1), (12, 1), (14, 1), (11, 10), (12, 10), (13, 10), (14, 10)):
        blueprint.set(x, 1, z, "minecraft:grass")
    blueprint.set(13, 1, 1, "minecraft:dead_bush")
    blueprint.set(14, 0, 3, "minecraft:coarse_dirt")
    blueprint.set(15, 0, 5, "minecraft:gravel")
    blueprint.set(15, 0, 6, "minecraft:gravel")
    return blueprint


def build_ancient_apothecary() -> Blueprint:
    blueprint = Blueprint()
    size = 13
    x0 = z0 = 0
    x1 = z1 = size - 1

    # a sunken floor of polished stone with a cracked, mossy rim
    blueprint.fill((x0, 0, z0), (x1, 0, z1), "minecraft:polished_deepslate")
    for x in range(x0, x1 + 1):
        for z in range(z0, z1 + 1):
            if (x * 7 + z * 13) % 11 == 0:
                blueprint.set(x, 0, z, "minecraft:cracked_deepslate_bricks")
            elif (x * 5 + z * 3) % 17 == 0:
                blueprint.set(x, 0, z, "minecraft:moss_block")

    # ruins of the outer wall: three courses high, broken in places
    for x in range(x0, x1 + 1):
        for z in (z0, z1):
            for y in (1, 2, 3):
                if (x + y * 3 + z) % 7 == 0 and y == 3:
                    continue
                blueprint.set(x, y, z, "minecraft:deepslate_bricks")
    for z in range(z0, z1 + 1):
        for x in (x0, x1):
            for y in (1, 2, 3):
                if (x + y * 5 + z) % 7 == 0 and y == 3:
                    continue
                blueprint.set(x, y, z, "minecraft:deepslate_bricks")
    for x in (x0, x1):
        for z in (z0, z1):
            blueprint.fill((x, 1, z), (x, 3, z), "minecraft:chiseled_deepslate")
    # a broken doorway on the south side
    blueprint.fill((6, 1, z1), (7, 2, z1), None)

    # four pillars holding up a roof that is long gone
    for x in (3, 9):
        for z in (3, 9):
            blueprint.fill((x, 1, z), (x, 3, z), "minecraft:deepslate_tiles")
            blueprint.set(x, 4, z, "minecraft:deepslate_brick_wall")
    blueprint.set(6, 4, 3, "minecraft:deepslate_brick_wall")
    blueprint.set(6, 4, 9, "minecraft:deepslate_brick_wall")

    # the altar
    blueprint.fill((5, 1, 5), (7, 1, 7), "minecraft:polished_deepslate")
    blueprint.set(6, 2, 6, f"{MODID}:herbalists_table")
    blueprint.set(5, 2, 5, f"{MODID}:mortar_and_pestle")
    blueprint.set(7, 2, 7, f"{MODID}:herbal_mill[facing=north]")
    blueprint.set(5, 2, 7, "minecraft:water_cauldron[level=3]")
    blueprint.set(7, 2, 5, f"{MODID}:drying_rack")
    for x, z in ((4, 4), (8, 4), (4, 8), (8, 8)):
        blueprint.set(x, 1, z, "minecraft:soul_lantern[hanging=false]")
    blueprint.set(6, 1, 4, "minecraft:lectern[facing=south]")
    blueprint.set(6, 1, 8, f"{MODID}:drying_rack")

    # alcoves along the north wall: the apothecary's stores
    blueprint.chest(3, 1, 1, f"{MODID}:chests/ancient_apothecary", facing="south")
    blueprint.chest(9, 1, 1, f"{MODID}:chests/ancient_apothecary", facing="south")
    blueprint.chest(1, 1, 6, f"{MODID}:chests/ancient_apothecary", facing="east")
    blueprint.set(2, 1, 2, "minecraft:barrel[facing=up]")
    blueprint.set(10, 1, 2, "minecraft:barrel[facing=up]")
    blueprint.set(2, 1, 10, "minecraft:bookshelf")
    blueprint.set(10, 1, 10, "minecraft:bookshelf")

    # the rare herbs that still grow between the stones
    rare = ["grave_moss", "moonflower", "nightshade", "glacier_lotus", "dragonscale_herb", "spiritwood",
            "wolfsbane", "hemlock"]
    for x in range(2, 11):
        for z in range(2, 11):
            if (x, z) == (6, 6):
                continue
            if (x, 1, z) in blueprint.blocks or (x, 0, z) not in blueprint.blocks:
                continue
            if (x * 3 + z * 5) % 6 != 0:
                continue
            if x in (2, 3, 9, 10) and z in (2, 3, 9, 10):
                blueprint.set(x, 0, z, "minecraft:moss_block")
            herb = rare[(x + z) % len(rare)]
            blueprint.set(x, 1, z, f"{MODID}:{herb}_crop[age=3]")
    for x, z, state in ((2, 5, "minecraft:soul_soil"), (10, 8, "minecraft:soul_soil"),
                        (4, 10, "minecraft:moss_block"), (9, 11, "minecraft:moss_block")):
        blueprint.set(x, 0, z, state)
    return blueprint


STRUCTURES = {
    "herbalist_hut": {
        "build": build_herbalist_hut,
        "spacing": 26,
        "separation": 9,
        "salt": 1471589,
        "terrain": "beard_thin",
    },
    "abandoned_hut": {
        "build": build_abandoned_hut,
        "spacing": 34,
        "separation": 12,
        "salt": 771293,
        "terrain": "beard_thin",
    },
    "ancient_apothecary": {
        "build": build_ancient_apothecary,
        "spacing": 62,
        "separation": 22,
        "salt": 5652419,
        "terrain": "beard_thin",
    },
}


def _write(path: pathlib.Path, data) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, indent=2) + "\n")


def generate(root: pathlib.Path, log=print) -> dict:
    """Write the structure templates (main resources) and their worldgen data (generated)."""
    templates = root / "src/generated/resources/data" / MODID / "structure"
    worldgen = root / "src/generated/resources/data" / MODID / "worldgen"
    total_blocks = 0

    for name, spec in STRUCTURES.items():
        blueprint = spec["build"]()
        total_blocks += len(blueprint.blocks)
        nbt.write_nbt(templates / f"{name}.nbt", f"{MODID}:{name}", blueprint.to_nbt(name))

        _write(worldgen / "structure" / f"{name}.json", {
            "type": "minecraft:jigsaw",
            "biomes": f"#{MODID}:has_structure/{name}",
            "step": "surface_structures",
            "terrain_adaptation": spec["terrain"],
            "spawn_overrides": {},
            "start_pool": f"{MODID}:{name}/start",
            "size": 1,
            "start_height": {"absolute": 0},
            "project_start_to_heightmap": "WORLD_SURFACE_WG",
            "max_distance_from_center": 80,
            "use_expansion_hack": False,
        })
        _write(worldgen / "structure_set" / f"{name}.json", {
            "placement": {
                "type": "minecraft:random_spread",
                "salt": spec["salt"],
                "spacing": spec["spacing"],
                "separation": spec["separation"],
            },
            "structures": [
                {"structure": f"{MODID}:{name}", "weight": 1},
            ],
        })
        _write(worldgen / "template_pool" / name / "start.json", {
            "name": f"{MODID}:{name}/start",
            "fallback": "minecraft:empty",
            "elements": [
                {
                    "weight": 1,
                    "element": {
                        "element_type": "minecraft:single_pool_element",
                        "location": f"{MODID}:{name}",
                        "processors": "minecraft:empty",
                        "projection": "rigid",
                    },
                },
            ],
        })

    log(f"  structures: 3 templates ({total_blocks} blocks), 3 structure sets, 3 template pools")
    return {"structures": len(STRUCTURES)}
