package com.ren.lostintime.common.config;

import com.ren.lostintime.LostInTime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = LostInTime.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

   private static final ForgeConfigSpec.IntValue IDENTIFICATION_PROCESS_DURATION = BUILDER.push("machines").comment("defines the amount in ticks how long the identification table needs top process one item").defineInRange("identification_process_duration", 300, 1, Integer.MAX_VALUE);

   private static final ForgeConfigSpec.IntValue DODO_BREED_COOLDOWN = BUILDER.pop().push("Entities").comment("this defines how much cooldown the dodo needs after breeding in ticks").defineInRange("dodo_breed_cooldown", 60000, 1, Integer.MAX_VALUE);
   private static final ForgeConfigSpec.IntValue DODO_EGG_COOLDOWN = BUILDER.comment("this defines how much time the dodo needs after breeding until it lays an egg").defineInRange("dodo_egg_cooldown", 30000, 1, Integer.MAX_VALUE);

    public static final ForgeConfigSpec SPEC = BUILDER.pop().build();

    public static int identificationTableProcessTime;

    public static int dodoBreedCooldown;
    public static int dodoEggCooldown;


    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        identificationTableProcessTime = IDENTIFICATION_PROCESS_DURATION.get();
        dodoBreedCooldown = DODO_BREED_COOLDOWN.get();
        dodoEggCooldown = DODO_EGG_COOLDOWN.get();
    }
}
