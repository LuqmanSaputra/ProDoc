package com.prodoc.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import androidx.navigation.toRoute
import com.prodoc.ProDocApplication
import com.prodoc.navigation.Screens
import com.prodoc.ui.auth.AuthState
import com.prodoc.ui.auth.AuthViewModel
import com.prodoc.ui.auth.AuthViewModelFactory
import com.prodoc.ui.auth.LoginScreen
import com.prodoc.ui.auth.RegisterScreen
import com.prodoc.ui.dashboard.*
import com.prodoc.ui.project.*
import com.prodoc.ui.project.detail.*

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
                        AuthViewModelFactory(repository)
                    }

                    val dashboardViewModel: DashboardViewModel by viewModels {
                        DashboardViewModelFactory(repository, database)
                    }

                    val authState by authViewModel.authState.collectAsState()

                    var lastProcessedAuthState by androidx.compose.runtime.saveable.rememberSaveable {
                        mutableStateOf<String?>(null)
                    }

                    LaunchedEffect(authState) {
                        Log.d("ProDoc", "[MainActivity] Reaksi Konteks Navigasi Terhadap Perubahan AuthState: $authState")

                        if (lastProcessedAuthState != authState.name) {
                            when (authState) {
                                AuthState.LOGGED_IN -> {
                                    if (navController.currentDestination?.route == Screens.Auth::class.qualifiedName) {
                                        navController.navigate(Screens.Dashboard) {
                                            popUpTo(Screens.Auth) { inclusive = true }
                                        }
                                    }
                                }
                                AuthState.LOGGED_OUT -> {
                                    if (navController.currentDestination?.route != Screens.Auth::class.qualifiedName) {
                                        navController.navigate(Screens.Auth) {
                                            popUpTo(navController.graph.id) {
                                                inclusive = true
                                            }
                                        }
                                    }
                                }
                            }
                            lastProcessedAuthState = authState.name
                        }
                    }

                    NavHost(
                        navController = navController,
                        startDestination = Screens.Auth
                    ) {
                        composable<Screens.Auth> {
                            var showLogin by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(true) }
                            if (showLogin) {
                                LoginScreen(
                                    viewModel = authViewModel,
                                    onLoginSuccess = {
                                        navController.navigate(Screens.Dashboard) {
                                            popUpTo(Screens.Auth) { inclusive = true }
                                        }
                                    },
                                    onNavigateToRegister = { showLogin = false }
                                )
                            } else {
                                RegisterScreen(
                                    viewModel = authViewModel,
                                    onRegisterSuccess = {
                                        navController.navigate(Screens.Dashboard) {
                                            popUpTo(Screens.Auth) { inclusive = true }
                                        }
                                    },
                                    onNavigateToLogin = { showLogin = true }
                                )
                            }
                        }

                        composable<Screens.Dashboard> {
                            DashboardScreen(
                                viewModel = dashboardViewModel,
                                onProjectClick = { projectId ->
                                    navController.navigate(Screens.ProjectDetail(projectId))
                                },
                                onSignOut = {
                                    authViewModel.logout()
                                }
                            )
                        }

                        composable<Screens.ProjectDetail> { backStackEntry ->
                            val routeData = backStackEntry.toRoute<Screens.ProjectDetail>()
                            val projectDetailViewModel: ProjectDetailViewModel = viewModel(
                                factory = ProjectDetailViewModelFactory(repository, routeData.projectId, database)
                            )
                            ProjectDetailScreen(
                                viewModel = projectDetailViewModel,
                                onBackClick = { navController.popBackStack() },
                                onMaterialClick = { materialId -> navController.navigate(Screens.MaterialDetail(materialId)) },
                                onLogicClick = { logicId -> navController.navigate(Screens.LogicDetail(logicId)) },
                                onDiagramClick = { diagramId -> navController.navigate(Screens.DiagramDetail(diagramId)) },
                                onProjectClick = { projectId -> navController.navigate(Screens.ProjectDetail(projectId)) }
                            )
                        }

                        composable<Screens.DiagramDetail> { backStackEntry ->
                            val routeData = backStackEntry.toRoute<Screens.DiagramDetail>()
                            val diagramDetailViewModel: DiagramDetailViewModel = viewModel(
                                factory = DiagramDetailViewModelFactory(repository, database, routeData.diagramId)
                            )
                            DiagramDetailScreen(
                                viewModel = diagramDetailViewModel,
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        composable<Screens.LogicDetail> { backStackEntry ->
                            val routeData = backStackEntry.toRoute<Screens.LogicDetail>()
                            val logicDetailViewModel: LogicDetailViewModel = viewModel(
                                factory = LogicDetailViewModelFactory(repository, database, routeData.logicId)
                            )
                            LogicDetailScreen(
                                viewModel = logicDetailViewModel,
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        composable<Screens.MaterialDetail> { backStackEntry ->
                            val routeData = backStackEntry.toRoute<Screens.MaterialDetail>()
                            val materialDetailViewModel: MaterialDetailViewModel = viewModel(
                                factory = MaterialDetailViewModelFactory(repository, database, routeData.materialId)
                            )
                            MaterialDetailScreen(
                                modifier = Modifier,
                                viewModel = materialDetailViewModel,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}