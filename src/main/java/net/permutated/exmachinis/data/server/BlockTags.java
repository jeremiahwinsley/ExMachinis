package net.permutated.exmachinis.data.server;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.ExMachinis;
import net.permutated.exmachinis.machines.base.AbstractMachineBlock;

import javax.annotation.Nullable;

import static net.permutated.exmachinis.util.ResourceUtil.blockTag;

public class BlockTags extends BlockTagsProvider {
    public BlockTags(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, ExMachinis.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        Block[] machines = ModRegistry.BLOCKS.getEntries().stream()
            .map(RegistryObject::get)
            .filter(AbstractMachineBlock.class::isInstance)
            .toArray(Block[]::new);

        tag(blockTag("minecraft:mineable/pickaxe")).add(machines);
        tag(blockTag("create:brittle")).add(machines);
    }
}
