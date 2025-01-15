package com.ren.lostintime.common.init;

import com.ren.lostintime.LostInTime;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class GroupInit {

    public static final DeferredRegister<CreativeModeTab> GROUP = DeferredRegister.create(Registries.CREATIVE_MODE_TAB,
            LostInTime.MODID);

    public static final RegistryObject<CreativeModeTab> LOST_IN_TIME_GROUP = register("lost_in_time",
            CreativeModeTab.builder().icon(() -> new ItemStack(ItemInit.AMBER.get())));

    public static RegistryObject<CreativeModeTab> register(String name, CreativeModeTab.Builder tabBuilder){
        CreativeModeTab.Builder finalTabBuilder = tabBuilder.title(Component.translatable(makeDescriptionId(name)));
        return GROUP.register(name, finalTabBuilder::build);
    }

    public static String makeDescriptionId(String name){
        return "creative_tab." + LostInTime.MODID + "." + name;
    }
}
