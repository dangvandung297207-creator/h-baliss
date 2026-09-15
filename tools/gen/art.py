"""Procedural pixel art for Herbalist's Craft.

Everything in the mod's art (herb sprites, growth stages, processed forms, vials,
cups, tools, block textures, workstation GUIs, property icons, particles) is drawn
here with integer pixel operations so that the result is a real, consistent 16x16
Minecraft look rather than recoloured placeholders.

Palette rules
-------------
* every herb inherits its category's base hue (healing = blood reds, cooling =
  frost blues, warming = embers, toxic = swamp purples, medicinal = sages,
  stimulant = suns, sedative = violets, protective = olives),
* a deterministic per-herb hash shifts the accent hue slightly and varies petal /
  berry shapes, so two herbs of the same category are still clearly different,
* shading is a three-tone ramp (dark / base / light) taken from the same hue,
  which is what makes the flat sprites read as Minecraft art.
"""
from __future__ import annotations

import colorsys
from .png import Image, parse_color


# ---------------------------------------------------------------------------
# colour helpers
# ---------------------------------------------------------------------------

def _hsv(h: float, s: float, v: float, a: int = 255):
    r, g, b = colorsys.hsv_to_rgb(h % 1.0, max(0.0, min(1.0, s)), max(0.0, min(1.0, v)))
    return (int(r * 255), int(g * 255), int(b * 255), a)


def _c(value):
    """Accept either an '#rrggbb' string or an RGBA tuple wherever a colour is expected."""
    if value is None or isinstance(value, tuple):
        return value
    return parse_color(value)


def _mix(c1, c2, t: float):
    return tuple(int(a + (b - a) * t) for a, b in zip(c1[:3], c2[:3])) + (c1[3] if len(c1) > 3 else 255,)


def _hash(text: str) -> float:
    """Deterministic 0..1 hash of a string (stable across runs and platforms)."""
    h = 2166136261
    for ch in text:
        h = ((h ^ ord(ch)) * 16777619) & 0xFFFFFFFF
    return h / 0xFFFFFFFF


CATEGORY_HUE = {
    "HEALING": 0.98,
    "COOLING": 0.55,
    "WARMING": 0.08,
    "TOXIC": 0.78,
    "MEDICINAL": 0.31,
    "STIMULANT": 0.13,
    "SEDATIVE": 0.75,
    "PROTECTIVE": 0.22,
}

RARITY_GLOW = {"COMMON": 0, "UNCOMMON": 1, "RARE": 2, "VERY_RARE": 3, "LEGENDARY": 4}


class Palette:
    """A three-tone herb palette derived from the category and the herb id."""

    def __init__(self, herb: dict):
        cat = herb.get("category", "MEDICINAL")
        h = CATEGORY_HUE.get(cat, 0.33)
        jitter = (_hash(herb["id"]) - 0.5) * 0.06
        accent_hue = h + jitter
        leaf_hue = 0.28 + (_hash(herb["id"] + "leaf") - 0.5) * 0.10
        if cat == "COOLING":
            leaf_hue = 0.45 + (_hash(herb["id"] + "leaf") - 0.5) * 0.08
        if cat == "TOXIC":
            leaf_hue = 0.30 + (_hash(herb["id"] + "leaf") - 0.5) * 0.12
        sat_boost = {"TOXIC": 0.55, "HEALING": 0.72}.get(cat, 0.62)

        self.accent = _hsv(accent_hue, sat_boost, 0.78)
        self.accent_dark = _hsv(accent_hue, min(0.95, sat_boost + 0.1), 0.52)
        self.accent_light = _hsv(accent_hue, max(0.18, sat_boost - 0.28), 0.94)
        self.leaf = _hsv(leaf_hue, 0.58, 0.46)
        self.leaf_dark = _hsv(leaf_hue, 0.66, 0.30)
        self.leaf_light = _hsv(leaf_hue + 0.02, 0.44, 0.62)
        self.stem = _hsv(leaf_hue - 0.02, 0.50, 0.36)
        self.root = _hsv(0.09, 0.42, 0.60)
        self.root_dark = _hsv(0.07, 0.50, 0.40)
        self.spore = _hsv(accent_hue, 0.20, 0.95)
        self.rarity = herb.get("rarity", "COMMON")
        self.herb = herb

    def glow_pixels(self) -> int:
        return RARITY_GLOW.get(self.rarity, 0)


# ---------------------------------------------------------------------------
# morphology sprites (16x16)
# ---------------------------------------------------------------------------

def _leaf_shape(img: Image, x: int, y: int, direction: int, p: Palette, length: int = 3) -> None:
    """Draws a small pointed leaf growing sideways from (x, y)."""
    for i in range(length):
        dx = i * direction
        img.set(x + dx, y - (1 if i < length - 1 else 0), p.leaf)
        img.set(x + dx, y, p.leaf_dark)
        if i < length - 1:
            img.set(x + dx, y - 1, p.leaf_light)


def _draw_leaf(p: Palette) -> Image:
    img = Image(16, 16)
    base_x, top = 8, 3
    img.line(base_x, 15, base_x, top, p.stem)
    _leaf_shape(img, base_x + 1, 10, 1, p, 4)
    _leaf_shape(img, base_x - 1, 7, -1, p, 4)
    _leaf_shape(img, base_x + 1, 5, 1, p, 3)
    img.set(base_x, top - 1, p.accent)
    img.set(base_x, top, p.accent_dark)
    img.set(base_x + 1, top, p.accent_light)
    return img


def _draw_flower(p: Palette) -> Image:
    img = Image(16, 16)
    img.line(8, 15, 8, 7, p.stem)
    _leaf_shape(img, 9, 11, 1, p, 3)
    _leaf_shape(img, 7, 9, -1, p, 3)
    cx, cy, r = 8, 5, 3
    for dy in range(-r, r + 1):
        for dx in range(-r, r + 1):
            d = dx * dx + dy * dy
            if d <= r * r and d > 0:
                img.set(cx + dx, cy + dy, p.accent if (dx + dy) % 2 == 0 else p.accent_dark)
    img.set(cx, cy, p.accent_light)
    img.set(cx, cy - r - 1, p.accent_light)
    img.set(cx + r + 1, cy, p.accent_light)
    img.set(cx - r - 1, cy, p.accent_dark)
    img.set(cx, cy + r + 1, p.accent_dark)
    return img


