package com.herbalistscraft.villager;

import com.herbalistscraft.Config;
import com.herbalistscraft.herb.HerbForms;
import com.herbalistscraft.herb.HerbRegistry;
import com.herbalistscraft.world.BiomeGroups;
import java.util.List;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.neoforge.common.BasicItemListing;

/**
 * Stock that follows the villager's own region. The first time an herbalist trades, the herbs
 * that actually grow around them are added to their offers - a swamp herbalist sells marsh
 * remedies, a taiga one sells what keeps in the cold.
 */
public final class BiomeTrades {
    private BiomeTrades() {}

    public static boolean isSeeded(Villager villager) {
        return villager.getPersistentData().getBoolean(SEEDED_TAG);
    }

    private static final String SEEDED_TAG = "herbalistscraft:biome_trades";

    public static void seed(Villager villager, ServerLevel level) {
        if (!Config.biomeTrades() || isSeeded(villager)) {
            return;
        }
        List<String> groups = BiomeGroups.groupsFor(level, villager.blockPosition());
        List<ResourceKey<com.herbalistscraft.herb.HerbDefinition>> local = level.registryAccess()
                .registryOrThrow(com.herbalistscraft.registry.ModHerbs.REGISTRY)
                .holders()
                .filter(holder -> matches(holder.value(), groups))
                .map(holder -> holder.key())
                .limit(3)
                .toList();
        for (var key : local) {
            ItemStack herb = new ItemStack(HerbForms.item(key, com.herbalistscraft.herb.HerbForm.FRESH));
            ItemStack seed = new ItemStack(HerbForms.seed(key));
            if (!herb.isEmpty()) {
                villager.getOffers().add(new BasicItemListing(new ItemStack(Items.EMERALD, 6), herb, 6, 6, 1.0F)
                        .getOffer(villager, villager.getRandom()));
            }
            if (!seed.isEmpty()) {
                villager.getOffers().add(new BasicItemListing(new ItemStack(Items.EMERALD, 3), seed, 6, 4, 1.0F)
                        .getOffer(villager, villager.getRandom()));
            }
        }
        villager.getPersistentData().putBoolean(SEEDED_TAG, true);
    }

    private static boolean matches(com.herbalistscraft.herb.HerbDefinition definition, List<String> groups) {
        for (String group : groups) {
            if (definition.biomeGroups().contains(group)) {
                return true;
            }
        }
        return false;
    }
}
