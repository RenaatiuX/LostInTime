package com.ren.lostintime.common.block;

import com.ren.lostintime.common.block.properties.TitanosarcolitesPart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class GiantTitanosarcolitesBlock extends Block implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<TitanosarcolitesPart> PART = EnumProperty.create("part",
            TitanosarcolitesPart.class);

    public GiantTitanosarcolitesBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(WATERLOGGED, false)
                .setValue(FACING, Direction.NORTH)
                .setValue(PART, TitanosarcolitesPart.BASE_LEFT));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(WATERLOGGED, FACING, PART);
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }
        if (!isStructureIntact(pLevel, pPos, pState)) {
            return pState.getValue(WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
    }

    private BlockPos getBaseLeftPos(BlockPos pos, BlockState state) {
        Direction forward = state.getValue(FACING);
        Direction right = forward.getCounterClockWise();
        Direction left = forward.getClockWise();
        Direction back = forward.getOpposite();

        return switch (state.getValue(PART)) {
            case BASE_LEFT -> pos;
            case BASE_RIGHT -> pos.relative(left);
            case PINCER_LEFT -> pos.relative(back);
            case PINCER_RIGHT -> pos.relative(left).relative(back);
        };
    }

    private boolean isStructureIntact(LevelAccessor level, BlockPos pos, BlockState state) {
        BlockPos baseLeftPos = getBaseLeftPos(pos, state);
        Direction forward = state.getValue(FACING);
        Direction right = forward.getCounterClockWise();

        BlockPos baseRightPos = baseLeftPos.relative(right);
        BlockPos pincerLeftPos = baseLeftPos.relative(forward);
        BlockPos pincerRightPos = baseLeftPos.relative(forward).relative(right);

        return checkPart(level, baseLeftPos, state, TitanosarcolitesPart.BASE_LEFT) &&
                checkPart(level, baseRightPos, state, TitanosarcolitesPart.BASE_RIGHT) &&
                checkPart(level, pincerLeftPos, state, TitanosarcolitesPart.PINCER_LEFT) &&
                checkPart(level, pincerRightPos, state, TitanosarcolitesPart.PINCER_RIGHT);
    }

    private boolean checkPart(LevelAccessor level, BlockPos pos, BlockState referenceState, TitanosarcolitesPart expectedPart) {
        BlockState stateAtPos = level.getBlockState(pos);
        return stateAtPos.is(this) &&
                stateAtPos.getValue(FACING) == referenceState.getValue(FACING) &&
                stateAtPos.getValue(PART) == expectedPart;
    }

    @Override
    public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        if (!pLevel.isClientSide && !pPlayer.isCreative()) {

            super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
        }
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (!pState.is(pNewState.getBlock())) {
            destroyOtherParts(pLevel, pPos, pState);
            super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        }
    }

    private void destroyOtherParts(Level level, BlockPos pos, BlockState state) {
        BlockPos baseLeftPos = getBaseLeftPos(pos, state);
        Direction forward = state.getValue(FACING);
        Direction right = forward.getClockWise();

        BlockPos[] parts = {baseLeftPos, baseLeftPos.relative(right), baseLeftPos.relative(forward),
                baseLeftPos.relative(forward).relative(right)};

        for (BlockPos partPos : parts) {
            if (!partPos.equals(pos)) {
                BlockState stateAtPos = level.getBlockState(partPos);
                if (stateAtPos.is(this)) {
                    boolean hasWater = stateAtPos.getValue(WATERLOGGED);
                    level.setBlock(partPos, hasWater ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
        Direction facing = pContext.getHorizontalDirection().getOpposite();
        return this.defaultBlockState()
                .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER)
                .setValue(FACING, facing);
    }
}
