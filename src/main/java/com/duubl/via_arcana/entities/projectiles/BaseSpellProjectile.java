package com.duubl.via_arcana.entities.projectiles;

import com.duubl.via_arcana.entities.ModEntities;
import com.duubl.via_arcana.init.ModAttributes;
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
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class BaseSpellProjectile extends AbstractHurtingProjectile {

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
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level() instanceof ServerLevel serverLevel) {
            if (this.getOwner() instanceof LivingEntity owner && result.getEntity() instanceof LivingEntity target) {
                float randomPitch = 0.75f + this.level().getRandom().nextFloat() * 0.5f; // Random between 0.75 and 1.25
                this.playSound(ModSounds.SPELL_HIT_3.get(), 1.0f, randomPitch);

                // Get the weapon the owner is holding
                ItemStack weaponStack = null;
                if (owner instanceof Player player) {
                    weaponStack = player.getMainHandItem();
                }

                // Calculate if this hit is a critical strike using the proper method
                double critChance = getCriticalStrikeChanceFromWeapon(weaponStack);
                boolean isCritical = this.level().getRandom().nextFloat() < critChance;
                
                // Get damage from the weapon using the proper method
                double damage = getDamageFromWeapon(weaponStack);
                if (isCritical) {
                    damage *= 2.0; // Critical hit doubles damage
                }

                DamageSource damageSource = this.damageSources().indirectMagic(this, owner);
                target.hurtServer(serverLevel, damageSource, (float) damage);

                // Get knockback from the weapon using the proper method
                double knockback = getKnockbackFromWeapon(weaponStack);
                if (knockback > 0) {
                    target.knockback((float) knockback, owner.getX() - target.getX(), owner.getZ() - target.getZ());
                }

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

    public double getDamageFromWeapon(ItemStack weaponStack) {
        if (weaponStack == null || weaponStack.isEmpty()) {
            return 2.5; // Default damage
        }
        
        final double[] totalDamage = {0.0};
        
        weaponStack.forEachModifier(EquipmentSlotGroup.MAINHAND, (attribute, modifier) -> {
            if (attribute.is(ModAttributes.MAGIC_DAMAGE)) {
                totalDamage[0] += modifier.amount();
            }
        });
        
        return totalDamage[0] > 0 ? totalDamage[0] : 2.5; // Fallback to default if no damage found
    }

    public double getKnockbackFromWeapon(ItemStack weaponStack) {
        if (weaponStack == null || weaponStack.isEmpty()) {
            return 0.5; // Default knockback
        }
        
        final double[] totalKnockback = {0.0};
        
        weaponStack.forEachModifier(EquipmentSlotGroup.MAINHAND, (attribute, modifier) -> {
            if (attribute.is(ModAttributes.KNOCKBACK)) {
                totalKnockback[0] += modifier.amount();
            }
        });
        
        return totalKnockback[0];
    }

    public double getCriticalStrikeChanceFromWeapon(ItemStack weaponStack) {
        if (weaponStack == null || weaponStack.isEmpty()) {
            return 0.0; // Default no critical chance
        }
        
        final double[] totalCritChance = {0.0};
        
        weaponStack.forEachModifier(EquipmentSlotGroup.MAINHAND, (attribute, modifier) -> {
            if (attribute.is(ModAttributes.CRITICAL_STRIKE_CHANCE)) {
                totalCritChance[0] += modifier.amount();
            }
        });
        
        return totalCritChance[0];
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