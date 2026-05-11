package nminhcuong.aipt.di

import nminhcuong.aipt.feature.chat.data.repository.AiTrainerChatRepositoryImpl
import nminhcuong.aipt.feature.chat.domain.repository.AiTrainerChatRepository
import nminhcuong.aipt.feature.dashboard.data.repository.WorkoutSessionRepositoryImpl
import nminhcuong.aipt.feature.dashboard.domain.repository.WorkoutSessionRepository
import nminhcuong.aipt.feature.exercise.data.repository.ExerciseRepositoryImpl
import nminhcuong.aipt.feature.exercise.domain.repository.ExerciseRepository
import nminhcuong.aipt.feature.profile.data.repository.ProfileRepositoryImpl
import nminhcuong.aipt.feature.profile.domain.repository.ProfileRepository
import nminhcuong.aipt.feature.workout.data.repository.WorkoutProgressRepositoryImpl
import nminhcuong.aipt.feature.workout.data.repository.WorkoutRepositoryImpl
import nminhcuong.aipt.feature.workout.data.repository.WorkoutScheduleRepositoryImpl
import nminhcuong.aipt.feature.workout.domain.repository.WorkoutProgressRepository
import nminhcuong.aipt.feature.workout.domain.repository.WorkoutRepository
import nminhcuong.aipt.feature.workout.domain.repository.WorkoutScheduleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindExerciseRepository(
        implementation: ExerciseRepositoryImpl,
    ): ExerciseRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        implementation: ProfileRepositoryImpl,
    ): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindWorkoutRepository(
        implementation: WorkoutRepositoryImpl,
    ): WorkoutRepository

    @Binds
    @Singleton
    abstract fun bindWorkoutScheduleRepository(
        implementation: WorkoutScheduleRepositoryImpl,
    ): WorkoutScheduleRepository

    @Binds
    @Singleton
    abstract fun bindWorkoutProgressRepository(
        implementation: WorkoutProgressRepositoryImpl,
    ): WorkoutProgressRepository

    @Binds
    @Singleton
    abstract fun bindWorkoutSessionRepository(
        implementation: WorkoutSessionRepositoryImpl,
    ): WorkoutSessionRepository

    @Binds
    @Singleton
    abstract fun bindAiTrainerChatRepository(
        implementation: AiTrainerChatRepositoryImpl,
    ): AiTrainerChatRepository
}
