package com.ren.lostintime.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

public class CooksoniaBlock extends BushBlock implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public CooksoniaBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(WATERLOGGED);
    }

    @Override
    protected boolean mayPlaceOn(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return pState.is(Blocks.MUD) || pState.is(Blocks.CLAY) || pState.is(Blocks.SAND)
                || super.mayPlaceOn(pState, pLevel, pPos);
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        boolean soilValid = super.canSurvive(pState, pLevel, pPos);
        boolean hasWater = pState.getValue(WATERLOGGED) || hasWaterNearby(pLevel, pPos);
        boolean shallowDepth = !pLevel.getFluidState(pPos.above()).is(FluidTags.WATER);
        boolean seaLevel = pPos.getY() >= pLevel.getSeaLevel() - 1;
        return soilValid && hasWater && shallowDepth && seaLevel;
    }

    private boolean hasWaterNearby(LevelReader pLevel, BlockPos pPos) {
        BlockPos blockPos = pPos.below();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (pLevel.getFluidState(blockPos.relative(direction)).is(FluidTags.WATER)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
        boolean isWater = fluidstate.getType() == Fluids.WATER;
        if (pContext.getLevel().getFluidState(pContext.getClickedPos().above()).is(FluidTags.WATER)) {
            return null;
        }
        return this.defaultBlockState().setValue(WATERLOGGED, isWater);
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }
        return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }
}
