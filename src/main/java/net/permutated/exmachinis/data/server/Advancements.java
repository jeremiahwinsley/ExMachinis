package net.permutated.exmachinis.data.server;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.minecraftforge.registries.RegistryObject;
import net.permutated.exmachinis.ExMachinis;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.util.Constants;
import org.apache.commons.lang3.function.TriFunction;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static net.permutated.exmachinis.util.TranslationKey.translateAdvancement;

public class Advancements implements ForgeAdvancementProvider.AdvancementGenerator {

    @Override
    @SuppressWarnings({"java:S1481", "java:S1854"}) // unused variables
    public void generate(HolderLookup.Provider registries, Consumer<Advancement> consumer, ExistingFileHelper existingFileHelper) {
        var root = Advancement.Builder.advancement()
            .display(displayInfo(
                ModRegistry.FLUX_SIEVE_ITEM.get(),
                translateAdvancement("root"),
                translateAdvancement("root.desc"),
                new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"),
                FrameType.TASK,
                false,
                false,
                false
            ))
            .addCriterion("item", InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT))
            .save(consumer, advancementPath("root"));

        BiFunction<RegistryObject<BlockItem>, String, Advancement> machineBuilder = (registryObject, machine) -> Advancement.Builder.advancement()
            .parent(root)
            .display(displayItem(registryObject, machine))
            .addCriterion("has_" + machine, hasItem(registryObject.get()))
            .save(consumer, advancementPath(machine));

        var sieve = machineBuilder.apply(ModRegistry.FLUX_SIEVE_ITEM, Constants.FLUX_SIEVE);
        var hammer = machineBuilder.apply(ModRegistry.FLUX_HAMMER_ITEM, Constants.FLUX_HAMMER);
        var compactor = machineBuilder.apply(ModRegistry.FLUX_COMPACTOR_ITEM, Constants.FLUX_COMPACTOR);

        TriFunction<RegistryObject<Item>, String, Advancement, Advancement> upgradeBuilder = (registryObject, upgrade, parent) -> Advancement.Builder.advancement()
            .parent(parent)
            .display(displayItem(registryObject, upgrade))
            .addCriterion("has_" + upgrade, hasItem(registryObject.get()))
            .save(consumer, advancementPath(upgrade));

        var gold = upgradeBuilder.apply(ModRegistry.GOLD_UPGRADE, Constants.GOLD_UPGRADE, root);
        var diamond = upgradeBuilder.apply(ModRegistry.DIAMOND_UPGRADE, Constants.DIAMOND_UPGRADE, gold);
        var netherite = upgradeBuilder.apply(ModRegistry.NETHERITE_UPGRADE, Constants.NETHERITE_UPGRADE, diamond);
    }

    private String advancementPath(String key) {
        return String.format("%s:%s", ExMachinis.MODID, key);
    }

    private CriterionTriggerInstance hasItem(ItemLike item) {
        return InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(item).build());
    }

    private DisplayInfo displayItem(RegistryObject<? extends Item> item, String translationKey) {
        return displayInfo(item.get(),
            translateAdvancement(translationKey),
            translateAdvancement(translationKey + ".desc"),
            null, FrameType.TASK, true, true, false);
    }

    @SuppressWarnings("java:S107") // arguments needed for returned class
    private DisplayInfo displayInfo(ItemLike title, Component description, Component icon, @Nullable ResourceLocation background, FrameType frame, boolean showToast, boolean announceChat, boolean hidden) {
        return new DisplayInfo(new ItemStack(title.asItem()), description, icon, background, frame, showToast, announceChat, hidden);
    }
}
