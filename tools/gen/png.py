"""Minimal dependency-free PNG writer.

Herbalist's Craft generates every texture from code so that the whole mod stays
consistent and reproducible: no binary blobs, no drifting art styles. This module
only needs the Python standard library (zlib + struct).
"""
from __future__ import annotations

import struct
import zlib


class Image:
    """An RGBA pixel buffer that can be written out as a PNG."""

    __slots__ = ("width", "height", "pixels")

    def __init__(self, width: int, height: int, color=(0, 0, 0, 0)):
        self.width = width
        self.height = height
        self.pixels = [color for _ in range(width * height)]

    # -- basic access ------------------------------------------------------
    def set(self, x: int, y: int, color) -> None:
        if 0 <= x < self.width and 0 <= y < self.height:
            self.pixels[y * self.width + x] = color

    def get(self, x: int, y: int):
        if 0 <= x < self.width and 0 <= y < self.height:
            return self.pixels[y * self.width + x]
        return (0, 0, 0, 0)

    def fill(self, color) -> None:
        self.pixels = [color for _ in range(self.width * self.height)]

    def rect(self, x0: int, y0: int, x1: int, y1: int, color) -> None:
        for y in range(y0, y1 + 1):
            for x in range(x0, x1 + 1):
                self.set(x, y, color)

    def frame(self, x0: int, y0: int, x1: int, y1: int, color) -> None:
        for x in range(x0, x1 + 1):
            self.set(x, y0, color)
            self.set(x, y1, color)
        for y in range(y0, y1 + 1):
            self.set(x0, y, color)
            self.set(x1, y, color)

    def line(self, x0: int, y0: int, x1: int, y1: int, color) -> None:
        dx = abs(x1 - x0)
        dy = -abs(y1 - y0)
        sx = 1 if x0 < x1 else -1
        sy = 1 if y0 < y1 else -1
        err = dx + dy
        while True:
            self.set(x0, y0, color)
            if x0 == x1 and y0 == y1:
                break
            e2 = 2 * err
            if e2 >= dy:
                err += dy
                x0 += sx
            if e2 <= dx:
                err += dx
                y0 += sy

    def ellipse(self, cx: float, cy: float, rx: float, ry: float, color) -> None:
        for y in range(self.height):
            for x in range(self.width):
                dx = (x + 0.5 - cx) / max(rx, 1e-6)
                dy = (y + 0.5 - cy) / max(ry, 1e-6)
                if dx * dx + dy * dy <= 1.0:
                    self.set(x, y, color)

    def blit(self, other: "Image", ox: int = 0, oy: int = 0) -> None:
        for y in range(other.height):
            for x in range(other.width):
                c = other.get(x, y)
                if c[3] > 0:
                    self.set(x + ox, y + oy, c)

    def copy(self) -> "Image":
        out = Image(self.width, self.height)
        out.pixels = list(self.pixels)
        return out

    def sub(self, x0: int, y0: int, w: int, h: int) -> "Image":
        out = Image(w, h)
        for y in range(h):
            for x in range(w):
                out.set(x, y, self.get(x0 + x, y0 + y))
        return out

    # -- transformations ---------------------------------------------------
    def shade(self, amount: float, desaturate: float = 0.0) -> "Image":
        """amount < 1 darkens, > 1 lightens. desaturate 0..1 pulls toward grey."""
        out = Image(self.width, self.height)
        for i, (r, g, b, a) in enumerate(self.pixels):
            if a == 0:
                out.pixels[i] = (r, g, b, a)
                continue
            if desaturate > 0:
                grey = 0.3 * r + 0.59 * g + 0.11 * b
                r = int(r + (grey - r) * desaturate)
                g = int(g + (grey - g) * desaturate)
                b = int(b + (grey - b) * desaturate)
            out.pixels[i] = (
                max(0, min(255, int(r * amount))),
                max(0, min(255, int(g * amount))),
                max(0, min(255, int(b * amount))),
                a,
            )
        return out

    def outline(self, color, diagonal: bool = False) -> "Image":
        """Adds a 1px outline around every opaque pixel."""
        out = self.copy()
        offsets = [(-1, 0), (1, 0), (0, -1), (0, 1)]
        if diagonal:
            offsets += [(-1, -1), (1, -1), (-1, 1), (1, 1)]
        for y in range(self.height):
            for x in range(self.width):
                if self.get(x, y)[3] != 0:
                    continue
                for dx, dy in offsets:
                    if self.get(x + dx, y + dy)[3] != 0:
                        out.set(x, y, color)
                        break
        return out

    def trim_bottom(self, rows: int) -> "Image":
        """Moves the sprite up by ``rows`` (used to make seed/growth variants)."""
        out = Image(self.width, self.height)
        for y in range(self.height):
            for x in range(self.width):
                if y + rows < self.height:
                    out.set(x, y, self.get(x, y + rows))
        return out

    def scaled_center(self, factor: float) -> "Image":
        out = Image(self.width, self.height)
        cx = (self.width - 1) / 2.0
        cy = (self.height - 1) / 2.0
        for y in range(self.height):
            for x in range(self.width):
                sx = int(round(cx + (x - cx) / factor))
                sy = int(round(cy + (y - cy) / factor))
                out.set(x, y, self.get(sx, sy))
        return out

    # -- output ------------------------------------------------------------
    def to_png(self) -> bytes:
        raw = bytearray()
        for y in range(self.height):
            raw.append(0)  # filter type 0
            for x in range(self.width):
                r, g, b, a = self.pixels[y * self.width + x]
                raw += bytes((r & 0xFF, g & 0xFF, b & 0xFF, a & 0xFF))
        header = _png_chunk(b"IHDR", struct.pack(">IIBBBBB", self.width, self.height, 8, 6, 0, 0, 0))
        return b"\x89PNG\r\n\x1a\n" + header + _png_chunk(b"IDAT", zlib.compress(bytes(raw), 9)) + _png_chunk(b"IEND", b"")


def _png_chunk(tag: bytes, data: bytes) -> bytes:
    return struct.pack(">I", len(data)) + tag + data + struct.pack(">I", zlib.crc32(tag + data) & 0xFFFFFFFF)


def write_png(path, image: Image) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_bytes(image.to_png())


def parse_color(value: str, alpha: int = 255):
    value = value.lstrip("#")
    if len(value) == 6:
        return (int(value[0:2], 16), int(value[2:4], 16), int(value[4:6], 16), alpha)
    if len(value) == 8:
        return (int(value[0:2], 16), int(value[2:4], 16), int(value[4:6], 16), int(value[6:8], 16))
    raise ValueError(f"bad color {value!r}")
