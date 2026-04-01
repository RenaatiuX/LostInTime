package com.ren.lostintime.common.init;

import com.mojang.serialization.Codec;
import com.ren.lostintime.LostInTime;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Optional;

public class MemoryModuleInit {
    public static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULE_TYPES = DeferredRegister.create(ForgeRegistries.MEMORY_MODULE_TYPES, LostInTime.MODID);

    public static final RegistryObject<MemoryModuleType<List<BlockPos>>> VISIBLE_BLOCKS = MEMORY_MODULE_TYPES.register("visible_blocks", () -> new MemoryModuleType<>(Optional.empty()));
    public static final RegistryObject<MemoryModuleType<LivingEntity>> GRABBED_PREY = MEMORY_MODULE_TYPES.register("grabbed_prey", () -> new MemoryModuleType<>(Optional.empty()));
    public static final RegistryObject<MemoryModuleType<Unit>> IN_LOVE = MEMORY_MODULE_TYPES.register("in_love", () -> new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE))));
    public static final RegistryObject<MemoryModuleType<Unit>> IS_SLEEPING = MEMORY_MODULE_TYPES.register("is_sleeping", () -> new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE))));
    public static final RegistryObject<MemoryModuleType<Unit>> IS_SITTING = MEMORY_MODULE_TYPES.register("is_sitting", () -> new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE))));
    public static final RegistryObject<MemoryModuleType<Unit>> SIT_ORDER = MEMORY_MODULE_TYPES.register("sit_order", () -> new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE))));
    public static final RegistryObject<MemoryModuleType<Unit>> FOLLOW_ORDER = MEMORY_MODULE_TYPES.register("follow_order", () -> new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE))));
    public static final RegistryObject<MemoryModuleType<Unit>> WANDER_ORDER = MEMORY_MODULE_TYPES.register("wander_order", () -> new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE))));
    public static final RegistryObject<MemoryModuleType<Unit>> FORCE_WAKE_UP = MEMORY_MODULE_TYPES.register("force_wake_up", () -> new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE))));
    public static final RegistryObject<MemoryModuleType<Unit>> SHOULD_SLEEP = MEMORY_MODULE_TYPES.register("should_wake_up", () -> new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE))));
    public static final RegistryObject<MemoryModuleType<Integer>> AQUATIC_PHASE_TIMER = MEMORY_MODULE_TYPES.register("aquatic_phase_timer", () -> new MemoryModuleType<>(Optional.of(Codec.INT)));
    public static final RegistryObject<MemoryModuleType<Boolean>> IS_WATER_PHASE = MEMORY_MODULE_TYPES.register("is_water_phase", () -> new MemoryModuleType<>(Optional.of(Codec.BOOL)));
    public static final RegistryObject<MemoryModuleType<Boolean>> IS_TRAVELING_TO_WATER = MEMORY_MODULE_TYPES.register("is_traveling_to_water", () -> new MemoryModuleType<>(Optional.of(Codec.BOOL)));
}

