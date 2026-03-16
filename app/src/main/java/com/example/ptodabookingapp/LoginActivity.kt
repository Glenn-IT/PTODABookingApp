package com.example.ptodabookingapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.ptodabookingapp.api.LoginRequest
import com.example.ptodabookingapp.api.RetrofitClient
import com.example.ptodabookingapp.ui.theme.PTODABookingAppTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PTODABookingAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(
                        modifier = Modifier.padding(innerPadding),
                        onLoginSuccess = { userName -> navigateToDashboard(userName) },
                        onNavigateToRegister = { navigateToRegister() },
                        activity = this@LoginActivity
                    )
                }
            }
        }
    }

    // Function to handle navigation to Dashboard
    private fun navigateToDashboard(userName: String) {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.putExtra("user_name", userName)
        startActivity(intent)
        finish() // Close LoginActivity
    }

    // Function to handle navigation to Register
    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }
}

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginSuccess: (String) -> Unit,
    onNavigateToRegister: () -> Unit,
    activity: LoginActivity
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome Back!", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(24.dp))

        // Email Input
        TextField(
            value = email,
            onValueChange = { 
                email = it
                errorMessage = "" // Clear error when user types
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Input
        TextField(
            value = password,
            onValueChange = { 
                password = it
                errorMessage = ""
            },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Error Message Display
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Login Button
        Button(
            onClick = {
                loginUser(email, password, activity, onLoginSuccess) { error ->
                    errorMessage = error
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Login")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigate to Register
        TextButton(onClick = { onNavigateToRegister() }, enabled = !isLoading) {
            Text("Don't have an account? Register")
        }
    }
}

// Function to login user with API
fun loginUser(
    email: String,
    password: String,
    activity: LoginActivity,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit
) {
    // Validation
    when {
        email.isEmpty() -> {
            onError("Email is required")
            return
        }
        password.isEmpty() -> {
            onError("Password is required")
            return
        }
        !email.contains("@") -> {
            onError("Invalid email format")
            return
        }
    }

    // Create request
    val request = LoginRequest(email, password)

    // Make API call
    RetrofitClient.instance.loginUser(request).enqueue(object : Callback<com.example.ptodabookingapp.api.ApiResponse<com.example.ptodabookingapp.api.User>> {
        override fun onResponse(
            call: Call<com.example.ptodabookingapp.api.ApiResponse<com.example.ptodabookingapp.api.User>>,
            response: Response<com.example.ptodabookingapp.api.ApiResponse<com.example.ptodabookingapp.api.User>>
        ) {
            if (response.isSuccessful && response.body()?.status == "success") {
                val userName = response.body()?.user?.name ?: "User"
                Toast.makeText(activity, "Login successful!", Toast.LENGTH_SHORT).show()
                onSuccess(userName)
            } else {
                onError("Invalid credentials")
                Toast.makeText(activity, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(
            call: Call<com.example.ptodabookingapp.api.ApiResponse<com.example.ptodabookingapp.api.User>>,
            t: Throwable
        ) {
            onError("Network Error: ${t.message}")
            Toast.makeText(activity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
        }
    })
}


