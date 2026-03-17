// Laravel AuthController.php Template
// Location: app/Http/Controllers/AuthController.php

<?php

namespace App\Http\Controllers;

use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use Illuminate\Validation\ValidationException;

class AuthController extends Controller
{
    public function register(Request $request)
    {
        // Validate input
        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'email' => 'required|string|email|max:255|unique:users',
            'password' => 'required|string|min:6',
        ]);

        try {
            // Create new user
            $user = User::create([
                'name' => $validated['name'],
                'email' => $validated['email'],
                'password' => Hash::make($validated['password']),
            ]);

            return response()->json([
                'status' => 'success',
                'message' => 'User registered successfully',
                'user' => [
                    'id' => $user->id,
                    'name' => $user->name,
                    'email' => $user->email,
                ]
            ], 201);

        } catch (\Exception $e) {
            return response()->json([
                'status' => 'error',
                'message' => 'Registration failed: ' . $e->getMessage()
            ], 500);
        }
    }

    public function login(Request $request)
    {
        // Validate input
        $validated = $request->validate([
            'email' => 'required|string|email',
            'password' => 'required|string',
        ]);

        // Find user by email
        $user = User::where('email', $validated['email'])->first();

        // Check if user exists and password is correct
        if (!$user || !Hash::check($validated['password'], $user->password)) {
            return response()->json([
                'status' => 'error',
                'message' => 'Invalid credentials'
            ], 401);
        }

        // Login successful
        return response()->json([
            'status' => 'success',
            'message' => 'Login successful',
            'user' => [
                'id' => $user->id,
                'name' => $user->name,
                'email' => $user->email,
            ]
        ], 200);
    }
}
?>

// ================================================
// Laravel routes/api.php
// ================================================

<?php

use App\Http\Controllers\AuthController;
use Illuminate\Support\Facades\Route;

Route::post('register', [AuthController::class, 'register']);
Route::post('login', [AuthController::class, 'login']);

?>

// ================================================
// Laravel .env file settings
// ================================================

APP_NAME="PTODABookingApp"
APP_ENV=local
APP_DEBUG=true
APP_URL=http://192.168.56.1:8000

DB_CONNECTION=mysql
DB_HOST=127.0.0.1
DB_PORT=3306
DB_DATABASE=ptoda_booking
DB_USERNAME=root
DB_PASSWORD=

// ================================================
// Create test user in MySQL (after migration)
// ================================================

Run in Laravel terminal:
php artisan tinker

Then type:
User::create(['name' => 'Test User', 'email' => 'test@test.com', 'password' => Hash::make('12345')]);

Or use MySQL directly:
INSERT INTO users (name, email, password) VALUES ('Test', 'test@test.com', '$2y$10$...');

