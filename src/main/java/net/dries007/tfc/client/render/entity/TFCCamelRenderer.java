/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.CamelRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class TFCCamelRenderer extends CamelRenderer
{
    public TFCCamelRenderer(EntityRendererProvider.Context context)
    {
        super(context, ModelLayers.CAMEL);
    }
}
