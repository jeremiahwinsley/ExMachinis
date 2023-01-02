package net.permutated.exmachinis;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.permutated.exmachinis.machines.compactor.FluxCompactorScreen;
import net.permutated.exmachinis.machines.crucible.FluxCrucibleRenderer;
import net.permutated.exmachinis.machines.crucible.FluxCrucibleScreen;
import net.permutated.exmachinis.machines.hammer.FluxHammerScreen;
import net.permutated.exmachinis.machines.sieve.FluxSieveScreen;

public class ClientSetup {
    private ClientSetup() {
        // nothing to do
    }

    public static void register() {
        MenuScreens.register(ModRegistry.FLUX_SIEVE_MENU.get(), FluxSieveScreen::new);
        MenuScreens.register(ModRegistry.FLUX_HAMMER_MENU.get(), FluxHammerScreen::new);
        MenuScreens.register(ModRegistry.FLUX_COMPACTOR_MENU.get(), FluxCompactorScreen::new);
        MenuScreens.register(ModRegistry.FLUX_CRUCIBLE_MENU.get(), FluxCrucibleScreen::new);
        BlockEntityRenderers.register(ModRegistry.FLUX_CRUCIBLE_TILE.get(), FluxCrucibleRenderer::new);
    }
}
