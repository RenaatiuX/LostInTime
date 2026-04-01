package com.ren.lostintime.client.renderer.entity;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.client.model.entities.DaeodonModel;
import com.ren.lostintime.client.renderer.layers.LITEyesLayer;
import com.ren.lostintime.common.entity.creatures.Daeodon;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DaeodonRenderer extends GeoEntityRenderer<Daeodon> {

    private static final ResourceLocation EYES_LAYER = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID,
            "textures/entity/daeodon/daeodon_glow.png");

    public DaeodonRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DaeodonModel());
        this.shadowRadius = 0.8F;
        this.addRenderLayer(new LITEyesLayer<>(this, EYES_LAYER, false));
    }

    @Override
    public float getMotionAnimThreshold(Daeodon animatable) {
        return 1.0E-6F;
    }

    @Override
    public RenderType getRenderType(Daeodon animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutoutNoCull(texture);
    }
}
