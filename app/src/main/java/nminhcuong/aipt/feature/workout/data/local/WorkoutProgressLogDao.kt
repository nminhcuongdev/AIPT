package nminhcuong.aipt.feature.workout.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutProgressLogDao {
    @Query("SELECT * FROM workout_progress_logs ORDER BY performedAt ASC, id ASC")
    fun observeLogs(): Flow<List<WorkoutProgressLogEntity>>

    @Insert
    suspend fun insertAll(logs: List<WorkoutProgressLogEntity>)
}
