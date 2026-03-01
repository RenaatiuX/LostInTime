package com.ren.lostintime.client.model.entities;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.creatures.Bothriolepis;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class BothriolepisModel extends DefaultedEntityGeoModel<Bothriolepis> {

    public BothriolepisModel() {
        super(ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "bothriolepis"));
    }
}
