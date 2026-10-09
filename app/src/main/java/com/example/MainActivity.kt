package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Lecture
import com.example.ui.components.AdMobInterstitialDialog
import com.example.ui.dialogs.AddEditLectureDialog
import com.example.ui.dialogs.PermissionsDialog
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.TimeTableTheme
import com.example.ui.viewmodel.TimetableViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TimetableViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val systemDark = isSystemInDarkTheme()
            var currentThemeMode by remember { mutableStateOf(viewModel.preferences.themeMode) }
            val isDark = when (currentThemeMode) {
                "dark" -> true
                "light" -> false
                else -> systemDark
            }

            TimeTableTheme(darkTheme = isDark) {
                MainAppNavHost(
                    viewModel = viewModel,
                    onThemeChanged = { currentThemeMode = it }
                )
            }
        }
    }
}

enum class ScreenState {
    ONBOARDING,
    HOME,
    SETTINGS
}

@Composable
fun MainAppNavHost(
    viewModel: TimetableViewModel,
    onThemeChanged: (String) -> Unit
) {
    val prefs = viewModel.preferences
    var currentScreen by remember {
        mutableStateOf(
            if (prefs.hasSeenOnboarding) ScreenState.HOME else ScreenState.ONBOARDING
        )
    }

    var lectureToEdit by remember { mutableStateOf<Lecture?>(null) }
    var showAddEditDialog by remember { mutableStateOf(false) }
    var lectureToDelete by remember { mutableStateOf<Lecture?>(null) }
    var showPermissionsDialog by remember { mutableStateOf(false) }

    var hasNotificationPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val app = com.example.TimeTableApplication.instance
            val granted = ContextCompat.checkSelfPermission(
                app,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            hasNotificationPermission = granted
            if (!granted) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            hasNotificationPermission = true
        }
    }

    val lectures by viewModel.lectures.collectAsStateWithLifecycle()
    val selectedDayFilter by viewModel.selectedDayFilter.collectAsStateWithLifecycle()
    val showInterstitial by viewModel.showInterstitialAd.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (currentScreen) {
            ScreenState.ONBOARDING -> {
                OnboardingScreen(
                    onFinish = {
                        prefs.hasSeenOnboarding = true
                        currentScreen = ScreenState.HOME
                    }
                )
            }

            ScreenState.HOME -> {
                HomeScreen(
                    lectures = lectures,
                    selectedDayFilter = selectedDayFilter,
                    onSelectDayFilter = { viewModel.setDayFilter(it) },
                    onAddNewLecture = {
                        lectureToEdit = null
                        showAddEditDialog = true
                    },
                    onEditLecture = { lecture ->
                        lectureToEdit = lecture
                        showAddEditDialog = true
                    },
                    onDeleteLecture = { lecture ->
                        lectureToDelete = lecture
                    },
                    onToggleLectureEnabled = { lecture, enabled ->
                        viewModel.toggleLectureEnabled(lecture, enabled)
                    },
                    onTestAlert = { lecture ->
                        viewModel.triggerTestAlert(lecture)
                    },
                    onOpenSettings = {
                        currentScreen = ScreenState.SETTINGS
                    },
                    onOpenPermissions = {
                        showPermissionsDialog = true
                    },
                    hasMissingPermissions = !hasNotificationPermission
                )
            }

            ScreenState.SETTINGS -> {
                SettingsScreen(
                    preferences = prefs,
                    onTestAlert = {
                        viewModel.triggerTestAlert(lectures.firstOrNull())
                    },
                    onNavigateBack = {
                        currentScreen = ScreenState.HOME
                    },
                    onThemeChanged = onThemeChanged
                )
            }
        }

        // Add / Edit Lecture Modal
        if (showAddEditDialog) {
            AddEditLectureDialog(
                lectureToEdit = lectureToEdit,
                onDismiss = {
                    showAddEditDialog = false
                    lectureToEdit = null
                },
                onSave = { subject, hour, minute, day, room, colorHex, emoji, repeatType, customDays ->
                    viewModel.addOrUpdateLecture(
                        id = lectureToEdit?.id ?: 0L,
                        subject = subject,
                        hour = hour,
                        minute = minute,
                        day = day,
                        room = room,
                        colorHex = colorHex,
                        categoryEmoji = emoji,
                        repeatType = repeatType,
                        customDays = customDays
                    )
                    showAddEditDialog = false
                    lectureToEdit = null
                }
            )
        }

        // Delete Confirmation Modal
        lectureToDelete?.let { lecture ->
            AlertDialog(
                onDismissRequest = { lectureToDelete = null },
                title = { Text("Delete Lecture?", fontWeight = FontWeight.Bold) },
                text = {
                    Text("Are you sure you want to delete '${lecture.subject}'? Its scheduled alarm will also be canceled.")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteLecture(lecture)
                            lectureToDelete = null
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.testTag("confirm_delete_button")
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { lectureToDelete = null },
                        modifier = Modifier.testTag("cancel_delete_button")
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Permissions Dialog
        if (showPermissionsDialog) {
            PermissionsDialog(
                onRequestNotificationPermission = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                },
                hasNotificationPermission = hasNotificationPermission,
                onDismiss = { showPermissionsDialog = false }
            )
        }

        // Interstitial Ad Dialog
        if (showInterstitial) {
            AdMobInterstitialDialog(
                onDismiss = { viewModel.dismissInterstitialAd() }
            )
        }
    }
}
