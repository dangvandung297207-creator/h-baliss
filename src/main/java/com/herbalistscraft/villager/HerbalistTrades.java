package com.herbalistscraft.villager;

import com.herbalistscraft.Config;
import com.herbalistscraft.herb.HerbForm;
import com.herbalistscraft.herb.HerbForms;
import com.herbalistscraft.herb.HerbRarity;
import com.herbalistscraft.herb.HerbRegistry;
import com.herbalistscraft.registry.ModItems;
import com.herbalistscraft.registry.ModMedicines;
import com.herbalistscraft.registry.ModVillagers;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.BasicItemListing;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

/**
 * The Herbalist's stock, novice to master. Common herbs and containers start cheap; seeds,
 * material and tools come next; knowledge, rare herbs and tiered medicine are what mastery buys.
 * Biome flavour is added per villager when they first trade (see {@link BiomeTrades}).
 */
public final class HerbalistTrades {
    private HerbalistTrades() {}

    public static void register(VillagerTradesEvent event) {
        if (event.getType() != ModVillagers.HERBALIST.get()) {
            return;
        }
        var trades = event.getTrades();
        trades.get(1).addAll(novice());
        trades.get(2).addAll(apprentice());
        trades.get(3).addAll(journeyman());
        trades.get(4).addAll(expert());
        trades.get(5).addAll(master());
    }

    private static List<VillagerTrades.ItemListing> novice() {
        List<VillagerTrades.ItemListing> list = new ArrayList<>();
        list.add(sell(4, ModItems.GLASS_VIAL.get(), 8, 2));
        list.add(sell(5, ModItems.CLAY_CUP.get(), 6, 2));
        list.add(sell(6, ModItems.PRUNING_SHEARS.get(), 3, 4));
        for (ItemStack herb : herbsOf(HerbRarity.COMMON, 4)) {
            list.add(new BasicItemListing(new ItemStack(Items.EMERALD, 2), herb, 8, 2, 1.0F));
        }
        list.add(new BasicItemListing(new ItemStack(ModItems.BLOODROOT.get(), 6),
                new ItemStack(Items.EMERALD, 1), 12, 3, 1.0F));
        return list;
    }

    private static List<VillagerTrades.ItemListing> apprentice() {
        List<VillagerTrades.ItemListing> list = new ArrayList<>();
        list.add(sell(8, ModItems.SEED_POUCH.get(), 3, 6));
        list.add(sell(3, ModItems.HERBAL_OIL.get(), 6, 4));
        list.add(sell(6, ModItems.PURITY_SALT.get(), 6, 5));
        list.add(sell(10, ModItems.MINOR_HEALING_TONIC.get(), 4, 8));
        list.add(sell(9, ModItems.WARMING_TEA.get(), 4, 8));
        list.add(sell(9, ModItems.COOLING_TEA.get(), 4, 8));
        return list;
    }

    private static List<VillagerTrades.ItemListing> journeyman() {
        List<VillagerTrades.ItemListing> list = new ArrayList<>();
        list.add(sell(12, ModItems.HERBAL_ALCOHOL.get(), 4, 8));
        list.add(sell(14, ModItems.SALVE_BASE.get(), 4, 8));
        list.add(sell(18, ModItems.JOURNAL_PAGE_HERBAL.get(), 2, 12));
        list.add(sell(22, ModItems.STRONG_HEALING_TONIC.get(), 2, 14));
        list.add(sell(24, ModItems.ANTISEPTIC_TONIC.get(), 2, 14));
        return list;
    }

    private static List<VillagerTrades.ItemListing> expert() {
        List<VillagerTrades.ItemListing> list = new ArrayList<>();
        list.add(sell(26, ModItems.FROST_CRYSTAL.get(), 2, 16));
        list.add(sell(26, ModItems.EMBER_ASH.get(), 2, 16));
        list.add(sell(28, ModItems.JOURNAL_PAGE_MEDICINAL.get(), 1, 20));
        list.add(sell(30, ModItems.RECOVERY_TEA.get(), 2, 18));
        for (ItemStack herb : herbsOf(HerbRarity.RARE, 2)) {
            list.add(new BasicItemListing(new ItemStack(Items.EMERALD, 16), herb, 4, 20, 1.0F));
        }
        return list;
    }

    private static List<VillagerTrades.ItemListing> master() {
        List<VillagerTrades.ItemListing> list = new ArrayList<>();
        list.add(sell(38, ModItems.LIFE_ESSENCE.get(), 1, 30));
        list.add(sell(40, ModItems.MOONWATER.get(), 2, 30));
        list.add(sell(44, ModItems.JOURNAL_PAGE_ANCIENT.get(), 1, 40));
        list.add(sell(48, ModItems.TOXIC_OIL.get(), 1, 30));
        for (ItemStack herb : herbsOf(HerbRarity.VERY_RARE, 2)) {
            list.add(new BasicItemListing(new ItemStack(Items.EMERALD, 24), herb, 3, 30, 1.0F));
        }
        return list;
    }

    /** Buying one of ours; the item costs emeralds. */
    private static VillagerTrades.ItemListing sell(int emeralds, net.minecraft.world.level.ItemLike item, int maxUses,
                                                   int xp) {
        return new BasicItemListing(new ItemStack(Items.EMERALD, emeralds), new ItemStack(item), maxUses, xp, 1.0F);
    }

    /** Fresh herbs of one rarity, for the levels that sell them. Falls back to any common herb. */
    private static List<ItemStack> herbsOf(HerbRarity rarity, int limit) {
        List<ItemStack> herbs = new ArrayList<>();
        var server = net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return herbs;
        }
        var registry = HerbRegistry.registry(server.registryAccess());
        for (var holder : ModItems.ITEMS.getEntries()) {
            if (herbs.size() >= limit || !(holder.get() instanceof com.herbalistscraft.herb.HerbItem herb)) {
                continue;
            }
            if (herb.form() != HerbForm.FRESH) {
                continue;
            }
            var definition = registry.get(herb.herb());
            if (definition != null && definition.rarity() == rarity) {
                herbs.add(new ItemStack(holder.get()));
            }
        }
        return herbs;
    }

    /** Seeds and cuttings the villager will buy from the player, for the level they are at. */
    public static List<VillagerTrades.ItemListing> buys(int level) {
        List<VillagerTrades.ItemListing> list = new ArrayList<>();
        for (ItemStack seed : seeds(level == 1 ? 3 : 5)) {
            list.add(new BasicItemListing(seed, new ItemStack(Items.EMERALD, 1), 12, 2, 1.0F));
        }
        if (level >= 3) {
            list.add(new BasicItemListing(new ItemStack(ModItems.ANTISEPTIC_TONIC.get()),
                    new ItemStack(Items.EMERALD, 12), 4, 8, 1.0F));
        }
        return list;
    }

    private static List<ItemStack> seeds(int limit) {
        List<ItemStack> seeds = new ArrayList<>();
        for (var entry : HerbForms.all(HerbForm.SEED).entrySet()) {
            if (seeds.size() >= limit) {
                break;
            }
            seeds.add(new ItemStack(entry.getValue()));
        }
        return seeds;
    }

    /** Medicine ids that exist, for the journal's trade hints. */
    public static List<String> knownMedicineTrades(ServerLevel level) {
        List<String> ids = new ArrayList<>();
        for (var key : ModMedicines.ALL) {
            ids.add(key.location().getPath());
        }
        return ids;
    }

}
