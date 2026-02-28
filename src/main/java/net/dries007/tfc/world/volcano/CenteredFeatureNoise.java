/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.volcano;

import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.BiomeNoiseSampler;
import net.dries007.tfc.world.ChunkHeightFiller;
import net.dries007.tfc.world.Seed;
import net.dries007.tfc.world.biome.BiomeExtension;
import net.dries007.tfc.world.biome.BiomeNoise;
import net.dries007.tfc.world.biome.BiomeSourceExtension;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.noise.Cellular2D;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;

import static net.dries007.tfc.world.TFCChunkGenerator.*;

public class CenteredFeatureNoise
{
    public static CenteredFeatureNoiseSampler cinder(Seed seed)
    {
        return new CenteredFeatureNoiseSampler()
        {
            final Cellular2D cellNoise = new Cellular2D(seed.seed()).spread(0.009f);
            final Noise2D jitterNoise = new OpenSimplex2D(seed.seed() + 8179234123L).octaves(2).scaled(-0.0016f, 0.0016f).spread(0.128f);

            @Override
            public double setColumnAndSampleHeight(double heightIn, int x, int z, BiomeSourceExtension biomeSource)
            {
                Cellular2D.Cell cell = cellNoise.cell(x, z);
                final BiomeExtension biome = biomeSource.getBiomeExtension(QuartPos.fromBlock((int) cell.x()), QuartPos.fromBlock((int) cell.y()));
                if (biome.hasCinderCones())
                {
                    final int rarity = biome.getCenteredFeatureRarity();
                    if (checkCellRarity(cell, rarity))
                    {
                        if (biome == TFCBiomes.ACTIVE_SHIELD_VOLCANO)
                            return modifyHeightShieldVolcano(cell, x, z, biome, heightIn);
                        else
                            return modifyHeight(cell, x, z, biome, heightIn);
                    }
                }
                return ChunkHeightFiller.NOT_PRESENT_RETURN;
            }

            public BiomeExtension getCenterBiome(int x, int z, BiomeSourceExtension biomeSource)
            {
                Cellular2D.Cell cell = cellNoise.cell(x, z);
                return biomeSource.getBiomeExtension(QuartPos.fromBlock((int) cell.x()), QuartPos.fromBlock((int) cell.y()));
            }

            /**
             * Calculate the closeness value to a volcano, in the range [0, 1]. 1 = Center of a volcano, 0 = Nowhere near.
             */
            public float calculateEasing(int x, int z, int rarity)
            {
                final Cellular2D.Cell cell = cellNoise.cell(x, z);
                if (checkCellRarity(cell, rarity))
                {
                    return calculateClampedEasing((float) cell.f1());
                }
                return 0;
            }

            private double modifyHeight(Cellular2D.Cell cell, int x, int z, BiomeExtension biome, double heightIn)
            {
                final double f1 = cell.f1();
                final double easing = Mth.clamp(calculateEasing((float) f1) + jitterNoise.noise(x, z), 0, 1);
                final double shape = calculateShape(1 - easing);
                final double volcanoAdditionalHeight = shape * biome.getCenteredFeatureScaleHeight();
                final double volcanoHeight = (SEA_LEVEL_Y + biome.getCenteredFeatureBaseHeight() + volcanoAdditionalHeight);
                final double weight = 10f * Mth.clamp((float) cell.f2() - f1, 0f, 0.1f);
                return Mth.lerp(easing * weight, heightIn, (0.2 * volcanoHeight + 0.8 * Math.max(volcanoHeight, heightIn + 0.6f * volcanoAdditionalHeight)));

            }

            private double modifyHeightShieldVolcano(Cellular2D.Cell cell, int x, int z, BiomeExtension biome, double heightIn)
            {
                if (cell != null)
                {
                    final double f1 = cell.f1();
                    final double easing = Mth.clamp(calculateEasing((float) f1) + (jitterNoise.noise(x, z)), 0, 1);
                    final double shape = calculateShape(1 - easing);
                    final double volcanoAdditionalHeight = shape * biome.getCenteredFeatureScaleHeight();
                    final double volcanoHeight = (SEA_LEVEL_Y + biome.getCenteredFeatureBaseHeight() + volcanoAdditionalHeight);
                    final double weight = 10f * Mth.clamp(cell.f2() - f1, 0f, 0.1f);
                    return Mth.lerp(easing * weight, heightIn, (0.2 * volcanoHeight + 0.8 * Math.max(volcanoHeight, heightIn + 0.6f * volcanoAdditionalHeight)));
                }
                return heightIn;
            }

            private static float calculateEasing(float f1)
            {
                return Mth.map(f1, 0, 0.23f, 1, 0);
            }

            private static float calculateClampedEasing(float f1)
            {
                return Mth.clamp(calculateEasing(f1), 0, 1);
            }

            /**
             * @param t The unscaled square distance from the volcano, roughly in [0, 1.2]
             * @return A noise function determining the volcano's height at any given position, in the range [0, 1]
             */
            private static double calculateShape(double t)
            {
                if (t > 0.025)
                {
                    return (5 / (9 * t + 1) - 0.5) * 0.279173646008;
                }
                else
                {
                    double a = (t * 9 + 0.05);
                    return (8 * a * a + 2.97663265306) * 0.279173646008;
                }
            }

            @Override
            public boolean isValidBiome(BiomeExtension biome)
            {
                return biome.hasCinderCones();
            }

            public double maxSafeDiameter(Cellular2D.Cell cell, int x, int z)
            {
                // This step is necessary because the exact center of a cell is not aligned with the exact center of a block
                // Which causes the sampled position to be on one side of the center, and potentially closer to a different cell
                final int deltaX = x - (int) cell.x();
                final int deltaZ = z - (int) cell.y();
                return cellNoise.cell(Math.signum(deltaX), Math.signum(deltaZ)).f2();
            }

            /**
             * Calculate the center of the nearest volcano, if one exists, to the given x, z, at the given y.
             */
            @Override
            @Nullable
            public BlockPos calculateCenter(int x, int y, int z, int rarity)
            {
                final Cellular2D.Cell cell = cellNoise.cell(x, z);
                if (checkCellRarity(cell, rarity) & maxSafeDiameter(cell, x, z) > 0.46)
                {
                    return new BlockPos((int) cell.x(), y, (int) cell.y());
                }
                return null;
            }
        };
    }

