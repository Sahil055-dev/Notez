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
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.notez.R
import com.example.notez.importantfiles.AuthState
import com.example.notez.importantfiles.AuthViewModel



@Composable
fun IntroPage(navController: NavController, authViewModel: AuthViewModel) {
    val isDarkTheme = isSystemInDarkTheme()
    val imageRes = if (isDarkTheme) {
        R.drawable.whitelogopng //  dark mode image
    } else {
        R.drawable.notezpnglogo //  light mode image
    }
    // Observe authentication state
    val authState by authViewModel.authState.collectAsState()

    // Remember context
    val context = LocalContext.current

    // Handle side effects based on authentication state
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Authenticated -> {
                // Navigate to Main Menu if authenticated
                navController.navigate("home") {
                    popUpTo("introPage") { inclusive = true }
                }
            }
            is AuthState.Error -> {
                // Show a Toast message if an error occurs
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }
    }

//UI Related
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    )
    {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "backgroundbook",
            modifier = Modifier.size(200.dp)
        )

        Text(
            text = "Welcome To NOTEZ APP",
            fontSize = 24.sp,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Please Login or Sign Up to continue",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        )
        {

            Button(onClick = {
                navController.navigate("loginpage")
            },
            ) {
                Text(text = "Login", fontWeight = FontWeight.SemiBold)
            }
            Button(onClick = {
                navController.navigate("signuppage")
            })
            {
                Text(text = "SignUp", fontWeight = FontWeight.SemiBold)
            }


        }


    }

}
