package com.project.vacationplanner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.project.vacationplanner.ui.model.TeamMemberUi
import com.project.vacationplanner.ui.theme.*

@Composable
fun VacationBalanceScreen(
    team: List<TeamMemberUi> = emptyList(),
    onBackClick: () -> Unit = {},
    onSetTeamBalance: (Int) -> Unit = {},
    onSetMemberBalance: (String, Int) -> Unit = { _, _ -> },
) {
    var showTeamDialog by remember { mutableStateOf(false) }
    var selectedMember by remember { mutableStateOf<TeamMemberUi?>(null) }

    if (showTeamDialog) {
        SetBalanceDialog(
            title = "Установить баланс команде",
            onDismiss = { showTeamDialog = false },
            onConfirm = { days ->
                onSetTeamBalance(days)
                showTeamDialog = false
            }
        )
    }

    if (selectedMember != null) {
        SetBalanceDialog(
            title = "Баланс для ${selectedMember!!.name}",
            onDismiss = { selectedMember = null },
            onConfirm = { days ->
                onSetMemberBalance(selectedMember!!.id, days)
                selectedMember = null
            }
        )
    }

    Scaffold(
        containerColor = Black,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Black)
                    .statusBarsPadding()
                    .padding(start = 4.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable {
                            android.util.Log.d("BackBtn", "clicked")
                            onBackClick()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.ArrowBack, null, tint = White)
                }
                Text("Управление днями", style = MaterialTheme.typography.titleLarge)
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { showTeamDialog = true },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black),
                ) {
                    Icon(Icons.Outlined.People, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Установить всей команде", style = MaterialTheme.typography.labelLarge)
                }
                Spacer(Modifier.height(8.dp))
                Text("Сотрудники", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
            }
            items(team) { member ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardDark)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(CardDarker),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = member.initials,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = WhiteSecondary,
                                fontWeight = FontWeight.SemiBold
                            ),
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(member.name, style = MaterialTheme.typography.bodyLarge)
                        Text("${member.totalDays} дней", style = MaterialTheme.typography.bodySmall)
                    }
                    IconButton(onClick = { selectedMember = member }) {
                        Icon(Icons.Outlined.Edit, null, tint = WhiteSecondary)
                    }
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun SetBalanceDialog(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
) {
    var input by remember { mutableStateOf("") }
    val days = input.toIntOrNull()

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CardDark)
                .padding(24.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = input,
                onValueChange = { if (it.length <= 3) input = it.filter { c -> c.isDigit() } },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Например: 28", style = MaterialTheme.typography.bodyLarge.copy(color = WhiteHint)) },
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
                onClick = { if (days != null) onConfirm(days) },
                enabled = days != null && days > 0,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = Black),
            ) {
                Text("Сохранить", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, showSystemUi = true)
@Composable
fun VacationBalanceScreenPreview() {
    VacationPlannerTheme { VacationBalanceScreen() }
}