package com.herbalistscraft.client;

import com.herbalistscraft.HerbalistsCraft;
import com.herbalistscraft.menu.HerbalMillMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * The mill's screen: two inputs on the left, the output on the right, and a turning wheel that
 * fills in as the batch grinds. Slots, arrow and wheel all sit where {@link GuiLayout} says, so
 * the painted texture and the click targets cannot drift apart.
 */
public class HerbalMillScreen extends AbstractContainerScreen<HerbalMillMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(HerbalistsCraft.MODID, "textures/gui/mill.png");
    private static final int WHEEL_FRAMES = 4;

    public HerbalMillScreen(HerbalMillMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = GuiLayout.Mill.WIDTH;
        this.imageHeight = GuiLayout.Mill.HEIGHT;
        this.inventoryLabelX = GuiLayout.Mill.PLAYER_INV_X;
        this.inventoryLabelY = GuiLayout.Mill.PLAYER_INV_Y - 11;
        this.titleLabelX = GuiLayout.Mill.TITLE_X;
        this.titleLabelY = GuiLayout.Mill.TITLE_Y;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        int progress = menu.progress();
        int max = Math.max(1, menu.maxProgress());
        int filled = Math.max(0, Math.min(GuiLayout.Mill.ARROW_LENGTH,
                GuiLayout.Mill.ARROW_LENGTH * progress / max));
        if (filled > 0) {
            graphics.blit(TEXTURE, leftPos + GuiLayout.Mill.ARROW_X, topPos + GuiLayout.Mill.ARROW_Y,
                    GuiLayout.Mill.ARROW_X, GuiLayout.Mill.ARROW_Y + 12, filled, 12);
        }
        int frame = (progress * WHEEL_FRAMES) / max;
        graphics.blit(TEXTURE, leftPos + GuiLayout.Mill.WHEEL_X, topPos + GuiLayout.Mill.WHEEL_Y,
                GuiLayout.Mill.WHEEL_X, 40 + frame * 18, 18, 18);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, titleLabelX, titleLabelY, 0x3F3F3F, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0x3F3F3F, false);
        int max = Math.max(1, menu.maxProgress());
        if (menu.progress() > 0) {
            int percent = Math.min(100, menu.progress() * 100 / max);
            graphics.drawString(font, percent + "%", GuiLayout.Mill.ARROW_X + 4, GuiLayout.Mill.ARROW_Y - 10,
                    0x4F6F4F, false);
        }
    }
}
