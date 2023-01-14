package net.permutated.exmachinis;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import net.permutated.exmachinis.items.UpgradeItem;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber
public class ConfigHolder {
    private ConfigHolder() {
        // nothing to do
    }

    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_SIEVE = "sieve";
    public static final String CATEGORY_HAMMER = "hammer";
    public static final String CATEGORY_COMPACTOR = "compactor";
    public static final String CATEGORY_CRUCIBLE = "crucible";


    public static final ServerConfig SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;

    static {
        final Pair<ServerConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_SPEC = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    public static class ServerConfig {
        // CATEGORY_GENERAL
        public final ForgeConfigSpec.IntValue goldTicksPerOperation;
        public final ForgeConfigSpec.IntValue goldEnergyPerBlock;

        public final ForgeConfigSpec.IntValue diamondTicksPerOperation;
        public final ForgeConfigSpec.IntValue diamondEnergyPerBlock;

        public final ForgeConfigSpec.IntValue netheriteTicksPerOperation;
        public final ForgeConfigSpec.IntValue netheriteEnergyPerBlock;

        public final ForgeConfigSpec.IntValue energyBufferSize;
        public final ForgeConfigSpec.IntValue maxEnergyPerTick;

        // CATEGORY_SIEVE
        public final ForgeConfigSpec.BooleanValue sieveFortuneEnabled;
        public final ForgeConfigSpec.BooleanValue sieveBulkProcessing;

        // CATEGORY_CRUCIBLE
        public final ForgeConfigSpec.EnumValue<UpgradeItem.Tier> crucibleMaxUpgradeTier;


        ServerConfig(ForgeConfigSpec.Builder builder) {
            builder
                .comment("General balance configs",
                    "The default calculations for each upgrade are calculated as follows:",
                    "(ticks per operation * RF per block) * blocks processed = RF per operation",
                    "RF per operation / ticks per operation = RF per tick")
                .push(CATEGORY_GENERAL);

            goldTicksPerOperation = builder
                .comment("Ticks per operation for the gold upgrade.")
                .defineInRange("goldTicksPerOperation", 160, 10, 1200);
            goldEnergyPerBlock = builder
                .comment("Energy per block for the gold upgrade.",
                    "(160 * 1280) * 8 = 1,280RF/operation, 8RF/t")
                .defineInRange("goldEnergyPerOperation", 1_280, 0, 128_000);

            diamondTicksPerOperation = builder
                .comment("Ticks per operation for the diamond upgrade.")
                .defineInRange("diamondTicksPerOperation", 80, 10, 1200);
            diamondEnergyPerBlock = builder
                .comment("Energy per block for the diamond upgrade.",
                    "(80 * 2560) * 64 = 163,840RF/operation, 2,048RF/t")
                .defineInRange("diamondEnergyPerOperation", 2_560, 0, 256_000);

            netheriteTicksPerOperation = builder
                .comment("Ticks per operation for the netherite upgrade.")
                .defineInRange("netheriteTicksPerOperation", 20, 10, 1200);
            netheriteEnergyPerBlock = builder
                .comment("Energy per block for the netherite upgrade.",
                    "(20 * 2560) * 64 = 163,840RF/operation, 8,192RF/t")
                .defineInRange("netheriteEnergyPerOperation", 2_560, 0, 256_000);

            energyBufferSize = builder
                .comment("Max energy buffer size for machines.")
                .defineInRange("energyBufferSize", 200_000, 20_000, 20_000_000);
            maxEnergyPerTick = builder
                .comment("Max energy transfer per tick for machines.")
                .defineInRange("maxEnergyPerTick", 10_000, 1_000, 1_000_000);

            builder.pop();

            builder.push(CATEGORY_SIEVE);

            sieveFortuneEnabled = builder
                .comment("Whether fortune is applied from an enchanted mesh.")
                .define("sieveFortuneEnabled", true);

            sieveBulkProcessing = builder
                .comment("Whether sieve rolls are processed in bulk for better performance.",
                    "Bulk processing will roll once per slot and multiply the output,",
                    "while single processing will roll for each input.")
                .define("sieveBulkProcessing", true);

            builder.pop();

            builder.push(CATEGORY_CRUCIBLE);

            crucibleMaxUpgradeTier = builder
                .comment("Maximum upgrade tier supported by the crucible.",
                    "The equivalent amount of heat on a standard crucible",
                    "is 50 (GOLD), 800 (DIAMOND), and 3200 (NETHERITE).")
                .defineEnum("crucibleMaxUpgradeTier", UpgradeItem.Tier.GOLD);

            builder.pop();

        }
    }
}
