package com.herbalistscraft.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

/**
 * The mod's five particles in one restrained class: a soft green medicinal spark, frost, ember,
 * toxic smoke and a falling dried leaf. Small, translucent, and slow enough to read.
 */
public class HerbalParticle extends TextureSheetParticle {
    public enum Kind {
        HERBAL(0.55F, 0.85F, 0.45F, -0.01F, 0.85F, 24),
        FROST(0.70F, 0.90F, 1.00F, 0.0F, 0.90F, 26),
        EMBER(1.00F, 0.65F, 0.25F, 0.01F, 0.95F, 20),
        TOXIC(0.45F, 0.55F, 0.30F, 0.0F, 0.70F, 34),
        LEAF(0.60F, 0.50F, 0.25F, 0.02F, 1.00F, 40);

        final float red;
        final float green;
        final float blue;
        final float gravity;
        final float alpha;
        final int lifetime;

        Kind(float red, float green, float blue, float gravity, float alpha, int lifetime) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.gravity = gravity;
            this.alpha = alpha;
            this.lifetime = lifetime;
        }
    }

    private final Kind kind;

    protected HerbalParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz,
                             Kind kind, SpriteSet sprites) {
        super(level, x, y, z, vx, vy, vz);
        this.kind = kind;
        this.setSize(0.08F, 0.08F);
        this.quadSize *= 0.7F + level.random.nextFloat() * 0.3F;
        this.lifetime = kind.lifetime + level.random.nextInt(8);
        this.gravity = kind.gravity;
        this.hasPhysics = false;
        this.alpha = kind.alpha;
        this.setColor(kind.red, kind.green, kind.blue);
        this.pickSprite(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        this.alpha = kind.alpha * (1.0F - (float) this.age / (float) this.lifetime) * 0.9F + 0.1F;
        this.yd *= 0.98D;
        if (kind == Kind.EMBER) {
            this.yd += 0.002D;
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    /** The provider every particle type in the mod shares; the kind is baked in. */
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        private final Kind kind;

        public Provider(SpriteSet sprites, Kind kind) {
            this.sprites = sprites;
            this.kind = kind;
        }

        @Override
        public HerbalParticle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                                             double vx, double vy, double vz, RandomSource random) {
            double spread = kind == Kind.TOXIC ? 0.02D : 0.05D;
            return new HerbalParticle(level, x, y, z,
                    vx + (random.nextDouble() - 0.5D) * spread,
                    vy + (random.nextDouble() - 0.5D) * spread,
                    vz + (random.nextDouble() - 0.5D) * spread, kind, sprites);
        }
    }

    /** Convenience for the potion-like effects that want a colour-matched cloud. */
    public static Kind kindFor(int argb) {
        int red = (argb >> 16) & 0xFF;
        int blue = argb & 0xFF;
        if (red > 180 && blue < 140) {
            return Kind.EMBER;
        }
        if (blue > 180 && red < 160) {
            return Kind.FROST;
        }
        return Kind.HERBAL;
    }
}
