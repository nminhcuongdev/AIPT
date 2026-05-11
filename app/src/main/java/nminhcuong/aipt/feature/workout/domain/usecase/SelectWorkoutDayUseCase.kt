package nminhcuong.aipt.feature.workout.domain.usecase

import nminhcuong.aipt.feature.workout.domain.model.WorkoutDay
import java.util.Calendar
import javax.inject.Inject

class SelectWorkoutDayUseCase @Inject constructor() {
    operator fun invoke(days: List<WorkoutDay>): WorkoutDay? {
        if (days.isEmpty()) return null
        val sortedDays = days.sortedBy { it.day }
        val maxDay = sortedDays.maxOfOrNull { it.day } ?: 1
        val targetDay = ((mondayBasedDay() - 1) % maxDay) + 1
        return sortedDays.firstOrNull { it.day == targetDay } ?: sortedDays.first()
    }

    private fun mondayBasedDay(): Int = when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> 1
        Calendar.TUESDAY -> 2
        Calendar.WEDNESDAY -> 3
        Calendar.THURSDAY -> 4
        Calendar.FRIDAY -> 5
        Calendar.SATURDAY -> 6
        else -> 7
    }
}
