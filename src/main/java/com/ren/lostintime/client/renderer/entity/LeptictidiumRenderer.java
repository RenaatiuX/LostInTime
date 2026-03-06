package com.ren.lostintime.client.renderer.entity;

import com.ren.lostintime.client.model.entities.LeptictidiumModel;
import com.ren.lostintime.common.entity.creatures.Leptictidium;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class LeptictidiumRenderer extends GeoEntityRenderer<Leptictidium> {

    public LeptictidiumRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new LeptictidiumModel());
        this.shadowRadius = 0.3F;
    }

    @Override
    public float getMotionAnimThreshold(Leptictidium animatable) {
        return 1.0E-6F;
    }

    @Override
    public RenderType getRenderType(Leptictidium animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutoutNoCull(texture);
    }
}
