package net.permutated.exmachinis;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.permutated.exmachinis.machines.compactor.FluxCompactorScreen;
import net.permutated.exmachinis.machines.hammer.FluxHammerScreen;
import net.permutated.exmachinis.machines.sieve.FluxSieveScreen;

@Mod.EventBusSubscriber(modid = ExMachinis.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    private ClientSetup() {
        // nothing to do
    }

    @SubscribeEvent
    public static void registerMenuScreens(final RegisterMenuScreensEvent event) {
        event.register(ModRegistry.FLUX_SIEVE_MENU.get(), FluxSieveScreen::new);
        event.register(ModRegistry.FLUX_HAMMER_MENU.get(), FluxHammerScreen::new);
        event.register(ModRegistry.FLUX_COMPACTOR_MENU.get(), FluxCompactorScreen::new);
    }
}
