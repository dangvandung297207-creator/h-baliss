"""Tiny NBT writer, used to emit Minecraft structure templates (``.nbt``).

Structure templates are gzip-compressed NBT. Writing them from a small blueprint DSL in
Python means the herbalist structures are fully data-driven (vanilla ``minecraft:jigsaw``
places them), need no custom structure classes, and are still easy to reshape later.
"""
from __future__ import annotations

import gzip
import struct

TAG_END = 0
TAG_BYTE = 1
TAG_SHORT = 2
TAG_INT = 3
TAG_LONG = 4
TAG_FLOAT = 5
TAG_DOUBLE = 6
TAG_BYTE_ARRAY = 7
TAG_STRING = 8
TAG_LIST = 9
TAG_COMPOUND = 10
TAG_INT_ARRAY = 11
TAG_LONG_ARRAY = 12


class Compound(dict):
    """Marker for a TAG_Compound; a plain dict is also written as a compound."""


class List:
    """A TAG_List with an explicit element type."""

    def __init__(self, element_type, values):
        self.element_type = element_type
        self.values = list(values)


class Byte(int):
    pass


class Short(int):
    pass


class Long(int):
    pass


class Float(float):
    pass


class Double(float):
    pass


def _write_payload(out: bytearray, tag_type: int, value) -> None:
    if tag_type == TAG_BYTE:
        out += struct.pack(">b", int(value))
    elif tag_type == TAG_SHORT:
        out += struct.pack(">h", int(value))
    elif tag_type == TAG_INT:
        out += struct.pack(">i", int(value))
    elif tag_type == TAG_LONG:
        out += struct.pack(">q", int(value))
    elif tag_type == TAG_FLOAT:
        out += struct.pack(">f", float(value))
    elif tag_type == TAG_DOUBLE:
        out += struct.pack(">d", float(value))
    elif tag_type == TAG_BYTE_ARRAY:
        out += struct.pack(">i", len(value))
        out += bytes((v & 0xFF for v in value))
    elif tag_type == TAG_STRING:
        encoded = value.encode("utf-8")
        out += struct.pack(">H", len(encoded)) + encoded
    elif tag_type == TAG_LIST:
        out += struct.pack(">b", value.element_type)
        out += struct.pack(">i", len(value.values))
        for item in value.values:
            _write_payload(out, value.element_type, item)
    elif tag_type == TAG_COMPOUND:
        for key, item in value.items():
            item_type = _tag_type(item)
            encoded = key.encode("utf-8")
            out += struct.pack(">b", item_type) + struct.pack(">H", len(encoded)) + encoded
            _write_payload(out, item_type, item)
        out += struct.pack(">b", TAG_END)
    elif tag_type == TAG_INT_ARRAY:
        out += struct.pack(">i", len(value))
        for v in value:
            out += struct.pack(">i", int(v))
    elif tag_type == TAG_LONG_ARRAY:
        out += struct.pack(">i", len(value))
        for v in value:
            out += struct.pack(">q", int(v))
    else:  # pragma: no cover - guarded by _tag_type
        raise ValueError(f"unsupported tag type {tag_type}")


def _tag_type(value) -> int:
    if isinstance(value, bool):
        return TAG_BYTE
    if isinstance(value, Byte):
        return TAG_BYTE
    if isinstance(value, Short):
        return TAG_SHORT
    if isinstance(value, Long):
        return TAG_LONG
    if isinstance(value, Float):
        return TAG_FLOAT
    if isinstance(value, Double):
        return TAG_DOUBLE
    if isinstance(value, int):
        return TAG_INT
    if isinstance(value, float):
        return TAG_DOUBLE
    if isinstance(value, str):
        return TAG_STRING
    if isinstance(value, List):
        return TAG_LIST
    if isinstance(value, (dict, Compound)):
        return TAG_COMPOUND
    if isinstance(value, (bytes, bytearray)):
        return TAG_BYTE_ARRAY
    raise TypeError(f"cannot serialise {value!r}")


def write_nbt(path, name: str, root) -> None:
    """Write ``root`` (a compound) as a gzipped NBT file with the given root name."""
    out = bytearray()
    out += struct.pack(">b", TAG_COMPOUND)
    encoded = name.encode("utf-8")
    out += struct.pack(">H", len(encoded)) + encoded
    _write_payload(out, TAG_COMPOUND, root)
    path.parent.mkdir(parents=True, exist_ok=True)
    with gzip.open(path, "wb") as handle:
        handle.write(bytes(out))


def write_nbt_bytes(name: str, root) -> bytes:
    out = bytearray()
    out += struct.pack(">b", TAG_COMPOUND)
    encoded = name.encode("utf-8")
    out += struct.pack(">H", len(encoded)) + encoded
    _write_payload(out, TAG_COMPOUND, root)
    return gzip.compress(bytes(out))


def int_list(values):
    return List(TAG_INT, [int(v) for v in values])


def double_list(values):
    return List(TAG_DOUBLE, [float(v) for v in values])


def string_list(values):
    return List(TAG_STRING, list(values))


def compound_list(values):
    return List(TAG_COMPOUND, list(values))
