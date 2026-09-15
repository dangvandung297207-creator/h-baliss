#!/usr/bin/env python3
"""Builds Herbalist's Craft's generated sources from the content files.

    python3 tools/generate.py            # write Java, assets and data into the repo
    python3 tools/generate.py --check    # generate into a temp dir and report only

``tools/content/*.json`` is the single source of truth. Editing it and re-running this script
is the supported way to add herbs and medicines: item ids, textures, models, recipes, loot,
advancements, lang keys and the registration Java all come from the same place.

Needs pillow (and numpy/soundfile for the procedural sounds):

    python3 -m venv /tmp/venv && /tmp/venv/bin/pip install pillow numpy soundfile
"""
from __future__ import annotations

import argparse
import json
import pathlib
import shutil
import sys
import tempfile

REPO = pathlib.Path(__file__).resolve().parent.parent
CONTENT = REPO / "tools" / "content"

sys.path.insert(0, str(REPO))

from tools.gen import assets, data, java, sounds  # noqa: E402


def load(name: str):
    path = CONTENT / f"{name}.json"
    if not path.exists():
        raise SystemExit(f"missing content file: {path}")
    return json.loads(path.read_text())


def load_content() -> dict:
    herbs = load("herbs")["herbs"]
    medicines = load("medicines")["medicines"]
    item_file = load("items")
    lang = load("lang")
    item_file["items"] = [i for i in item_file["items"] if i.get("disabled") is not True]
    return {
        "herbs": herbs,
        "medicines": medicines,
        "items": item_file["items"],
        "experimental": item_file["experimental"],
        "lang": lang,
    }


def validate(content: dict) -> None:
    herbs, medicines = content["herbs"], content["medicines"]
    herb_ids = {h["id"] for h in herbs}
    if len(herb_ids) != len(herbs):
        raise SystemExit("duplicate herb ids in herbs.json")
    properties = set(content["lang"]["properties"])
    forms = set(content["lang"]["forms"])
    for herb in herbs:
        for form in herb.get("forms", ["fresh", "seed"]):
            if form not in forms:
                raise SystemExit(f"{herb['id']}: unknown form {form}")
        for key, value in herb.get("properties", {}).items():
            if value and key not in properties:
                raise SystemExit(f"{herb['id']}: unknown property {key}")
    seen = set()
    for medicine in medicines:
        if medicine["id"] in seen:
            raise SystemExit(f"duplicate medicine id {medicine['id']}")
        seen.add(medicine["id"])
        for effect in medicine.get("effects", []) + medicine.get("side_effects", []):
            if effect.get("herb") and effect["herb"] not in herb_ids:
                raise SystemExit(f"{medicine['id']}: unknown herb {effect['herb']}")


def generate(root: pathlib.Path, log=print) -> dict:
    content = load_content()
    validate(content)
    stats: dict = {}
    log(f"content: {len(content['herbs'])} herbs, {len(content['medicines'])} medicines, "
        f"{len(content['items'])} items, {len(content['experimental'])} experimental")
    stats.update(assets.generate(root, content["herbs"], content["medicines"], content["items"],
                                 content["experimental"], content["lang"], log=log))
    stats.update(data.generate(root, content["herbs"], content["medicines"], content["lang"], log=log))
    java.generate(root, content["herbs"], content["medicines"], content["items"],
                  content["experimental"], log=log)
    stats["sounds"] = sounds.generate_sounds(root, log=log)
    return stats


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--check", action="store_true",
                        help="generate into a temporary directory and throw the result away")
    parser.add_argument("--root", type=pathlib.Path, default=REPO,
                        help="repository root to generate into (default: this repository)")
    args = parser.parse_args()

    if args.check:
        with tempfile.TemporaryDirectory() as tmp:
            stats = generate(pathlib.Path(tmp))
            counts = {key: value for key, value in stats.items() if isinstance(value, (int, bool))}
            print(f"check ok: {counts}")
            return 0

    stats = generate(args.root.resolve())
    counts = {key: value for key, value in stats.items() if isinstance(value, (int, bool))}
    print("generated:", ", ".join(f"{key}={value}" for key, value in counts.items()))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
