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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.project.vacationplanner.ui.theme.*
import com.project.vacationplanner.ui.model.EmployeeVacationStats
import com.project.vacationplanner.ui.model.MyVacationRequestUi
import com.project.vacationplanner.ui.enums.VacationStatus



private val employeeBusyDays    = setOf(20, 21, 22, 23, 24, 25, 26, 27)
private val employeeOverlapDays = emptySet<Int>()

@Composable
fun HomeEmployeeScreen(
    initialTab: Int = 0,
    stats: EmployeeVacationStats = EmployeeVacationStats(),
    requests: List<MyVacationRequestUi> = emptyList(),
    userName: String = "",
    onTabNavClick: (Int) -> Unit = {},
    onBellClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
    onCancelRequest: (String) -> Unit = {},
    onSubmitRequest: (startDate: String, endDate: String) -> Unit = { _, _ -> },
) {
    var selectedTab by remember { mutableIntStateOf(initialTab) }
    var showNewRequest  by remember { mutableStateOf(false) }
    val tabs = listOf("Календарь", "Мои заявки")
    Scaffold(
        containerColor = Black,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Black)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Мои отпуска", style = MaterialTheme.typography.titleLarge)
                    if (userName.isNotBlank()) {
                        Text(userName, style = MaterialTheme.typography.bodySmall)
                    }
                }
                IconButton(onClick = onBellClick) {
                    Icon(Icons.Outlined.Notifications, null, tint = White)
                }
                IconButton(onClick = onMoreClick) {
                    Icon(Icons.Outlined.MoreVert, null, tint = White)
                }
            }
        },
        bottomBar = {
            EmployeeBottomBar(currentTab = 0, onTabClick = onTabNavClick)
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                EmployeeStatTile("${stats.totalDays}","Всего дней", Modifier.weight(1f))
                EmployeeStatTile("${stats.usedDays}", "Использовано", Modifier.weight(1f), dimmed = true)
                EmployeeStatTile("${stats.leftDays}","Осталось", Modifier.weight(1f), dimmed = true)
            }
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
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
                0 -> CalendarTab()
                1 -> MyRequestsTab(requests = requests, onCancel = onCancelRequest)
            }
        }
    }
    if (showNewRequest) {
        NewVacationDialog(
            onDismiss = { showNewRequest = false },
            onSubmit  = { start, end ->
                onSubmitRequest(start, end)
                showNewRequest = false
            },
        )
    }
}


