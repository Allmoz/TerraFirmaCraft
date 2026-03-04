package net.dries007.tfc.world.volcano;

import net.dries007.tfc.world.noise.Cellular2D;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;

/**
 * Interface for different styles of a single volcano type. Used by stratovolcanoes to have a wider variety of shapes
 */
public interface VolcanoVariant
{
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

    void buildSurface(SurfaceBuilderContext context, int startY, int endY, CenteredFeatureNoiseSampler sampler);
}