def _draw_grass(p: Palette) -> Image:
    img = Image(16, 16)
    blades = [(-3, 10), (-2, 6), (-1, 8), (0, 4), (1, 7), (2, 5), (3, 9)]
    for i, (off, height) in enumerate(blades):
        x = 8 + off
        top = 15 - height
        for y in range(15, top - 1, -1):
            img.set(x, y, p.leaf_dark if (x + y) % 3 else p.leaf)
        img.set(x, top, p.leaf_light)
        if i % 3 == 1:
            img.set(x, top - 1, p.accent)
    return img


def _draw_berry(p: Palette) -> Image:
    img = Image(16, 16)
    img.ellipse(8, 9, 5.5, 4.5, p.leaf_dark)
    img.ellipse(7, 8, 4.0, 3.0, p.leaf)
    img.ellipse(10, 10, 3.0, 2.5, p.leaf_dark)
    for (bx, by) in ((6, 8), (9, 7), (11, 10), (7, 11), (10, 12)):
        img.set(bx, by, p.accent)
        img.set(bx + 1, by, p.accent_dark)
        img.set(bx, by + 1, p.accent_dark)
        img.set(bx, by - 1, p.accent_light)
    img.line(8, 4, 8, 6, p.stem)
    _leaf_shape(img, 9, 5, 1, p, 2)
    _leaf_shape(img, 7, 4, -1, p, 2)
    return img


def _draw_root(p: Palette) -> Image:
    img = Image(16, 16)
    img.line(8, 6, 8, 3, p.stem)
    _leaf_shape(img, 9, 4, 1, p, 2)
    _leaf_shape(img, 7, 3, -1, p, 2)
    img.ellipse(8, 10, 3.6, 3.2, p.root)
    img.ellipse(7, 9.5, 2.2, 2.0, p.leaf_light if False else _mix(p.root, (255, 255, 255, 255), 0.25))
    for (dx, dy) in ((-2, 3), (0, 4), (2, 3), (-1, 4)):
        img.line(8 + dx, 12, 8 + dx + (1 if dx >= 0 else -1), 14, p.root_dark)
    img.set(8, 10, p.accent_dark)
    return img


def _draw_bulb(p: Palette) -> Image:
    img = Image(16, 16)
    img.line(8, 9, 8, 2, p.stem)
    _leaf_shape(img, 9, 4, 1, p, 3)
    _leaf_shape(img, 7, 3, -1, p, 3)
    img.ellipse(8, 11, 4.2, 3.4, p.root)
    img.ellipse(7, 10, 2.4, 2.0, _mix(p.root, (255, 255, 255, 255), 0.28))
    img.line(6, 13, 5, 15, p.root_dark)
    img.line(10, 13, 11, 15, p.root_dark)
    img.set(8, 14, p.root_dark)
    return img


def _draw_mushroom(p: Palette) -> Image:
    img = Image(16, 16)
    img.rect(7, 9, 8, 14, p.spore)
    img.rect(7, 9, 7, 14, _mix(p.spore, (0, 0, 0, 255), 0.18))
    for dx in range(-4, 5):
        h = int((16 - dx * dx) / 6) + 1
        for dy in range(h):
            img.set(8 + dx, 9 - dy, p.accent if (dx + dy) % 5 else p.accent_dark)
    img.rect(4, 9, 11, 9, p.accent_dark)
    for (sx, sy) in ((6, 7), (9, 6), (11, 8), (7, 5)):
        img.set(sx, sy, p.accent_light)
    return img


