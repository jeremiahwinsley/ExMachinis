package net.permutated.exmachinis.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.data.event.GatherDataEvent;
import net.permutated.exmachinis.ExMachinis;
import net.permutated.exmachinis.data.client.BlockStates;
import net.permutated.exmachinis.data.client.ItemModels;
import net.permutated.exmachinis.data.client.Languages;
import net.permutated.exmachinis.data.server.*;

@Mod.EventBusSubscriber(modid = ExMachinis.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGenerators {
    private DataGenerators() {}

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();

        if (event.includeServer()) {
            generator.addProvider(true, new Advancements(generator, fileHelper));
            generator.addProvider(true, new BlockTags(generator, fileHelper));
            generator.addProvider(true, new CompactingRecipes(generator));
            generator.addProvider(true, new CraftingRecipes(generator));
            generator.addProvider(true, new BlockLoot(generator));
        }
        if (event.includeClient()) {
            generator.addProvider(true, new BlockStates(generator, fileHelper));
            generator.addProvider(true, new ItemModels(generator, fileHelper));
            generator.addProvider(true, new Languages.English(generator));
        }

    }
}
