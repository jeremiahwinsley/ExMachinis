package net.permutated.exmachinis.util;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.core.Direction;

import java.util.Map;

public class Constants {
    private Constants() {
        // nothing to do
    }

    public static final String FLUX_SIEVE = "flux_sieve";
    public static final String FLUX_HAMMER = "flux_hammer";
    public static final String FLUX_COMPACTOR = "flux_compactor";
    public static final String FLUX_CRUCIBLE = "flux_crucible";

    public static final String UNKNOWN = "unknown";

    public static final String GOLD_UPGRADE = "gold_upgrade";
    public static final String DIAMOND_UPGRADE = "diamond_upgrade";
    public static final String NETHERITE_UPGRADE = "netherite_upgrade";

    public static final String COMPACTING = "compacting";

    public static class NBT {
        private NBT() {
            // nothing to do
        }

        public static final String REGISTRY = "registry";
        public static final String VERSION = "version";
        public static final String ENERGY = "energy";
        public static final String FLUID = "fluid";
        public static final String INVENTORY = "inventory";
        public static final String UPGRADES = "upgrades";

        public static final String RANGE = "range";
        public static final String ENABLED = "enabled";
        public static final String CONTENTS = "contents";
        public static final String POSITION = "position";
        public static final String OUTPUT = "output";
    }

    public static class JSON
    {
        private JSON() {
            // nothing to do
        }

        public static final String INPUT = "input";
        public static final String INGREDIENT = "ingredient";
        public static final String OUTPUT = "output";
        public static final String ITEM = "item";
        public static final String COUNT = "count";
        public static final String NBT = "nbt";
        public static final String TAG = "tag";
    }

    public static final Map<Direction, Quaternion> ROTATIONS = Map.ofEntries(
        Map.entry(Direction.UP, Vector3f.ZN.rotationDegrees(90)),
        Map.entry(Direction.DOWN, Vector3f.ZN.rotationDegrees(-90)),
        Map.entry(Direction.NORTH, Vector3f.YN.rotationDegrees(90)),
        Map.entry(Direction.SOUTH, Vector3f.YN.rotationDegrees(-90)),
        Map.entry(Direction.WEST, Vector3f.YN.rotationDegrees(0)),
        Map.entry(Direction.EAST, Vector3f.YN.rotationDegrees(180))
    );
}
