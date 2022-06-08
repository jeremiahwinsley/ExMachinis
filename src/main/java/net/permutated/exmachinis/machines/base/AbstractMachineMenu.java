package net.permutated.exmachinis.machines.base;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.registries.RegistryObject;
import net.permutated.exmachinis.util.WorkStatus;

import javax.annotation.Nullable;

public abstract class AbstractMachineMenu extends AbstractContainerMenu {

    @Nullable // should only be accessed from server
    private final AbstractMachineTile tileEntity;

    protected boolean enableMeshSlot;
    protected final BlockPos blockPos;

    protected int totalSlots = 0;

    protected AbstractMachineMenu(@Nullable MenuType<?> containerType, int windowId, Inventory playerInventory, FriendlyByteBuf packetBuffer) {
        super(containerType, windowId);

        enableMeshSlot = packetBuffer.readBoolean();
        blockPos = packetBuffer.readBlockPos();

        Level world = playerInventory.player.getCommandSenderWorld();

        tileEntity = (AbstractMachineTile) world.getBlockEntity(blockPos);
        IItemHandler wrappedInventory = new InvWrapper(playerInventory);

        //TODO move to data holder
        tileEntity.setWorkStatus(packetBuffer.readEnum(WorkStatus.class));

        if (tileEntity != null) {
            tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
                int index = 0;
                addSlot(new SlotItemHandler(handler, index++, 116, 53));
                if (enableMeshSlot) {
                    addSlot(new SlotItemHandler(handler, index++, 80, 36));
                }

                // 3 x 3
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        addSlot(new SlotItemHandler(handler, index++, 8 + j * 18, 18 + i * 18));
                    }
                }
            });
        }

        registerPlayerSlots(wrappedInventory);
        registerDataSlots();
    }

    protected abstract RegistryObject<Block> getBlock();

    protected WorkStatus getWorkStatus() {
        return tileEntity.getWorkStatus();
    }

    @Override
    public boolean stillValid(Player playerEntity) {
        if (tileEntity != null) {
            Level world = tileEntity.getLevel();
            if (world != null) {
                ContainerLevelAccess callable = ContainerLevelAccess.create(world, tileEntity.getBlockPos());
                return stillValid(callable, playerEntity, getBlock().get());
            }
        }
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        int inventorySize = totalSlots;

        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();

            if (index < inventorySize) {
                if (!this.moveItemStackTo(stack, inventorySize, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, inventorySize, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    public void registerPlayerSlots(IItemHandler wrappedInventory) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new SlotItemHandler(wrappedInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlot(new SlotItemHandler(wrappedInventory, i, 8 + i * 18, 142));
        }
    }

    public void registerDataSlots() {

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return tileEntity.getWorkStatus().ordinal();
            }

            @Override
            public void set(int value) {
                tileEntity.setWorkStatus(WorkStatus.values()[value]);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return tileEntity.getWork();
            }

            @Override
            public void set(int value) {
                tileEntity.setWork(value);
            }
        });

        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return getEnergy() & 0xffff;
            }

            @Override
            public void set(int value) {
                tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(energy -> {
                    int energyStored = energy.getEnergyStored() & 0xffff0000;
                    ((AbstractMachineTile.MachineEnergyStorage)energy).setEnergy(energyStored + (value & 0xffff));
                });
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return (getEnergy() >> 16) & 0xffff;
            }

            @Override
            public void set(int value) {
                tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(energy -> {
                    int energyStored = energy.getEnergyStored() & 0x0000ffff;
                    ((AbstractMachineTile.MachineEnergyStorage)energy).setEnergy(energyStored | (value << 16));
                });
            }
        });
    }

    public int getEnergy() {
        return tileEntity.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
    }

    public int getMaxEnergy() {
        return tileEntity.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0);
    }

    public int getWork() {
        return tileEntity.getWork();
    }

    public int getMaxWork() {
        return tileEntity.getMaxWork();
    }

    public float getWorkFraction() {
        if (getWork() == 0) {
            return 0f;
        } else {
            return ((float) getWork()) / getMaxWork();
        }
    }

    public float getEnergyFraction() {
        if(getEnergy() == 0) {
            return 0f;
        } else {
            return ((float) getEnergy()) / getMaxEnergy();
        }
    }
}
