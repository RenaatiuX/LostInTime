package com.ren.lostintime.common.entity.goal;

import com.ren.lostintime.common.config.Config;
import com.ren.lostintime.common.entity.util.IEggLayer;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.GameRules;

public class EggBreedGoal<T extends Animal & IEggLayer> extends BreedGoal {

    private final T animal;

    public EggBreedGoal(T animal, double pSpeedModifier) {
        super(animal, pSpeedModifier);
        this.animal = animal;
    }

    @Override
    public boolean canUse() {
        return super.canUse() && !animal.hasEgg();

    }

    @Override
    protected void breed() {
        ServerPlayer serverplayer = this.animal.getLoveCause();
        if (serverplayer == null && this.partner.getLoveCause() != null) {
            serverplayer = this.partner.getLoveCause();
        }
        if (serverplayer != null) {
            serverplayer.awardStat(Stats.ANIMALS_BRED);
            assert this.partner != null;
            CriteriaTriggers.BRED_ANIMALS.trigger(serverplayer, this.animal, this.partner, (AgeableMob)null);
        }

        int cooldown = Config.dodoBreedCooldown + level.random.nextInt(1500) - level.random.nextInt(3000);
        int eggTicks = Config.dodoEggCooldown + level.random.nextInt(1000) - level.random.nextInt(2000);

        this.animal.hatchEgg(eggTicks);

        this.animal.setAge(cooldown);
        this.partner.setAge(cooldown);
        this.animal.resetLove();
        this.partner.resetLove();
        RandomSource randomsource = this.animal.getRandom();
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            this.level.addFreshEntity(new ExperienceOrb(this.level, this.animal.getX(), this.animal.getY(), this.animal.getZ(),
                    randomsource.nextInt(7) + 1));
        }
    }
}
