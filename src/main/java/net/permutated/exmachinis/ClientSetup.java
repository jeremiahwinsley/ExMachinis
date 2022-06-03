package net.permutated.exmachinis;

import net.minecraft.client.gui.screens.MenuScreens;
import net.permutated.exmachinis.machines.hammer.FluxHammerScreen;
import net.permutated.exmachinis.machines.sieve.FluxSieveScreen;

public class ClientSetup {
    private ClientSetup() {
        // nothing to do
    }

    public static void register() {
        MenuScreens.register(ModRegistry.FLUX_SIEVE_MENU.get(), FluxSieveScreen::new);
        MenuScreens.register(ModRegistry.FLUX_HAMMER_MENU.get(), FluxHammerScreen::new);
    }
}
