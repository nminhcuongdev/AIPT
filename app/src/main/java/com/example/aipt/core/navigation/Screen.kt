package com.example.aipt.core.navigation

sealed class Screen(val route: String) {
    data object ExerciseList : Screen("exercise_list")
    data object ExerciseDetail : Screen("exercise_detail/{exerciseId}") {
        fun createRoute(exerciseId: Int) = "exercise_detail/$exerciseId"
    }
}
