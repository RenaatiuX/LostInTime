package com.ren.lostintime.common.init;

import com.mojang.serialization.Codec;
import com.ren.lostintime.LostInTime;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
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
}
