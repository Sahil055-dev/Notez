package com.example.notez.basicpages

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.notez.R
import com.example.notez.importantfiles.AuthState
import com.example.notez.importantfiles.AuthViewModel
import com.example.notez.mainuipages.BranchDropdownMenu
import com.example.notez.mainuipages.SemesterDropdownMenu
import com.example.notez.mainuipages.YearDropdownMenu
import com.example.notez.ui.theme.Tertiary
import com.example.notez.ui.theme.darkContainer
import com.example.notez.ui.theme.lightContainer


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpPage(navController: NavController, authViewModel: AuthViewModel) {
    val isDarkTheme = isSystemInDarkTheme()
    val imageRes = if (isDarkTheme) {
        R.drawable.whitelogopng // Dark mode image
    } else {
        R.drawable.notezpnglogo // Light mode image
    }
    val containerBackground = if (isDarkTheme) darkContainer else lightContainer
    var isSignUpDetails by remember { mutableStateOf(true) }

    // Other states
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmpassword by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var passwordVisible1 by remember { mutableStateOf(false) }

    // State for dropdowns
    var selectedBranch by rememberSaveable { mutableStateOf("Computer Engineering") }
    var selectedYear by rememberSaveable { mutableIntStateOf(1) }
    var selectedSemester by rememberSaveable { mutableIntStateOf(1) }

    // Error Variables
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    // Firebase Related
    val authState by authViewModel.authState.collectAsState()
    val context = LocalContext.current

    // React to authentication state changes
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Sign Up") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                modifier = Modifier.shadow(elevation = 12.dp)
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(padding),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    modifier = Modifier.size(width = 175.dp, height = 100.dp),
                    contentDescription = "Logo"
                )

                Text(
                    text = "Enter your Details",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { isSignUpDetails = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSignUpDetails) containerBackground else Color.Transparent
                        ),
                        shape = RectangleShape,
                        modifier = Modifier
                            .weight(1f)
                            .border(5.dp, color = if (isSignUpDetails) containerBackground else Color.Transparent)
                    ) {
                        Text("Sign Up Details", color = if(isDarkTheme) Color.White else Color.Black)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { isSignUpDetails = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isSignUpDetails) containerBackground else Color.Transparent
                        ),
                        shape = RectangleShape,
                        modifier = Modifier
                            .weight(1f)
                            .border(5.dp, color = if (isSignUpDetails) Color.Transparent else containerBackground)
                    ) {
                        Text("Preferences", color = if(isDarkTheme) Color.White else Color.Black)
                    }
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RectangleShape,
                        colors = CardDefaults.cardColors(containerColor = containerBackground)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            if (isSignUpDetails) {
                                OutlinedTextField(
                                    value = name,
                                    onValueChange = {
                                        name = it
                                        nameError = null
                                    },
                                    label = { Text("Full Name") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                if (nameError != null) {
                                    Text(text = nameError ?: "", color = Color.Red, fontSize = 12.sp)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = email,
                                    onValueChange = {
                                        email = it
                                        emailError = null
                                    },
                                    label = { Text("Email") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                if (emailError != null) {
                                    Text(text = emailError ?: "", color = Color.Red, fontSize = 12.sp)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

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
                                    modifier = Modifier.fillMaxWidth()
                                )
                                if (passwordError != null) {
                                    Text(text = passwordError ?: "", color = Color.Red, fontSize = 12.sp)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = confirmpassword,
                                    onValueChange = {
                                        confirmpassword = it
                                        confirmPasswordError = null
                                    },
                                    label = { Text("Confirm Password") },
                                    visualTransformation = if (passwordVisible1) VisualTransformation.None else PasswordVisualTransformation(),
                                    trailingIcon = {
                                        val image = if (passwordVisible1) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                        IconButton(onClick = { passwordVisible1 = !passwordVisible1 }) {
                                            Icon(imageVector = image, contentDescription = null)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                if (confirmPasswordError != null) {
                                    Text(text = confirmPasswordError ?: "", color = Color.Red, fontSize = 12.sp)
                                }
                            } else {
                                BranchDropdownMenu(selectedBranch = selectedBranch) { branch ->
                                    selectedBranch = branch
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                YearDropdownMenu(selectedYear = selectedYear) { year ->
                                    selectedYear = year
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                val semesters = when (selectedYear) {
                                    1 -> listOf(1, 2)
                                    2 -> listOf(3, 4)
                                    3 -> listOf(5, 6)
                                    4 -> listOf(7, 8)
                                    else -> listOf(1, 2)
                                }
                                SemesterDropdownMenu(selectedSemester = selectedSemester, semesters = semesters) { semester ->
                                    selectedSemester = semester
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        var isValid = true

                        // Validate Name
                        if (name.isBlank()) {
                            nameError = "Name is required"
                            isValid = false
                        }

                        // Validate Email
                        if (email.isBlank()) {
                            emailError = "Email is required"
                            isValid = false
                        }

                        // Validate Password
                        if (password.isBlank()) {
                            passwordError = "Password is required"
                            isValid = false
                        }

                        // Validate Confirm Password
                        if (confirmpassword.isBlank()) {
                            confirmPasswordError = "Password confirmation is required"
                            isValid = false
                        } else if (password != confirmpassword) {
                            confirmPasswordError = "Passwords do not match"
                            isValid = false
                        }

                        // If all inputs are valid, proceed with signup
                        if (isValid) {
                            authViewModel.signup(
                                name,
                                email,
                                password,
                                confirmpassword,
                                selectedBranch,
                                selectedYear,
                                selectedSemester
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25)
                ) {
                    Text(text = "Sign Up", fontWeight = FontWeight.SemiBold)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Already have an account?", fontSize = 16.sp)
                    TextButton(onClick = { navController.navigate("loginpage") }) {
                        Text(text = "Login", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    )
}
