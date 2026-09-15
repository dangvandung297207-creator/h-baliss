"""Generates the registration Java that must stay in lockstep with the content files.

Editing ``tools/content/*.json`` and re-running ``tools/generate.py`` is the supported way
to add herbs, medicines and workstations: the item ids, texture names, lang keys, recipes,
loot tables and the registration code below all come from the same source of truth.
"""
from __future__ import annotations

import pathlib

from .art import LAYOUT

MODID = "herbalistscraft"
PACKAGE = "com.herbalistscraft"
# The package name is also the on-disk path below src/main/java.
PACKAGE_PATH = pathlib.Path(*PACKAGE.split("."))

HEADER = """/*
 * GENERATED FILE - do not edit by hand.
 * Produced by tools/gen/java.py from tools/content/*.json; run `python3 tools/generate.py`.
 */
package {package};

"""

VANILLA_RARITY = {"COMMON": "Rarity.COMMON", "UNCOMMON": "Rarity.UNCOMMON", "RARE": "Rarity.RARE",
                  "VERY_RARE": "Rarity.EPIC", "LEGENDARY": "Rarity.EPIC"}

FORM_CONSTANT = {"fresh": "FRESH", "seed": "SEED", "dried": "DRIED", "powder": "POWDER", "extract": "EXTRACT"}
FORM_ITEM = {"fresh": "{herb}", "seed": "{herb}_seeds", "dried": "dried_{herb}",
             "powder": "{herb}_powder", "extract": "{herb}_extract"}
FORM_STACK = {"fresh": 64, "seed": 64, "dried": 64, "powder": 64, "extract": 16}
FORM_QUALITY = {"fresh": 1.0, "dried": 0.8, "powder": 1.0, "extract": 1.5, "seed": 1.0}

# Default stack size per item kind, with per-id overrides, and the behaviour class for the
# handful of items that do more than sit in an inventory.
ITEM_STACK = {"MATERIAL": 64, "BASE": 16, "CATALYST": 64, "TOOL": 1, "KNOWLEDGE": 1,
              "glass_vial": 16, "clay_cup": 16, "moonwater": 8, "frost_crystal": 32,
              "life_essence": 16, "shadow_ichor": 16}
ITEM_RARITY = {"moonwater": "Rarity.UNCOMMON", "frost_crystal": "Rarity.UNCOMMON",
               "life_essence": "Rarity.RARE", "shadow_ichor": "Rarity.RARE",
               "herbalists_journal": "Rarity.UNCOMMON"}
ITEM_SPECIAL = {
    "glass_vial": "new FillableContainerItem(p, FillTarget.WATER_VIAL)",
    "clay_cup": "new FillableContainerItem(p, FillTarget.SPRING_WATER_CUP)",
    "pruning_shears": "new PruningShearsItem(p.durability(238))",
    "seed_pouch": "new SeedPouchItem(p)",
    "herbalists_journal": "new JournalItem(p)",
    "journal_page_herbal": "new JournalPageItem(p, JournalPageKind.HERBAL)",
    "journal_page_medicinal": "new JournalPageItem(p, JournalPageKind.MEDICINAL)",
    "journal_page_toxic": "new JournalPageItem(p, JournalPageKind.TOXIC)",
    "journal_page_ancient": "new JournalPageItem(p, JournalPageKind.ANCIENT)",
}
EXPERIMENTAL_SPECIAL = {
    "experimental_tonic": "new ExperimentalTonicItem",
    "experimental_tea": "new ExperimentalTeaItem",
    "failed_mixture": "new FailedMixtureItem",
}


def _const(name: str) -> str:
    return name.upper().replace("-", "_")


def _write(path: pathlib.Path, body: str) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(body)


def generate(root: pathlib.Path, herbs: list, medicines: list, items: list, experimental: list, log=print) -> None:
    src = root / "src/main/java" / PACKAGE_PATH
    _mod_herbs(src, herbs)
    _mod_medicines(src, medicines)
    _mod_items(src, herbs, medicines, items, experimental)
    _mod_blocks(src, herbs, items)
    _gui_layout(src)
    log("  java: ModHerbs, ModMedicines, ModItems, ModBlocks and GuiLayout regenerated")


# ---------------------------------------------------------------------------

