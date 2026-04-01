package com.ren.lostintime.client.renderer.entity;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.client.model.entities.DeinonychusModel;
import com.ren.lostintime.client.renderer.layers.LITBloodLayer;
import com.ren.lostintime.client.renderer.layers.LITEyesLayer;
import com.ren.lostintime.common.entity.creatures.Deinonychus;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DeinonychusRenderer extends GeoEntityRenderer<Deinonychus> {

    private static final ResourceLocation EYES_LAYER = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID,
            "textures/entity/deinonychus/deinonychus_glow.png");
    private static final ResourceLocation BLOOD_LAYER = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID,
            "textures/entity/deinonychus/deinonychus_blood.png");

    public DeinonychusRenderer(EntityRendererProvider.Context context) {
        super(context, new DeinonychusModel());
        this.shadowRadius = 0.5F;
        addRenderLayer(new LITEyesLayer<>(this, EYES_LAYER, true));
        addRenderLayer(new LITBloodLayer<>(this, BLOOD_LAYER));
    }

    @Override
    public float getMotionAnimThreshold(Deinonychus animatable) {
        return 1.0E-6F;
    }

    @Override
    public RenderType getRenderType(Deinonychus animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutoutNoCull(texture);
    }
}
