# PTODA Booking App - Complete Development Roadmap

## 📋 Table of Contents
1. [Project Overview](#project-overview)
2. [Tech Stack](#tech-stack)
3. [Prerequisites](#prerequisites)
4. [Backend Setup (Laravel API)](#backend-setup-laravel-api)
5. [Frontend Setup (Android Kotlin)](#frontend-setup-android-kotlin)
6. [Network Configuration](#network-configuration)
7. [Step-by-Step Implementation](#step-by-step-implementation)
8. [Troubleshooting](#troubleshooting)
9. [Testing Checklist](#testing-checklist)

---

## 🎯 Project Overview

**PTODA Booking App** is a mobile application that allows users to:
- Register a new account
- Login with existing credentials
- Access a dashboard after authentication
- Logout from the application

**Flow: Register → Login → Dashboard → Logout**

---

## 💻 Tech Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Frontend** | Android Studio (Kotlin) | Latest |
| **Backend** | Laravel API | 8.x+ |
| **Database** | MySQL | 5.7+ |
| **Local Server** | XAMPP | Latest |
| **Networking** | Retrofit 2 | 2.9.0 |
| **JSON Parsing** | Gson | Latest |

---

## ⚙️ Prerequisites

### For Backend Development:
- [ ] XAMPP installed and running (Apache + MySQL)
- [ ] Laravel 8.x+ installed
- [ ] Composer package manager
- [ ] MySQL workbench or PhpMyAdmin for database management

### For Frontend Development:
- [ ] Android Studio installed
- [ ] Android SDK (API 21+)
- [ ] Android emulator or physical device (for testing)
- [ ] Gradle build system

### Network Requirements:
- [ ] PC and phone/emulator on the **same WiFi network**
- [ ] Note down your PC's IP address (use `ipconfig` command)
- [ ] Note down your phone's IP address

---

## 🔧 Backend Setup (Laravel API)

### Step 1: Create Laravel Project

```bash
# Create new Laravel project
composer create-project laravel/laravel ptoda-api

# Navigate to project
cd ptoda-api
```

### Step 2: Create Database

```sql
-- In MySQL/PhpMyAdmin
CREATE DATABASE ptoda_booking;
USE ptoda_booking;
```

### Step 3: Create User Migration

```bash
php artisan make:migration create_users_table
```

In `database/migrations/xxxx_create_users_table.php`:

```php
public function up()
{
    Schema::create('users', function (Blueprint $table) {
        $table->id();
        $table->string('name');
        $table->string('email')->unique();
        $table->string('password');
        $table->timestamps();
    });
}
```

Run migration:
```bash
php artisan migrate
```

### Step 4: Create User Model & Controller

```bash
php artisan make:model User --migration=false
php artisan make:controller AuthController
```

### Step 5: Implement AuthController

In `app/Http/Controllers/AuthController.php`:

```php
<?php

namespace App\Http\Controllers;

use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Validator;

class AuthController extends Controller
{
    // Register
    public function register(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'name' => 'required|string|max:255',
            'email' => 'required|string|email|unique:users',
            'password' => 'required|string|min:6',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'status' => 'error',
                'message' => $validator->errors()->first(),
            ], 400);
        }

        $user = User::create([
            'name' => $request->name,
            'email' => $request->email,
            'password' => Hash::make($request->password),
        ]);

        return response()->json([
            'status' => 'success',
            'message' => 'User registered successfully',
            'user' => $user,
        ], 201);
    }

    // Login
    public function login(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'email' => 'required|string|email',
            'password' => 'required|string|min:6',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'status' => 'error',
                'message' => $validator->errors()->first(),
            ], 400);
        }

        $user = User::where('email', $request->email)->first();

        if (!$user || !Hash::check($request->password, $user->password)) {
            return response()->json([
                'status' => 'error',
                'message' => 'Invalid credentials',
            ], 401);
        }

        return response()->json([
            'status' => 'success',
            'message' => 'Login successful',
            'user' => $user,
        ], 200);
    }
}
```

### Step 6: Create Routes

In `routes/api.php`:

```php
<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\AuthController;

Route::post('register', [AuthController::class, 'register']);
Route::post('login', [AuthController::class, 'login']);
```

### Step 7: Configure CORS (if needed)

In `config/cors.php`:

```php
'allowed_origins' => ['*'],
'allowed_methods' => ['*'],
```

### Step 8: Run Laravel Server

```bash
# Make sure you're in the ptoda-api directory
php artisan serve --host=0.0.0.0 --port=8000

# Or if running from XAMPP:
# Start Apache and MySQL from XAMPP Control Panel
# Access via: http://192.168.1.6:8000/api/ (use your PC's IP)
```

---

## 📱 Frontend Setup (Android Kotlin)

### Step 1: Create Android Project

In Android Studio:
1. Click **File → New → New Android Project**
2. Select **Empty Activity**
3. Name: `PTODABookingApp`
4. Language: **Kotlin**
5. Minimum SDK: **API 21 (Android 5.0)**

### Step 2: Add Dependencies

Edit `app/build.gradle.kts`:

```kotlin
dependencies {
    // Retrofit & Gson
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Android Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
```

Click **Sync Now**

---

## 🌐 Network Configuration

### Understanding Network IPs in Your Setup

```
Your PC:        192.168.1.6     (Backend API Host)
Your Phone:     192.168.1.4     (Frontend Client)
Emulator:       10.0.2.2        (Special mapping to PC localhost)
```

### Step 1: Find Your PC's IP Address

**On Windows:**
```powershell
ipconfig
```

Look for "IPv4 Address" under your WiFi connection (e.g., 192.168.1.6)

**On Mac/Linux:**
```bash
ifconfig
```

### Step 2: Update network_security_config.xml

Create file: `app/src/main/res/xml/network_security_config.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <!-- Allow cleartext (HTTP) traffic for local development servers -->
    <domain-config cleartextTrafficPermitted="true">
        <!-- Your PC's IP -->
        <domain includeSubdomains="true">192.168.1.6</domain>
        <!-- Your Phone's IP (if needed) -->
        <domain includeSubdomains="true">192.168.1.4</domain>
        <!-- For Emulator -->
        <domain includeSubdomains="true">10.0.2.2</domain>
        <!-- Localhost variants -->
        <domain includeSubdomains="true">localhost</domain>
        <domain includeSubdomains="true">127.0.0.1</domain>
    </domain-config>

    <!-- HTTPS for production domains -->
    <domain-config cleartextTrafficPermitted="false">
        <domain includeSubdomains="true">example.com</domain>
    </domain-config>
</network-security-config>
```

### Step 3: Update AndroidManifest.xml

Add in `app/src/main/AndroidManifest.xml` inside `<application>` tag:

```xml
<application
    android:networkSecurityConfig="@xml/network_security_config"
    ...>
    <!-- rest of your app config -->
</application>
```

Also add Internet permission before `</manifest>`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

---

## 📝 Step-by-Step Implementation

### STEP 1: Create Data Models

Create `java/com/yourpackage/models/User.kt`:

```kotlin
data class User(
    val id: Int,
    val name: String,
    val email: String
)
```

Create `java/com/yourpackage/models/ApiResponse.kt`:

```kotlin
data class ApiResponse<T>(
    val status: String,
    val user: T? = null,
    val message: String? = null
)
```

Create `java/com/yourpackage/models/LoginRequest.kt`:

```kotlin
data class LoginRequest(
    val email: String,
    val password: String
)
```

Create `java/com/yourpackage/models/RegisterRequest.kt`:

```kotlin
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)
```

### STEP 2: Create Retrofit API Service

Create `java/com/yourpackage/network/ApiService.kt`:

```kotlin
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("register")
    fun registerUser(@Body request: RegisterRequest): Call<ApiResponse<User>>

    @POST("login")
    fun loginUser(@Body request: LoginRequest): Call<ApiResponse<User>>
}
```

### STEP 3: Create Retrofit Client

Create `java/com/yourpackage/network/RetrofitClient.kt`:

```kotlin
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // ⚠️ IMPORTANT: Update this IP address to match your PC's IP
    private const val BASE_URL = "http://192.168.1.6:8000/api/"

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}
```

**Note:** If using emulator, change to `http://10.0.2.2:8000/api/`

### STEP 4: Create RegisterActivity

Create `java/com/yourpackage/RegisterActivity.kt`:

```kotlin
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        registerButton = findViewById(R.id.registerButton)

        registerButton.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val name = nameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString()

        // Validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val request = RegisterRequest(name, email, password)

        RetrofitClient.instance.registerUser(request).enqueue(object : Callback<ApiResponse<User>> {
            override fun onResponse(
                call: Call<ApiResponse<User>>,
                response: Response<ApiResponse<User>>
            ) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Registered successfully! Please login.",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish() // Go back to Login
                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Error: ${response.body()?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<User>>, t: Throwable) {
                Toast.makeText(
                    this@RegisterActivity,
                    "Network Error: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}
```

### STEP 5: Create LoginActivity

Create `java/com/yourpackage/LoginActivity.kt`:

```kotlin
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        registerLink = findViewById(R.id.registerLink)

        loginButton.setOnClickListener {
            loginUser()
        }

        registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString()

        // Validation
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val request = LoginRequest(email, password)

        RetrofitClient.instance.loginUser(request).enqueue(object : Callback<ApiResponse<User>> {
            override fun onResponse(
                call: Call<ApiResponse<User>>,
                response: Response<ApiResponse<User>>
            ) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(
                        this@LoginActivity,
                        "Login successful!",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Navigate to Dashboard
                    val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                    intent.putExtra("user_name", response.body()?.user?.name)
                    intent.putExtra("user_email", response.body()?.user?.email)
                    intent.putExtra("user_id", response.body()?.user?.id)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Invalid credentials",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<User>>, t: Throwable) {
                Toast.makeText(
                    this@LoginActivity,
                    "Network Error: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}
```

### STEP 6: Create DashboardActivity

Create `java/com/yourpackage/DashboardActivity.kt`:

```kotlin
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {
    private lateinit var welcomeTextView: TextView
    private lateinit var userEmailTextView: TextView
    private lateinit var logoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Initialize views
        welcomeTextView = findViewById(R.id.welcomeTextView)
        userEmailTextView = findViewById(R.id.userEmailTextView)
        logoutButton = findViewById(R.id.logoutButton)

        // Get data from intent
        val userName = intent.getStringExtra("user_name") ?: "User"
        val userEmail = intent.getStringExtra("user_email") ?: "N/A"

        // Display welcome message
        welcomeTextView.text = "Welcome, $userName"
        userEmailTextView.text = "Email: $userEmail"

        // Logout functionality
        logoutButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            // Clear back stack
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
```

### STEP 7: Update MainActivity

Make MainActivity redirect to LoginActivity on startup:

```kotlin
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Redirect to LoginActivity
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
```

### STEP 8: Create Layout Files

#### activity_register.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Register"
        android:textSize="24sp"
        android:textStyle="bold"
        android:gravity="center"
        android:layout_marginBottom="30dp" />

    <EditText
        android:id="@+id/nameEditText"
        android:layout_width="match_parent"
        android:layout_height="48dp"
        android:hint="Full Name"
        android:inputType="text"
        android:padding="12dp"
        android:layout_marginBottom="12dp" />

    <EditText
        android:id="@+id/emailEditText"
        android:layout_width="match_parent"
        android:layout_height="48dp"
        android:hint="Email"
        android:inputType="textEmailAddress"
        android:padding="12dp"
        android:layout_marginBottom="12dp" />

    <EditText
        android:id="@+id/passwordEditText"
        android:layout_width="match_parent"
        android:layout_height="48dp"
        android:hint="Password"
        android:inputType="textPassword"
        android:padding="12dp"
        android:layout_marginBottom="24dp" />

    <Button
        android:id="@+id/registerButton"
        android:layout_width="match_parent"
        android:layout_height="48dp"
        android:text="Register"
        android:textSize="16sp" />

</LinearLayout>
```

#### activity_login.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Login"
        android:textSize="24sp"
        android:textStyle="bold"
        android:gravity="center"
        android:layout_marginBottom="30dp" />

    <EditText
        android:id="@+id/emailEditText"
        android:layout_width="match_parent"
        android:layout_height="48dp"
        android:hint="Email"
        android:inputType="textEmailAddress"
        android:padding="12dp"
        android:layout_marginBottom="12dp" />

    <EditText
        android:id="@+id/passwordEditText"
        android:layout_width="match_parent"
        android:layout_height="48dp"
        android:hint="Password"
        android:inputType="textPassword"
        android:padding="12dp"
        android:layout_marginBottom="24dp" />

    <Button
        android:id="@+id/loginButton"
        android:layout_width="match_parent"
        android:layout_height="48dp"
        android:text="Login"
        android:textSize="16sp" />

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:gravity="center"
        android:layout_marginTop="16dp">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Don't have an account? " />

        <TextView
            android:id="@+id/registerLink"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Register"
            android:textColor="#007BFF"
            android:textStyle="bold" />

    </LinearLayout>

</LinearLayout>
```

#### activity_dashboard.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:id="@+id/welcomeTextView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Welcome, User"
        android:textSize="24sp"
        android:textStyle="bold"
        android:gravity="center"
        android:layout_marginBottom="16dp" />

    <TextView
        android:id="@+id/userEmailTextView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Email: N/A"
        android:textSize="16sp"
        android:gravity="center"
        android:layout_marginBottom="30dp" />

    <Button
        android:id="@+id/logoutButton"
        android:layout_width="match_parent"
        android:layout_height="48dp"
        android:text="Logout"
        android:textSize="16sp" />

</LinearLayout>
```

### STEP 9: Update AndroidManifest.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:networkSecurityConfig="@xml/network_security_config"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.PTODABookingApp">

        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <activity android:name=".LoginActivity" />
        <activity android:name=".RegisterActivity" />
        <activity android:name=".DashboardActivity" />

    </application>

</manifest>
```

---

## 🐛 Troubleshooting

### Issue 1: "Cleartext communication not permitted"
**Solution:**
- Update `network_security_config.xml` with your PC's IP
- Ensure `cleartextTrafficPermitted="true"` for your IP range
- Restart the app after making changes

### Issue 2: "Failed to connect to 192.168.x.x (port 8000)"
**Checklist:**
- [ ] PC and phone are on the same WiFi network
- [ ] Laravel server is running: `php artisan serve --host=0.0.0.0 --port=8000`
- [ ] PC firewall allows port 8000 (add exception if needed)
- [ ] PC's IP address in `RetrofitClient.kt` is correct
- [ ] Phone can ping PC: Test with browser first

### Issue 3: "Network Error: Connection timed out"
**Solutions:**
1. **For Physical Device:**
   - Verify PC IP: `ipconfig` (look for IPv4 Address)
   - Update `RetrofitClient.kt` with correct IP
   - Update `network_security_config.xml` with correct IP

2. **For Emulator:**
   - Use `10.0.2.2` instead of `192.168.x.x`
   - This is the special alias for localhost in Android emulator

### Issue 4: "API Response is null"
**Check:**
- [ ] Laravel API is returning correct JSON format
- [ ] Check Laravel logs: `storage/logs/laravel.log`
- [ ] Verify API endpoint URLs in `ApiService.kt`

### Issue 5: "Database connection error"
**Solutions:**
- [ ] Start MySQL in XAMPP Control Panel
- [ ] Verify credentials in `.env` file
- [ ] Run migrations: `php artisan migrate`

---

## ✅ Testing Checklist

### Before Testing:
- [ ] PC's IP address noted and configured
- [ ] Laravel server running on `http://192.168.x.x:8000`
- [ ] MySQL database created and migrated
- [ ] Android dependencies synced
- [ ] `network_security_config.xml` updated
- [ ] Internet permission added to `AndroidManifest.xml`

### Test Flow:
```
1. [ ] Start App → Should redirect to LoginActivity
2. [ ] Click "Register" → Should open RegisterActivity
3. [ ] Fill and submit registration form
   - [ ] Success: Toast message + return to login
   - [ ] Failure: Error message displayed
4. [ ] Login with registered credentials
   - [ ] Success: Navigate to DashboardActivity
   - [ ] Failure: "Invalid credentials" message
5. [ ] Dashboard shows user info
   - [ ] Welcome message displays name
   - [ ] Email is shown correctly
6. [ ] Click Logout
   - [ ] Should return to LoginActivity
   - [ ] Back button doesn't show previous screen
```

### Testing with Different Scenarios:

**Test 1: Invalid Email Format**
- [ ] Try registering with invalid email
- [ ] Should show validation error

**Test 2: Duplicate Email**
- [ ] Register with same email twice
- [ ] Should show "email already exists" error

**Test 3: Short Password**
- [ ] Try password with less than 6 characters
- [ ] Should show validation error

**Test 4: Empty Fields**
- [ ] Try submitting form with empty fields
- [ ] Should show error toast

---

## 📌 Quick Reference

### Directory Structure
```
PTODABookingApp/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/yourpackage/
│   │       │   ├── MainActivity.kt
│   │       │   ├── LoginActivity.kt
│   │       │   ├── RegisterActivity.kt
│   │       │   ├── DashboardActivity.kt
│   │       │   ├── models/
│   │       │   │   ├── User.kt
│   │       │   │   ├── ApiResponse.kt
│   │       │   │   ├── LoginRequest.kt
│   │       │   │   └── RegisterRequest.kt
│   │       │   └── network/
│   │       │       ├── ApiService.kt
│   │       │       └── RetrofitClient.kt
│   │       ├── res/
│   │       │   ├── layout/
│   │       │   │   ├── activity_main.xml
│   │       │   │   ├── activity_login.xml
│   │       │   │   ├── activity_register.xml
│   │       │   │   └── activity_dashboard.xml
│   │       │   └── xml/
│   │       │       └── network_security_config.xml
│   │       └── AndroidManifest.xml
│   └── build.gradle.kts
└── build.gradle.kts
```

### Important IPs to Remember
- **PC/Backend:** 192.168.1.6 (update based on your ipconfig)
- **Phone/Frontend:** 192.168.1.4 (your device IP)
- **Emulator:** 10.0.2.2 (use this instead of 192.168.x.x)

---

## 🎓 Kotlin Learning Notes

### Why MainActivity and not LoginActivity directly?

Android apps start with a **Launcher Activity** defined in `AndroidManifest.xml`. This activity should:
1. Load the default splash/initialization screen
2. Redirect to the appropriate screen based on app state

**Best Practice:**
- **MainActivity** → Check authentication status
- If logged in → Go to Dashboard
- If not logged in → Go to Login

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val isLoggedIn = checkIfUserIsLoggedIn()
        
        if (isLoggedIn) {
            startActivity(Intent(this, DashboardActivity::class.java))
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }
}
```

---

## 📞 Support Resources

- [Retrofit Documentation](https://square.github.io/retrofit/)
- [Android Documentation](https://developer.android.com/)
- [Laravel Documentation](https://laravel.com/docs)
- [Kotlin Documentation](https://kotlinlang.org/docs/)

---

**Last Updated:** March 2026
**Version:** 1.0

