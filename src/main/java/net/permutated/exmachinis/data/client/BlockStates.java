package net.permutated.exmachinis.data.client;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.permutated.exmachinis.ExMachinis;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.machines.hammer.FluxHammerBlock;
import net.permutated.exmachinis.util.Constants;

import static net.permutated.exmachinis.util.ResourceUtil.block;

public class BlockStates extends BlockStateProvider {
    public BlockStates(DataGenerator generator, ExistingFileHelper fileHelper) {
        super(generator, ExMachinis.MODID, fileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        sieveModel();
        hammerModel();
        compactorModel();
        crucibleModel();
    }

    protected void hammerModel() {
        var hammerBlock = ModRegistry.FLUX_HAMMER_BLOCK.get();
        ModelFile model = models().getExistingFile(block(Constants.FLUX_HAMMER));
        ModelFile hopperModel = models().getExistingFile(block(Constants.FLUX_HAMMER.concat("_hopper")));
        horizontalBlock(hammerBlock, blockState -> Boolean.TRUE.equals(blockState.getValue(FluxHammerBlock.HOPPER)) ? hopperModel : model);
        simpleBlockItem(hammerBlock, hopperModel);
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

    protected void crucibleModel() {
        var crucibleBlock = ModRegistry.FLUX_CRUCIBLE_BLOCK.get();
        ModelFile model = models().getExistingFile(block(Constants.FLUX_CRUCIBLE));
        simpleBlock(crucibleBlock, model);
        simpleBlockItem(crucibleBlock, model);
    }
}
