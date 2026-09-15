package com.herbalistscraft.command;

import com.google.common.collect.ImmutableList;
import com.herbalistscraft.HerbalistsCraft;
import com.herbalistscraft.herb.HerbRegistry;
import com.herbalistscraft.knowledge.Discovery;
import com.herbalistscraft.knowledge.Knowledge;
import com.herbalistscraft.medicine.ToxicityManager;
import com.herbalistscraft.season.Seasons;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/** {@code /herbalist knowledge|season|toxin} - a window into the player's own progress. */
@EventBusSubscriber(modid = HerbalistsCraft.MODID)
public final class HerbalistCommand {
    private HerbalistCommand() {}

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("herbalist")
                .then(Commands.literal("knowledge").executes(HerbalistCommand::knowledge))
                .then(Commands.literal("season").executes(HerbalistCommand::season))
                .then(Commands.literal("toxin").executes(HerbalistCommand::toxin));
        event.getDispatcher().register(root);
    }

    private static int knowledge(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            return 0;
        }
        Knowledge knowledge = Discovery.knowledge(player);
        context.getSource().sendSuccess(() -> Component.translatable("command.herbalistscraft.knowledge",
                knowledge.herbCount(), knowledge.propertyCount(), knowledge.recipeCount(),
                knowledge.medicineCount()).withStyle(ChatFormatting.GREEN), false);
        return knowledge.herbCount();
    }

    private static int season(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            return 0;
        }
        context.getSource().sendSuccess(() -> Component.translatable("command.herbalistscraft.season",
                Seasons.current(player.level()).displayName()).withStyle(ChatFormatting.AQUA), false);
        return 1;
    }

    private static int toxin(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            return 0;
        }
        int load = ToxicityManager.load(player);
        context.getSource().sendSuccess(() -> Component.translatable("command.herbalistscraft.toxin", load,
                ToxicityManager.band(player).displayName()).withStyle(ChatFormatting.RED), false);
        return load;
    }

    /** Herbs the player has yet to find, for the guide pages. */
    public static List<String> missing(ServerPlayer player) {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        var registry = HerbRegistry.registry(player.server.registryAccess());
        for (var entry : registry.entrySet()) {
            if (!Discovery.knowledge(player).knowsHerb(entry.getKey().location())) {
                builder.add(entry.getKey().location().getPath());
            }
        }
        return builder.build();
    }
}
