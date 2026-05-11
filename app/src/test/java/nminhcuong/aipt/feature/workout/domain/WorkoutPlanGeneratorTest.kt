package nminhcuong.aipt.feature.workout.domain

import com.google.gson.Gson
import nminhcuong.aipt.feature.profile.domain.model.EquipmentStatus
import nminhcuong.aipt.feature.profile.domain.model.GymEquipment
import nminhcuong.aipt.feature.profile.domain.model.UserProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkoutPlanGeneratorTest {
    private val generator = WorkoutPlanGenerator()
    private val gson = Gson()

    @Test
    fun `buildRequest maps profile into backend schema with defaults`() {
        val request = generator.buildRequest(
            profile = UserProfile(
                name = "Minh",
                age = null,
                heightCm = null,
                weightKg = null,
                bodyFatPercent = null,
                skeletalMuscleMassKg = null,
                bodyWaterLiters = null,
                visceralFatLevel = null,
                basalMetabolicRateKcal = null,
                waistHipRatio = null,
                leftArmMuscleKg = null,
                rightArmMuscleKg = null,
                trunkMuscleKg = null,
                leftLegMuscleKg = null,
                rightLegMuscleKg = null,
                trainingGoal = "build_muscle",
                daysPerWeek = 1,
                sessionDurationMinutes = 10,
                experienceLevel = "",
                injuriesOrLimitations = "",
                preferredLanguage = "",
            ),
            equipment = listOf(
                GymEquipment(id = 1, name = "Dumbbell", imageUrl = "", status = EquipmentStatus.Available),
            ),
        )

        assertNotNull(request.basicProfile.age)
        assertNotNull(request.basicProfile.heightCm)
        assertNotNull(request.basicProfile.weightKg)
        assertNotNull(request.bodyComposition.bodyFatPercentage)
        assertNotNull(request.bodyComposition.skeletalMuscleMassKg)
        assertNotNull(request.bodyComposition.bodyWaterPercentage)
        assertNotNull(request.bodyComposition.visceralFatLevel)
        assertNotNull(request.bodyComposition.bmrKcal)
        assertNotNull(request.bodyComposition.waistToHipRatio)
        assertNotNull(request.segmentalMuscleMass.leftArmMuscleKg)
        assertNotNull(request.segmentalMuscleMass.rightArmMuscleKg)
        assertNotNull(request.segmentalMuscleMass.trunkMuscleKg)
        assertNotNull(request.segmentalMuscleMass.leftLegMuscleKg)
        assertNotNull(request.segmentalMuscleMass.rightLegMuscleKg)

        assertEquals(0, request.basicProfile.age)
        assertEquals(0, request.basicProfile.heightCm)
        assertEquals(0, request.basicProfile.weightKg)
        assertEquals(0.0, request.bodyComposition.bodyFatPercentage!!, 0.0)
        assertEquals(0.0, request.bodyComposition.skeletalMuscleMassKg!!, 0.0)
        assertEquals(0.0, request.bodyComposition.bodyWaterPercentage!!, 0.0)
        assertEquals(0, request.bodyComposition.visceralFatLevel!!)
        assertEquals(0, request.bodyComposition.bmrKcal!!)
        assertEquals(0.0, request.bodyComposition.waistToHipRatio!!, 0.0)
        assertEquals(0.0, request.segmentalMuscleMass.leftArmMuscleKg!!, 0.0)
        assertEquals(0.0, request.segmentalMuscleMass.rightArmMuscleKg!!, 0.0)
        assertEquals(0.0, request.segmentalMuscleMass.trunkMuscleKg!!, 0.0)
        assertEquals(0.0, request.segmentalMuscleMass.leftLegMuscleKg!!, 0.0)
        assertEquals(0.0, request.segmentalMuscleMass.rightLegMuscleKg!!, 0.0)
        assertEquals(1, request.preferences.daysPerWeek)
        assertEquals(20, request.preferences.sessionDurationMinutes)
        assertEquals("beginner", request.preferences.experienceLevel)
        assertEquals("None", request.preferences.injuriesOrLimitations)
        assertEquals("en", request.preferences.preferredLanguage)
        assertTrue(request.equipment.isNotEmpty())
        assertFalse(request.equipment.first().status.isBlank())

        val json = gson.toJson(request)
        assertTrue(json.contains("\"body_water_percentage\""))
        assertFalse(json.contains("body_water_liters"))
        assertTrue(json.contains("\"days_per_week\":1"))
        assertTrue(json.contains("\"session_duration_minutes\":20"))
    }
}
