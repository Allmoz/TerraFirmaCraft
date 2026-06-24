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
import net.dries007.tfc.data.Accessors;
import net.dries007.tfc.world.biome.TFCBiomes;

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

        //======Common Tags======//

        tag(commonTagOf(Registries.BIOME, "is_lake")).addTag(TFCTags.Biomes.IS_LAKE);

        tag(Tags.Biomes.IS_OCEAN).addTag(TFCTags.Biomes.IS_OCEAN);

        tag(Tags.Biomes.IS_RIVER).addTag(TFCTags.Biomes.IS_RIVER);

        tag(Tags.Biomes.IS_MOUNTAIN).add(
            TFCBiomes.EXTREME_DOLINE_MOUNTAINS.key(), TFCBiomes.GLACIALLY_CARVED_MOUNTAINS.key(),
            TFCBiomes.GLACIALLY_CARVED_OCEANIC_MOUNTAINS.key(), TFCBiomes.GLACIATED_MOUNTAINS.key(),
            TFCBiomes.GLACIATED_OCEANIC_MOUNTAINS.key(), TFCBiomes.ICE_SHEET_MOUNTAINS.key(),
            TFCBiomes.ICE_SHEET_MOUNTAINS_EDGE.key(), TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS.key(),
            TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS_EDGE.key(), TFCBiomes.MOUNTAINS.key(),
            TFCBiomes.OCEANIC_MOUNTAINS.key(), TFCBiomes.OLD_MOUNTAINS.key(),
            TFCBiomes.COLLISIONAL_MOUNTAINS.key(), TFCBiomes.GLACIATED_VOLCANIC_OCEANIC_MOUNTAINS.key(),
            TFCBiomes.GLACIALLY_CARVED_MOUNTAINS.key(), TFCBiomes.GLACIALLY_CARVED_OCEANIC_MOUNTAINS.key(),
            TFCBiomes.GLACIATED_VOLCANIC_MOUNTAINS.key(), TFCBiomes.ICE_SHEET_VOLCANIC_OCEANIC_MOUNTAINS.key(),
            TFCBiomes.ICE_SHEET_VOLCANIC_MOUNTAINS.key(), TFCBiomes.VOLCANIC_MOUNTAIN_ISLANDS.key()
        );

        tag(commonTagOf(Registries.BIOME, "is_volcanic"))
            .addTag(TFCTags.Biomes.HAS_CINDER_CONES)
            .addTag(TFCTags.Biomes.HAS_STRATOVOLCANOES)
            .addTag(TFCTags.Biomes.HAS_TUYAS)
            .addTag(TFCTags.Biomes.HAS_TUFF_CONES)
            .addTag(TFCTags.Biomes.IS_SHIELD_VOLCANO)
        ;

        tag(Tags.Biomes.IS_AQUATIC)
            .addTag(TFCTags.Biomes.IS_OCEAN)
            .addTag(TFCTags.Biomes.IS_RIVER)
            .addTag(TFCTags.Biomes.IS_LAKE);

        tag(Tags.Biomes.IS_AQUATIC_ICY).add(
            TFCBiomes.ICE_SHEET_OCEANIC.key(), TFCBiomes.ICE_SHEET_SHORE.key(),
            TFCBiomes.MELTWATER_LAKE.key(), TFCBiomes.SUBGLACIAL_LAKE.key()
        );

        tag(Tags.Biomes.IS_BADLANDS).add(
            TFCBiomes.BADLANDS.key(), TFCBiomes.BURREN_BADLANDS.key(),
            TFCBiomes.BURREN_BADLANDS_TALL.key(), TFCBiomes.BUTTES.key(),
            TFCBiomes.HOODOOS.key(), TFCBiomes.STAIR_STEP_CANYONS.key(),
            TFCBiomes.WHORLED_CANYONS.key(), TFCBiomes.MESAS.key()
        );

        tag(Tags.Biomes.IS_BEACH).add(
            TFCBiomes.SHORE.key(), TFCBiomes.COASTAL_DUNES.key(),
            TFCBiomes.TIDAL_FLATS.key(), TFCBiomes.ROCKY_SHORES.key(),
            TFCBiomes.MUD_FLATS.key(), TFCBiomes.SEA_STACKS.key(),
            TFCBiomes.SETBACK_CLIFFS.key(), TFCBiomes.TERRACE_UPPER.key(),
            TFCBiomes.TERRACE_LOWER.key(), TFCBiomes.EMBAYMENTS.key(),
            TFCBiomes.SHIELD_VOLCANO_SHORE.key(), TFCBiomes.OLD_SHIELD_VOLCANO_SHORE.key(),
            TFCBiomes.ICE_SHEET_SHORE.key()
        );

        tag(Tags.Biomes.IS_COLD_OVERWORLD).add(
            TFCBiomes.ICE_SHEET.key(), TFCBiomes.ICE_SHEET_EDGE.key(),
            TFCBiomes.ICE_SHEET_MOUNTAINS.key(), TFCBiomes.ICE_SHEET_MOUNTAINS_EDGE.key(),
            TFCBiomes.ICE_SHEET_OCEANIC.key(), TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS.key(),
            TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS_EDGE.key(), TFCBiomes.ICE_SHEET_SHORE.key(),
            TFCBiomes.ICE_SHEET_TUYAS.key(), TFCBiomes.ICE_SHEET_TUYAS_EDGE.key(),
            TFCBiomes.ICE_SHEET_SHIELD_VOLCANO.key(), TFCBiomes.SUBGLACIAL_LAKE.key(),
            TFCBiomes.MELTWATER_LAKE.key(), TFCBiomes.GLACIATED_MOUNTAINS.key(),
            TFCBiomes.GLACIATED_OCEANIC_MOUNTAINS.key(), TFCBiomes.GLACIALLY_CARVED_MOUNTAINS.key(),
            TFCBiomes.GLACIALLY_CARVED_OCEANIC_MOUNTAINS.key(), TFCBiomes.GLACIATED_SHIELD_VOLCANO.key(),
            TFCBiomes.DRUMLINS.key(), TFCBiomes.TUYAS.key(),
            TFCBiomes.KNOB_AND_KETTLE.key(), TFCBiomes.PATTERNED_GROUND.key(),
            TFCBiomes.INVERTED_PATTERNED_GROUND.key(), TFCBiomes.STONE_CIRCLES.key(),
            TFCBiomes.GLACIATED_VOLCANIC_MOUNTAINS.key(), TFCBiomes.GLACIATED_VOLCANIC_OCEANIC_MOUNTAINS.key(),
            TFCBiomes.ICE_SHEET_VOLCANIC_MOUNTAINS.key(), TFCBiomes.ICE_SHEET_VOLCANIC_OCEANIC_MOUNTAINS.key()
        );

        tag(Tags.Biomes.IS_DEEP_OCEAN).add(
            TFCBiomes.DEEP_OCEAN.key(), TFCBiomes.DEEP_OCEAN_TRENCH.key(),
            TFCBiomes.DEEP_OCEAN_ATOLLS.key()
        );

        tag(Tags.Biomes.IS_DRY).add(
            TFCBiomes.GRASSY_DUNES.key(), TFCBiomes.BUTTES.key(),
            TFCBiomes.HOODOOS.key(), TFCBiomes.WHORLED_CANYONS.key(),
            TFCBiomes.MESAS.key(), TFCBiomes.STAIR_STEP_CANYONS.key(),
            TFCBiomes.DUNE_SEA.key(), TFCBiomes.SALT_FLATS.key(),
            TFCBiomes.ROCKY_PLATEAU.key()
        );

        tag(Tags.Biomes.IS_HILL).add(
            TFCBiomes.HILLS.key(), TFCBiomes.ROLLING_HILLS.key(),
            TFCBiomes.HIGHLANDS.key(), TFCBiomes.CENOTE_HILLS.key(),
            TFCBiomes.CENOTE_ROLLING_HILLS.key(), TFCBiomes.CENOTE_HIGHLANDS.key(),
            TFCBiomes.DOLINE_HILLS.key(), TFCBiomes.DOLINE_ROLLING_HILLS.key(),
            TFCBiomes.DOLINE_HIGHLANDS.key(), TFCBiomes.SHILIN_HILLS.key(),
            TFCBiomes.SHILIN_HIGHLANDS.key(), TFCBiomes.SHILIN_CANYONS.key(),
            TFCBiomes.TOWER_KARST_HILLS.key(), TFCBiomes.TOWER_KARST_HIGHLANDS.key(),
            TFCBiomes.DRUMLINS.key(), TFCBiomes.KNOB_AND_KETTLE.key()
        );

        tag(Tags.Biomes.IS_HOT_OVERWORLD).add(
            TFCBiomes.OCEAN_REEF.key(), TFCBiomes.WHORLED_CANYONS.key(),
            TFCBiomes.MESAS.key(), TFCBiomes.BUTTES.key(),
            TFCBiomes.HOODOOS.key(), TFCBiomes.STAIR_STEP_CANYONS.key()
        );

        tag(Tags.Biomes.IS_ICY).add(
            TFCBiomes.ICE_SHEET_MOUNTAINS.key(), TFCBiomes.ICE_SHEET.key(),
            TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS.key(), TFCBiomes.ICE_SHEET_OCEANIC.key(),
            TFCBiomes.ICE_SHEET_EDGE.key(), TFCBiomes.ICE_SHEET_MOUNTAINS_EDGE.key(),
            TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS_EDGE.key(), TFCBiomes.ICE_SHEET_SHIELD_VOLCANO.key(),
            TFCBiomes.ICE_SHEET_SHORE.key(), TFCBiomes.ICE_SHEET_TUYAS.key(),
            TFCBiomes.ICE_SHEET_TUYAS_EDGE.key()
        );

        tag(Tags.Biomes.IS_PLAINS).add(
            TFCBiomes.PLAINS.key(), TFCBiomes.LOWLANDS.key(),
            TFCBiomes.BURREN_PLAINS.key(), TFCBiomes.DOLINE_PLAINS.key(),
            TFCBiomes.CENOTE_PLAINS.key(), TFCBiomes.TOWER_KARST_PLAINS.key(),
            TFCBiomes.SALT_FLATS.key(), TFCBiomes.MUD_FLATS.key(),
            TFCBiomes.TIDAL_FLATS.key(), TFCBiomes.PATTERNED_GROUND.key(),
            TFCBiomes.INVERTED_PATTERNED_GROUND.key(), TFCBiomes.SHILIN_PLAINS.key()
        );

        tag(Tags.Biomes.IS_PLATEAU).add(
            TFCBiomes.PLATEAU.key(), TFCBiomes.PLATEAU_WIDE.key(),
            TFCBiomes.PLATEAU_LAKE.key(), TFCBiomes.ROCKY_PLATEAU.key(),
            TFCBiomes.BURREN_PLATEAU.key(), TFCBiomes.SHILIN_PLATEAU.key(),
            TFCBiomes.DOLINE_PLATEAU.key(), TFCBiomes.CENOTE_PLATEAU.key(),
            TFCBiomes.EXTREME_DOLINE_PLATEAU.key()
        );

        tag(Tags.Biomes.IS_RARE)
            .addTag(TFCTags.Biomes.KARSTS)
            .add(
                TFCBiomes.GUANO_ISLAND.key(),
                TFCBiomes.VOLCANIC_OCEANIC_MOUNTAIN_LAKE.key(),
                TFCBiomes.OCEANIC_MOUNTAIN_LAKE.key(),
                TFCBiomes.WHORLED_CANYONS.key(),
                TFCBiomes.STAIR_STEP_CANYONS.key()
            );

        tag(Tags.Biomes.IS_SANDY).add(
            TFCBiomes.COASTAL_DUNES.key(), TFCBiomes.DUNE_SEA.key(),
            TFCBiomes.GRASSY_DUNES.key(), TFCBiomes.SHORE.key(),
            TFCBiomes.SALT_FLATS.key(), TFCBiomes.TIDAL_FLATS.key()
        );

        tag(Tags.Biomes.IS_SHALLOW_OCEAN).add(
            TFCBiomes.OCEAN_REEF.key(), TFCBiomes.OCEAN_ATOLLS.key(),
            TFCBiomes.OCEAN_RIDGE.key()
        );

        tag(Tags.Biomes.IS_SNOWY).add(
            TFCBiomes.ICE_SHEET.key(), TFCBiomes.ICE_SHEET_EDGE.key(),
            TFCBiomes.ICE_SHEET_MOUNTAINS.key(), TFCBiomes.ICE_SHEET_MOUNTAINS_EDGE.key(),
            TFCBiomes.ICE_SHEET_OCEANIC.key(), TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS.key(),
            TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS_EDGE.key(), TFCBiomes.ICE_SHEET_SHORE.key(),
            TFCBiomes.ICE_SHEET_TUYAS.key(), TFCBiomes.ICE_SHEET_TUYAS_EDGE.key(),
            TFCBiomes.ICE_SHEET_SHIELD_VOLCANO.key(), TFCBiomes.SUBGLACIAL_LAKE.key(),
            TFCBiomes.MELTWATER_LAKE.key(), TFCBiomes.GLACIATED_MOUNTAINS.key(),
            TFCBiomes.GLACIATED_OCEANIC_MOUNTAINS.key(), TFCBiomes.GLACIALLY_CARVED_MOUNTAINS.key(),
            TFCBiomes.GLACIALLY_CARVED_OCEANIC_MOUNTAINS.key(), TFCBiomes.GLACIATED_SHIELD_VOLCANO.key(),
            TFCBiomes.DRUMLINS.key(), TFCBiomes.TUYAS.key(),
            TFCBiomes.KNOB_AND_KETTLE.key(), TFCBiomes.PATTERNED_GROUND.key(),
            TFCBiomes.INVERTED_PATTERNED_GROUND.key(), TFCBiomes.STONE_CIRCLES.key(),
            TFCBiomes.GLACIATED_VOLCANIC_MOUNTAINS.key(), TFCBiomes.GLACIATED_VOLCANIC_OCEANIC_MOUNTAINS.key(),
            TFCBiomes.ICE_SHEET_VOLCANIC_MOUNTAINS.key(), TFCBiomes.ICE_SHEET_VOLCANIC_OCEANIC_MOUNTAINS.key()
        );

        tag(Tags.Biomes.IS_SNOWY_PLAINS)
            .add(TFCBiomes.ICE_SHEET.key())
            .add(TFCBiomes.ICE_SHEET_EDGE.key())
            .add(TFCBiomes.PATTERNED_GROUND.key())
            .add(TFCBiomes.INVERTED_PATTERNED_GROUND.key())
            .add(TFCBiomes.STONE_CIRCLES.key());

        tag(Tags.Biomes.IS_STONY_SHORES)
            .add(TFCBiomes.ROCKY_SHORES.key());

        tag(Tags.Biomes.IS_SWAMP).add(
            TFCBiomes.SALT_MARSH.key(), TFCBiomes.LOWLANDS.key(),
            TFCBiomes.TIDAL_FLATS.key()
        );

        //tag(Tags.Biomes.IS_TEMPERATE_OVERWORLD) What should be here??

        tag(Tags.Biomes.IS_WET_OVERWORLD).add(
                TFCBiomes.SALT_MARSH.key(), TFCBiomes.LOWLANDS.key(),
                TFCBiomes.TIDAL_FLATS.key())
            .addTag(TFCTags.Biomes.KARSTS);

        tag(Tags.Biomes.IS_WINDSWEPT).add(
            TFCBiomes.SEA_STACKS.key(), TFCBiomes.SETBACK_CLIFFS.key()
        );

        tag(Tags.Biomes.IS_SPARSE_VEGETATION_OVERWORLD).add(
            TFCBiomes.DUNE_SEA.key(), TFCBiomes.SALT_FLATS.key(),
            TFCBiomes.BADLANDS.key(), TFCBiomes.BUTTES.key(),
            TFCBiomes.HOODOOS.key(), TFCBiomes.STAIR_STEP_CANYONS.key(),
            TFCBiomes.WHORLED_CANYONS.key(), TFCBiomes.MESAS.key(),
            TFCBiomes.ROCKY_PLATEAU.key(), TFCBiomes.CANYONS.key(),
            TFCBiomes.BURREN_BADLANDS.key(), TFCBiomes.BURREN_BADLANDS_TALL.key(),
            TFCBiomes.ICE_SHEET_SHORE.key(), TFCBiomes.ICE_SHEET_MOUNTAINS_EDGE.key(),
            TFCBiomes.ICE_SHEET_MOUNTAINS.key(), TFCBiomes.ICE_SHEET_EDGE.key(),
            TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS_EDGE.key(), TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS.key(),
            TFCBiomes.ICE_SHEET_OCEANIC.key(), TFCBiomes.ICE_SHEET_TUYAS.key(),
            TFCBiomes.ICE_SHEET_TUYAS_EDGE.key(), TFCBiomes.ICE_SHEET_SHIELD_VOLCANO.key(),
            TFCBiomes.ICE_SHEET.key(), TFCBiomes.ICE_SHEET_MOUNTAINS_EDGE.key(),
            TFCBiomes.ICE_SHEET_MOUNTAINS.key(), TFCBiomes.ICE_SHEET_EDGE.key(),
            TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS_EDGE.key(), TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS.key(),
            TFCBiomes.ICE_SHEET_VOLCANIC_MOUNTAINS.key(), TFCBiomes.ICE_SHEET_VOLCANIC_OCEANIC_MOUNTAINS.key(),
            TFCBiomes.ACTIVE_SHIELD_VOLCANO.key(), TFCBiomes.ANCIENT_SHIELD_VOLCANO.key(),
            TFCBiomes.EXTINCT_SHIELD_VOLCANO.key(), TFCBiomes.DORMANT_SHIELD_VOLCANO.key(),
            TFCBiomes.GLACIATED_MOUNTAINS.key(), TFCBiomes.GLACIATED_OCEANIC_MOUNTAINS.key(),
            TFCBiomes.GLACIALLY_CARVED_MOUNTAINS.key(), TFCBiomes.GLACIALLY_CARVED_OCEANIC_MOUNTAINS.key(),
            TFCBiomes.GLACIALLY_CARVED_VOLCANIC_MOUNTAINS.key(), TFCBiomes.GLACIALLY_CARVED_VOLCANIC_OCEANIC_MOUNTAINS.key(),
            TFCBiomes.GLACIATED_SHIELD_VOLCANO.key(), TFCBiomes.GLACIATED_VOLCANIC_MOUNTAINS.key(),
            TFCBiomes.GLACIATED_VOLCANIC_OCEANIC_MOUNTAINS.key(), TFCBiomes.DRUMLINS.key(),
            TFCBiomes.TUYAS.key(), TFCBiomes.KNOB_AND_KETTLE.key(),
            TFCBiomes.PATTERNED_GROUND.key(), TFCBiomes.INVERTED_PATTERNED_GROUND.key(),
            TFCBiomes.STONE_CIRCLES.key()
        );

        //=====TFC Tags=====//

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

        tag(TFCTags.Biomes.HAS_ATOLLS).add(
            TFCBiomes.OCEAN_ATOLLS.key(), TFCBiomes.DEEP_OCEAN_ATOLLS.key()
        );

        tag(TFCTags.Biomes.HAS_CINDER_CONES).add(
            TFCBiomes.CANYONS.key(), TFCBiomes.RIFT_VALLEY.key(),
            TFCBiomes.VOLCANIC_MOUNTAIN_ISLANDS.key(), TFCBiomes.DOLINE_CANYONS.key(),
            TFCBiomes.ACTIVE_SHIELD_VOLCANO.key()
        );

        tag(TFCTags.Biomes.HAS_PREDICTABLE_WINDS)
            .addTag(TFCTags.Biomes.IS_OCEAN);

        tag(TFCTags.Biomes.HAS_STRATOVOLCANOES).add(
            TFCBiomes.VOLCANIC_MOUNTAINS.key(), TFCBiomes.VOLCANIC_OCEANIC_MOUNTAINS.key(),
            TFCBiomes.OCEANIC_VOLCANIC_ARC.key(), TFCBiomes.VOLCANIC_ISLAND.key(),
            TFCBiomes.VOLCANIC_MOUNTAIN_LAKE.key(), TFCBiomes.VOLCANIC_OCEANIC_MOUNTAIN_LAKE.key(),
            TFCBiomes.ICE_SHEET_VOLCANIC_MOUNTAINS.key(), TFCBiomes.ICE_SHEET_VOLCANIC_OCEANIC_MOUNTAINS.key(),
            TFCBiomes.GLACIATED_VOLCANIC_MOUNTAINS.key(), TFCBiomes.GLACIATED_VOLCANIC_OCEANIC_MOUNTAINS.key(),
            TFCBiomes.GLACIALLY_CARVED_VOLCANIC_MOUNTAINS.key(), TFCBiomes.GLACIALLY_CARVED_VOLCANIC_OCEANIC_MOUNTAINS.key()
        );

        tag(TFCTags.Biomes.HAS_TUFF_CONES).add(
            TFCBiomes.DORMANT_SHIELD_VOLCANO.key(), TFCBiomes.EXTINCT_SHIELD_VOLCANO.key(),
            TFCBiomes.ANCIENT_SHIELD_VOLCANO.key(), TFCBiomes.SUNKEN_SHIELD_VOLCANO.key(),
            TFCBiomes.SHIELD_VOLCANO_SHORE.key(), TFCBiomes.OLD_SHIELD_VOLCANO_SHORE.key(),
            TFCBiomes.GLACIATED_SHIELD_VOLCANO.key(), TFCBiomes.ICE_SHEET_SHIELD_VOLCANO.key()
        );

        tag(TFCTags.Biomes.HAS_TUYAS).add(
            TFCBiomes.TUYAS.key(), TFCBiomes.ICE_SHEET_TUYAS.key(), TFCBiomes.ICE_SHEET_TUYAS_EDGE.key()
        );

        tag(TFCTags.Biomes.IS_BURREN).add(
            TFCBiomes.BURREN_PLATEAU.key(), TFCBiomes.BURREN_BADLANDS.key(), TFCBiomes.BURREN_BADLANDS_TALL.key(),
            TFCBiomes.BURREN_PLAINS.key(), TFCBiomes.BURREN_ROCHE_MOUTONEE.key()
        );

        tag(TFCTags.Biomes.IS_DOLINES).add(
            TFCBiomes.DOLINE_PLAINS.key(), TFCBiomes.DOLINE_HILLS.key(),
            TFCBiomes.DOLINE_ROLLING_HILLS.key(), TFCBiomes.DOLINE_HIGHLANDS.key(),
            TFCBiomes.DOLINE_PLATEAU.key(), TFCBiomes.DOLINE_CANYONS.key()
        );

        tag(TFCTags.Biomes.IS_EXTREME_DOLINES).add(
            TFCBiomes.EXTREME_DOLINE_PLATEAU.key(), TFCBiomes.EXTREME_DOLINE_MOUNTAINS.key()
        );

        tag(TFCTags.Biomes.IS_GLACIATED).add(
            TFCBiomes.GLACIATED_MOUNTAINS.key(), TFCBiomes.GLACIATED_OCEANIC_MOUNTAINS.key(),
            TFCBiomes.GLACIATED_SHIELD_VOLCANO.key(), TFCBiomes.GLACIATED_VOLCANIC_MOUNTAINS.key(),
            TFCBiomes.GLACIATED_VOLCANIC_OCEANIC_MOUNTAINS.key()
        );

        tag(TFCTags.Biomes.IS_ICE_SHEET).add(
            TFCBiomes.ICE_SHEET.key(), TFCBiomes.ICE_SHEET_EDGE.key(),
            TFCBiomes.ICE_SHEET_MOUNTAINS.key(), TFCBiomes.ICE_SHEET_MOUNTAINS_EDGE.key(),
            TFCBiomes.ICE_SHEET_OCEANIC.key(), TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS.key(),
            TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS_EDGE.key(), TFCBiomes.ICE_SHEET_SHORE.key(),
            TFCBiomes.ICE_SHEET_TUYAS.key(), TFCBiomes.ICE_SHEET_TUYAS_EDGE.key(),
            TFCBiomes.ICE_SHEET_SHIELD_VOLCANO.key(), TFCBiomes.ICE_SHEET_VOLCANIC_MOUNTAINS.key(),
            TFCBiomes.ICE_SHEET_VOLCANIC_OCEANIC_MOUNTAINS.key(), TFCBiomes.SUBGLACIAL_LAKE.key()
        );

        tag(TFCTags.Biomes.IS_KARST).add(
            TFCBiomes.BURREN_PLATEAU.key(), TFCBiomes.BURREN_BADLANDS.key(),
            TFCBiomes.BURREN_PLAINS.key(), TFCBiomes.BURREN_ROCHE_MOUTONEE.key(),
            TFCBiomes.DOLINE_PLAINS.key(), TFCBiomes.DOLINE_HILLS.key(),
            TFCBiomes.DOLINE_ROLLING_HILLS.key(), TFCBiomes.DOLINE_HIGHLANDS.key(),
            TFCBiomes.DOLINE_PLATEAU.key(), TFCBiomes.DOLINE_CANYONS.key(),
            TFCBiomes.SHILIN_PLAINS.key(), TFCBiomes.SHILIN_CANYONS.key(),
            TFCBiomes.SHILIN_HILLS.key(), TFCBiomes.SHILIN_HIGHLANDS.key(),
            TFCBiomes.SHILIN_PLATEAU.key(), TFCBiomes.TOWER_KARST_PLAINS.key(),
            TFCBiomes.TOWER_KARST_CANYONS.key(), TFCBiomes.TOWER_KARST_HILLS.key(),
            TFCBiomes.TOWER_KARST_HIGHLANDS.key(), TFCBiomes.EXTREME_DOLINE_PLATEAU.key(),
            TFCBiomes.EXTREME_DOLINE_MOUNTAINS.key(), TFCBiomes.TOWER_KARST_LAKE.key(),
            TFCBiomes.TOWER_KARST_BAY.key(), TFCBiomes.BURREN_BADLANDS_TALL.key()
        );

        tag(TFCTags.Biomes.IS_LAKE).add(
            TFCBiomes.LAKE.key(), TFCBiomes.MOUNTAIN_LAKE.key(),
            TFCBiomes.OLD_MOUNTAIN_LAKE.key(), TFCBiomes.OCEANIC_MOUNTAIN_LAKE.key(),
            TFCBiomes.VOLCANIC_MOUNTAIN_LAKE.key(), TFCBiomes.VOLCANIC_OCEANIC_MOUNTAIN_LAKE.key(),
            TFCBiomes.PLATEAU_LAKE.key(), TFCBiomes.SUBGLACIAL_LAKE.key(),
            TFCBiomes.MELTWATER_LAKE.key(), TFCBiomes.TOWER_KARST_LAKE.key(),
            TFCBiomes.RIFT_LAKE.key()
        );

        tag(TFCTags.Biomes.IS_OCEAN).add(
            TFCBiomes.OCEAN.key(), TFCBiomes.OCEAN_REEF.key(),
            TFCBiomes.DEEP_OCEAN.key(), TFCBiomes.DEEP_OCEAN_TRENCH.key(),
            TFCBiomes.OCEAN_RIDGE.key(), TFCBiomes.OCEAN_ATOLLS.key(),
            TFCBiomes.DEEP_OCEAN_ATOLLS.key(), TFCBiomes.OCEANIC_VOLCANIC_ARC.key(),
            TFCBiomes.SHORE.key(), TFCBiomes.TIDAL_FLATS.key(),
            TFCBiomes.SEA_STACKS.key(), TFCBiomes.TERRACE_UPPER.key(),
            TFCBiomes.TERRACE_LOWER.key(), TFCBiomes.SETBACK_CLIFFS.key(),
            TFCBiomes.COASTAL_DUNES.key(), TFCBiomes.ROCKY_SHORES.key(),
            TFCBiomes.EMBAYMENTS.key(), TFCBiomes.SHIELD_VOLCANO_SHORE.key(),
            TFCBiomes.OLD_SHIELD_VOLCANO_SHORE.key(), TFCBiomes.ICE_SHEET_OCEANIC.key(),
            TFCBiomes.ICE_SHEET_SHORE.key()
        );

        tag(TFCTags.Biomes.IS_RIFT).add(
            TFCBiomes.RIFT_VALLEY.key(), TFCBiomes.RIFT_LAKE.key()
        );

        tag(TFCTags.Biomes.IS_RIVER).add(
            TFCBiomes.RIVER.key(), TFCBiomes.RIVER_VALLEY.key()
        );

        tag(TFCTags.Biomes.IS_SHIELD_VOLCANO).add(
            TFCBiomes.ACTIVE_SHIELD_VOLCANO.key(), TFCBiomes.ANCIENT_SHIELD_VOLCANO.key(),
            TFCBiomes.DORMANT_SHIELD_VOLCANO.key(), TFCBiomes.EXTINCT_SHIELD_VOLCANO.key(),
            TFCBiomes.SUNKEN_SHIELD_VOLCANO.key(), TFCBiomes.SHIELD_VOLCANO_SHORE.key(),
            TFCBiomes.OLD_SHIELD_VOLCANO_SHORE.key(), TFCBiomes.GLACIATED_SHIELD_VOLCANO.key(),
            TFCBiomes.ICE_SHEET_SHIELD_VOLCANO.key()
        );

        tag(TFCTags.Biomes.IS_SHILIN).add(
            TFCBiomes.SHILIN_PLAINS.key(), TFCBiomes.SHILIN_CANYONS.key(),
            TFCBiomes.SHILIN_HILLS.key(), TFCBiomes.SHILIN_HIGHLANDS.key(),
            TFCBiomes.SHILIN_PLATEAU.key()
        );

        tag(TFCTags.Biomes.IS_TOWER_KARST).add(
            TFCBiomes.TOWER_KARST_PLAINS.key(), TFCBiomes.TOWER_KARST_CANYONS.key(),
            TFCBiomes.TOWER_KARST_HILLS.key(), TFCBiomes.TOWER_KARST_HIGHLANDS.key(),
            TFCBiomes.TOWER_KARST_LAKE.key(), TFCBiomes.TOWER_KARST_BAY.key()
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
        );
    }
}
