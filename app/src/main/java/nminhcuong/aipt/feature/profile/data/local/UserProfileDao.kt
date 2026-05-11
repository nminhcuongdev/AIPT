package nminhcuong.aipt.feature.profile.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles WHERE id = 1 LIMIT 1")
    fun observeProfile(): Flow<UserProfileEntity?>

    @Upsert
    suspend fun saveProfile(profile: UserProfileEntity)

    @Query("DELETE FROM user_profiles WHERE id = 1")
    suspend fun deleteProfile()
}
