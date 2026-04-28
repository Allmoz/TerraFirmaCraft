/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.blocks.DecorationBlockHolder;
import net.dries007.tfc.common.blocks.OreDeposit;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.crop.Crop;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.common.blocks.plant.coral.Coral;
import net.dries007.tfc.common.blocks.plant.fruit.FruitBlocks;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.rock.RockCategory;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.HideItemType;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Metal;


@SuppressWarnings("unused")
public final class TFCCreativeTabs
{
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TerraFirmaCraft.MOD_ID);

    public static final Id TFC_BUILDING_BLOCKS = register("tfc_0building_blocks",
        () -> new ItemStack(TFCBlocks.ROCK_BLOCKS.get(Rock.SHALE).get(Rock.BlockType.CHISELED)), TFCCreativeTabs::fillBuildingBlocksTab);
    public static final Id TFC_COLORED_BLOCKS = register("tfc_1colored_blocks",
        () -> new ItemStack(TFCBlocks.STAINED_WATTLE.get(DyeColor.CYAN)), TFCCreativeTabs::fillColoredBlocksTab);
    public static final Id TFC_NATURAL_BLOCKS = register("tfc_2natural_blocks",
        () -> new ItemStack(TFCBlocks.SOIL.get(SoilBlockType.GRASS).get(SoilBlockType.Variant.ANDISOL)), TFCCreativeTabs::fillSoilsStonesTab);
    public static final Id TFC_FLORA_CROPS = register("tfc_3flora_crops",
        () -> new ItemStack(TFCBlocks.PLANTS.get(Plant.ATHYRIUM_FERN)), TFCCreativeTabs::fillFloraCropsTab);
    public static final Id TFC_FUNCTIONAL_BLOCKS = register("tfc_4functional_blocks",
        () -> new ItemStack(TFCItems.HANGING_SIGNS.get(Wood.SPRUCE).get(Metal.BISMUTH_BRONZE)), TFCCreativeTabs::fillFunctionalBlocksTab);
    public static final Id TFC_TOOLS_UTILITIES = register("tfc_5tools_utilities",
        () -> new ItemStack(TFCItems.METAL_ITEMS.get(Metal.STEEL).get(Metal.ItemType.SAW)), TFCCreativeTabs::fillToolsUtilitiesTab);
    public static final Id TFC_COMBAT = register("tfc_6combat",
        () -> new ItemStack(TFCItems.METAL_ITEMS.get(Metal.RED_STEEL).get(Metal.ItemType.JAVELIN)), TFCCreativeTabs::fillCombatTab);
    public static final Id TFC_FOODS_DRINKS = register("tfc_7foods_drinks",
        () -> new ItemStack(TFCItems.FOOD.get(Food.GREEN_APPLE)), TFCCreativeTabs::fillFoodsDrinksTab);
    public static final Id TFC_METALS_INGREDIENTS = register("tfc_8metals_ingredients",
        () -> new ItemStack(TFCItems.METAL_ITEMS.get(Metal.BLACK_BRONZE).get(Metal.ItemType.INGOT)), TFCCreativeTabs::fillMetalsIngredientsTab);
    public static final Id TFC_SPAWN_EGGS = register("tfc_9spawn_eggs",
        () -> new ItemStack(Objects.requireNonNull(SpawnEggItem.byId(TFCEntities.CARIBOU.get()))), TFCCreativeTabs::fillSpawnEggsTab);


    public static Stream<CreativeModeTab.DisplayItemsGenerator> generators()
    {
        return Stream.of(TFC_BUILDING_BLOCKS, TFC_COLORED_BLOCKS, TFC_NATURAL_BLOCKS, TFC_FLORA_CROPS, TFC_FUNCTIONAL_BLOCKS, TFC_TOOLS_UTILITIES, TFC_COMBAT, TFC_FOODS_DRINKS, TFC_METALS_INGREDIENTS, TFC_SPAWN_EGGS).map(holder -> holder.generator);
    }

    public static void setAllTabContentAsNonDecaying(BuildCreativeModeTabContentsEvent event)
    {
        // todo 1.21, verify that this works properly (event priority first, then mod order). Needs an addon lol
        // Otherwise, re-add the mixin from 1.20
        FoodCapability.setTransientNonDecaying(event.getTab().getIconItem());
        event.getParentEntries().forEach(FoodCapability::setTransientNonDecaying);
        event.getSearchEntries().forEach(FoodCapability::setTransientNonDecaying);
    }

    private static void fillBuildingBlocksTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output out)
    {
        for (Wood wood : Wood.VALUES)
        {
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.LOG));
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.WOOD));
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.STRIPPED_LOG));
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.STRIPPED_WOOD));
            accept(out, TFCItems.SUPPORTS, wood);
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.PLANKS));
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.STAIRS));
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.SLAB));
            if (wood == Wood.PALM)
            {
                out.accept(TFCBlocks.PALM_MOSAIC);
                out.accept(TFCBlocks.PALM_MOSAIC_STAIRS);
                out.accept(TFCBlocks.PALM_MOSAIC_SLAB);
            }
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.LOG_FENCE));
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.FENCE));
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.FENCE_GATE));
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.DOOR));
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.TRAPDOOR));
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.PRESSURE_PLATE));
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.BUTTON));
        }
        out.accept(TFCBlocks.GOLDEN_BAMBOO_BLOCK);

        for (Rock rock : Rock.VALUES)
        {
            for (Rock.BlockType type : new Rock.BlockType[] {
                Rock.BlockType.RAW,
                Rock.BlockType.COBBLE,
                Rock.BlockType.MOSSY_COBBLE,
                Rock.BlockType.BRICKS,
                Rock.BlockType.CRACKED_BRICKS,
                Rock.BlockType.MOSSY_BRICKS,
                Rock.BlockType.SMOOTH,
                Rock.BlockType.CHISELED,
                Rock.BlockType.AQUEDUCT,
                Rock.BlockType.PRESSURE_PLATE,
                Rock.BlockType.BUTTON
            })
            {
                accept(out, TFCBlocks.ROCK_BLOCKS, rock, type);
                if (type.hasVariants())
                {
                    accept(out, TFCBlocks.ROCK_DECORATIONS.get(rock).get(type));
                }
            }
        }

        out.accept(Blocks.BRICKS);
        out.accept(Blocks.BRICK_STAIRS);
        out.accept(Blocks.BRICK_SLAB);
        out.accept(Blocks.BRICK_WALL);

        out.accept(TFCBlocks.WATTLE);
        out.accept(TFCBlocks.UNSTAINED_WATTLE);
        out.accept(TFCBlocks.THATCH);

        out.accept(TFCBlocks.PLAIN_ALABASTER);
        out.accept(TFCBlocks.PLAIN_ALABASTER_BRICKS);
        out.accept(TFCBlocks.PLAIN_POLISHED_ALABASTER);

        for (SoilBlockType.Variant variant : SoilBlockType.Variant.values())
        {
            accept(out, TFCBlocks.SOIL, SoilBlockType.MUD_BRICKS, variant);
            accept(out, TFCBlocks.SOIL, SoilBlockType.MUD_BRICKS, variant);
            accept(out, TFCBlocks.MUD_BRICK_DECORATIONS.get(variant));
        }
        out.accept(TFCBlocks.SMOOTH_MUD_BRICKS);

        for (SandBlockType type : SandBlockType.values())
        {
            TFCBlocks.SANDSTONE.get(type).values().forEach(out::accept);
            TFCBlocks.SANDSTONE_DECORATIONS.get(type).values().forEach(reg -> accept(out, reg));
        }

        for (Metal metal : Metal.values())
        {
            for (Metal.BlockType type : new Metal.BlockType[] {
                Metal.BlockType.BLOCK,
                Metal.BlockType.EXPOSED_BLOCK,
                Metal.BlockType.WEATHERED_BLOCK,
                Metal.BlockType.OXIDIZED_BLOCK,
                Metal.BlockType.BLOCK_SLAB,
                Metal.BlockType.EXPOSED_BLOCK_SLAB,
                Metal.BlockType.WEATHERED_BLOCK_SLAB,
                Metal.BlockType.OXIDIZED_BLOCK_SLAB,
                Metal.BlockType.BLOCK_STAIRS,
                Metal.BlockType.EXPOSED_BLOCK_STAIRS,
                Metal.BlockType.WEATHERED_BLOCK_STAIRS,
                Metal.BlockType.OXIDIZED_BLOCK_STAIRS,
                Metal.BlockType.GRATE,
                Metal.BlockType.EXPOSED_GRATE,
                Metal.BlockType.WEATHERED_GRATE,
                Metal.BlockType.OXIDIZED_GRATE,
                Metal.BlockType.BARS,
                Metal.BlockType.CHAIN,
                Metal.BlockType.TRAPDOOR
            })
            {
                accept(out, TFCBlocks.METALS, metal, type);
            }
        }
    }

    private static void fillColoredBlocksTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output out)
    {
        out.accept(TFCBlocks.WATTLE);
        out.accept(TFCBlocks.UNSTAINED_WATTLE);
        TFCBlocks.STAINED_WATTLE.values().forEach(out::accept);

        out.accept(TFCBlocks.PLAIN_ALABASTER);
        out.accept(TFCBlocks.PLAIN_ALABASTER_BRICKS);
        out.accept(TFCBlocks.PLAIN_POLISHED_ALABASTER);

        for (DyeColor color : DyeColor.values())
        {
            accept(out, TFCBlocks.RAW_ALABASTER, color);
            accept(out, TFCBlocks.ALABASTER_BRICKS, color);
            accept(out, TFCBlocks.ALABASTER_BRICK_DECORATIONS.get(color));
            accept(out, TFCBlocks.POLISHED_ALABASTER, color);
            accept(out, TFCBlocks.ALABASTER_POLISHED_DECORATIONS.get(color));
        }

        out.accept(TFCItems.VESSEL);
        TFCItems.GLAZED_VESSELS.values().forEach(out::accept);

        out.accept(TFCBlocks.LARGE_VESSEL);
        TFCBlocks.GLAZED_LARGE_VESSELS.values().forEach(out::accept);

        out.accept(TFCBlocks.CANDLE);
        for (DyeColor color : DyeColor.values())
        {
            accept(out, TFCBlocks.DYED_CANDLE, color);
        }

        TFCItems.WINDMILL_BLADES.values().forEach(out::accept);

        for (DyeColor color : DyeColor.values())
        {
            TFCFluids.FLUIDS.getEntries().forEach(fluid -> {
                if (fluid.getId().toString().endsWith("_dye"))
                {
                    out.accept(fluid.value().getBucket());
                }
            });
        }
    }

    private static void fillSoilsStonesTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output out)
    {
        for (SoilBlockType.Variant variant : SoilBlockType.Variant.values())
        {
            for (SoilBlockType type : SoilBlockType.VALUES)
            {
                if (type != SoilBlockType.MUD_BRICKS && type != SoilBlockType.DRYING_BRICKS)
                {
                    accept(out, TFCBlocks.SOIL, type, variant);
                }
            }
        }

        out.accept(TFCBlocks.PEAT);
        out.accept(TFCBlocks.PEAT_GRASS);

        out.accept(TFCBlocks.WHITE_KAOLIN_CLAY);
        out.accept(TFCBlocks.PINK_KAOLIN_CLAY);
        out.accept(TFCBlocks.RED_KAOLIN_CLAY);
        out.accept(TFCBlocks.KAOLIN_CLAY_GRASS);
        out.accept(TFCBlocks.HARDENED_CLAY);

        for (SandBlockType type : SandBlockType.values())
        {
            accept(out, TFCBlocks.SAND, type);
        }

        for (Rock rock : Rock.VALUES)
        {
            for (Rock.BlockType type : new Rock.BlockType[] {
                Rock.BlockType.HARDENED,
                Rock.BlockType.SPIKE,
                Rock.BlockType.RAW,
                Rock.BlockType.COBBLE,
                Rock.BlockType.MOSSY_COBBLE,
                Rock.BlockType.GRAVEL,
                Rock.BlockType.LOOSE,
                Rock.BlockType.MOSSY_LOOSE,
            })
            {
                accept(out, TFCBlocks.ROCK_BLOCKS, rock, type);
            }
        }

        for (Ore ore : Ore.values())
        {
            if (ore.isGraded())
            {
                accept(out, TFCBlocks.SMALL_ORES, ore);
            }
        }
        for (Ore ore : Ore.values())
        {
            if (!ore.isGraded()) accept(out, TFCItems.ORES, ore);
        }
        for (OreDeposit deposit : OreDeposit.values())
        {
            TFCBlocks.ORE_DEPOSITS.values().forEach(map -> accept(out, map, deposit));
        }
        for (Ore ore : Ore.values())
        {
            if (ore.isGraded())
            {
                TFCBlocks.GRADED_ORES.values().forEach(map -> map.get(ore).values().forEach(out::accept));
            }
            else
            {
                TFCBlocks.ORES.values().forEach(map -> accept(out, map, ore));
            }
        }

        TFCBlocks.MAGMA_BLOCKS.values().forEach(out::accept);

        out.accept(TFCBlocks.CALCITE);
        out.accept(TFCBlocks.ICICLE);

        out.accept(Blocks.ICE);
        out.accept(TFCBlocks.SEA_ICE);
        out.accept(Blocks.PACKED_ICE);
        out.accept(Blocks.BLUE_ICE);

        out.accept(Items.WATER_BUCKET);

        TFCFluids.FLUIDS.getEntries().forEach(fluid -> {
            if (fluid.getId().toString().contains("salt_water"))
            {
                out.accept(fluid.value().getBucket());
            }
            else if (fluid.getId().toString().contains("spring_water"))
            {
                out.accept(fluid.value().getBucket());
            }
        });

        out.accept(Items.LAVA_BUCKET);
    }

    private static void fillFloraCropsTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output out)
    {
        TFCBlocks.PLANTS.forEach((plant, reg) -> {
            if (plant.needsItem())
            {
                out.accept(reg);
            }
        });

        for (Wood wood : Wood.VALUES)
        {
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.SAPLING));
            switch (wood)
            {
                case PINE -> out.accept(TFCBlocks.PINE_KRUMMHOLZ);
                case SPRUCE -> out.accept(TFCBlocks.SPRUCE_KRUMMHOLZ);
                case WHITE_CEDAR -> out.accept(TFCBlocks.WHITE_CEDAR_KRUMMHOLZ);
                case DOUGLAS_FIR -> out.accept(TFCBlocks.DOUGLAS_FIR_KRUMMHOLZ);
                case ASPEN -> out.accept(TFCBlocks.ASPEN_KRUMMHOLZ);
            }
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.LEAVES));
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.FALLEN_LEAVES));
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.TWIG));
        }

        out.accept(Blocks.BAMBOO);
        out.accept(TFCBlocks.SEA_PICKLE);

        for (Crop crop : Crop.values())
        {
            accept(out, TFCBlocks.WILD_CROPS, crop);
            if (crop == Crop.PUMPKIN)
                out.accept(TFCBlocks.PUMPKIN);
            else if (crop == Crop.MELON)
                out.accept(TFCBlocks.MELON);
            accept(out, TFCItems.CROP_SEEDS, crop);
        }

        TFCBlocks.SPREADING_BUSHES.values().forEach(out::accept);
        TFCBlocks.STATIONARY_BUSHES.values().forEach(out::accept);
        out.accept(TFCBlocks.CRANBERRY_BUSH);

        for (FruitBlocks.Tree tree : FruitBlocks.Tree.values())
        {
            accept(out, TFCBlocks.FRUIT_TREE_SAPLINGS, tree);
            accept(out, TFCBlocks.FRUIT_TREE_LEAVES, tree);
        }

        out.accept(TFCBlocks.BANANA_SAPLING);

        TFCBlocks.GROUNDCOVER.forEach((type, reg) -> {
            if (type.getVanillaItem() == null)
            {
                out.accept(reg);
            }
            else
            {
                out.accept(type.getVanillaItem());
            }
        });

        for (Coral coral : Coral.values())
        {
            TFCBlocks.CORAL.get(coral).values().forEach(out::accept);
            accept(out, TFCItems.CORAL_FANS, coral);
            accept(out, TFCItems.DEAD_CORAL_FANS, coral);
        }

        out.accept(TFCBlocks.TREE_ROOTS);
    }

    private static void fillFunctionalBlocksTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output out)
    {
        out.accept(TFCItems.TORCH);
        out.accept(TFCItems.DEAD_TORCH);

        for (Metal metal : Metal.values())
        {
            for (Metal.BlockType type : new Metal.BlockType[] {
                Metal.BlockType.LAMP,
            })
            {
                accept(out, TFCBlocks.METALS, metal, type);
            }
        }

        for (Metal metal : Metal.values())
        {
            for (Metal.BlockType type : new Metal.BlockType[] {
                Metal.BlockType.ANVIL
            })
            {
                accept(out, TFCBlocks.METALS, metal, type);
            }
        }

        out.accept(TFCBlocks.THATCH_BED);
        out.accept(TFCBlocks.FIREPIT);
        out.accept(TFCItems.WROUGHT_IRON_GRILL);
        out.accept(TFCBlocks.GRILL);
        out.accept(TFCItems.POT);
        out.accept(TFCBlocks.POT);
        out.accept(TFCBlocks.STOVE);
        out.accept(TFCBlocks.STOVE_POT);
        out.accept(TFCBlocks.BELLOWS);
        out.accept(TFCBlocks.POWDERKEG);
        out.accept(TFCBlocks.BARREL_RACK);
        out.accept(TFCBlocks.CERAMIC_BOWL);
        out.accept(Items.BOWL);
        out.accept(TFCBlocks.QUERN);
        out.accept(TFCItems.HANDSTONE);

        out.accept(TFCBlocks.BRONZE_BELL);
        out.accept(TFCBlocks.BRASS_BELL);
        out.accept(Blocks.BELL);
        out.accept(TFCBlocks.METALS.get(Metal.BRASS).get(Metal.BlockType.BLOCK));

        out.accept(TFCBlocks.CREATIVE_MOTOR);
        TFCItems.WINDMILL_BLADES.values().forEach(out::accept);
        out.accept(TFCItems.RUSTIC_WINDMILL_BLADE);
        out.accept(TFCItems.LATTICE_WINDMILL_BLADE);
        out.accept(TFCBlocks.CRANKSHAFT);
        out.accept(TFCBlocks.TRIP_HAMMER);
        out.accept(TFCBlocks.POWER_LOOM);
        out.accept(TFCBlocks.STEEL_PIPE);
        out.accept(TFCBlocks.STEEL_PUMP);

        out.accept(TFCBlocks.VANE);
        out.accept(TFCBlocks.ANEMOMETER);
        out.accept(TFCBlocks.CALENDAR_CLOCK);
        out.accept(TFCBlocks.THERMOMETER);

        out.accept(TFCBlocks.CRUCIBLE);
        out.accept(TFCBlocks.CHANNEL);
        out.accept(TFCBlocks.MOLD_TABLE);

        out.accept(TFCBlocks.BLOOMERY);
        out.accept(TFCBlocks.BLAST_FURNACE);

        out.accept(TFCBlocks.COMPOSTER);
        out.accept(TFCBlocks.NEST_BOX);
        out.accept(Blocks.CARVED_PUMPKIN);
        out.accept(TFCBlocks.JACK_O_LANTERN);

        out.accept(TFCBlocks.FIRE_BRICKS);
        out.accept(TFCBlocks.REINFORCED_FIRE_BRICKS);
        out.accept(TFCBlocks.FIRE_BRICK_SHELF);
        out.accept(TFCBlocks.FIREPROOF_DOOR);
        out.accept(TFCBlocks.FIREBOX);
        out.accept(TFCBlocks.FIRE_CLAY_BLOCK);

        out.accept(TFCBlocks.AGGREGATE);

        for (Wood wood : Wood.VALUES)
        {
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.WORKBENCH));
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.CHEST));
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.TRAPPED_CHEST));
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.BARREL));
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.SCRIBING_TABLE));
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.SEWING_TABLE));
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.LOOM));
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.SLUICE));
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.LECTERN));
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.BOOKSHELF));
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.SHELF));
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.TOOL_RACK));
            accept(out, TFCItems.SUPPORTS, wood);
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.SIGN));
            for (Metal metal : Metal.values())
            {
                accept(out, TFCItems.HANGING_SIGNS.get(wood), metal);
            }
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.AXLE));
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.BLADED_AXLE));
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.ENCASED_AXLE));
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.GEAR_BOX));
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.CLUTCH));
            out.accept(TFCBlocks.WOODS.get(wood).get(Wood.BlockType.WATER_WHEEL));
        }

        out.accept(TFCBlocks.LARGE_VESSEL);
        TFCBlocks.GLAZED_LARGE_VESSELS.values().forEach(out::accept);

        out.accept(TFCBlocks.CANDLE);
        for (DyeColor color : DyeColor.values())
        {
            accept(out, TFCBlocks.DYED_CANDLE, color);
        }
    }

    private static void fillToolsUtilitiesTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output out)
    {
        out.accept(TFCItems.FIRESTARTER);
        out.accept(TFCItems.FLINT_AND_PYRITE);
        out.accept(Items.FLINT_AND_STEEL);

        for (RockCategory.ItemType type : RockCategory.ItemType.values())
        {
            for (RockCategory category : RockCategory.values())
            {
                accept(out, TFCItems.ROCK_TOOLS, category, RockCategory.ItemType.AXE);
                accept(out, TFCItems.ROCK_TOOLS, category, RockCategory.ItemType.HAMMER);
                accept(out, TFCItems.ROCK_TOOLS, category, RockCategory.ItemType.HOE);
                accept(out, TFCItems.ROCK_TOOLS, category, RockCategory.ItemType.KNIFE);
                accept(out, TFCItems.ROCK_TOOLS, category, RockCategory.ItemType.SHOVEL);
            }
        }

        for (Metal metal : Metal.values())
        {
            if (metal == Metal.RED_STEEL)
                out.accept(TFCItems.RED_STEEL_BUCKET);
            else if (metal == Metal.BLUE_STEEL)
                out.accept(TFCItems.BLUE_STEEL_BUCKET);

            for (Metal.ItemType itemType : new Metal.ItemType[] {
                Metal.ItemType.TUYERE,
                Metal.ItemType.PICKAXE,
                Metal.ItemType.PROPICK,
                Metal.ItemType.AXE,
                Metal.ItemType.SHOVEL,
                Metal.ItemType.HOE,
                Metal.ItemType.CHISEL,
                Metal.ItemType.HAMMER,
                Metal.ItemType.SAW,
                Metal.ItemType.KNIFE,
                Metal.ItemType.SCYTHE,
                Metal.ItemType.FISHING_ROD,
                Metal.ItemType.SHEARS
            })
            {
                accept(out, TFCItems.METAL_ITEMS, metal, itemType);
            }
        }

        out.accept(TFCItems.BLOWPIPE);
        out.accept(TFCItems.CERAMIC_BLOWPIPE);
        out.accept(TFCItems.BLOWPIPE_WITH_GLASS);
        out.accept(TFCItems.CERAMIC_BLOWPIPE_WITH_GLASS);
        out.accept(TFCItems.JACKS);
        out.accept(TFCItems.GEM_SAW);
        out.accept(TFCItems.PADDLE);
        out.accept(TFCItems.WOOL_CLOTH);

        out.accept(TFCItems.BONE_NEEDLE);

        out.accept(Items.WRITABLE_BOOK);
        out.accept(Items.SPYGLASS);
        out.accept(Items.CLOCK);
        out.accept(Items.LEAD);
        out.accept(Items.SADDLE);

        out.accept(TFCItems.SPINDLE);
        out.accept(TFCItems.JUTE_NET);

        out.accept(TFCItems.HANDSTONE);
        out.accept(Items.BOWL);
        out.accept(TFCBlocks.CERAMIC_BOWL);

        for (Wood wood : Wood.VALUES)
        {
            accept(out, TFCItems.BOATS, wood);
        }

        out.accept(Items.MINECART);

        for (Wood wood : Wood.VALUES)
        {
            accept(out, TFCItems.CHEST_MINECARTS, wood);
        }

        out.accept(Items.RAIL);
        out.accept(Items.POWERED_RAIL);
        out.accept(Items.DETECTOR_RAIL);
        out.accept(Items.ACTIVATOR_RAIL);

        out.accept(TFCItems.EMPTY_PAN);
        out.accept(TFCItems.VESSEL);
        for (DyeColor color : DyeColor.values())
        {
            accept(out, TFCItems.GLAZED_VESSELS, color);
        }

        out.accept(TFCItems.WOODEN_BUCKET);
        out.accept(TFCItems.JUG);

        out.accept(TFCItems.SILICA_GLASS_BOTTLE);
        out.accept(TFCItems.HEMATITIC_GLASS_BOTTLE);
        out.accept(TFCItems.OLIVINE_GLASS_BOTTLE);
        out.accept(TFCItems.VOLCANIC_GLASS_BOTTLE);

        out.accept(TFCItems.EMPTY_JAR);
        out.accept(TFCItems.EMPTY_JAR_WITH_LID);
        out.accept(TFCItems.JAR_LID);

        TFCItems.FRESHWATER_FISH_BUCKETS.values().forEach(out::accept);
        out.accept(TFCItems.COD_BUCKET);
        out.accept(TFCItems.JELLYFISH_BUCKET);
        out.accept(TFCItems.TROPICAL_FISH_BUCKET);
        out.accept(TFCItems.PUFFERFISH_BUCKET);

    }

    private static void fillCombatTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output out)
    {
        for (RockCategory.ItemType type : RockCategory.ItemType.values())
        {
            for (RockCategory category : RockCategory.values())
            {
                accept(out, TFCItems.ROCK_TOOLS, category, RockCategory.ItemType.AXE);
                accept(out, TFCItems.ROCK_TOOLS, category, RockCategory.ItemType.JAVELIN);
                accept(out, TFCItems.ROCK_TOOLS, category, RockCategory.ItemType.KNIFE);
            }
        }

        out.accept(Items.LEATHER_HELMET);
        out.accept(Items.LEATHER_CHESTPLATE);
        out.accept(Items.LEATHER_LEGGINGS);
        out.accept(Items.LEATHER_BOOTS);
        out.accept(Items.SHIELD);
        out.accept(Items.LEATHER_HORSE_ARMOR);

        for (Metal metal : Metal.values())
        {
            for (Metal.ItemType itemType : new Metal.ItemType[] {
                Metal.ItemType.AXE,
                Metal.ItemType.HAMMER,
                Metal.ItemType.KNIFE,
                Metal.ItemType.JAVELIN,
                Metal.ItemType.SWORD,
                Metal.ItemType.MACE,

                Metal.ItemType.HELMET,
                Metal.ItemType.CHESTPLATE,
                Metal.ItemType.GREAVES,
                Metal.ItemType.BOOTS,

                Metal.ItemType.SHIELD,
                Metal.ItemType.HORSE_ARMOR,
            })
            {
                accept(out, TFCItems.METAL_ITEMS, metal, itemType);
            }
        }

        out.accept(Items.GUNPOWDER);
        out.accept(TFCBlocks.POWDERKEG);
        out.accept(Items.SNOWBALL);
        out.accept(Items.BOW);
        out.accept(Items.CROSSBOW);
        out.accept(Items.ARROW);
        out.accept(TFCItems.GLOW_ARROW);
    }

    private static void fillFoodsDrinksTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output out)
    {
        out.accept(TFCBlocks.CAKE);
        TFCItems.FOOD.values().forEach(out::accept);
        TFCItems.SOUPS.values().forEach(out::accept);
        TFCItems.SALADS.values().forEach(out::accept);

        out.accept(TFCItems.EMPTY_JAR);
        out.accept(TFCItems.EMPTY_JAR_WITH_LID);

        for (Food food : Food.values())
        {
            accept(out, TFCItems.FRUIT_PRESERVES, food);
            accept(out, TFCItems.UNSEALED_FRUIT_PRESERVES, food);
            accept(out, TFCItems.JAM, food);
        }

        out.accept(Items.WATER_BUCKET);
        out.accept(Items.MILK_BUCKET);

        TFCFluids.FLUIDS.getEntries().forEach(fluid -> {
            if (fluid.getId().toString().endsWith("beer"))
            {
                out.accept(fluid.value().getBucket());
            }
            else if (fluid.getId().toString().endsWith("cider"))
            {
                out.accept(fluid.value().getBucket());
            }
            else if (fluid.getId().toString().endsWith("rum"))
            {
                out.accept(fluid.value().getBucket());
            }
            else if (fluid.getId().toString().endsWith("sake"))
            {
                out.accept(fluid.value().getBucket());
            }
            else if (fluid.getId().toString().endsWith("vodka"))
            {
                out.accept(fluid.value().getBucket());
            }
            else if (fluid.getId().toString().endsWith("whiskey"))
            {
                out.accept(fluid.value().getBucket());
            }
        });
    }

    private static void fillMetalsIngredientsTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output out)
    {
        out.accept(Items.LEATHER);
        out.accept(Items.GUNPOWDER);
        out.accept(TFCItems.SOOT);
        out.accept(TFCItems.SANDPAPER);
        out.accept(Items.PAPER);
        out.accept(Items.BOOK);
        out.accept(TFCItems.BLANK_DISC);
        out.accept(TFCItems.BRASS_MECHANISMS);
        out.accept(TFCItems.BURLAP_CLOTH);
        out.accept(TFCItems.SILK_CLOTH);
        out.accept(TFCItems.WOOL_CLOTH);
        out.accept(TFCItems.WOOL);
        out.accept(TFCItems.WOOL_YARN);
        out.accept(TFCItems.COMPOST);
        out.accept(TFCItems.ROTTEN_COMPOST);
        out.accept(TFCItems.PURE_NITROGEN);
        out.accept(TFCItems.PURE_POTASSIUM);
        out.accept(TFCItems.PURE_PHOSPHORUS);
        out.accept(TFCItems.DAUB);
        out.accept(TFCItems.DIRTY_JUTE_NET);
        out.accept(TFCItems.CACTUS_WOOD);
        out.accept(TFCItems.DRIED_CACTUS_WOOD);
        out.accept(TFCItems.FIRE_CLAY);
        out.accept(TFCItems.KAOLIN_CLAY);
        out.accept(TFCItems.GLUE);
        out.accept(TFCItems.GOAT_HORN);
        out.accept(TFCItems.ALFALFA);
        out.accept(TFCItems.CANOLA);
        out.accept(TFCItems.JUTE);
        out.accept(TFCItems.JUTE_FIBER);
        out.accept(TFCItems.OLIVE_PASTE);
        out.accept(TFCItems.CANOLA_PASTE);
        out.accept(TFCItems.MORTAR);
        out.accept(TFCItems.PAPYRUS);
        out.accept(TFCItems.PAPYRUS_STRIP);
        out.accept(TFCItems.SOAKED_PAPYRUS_STRIP);
        out.accept(TFCItems.UNREFINED_PAPER);
        out.accept(TFCItems.STICK_BUNCH);
        out.accept(TFCItems.STICK_BUNDLE);
        out.accept(Items.BOWL);
        out.accept(TFCItems.STRAW);

        TFCItems.POWDERS.values().forEach(out::accept);
        TFCItems.ORE_POWDERS.values().forEach(out::accept);

        out.accept(TFCItems.BLUBBER);
        for (HideItemType type : HideItemType.values())
        {
            TFCItems.HIDES.get(type).values().forEach(out::accept);
        }
        out.accept(TFCItems.TREATED_HIDE);
        out.accept(Items.INK_SAC);
        out.accept(Items.GLOW_INK_SAC);

        out.accept(TFCItems.ALABASTER_BRICK);
        out.accept(TFCItems.UNFIRED_BRICK);
        out.accept(Items.BRICK);
        out.accept(TFCItems.UNFIRED_FIRE_BRICK);
        out.accept(TFCItems.FIRE_BRICK);
        out.accept(TFCItems.UNFIRED_CRUCIBLE);
        out.accept(TFCItems.UNFIRED_FLOWER_POT);
        out.accept(Items.FLOWER_POT);
        out.accept(TFCItems.UNFIRED_BOWL);
        out.accept(TFCBlocks.CERAMIC_BOWL);
        out.accept(TFCItems.UNFIRED_PAN);
        out.accept(TFCItems.UNFIRED_SPINDLE_HEAD);
        out.accept(TFCItems.SPINDLE_HEAD);
        out.accept(TFCItems.UNFIRED_POT);
        out.accept(TFCItems.UNFIRED_VESSEL);
        out.accept(TFCItems.UNFIRED_LARGE_VESSEL);
        out.accept(TFCItems.UNFIRED_JUG);


        for (DyeColor color : DyeColor.values())
        {
            accept(out, TFCItems.UNFIRED_GLAZED_VESSELS, color);
            accept(out, TFCItems.UNFIRED_GLAZED_LARGE_VESSELS, color);
        }
        for (Metal.ItemType type : Metal.ItemType.values())
        {
            accept(out, TFCItems.UNFIRED_MOLDS, type);
            accept(out, TFCItems.MOLDS, type);
            if (type == Metal.ItemType.INGOT)
            {
                out.accept(TFCItems.UNFIRED_FIRE_INGOT_MOLD);
                out.accept(TFCItems.FIRE_INGOT_MOLD);
            }
        }
        out.accept(TFCItems.UNFIRED_BELL_MOLD);
        out.accept(TFCItems.BELL_MOLD);
        out.accept(TFCItems.UNFIRED_CHANNEL);
        out.accept(TFCItems.UNFIRED_MOLD_TABLE);

        out.accept(TFCItems.UNFIRED_BLOWPIPE);
        out.accept(TFCItems.SILICA_GLASS_BATCH);
        out.accept(TFCItems.HEMATITIC_GLASS_BATCH);
        out.accept(TFCItems.OLIVINE_GLASS_BATCH);
        out.accept(TFCItems.VOLCANIC_GLASS_BATCH);
        out.accept(TFCItems.LAMP_GLASS);
        out.accept(TFCItems.LENS);
        out.accept(TFCItems.SILICA_GLASS_BOTTLE);
        out.accept(TFCItems.HEMATITIC_GLASS_BOTTLE);
        out.accept(TFCItems.OLIVINE_GLASS_BOTTLE);
        out.accept(TFCItems.VOLCANIC_GLASS_BOTTLE);
        out.accept(TFCItems.EMPTY_JAR);
        out.accept(TFCItems.EMPTY_JAR_WITH_LID);
        out.accept(TFCItems.JAR_LID);

        for (Wood wood : Wood.VALUES)
        {
            accept(out, TFCItems.LUMBER, wood);
        }

        out.accept(TFCItems.UNFIRED_VESSEL);
        TFCItems.UNFIRED_GLAZED_VESSELS.values().forEach(out::accept);

        out.accept(TFCItems.UNFIRED_LARGE_VESSEL);
        TFCItems.UNFIRED_GLAZED_LARGE_VESSELS.values().forEach(out::accept);

        for (DyeColor color : DyeColor.values())
        {
            out.accept(DyeItem.byColor(color));
        }

        TFCItems.GEMS.values().forEach(out::accept);

        out.accept(Items.CHARCOAL);
        out.accept(TFCBlocks.LIGNITE);
        out.accept(TFCBlocks.BITUMINOUS_COAL);
        out.accept(TFCBlocks.HALITE);

        for (RockCategory.ItemType type : RockCategory.ItemType.values())
        {
            for (RockCategory category : RockCategory.values())
            {
                accept(out, TFCItems.ROCK_TOOLS, category, RockCategory.ItemType.AXE_HEAD);
                accept(out, TFCItems.ROCK_TOOLS, category, RockCategory.ItemType.HOE_HEAD);
                accept(out, TFCItems.ROCK_TOOLS, category, RockCategory.ItemType.HAMMER_HEAD);
                accept(out, TFCItems.ROCK_TOOLS, category, RockCategory.ItemType.JAVELIN_HEAD);
                accept(out, TFCItems.ROCK_TOOLS, category, RockCategory.ItemType.SHOVEL_HEAD);
                accept(out, TFCItems.ROCK_TOOLS, category, RockCategory.ItemType.KNIFE_HEAD);
            }
        }

        for (Metal metal : Metal.values())
        {
            TFCFluids.FLUIDS.getEntries().forEach(fluid -> {
                if (fluid.getId().toString().endsWith("/" + metal.getSerializedName()))
                {
                    out.accept(fluid.value().getBucket());
                }
            });
            for (Metal.BlockType type : new Metal.BlockType[] {
                Metal.BlockType.ANVIL
            })
            {
                accept(out, TFCBlocks.METALS, metal, type);
            }

            if (metal == Metal.WROUGHT_IRON)
            {
                out.accept(TFCItems.RAW_IRON_BLOOM);
                out.accept(TFCItems.REFINED_IRON_BLOOM);
            }

            for (Metal.ItemType itemType : new Metal.ItemType[] {
                Metal.ItemType.INGOT,
                Metal.ItemType.DOUBLE_INGOT,
                Metal.ItemType.SHEET,
                Metal.ItemType.DOUBLE_SHEET,
                Metal.ItemType.ROD,

                Metal.ItemType.UNFINISHED_LAMP,

                Metal.ItemType.PICKAXE_HEAD,
                Metal.ItemType.PROPICK_HEAD,
                Metal.ItemType.AXE_HEAD,
                Metal.ItemType.SHOVEL_HEAD,
                Metal.ItemType.HOE_HEAD,
                Metal.ItemType.CHISEL_HEAD,
                Metal.ItemType.HAMMER_HEAD,
                Metal.ItemType.SAW_BLADE,
                Metal.ItemType.KNIFE_BLADE,
                Metal.ItemType.SCYTHE_BLADE,
                Metal.ItemType.JAVELIN_HEAD,
                Metal.ItemType.SWORD_BLADE,
                Metal.ItemType.MACE_HEAD,
                Metal.ItemType.FISH_HOOK,

                Metal.ItemType.UNFINISHED_HELMET,
                Metal.ItemType.UNFINISHED_CHESTPLATE,
                Metal.ItemType.UNFINISHED_GREAVES,
                Metal.ItemType.UNFINISHED_BOOTS,
            })
            {
                accept(out, TFCItems.METAL_ITEMS, metal, itemType);
            }
        }

        out.accept(TFCItems.ARIDISOL_MUD_BRICK);
        out.accept(TFCItems.OXISOL_MUD_BRICK);
        out.accept(TFCItems.FLUVISOL_MUD_BRICK);
        out.accept(TFCItems.ENTISOL_MUD_BRICK);
        out.accept(TFCItems.ANDISOL_MUD_BRICK);
        out.accept(TFCItems.MOLLISOL_MUD_BRICK);
        out.accept(TFCItems.ALFISOL_MUD_BRICK);
        out.accept(TFCItems.PODZOL_MUD_BRICK);

        for (SoilBlockType.Variant variant : SoilBlockType.Variant.values())
        {
            accept(out, TFCBlocks.SOIL, SoilBlockType.DRYING_BRICKS, variant);
        }

        for (Rock rock : Rock.VALUES)
        {
            accept(out, TFCItems.BRICKS, rock);
        }

        for (Ore ore : Ore.values())
        {
            if (ore.isGraded())
            {
                accept(out, TFCBlocks.SMALL_ORES, ore);
                accept(out, TFCItems.GRADED_ORES, ore, Ore.Grade.POOR);
                accept(out, TFCItems.GRADED_ORES, ore, Ore.Grade.NORMAL);
                accept(out, TFCItems.GRADED_ORES, ore, Ore.Grade.RICH);
            }
        }

        TFCFluids.FLUIDS.getEntries().forEach(fluid -> {
            out.accept(fluid.value().getBucket());
        });
    }

    private static void fillSpawnEggsTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output out)
    {
        TFCEntities.ENTITIES.getEntries().forEach(entity -> {
            final SpawnEggItem item = SpawnEggItem.byId(entity.value());
            if (item != null)
            {
                out.accept(item);
            }
        });
    }


    // Helpers

    private static Id register(String name, Supplier<ItemStack> icon, CreativeModeTab.DisplayItemsGenerator displayItems)
    {
        final var holder = CREATIVE_TABS.register(name, () -> CreativeModeTab.builder()
            .icon(icon)
            .title(Component.translatable("tfc.creative_mode_tab." + name))
            .displayItems(displayItems)
            .build());
        return new Id(holder, displayItems);
    }

    private static <R extends ItemLike, K1, K2> void accept(CreativeModeTab.Output out, Map<K1, Map<K2, R>> map, K1 key1, K2 key2)
    {
        if (map.containsKey(key1))
        {
            accept(out, map.get(key1), key2);
        }
    }

    private static <R extends ItemLike, K> void accept(CreativeModeTab.Output out, Map<K, R> map, K key)
    {
        if (map.containsKey(key))
        {
            out.accept(map.get(key));
        }
    }

    private static void accept(CreativeModeTab.Output out, DecorationBlockHolder decoration)
    {
        out.accept(decoration.stair());
        out.accept(decoration.slab());
        out.accept(decoration.wall());
    }

    public record Id(DeferredHolder<CreativeModeTab, CreativeModeTab> tab, CreativeModeTab.DisplayItemsGenerator generator) {}

    public static void addToVanillaTabs(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(Wood.OAK.getBlock(Wood.BlockType.BARREL).get());
            event.accept(Wood.OAK.getBlock(Wood.BlockType.CLUTCH).get());
            event.accept(TFCBlocks.VANE);
            event.accept(TFCBlocks.ANEMOMETER);
            event.accept(TFCBlocks.CALENDAR_CLOCK);
            event.accept(TFCBlocks.THERMOMETER);
        }
    }
}
