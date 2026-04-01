package com.ren.lostintime.common.block;

import com.ren.lostintime.common.entity.LITAnimal;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class LITEggBlock extends Block {

    private static final VoxelShape ONE_EGG_SHAPE = Block.box(6, 0, 5, 11, 5, 10);
    private static final VoxelShape MULTI_EGG_SHAPE = Block.box(3, 0, 3, 13, 6, 13);

    public static final IntegerProperty HATCH = BlockStateProperties.HATCH;
    public static final IntegerProperty EGGS = BlockStateProperties.EGGS;

    private final Supplier<? extends EntityType> entityType;
    private final TagKey<Block> validBlock;
    private final int maxEggs;

    public LITEggBlock(Properties pProperties, Supplier<? extends EntityType> entityType, TagKey<Block> validBlock, int maxEggs) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HATCH, 0));
        this.entityType = entityType;
        this.validBlock = validBlock;
        this.maxEggs = Math.min(maxEggs, 4);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return pState.getValue(EGGS) > 1 ? MULTI_EGG_SHAPE : ONE_EGG_SHAPE;
    }

    @Override
    public boolean canBeReplaced(BlockState pState, BlockPlaceContext pUseContext) {
        return !pUseContext.isSecondaryUseActive() && pUseContext.getItemInHand().is(this.asItem()) && pState.getValue(EGGS) < this.maxEggs ? true : super.canBeReplaced(pState, pUseContext);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState blockstate = pContext.getLevel().getBlockState(pContext.getClickedPos());
        if (blockstate.is(this)) {
            return blockstate.setValue(EGGS, Math.min(this.maxEggs, blockstate.getValue(EGGS) + 1));
        } else {
            return super.getStateForPlacement(pContext);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(HATCH, EGGS);
    }

    public int getHatchLevel(BlockState pState) {
        return pState.getValue(HATCH);
    }

    private boolean isReadyToHatch(BlockState pState) {
        return this.getHatchLevel(pState) == 2;
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (this.shouldUpdateHatchLevel(pLevel, pPos.below()) && pState.is(this)) {
            int i = pState.getValue(HATCH);
            if (i < 2) {
                pLevel.playSound(null, pPos, SoundEvents.TURTLE_EGG_CRACK, SoundSource.BLOCKS, 0.7F,
                        0.9F + pRandom.nextFloat() * 0.2F);
                pLevel.setBlock(pPos, pState.setValue(HATCH, i + 1), 2);
            } else {
                spawnAnimal(pLevel, pPos, pState);
            }
        }
    }

    public void spawnAnimal(ServerLevel pLevel, BlockPos pPos, BlockState pState) {
        pLevel.playSound(null, pPos, SoundEvents.TURTLE_EGG_HATCH, SoundSource.BLOCKS, 0.7F,
                0.9F + pLevel.random.nextFloat() * 0.2F);
        pLevel.removeBlock(pPos, false);
        pLevel.gameEvent(GameEvent.BLOCK_DESTROY, pPos, GameEvent.Context.of(pState));

        pLevel.levelEvent(2001, pPos, Block.getId(pState));

        int numberOfEggs = pState.getValue(EGGS);

        for (int j = 0; j < numberOfEggs; j++) {
            LITAnimal baby = (LITAnimal) entityType.get().create(pLevel);
            if (baby != null) {
                baby.setAge(-baby.getGrowthTicks());
                Vec3 vec3 = pPos.getCenter();
                baby.finalizeSpawn(pLevel, pLevel.getCurrentDifficultyAt(pPos), MobSpawnType.BREEDING, null, null);
                baby.moveTo(vec3.x(), vec3.y(), vec3.z(), Mth.wrapDegrees(pLevel.random.nextFloat() * 360.0F), 0.0F);
                pLevel.addFreshEntity(baby);
            }
        }
    }

    private boolean shouldUpdateHatchLevel(Level level, BlockPos ground) {
        return level.random.nextInt(500) == 0 && level.getBlockState(ground).is(validBlock);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        boolean flag = hatchBoost(level, pos);
        if (!level.isClientSide() && flag) {
            level.levelEvent(3009, pos, 0);
        }
        level.gameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Context.of(state));
    }

    public boolean hatchBoost(BlockGetter level, BlockPos pos) {
        return level.getBlockState(pos.below()).is(this.validBlock);
    }

}
