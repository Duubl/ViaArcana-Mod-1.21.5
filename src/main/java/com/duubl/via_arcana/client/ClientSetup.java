package com.duubl.via_arcana.client;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientSetup {
    
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Animation layers are now registered automatically when players join
        // The layer will be created when needed in the PlayAnimationHandler
    }
}
