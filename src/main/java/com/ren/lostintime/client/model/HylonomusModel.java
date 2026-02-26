package com.ren.lostintime.client.model;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.creatures.Hylonomus;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class HylonomusModel extends DefaultedEntityGeoModel<Hylonomus> {

    public HylonomusModel() {
        super(ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "hylonomus"));
    }
}
