package com.ren.lostintime.common.block;

import com.ren.lostintime.common.block.properties.TitanosarcolitesPart;
import com.ren.lostintime.common.init.BlockInit;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
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
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

public class GiantTitanosarcolitesBlock extends Block implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<TitanosarcolitesPart> PART = EnumProperty.create("part",
            TitanosarcolitesPart.class);

    private static final Map<TitanosarcolitesPart, EnumMap<Direction, VoxelShape>> SHAPES = Util.make(new EnumMap<>(TitanosarcolitesPart.class), part ->{
        part.put(TitanosarcolitesPart.BASE_LEFT, createRotatedShape(Direction.NORTH, makeBaseShape()));
        part.put(TitanosarcolitesPart.BASE_RIGHT, createRotatedShape(Direction.NORTH, makeBaseShape()));
        part.put(TitanosarcolitesPart.PINCER_LEFT, createRotatedShape(Direction.NORTH, makeLeftPincerShape()));
        part.put(TitanosarcolitesPart.PINCER_RIGHT, createRotatedShape(Direction.NORTH, makeRightPincerShape()));
    });

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
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPES.getOrDefault(pState.getValue(PART), new EnumMap<>(Direction.class)).getOrDefault(pState.getValue(FACING), Shapes.block());
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        Direction forward = pState.getValue(FACING);
        Direction right = forward.getCounterClockWise();

        BlockPos baseRight = pPos.relative(right);
        BlockPos pincerLeft = pPos.relative(forward);
        BlockPos pincerRight = pPos.relative(forward).relative(right);

        return canPlacePart(pLevel, baseRight) &&
                canPlacePart(pLevel, pincerLeft) &&
                canPlacePart(pLevel, pincerRight);
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        Direction forward = pState.getValue(FACING);
        Direction right = forward.getCounterClockWise();

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

            pLevel.setBlock(baseRight, giantBase.setValue(GiantTitanosarcolitesBlock.PART, TitanosarcolitesPart.BASE_RIGHT), 3);
            pLevel.setBlock(pincerLeft, giantBase.setValue(GiantTitanosarcolitesBlock.PART, TitanosarcolitesPart.PINCER_LEFT), 3);
            pLevel.setBlock(pincerRight, giantBase.setValue(GiantTitanosarcolitesBlock.PART, TitanosarcolitesPart.PINCER_RIGHT), 3);
        }
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
        Direction left = forward.getClockWise();
        Direction back = forward.getOpposite();

        return switch (state.getValue(PART)) {
            case BASE_LEFT -> pos;
            case BASE_RIGHT -> pos.relative(left);
            case PINCER_LEFT -> pos.relative(back);
            case PINCER_RIGHT -> pos.relative(left).relative(back);
        };
    }

    private boolean canPlacePart(LevelReader level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.canBeReplaced() || state.is(Blocks.WATER);
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

    private static EnumMap<Direction, VoxelShape> createRotatedShape(Direction from, VoxelShape shape){
        return Util.make(new EnumMap<>(Direction.class), m -> {
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                m.put(dir, TitanosarcolitesBlock.rotateShape(from, dir, shape));
            }
        });
    }


    private static VoxelShape makeBaseShape(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0, 0, 1, 0.4375, 1), BooleanOp.OR);

        return shape;
    }

    private static VoxelShape makeLeftPincerShape(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.5, 0, 0, 1, 0.25, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.00625, 0.25, 0.4375, 0.00625, 0.5), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0, 0.5, 0.125, 0.25), BooleanOp.OR);

        return shape;
    }

    private static VoxelShape makeRightPincerShape(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0, 0, 0.5, 0.25, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 0.00625, 0.25, 0.9375, 0.00625, 0.5), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5, 0, 0, 0.9375, 0.125, 0.25), BooleanOp.OR);

        return shape;
    }
}
