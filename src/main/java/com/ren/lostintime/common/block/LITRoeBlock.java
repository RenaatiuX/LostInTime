package com.ren.lostintime.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Optional;
import java.util.function.Supplier;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class LITRoeBlock extends Block implements BucketPickup, SimpleWaterloggedBlock {

    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.5D, 16.0D);

    private final int minBabySpawn;
    private final int maxBabySpawn;
    private final int minHatchTickDelay;
    private final int maxHatchTickDelay;
    private final Supplier<? extends EntityType<? extends Mob>> entityType;
    private final Supplier<? extends Item> roeBucketItem;
    private final boolean canBePlacedUnderWater;


    public LITRoeBlock(Properties pProperties, Supplier<? extends EntityType<? extends Mob>> entityType,
                       Supplier<? extends Item> roeBucketItem,
                       int minBabySpawn, int maxBabySpawn, int minHatchTickDelay, int maxHatchTickDelay) {
        this(pProperties, entityType, roeBucketItem, minBabySpawn, maxBabySpawn, minHatchTickDelay, maxHatchTickDelay, false);
    }


    public LITRoeBlock(Properties pProperties, Supplier<? extends EntityType<? extends Mob>> entityType,
                       Supplier<? extends Item> roeBucketItem,
                       int minBabySpawn, int maxBabySpawn, int minHatchTickDelay, int maxHatchTickDelay, boolean canBePlacedUnderWater) {
        super(pProperties);
        this.entityType = entityType;
        this.roeBucketItem = roeBucketItem;
        this.minBabySpawn = minBabySpawn;
        this.maxBabySpawn = maxBabySpawn;
        this.minHatchTickDelay = minHatchTickDelay;
        this.maxHatchTickDelay = maxHatchTickDelay;
        this.canBePlacedUnderWater = canBePlacedUnderWater;
    }

    @Override
    public boolean canPlaceLiquid(BlockGetter pLevel, BlockPos pPos, BlockState pState, Fluid pFluid) {
        return canBePlacedUnderWater && SimpleWaterloggedBlock.super.canPlaceLiquid(pLevel, pPos, pState, pFluid);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return mayPlaceOn(pLevel, pPos.below());
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        pLevel.scheduleTick(pPos, this, getFrogspawnHatchDelay(pLevel.getRandom()));
    }

    private int getFrogspawnHatchDelay(RandomSource pRandom) {
        return pRandom.nextInt(minHatchTickDelay, maxHatchTickDelay + 1);
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }
        return !this.canSurvive(pState, pLevel, pPos) ? Blocks.AIR.defaultBlockState() :
                super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (!this.canSurvive(pState, pLevel, pPos)) {
            this.destroyBlock(pLevel, pPos);
        } else {
            this.hatchFrogspawn(pLevel, pPos, pRandom);
        }
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        if (pEntity.getType().equals(EntityType.FALLING_BLOCK)) {
            this.destroyBlock(pLevel, pPos);
        }
    }

    private boolean mayPlaceOn(BlockGetter pLevel, BlockPos pPos) {
        FluidState fluidstate = pLevel.getFluidState(pPos);
        FluidState fluidstate1 = pLevel.getFluidState(pPos.above());
        if (canBePlacedUnderWater){
            var state = pLevel.getBlockState(pPos);
            return fluidstate1.is(FluidTags.WATER) && state.isFaceSturdy(pLevel, pPos, Direction.UP);
        }
        return fluidstate.getType() == Fluids.WATER && fluidstate1.getType() == Fluids.EMPTY;
    }

    private void hatchFrogspawn(ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        this.destroyBlock(pLevel, pPos);
        pLevel.playSound((Player)null, pPos, SoundEvents.FROGSPAWN_HATCH, SoundSource.BLOCKS, 1.0F, 1.0F);
        this.spawnBabies(pLevel, pPos, pRandom);
    }

    private void destroyBlock(Level pLevel, BlockPos pPos) {
        pLevel.destroyBlock(pPos, false);
    }

    private void spawnBabies(ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        int i = pRandom.nextInt(minBabySpawn, maxBabySpawn);
        for(int j = 1; j <= i; ++j) {
            Mob baby = entityType.get().create(pLevel);
            if (baby != null) {
                double d0 = (double)pPos.getX() + this.getRandomBabyPositionOffset(pRandom);
                double d1 = (double)pPos.getZ() + this.getRandomBabyPositionOffset(pRandom);
                int k = pRandom.nextInt(1, 361);
                baby.moveTo(d0, (double)pPos.getY() - 0.5D, d1, (float)k, 0.0F);
                baby.setPersistenceRequired();
                pLevel.addFreshEntity(baby);
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(WATERLOGGED);
    }

    private double getRandomBabyPositionOffset(RandomSource pRandom) {
        return Mth.clamp(pRandom.nextDouble(), 0.1, 0.9);
    }

    @Override
    public ItemStack pickupBlock(LevelAccessor pLevel, BlockPos pPos, BlockState pState) {
        pLevel.setBlock(pPos, Blocks.AIR.defaultBlockState(), 3);
        return new ItemStack(roeBucketItem.get());
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Optional.of(SoundEvents.BUCKET_FILL);
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
        boolean flag = fluidstate.getType() == Fluids.WATER;
        BlockState blockstate = super.defaultBlockState();
        return blockstate.setValue(WATERLOGGED,flag);
    }

    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }
}
