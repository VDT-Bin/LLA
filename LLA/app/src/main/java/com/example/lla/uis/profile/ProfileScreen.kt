package com.example.lla.uis.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lla.uis.auth.AuthViewModel

@Composable
fun ProfileScreen(modifier: Modifier = Modifier,
                  navController : NavController
                  ,authViewModel : AuthViewModel
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Profile Screen", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            authViewModel.logout()
            navController.navigate("login")
        }) {
            Text("Logout")
        }
    }
}
