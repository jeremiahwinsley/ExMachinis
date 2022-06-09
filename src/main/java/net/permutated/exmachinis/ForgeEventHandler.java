package net.permutated.exmachinis;

import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.permutated.exmachinis.recipes.CompactingRecipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Mod.EventBusSubscriber(modid = ExMachinis.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExMachinis.MODID);

    private ForgeEventHandler() {
        // nothing to do
    }

    @SubscribeEvent
    public static void onClientLoggedOutEvent(final ClientPlayerNetworkEvent.LoggedOutEvent event) {
        ModRegistry.COMPACTING_REGISTRY.clearRecipes();
    }

    public static void loadCompactingRecipes(RecipeManager recipeManager) {
        List<CompactingRecipe> compactingRecipes = recipeManager
            .getAllRecipesFor(ModRegistry.COMPACTING_RECIPE_TYPE.get());
        ModRegistry.COMPACTING_REGISTRY.setRecipeList(compactingRecipes);
        LOGGER.debug("Registered {} compacting recipes", compactingRecipes.size());
    }

    @SubscribeEvent
    public static void onRecipesUpdatedEvent(final RecipesUpdatedEvent event) {
        ModRegistry.COMPACTING_REGISTRY.clearRecipes();
        loadCompactingRecipes(event.getRecipeManager());
    }

    @SubscribeEvent
    public static void onServerStartingEvent(final ServerStartingEvent event) {
        if (event.getServer().isDedicatedServer()) {
            loadCompactingRecipes(event.getServer().getRecipeManager());
        }
    }
}
