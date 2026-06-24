/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.data.providers;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.data.Accessors;

public class BuiltinBiomeTags extends BiomeTagsProvider implements Accessors
{
    public BuiltinBiomeTags(
        GatherDataEvent event,
        CompletableFuture<HolderLookup.Provider> lookup
    )
    {
        super(event.getGenerator().getPackOutput(), lookup, TerraFirmaCraft.MOD_ID, event.getExistingFileHelper());
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        //=====TFC Tags=====//

        /*tag(TFCTags.Biomes.HAS_PREDICTABLE_WINDS)
            .addTag(TFCTags.Biomes.IS_OCEAN)
            .add(TFCBiomes.SHORE.key())
            .add(TFCBiomes.TIDAL_FLATS.key());

        tag(TFCTags.Biomes.BURRENS).add(
            TFCBiomes.BURREN_PLATEAU.key(), TFCBiomes.BURREN_BADLANDS.key(), TFCBiomes.BURREN_BADLANDS_TALL.key(),
            TFCBiomes.BURREN_PLAINS.key(), TFCBiomes.BURREN_ROCHE_MOUTONEE.key()
        );

        tag(TFCTags.Biomes.CENOTES).add(
            TFCBiomes.CENOTE_PLAINS.key(), TFCBiomes.CENOTE_HILLS.key(),
            TFCBiomes.CENOTE_ROLLING_HILLS.key(), TFCBiomes.CENOTE_CANYONS.key(),
            TFCBiomes.CENOTE_HIGHLANDS.key(), TFCBiomes.CENOTE_PLATEAU.key()
        );

        tag(TFCTags.Biomes.DOLINES).add(
            TFCBiomes.DOLINE_PLAINS.key(), TFCBiomes.DOLINE_HILLS.key(),
            TFCBiomes.DOLINE_ROLLING_HILLS.key(), TFCBiomes.DOLINE_HIGHLANDS.key(),
            TFCBiomes.DOLINE_PLATEAU.key(), TFCBiomes.DOLINE_CANYONS.key()
        );

        tag(TFCTags.Biomes.IS_LAKE).add(
            TFCBiomes.LAKE.key(), TFCBiomes.MOUNTAIN_LAKE.key(),
            TFCBiomes.OLD_MOUNTAIN_LAKE.key(), TFCBiomes.OCEANIC_MOUNTAIN_LAKE.key(),
            TFCBiomes.VOLCANIC_MOUNTAIN_LAKE.key(), TFCBiomes.VOLCANIC_OCEANIC_MOUNTAIN_LAKE.key(),
            TFCBiomes.PLATEAU_LAKE.key(), TFCBiomes.SUBGLACIAL_LAKE.key(),
            TFCBiomes.MELTWATER_LAKE.key(), TFCBiomes.TOWER_KARST_LAKE.key()
        );

        tag(TFCTags.Biomes.IS_OCEAN).add(
            TFCBiomes.OCEAN.key(), TFCBiomes.OCEAN_REEF.key(),
            TFCBiomes.DEEP_OCEAN.key(), TFCBiomes.DEEP_OCEAN_TRENCH.key(),
            TFCBiomes.SHORE.key(), TFCBiomes.TIDAL_FLATS.key(),
            TFCBiomes.SEA_STACKS.key(), TFCBiomes.TERRACE_UPPER.key(),
            TFCBiomes.TERRACE_LOWER.key(), TFCBiomes.SETBACK_CLIFFS.key(),
            TFCBiomes.COASTAL_DUNES.key(), TFCBiomes.ROCKY_SHORES.key(),
            TFCBiomes.EMBAYMENTS.key(), TFCBiomes.SHIELD_VOLCANO_SHORE.key(),
            TFCBiomes.OLD_SHIELD_VOLCANO_SHORE.key(), TFCBiomes.ICE_SHEET_OCEANIC.key(),
            TFCBiomes.ICE_SHEET_SHORE.key()
        );

        tag(TFCTags.Biomes.IS_RIVER)
            .add(TFCBiomes.RIVER.key());

        tag(TFCTags.Biomes.IS_VOLCANIC).add(
            TFCBiomes.CANYONS.key(), TFCBiomes.VOLCANIC_MOUNTAINS.key(),
            TFCBiomes.VOLCANIC_OCEANIC_MOUNTAINS.key(), TFCBiomes.ACTIVE_SHIELD_VOLCANO.key(),
            TFCBiomes.VOLCANIC_MOUNTAIN_LAKE.key(), TFCBiomes.VOLCANIC_OCEANIC_MOUNTAIN_LAKE.key()
        );

        tag(TFCTags.Biomes.KARSTS)
            .addTag(TFCTags.Biomes.BURRENS)
            .addTag(TFCTags.Biomes.CENOTES)
            .addTag(TFCTags.Biomes.DOLINES)
            .addTag(TFCTags.Biomes.SHILINS)
            .addTag(TFCTags.Biomes.TOWER_KARSTS);

        tag(TFCTags.Biomes.SHILINS).add(
            TFCBiomes.SHILIN_PLAINS.key(), TFCBiomes.SHILIN_CANYONS.key(),
            TFCBiomes.SHILIN_HILLS.key(), TFCBiomes.SHILIN_HIGHLANDS.key(),
            TFCBiomes.SHILIN_PLATEAU.key()
        );

        tag(TFCTags.Biomes.TOWER_KARSTS).add(
            TFCBiomes.TOWER_KARST_PLAINS.key(), TFCBiomes.TOWER_KARST_CANYONS.key(),
            TFCBiomes.TOWER_KARST_HILLS.key(), TFCBiomes.TOWER_KARST_HIGHLANDS.key(),
            TFCBiomes.EXTREME_DOLINE_PLATEAU.key(), TFCBiomes.EXTREME_DOLINE_MOUNTAINS.key(),
            TFCBiomes.TOWER_KARST_LAKE.key(), TFCBiomes.TOWER_KARST_BAY.key()
        );*/

        //=====Common Tags=====//

        tag(commonTagOf(Registries.BIOME, "is_lake"))
            .addTag(TFCTags.Biomes.IS_LAKE);

        tag(Tags.Biomes.IS_OCEAN)
            .addTag(TFCTags.Biomes.IS_OCEAN);

        tag(Tags.Biomes.IS_RIVER)
            .addTag(TFCTags.Biomes.IS_RIVER);

        tag(Tags.Biomes.IS_MOUNTAIN)
            .add(TFCBiomes.EXTREME_DOLINE_MOUNTAINS.key())
            .add(TFCBiomes.GLACIALLY_CARVED_MOUNTAINS.key())
            .add(TFCBiomes.GLACIALLY_CARVED_OCEANIC_MOUNTAINS.key())
            .add(TFCBiomes.GLACIATED_MOUNTAINS.key())
            .add(TFCBiomes.GLACIATED_OCEANIC_MOUNTAINS.key())
            .add(TFCBiomes.ICE_SHEET_MOUNTAINS.key())
            .add(TFCBiomes.ICE_SHEET_MOUNTAINS_EDGE.key())
            .add(TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS.key())
            .add(TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS_EDGE.key())
            .add(TFCBiomes.MOUNTAINS.key())
            .add(TFCBiomes.OCEANIC_MOUNTAINS.key())
            .add(TFCBiomes.OLD_MOUNTAINS.key());

        tag(commonTagOf(Registries.BIOME, "is_volcanic"))
            .addTag(TFCTags.Biomes.IS_VOLCANIC);

        tag(Tags.Biomes.IS_AQUATIC)
            .addTag(TFCTags.Biomes.IS_OCEAN)
            .addTag(TFCTags.Biomes.IS_RIVER)
            .addTag(TFCTags.Biomes.IS_LAKE);

        tag(Tags.Biomes.IS_AQUATIC_ICY)
            .add(TFCBiomes.ICE_SHEET_OCEANIC.key())
            .add(TFCBiomes.ICE_SHEET_SHORE.key())
            .add(TFCBiomes.MELTWATER_LAKE.key())
            .add(TFCBiomes.SUBGLACIAL_LAKE.key());

        tag(Tags.Biomes.IS_BADLANDS)
            .add(TFCBiomes.BADLANDS.key())
            .add(TFCBiomes.BURREN_BADLANDS.key())
            .add(TFCBiomes.BURREN_BADLANDS_TALL.key())
            .add(TFCBiomes.BUTTES.key())
            .add(TFCBiomes.HOODOOS.key())
            .add(TFCBiomes.STAIR_STEP_CANYONS.key())
            .add(TFCBiomes.WHORLED_CANYONS.key())
            .add(TFCBiomes.MESAS.key());

        tag(Tags.Biomes.IS_BEACH)
            .add(TFCBiomes.SHORE.key())
            .add(TFCBiomes.COASTAL_DUNES.key())
            .add(TFCBiomes.TIDAL_FLATS.key())
            .add(TFCBiomes.ROCKY_SHORES.key())
            .add(TFCBiomes.MUD_FLATS.key())
            .add(TFCBiomes.SEA_STACKS.key())
            .add(TFCBiomes.SETBACK_CLIFFS.key())
            .add(TFCBiomes.TERRACE_UPPER.key())
            .add(TFCBiomes.TERRACE_LOWER.key())
            .add(TFCBiomes.EMBAYMENTS.key())
            .add(TFCBiomes.SHIELD_VOLCANO_SHORE.key())
            .add(TFCBiomes.OLD_SHIELD_VOLCANO_SHORE.key())
            .add(TFCBiomes.ICE_SHEET_SHORE.key());

        tag(Tags.Biomes.IS_COLD_OVERWORLD)
            .add(TFCBiomes.ICE_SHEET.key())
            .add(TFCBiomes.ICE_SHEET_EDGE.key())
            .add(TFCBiomes.ICE_SHEET_MOUNTAINS.key())
            .add(TFCBiomes.ICE_SHEET_MOUNTAINS_EDGE.key())
            .add(TFCBiomes.ICE_SHEET_OCEANIC.key())
            .add(TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS.key())
            .add(TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS_EDGE.key())
            .add(TFCBiomes.ICE_SHEET_SHORE.key())
            .add(TFCBiomes.ICE_SHEET_TUYAS.key())
            .add(TFCBiomes.ICE_SHEET_TUYAS_EDGE.key())
            .add(TFCBiomes.ICE_SHEET_SHIELD_VOLCANO.key())
            .add(TFCBiomes.SUBGLACIAL_LAKE.key())
            .add(TFCBiomes.MELTWATER_LAKE.key())
            .add(TFCBiomes.GLACIATED_MOUNTAINS.key())
            .add(TFCBiomes.GLACIATED_OCEANIC_MOUNTAINS.key())
            .add(TFCBiomes.GLACIALLY_CARVED_MOUNTAINS.key())
            .add(TFCBiomes.GLACIALLY_CARVED_OCEANIC_MOUNTAINS.key())
            .add(TFCBiomes.GLACIATED_SHIELD_VOLCANO.key())
            .add(TFCBiomes.DRUMLINS.key())
            .add(TFCBiomes.TUYAS.key())
            .add(TFCBiomes.KNOB_AND_KETTLE.key())
            .add(TFCBiomes.PATTERNED_GROUND.key())
            .add(TFCBiomes.INVERTED_PATTERNED_GROUND.key())
            .add(TFCBiomes.STONE_CIRCLES.key());

        tag(Tags.Biomes.IS_DEEP_OCEAN)
            .add(TFCBiomes.DEEP_OCEAN.key())
            .add(TFCBiomes.DEEP_OCEAN_TRENCH.key());

        tag(Tags.Biomes.IS_DRY)
            .add(TFCBiomes.GRASSY_DUNES.key())
            .add(TFCBiomes.BUTTES.key())
            .add(TFCBiomes.HOODOOS.key())
            .add(TFCBiomes.WHORLED_CANYONS.key())
            .add(TFCBiomes.MESAS.key())
            .add(TFCBiomes.STAIR_STEP_CANYONS.key())
            .add(TFCBiomes.DUNE_SEA.key())
            .add(TFCBiomes.SALT_FLATS.key())
            .add(TFCBiomes.ROCKY_PLATEAU.key());


        tag(Tags.Biomes.IS_HILL)
            .add(TFCBiomes.HILLS.key())
            .add(TFCBiomes.ROLLING_HILLS.key())
            .add(TFCBiomes.HIGHLANDS.key())
            .add(TFCBiomes.CENOTE_HILLS.key())
            .add(TFCBiomes.CENOTE_ROLLING_HILLS.key())
            .add(TFCBiomes.CENOTE_HIGHLANDS.key())
            .add(TFCBiomes.DOLINE_HILLS.key())
            .add(TFCBiomes.DOLINE_ROLLING_HILLS.key())
            .add(TFCBiomes.DOLINE_HIGHLANDS.key())
            .add(TFCBiomes.SHILIN_HILLS.key())
            .add(TFCBiomes.SHILIN_HIGHLANDS.key())
            .add(TFCBiomes.SHILIN_CANYONS.key())
            .add(TFCBiomes.TOWER_KARST_HILLS.key())
            .add(TFCBiomes.TOWER_KARST_HIGHLANDS.key())
            .add(TFCBiomes.DRUMLINS.key())
            .add(TFCBiomes.KNOB_AND_KETTLE.key());

        tag(Tags.Biomes.IS_HOT_OVERWORLD)
            .add(TFCBiomes.OCEAN_REEF.key())
            .add(TFCBiomes.WHORLED_CANYONS.key())
            .add(TFCBiomes.MESAS.key())
            .add(TFCBiomes.BUTTES.key())
            .add(TFCBiomes.HOODOOS.key())
            .add(TFCBiomes.STAIR_STEP_CANYONS.key());

        tag(Tags.Biomes.IS_ICY)
            .add(TFCBiomes.ICE_SHEET_MOUNTAINS.key())
            .add(TFCBiomes.ICE_SHEET.key())
            .add(TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS.key())
            .add(TFCBiomes.ICE_SHEET_OCEANIC.key())
            .add(TFCBiomes.ICE_SHEET_EDGE.key())
            .add(TFCBiomes.ICE_SHEET_MOUNTAINS_EDGE.key())
            .add(TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS_EDGE.key())
            .add(TFCBiomes.ICE_SHEET_SHIELD_VOLCANO.key())
            .add(TFCBiomes.ICE_SHEET_SHORE.key())
            .add(TFCBiomes.ICE_SHEET_TUYAS.key())
            .add(TFCBiomes.ICE_SHEET_TUYAS_EDGE.key());

        tag(Tags.Biomes.IS_PLAINS)
            .add(TFCBiomes.PLAINS.key())
            .add(TFCBiomes.LOWLANDS.key())
            .add(TFCBiomes.BURREN_PLAINS.key())
            .add(TFCBiomes.DOLINE_PLAINS.key())
            .add(TFCBiomes.CENOTE_PLAINS.key())
            .add(TFCBiomes.TOWER_KARST_PLAINS.key())
            .add(TFCBiomes.SALT_FLATS.key())
            .add(TFCBiomes.MUD_FLATS.key())
            .add(TFCBiomes.TIDAL_FLATS.key())
            .add(TFCBiomes.PATTERNED_GROUND.key())
            .add(TFCBiomes.INVERTED_PATTERNED_GROUND.key())
            .add(TFCBiomes.SHILIN_PLAINS.key());

        tag(Tags.Biomes.IS_PLATEAU)
            .add(TFCBiomes.PLATEAU.key())
            .add(TFCBiomes.PLATEAU_WIDE.key())
            .add(TFCBiomes.PLATEAU_LAKE.key())
            .add(TFCBiomes.ROCKY_PLATEAU.key())
            .add(TFCBiomes.BURREN_PLATEAU.key())
            .add(TFCBiomes.SHILIN_PLATEAU.key())
            .add(TFCBiomes.DOLINE_PLATEAU.key())
            .add(TFCBiomes.CENOTE_PLATEAU.key())
            .add(TFCBiomes.EXTREME_DOLINE_PLATEAU.key());

        tag(Tags.Biomes.IS_RARE)
            .addTag(TFCTags.Biomes.KARSTS)
            .add(TFCBiomes.GUANO_ISLAND.key())
            .add(TFCBiomes.VOLCANIC_OCEANIC_MOUNTAIN_LAKE.key())
            .add(TFCBiomes.OCEANIC_MOUNTAIN_LAKE.key())
            .add(TFCBiomes.WHORLED_CANYONS.key())
            .add(TFCBiomes.STAIR_STEP_CANYONS.key());

        tag(Tags.Biomes.IS_SANDY)
            .add(TFCBiomes.COASTAL_DUNES.key())
            .add(TFCBiomes.DUNE_SEA.key())
            .add(TFCBiomes.GRASSY_DUNES.key())
            .add(TFCBiomes.SHORE.key())
            .add(TFCBiomes.SALT_FLATS.key())
            .add(TFCBiomes.TIDAL_FLATS.key());

        tag(Tags.Biomes.IS_SHALLOW_OCEAN)
            .add(TFCBiomes.OCEAN_REEF.key());

        tag(Tags.Biomes.IS_SNOWY)
            .add(TFCBiomes.ICE_SHEET.key())
            .add(TFCBiomes.ICE_SHEET_EDGE.key())
            .add(TFCBiomes.ICE_SHEET_MOUNTAINS.key())
            .add(TFCBiomes.ICE_SHEET_MOUNTAINS_EDGE.key())
            .add(TFCBiomes.ICE_SHEET_OCEANIC.key())
            .add(TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS.key())
            .add(TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS_EDGE.key())
            .add(TFCBiomes.ICE_SHEET_SHORE.key())
            .add(TFCBiomes.ICE_SHEET_TUYAS.key())
            .add(TFCBiomes.ICE_SHEET_TUYAS_EDGE.key())
            .add(TFCBiomes.ICE_SHEET_SHIELD_VOLCANO.key())
            .add(TFCBiomes.SUBGLACIAL_LAKE.key())
            .add(TFCBiomes.MELTWATER_LAKE.key())
            .add(TFCBiomes.GLACIATED_MOUNTAINS.key())
            .add(TFCBiomes.GLACIATED_OCEANIC_MOUNTAINS.key())
            .add(TFCBiomes.GLACIALLY_CARVED_MOUNTAINS.key())
            .add(TFCBiomes.GLACIALLY_CARVED_OCEANIC_MOUNTAINS.key())
            .add(TFCBiomes.GLACIATED_SHIELD_VOLCANO.key())
            .add(TFCBiomes.DRUMLINS.key())
            .add(TFCBiomes.TUYAS.key())
            .add(TFCBiomes.KNOB_AND_KETTLE.key())
            .add(TFCBiomes.PATTERNED_GROUND.key())
            .add(TFCBiomes.INVERTED_PATTERNED_GROUND.key())
            .add(TFCBiomes.STONE_CIRCLES.key());

        tag(Tags.Biomes.IS_SNOWY_PLAINS)
            .add(TFCBiomes.ICE_SHEET.key())
            .add(TFCBiomes.ICE_SHEET_EDGE.key())
            .add(TFCBiomes.PATTERNED_GROUND.key())
            .add(TFCBiomes.INVERTED_PATTERNED_GROUND.key())
            .add(TFCBiomes.STONE_CIRCLES.key());

        tag(Tags.Biomes.IS_STONY_SHORES)
            .add(TFCBiomes.ROCKY_SHORES.key());

        tag(Tags.Biomes.IS_SWAMP)
            .add(TFCBiomes.SALT_MARSH.key())
            .add(TFCBiomes.LOWLANDS.key())
            .add(TFCBiomes.TIDAL_FLATS.key());

        //tag(Tags.Biomes.IS_TEMPERATE_OVERWORLD) What should be here??

        tag(Tags.Biomes.IS_WET_OVERWORLD)
            .addTag(TFCTags.Biomes.KARSTS)
            .add(TFCBiomes.SALT_MARSH.key())
            .add(TFCBiomes.LOWLANDS.key())
            .add(TFCBiomes.TIDAL_FLATS.key());

        tag(Tags.Biomes.IS_WINDSWEPT)
            .add(TFCBiomes.SEA_STACKS.key())
            .add(TFCBiomes.SETBACK_CLIFFS.key());

        tag(Tags.Biomes.IS_SPARSE_VEGETATION_OVERWORLD)
            .add(TFCBiomes.DUNE_SEA.key())
            .add(TFCBiomes.SALT_FLATS.key())
            .add(TFCBiomes.BADLANDS.key())
            .add(TFCBiomes.BUTTES.key())
            .add(TFCBiomes.HOODOOS.key())
            .add(TFCBiomes.STAIR_STEP_CANYONS.key())
            .add(TFCBiomes.WHORLED_CANYONS.key())
            .add(TFCBiomes.MESAS.key())
            .add(TFCBiomes.ROCKY_PLATEAU.key())
            .add(TFCBiomes.CANYONS.key())
            .add(TFCBiomes.BURREN_BADLANDS.key())
            .add(TFCBiomes.BURREN_BADLANDS_TALL.key())
            .add(TFCBiomes.ICE_SHEET_SHORE.key())
            .add(TFCBiomes.ICE_SHEET_MOUNTAINS_EDGE.key())
            .add(TFCBiomes.ICE_SHEET_MOUNTAINS.key())
            .add(TFCBiomes.ICE_SHEET_EDGE.key())
            .add(TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS_EDGE.key())
            .add(TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS.key())
            .add(TFCBiomes.ICE_SHEET_OCEANIC.key())
            .add(TFCBiomes.ICE_SHEET_TUYAS.key())
            .add(TFCBiomes.ICE_SHEET_TUYAS_EDGE.key())
            .add(TFCBiomes.ICE_SHEET_SHIELD_VOLCANO.key())
            .add(TFCBiomes.ICE_SHEET.key())
            .add(TFCBiomes.ICE_SHEET_MOUNTAINS_EDGE.key())
            .add(TFCBiomes.ICE_SHEET_MOUNTAINS.key())
            .add(TFCBiomes.ICE_SHEET_EDGE.key())
            .add(TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS_EDGE.key())
            .add(TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS.key())
            .add(TFCBiomes.ACTIVE_SHIELD_VOLCANO.key())
            .add(TFCBiomes.ANCIENT_SHIELD_VOLCANO.key())
            .add(TFCBiomes.EXTINCT_SHIELD_VOLCANO.key())
            .add(TFCBiomes.DORMANT_SHIELD_VOLCANO.key())
            .add(TFCBiomes.GLACIATED_MOUNTAINS.key())
            .add(TFCBiomes.GLACIATED_OCEANIC_MOUNTAINS.key())
            .add(TFCBiomes.GLACIALLY_CARVED_MOUNTAINS.key())
            .add(TFCBiomes.GLACIALLY_CARVED_OCEANIC_MOUNTAINS.key())
            .add(TFCBiomes.GLACIATED_SHIELD_VOLCANO.key())
            .add(TFCBiomes.DRUMLINS.key())
            .add(TFCBiomes.TUYAS.key())
            .add(TFCBiomes.KNOB_AND_KETTLE.key())
            .add(TFCBiomes.PATTERNED_GROUND.key())
            .add(TFCBiomes.INVERTED_PATTERNED_GROUND.key())
            .add(TFCBiomes.STONE_CIRCLES.key());
    }
}
