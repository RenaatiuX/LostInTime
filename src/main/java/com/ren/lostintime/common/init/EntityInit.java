package com.ren.lostintime.common.init;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.creatures.*;
import com.ren.lostintime.common.entity.misc.LITBoat;
import com.ren.lostintime.common.entity.misc.LITChestBoat;
import com.ren.lostintime.common.entity.projectile.GuardianSpike;
import com.ren.lostintime.common.entity.projectile.LITThrownEgg;
import com.ren.lostintime.common.entity.projectile.ThrownKnife;
import com.ren.lostintime.common.entity.skeleton.FossilEntity;
import net.minecraft.world.entity.Entity;
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
    public static final RegistryObject<EntityType<Anomalocaris>> ANOMALOCARIS = registerWaterMob("anomalocaris",
            Anomalocaris::new, 0.8F, 0.5F);
    public static final RegistryObject<EntityType<Bothriolepis>> BOTHRIOLEPIS = registerWaterMob("bothriolepis",
            Bothriolepis::new, 0.8F, 0.5F);
    public static final RegistryObject<EntityType<Hylonomus>> HYLONOMUS = registerMob("hylonomus",
            Hylonomus::new, 0.4F, 0.4F);
    public static final RegistryObject<EntityType<Endoceras>> ENDOCERAS = registerWaterMob("endoceras",
            Endoceras::new, 1.0F, 1.0F);
    public static final RegistryObject<EntityType<Daeodon>> DAEODON = registerMob("daeodon",
            Daeodon::new, 1.2F, 1.5F);
    public static final RegistryObject<EntityType<Leptictidium>> LEPTICTIDIUM = registerMob("leptictidium",
            Leptictidium::new, 0.5F, 0.5F);
    public static final RegistryObject<EntityType<Scutosaurus>> SCUTOSAURUS = registerMob("scutosaurus",
            Scutosaurus::new, 1.0F, 1.0F);
    public static final RegistryObject<EntityType<Plesiosaurus>> PLESIOSAURUS = registerWaterMob("plesiosaurus",
            Plesiosaurus::new, 1.5F, 1.0F);
    public static final RegistryObject<EntityType<Mastodonsaurus>> MASTODONSAURUS = registerMob("mastodonsaurus",
            Mastodonsaurus::new, 1.0F, 1.0F);
    public static final RegistryObject<EntityType<Helicoprion>> HELICOPRION = registerWaterMob("helicoprion",
            Helicoprion::new, 1.8F, 1.6F);
    public static final RegistryObject<EntityType<Deinonychus>> DEINONYCHUS = registerMob("deinonychus",
            Deinonychus::new, 1.0F, 1.0F);
    public static final RegistryObject<EntityType<Pterygotus>> PTERYGOTUS = registerWaterMob("pterygotus",
            Pterygotus::new, 0.8F, 0.5F);

    public static final RegistryObject<EntityType<GuardianSpike>> GUARDIAN_SPIKE = registerProjectile("guardian_spike",
            GuardianSpike::new, 0.5F, 0.5F);
    public static final RegistryObject<EntityType<LITThrownEgg>> LIT_THROWN_EGG = registerProjectile("lit_thrown_egg",
            LITThrownEgg::new, 0.25F, 0.25F);
    public static final RegistryObject<EntityType<LITBoat>> LIT_BOAT = registerMiscEntity("lit_boat",
            LITBoat::new, 1.375F, 0.5625F);
    public static final RegistryObject<EntityType<LITChestBoat>> LIT_CHEST_BOAT = registerMiscEntity("lit_chest_boat",
            LITChestBoat::new, 1.375F, 0.5625F);
    public static final RegistryObject<EntityType<ThrownKnife>> THROWN_KNIFE = registerProjectile("thrown_knife",
            ThrownKnife::new, 0.5F, 0.5F);

    public static final RegistryObject<EntityType<FossilEntity>> LIT_SKELETON =
            registerMiscEntity("lit_skeleton", FossilEntity::new, 1.0f, 1.5f);

    public static <T extends Projectile> RegistryObject<EntityType<T>> registerProjectile(String name, EntityType.EntityFactory<T> entity,
                                                                                          float width, float height) {
        return ENTITIES.register(name,
                () -> EntityType.Builder.of(entity, MobCategory.MISC).sized(width, height).build(name));
    }

    public static <T extends Mob> RegistryObject<EntityType<T>> registerWaterMob(String name, EntityType.EntityFactory<T> entity,
                                                                            float width, float height) {
        return ENTITIES.register(name,
                () -> EntityType.Builder.of(entity, MobCategory.WATER_CREATURE).sized(width, height).build(name));
    }

    public static <T extends Mob> RegistryObject<EntityType<T>> registerMob(String name, EntityType.EntityFactory<T> entity,
                                                                            float width, float height) {
        return ENTITIES.register(name,
                () -> EntityType.Builder.of(entity, MobCategory.CREATURE).sized(width, height).build(name));
    }

    public static <T extends Entity> RegistryObject<EntityType<T>> registerMiscEntity(String name, EntityType.EntityFactory<T> entity,
                                                                                      float width, float height) {
        return ENTITIES.register(name, () -> EntityType.Builder.of(entity, MobCategory.MISC).sized(width, height).build(name));
    }
}
