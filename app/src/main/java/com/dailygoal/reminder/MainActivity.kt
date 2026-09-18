package com.dailygoal.reminder

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.dailygoal.reminder.ui.animation.AimlyMotionSpecs
import com.dailygoal.reminder.ui.navigation.NavGraph
import com.dailygoal.reminder.ui.navigation.Screen
import com.dailygoal.reminder.ui.theme.DailyGoalReminderTheme
import com.dailygoal.reminder.ui.viewmodel.GoalViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: GoalViewModel by viewModels()
    private var pendingDeepLinkRoute by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIncomingIntent(intent)

        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val isDark = when (themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> isSystemInDarkTheme()
            }

            DailyGoalReminderTheme(darkTheme = isDark) {
                val animatedBgColor by animateColorAsState(
                    targetValue = MaterialTheme.colorScheme.background,
                    animationSpec = AimlyMotionSpecs.contentTween(),
                    label = "ThemeBackgroundAnim"
                )

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = animatedBgColor
                ) {
                    val navController = rememberNavController()

                    LaunchedEffect(pendingDeepLinkRoute) {
                        pendingDeepLinkRoute?.let { route ->
                            try {
                                navController.navigate(route) {
                                    launchSingleTop = true
                                }
                            } catch (e: Exception) {
                                Log.w(TAG, "Navigation failed for deep link route: $route", e)
                            }
                            pendingDeepLinkRoute = null
                        }
                    }

                    NavGraph(navController = navController, viewModel = viewModel)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent == null) return

        val deepLinkStr = intent.getStringExtra("deep_link") ?: intent.dataString
        val goalIdExtra = intent.getLongExtra("navigation_goal_id", -1L)

        Log.d(TAG, "Handling incoming intent: deepLinkStr=$deepLinkStr, goalIdExtra=$goalIdExtra")

        if (goalIdExtra != -1L) {
            pendingDeepLinkRoute = Screen.GoalDetail.createRoute(goalIdExtra)
            return
        }

        if (!deepLinkStr.isNullOrBlank()) {
            val uri = Uri.parse(deepLinkStr)
            val route = parseDeepLinkUri(uri)
            if (route != null) {
                pendingDeepLinkRoute = route
            }
        }
    }

    private fun parseDeepLinkUri(uri: Uri): String? {
        val host = uri.host?.lowercase() ?: uri.path?.lowercase()
        val pathSegments = uri.pathSegments

        return when {
            host == "home" -> Screen.Home.route
            host == "history" -> Screen.History.route
            host == "statistics" -> Screen.Statistics.route
            host == "settings" || host == "notification_settings" -> Screen.NotificationSettings.route
            host == "goal" && pathSegments.isNotEmpty() -> {
                val goalId = pathSegments[0].toLongOrNull()
                if (goalId != null) Screen.GoalDetail.createRoute(goalId) else Screen.Home.route
            }
            else -> null
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
