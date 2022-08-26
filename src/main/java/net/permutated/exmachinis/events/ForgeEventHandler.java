package net.permutated.exmachinis.events;

import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.permutated.exmachinis.ExMachinis;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.recipes.CompactingRecipe;

import java.util.List;

@Mod.EventBusSubscriber(modid = ExMachinis.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventHandler {

    private ForgeEventHandler() {
        // nothing to do
    }

    public static void loadCompactingRecipes(RecipeManager recipeManager) {
        List<CompactingRecipe> compactingRecipes = recipeManager
            .getAllRecipesFor(ModRegistry.COMPACTING_RECIPE_TYPE.get());
        ModRegistry.COMPACTING_REGISTRY.setRecipeList(compactingRecipes);
        ExMachinis.LOGGER.debug("Registered {} compacting recipes", compactingRecipes.size());
    }

    @SubscribeEvent
    public static void onRecipesUpdatedEvent(final RecipesUpdatedEvent event) {
        ExMachinis.LOGGER.debug("Loading recipes on server sync");
        loadCompactingRecipes(event.getRecipeManager());
    }

    @SubscribeEvent
    public static void onServerStartingEvent(final ServerStartingEvent event) {
        if (event.getServer().isDedicatedServer()) {
            ExMachinis.LOGGER.debug("Loading recipes on server startup");
            loadCompactingRecipes(event.getServer().getRecipeManager());
        }
    }
}
