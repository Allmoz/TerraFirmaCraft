package net.dries007.tfc.common.items;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.AbstractRopeBlock;
import net.dries007.tfc.common.blocks.GroundedRopeBlock;
import net.dries007.tfc.common.blocks.RopeAnchorBlock;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.entities.misc.RopeKnot;
import net.dries007.tfc.util.Helpers;

public class RopeItem extends Item
{
    public RopeItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        final Level level = context.getLevel();
        final BlockPos blockpos = context.getClickedPos();
        final BlockState state = level.getBlockState(blockpos);
        if (Helpers.isBlock(state, TFCTags.Blocks.ROPE_ANCHORS) && state.hasProperty(TFCBlockStateProperties.HAS_ROPE))
        {
            if (!state.getValue(TFCBlockStateProperties.HAS_ROPE))
            {
                final Player player = context.getPlayer();
                if (!level.isClientSide && player != null)
                {
                    bindToAnchor(player, level, blockpos);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand)
    {
        final ItemStack stack = player.getItemInHand(usedHand);
        final RopeKnot knot = getKnotAt(level, player.blockPosition(), player);
        if (knot == null)
        {
            return InteractionResultHolder.pass(stack);
        }
        else
        {
            placeRopes2(level, player, stack, knot.blockPosition());
            knot.discard();
            return InteractionResultHolder.consume(stack);
        }
    }

    public static void bindToAnchor(Player player, Level level, BlockPos pos)
    {
        final RopeKnot knot = RopeKnot.getNewKnotAtLocation(level, pos);
        if (knot != null)
        {
            knot.playPlacementSound();
            knot.setLeashedTo(player, true);
            level.gameEvent(GameEvent.BLOCK_ATTACH, pos, GameEvent.Context.of(player));
            player.displayClientMessage(Component.translatable("tfc.tooltip.rope.throw_me"), true);
        }
    }

    /**
     * Only the thing being held by the leash is aware of the leash in code. The player, who holds the leash, has no data for what entities are attached to it.
     * Vanilla's solution is to search in an area and look for matches.
     */
    @Nullable
    public static RopeKnot getKnotAt(Level level, BlockPos pos, Player player)
    {
        if (!level.isLoaded(pos))
            return null;
        final AABB bounds = new AABB(player.blockPosition()).inflate(7d);
        final List<RopeKnot> knots = level.getEntitiesOfClass(RopeKnot.class, bounds, p -> p.getLeashHolder() == player);
        return knots.isEmpty() ? null : knots.getFirst();
    }

    public static void placeRopes2(Level level, Player player, ItemStack stack, BlockPos origin)
    {
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos().set(origin);
        final int count = stack.getCount();
        final Direction dir = player.getDirection();
        final DirectionProperty facing = AbstractRopeBlock.FACING;
        final BlockState hangingRope = TFCBlocks.HANGING_ROPE.get().defaultBlockState().setValue(facing, dir.getOpposite());
        final BlockState horizontalRope = TFCBlocks.ROPE.get().defaultBlockState().setValue(facing, dir.getOpposite()).setValue(GroundedRopeBlock.ASCENDING, false);
        final BlockState slopeRope = horizontalRope.setValue(GroundedRopeBlock.ASCENDING, true);

        BlockState state = level.getBlockState(cursor);
        RopeState previous = RopeState.HORIZONTAL;
        if (Helpers.isBlock(state, TFCTags.Blocks.ROPE_ANCHORS) && state.hasProperty(RopeAnchorBlock.HAS_ROPE))
        {
            if (!state.getValue(RopeAnchorBlock.HAS_ROPE))
            {
                stack.shrink(1);
                level.setBlockAndUpdate(cursor, state.setValue(facing, dir).setValue(RopeAnchorBlock.HAS_ROPE, true));
            }
            else
            {
                return;
            }
        }
        cursor.move(dir);
        for (int i = 0; i < count - 1; i++)
        {
            state = level.getBlockState(cursor);
            if (canRopeReplace(state)) // if we have an open block where we are intending to place
            {
                if (previous == RopeState.VERTICAL)
                {
                    cursor.move(0, -1, 0);
                    state = level.getBlockState(cursor);
                    cursor.move(0, 1, 0);
                    if (canRopeReplace(state))
                    {
                        if (!hangingRope.canSurvive(level, cursor))
                            return;
                        level.setBlockAndUpdate(cursor, hangingRope);
                        stack.shrink(1);
                        cursor.move(0, -1, 0);
                    }
                    else
                    {
                        if (!slopeRope.canSurvive(level, cursor))
                            return;
                        level.setBlockAndUpdate(cursor, slopeRope);
                        stack.shrink(1);
                        previous = RopeState.SLOPE;
                        cursor.move(dir);
                    }
                }
                else
                {
                    cursor.move(0, -1, 0);
                    state = level.getBlockState(cursor);
                    if (canRopeReplace(state))
                    {
                        cursor.move(0, -1, 0);
                        state = level.getBlockState(cursor);
                        cursor.move(0, 1, 0);
                        if (canRopeReplace(state))
                        {
                            if (!hangingRope.canSurvive(level, cursor))
                                return;
                            level.setBlockAndUpdate(cursor, hangingRope);
                            stack.shrink(1);
                            cursor.move(0, -1, 0);
                            previous = RopeState.VERTICAL;
                        }
                        else
                        {
                            if (!slopeRope.canSurvive(level, cursor))
                                return;
                            level.setBlockAndUpdate(cursor, slopeRope);
                            stack.shrink(1);
                            previous = RopeState.SLOPE;
                            cursor.move(dir);
                        }
                    }
                    else
                    {
                        cursor.move(0, 1, 0);
                        state = level.getBlockState(cursor);
                        if (canRopeReplace(state))
                        {
                            if (!horizontalRope.canSurvive(level, cursor))
                                return;
                            level.setBlockAndUpdate(cursor, horizontalRope);
                            stack.shrink(1);
                            previous = RopeState.HORIZONTAL;
                            cursor.move(dir);
                        }
                    }
                }
            }
        }
    }

    private static boolean canRopeReplace(BlockState state)
    {
        return state.canBeReplaced() && (state.getFluidState().isEmpty() || Helpers.isFluid(state.getFluidState(), FluidTags.WATER));
    }

    private enum RopeState
    {
        HORIZONTAL,
        VERTICAL,
        SLOPE
    }
}
