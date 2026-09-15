package com.herbalistscraft.client;

import com.herbalistscraft.Config;
import com.herbalistscraft.HerbalistsCraft;
import com.herbalistscraft.herb.HerbDefinition;
import com.herbalistscraft.herb.HerbForms;
import com.herbalistscraft.herb.HerbItem;
import com.herbalistscraft.herb.HerbProperty;
import com.herbalistscraft.herb.HerbSeason;
import com.herbalistscraft.knowledge.Discovery;
import com.herbalistscraft.knowledge.Knowledge;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

/**
 * Herb tooltips: category, the properties the player has actually learned, where and when the
 * herb grows, how toxic it is and what it is used for. Anything not yet learned is shown as
 * "??? / Unknown Property" rather than being hidden, so the player can see there is more to find.
 */
@EventBusSubscriber(modid = HerbalistsCraft.MODID, value = Dist.CLIENT)
public final class HerbTooltips {
    private HerbTooltips() {}

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        HerbItem herb = HerbForms.asHerb(event.getItemStack());
        if (herb == null || event.getEntity() == null) {
            return;
        }
        Player player = event.getEntity();
        Knowledge knowledge = Discovery.knowledge(player);
        List<Component> tooltip = event.getToolTip();
        if (event.getContext().registries() == null) {
            return;
        }
        HerbDefinition definition = com.herbalistscraft.herb.HerbRegistry.get(
                event.getContext().registries(), herb.herb()).orElse(null);
        if (definition == null) {
            return;
        }
        boolean identified = knowledge.knowsHerb(herb.herb().location());
        if (!identified) {
            tooltip.add(Component.translatable("gui.herbalistscraft.journal.unknown_herb")
                    .withStyle(ChatFormatting.DARK_GRAY));
            tooltip.add(Component.translatable("tooltip.herbalistscraft.herb.taste_hint")
                    .withStyle(ChatFormatting.DARK_GRAY));
            return;
        }
        tooltip.add(Component.translatable("tooltip.herbalistscraft.herb.lore",
                Component.translatable(definition.loreKey(herb.herb().location().getPath())))
                .withStyle(ChatFormatting.DARK_GRAY));
        if (Config.showPropertyIcons()) {
            for (Map.Entry<HerbProperty, Float> entry : definition.properties().entrySet()) {
                if (entry.getValue() <= 0.0F) {
                    continue;
                }
                MutableComponent line = knowledge.knowsProperty(entry.getKey())
                        ? entry.getKey().displayName().copy()
                        : Component.translatable("tooltip.herbalistscraft.property.unknown");
                tooltip.add(Component.literal(" • ").append(line).withStyle(ChatFormatting.AQUA));
            }
        }
        StringBuilder seasons = new StringBuilder();
        for (HerbSeason season : definition.seasons()) {
            if (seasons.length() > 0) {
                seasons.append(", ");
            }
            seasons.append(season.displayName().getString());
        }
        if (seasons.length() > 0) {
            tooltip.add(Component.translatable("tooltip.herbalistscraft.herb.seasons", seasons.toString())
                    .withStyle(ChatFormatting.GRAY));
        }
        if (!definition.biomeGroups().isEmpty()) {
            tooltip.add(Component.translatable("tooltip.herbalistscraft.herb.biomes",
                    String.join(", ", definition.biomeGroups())).withStyle(ChatFormatting.GRAY));
        }
        if (!definition.uses().isEmpty()) {
            tooltip.add(Component.translatable("tooltip.herbalistscraft.herb.uses_line",
                    String.join(", ", definition.uses())).withStyle(ChatFormatting.DARK_GRAY));
        }
        tooltip.add(Component.translatable("gui.herbalistscraft.journal.herbs.known", knowledge.herbCount(), 49)
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}
