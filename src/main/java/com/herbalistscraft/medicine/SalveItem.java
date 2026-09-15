package com.herbalistscraft.medicine;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/** A salve worked into the skin: a short animation, then protection or healing that lasts. */
public class SalveItem extends Item {
    private final ResourceKey<MedicineDefinition> medicine;

    public SalveItem(Properties properties, ResourceKey<MedicineDefinition> medicine) {
        super(properties);
        this.medicine = medicine;
    }

    public ResourceKey<MedicineDefinition> medicine() {
        return medicine;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (MedicineUse.blockedByCooldown(player, stack)) {
            return InteractionResultHolder.fail(stack);
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            MedicineApplier.apply(serverPlayer, medicine.location(), false, false);
            com.herbalistscraft.knowledge.Discovery.discoverMedicine(serverPlayer, medicine.location(),
                    Component.translatable("item.herbalistscraft." + medicine.location().getPath()),
                    MedicineUse.tier(serverPlayer, medicine));
            serverPlayer.getCooldowns().addCooldown(this, MedicineUse.cooldownTicks(serverPlayer, medicine));
            serverPlayer.displayClientMessage(Component.translatable("message.herbalistscraft.salve.applied")
                    .withStyle(ChatFormatting.GREEN), true);
        }
        level.playSound(null, entity.blockPosition(), com.herbalistscraft.registry.ModSounds.SALVE.get(),
                SoundSource.PLAYERS, 0.8F, 1.0F);
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            serverLevel.sendParticles(com.herbalistscraft.registry.ModParticles.DRIED_LEAF.get(),
                    entity.getX(), entity.getY() + 1.0D, entity.getZ(), 5, 0.3D, 0.4D, 0.3D, 0.0D);
        }
        return MedicineUse.onConsumed(stack, level, entity, false);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 24;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BRUSH;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        MedicineUse.appendTooltip(stack, context, tooltip, medicine);
    }
}
