package com.example.notez.basicpages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

import com.example.notez.importantfiles.AuthViewModel
import com.example.notez.importantfiles.UserPreferences
import com.example.notez.mainuipages.BranchDropdownMenu
import com.example.notez.mainuipages.SemesterDropdownMenu
import com.example.notez.mainuipages.YearDropdownMenu
import com.example.notez.ui.theme.Secondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(
    authViewModel: AuthViewModel,
    navController: NavHostController
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                modifier = Modifier.shadow(elevation = 12.dp)
                // Customize content color if needed
            )
        },
        content = { paddingValues ->
            ProfilePageContent(
                paddingValues = paddingValues,
                authViewModel = authViewModel,
                navController = navController
            )
        }
    )
}



@Composable
fun ProfilePageContent(
    paddingValues: PaddingValues,
    authViewModel: AuthViewModel,
    navController: NavHostController
) {
    // Collect user information and preferences from ViewModel
    val user by authViewModel.currentUser.collectAsState()
    val userPreferences by authViewModel.userPreferences.collectAsState()
    val isLoading = remember { mutableStateOf(false) }
    // State variables for name and email
    val name = rememberSaveable { mutableStateOf(user?.displayName ?: "User") }
    val email = rememberSaveable { mutableStateOf(user?.email ?: "No email") }

    // State variables for preferences (branch, year, semester)
    var selectedBranch by rememberSaveable { mutableStateOf(userPreferences?.branch ?: "Computer Engineering") }
    var selectedYear by rememberSaveable { mutableIntStateOf(userPreferences?.year ?: 1) }
    var selectedSemester by rememberSaveable { mutableIntStateOf(userPreferences?.semester ?: 1) }

    // Define semesters based on the selected year
    val semesters = when (selectedYear) {
        1 -> listOf(1, 2)
        2 -> listOf(3, 4)
        3 -> listOf(5, 6)
        4 -> listOf(7, 8)
        else -> listOf(1, 2)
    }
    LaunchedEffect(Unit) {
        authViewModel.refreshUserPreferences()
    }
    if (userPreferences == null || isLoading.value) {
        CircularProgressIndicator() // Show a loading indicator
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(12.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Display User Information
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(text = "Hello ${name.value} !", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    Text(text = email.value, fontWeight = FontWeight.Normal, fontSize = 16.sp, color = Secondary)
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            // Branch Dropdown Menu
            Text(text = "Select Branch", fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
            BranchDropdownMenu(selectedBranch = selectedBranch) { branch ->
                selectedBranch = branch
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Year Dropdown Menu
            Text(text = "Select Year", fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
            YearDropdownMenu(selectedYear = selectedYear) { year ->
                selectedYear = year
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Semester Dropdown Menu
            Text(text = "Select Semester", fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
            SemesterDropdownMenu(
                selectedSemester = selectedSemester,
                semesters = semesters
            ) { semester ->
                selectedSemester = semester
            }

            Spacer(modifier = Modifier.height(25.dp))

            // Save Button
            Button(
                onClick = {
                    isLoading.value = true // Start loading when the save button is clicked
                    val preferences = UserPreferences(selectedBranch, selectedYear, selectedSemester)
                    authViewModel.updateUserPreferences(user?.uid ?: "", preferences)
                    navController.navigate("home?branch=${selectedBranch}&year=${selectedYear}&semester=${selectedSemester}") {
                        popUpTo("profile") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.97F)
                    .size(width = 40.dp, height = 45.dp),
                shape = RoundedCornerShape(25)
            ) {
                Text("Save", fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Logout Button
            Button(
                onClick = {
                    authViewModel.logout()
                    navController.navigate("loginpage") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.97F)
                    .size(width = 40.dp, height = 45.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0XFFf75f54)),
                shape = RoundedCornerShape(25)
            ) {
                Text("Logout", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}














