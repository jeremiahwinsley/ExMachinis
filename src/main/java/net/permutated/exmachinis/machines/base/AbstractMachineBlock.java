package net.permutated.exmachinis.machines.base;

import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.network.NetworkHooks;
import net.permutated.exmachinis.ExMachinis;
import net.permutated.exmachinis.items.UpgradeItem;

import javax.annotation.Nullable;

public abstract class AbstractMachineBlock extends Block implements EntityBlock {

    protected AbstractMachineBlock() {
        super(Properties.of(Material.METAL).strength(3.0F, 3.0F).noOcclusion());

    }

    public abstract IContainerFactory<AbstractMachineMenu> containerFactory();

    @SuppressWarnings("java:S1452") // wildcard required here
    public abstract BlockEntityType<? extends AbstractMachineTile> getTileType();


    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == getTileType() ? AbstractMachineTile::tick : null;
    }


    @Override
    public void destroy(LevelAccessor world, BlockPos blockPos, BlockState blockState) {
        if (world.getBlockEntity(blockPos) instanceof AbstractMachineTile machineTile) {
            machineTile.dropItems();
        }

        super.destroy(world, blockPos, blockState);
    }


    @Override
    @SuppressWarnings("java:S1874") // deprecated method from super class
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof AbstractMachineTile machineTile) {
                machineTile.dropItems();
                level.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    @SuppressWarnings("java:S1874") // deprecated method from super class
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockRayTraceResult) {
        if (!world.isClientSide) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof AbstractMachineTile machineTile) {
                ItemStack stackInHand = player.getItemInHand(hand).copy();
                if (!stackInHand.isEmpty() && stackInHand.getItem() instanceof UpgradeItem handItem) {
                    ItemStack inSlot = machineTile.upgradeStackHandler.getStackInSlot(0).copy();
                    if (inSlot.isEmpty()) { // no upgrades in machine, move the whole stack
                        ItemStack result = machineTile.upgradeStackHandler.insertItem(0, stackInHand, true);
                        if (result.isEmpty()) { // verify that all items were inserted
                            ItemStack actual = machineTile.upgradeStackHandler.insertItem(0, stackInHand, false);
                            player.setItemInHand(hand, actual);
                            return InteractionResult.SUCCESS;
                        }
                    } else if (inSlot.sameItem(stackInHand)) { // same upgrade type in machine
                        if (inSlot.getCount() < inSlot.getMaxStackSize()) { // make sure the stack size is not max already
                            ItemStack result = machineTile.upgradeStackHandler.insertItem(0, stackInHand, true);
                            int maxInsert = inSlot.getMaxStackSize() - inSlot.getCount(); // the max that can be inserted in the slot.
                            int expectedToRemain = Math.max(stackInHand.getCount() - maxInsert, 0); // if max is greater than count, clamp to 0
                            if (expectedToRemain == result.getCount()) { // verify that the simulated count matches the expected count
                                ItemStack actual = machineTile.upgradeStackHandler.insertItem(0, stackInHand, false);
                                player.setItemInHand(hand, actual);
                                return InteractionResult.SUCCESS;
                            }
                        }
                    } else if (inSlot.getItem() instanceof UpgradeItem slotItem && handItem.getTier().compareTo(slotItem.getTier()) > 0) {
                        // handItem is a higher tier UpgradeItem, so swap slots
                        machineTile.upgradeStackHandler.setStackInSlot(0, stackInHand);
                        player.setItemInHand(hand, inSlot);
                        return InteractionResult.SUCCESS;
                    }
                }

                MenuProvider containerProvider = new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return new TranslatableComponent(getDescriptionId());
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
                        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                        machineTile.updateContainer(buffer);
                        return containerFactory().create(i, playerInventory, buffer);
                    }
                };
                NetworkHooks.openGui((ServerPlayer) player, containerProvider, machineTile::updateContainer);
            } else {
                ExMachinis.LOGGER.error("tile entity not instance of AbstractMachineTile");
                return InteractionResult.FAIL;
            }
        }
        return InteractionResult.SUCCESS;
    }

}
