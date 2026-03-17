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
import com.example.ptodabookingapp.models.LoginRequest
import com.example.ptodabookingapp.models.AuthResponse
import com.example.ptodabookingapp.api.RetrofitClient
import com.example.ptodabookingapp.ui.theme.PTODABookingAppTheme
import com.example.ptodabookingapp.utils.PreferenceManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize PreferenceManager
        PreferenceManager.init(this)
        
        setContent {
            PTODABookingAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(
                        modifier = Modifier.padding(innerPadding),
                        activity = this@LoginActivity
                    )
                }
            }
        }
    }

    fun navigateBasedOnUserType(userType: String, isVerified: Boolean) {
        when {
            userType == "driver" && isVerified -> {
                startActivity(Intent(this, DriverDashboardActivity::class.java))
            }
            userType == "driver" && !isVerified -> {
                Toast.makeText(this, "Please verify your account", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, DriverDashboardActivity::class.java))
            }
            userType == "passenger" -> {
                startActivity(Intent(this, DashboardActivity::class.java))
            }
        }
        finish()
    }

    // Function to handle navigation to Register
    fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    activity: LoginActivity
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf("driver") }
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
                if (!isLoading) {
                    isLoading = true
                    loginUser(email, password, userType, activity) { error ->
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
                Text("Login")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigate to Register
        TextButton(
            onClick = { activity.navigateToRegister() },
            enabled = !isLoading
        ) {
            Text("Don't have an account? Register")
        }
    }
}

// Function to login user with API
fun loginUser(
    email: String,
    password: String,
    userType: String,
    activity: LoginActivity,
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

    // Create request with user_type
    val request = LoginRequest(
        email = email,
        password = password,
        user_type = userType
    )

    // Make API call
    RetrofitClient.instance.login(request).enqueue(object : Callback<AuthResponse> {
        override fun onResponse(
            call: Call<AuthResponse>,
            response: Response<AuthResponse>
        ) {
            if (response.isSuccessful) {
                val authResponse = response.body()
                if (authResponse != null && authResponse.status == "success") {
                    // Save token and user data
                    authResponse.token?.let { PreferenceManager.saveToken(it) }
                    PreferenceManager.saveUserData(
                        authResponse.user.id,
                        authResponse.user.name,
                        authResponse.user.email,
                        authResponse.user_type,
                        authResponse.is_verified
                    )

                    Toast.makeText(activity, "Login successful!", Toast.LENGTH_SHORT).show()
                    onError("")
                    
                    // Navigate based on user type
                    activity.navigateBasedOnUserType(authResponse.user_type, authResponse.is_verified)
                } else {
                    onError("Login failed. Please try again.")
                }
            } else {
                onError("Invalid credentials")
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


