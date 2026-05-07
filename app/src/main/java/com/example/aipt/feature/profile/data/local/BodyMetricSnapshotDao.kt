package com.example.aipt.feature.profile.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface BodyMetricSnapshotDao {
    @Query("SELECT * FROM body_metric_snapshots ORDER BY capturedAt ASC")
    fun observeSnapshots(): Flow<List<BodyMetricSnapshotEntity>>

    @Upsert
    suspend fun saveSnapshot(snapshot: BodyMetricSnapshotEntity)
}
