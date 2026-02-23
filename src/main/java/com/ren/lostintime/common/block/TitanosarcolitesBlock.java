package com.ren.lostintime.common.block;

import com.ren.lostintime.common.block.properties.TitanosarcolitesPart;
import com.ren.lostintime.common.init.BlockInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class TitanosarcolitesBlock extends Block implements SimpleWaterloggedBlock, BonemealableBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final Map<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);

    //Bad idea i think
    static {
        VoxelShape baseShape = makeShape();
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            SHAPES.put(dir, rotateShape(Direction.NORTH, dir, baseShape));
        }
    }

    public TitanosarcolitesBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(WATERLOGGED, true)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(WATERLOGGED, FACING);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPES.get(pState.getValue(FACING));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
        Direction facing = pContext.getHorizontalDirection().getOpposite();
        return this.defaultBlockState()
                .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER)
                .setValue(FACING, facing);
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }
        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
    }

    @Override
    public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation direction) {
        return state.setValue(FACING, direction.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader pLevel, BlockPos pPos, BlockState pState, boolean pIsClient) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
        Direction forward = pState.getValue(FACING);
        Direction right = forward.getCounterClockWise();

        BlockPos baseLeft = pPos;
        BlockPos baseRight = pPos.relative(right);
        BlockPos pincerLeft = pPos.relative(forward);
        BlockPos pincerRight = pPos.relative(forward).relative(right);

        if (canPlacePart(pLevel, baseRight) &&
                canPlacePart(pLevel, pincerLeft) &&
                canPlacePart(pLevel, pincerRight)) {

            boolean hasWater = pState.getValue(WATERLOGGED);
            BlockState giantBase = BlockInit.GIANT_TITANOSARCOLITES.get().defaultBlockState()
                    .setValue(GiantTitanosarcolitesBlock.FACING, forward)
                    .setValue(GiantTitanosarcolitesBlock.WATERLOGGED, hasWater);

            pLevel.setBlock(baseLeft, giantBase.setValue(GiantTitanosarcolitesBlock.PART, TitanosarcolitesPart.BASE_LEFT), 3);
            pLevel.setBlock(baseRight, giantBase.setValue(GiantTitanosarcolitesBlock.PART, TitanosarcolitesPart.BASE_RIGHT), 3);
            pLevel.setBlock(pincerLeft, giantBase.setValue(GiantTitanosarcolitesBlock.PART, TitanosarcolitesPart.PINCER_LEFT), 3);
            pLevel.setBlock(pincerRight, giantBase.setValue(GiantTitanosarcolitesBlock.PART, TitanosarcolitesPart.PINCER_RIGHT), 3);
        }
    }

    private boolean canPlacePart(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.canBeReplaced() || state.is(Blocks.WATER);
    }

    public static VoxelShape makeShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0, 0.625, 1, 0.25, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.8125, 0, 0, 1, 0.125, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 0.00625, 0, 0.8125, 0.00625, 0.25), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0, 0.1875, 0.125, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.00625, 0, 0.4375, 0.00625, 0.25), BooleanOp.OR);

        return shape;
    }

    public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{ shape, Shapes.empty() };

        int times = (to.get2DDataValue() - from.get2DDataValue() + 4) % 4;
        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(buffer[1],
                    Shapes.box(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
    }
}
