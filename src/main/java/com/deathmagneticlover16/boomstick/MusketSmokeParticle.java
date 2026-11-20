package com.deathmagneticlover16.boomstick;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public final class MusketSmokeParticle extends TextureSheetParticle {
    private final float startingSize;

    private MusketSmokeParticle(ClientLevel level, double x, double y, double z,
                                double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.xd = xSpeed * 0.35D;
        this.yd = 0.012D + Math.abs(ySpeed) * 0.12D;
        this.zd = zSpeed * 0.35D;
        this.friction = 0.985F;
        this.lifetime = 110 + level.random.nextInt(71);
        this.startingSize = 0.55F + level.random.nextFloat() * 0.45F;
        this.quadSize = startingSize;
        float shade = 0.82F + level.random.nextFloat() * 0.16F;
        this.setColor(shade, shade, shade);
        this.alpha = 0.32F;
        this.pickSprite(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        float life = (float) age / lifetime;
        this.quadSize = startingSize * (1.0F + life * 2.4F);
        if (life > 0.80F) this.alpha = 0.32F * (1.0F - life) / 0.20F;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static final class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new MusketSmokeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        }
    }
}
