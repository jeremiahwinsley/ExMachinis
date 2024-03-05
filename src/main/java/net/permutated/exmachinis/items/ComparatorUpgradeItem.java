package net.permutated.exmachinis.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.permutated.exmachinis.util.Constants;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static net.permutated.exmachinis.util.TranslationKey.translateTooltip;

public class ComparatorUpgradeItem extends Item {
    public ComparatorUpgradeItem() {
        super(new Properties().stacksTo(1).setNoRepair());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, level, tooltip, flagIn);

        String direction = Optional.of(stack)
            .map(ItemStack::getTag)
            .map(tag -> tag.getString(Constants.NBT.DIRECTION))
            .orElse("NONE");

        tooltip.add(translateTooltip("comparatorDirection", direction).withStyle(ChatFormatting.WHITE));
        tooltip.add(Component.empty());
        tooltip.add(translateTooltip("comparatorSetDirection1").withStyle(ChatFormatting.GRAY));
        tooltip.add(translateTooltip("comparatorSetDirection2").withStyle(ChatFormatting.GRAY));
        tooltip.add(translateTooltip("comparatorSetDirection3").withStyle(ChatFormatting.GRAY));
    }

    public static @Nullable Direction getDirection(ItemStack stack) {
        if (stack.getItem() instanceof ComparatorUpgradeItem) {
            return Optional.of(stack)
                .map(ItemStack::getTag)
                .map(tag -> tag.getString(Constants.NBT.DIRECTION))
                .map(Direction::byName)
                .orElse(null);
        }
        return null;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.isSecondaryUseActive()) {
            ItemStack stack = context.getItemInHand().copy();
            stack.getOrCreateTag().putString(Constants.NBT.DIRECTION, context.getClickedFace().toString());
            context.getPlayer().setItemInHand(context.getHand(), stack);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player.isSecondaryUseActive()) {
            ItemStack stack = player.getItemInHand(hand).copy();
            stack.removeTagKey(Constants.NBT.DIRECTION);
            return InteractionResultHolder.consume(stack);
        }
        return super.use(level, player, hand);
    }
}
