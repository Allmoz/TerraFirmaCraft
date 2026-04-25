package net.dries007.tfc.client;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.GlassBasinBlock;
import net.dries007.tfc.common.component.glass.GlassOperation;
import net.dries007.tfc.common.component.glass.GlassOperations;
import net.dries007.tfc.util.Helpers;


public class GlassblowingOverlays
{
    public static boolean render(Minecraft minecraft, GuiGraphics graphics)
    {
        final Level level = minecraft.level;
        final Player player = minecraft.player;
        final BlockPos targetedPos = ClientHelpers.getTargetedPos();
        final Direction targetedFace = ClientHelpers.getTargetedFace();

        if (level != null && targetedPos != null && targetedFace != null && player != null)
        {
            final BlockState targetedState = level.getBlockState(targetedPos);
            final BlockPos center = targetedPos.relative(targetedFace);
            if (GlassBasinBlock.isValid(level, center))
            {
                int x = graphics.guiWidth() / 2 + 3;
                int y = graphics.guiHeight() / 2 + 8;
                // todo translatable
                Component line = Component.literal("debug[Basin Pour]");
                drawCenteredText(minecraft, graphics, line, x, y);
                return true;
            }
            if (targetedFace == Direction.UP && Helpers.isBlock(targetedState, TFCTags.Blocks.GLASS_POURING_TABLE) && level.getBlockState(targetedPos.above()).isAir())
            {
                int x = graphics.guiWidth() / 2 + 3;
                int y = graphics.guiHeight() / 2 + 8;
                // todo translatable
                Component line = Component.literal("debug[Table Pour]");
                drawCenteredText(minecraft, graphics, line, x, y);
                return true;
            }
            ItemStack held = player.getMainHandItem();
            ItemStack otherItem = player.getOffhandItem();
            if (!Helpers.isItem(held, TFCTags.Items.GLASS_BLOWPIPES))
            {
                held = player.getOffhandItem();
                otherItem = player.getMainHandItem();
            }
            final GlassOperation op = GlassOperation.get(otherItem, player);
            if (op != null){
                Component line = Component.translatable(op.getTranslationId());
                int x = graphics.guiWidth() / 2 + 3;
                int y = graphics.guiHeight() / 2 + 8;
                drawCenteredText(minecraft, graphics, line, x, y);
            }
        }

        return false;
    }

    private static void drawCenteredText(Minecraft minecraft, GuiGraphics graphics, Component text, int x, int y)
    {
        final int textWidth = minecraft.font.width(text) / 2;
        graphics.drawString(minecraft.font, text, x - textWidth, y, 0xCCCCCC, true);
    }
}
