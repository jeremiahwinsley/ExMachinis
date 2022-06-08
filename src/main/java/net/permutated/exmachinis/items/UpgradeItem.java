package net.permutated.exmachinis.items;

import net.minecraft.world.item.Item;
import net.permutated.exmachinis.ModRegistry;

public class UpgradeItem extends Item {

    public enum Tier {
        GOLD(3),
        DIAMOND(3),
        NETHERITE(1),
        ;

        final int stackSize;
        Tier(int stackSize) {
            this.stackSize = stackSize;
        }
    }

    public UpgradeItem(Tier tier) {
        super(new Properties().stacksTo(tier.stackSize).tab(ModRegistry.CREATIVE_TAB).setNoRepair());
        this.tier = tier;
    }

    private final Tier tier;

    public Tier getTier() {
        return this.tier;
    }
}
