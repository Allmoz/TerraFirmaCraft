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
import net.minecraft.util.Mth;
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
import net.dries007.tfc.util.climate.ClimateModel;
import net.dries007.tfc.util.tracker.WeatherHelpers;
import net.dries007.tfc.util.tracker.WorldTracker;
import net.dries007.tfc.world.chunkdata.ChunkData;

import static net.dries007.tfc.world.TFCChunkGenerator.*;

public class LeavesBlockModel implements IDynamicBakedModel, IUnbakedGeometry<LeavesBlockModel>
{
    private final BlockModel denseLeaves;
    private final BlockModel sparseLeaves;
    private final BlockModel bare;
    private final BlockModel blooming;

    @Nullable private BakedModel denseLeavesBakedModel;
    @Nullable private BakedModel sparseLeavesBakedModel;
    @Nullable private BakedModel bareBakedModel;
    @Nullable private BakedModel bloomingBakedModel;

    public LeavesBlockModel(BlockModel denseLeaves, BlockModel sparseLeaves, BlockModel bare, BlockModel snowyBare, BlockModel snowyLeaves, BlockModel blooming)
    {
        this.denseLeaves = denseLeaves;
        this.sparseLeaves = sparseLeaves;
        this.bare = bare;
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
        // TODO: Should maybe add a check here to use the old system if you have fast graphics. Would need to keep winter foliage map around for this to work, though

        // Checks whether the tree species has a flowering stage
        final boolean flowers;
        if (pos == null)
        {
            pos = BlockPos.ZERO;
        }
        // Default to using same texture all year round (evergreen behavior)
        if (state == null)
        {
            assert denseLeavesBakedModel != null;
            return denseLeavesBakedModel;
        }
        else
        {
            final Block block = state.getBlock();
            if (block instanceof TFCLeavesBlock)
            {
                // Skip all the other calculations if the tree is an evergreen
                if (((TFCLeavesBlock) block).isConifer())
                {
                    assert denseLeavesBakedModel != null;
                    return denseLeavesBakedModel;
                }
                flowers = ((TFCLeavesBlock) block).hasFlowers();
            }
            else
            {
                flowers = false;
            }
        }


        final Level level = ClientHelpers.getLevel();
        if (level == null)
        {
            assert denseLeavesBakedModel != null;
            return denseLeavesBakedModel;
        }

        // Calculates the seasons based on average temperature
        // Should match the method used in TFCColors

        // Hash climate values based on block positions. This helps with transitional areas
        final BlockPos seaLevelPos = new BlockPos(pos.getX(), SEA_LEVEL_Y, pos.getZ());
        float temp = Climate.getAverageTemperature(level, seaLevelPos);
        float rainVar = Climate.getRainfallVariance(level, pos);
        if ((temp > 14.7 && temp < 15.8) || (rainVar > 0.38 && rainVar < 0.42))
        {
            final int positionClimateHash = (Helpers.hash(912381187503828153L, pos) & 127);
            temp += (float) (positionClimateHash - 63) / 4_000f;
            rainVar += (float) (positionClimateHash - 63) / 60_000f;
        }
        final float rainVarAbs = Math.abs(rainVar);

        // Skip calcs if above a climate threshold
        if (temp > 15f && rainVarAbs < 0.4)
        {
            assert denseLeavesBakedModel != null;
            return denseLeavesBakedModel;
        }

        float timeOfYear = Calendars.CLIENT.getCalendarFractionOfYear();

        // See Desmos: https://www.desmos.com/calculator/ckdweimnf0
        final float x;
        final boolean inNorthernHemisphere = ClientHelpers.inNorthernHemisphere();
        float seasonOffset = 0;
        if (temp <= 15f)
        {
            // Numbers chosen to create a 2.5-month summer at -20c avg, and a 12-month "summer" at 15c avg
            x = 1.25f * Math.max(temp, -20f) + 5.3f;
            if (!inNorthernHemisphere)
            {
                seasonOffset = 0.5f;
            }
        }
        // Small gap in temperature is so that there are small evergreen bands between dry-season controlled areas and winter-controlled areas
        else if (rainVarAbs > 0.4 && temp > 15.5f)
        {
            final float avgRain = Climate.getAverageRainfall(level, seaLevelPos);
            final float minRain = avgRain * (1 - rainVarAbs);

            if (minRain > 200)
            {
                assert denseLeavesBakedModel != null;
                return denseLeavesBakedModel;
            }

            // Numbers chosen to create a 4-month wet season at max rain var & min rain = 0, and a 12-month "wet season" at minimum rain var & min rain = 200
            // Uses multiple variables to ensure smooth transitions, and that biomes that have green grass year-round do not lose leaves
            x = .2604f * (0.4f - rainVarAbs) * (200f - minRain) + 18.75f + 5.3f;
            if (rainVar < -0.4)
            {
                seasonOffset = 0.5f;
            }
        }
        // If not in any of the above areas, must be in an evergreen border-belt
        else
        {
            assert denseLeavesBakedModel != null;
            return denseLeavesBakedModel;
        }
        final float cubedTerm = 0.000203f * x * x * x; // 1 / 17^3
        final float squaredTerm = 0.00346f * x * x; // 1 / 17^2

        // Offset the seasons by six months if dry-season controls and the dry season occurs in winter months
        timeOfYear = (timeOfYear + seasonOffset) % 1;

        final float autumnEnd = (cubedTerm - squaredTerm + 10.5f) / 12f;

        // Positional hashing to fuzz the time of year per-block
        final int positionDeltaHash = (Helpers.hash(836494187578334123L, pos) & 127);
        timeOfYear = (timeOfYear + ((positionDeltaHash - 63) / 4096f)) % 1;

        if (timeOfYear > autumnEnd)
        {
            assert bareBakedModel != null;
            return bareBakedModel;
        }

        final float autumnStart = (cubedTerm + squaredTerm + 8.2f) / 12f;
        final float autumnMid = 0.5f * (autumnEnd + autumnStart);
        if (timeOfYear > autumnMid)
        {
            assert sparseLeavesBakedModel != null;
            return sparseLeavesBakedModel;
        }

        final float springEnd = 1f - autumnStart;
        if (timeOfYear > springEnd)
        {
            assert denseLeavesBakedModel != null;
            return denseLeavesBakedModel;
        }

        final float springMid = 1f - autumnMid;
        if (timeOfYear > springMid)
        {
            assert sparseLeavesBakedModel != null;
            return sparseLeavesBakedModel;
        }

        // This is built such that humid tropical regions never bloom
        final float springStart = 1f - autumnEnd;
        if (timeOfYear > springStart)
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

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> atlas, ModelState modelState, ItemOverrides overrides)
    {
        denseLeavesBakedModel = denseLeaves.bake(baker, atlas, modelState);
        sparseLeavesBakedModel = sparseLeaves.bake(baker, atlas, modelState);
        bareBakedModel = bare.bake(baker, atlas, modelState);
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