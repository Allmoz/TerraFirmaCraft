package net.dries007.tfc.common.entities;

import net.minecraft.world.entity.LivingEntity;

public interface Scareable
{
    default boolean isScaredBy(LivingEntity entity)
    {
        return false;
    }

    default boolean isCurrentlyScareable()
    {
        return true;
    }
}
