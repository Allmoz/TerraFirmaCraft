package net.dries007.tfc.common.entities.prey;

import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.entities.ai.TFCGroundPathNavigation;

public class TFCArmadillo extends Armadillo
{
    public TFCArmadillo(EntityType<? extends Animal> entityType, Level level)
    {
        super(entityType, level);
    }

    @Override
    protected Brain.Provider<Armadillo> brainProvider()
    {
        return (Brain.Provider<Armadillo>) TFCArmadilloAi.brainProvider();
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic)
    {
        return TFCArmadilloAi.makeBrain(brainProvider().makeBrain(dynamic));
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob)
    {
        return TFCEntities.ARMADILLO.get().create(level);
    }

    @Override
    public boolean isScaredBy(LivingEntity entity)
    {
        if (entity.getType().is(TFCTags.Entities.LAND_PREDATORS))
        {
            return true;
        }
        return super.isScaredBy(entity);
    }

    @Override
    protected PathNavigation createNavigation(Level level)
    {
        return new TFCGroundPathNavigation(this, level);
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level)
    {
        return level.getBlockState(pos.below()).is(TFCTags.Blocks.BUSH_PLANTABLE_ON) ? 10.0F : level.getPathfindingCostFromLightLevels(pos) - 0.5F;
    }
}
