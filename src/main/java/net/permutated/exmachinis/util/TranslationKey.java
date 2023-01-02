package net.permutated.exmachinis.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.permutated.exmachinis.ExMachinis;

import java.text.NumberFormat;
import java.util.Arrays;

public class TranslationKey {
    private TranslationKey() {
        // nothing to do
    }

    protected static final NumberFormat numberFormat = NumberFormat.getInstance();
    private static final String FORMAT = "%s." + ExMachinis.MODID + ".%s";

    public static String tooltip(String key) {
        return String.format(FORMAT, "tooltip", key);
    }

    public static String block(String key) {
        return String.format(FORMAT, "block", key);
    }

    public static String item(String key) {
        return String.format(FORMAT, "item", key);
    }

    public static String gui(String key) {
        return String.format(FORMAT, "gui", key);
    }

    public static String tab() {
        return String.format("itemGroup.%s", ExMachinis.MODID);
    }

    public static String jei(String key) {
        return String.format(ExMachinis.MODID + ".int.jei.category.%s", key);
    }

    public static String chat(String key) {
        return String.format(FORMAT, "chat", key);
    }

    public static String advancement(String key) {
        return String.format(FORMAT, "advancement", key);
    }

    public static String desc(String key) {
        return key.concat(".desc");
    }

    public static MutableComponent translateTooltip(String key) {
        return Component.translatable(TranslationKey.tooltip(key));
    }

    public static MutableComponent translateTooltip(String key, Object... values) {
        return Component.translatable(TranslationKey.tooltip(key), values);
    }

    public static MutableComponent translateTooltipInt(String key, Integer... values) {
        Object[] formatted = Arrays.stream(values).map(numberFormat::format).toArray();
        return Component.translatable(TranslationKey.tooltip(key), formatted);
    }

    public static MutableComponent translateGui(String key) {
        return Component.translatable(TranslationKey.gui(key));
    }

    public static MutableComponent translateGui(String key, Object... values) {
        return Component.translatable(TranslationKey.gui(key), values);
    }

    public static MutableComponent translateJei(String key) {
        return Component.translatable(TranslationKey.jei(key));
    }

    public static MutableComponent translateAdvancement(String key) {
        return Component.translatable(TranslationKey.advancement(key));
    }
}
