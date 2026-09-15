package com.herbalistscraft.event;

import com.herbalistscraft.Config;
import com.herbalistscraft.HerbalistsCraft;
import com.herbalistscraft.knowledge.Discovery;
import com.herbalistscraft.medicine.MedicineApplier;
import com.herbalistscraft.medicine.MedicineDefinition;
import com.herbalistscraft.medicine.ToxicityManager;
import com.herbalistscraft.registry.ModVillagers;
import com.herbalistscraft.villager.BiomeTrades;
import com.herbalistscraft.villager.HerbalistTrades;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.player.TradeWithVillagerEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

/** Gameplay events that belong to the server: trades, toxin decay and villager stock. */
@EventBusSubscriber(modid = HerbalistsCraft.MODID)
public final class CommonEvents {
    private CommonEvents() {}

    private static final int TOXIN_TICK_INTERVAL = 1200;

    @SubscribeEvent
    public static void onVillagerTrades(VillagerTradesEvent event) {
        HerbalistTrades.register(event);
    }

    @SubscribeEvent
    public static void onTrade(TradeWithVillagerEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        Discovery.onTrade(player);
        if (event.getAbstractVillager() instanceof Villager villager
                && villager.getVillagerData().getProfession() == ModVillagers.HERBALIST.get()
                && player.level() instanceof ServerLevel level) {
            BiomeTrades.seed(villager, level);
        }
        ItemStack sold = event.getMerchantOffer().getResult();
        ResourceLocation id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(sold.getItem());
        MedicineDefinition definition = MedicineApplier.lookup(player.server.registryAccess(), id).orElse(null);
        if (definition != null) {
            Discovery.discoverMedicine(player, id, sold.getHoverName(), definition.tier());
        }
    }

    /** Toxin decay and the mild afternoon reminder that poisons add up. */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || player.tickCount % TOXIN_TICK_INTERVAL != 0) {
            return;
        }
        int decay = Math.max(0, Config.toxinDecayPerMinute());
        if (decay > 0) {
            ToxicityManager.decay(player, decay);
        }
    }
}
