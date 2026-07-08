package net.dries007.tfc.common.entities.prey;

import java.util.Set;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BabyFollowAdult;
import net.minecraft.world.entity.ai.behavior.CountDownCooldownTicks;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.FollowTemptation;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.RandomLookAround;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTargetSometimes;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromLookTarget;
import net.minecraft.world.entity.ai.behavior.Swim;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.animal.armadillo.ArmadilloAi;
import net.minecraft.world.entity.schedule.Activity;

import net.dries007.tfc.common.entities.ai.TFCBrain;

public class TFCArmadilloAi
{
    // TODO: Rework ARMADILLO_SCARE_DETECTED to function with TFC predators
    protected static final ImmutableList<SensorType<? extends Sensor<? super Armadillo>>> SENSOR_TYPES = ImmutableList.of(
        SensorType.NEAREST_LIVING_ENTITIES, SensorType.HURT_BY, TFCBrain.TEMPTATION_SENSOR.get(), SensorType.NEAREST_ADULT, SensorType.ARMADILLO_SCARE_DETECTED
    );

    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
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
        MemoryModuleType.NEAREST_VISIBLE_ADULT,
        MemoryModuleType.DANGER_DETECTED_RECENTLY
    );

    // Copied from ArmadilloAi (private method)
    private static final OneShot<TFCArmadillo> ARMADILLO_ROLLING_OUT = BehaviorBuilder.create(
        instance -> instance.group(instance.absent(MemoryModuleType.DANGER_DETECTED_RECENTLY))
            .apply(instance, p ->
                ((level, entity, gameTime) ->
                    {
                        if (entity.isScared())
                        {
                            entity.rollOut();
                            return true;
                        }
                        else
                        {
                            return false;
                        }
                    }
                )
            )
    );

    public static Brain.Provider<TFCArmadillo> brainProvider()
    {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    protected static Brain<?> makeBrain(Brain<? extends Armadillo> brain)
    {
        initCoreActivity((Brain<TFCArmadillo>) brain);
        initIdleActivity((Brain<TFCArmadillo>) brain);
        initScaredActivity((Brain<TFCArmadillo>) brain);

        brain.setCoreActivities(Set.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();

        return brain;
    }

    public static void initCoreActivity(Brain<TFCArmadillo> brain)
    {
        brain.addActivity(
            Activity.CORE,
            0,
            ImmutableList.of(
                new Swim(0.8F),
                new ArmadilloAi.ArmadilloPanic(2.0F),
                new LookAtTargetSink(45, 90) {
                    @Override
                    protected boolean checkExtraStartConditions(ServerLevel level, Mob owner)
                    {
                        if (owner instanceof TFCArmadillo armadillo && armadillo.isScared())
                        {
                            return false;
                        }

                        return super.checkExtraStartConditions(level, owner);
                    }
                },
                new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS),
                new CountDownCooldownTicks(MemoryModuleType.GAZE_COOLDOWN_TICKS),
                ARMADILLO_ROLLING_OUT
            )
        );
    }

    public static void initIdleActivity(Brain<TFCArmadillo> brain)
    {
        brain.addActivity(
            Activity.IDLE,
            ImmutableList.of(
                Pair.of(0, SetEntityLookTargetSometimes.create(EntityType.PLAYER, 6.0F, UniformInt.of(30, 60))),
                Pair.of(
                    1,
                    new RunOne<>(
                        ImmutableList.of(
                            Pair.of(new FollowTemptation(p_316818_ -> 1.25F, p_319682_ -> p_319682_.isBaby() ? 1.0 : 2.0), 1),
                            Pair.of(BabyFollowAdult.create(UniformInt.of(5, 16), 1.25F), 1)
                        )
                    )
                ),
                Pair.of(2, new RandomLookAround(UniformInt.of(150, 250), 30.0F, 0.0F, 0.0F)),
                Pair.of(
                    4,
                    new RunOne<>(
                        ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT),
                        ImmutableList.of(
                            Pair.of(RandomStroll.stroll(1.0F), 1),
                            Pair.of(SetWalkTargetFromLookTarget.create(1.0F, 3), 1),
                            Pair.of(new DoNothing(30, 60), 1)
                        )
                    )
                )
            )
        );
    }

    public static void initScaredActivity(Brain<TFCArmadillo> brain)
    {
        brain.addActivityWithConditions(
            Activity.PANIC,
            ImmutableList.of(Pair.of(0, new ArmadilloAi.ArmadilloBallUp())),
            Set.of(
                Pair.of(MemoryModuleType.DANGER_DETECTED_RECENTLY, MemoryStatus.VALUE_PRESENT),
                Pair.of(MemoryModuleType.IS_PANICKING, MemoryStatus.VALUE_ABSENT)
            )
        );
    }

    public static void updateActivity(TFCArmadillo armadillo)
    {
        armadillo.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.PANIC, Activity.IDLE));
    }

}
