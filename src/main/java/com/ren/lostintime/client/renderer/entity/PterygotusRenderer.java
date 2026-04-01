package com.ren.lostintime.client.renderer.entity;

import com.ren.lostintime.client.model.entities.PterygotusModel;
import com.ren.lostintime.common.entity.creatures.Pterygotus;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PterygotusRenderer extends GeoEntityRenderer<Pterygotus> {

    public PterygotusRenderer(EntityRendererProvider.Context context) {
        super(context, new PterygotusModel());
        this.shadowRadius = 0.5F;
    }

    @Override
    public float getMotionAnimThreshold(Pterygotus animatable) {
        return 1.0E-6F;
    }

    @Override
    public RenderType getRenderType(Pterygotus animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutoutNoCull(texture);
    }
}
