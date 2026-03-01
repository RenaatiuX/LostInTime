package com.ren.lostintime.client.render.entity;

import com.ren.lostintime.client.model.entities.HylonomusModel;
import com.ren.lostintime.common.entity.creatures.Hylonomus;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HylonomusRender extends GeoEntityRenderer<Hylonomus> {

    public HylonomusRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HylonomusModel());
        this.shadowRadius = 0.5F;
    }

    @Override
    public float getMotionAnimThreshold(Hylonomus animatable) {
        return 1.0E-6F;
    }

    @Override
    public RenderType getRenderType(Hylonomus animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutoutNoCull(texture);
    }
}
