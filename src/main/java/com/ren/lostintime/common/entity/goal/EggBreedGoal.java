package com.ren.lostintime.common.entity.goal;

import com.ren.lostintime.common.entity.LITAnimal;
import com.ren.lostintime.common.entity.creatures.Dodo;
import com.ren.lostintime.common.entity.util.IEggLayer;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.level.GameRules;

public class EggBreedGoal extends BreedGoal {

    private final LITAnimal litAnimal;

    public EggBreedGoal(LITAnimal litAnimal, double pSpeedModifier) {
        super(litAnimal, pSpeedModifier);
        this.litAnimal = litAnimal;
    }

    @Override
    public boolean canUse() {
        if (this.litAnimal instanceof IEggLayer eggLayer)
            return super.canUse() && !eggLayer.hasEgg();

        return false;
    }

    @Override
    protected void breed() {
        ServerPlayer serverplayer = this.litAnimal.getLoveCause();
        if (serverplayer == null && this.partner.getLoveCause() != null) {
            serverplayer = this.partner.getLoveCause();
        }
        if (serverplayer != null) {
            serverplayer.awardStat(Stats.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger(serverplayer, this.litAnimal, this.partner, (AgeableMob)null);
        }

        if (this.litAnimal instanceof IEggLayer eggLayer) {
            eggLayer.setHasEgg(true);
        }
        this.litAnimal.setAge(6000);
        this.partner.setAge(6000);
        this.litAnimal.resetLove();
        this.partner.resetLove();
        RandomSource randomsource = this.litAnimal.getRandom();
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            this.level.addFreshEntity(new ExperienceOrb(this.level, this.litAnimal.getX(), this.litAnimal.getY(), this.litAnimal.getZ(),
                    randomsource.nextInt(7) + 1));
        }
    }
}
