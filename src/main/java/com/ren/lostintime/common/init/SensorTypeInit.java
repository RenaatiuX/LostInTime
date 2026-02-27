package com.ren.lostintime.common.init;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.ai.InLoveSensor;
import com.ren.lostintime.common.entity.ai.SleepSensor;
import com.ren.lostintime.common.entity.ai.VisibleBlockSensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SensorTypeInit {
    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES = DeferredRegister.create(ForgeRegistries.SENSOR_TYPES, LostInTime.MODID);

    public static final RegistryObject<SensorType<VisibleBlockSensor>> VISIBLE_BLOCKS_SENSOR = SENSOR_TYPES.register("visible_blocks_sensor", () -> new SensorType<>(VisibleBlockSensor::new));
    public static final RegistryObject<SensorType<InLoveSensor>> IN_LOVE_SENSOR = SENSOR_TYPES.register("in_love_sensor", () -> new SensorType<>(InLoveSensor::new));
    public static final RegistryObject<SensorType<SleepSensor>> SLEEP_SENSOR = SENSOR_TYPES.register("sleep_sensor", () -> new SensorType<>(SleepSensor::new));
}
