package com.ren.lostintime.common.init;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.enchantments.FossilKnowledgeEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EnchantmentInit {

    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister
            .create(ForgeRegistries.ENCHANTMENTS, LostInTime.MODID);

    public static final RegistryObject<FossilKnowledgeEnchantment> FOSSIL_KNOWLEDGE =
            ENCHANTMENTS.register("fossil_knowledge", FossilKnowledgeEnchantment::new);
}
