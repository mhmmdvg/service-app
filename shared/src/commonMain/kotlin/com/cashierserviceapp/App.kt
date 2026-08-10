package com.cashierserviceapp

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.cashierserviceapp.navigation.BottomNavigation

@Composable
@Preview
fun App() {
    com.cashierserviceapp.ui.theme.CashierServiceTheme {
        Scaffold(
            bottomBar = {
                BottomNavigation()
            }
        ) {
            Text("Hello World")
        }
    }
}