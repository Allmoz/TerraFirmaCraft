/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.volcano;

import net.dries007.tfc.world.noise.Cellular2D;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;

/**
 * Interface for different styles of a single volcano type. Used by stratovolcanoes to have a wider variety of shapes
 */
public interface VolcanoVariant
{
    String name();

    default double getHeight(double heightIn, int x, int z, double maxDiam, double biomeScaleHeight, double biomeBaseHeight, Cellular2D.Cell cell)
    {
        return heightIn;
    }

    // TODO: Javadocs
    default double getLandHeight(double heightIn, int x, int z, double maxDiam, double biomeScaleHeight, double biomeBaseHeight, Cellular2D.Cell cell)
    {
        return heightIn;
    }

    default double getGlacierHeight(double heightIn, int x, int z, double maxDiam, double biomeScaleHeight, double biomeBaseHeight, Cellular2D.Cell cell)
    {
        return 0;
    }

    default double getFluidHeight(double heightIn, int x, int z, double maxDiam, double biomeScaleHeight, double biomeBaseHeight, Cellular2D.Cell cell)
    {
        return 0;
    }

    boolean buildSurface(SurfaceBuilderContext context, int oceanFloorHeight, int preVolcanicHeight, CenteredFeatureNoiseSampler sampler);
}
