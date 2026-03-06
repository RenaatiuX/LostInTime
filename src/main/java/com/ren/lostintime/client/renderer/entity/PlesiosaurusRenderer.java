package com.ren.lostintime.client.renderer.entity;

import com.ren.lostintime.client.model.entities.PlesiosaurusModel;
import com.ren.lostintime.common.entity.creatures.Plesiosaurus;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PlesiosaurusRenderer extends GeoEntityRenderer<Plesiosaurus> {

    public PlesiosaurusRenderer(EntityRendererProvider.Context context) {
        super(context, new PlesiosaurusModel());
        this.shadowRadius = 0.8F;
    }

    @Override
    public float getMotionAnimThreshold(Plesiosaurus animatable) {
        return 1.0E-6F;
    }

    @Override
    public RenderType getRenderType(Plesiosaurus animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutoutNoCull(texture);
    }
}
