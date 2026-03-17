package com.example.ptodabookingapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ptodabookingapp.api.RetrofitClient
import com.example.ptodabookingapp.models.DriverDashboardResponse
import com.example.ptodabookingapp.ui.theme.PTODABookingAppTheme
import com.example.ptodabookingapp.utils.PreferenceManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DriverDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        PreferenceManager.init(this)
        val driverName = PreferenceManager.getUserName() ?: "Driver"
        
        setContent {
            PTODABookingAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DriverWelcomePage(
                        modifier = Modifier.padding(innerPadding),
                        driverName = driverName,
                        activity = this@DriverDashboardActivity
                    )
                }
            }
        }
        
        loadDriverData()
    }
    
    private fun loadDriverData() {
        val token = PreferenceManager.getToken() ?: return

        RetrofitClient.instance.getDriverDashboard(token).enqueue(object : Callback<DriverDashboardResponse> {
            override fun onResponse(call: Call<DriverDashboardResponse>, response: Response<DriverDashboardResponse>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        PreferenceManager.saveUserData(
                            data.user.id,
                            data.user.name,
                            data.user.email,
                            "driver",
                            data.driver_info.is_verified
                        )
                    }
                }
            }

            override fun onFailure(call: Call<DriverDashboardResponse>, t: Throwable) {
                Toast.makeText(this@DriverDashboardActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

@Composable
fun DriverWelcomePage(
    modifier: Modifier = Modifier,
    driverName: String,
    activity: ComponentActivity
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon
        Icon(
            Icons.Default.Home,
            contentDescription = "Driver Dashboard",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Welcome Text
        Text(
            text = "Welcome!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Driver Name
        Text(
            text = driverName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Status Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Driver Dashboard",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    "You are logged in as a Driver",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}