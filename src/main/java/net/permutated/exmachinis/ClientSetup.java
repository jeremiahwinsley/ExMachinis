package net.permutated.exmachinis;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.permutated.exmachinis.machines.compactor.FluxCompactorScreen;
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

        ItemBlockRenderTypes.setRenderLayer(ModRegistry.FLUX_SIEVE_BLOCK.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(ModRegistry.FLUX_HAMMER_BLOCK.get(), RenderType.cutoutMipped());
    }
}
