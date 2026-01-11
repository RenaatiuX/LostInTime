package com.ren.lostintime.common.entity.goal;

import com.ren.lostintime.common.entity.LITAnimal;
import com.ren.lostintime.common.entity.util.IEggLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.function.Supplier;

public class LayEggGoal extends MoveToBlockGoal {

    private final LITAnimal animal;
    private final TagKey<Block> nestBlock;
    private final Supplier<Block> eggBlock;
    private final double distance;

    public LayEggGoal(LITAnimal pEggLayer, double pSpeedModifier, TagKey<Block> pNestBlock, Supplier<Block> pEgg, double distance) {
        super(pEggLayer, pSpeedModifier, 16);
        this.animal = pEggLayer;
        this.nestBlock = pNestBlock;
        this.eggBlock = pEgg;
        this.distance = distance;
    }

    @Override
    public boolean canUse() {
        if (animal instanceof IEggLayer eggLayer) {
            return eggLayer.hasEgg() && super.canUse();
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if (animal instanceof IEggLayer eggLayer) {
            return eggLayer.hasEgg() && super.canContinueToUse();
        }
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        BlockPos blockpos = this.animal.blockPosition();
        if (!this.animal.isInWater() && this.animal instanceof IEggLayer eggLayer) {
            if (eggLayer.getLayEggCounter() < 1) {
                eggLayer.setLayingEgg(true);
            } else if (eggLayer.getLayEggCounter() > this.adjustedTickDelay(100)) {
                Level level = this.animal.level();
                level.playSound(null, blockpos, SoundEvents.TURTLE_LAY_EGG, SoundSource.BLOCKS, 0.3F, 0.9F + level.random.nextFloat() * 0.2F);
                BlockPos blockpos1 = this.blockPos.above();
                BlockState blockstate = eggBlock.get().defaultBlockState();
                level.setBlock(blockpos1, blockstate, 3);
                level.gameEvent(GameEvent.BLOCK_PLACE, blockpos1, GameEvent.Context.of(this.animal, blockstate));
                eggLayer.setHasEgg(false);
                eggLayer.setLayingEgg(false);
                eggLayer.onEggLaid();
                this.animal.setInLoveTime(600);
            }

            if (eggLayer.isLayingEgg()) {
                int prevLayEggCounter = eggLayer.getLayEggCounter();
                eggLayer.setLayEggCounter(++prevLayEggCounter);
            }
        }
    }

    @Override
    protected boolean isValidTarget(LevelReader pLevel, BlockPos pPos) {
        return pLevel.isEmptyBlock(pPos.above()) && pLevel.getBlockState(pPos).is(nestBlock);
    }
}
