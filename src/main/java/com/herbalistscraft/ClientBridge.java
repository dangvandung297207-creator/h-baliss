package com.herbalistscraft;

import net.minecraft.world.entity.player.Player;

/**
 * Keeps client-only lookups out of common classes. The client half is installed by
 * {@code client.ClientSetup}; on a dedicated server the supplier returns null and every
 * caller has to cope with that (tooltips simply show the undiscovered variant).
 */
public final class ClientBridge {
    private static java.util.function.Supplier<Player> localPlayer = () -> null;
    private static java.util.function.Consumer<Boolean> journalOpener = seasonal -> { };

    private ClientBridge() {}

    public static void installLocalPlayer(java.util.function.Supplier<Player> supplier) {
        localPlayer = supplier;
    }

    public static void installJournalOpener(java.util.function.Consumer<Boolean> opener) {
        journalOpener = opener;
    }

    /** Opens the journal screen on the client; a no-op anywhere else. */
    public static void openJournal(boolean seasonalPages) {
        journalOpener.accept(seasonalPages);
    }

    public static Player localPlayer() {
        try {
            return localPlayer.get();
        } catch (Throwable ignored) {
            return null;
        }
    }
}
