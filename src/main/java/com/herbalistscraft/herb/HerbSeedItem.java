package com.herbalistscraft.herb;

import com.herbalistscraft.HerbalistsCraft;
import com.herbalistscraft.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** Seeds that can be planted back into the ground to grow the herb. */
public class HerbSeedItem extends Item {
    private final ResourceKey<HerbDefinition> herb;

    public HerbSeedItem(Properties properties, ResourceKey<HerbDefinition> herb) {
        super(properties);
        this.herb = herb;
    }

    public ResourceKey<HerbDefinition> herbKey() {
        return herb;
    }

    public ResourceLocation cropId() {
        return ResourceLocation.fromNamespaceAndPath(HerbalistsCraft.MODID, herb.location().getPath() + "_crop");
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        BlockState existing = level.getBlockState(pos);
        Block crop = level.registryAccess().registryOrThrow(Registries.BLOCK).get(cropId());
        if (crop == null) {
            return InteractionResult.PASS;
        }
        if (!existing.isAir() && !existing.canBeReplaced()) {
            return InteractionResult.PASS;
        }
        BlockState placed = crop.defaultBlockState();
        if (!placed.canSurvive(level, pos)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            level.setBlock(pos, placed, Block.UPDATE_ALL);
            level.playSound(null, pos, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 0.8F, 1.0F);
            if (context.getPlayer() != null && !context.getPlayer().getAbilities().instabuild) {
                context.getItemInHand().shrink(1);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        return "item." + HerbalistsCraft.MODID + "." + herb.location().getPath() + "_seeds";
    }

    /** Kept for the crop class, which needs the block to exist before seeds are used. */
    public Block cropBlock() {
        return ModBlocks.BLOCKS.getRegistry()
                .map(registry -> registry.get(cropId()))
                .orElse(null);
    }
}
