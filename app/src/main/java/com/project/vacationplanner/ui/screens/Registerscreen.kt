package com.project.vacationplanner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.sp
import com.project.vacationplanner.ui.components.*
import com.project.vacationplanner.ui.theme.*

@Composable
fun RegisterScreen(
    onRegisterClick: (name: String, email: String, password: String, position: String, companyCode: String) -> Unit = { _, _, _, _, _ -> },
    onLoginClick:    () -> Unit = {},
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }
    var companyCode by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(64.dp))

        // Logo
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(White),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.WorkOutline,
                contentDescription = null,
                tint = Black,
                modifier = Modifier.size(40.dp),
            )
        }

        Spacer(Modifier.height(20.dp))

        Text("VacationPro", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Планирование отпусков для команды",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(36.dp))

        // Tab switcher
        VpTabSwitcher(
            tabs = listOf("Вход", "Регистрация"),
            selectedIndex = 1,
            onTabSelected = { if (it == 0) onLoginClick() },
        )

        Spacer(Modifier.height(28.dp))

        // Full name
        VpFieldLabel("Полное имя")
        VpTextField(
            value = fullName,
            onValueChange = { fullName = it },
            placeholder = "Иван Иванов",
            leadingIcon = { Icon(Icons.Outlined.Person, null) },
        )

        Spacer(Modifier.height(16.dp))

        // Email
        VpFieldLabel("Email адрес")
        VpTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "example@company.com",
            leadingIcon = { Icon(Icons.Outlined.Mail, null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        )

        Spacer(Modifier.height(16.dp))

        // Password
        VpFieldLabel("Пароль")
        VpTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = "Минимум 8 символов",
            leadingIcon = { Icon(Icons.Outlined.Lock, null) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        )
        Spacer(Modifier.height(16.dp))

        // Position / должность
        VpFieldLabel("Должность")
        VpTextField(
            value = position,
            onValueChange = { position = it },
            placeholder = "Senior Java Programmer",
            leadingIcon = { Icon(Icons.Outlined.Badge, null) },
            supportingText = "Ваша должность в компании",
        )
        Spacer(Modifier.height(16.dp))

        // Company code
        VpFieldLabel("Код компании")
        VpTextField(
            value = companyCode,
            onValueChange = { companyCode = it },
            placeholder = "Необязательно",
            leadingIcon = { Icon(Icons.Outlined.Tag, null) },
            supportingText = "Присоединитесь к существующей компании или создайте новую",
        )

        Spacer(Modifier.height(32.dp))

        VpPrimaryButton(
            text = "Создать аккаунт",
            onClick = { onRegisterClick(fullName, email, password, position, companyCode) },
            enabled = fullName.isNotBlank() && email.isNotBlank() && password.length >= 8,
        )
        Spacer(Modifier.height(32.dp))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    VacationPlannerTheme { RegisterScreen() }
}