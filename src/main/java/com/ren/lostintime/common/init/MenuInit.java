package com.ren.lostintime.common.init;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.menu.IdentificationMenu;
import com.ren.lostintime.common.menu.SoulExtractorMenu;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MenuInit {

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, LostInTime.MODID);

    public static final RegistryObject<MenuType<IdentificationMenu>> IDENTIFICATION_TABLE_MENU = registerMenuType(
            "identification_table_menu", IdentificationMenu::new);

    public static final RegistryObject<MenuType<SoulExtractorMenu>> SOUL_EXTRACTOR_MENU = registerMenuType(
            "soul_extractor_menu", SoulExtractorMenu::new);

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(String key, MenuType.MenuSupplier<T> factory) {
        return MENUS.register(key, () -> new MenuType<>(factory, FeatureFlags.VANILLA_SET));
    }
}
