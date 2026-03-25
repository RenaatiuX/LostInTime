package com.ren.lostintime.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import org.jetbrains.annotations.NotNull;

public class LITBaseParticle extends TextureSheetParticle {

    public final SpriteSet spriteSet;

    protected LITBaseParticle(ClientLevel pLevel, double pX, double pY, double pZ, SpriteSet pSpriteSet) {
        super(pLevel, pX, pY, pZ);
        this.spriteSet = pSpriteSet;
        this.gravity = 0.3F;
        this.hasPhysics = true;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }
}
