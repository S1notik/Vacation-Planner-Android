package com.project.vacationplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.project.vacationplanner.ui.screens.HomeEmployeeScreen
import com.project.vacationplanner.ui.screens.HomeEmployerScreen
import com.project.vacationplanner.ui.screens.LoginScreen
import com.project.vacationplanner.ui.screens.ProfileScreen
import com.project.vacationplanner.ui.screens.RoleSelectionScreen
import com.project.vacationplanner.ui.screens.StatisticsScreen
import com.project.vacationplanner.ui.theme.VacationPlannerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VacationPlannerTheme {
                //  LoginScreen()
                // RegisterScreen()
                // RoleSelectionScreen()
//                 ProfileScreen()
//                HomeEmployerScreen();
//                StatisticsScreen();
                HomeEmployeeScreen();
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    VacationPlannerTheme {
        Greeting("Android")
    }
}