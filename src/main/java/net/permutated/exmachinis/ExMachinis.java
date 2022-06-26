package net.permutated.exmachinis;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ExMachinis.MODID)
public class ExMachinis {
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final String MODID = "exmachinis";

    public ExMachinis() {
        LOGGER.info("Registering mod: {}", MODID);

        ModRegistry.register();

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ConfigHolder.SERVER_SPEC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetupEvent);
    }

    public void onClientSetupEvent(final FMLClientSetupEvent event) {
        ClientSetup.register();
    }
}
