package net.permutated.exmachinis;

import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.constant.EmptyPart;
import com.mojang.datafixers.util.Unit;
import net.minecraft.core.Registry;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.permutated.exmachinis.items.UpgradeItem;
import net.permutated.exmachinis.machines.base.AbstractMachineTile;
import net.permutated.exmachinis.machines.compactor.FluxCompactorBlock;
import net.permutated.exmachinis.machines.compactor.FluxCompactorMenu;
import net.permutated.exmachinis.machines.compactor.FluxCompactorTile;
import net.permutated.exmachinis.machines.crucible.FluxCrucibleBlock;
import net.permutated.exmachinis.machines.crucible.FluxCrucibleMenu;
import net.permutated.exmachinis.machines.crucible.FluxCrucibleTile;
import net.permutated.exmachinis.machines.hammer.FluxHammerBlock;
import net.permutated.exmachinis.machines.hammer.FluxHammerMenu;
import net.permutated.exmachinis.machines.hammer.FluxHammerTile;
import net.permutated.exmachinis.machines.sieve.FluxSieveBlock;
import net.permutated.exmachinis.machines.sieve.FluxSieveMenu;
import net.permutated.exmachinis.machines.sieve.FluxSieveTile;
import net.permutated.exmachinis.recipes.CompactingRecipe;
import net.permutated.exmachinis.recipes.CompactingRegistry;
import net.permutated.exmachinis.util.Constants;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

import static net.permutated.exmachinis.util.ResourceUtil.prefix;

