package com.herbalistscraft.client;

import com.herbalistscraft.block.entity.DryingRackBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

/**
 * Draws the herb lying on the drying rack, and shrinks it as the moisture leaves: the player can
 * see at a glance whether a herb is still fresh, half dried or ready to take.
 */
public class DryingRackRenderer implements BlockEntityRenderer<DryingRackBlockEntity> {
    private static final float SHELF_Y = 0.42F;

    private final ItemRenderer itemRenderer;

    public DryingRackRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(DryingRackBlockEntity rack, float partialTick, PoseStack pose, MultiBufferSource buffers,
                       int packedLight, int packedOverlay) {
        ItemStack stored = rack.stored();
        if (stored.isEmpty()) {
            return;
        }
        float progress = rack.progressFraction();
        float scale = 0.5F - 0.16F * progress;

        pose.pushPose();
        pose.translate(0.5D, SHELF_Y + 0.06D, 0.5D);
        pose.mulPose(new Quaternionf().rotateX((float) Math.toRadians(90.0D)));
        pose.mulPose(new Quaternionf().rotateZ((float) Math.toRadians(15.0D * (progress - 0.5F))));
        pose.scale(scale, scale, scale);

        int light = net.minecraft.client.renderer.LightTexture.pack(12, 15);
        itemRenderer.renderStatic(stored, ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, pose, buffers,
                rack.getLevel(), rack.getBlockPos().hashCode());
        pose.popPose();
    }
}
