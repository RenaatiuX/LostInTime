package com.ren.lostintime.client.render.entity;

import com.ren.lostintime.client.model.AnomalocarisModel;
import com.ren.lostintime.common.entity.creatures.Anomalocaris;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AnomalocarisRender extends GeoEntityRenderer<Anomalocaris> {

    public AnomalocarisRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new AnomalocarisModel());
        this.shadowRadius = 0.5F;
    }

    @Override
    public float getMotionAnimThreshold(Anomalocaris animatable) {
        return 1.0E-6F;
    }

    @Override
    public RenderType getRenderType(Anomalocaris animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutoutNoCull(texture);
    }
}
