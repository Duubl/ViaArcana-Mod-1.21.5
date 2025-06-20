package com.duubl.via_arcana.items.weapons.magic;

import com.duubl.via_arcana.entities.projectiles.BaseSpellProjectile;
import com.duubl.via_arcana.magic.ManaComponent;
import com.duubl.via_arcana.magic.ManaComponentAttachment;
import com.duubl.via_arcana.network.packets.ManaUpdatePacket;
import com.duubl.via_arcana.sounds.ModSounds;
import com.duubl.via_arcana.init.ModAttributes;
import com.duubl.via_arcana.particles.ColoredMagicParticle;
import com.duubl.via_arcana.particles.ModParticles;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.phys.Vec3;

public class WandOfSplinters extends MagicWeapon {

    private static final float BASE_DAMAGE = 2.5f;
    private static final float BASE_PROJECTILE_SPEED = 1.0f;
    private static final float BASE_KNOCKBACK = 0.5f;
    private static final float BASE_CRITICAL_STRIKE_CHANCE = 0.05f;
    private static final float BASE_MANA_COST = 5f;
    private static final float BASE_CAST_SPEED = 60f;

    private float r = 0.282F;
    private float g = 0.282F;
    private float b = 1.0F;
    private float scale = 0.25F;

    public WandOfSplinters(Properties properties) {
        super(properties.attributes(createAttributes()));

        this.setTrailParticle(ModParticles.COLORED_MAGIC_PARTICLE.get());
        this.setImpactParticle(ParticleTypes.CLOUD);
    }

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
            .add(ModAttributes.MAGIC_DAMAGE, new AttributeModifier(ResourceLocation.withDefaultNamespace("weapon.magic_damage"), BASE_DAMAGE, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
            .add(ModAttributes.PROJECTILE_SPEED, new AttributeModifier(ResourceLocation.withDefaultNamespace("weapon.projectile_speed"), BASE_PROJECTILE_SPEED, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
            .add(ModAttributes.KNOCKBACK, new AttributeModifier(ResourceLocation.withDefaultNamespace("weapon.knockback"), BASE_KNOCKBACK, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
            .add(ModAttributes.CRITICAL_STRIKE_CHANCE, new AttributeModifier(ResourceLocation.withDefaultNamespace("weapon.critical_strike"), BASE_CRITICAL_STRIKE_CHANCE, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
            .add(ModAttributes.MANA_COST, new AttributeModifier(ResourceLocation.withDefaultNamespace("weapon.mana_cost"), BASE_MANA_COST, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
            .add(ModAttributes.CAST_SPEED, new AttributeModifier(ResourceLocation.withDefaultNamespace("weapon.cast_speed"), BASE_CAST_SPEED, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
            .build();
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            // Check cooldown
            if (isOnCooldown(player)) {
                return InteractionResult.FAIL;
            }

            ManaComponent manaComponent = player.getData(ManaComponentAttachment.MANA_COMPONENT);
            if (manaComponent != null) {
                boolean success = manaComponent.consumeMana(getManaCostFromItem(player.getItemInHand(hand)));
                if (success) {
                    // Set particle color for this wand (light blue)
                    ColoredMagicParticle.Provider.setColor(r, g, b);
                    ColoredMagicParticle.Provider.setScale(scale);

                    // Get the target position (where the player is looking)
                    Vec3 lookVec = player.getLookAngle();
                    double targetX = player.getX() + lookVec.x * 32; // 32 blocks ahead
                    double targetY = player.getY() + lookVec.y * 32;
                    double targetZ = player.getZ() + lookVec.z * 32;

                    // Calculate direction vector
                    double dirX = targetX - player.getX();
                    double dirY = targetY - (player.getY() + player.getEyeHeight());
                    double dirZ = targetZ - player.getZ();

                    // Create and shoot the projectile
                    BaseSpellProjectile projectile = new BaseSpellProjectile(level, player, dirX, dirY, dirZ);
                    projectile.setImpactParticle(getImpactParticle());
                    projectile.setTrailParticle(getTrailParticle());
                    
                    // Set the initial position to the player's eye level
                    projectile.setPos(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());
                    projectile.setOwner(player);
                    level.addFreshEntity(projectile);
                    
                    // Start the cooldown
                    startCooldown(player);
                    
                    // Sync the new mana value to the client
                    if (player instanceof ServerPlayer serverPlayer) {
                        PacketDistributor.sendToPlayer(serverPlayer,
                            new ManaUpdatePacket(player.getUUID(), manaComponent.getMana(), manaComponent.getMaxMana()));
                    }

                    // Play sound on server side
                    float randomPitch = 0.75f + level.getRandom().nextFloat() * 0.5f; // Random between 0.75 and 1.25
                    player.playSound(ModSounds.SPELL_CAST_2.get(), 0.5f, randomPitch);
                }
            }
        } else {
            // On client side, only play sound if we're not on cooldown
            if (!isOnCooldown(player)) {
                float randomPitch = 0.75f + level.getRandom().nextFloat() * 0.5f; // Random between 0.75 and 1.25
                player.playSound(ModSounds.SPELL_CAST_2.get(), 0.5f, randomPitch);
            }
        }

        return InteractionResult.SUCCESS;
    }
}