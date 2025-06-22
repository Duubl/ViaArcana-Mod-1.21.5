package com.duubl.via_arcana.network.handler;

import com.duubl.via_arcana.ViaArcana;
import com.duubl.via_arcana.network.packets.AnimationBroadcastPacket;
import com.duubl.via_arcana.network.packets.PlayAnimationPacket;

import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.api.layered.AnimationStack;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

public class PlayAnimationHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation WEAPON_SLAM_ANIMATION = ResourceLocation.fromNamespaceAndPath(ViaArcana.MODID, "weapon_slam.player");
    private static final ResourceLocation ANIMATION_LAYER_KEY = ResourceLocation.fromNamespaceAndPath(ViaArcana.MODID, "weapon_slam_layer");
    
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof AbstractClientPlayer clientPlayer) {
            LOGGER.info("Player logged in, registering animation layer for: " + clientPlayer.getName().getString());
            
            // Use the correct API to register the animation layer
            PlayerAnimationAccess.REGISTER_ANIMATION_EVENT.register((player, animationStack) -> {
                if (player == clientPlayer) {
                    ModifierLayer<IAnimation> weaponSlamLayer = new ModifierLayer<>();
                    animationStack.addAnimLayer(1000, weaponSlamLayer);
                    
                    // Store the layer in the player's associated data
                    PlayerAnimationAccess.getPlayerAssociatedData(clientPlayer).set(ANIMATION_LAYER_KEY, weaponSlamLayer);
                    
                    LOGGER.info("Animation layer registered for player: " + clientPlayer.getName().getString());
                }
            });
        }
    }

    public static void broadcastAnimation(ServerPlayer player) {
        LOGGER.info("Broadcasting animation for player: " + player.getName().getString());
        
        // Get nearby players to see if anyone is around
        var nearbyPlayers = player.serverLevel().getPlayers(p -> 
            p.distanceToSqr(player) <= 64.0 * 64.0
        );
        LOGGER.info("Found " + nearbyPlayers.size() + " nearby players");
        
        // Broadcast to all players within 64 blocks
        PacketDistributor.sendToPlayersNear(
            player.serverLevel(),
            player,
            player.getX(), player.getY(), player.getZ(),
            64.0, // Radius in blocks
            new AnimationBroadcastPacket()
        );
        
        LOGGER.info("Broadcast packet sent");
    }

    public static void handleBroadcastPacket(AnimationBroadcastPacket packet, IPayloadContext context) {
        LOGGER.info("Received broadcast packet");
        
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> {
                Player player = context.player();
                if (player != null) {
                    LOGGER.info("Handling animation for player: " + player.getName().getString());
                    handleAnimation(player);
                } else {
                    LOGGER.warn("Player is null in broadcast packet handler");
                }
            });
        }
    }
    
    public static void handleAnimation(Player player) {
        LOGGER.info("handleAnimation called for player: " + player.getName().getString());
        
        // This will be called on the client side to trigger the animation
        if (player.level().isClientSide()) {
            LOGGER.info("Triggering weapon slam animation on client side");
            // Trigger the player animation using PlayerAnimator
            triggerWeaponSlamAnimation(player);
        } else {
            LOGGER.warn("handleAnimation called on server side, should be client side");
        }
    }
    
    private static void triggerWeaponSlamAnimation(Player player) {
        LOGGER.info("triggerWeaponSlamAnimation called");
        
        if (!(player instanceof AbstractClientPlayer clientPlayer)) {
            LOGGER.warn("Player is not AbstractClientPlayer: " + player.getClass().getSimpleName());
            return;
        }
        
        // Get the animation layer for this player using the correct API
        IAnimation animation = PlayerAnimationAccess.getPlayerAssociatedData(clientPlayer).get(ANIMATION_LAYER_KEY);
        
        if (animation == null) {
            LOGGER.warn("Animation layer is null, creating new one");
            // Create the layer if it doesn't exist
            ModifierLayer<IAnimation> weaponSlamLayer = new ModifierLayer<>();
            
            // Add the layer to the player's animation stack
            PlayerAnimationAccess.getPlayerAnimLayer(clientPlayer).addAnimLayer(1000, weaponSlamLayer);
            
            // Store it in the associated data
            PlayerAnimationAccess.getPlayerAssociatedData(clientPlayer).set(ANIMATION_LAYER_KEY, weaponSlamLayer);
            
            animation = weaponSlamLayer;
            LOGGER.info("Animation layer created and added to stack");
        }
        
        if (animation instanceof ModifierLayer) {
            @SuppressWarnings("unchecked")
            ModifierLayer<IAnimation> modifierLayer = (ModifierLayer<IAnimation>) animation;
            
            LOGGER.info("Animation layer found, checking if animation exists in registry");
            
            // Check if the animation exists in the registry
            var animationData = PlayerAnimationRegistry.getAnimation(WEAPON_SLAM_ANIMATION);
            if (animationData == null) {
                LOGGER.error("Animation not found in registry: " + WEAPON_SLAM_ANIMATION);
                return;
            }
            
            LOGGER.info("Animation found in registry, playing animation");
            
            if (modifierLayer.getAnimation() != null) {
                LOGGER.info("Replacing existing animation");
                modifierLayer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(20, Ease.LINEAR), null);
            } else {
                LOGGER.info("Setting new animation");
                // Get the weapon slam animation and create the player
                var animationPlayer = animationData.playAnimation()
                        .setFirstPersonMode(FirstPersonMode.THIRD_PERSON_MODEL)
                        .setFirstPersonConfiguration(new FirstPersonConfiguration().setShowRightArm(true).setShowLeftItem(false));
                
                modifierLayer.replaceAnimationWithFade(AbstractFadeModifier.functionalFadeIn(20, (modelName, type, value) -> value), animationPlayer);
            }
        } else {
            LOGGER.error("Animation layer is not a ModifierLayer: " + (animation != null ? animation.getClass().getSimpleName() : "null"));
        }
    }

    public static void handlePacket(PlayAnimationPacket packet, IPayloadContext context) {
        LOGGER.info("Received PlayAnimationPacket");
        
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                ServerPlayer player = (ServerPlayer) context.player();
                if (player != null) {
                    LOGGER.info("Processing animation packet for player: " + player.getName().getString());
                    
                    // Send the broadcast packet to the client (this will trigger the animation on client side)
                    LOGGER.info("Sending broadcast packet to client");
                    PacketDistributor.sendToPlayer(player, new AnimationBroadcastPacket());
                } else {
                    LOGGER.warn("ServerPlayer is null in packet handler");
                }
            });
        }
    }
}
