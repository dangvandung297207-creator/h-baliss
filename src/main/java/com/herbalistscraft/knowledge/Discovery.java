package com.herbalistscraft.knowledge;

import com.herbalistscraft.HerbalistsCraft;
import com.herbalistscraft.herb.HerbDefinition;
import com.herbalistscraft.herb.HerbProperty;
import com.herbalistscraft.herb.HerbRarity;
import com.herbalistscraft.herb.HerbRegistry;
import com.herbalistscraft.herb.HerbSeason;
import com.herbalistscraft.registry.ModAttachments;
import com.herbalistscraft.registry.ModHerbs;
import com.herbalistscraft.registry.ModSounds;
import com.herbalistscraft.season.Seasons;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * The single place where herbs, properties, recipes and medicines become known.
 * Every discovery is permanent, logged in chat, sounded, and often unlocks an advancement.
 */
public final class Discovery {
    private Discovery() {}

    public static Knowledge knowledge(Player player) {
        return player.getData(ModAttachments.KNOWLEDGE);
    }

    // ---- herbs --------------------------------------------------------------

    public static void onHarvest(ServerLevel level, Player player, ResourceKey<HerbDefinition> key,
                                 HerbDefinition definition, List<ItemStack> drops) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        discoverHerb(serverPlayer, key, definition, true);
        discoverBiome(serverPlayer, level);
        discoverSeason(serverPlayer, level);
    }

    /** Learned by working a herb rather than by finding it: grinding, milling or steeping. */
    public static void onGround(ServerPlayer player, ItemStack stack) {
        var item = com.herbalistscraft.herb.HerbForms.asHerb(stack);
        if (item == null) {
            return;
        }
        var definition = HerbRegistry.get(player.server.registryAccess(), item.herb()).orElse(null);
        if (definition != null) {
            discoverHerb(player, item.herb(), definition, false);
        }
    }

    public static void discoverHerb(ServerPlayer player, ResourceKey<HerbDefinition> key, HerbDefinition definition,
                                    boolean firstHarvest) {
        ResourceLocation id = key.location();
        Knowledge knowledge = knowledge(player);
        boolean isNew = knowledge.learnHerb(id);
        boolean propertyLearned = learnProperties(knowledge, definition);
        if (!isNew && !propertyLearned) {
            return;
        }
        sync(player, knowledge);
        if (isNew) {
            player.displayClientMessage(Component.translatable("message.herbalistscraft.herb.discovered",
                    itemName(id)).withStyle(ChatFormatting.GREEN), false);
            announceProperties(player, definition, knowledge);
            player.level().playSound(null, player.blockPosition(), ModSounds.DISCOVERY.get(), SoundSource.PLAYERS,
                    0.8F, 1.2F);
            if (player.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, player.getX(), player.getY() + 1.2D,
                        player.getZ(), 10, 0.4D, 0.4D, 0.4D, 0.02D);
            }
            AdvancementGrants.grant(player, AdvancementGrants.FIRST_LEAF);
            if (knowledge.herbCount() >= 20) {
                AdvancementGrants.grant(player, AdvancementGrants.BOTANICAL_SCHOLAR);
            }
            if (allCommonKnown(player, knowledge)) {
                AdvancementGrants.grant(player, AdvancementGrants.MASTER_HERBALIST);
            }
            player.displayClientMessage(Component.translatable("message.herbalistscraft.knowledge.journal_hint")
                    .withStyle(ChatFormatting.GRAY), false);
        }
        if (firstHarvest) {
            player.displayClientMessage(Component.translatable("message.herbalistscraft.herb.discovered_fresh",
                    itemName(id)).withStyle(ChatFormatting.DARK_GREEN), true);
        }
    }

    private static boolean learnProperties(Knowledge knowledge, HerbDefinition definition) {
        boolean learned = false;
        for (Map.Entry<HerbProperty, Float> entry : definition.properties().entrySet()) {
            if (entry.getValue() > 0.0F && knowledge.learnProperty(entry.getKey())) {
                learned = true;
            }
        }
        return learned;
    }

    private static void announceProperties(ServerPlayer player, HerbDefinition definition, Knowledge knowledge) {
        for (HerbProperty property : definition.properties().keySet()) {
            player.displayClientMessage(Component.translatable("message.herbalistscraft.property.discovered",
                    property.displayName()).withStyle(ChatFormatting.AQUA), false);
        }
    }

    private static boolean allCommonKnown(ServerPlayer player, Knowledge knowledge) {
        return HerbRegistry.registry(player.server.registryAccess()).stream()
                .filter(definition -> definition.rarity() == HerbRarity.COMMON)
                .allMatch(definition -> knowledge.knowsHerb(registryId(player, definition)));
    }

    private static ResourceLocation registryId(ServerPlayer player, HerbDefinition definition) {
        return HerbRegistry.registry(player.server.registryAccess()).getKey(definition);
    }

    // ---- recipes, medicines, trades, seasons, biomes -------------------------

    public static void discoverRecipe(ServerPlayer player, ResourceLocation recipe, String displayName) {
        Knowledge knowledge = knowledge(player);
        if (!knowledge.learnRecipe(recipe)) {
            return;
        }
        sync(player, knowledge);
        player.displayClientMessage(Component.translatable("message.herbalistscraft.recipe.discovered",
                Component.literal(displayName)).withStyle(ChatFormatting.GOLD), false);
        player.level().playSound(null, player.blockPosition(), ModSounds.DISCOVERY.get(), SoundSource.PLAYERS,
                0.6F, 1.4F);
        if (knowledge.recipeCount() >= 25) {
            AdvancementGrants.grant(player, AdvancementGrants.DEEP_KNOWLEDGE);
        }
    }

    public static void discoverMedicine(ServerPlayer player, ResourceLocation medicine, Component name, int tier) {
        Knowledge knowledge = knowledge(player);
        if (!knowledge.learnMedicine(medicine)) {
            return;
        }
        sync(player, knowledge);
        player.displayClientMessage(Component.translatable("message.herbalistscraft.medicine.discovered", name)
                .withStyle(ChatFormatting.GOLD), false);
        if (tier >= 4) {
            AdvancementGrants.grant(player, AdvancementGrants.ANCIENT_KNOWLEDGE);
        }
    }

    public static void onTrade(ServerPlayer player) {
        AdvancementGrants.grant(player, AdvancementGrants.TRADING_HERBS);
    }

    public static void onCoating(ServerPlayer player) {
        AdvancementGrants.grant(player, AdvancementGrants.POISONERS_ART);
    }

    public static void onCraft(ServerPlayer player) {
        AdvancementGrants.grant(player, AdvancementGrants.FIELD_MEDICINE);
    }

    public static void onMill(ServerPlayer player) {
        AdvancementGrants.grant(player, AdvancementGrants.MILLWRIGHT);
    }

    public static void onDry(ServerPlayer player) {
        Knowledge knowledge = knowledge(player);
        knowledge.seeSeason(Seasons.current(player.level()));
        sync(player, knowledge);
        AdvancementGrants.grant(player, AdvancementGrants.DRYING_TIME);
    }

    public static void onToxinPurged(ServerPlayer player) {
        AdvancementGrants.grant(player, AdvancementGrants.TOXIN_SURVIVOR);
    }

    public static void discoverBiome(ServerPlayer player, ServerLevel level) {
        for (String group : com.herbalistscraft.world.BiomeGroups.groupsFor(level, player.blockPosition())) {
            Knowledge knowledge = knowledge(player);
            if (knowledge.visitBiome(group)) {
                sync(player, knowledge);
                if (knowledge.biomeCount() >= 8) {
                    AdvancementGrants.grant(player, AdvancementGrants.WANDERER);
                }
            }
        }
    }

    public static void discoverSeason(ServerPlayer player, ServerLevel level) {
        HerbSeason season = Seasons.current(level);
        Knowledge knowledge = knowledge(player);
        if (knowledge.seeSeason(season) && knowledge.seasonCount() >= 4) {
            sync(player, knowledge);
            AdvancementGrants.grant(player, AdvancementGrants.FOUR_SEASONS);
        }
    }

    // ---- helpers ------------------------------------------------------------

    public static void sync(ServerPlayer player, Knowledge knowledge) {
        player.setData(ModAttachments.KNOWLEDGE, knowledge);
        player.syncData(ModAttachments.KNOWLEDGE);
    }

    public static Component itemName(ResourceLocation herb) {
        return Component.translatable("item." + HerbalistsCraft.MODID + "." + herb.getPath());
    }

    public static ResourceKey<HerbDefinition> key(ResourceLocation herb) {
        return ResourceKey.create(ModHerbs.REGISTRY, herb);
    }
}
