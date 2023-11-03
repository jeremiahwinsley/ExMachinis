package net.permutated.exmachinis.data.server;

import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import net.permutated.exmachinis.ModRegistry;

public class BlockLoot extends VanillaBlockLoot {
    @Override
    protected void generate() {
        ModRegistry.BLOCKS.getEntries().forEach(block -> dropSelf(block.get()));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModRegistry.BLOCKS.getEntries().stream().map(RegistryObject::get).toList();
    }
}