    public static CenteredFeatureNoiseSampler tuffRing(Seed seed)
    {
        return new CenteredFeatureNoiseSampler()
        {
            final Cellular2D cellNoise = new Cellular2D(seed.seed(), 0.2f, 1).spread(0.003f);
            final Noise2D jitterNoise = new OpenSimplex2D(seed.seed() + 1234123L).octaves(2).scaled(-0.032f, 0.032f).spread(0.064f);
            final Noise2D addedCliffNoise = new OpenSimplex2D(seed.seed()).octaves(2).spread(0.1).scaled(-2, 10);
            final Noise2D everywhereNoise = new OpenSimplex2D(seed.next()).octaves(3).spread(0.03).scaled(-10, 10);

            @Override
            public double setColumnAndSampleHeight(double heightIn, int x, int z, BiomeSourceExtension biomeSource)
            {
                Cellular2D.Cell cell = cellNoise.cell(x, z);
                final BiomeExtension biome = biomeSource.getBiomeExtension(QuartPos.fromBlock((int) cell.x()), QuartPos.fromBlock((int) cell.y()));
                if (biome.hasTuffRings())
                {
                    final int rarity = biome.getCenteredFeatureRarity();
                    if (checkCellRarity(cell, rarity))
                    {
                        return modifyHeight(cell, x, z, biome, heightIn);
                    }
                }
                return ChunkHeightFiller.NOT_PRESENT_RETURN;
            }

            public BiomeExtension getCenterBiome(int x, int z, BiomeSourceExtension biomeSource)
            {
                Cellular2D.Cell cell = cellNoise.cell(x, z);
                return biomeSource.getBiomeExtension(QuartPos.fromBlock((int) cell.x()), QuartPos.fromBlock((int) cell.y()));
            }

            private double modifyHeight(Cellular2D.Cell cell, int x, int z, BiomeExtension biome, double heightIn)
            {
                if (cell != null)
                {
                    final double f1 = cell.f1();
                    final double easing = Mth.clamp(calculateEasing((float) f1) + jitterNoise.noise(x, z), 0, 1);
                    final double shape = calculateShape(1 - easing);
                    final double ringAdditionalHeight = (shape * biome.getCenteredFeatureScaleHeight() + (shape > 0.5 ? addedCliffNoise.noise(x, z) : 0f));
                    final double gapAdjustedAdditionalHeight = Math.min(ringAdditionalHeight * getGapVerticalEasing(cell), ringAdditionalHeight);
                    final double ringHeight = SEA_LEVEL_Y + biome.getCenteredFeatureBaseHeight() + gapAdjustedAdditionalHeight + everywhereNoise.noise(x, z);

                    // Abruptly scales down to the baseHeight near the edges of cells
                    final double delta = 25 * Mth.clamp(cell.f2() - f1, 0, 0.04);
                    return Mth.lerp(delta, heightIn, Math.max(ringHeight, heightIn));
                }
                return heightIn;
            }

            /**
             * Calculate the closeness value to a tuff ring center, in the range [0, 1]. 1 = Center, 0 = Nowhere near.
             */
            public float calculateEasing(int x, int z, int rarity)
            {
                final Cellular2D.Cell cell = cellNoise.cell(x, z);
                if (checkCellRarity(cell, rarity))
                {
                    return calculateClampedEasing((float) cell.f1());
                }
                return 0;
            }

            private static float calculateClampedEasing(float f1)
            {
                return Mth.clamp(calculateEasing(f1), 0, 1);
            }

            private static float calculateEasing(float f1)
            {
                return Mth.map(f1, 0, 0.23f, 1, 0);
            }

            /**
             * @param t The unscaled square distance from the tuff ring center, roughly in [0, 1.2]
             * @return A noise function determining the tuff ring's height at any given position, in the range [0, 1]
             */
            private static double calculateShape(double t)
            {
                return t < 0.03f ? 0 // Flat
                    : t < 0.10f ? t * 7f - 0.21f // Slope up to 0.5
                    : t < 0.15f ? t * 6.6667f // Cliff, 0.67 slope to 1.0
                    : t < 0.20f ? 1 - (t - 0.15f) * 6.6667f // Ridge, slope 1.0 down to 0.67
                    : 1.5f - (5 * t); // Cliff base, slope 0.5 down
            }

            @Override
            public boolean isValidBiome(BiomeExtension biome)
            {
                return biome.hasTuffRings();
            }

            /**
             * Calculate the center of the nearest tuff ring, if one exists, to the given x, z, at the given y.
             */
            @Override
            @Nullable
            public BlockPos calculateCenter(int x, int y, int z, int rarity)
            {
                final Cellular2D.Cell cell = cellNoise.cell(x, z);
                if (checkCellRarity(cell, rarity))
                {
                    return new BlockPos((int) cell.x(), y, (int) cell.y());
                }
                return null;
            }
        };
    }

