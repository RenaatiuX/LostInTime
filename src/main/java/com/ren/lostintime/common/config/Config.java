package com.ren.lostintime.common.config;

import com.google.common.collect.ImmutableMap;
import com.ren.lostintime.LostInTime;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = LostInTime.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue IDENTIFICATION_PROCESS_DURATION = BUILDER.push("machines").comment("defines the amount in ticks how long the identification table needs top process one item").defineInRange("identification_process_duration", 300, 1, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue DODO_BREED_COOLDOWN = BUILDER.pop().push("Entities").comment("this defines how much cooldown the dodo needs after breeding in ticks").defineInRange("dodo_breed_cooldown", 6000, 1, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.IntValue DODO_EGG_COOLDOWN = BUILDER.comment("this defines how much time the dodo needs after breeding until it lays an egg").defineInRange("dodo_egg_cooldown", 3000, 1, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.IntValue DODO_PECK_COOLDOWN = BUILDER.comment("this defines how much time the dodo needs after pecking until it can peck again").defineInRange("dodo_peck_cooldown", 2000, 1, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> DODO_GOLDEN_FOOD =
            BUILDER.comment("this defines the golden food the dodo can eat with its respective multiplier for the peck loot").comment("Format: itemid=multiplier")
                    .defineList(
                            "dodo_golden_food",
                            ImmutableMap.<Item, Float>builder()
                                    .put(Items.GOLDEN_CARROT, 2f)
                                    .put(Items.GOLDEN_APPLE, 4f)
                                    .put(Items.ENCHANTED_GOLDEN_APPLE, 8f)
                                    .build().entrySet().stream().map(e -> ForgeRegistries.ITEMS.getKey(e.getKey()) + "=" + e.getValue()).toList(),
                            Config::validateGoldenFood
                    );

    public static final ForgeConfigSpec SPEC = BUILDER.pop().build();

    public static int identificationTableProcessTime;
    public static int dodoBreedCooldown;
    public static int dodoEggCooldown;
    public static int dodoPeckCooldown;

    public static Map<Item, Float> goldenFoodMultipliers = ImmutableMap.of();


    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        identificationTableProcessTime = IDENTIFICATION_PROCESS_DURATION.get();
        dodoBreedCooldown = DODO_BREED_COOLDOWN.get();
        dodoEggCooldown = DODO_EGG_COOLDOWN.get();

        var mapBuilder = ImmutableMap.<Item, Float>builder();

        for (String entry : DODO_GOLDEN_FOOD.get()) {
            String[] split = entry.split("=");
            ResourceLocation id = new ResourceLocation(split[0]);
            float multiplier = Math.max(1f, Float.parseFloat(split[1]));

            Item item = ForgeRegistries.ITEMS.getValue(id);
            if (item != null) {
                mapBuilder.put(item, multiplier);
            }
        }

        goldenFoodMultipliers = mapBuilder.build();
        dodoPeckCooldown = DODO_PECK_COOLDOWN.get();

    }

    private static boolean validateGoldenFood(Object obj) {
        if (!(obj instanceof String s)) return false;

        String[] split = s.split("=");
        if (split.length != 2) return false;

        // Validate item id
        ResourceLocation id;
        try {
            id = new ResourceLocation(split[0]);
        } catch (ResourceLocationException e) {
            return false;
        }

        if (!ForgeRegistries.ITEMS.containsKey(id)) return false;

        // Validate float
        try {
            Float.parseFloat(split[1]);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }
}
