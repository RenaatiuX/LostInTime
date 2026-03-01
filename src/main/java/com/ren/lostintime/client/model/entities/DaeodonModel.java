package com.ren.lostintime.client.model.entities;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.creatures.Daeodon;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class DaeodonModel extends DefaultedEntityGeoModel<Daeodon> {

    public DaeodonModel() {
        super(ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "daeodon"));
    }

    @Override
    public ResourceLocation getTextureResource(Daeodon animatable) {
        return ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/entity/daeodon/daeodon.png");
    }
}
