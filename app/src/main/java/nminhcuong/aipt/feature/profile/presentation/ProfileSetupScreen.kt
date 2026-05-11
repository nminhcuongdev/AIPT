package nminhcuong.aipt.feature.profile.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import nminhcuong.aipt.feature.profile.presentation.components.BasicInfoScreen
import nminhcuong.aipt.feature.profile.presentation.components.GymEquipmentScreen
import nminhcuong.aipt.feature.profile.presentation.components.InBodyScreen
import nminhcuong.aipt.feature.profile.presentation.components.PreferencesScreen
import nminhcuong.aipt.feature.profile.presentation.components.ProfileManagerScreen
import nminhcuong.aipt.feature.profile.presentation.components.TrainingGoalScreen

@Composable
fun ProfileManagerRoute(
    onBack: () -> Unit,
    onEditProfile: () -> Unit,
    onCreateProfile: () -> Unit,
    onCreateWorkoutPlan: () -> Unit,
    viewModel: ProfileSetupViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    ProfileManagerScreen(
        state = state,
        onBack = onBack,
        onEditProfile = onEditProfile,
        onCreateProfile = onCreateProfile,
        onCreateWorkoutPlan = onCreateWorkoutPlan,
        onDeleteProfile = viewModel::onDeleteProfile,
    )
}

@Composable
fun BasicInfoRoute(onNext: () -> Unit, viewModel: ProfileSetupViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    BasicInfoScreen(
        state = state,
        onNameChanged = viewModel::onNameChanged,
        onAgeChanged = viewModel::onAgeChanged,
        onHeightChanged = viewModel::onHeightChanged,
        onWeightChanged = viewModel::onWeightChanged,
        onNext = onNext,
    )
}

@Composable
fun InBodyRoute(onBack: () -> Unit, onNext: () -> Unit, viewModel: ProfileSetupViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    InBodyScreen(
        state = state,
        onBodyFatPercentChanged = viewModel::onBodyFatPercentChanged,
        onSkeletalMuscleMassChanged = viewModel::onSkeletalMuscleMassChanged,
        onBodyWaterLitersChanged = viewModel::onBodyWaterLitersChanged,
        onVisceralFatLevelChanged = viewModel::onVisceralFatLevelChanged,
        onBasalMetabolicRateChanged = viewModel::onBasalMetabolicRateChanged,
        onWaistHipRatioChanged = viewModel::onWaistHipRatioChanged,
        onLeftArmMuscleChanged = viewModel::onLeftArmMuscleChanged,
        onRightArmMuscleChanged = viewModel::onRightArmMuscleChanged,
        onTrunkMuscleChanged = viewModel::onTrunkMuscleChanged,
        onLeftLegMuscleChanged = viewModel::onLeftLegMuscleChanged,
        onRightLegMuscleChanged = viewModel::onRightLegMuscleChanged,
        onBack = onBack,
        onNext = onNext,
    )
}

@Composable
fun TrainingGoalRoute(onBack: () -> Unit, onNext: () -> Unit, viewModel: ProfileSetupViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    TrainingGoalScreen(
        selectedGoal = state.selectedGoal,
        onGoalSelected = viewModel::onGoalSelected,
        onBack = onBack,
        onNext = onNext,
    )
}

@Composable
fun PreferencesRoute(onBack: () -> Unit, onNext: () -> Unit, viewModel: ProfileSetupViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    PreferencesScreen(
        state = state,
        onDaysPerWeekChanged = viewModel::onDaysPerWeekChanged,
        onSessionDurationChanged = viewModel::onSessionDurationChanged,
        onExperienceLevelSelected = viewModel::onExperienceLevelSelected,
        onInjuriesChanged = viewModel::onInjuriesChanged,
        onPreferredLanguageSelected = viewModel::onPreferredLanguageSelected,
        onBack = onBack,
        onNext = onNext,
    )
}

@Composable
fun GymEquipmentRoute(onBack: () -> Unit, onFinish: () -> Unit, viewModel: ProfileSetupViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    GymEquipmentScreen(
        state = state,
        onEquipmentSwiped = viewModel::onEquipmentSwiped,
        onResetEquipment = viewModel::onResetEquipment,
        onSaveProfile = viewModel::onSaveProfile,
        onBack = onBack,
        onFinish = onFinish,
    )
}