    /**
     * Method for adding a single gap to a cellular feature at a random angle
     *
     * @return a value ranging from 1 far from the gap, to 0 in the middle of a large gap
     */
    private static double getGapVerticalEasing(Cellular2D.Cell cell)
    {
        // Gap size from 0 to 2
        final double gapSize = 2 * Helpers.hashDouble(cell.noise(), 112);

        // Only adjust this if a gap should exist at all
        double gapVerticalEasing = 1;
        if (gapSize > 0)
        {
            final double aGap = 4 * Helpers.hashDouble(cell.noise(), 113);
            final double a1 = cell.angle();
            final double angleToGap = Math.abs(a1 - aGap);
            // If angle to gap is larger than the gap size, we are far from the gap and can skip calculations
            if (angleToGap < gapSize)
            {
                final double angleToGapEdge = Math.abs(Math.min(angleToGap + gapSize, angleToGap - gapSize));

                // Gap scale should be lowest at the center of the gap
                gapVerticalEasing = Mth.clampedMap(angleToGapEdge, 0, Math.max(0.3, 0.6 * gapSize), 1, 0);
            }
        }
        return gapVerticalEasing;
    }

    public static CenteredFeatureNoiseSampler tuya(Seed seed)
    {
        return new CenteredFeatureNoiseSampler()
        {
            final Cellular2D cellNoise = new Cellular2D(seed.seed(), 0.21f, 1).spread(0.0033f);
            final Noise2D jitterNoise = new OpenSimplex2D(seed.seed() + 1234123L).octaves(2).scaled(-0.016f, 0.016f).spread(0.128f);
            final Noise2D addedNoise = new OpenSimplex2D(seed.seed()).octaves(2).spread(0.1).scaled(-2, 3);

            @Override
            public double setColumnAndSampleHeight(double heightIn, int x, int z, BiomeSourceExtension biomeSource)
            {
                Cellular2D.Cell cell = cellNoise.cell(x, z);
                final BiomeExtension biome = biomeSource.getBiomeExtension(QuartPos.fromBlock((int) cell.x()), QuartPos.fromBlock((int) cell.y()));
                final int rarity = biome.getCenteredFeatureRarity();
                if (biome.hasTuyas())
                {
                    if (checkCellRarity(cell, rarity))
                    {
                        return modifyHeight(cell, x, z, biome, heightIn);
                    }
                }
                return ChunkHeightFiller.NOT_PRESENT_RETURN;
            }

            public BiomeExtension getCenterBiome(int x, int z, BiomeSourceExtension biomeSource)
            {
                Cellular2D.Cell cell = cellNoise.cell(x, z);
                return biomeSource.getBiomeExtension(QuartPos.fromBlock((int) cell.x()), QuartPos.fromBlock((int) cell.y()));
            }

            public double modifyHeight(Cellular2D.Cell cell, double x, double z, BiomeExtension biome, double heightIn)
            {
                if (cell != null)
                {
                    final double easing = Mth.clamp(calculateEasing((float) cell.f1()) + jitterNoise.noise(x, z), 0, 1);
                    final double shape = biome.getCenteredFeatureIce() ? calculateIcyShape(1 - easing) : calculateShape(1 - easing);
                    final double additionalHeight = (float) (shape * biome.getCenteredFeatureScaleHeight() + addedNoise.noise(x, z));
                    final double tuyaHeight = SEA_LEVEL_Y + biome.getCenteredFeatureBaseHeight() + additionalHeight;
                    return Mth.lerp(easing, heightIn, 0.5f * (tuyaHeight + Math.max(tuyaHeight, heightIn + 0.4f * additionalHeight)));
                }
                return heightIn;
            }

            /**
             * @param t The unscaled square distance from the tuya, roughly in [0, 1.2]
             * @return A noise function determining the tuya's height at any given position, in the range [0, 1]
             */
            private static double calculateShape(double t)
            {
                return t < 0.015f ? Mth.map(t, 0f, 0.015f, 0.8f, 1f)// Caldera
                    : t < 0.125f ? Mth.map(t, 0.025f, 0.125f, 1f, 0.75f) // Shallow slope down
                    : Mth.clampedMap(t, 0.125f, 0.16f, 0.75f, 0f); // Cliff
            }

            /**
             * @param t The unscaled square distance from the tuya, roughly in [0, 1.2]
             * @return A noise function determining the tuya's height at any given position, in the range [0, 1]
             */
            private static double calculateIcyShape(double t)
            {
                return t < 0.015f ? Mth.map(t, 0f, 0.015f, 0.8f, 1f) // Caldera
                    : t < 0.125f ? Mth.map(t, 0.025f, 0.125f, 1f, 0.75f) // Shallow slope down
                    : t < 0.16 ? Mth.map(t, 0.125f, 0.16f, 0.75f, 0f) // Cliff
                    : Mth.clampedMap(t, 0.16f, 0.19f, 0f, 0.5f); // Ice edge
            }

            /**
             * Calculate the closeness value to a tuya, in the range [0, 1]. 1 = Center of a tuya, 0 = Nowhere near.
             */
            public float calculateEasing(int x, int z, int rarity)
            {
                final Cellular2D.Cell cell = cellNoise.cell(x, z);
                if (checkCellRarity(cell, rarity))
                {
                    return calculateClampedEasing((float) cell.f1());
                }
                return 0;
            }

            private static float calculateEasing(float f1)
            {
                return Mth.map(f1, 0, 0.23f, 1, 0);
            }

            private static float calculateClampedEasing(float f1)
            {
                return Mth.clamp(calculateEasing(f1), 0, 1);
            }

            @Override
            public boolean isValidBiome(BiomeExtension biome)
            {
                return biome.hasTuyas();
            }

            /**
             * Calculate the center of the nearest tuya, if one exists, to the given x, z, at the given y.
             */
            @Nullable
            public BlockPos calculateCenter(int x, int y, int z, int rarity)
            {
                final Cellular2D.Cell cell = cellNoise.cell(x, z);
                if (checkCellRarity(cell, rarity))
                {
                    return new BlockPos((int) cell.x(), y, (int) cell.y());
                }
                return null;
            }
        };
    }

