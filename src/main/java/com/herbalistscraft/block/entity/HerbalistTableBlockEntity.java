package com.herbalistscraft.block.entity;

import com.herbalistscraft.Config;
import com.herbalistscraft.knowledge.Discovery;
import com.herbalistscraft.knowledge.Knowledge;
import com.herbalistscraft.mixing.MixingCalculator;
import com.herbalistscraft.mixing.MixingResult;
import com.herbalistscraft.mixing.MixtureData;
import com.herbalistscraft.registry.ModBlockEntities;
import com.herbalistscraft.registry.ModDataComponents;
import com.herbalistscraft.registry.ModItems;
import com.herbalistscraft.registry.ModParticles;
import com.herbalistscraft.registry.ModSounds;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * The Herbalist's Table. Four inputs - base, herb, extract, catalyst - and a slow, deliberate
 * brew. A written recipe produces its named medicine; anything else is computed and bottled as
 * an experimental mixture, which is why the player cannot simply guess their way to power.
 */
public class HerbalistTableBlockEntity extends BlockEntity implements Container, ContainerData {
    public static final int SLOT_BASE = 0;
    public static final int SLOT_HERB = 1;
    public static final int SLOT_EXTRACT = 2;
    public static final int SLOT_RESULT = 3;
    public static final int SLOT_CATALYST = 4;
    public static final int DATA_PROGRESS = 0;
    public static final int DATA_BREW_TIME = 1;
    public static final int DATA_FLAGS = 2;
    public static final int DATA_POTENCY = 3;
    public static final int FLAG_EXPERIMENT = 1;
    public static final int FLAG_KNOWN = 2;

    private final NonNullList<ItemStack> items = NonNullList.withSize(5, ItemStack.EMPTY);
    private int progress;
    private int brewTime;
    private int flags;
    private int potency;
    private int soundCooldown;

