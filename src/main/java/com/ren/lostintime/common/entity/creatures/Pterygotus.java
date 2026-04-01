package com.ren.lostintime.common.entity.creatures;

import com.mojang.serialization.Dynamic;
import com.ren.lostintime.common.entity.LITAnimal;
import com.ren.lostintime.common.entity.LITSemiAquaticAnimal;
import com.ren.lostintime.common.entity.LITWaterAnimal;
import com.ren.lostintime.common.entity.ai.PterygotusAi;
import com.ren.lostintime.common.entity.util.IItemEater;
import com.ren.lostintime.common.init.EntityInit;
import com.ren.lostintime.common.init.MemoryModuleInit;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class Pterygotus extends LITSemiAquaticAnimal implements GeoEntity, IItemEater {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public Pterygotus(EntityType<? extends LITAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    // ==========================================
    // ATTRIBUTES
    // ==========================================
    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 15.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    // ==========================================
    // BRAIN
    // ==========================================
    @Override
    protected Brain.Provider<Pterygotus> brainProvider() {
        return Brain.provider(PterygotusAi.MEMORY_TYPES, PterygotusAi.SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> pDynamic) {
        Brain<Pterygotus> brain = this.brainProvider().makeBrain(pDynamic);
        PterygotusAi.makeBrain(brain);
        return brain;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Brain<Pterygotus> getBrain() {
        return (Brain<Pterygotus>) super.getBrain();
    }

    @Override
    protected void customServerAiStep() {
        this.level().getProfiler().push("pterygotusBrain");
        this.getBrain().tick((ServerLevel) this.level(), this);
        this.level().getProfiler().pop();
        PterygotusAi.updateActivity(this);
        super.customServerAiStep();
    }

    // ==========================================
    // BREATHING
    // ==========================================
    @Override
    public int getMaxAirSupply() {
        return 2400;
    }

    // ==========================================
    // PASSENGER
    // ==========================================
    @Override
    protected boolean canAddPassenger(Entity pPassenger) {
        return this.getPassengers().size() < 3 && pPassenger instanceof Pterygotus baby && baby.isBaby();
    }

    @Override
    public double getPassengersRidingOffset() {
        return 0.6D;
    }

    @Override
    protected void positionRider(Entity pPassenger, Entity.MoveFunction pCallback) {
        super.positionRider(pPassenger, pCallback);
        if (this.hasPassenger(pPassenger)) {
            int index = this.getPassengers().indexOf(pPassenger);

            float yRot = this.yBodyRot * ((float)Math.PI / 180F);
            float zOffset = 0.6F - (index * 0.6F);

            float x = (float)(-Math.sin(yRot) * zOffset);
            float z = (float)(Math.cos(yRot) * zOffset);

            float y = (float)(this.getY() + this.getPassengersRidingOffset() + pPassenger.getMyRidingOffset());

            pCallback.accept(pPassenger, this.getX() + x, y, this.getZ() + z);

            pPassenger.setYBodyRot(this.yBodyRot);
            pPassenger.setYHeadRot(this.yHeadRot);
        }
    }

    // ==========================================
    // HURT AND PANIC IN THE BABIES
    // ==========================================
    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        boolean wasHurt = super.hurt(pSource, pAmount);

        if (wasHurt && !this.level().isClientSide) {
            if (this.isVehicle()) {
                List<Entity> passengers = List.copyOf(this.getPassengers());

                for (Entity passenger : passengers) {
                    passenger.stopRiding();
                    if (passenger instanceof Pterygotus baby) {
                        baby.getBrain().setMemory(MemoryModuleType.IS_PANICKING, true);

                        if (pSource.getEntity() instanceof LivingEntity attacker) {
                            baby.getBrain().setMemory(MemoryModuleType.HURT_BY_ENTITY, attacker);
                        }
                    }
                }
            }
        }
        return wasHurt;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return EntityInit.PTERYGOTUS.get().create(pLevel);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public boolean isFoodItem(ItemStack stack) {
        return stack.is(ItemTags.FISHES);
    }

    @Override
    protected void handleSemiAquaticNeeds() {
        if (this.timeOnLand > 2400 && this.timeOnLand % 20 == 0) {
            this.hurt(this.damageSources().drown(), 2.0F);
        }
    }

    @Override
    public int getWaterPhaseDuration() {
        return 3600;
    }

    @Override
    public int getLandPhaseDuration() {
        return this.getMaxAirSupply();
    }

    @Override
    public float getSpeed() {
        return this.isInWaterOrBubble()
                ? (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED)
                : super.getSpeed();
    }

    @Override
    public boolean canDrownInFluidType(FluidType type) {
        return false;
    }
}
