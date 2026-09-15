package com.herbalistscraft.menu;

import com.herbalistscraft.block.entity.HerbalistTableBlockEntity;
import com.herbalistscraft.client.GuiLayout;
import com.herbalistscraft.knowledge.Knowledge;
import com.herbalistscraft.mixing.MixingResult;
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

/**
 * The Herbalist's Table screen contract: base, herb, extract and catalyst in, result out, and
 * the knowledge panel beside them. Deliberately not a brewing stand: five separate slots, a slow
 * brew and a preview the player only fully understands once they have learned the medicine.
 */
public class HerbalistTableMenu extends AbstractContainerMenu {
    private final Container container;
    private final ContainerData data;

    public HerbalistTableMenu(int id, Inventory inventory, BlockPos pos) {
        this(id, inventory, containerAt(inventory, pos), dataAt(inventory, pos));
    }

    public HerbalistTableMenu(int id, Inventory inventory, Container container, ContainerData data) {
        super(ModMenus.HERBALISTS_TABLE.get(), id);
        this.container = container;
        this.data = data;

        addSlot(new Slot(container, HerbalistTableBlockEntity.SLOT_BASE,
                GuiLayout.Table.BASE_X, GuiLayout.Table.BASE_Y));
        addSlot(new Slot(container, HerbalistTableBlockEntity.SLOT_HERB,
                GuiLayout.Table.HERB_X, GuiLayout.Table.HERB_Y));
        addSlot(new Slot(container, HerbalistTableBlockEntity.SLOT_EXTRACT,
                GuiLayout.Table.EXTRACT_X, GuiLayout.Table.EXTRACT_Y));
        addSlot(new OutputSlot(container, HerbalistTableBlockEntity.SLOT_RESULT,
                GuiLayout.Table.RESULT_X, GuiLayout.Table.RESULT_Y));
        addSlot(new Slot(container, HerbalistTableBlockEntity.SLOT_CATALYST,
                GuiLayout.Table.CATALYST_X, GuiLayout.Table.CATALYST_Y));

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9,
                        GuiLayout.Table.PLAYER_INV_X + column * 18,
                        GuiLayout.Table.PLAYER_INV_Y + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, GuiLayout.Table.HOTBAR_X + column * 18, GuiLayout.Table.HOTBAR_Y));
        }
        addDataSlots(data);
    }

    private static Container containerAt(Inventory inventory, BlockPos pos) {
        return inventory.player.level().getBlockEntity(pos) instanceof HerbalistTableBlockEntity table
                ? table : new SimpleContainer(5);
    }

    private static ContainerData dataAt(Inventory inventory, BlockPos pos) {
        return inventory.player.level().getBlockEntity(pos) instanceof HerbalistTableBlockEntity table
                ? table : new ContainerData() {
                    @Override
                    public int get(int index) {
                        return 0;
                    }

                    @Override
                    public void set(int index, int value) {
                    }

                    @Override
                    public int getCount() {
                        return 4;
                    }
                };
    }

    public int progress() {
        return data.get(HerbalistTableBlockEntity.DATA_PROGRESS);
    }

    public int brewTime() {
        return Math.max(1, data.get(HerbalistTableBlockEntity.DATA_BREW_TIME));
    }

    public int flags() {
        return data.get(HerbalistTableBlockEntity.DATA_FLAGS);
    }

    public int potency() {
        return data.get(HerbalistTableBlockEntity.DATA_POTENCY);
    }

    /** True while the current brew is an unnamed experiment rather than a written recipe. */
    public boolean isExperiment() {
        return (flags() & HerbalistTableBlockEntity.FLAG_EXPERIMENT) != 0;
    }

    /** The preview shown in the knowledge panel. The screen decides what the player may see. */
    public MixingResult preview(net.minecraft.world.level.Level level, Knowledge knowledge) {
        return com.herbalistscraft.mixing.MixingCalculator.calculate(level, container.getItem(0),
                container.getItem(1), container.getItem(2), container.getItem(4), knowledge);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            copy = stack.copy();
            if (index < 5) {
                if (!moveItemStackTo(stack, 5, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack, 0, 5, false)) {
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
