package com.ren.lostintime.client.renderer.entity;

import com.ren.lostintime.client.model.entities.HelicoprionModel;
import com.ren.lostintime.common.entity.creatures.Helicoprion;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HelicoprionRenderer extends GeoEntityRenderer<Helicoprion> {

    public HelicoprionRenderer(EntityRendererProvider.Context context) {
        super(context, new HelicoprionModel());
        this.shadowRadius = 0.8F;
    }

    @Override
    public float getMotionAnimThreshold(Helicoprion animatable) {
        return 1.0E-6F;
    }

    @Override
    public RenderType getRenderType(Helicoprion animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutoutNoCull(texture);
    }
}
