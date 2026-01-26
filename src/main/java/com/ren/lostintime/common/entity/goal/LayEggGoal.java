package com.ren.lostintime.common.entity.goal;

import com.ren.lostintime.common.entity.util.IEggLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class LayEggGoal<E extends AgeableMob & IEggLayer> extends MoveToBlockGoal {

    private final E animal;

    public LayEggGoal(E pEggLayer, double pSpeedModifier) {
        super(pEggLayer, pSpeedModifier, 16);
        this.animal = pEggLayer;
    }

    @Override
    public boolean canUse() {
        return animal.hasEgg() && animal.canLayEgg() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        System.out.println("getting here");
        return animal.hasEgg() && animal.canLayEgg() && super.canContinueToUse();
    }

    @Override
    public void tick() {
        super.tick();
        BlockPos blockpos = this.animal.blockPosition();
        System.out.println("are we here");
        if (!this.animal.isInWater() && isReachedTarget()) {
            Level level = this.animal.level();
            level.playSound(null, blockpos, SoundEvents.TURTLE_LAY_EGG, SoundSource.BLOCKS, 0.3F, 0.9F + level.random.nextFloat() * 0.2F);
            BlockPos blockpos1 = this.blockPos.above();
            BlockState blockstate = animal.getEgg();
            level.setBlock(blockpos1, blockstate, 3);
            level.gameEvent(GameEvent.BLOCK_PLACE, blockpos1, GameEvent.Context.of(this.animal, blockstate));
            animal.onEggLayed(blockpos1);
            //ensuring a slight cooldown after laying the egg for breeding
            if (this.animal.getAge() <= 1000){
                this.animal.setAge(1000);
            }

        }
    }

    @Override
    protected boolean isValidTarget(LevelReader pLevel, BlockPos pPos) {
        return pLevel.isEmptyBlock(pPos.above()) && animal.validEggPosition(pPos, pLevel.getBlockState(pPos));
    }
}
