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
import com.example.ptodabookingapp.api.RegisterRequest
import com.example.ptodabookingapp.api.RetrofitClient
import com.example.ptodabookingapp.ui.theme.PTODABookingAppTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PTODABookingAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RegisterScreen(
                        modifier = Modifier.padding(innerPadding),
                        onNavigateToLogin = { navigateToLogin() },
                        activity = this@RegisterActivity
                    )
                }
            }
        }
    }

    // Function to handle navigation to Login
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Close RegisterActivity
    }
}

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit,
    activity: RegisterActivity
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create Account", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(24.dp))

        // Name Input
        TextField(
            value = name,
            onValueChange = { 
                name = it
                errorMessage = ""
            },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Email Input
        TextField(
            value = email,
            onValueChange = { 
                email = it
                errorMessage = ""
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

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Password Input
        TextField(
            value = confirmPassword,
            onValueChange = { 
                confirmPassword = it
                errorMessage = ""
            },
            label = { Text("Confirm Password") },
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

        // Register Button
        Button(
            onClick = {
                registerUser(name, email, password, confirmPassword, activity, onNavigateToLogin) { error ->
                    errorMessage = error
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Register")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigate to Login
        TextButton(onClick = { onNavigateToLogin() }, enabled = !isLoading) {
            Text("Already have an account? Login")
        }
    }
}

// Function to register user with API
fun registerUser(
    name: String,
    email: String,
    password: String,
    confirmPassword: String,
    activity: RegisterActivity,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    // Validation
    when {
        name.isEmpty() -> {
            onError("Name is required")
            return
        }
        email.isEmpty() -> {
            onError("Email is required")
            return
        }
        password.isEmpty() -> {
            onError("Password is required")
            return
        }
        confirmPassword.isEmpty() -> {
            onError("Confirm password is required")
            return
        }
        !email.contains("@") -> {
            onError("Invalid email format")
            return
        }
        password != confirmPassword -> {
            onError("Passwords don't match")
            return
        }
        password.length < 6 -> {
            onError("Password must be at least 6 characters")
            return
        }
    }

    // Create request
    val request = RegisterRequest(name, email, password)

    // Make API call
    RetrofitClient.instance.registerUser(request).enqueue(object : Callback<com.example.ptodabookingapp.api.ApiResponse<com.example.ptodabookingapp.api.User>> {
        override fun onResponse(
            call: Call<com.example.ptodabookingapp.api.ApiResponse<com.example.ptodabookingapp.api.User>>,
            response: Response<com.example.ptodabookingapp.api.ApiResponse<com.example.ptodabookingapp.api.User>>
        ) {
            if (response.isSuccessful && response.body()?.status == "success") {
                Toast.makeText(activity, "Registered successfully! Please login.", Toast.LENGTH_SHORT).show()
                onSuccess()
            } else {
                val errorMsg = response.body()?.message ?: "Registration failed"
                onError(errorMsg)
                Toast.makeText(activity, "Error: $errorMsg", Toast.LENGTH_SHORT).show()
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

