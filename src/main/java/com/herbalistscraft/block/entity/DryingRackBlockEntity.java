package com.herbalistscraft.block.entity;

import com.herbalistscraft.Config;
import com.herbalistscraft.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** One herb on the rack, drying from fresh to dried over the configured time. */
public class DryingRackBlockEntity extends BlockEntity {
    private ItemStack stored = ItemStack.EMPTY;
    private ItemStack result = ItemStack.EMPTY;
    private int progress;
    private int total;

    public DryingRackBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DRYING_RACK.get(), pos, state);
    }

    public boolean isEmpty() {
        return stored.isEmpty();
    }

    public ItemStack stored() {
        return stored;
    }

    public boolean startDrying(ItemStack stack) {
        if (!stored.isEmpty()) {
            return false;
        }
        ItemStack dried = com.herbalistscraft.mixing.HerbProcessing.dry(stack);
        if (dried.isEmpty()) {
            return false;
        }
        this.stored = stack.copyWithCount(1);
        this.result = dried;
        this.total = Math.max(20, Config.dryingTicks());
        this.progress = 0;
        setChanged();
        return true;
    }

    public boolean isDone() {
        return !stored.isEmpty() && progress >= total;
    }

    public ItemStack take() {
        ItemStack out = result.copy();
        stored = ItemStack.EMPTY;
        result = ItemStack.EMPTY;
        progress = 0;
        total = 0;
        setChanged();
        return out;
    }

    public float progressFraction() {
        return total <= 0 ? 0.0F : Math.min(1.0F, progress / (float) total);
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (stored.isEmpty() || progress >= total) {
            return;
        }
        progress++;
        if (progress % 60 == 0) {
            level.sendParticles(net.minecraft.core.particles.ParticleTypes.CLOUD, pos.getX() + 0.5D,
                    pos.getY() + 1.1D, pos.getZ() + 0.5D, 1, 0.2D, 0.05D, 0.2D, 0.0D);
        }
        if (progress >= total) {
            level.playSound(null, pos, com.herbalistscraft.registry.ModSounds.DRY.get(),
                    net.minecraft.sounds.SoundSource.BLOCKS, 0.6F, 1.1F);
        }
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!stored.isEmpty()) {
            tag.put("stored", stored.save(registries));
        }
        if (!result.isEmpty()) {
            tag.put("result", result.save(registries));
        }
        tag.putInt("progress", progress);
        tag.putInt("total", total);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        stored = tag.contains("stored") ? ItemStack.parse(registries, tag.getCompound("stored")).orElse(ItemStack.EMPTY)
                : ItemStack.EMPTY;
        result = tag.contains("result") ? ItemStack.parse(registries, tag.getCompound("result")).orElse(ItemStack.EMPTY)
                : ItemStack.EMPTY;
        progress = tag.getInt("progress");
        total = tag.getInt("total");
    }

    public int progress() {
        return progress;
    }

    public int total() {
        return total;
    }
}
