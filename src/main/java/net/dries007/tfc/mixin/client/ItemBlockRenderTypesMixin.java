/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.wood.TFCLeavesBlock;
import net.dries007.tfc.util.Helpers;

@Mixin(ItemBlockRenderTypes.class)
public abstract class ItemBlockRenderTypesMixin
{
    @Shadow private static boolean renderCutout;
    @Shadow @Final private static ChunkRenderTypeSet CUTOUT_MIPPED;
    @Shadow @Final private static ChunkRenderTypeSet SOLID;

    @Inject(method="getRenderLayers", at=@At("HEAD"), cancellable = true)
    private static void getTFCLeavesRenderType(@NotNull BlockState state, CallbackInfoReturnable<ChunkRenderTypeSet> cir)
    {
        if (Helpers.isBlock(state, TFCTags.Blocks.RENDERS_AS_LEAVES))
        {
            cir.setReturnValue(renderCutout ? CUTOUT_MIPPED : SOLID);
        }
    }
}
