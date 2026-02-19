package com.ren.lostintime.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.function.Supplier;

public class RoeBucketItem extends Item {

    private final Supplier<? extends Block> roeBlockSupplier;
    private final Supplier<? extends SoundEvent> emptySoundSupplier;

    public RoeBucketItem(Supplier<? extends Block> roeBlock, Supplier<? extends SoundEvent> emptySoundSupplier, Properties properties) {
        super(properties.stacksTo(1));
        this.roeBlockSupplier = roeBlock;
        this.emptySoundSupplier = emptySoundSupplier;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (hit.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(stack);
        }

        BlockPos waterPos = hit.getBlockPos();
        BlockPos placePos = waterPos.above();

        if (!level.isClientSide) {
            FluidState fluidState = level.getFluidState(waterPos);
            BlockState placeState = level.getBlockState(placePos);

            boolean isWaterSource =
                    fluidState.is(Fluids.WATER) && fluidState.isSource();
            boolean canPlaceAbove = placeState.isAir();

            if (isWaterSource && canPlaceAbove) {
                level.setBlock(placePos, getRoeBlockSupplier().defaultBlockState(), 11);
                level.playSound(null, placePos, getEmptySound(), SoundSource.BLOCKS, 1.0F, 1.0F);

                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                    ItemStack emptyBucket = new ItemStack(Items.BUCKET);
                    if (!player.addItem(emptyBucket)) {
                        player.drop(emptyBucket, false);
                    }
                }

                return InteractionResultHolder.success(stack);
            }
        }

        return InteractionResultHolder.pass(stack);
    }

    public Block getRoeBlockSupplier() {
        return roeBlockSupplier.get();
    }

    protected SoundEvent getEmptySound() {
        return emptySoundSupplier.get();
    }
}
