package com.ren.lostintime.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SoulConfigurator extends LITMachineBlock {

    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public SoulConfigurator(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(PART, Part.MAIN).setValue(FACING, Direction.NORTH));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return null;
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
    public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        if (pLevel.isClientSide) return;
        Direction facing = pState.getValue(FACING);
        Part part = pState.getValue(PART);

        BlockPos mainPos = switch (part) {
            case MAIN -> pPos;
            case TOP -> pPos.below();
            case SIDE -> pPos.relative(facing.getCounterClockWise());
        };

        BlockPos topPos = mainPos.above();
        BlockPos sidePos = mainPos.relative(facing.getClockWise());

        pLevel.destroyBlock(topPos, false);
        pLevel.destroyBlock(sidePos, false);

        if (part != Part.MAIN) {
            pLevel.destroyBlock(mainPos, !pPlayer.isCreative());
        }

        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
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

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase();
        }
    }
}
