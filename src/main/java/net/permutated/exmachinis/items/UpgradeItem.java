package net.permutated.exmachinis.items;

import net.minecraft.world.item.Item;
import net.permutated.exmachinis.ModRegistry;

public class UpgradeItem extends Item {

    public enum Tier {
        BASIC,
        ADVANCED,
        ULTIMATE,
    }

    public UpgradeItem(Tier tier) {
        super(new Properties().stacksTo(3).tab(ModRegistry.CREATIVE_TAB).setNoRepair());
        this.tier = tier;
    }

    private final Tier tier;

    public Tier getTier() {
        return this.tier;
    }
}
