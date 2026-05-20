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
import androidx.compose.ui.platform.LocalContext
import com.project.vacationplanner.data.TokenManager
import com.project.vacationplanner.ui.model.EmployerStats
import com.project.vacationplanner.viewmodel.*
import androidx.compose.runtime.*
import com.project.vacationplanner.data.repository.TeamRepository
import kotlinx.coroutines.launch


// Маршруты навигации
object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val ROLE_SELECTION = "role_selection"
    const val HOME_EMPLOYER = "home_employer"
    const val HOME_EMPLOYEE = "home_employee"

    const val HOME_EMPLOYER_REQUESTS = "home_employer_requests"

    const val HOME_EMPLOYEE_REQUESTS = "home_employee_requests"

    const val PROFILE = "profile"
    const val STATISTICS = "statistics"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val uiState by authViewModel.uiState.collectAsState()
    val userRole by authViewModel.userRole.collectAsState()
    val context = LocalContext.current

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
                onRegisterClick = { name, email, password, position, companyCode ->
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("registerData", "$name|$email|$password|$position|$companyCode")
                    navController.navigate(Routes.ROLE_SELECTION)
                },
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.ROLE_SELECTION) {
            val scope = rememberCoroutineScope()
            val data = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<String>("registerData") ?: ""
            val parts = data.split("|")
            val name = parts.getOrElse(0) { "" }
            val email = parts.getOrElse(1) { "" }
            val password = parts.getOrElse(2) { "" }
            val position = parts.getOrElse(3) { "" }
            val companyCode = parts.getOrElse(4) { "" }


            RoleSelectionScreen(
                onBackClick = { navController.popBackStack() },
                onSelectRole = { isEmployer ->
                    val role = if (isEmployer) "EMPLOYER" else "EMPLOYEE"
                    authViewModel.register(email, password, name, role)
                    scope.launch {
                        TokenManager.savePosition(context, position)
                        if (companyCode.isNotBlank()) {
                            TeamRepository(context).joinTeam(companyCode)
                        }
                    }
                }
            )
        }

        composable(Routes.HOME_EMPLOYER) {
            val vacationVm: VacationViewModel = viewModel()
            val teamVm: TeamViewModel = viewModel()
            val teamVacations by vacationVm.teamVacations.collectAsState()
            val teamMembers by teamVm.teamMembers.collectAsState()
            var companyName by remember { mutableStateOf("") }

            LaunchedEffect(Unit) {
                companyName = TokenManager.getName(context) ?: ""
                vacationVm.loadTeamVacations()
                teamVm.loadTeamMembers()
            }

            HomeEmployerScreen(
                stats = EmployerStats(
                    employeesCount = teamMembers.size,
                    pendingCount = teamVacations.count { it.isNew },
                    approvedCount = teamVacations.count { !it.isNew },
                    totalDays = teamMembers.size * 28
                ),
                requests = teamVacations,
                team = teamMembers,
                companyName = companyName,
                onApprove = { id -> vacationVm.approveVacation(id) },
                onReject = { id -> vacationVm.rejectVacation(id) },
                onTabNavClick = { tab ->
                    when (tab) {
                        1 -> navController.navigate(Routes.STATISTICS)
                        2 -> navController.navigate(Routes.PROFILE)
                    }
                }
            )
        }

        composable(Routes.HOME_EMPLOYEE) {
            val vacationVm: VacationViewModel = viewModel()
            val myVacations by vacationVm.myVacations.collectAsState()
            val balance by vacationVm.balance.collectAsState()
            var userName by remember { mutableStateOf("") }

            LaunchedEffect(Unit) {
                userName = TokenManager.getName(context) ?: ""
                vacationVm.loadMyVacations()
                vacationVm.loadBalance()
            }

            HomeEmployeeScreen(
                stats = balance,
                requests = myVacations,
                userName = userName,
                onCancelRequest = { id -> vacationVm.cancelVacation(id) },
                onSubmitRequest = { start, end -> vacationVm.createVacation(start, end) },
                onTabNavClick = { tab ->
                    when (tab) {
                        2 -> navController.navigate(Routes.PROFILE)
                    }
                }
            )
        }

        composable(Routes.PROFILE) {
            var userName by remember { mutableStateOf("") }
            var userEmail by remember { mutableStateOf("") }
            var userPosition by remember { mutableStateOf("") }

            LaunchedEffect(Unit) {
                userName = TokenManager.getName(context) ?: ""
                userEmail = TokenManager.getEmail(context) ?: ""
                userPosition = TokenManager.getPosition(context) ?: ""
            }

            ProfileScreen(
                name = userName,
                email = userEmail,
                position = userPosition,
                currentTab = 3,
                onBackClick = { navController.popBackStack() },
                onLogoutClick = {
                    authViewModel.logout()
                    navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                },
                onTabClick = { tab ->
                    when (tab) {
                        0 -> {
                            val dest = if (userRole == "EMPLOYER") Routes.HOME_EMPLOYER else Routes.HOME_EMPLOYEE
                            navController.navigate(dest) { popUpTo(0) { inclusive = true } }
                        }
                        1 -> {
                            val dest = if (userRole == "EMPLOYER") Routes.HOME_EMPLOYER_REQUESTS else Routes.HOME_EMPLOYEE_REQUESTS
                            navController.navigate(dest) { popUpTo(0) { inclusive = true } }
                        }
                        2 -> navController.navigate(Routes.STATISTICS) { popUpTo(0) { inclusive = true } }
                        3 -> {}
                    }
                }
            )
        }

        composable(Routes.STATISTICS) {
            val statisticsVm: StatisticsViewModel = viewModel()
            val state by statisticsVm.state.collectAsState()

            LaunchedEffect(Unit) { statisticsVm.load() }

            StatisticsScreen(
                state = state,
                onTabNavClick = { tab ->
                    when (tab) {
                        0 -> navController.navigate(Routes.HOME_EMPLOYER) { popUpTo(0) { inclusive = true } }
                        1 -> navController.navigate(Routes.HOME_EMPLOYER_REQUESTS) { popUpTo(0) { inclusive = true } }
                        3 -> navController.navigate(Routes.PROFILE)
                    }
                }
            )
        }

        composable(Routes.HOME_EMPLOYER_REQUESTS) {
            val vacationVm: VacationViewModel = viewModel()
            val teamVm: TeamViewModel = viewModel()
            val teamVacations by vacationVm.teamVacations.collectAsState()
            val teamMembers by teamVm.teamMembers.collectAsState()

            LaunchedEffect(Unit) {
                vacationVm.loadTeamVacations()
                teamVm.loadTeamMembers()
            }

            HomeEmployerScreen(
                initialTab = 1,
                stats = EmployerStats(
                    employeesCount = teamMembers.size,
                    pendingCount = teamVacations.count { it.isNew },
                    approvedCount = teamVacations.count { !it.isNew },
                    totalDays = teamMembers.size * 28
                ),
                requests = teamVacations,
                team = teamMembers,
                onApprove = { id -> vacationVm.approveVacation(id) },
                onReject = { id -> vacationVm.rejectVacation(id) },
                onTabNavClick = { tab ->
                    when (tab) {
                        1 -> navController.navigate(Routes.STATISTICS)
                        2 -> navController.navigate(Routes.PROFILE)
                    }
                }
            )
        }

        composable(Routes.HOME_EMPLOYEE_REQUESTS) {
            val vacationVm: VacationViewModel = viewModel()
            val myVacations by vacationVm.myVacations.collectAsState()
            val balance by vacationVm.balance.collectAsState()
            var userName by remember { mutableStateOf("") }

            LaunchedEffect(Unit) {
                userName = TokenManager.getName(context) ?: ""
                vacationVm.loadMyVacations()
                vacationVm.loadBalance()
            }

            HomeEmployeeScreen(
                initialTab = 1,
                stats = balance,
                requests = myVacations,
                userName = userName,
                onCancelRequest = { id -> vacationVm.cancelVacation(id) },
                onSubmitRequest = { start, end -> vacationVm.createVacation(start, end) },
                onTabNavClick = { tab ->
                    when (tab) {
                        2 -> navController.navigate(Routes.PROFILE)
                    }
                }
            )
        }

    }
}