package com.ren.lostintime.common.block;

import com.ren.lostintime.common.blockentity.SoulConfiguratorBE;
import com.ren.lostintime.common.blockentity.SoulExtractorBE;
import com.ren.lostintime.common.init.BlockEntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SoulConfiguratorBlock extends LITMachineBlock {

    protected static final VoxelShape SHAPE_TOP = makeTopShape();
    protected static final VoxelShape SHAPE_MAIN = makeMainShape();
    protected static final VoxelShape SHAPE_SIDE = makeSideShape();

    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public SoulConfiguratorBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(PART, Part.MAIN).setValue(FACING, Direction.NORTH).setValue(ON, false));
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        if (pState.getValue(PART).isMain()){
            return new SoulConfiguratorBE(pPos, pState);
        }
        return null;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (!pState.is(pNewState.getBlock())) {
            Part part = pState.getValue(PART);
            Direction facing = pState.getValue(FACING);

            if (part == Part.MAIN) {
                BlockPos topPos = pPos.above();
                BlockPos sidePos = pPos.relative(facing.getClockWise());
                removePart(pLevel, topPos);
                removePart(pLevel, sidePos);
            } else {
                removePart(pLevel, part.getMainPos(pPos, facing));
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    private void removePart(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.is(this)) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 35);
            level.levelEvent(2001, pos, Block.getId(state));
        }
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide || !pState.getValue(PART).isMain() ? null : createTickerHelper(pBlockEntityType, BlockEntityInit.SOUL_CONFIGURATOR.get(), SoulConfiguratorBE::tick);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        pPos = pState.getValue(PART).getMainPos(pPos, pState.getValue(FACING));
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        if (pLevel.isClientSide) return;

        Direction facing = pState.getValue(FACING);

        BlockPos topPos = pPos.above();
        BlockPos sidePos = pPos.relative(facing.getClockWise());

        pLevel.setBlock(topPos, pState.setValue(PART, Part.TOP).setValue(FACING, facing), 3);
        pLevel.setBlock(sidePos, pState.setValue(PART, Part.SIDE).setValue(FACING, facing), 3);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(PART, FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Level level = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();
        Direction direction = pContext.getHorizontalDirection();

        BlockPos topPos = pos.above();
        BlockPos sidePos = pos.relative(direction.getClockWise());

        if (!level.getBlockState(topPos).canBeReplaced(pContext) || !level.getBlockState(sidePos).canBeReplaced(pContext)) {
            return null;
        }

        return this.defaultBlockState().setValue(PART, Part.MAIN).setValue(FACING, direction);
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(FACING, pRot.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    public enum Part implements StringRepresentable {

        MAIN, TOP, SIDE;

        public boolean isMain(){
            return this == MAIN;
        }

        public BlockPos getMainPos(BlockPos pos, Direction facing){
            return switch (this) {
                case MAIN -> pos;
                case TOP -> pos.below();
                case SIDE -> pos.relative(facing.getCounterClockWise());
            };
        }

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase();
        }
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Part part = pState.getValue(PART);
        Direction facing = pState.getValue(FACING);

        VoxelShape baseShape = switch (part) {
            case MAIN -> SHAPE_MAIN;
            case TOP -> SHAPE_TOP;
            case SIDE -> SHAPE_SIDE;
        };

        return rotateShape(facing, baseShape);
    }

    public static VoxelShape rotateShape(Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};
        int times = to.get2DDataValue();
        for (int i = 0; i < times; i++) {
            buffer[1] = Shapes.empty();
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) ->
                    buffer[1] = Shapes.join(buffer[1], Shapes.box(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX), BooleanOp.OR)
            );
            buffer[0] = buffer[1];
        }
        return buffer[0].optimize();
    }

    private static VoxelShape makeTopShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(-3.469446951953614e-18, 0, 0.0625, 0.9374999999999999, 0.5625, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.5625, 0.125, 0.875, 1, 0.875), BooleanOp.OR);
        return shape;
    }

    private static VoxelShape makeMainShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0.3125, 0.0625, 0.9375, 1, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0, 1, 0.3125, 1), BooleanOp.OR);
        return shape.optimize();
    }

    private static VoxelShape makeSideShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0.75, 0, 1, 1, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.3125, 0.0625, 1, 0.75, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0, 1, 0.3125, 1), BooleanOp.OR);
        return shape.optimize();
    }
}
