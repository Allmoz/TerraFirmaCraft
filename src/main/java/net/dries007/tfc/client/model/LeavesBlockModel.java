/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model;

import java.util.List;
import java.util.function.Function;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blocks.wood.TFCLeavesBlock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.climate.Climate;

public class LeavesBlockModel implements IDynamicBakedModel, IUnbakedGeometry<LeavesBlockModel>
{
    private final BlockModel denseLeaves;
    private final BlockModel sparseLeaves;
    private final BlockModel bare;
    private final BlockModel snowyBare;
    private final BlockModel snowyLeaves;
    private final BlockModel blooming;

    @Nullable private BakedModel denseLeavesBakedModel;
    @Nullable private BakedModel sparseLeavesBakedModel;
    @Nullable private BakedModel bareBakedModel;
    @Nullable private BakedModel snowyBareBakedModel;
    @Nullable private BakedModel snowyLeavesBakedModel;
    @Nullable private BakedModel bloomingBakedModel;

    public LeavesBlockModel(BlockModel denseLeaves, BlockModel sparseLeaves, BlockModel bare, BlockModel snowyBare, BlockModel snowyLeaves, BlockModel blooming)
    {
        this.denseLeaves = denseLeaves;
        this.sparseLeaves = sparseLeaves;
        this.bare = bare;
        this.snowyBare = snowyBare;
        this.snowyLeaves = snowyLeaves;
        this.blooming = blooming;
    }