def _draw_bark(p: Palette) -> Image:
    img = Image(16, 16)
    for y in range(2, 15):
        wobble = 1 if (y // 2) % 2 == 0 else 0
        img.set(6 + wobble, y, p.root_dark)
        img.set(7 + wobble, y, p.root)
        img.set(8 + wobble, y, p.root)
        img.set(9 + wobble, y, p.root_dark if y % 3 else _mix(p.root, (0, 0, 0, 255), 0.25))
    for y in (4, 8, 12):
        img.set(7, y, _mix(p.root, (0, 0, 0, 255), 0.35))
    _leaf_shape(img, 10, 4, 1, p, 3)
    _leaf_shape(img, 6, 11, -1, p, 3)
    img.set(8, 2, p.accent)
    return img


def _draw_vine(p: Palette) -> Image:
    img = Image(16, 16)
    path = [(4, 15), (5, 13), (7, 11), (6, 9), (8, 7), (10, 5), (9, 3)]
    for i in range(len(path) - 1):
        img.line(path[i][0], path[i][1], path[i + 1][0], path[i + 1][1], p.leaf_dark)
    for (x, y) in path:
        img.set(x, y, p.leaf)
    for (x, y, d) in ((5, 12, 1), (7, 10, -1), (6, 8, 1), (9, 6, 1), (10, 4, -1)):
        _leaf_shape(img, x + (1 if d > 0 else -1), y, d, p, 2)
    img.set(9, 2, p.accent)
    img.set(10, 2, p.accent_light)
    return img


def _draw_moss(p: Palette) -> Image:
    img = Image(16, 16)
    img.ellipse(8, 11, 6.0, 4.0, p.leaf_dark)
    img.ellipse(7, 10, 4.5, 2.8, p.leaf)
    for (x, y) in ((5, 10), (8, 9), (11, 11), (6, 12), (10, 13), (9, 12)):
        img.set(x, y, p.leaf_light)
    for (x, y) in ((4, 8), (7, 7), (11, 8), (13, 10)):
        img.line(x, y, x - 1, y - 3, p.leaf_dark)
        img.set(x - 1, y - 4, p.accent if x % 2 else p.leaf_light)
    return img


def _draw_fungus(p: Palette) -> Image:
    img = Image(16, 16)
    img.rect(3, 6, 4, 14, p.root_dark)
    img.rect(4, 6, 4, 14, _mix(p.root, (0, 0, 0, 255), 0.2))
    for dx in range(0, 9):
        h = int(((9 * 9) - (dx - 0)) / 12) + 1
        for dy in range(h):
            img.set(4 + dx, 7 - dy, p.accent if (dx + dy) % 4 else p.accent_dark)
    img.rect(5, 8, 12, 8, p.accent_dark)
    for (sx, sy) in ((7, 6), (10, 5), (12, 6)):
        img.set(sx, sy, p.accent_light)
    return img


MORPHOLOGY_DRAWERS = {
    "LEAF": _draw_leaf,
    "FLOWER": _draw_flower,
    "GRASS": _draw_grass,
    "BERRY": _draw_berry,
    "ROOT": _draw_root,
    "BULB": _draw_bulb,
    "MUSHROOM": _draw_mushroom,
    "BARK": _draw_bark,
    "VINE": _draw_vine,
    "MOSS": _draw_moss,
    "FUNGUS": _draw_fungus,
}


def herb_sprite(herb: dict) -> Image:
    p = Palette(herb)
    drawer = MORPHOLOGY_DRAWERS.get(herb.get("morphology", "LEAF"), _draw_leaf)
    img = drawer(p)
    if p.glow_pixels() >= 2:  # rare herbs get a subtle sparkle so they catch the eye
        for (x, y) in ((3, 4), (12, 6), (4, 12)):
            if img.get(x, y)[3] == 0:
                img.set(x, y, p.accent_light)
    return img.outline((20, 16, 12, 190))


def crop_stage_sprite(herb: dict, stage: int) -> Image:
    """Growth stages 0..3; stage 3 is the harvestable plant."""
    p = Palette(herb)
    img = Image(16, 16)
    if stage == 0:
        for (x, y, h) in ((6, 14, 2), (8, 14, 3), (10, 14, 2)):
            img.line(x, 15, x, y, p.stem)
            img.set(x, y - 1, p.leaf_light if h > 2 else p.leaf)
        return img.outline((20, 16, 12, 160))
    if stage == 1:
        img.line(8, 15, 8, 9, p.stem)
        _leaf_shape(img, 9, 12, 1, p, 2)
        _leaf_shape(img, 7, 11, -1, p, 2)
        img.set(8, 8, p.leaf_light)
        return img.outline((20, 16, 12, 160))
    if stage == 2:
        full = MORPHOLOGY_DRAWERS.get(herb.get("morphology", "LEAF"), _draw_leaf)(p)
        grown = full.scaled_center(0.72).trim_bottom(-2)
        merged = img.copy()
        merged.blit(grown, 0, 0)
        return merged.outline((20, 16, 12, 170))
    return herb_sprite(herb)


def dried_sprite(herb: dict) -> Image:
    return herb_sprite(herb).shade(0.82, 0.42).outline((24, 18, 10, 190))


def powder_sprite(herb: dict) -> Image:
    p = Palette(herb)
    img = Image(16, 16)
    body = p.leaf if herb.get("morphology") in ("ROOT", "BULB", "BARK") else p.accent
    body_dark = _mix(body, (0, 0, 0, 255), 0.3)
    body_light = _mix(body, (255, 255, 255, 255), 0.3)
    img.ellipse(8, 11, 5.0, 3.0, body_dark)
    img.ellipse(8, 10, 4.4, 2.4, body)
    for (x, y) in ((5, 10), (7, 9), (9, 9), (11, 11), (6, 12), (10, 12), (8, 11)):
        img.set(x, y, body_light)
    for (x, y) in ((4, 13), (12, 13), (8, 13)):
        img.set(x, y, body_dark)
    img.set(8, 7, body_light)
    img.set(9, 6, body)
    return img.outline((26, 20, 14, 170))


def extract_sprite(herb: dict) -> Image:
    p = Palette(herb)
    img = Image(16, 16)
    glass = (206, 226, 232, 210)
    img.rect(6, 3, 9, 4, glass)
    img.rect(5, 5, 10, 14, glass)
    img.rect(5, 8, 10, 14, p.accent)
    img.rect(5, 8, 5, 14, _mix(p.accent, (0, 0, 0, 255), 0.25))
    img.rect(6, 6, 9, 7, _mix(p.accent_light, (255, 255, 255, 255), 0.4))
    img.set(4, 6, glass)
    img.set(4, 7, glass)
    img.set(11, 5, glass)
    img.rect(6, 1, 9, 2, (146, 108, 74, 255))
    img.set(7, 10, _mix(p.accent, (255, 255, 255, 255), 0.5))
    return img.outline((18, 14, 10, 190))


def seed_sprite(herb: dict) -> Image:
    p = Palette(herb)
    img = Image(16, 16)
    seed = _mix(p.root, (40, 30, 20, 255), 0.25)
    seed_dark = _mix(seed, (0, 0, 0, 255), 0.3)
    for (x, y) in ((5, 10), (9, 12), (7, 6), (11, 8)):
        img.set(x, y, seed)
        img.set(x + 1, y, seed_dark)
        img.set(x, y + 1, seed_dark)
        img.set(x + 1, y + 1, seed)
    img.set(6, 6, p.leaf_light)
    img.set(6, 5, p.leaf)
    img.set(12, 7, p.leaf_light)
    return img.outline((22, 16, 10, 170))


# ---------------------------------------------------------------------------
# containers, catalysts, tools
# ---------------------------------------------------------------------------

def vial_sprite(liquid=None, cracked: bool = False) -> Image:
    liquid = _c(liquid)
    img = Image(16, 16)
    glass = (204, 224, 232, 220)
    glass_dark = (150, 176, 186, 230)
    img.rect(6, 2, 9, 3, (146, 108, 74, 255))
    img.rect(6, 4, 9, 5, glass_dark)
    img.rect(5, 6, 10, 14, glass)
    img.rect(5, 6, 5, 14, glass_dark)
    img.rect(10, 6, 10, 14, glass_dark)
    if liquid:
        img.rect(6, 8, 9, 13, liquid)
        img.set(6, 9, _mix(liquid, (255, 255, 255, 255), 0.45))
        img.set(9, 12, _mix(liquid, (0, 0, 0, 255), 0.25))
    if cracked:
        img.line(6, 8, 8, 10, (90, 110, 118, 255))
        img.line(8, 10, 7, 12, (90, 110, 118, 255))
    return img.outline((18, 14, 10, 190))


def cup_sprite(liquid=None) -> Image:
    liquid = _c(liquid)
    img = Image(16, 16)
    clay = (176, 132, 104, 255)
    clay_dark = (126, 90, 68, 255)
    clay_light = (206, 166, 134, 255)
    img.rect(3, 6, 11, 7, clay_light)
    img.rect(4, 8, 10, 12, clay)
    img.rect(4, 12, 10, 12, clay_dark)
    img.set(12, 8, clay_dark)
    img.set(12, 9, clay_dark)
    img.rect(2, 13, 12, 14, clay_dark)
    if liquid:
        img.rect(5, 6, 10, 7, liquid)
        img.set(6, 6, _mix(liquid, (255, 255, 255, 255), 0.4))
    return img.outline((22, 16, 12, 190))


def jar_sprite(content=None) -> Image:
    content = _c(content)
    img = Image(16, 16)
    glass = (208, 226, 232, 215)
    glass_dark = (150, 176, 186, 230)
    img.rect(3, 4, 12, 13, glass)
    img.rect(3, 4, 3, 13, glass_dark)
    img.rect(12, 4, 12, 13, glass_dark)
    img.rect(2, 2, 13, 3, (146, 108, 74, 255))
    if content:
        img.rect(4, 8, 11, 12, content)
        img.set(5, 9, _mix(content, (255, 255, 255, 255), 0.4))
    return img.outline((18, 14, 10, 190))


def oil_sprite(color) -> Image:
    """A squat flask of weapon oil: wide body, dark liquid, a droplet on the glass."""
    img = Image(16, 16)
    base = color if isinstance(color, tuple) else parse_color(color)
    glass = (204, 224, 232, 210)
    glass_dark = (150, 176, 186, 230)
    img.rect(7, 1, 8, 3, (146, 108, 74, 255))
    img.rect(5, 4, 10, 5, glass)
    img.rect(3, 6, 12, 14, glass)
    img.rect(3, 6, 3, 14, glass_dark)
    img.rect(12, 6, 12, 14, glass_dark)
    img.rect(4, 9, 11, 13, base)
    img.set(5, 10, _mix(base, (255, 255, 255, 255), 0.4))
    img.set(10, 12, _mix(base, (0, 0, 0, 255), 0.3))
    img.set(6, 6, (250, 250, 240, 255))
    img.rect(6, 6, 9, 7, _mix(base, (255, 255, 255, 255), 0.5))
    return img.outline((18, 14, 10, 190))


def powder_pile_sprite(color) -> Image:
    img = Image(16, 16)
    base = parse_color(color) if isinstance(color, str) else color
    dark = _mix(base, (0, 0, 0, 255), 0.3)
    light = _mix(base, (255, 255, 255, 255), 0.35)
    img.ellipse(8, 11, 5.5, 3.2, dark)
    img.ellipse(8, 10, 4.8, 2.6, base)
    img.ellipse(7, 9, 2.4, 1.4, light)
    for (x, y) in ((4, 12), (12, 12), (8, 13)):
        img.set(x, y, dark)
    return img.outline((26, 20, 14, 170))


def crystal_sprite(color) -> Image:
    img = Image(16, 16)
    base = parse_color(color) if isinstance(color, str) else color
    dark = _mix(base, (0, 0, 0, 255), 0.28)
    light = _mix(base, (255, 255, 255, 255), 0.45)
    img.line(8, 2, 12, 8, light)
    img.line(8, 2, 4, 9, base)
    img.line(12, 8, 8, 14, dark)
    img.line(4, 9, 8, 14, base)
    img.line(4, 9, 12, 8, light)
    img.rect(7, 6, 9, 11, base)
    img.set(8, 5, light)
    img.set(6, 9, dark)
    return img.outline((20, 22, 26, 190))


def page_sprite(ancient: bool = False) -> Image:
    img = Image(16, 16)
    paper = (238, 232, 214, 255) if not ancient else (226, 208, 158, 255)
    paper_dark = (196, 186, 164, 255) if not ancient else (182, 160, 112, 255)
    ink = (86, 70, 48, 255)
    img.rect(3, 2, 12, 14, paper)
    img.rect(3, 2, 3, 14, paper_dark)
    img.rect(12, 2, 12, 14, paper_dark)
    for y in range(4, 13, 2):
        img.rect(5, y, 11, y, ink if not ancient else _mix(ink, paper_dark, 0.3))
    if ancient:
        img.rect(9, 10, 13, 14, (150, 40, 40, 255))
        img.set(11, 12, (222, 190, 120, 255))
    return img.outline((40, 32, 22, 190))


def shears_sprite() -> Image:
    img = Image(16, 16)
    steel = (196, 200, 206, 255)
    steel_dark = (128, 134, 142, 255)
    handle = (140, 60, 50, 255)
    img.line(3, 12, 10, 3, steel)
    img.line(4, 13, 11, 4, steel_dark)
    img.line(12, 12, 5, 3, steel)
    img.line(11, 13, 4, 4, steel_dark)
    img.line(3, 12, 2, 14, handle)
    img.line(12, 12, 13, 14, handle)
    img.set(8, 8, (240, 244, 248, 255))
    return img.outline((30, 26, 24, 190))


def pouch_sprite() -> Image:
    img = Image(16, 16)
    leather = (154, 112, 70, 255)
    leather_dark = (112, 78, 48, 255)
    leather_light = (188, 146, 98, 255)
    img.ellipse(8, 10, 5.0, 4.4, leather)
    img.ellipse(7, 9, 3.0, 2.4, leather_light)
    img.rect(4, 5, 12, 6, leather_dark)
    img.line(4, 5, 12, 5, (206, 176, 120, 255))
    img.set(8, 3, (150, 200, 120, 255))
    img.set(7, 2, (120, 176, 96, 255))
    img.set(8, 7, (206, 176, 120, 255))
    return img.outline((30, 22, 14, 190))


def journal_sprite() -> Image:
    img = Image(16, 16)
    cover = (72, 106, 70, 255)
    cover_dark = (48, 76, 50, 255)
    cover_light = (104, 142, 96, 255)
    page = (232, 226, 206, 255)
    img.rect(3, 2, 13, 14, cover)
    img.rect(3, 2, 3, 14, cover_dark)
    img.rect(13, 2, 13, 14, cover_dark)
    img.rect(4, 3, 12, 3, cover_light)
    img.rect(5, 4, 11, 13, page)
    img.set(6, 6, (150, 60, 50, 255))
    img.set(7, 5, (90, 150, 80, 255))
    img.line(6, 8, 10, 11, (140, 130, 110, 255))
    img.line(10, 8, 6, 11, (140, 130, 110, 255))
    img.rect(12, 7, 13, 9, (196, 164, 92, 255))
    return img.outline((26, 20, 14, 190))


# ---------------------------------------------------------------------------
# block textures (16x16)
# ---------------------------------------------------------------------------

def stone_texture(base=(122, 118, 112, 255), seed: str = "stone") -> Image:
    img = Image(16, 16)
    img.fill(base)
    h = _hash(seed)
    for i in range(16):
        for j in range(16):
            n = ((i * 7 + j * 13 + int(h * 1000)) % 11) / 11.0
            shade = 0.86 + n * 0.24
            img.set(i, j, (min(255, int(base[0] * shade)), min(255, int(base[1] * shade)), min(255, int(base[2] * shade)), 255))
    return img


def wood_texture(base=(142, 106, 66, 255), seed: str = "wood") -> Image:
    img = Image(16, 16)
    img.fill(base)
    h = _hash(seed)
    for y in range(16):
        off = (int(h * 17) + y) % 4
        for x in range(16):
            if (x + off) % 4 == 0:
                img.set(x, y, (int(base[0] * 0.78), int(base[1] * 0.76), int(base[2] * 0.74), 255))
            elif (x + off) % 7 == 0:
                img.set(x, y, (min(255, int(base[0] * 1.12)), min(255, int(base[1] * 1.10)), min(255, int(base[2] * 1.06)), 255))
    return img


def mortar_side_texture() -> Image:
    img = Image(16, 16)
    stone = stone_texture((128, 124, 118, 255), "mortar")
    bowl = (108, 104, 100, 255)
    img.fill((0, 0, 0, 0))
    img.rect(2, 4, 13, 15, stone.get(4, 4))
    for y in range(4, 16):
        for x in range(2, 14):
            img.set(x, y, stone.get(x, y))
    img.rect(2, 3, 13, 4, (152, 148, 140, 255))
    img.rect(2, 3, 2, 15, (92, 88, 86, 255))
    img.rect(13, 3, 13, 15, (86, 82, 80, 255))
    img.rect(1, 15, 14, 15, bowl)
    img.rect(4, 8, 11, 9, (96, 92, 90, 255))
    return img


def mortar_top_texture() -> Image:
    img = Image(16, 16)
    img.fill((0, 0, 0, 0))
    img.ellipse(8, 8, 7.0, 7.0, (146, 142, 136, 255))
    img.ellipse(8, 8, 5.4, 5.4, (74, 70, 68, 255))
    img.ellipse(8, 8, 4.2, 4.2, (58, 54, 54, 255))
    for (x, y) in ((6, 9), (9, 7), (10, 10), (7, 6)):
        img.set(x, y, (110, 132, 84, 255))
    img.ellipse(10, 5, 2.0, 2.0, (150, 146, 140, 255))
    return img


def mill_side_texture() -> Image:
    img = wood_texture((136, 100, 62, 255), "mill_side")
    img.rect(0, 11, 15, 15, (104, 76, 48, 255))
    for x in range(0, 16, 3):
        img.set(x, 12, (86, 62, 40, 255))
    img.rect(3, 2, 12, 2, (156, 120, 78, 255))
    return img


def mill_front_texture() -> Image:
    img = mill_side_texture()
    img.ellipse(8, 7, 5.0, 5.0, (150, 148, 144, 255))
    img.ellipse(8, 7, 3.8, 3.8, (118, 116, 112, 255))
    img.ellipse(8, 7, 1.2, 1.2, (86, 84, 82, 255))
    img.line(8, 3, 8, 11, (134, 132, 128, 255))
    img.line(4, 7, 12, 7, (134, 132, 128, 255))
    return img


def mill_top_texture() -> Image:
    img = wood_texture((122, 90, 56, 255), "mill_top")
    img.ellipse(8, 8, 5.6, 5.0, (58, 42, 28, 255))
    img.ellipse(8, 8, 4.4, 3.8, (34, 24, 18, 255))
    return img


def table_top_texture() -> Image:
    img = wood_texture((132, 96, 58, 255), "table_top")
    img.rect(1, 1, 14, 14, (146, 108, 66, 255))
    for y in range(2, 14, 3):
        img.rect(2, y, 13, y, (122, 88, 52, 255))
    img.rect(2, 2, 5, 5, (74, 106, 68, 255))
    img.set(3, 3, (96, 138, 84, 255))
    img.rect(10, 9, 12, 11, (200, 200, 206, 255))
    img.set(11, 10, (140, 190, 216, 255))
    return img


def table_side_texture() -> Image:
    img = wood_texture((120, 86, 52, 255), "table_side")
    img.rect(0, 0, 15, 2, (146, 108, 66, 255))
    img.rect(0, 3, 15, 3, (98, 70, 42, 255))
    img.line(2, 4, 2, 15, (104, 74, 44, 255))
    img.line(13, 4, 13, 15, (104, 74, 44, 255))
    return img


def rack_texture() -> Image:
    img = Image(16, 16)
    img.fill((0, 0, 0, 0))
    wood = wood_texture((148, 112, 68, 255), "rack")
    for y in range(16):
        for x in range(16):
            if (x < 2 or x > 13 or 6 <= y <= 7) and x in (0, 1, 14, 15, 2, 13, 4, 5, 6, 7, 8, 9, 10, 11, 12) and 0 <= y <= 15:
                pass
    img.rect(1, 0, 2, 15, wood.get(1, 1))
    img.rect(13, 0, 14, 15, wood.get(2, 2))
    img.rect(0, 0, 15, 1, wood.get(3, 3))
    img.rect(1, 6, 14, 7, wood.get(4, 4))
    for x in range(1, 15, 3):
        img.set(x, 8, (110, 82, 50, 255))
    return img


# ---------------------------------------------------------------------------
# GUI textures
# ---------------------------------------------------------------------------

GUI_PANEL = (198, 176, 138, 255)
GUI_PANEL_DARK = (150, 128, 96, 255)
GUI_PANEL_LIGHT = (226, 208, 170, 255)
GUI_SLOT = (110, 92, 68, 255)
GUI_SLOT_INNER = (78, 64, 46, 255)
GUI_INK = (62, 48, 32, 255)
GUI_ACCENT = (96, 128, 78, 255)


def _panel(img: Image, x0: int, y0: int, w: int, h: int) -> None:
    img.rect(x0, y0, x0 + w - 1, y0 + h - 1, GUI_PANEL)
    img.frame(x0, y0, x0 + w - 1, y0 + h - 1, GUI_PANEL_DARK)
    img.rect(x0 + 1, y0 + 1, x0 + w - 2, y0 + 1, GUI_PANEL_LIGHT)
    img.rect(x0 + 1, y0 + 1, x0 + 1, y0 + h - 2, GUI_PANEL_LIGHT)
    # subtle wood grain
    for y in range(y0 + 3, y0 + h - 2, 5):
        for x in range(x0 + 2, x0 + w - 2, 1):
            if (x + y) % 9 == 0:
                img.set(x, y, _mix(GUI_PANEL, GUI_PANEL_DARK, 0.25))


def slot(img: Image, x: int, y: int, size: int = 18) -> None:
    img.rect(x, y, x + size - 1, y + size - 1, GUI_SLOT)
    img.rect(x + 1, y + 1, x + size - 2, y + size - 2, GUI_SLOT_INNER)
    img.rect(x + 1, y + 1, x + size - 2, y + 1, _mix(GUI_SLOT, (0, 0, 0, 255), 0.35))


def arrow(img: Image, x: int, y: int, length: int = 22) -> None:
    img.rect(x, y + 3, x + length - 6, y + 5, GUI_SLOT_INNER)
    for i in range(6):
        img.line(x + length - 6 + i, y + 3 - i + 1, x + length - 6 + i, y + 5 + i - 1, GUI_SLOT_INNER)


# ---------------------------------------------------------------------------
# GUI layout: single source of truth for both the drawn panels and the Java
# screen classes (tools/gen/java.py emits com.herbalistscraft.client.GuiLayout
# from this table, so the two can never drift apart).
# ---------------------------------------------------------------------------

LAYOUT = {
    "mill": {
        "width": 176, "height": 166,
        "input": [55, 17], "additive": [55, 53], "output": [115, 35],
        "arrow": [82, 36, 26], "wheel": [40, 33],
        "player_inv": [7, 83], "hotbar": [7, 141],
        "title": [8, 6],
    },
    "table": {
        "width": 246, "height": 190,
        "base": [20, 21], "herb": [54, 21], "extract": [88, 21],
        "result": [54, 56], "catalyst": [20, 84],
        "player_inv": [7, 106], "hotbar": [7, 164],
        "knowledge": [126, 20, 239, 178], "knowledge_title": [130, 24],
        "title": [8, 6],
    },
    "journal": {
        "width": 240, "height": 190,
        "spine": [0, 0, 13, 189], "title": [16, 4, 236, 16],
        "tab_rail": [16, 20, 44, 186], "tab_x": 18, "tab_y": 24, "tab_w": 24, "tab_h": 20, "tab_gap": 24,
        "list": [48, 20, 130, 186], "detail": [134, 20, 236, 186],
        "rows_visible": 9, "row_height": 18,
    },
}


def gui_mill() -> Image:
    layout = LAYOUT["mill"]
    img = Image(256, 256)
    _panel(img, 0, 0, layout["width"], layout["height"])
    img.rect(6, 5, layout["width"] - 7, 15, GUI_PANEL_DARK)
    img.rect(7, 6, layout["width"] - 8, 14, _mix(GUI_PANEL, GUI_PANEL_LIGHT, 0.5))
    slot(img, *layout["input"])
    slot(img, *layout["additive"])
    slot(img, *layout["output"])
    arrow(img, layout["arrow"][0], layout["arrow"][1], layout["arrow"][2])
    mill_wheel = Image(20, 20)
    for r in (9, 6, 3):
        mill_wheel.ellipse(10, 10, r, r, GUI_SLOT if r != 6 else GUI_SLOT_INNER)
    img.blit(mill_wheel, layout["wheel"][0], layout["wheel"][1])
    for row in range(3):
        for col in range(9):
            slot(img, layout["player_inv"][0] + col * 18, layout["player_inv"][1] + row * 18)
    for col in range(9):
        slot(img, layout["hotbar"][0] + col * 18, layout["hotbar"][1])
    return img


def gui_table() -> Image:
    layout = LAYOUT["table"]
    img = Image(256, 256)
    _panel(img, 0, 0, layout["width"], layout["height"])
    img.rect(6, 5, layout["width"] - 7, 15, GUI_PANEL_DARK)
    img.rect(7, 6, layout["width"] - 8, 14, _mix(GUI_PANEL, GUI_PANEL_LIGHT, 0.5))
    slot(img, *layout["base"])
    slot(img, *layout["herb"])
    slot(img, *layout["extract"])
    slot(img, *layout["result"])
    slot(img, *layout["catalyst"])
    arrow(img, 40, 23, 12)
    arrow(img, 74, 23, 12)
    for y in range(40, layout["result"][1]):
        img.set(layout["result"][0] + 8, y, GUI_SLOT_INNER)
    kx0, ky0, kx1, ky1 = layout["knowledge"]
    img.rect(kx0, ky0, kx1, ky1, _mix(GUI_PANEL, GUI_PANEL_LIGHT, 0.35))
    img.frame(kx0, ky0, kx1, ky1, GUI_PANEL_DARK)
    img.rect(kx0 + 2, ky0 + 2, kx1 - 2, ky0 + 8, _mix(GUI_ACCENT, GUI_PANEL, 0.55))
    for i in range(0, ky1 - ky0 - 26, 6):
        img.rect(kx0 + 3, ky0 + 14 + i, kx1 - 3, ky0 + 14 + i, _mix(GUI_PANEL, GUI_PANEL_DARK, 0.35))
    for row in range(3):
        for col in range(9):
            slot(img, layout["player_inv"][0] + col * 18, layout["player_inv"][1] + row * 18)
    for col in range(9):
        slot(img, layout["hotbar"][0] + col * 18, layout["hotbar"][1])
    return img


def gui_journal() -> Image:
    layout = LAYOUT["journal"]
    img = Image(256, 256)
    _panel(img, 0, 0, layout["width"], layout["height"])
    sx0, sy0, sx1, sy1 = layout["spine"]
    img.rect(sx0, sy0, sx1, sy1, (96, 68, 46, 255))
    img.rect(12, 0, 13, 189, (66, 46, 30, 255))
    tx0, ty0, tx1, ty1 = layout["title"]
    img.rect(tx0, ty0, tx1, ty1, _mix(GUI_ACCENT, GUI_PANEL, 0.45))
    img.frame(tx0, ty0, tx1, ty1, GUI_PANEL_DARK)
    rx0, ry0, rx1, ry1 = layout["tab_rail"]
    img.rect(rx0, ry0, rx1, ry1, _mix(GUI_PANEL, GUI_PANEL_DARK, 0.18))
    lx0, ly0, lx1, ly1 = layout["list"]
    img.rect(lx0, ly0, lx1, ly1, _mix(GUI_PANEL, (255, 255, 255, 255), 0.16))
    img.frame(lx0, ly0, lx1, ly1, GUI_PANEL_DARK)
    dx0, dy0, dx1, dy1 = layout["detail"]
    img.rect(dx0, dy0, dx1, dy1, _mix(GUI_PANEL, (255, 255, 255, 255), 0.24))
    img.frame(dx0, dy0, dx1, dy1, GUI_PANEL_DARK)
    for y in range(dy0 + 4, dy1 - 2, 8):
        img.rect(dx0 + 2, y, dx1 - 2, y, _mix(GUI_PANEL, GUI_PANEL_DARK, 0.22))
    return img


# ---------------------------------------------------------------------------
# icons
# ---------------------------------------------------------------------------

def property_icon(name: str, color) -> Image:
    img = Image(16, 16)
    c = parse_color(color) if isinstance(color, str) else color
    dark = _mix(c, (0, 0, 0, 255), 0.35)
    light = _mix(c, (255, 255, 255, 255), 0.45)
    if name == "healing":
        img.ellipse(6, 7, 3.6, 3.6, c)
        img.ellipse(10, 7, 3.6, 3.6, c)
        for i in range(8):
            img.line(8, 5 + i, 3 + i, 12 - i if i < 4 else 8, dark)
        img.rect(4, 11, 11, 12, dark)
        img.line(4, 4, 5, 12, light)
        img.line(11, 4, 12, 12, light)
    elif name == "hemostatic":
        img.ellipse(8, 7, 4.4, 4.4, c)
        img.rect(6, 6, 9, 11, c)
        img.set(7, 6, light)
        img.line(3, 12, 12, 3, (240, 240, 240, 255))
    elif name == "cooling":
        for i in range(16):
            img.set(8, i, c if i % 2 == 0 else light)
            img.set(i, 8, c if i % 2 == 0 else light)
        img.line(3, 3, 12, 12, c)
        img.line(12, 3, 3, 12, c)
        img.set(8, 8, light)
    elif name == "warming":
        img.ellipse(8, 10, 3.6, 3.0, c)
        img.line(8, 9, 8, 2, light)
        img.line(6, 6, 6, 3, c)
        img.line(10, 7, 10, 4, c)
        img.rect(6, 12, 9, 13, dark)
    elif name == "antiseptic":
        for i in range(16):
            img.set(8, i, light if i % 4 else c)
            img.set(i, 8, light if i % 4 else c)
        img.set(4, 4, c)
        img.set(11, 4, c)
        img.set(4, 11, c)
        img.set(11, 11, c)
    elif name == "stimulant":
        img.line(9, 2, 5, 9, c)
        img.line(5, 9, 10, 8, c)
        img.line(10, 8, 6, 14, light)
        img.line(9, 2, 11, 8, dark)
    elif name == "sedative":
        img.ellipse(8, 8, 6.0, 6.0, dark)
        img.ellipse(8, 8, 5.0, 5.0, c)
        img.ellipse(10, 6, 3.6, 3.6, (0, 0, 0, 0))
        for i in range(3):
            img.set(10 + i, 4 + i, light)
    elif name == "toxic":
        img.ellipse(8, 7, 4.6, 4.4, c)
        img.rect(5, 10, 10, 13, c)
        img.rect(6, 11, 9, 12, (16, 16, 16, 255))
        img.set(6, 6, light)
        img.set(9, 5, light)
        img.set(4, 9, dark)
        img.set(11, 9, dark)
    elif name == "fire_ward":
        img.ellipse(8, 12, 5.0, 3.0, dark)
        img.rect(3, 6, 12, 11, dark)
        img.line(3, 6, 8, 1, dark)
        img.line(12, 6, 8, 1, dark)
        img.ellipse(8, 10, 2.4, 2.6, c)
        img.line(8, 9, 8, 5, light)
    elif name == "cold_ward":
        img.rect(3, 6, 12, 12, dark)
        img.line(3, 6, 8, 1, dark)
        img.line(12, 6, 8, 1, dark)
        for i in range(4, 12):
            img.set(i, 8, c)
        img.line(5, 10, 10, 10, light)
        img.line(8, 5, 8, 11, c)
    elif name == "hydration":
        img.ellipse(8, 9, 4.0, 4.6, c)
        img.line(8, 2, 5, 7, c)
        img.line(8, 2, 11, 7, c)
        img.set(6, 8, light)
        img.set(6, 9, light)
    elif name == "energy":
        for i in range(16):
            for j in range(16):
                if (i - 8) ** 2 + (j - 8) ** 2 <= 30:
                    img.set(i, j, c if (i + j) % 3 else light)
        for i in range(3, 13):
            img.set(i, 8, light)
            img.set(8, i, light)
    elif name == "respiratory":
        img.ellipse(5, 8, 2.6, 4.6, c)
        img.ellipse(11, 8, 2.6, 4.6, c)
        img.rect(7, 4, 8, 8, dark)
        img.line(7, 4, 7, 7, light)
        img.line(8, 4, 8, 7, light)
    elif name == "regeneration":
        for r in (6, 4, 2):
            for a in range(0, 360, 20):
                import math
                x = int(8 + math.cos(math.radians(a)) * r)
                y = int(8 + math.sin(math.radians(a)) * r)
                img.set(x, y, c if r != 4 else light)
        img.set(8, 8, dark)
    else:
        img.ellipse(8, 8, 5.0, 5.0, c)
    return img.outline((24, 18, 12, 200))


def tab_icon(name: str) -> Image:
    img = Image(16, 16)
    if name == "herbs":
        img.line(8, 15, 8, 4, (72, 108, 60, 255))
        for i in range(3):
            img.line(8, 11 - i * 3, 12, 8 - i * 3, (86, 132, 70, 255))
            img.line(8, 10 - i * 3, 4, 7 - i * 3, (86, 132, 70, 255))
        img.set(8, 3, (150, 190, 110, 255))
    elif name == "properties":
        img.ellipse(8, 8, 5.4, 5.4, (200, 160, 80, 255))
        img.ellipse(8, 8, 3.6, 3.6, (120, 90, 40, 255))
        img.set(8, 8, (240, 210, 130, 255))
    elif name == "recipes":
        img.rect(3, 3, 12, 12, (226, 216, 190, 255))
        img.frame(3, 3, 12, 12, (140, 120, 90, 255))
        for y in range(5, 11, 2):
            img.rect(5, y, 10, y, (110, 96, 76, 255))
    elif name == "research":
        img.ellipse(6, 6, 3.4, 3.4, (206, 226, 232, 255))
        img.ellipse(6, 6, 1.8, 1.8, (96, 150, 170, 255))
        img.line(9, 9, 13, 13, (140, 110, 80, 255))
    elif name == "seasons":
        for r, c in ((6, (150, 190, 120, 255)), (4, (220, 190, 90, 255)), (2, (200, 120, 60, 255))):
            for a in range(0, 360, 15):
                import math
                img.set(int(8 + math.cos(math.radians(a)) * r), int(8 + math.sin(math.radians(a)) * r), c)
    elif name == "biomes":
        img.ellipse(8, 11, 6.0, 3.4, (120, 150, 110, 255))
        img.line(3, 9, 8, 4, (140, 170, 130, 255))
        img.line(13, 9, 8, 4, (110, 140, 105, 255))
        img.set(8, 3, (230, 236, 240, 255))
    else:
        img.ellipse(8, 8, 5.0, 5.0, (150, 150, 150, 255))
    return img.outline((28, 22, 16, 200))


def rarity_badge(rarity: str) -> Image:
    colors = {"COMMON": (168, 168, 168), "UNCOMMON": (108, 196, 96), "RARE": (96, 148, 224),
              "VERY_RARE": (188, 108, 224), "LEGENDARY": (232, 168, 64)}
    img = Image(8, 8)
    c = colors.get(rarity, (168, 168, 168)) + (255,)
    light = _mix(c, (255, 255, 255, 255), 0.4)
    for i in range(8):
        for j in range(8):
            d = abs(i - 3.5) + abs(j - 3.5)
            if d <= 4.2:
                img.set(i, j, light if d < 2 else c)
    return img.outline((30, 24, 18, 210))


def arrow_icon() -> Image:
    img = Image(16, 16)
    img.rect(2, 6, 9, 9, GUI_SLOT_INNER)
    for i in range(5):
        img.line(9 + i, 3 + i, 9 + i, 12 - i, GUI_SLOT_INNER)
    return img


def check_icon() -> Image:
    img = Image(8, 8)
    img.line(1, 4, 3, 6, (108, 196, 96, 255))
    img.line(3, 6, 6, 1, (108, 196, 96, 255))
    return img


def lock_icon() -> Image:
    img = Image(8, 8)
    img.rect(1, 3, 6, 7, (150, 138, 110, 255))
    img.line(2, 3, 2, 1, (110, 100, 78, 255))
    img.line(5, 3, 5, 1, (110, 100, 78, 255))
    img.set(2, 1, (110, 100, 78, 255))
    img.set(5, 1, (110, 100, 78, 255))
    img.set(3, 5, (80, 72, 58, 255))
    return img


def mortar_icon() -> Image:
    """Inventory icon for the Mortar and Pestle (a small bowl with a leaning pestle)."""
    img = Image(16, 16)
    stone = (140, 136, 130, 255)
    stone_dark = (98, 94, 90, 255)
    stone_light = (176, 172, 164, 255)
    img.ellipse(8, 11, 5.6, 3.6, stone_dark)
    img.ellipse(8, 10, 5.0, 3.0, stone)
    img.ellipse(8, 9, 3.6, 1.8, (74, 70, 68, 255))
    img.set(5, 8, stone_light)
    img.rect(7, 2, 8, 9, (126, 94, 60, 255))
    img.set(7, 2, (150, 116, 76, 255))
    img.set(8, 5, (104, 76, 48, 255))
    return img.outline((24, 20, 16, 200))


def workstation_icon(kind: str) -> Image:
    """Compact inventory icons for the workstations (each one reads at 16x16)."""
    img = Image(16, 16)
    wood = (146, 108, 66, 255)
    wood_dark = (104, 76, 46, 255)
    wood_light = (186, 146, 96, 255)
    stone = (150, 146, 140, 255)
    if kind == "mill":
        img.rect(2, 8, 13, 14, wood)
        img.rect(1, 6, 14, 8, wood_light)
        img.rect(3, 3, 12, 6, wood_dark)
        img.ellipse(8, 11, 3.4, 3.0, stone)
        img.ellipse(8, 11, 1.4, 1.2, (86, 84, 82, 255))
        img.set(12, 5, wood_light)
    elif kind == "table":
        img.rect(1, 4, 14, 6, wood_light)
        img.rect(1, 5, 14, 6, wood)
        img.rect(2, 6, 3, 14, wood_dark)
        img.rect(12, 6, 13, 14, wood_dark)
        img.rect(6, 1, 9, 4, (74, 106, 68, 255))
        img.set(7, 2, (110, 156, 96, 255))
        img.rect(10, 1, 13, 3, (206, 226, 232, 255))
        img.set(11, 2, (150, 190, 216, 255))
    else:  # rack
        img.rect(2, 1, 3, 14, wood)
        img.rect(12, 1, 13, 14, wood)
        img.rect(1, 1, 14, 2, wood_light)
        img.rect(2, 7, 13, 8, wood_dark)
        img.rect(5, 2, 10, 6, (126, 158, 96, 255))
        img.set(6, 3, (156, 190, 118, 255))
        img.rect(4, 9, 11, 13, (168, 150, 96, 255))
    return img.outline((24, 20, 16, 200))


def particle_sprite(name: str) -> Image:
    img = Image(8, 8)
    if name == "herbal_spark":
        c = (140, 200, 110, 255)
        for i in range(8):
            img.set(3, i, c)
            img.set(i, 3, c)
        img.rect(2, 2, 5, 5, (96, 156, 78, 255))
        img.set(3, 3, (220, 248, 190, 255))
    elif name == "frost_spark":
        c = (206, 236, 250, 255)
        for i in range(8):
            for j in range(8):
                if (i - 3.5) ** 2 + (j - 3.5) ** 2 <= 9:
                    img.set(i, j, c if (i + j) % 3 else (150, 190, 230, 255))
        img.set(3, 3, (255, 255, 255, 255))
    elif name == "ember_spark":
        for i in range(8):
            for j in range(8):
                d = (i - 3.5) ** 2 + (j - 3.5) ** 2
                if d <= 10:
                    img.set(i, j, (240, 150, 60, 255) if d > 5 else (255, 220, 140, 255))
        img.set(3, 2, (255, 250, 200, 255))
    elif name == "toxic_smoke":
        for i in range(8):
            for j in range(8):
                if (i - 3.5) ** 2 / 9 + (j - 3.5) ** 2 / 6 <= 1.2:
                    img.set(i, j, (120, 150, 80, 255) if (i + j) % 2 else (86, 110, 62, 255))
        img.set(3, 3, (160, 190, 120, 255))
    elif name == "dried_leaf":
        img.line(1, 6, 6, 1, (150, 130, 80, 255))
        img.line(1, 5, 5, 1, (176, 156, 100, 255))
        img.set(3, 3, (196, 176, 120, 255))
    else:
        img.rect(2, 2, 5, 5, (200, 200, 200, 255))
    return img
