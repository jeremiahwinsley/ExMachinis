package net.permutated.exmachinis.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;

import static net.permutated.exmachinis.util.TranslationKey.translateGui;

public enum WorkStatus {
    NONE(translateGui("noStatus")),
    WORKING(translateGui("working").withStyle(ChatFormatting.DARK_GREEN)),
    MISSING_INVENTORY(translateGui("inventoryMissing").withStyle(ChatFormatting.RED)),
    INVENTORY_FULL(translateGui("inventoryFull").withStyle(ChatFormatting.RED)),
    OUT_OF_ENERGY(translateGui("outOfEnergy").withStyle(ChatFormatting.RED)),
    MISSING_MESH(translateGui("meshMissing").withStyle(ChatFormatting.RED)),
    REDSTONE_DISABLED(translateGui("redstoneDisabled").withStyle(ChatFormatting.RED)),
    ;

    final MutableComponent translation;
    WorkStatus(MutableComponent component) {
        this.translation = component;
    }

    public MutableComponent getTranslation() {
        return this.translation;
    }
}
