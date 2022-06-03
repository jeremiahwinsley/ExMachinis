package net.permutated.exmachinis.data.client;

import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import net.permutated.exmachinis.ExMachinis;
import net.permutated.exmachinis.ModRegistry;

import java.util.Objects;

public class BlockStates extends BlockStateProvider {
    public BlockStates(DataGenerator generator, ExistingFileHelper fileHelper) {
        super(generator, ExMachinis.MODID, fileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        machine(ModRegistry.FLUX_SIEVE_BLOCK, "netherite_block");
        machine(ModRegistry.FLUX_HAMMER_BLOCK, "diamond_block");
        machine(ModRegistry.FLUX_COMPACTOR_BLOCK, "emerald_block");
    }


    protected void machine(RegistryObject<Block> block, String texture) {
        String blockName = Objects.requireNonNull(block.get().getRegistryName()).toString();
        ModelFile model = models().cubeAll(blockName, new ResourceLocation("block/".concat(texture)));
        simpleBlock(block.get(), model);
        simpleBlockItem(block.get(), model);
    }
}
