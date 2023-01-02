package net.permutated.exmachinis.network;

import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.permutated.exmachinis.ExMachinis;
import net.permutated.exmachinis.util.ResourceUtil;


public class NetworkDispatcher {
    private NetworkDispatcher() {
        // nothing to do
    }

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(ResourceUtil.prefix("main"),
        () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public static void register() {
        int packetIndex = 0;
        INSTANCE.registerMessage(packetIndex++, PacketFluidSync.class, PacketFluidSync::toBytes, PacketFluidSync::new, PacketFluidSync::handle);

        ExMachinis.LOGGER.info("Registered {} network packets", packetIndex);
    }
}