    public HerbalistTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HERBALISTS_TABLE.get(), pos, state);
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
        }
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        if (slot != SLOT_RESULT) {
            progress = 0;
            brewTime = 0;
        }
        setChanged();
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

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot != SLOT_RESULT;
    }

    // ---- container data (menu sync) -----------------------------------------

    @Override
    public int get(int index) {
        return switch (index) {
            case DATA_PROGRESS -> progress;
            case DATA_BREW_TIME -> brewTime;
            case DATA_FLAGS -> flags;
            case DATA_POTENCY -> potency;
            default -> 0;
        };
    }

    @Override
    public void set(int index, int value) {
        switch (index) {
            case DATA_PROGRESS -> progress = value;
            case DATA_BREW_TIME -> brewTime = value;
            case DATA_FLAGS -> flags = value;
            case DATA_POTENCY -> potency = value;
            default -> {
            }
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    public boolean isBrewing() {
        return progress > 0 && !items.get(SLOT_BASE).isEmpty() && !items.get(SLOT_HERB).isEmpty();
    }

    public boolean isExperiment() {
        return (flags & FLAG_EXPERIMENT) != 0;
    }

    /** Preview for the open menu: computed fresh, never mutating the table. */
    public MixingResult preview(Knowledge knowledge) {
        if (level == null) {
            return MixingResult.failure(List.of());
        }
        return MixingCalculator.calculate(level, items.get(SLOT_BASE), items.get(SLOT_HERB),
                items.get(SLOT_EXTRACT), items.get(SLOT_CATALYST), knowledge);
    }

    // ---- brewing ------------------------------------------------------------

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!items.get(SLOT_RESULT).isEmpty()) {
            progress = 0;
            brewTime = 0;
            return;
        }
        if (items.get(SLOT_BASE).isEmpty() || items.get(SLOT_HERB).isEmpty()) {
            progress = 0;
            brewTime = 0;
            flags = 0;
            potency = 0;
            return;
        }
        MixingResult preview = MixingCalculator.calculate(level, items.get(SLOT_BASE), items.get(SLOT_HERB),
                items.get(SLOT_EXTRACT), items.get(SLOT_CATALYST), null);
        if (preview.failure() && !Config.experimentation()) {
            progress = 0;
            return;
        }
        if (brewTime <= 0) {
            brewTime = preview.experimental() ? 240 : 200;
        }
        progress++;
        potency = Math.round(preview.potency() * 100.0F);
        if (progress % 40 == 0 && soundCooldown-- <= 0) {
            soundCooldown = 2;
            level.playSound(null, pos, ModSounds.POUR.get(), SoundSource.BLOCKS, 0.3F, 1.2F);
        }
        if (progress % 25 == 0) {
            serverLevel.sendParticles(preview.experimental() ? ModParticles.TOXIC_SMOKE.get()
                            : ModParticles.HERBAL_SPARK.get(),
                    pos.getX() + 0.5D, pos.getY() + 1.05D, pos.getZ() + 0.5D, 2, 0.25D, 0.1D, 0.25D, 0.0D);
        }
        setChanged();
        if (progress >= brewTime) {
            finish(serverLevel, pos, preview);
        }
    }

    private void finish(ServerLevel level, BlockPos pos, MixingResult preview) {
        ItemStack result = resultStack(preview);
        if (result.isEmpty()) {
            progress = 0;
            return;
        }
        items.set(SLOT_RESULT, result);
        consume(SLOT_BASE);
        consume(SLOT_HERB);
        if (!items.get(SLOT_EXTRACT).isEmpty()) {
            consume(SLOT_EXTRACT);
        }
        if (!items.get(SLOT_CATALYST).isEmpty()) {
            consume(SLOT_CATALYST);
        }
        flags = (preview.experimental() ? FLAG_EXPERIMENT : 0) | (preview.known() ? FLAG_KNOWN : 0);
        progress = 0;
        brewTime = 0;
        level.playSound(null, pos, ModSounds.POUR.get(), SoundSource.BLOCKS, 0.9F, 0.9F);
        level.sendParticles(preview.experimental() ? ModParticles.TOXIC_SMOKE.get() : ModParticles.HERBAL_SPARK.get(),
                pos.getX() + 0.5D, pos.getY() + 1.1D, pos.getZ() + 0.5D, 8, 0.3D, 0.2D, 0.3D, 0.01D);
        for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, new AABB(pos).inflate(6.0D))) {
            if (preview.experimental()) {
                Discovery.onCraft(player);
            } else {
                Discovery.discoverRecipe(player, preview.output(),
                        result.getHoverName().getString());
                Discovery.onCraft(player);
            }
        }
        setChanged();
    }

    private ItemStack resultStack(MixingResult preview) {
        if (preview.experimental()) {
            ItemStack stack = new ItemStack(preview.outputName().contains("tea")
                    ? ModItems.EXPERIMENTAL_TEA.get() : ModItems.EXPERIMENTAL_TONIC.get());
            stack.set(ModDataComponents.MIXTURE.get(), new MixtureData(preview.potency(), 0,
                    propertiesOf(preview), false, com.herbalistscraft.mixing.PropertyEffects.summary(preview.properties())));
            return stack;
        }
        if (preview.failure()) {
            return new ItemStack(ModItems.FAILED_MIXTURE.get());
        }
        return net.minecraft.core.registries.BuiltInRegistries.ITEM.getOptional(preview.output())
                .map(ItemStack::new)
                .orElse(ItemStack.EMPTY);
    }

    private static java.util.Map<String, Float> propertiesOf(MixingResult preview) {
        java.util.Map<String, Float> out = new java.util.LinkedHashMap<>();
        preview.properties().forEach((property, value) -> {
            if (value > 0.0F) {
                out.put(property.name(), value);
            }
        });
        return out;
    }

    private void consume(int slot) {
        ItemStack stack = items.get(slot);
        if (!stack.isEmpty()) {
            stack.shrink(1);
        }
    }

    // ---- persistence --------------------------------------------------------

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
        tag.putInt("progress", progress);
        tag.putInt("brew_time", brewTime);
        tag.putInt("flags", flags);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ContainerHelper.loadAllItems(tag, items, registries);
        progress = tag.getInt("progress");
        brewTime = tag.getInt("brew_time");
        flags = tag.getInt("flags");
    }

    /** Medicine id of the last completed brew, for the journal. */
    public ResourceLocation lastOutput() {
        return items.get(SLOT_RESULT).isEmpty() ? null
                : net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(items.get(SLOT_RESULT).getItem());
    }
}
