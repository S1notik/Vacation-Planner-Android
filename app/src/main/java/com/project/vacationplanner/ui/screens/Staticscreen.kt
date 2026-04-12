package com.project.vacationplanner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.vacationplanner.ui.theme.*
import com.project.vacationplanner.ui.model.StatisticsUiState


@Composable
fun StatisticsScreen(
    state: StatisticsUiState = StatisticsUiState(),
    onTabNavClick: (Int) -> Unit = {},
    onBellClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
) {
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
                    Text("Статистика", style = MaterialTheme.typography.titleLarge)
                    if (state.userName.isNotBlank()) {
                        Text(state.userName, style = MaterialTheme.typography.bodySmall)
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
            StatisticsBottomBar(currentTab = 2, onTabClick = onTabNavClick)
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            Spacer(Modifier.height(12.dp))
            // Статистика заявок
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardDark)
                    .padding(16.dp),
            ) {
                Text("Статистика заявок", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                RequestStatRow(
                    icon       = Icons.Outlined.AccessTime,
                    iconBg     = Color(0xFFFF9800),
                    title      = "Ожидают рассмотрения",
                    subtitle   = "В обработке",
                    count      = state.pendingCount,
                )
                Spacer(Modifier.height(8.dp))
                RequestStatRow(
                    icon       = Icons.Outlined.CheckCircle,
                    iconBg     = Color(0xFF4CAF50),
                    title      = "Одобрено",
                    subtitle   = "Подтверждено",
                    count      = state.approvedCount,
                )
                Spacer(Modifier.height(8.dp))
                RequestStatRow(
                    icon       = Icons.Outlined.Cancel,
                    iconBg     = Color(0xFFF44336),
                    title      = "Отклонено",
                    subtitle   = "Не подтверждено",
                    count      = state.rejectedCount,
                )
            }
            Spacer(Modifier.height(12.dp))
            // Аналитика за год
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardDark)
                    .padding(16.dp),
            ) {
                Text("Аналитика за год", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                AnalyticsRow(label = "Средний отпуск",  value = if (state.avgVacationDays > 0) "${state.avgVacationDays} дней" else "—")
                Divider()
                AnalyticsRow(label = "Всего заявок",    value = if (state.totalRequests > 0) "${state.totalRequests}" else "—")
                Divider()
                AnalyticsRow(label = "Осталось дней",   value = if (state.remainingDays > 0) "${state.remainingDays}" else "—")
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun RequestStatRow(
    icon:     ImageVector,
    iconBg:   Color,
    title:    String,
    subtitle: String,
    count:    Int,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardDarker)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconBg.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = iconBg, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title,    style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(2.dp))
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
        }
        Text(
            text  = count.toString(),
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}

@Composable
private fun AnalyticsRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun Divider() {
    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(DividerColor))
}

@Composable
private fun StatisticsBottomBar(currentTab: Int, onTabClick: (Int) -> Unit) {
    val items = listOf(
        Icons.Outlined.Home        to "Главная",
        Icons.Outlined.Description to "Заявки",
        Icons.Outlined.BarChart    to "Статистика",
        Icons.Outlined.Person      to "Профиль",
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
fun StatisticsScreenPreview() {
    VacationPlannerTheme { StatisticsScreen() }
}