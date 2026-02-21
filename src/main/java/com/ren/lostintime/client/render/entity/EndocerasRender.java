package com.ren.lostintime.client.render.entity;

import com.ren.lostintime.client.model.EndocerasModel;
import com.ren.lostintime.common.entity.creatures.Endoceras;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EndocerasRender extends GeoEntityRenderer<Endoceras> {

    public EndocerasRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new EndocerasModel());
        this.shadowRadius = 0.5F;
    }

    /*@Override
    public float getMotionAnimThreshold(Endoceras animatable) {
        return 1.0E-6F;
    }*/

    @Override
    public RenderType getRenderType(Endoceras animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutoutNoCull(texture);
    }
}
