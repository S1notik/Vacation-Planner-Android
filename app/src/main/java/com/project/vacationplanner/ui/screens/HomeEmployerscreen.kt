package com.project.vacationplanner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.vacationplanner.ui.theme.*
import com.project.vacationplanner.ui.model.EmployerStats
import com.project.vacationplanner.ui.model.RecentActivityUi
import com.project.vacationplanner.ui.model.TeamMemberUi
import com.project.vacationplanner.ui.model.VacationRequestUi



private val busyDays = setOf(15, 16, 17, 18, 19, 23, 24, 25, 26, 27)
private val overlapDays = setOf(20, 21, 22)

@Composable
fun HomeEmployerScreen(
    initialTab: Int = 0,
    stats: EmployerStats = EmployerStats(),
    requests: List<VacationRequestUi> = emptyList(),
    team: List<TeamMemberUi>  = emptyList(),
    activity: List<RecentActivityUi> = emptyList(),
    companyName: String = "",
    onMenuClick: () -> Unit = {},
    onBellClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
    onApprove: (String) -> Unit = {},
    onReject: (String) -> Unit = {},
    onTeamMore: (String) -> Unit = {},
    onTabNavClick: (Int) -> Unit = {},
    inviteCode: String = "",
    onCreateTeam: (String) -> Unit = {},
    calendarData: Map<Int, List<String>> = emptyMap(),
    onMonthChanged: (year: Int, month: Int) -> Unit = { _, _ -> },
    onSubmitRequest: (startDate: String, endDate: String) -> Unit = { _, _ -> },
    ) {
    var selectedTab by remember { mutableIntStateOf(initialTab) }
    val tabs = listOf("Обзор", "Заявки (${requests.size})", "Команда")
    var showNewRequest by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Black,
        topBar = {
            EmployerTopBar(
                companyName = companyName,
                onMenuClick = onMenuClick,
                onBellClick = onBellClick,
                onMoreClick = onMoreClick,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showNewRequest = true },
                containerColor = White,
                contentColor = Black,
                shape = CircleShape,
            ) {
                Icon(Icons.Outlined.Add, null)
            }
        },
        bottomBar = { EmployerBottomBar(currentTab = 0, onTabClick = onTabNavClick) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    StatTile(Icons.Outlined.People,stats.employeesCount.toString(), "Сотрудников", Modifier.weight(1f))
                    StatTile(Icons.Outlined.AccessTime, stats.pendingCount.toString(),   "Ожидают",     Modifier.weight(1f))
                }
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    StatTile(Icons.Outlined.CheckCircle,stats.approvedCount.toString(), "Одобрено",    Modifier.weight(1f))
                    StatTile(Icons.Outlined.CalendarMonth,stats.totalDays.toString(),    "Всего дней",  Modifier.weight(1f))
                }
                Spacer(Modifier.height(16.dp))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                tabs.forEachIndexed { index, label ->
                    val selected = index == selectedTab
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTab = index },
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = if (selected) White else WhiteHint,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                            ),
                            modifier = Modifier.padding(vertical = 10.dp),
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .background(if (selected) White else Color.Transparent),
                        )
                    }
                }
            }
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(DividerColor))
            Spacer(Modifier.height(12.dp))
            when (selectedTab) {
                0 -> OverviewTab(
                    activity = activity,
                    calendarData = calendarData,
                    onMonthChanged = onMonthChanged
                )                1 -> RequestsTab(requests = requests, onApprove = onApprove, onReject = onReject)
                2 -> TeamTab(team = team, inviteCode = inviteCode, onMore = onTeamMore,
                    onCreateTeam = onCreateTeam)

            }
        }
        if (showNewRequest) {
            EmployerVacationDialog(
                onDismiss = { showNewRequest = false },
                onSubmit = { start, end ->
                    onSubmitRequest(start, end)
                    showNewRequest = false
                }
            )
        }
    }
}

