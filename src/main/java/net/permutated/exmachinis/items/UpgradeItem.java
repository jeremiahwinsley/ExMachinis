package net.permutated.exmachinis.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.permutated.exmachinis.ConfigHolder;
import net.permutated.exmachinis.ModRegistry;

import javax.annotation.Nullable;
import java.util.List;

import static net.permutated.exmachinis.util.TranslationKey.translateTooltip;

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

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, level, tooltip, flagIn);

        // Items Processed: 2/4/8
        // RF cost per block: 1280
        // Processing time: 160 ticks
        Component itemsProcessed = TextComponent.EMPTY;
        int cost = 0;
        int time = 0;
        switch (getTier()) {
            case GOLD -> {
                itemsProcessed = translateTooltip("goldItemsProcessed");
                cost = ConfigHolder.SERVER.goldEnergyPerBlock.get();
                time = ConfigHolder.SERVER.goldTicksPerOperation.get();
            }
            case DIAMOND -> {
                itemsProcessed = translateTooltip("diamondItemsProcessed");
                cost = ConfigHolder.SERVER.diamondEnergyPerBlock.get();
                time = ConfigHolder.SERVER.diamondTicksPerOperation.get();
            }
            case NETHERITE -> {
                itemsProcessed = translateTooltip("netheriteItemsProcessed");
                cost = ConfigHolder.SERVER.netheriteEnergyPerBlock.get();
                time = ConfigHolder.SERVER.netheriteTicksPerOperation.get();
            }
        }

        tooltip.add(translateTooltip("itemsProcessed", itemsProcessed).withStyle(ChatFormatting.GRAY));
        tooltip.add(translateTooltip("costPerBlock", cost).withStyle(ChatFormatting.GRAY));
        tooltip.add(translateTooltip("processingTime", time).withStyle(ChatFormatting.GRAY));
    }
}
