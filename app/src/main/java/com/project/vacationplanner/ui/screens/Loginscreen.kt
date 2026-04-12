package com.project.vacationplanner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.vacationplanner.ui.components.*
import com.project.vacationplanner.ui.theme.*

@Composable
fun LoginScreen(
    onLoginClick: (email: String, password: String, companyCode: String) -> Unit = { _, _, _ -> },
    onRegisterClick: () -> Unit = {},
    onForgotClick: () -> Unit = {},
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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
        Text("Планировщик", style = MaterialTheme.typography.headlineLarge)
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
            selectedIndex = 0,
            onTabSelected = { if (it == 1) onRegisterClick() },
        )
        Spacer(Modifier.height(28.dp))

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
            placeholder = "Введите пароль",
            leadingIcon = { Icon(Icons.Outlined.Lock, null) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        )
        Spacer(Modifier.height(16.dp))

        // Company code
        VpFieldLabel("Код компании")
        VpTextField(
            value = companyCode,
            onValueChange = { companyCode = it },
            placeholder = "Необязательно",
            leadingIcon = { Icon(Icons.Outlined.Tag, null) },
            supportingText = "Для входа в общий аккаунт компании",
        )

        Spacer(Modifier.height(32.dp))

        VpPrimaryButton(
            text    = "Войти",
            onClick = { onLoginClick(email, password, companyCode) },
        )

        Spacer(Modifier.height(20.dp))

        TextButton(
            onClick = onForgotClick,
            colors  = ButtonDefaults.textButtonColors(contentColor = WhiteSecondary),
        ) {
            Text("Забыли пароль?", style = MaterialTheme.typography.bodyMedium.copy(color = WhiteSecondary))
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    VacationPlannerTheme { LoginScreen() }
}
