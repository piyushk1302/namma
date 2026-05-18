package com.nammarailu.app

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nammarailu.app.ui.screens.*
import com.nammarailu.app.util.Routes
import com.nammarailu.app.viewmodel.MainViewModel

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    viewModel: MainViewModel         = viewModel()
) {
    NavHost(
        navController    = navController,
        startDestination = Routes.STATION_SELECT
    ) {

        composable(Routes.STATION_SELECT) {
            StationSelectionScreen(
                viewModel         = viewModel,
                onStationSelected = { station ->
                    viewModel.selectStation(station)
                    navController.navigate(Routes.DASHBOARD)
                }
            )
        }

        composable(Routes.DASHBOARD) {
            DashboardScreen(
                viewModel          = viewModel,
                onTrainSelected    = { train ->
                    viewModel.selectTrain(train)
                    navController.navigate(Routes.COACH_LAYOUT)
                },
                onSetDestination   = { navController.navigate(Routes.DESTINATION) },
                onOpenAI           = { navController.navigate(Routes.AI_ASSISTANT) },
                onOpenPlatformPing = { navController.navigate(Routes.PLATFORM_PING) }
            )
        }

        composable(Routes.COACH_LAYOUT) {
            CoachLayoutScreen(
                viewModel      = viewModel,
                onBack         = { navController.popBackStack() },
                onPlatformPing = { navController.navigate(Routes.PLATFORM_PING) }
            )
        }

        composable(Routes.PLATFORM_PING) {
            PlatformPingScreen(
                viewModel = viewModel,
                onBack    = { navController.popBackStack() }
            )
        }

        composable(Routes.AI_ASSISTANT) {
            AiAssistantScreen(
                viewModel = viewModel,
                onBack    = { navController.popBackStack() }
            )
        }

        composable(Routes.DESTINATION) {
            DestinationAlarmScreen(
                viewModel = viewModel,
                onBack    = { navController.popBackStack() }
            )
        }
    }
}
