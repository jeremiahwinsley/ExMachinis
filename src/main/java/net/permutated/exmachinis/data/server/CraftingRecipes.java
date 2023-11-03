package net.permutated.exmachinis.data.server;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import net.permutated.exmachinis.ExMachinis;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.data.builders.CompactingRecipeBuilder;
import novamachina.exnihilosequentia.world.item.EXNItems;
import novamachina.novacore.world.item.ItemDefinition;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class CraftingRecipes extends RecipeProvider {
    public CraftingRecipes(PackOutput packOutput) {
        super(packOutput);
    }

    private ShapedRecipeBuilder shaped(ItemLike provider) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.MISC, provider)
            .group(ExMachinis.MODID);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        buildCraftingRecipes(consumer);
        buildCompactingRecipes(consumer);
    }

    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        shaped(ModRegistry.GOLD_UPGRADE.get())
            .pattern("tct")
            .pattern("gdg")
            .pattern("ggg")
            .define('t', Items.CYAN_TERRACOTTA)
            .define('c', Tags.Items.DYES_GREEN)
            .define('d', Tags.Items.INGOTS_GOLD)
            .define('g', Tags.Items.GLASS)
            .unlockedBy("has_gold_ingot", has(Tags.Items.INGOTS_GOLD))
            .save(consumer);

        shaped(ModRegistry.DIAMOND_UPGRADE.get())
            .pattern("nun")
            .pattern("nun")
            .pattern("nun")
            .define('u', ModRegistry.GOLD_UPGRADE.get())
            .define('n', Tags.Items.GEMS_DIAMOND)
            .unlockedBy("has_diamond", has(Tags.Items.GEMS_DIAMOND))
            .save(consumer);

        shaped(ModRegistry.NETHERITE_UPGRADE.get())
            .pattern("nun")
            .pattern("nun")
            .pattern("nun")
            .define('u', ModRegistry.DIAMOND_UPGRADE.get())
            .define('n', Tags.Items.INGOTS_NETHERITE)
            .unlockedBy("has_netherite_ingot", has(Tags.Items.INGOTS_NETHERITE))
            .save(consumer);

        var sieveTag = TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation("exnihilosequentia:sieves"));
        shaped(ModRegistry.FLUX_SIEVE_ITEM.get())
            .pattern("bbb")
            .pattern("bsb")
            .pattern("ihi")
            .define('b', Items.IRON_BARS)
            .define('s', sieveTag)
            .define('i', Tags.Items.STORAGE_BLOCKS_IRON)
            .define('h', Items.HOPPER)
            .unlockedBy("has_sieve", has(sieveTag))
            .save(consumer);

        var hammerItem = EXNItems.HAMMER_DIAMOND.asItem();
        shaped(ModRegistry.FLUX_HAMMER_ITEM.get())
            .pattern("ggg")
            .pattern("gdg")
            .pattern("ihi")
            .define('g', Tags.Items.GLASS_PANES)
            .define('d', hammerItem)
            .define('i', Tags.Items.STORAGE_BLOCKS_IRON)
            .define('h', Items.HOPPER)
            .unlockedBy("has_diamond_hammer", has(hammerItem))
            .save(consumer);

        shaped(ModRegistry.FLUX_COMPACTOR_ITEM.get())
            .pattern("ipi")
            .pattern("pcp")
            .pattern("ihi")
            .define('i', Tags.Items.STORAGE_BLOCKS_IRON)
            .define('p', Items.PISTON)
            .define('c', Items.COMPARATOR)
            .define('h', Items.HOPPER)
            .unlockedBy("has_anvil", has(Items.ANVIL))
            .save(consumer);
    }

    protected void buildCompactingRecipes(Consumer<FinishedRecipe> consumer) {
        CompactingRecipeBuilder.builder(Objects.requireNonNull(Items.RAW_IRON))
            .setInput(Ingredient.of(EXNItems.IRON.getPieceItem()), 4)
            .build(consumer);

        CompactingRecipeBuilder.builder(Objects.requireNonNull(Items.RAW_GOLD))
            .setInput(Ingredient.of(EXNItems.GOLD.getPieceItem()), 4)
            .build(consumer);

        CompactingRecipeBuilder.builder(Objects.requireNonNull(Items.RAW_COPPER))
            .setInput(Ingredient.of(EXNItems.COPPER.getPieceItem()), 4)
            .build(consumer);

        var defaultPieces = List.of(
            EXNItems.LEAD,
            EXNItems.NICKEL,
            EXNItems.SILVER,
            EXNItems.TIN,
            EXNItems.ALUMINUM,
            EXNItems.PLATINUM,
            EXNItems.URANIUM,
            EXNItems.ZINC
        );

        for (var piece : defaultPieces) {
            var oreItem = Objects.requireNonNull(piece.getRawOreItem())
                .map(ItemDefinition::asItem, item -> item);
            CompactingRecipeBuilder.builder(oreItem)
                .setInput(Ingredient.of(piece.getPieceItem()), 4)
                .build(consumer);
        }

        // novamachina.exnihilosequentia.data.recipes.providers.CraftingRecipes#addPebbleBlocks
        var pebbleMap = Map.ofEntries(
            Map.entry(Blocks.ANDESITE, EXNItems.PEBBLE_ANDESITE.asItem()),
            Map.entry(Blocks.BASALT, EXNItems.PEBBLE_BASALT.asItem()),
            Map.entry(Blocks.BLACKSTONE, EXNItems.PEBBLE_BLACKSTONE.asItem()),
            Map.entry(Blocks.COBBLESTONE, EXNItems.PEBBLE_STONE.asItem()),
            Map.entry(Blocks.CALCITE, EXNItems.PEBBLE_CALCITE.asItem()),
            Map.entry(Blocks.DEEPSLATE, EXNItems.PEBBLE_DEEPSLATE.asItem()),
            Map.entry(Blocks.DIORITE, EXNItems.PEBBLE_DIORITE.asItem()),
            Map.entry(Blocks.DRIPSTONE_BLOCK, EXNItems.PEBBLE_DRIPSTONE.asItem()),
            Map.entry(Blocks.END_STONE, EXNItems.PEBBLE_END_STONE.asItem()),
            Map.entry(Blocks.GRANITE, EXNItems.PEBBLE_GRANITE.asItem()),
            Map.entry(Blocks.NETHERRACK, EXNItems.PEBBLE_NETHERRACK.asItem()),
            Map.entry(Blocks.TUFF, EXNItems.PEBBLE_TUFF.asItem())
        );

        pebbleMap.forEach((block, pebble) -> CompactingRecipeBuilder.builder(block.asItem())
            .setInput(Ingredient.of(pebble), 4)
            .build(consumer));
    }

}
