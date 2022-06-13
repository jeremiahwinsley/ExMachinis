package net.permutated.exmachinis.data.client;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import net.permutated.exmachinis.ExMachinis;
import net.permutated.exmachinis.ModRegistry;

import java.util.Objects;

import static net.permutated.exmachinis.util.ResourceUtil.prefix;

public class BlockStates extends BlockStateProvider {
    public BlockStates(DataGenerator generator, ExistingFileHelper fileHelper) {
        super(generator, ExMachinis.MODID, fileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        machine(ModRegistry.FLUX_SIEVE_BLOCK);
        directional(ModRegistry.FLUX_HAMMER_BLOCK);
        machine(ModRegistry.FLUX_COMPACTOR_BLOCK, "emerald_block");
    }

    protected void directional(RegistryObject<Block> block) {
        String blockName = Objects.requireNonNull(block.get().getRegistryName()).getPath();
        ModelFile model = models().getExistingFile(prefix("block/".concat(blockName)));
        horizontalBlock(block.get(), model);
        simpleBlockItem(block.get(), model);
    }

    protected void machine(RegistryObject<Block> block) {
        String blockName = Objects.requireNonNull(block.get().getRegistryName()).getPath();
        ModelFile model = models().getExistingFile(prefix("block/".concat(blockName)));
        simpleBlock(block.get(), model);
        simpleBlockItem(block.get(), model);
    }

    protected void machine(RegistryObject<Block> block, String texture) {
        String blockName = Objects.requireNonNull(block.get().getRegistryName()).toString();
        ModelFile model = models().cubeAll(blockName, new ResourceLocation("block/".concat(texture)));
        simpleBlock(block.get(), model);
        simpleBlockItem(block.get(), model);
    }
}
