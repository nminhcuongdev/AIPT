package nminhcuong.aipt.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import nminhcuong.aipt.feature.dashboard.data.local.WorkoutSessionStateDao
import nminhcuong.aipt.feature.dashboard.data.local.WorkoutSessionStateEntity
import nminhcuong.aipt.feature.exercise.data.local.ExerciseDao
import nminhcuong.aipt.feature.exercise.data.local.ExerciseEntity
import nminhcuong.aipt.feature.profile.data.local.BodyMetricSnapshotDao
import nminhcuong.aipt.feature.profile.data.local.BodyMetricSnapshotEntity
import nminhcuong.aipt.feature.profile.data.local.GymEquipmentDao
import nminhcuong.aipt.feature.profile.data.local.GymEquipmentEntity
import nminhcuong.aipt.feature.profile.data.local.UserProfileDao
import nminhcuong.aipt.feature.profile.data.local.UserProfileEntity
import nminhcuong.aipt.feature.workout.data.local.WorkoutDayDao
import nminhcuong.aipt.feature.workout.data.local.WorkoutDayEntity
import nminhcuong.aipt.feature.workout.data.local.WorkoutProgressLogDao
import nminhcuong.aipt.feature.workout.data.local.WorkoutProgressLogEntity

@Database(
    entities = [
        ExerciseEntity::class,
        UserProfileEntity::class,
        GymEquipmentEntity::class,
        WorkoutDayEntity::class,
        WorkoutSessionStateEntity::class,
        WorkoutProgressLogEntity::class,
        BodyMetricSnapshotEntity::class,
    ],
    version = 8,
    exportSchema = false,
)
abstract class AiptDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun gymEquipmentDao(): GymEquipmentDao
    abstract fun workoutDayDao(): WorkoutDayDao
    abstract fun workoutSessionStateDao(): WorkoutSessionStateDao
    abstract fun workoutProgressLogDao(): WorkoutProgressLogDao
    abstract fun bodyMetricSnapshotDao(): BodyMetricSnapshotDao
}