@Composable
private fun OverviewTab(
    activity: List<RecentActivityUi>,
    calendarData: Map<Int, List<String>> = emptyMap(),
    onMonthChanged: (year: Int, month: Int) -> Unit = { _, _ -> },
) {
    var selectedDay by remember { mutableStateOf<Int?>(null) }
    var currentYear by remember { mutableIntStateOf(2026) }
    var currentMonth by remember { mutableIntStateOf(5) }

    val monthNames = listOf("Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
        "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь")

    val daysInMonth = when (currentMonth) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (currentYear % 4 == 0) 29 else 28
        else -> 30
    }

    if (selectedDay != null) {
        val employees = calendarData[selectedDay] ?: emptyList()
        DayVacationDialog(
            day = selectedDay!!,
            employees = employees,
            onDismiss = { selectedDay = null }
        )
    }

    val firstDayOfWeek = java.util.Calendar.getInstance().apply {
        set(currentYear, currentMonth - 1, 1)
    }.get(java.util.Calendar.DAY_OF_WEEK).let {
        if (it == java.util.Calendar.SUNDAY) 6 else it - 2
    }

    val cells = buildList {
        repeat(firstDayOfWeek) { add(null) }
        for (d in 1..daysInMonth) add(d)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardDark)
                .padding(16.dp),
        ) {
            Text("Календарь отпусков", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(2.dp))
            Text("График на текущий период", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(12.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardDarker)
                    .padding(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = {
                        if (currentMonth == 1) { currentMonth = 12; currentYear-- }
                        else currentMonth--
                        onMonthChanged(currentYear, currentMonth)
                    }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.KeyboardArrowLeft, null, tint = WhiteSecondary, modifier = Modifier.size(20.dp))
                    }
                    Text("${monthNames[currentMonth - 1]} $currentYear", style = MaterialTheme.typography.titleMedium)
                    IconButton(onClick = {
                        if (currentMonth == 12) { currentMonth = 1; currentYear++ }
                        else currentMonth++
                        onMonthChanged(currentYear, currentMonth)
                    }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.KeyboardArrowRight, null, tint = WhiteSecondary, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс").forEach { day ->
                        Text(
                            text = day,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall.copy(color = WhiteHint),
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                cells.chunked(7).forEach { week ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        week.forEach { day ->
                            Box(
                                modifier = Modifier.weight(1f).aspectRatio(1f),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (day != null) {
                                    val isBusy = calendarData.containsKey(day)
                                    val count = calendarData[day]?.size ?: 0
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isBusy) CardDark else Color.Transparent)
                                            .clickable { selectedDay = day },
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = day.toString(),
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = if (isBusy) White else WhiteSecondary,
                                                ),
                                            )
                                            if (count > 1) {
                                                Text(
                                                    text = "+${count - 1}",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp, color = WhiteHint),
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        repeat(7 - week.size) { Box(modifier = Modifier.weight(1f).aspectRatio(1f)) }
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(CardDark)
                                .border(1.dp, DividerColor, RoundedCornerShape(4.dp)),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Занято", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardDark)
                .padding(16.dp),
        ) {
            Text("Недавняя активность", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))
            if (activity.isEmpty()) {
                Text("Нет активности", style = MaterialTheme.typography.bodyMedium)
            } else {
                activity.forEach { item ->
                    ActivityRow(item = item)
                    if (item != activity.last()) Spacer(Modifier.height(8.dp))
                }
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun ActivityRow(item: RecentActivityUi) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardDarker)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        InitialsAvatar(initials = item.initials)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.employeeName, style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(2.dp))
            Text(item.dateRange, style = MaterialTheme.typography.bodySmall)
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(CardDark)
                .padding(horizontal = 10.dp, vertical = 5.dp),
        ) {
            Text("${item.daysCount}д", style = MaterialTheme.typography.bodySmall.copy(color = WhiteSecondary))
        }
    }
}

