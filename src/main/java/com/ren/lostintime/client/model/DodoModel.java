package com.ren.lostintime.client.model;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.creatures.Dodo;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class DodoModel extends DefaultedEntityGeoModel<Dodo> {

    public DodoModel() {
        super(new ResourceLocation(LostInTime.MODID, "dodo"));
    }
}
