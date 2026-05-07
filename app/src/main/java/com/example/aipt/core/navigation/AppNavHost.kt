package com.example.aipt.core.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.aipt.feature.chat.presentation.AiTrainerChatRoute
import com.example.aipt.feature.dashboard.presentation.TodayDashboardRoute
import com.example.aipt.feature.exercise.presentation.detail.ExerciseDetailRoute
import com.example.aipt.feature.exercise.presentation.list.ExerciseListRoute
import com.example.aipt.feature.home.presentation.MainMenuRoute
import com.example.aipt.feature.profile.presentation.BasicInfoRoute
import com.example.aipt.feature.profile.presentation.GymEquipmentRoute
import com.example.aipt.feature.profile.presentation.InBodyRoute
import com.example.aipt.feature.profile.presentation.PreferencesRoute
import com.example.aipt.feature.profile.presentation.ProfileManagerRoute
import com.example.aipt.feature.profile.presentation.ProfileSetupViewModel
import com.example.aipt.feature.profile.presentation.TrainingGoalRoute
import com.example.aipt.feature.workout.presentation.ProgressTrackingRoute
import com.example.aipt.feature.workout.presentation.WorkoutPlanRoute
import com.example.aipt.feature.workout.presentation.WorkoutSessionRoute

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Startup.route,
    ) {
        composable(Screen.Startup.route) {
            StartupRoute(
                onProfileMissing = {
                    navController.navigate(Screen.ProfileFlow.route) {
                        popUpTo(Screen.Startup.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onProfileReady = {
                    navController.navigate(Screen.TodayDashboard.route) {
                        popUpTo(Screen.Startup.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
        navigation(
            startDestination = Screen.BasicInfo.route,
            route = Screen.ProfileFlow.route,
        ) {
            composable(Screen.BasicInfo.route) {
                val parentEntry = navController.getBackStackEntry(Screen.ProfileFlow.route)
                val viewModel = hiltViewModel<ProfileSetupViewModel>(parentEntry)
                BasicInfoRoute(
                    viewModel = viewModel,
                    onNext = { navController.navigate(Screen.InBody.route) },
                )
            }
            composable(Screen.InBody.route) {
                val parentEntry = navController.getBackStackEntry(Screen.ProfileFlow.route)
                val viewModel = hiltViewModel<ProfileSetupViewModel>(parentEntry)
                InBodyRoute(
                    viewModel = viewModel,
                    onBack = navController::popBackStack,
                    onNext = { navController.navigate(Screen.TrainingGoal.route) },
                )
            }
            composable(Screen.TrainingGoal.route) {
                val parentEntry = navController.getBackStackEntry(Screen.ProfileFlow.route)
                val viewModel = hiltViewModel<ProfileSetupViewModel>(parentEntry)
                TrainingGoalRoute(
                    viewModel = viewModel,
                    onBack = navController::popBackStack,
                    onNext = { navController.navigate(Screen.Preferences.route) },
                )
            }
            composable(Screen.Preferences.route) {
                val parentEntry = navController.getBackStackEntry(Screen.ProfileFlow.route)
                val viewModel = hiltViewModel<ProfileSetupViewModel>(parentEntry)
                PreferencesRoute(
                    viewModel = viewModel,
                    onBack = navController::popBackStack,
                    onNext = { navController.navigate(Screen.GymEquipment.route) },
                )
            }
            composable(Screen.GymEquipment.route) {
                val parentEntry = navController.getBackStackEntry(Screen.ProfileFlow.route)
                val viewModel = hiltViewModel<ProfileSetupViewModel>(parentEntry)
                GymEquipmentRoute(
                    viewModel = viewModel,
                    onBack = navController::popBackStack,
                    onFinish = {
                        navController.navigate(Screen.WorkoutPlan.route) {
                            launchSingleTop = true
                        }
                    },
                )
            }
        }
        composable(Screen.TodayDashboard.route) {
            TodayDashboardRoute(
                onBackClick = { navController.navigate(Screen.MainMenu.route) { launchSingleTop = true } },
                onCreatePlanClick = { navController.navigate(Screen.WorkoutPlan.route) },
                onStartWorkoutClick = { navController.navigate(Screen.WorkoutSession.route) },
                onProfileClick = { navController.navigate(Screen.ProfileManager.route) },
            )
        }
        composable(Screen.ProfileManager.route) {
            ProfileManagerRoute(
                onBack = navController::popBackStack,
                onEditProfile = { navController.navigate(Screen.ProfileFlow.route) },
                onCreateProfile = { navController.navigate(Screen.ProfileFlow.route) },
                onCreateWorkoutPlan = { navController.navigate(Screen.WorkoutPlan.route) },
            )
        }
        composable(Screen.MainMenu.route) {
            MainMenuRoute(
                onDashboardClick = { navController.navigate(Screen.TodayDashboard.route) },
                onCreatePlanClick = { navController.navigate(Screen.WorkoutPlan.route) },
                onTrackProgressClick = { navController.navigate(Screen.WorkoutProgress.route) },
                onExerciseLibraryClick = { navController.navigate(Screen.ExerciseList.route) },
                onAiTrainerChatClick = { navController.navigate(Screen.AiTrainerChat.route) },
                onProfileClick = { navController.navigate(Screen.ProfileManager.route) },
            )
        }
        composable(Screen.ExerciseList.route) {
            ExerciseListRoute(
                onProfileClick = { navController.navigate(Screen.ProfileManager.route) },
                onWorkoutPlanClick = { navController.navigate(Screen.WorkoutPlan.route) },
                onProgressClick = { navController.navigate(Screen.WorkoutProgress.route) },
                onExerciseClick = { exerciseId ->
                    navController.navigate(Screen.ExerciseDetail.createRoute(exerciseId))
                },
            )
        }
        composable(Screen.WorkoutPlan.route) {
            WorkoutPlanRoute(
                onBackClick = navController::popBackStack,
                onTrackProgressClick = { navController.navigate(Screen.WorkoutProgress.route) },
            )
        }
        composable(Screen.WorkoutProgress.route) {
            ProgressTrackingRoute(onBackClick = navController::popBackStack)
        }
        composable(Screen.WorkoutSession.route) {
            WorkoutSessionRoute(
                onBackClick = navController::popBackStack,
                onFinishClick = {
                    navController.navigate(Screen.TodayDashboard.route) {
                        popUpTo(Screen.TodayDashboard.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(Screen.AiTrainerChat.route) {
            AiTrainerChatRoute(onBackClick = navController::popBackStack)
        }
        composable(
            route = Screen.ExerciseDetail.route,
            arguments = listOf(navArgument("exerciseId") { type = NavType.IntType }),
        ) {
            ExerciseDetailRoute(onBackClick = navController::popBackStack)
        }
    }
}



