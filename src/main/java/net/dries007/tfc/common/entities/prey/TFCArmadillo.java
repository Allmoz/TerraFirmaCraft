package net.dries007.tfc.common.entities.prey;

import com.mojang.serialization.Dynamic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.TFCEntities;

public class TFCArmadillo extends Armadillo
{
    public TFCArmadillo(EntityType<? extends Animal> entityType, Level level)
    {
        super(entityType, level);
    }

    @Override
    protected Brain.Provider<Armadillo> brainProvider()
    {
        return (Brain.Provider<Armadillo>) tfcBrain();
    }

    protected Brain.Provider<? extends Armadillo> tfcBrain()
    {
        return Brain.provider(TFCArmadilloAi.MEMORY_TYPES, TFCArmadilloAi.SENSOR_TYPES);
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
}
