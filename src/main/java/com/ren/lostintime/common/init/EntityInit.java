package com.ren.lostintime.common.init;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.creatures.Dodo;
import com.ren.lostintime.common.entity.projectile.GuardianSpike;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityInit {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES,
            LostInTime.MODID);

    public static final RegistryObject<EntityType<Dodo>> DODO = registerMob("dodo", Dodo::new,
            0.6F, 1.0F);

    public static final RegistryObject<EntityType<GuardianSpike>> GUARDIAN_SPIKE = registerProjectile("guardian_spike",
            GuardianSpike::new);


    public static <T extends Projectile> RegistryObject<EntityType<T>> registerProjectile(String name, EntityType.EntityFactory<T> entity) {
        return ENTITIES.register(name,
                () -> EntityType.Builder.of(entity, MobCategory.MISC).sized(0.5F, 0.5F).build(name));
    }

    public static <T extends Mob> RegistryObject<EntityType<T>> registerMob(String name, EntityType.EntityFactory<T> entity,
                                                                            float width, float height) {
        return ENTITIES.register(name,
                () -> EntityType.Builder.of(entity, MobCategory.CREATURE).sized(width, height).build(name));
    }
}
