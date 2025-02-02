package net.dries007.tfc.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.dries007.tfc.util.Helpers;

public class AbstractRopeBlock extends HorizontalDirectionalBlock implements IFluidLoggable, IForgeBlockExtension
{
    public static final FluidProperty FLUID = TFCBlockStateProperties.WATER;

    public static final VoxelShape SHAPE_X = box(7, 0, 0, 9, 2, 16);
    public static final VoxelShape SHAPE_Z = Helpers.rotateShape(Direction.EAST, 7, 0, 0, 9, 2, 16);

    private final ExtendedProperties properties;

    public AbstractRopeBlock(ExtendedProperties properties)
    {
        super(properties.properties());
        this.properties = properties;
        registerDefaultState(getStateDefinition().any().setValue(FLUID, FLUID.keyFor(Fluids.EMPTY)));
    }


    @Override
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }

    @Override
    public FluidProperty getFluidProperty()
    {
        return FLUID;
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(FACING, FLUID));
    }

    @Override
    protected MapCodec<HorizontalDirectionalBlock> codec()
    {
        return fakeBlockCodec();
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }
}
