package net.permutated.exmachinis;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class ConfigHolder {
    private ConfigHolder() {
        // nothing to do
    }

    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_SIEVE = "sieve";
    public static final String CATEGORY_HAMMER = "hammer";
    public static final String CATEGORY_COMPACTOR = "compactor";


    public static final ServerConfig SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;

    static {
        final Pair<ServerConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_SPEC = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    public static class ServerConfig {
        // CATEGORY_GENERAL
        public final ForgeConfigSpec.IntValue noneWorkDelay;
        public final ForgeConfigSpec.IntValue basicWorkDelay;
        public final ForgeConfigSpec.IntValue advancedWorkDelay;
        public final ForgeConfigSpec.IntValue ultimateWorkDelay;

        public final ForgeConfigSpec.IntValue noneWorkCost;
        public final ForgeConfigSpec.IntValue basicWorkCost;
        public final ForgeConfigSpec.IntValue advancedWorkCost;
        public final ForgeConfigSpec.IntValue ultimateWorkCost;


        // CATEGORY_SIEVE
        // rf per block
        // multiplier per upgrade?

        // CATEGORY_HAMMER


        // CATEGORY_COMPACTOR



        ServerConfig(ForgeConfigSpec.Builder builder) {
            builder.push(CATEGORY_GENERAL);
            noneWorkDelay = builder.defineInRange("noneWorkDelay", 160, 10, 1200);
            basicWorkDelay = builder.defineInRange("basicWorkDelay", 80, 10, 1200);
            advancedWorkDelay = builder.defineInRange("advancedWorkDelay", 40, 10, 1200);
            ultimateWorkDelay = builder.defineInRange("ultimateWorkDelay", 20, 10, 1200);

            // cost per block, max 1k out of 100,000 buffer? make buffer config
            noneWorkCost = builder.defineInRange("noneWorkCost", 100, 0, 1_000);
            basicWorkCost = builder.defineInRange("basicWorkCost", 200, 0, 1_000);
            advancedWorkCost = builder.defineInRange("advancedWorkCost", 400, 0, 1_000);
            ultimateWorkCost = builder.defineInRange("ultimateWorkCost", 800, 0, 1_000);
        }
    }
}
