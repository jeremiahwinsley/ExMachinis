package net.permutated.exmachinis.machines.base;

import net.permutated.exmachinis.util.WorkStatus;

public class DataHolderServer implements DataHolder {
    final AbstractMachineTile blockEntity;
    public DataHolderServer(AbstractMachineTile blockEntity) {
        this.blockEntity = blockEntity;
    }

    @Override
    public int getWork() {
        return blockEntity.getWork();
    }

    @Override
    public int getMaxWork() {
        return blockEntity.getMaxWork();
    }

    @Override
    public int getEnergy() {
        return blockEntity.energyStorage.getEnergyStored();
    }

    @Override
    public int getMaxEnergy() {
        return blockEntity.energyStorage.getMaxEnergyStored();
    }

    @Override
    public WorkStatus getWorkStatus() {
        return blockEntity.getWorkStatus();
    }
}