public class ModRegistry {
    private ModRegistry() {
        // nothing to do
    }

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ExMachinis.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ExMachinis.MODID);
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ExMachinis.MODID);
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, ExMachinis.MODID);

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registry.RECIPE_TYPE_REGISTRY, ExMachinis.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ExMachinis.MODID);


    public static final CreativeModeTab CREATIVE_TAB = new ModItemGroup(ExMachinis.MODID,
        () -> new ItemStack(ModRegistry.NETHERITE_UPGRADE.get()));

    // bulk upgrades, add efficiency/speed/something else?
    public static final RegistryObject<Item> GOLD_UPGRADE = upgradeItem(Constants.GOLD_UPGRADE, UpgradeItem.Tier.GOLD);
    public static final RegistryObject<Item> DIAMOND_UPGRADE = upgradeItem(Constants.DIAMOND_UPGRADE, UpgradeItem.Tier.DIAMOND);
    public static final RegistryObject<Item> NETHERITE_UPGRADE = upgradeItem(Constants.NETHERITE_UPGRADE, UpgradeItem.Tier.NETHERITE);


    // Flux Sieve
    public static final RegistryObject<Block> FLUX_SIEVE_BLOCK = BLOCKS.register(Constants.FLUX_SIEVE, FluxSieveBlock::new);
    public static final RegistryObject<BlockEntityType<FluxSieveTile>> FLUX_SIEVE_TILE = blockEntity(FLUX_SIEVE_BLOCK, FluxSieveTile::new);
    public static final RegistryObject<MenuType<FluxSieveMenu>> FLUX_SIEVE_MENU = container(Constants.FLUX_SIEVE, FluxSieveMenu::new);
    public static final RegistryObject<BlockItem> FLUX_SIEVE_ITEM = blockItem(FLUX_SIEVE_BLOCK);

    // Flux Hammer
    public static final RegistryObject<Block> FLUX_HAMMER_BLOCK = BLOCKS.register(Constants.FLUX_HAMMER, FluxHammerBlock::new);
    public static final RegistryObject<BlockEntityType<FluxHammerTile>> FLUX_HAMMER_TILE = blockEntity(FLUX_HAMMER_BLOCK, FluxHammerTile::new);
    public static final RegistryObject<MenuType<FluxHammerMenu>> FLUX_HAMMER_MENU = container(Constants.FLUX_HAMMER, FluxHammerMenu::new);
    public static final RegistryObject<BlockItem> FLUX_HAMMER_ITEM = blockItem(FLUX_HAMMER_BLOCK);

    // Flux Compactor
    public static final RegistryObject<Block> FLUX_COMPACTOR_BLOCK = BLOCKS.register(Constants.FLUX_COMPACTOR, FluxCompactorBlock::new);
    public static final RegistryObject<BlockEntityType<FluxCompactorTile>> FLUX_COMPACTOR_TILE = blockEntity(FLUX_COMPACTOR_BLOCK, FluxCompactorTile::new);
    public static final RegistryObject<MenuType<FluxCompactorMenu>> FLUX_COMPACTOR_MENU = container(Constants.FLUX_COMPACTOR, FluxCompactorMenu::new);
    public static final RegistryObject<BlockItem> FLUX_COMPACTOR_ITEM = blockItem(FLUX_COMPACTOR_BLOCK);

    // Flux Crucible
    public static final RegistryObject<Block> FLUX_CRUCIBLE_BLOCK = BLOCKS.register(Constants.FLUX_CRUCIBLE, FluxCrucibleBlock::new);
    public static final RegistryObject<BlockEntityType<FluxCrucibleTile>> FLUX_CRUCIBLE_TILE = blockEntity(FLUX_CRUCIBLE_BLOCK, FluxCrucibleTile::new);
    public static final RegistryObject<MenuType<FluxCrucibleMenu>> FLUX_CRUCIBLE_MENU = container(Constants.FLUX_CRUCIBLE, FluxCrucibleMenu::new);
    public static final RegistryObject<BlockItem> FLUX_CRUCIBLE_ITEM = blockItem(FLUX_CRUCIBLE_BLOCK);

    public static final RegistryObject<RecipeType<CompactingRecipe>> COMPACTING_RECIPE_TYPE = RECIPE_TYPES.register(Constants.COMPACTING, () -> RecipeType.simple(prefix(Constants.COMPACTING)));
    public static final RegistryObject<RecipeSerializer<CompactingRecipe>> COMPACTING_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register(Constants.COMPACTING, CompactingRecipe.Serializer::new);
    public static final CompactingRegistry COMPACTING_REGISTRY = new CompactingRegistry();

    /**
     * Register a BlockItem for a Block
     *
     * @param registryObject the Block
     * @return the new registry object
     */
    private static RegistryObject<BlockItem> blockItem(RegistryObject<Block> registryObject) {
        return ITEMS.register(registryObject.getId().getPath(),
            () -> new BlockItem(registryObject.get(), new Item.Properties().tab(CREATIVE_TAB)));
    }

    /**
     * Register an UpgradeItem of a specific tier
     * @param name the base name for the upgrade
     * @param tier the upgrade tier
     * @return the new registry object
     */
    public static RegistryObject<Item> upgradeItem(String name, UpgradeItem.Tier tier) {
        return ITEMS.register(name, () -> new UpgradeItem(tier));
    }

    /**
     * Used as a NOOP type for the tile registry builder to avoid passing null
     *
     * @see BlockEntityType.Builder#build(Type)
     * @see #blockEntity(RegistryObject, BlockEntityType.BlockEntitySupplier)
     */
    private static final Type<Unit> EMPTY_PART = new EmptyPart();

    /**
     * Register a tile entity for a Block
     *
     * @param registryObject a registry object containing a Block
     * @param supplier       a Supplier that returns the new Block Entity
     * @return the new registry object
     */
    private static <T extends AbstractMachineTile> RegistryObject<BlockEntityType<T>> blockEntity(RegistryObject<Block> registryObject, BlockEntityType.BlockEntitySupplier<T> supplier) {
        return TILES.register(registryObject.getId().getPath(),
            () -> BlockEntityType.Builder.of(supplier, registryObject.get()).build(EMPTY_PART));
    }

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> container(String path, IContainerFactory<T> supplier) {
        return CONTAINERS.register(path, () -> IForgeMenuType.create(supplier));
    }

    public static void register() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        BLOCKS.register(bus);
        TILES.register(bus);
        CONTAINERS.register(bus);
        RECIPE_TYPES.register(bus);
        RECIPE_SERIALIZERS.register(bus);
    }

    public static final class ModItemGroup extends CreativeModeTab {
        private final Supplier<ItemStack> iconSupplier;

        public ModItemGroup(final String name, final Supplier<ItemStack> iconSupplier) {
            super(name);
            this.iconSupplier = iconSupplier;
        }

        @Nonnull
        @Override
        public ItemStack makeIcon() {
            return iconSupplier.get();
        }
    }
}
