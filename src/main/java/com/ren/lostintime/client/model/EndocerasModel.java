package com.ren.lostintime.client.model;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.creatures.Endoceras;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class EndocerasModel extends DefaultedEntityGeoModel<Endoceras> {
    public EndocerasModel() {
        super(new ResourceLocation(LostInTime.MODID, "endoceras"));
    }
}
