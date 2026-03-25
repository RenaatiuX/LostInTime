package com.ren.lostintime.common.init;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.effect.BleedingEffect;
import com.ren.lostintime.common.effect.FractureEffect;
import com.ren.lostintime.common.effect.InfectionEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MobEffectInit {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister
            .create(ForgeRegistries.MOB_EFFECTS, LostInTime.MODID);

    public static final RegistryObject<MobEffect> BLEEDING = MOB_EFFECTS.register("bleeding",
            BleedingEffect::new);
    public static final RegistryObject<MobEffect> INFECTION = MOB_EFFECTS.register("infection",
            InfectionEffect::new);
    public static final RegistryObject<MobEffect> FRACTURE = MOB_EFFECTS.register("fracture",
            FractureEffect::new);

}
