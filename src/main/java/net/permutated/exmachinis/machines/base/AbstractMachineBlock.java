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

import javax.annotation.Nullable;

public abstract class AbstractMachineBlock extends Block implements EntityBlock {

    protected AbstractMachineBlock() {
        super(Properties.of(Material.METAL).strength(3.0F, 3.0F));
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
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (!state.is(newState.getBlock()))
        {
            if (level.getBlockEntity(pos) instanceof AbstractMachineTile machineTile)
            {
                machineTile.dropItems();
                level.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    @SuppressWarnings("java:S1874") // deprecated method from super class
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockRayTraceResult)
    {
        if (!world.isClientSide) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof AbstractMachineTile machineTile) {
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
