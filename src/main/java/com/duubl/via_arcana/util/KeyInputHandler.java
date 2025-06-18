package com.duubl.via_arcana.util;

import com.duubl.via_arcana.ViaArcana;
import com.duubl.via_arcana.items.accessories.AgilityScarf;
import com.duubl.via_arcana.network.packets.LeapPacket;
import com.duubl.via_arcana.network.NetworkHandler;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import top.theillusivec4.curios.api.CuriosApi;

@EventBusSubscriber(modid = ViaArcana.MODID)
public class KeyInputHandler {
    private static final long DOUBLE_TAP_WINDOW = 300;
    private static long lastForwardPress = 0;
    private static long lastBackwardPress = 0;
    private static long lastLeftPress = 0;
    private static long lastRightPress = 0;
    private static boolean forwardPressed = false;
    private static boolean backwardPressed = false;
    private static boolean leftPressed = false;
    private static boolean rightPressed = false;

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.options == null) return;

        boolean hasScarf = CuriosApi.getCuriosInventory(player).map(handler ->
                !handler.findCurios(stack -> stack.getItem() instanceof AgilityScarf).isEmpty()
        ).orElse(false);
        if (!hasScarf) return;

        long currentTime = System.currentTimeMillis();
        int key = event.getKey();
        int action = event.getAction();

        Vec3 direction = null;
        boolean sendPacket = false;

        // Update key states
        if (key == minecraft.options.keyUp.getKey().getValue()) {
            forwardPressed = action == InputConstants.PRESS;
            if (action == InputConstants.PRESS && currentTime - lastForwardPress <= DOUBLE_TAP_WINDOW) {
                sendPacket = true;
            }
            if (action == InputConstants.PRESS) lastForwardPress = currentTime;
        } else if (key == minecraft.options.keyDown.getKey().getValue()) {
            backwardPressed = action == InputConstants.PRESS;
            if (action == InputConstants.PRESS && currentTime - lastBackwardPress <= DOUBLE_TAP_WINDOW) {
                sendPacket = true;
            }
            if (action == InputConstants.PRESS) lastBackwardPress = currentTime;
        } else if (key == minecraft.options.keyLeft.getKey().getValue()) {
            leftPressed = action == InputConstants.PRESS;
            if (action == InputConstants.PRESS && currentTime - lastLeftPress <= DOUBLE_TAP_WINDOW) {
                sendPacket = true;
            }
            if (action == InputConstants.PRESS) lastLeftPress = currentTime;
        } else if (key == minecraft.options.keyRight.getKey().getValue()) {
            rightPressed = action == InputConstants.PRESS;
            if (action == InputConstants.PRESS && currentTime - lastRightPress <= DOUBLE_TAP_WINDOW) {
                sendPacket = true;
            }
            if (action == InputConstants.PRESS) lastRightPress = currentTime;
        }

        // Calculate direction based on pressed keys when double-tap detected
        if (sendPacket) {
            direction = new Vec3(0, 0, 0);
            if (forwardPressed) direction = direction.add(0, 0, 1);
            if (backwardPressed) direction = direction.add(0, 0, -1);
            if (leftPressed) direction = direction.add(1, 0, 0);
            if (rightPressed) direction = direction.add(-1, 0, 0);

            // Only send if a direction was pressed
            if (direction.lengthSqr() > 0) {
                float yaw = player.getYRot();
                direction = direction.yRot((float) Math.toRadians(-yaw)); // Reverted to -yaw for correct Z-axis alignment
                direction = direction.normalize(); // Normalize for consistent distance
                NetworkHandler.sendToServer(new LeapPacket(direction));
            }
        }
    }
}