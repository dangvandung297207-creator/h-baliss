package com.herbalistscraft.client;

import com.herbalistscraft.HerbalistsCraft;
import com.herbalistscraft.knowledge.Discovery;
import com.herbalistscraft.knowledge.Knowledge;
import com.herbalistscraft.menu.HerbalistTableMenu;
import com.herbalistscraft.mixing.MixingResult;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * The Herbalist's Table: five slots on the left and a knowledge panel on the right that says
 * what the brew will become, how strong it is and whether the recipe is known. The panel is the
 * point of the table - the slots are only the machinery behind it.
 */
public class HerbalistTableScreen extends AbstractContainerScreen<HerbalistTableMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(HerbalistsCraft.MODID, "textures/gui/table.png");
    private static final int TEXT_COLOUR = 0x303030;

    public HerbalistTableScreen(HerbalistTableMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = GuiLayout.Table.WIDTH;
        this.imageHeight = GuiLayout.Table.HEIGHT;
        this.inventoryLabelX = GuiLayout.Table.PLAYER_INV_X;
        this.inventoryLabelY = GuiLayout.Table.PLAYER_INV_Y - 11;
        this.titleLabelX = GuiLayout.Table.TITLE_X;
        this.titleLabelY = GuiLayout.Table.TITLE_Y;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        int brew = menu.brewTime();
        int filled = Math.max(0, Math.min(40, 40 * menu.progress() / brew));
        if (filled > 0) {
            graphics.blit(TEXTURE, leftPos + GuiLayout.Table.RESULT_X + 22, topPos + GuiLayout.Table.RESULT_Y + 4,
                    GuiLayout.Table.RESULT_X + 22, 20, filled, 8);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, titleLabelX, titleLabelY, TEXT_COLOUR, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, TEXT_COLOUR, false);
        graphics.drawString(font, Component.translatable("gui.herbalistscraft.table.knowledge"),
                GuiLayout.Table.KNOWLEDGE_TITLE_X, GuiLayout.Table.KNOWLEDGE_TITLE_Y, TEXT_COLOUR, false);
        renderKnowledgePanel(graphics);
    }

    private void renderKnowledgePanel(GuiGraphics graphics) {
        Knowledge knowledge = Discovery.knowledge(minecraft.player);
        MixingResult preview = menu.preview(minecraft.level, knowledge);
        int x = GuiLayout.Table.KNOWLEDGE_X0 + 4;
        int y = GuiLayout.Table.KNOWLEDGE_Y0 + 18;
        for (Component line : preview.lines()) {
            graphics.drawString(font, line, x, y, TEXT_COLOUR, false);
            y += 11;
        }
        if (menu.progress() > 0) {
            y += 4;
            graphics.drawString(font, Component.translatable("gui.herbalistscraft.table.brewing",
                    menu.progress() * 100 / Math.max(1, menu.brewTime())), x, y, 0x4F6F4F, false);
            y += 11;
        }
        if (menu.isExperiment()) {
            graphics.drawString(font, Component.translatable("gui.herbalistscraft.table.experimental_note")
                    .withStyle(ChatFormatting.YELLOW), x, y, 0x8A6D1F, false);
        }
    }
}