@Composable
private fun CalendarTab() {
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
            Text("График отпусков", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(2.dp))
            Text("Просмотрите доступные периоды", style = MaterialTheme.typography.bodyMedium)
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
                    IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.KeyboardArrowLeft, null, tint = WhiteSecondary, modifier = Modifier.size(20.dp))
                    }
                    Text("Март 2026", style = MaterialTheme.typography.titleMedium)
                    IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
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
                val cells = buildList {
                    repeat(6) { add(null) }
                    for (d in 1..31) add(d)
                }
                cells.chunked(7).forEach { week ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        week.forEach { day ->
                            Box(
                                modifier = Modifier.weight(1f).aspectRatio(1f),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (day != null) {
                                    val isBusy  = day in employeeBusyDays
                                    val isToday = day == 31
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .then(if (isToday) Modifier.border(1.dp, White, RoundedCornerShape(8.dp)) else Modifier)
                                            .background(if (isBusy) CardDark else Color.Transparent),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = day.toString(),
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = if (isBusy) White else WhiteSecondary,
                                            ),
                                        )
                                    }
                                }
                            }
                        }
                        repeat(7 - week.size) { Box(modifier = Modifier.weight(1f).aspectRatio(1f)) }
                    }
                }
                Spacer(Modifier.height(12.dp))
                // Legend
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    LegendItem(bg = CardDark, border = null,  label = "Занято")
                    LegendItem(bg = White,    border = null,  label = "Выбрано")
                    LegendItem(bg = Color.Transparent, border = White, label = "Сегодня")
                }
            }
        }
        Spacer(Modifier.height(12.dp))

        // Info card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardDark)
                .padding(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("💡", fontSize = 16.sp)
                Spacer(Modifier.width(8.dp))
                Text("Полезная информация", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.height(10.dp))
            listOf(
                "Оранжевые даты заняты другими сотрудниками",
                "Минимальная продолжительность — 7 дней",
                "Заявки рассматриваются до 3 рабочих дней",
            ).forEach { tip ->
                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                    Text("• ", style = MaterialTheme.typography.bodyMedium)
                    Text(tip, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        Spacer(Modifier.height(80.dp))
    }
}

@Composable
private fun LegendItem(bg: Color, border: Color?, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(bg)
                .then(if (border != null) Modifier.border(1.dp, border, RoundedCornerShape(4.dp)) else Modifier),
        )
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun MyRequestsTab(
    requests: List<MyVacationRequestUi>,
    onCancel: (String) -> Unit,
) {
    if (requests.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Нет заявок", style = MaterialTheme.typography.bodyMedium)
        }
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(requests) { req ->
            MyRequestCard(request = req, onCancel = { onCancel(req.id) })
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun MyRequestCard(request: MyVacationRequestUi, onCancel: () -> Unit) {
    val (statusLabel, statusColor) = when (request.status) {
        VacationStatus.PENDING  -> "Ожидает" to Color(0xFFFF9800)
        VacationStatus.APPROVED -> "Одобрено" to Color(0xFF4CAF50)
        VacationStatus.REJECTED -> "Отклонено" to Color(0xFFF44336)
        else -> "Неизвестно" to Color.Gray
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardDark)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.CalendarMonth, null, tint = WhiteSecondary, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(request.dateRange, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(statusColor.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.AccessTime, null, tint = statusColor, modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(statusLabel, style = MaterialTheme.typography.bodySmall.copy(color = statusColor))
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            "${request.workDays} рабочих дней • Создано ${request.createdDate}",
            style = MaterialTheme.typography.bodySmall,
        )
        if (request.status == VacationStatus.PENDING) {
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = White),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(DividerColor),
                ),
            ) {
                Icon(Icons.Outlined.Delete, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Отменить заявку", style = MaterialTheme.typography.labelLarge.copy(color = White))
            }
        }
    }
}

// ── Dialog: Новая заявка ──────────────────────────────────────────────────────

@Composable
private fun NewVacationDialog(
    onDismiss: () -> Unit,
    onSubmit:  (startDate: String, endDate: String) -> Unit,
) {
    var startDate by remember { mutableStateOf("") }
    var endDate   by remember { mutableStateOf("") }
    val isReady   = startDate.isNotBlank() && endDate.isNotBlank()

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CardDark)
                .padding(24.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Новая заявка на отпуск", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Выберите даты начала и окончания вашего отпуска",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Outlined.Close, null, tint = WhiteHint, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(Modifier.height(20.dp))
            Text("Дата начала", style = MaterialTheme.typography.bodyLarge.copy(color = WhiteSecondary))
            Spacer(Modifier.height(6.dp))
            DateInputField(
                value = startDate,
                onValueChange = { startDate = it },
            )
            Spacer(Modifier.height(14.dp))
            Text("Дата окончания", style = MaterialTheme.typography.bodyLarge.copy(color = WhiteSecondary))
            Spacer(Modifier.height(6.dp))
            DateInputField(
                value = endDate,
                onValueChange = { endDate = it },
            )
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    val startConverted = startDate.split(".").reversed().joinToString("-")
                    val endConverted = endDate.split(".").reversed().joinToString("-")
                    onSubmit(startConverted, endConverted) },
                enabled = isReady,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonWhite,
                    contentColor = Black,
                    disabledContainerColor = Color(0xFFE0E0E0),
                    disabledContentColor = WhiteHint,
                ),
                elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp, 0.dp),
            ) {
                Text("Отправить заявку", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun DateInputField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
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
}


@Composable
private fun EmployeeStatTile(value: String, label: String, modifier: Modifier, dimmed: Boolean = false) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CardDark)
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = if (dimmed) WhiteSecondary else White,
            ),
        )
        Spacer(Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun EmployeeBottomBar(currentTab: Int, onTabClick: (Int) -> Unit) {
    val items = listOf(
        Icons.Outlined.Home to "Главная",
        Icons.Outlined.Description to "Заявки",
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
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (selected) White else WhiteHint,
                    ),
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, showSystemUi = true)
@Composable
fun HomeEmployeeScreenPreview() {
    VacationPlannerTheme { HomeEmployeeScreen() }
}