package nminhcuong.aipt.di

import android.content.Context
import androidx.room.Room
import nminhcuong.aipt.core.data.local.AiptDatabase
import nminhcuong.aipt.feature.dashboard.data.local.WorkoutSessionStateDao
import nminhcuong.aipt.feature.exercise.data.local.ExerciseDao
import nminhcuong.aipt.feature.profile.data.local.BodyMetricSnapshotDao
import nminhcuong.aipt.feature.profile.data.local.GymEquipmentDao
import nminhcuong.aipt.feature.profile.data.local.UserProfileDao
import nminhcuong.aipt.feature.workout.data.local.WorkoutDayDao
import nminhcuong.aipt.feature.workout.data.local.WorkoutProgressLogDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AiptDatabase =
        Room.databaseBuilder(
            context = context,
            klass = AiptDatabase::class.java,
            name = "aipt.db",
        ).fallbackToDestructiveMigration(dropAllTables = true).build()

    @Provides
    fun provideExerciseDao(database: AiptDatabase): ExerciseDao = database.exerciseDao()

    @Provides
    fun provideUserProfileDao(database: AiptDatabase): UserProfileDao = database.userProfileDao()

    @Provides
    fun provideGymEquipmentDao(database: AiptDatabase): GymEquipmentDao = database.gymEquipmentDao()

    @Provides
    fun provideWorkoutDayDao(database: AiptDatabase): WorkoutDayDao = database.workoutDayDao()

    @Provides
    fun provideWorkoutSessionStateDao(database: AiptDatabase): WorkoutSessionStateDao = database.workoutSessionStateDao()

    @Provides
    fun provideWorkoutProgressLogDao(database: AiptDatabase): WorkoutProgressLogDao = database.workoutProgressLogDao()

    @Provides
    fun provideBodyMetricSnapshotDao(database: AiptDatabase): BodyMetricSnapshotDao = database.bodyMetricSnapshotDao()
}
