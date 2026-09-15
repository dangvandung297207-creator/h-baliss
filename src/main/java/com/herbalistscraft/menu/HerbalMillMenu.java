package com.herbalistscraft.menu;

import com.herbalistscraft.block.entity.HerbalMillBlockEntity;
import com.herbalistscraft.client.GuiLayout;
import com.herbalistscraft.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/** The mill's screen contract: two inputs, one output, a progress bar and the player's pockets. */
public class HerbalMillMenu extends AbstractContainerMenu {
    private static final int PLAYER_ROWS = 3;
    private static final int PLAYER_COLUMNS = 9;

    private final Container container;
    private final ContainerData data;

    public HerbalMillMenu(int id, Inventory inventory, BlockPos pos) {
        this(id, inventory, containerAt(inventory, pos), dataAt(inventory, pos));
    }

    public HerbalMillMenu(int id, Inventory inventory, Container container, ContainerData data) {
        super(ModMenus.HERBAL_MILL.get(), id);
        this.container = container;
        this.data = data;

        addSlot(new Slot(container, HerbalMillBlockEntity.SLOT_INPUT,
                GuiLayout.Mill.INPUT_X, GuiLayout.Mill.INPUT_Y));
        addSlot(new Slot(container, HerbalMillBlockEntity.SLOT_ADDITIVE,
                GuiLayout.Mill.ADDITIVE_X, GuiLayout.Mill.ADDITIVE_Y));
        addSlot(new OutputSlot(container, HerbalMillBlockEntity.SLOT_OUTPUT,
                GuiLayout.Mill.OUTPUT_X, GuiLayout.Mill.OUTPUT_Y));

        for (int row = 0; row < PLAYER_ROWS; row++) {
            for (int column = 0; column < PLAYER_COLUMNS; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9,
                        GuiLayout.Mill.PLAYER_INV_X + column * 18,
                        GuiLayout.Mill.PLAYER_INV_Y + row * 18));
            }
        }
        for (int column = 0; column < PLAYER_COLUMNS; column++) {
            addSlot(new Slot(inventory, column, GuiLayout.Mill.HOTBAR_X + column * 18, GuiLayout.Mill.HOTBAR_Y));
        }
        addDataSlots(data);
    }

    private static Container containerAt(Inventory inventory, BlockPos pos) {
        return inventory.player.level().getBlockEntity(pos) instanceof HerbalMillBlockEntity mill
                ? mill : new SimpleContainer(3);
    }

    private static ContainerData dataAt(Inventory inventory, BlockPos pos) {
        return inventory.player.level().getBlockEntity(pos) instanceof HerbalMillBlockEntity mill
                ? mill : new ContainerData() {
                    @Override
                    public int get(int index) {
                        return 0;
                    }

                    @Override
                    public void set(int index, int value) {
                    }

                    @Override
                    public int getCount() {
                        return 2;
                    }
                };
    }

    public int progress() {
        return data.get(0);
    }

    public int maxProgress() {
        return data.get(1);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            copy = stack.copy();
            if (index < 3) {
                if (!moveItemStackTo(stack, 3, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack, 0, 3, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    /** The output slot takes nothing out of the player's hands. */
    private static class OutputSlot extends Slot {
        OutputSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }
}
