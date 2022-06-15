package net.permutated.exmachinis.data.client;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import net.permutated.exmachinis.ExMachinis;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.machines.hammer.FluxHammerBlock;
import net.permutated.exmachinis.util.Constants;
import net.permutated.exmachinis.util.ResourceUtil;

import java.util.Objects;

import static net.permutated.exmachinis.util.ResourceUtil.block;
import static net.permutated.exmachinis.util.ResourceUtil.prefix;

public class BlockStates extends BlockStateProvider {
    public BlockStates(DataGenerator generator, ExistingFileHelper fileHelper) {
        super(generator, ExMachinis.MODID, fileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        sieveModel();
        hammerModel();
        compactorModel();
    }

    protected void hammerModel() {
        var hammerBlock = ModRegistry.FLUX_HAMMER_BLOCK.get();
        ModelFile model = models().getExistingFile(block(Constants.FLUX_HAMMER));
        ModelFile hopperModel = models().getExistingFile(block(Constants.FLUX_HAMMER.concat("_hopper")));
        horizontalBlock(hammerBlock, blockState -> Boolean.TRUE.equals(blockState.getValue(FluxHammerBlock.HOPPER)) ? hopperModel : model);
        simpleBlockItem(hammerBlock, model);
    }

    protected void sieveModel() {
        var sieveBlock = ModRegistry.FLUX_SIEVE_BLOCK.get();
        ModelFile model = models().getExistingFile(block(Constants.FLUX_SIEVE));
        simpleBlock(sieveBlock, model);
        simpleBlockItem(sieveBlock, model);
    }

    protected void compactorModel() {
        var compactorBlock = ModRegistry.FLUX_COMPACTOR_BLOCK.get();
        ModelFile model = models().cubeAll(Constants.FLUX_COMPACTOR, block(Constants.FLUX_COMPACTOR));
        simpleBlock(compactorBlock, model);
        simpleBlockItem(compactorBlock, model);
    }
}
