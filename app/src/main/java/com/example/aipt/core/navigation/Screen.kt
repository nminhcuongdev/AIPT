package com.example.aipt.core.navigation

sealed class Screen(val route: String) {
    data object ProfileFlow : Screen("profile_flow")
    data object BasicInfo : Screen("profile_basic_info")
    data object InBody : Screen("profile_inbody")
    data object TrainingGoal : Screen("profile_training_goal")
    data object GymEquipment : Screen("profile_gym_equipment")
    data object ExerciseList : Screen("exercise_list")
    data object ExerciseDetail : Screen("exercise_detail/{exerciseId}") {
        fun createRoute(exerciseId: Int) = "exercise_detail/$exerciseId"
    }
}
