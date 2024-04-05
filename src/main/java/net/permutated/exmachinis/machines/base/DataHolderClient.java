package net.permutated.exmachinis.machines.base;

import net.permutated.exmachinis.util.WorkStatus;

public class DataHolderClient implements DataHolder {
    private int work;
    private int maxWork;
    private int energy;
    private int maxEnergy;
    private WorkStatus workStatus;

    @Override
    public int getWork() {
        return work;
    }

    @Override
    public int getMaxWork() {
        return maxWork;
    }

    @Override
    public int getEnergy() {
        return energy;
    }

    @Override
    public int getMaxEnergy() {
        return maxEnergy;
    }

    @Override
    public WorkStatus getWorkStatus() {
        return workStatus;
    }

    @Override
    public void setWork(int work) {
        this.work = work;
    }

    @Override
    public void setMaxWork(int maxWork) {
        this.maxWork = maxWork;
    }

    @Override
    public void setEnergy(int energy) {
        this.energy = energy;
    }

    @Override
    public void setMaxEnergy(int maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    @Override
    public void setWorkStatus(int workStatus) {
        this.workStatus = WorkStatus.values()[workStatus];
    }
}