    public static CenteredFeatureNoiseSampler stratovolcano(Seed seed)
    {
        return new CenteredFeatureNoiseSampler()
        {
            final Cellular2D cellNoise = new Cellular2D(seed.seed(), 2).spread(0.0024f); // TODO: Evaluate whether we want this extra sample, or if we want lower jitter
            final Noise2D ridgeWarpNoise = new OpenSimplex2D(seed.seed() + 23L).octaves(2).scaled(-0.4f, 0.4f).spread(0.09f);
            final Noise2D rimWarpNoise = new OpenSimplex2D(seed.seed() + 1431L).octaves(2).scaled(-0.08f, 0.08f).spread(0.03f);
            final Noise2D textureNoise = new OpenSimplex2D(seed.seed() + 24482L).octaves(3).spread(0.06).scaled(0.92, 1.08);
            final double verticalScale = 1.2; // TODO: This is a temporary variable, get rid of it

            @Override
            public double setColumnAndSampleHeight(double heightIn, int x, int z, BiomeSourceExtension biomeSource)
            {
                Cellular2D.Cell cell = cellNoise.cell(x, z);
                final BiomeExtension biome = biomeSource.getBiomeExtension(QuartPos.fromBlock((int) cell.x()), QuartPos.fromBlock((int) cell.y()));
                if (biome.hasStratovolcanoes())
                {
                    final int rarity = biome.getCenteredFeatureRarity();
                    if (checkCellRarity(cell, rarity))
                    {
                        return modifyHeight(cell, x, z, biome, heightIn);
                    }
                }
                return ChunkHeightFiller.NOT_PRESENT_RETURN;
            }

            public BiomeExtension getCenterBiome(int x, int z, BiomeSourceExtension biomeSource)
            {
                Cellular2D.Cell cell = cellNoise.cell(x, z);
                return biomeSource.getBiomeExtension(QuartPos.fromBlock((int) cell.x()), QuartPos.fromBlock((int) cell.y()));
            }

            /**
             * Calculate the closeness value to a volcano, in the range [0, 1]. 1 = Center of a volcano, 0 = Nowhere near.
             */
            public float calculateEasing(int x, int z, int rarity)
            {
                final Cellular2D.Cell cell = cellNoise.cell(x, z);
                if (checkCellRarity(cell, rarity))
                {
                    return calculateClampedEasing((float) cell.f1());
                }
                return 0;
            }

            private double modifyHeight(Cellular2D.Cell cell, int x, int z, BiomeExtension biome, double heightIn)
            {
                // We start by determining the diameter and height of the cone
                // Note that apex heights are scaled to equal the actual diameter of the feature
                double maxDiameter = Math.sqrt(Math.min(1, maxSafeDiameterSquared(cell)));

                // Then we set up our polar coordinate system
                final double shape; // The output height, domain [0, 1]

                final int borderHeight;
                if (maxDiameter >= 0.7) // TODO: Work out what this number should be
                {
                    shape = getHeightCraterLake(cell, maxDiameter, x, z);
                    borderHeight = 30;
                }
                else if (maxDiameter >= 0.5)
                {
                    // TODO: Select a shape for a large volcano
                    shape = getHeightFuji(cell, maxDiameter, x, z);
                    borderHeight = 20;
                }
                else
                {
                    // TODO: Select a shape for a small volcano
                    shape = getHeightFuji(cell, maxDiameter, x, z);
                    borderHeight = 10;
                }

                final double volcanoAdditionalHeight = shape * biome.getCenteredFeatureScaleHeight();
                final double volcanoHeight = (SEA_LEVEL_Y + biome.getCenteredFeatureBaseHeight() + volcanoAdditionalHeight);

                // TODO: These borders are just for visuals
                if (cell.f2() - cell.f1() < 0.05)
                {
                    return heightIn + borderHeight;
                }
                return Math.max(heightIn, volcanoHeight);

            }

            // Simple cone shape, similar to Mt. Fuji, Japan
            private double getHeightFuji(Cellular2D.Cell cell, double maxDiam, int x, int z)
            {
                final double noise = cell.noise();
                final double apexHeight = maxDiam; // TODO: Random heights? 0.25 * (3 + Helpers.hashDouble(noise, 2)) * maxDiam;

                // Simple cone
                final double r0 = Mth.map(Mth.sqrt((float) cell.f1()), 0, apexHeight * 0.5, 0, 1); // Radius squared, range [0, 1]
                final double craterSize = 0.04 + 0.06 * Helpers.hashDouble(noise, 10);
                double shape = apexHeight * calculateSimpleRadialShape(r0, craterSize) * verticalScale;
                shape = shape * (0.9 + 0.1 * calculateCircumferentialErosion(cell, craterSize, 0.2, 0.9, 1, r0, x, z, 3, (int) (maxDiam * 16)));

                return shape;
            }

            // Large Crater with a central lake, Similar to Crater Lake, OR
            private double getHeightCraterLake(Cellular2D.Cell cell, double maxDiam, int x, int z)
            {
                final double noise = cell.noise();

                // Simple cone
                final double f1 = cell.f1();
                final double r = Mth.map(Mth.sqrt((float) f1), 0, maxDiam * 0.5, 0, 1); // Radius squared, range [0, 1]
                final double craterSize = 0.5 + rimWarpNoise.noise(x, z); // Domain warp the rim to get a wavy shape
                final double rimHeight = 0.35;
                double shape = rimHeight * calculateSimpleRadialShape(r, craterSize) * verticalScale;
                shape = shape * (0.88 + 0.12 * calculateCircumferentialErosion(cell, craterSize, craterSize + 0.06, 0.95, 1, r, x, z, 24, (int) (maxDiam * 32)));
                shape = shape * (0.93 + 0.08 * calculateCircumferentialErosion(cell, craterSize * 0.4, craterSize * 0.8, craterSize * 0.8, craterSize, r, x, z, 24, (int) (maxDiam * 32)));
                return shape * textureNoise.noise(x, z);
            }

            private double calculateCircumferentialErosion(Cellular2D.Cell cell, double rInner0, double rInner1, double rOuter1, double rOuter0, double r, int x, int z, int minRidgeCount, int addedRidgeCount)
            {
                final double noise = Helpers.hashDouble(cell.noise(), 213);
                final int ridges = (int) (noise * addedRidgeCount) + minRidgeCount;
                final double ridgeWarping = ridgeWarpNoise.noise(x, z) / ridges;
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

            /**
             * @param r The scaled, non-square distance from the volcano, from 0 at center to 1 at edge of influence
             * @param rCrater The radius of the crater
             * @return A noise function determining the volcano's height at any given position, in the range [0, 1]
             */
            private static double calculateSimpleRadialShape(double r, double rCrater)
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

            private static float calculateEasing(float f1)
            {
                return Mth.map(f1, 0, 0.23f, 1, 0);
            }

            private static float calculateClampedEasing(float f1)
            {
                return Mth.clamp(calculateEasing(f1), 0, 1);
            }

            /**
             * Method for adding a single gap to a cellular feature at a random angle
             * @param gapSize size of the gap in diamond angle units
             * @param a1 angle of this position
             * @param aGap angle of the gap
             * @return a value ranging from 1 far from the gap, to 0 in the middle of a large gap
             */
            private static double getGapVerticalEasing(double gapSize, double a1, double aGap)
            {
                // Only adjust this if a gap should exist at all
                double gapVerticalEasing = 1;
                if (gapSize > 0)
                {
                    final double angleToGap = Math.abs(a1 - aGap);
                    // If angle to gap is larger than the gap size, we are far from the gap and can skip calculations
                    if (angleToGap < gapSize)
                    {
                        final double angleToGapEdge = Math.abs(Math.min(angleToGap + gapSize, angleToGap - gapSize));

                        // Gap scale should be lowest at the center of the gap
                        gapVerticalEasing = Mth.clampedMap(angleToGapEdge, 0, Math.max(0.3, 0.6 * gapSize), 1, 0);
                    }
                }
                return gapVerticalEasing;
            }

            public double maxSafeDiameterSquared(Cellular2D.Cell cell)
            {
                // This step is necessary because the exact center of a cell is not aligned with the exact center of a block
                // Which causes the sampled position to be on one side of the center, and potentially closer to a different cell
                double f2 = Math.min(cellNoise.cell(cell.x() + 1, cell.y()).f2(), cellNoise.cell(cell.x() - 1, cell.y()).f2());
                f2 = Math.min(f2, cellNoise.cell(cell.x(), cell.y() + 1).f2());
                f2 = Math.min(f2, cellNoise.cell(cell.x(), cell.y() - 1).f2());

                return f2;
            }

            @Override
            public boolean isValidBiome(BiomeExtension biome)
            {
                return biome.hasStratovolcanoes();
            }

            /**
             * Calculate the center of the nearest volcano, if one exists, to the given x, z, at the given y.
             */
            @Override
            @Nullable
            public BlockPos calculateCenter(int x, int y, int z, int rarity)
            {
                final Cellular2D.Cell cell = cellNoise.cell(x, z);
                if (checkCellRarity(cell, rarity))
                {
                    return new BlockPos((int) cell.x(), y, (int) cell.y());
                }
                return null;
            }
        };
    }
}
