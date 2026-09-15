"""Shared herb rules that the item, recipe, texture and data generators all agree on.

A herb's item forms decide which items exist, which Mortar/Mill recipes can exist, and which
ingredients the medicines can ask for. Keeping the rule here means those three things can never
drift apart: a herb always gets its declared forms, plus any form a medicine needs from it.
"""
from __future__ import annotations

ALL_FORMS = ("fresh", "seed", "dried", "powder", "extract")


def forms_of(herb: dict, medicines=()) -> list[str]:
    """The item forms ``herb`` actually has: declared forms plus whatever medicines demand.

    Content may narrow a herb's forms (for example a berry that is never powdered), but a
    medicine that lists ``{"herb": "comfrey", "form": "extract"}`` always gets a real item to
    ask for, so the recipe it produces is always craftable.
    """
    forms = set(herb.get("forms") or ALL_FORMS)
    herb_id = herb.get("id")
    for medicine in medicines:
        craft = medicine.get("craft") or {}
        if craft.get("herb") == herb_id and craft.get("form"):
            forms.add(craft["form"])
        if craft.get("extract") == herb_id:
            forms.add("extract")
    return [form for form in ALL_FORMS if form in forms]
