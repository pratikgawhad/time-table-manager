package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Lecture
import com.example.data.preferences.SettingsPreferences
import com.example.ui.components.AdMobBannerCard
import com.example.ui.dialogs.AddEditLectureDialog
import com.example.ui.dialogs.PermissionsDialog
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PrivacyPolicyScreen
import com.example.ui.screens.ScheduleScreen
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

enum class MainTab {
    TODAY,
    SCHEDULE,
    SETTINGS
}

enum class ModalScreen {
    NONE,
    PRIVACY_POLICY
}

@Composable
fun MainAppNavHost(
    viewModel: TimetableViewModel,
    onThemeChanged: (String) -> Unit
) {
    val prefs: SettingsPreferences = viewModel.preferences
    var hasSeenOnboarding by remember { mutableStateOf(prefs.hasSeenOnboarding) }
    var currentTab by remember { mutableStateOf(MainTab.TODAY) }
    var currentModal by remember { mutableStateOf(ModalScreen.NONE) }

    var showAddEditDialog by remember { mutableStateOf(false) }
    var lectureToEdit by remember { mutableStateOf<Lecture?>(null) }
    var lectureToDelete by remember { mutableStateOf<Lecture?>(null) }
    var showPermissionsDialog by remember { mutableStateOf(false) }

    var hasNotificationPermission by remember { mutableStateOf(true) }
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
    val allLectures by viewModel.allLectures.collectAsStateWithLifecycle()
    val selectedDayFilter by viewModel.selectedDayFilter.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (!hasSeenOnboarding) {
            OnboardingScreen(
                onFinish = {
                    prefs.hasSeenOnboarding = true
                    hasSeenOnboarding = true
                }
            )
        } else {
            when (currentModal) {
                ModalScreen.PRIVACY_POLICY -> {
                    BackHandler { currentModal = ModalScreen.NONE }
                    PrivacyPolicyScreen(
                        onNavigateBack = { currentModal = ModalScreen.NONE }
                    )
                }

                ModalScreen.NONE -> {
                    if (currentTab != MainTab.TODAY) {
                        BackHandler { currentTab = MainTab.TODAY }
                    }

                    Scaffold(
                        bottomBar = {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                AdMobBannerCard()
                                NavigationBar(
                                    containerColor = Color.White,
                                    tonalElevation = 8.dp
                                ) {
                                    NavigationBarItem(
                                        icon = { Icon(Icons.Default.Today, contentDescription = "Today") },
                                        label = { Text("Today") },
                                        selected = currentTab == MainTab.TODAY,
                                        onClick = { currentTab = MainTab.TODAY },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = Color(0xFF6366F1),
                                            selectedTextColor = Color(0xFF6366F1),
                                            indicatorColor = Color(0xFFEEF2FF),
                                            unselectedIconColor = Color(0xFF64748B),
                                            unselectedTextColor = Color(0xFF64748B)
                                        ),
                                        modifier = Modifier.testTag("tab_today")
                                    )
                                    NavigationBarItem(
                                        icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Schedule") },
                                        label = { Text("Schedule") },
                                        selected = currentTab == MainTab.SCHEDULE,
                                        onClick = { currentTab = MainTab.SCHEDULE },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = Color(0xFF6366F1),
                                            selectedTextColor = Color(0xFF6366F1),
                                            indicatorColor = Color(0xFFEEF2FF),
                                            unselectedIconColor = Color(0xFF64748B),
                                            unselectedTextColor = Color(0xFF64748B)
                                        ),
                                        modifier = Modifier.testTag("tab_schedule")
                                    )
                                    NavigationBarItem(
                                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                                        label = { Text("Settings") },
                                        selected = currentTab == MainTab.SETTINGS,
                                        onClick = { currentTab = MainTab.SETTINGS },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = Color(0xFF6366F1),
                                            selectedTextColor = Color(0xFF6366F1),
                                            indicatorColor = Color(0xFFEEF2FF),
                                            unselectedIconColor = Color(0xFF64748B),
                                            unselectedTextColor = Color(0xFF64748B)
                                        ),
                                        modifier = Modifier.testTag("tab_settings")
                                    )
                                }
                            }
                        }
                    ) { paddingValues ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        ) {
                            when (currentTab) {
                                MainTab.TODAY -> {
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
                                            currentTab = MainTab.SETTINGS
                                        },
                                        onOpenPermissions = {
                                            showPermissionsDialog = true
                                        },
                                        onLoadSampleData = {
                                            viewModel.loadSampleData()
                                        },
                                        hasMissingPermissions = !hasNotificationPermission
                                    )
                                }

                                MainTab.SCHEDULE -> {
                                    ScheduleScreen(
                                        lectures = allLectures,
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
                                        }
                                    )
                                }

                                MainTab.SETTINGS -> {
                                    SettingsScreen(
                                        preferences = prefs,
                                        onTestAlert = {
                                            viewModel.triggerTestAlert(allLectures.firstOrNull())
                                        },
                                        onNavigateBack = {
                                            currentTab = MainTab.TODAY
                                        },
                                        onLoadSampleData = {
                                            viewModel.loadSampleData()
                                        },
                                        onOpenPrivacyPolicy = {
                                            currentModal = ModalScreen.PRIVACY_POLICY
                                        },
                                        onThemeChanged = onThemeChanged
                                    )
                                }
                            }
                        }
                    }
                }
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

        // Delete Lecture Confirmation Dialog
        lectureToDelete?.let { lecture ->
            AlertDialog(
                onDismissRequest = { lectureToDelete = null },
                title = { Text("Delete Lecture") },
                text = { Text("Are you sure you want to remove '${lecture.subject}' from your timetable?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteLecture(lecture)
                            lectureToDelete = null
                        }
                    ) {
                        Text("Delete", color = Color(0xFFEF4444))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { lectureToDelete = null }) {
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
    }
}
