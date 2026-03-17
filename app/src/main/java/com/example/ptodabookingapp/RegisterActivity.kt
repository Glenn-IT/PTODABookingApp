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
import com.example.ptodabookingapp.models.RegisterRequest
import com.example.ptodabookingapp.models.AuthResponse
import com.example.ptodabookingapp.api.RetrofitClient
import com.example.ptodabookingapp.ui.theme.PTODABookingAppTheme
import com.example.ptodabookingapp.utils.PreferenceManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize PreferenceManager
        PreferenceManager.init(this)
        
        setContent {
            PTODABookingAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RegisterScreen(
                        modifier = Modifier.padding(innerPadding),
                        activity = this@RegisterActivity
                    )
                }
            }
        }
    }

    // Function to handle navigation to Login
    fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    activity: RegisterActivity
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf("driver") }
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

        Spacer(modifier = Modifier.height(16.dp))

        // User Type Selection
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = userType == "driver",
                    onClick = { userType = "driver" },
                    enabled = !isLoading
                )
                Text("Driver", modifier = Modifier.padding(start = 8.dp))
            }

            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = userType == "passenger",
                    onClick = { userType = "passenger" },
                    enabled = !isLoading
                )
                Text("Passenger", modifier = Modifier.padding(start = 8.dp))
            }
        }

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
                if (!isLoading) {
                    isLoading = true
                    registerUser(name, email, password, confirmPassword, userType, activity) { error ->
                        isLoading = false
                        errorMessage = error
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Register")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigate to Login
        TextButton(
            onClick = { activity.navigateToLogin() },
            enabled = !isLoading
        ) {
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
    userType: String,
    activity: RegisterActivity,
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

    // Create request with user_type
    val request = RegisterRequest(
        name = name,
        email = email,
        password = password,
        user_type = userType
    )

    // Make API call
    RetrofitClient.instance.register(request).enqueue(object : Callback<AuthResponse> {
        override fun onResponse(
            call: Call<AuthResponse>,
            response: Response<AuthResponse>
        ) {
            if (response.isSuccessful) {
                val authResponse = response.body()
                if (authResponse != null && authResponse.status == "success") {
                    Toast.makeText(activity, "Registered successfully! Please login.", Toast.LENGTH_SHORT).show()
                    onError("")
                    activity.navigateToLogin()
                } else {
                    val errorMsg = authResponse?.message ?: "Registration failed"
                    onError(errorMsg)
                }
            } else {
                onError("Registration failed. Please try again.")
            }
        }

        override fun onFailure(
            call: Call<AuthResponse>,
            t: Throwable
        ) {
            onError("Network Error: ${t.message}")
        }
    })
}

