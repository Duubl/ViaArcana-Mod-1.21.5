package com.duubl.via_arcana.entities.projectiles;

import com.duubl.via_arcana.entities.ModEntities;
import com.duubl.via_arcana.items.weapons.magic.MagicWeapon;
import com.duubl.via_arcana.particles.ModParticles;
import com.duubl.via_arcana.sounds.ModSounds;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class BaseSpellProjectile extends AbstractHurtingProjectile {
    // Define all the data accessors
    private static final EntityDataAccessor<Float> DAMAGE = 
        SynchedEntityData.defineId(BaseSpellProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> PROJECTILE_SPEED = 
        SynchedEntityData.defineId(BaseSpellProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> KNOCKBACK = 
        SynchedEntityData.defineId(BaseSpellProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> CRITICAL_STRIKE_CHANCE = 
        SynchedEntityData.defineId(BaseSpellProjectile.class, EntityDataSerializers.FLOAT);

    private ParticleOptions impactParticle;
    private ParticleOptions trailParticle;

    public BaseSpellProjectile(EntityType<? extends BaseSpellProjectile> entityType, Level level) {
        super(entityType, level);
        this.trailParticle = ModParticles.COLORED_MAGIC_PARTICLE.get();
        this.impactParticle = ParticleTypes.CLOUD;
    }

    public BaseSpellProjectile(Level level, Player player, double xDir, double yDir, double zDir) {
        super(ModEntities.BASE_SPELL_PROJECTILE.get(), 
        player,
        new Vec3(xDir, yDir, zDir), 
        level);
        
        // Initialize default particles
        this.trailParticle = ModParticles.COLORED_MAGIC_PARTICLE.get();
        this.impactParticle = ParticleTypes.CLOUD;
        
        // Get all values from the wand
        if (player.getMainHandItem().getItem() instanceof MagicWeapon wand) {
            this.setDamage(wand.getDamage());
            this.setProjectileSpeed(wand.getProjectileSpeed());
            this.setKnockback(wand.getKnockback());
            this.setCriticalStrikeChance(wand.getCriticalStrikeChance());
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DAMAGE, 0.0f);
        builder.define(PROJECTILE_SPEED, 0.0f);
        builder.define(KNOCKBACK, 0.0f);
        builder.define(CRITICAL_STRIKE_CHANCE, 0.0f);
    }

    // Getters and setters for all properties
    public void setDamage(float damage) {
        this.entityData.set(DAMAGE, damage);
    }

    public float getDamage() {
        return this.entityData.get(DAMAGE);
    }

    public void setProjectileSpeed(float speed) {
        this.entityData.set(PROJECTILE_SPEED, speed);
    }

    public float getProjectileSpeed() {
        return this.entityData.get(PROJECTILE_SPEED);
    }

    public void setKnockback(float knockback) {
        this.entityData.set(KNOCKBACK, knockback);
    }

    public float getKnockback() {
        return this.entityData.get(KNOCKBACK);
    }

    public void setCriticalStrikeChance(float chance) {
        this.entityData.set(CRITICAL_STRIKE_CHANCE, chance);
    }

    public float getCriticalStrikeChance() {
        return this.entityData.get(CRITICAL_STRIKE_CHANCE);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level() instanceof ServerLevel serverLevel) {
            if (this.getOwner() instanceof LivingEntity owner && result.getEntity() instanceof LivingEntity target) {
                float randomPitch = 0.75f + this.level().getRandom().nextFloat() * 0.5f; // Random between 0.75 and 1.25
                this.playSound(ModSounds.SPELL_HIT_3.get(), 1.0f, randomPitch);

                // Calculate if this hit is a critical strike
                boolean isCritical = this.level().getRandom().nextFloat() < this.getCriticalStrikeChance();
                float damage = isCritical ? this.getDamage() * 2.0f : this.getDamage();

                DamageSource damageSource = this.damageSources().indirectMagic(this, owner);
                target.hurtServer(serverLevel, damageSource, damage);

                // Calculate knockback direction
                Vec3 knockbackDir = this.getDeltaMovement().normalize();

                // Apply knockback with the wand's knockback value
                target.setDeltaMovement(
                    target.getDeltaMovement().add(
                        knockbackDir.x * this.getKnockback(),
                        knockbackDir.y * this.getKnockback(),
                        knockbackDir.z * this.getKnockback()
                    )
                );

                // Spawn critical hit particles if it was a critical strike
                if (isCritical) {
                    double x = target.getX();
                    double y = target.getY() + target.getEyeHeight();
                    double z = target.getZ();
                    serverLevel.sendParticles(ParticleTypes.CRIT, 
                        x, y, z, 15, 0.5D, 0.5D, 0.5D, 0.1D);
                }
            }
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        
        // Play hit sound for block hits with random pitch
        float randomPitch = 0.75f + this.level().getRandom().nextFloat() * 0.5f; // Random between 0.75 and 1.25
        this.playSound(ModSounds.SPELL_HIT_3.get(), 1.0f, randomPitch);
            
        if (this.level() instanceof ServerLevel serverLevel) {
            double x = this.getX();
            double y = this.getY() + 0.2;
            double z = this.getZ();
            serverLevel.sendParticles(getImpactParticle(), x, y, z, 25, 0.25D, 0.25D, 0.25D, 0.05D);
            this.discard();
        }
    }
    
    @Override
    public ParticleOptions getTrailParticle() {
        return this.trailParticle;
    }

    public ParticleOptions getImpactParticle() {
        return this.impactParticle;
    }

    public void setImpactParticle(ParticleOptions particle) {
        this.impactParticle = particle;
    }

    public void setTrailParticle(ParticleOptions particle) {
        this.trailParticle = particle;
    }

    private void createParticleTrail() {
        ParticleOptions particleoptions = this.getTrailParticle();
        Vec3 vec3 = this.position();
        if (particleoptions != null) {
            // Spawn multiple particles with slight position variations
            for (int i = 0; i < 3; i++) {
                double offsetX = (this.level().getRandom().nextDouble() - 0.5) * 0.1;
                double offsetY = (this.level().getRandom().nextDouble() - 0.5) * 0.1;
                double offsetZ = (this.level().getRandom().nextDouble() - 0.5) * 0.1;
                this.level().addParticle(particleoptions, 
                    vec3.x + offsetX, 
                    vec3.y + 0.5 + offsetY, 
                    vec3.z + offsetZ, 
                    0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.createParticleTrail();
        }
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }
} 