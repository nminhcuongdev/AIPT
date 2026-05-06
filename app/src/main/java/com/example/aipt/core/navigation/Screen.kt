package com.example.aipt.core.navigation

sealed class Screen(val route: String) {
    data object Startup : Screen("startup")
    data object MainMenu : Screen("main_menu")
    data object TodayDashboard : Screen("today_dashboard")
    data object ProfileFlow : Screen("profile_flow")
    data object BasicInfo : Screen("profile_basic_info")
    data object InBody : Screen("profile_inbody")
    data object TrainingGoal : Screen("profile_training_goal")
    data object Preferences : Screen("profile_preferences")
    data object GymEquipment : Screen("profile_gym_equipment")
    data object ExerciseList : Screen("exercise_list")
    data object ExerciseDetail : Screen("exercise_detail/{exerciseId}") {
        fun createRoute(exerciseId: Int) = "exercise_detail/$exerciseId"
    }
    data object WorkoutPlan : Screen("workout_plan")
    data object WorkoutProgress : Screen("workout_progress")
    data object AiTrainerChat : Screen("ai_trainer_chat")
}


