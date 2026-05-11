package nminhcuong.aipt.feature.workout.data.repository

import android.util.Log
import com.google.gson.Gson
import nminhcuong.aipt.feature.workout.data.remote.WorkoutPlanApi
import nminhcuong.aipt.feature.workout.domain.model.WorkoutDayProgressAnalysisRequest
import nminhcuong.aipt.feature.workout.domain.model.WorkoutDayProgressAnalysisResponse
import nminhcuong.aipt.feature.workout.domain.model.WorkoutPlanRequest
import nminhcuong.aipt.feature.workout.domain.model.WorkoutPlanResponse
import nminhcuong.aipt.feature.workout.domain.repository.WorkoutRepository
import javax.inject.Inject

class WorkoutRepositoryImpl @Inject constructor(
    private val api: WorkoutPlanApi,
    private val gson: Gson,
) : WorkoutRepository {
    override suspend fun createWorkoutPlan(request: WorkoutPlanRequest): WorkoutPlanResponse {
        Log.d(TAG, "createWorkoutPlan request=${gson.toJson(request)}")
        return api.createWorkoutPlan(request)
    }

    override suspend fun analyzeWorkoutDayProgress(request: WorkoutDayProgressAnalysisRequest): WorkoutDayProgressAnalysisResponse =
        api.analyzeWorkoutDayProgress(request)

    private companion object {
        const val TAG = "WorkoutPlanRequest"
    }
}