package com.project.vacationplanner.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.project.vacationplanner.data.repository.TeamRepository
import com.project.vacationplanner.ui.theme.Black
import com.project.vacationplanner.ui.theme.White
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

    const val CREATE_TEAM = "create_team"

    const val ROLE_SELECTION_AUTO = "role_selection_auto"

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
                    val hasTeam = TeamRepository(context).getTeamMembers().isSuccess
                    if (hasTeam) {
                        // Загрузить и сохранить инвайт-код
                        TeamRepository(context).getTeamInfo().onSuccess { info ->
                            TokenManager.saveInviteCode(context, info.inviteCode)
                        }
                        navController.navigate(Routes.HOME_EMPLOYER) { popUpTo(0) { inclusive = true } }
                    } else {
                        navController.navigate(Routes.CREATE_TEAM) { popUpTo(0) { inclusive = true } }
                    }
                } else {
                    val code = authViewModel.pendingInviteCode.value
                    if (!code.isNullOrBlank()) {
                        TeamRepository(context).joinTeam(code)
                        authViewModel.pendingInviteCode.value = null
                    }
                    navController.navigate(Routes.HOME_EMPLOYEE) { popUpTo(0) { inclusive = true } }
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
                    if (companyCode.isNotBlank()) {
                        navController.navigate(Routes.ROLE_SELECTION_AUTO)
                    } else {
                        navController.navigate(Routes.ROLE_SELECTION)
                    }
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
            var inviteCode by remember { mutableStateOf("") }
            val calendarData by teamVm.calendarData.collectAsState()
            LaunchedEffect(Unit) {
                companyName = TokenManager.getName(context) ?: ""
                inviteCode = TokenManager.getInviteCode(context) ?: ""
                teamVm.loadTeamMembers()
                teamVm.loadCalendar(2026, 5)
            }

            HomeEmployerScreen(
                stats = EmployerStats(
                    employeesCount = teamMembers.size,
                    pendingCount = teamVacations.count { it.isNew },
                    approvedCount = teamVacations.count { !it.isNew && it.status == "APPROVED" },
                    totalDays = teamMembers.size * 28
                ),
                requests = teamVacations.filter { it.isNew },
                team = teamMembers,
                companyName = companyName,
                inviteCode = inviteCode,
                calendarData = calendarData,
                onMonthChanged = { year, month -> teamVm.loadCalendar(year, month) },
                onCreateTeam = { name -> teamVm.createTeam(name) },
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
            val teamVm: TeamViewModel = viewModel()
            val myVacations by vacationVm.myVacations.collectAsState()
            val balance by vacationVm.balance.collectAsState()
            var userName by remember { mutableStateOf("") }
            val calendarData by teamVm.calendarData.collectAsState()

            LaunchedEffect(Unit) {
                userName = TokenManager.getName(context) ?: ""
                vacationVm.loadMyVacations()
                vacationVm.loadBalance()
                teamVm.loadCalendar(2026, 5)
            }

            HomeEmployeeScreen(
                stats = balance,
                requests = myVacations,
                userName = userName,
                onCancelRequest = { id -> vacationVm.cancelVacation(id) },
                onSubmitRequest = { start, end -> vacationVm.createVacation(start, end) },
                calendarData = calendarData,
                onMonthChanged = { year, month -> teamVm.loadCalendar(year, month) },
                onTabNavClick = { tab ->
                    when (tab) {
                        1 -> navController.navigate(Routes.HOME_EMPLOYEE_REQUESTS)
                        2 -> navController.navigate(Routes.PROFILE)
                    }
                }
            )
        }

        composable(Routes.PROFILE) {
            var userName by remember { mutableStateOf("") }
            var userEmail by remember { mutableStateOf("") }
            var userPosition by remember { mutableStateOf("") }
            var userRole2 by remember { mutableStateOf("") }

            LaunchedEffect(Unit) {
                userName = TokenManager.getName(context) ?: ""
                userEmail = TokenManager.getEmail(context) ?: ""
                userPosition = TokenManager.getPosition(context) ?: ""
                userRole2 = TokenManager.getRole(context) ?: ""
            }

            ProfileScreen(
                name = userName,
                email = userEmail,
                position = userPosition,
                role = userRole2,
                onBackClick = { navController.popBackStack() },
                onLogoutClick = {
                    authViewModel.logout()
                    navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                },
                onTabClick = { tab ->
                    when (tab) {
                        0 -> {
                            val dest = if (userRole2 == "EMPLOYER") Routes.HOME_EMPLOYER else Routes.HOME_EMPLOYEE
                            navController.navigate(dest) { popUpTo(0) { inclusive = true } }
                        }
                        1 -> {
                            val dest = if (userRole2 == "EMPLOYER") Routes.HOME_EMPLOYER_REQUESTS else Routes.HOME_EMPLOYEE_REQUESTS
                            navController.navigate(dest) { popUpTo(0) { inclusive = true } }
                        }
                        2 -> {
                            if (userRole2 == "EMPLOYER") {
                                navController.navigate(Routes.STATISTICS) { popUpTo(0) { inclusive = true } }
                            }
                        }
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
                        0 -> navController.navigate(Routes.HOME_EMPLOYER) {
                            popUpTo(0) {
                                inclusive = true
                            }
                        }

                        1 -> navController.navigate(Routes.HOME_EMPLOYER_REQUESTS) {
                            popUpTo(0) {
                                inclusive = true
                            }
                        }

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
            var inviteCode by remember { mutableStateOf("") }
            var companyName by remember { mutableStateOf("") }


            LaunchedEffect(Unit) {
                vacationVm.loadTeamVacations()
                teamVm.loadTeamMembers()
                companyName = TokenManager.getName(context) ?: ""
                inviteCode = TokenManager.getInviteCode(context) ?: ""
            }

            HomeEmployerScreen(
                initialTab = 1,
                stats = EmployerStats(
                    employeesCount = teamMembers.size,
                    pendingCount = teamVacations.count { it.isNew },
                    approvedCount = teamVacations.count { !it.isNew },
                    totalDays = teamMembers.size * 28
                ),
                requests = teamVacations.filter { it.isNew },
                team = teamMembers,
                inviteCode = inviteCode,
                onCreateTeam = { name -> teamVm.createTeam(name) },
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
            val teamVm: TeamViewModel = viewModel()
            val myVacations by vacationVm.myVacations.collectAsState()
            val balance by vacationVm.balance.collectAsState()
            var userName by remember { mutableStateOf("") }
            val calendarData by teamVm.calendarData.collectAsState()

            LaunchedEffect(Unit) {
                userName = TokenManager.getName(context) ?: ""
                vacationVm.loadMyVacations()
                vacationVm.loadBalance()
                teamVm.loadCalendar(2026, 5)
            }

            HomeEmployeeScreen(
                initialTab = 1,
                stats = balance,
                requests = myVacations,
                userName = userName,
                calendarData = calendarData,
                onMonthChanged = { year, month -> teamVm.loadCalendar(year, month) },
                onCancelRequest = { id -> vacationVm.cancelVacation(id) },
                onSubmitRequest = { start, end -> vacationVm.createVacation(start, end) },
                onTabNavClick = { tab ->
                    when (tab) {
                        2 -> navController.navigate(Routes.PROFILE)
                    }
                }
            )
        }

        composable(Routes.CREATE_TEAM) {
            val teamVm: TeamViewModel = viewModel()

            CreateTeamScreen(
                onCreateTeam = { name ->
                    teamVm.createTeam(name)
                    navController.navigate(Routes.HOME_EMPLOYER) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.ROLE_SELECTION_AUTO) {
            val data = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<String>("registerData") ?: ""
            val parts = data.split("|")
            val name = parts.getOrElse(0) { "" }
            val email = parts.getOrElse(1) { "" }
            val password = parts.getOrElse(2) { "" }
            val position = parts.getOrElse(3) { "" }
            val companyCode = parts.getOrElse(4) { "" }

            LaunchedEffect(Unit) {
                authViewModel.pendingInviteCode.value = companyCode
                authViewModel.register(email, password, name, "EMPLOYEE")
                TokenManager.savePosition(context, position)
            }

            Box(
                modifier = Modifier.fillMaxSize().background(Black),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = White)
            }
        }

    }
}