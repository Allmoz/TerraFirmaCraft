package net.dries007.tfc.common.entities.livestock.camel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.dries007.tfc.common.entities.ai.SetLookTarget;
import net.dries007.tfc.common.entities.ai.TFCBrain;
import net.dries007.tfc.common.entities.ai.livestock.BreedBehavior;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.camel.CamelAi;
import net.minecraft.world.entity.schedule.Activity;

import java.util.function.Predicate;

public class TFCCamelAi {
    protected static final ImmutableList<SensorType<? extends Sensor<? super Camel>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.HURT_BY, TFCBrain.TEMPTATION_SENSOR.get(), SensorType.NEAREST_ADULT);

    public static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
        MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET,
        MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATE_RECENTLY,
        MemoryModuleType.BREED_TARGET, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ADULT,
        MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleType.IS_TEMPTED, MemoryModuleType.AVOID_TARGET,
        MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.HURT_BY, MemoryModuleType.IS_PANICKING, MemoryModuleType.GAZE_COOLDOWN_TICKS
    );

    @SuppressWarnings("unchecked")
    public static Brain<?> makeBrain(Brain<? extends Camel> brain) {
        initCoreActivity((Brain<TFCCamel>) brain);
        initIdleActivity((Brain<TFCCamel>) brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    private static void initCoreActivity(Brain<TFCCamel> brain) {
        brain.addActivity(
            Activity.CORE,
            0,
            ImmutableList.of(
                new Swim(0.8F),
                new CamelAi.CamelPanic(2.0F),
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink(),
                new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS),
                new CountDownCooldownTicks(MemoryModuleType.GAZE_COOLDOWN_TICKS)
            )
        );
    }

    public static void initIdleActivity(Brain<TFCCamel> brain) {
        brain.addActivity(
            Activity.IDLE,
            ImmutableList.of(
                Pair.of(0, SetLookTarget.create(EntityType.PLAYER, 6.0F, UniformInt.of(30, 60))),
                Pair.of(1, new BreedBehavior<>(1.0F)),
                Pair.of(2, new RunOne<>(
                    ImmutableList.of(
                        Pair.of(new FollowTemptation(e -> e.isBaby() ? 1.0F : 2.0F), 1),
                        Pair.of(BabyFollowAdult.create(UniformInt.of(5, 16), 1.0F), 1)
                    )
                )),
                Pair.of(3, new RandomLookAround(UniformInt.of(150, 250), 30.0F, 0.0F, 0.0F)),
                Pair.of(4, new RunOne<>(
                    ImmutableList.of(
                        Pair.of(BehaviorBuilder.triggerIf(Predicate.not(Camel::refuseToMove), RandomStroll.stroll(1.0F)), 1),
                        Pair.of(BehaviorBuilder.triggerIf(Predicate.not(Camel::refuseToMove), SetWalkTargetFromLookTarget.create(1.0F, 3)), 1),
                        Pair.of(new CamelAi.RandomSitting(20), 1),
                        Pair.of(new DoNothing(30, 60), 1)
                    )
                ))
            )
        );
    }

    public static void updateActivity(Camel camel) {
        camel.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.IDLE));
    }
}
