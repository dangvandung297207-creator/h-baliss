package com.herbalistscraft.block.entity;

import com.herbalistscraft.herb.HerbCropBlock;
import com.herbalistscraft.knowledge.Discovery;
import com.herbalistscraft.recipe.MillRecipe;
import com.herbalistscraft.registry.ModBlockEntities;
import com.herbalistscraft.registry.ModParticles;
import com.herbalistscraft.registry.ModSounds;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * The mill's brain: it watches its two input slots, finds a matching recipe once, then grinds
 * steadily. Nothing here ticks inventories it does not own, and the recipe lookup only runs
 * when the inputs change.
 */
public class HerbalMillBlockEntity extends BlockEntity implements Container {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_ADDITIVE = 1;
    public static final int SLOT_OUTPUT = 2;

    private final NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
    private int progress;
    private int maxProgress;
    private MillRecipe current;
    private int soundCooldown;

    public HerbalMillBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HERBAL_MILL.get(), pos, state);
    }

    // ---- container ----------------------------------------------------------

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack stack = ContainerHelper.removeItem(items, slot, amount);
        if (!stack.isEmpty()) {
            setChanged();
            onContentsChanged(slot);
        }
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = ContainerHelper.takeItem(items, slot);
        onContentsChanged(slot);
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        setChanged();
        onContentsChanged(slot);
    }

    @Override
    public boolean stillValid(Player player) {
        return level != null && level.getBlockEntity(worldPosition) == this
                && player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D,
                worldPosition.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void clearContent() {
        items.clear();
        setChanged();
    }

    public int progress() {
        return progress;
    }

    public int maxProgress() {
        return maxProgress;
    }

    private void onContentsChanged(int slot) {
        if (slot != SLOT_OUTPUT) {
            current = null;
            progress = 0;
        }
    }

    // ---- ticking ------------------------------------------------------------

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (progress > 0) {
            if (soundCooldown-- <= 0) {
                soundCooldown = 30;
                level.playSound(null, pos, ModSounds.MILL.get(), SoundSource.BLOCKS, 0.35F, 1.0F);
            }
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ModParticles.HERBAL_SPARK.get(), pos.getX() + 0.5D, pos.getY() + 1.1D,
                        pos.getZ() + 0.5D, 1, 0.25D, 0.0D, 0.25D, 0.0D);
            }
        }
        if (current == null) {
            current = MillRecipe.find(level, items.get(SLOT_INPUT), items.get(SLOT_ADDITIVE));
            if (current == null) {
                progress = 0;
                maxProgress = 0;
                return;
            }
            maxProgress = current.processingTime();
            if (!canAccept(current)) {
                current = null;
                return;
            }
        }
        progress++;
        setChanged();
        if (progress >= maxProgress) {
            finish(level, pos);
        }
    }

    private boolean canAccept(MillRecipe recipe) {
        ItemStack output = items.get(SLOT_OUTPUT);
        ItemStack result = recipe.result();
        if (output.isEmpty()) {
            return true;
        }
        return ItemStack.isSameItemSameComponents(output, result)
                && output.getCount() + result.getCount() <= output.getMaxStackSize();
    }

    private void finish(Level level, BlockPos pos) {
        MillRecipe recipe = current;
        current = null;
        progress = 0;
        maxProgress = 0;
        if (recipe == null) {
            return;
        }
        ItemStack result = recipe.result().copy();
        ItemStack output = items.get(SLOT_OUTPUT);
        if (output.isEmpty()) {
            items.set(SLOT_OUTPUT, result);
        } else {
            output.grow(result.getCount());
        }
        items.get(SLOT_INPUT).shrink(1);
        recipe.additive().ifPresent(ingredient -> {
            if (!items.get(SLOT_ADDITIVE).isEmpty()) {
                items.get(SLOT_ADDITIVE).shrink(1);
            }
        });
        level.playSound(null, pos, ModSounds.MILL.get(), SoundSource.BLOCKS, 0.8F, 0.8F);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ModParticles.DRIED_LEAF.get(), pos.getX() + 0.5D, pos.getY() + 1.2D,
                    pos.getZ() + 0.5D, 6, 0.3D, 0.2D, 0.3D, 0.01D);
            ItemStack ground = items.get(SLOT_OUTPUT).isEmpty() ? ItemStack.EMPTY : items.get(SLOT_OUTPUT).copy();
            for (ServerPlayer player : serverLevel.getEntitiesOfClass(ServerPlayer.class,
                    new AABB(pos).inflate(8.0D))) {
                Discovery.onMill(player);
                Discovery.onGround(player, ground);
            }
        }
        setChanged();
    }

    // ---- persistence --------------------------------------------------------

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
        tag.putInt("progress", progress);
        tag.putInt("max_progress", maxProgress);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ContainerHelper.loadAllItems(tag, items, registries);
        progress = tag.getInt("progress");
        maxProgress = tag.getInt("max_progress");
    }

    /** Filled by the menu so the player can see the beat of the machinery. */
    public static int[] data(HerbalMillBlockEntity mill) {
        return new int[] { mill.progress, mill.maxProgress };
    }
}
