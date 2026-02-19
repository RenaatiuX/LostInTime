package com.ren.lostintime.client.render.entity;

import com.ren.lostintime.client.model.BothriolepisModel;
import com.ren.lostintime.common.entity.creatures.Bothriolepis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BothriolepisRender extends GeoEntityRenderer<Bothriolepis> {

    public BothriolepisRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BothriolepisModel());
        this.shadowRadius = 0.5F;
    }

    @Override
    public float getMotionAnimThreshold(Bothriolepis animatable) {
        return 1.0E-6F;
    }

    @Override
    public RenderType getRenderType(Bothriolepis animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutoutNoCull(texture);
    }
}
