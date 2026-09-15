package com.herbalistscraft.registry;

import com.herbalistscraft.herb.HerbForms;
import java.util.List;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

/**
 * The creative tab, in the order the design asks for: herbs, seeds, processing, medicines,
 * teas, salves, oils, tools, knowledge, workstations.
 */
public final class CreativeTabContent {
    private CreativeTabContent() {}

    public static void fill(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        // herbs - fresh, then dried, powder and extract for the ones that have them
        for (var entry : HerbForms.all(com.herbalistscraft.herb.HerbForm.FRESH).entrySet()) {
            output.accept(entry.getValue());
        }
        for (var entry : HerbForms.all(com.herbalistscraft.herb.HerbForm.DRIED).entrySet()) {
            output.accept(entry.getValue());
        }
        for (var entry : HerbForms.all(com.herbalistscraft.herb.HerbForm.POWDER).entrySet()) {
            output.accept(entry.getValue());
        }
        for (var entry : HerbForms.all(com.herbalistscraft.herb.HerbForm.EXTRACT).entrySet()) {
            output.accept(entry.getValue());
        }
        // seeds
        for (var entry : HerbForms.all(com.herbalistscraft.herb.HerbForm.SEED).entrySet()) {
            output.accept(entry.getValue());
        }
        // processing materials and bases
        accept(output, ModItems.GLASS_VIAL, ModItems.CLAY_CUP, ModItems.WATER_VIAL, ModItems.SPRING_WATER_CUP,
                ModItems.HERBAL_OIL, ModItems.HERBAL_ALCOHOL, ModItems.SALVE_BASE, ModItems.MOONWATER,
                ModItems.PURITY_SALT, ModItems.EMBER_ASH, ModItems.FROST_CRYSTAL, ModItems.LIFE_ESSENCE,
                ModItems.SHADOW_ICHOR);
        // medicines, by kind
        for (var holder : ModItems.ITEMS.getEntries()) {
            if (holder.get() instanceof com.herbalistscraft.medicine.TonicItem) {
                output.accept(holder.get());
            }
        }
        for (var holder : ModItems.ITEMS.getEntries()) {
            if (holder.get() instanceof com.herbalistscraft.medicine.TeaItem) {
                output.accept(holder.get());
            }
        }
        for (var holder : ModItems.ITEMS.getEntries()) {
            if (holder.get() instanceof com.herbalistscraft.medicine.SalveItem) {
                output.accept(holder.get());
            }
        }
        for (var holder : ModItems.ITEMS.getEntries()) {
            if (holder.get() instanceof com.herbalistscraft.medicine.WeaponOilItem) {
                output.accept(holder.get());
            }
        }
        accept(output, ModItems.EXPERIMENTAL_TONIC, ModItems.EXPERIMENTAL_TEA, ModItems.FAILED_MIXTURE);
        // tools and knowledge
        accept(output, ModItems.PRUNING_SHEARS, ModItems.SEED_POUCH, ModItems.HERBALISTS_JOURNAL,
                ModItems.JOURNAL_PAGE_HERBAL, ModItems.JOURNAL_PAGE_MEDICINAL, ModItems.JOURNAL_PAGE_TOXIC,
                ModItems.JOURNAL_PAGE_ANCIENT);
        // workstations
        accept(output, ModBlocks.MORTAR_AND_PESTLE_ITEM, ModBlocks.HERBAL_MILL_ITEM, ModBlocks.HERBALISTS_TABLE_ITEM,
                ModBlocks.DRYING_RACK_ITEM);
    }

    private static void accept(CreativeModeTab.Output output, net.minecraft.world.level.ItemLike... items) {
        for (net.minecraft.world.level.ItemLike item : items) {
            output.accept(item);
        }
    }

    /** Items shown on the tab's search page, in addition to the registered ones. */
    public static List<ItemStack> showcase() {
        return List.of(new ItemStack(ModItems.HERBALISTS_JOURNAL.get()));
    }
}
