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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.vacationplanner.ui.theme.*

@Composable
fun ProfileScreen(
    name: String = "Алексей Петров",
    position: String = "Senior Frontend Developer",
    email: String = "aleksey.petrov@company.com",
    phone: String = "+7 (999) 123-45-67",
    department: String = "Разработка",
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onSwitchRoleClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onTabClick: (Int) -> Unit = {},
    currentTab: Int = 2,
) {
    val initials = name.split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")

    Scaffold(
        containerColor = Black,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Black)
                    .padding(start = 4.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Outlined.ArrowBack, null, tint = White)
                }
                Text("Профиль", style = MaterialTheme.typography.titleLarge)
            }
        },
        bottomBar = {
            ProfileBottomBar(currentTab = currentTab, onTabClick = onTabClick)
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(CardDark),
                contentAlignment = Alignment.Center,
            ) {
                Text(initials, style = MaterialTheme.typography.headlineMedium.copy(color = WhiteSecondary))
            }
            Spacer(Modifier.height(16.dp))
            Text(name, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(4.dp))
            Text(position, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(28.dp))
            ProfileActionRow(
                icon = Icons.Outlined.Edit,
                label = "Изменить данные",
                onClick = onEditClick,
            )
            Spacer(Modifier.height(10.dp))

            ProfileActionRow(
                icon = Icons.Outlined.Logout,
                label = "Выйти из аккаунта",
                onClick = onLogoutClick,
            )

            Spacer(Modifier.height(20.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardDark),
            ) {
                ProfileInfoRow(label = "Email", value = email)
                ProfileDivider()
                ProfileInfoRow(label = "Роль", value = if (position == "EMPLOYER") "Работодатель" else "Сотрудник")
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ProfileActionRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardDark)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(CardDarker),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = WhiteSecondary, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(14.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Spacer(Modifier.width(12.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(2f),
        )
    }
}

@Composable
private fun ProfileDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(DividerColor),
    )
}

@Composable
private fun ProfileBottomBar(currentTab: Int, onTabClick: (Int) -> Unit) {
    val items = listOf(
        Icons.Outlined.Home to "Главная",
        Icons.Outlined.Description to "Заявки",
        Icons.Outlined.BarChart to "Статистика",
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
fun ProfileScreenPreview() {
    VacationPlannerTheme { ProfileScreen() }
}