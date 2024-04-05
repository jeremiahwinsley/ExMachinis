package net.permutated.exmachinis;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.permutated.exmachinis.machines.base.AbstractMachineTile;
import org.slf4j.Logger;

@Mod(ExMachinis.MODID)
public class ExMachinis {
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final String MODID = "exmachinis";

    public ExMachinis(IEventBus modEventBus) {
        LOGGER.info("Registering mod: {}", MODID);

        ModRegistry.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ConfigHolder.SERVER_SPEC);
        modEventBus.addListener(ExMachinis::registerCapabilities);
    }

    public static void registerCapabilities(final RegisterCapabilitiesEvent event) {
        AbstractMachineTile.registerCapabilities(event, ModRegistry.FLUX_COMPACTOR_TILE.get());
        AbstractMachineTile.registerCapabilities(event, ModRegistry.FLUX_HAMMER_TILE.get());
        AbstractMachineTile.registerCapabilities(event, ModRegistry.FLUX_SIEVE_TILE.get());
    }
}