def _mod_herbs(src: pathlib.Path, herbs: list) -> None:
    out = [HEADER.format(package=f"{PACKAGE}.registry")]
    out.append("""import com.herbalistscraft.herb.HerbCategory;
import com.herbalistscraft.herb.HerbDefinition;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/** Every herb id in Herbalist's Craft, as registry keys into the synced herb datapack registry. */
public final class ModHerbs {
    public static final ResourceKey<Registry<HerbDefinition>> REGISTRY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(""" + f'"{MODID}"' + """, "herb"));

    private ModHerbs() {}

    public static ResourceKey<HerbDefinition> key(String path) {
        return ResourceKey.create(REGISTRY, ResourceLocation.fromNamespaceAndPath(""" + f'"{MODID}"' + """, path));
    }
""")
    for herb in herbs:
        out.append(f'    public static final ResourceKey<HerbDefinition> {_const(herb["id"])} = key("{herb["id"]}"); // {herb["name"]}\n')

    out.append("\n    /** All herbs, in content-file order. */\n    public static final List<ResourceKey<HerbDefinition>> ALL = List.of(\n")
    out.append(",\n".join(f"            {_const(h['id'])}" for h in herbs))
    out.append("\n    );\n")

    by_category: dict[str, list] = {}
    for herb in herbs:
        by_category.setdefault(herb["category"], []).append(herb)
    out.append("\n    /** Herbs grouped by category, used for the journal and the creative tab. */\n")
    out.append("    public static final Map<HerbCategory, List<ResourceKey<HerbDefinition>>> BY_CATEGORY = Map.ofEntries(\n")
    entries = []
    for category, group in by_category.items():
        entries.append(f"            Map.entry(HerbCategory.{category}, List.of({', '.join(_const(h['id']) for h in group)}))")
    out.append(",\n".join(entries))
    out.append("\n    );\n}\n")
    _write(src / "registry/ModHerbs.java", "".join(out))


def _mod_medicines(src: pathlib.Path, medicines: list) -> None:
    out = [HEADER.format(package=f"{PACKAGE}.registry")]
    out.append("""import com.herbalistscraft.medicine.MedicineDefinition;
import com.herbalistscraft.medicine.MedicineKind;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/** Every medicine id in Herbalist's Craft, as registry keys into the synced medicine datapack registry. */
public final class ModMedicines {
    public static final ResourceKey<Registry<MedicineDefinition>> REGISTRY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(""" + f'"{MODID}"' + """, "medicine"));

    private ModMedicines() {}

    public static ResourceKey<MedicineDefinition> key(String path) {
        return ResourceKey.create(REGISTRY, ResourceLocation.fromNamespaceAndPath(""" + f'"{MODID}"' + """, path));
    }
""")
    for med in medicines:
        out.append(f'    public static final ResourceKey<MedicineDefinition> {_const(med["id"])} = key("{med["id"]}"); // {med["name"]}\n')
    out.append("\n    public static final List<ResourceKey<MedicineDefinition>> ALL = List.of(\n")
    out.append(",\n".join(f"            {_const(m['id'])}" for m in medicines))
    out.append("\n    );\n")

    by_kind: dict[str, list] = {}
    for med in medicines:
        by_kind.setdefault(med["kind"], []).append(med)
    out.append("\n    /** Medicines grouped by kind, used by the journal and the trades. */\n")
    out.append("    public static final Map<MedicineKind, List<ResourceKey<MedicineDefinition>>> BY_KIND = Map.ofEntries(\n")
    entries = []
    for kind, group in by_kind.items():
        entries.append(f"            Map.entry(MedicineKind.{kind}, List.of({', '.join(_const(m['id']) for m in group)}))")
    out.append(",\n".join(entries))
    out.append("\n    );\n}\n")
    _write(src / "registry/ModMedicines.java", "".join(out))


