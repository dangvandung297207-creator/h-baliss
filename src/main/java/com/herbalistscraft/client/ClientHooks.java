package com.herbalistscraft.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

/** The handful of client actions common code needs to trigger; called from {@code ClientBridge}. */
public final class ClientHooks {
    private ClientHooks() {}

    /** Opens the journal, optionally on its seasonal pages. */
    public static void openJournal(boolean seasonalPages) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) {
            return;
        }
        minecraft.setScreen(new JournalScreen(seasonalPages ? JournalScreen.Tab.SEASONAL : JournalScreen.Tab.HERBS));
    }
}
