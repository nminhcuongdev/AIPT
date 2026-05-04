package com.example.aipt.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.aipt.feature.exercise.presentation.detail.ExerciseDetailRoute
import com.example.aipt.feature.exercise.presentation.list.ExerciseListRoute

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.ExerciseList.route,
    ) {
        composable(Screen.ExerciseList.route) {
            ExerciseListRoute(
                onExerciseClick = { exerciseId ->
                    navController.navigate(Screen.ExerciseDetail.createRoute(exerciseId))
                },
            )
        }
        composable(
            route = Screen.ExerciseDetail.route,
            arguments = listOf(navArgument("exerciseId") { type = NavType.IntType }),
        ) {
            ExerciseDetailRoute(onBackClick = navController::popBackStack)
        }
    }
}
