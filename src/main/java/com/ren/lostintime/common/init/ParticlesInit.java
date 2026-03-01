package com.ren.lostintime.common.init;

import com.ren.lostintime.LostInTime;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ParticlesInit {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPE =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, LostInTime.MODID);

    public static final RegistryObject<SimpleParticleType> SLEEPING_PARTICLES =
            PARTICLE_TYPE.register("sleeping", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> BLEEDING_DROPLET =
            PARTICLE_TYPE.register("bleeding_droplet", () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> BLEEDING_STREAM =
            PARTICLE_TYPE.register("bleeding_stream", () -> new SimpleParticleType(false));

}
