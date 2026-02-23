package com.ren.lostintime.common.init;

import com.ren.lostintime.LostInTime;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AttributeInit {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, LostInTime.MODID);

    public static final RegistryObject<Attribute> MAX_HUNGER = ATTRIBUTES.register("max_hunger",
            () -> new RangedAttribute("attribute.name.lostintime.max_hunger", 100.0D, 0.0D, 1024.0D).setSyncable(true));
}
