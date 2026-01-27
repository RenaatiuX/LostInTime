package com.ren.lostintime.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CoralPlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class LITTallSpongeBlock extends CoralPlantBlock {

    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    private final Supplier<Block> deadBlock;

    public LITTallSpongeBlock(Supplier<Block> pDeadBlock, Properties pProperties) {
        super(pDeadBlock.get(), pProperties);
        this.deadBlock = pDeadBlock;
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER).setValue(WATERLOGGED, true));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockPos pos = pContext.getClickedPos();
        Level level = pContext.getLevel();

        if (pos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(pos.above()).canBeReplaced(pContext)) {
            FluidState fluid = level.getFluidState(pos);
            boolean water = fluid.is(FluidTags.WATER) && fluid.getAmount() == 8;

            return this.defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER).setValue(WATERLOGGED, water);
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        boolean water = pState.getValue(WATERLOGGED);
        BlockState upper = pState.setValue(HALF, DoubleBlockHalf.UPPER).setValue(WATERLOGGED, water);
        pLevel.setBlock(pPos.above(), upper, 2);
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pState.getValue(HALF) != DoubleBlockHalf.LOWER) return;
        if (!scanForWater(pState, pLevel, pPos)) {
            BlockState deadLower = deadBlock.get()
                    .defaultBlockState()
                    .setValue(HALF, DoubleBlockHalf.LOWER)
                    .setValue(WATERLOGGED, pState.getValue(WATERLOGGED));

            BlockState deadUpper = deadLower.setValue(HALF, DoubleBlockHalf.UPPER);

            pLevel.setBlock(pPos, deadLower, 2);
            pLevel.setBlock(pPos.above(), deadUpper.setValue(HALF, DoubleBlockHalf.UPPER), 2);
            ;
        }
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        DoubleBlockHalf half = pState.getValue(HALF);
        if (pFacingState.getBlock() instanceof LITDeadTallSpongeBlock) {
            return pState;
        }

        if (half == DoubleBlockHalf.UPPER) {
            if (!pLevel.getBlockState(pCurrentPos.below()).is(this)) {
                return Blocks.AIR.defaultBlockState();
            }
        } else {
            if (pFacing == Direction.UP && !pFacingState.is(this)) {
                return Blocks.AIR.defaultBlockState();
            }
        }

        return pState;
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        if (pState.getValue(HALF) == DoubleBlockHalf.LOWER) {
            this.tryScheduleDieTick(pState, pLevel, pPos);
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        DoubleBlockHalf half = state.getValue(HALF);

        if (half == DoubleBlockHalf.UPPER) {
            BlockState below = level.getBlockState(pos.below());
            return below.is(this) && below.getValue(HALF) == DoubleBlockHalf.LOWER;
        }

        return super.canSurvive(state, level, pos);
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF, WATERLOGGED);
    }
}
