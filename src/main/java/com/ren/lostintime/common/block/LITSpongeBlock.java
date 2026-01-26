package com.ren.lostintime.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class LITSpongeBlock extends Block {

    private static final int PARTICLE_INTERVAL = 100;

    private final Supplier<Block> deadBlock;
    private final boolean spawnParticles;

    public LITSpongeBlock(Supplier<Block> deadBlock, boolean spawnParticles, Properties pProperties) {
        super(pProperties);
        this.deadBlock = deadBlock;
        this.spawnParticles = spawnParticles;
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (!this.scanForWater(pLevel, pPos)) {
            pLevel.setBlock(pPos, this.deadBlock.get().defaultBlockState(), 2);
            return;
        }

        if (spawnParticles) {
            pLevel.sendParticles(ParticleTypes.BUBBLE_COLUMN_UP, pPos.getX() + 0.5, pPos.getY() + 1.0   , pPos.getZ() +
                    0.5, 6, 0.15, 0.05, 0.15, 0.15);
            pLevel.scheduleTick(pPos, this, PARTICLE_INTERVAL);
        }
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        if (!this.scanForWater(pLevel, pPos)) {
            pLevel.scheduleTick(pPos, this, 60 + pLevel.getRandom().nextInt(40));
        }

        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
    }

    protected boolean scanForWater(BlockGetter pLevel, BlockPos pPos) {
        BlockState state = pLevel.getBlockState(pPos);
        for (Direction direction : Direction.values()) {
            FluidState fluidstate = pLevel.getFluidState(pPos.relative(direction));
            if (state.canBeHydrated(pLevel, pPos, fluidstate, pPos.relative(direction))) {
                return true;
            }
        }

        return false;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        if (!this.scanForWater(pContext.getLevel(), pContext.getClickedPos())) {
            pContext.getLevel().scheduleTick(pContext.getClickedPos(), this, 60 + pContext.getLevel().getRandom().nextInt(40));
        }
        if (spawnParticles) {
            pContext.getLevel().scheduleTick(pContext.getClickedPos(), this, PARTICLE_INTERVAL);
        }

        return this.defaultBlockState();
    }
}
