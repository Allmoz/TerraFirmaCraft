package net.dries007.tfc.common.entities.ai.prey;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;

import net.dries007.tfc.common.entities.Scareable;

public class ScareSensor extends Sensor<LivingEntity>
{
    private final int scareDistance;
    private final int memoryTimeToLive;

    public ScareSensor(int scareDistance, int memoryTimeToLive)
    {
        super(5);
        this.scareDistance = scareDistance;
        this.memoryTimeToLive = memoryTimeToLive;
    }

    public ScareSensor()
    {
        super(5);
        this.scareDistance = 8;
        this.memoryTimeToLive = 80;
    }

    @Override
    protected void doTick(ServerLevel level, LivingEntity entity)
    {
        if (!this.readyTest(entity))
        {
            this.clearMemory(entity);
        }
        else
        {
            this.checkForMobsNearby(entity);
        }
    }

    @Override
    public Set<MemoryModuleType<?>> requires()
    {
        return Set.of(MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.DANGER_DETECTED_RECENTLY);
    }

    public void checkForMobsNearby(LivingEntity sensingEntity)
    {
        Optional<List<LivingEntity>> memory = sensingEntity.getBrain().getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES);
        if (memory.isPresent())
        {
            boolean scaryMobNearby = memory.get().stream().anyMatch(entity -> isScaryMob(sensingEntity, entity) && entity.closerThan(sensingEntity, scareDistance));
            if (scaryMobNearby)
            {
                setDangerDetected(sensingEntity);
            }
        }
    }

    public boolean readyTest(LivingEntity entity)
    {
        return entity instanceof Scareable scareable && scareable.isCurrentlyScareable();
    }

    public boolean isScaryMob(LivingEntity entity, LivingEntity mob)
    {
        return entity instanceof Scareable scareable && scareable.isScaredBy(mob);
    }

    public void setDangerDetected(LivingEntity sensingEntity)
    {
        sensingEntity.getBrain().setMemoryWithExpiry(MemoryModuleType.DANGER_DETECTED_RECENTLY, true, this.memoryTimeToLive);
    }

    public void clearMemory(LivingEntity sensingEntity)
    {
        sensingEntity.getBrain().eraseMemory(MemoryModuleType.DANGER_DETECTED_RECENTLY);
    }
}
