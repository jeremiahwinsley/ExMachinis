package net.permutated.exmachinis;

import com.mojang.logging.LogUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.permutated.exmachinis.machines.base.AbstractMachineBlock;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExMachinis.MODID)
public class ExMachinis
{
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final String MODID = "exmachinis";

    public ExMachinis()
    {
        LOGGER.info("Registering mod: {}", MODID);

        ModRegistry.register();

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ConfigHolder.SERVER_SPEC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetupEvent);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onClientSetupEvent(final FMLClientSetupEvent event) {
        ClientSetup.register();
    }
}
