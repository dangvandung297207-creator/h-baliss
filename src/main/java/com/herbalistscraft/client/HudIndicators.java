package com.herbalistscraft.client;

import com.herbalistscraft.Config;
import com.herbalistscraft.herb.HerbSeason;
import com.herbalistscraft.medicine.ToxicityManager;
import com.herbalistscraft.registry.ModAttachments;
import com.herbalistscraft.season.Seasons;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/**
 * A small, quiet corner indicator: accumulated toxin as a rising bar, plus the current season
 * once the player has learned about seasons. Nothing is shown before it has been earned.
 */
public final class HudIndicators {
    private HudIndicators() {}

    public static void renderToxin(GuiGraphics graphics, DeltaTracker delta) {
        if (!Config.showHudIndicators()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null || minecraft.options.hideGui || minecraft.screen != null) {
            return;
        }
        int load = player.getData(ModAttachments.TOXIN);
        if (load <= 0 && !knowledgeShowsSeason(player)) {
            return;
        }
        int x = 6;
        int y = graphics.guiHeight() - 46;
        if (load > 0) {
            int width = 40;
            int filled = Math.min(width, Math.round(width * Math.min(1.0F, load / 120.0F)));
            graphics.fill(x, y, x + width, y + 4, 0x88101010);
            graphics.fill(x, y, x + filled, y + 4, 0xCC9B2C2C);
            graphics.drawString(minecraft.font, Component.translatable("gui.herbalistscraft.hud.toxin",
                    load, ToxicityManager.band(player).displayName().getString()), x, y - 10, 0xFFB05050, true);
            y += 14;
        }
        if (knowledgeShowsSeason(player)) {
            HerbSeason season = Seasons.current(player.level());
            graphics.drawString(minecraft.font, Component.translatable("gui.herbalistscraft.hud.season",
                    season.displayName().getString()), x, y, 0xFF7FD07F, true);
        }
    }

    private static boolean knowledgeShowsSeason(Player player) {
        return com.herbalistscraft.knowledge.Discovery.knowledge(player).seasonCount() > 0;
    }
}
