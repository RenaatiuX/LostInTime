package com.ren.lostintime.client.model.entities;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.creatures.Helicoprion;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class HelicoprionModel extends DefaultedEntityGeoModel<Helicoprion> {

    public HelicoprionModel() {
        super(ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "helicoprion"));
    }

    @Override
    public void setCustomAnimations(Helicoprion animatable, long instanceId, AnimationState<Helicoprion> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        // 1. Buscamos el hueso principal de tu modelo (¡Asegúrate de que se llame así en Blockbench!)
        CoreGeoBone rootBone = this.getAnimationProcessor().getBone("helicoprion");

        if (rootBone != null) {
            // 2. Calculamos el giro suave usando los frames del juego
            float partialTick = animationState.getPartialTick();
            float smoothPitch = Mth.lerp(partialTick, animatable.prevBreachPitch, animatable.breachPitch);

            // 3. Aplicamos el giro al hueso
            if (animatable.breachPitch != 0.0F || animatable.prevBreachPitch != 0.0F) {

                // GeckoLib usa Radianes en lugar de Grados para los huesos, así que hacemos la conversión matemática.
                // IMPORTANTE: Si ves que el tiburón gira hacia ABAJO cuando debería ir hacia ARRIBA,
                // cambia el signo a negativo: -(smoothPitch * ((float)Math.PI / 180F))
                rootBone.setRotX(-(smoothPitch * ((float)Math.PI / 180F)));
            }
        }
    }
}