@Composable
private fun RequestsTab(
    requests:  List<VacationRequestUi>,
    onApprove: (String) -> Unit,
    onReject:  (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(requests) { request ->
            RequestCard(
                request = request,
                onApprove = { onApprove(request.id) },
                onReject = { onReject(request.id) },
            )
        }
        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun RequestCard(
    request:   VacationRequestUi,
    onApprove: () -> Unit,
    onReject:  () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardDark)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            InitialsAvatar(initials = request.initials)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(request.employeeName, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.CalendarMonth, null, tint = WhiteHint, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("${request.startDate} — ${request.endDate}", style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.height(2.dp))
                Text("${request.workDays} рабочих дней", style = MaterialTheme.typography.bodySmall)
            }
            if (request.isNew) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CardDarker)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text("Новая", style = MaterialTheme.typography.bodySmall.copy(color = White))
                }
            }
        }
        if (request.isNew) {
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onApprove,
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonWhite,
                        contentColor = Black
                    ),
                    elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp, 0.dp),
                ) {
                    Icon(Icons.Outlined.CheckCircle, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Одобрить", style = MaterialTheme.typography.labelLarge)
                }
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = White),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(DividerColor),
                    ),
                ) {
                    Icon(Icons.Outlined.Close, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Отклонить",
                        style = MaterialTheme.typography.labelLarge.copy(color = White)
                    )
                }
            }
        }
    }
}

@Composable
private fun TeamTab(
    team: List<TeamMemberUi>,
    inviteCode: String,
    onMore: (String) -> Unit,
    onCreateTeam: (String) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        CreateTeamDialog(
            onDismiss = { showDialog = false },
            onConfirm = { name ->
                onCreateTeam(name)
                showDialog = false
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            if (inviteCode.isNotBlank() || team.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardDark)
                        .padding(16.dp),
                ) {
                    Text("Инвайт-код команды", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardDarker)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(inviteCode, style = MaterialTheme.typography.titleMedium.copy(color = White))
                        Icon(Icons.Outlined.ContentCopy, null, tint = WhiteSecondary, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
        items(team) { member ->
            TeamMemberCard(member = member, onMore = { onMore(member.id) })
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun TeamMemberCard(member: TeamMemberUi, onMore: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardDark)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            InitialsAvatar(initials = member.initials)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(member.name,     style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(2.dp))
                Text(member.position, style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onMore, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Outlined.MoreVert, null, tint = WhiteHint, modifier = Modifier.size(18.dp))
            }
        }
        if (member.usedDays > 0) {
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Использовано", style = MaterialTheme.typography.bodySmall)
                Text("${member.usedDays}/${member.totalDays} дней", style = MaterialTheme.typography.bodySmall.copy(color = WhiteSecondary))
            }
            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { member.usedDays.toFloat() / member.totalDays.toFloat() },
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                color = White,
                trackColor = CardDarker,
            )
        }
    }
}

@Composable
private fun InitialsAvatar(initials: String, size: Int = 44) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(CardDarker),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.bodyLarge.copy(color = WhiteSecondary, fontWeight = FontWeight.SemiBold),
        )
    }
}

@Composable
private fun StatTile(icon: ImageVector, value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CardDark)
            .padding(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(CardDarker),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = WhiteSecondary, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(value, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
        }
    }
}

@Composable
private fun EmployerTopBar(
    companyName: String,
    onMenuClick: () -> Unit,
    onBellClick: () -> Unit,
    onMoreClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Black)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onMenuClick) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CardDark),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.Menu, null, tint = White, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("Планировщик", style = MaterialTheme.typography.titleLarge)
            Text(companyName,   style = MaterialTheme.typography.bodySmall)
        }
        IconButton(onClick = onBellClick) { Icon(Icons.Outlined.Notifications, null, tint = White) }
        IconButton(onClick = onMoreClick) { Icon(Icons.Outlined.MoreVert, null, tint = White) }
    }
}

@Composable
private fun EmployerBottomBar(currentTab: Int, onTabClick: (Int) -> Unit) {
    val items = listOf(
        Icons.Outlined.Home to "Главная",
        Icons.Outlined.BarChart to "Статистика",
        Icons.Outlined.CalendarMonth to "Дни",
        Icons.Outlined.Person to "Профиль",
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDarker)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        items.forEachIndexed { index, (icon, label) ->
            val selected = index == currentTab
            Column(
                modifier = Modifier.clickable { onTabClick(index) },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (selected) White else WhiteHint,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall.copy(color = if (selected) White else WhiteHint),
                )
            }
        }
    }
}


