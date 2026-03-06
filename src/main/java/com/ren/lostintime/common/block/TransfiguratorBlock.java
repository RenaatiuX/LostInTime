package com.ren.lostintime.common.block;

import com.ren.lostintime.common.blockentity.TransfiguratorBE;
import com.ren.lostintime.common.init.BlockEntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class TransfiguratorBlock extends LITMachineBlock {

    private static final VoxelShape BOTTOM_SHAPE = makeBottomShape();
    private static final VoxelShape UPPER_SHAPE = makeUpperShape();

    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public TransfiguratorBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(ON, false)
                .setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (pState.getValue(HALF) == DoubleBlockHalf.UPPER) {
            pPos = pPos.below();
        }
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof MenuProvider && pPlayer instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer, (MenuProvider) blockEntity, pPos);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new TransfiguratorBE(pPos, pState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, BlockEntityInit.TRANSFIGURATOR.get(), TransfiguratorBE::tick);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockPos pos = pContext.getClickedPos();
        Level level = pContext.getLevel();

        if (pos.getY() < level.getMaxBuildHeight() - 1
                && level.getBlockState(pos.above()).canBeReplaced(pContext)) {

            return this.defaultBlockState()
                    .setValue(HALF, DoubleBlockHalf.LOWER)
                    .setValue(FACING, pContext.getHorizontalDirection().getOpposite())
                    .setValue(ON, false);
        }

        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return super.updateShape(state, dir, neighborState, level, pos, neighborPos);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (!pState.is(pNewState.getBlock())) {
            DoubleBlockHalf half = pState.getValue(HALF);
            BlockPos otherPos = half == DoubleBlockHalf.LOWER ? pPos.above() : pPos.below();
            BlockState otherState = pLevel.getBlockState(otherPos);
            if (otherState.is(this) && otherState.getValue(HALF) != half) {
                pLevel.setBlock(otherPos, Blocks.AIR.defaultBlockState(), 35);
                pLevel.levelEvent(2001, otherPos, Block.getId(otherState));
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return pState.getValue(HALF) == DoubleBlockHalf.LOWER ? BOTTOM_SHAPE : UPPER_SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(HALF, FACING);
        super.createBlockStateDefinition(pBuilder);
    }

    private static VoxelShape makeBottomShape(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0, 0, 1, 0.3125, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.3125, 0.0625, 0.9375, 0.6875, 0.9375), BooleanOp.OR);

        return shape;
    }

    private static VoxelShape makeUpperShape(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.75, 0, 0.0625, 0.9375, 0.3125, 0.25), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.0625, 0.25, 0.3125, 0.25), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0, 0.125, 0.125, 0.3125, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0.3125, 0.125, 0.125, 0.6875, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.875, 0.3125, 0.125, 0.875, 0.6875, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.875, 0, 0.125, 0.875, 0.3125, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0.3125, 0.875, 0.875, 0.6875, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0, 0.875, 0.875, 0.3125, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0.3125, 0.125, 0.875, 0.6875, 0.125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0, 0.125, 0.875, 0.3125, 0.125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.75, 0.25, 0.3125, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.75, 0, 0.75, 0.9375, 0.3125, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.3125, 0.0625, 0.25, 0.6875, 0.25), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.3125, 0.75, 0.25, 0.6875, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.75, 0.3125, 0.75, 0.9375, 0.6875, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.75, 0.3125, 0.0625, 0.9375, 0.6875, 0.25), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.6875, 0.0625, 0.9375, 0.8125, 0.9375), BooleanOp.OR);

        return shape;
    }
}
