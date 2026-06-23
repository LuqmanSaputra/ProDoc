package com.prodoc.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.prodoc.ProDocApplication
import com.prodoc.navigation.Screens
import com.prodoc.ui.auth.LoginScreen
import com.prodoc.ui.auth.RegisterScreen
import com.prodoc.ui.auth.AuthViewModel
import com.prodoc.ui.auth.AuthViewModelFactory
import com.prodoc.ui.dashboard.DashboardScreen
import com.prodoc.ui.dashboard.DashboardViewModel
import com.prodoc.ui.dashboard.DashboardViewModelFactory
import com.prodoc.ui.project.ProjectDetailScreen
import com.prodoc.ui.project.*
import com.prodoc.ui.project.ProjectDetailViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val appContainer = (application as ProDocApplication)
                    val repository = appContainer.projectRepository
                    val database = appContainer.database

                    val authViewModel: AuthViewModel by viewModels {
                        AuthViewModelFactory(database.userDao())
                    }

                    val dashboardViewModel: DashboardViewModel by viewModels {
                        DashboardViewModelFactory(repository, database)
                    }

                    val initialRoute = if (authViewModel.isUserLoggedIn) Screens.Dashboard else Screens.Auth

                    NavHost(
                        navController = navController,
                        startDestination = initialRoute
                    ) {
                        composable<Screens.Auth> {
                            var isRegisterMode by remember { mutableStateOf(false) }

                            if (isRegisterMode) {
                                RegisterScreen(
                                    viewModel = authViewModel,
                                    onRegisterSuccess = {
                                        navController.navigate(Screens.Dashboard) {
                                            popUpTo(Screens.Auth) { inclusive = true }
                                        }
                                    },
                                    onNavigateToLogin = { isRegisterMode = false }
                                )
                            } else {
                                LoginScreen(
                                    viewModel = authViewModel,
                                    onLoginSuccess = {
                                        navController.navigate(Screens.Dashboard) {
                                            popUpTo(Screens.Auth) { inclusive = true }
                                        }
                                    },
                                    onNavigateToRegister = { isRegisterMode = true }
                                )
                            }
                        }

                        composable<Screens.Dashboard> {
                            DashboardScreen(
                                viewModel = dashboardViewModel,
                                onProjectClick = { id ->
                                    navController.navigate(Screens.ProjectDetail(projectId = id))
                                },
                                onSignOut = {
                                    authViewModel.resetAuthState()

                                    navController.navigate(Screens.Auth) {
                                        popUpTo(Screens.Dashboard) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable<Screens.ProjectDetail> { backStackEntry ->
                            val routeData = backStackEntry.toRoute<Screens.ProjectDetail>()

                            val detailViewModel: ProjectDetailViewModel = viewModel(
                                factory = ProjectDetailViewModelFactory(repository, routeData.projectId, database)
                            )

                            ProjectDetailScreen(
                                viewModel = detailViewModel,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}