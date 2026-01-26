package com.ren.lostintime.client.model;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.creatures.Anomalocaris;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class AnomalocarisModel extends DefaultedEntityGeoModel<Anomalocaris> {

    public AnomalocarisModel() {
        super(new ResourceLocation(LostInTime.MODID, "anomalocaris"));
    }
}
