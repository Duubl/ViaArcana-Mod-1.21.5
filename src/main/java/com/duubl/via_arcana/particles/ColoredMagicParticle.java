package com.duubl.via_arcana.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class ColoredMagicParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;
    private final float initialScale;
    
    public ColoredMagicParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, float r, float g, float b, float scale, SpriteSet spriteSet) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.age = 0; // Ensure we start at the first frame
        this.setColor(r, g, b);
        this.setLifetime(20); // 5 frames × 4 ticks per frame = 20 ticks total
        this.gravity = 0.0F;
        this.spriteSet = spriteSet;
        this.setSpriteFromAge(spriteSet);
        this.alpha = 0.8F;
        this.initialScale = scale;
        this.quadSize = scale;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.spriteSet);
        
        // Calculate scale based on remaining lifetime
        float lifeRatio = 1.0F - (float)this.age / (float)this.lifetime;
        this.quadSize = this.initialScale * lifeRatio;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT; // Change to translucent for better visibility
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;
        private static float defaultR = 1.0F;
        private static float defaultG = 1.0F;
        private static float defaultB = 1.0F;
        private static float defaultScale = 0.25F;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public static void setColor(float r, float g, float b) {
            defaultR = r;
            defaultG = g;
            defaultB = b;
        }

        public static void setScale(float scale) {
            defaultScale = scale;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new ColoredMagicParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, defaultR, defaultG, defaultB, defaultScale, this.spriteSet);
        }
    }
}
