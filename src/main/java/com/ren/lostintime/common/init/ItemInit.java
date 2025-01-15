package com.ren.lostintime.common.init;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.item.GoldenEyeItem;
import com.ren.lostintime.common.item.GuardianSpikeItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.function.Supplier;

public class ItemInit {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LostInTime.MODID);

    //MISC
    public static final RegistryObject<Item> AMBER = registerSimple("amber");
    public static final RegistryObject<Item> GUARDIAN_SPIKE = ITEMS.register("guardian_spike",
            () -> new GuardianSpikeItem(new Item.Properties().stacksTo(16)));

    //EQUIP
    public static final RegistryObject<Item> GOLDEN_EYE = ITEMS.register("golden_eye",
            () -> new GoldenEyeItem(new Item.Properties()));

    //FOOD
    public static final RegistryObject<Item> RAW_DODO = registerFood("raw_dodo", FoodInit.RAW_DODO);
    public static final RegistryObject<Item> COOKED_DODO = registerFood("cooked_dodo", FoodInit.COOKED_DODO);

    public static final RegistryObject<Item> MANGO = registerFood("mango", FoodInit.MANGO);


    //SPAWN EGG
    public static final RegistryObject<Item> DODO_SPAWN_EGG = registerSpawnEgg("dodo_spawn_egg",
            EntityInit.DODO, 3679516, 7164742);

    private static RegistryObject<Item> registerSimple(final String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties()));
    }

    public static RegistryObject<Item> registerFood(final String name, FoodProperties foodProperties) {
        return ITEMS.register(name, () -> new Item(new Item.Properties().food(foodProperties)));
    }

    public static RegistryObject<Item> registerSpawnEgg(final String name, Supplier<? extends EntityType<? extends Mob>>
            type, int backgroundColor, int highlightColor) {
        return ITEMS.register(name, () -> new ForgeSpawnEggItem(type, backgroundColor, highlightColor, new Item.Properties()));
    }
}
