/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.volcano;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.Seed;
import net.dries007.tfc.world.biome.BiomeExtension;
import net.dries007.tfc.world.noise.Cellular2D;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceStates;
import net.dries007.tfc.world.surface.builder.NormalSurfaceBuilder;
import net.dries007.tfc.world.surface.builder.SimpleSurfaceBuilder;

import static net.dries007.tfc.world.TFCChunkGenerator.*;

public class VolcanoVariants
{
    // Simple cone shape, similar to Mt. Fuji, Japan
    public static VolcanoVariant fuji(Seed seed)
    {
        final Noise2D ridgeWarpNoise = new OpenSimplex2D(seed.seed() + 23L).octaves(2).scaled(-0.4f, 0.4f).spread(0.09f);
        final Noise2D skirtTextureNoise = new OpenSimplex2D(seed.seed() + 2982L).octaves(3).spread(0.09).scaled(-0.05, 0.05);
        final double maxRadiusScale = 0.45;

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

                // Simple cone
                final double r = Mth.map(Mth.sqrt((float) cell.f1()), 0, maxDiam * maxRadiusScale, 0, 1); // Radius, range [0, 1]
                final double craterSize = 0.04 + 0.1 * Helpers.hashDouble(noise, 10);
                double shape = maxDiam * calculateSimpleRadialShapeWithSkirt(r, craterSize, 0.9, cell.f1(), cell.f2(), 2) * 1.2;
                shape = shape * (1 - 0.1 * calculateCircumferentialErosion(cell, craterSize, 0.2, 0.9, 1, r, 3, (int) (maxDiam * 16), ridgeWarpNoise.noise(x, z)));

                if (r > 0.65)
                {
                    // Add some texture to the volcano "skirt"
                    shape += skirtTextureNoise.noise(x, z) * Mth.clampedMap(r, 0.65, 1.2, 0, 1);
                }

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

    // Large Crater with a central lake, Similar to Crater Lake, Oregon
    // This version returns the land height, not the water surface
    public static VolcanoVariant craterLake(Seed seed)
    {
        final Noise2D ridgeWarpNoise = new OpenSimplex2D(seed.seed() + 23L).octaves(2).scaled(-0.4f, 0.4f).spread(0.09f);
        final Noise2D rimWarpNoise = new OpenSimplex2D(seed.seed() + 1431L).octaves(2).scaled(-0.08f, 0.08f).spread(0.03f);
        final Noise2D textureNoise = new OpenSimplex2D(seed.seed() + 24482L).octaves(3).spread(0.06).scaled(0.85, 1.08);
        final Noise2D skirtTextureNoise = new OpenSimplex2D(seed.seed() + 2982L).octaves(3).spread(0.09).scaled(-0.09, 0.09);
        final double maxRadiusScale = 0.45;

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
                final double r = Mth.map(Mth.sqrt((float) f1), 0, maxDiam * maxRadiusScale, 0, 1); // Radius, range [0, 1]
                final double craterSize = 0.5 + rimWarpNoise.noise(x, z); // Domain warp the rim to get a wavy shape
                final double rimHeight = 0.35;
                double shape = rimHeight * calculateSimpleRadialShapeWithSkirt(r, craterSize, 2, f1, cell.f2(), 5) * 1.2;
                shape = shape * (1 - 0.12 * calculateCircumferentialErosion(cell, craterSize, craterSize + 0.06, 0.95, 1, r, 24, (int) (maxDiam * 32), ridgeWarpNoise.noise(x, z)));
                shape = shape * (1 - 0.08 * calculateCircumferentialErosion(cell, craterSize * 0.4, craterSize * 0.8, craterSize * 0.8, craterSize, r, 24, (int) (maxDiam * 32), ridgeWarpNoise.noise(x, z)));

                if (r < craterSize)
                {
                    // Add "Wizard Island" with random height and offset
                    final double noise = cell.noise();
                    final double apexHeight = rimHeight * (0.65 + 0.45 * Helpers.hashDouble(noise, 8));
                    double xOffset = -40 + 80 * Helpers.hashDouble(noise, 6);
                    double zOffset = -40 + 80 * Helpers.hashDouble(noise, 7);
                    shape = addOffsetCone(shape, cell.x() + xOffset, cell.y() + zOffset, x, z, 0, apexHeight, apexHeight * maxDiam * 300, noise);
                }
                else if (r > 1)
                {
                    // Add some texture to the volcano "skirt"
                    shape += skirtTextureNoise.noise(x, z) * Mth.clampedMap(r, 1, 1.2, 0, 1);
                }
                shape *= textureNoise.noise(x, z);
                return scaleShape(shape, biomeBaseHeight, biomeScaleHeight);
            }

            // Use a point within the cell, rather than the cell center, for the peak of a cone
            public double addOffsetCone(double shapeIn, double xCenter, double zCenter, double x, double z, double baseHeight, double apexHeight, double maxRadius, double noise)
            {
                final double maxDiam = apexHeight - baseHeight;
                final double r = Mth.clampedMap(Math.sqrt((x - xCenter) * (x - xCenter) + (z - zCenter) * (z - zCenter)), 0, maxRadius, 0, 1);
                final double a = Helpers.diamondAngle((x - xCenter), (z - zCenter)) + Helpers.hashDouble(noise, 3213);

                // Simple cone
                final double craterSize = 0.03 + 0.03 * Helpers.hashDouble(noise, 10);
                double shape = calculateSimpleRadialShapeNoSkirt(r, craterSize, 1.1);
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

                // Scale ridges larger on volcanoes with fewer ridges
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
                final double r = Mth.map(Mth.sqrt((float) f1), 0, maxDiam * maxRadiusScale, 0, 1); // Radius, range [0, 1]
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
                final BiomeExtension biome = context.stratovolcanoBiome();
                final int landHeight = (int) Math.round(this.getLandHeight(heightIn, x, z, maxDiam, biome.getCenteredFeatureScaleHeight(), biome.getCenteredFeatureBaseHeight(), cell));
                final int waterHeight = (int) Math.round(this.getFluidHeight(heightIn, x, z, maxDiam, biome.getCenteredFeatureScaleHeight(), biome.getCenteredFeatureBaseHeight(), cell));

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

    // Complex top crater, similar to Mt. Rainier, Washington
    public static VolcanoVariant tahoma(Seed seed)
    {
        final Noise2D ridgeWarpNoise = new OpenSimplex2D(seed.seed() + 23L).octaves(2).scaled(-0.4f, 0.4f).spread(0.09f);
        final Noise2D skirtTextureNoise = new OpenSimplex2D(seed.seed() + 2982L).octaves(3).spread(0.09).scaled(-0.05, 0.05);
        final Noise2D textureNoise = new OpenSimplex2D(seed.seed() + 248582L).octaves(3).spread(0.06).scaled(0.94, 1.06);
        final double maxRadiusScale = 0.45;

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

                // Simple cone
                final double r = Mth.map(Mth.sqrt((float) cell.f1()), 0, maxDiam * maxRadiusScale, 0, 1); // Radius, range [0, 1]
                final double craterSize = 0.20 + 0.25 * Helpers.hashDouble(noise, 1013);
                double shape = maxDiam * calculateSimpleRadialShapeWithSkirt(r, craterSize, 1.1, cell.f1(), cell.f2(), 2) * 1.0;
                shape = shape * (1 - 0.1 * calculateCircumferentialErosion(cell, craterSize, 1.3 * craterSize, 0.9, 1, r, 3, (int) (maxDiam * 16), ridgeWarpNoise.noise(x, z)));

                if (r < 0.65)
                {
                    // Large crater rim
                    shape *= (1 - 0.52 * craterSize * calculateVariableCraterRim(cell, 0.5 * craterSize, craterSize, 0.65, r, (int) (1 + Helpers.hashDouble(noise, 978) * 3), x, z));
                    // Inner cone
                    if (r < craterSize)
                    {
                        final double innerConeScale = (0.65 + 0.35 * Helpers.hashDouble(noise, 8973)) * craterSize;
                        final double craterBaseHeight = maxDiam * (1 - 0.9 * craterSize);
                        final double innerCone = craterBaseHeight + innerConeScale * calculateSimpleRadialShapeNoSkirt(Mth.clampedMap(r, 0, craterSize, 0, 1), 0.7 * craterSize, 1);
                        shape = Math.max(shape, innerCone);
                    }
                }
                else
                {
                    // Add some texture to the volcano "skirt"
                    shape += skirtTextureNoise.noise(x, z) * Mth.clampedMap(r, 0.65, 1.2, 0, 1);
                }

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

            public double calculateVariableCraterRim(Cellular2D.Cell cell, double rInner, double rCrater, double rOuter, double r, int peakCount, int x, int z)
            {
                final double noise = Helpers.hashDouble(cell.noise(), 43);
                double a = cell.angle() + noise;
                a = a >= 4 ? a - 4 : a < 0 ? a + 4 : a;

                final double peakShape = Math.abs((a * 0.5 * peakCount % 2) - 1);

                // Smooth out peaks/valleys away from rim
                if (r < rInner || r > rOuter)
                {
                    return 0;
                }

                final double easing;
                if (r <= rCrater)
                {
                    easing = Mth.map(r, rInner, rCrater, 0, 1);
                }
                else
                {
                    easing = Mth.map(r, rCrater, rOuter, 1, 0);
                }

                return peakShape * easing * textureNoise.noise(x, z);
            }
        };
    }

    // Granite dome/batholith: https://en.wikipedia.org/wiki/Granite_dome
    public static VolcanoVariant dome(Seed seed)
    {
        final Noise2D textureNoise = new OpenSimplex2D(seed.seed() + 24852L).octaves(4).spread(0.2).scaled(0.8, 1.2);
        final Noise2D radialWarpNoise = new OpenSimplex2D(seed.seed() + 133L).octaves(2).scaled(-0.006f, 0f).spread(0.05f);

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
                // Variable radii
                final double maxR = maxDiam * 0.5 - 0.08 * (Helpers.hashDouble(cell.noise(), 1398));

                // Max some domes eroded
                final double erosionWarp = Helpers.hashDouble(cell.noise(), 1348) > 0.6 ? radialWarpNoise.noise(x, z) : 0;

                // Simple dome, we don't take sqrt of f1 because our end shape is an r2 function anyways
                final double r2 = Mth.map(cell.f1() + erosionWarp, 0, maxR * maxR, 0, 1); // Radius squared, range [0, 1]
                final double verticalScale = 1.1 + Helpers.hashDouble(cell.noise(), 83) - maxDiam;

                double shape = verticalScale * maxDiam * (1 - r2);
                shape *= textureNoise.noise(x, z);

                return scaleShape(shape, biomeBaseHeight, biomeScaleHeight) ;
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
                SimpleSurfaceBuilder.ROCKY_SHORE.apply(seed).buildSurface(context, startY, endY);
            }
        };
    }


    /**
     * @param r       The scaled, non-square distance from the volcano, from 0 at center to 1 at edge of influence
     * @param rCrater The radius of the crater
     * @return A noise function determining the volcano's height at any given position, in the range [0, 1]
     */
    public static double calculateSimpleRadialShapeWithSkirt(double r, double rCrater, double craterDepthScale, double f1, double f2, double skirtSlope)
    {
        if (r >= 1)
        {
            // Outside of the detailed radius, decrease below base level. Faster near cell edge
            return (1 - r) * Mth.clampedMap(f2 - f1, 0, 0.1, skirtSlope, 1);
        }
        else
        {
            return calculateSimpleRadialShape(r, rCrater, craterDepthScale);
        }
    }

    /**
     * @param r       The scaled, non-square distance from the volcano, from 0 at center to 1 at edge of influence
     * @param rCrater The radius of the crater
     * @return A noise function determining the volcano's height at any given position, in the range [0, 1]
     */
    public static double calculateSimpleRadialShapeNoSkirt(double r, double rCrater, double craterDepthScale)
    {
        if (r >= 1)
        {
            // Outside of the radius, return 0
            return 0;
        }
        else
        {
            return calculateSimpleRadialShape(r, rCrater, craterDepthScale);
        }
    }

    public static double calculateSimpleRadialShape(double r, double rCrater, double craterDepthScale)
    {
        if (r > rCrater)
        {
            // Main slopes
            double x = Mth.map(r, rCrater, 1, 0, 1);
            return Helpers.hyperbolicSection(x, 1, 1);
        }
        else
        {
            // Interior of crater
            double craterBaseHeight = 1 - craterDepthScale * rCrater;
            return Helpers.hyperbolicSection(rCrater - r, rCrater, craterDepthScale * rCrater) + craterBaseHeight;
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
