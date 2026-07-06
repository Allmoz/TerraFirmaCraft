package net.dries007.tfc.common.entities.livestock.camel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.dries007.tfc.common.entities.ai.TFCBrain;
import net.dries007.tfc.common.entities.ai.livestock.BreedBehavior;
import net.dries007.tfc.common.entities.ai.prey.AvoidPredatorAndRammersBehavior;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.camel.CamelAi;
import net.minecraft.world.entity.schedule.Activity;

import java.util.function.Predicate;

public class TFCCamelAi {
    protected static final ImmutableList<SensorType<? extends Sensor<? super Camel>>> SENSOR_TYPES = ImmutableList.of(
        SensorType.NEAREST_LIVING_ENTITIES, SensorType.HURT_BY, TFCBrain.TEMPTATION_SENSOR.get(), SensorType.NEAREST_ADULT
    );

    public static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
        MemoryModuleType.IS_PANICKING,
        MemoryModuleType.HURT_BY,
        MemoryModuleType.HURT_BY_ENTITY,
        MemoryModuleType.WALK_TARGET,
        MemoryModuleType.LOOK_TARGET,
        MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
        MemoryModuleType.PATH,
        MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
        MemoryModuleType.TEMPTING_PLAYER,
        MemoryModuleType.TEMPTATION_COOLDOWN_TICKS,
        MemoryModuleType.GAZE_COOLDOWN_TICKS,
        MemoryModuleType.IS_TEMPTED,
        MemoryModuleType.BREED_TARGET,
        MemoryModuleType.NEAREST_VISIBLE_ADULT
    );

    public static Brain.Provider<TFCCamel> brainProvider()
    {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    @SuppressWarnings("unchecked")
    public static Brain<?> makeBrain(Brain<? extends Camel> brain)
    {
        initCoreActivity((Brain<TFCCamel>) brain);
        initIdleActivity((Brain<TFCCamel>) brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    private static void initCoreActivity(Brain<TFCCamel> brain)
    {
        brain.addActivity(
            Activity.CORE,
            0,
            ImmutableList.of(
                new Swim(0.8F),
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink(),
                new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS),
                new CountDownCooldownTicks(MemoryModuleType.GAZE_COOLDOWN_TICKS)
            )
        );
    }

    public static void initIdleActivity(Brain<TFCCamel> brain)
    {
        brain.addActivity(
            Activity.IDLE,
            ImmutableList.of(
                Pair.of(0, SetEntityLookTargetSometimes.create(EntityType.PLAYER, 6.0F, UniformInt.of(30, 60))),
                Pair.of(1, AvoidPredatorAndRammersBehavior.create(true)),
                Pair.of(1, new AnimalPanic<>(4.0F)),
                Pair.of(1, new BreedBehavior<>(1.0F)),
                Pair.of(
                    2,
                    new RunOne<>(
                        ImmutableList.of(
                            Pair.of(new FollowTemptation(e -> e.isBaby() ? 2.5F : 3.5F), 1),
                            Pair.of(BabyFollowAdult.create(UniformInt.of(5, 16), 2.5F), 1)
                        )
                    )
                ),
                Pair.of(3, new RandomLookAround(UniformInt.of(150, 250), 30.0F, 0.0F, 0.0F)),
                Pair.of(
                    4,
                    new RunOne<>(
                        ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT),
                        ImmutableList.of(
                            Pair.of(BehaviorBuilder.triggerIf(Predicate.not(Camel::refuseToMove), RandomStroll.stroll(2.0F)), 1),
                            Pair.of(BehaviorBuilder.triggerIf(Predicate.not(Camel::refuseToMove), SetWalkTargetFromLookTarget.create(2.0F, 3)), 1),
                            Pair.of(new CamelAi.RandomSitting(20), 1),
                            Pair.of(new DoNothing(30, 60), 1)
                        )
                    )
                )
            )
        );
    }

    public static void updateActivity(Camel camel)
    {
        camel.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.IDLE));
    }
}
