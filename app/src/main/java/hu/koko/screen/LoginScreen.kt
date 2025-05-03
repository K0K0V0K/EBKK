package hu.koko.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()

    // 🎨 Lottie animation setup
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("bg.lottie"))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    // Dialog state
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    listOf(
                        Color(0xFF6A8D92),  // Light gray-blue
                        Color(0xFF4E6871)   // Slightly darker blue-gray
                    )
                )
            )
    ) {
        // 🎥 Lottie Animation at the top of input fields
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier
                .size(400.dp)  // Adjust the size of the animation
                .padding(top = 100.dp, bottom = 32.dp),
            contentScale = ContentScale.Fit
        )

        // Info Icon in the top-right corner
        IconButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Info, contentDescription = "Info", tint = Color(0xFF89CFF0))
        }

        // Dialog when info icon is clicked
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "Regisztráció") },
                text = { Text("A regisztrációhoz lèpj kapcsolatba az adminnal.") },
                confirmButton = {
                    TextButton(
                        onClick = { showDialog = false }
                    ) {
                        Text("OK")
                    }
                }
            )
        }

        // Login screen content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 200.dp, start = 24.dp, end = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Email input field with rounded corners and shadow
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .background(Color.White, shape = CircleShape)
                    .shadow(8.dp, shape = CircleShape)
            )

            // Password input field with rounded corners and shadow
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Jelszó") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .background(Color.White, shape = CircleShape)
                    .shadow(8.dp, shape = CircleShape)
            )

            // Gradient button with rounded corners
            Button(
                onClick = {
                    loading = true
                    error = null
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            loading = false
                            if (task.isSuccessful) {
                                onLoginSuccess()
                            } else {
                                error = task.exception?.message ?: "Hiba :("
                                Log.e("Login", "Error", task.exception)
                            }
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = buttonColors(
                    containerColor = Color(0xFF4E6871), // Dark blue
                    contentColor = Color.White
                )
            ) {
                Text(if (loading) "Folyamatban..." else "Bejelentkezés")
            }

            // Display error message if login fails
            error?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
