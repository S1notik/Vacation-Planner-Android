package com.project.vacationplanner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.project.vacationplanner.ui.theme.*

@Composable
fun CreateTeamScreen(
    onCreateTeam: (String) -> Unit = {},
) {
    var teamName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CardDark)
                .padding(24.dp),
        ) {
            Text("Новая команда", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text(
                "Придумайте название для вашей команды",
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(Modifier.height(20.dp))
            Text("Название команды", style = MaterialTheme.typography.bodyLarge.copy(color = WhiteSecondary))
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = teamName,
                onValueChange = { teamName = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Например: IT-Отдел", style = MaterialTheme.typography.bodyLarge.copy(color = WhiteHint))
                },
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
                onClick = { if (teamName.isNotBlank()) onCreateTeam(teamName) },
                enabled = teamName.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = White,
                    contentColor = Black,
                    disabledContainerColor = CardDarker,
                    disabledContentColor = WhiteHint,
                ),
                elevation = ButtonDefaults.buttonElevation(0.dp),
            ) {
                Text("Создать команду", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}