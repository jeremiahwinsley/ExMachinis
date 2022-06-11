package net.permutated.exmachinis.machines.base;

import net.minecraft.util.Mth;
import net.permutated.exmachinis.util.WorkStatus;

public interface DataHolder {
    int getWork();
    int getMaxWork();
    int getEnergy();
    int getMaxEnergy();
    WorkStatus getWorkStatus();

    default void setWork(int work) {}
    default void setMaxWork(int maxWork) {}
    default void setEnergy(int energy) {}
    default void setMaxEnergy(int maxEnergy) {}
    default void setWorkStatus(int workStatus) {}

    default float getWorkFraction() {
        if (getWork() == 0) {
            return 0f;
        } else {
            return ((float) Mth.clamp(getWork(), 0, getMaxWork())) / getMaxWork();
        }
    }

    default float getEnergyFraction() {
        if (getEnergy() == 0) {
            return 0f;
        } else {
            return ((float) Mth.clamp(getEnergy(), 0, getMaxEnergy())) / getMaxEnergy();
        }
    }
}
