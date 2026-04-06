/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;

import java.util.Arrays;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.Seed;
import net.dries007.tfc.world.biome.BiomeExtension;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.volcano.CenteredFeatureNoise;
import net.dries007.tfc.world.volcano.CenteredFeatureNoiseSampler;
import net.dries007.tfc.world.volcano.VolcanoVariant;

public class StratovolcanoSurfaceBuilder implements SurfaceBuilder
{
    public static SurfaceBuilderFactory create(SurfaceBuilderFactory parent)
    {
        return seed -> new StratovolcanoSurfaceBuilder(parent.apply(seed), seed);
    }

    private final SurfaceBuilder parent;
    private final Seed seed;

    public StratovolcanoSurfaceBuilder(SurfaceBuilder parent, Seed seed)
    {
        this.seed = seed;
        this.parent = parent;
    }

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        final BiomeExtension biome = context.stratovolcanoBiome();
        if (biome.hasStratovolcanoes())
        {
            final CenteredFeatureNoiseSampler sampler = CenteredFeatureNoise.stratovolcano(seed);
            final int preVolcanicHeight = context.getPreVolcanicHeight();
            if (startY > preVolcanicHeight)
            {
                VolcanoVariant variant = sampler.getVolcanoVariant(sampler.getCellularNoise().cell(context.pos().getX(), context.pos().getZ()));
                if (variant != null)
                {
                    variant.buildSurface(context, startY, preVolcanicHeight, sampler);
                    return;
                }
            }
        }
        parent.buildSurface(context, startY, endY);
    }
}
