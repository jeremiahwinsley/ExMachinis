package net.permutated.exmachinis.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ClientUtil {
    private ClientUtil() {
        // nothing to do
    }

    public static @Nullable Level getClientLevel() {
        return Minecraft.getInstance().level;
    }

    public static @Nullable Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }
}
