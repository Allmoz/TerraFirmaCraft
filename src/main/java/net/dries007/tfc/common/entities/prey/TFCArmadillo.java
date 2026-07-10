/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.prey;

import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.entities.ai.TFCGroundPathNavigation;

// TODO: Implement Temptable?
public class TFCArmadillo extends Armadillo
{
    public static final EntityDataAccessor<Boolean> DATA_IS_MALE = SynchedEntityData.defineId(TFCArmadillo.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> DATA_IS_BABY = SynchedEntityData.defineId(TFCArmadillo.class, EntityDataSerializers.BOOLEAN);

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
    public void tick()
    {
        super.tick();
        if (level().getGameTime() % 4000 == 0 && random.nextInt(2000) == 0)
        {
            setBaby(false);
        }
    }

    // These are only used for the tooltips
    public boolean displayMaleCharacteristics()
    {
        return isMale() && !isBaby();
    }

    public boolean displayFemaleCharacteristics()
    {
        return !isMale();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        builder.define(DATA_IS_MALE, true);
        builder.define(DATA_IS_BABY, false);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key)
    {
        super.onSyncedDataUpdated(key);
        if (DATA_IS_BABY.equals(key))
        {
            refreshDimensions();
        }
    }

    public boolean isMale()
    {
        return entityData.get(DATA_IS_MALE);
    }

    public void setIsMale(boolean male)
    {
        entityData.set(DATA_IS_MALE, male);
    }

    @Override
    public boolean isBaby()
    {
        return entityData.get(DATA_IS_BABY);
    }

    @Override
    public void setBaby(boolean baby)
    {
        entityData.set(DATA_IS_BABY, baby);
    }

    @Override
    public void setAge(int age)
    {
        super.setAge(0);
    }

    @Override
    public int getAge()
    {
        return isBaby() ? -24000 : 0;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnData)
    {
        setIsMale(level.getRandom().nextBoolean());
        setBaby(random.nextFloat() < 0.1F);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnData);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("male", isMale());
        tag.putBoolean("baby", isBaby());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        setIsMale(tag.getBoolean("male"));
        setBaby(tag.getBoolean("baby"));
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
