package net.permutated.exmachinis.events;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.permutated.exmachinis.ExMachinis;
import net.permutated.exmachinis.ModRegistry;

@Mod.EventBusSubscriber(modid = ExMachinis.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventHandler {

    private ClientEventHandler() {
        // nothing to do
    }

    @SubscribeEvent
    public static void onClientLoggedOutEvent(final ClientPlayerNetworkEvent.LoggingOut event) {
        ExMachinis.LOGGER.debug("Clearing recipe cache after logging out");
        ModRegistry.COMPACTING_REGISTRY.clearRecipes();
    }
}
