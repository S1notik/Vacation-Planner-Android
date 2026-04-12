package com.project.vacationplanner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.vacationplanner.ui.theme.*


@Composable
fun RoleSelectionScreen(
    onBackClick: () -> Unit = {},
    onSelectRole: (isEmployer: Boolean) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "Назад", tint = White)
            }
            Spacer(Modifier.width(4.dp))
            Text("Выберите роль", style = MaterialTheme.typography.titleLarge)
        }
        Spacer(Modifier.height(24.dp))
        Text(
            text = "Как вы хотите использовать приложение?",
            style = MaterialTheme.typography.bodyLarge.copy(color = WhiteSecondary),
        )
        Spacer(Modifier.height(20.dp))
        RoleCard(
            icon = Icons.Outlined.Business,
            title = "Работодатель",
            subtitle = "Управляйте отпусками всей команды",
            features = listOf(
                "Календарь отпусков" to "Управление заявками",
                "Аналитика" to "Команда",
            ),
            onClick = { onSelectRole(true) },
        )
        Spacer(Modifier.height(14.dp))
        RoleCard(
            icon = Icons.Outlined.Person,
            title = "Сотрудник",
            subtitle = "Планируйте свои отпуска",
            features = listOf(
                "Выбор дат" to "Мои заявки",
                "Календарь" to "Уведомления",
            ),
            onClick = { onSelectRole(false) },
        )
        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(CardDark)
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Text("💡", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Совет: Вы можете переключаться между ролями в настройках приложения",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun RoleCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    features: List<Pair<String, String>>,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(CardDark)
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardDarker),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = White, modifier = Modifier.size(26.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(2.dp))
                Text(subtitle, style = MaterialTheme.typography.bodyMedium)
            }
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = WhiteHint,
                modifier = Modifier.size(22.dp),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DividerColor),
        )
        Column(modifier = Modifier.padding(18.dp)) {
            features.forEach { (left, right) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                ) {
                    BulletItem(left, modifier = Modifier.weight(1f))
                    BulletItem(right, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun BulletItem(text: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(WhiteHint),
        )
        Spacer(Modifier.width(6.dp))
        Text(text, style = MaterialTheme.typography.bodySmall.copy(color = WhiteSecondary))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, showSystemUi = true)
@Composable
fun RoleSelectionScreenPreview() {
    VacationPlannerTheme { RoleSelectionScreen() }
}