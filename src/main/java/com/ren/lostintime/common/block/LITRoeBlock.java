package com.ren.lostintime.common.block;

import com.ren.lostintime.common.entity.LITAnimal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Supplier;

public class LITRoeBlock extends Block {

    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.5D, 16.0D);

    private final int minBabySpawn;
    private final int maxBabySpawn;
    private final int minHatchTickDelay;
    private final int maxHatchTickDelay;
    private final Supplier<? extends EntityType> entityType;

    public LITRoeBlock(Properties pProperties, Supplier<? extends EntityType> entityType,
                       int minBabySpawn, int maxBabySpawn, int minHatchTickDelay, int maxHatchTickDelay) {
        super(pProperties);
        this.entityType = entityType;
        this.minBabySpawn = minBabySpawn;
        this.maxBabySpawn = maxBabySpawn;
        this.minHatchTickDelay = minHatchTickDelay;
        this.maxHatchTickDelay = maxHatchTickDelay;
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
        return fluidstate.getType() == Fluids.WATER && fluidstate1.getType() == Fluids.EMPTY;
    }

    private void hatchFrogspawn(ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        this.destroyBlock(pLevel, pPos);
        pLevel.playSound((Player)null, pPos, SoundEvents.FROGSPAWN_HATCH, SoundSource.BLOCKS, 1.0F, 1.0F);
        this.spawnBabes(pLevel, pPos, pRandom);
    }

    private void destroyBlock(Level pLevel, BlockPos pPos) {
        pLevel.destroyBlock(pPos, false);
    }

    private void spawnBabes(ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        int i = pRandom.nextInt(minBabySpawn, maxBabySpawn);
        for(int j = 1; j <= i; ++j) {
            LITAnimal baby = (LITAnimal) entityType.get().create(pLevel);
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

    private double getRandomBabyPositionOffset(RandomSource pRandom) {
        double d0 = (double)(Tadpole.HITBOX_WIDTH / 2.0F);
        return Mth.clamp(pRandom.nextDouble(), d0, 1.0D - d0);
    }
}
