package com.example.notez.basicpages

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.notez.R
import com.example.notez.importantfiles.AuthState
import com.example.notez.importantfiles.AuthViewModel


@Composable

fun LoginPage(navController: NavController, authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    // UI Related
    val imageRes = if (isSystemInDarkTheme()) {
        R.drawable.whitelogopng
    } else {
        R.drawable.notezpnglogo
    }

    // Firebase AuthState listener
    val authState by authViewModel.authState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> navController.navigate("home") {
                popUpTo("introPage") { inclusive = true }
            }
            is AuthState.Error -> {
                Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(100.dp))

        // App Logo
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Notez",
            modifier = Modifier.size(width = 175.dp, height = 100.dp)
        )

        Text(
            text = "Welcome To NOTEZ APP",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Login to your Account", fontSize = 20.sp, fontWeight = FontWeight.Normal)

        Spacer(modifier = Modifier.height(16.dp))

        // Email Input
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = null
            },
            label = { Text("Email Address")},
            isError = emailError != null,
            modifier = Modifier.fillMaxWidth(0.85F),
        )
        if (emailError != null) {
            Text(text = emailError ?: "", color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Password Input
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = null
            },
            label = { Text("Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = null)
                }
            },
            isError = passwordError != null,
            modifier = Modifier.fillMaxWidth(0.85F)
        )
        if (passwordError != null) {
            Text(text = passwordError ?: "", color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Forgot Password
        TextButton(onClick = { /* Implement Forgot Password logic */ }) {
            Text(text = "Forgot Password?", fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Login Button
        Button(
            onClick = {
                var isValid = true
                if (email.isBlank()) {
                    emailError = "Email is required"
                    isValid = false
                }
                if (password.isBlank()) {
                    passwordError = "Password is required"
                    isValid = false
                }
                if (isValid) {
                    authViewModel.login(email, password)
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.60F)
                .size(width = 40.dp, height = 50.dp),
            shape = RoundedCornerShape(25)
        ) {
            Text(text = "Login", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Don't have an account? SignUp
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Don't have an account?", fontSize = 16.sp)
            TextButton(onClick = {
                navController.navigate("signuppage")
            }) {
                Text(text = "Sign Up", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
