package net.permutated.exmachinis;

import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.constant.EmptyPart;
import com.mojang.datafixers.util.Unit;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.permutated.exmachinis.items.UpgradeItem;
import net.permutated.exmachinis.machines.base.AbstractMachineTile;
import net.permutated.exmachinis.machines.compactor.FluxCompactorBlock;
import net.permutated.exmachinis.machines.compactor.FluxCompactorMenu;
import net.permutated.exmachinis.machines.compactor.FluxCompactorTile;
import net.permutated.exmachinis.machines.hammer.FluxHammerBlock;
import net.permutated.exmachinis.machines.hammer.FluxHammerMenu;
import net.permutated.exmachinis.machines.hammer.FluxHammerTile;
import net.permutated.exmachinis.machines.sieve.FluxSieveBlock;
import net.permutated.exmachinis.machines.sieve.FluxSieveMenu;
import net.permutated.exmachinis.machines.sieve.FluxSieveTile;
import net.permutated.exmachinis.recipes.CompactingRecipe;
import net.permutated.exmachinis.recipes.CompactingRegistry;
import net.permutated.exmachinis.util.Constants;
import net.permutated.exmachinis.util.TranslationKey;

import java.util.function.Supplier;

import static net.permutated.exmachinis.util.ResourceUtil.prefix;

public class ModRegistry {
    private ModRegistry() {
        // nothing to do
    }

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ExMachinis.MODID);
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ExMachinis.MODID);
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ExMachinis.MODID);
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, ExMachinis.MODID);

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, ExMachinis.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, ExMachinis.MODID);

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ExMachinis.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register("creative_tab", () -> CreativeModeTab.builder()
        .title(Component.translatable(TranslationKey.tab()))
        .icon(() -> ModRegistry.NETHERITE_UPGRADE.get().getDefaultInstance())
        .displayItems((parameters, output) -> ITEMS.getEntries().stream()
            .map(Supplier::get)
            .map(Item::getDefaultInstance)
            .forEach(output::accept))
        .build()
    );

    // bulk upgrades, add efficiency/speed/something else?
    public static final DeferredItem<Item> GOLD_UPGRADE = upgradeItem(Constants.GOLD_UPGRADE, UpgradeItem.Tier.GOLD);
    public static final DeferredItem<Item> DIAMOND_UPGRADE = upgradeItem(Constants.DIAMOND_UPGRADE, UpgradeItem.Tier.DIAMOND);
    public static final DeferredItem<Item> NETHERITE_UPGRADE = upgradeItem(Constants.NETHERITE_UPGRADE, UpgradeItem.Tier.NETHERITE);


    // Flux Sieve
    public static final DeferredBlock<FluxSieveBlock> FLUX_SIEVE_BLOCK = BLOCKS.register(Constants.FLUX_SIEVE, FluxSieveBlock::new);
    public static final Supplier<BlockEntityType<FluxSieveTile>> FLUX_SIEVE_TILE = blockEntity(FLUX_SIEVE_BLOCK, FluxSieveTile::new);
    public static final Supplier<MenuType<FluxSieveMenu>> FLUX_SIEVE_MENU = container(Constants.FLUX_SIEVE, FluxSieveMenu::new);
    public static final DeferredItem<BlockItem> FLUX_SIEVE_ITEM = blockItem(FLUX_SIEVE_BLOCK);

    // Flux Hammer
    public static final DeferredBlock<FluxHammerBlock> FLUX_HAMMER_BLOCK = BLOCKS.register(Constants.FLUX_HAMMER, FluxHammerBlock::new);
    public static final Supplier<BlockEntityType<FluxHammerTile>> FLUX_HAMMER_TILE = blockEntity(FLUX_HAMMER_BLOCK, FluxHammerTile::new);
    public static final Supplier<MenuType<FluxHammerMenu>> FLUX_HAMMER_MENU = container(Constants.FLUX_HAMMER, FluxHammerMenu::new);
    public static final DeferredItem<BlockItem> FLUX_HAMMER_ITEM = blockItem(FLUX_HAMMER_BLOCK);

    // Flux Compactor
    public static final DeferredBlock<FluxCompactorBlock> FLUX_COMPACTOR_BLOCK = BLOCKS.register(Constants.FLUX_COMPACTOR, FluxCompactorBlock::new);
    public static final Supplier<BlockEntityType<FluxCompactorTile>> FLUX_COMPACTOR_TILE = blockEntity(FLUX_COMPACTOR_BLOCK, FluxCompactorTile::new);
    public static final Supplier<MenuType<FluxCompactorMenu>> FLUX_COMPACTOR_MENU = container(Constants.FLUX_COMPACTOR, FluxCompactorMenu::new);
    public static final DeferredItem<BlockItem> FLUX_COMPACTOR_ITEM = blockItem(FLUX_COMPACTOR_BLOCK);

    public static final Supplier<RecipeType<CompactingRecipe>> COMPACTING_RECIPE_TYPE = RECIPE_TYPES.register(Constants.COMPACTING, () -> RecipeType.simple(prefix(Constants.COMPACTING)));
    public static final Supplier<CompactingRecipe.Serializer> COMPACTING_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register(Constants.COMPACTING, CompactingRecipe.Serializer::new);
    public static final CompactingRegistry COMPACTING_REGISTRY = new CompactingRegistry();

    /**
     * Register a BlockItem for a Block
     *
     * @param holder the Block
     * @return the new registry object
     */
    private static DeferredItem<BlockItem> blockItem(Holder<Block> holder) {
        return ITEMS.register(holder.unwrapKey().map(ResourceKey::location).map(ResourceLocation::getPath).orElseThrow(),
            () -> new BlockItem(holder.value(), new Item.Properties()));
    }

    /**
     * Register an UpgradeItem of a specific tier
     * @param name the base name for the upgrade
     * @param tier the upgrade tier
     * @return the new registry object
     */
    public static DeferredItem<Item> upgradeItem(String name, UpgradeItem.Tier tier) {
        return ITEMS.register(name, () -> new UpgradeItem(tier));
    }

    /**
     * Used as a NOOP type for the tile registry builder to avoid passing null
     *
     * @see BlockEntityType.Builder#build(Type)
     * @see #blockEntity(Holder, BlockEntityType.BlockEntitySupplier)
     */
    private static final Type<Unit> EMPTY_PART = new EmptyPart();

    /**
     * Register a tile entity for a Block
     *
     * @param holder   a Holder containing a Block
     * @param supplier a Supplier that returns the new Block Entity
     * @return the new registry object
     */
    private static <T extends AbstractMachineTile> Supplier<BlockEntityType<T>> blockEntity(Holder<Block> holder, BlockEntityType.BlockEntitySupplier<T> supplier) {
        return TILES.register(holder.unwrapKey().map(ResourceKey::location).map(ResourceLocation::getPath).orElseThrow(),
            () -> BlockEntityType.Builder.of(supplier, holder.value()).build(EMPTY_PART));
    }

    private static <T extends AbstractContainerMenu> Supplier<MenuType<T>> container(String path, IContainerFactory<T> supplier) {
        return CONTAINERS.register(path, () -> IMenuTypeExtension.create(supplier));
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
        BLOCKS.register(bus);
        TILES.register(bus);
        CONTAINERS.register(bus);
        RECIPE_TYPES.register(bus);
        RECIPE_SERIALIZERS.register(bus);
        CREATIVE_TABS.register(bus);
    }
}
