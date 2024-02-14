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
import thedarkcolour.exdeorum.registry.EItems;

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

        var sieveTag = TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation("exmachinis:sieves"));
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

        var hammerItem = EItems.DIAMOND_HAMMER.get();
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
        CompactingRecipeBuilder.builder(Objects.requireNonNull(Items.IRON_ORE))
            .setInput(Ingredient.of(EItems.IRON_ORE_CHUNK.get()), 4)
            .build(consumer);

        CompactingRecipeBuilder.builder(Objects.requireNonNull(Items.GOLD_ORE))
            .setInput(Ingredient.of(EItems.GOLD_ORE_CHUNK.get()), 4)
            .build(consumer);

        CompactingRecipeBuilder.builder(Objects.requireNonNull(Items.COPPER_ORE))
            .setInput(Ingredient.of(EItems.COPPER_ORE_CHUNK.get()), 4)
            .build(consumer);

//        var defaultPieces = List.of(
//            EItems.LEAD_ORE_CHUNK,
//            EItems.NICKEL_ORE_CHUNK,
//            EItems.SILVER_ORE_CHUNK,
//            EItems.TIN_ORE_CHUNK,
//            EItems.ALUMINUM_ORE_CHUNK,
//            EItems.PLATINUM_ORE_CHUNK,
//            EItems.URANIUM_ORE_CHUNK,
//            EItems.ZINC_ORE_CHUNK
//        );
//
//        for (var piece : defaultPieces) {
//            var oreItem = Objects.requireNonNull(piece.getRawOreItem())
//                .map(ItemDefinition::asItem, item -> item);
//            CompactingRecipeBuilder.builder(oreItem)
//                .setInput(Ingredient.of(piece.getPieceItem()), 4)
//                .build(consumer);
//        }

        var pebbleMap = Map.ofEntries(
            Map.entry(Blocks.ANDESITE, EItems.ANDESITE_PEBBLE.get()),
            Map.entry(Blocks.BASALT, EItems.BASALT_PEBBLE.get()),
            Map.entry(Blocks.BLACKSTONE, EItems.BLACKSTONE_PEBBLE.get()),
            Map.entry(Blocks.STONE, EItems.STONE_PEBBLE.get()),
            Map.entry(Blocks.CALCITE, EItems.CALCITE_PEBBLE.get()),
            Map.entry(Blocks.DEEPSLATE, EItems.DEEPSLATE_PEBBLE.get()),
            Map.entry(Blocks.DIORITE, EItems.DIORITE_PEBBLE.get()),
            Map.entry(Blocks.GRANITE, EItems.GRANITE_PEBBLE.get()),
            Map.entry(Blocks.TUFF, EItems.TUFF_PEBBLE.get())
        );

        pebbleMap.forEach((block, pebble) -> CompactingRecipeBuilder.builder(block.asItem())
            .setInput(Ingredient.of(pebble), 4)
            .build(consumer));
    }

}