    @Override
    public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData data)
    {
        return data.derive().with(BakedModelData.PROPERTY, new BakedModelData(getModelFromBlockState(state, pos))).build();
    }

    /**
     * TODO: Link to getSeasonalFoliageColor and write comment and stuff
     */
    private BakedModel getModelFromBlockState(@Nullable BlockState state, @Nullable BlockPos pos)
    {
        // Checks whether the tree species has a flowering stage
        final boolean flowers;
        if (pos == null)
        {
            pos = BlockPos.ZERO;
        }
        if (state == null)
        {
            flowers = false;
        }
        else
        {
            final Block block = state.getBlock();
            if (block instanceof TFCLeavesBlock)
            {
                flowers = ((TFCLeavesBlock) block).hasFlowers();
            }
            else
            {
                flowers = false;
            }
        }

        // Calculates the seasons based on average temperature
        // Should match the method used in TFCColors
        // TODO: depending on climate, should use either temp or rainVar
        // TODO: hashing could happen here if we want to make a hashed border between decid/everg/monso
        final Level level = ClientHelpers.getLevel();
        final float temp = level != null ? Climate.getAverageTemperature(level, pos) : 5f;
        float timeOfYear = Calendars.CLIENT.getCalendarFractionOfYear();
        final float tempClamped = temp > 12f ? 12f : Math.max(temp, -20f);

        final float cubedTerm = 1.5f * (float) Math.pow(tempClamped + 3f, 3f) / 4913f;
        final float squaredTerm = 0.5f * (float) Math.pow(tempClamped + 3f, 2f) / 289f;
        final float autumnStart = (cubedTerm + squaredTerm + 8.5f) / 12f;
        final float autumnEnd = temp > 12f ? autumnStart : (cubedTerm - squaredTerm + 10.5f) / 12f;
        final float autumnMid = 0.5f * (autumnEnd + autumnStart);
        final float springStart = 1f - autumnEnd;
        final float springMid = 1f - autumnMid;
        final float springEnd = 1f - autumnStart;

        // Positional hashing to fuzz the time of year per-block
        final int positionDeltaHash = (Helpers.hash(836494187578334123L, pos) & 127) - 63;
        timeOfYear = (timeOfYear + (positionDeltaHash / 4096f)) % 1;

        // TODO: hook this up to alc's magic snow system
        final boolean snowy = false;

        if (snowy)
        {
            if (timeOfYear > autumnEnd || timeOfYear < springStart)
            {
                assert snowyBareBakedModel != null;
                return snowyBareBakedModel;
            }
            else
            {
                assert snowyLeavesBakedModel != null;
                return snowyLeavesBakedModel;
            }
        }
        else
        {
            if (timeOfYear > autumnEnd)
            {
                assert bareBakedModel != null;
                return bareBakedModel;
            }
            else if (timeOfYear > autumnMid)
            {
                assert sparseLeavesBakedModel != null;
                return sparseLeavesBakedModel;
            }
            else if (timeOfYear > springEnd)
            {
                assert denseLeavesBakedModel != null;
                return denseLeavesBakedModel;
            }
            else if (timeOfYear > springMid)
            {
                assert sparseLeavesBakedModel != null;
                return sparseLeavesBakedModel;
            }
            else if (timeOfYear > springStart)
            {
                if (flowers)
                {
                    assert bloomingBakedModel != null;
                    return bloomingBakedModel;
                }
                else
                {
                    assert sparseLeavesBakedModel != null;
                    return sparseLeavesBakedModel;
                }
            }
            else
            {
                assert bareBakedModel != null;
                return bareBakedModel;
            }
        }
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> atlas, ModelState modelState, ItemOverrides overrides)
    {
        denseLeavesBakedModel = denseLeaves.bake(baker, atlas, modelState);
        sparseLeavesBakedModel = sparseLeaves.bake(baker, atlas, modelState);
        bareBakedModel = bare.bake(baker, atlas, modelState);
        snowyBareBakedModel = snowyBare.bake(baker, atlas, modelState);
        snowyLeavesBakedModel = snowyLeaves.bake(baker, atlas, modelState);
        bloomingBakedModel = blooming.bake(baker, atlas, modelState);
        return this;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random, ModelData modelData, @Nullable RenderType renderType)
    {
        final BakedModelData bakedData = modelData.get(BakedModelData.PROPERTY);
        if (bakedData != null)
        {
            return bakedData.toRender.getQuads(state, direction, random, modelData, renderType);
        }

        return getModelFromBlockState(state, null).getQuads(state, direction, random, modelData, renderType);
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context)
    {
        denseLeaves.resolveParents(modelGetter);
        sparseLeaves.resolveParents(modelGetter);
        bare.resolveParents(modelGetter);
        snowyBare.resolveParents(modelGetter);
        snowyLeaves.resolveParents(modelGetter);
        blooming.resolveParents(modelGetter);
    }

    @Override
    public boolean useAmbientOcclusion()
    {
        return true;
    }

    @Override
    public boolean isGui3d()
    {
        return false;
    }

    @Override
    public boolean usesBlockLight()
    {
        return true;
    }

    @Override
    public boolean isCustomRenderer()
    {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public TextureAtlasSprite getParticleIcon()
    {
        return bloomingBakedModel != null ? bloomingBakedModel.getParticleIcon() : RenderHelpers.missingTexture();
    }

    @Override
    public TextureAtlasSprite getParticleIcon(ModelData data)
    {
        final BakedModelData bakedData = data.get(BakedModelData.PROPERTY);
        return bakedData != null ? bakedData.toRender.getParticleIcon(data) : RenderHelpers.missingTexture();
    }

    @Override
    public ItemOverrides getOverrides()
    {
        return ItemOverrides.EMPTY;
    }

    record BakedModelData(BakedModel toRender)
    {
        public static final ModelProperty<BakedModelData> PROPERTY = new ModelProperty<>();
    }

    public static class Loader implements IGeometryLoader<LeavesBlockModel>
    {
        public static final Loader INSTANCE = new Loader();

        private Loader() {}

        @Override
        public LeavesBlockModel read(JsonObject json, JsonDeserializationContext context) throws JsonParseException
        {
            return new LeavesBlockModel(
                context.deserialize(json.get("dense_leaves"), BlockModel.class),
                context.deserialize(json.get("sparse_leaves"), BlockModel.class),
                context.deserialize(json.get("bare"), BlockModel.class),
                context.deserialize(json.get("snowy_bare"), BlockModel.class),
                context.deserialize(json.get("snowy_leaves"), BlockModel.class),
                context.deserialize(json.get("blooming"), BlockModel.class)
            );
        }
    }
}