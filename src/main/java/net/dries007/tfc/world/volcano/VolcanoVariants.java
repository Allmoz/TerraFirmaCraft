package net.dries007.tfc.world.volcano;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.Seed;
import net.dries007.tfc.world.noise.Cellular2D;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceStates;
import net.dries007.tfc.world.surface.builder.NormalSurfaceBuilder;

import static com.ibm.icu.impl.ValidIdentifiers.Datatype.*;
import static net.dries007.tfc.world.TFCChunkGenerator.*;
import static net.dries007.tfc.world.volcano.CenteredFeatureNoise.*;

public class VolcanoVariants
{
    // Simple cone shape, similar to Mt. Fuji, Japan
    public static VolcanoVariant fuji(Seed seed)
    {
        final Noise2D ridgeWarpNoise = new OpenSimplex2D(seed.seed() + 23L).octaves(2).scaled(-0.4f, 0.4f).spread(0.09f);

        return new VolcanoVariant()
        {
            @Override
            public double getHeight(double heightIn, int x, int z, double maxDiam, double biomeScaleHeight, double biomeBaseHeight, Cellular2D.Cell cell)
            {
                return getLandHeight(heightIn, x, z, maxDiam, biomeScaleHeight, biomeBaseHeight, cell);
            }

            @Override
            public double getLandHeight(double heightIn, int x, int z, double maxDiam, double biomeScaleHeight, double biomeBaseHeight, Cellular2D.Cell cell)
            {
                final double noise = cell.noise();
                final double apexHeight = maxDiam; // TODO: Random heights? 0.25 * (3 + Helpers.hashDouble(noise, 2)) * maxDiam;

                // Simple cone
                final double r = Mth.map(Mth.sqrt((float) cell.f1()), 0, apexHeight * 0.5, 0, 1); // Radius squared, range [0, 1]
                final double craterSize = 0.04 + 0.06 * Helpers.hashDouble(noise, 10);
                double shape = apexHeight * calculateSimpleRadialShape(r, craterSize) * 1.2;
                shape = shape * (0.9 + 0.1 * calculateCircumferentialErosion(cell, craterSize, 0.2, 0.9, 1, r, 3, (int) (maxDiam * 16), ridgeWarpNoise.noise(x, z)));

                return scaleShape(shape, biomeBaseHeight, biomeScaleHeight);
            }

            @Override
            public double getGlacierHeight(double heightIn, int x, int z, double maxDiam, double biomeScaleHeight, double biomeBaseHeight, Cellular2D.Cell cell)
            {
                return VolcanoVariant.super.getGlacierHeight(heightIn, x, z, maxDiam, biomeScaleHeight, biomeBaseHeight, cell);
            }

            @Override
            public double getFluidHeight(double heightIn, int x, int z, double maxDiam, double biomeScaleHeight, double biomeBaseHeight, Cellular2D.Cell cell)
            {
                return VolcanoVariant.super.getFluidHeight(heightIn, x, z, maxDiam, biomeScaleHeight, biomeBaseHeight, cell);
            }

            @Override
            public void buildSurface(SurfaceBuilderContext context, int startY, int endY, CenteredFeatureNoiseSampler sampler)
            {
                NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY);
            }
        };
    }

    // Large Crater with a central lake, Similar to Crater Lake, OR
    // This version returns the land height, not the water surface
    public static VolcanoVariant craterLake(Seed seed)
    {
        final Noise2D ridgeWarpNoise = new OpenSimplex2D(seed.seed() + 23L).octaves(2).scaled(-0.4f, 0.4f).spread(0.09f);
        final Noise2D rimWarpNoise = new OpenSimplex2D(seed.seed() + 1431L).octaves(2).scaled(-0.08f, 0.08f).spread(0.03f);
        final Noise2D textureNoise = new OpenSimplex2D(seed.seed() + 24482L).octaves(3).spread(0.06).scaled(0.92, 1.08);

        return new VolcanoVariant()
        {
            @Override
            public double getHeight(double heightIn, int x, int z, double maxDiam, double biomeScaleHeight, double biomeBaseHeight, Cellular2D.Cell cell)
            {
                final double landHeight = getLandHeight(heightIn, x, z, maxDiam, biomeScaleHeight, biomeBaseHeight, cell);
                final double waterHeight = getFluidHeight(heightIn, x, z, maxDiam, biomeScaleHeight, biomeBaseHeight, cell);

                return Math.max(landHeight, waterHeight);
            }

            @Override
            public double getLandHeight(double heightIn, int x, int z, double maxDiam, double biomeScaleHeight, double biomeBaseHeight, Cellular2D.Cell cell)
            {
                // Simple cone
                final double f1 = cell.f1();
                final double r = Mth.map(Mth.sqrt((float) f1), 0, maxDiam * 0.5, 0, 1); // Radius squared, range [0, 1]
                final double craterSize = 0.5 + rimWarpNoise.noise(x, z); // Domain warp the rim to get a wavy shape
                final double rimHeight = 0.35;
                double shape = rimHeight * calculateSimpleRadialShape(r, craterSize) * 1.2;
                shape = shape * (0.88 + 0.12 * calculateCircumferentialErosion(cell, craterSize, craterSize + 0.06, 0.95, 1, r, 24, (int) (maxDiam * 32), ridgeWarpNoise.noise(x, z)));
                shape = shape * (0.93 + 0.08 * calculateCircumferentialErosion(cell, craterSize * 0.4, craterSize * 0.8, craterSize * 0.8, craterSize, r, 24, (int) (maxDiam * 32), ridgeWarpNoise.noise(x, z)));

                if (r < craterSize)
                {
                    // Add "Wizard Island" with random height and offset
                    final double noise = cell.noise();
                    final double apexHeight = rimHeight * (0.65 + 0.45 * Helpers.hashDouble(noise, 8));
                    double xOffset = -40 + 80 * Helpers.hashDouble(noise, 6);
                    double zOffset = -40 + 80 * Helpers.hashDouble(noise, 7);
                    shape = addOffsetCone(shape, cell.x() + xOffset, cell.y() + zOffset, x, z, 0, apexHeight, apexHeight * maxDiam * 300, noise);
                }
                shape *= textureNoise.noise(x, z);
                return scaleShape(shape, biomeBaseHeight, biomeScaleHeight);
            }

            // Use a point within the cell, rather than the cell center, for the peak of a cone
            public double addOffsetCone(double shapeIn, double xCenter, double zCenter, double x, double z, double baseHeight, double apexHeight, double maxRadius, double noise)
            {
                final double maxDiam = apexHeight - baseHeight;
                final double r = Mth.clampedMap(Math.sqrt((x - xCenter) * (x - xCenter) + (z - zCenter) * (z - zCenter)), 0, maxRadius, 0, 1);
                final double a = Helpers.diamondAngle((x - xCenter), (z-zCenter)) + Helpers.hashDouble(noise, 3213);

                // Simple cone
                final double craterSize = 0.03 + 0.03 * Helpers.hashDouble(noise, 10);
                double shape = calculateSimpleRadialShape(r, craterSize);
                shape = shape * (0.9 + 0.1 * calculateOffsetCircumferentialErosion(craterSize, 0.2, 0.9, 1, r, a, (int) (3 + Helpers.hashDouble(noise, 1313) * maxDiam * 12), ridgeWarpNoise.noise(x, z), noise));
                shape = Mth.map(shape, 0, 1, baseHeight, apexHeight);
                return Math.max(shape, shapeIn);
            }

            // Use a point within the cell, rather than the cell center, for the center of circumferential erosion
            public static double calculateOffsetCircumferentialErosion(double rInner0, double rInner1, double rOuter1, double rOuter0, double r, double aIn, int ridges, double ridgeWarpNoise, double noiseIn)
            {
                final double noise = Helpers.hashDouble(noiseIn, 213);
                final double ridgeWarping = ridgeWarpNoise / ridges;
                double a = aIn + ridgeWarping;
                a = a >= 4 ? a - 4 : a < 0 ? a + 4 : a;

                final double erosion = (2 - noise);
                final double fluvialShape = Math.abs((a * 0.5 * ridges % 2) - 1);

                // Smooth out ridges at an inner and outer radius
                final double easing;
                if (r <= rInner1)
                {
                    easing = Mth.clampedMap(r, rInner0, rInner1, 0, 1);
                }
                else
                {
                    easing = Mth.clampedMap(r, rOuter0, rOuter1, 0, 1);
                }

                // Scale ridges larger on volcanoes with fewer ridges, from 0.6 to 1.4
                final double ridgeScale = Mth.clampedMap(ridges, 3, 10, 1.5, 0.5);

                return (fluvialShape - 1) * erosion * easing * ridgeScale;
            }

            @Override
            public double getGlacierHeight(double heightIn, int x, int z, double maxDiam, double biomeScaleHeight, double biomeBaseHeight, Cellular2D.Cell cell)
            {
                return VolcanoVariant.super.getGlacierHeight(heightIn, x, z, maxDiam, biomeScaleHeight, biomeBaseHeight, cell);
            }

            @Override
            public double getFluidHeight(double heightIn, int x, int z, double maxDiam, double biomeScaleHeight, double biomeBaseHeight, Cellular2D.Cell cell)
            {
                // Simple cone
                final double f1 = cell.f1();
                final double r = Mth.map(Mth.sqrt((float) f1), 0, maxDiam * 0.5, 0, 1); // Radius squared, range [0, 1]
                final double craterSize = 0.5 + rimWarpNoise.noise(x, z); // Domain warp the rim to get a wavy shape
                if (r < craterSize)
                {
                    return scaleShape(0.2, biomeBaseHeight, biomeScaleHeight);
                }
                else
                {
                    return 0;
                }
            }

            @Override
            public void buildSurface(SurfaceBuilderContext context, int startY, int endY, CenteredFeatureNoiseSampler sampler)
            {
                final BlockPos pos = context.pos();
                final int x = pos.getX();
                final int z = pos.getZ();

                final Cellular2D cellNoise = sampler.getCellularNoise();
                final Cellular2D.Cell cell = cellNoise.cell(x, z);
                final int heightIn = context.getPreVolcanicHeight();
                final double maxDiam = Math.sqrt(CenteredFeatureNoise.maxSafeDiameterSquared(cell, cellNoise));
                final int landHeight = (int) Math.round(this.getLandHeight(heightIn, x, z, maxDiam, context.biome().getCenteredFeatureScaleHeight(), context.biome().getCenteredFeatureBaseHeight(), cell));
                final int waterHeight = (int) Math.round(this.getFluidHeight(heightIn, x, z, maxDiam, context.biome().getCenteredFeatureScaleHeight(), context.biome().getCenteredFeatureBaseHeight(), cell));

                if (waterHeight > landHeight)
                {
                    buildWaterSurface(context, waterHeight, landHeight);
                }

                NormalSurfaceBuilder.ROCKY.buildSurface(context, startY, endY);
            }

            private void buildWaterSurface(SurfaceBuilderContext context, int startY, int landHeight)
            {
                final BlockState water = Fluids.WATER.getSource().defaultFluidState().createLegacyBlock();

                for (int y = startY; y >= landHeight; --y)
                {
                    context.setBlockState(y, water);
                }

                for (int y = landHeight; y >= landHeight - 5; --y)
                {
                    context.setBlockState(y, SurfaceStates.RAW);
                }
            }
        };
    }


    /**
     * @param r The scaled, non-square distance from the volcano, from 0 at center to 1 at edge of influence
     * @param rCrater The radius of the crater
     * @return A noise function determining the volcano's height at any given position, in the range [0, 1]
     */
    public static double calculateSimpleRadialShape(double r, double rCrater)
    {
        if (r >= 1)
        {
            return 0;
        }
        else if (r > rCrater)
        {
            // Main slopes
            double x = Mth.map(r, rCrater, 1, 0, 1);
            return Helpers.hyperbolicSection(x, 1, 1);
        }
        else
        {
            // Interior of crater
            double craterBaseHeight = 1 - 2 * rCrater;
            return Helpers.hyperbolicSection(rCrater - r, rCrater, 2 * rCrater) + craterBaseHeight;
        }
    }

    public static double calculateCircumferentialErosion(Cellular2D.Cell cell, double rInner0, double rInner1, double rOuter1, double rOuter0, double r, int minRidgeCount, int addedRidgeCount, double ridgeWarpNoise)
    {
        final double noise = Helpers.hashDouble(cell.noise(), 213);
        final int ridges = (int) (noise * addedRidgeCount) + minRidgeCount;
        final double ridgeWarping = ridgeWarpNoise / ridges;
        double a = cell.angle() + ridgeWarping;
        a = a >= 4 ? a - 4 : a < 0 ? a + 4 : a;

        final double erosion = (2 - noise);
        final double fluvialShape = Math.abs((a * 0.5 * ridges % 2) - 1);

        // Smooth out ridges at an inner and outer radius
        final double easing;
        if (r <= rInner1)
        {
            easing = Mth.clampedMap(r, rInner0, rInner1, 0, 1);
        }
        else
        {
            easing = Mth.clampedMap(r, rOuter0, rOuter1, 0, 1);
        }

        // Scale ridges larger on volcanoes with fewer ridges, from 0.6 to 1.4
        final double ridgeScale = Mth.clampedMap(ridges, minRidgeCount, minRidgeCount + addedRidgeCount, 1.5, 0.5);

        return (fluvialShape - 1) * erosion * easing * ridgeScale;
    }

    public static double scaleShape(double shape, double base, double scale)
    {
        return SEA_LEVEL_Y + base + shape * scale;
    }

}
