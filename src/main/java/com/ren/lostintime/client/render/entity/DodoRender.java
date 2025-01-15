package com.ren.lostintime.client.render.entity;

import com.ren.lostintime.client.model.DodoModel;
import com.ren.lostintime.common.entity.creatures.Dodo;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DodoRender extends GeoEntityRenderer<Dodo> {

    public DodoRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DodoModel());
        this.shadowRadius = 0.5F;
    }
}
