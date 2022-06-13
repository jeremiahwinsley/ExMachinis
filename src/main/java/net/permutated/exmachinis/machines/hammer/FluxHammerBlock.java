package net.permutated.exmachinis.machines.hammer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.network.IContainerFactory;
import net.permutated.exmachinis.ModRegistry;
import net.permutated.exmachinis.machines.base.AbstractMachineBlock;
import net.permutated.exmachinis.machines.base.AbstractMachineMenu;
import net.permutated.exmachinis.machines.base.AbstractMachineTile;
import net.permutated.exmachinis.util.BlockUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Stream;

import static net.permutated.exmachinis.util.TranslationKey.translateTooltip;

public class FluxHammerBlock extends AbstractMachineBlock {

    private static final VoxelShape NORTH_SHAPE = Stream.of(
        Block.box(0, 0, 2, 16, 10, 15),
        Block.box(5, 10, 7, 13, 13, 15),
        Shapes.join(Block.box(13, 12, 6, 15, 16, 14),
            Shapes.join(Block.box(3, 12, 14, 15, 16, 16),
                Shapes.join(Block.box(3, 12, 6, 5, 16, 14),
                    Block.box(3, 12, 4, 15, 16, 6), BooleanOp.AND), BooleanOp.AND), BooleanOp.AND)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape SOUTH_SHAPE = BlockUtil.rotateShape(Direction.NORTH, Direction.SOUTH, NORTH_SHAPE);
    private static final VoxelShape EAST_SHAPE = BlockUtil.rotateShape(Direction.NORTH, Direction.EAST, NORTH_SHAPE);
    private static final VoxelShape WEST_SHAPE = BlockUtil.rotateShape(Direction.NORTH, Direction.WEST, NORTH_SHAPE);

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty HOPPER = BooleanProperty.create("hopper");

    public FluxHammerBlock() {
        super();
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    @SuppressWarnings("java:S1874") // deprecated
    public VoxelShape getShape(BlockState blockState, BlockGetter reader, BlockPos pos, CollisionContext context) {
        return switch (blockState.getValue(FACING)) {
            case EAST -> EAST_SHAPE;
            case WEST -> WEST_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            default -> NORTH_SHAPE;
        };
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        var above = context.getClickedPos().above();
        var isAir = context.getLevel().getBlockState(above).isAir();

        return this.defaultBlockState()
            .setValue(FACING, context.getHorizontalDirection().getOpposite())
            .setValue(HOPPER, !isAir);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HOPPER);
    }

    @Override
    @SuppressWarnings("java:S1874") // mojang deprecated
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos) {
        if (facing.equals(Direction.UP)) {
            var current = state.getValue(HOPPER);
            if (facingState.isAir() && Boolean.TRUE.equals(current)) {
                return state.setValue(HOPPER, false);
            }

            if (!facingState.isAir() && Boolean.FALSE.equals(current)) {
                return state.setValue(HOPPER, true);
            }
        }
        return state;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FluxHammerTile(pos, state);
    }

    @Override
    public BlockEntityType<? extends AbstractMachineTile> getTileType() {
        return ModRegistry.FLUX_HAMMER_TILE.get();
    }

    @Override
    public IContainerFactory<AbstractMachineMenu> containerFactory() {
        return FluxHammerMenu::new;
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter reader, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, reader, tooltip, flagIn);
        tooltip.add(translateTooltip("hammer1"));
    }
}
