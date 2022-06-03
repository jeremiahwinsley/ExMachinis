package net.permutated.exmachinis.data.client;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.ExMachinis;

import static net.permutated.exmachinis.util.TranslationKey.*;

public class Languages {
    private Languages() {
        // nothing to do
    }

    public static class English extends LanguageProvider {

        public English(DataGenerator gen) {
            super(gen, ExMachinis.MODID, "en_us");
        }

        @Override
        protected void addTranslations() {
            addBlock(ModRegistry.FLUX_SIEVE_BLOCK, "Flux Sieve");
            addBlock(ModRegistry.FLUX_HAMMER_BLOCK, "Flux Hammer");
            addBlock(ModRegistry.FLUX_COMPACTOR_BLOCK, "Flux Compactor");

            addItem(ModRegistry.BULK_UPGRADE_BASIC, "Bulk Upgrade");

            add(gui("noStatus"), "Machine starting up...");
            add(gui("inventoryMissing"), "Place inventory above machine.");
            add(gui("inventoryFull"), "Inventory is full.");
            add(gui("working"), "Machine is working.");

            add(gui("workArea"), "Work area (in chunks)");
            add(gui("workAreaBlocks"), "Work area (in blocks)");
            add(gui("toggleWork"), "Working status");
            add(tab(), "Ex Machinis");


            add(tooltip("activated"), "Activated");
            add(tooltip("progress"), "Progress: %d/%d seconds");

            add(tooltip("player"), "Player: %s");
        }
    }
}