def _mod_items(src: pathlib.Path, herbs: list, medicines: list, items: list, experimental: list) -> None:
    """Non-herb items come straight from items.json: kind decides stacking, a small
    special-case table decides which behaviour class an id gets."""
    out = [HEADER.format(package=f"{PACKAGE}.registry")]
    out.append("""import com.herbalistscraft.HerbalistsCraft;
import com.herbalistscraft.herb.HerbForm;
import com.herbalistscraft.herb.HerbItem;
import com.herbalistscraft.herb.HerbSeedItem;
import com.herbalistscraft.knowledge.JournalItem;
import com.herbalistscraft.knowledge.JournalPageItem;
import com.herbalistscraft.knowledge.JournalPageKind;
import com.herbalistscraft.medicine.ExperimentalTeaItem;
import com.herbalistscraft.medicine.ExperimentalTonicItem;
import com.herbalistscraft.medicine.FailedMixtureItem;
import com.herbalistscraft.medicine.SalveItem;
import com.herbalistscraft.medicine.TeaItem;
import com.herbalistscraft.medicine.TonicItem;
import com.herbalistscraft.medicine.WeaponOilItem;
import com.herbalistscraft.tool.FillTarget;
import com.herbalistscraft.tool.FillableContainerItem;
import com.herbalistscraft.tool.PruningShearsItem;
import com.herbalistscraft.tool.SeedPouchItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Every item in Herbalist's Craft. Workstation block items live in {@link ModBlocks}. */
public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(HerbalistsCraft.MODID);

    private ModItems() {}
""")
    for herb in herbs:
        forms = herb.get("forms", ["fresh", "seed"])
        rarity = VANILLA_RARITY.get(herb.get("rarity", "COMMON"), "Rarity.COMMON")
        for form in forms:
            item_id = FORM_ITEM[form].format(herb=herb["id"])
            const = _const(item_id)
            if rarity != "Rarity.COMMON":
                props = f"new Item.Properties().stacksTo({FORM_STACK[form]}).rarity({rarity})"
            else:
                props = f"new Item.Properties().stacksTo({FORM_STACK[form]})"
            if form == "seed":
                ctor = f"new HerbSeedItem(p, ModHerbs.{_const(herb['id'])})"
            else:
                ctor = (f"new HerbItem(p, ModHerbs.{_const(herb['id'])}, "
                        f"HerbForm.{FORM_CONSTANT[form]}, {FORM_QUALITY[form]}f)")
            out.append(f'    public static final DeferredItem<Item> {const} = ITEMS.registerItem("{item_id}",\n'
                       f'            p -> {ctor}, {props});\n')
        out.append("\n")

    for med in medicines:
        const = _const(med["id"])
        rarity = VANILLA_RARITY.get(med.get("rarity", "COMMON"), "Rarity.COMMON")
        props = f"new Item.Properties().stacksTo(8).rarity({rarity})"
        ctor = {
            "TONIC": "new TonicItem",
            "TEA": "new TeaItem",
            "SALVE": "new SalveItem",
            "OIL": "new WeaponOilItem",
        }[med["kind"]]
        out.append(f'    public static final DeferredItem<Item> {const} = ITEMS.registerItem("{med["id"]}",\n'
                   f'            p -> {ctor}(p, ModMedicines.{const}), {props});\n')

    out.append("\n    // ---- materials, containers, tools and knowledge ---------------------------\n")
    for entry in items:
        kind = entry["kind"]
        if kind == "BLOCK":
            continue  # block items are registered next to their blocks in ModBlocks
        item_id = entry["id"]
        const = _const(item_id)
        stacks = ITEM_STACK.get(kind, 64)
        if item_id in ITEM_STACK:
            stacks = ITEM_STACK[item_id]
        properties = f"new Item.Properties().stacksTo({stacks})"
        if item_id in ITEM_RARITY:
            properties = f"new Item.Properties().stacksTo({stacks}).rarity({ITEM_RARITY[item_id]})"
        ctor = ITEM_SPECIAL.get(item_id, "new Item")
        call = ctor if ctor.endswith(")") else f"{ctor}(p)"
        out.append(f'    public static final DeferredItem<Item> {const} = ITEMS.registerItem("{item_id}",\n'
                   f'            p -> {call}, {properties});\n')

    out.append("\n    // ---- outputs of failed or unknown experiments -----------------------------\n")
    for entry in experimental:
        item_id = entry["id"]
        const = _const(item_id)
        ctor = EXPERIMENTAL_SPECIAL[item_id]
        out.append(f'    public static final DeferredItem<Item> {const} = ITEMS.registerItem("{item_id}",\n'
                   f'            p -> {ctor}(p), new Item.Properties().stacksTo(8));\n')
    out.append("}\n")
    _write(src / "registry/ModItems.java", "".join(out))