@Composable
private fun CreateTeamDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var teamName by remember { mutableStateOf("") }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CardDark)
                .padding(24.dp),
        ) {
            Text("Новая команда", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text("Придумайте название для вашей команды", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(20.dp))
            OutlinedTextField(
                value = teamName,
                onValueChange = { teamName = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Например: IT-Отдел", style = MaterialTheme.typography.bodyLarge.copy(color = WhiteHint)) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = White,
                    unfocusedBorderColor = DividerColor,
                    focusedContainerColor = Black,
                    unfocusedContainerColor = Black,
                    focusedTextColor = White,
                    unfocusedTextColor = White,
                    cursorColor = White,
                ),
            )
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = { if (teamName.isNotBlank()) onConfirm(teamName) },
                enabled = teamName.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black),
            ) {
                Text("Создать команду", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun DayVacationDialog(
    day: Int,
    employees: List<String>,
    onDismiss: () -> Unit,
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CardDark)
                .padding(24.dp),
        ) {
            Text("Отпуска $day числа", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))
            if (employees.isEmpty()) {
                Text("Нет сотрудников в отпуске", style = MaterialTheme.typography.bodyMedium)
            } else {
                employees.forEach { name ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardDarker)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        InitialsAvatar(
                            initials = name.split(" ").take(2)
                                .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                                .joinToString(""),
                            size = 36
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(name, style = MaterialTheme.typography.bodyLarge)
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black),
            ) {
                Text("Закрыть", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}


@Composable
private fun EmployerVacationDialog(
    onDismiss: () -> Unit,
    onSubmit: (startDate: String, endDate: String) -> Unit,
) {
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    val isReady = startDate.isNotBlank() && endDate.isNotBlank()

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CardDark)
                .padding(24.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Мой отпуск", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(4.dp))
                    Text("Будет одобрен автоматически", style = MaterialTheme.typography.bodyMedium)
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Outlined.Close, null, tint = WhiteHint, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(Modifier.height(20.dp))
            Text("Дата начала", style = MaterialTheme.typography.bodyLarge.copy(color = WhiteSecondary))
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = startDate,
                onValueChange = { startDate = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("дд.мм.гггг", style = MaterialTheme.typography.bodyLarge.copy(color = WhiteHint)) },
                trailingIcon = { Icon(Icons.Outlined.CalendarMonth, null, tint = WhiteSecondary) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = White,
                    unfocusedBorderColor = DividerColor,
                    focusedContainerColor = Black,
                    unfocusedContainerColor = Black,
                    focusedTextColor = White,
                    unfocusedTextColor = White,
                    cursorColor = White,
                ),
            )
            Spacer(Modifier.height(14.dp))
            Text("Дата окончания", style = MaterialTheme.typography.bodyLarge.copy(color = WhiteSecondary))
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = endDate,
                onValueChange = { endDate = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("дд.мм.гггг", style = MaterialTheme.typography.bodyLarge.copy(color = WhiteHint)) },
                trailingIcon = { Icon(Icons.Outlined.CalendarMonth, null, tint = WhiteSecondary) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = White,
                    unfocusedBorderColor = DividerColor,
                    focusedContainerColor = Black,
                    unfocusedContainerColor = Black,
                    focusedTextColor = White,
                    unfocusedTextColor = White,
                    cursorColor = White,
                ),
            )
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    val startConverted = startDate.split(".").reversed().joinToString("-")
                    val endConverted = endDate.split(".").reversed().joinToString("-")
                    onSubmit(startConverted, endConverted)
                },
                enabled = isReady,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black),
                elevation = ButtonDefaults.buttonElevation(0.dp),
            ) {
                Text("Создать отпуск", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, showSystemUi = true)
@Composable
fun HomeEmployerScreenPreview() {
    VacationPlannerTheme { HomeEmployerScreen() }
}

