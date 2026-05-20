package com.project.vacationplanner.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.project.vacationplanner.ui.screens.*
import com.project.vacationplanner.viewmodel.AuthUiState
import com.project.vacationplanner.viewmodel.AuthViewModel

// Маршруты навигации
object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val ROLE_SELECTION = "role_selection"
    const val HOME_EMPLOYER = "home_employer"
    const val HOME_EMPLOYEE = "home_employee"

    const val HOME_EMPLOYEE_REQUESTS = "home_employee_requests"

    const val HOME_EMPLOYER_REQUESTS = "home_employer_requests"  // ← добавить

    const val PROFILE = "profile"
    const val STATISTICS = "statistics"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val uiState by authViewModel.uiState.collectAsState()
    val userRole by authViewModel.userRole.collectAsState() // ← сюда

    LaunchedEffect(Unit) {
        authViewModel.checkAuth()
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.Success -> {
                val role = (uiState as AuthUiState.Success).role
                if (role == "EMPLOYER") {
                    navController.navigate(Routes.HOME_EMPLOYER) {
                        popUpTo(0) { inclusive = true }
                    }
                } else {
                    navController.navigate(Routes.HOME_EMPLOYEE) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            else -> {}
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginClick = { email, password, _ ->
                    authViewModel.login(email, password)
                },
                onRegisterClick = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterClick = { name, email, password, _, _ ->
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("registerData", "$name|$email|$password")
                    navController.navigate(Routes.ROLE_SELECTION)
                },
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.ROLE_SELECTION) {
            val data = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<String>("registerData") ?: ""
            val parts = data.split("|")
            val name = parts.getOrElse(0) { "" }
            val email = parts.getOrElse(1) { "" }
            val password = parts.getOrElse(2) { "" }

            RoleSelectionScreen(
                onBackClick = { navController.popBackStack() },
                onSelectRole = { isEmployer ->
                    val role = if (isEmployer) "EMPLOYER" else "EMPLOYEE"
                    authViewModel.register(email, password, name, role)
                }
            )
        }

        composable(Routes.HOME_EMPLOYER) {
            HomeEmployerScreen(
                onMenuClick = {},
                onBellClick = {},
                onMoreClick = {},
                onTabNavClick = { tab ->
                    when (tab) {
                        1 -> navController.navigate(Routes.STATISTICS)
                        2 -> navController.navigate(Routes.PROFILE)
                    }
                }
            )
        }

        composable(Routes.HOME_EMPLOYEE) {
            HomeEmployeeScreen(
                onBellClick = {},
                onMoreClick = {},
                onTabNavClick = { tab ->
                    when (tab) {
                        2 -> navController.navigate(Routes.PROFILE)
                    }
                }
            )
        }

        composable(Routes.HOME_EMPLOYEE_REQUESTS) {
            HomeEmployeeScreen(
                initialTab = 1,
                onBellClick = {},
                onMoreClick = {},
                onTabNavClick = { tab ->
                    when (tab) {
                        2 -> navController.navigate(Routes.PROFILE)
                    }
                }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                currentTab = 3,  // ← было 2, Профиль = индекс 3
                onBackClick = { navController.popBackStack() },
                onLogoutClick = {
                    authViewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onTabClick = { tab ->
                    when (tab) {
                        0 -> {
                            val dest = if (userRole == "EMPLOYER") Routes.HOME_EMPLOYER else Routes.HOME_EMPLOYEE
                            navController.navigate(dest) { popUpTo(0) { inclusive = true } }
                        }
                        1 -> {
                            if (userRole == "EMPLOYER") {
                                navController.navigate(Routes.HOME_EMPLOYER) { popUpTo(0) { inclusive = true } }
                            } else {
                                navController.navigate(Routes.HOME_EMPLOYEE_REQUESTS) { popUpTo(0) { inclusive = true } }
                            }
                        }
                        2 -> {
                            navController.navigate(Routes.STATISTICS) { popUpTo(0) { inclusive = true } }
                        }
                        3 -> { /* уже на профиле */ }
                    }
                }
            )
        }


        composable(Routes.HOME_EMPLOYER_REQUESTS) {
            HomeEmployerScreen(
                initialTab = 1,
                onTabNavClick = { tab ->
                    when (tab) {
                        1 -> navController.navigate(Routes.STATISTICS)
                        2 -> navController.navigate(Routes.PROFILE)
                    }
                }
            )
        }

        composable(Routes.STATISTICS) {
            StatisticsScreen(
                onTabNavClick = { tab ->
                    when (tab) {
                        0 -> navController.navigate(Routes.HOME_EMPLOYER) {
                            popUpTo(0) { inclusive = true }
                        }
                        1 -> navController.navigate(Routes.HOME_EMPLOYER_REQUESTS) {  // ← исправить
                            popUpTo(0) { inclusive = true }
                        }
                        3 -> navController.navigate(Routes.PROFILE)
                    }
                }
            )
        }

    }
}