def _mod_blocks(src: pathlib.Path, herbs: list, items: list) -> None:
    out = [HEADER.format(package=f"{PACKAGE}.registry")]
    out.append("""import com.herbalistscraft.HerbalistsCraft;
import com.herbalistscraft.block.DryingRackBlock;
import com.herbalistscraft.block.HerbalMillBlock;
import com.herbalistscraft.block.HerbalistTableBlock;
import com.herbalistscraft.block.MortarBlock;
import com.herbalistscraft.herb.HerbCropBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Herb crops and the four workstations, plus their block items. */
public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(HerbalistsCraft.MODID);

    private ModBlocks() {}

    private static BlockBehaviour.Properties cropProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .noCollission()
                .randomTicks()
                .instabreak()
                .sound(SoundType.GRASS)
                .noOcclusion()
                .pushReaction(PushReaction.DESTROY);
    }
""")
    for herb in herbs:
        out.append(f'    public static final DeferredBlock<HerbCropBlock> {_const(herb["id"])}_CROP = BLOCKS.registerBlock('
                   f'"{herb["id"]}_crop",\n            p -> new HerbCropBlock(p, ModHerbs.{_const(herb["id"])}), cropProperties());\n')
    out.append("""
    public static final DeferredBlock<MortarBlock> MORTAR_AND_PESTLE = BLOCKS.registerBlock("mortar_and_pestle",
            p -> new MortarBlock(p), BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)
                    .sound(SoundType.STONE).noOcclusion());

    public static final DeferredBlock<HerbalMillBlock> HERBAL_MILL = BLOCKS.registerBlock("herbal_mill",
            p -> new HerbalMillBlock(p), BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD).strength(2.5F, 6.0F).sound(SoundType.WOOD).noOcclusion());

    public static final DeferredBlock<HerbalistTableBlock> HERBALISTS_TABLE = BLOCKS.registerBlock("herbalists_table",
            p -> new HerbalistTableBlock(p), BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD).strength(2.0F, 4.0F).sound(SoundType.WOOD).noOcclusion());

    public static final DeferredBlock<DryingRackBlock> DRYING_RACK = BLOCKS.registerBlock("drying_rack",
            p -> new DryingRackBlock(p), BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD).strength(1.2F, 3.0F).sound(SoundType.WOOD).noOcclusion());

    public static final DeferredItem<BlockItem> MORTAR_AND_PESTLE_ITEM =
            ModItems.ITEMS.registerSimpleBlockItem("mortar_and_pestle", MORTAR_AND_PESTLE);
    public static final DeferredItem<BlockItem> HERBAL_MILL_ITEM =
            ModItems.ITEMS.registerSimpleBlockItem("herbal_mill", HERBAL_MILL);
    public static final DeferredItem<BlockItem> HERBALISTS_TABLE_ITEM =
            ModItems.ITEMS.registerSimpleBlockItem("herbalists_table", HERBALISTS_TABLE);
    public static final DeferredItem<BlockItem> DRYING_RACK_ITEM =
            ModItems.ITEMS.registerSimpleBlockItem("drying_rack", DRYING_RACK);
}
""")
    _write(src / "registry/ModBlocks.java", "".join(out))


def _gui_layout(src: pathlib.Path) -> None:
    out = [HEADER.format(package=f"{PACKAGE}.client")]
    out.append("""/** Screen geometry, generated from the same table the GUI textures are painted from. */
public final class GuiLayout {
    private GuiLayout() {}

""")
    for screen, values in LAYOUT.items():
        out.append(f"    public static final class {screen.capitalize()} {{\n")
        out.append(f"        private {screen.capitalize()}() {{}}\n")
        for key, value in values.items():
            const = key.upper()
            if isinstance(value, list):
                if len(value) == 2:
                    out.append(f"        public static final int {const}_X = {value[0]};\n")
                    out.append(f"        public static final int {const}_Y = {value[1]};\n")
                elif len(value) == 3:
                    out.append(f"        public static final int {const}_X = {value[0]};\n")
                    out.append(f"        public static final int {const}_Y = {value[1]};\n")
                    out.append(f"        public static final int {const}_LENGTH = {value[2]};\n")
                elif len(value) == 4:
                    out.append(f"        public static final int {const}_X0 = {value[0]};\n")
                    out.append(f"        public static final int {const}_Y0 = {value[1]};\n")
                    out.append(f"        public static final int {const}_X1 = {value[2]};\n")
                    out.append(f"        public static final int {const}_Y1 = {value[3]};\n")
            else:
                out.append(f"        public static final int {const} = {value};\n")
        out.append("    }\n\n")
    out.append("}\n")
    _write(src / "client/GuiLayout.java", "".join(out))
