package net.permutated.exmachinis.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.permutated.exmachinis.data.client.BlockStates;
import net.permutated.exmachinis.data.client.ItemModels;
import net.permutated.exmachinis.data.client.Languages;
import net.permutated.exmachinis.data.server.*;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGenerators {
    private DataGenerators() {}

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();

        if (event.includeServer()) {
            generator.addProvider(new BlockTags(generator, fileHelper));
            generator.addProvider(new CraftingRecipes(generator));
            generator.addProvider(new BlockLoot(generator));
        }
        if (event.includeClient()) {
            generator.addProvider(new BlockStates(generator, fileHelper));
            generator.addProvider(new ItemModels(generator, fileHelper));
            generator.addProvider(new Languages.English(generator));
        }

    }
}
