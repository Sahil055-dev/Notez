package com.example.notez.mainuipages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.notez.database.AppDatabase
import com.example.notez.database.SubjectEntity
import com.example.notez.importantfiles.AuthViewModel
import com.example.notez.ui.theme.colors
import com.example.notez.ui.theme.onlightSurf

@Composable
fun Home(
    navController: NavController,
    authViewModel: AuthViewModel,
    db: AppDatabase
) {


    var selectedBranch by remember { mutableStateOf("Computer Engineering") }
    var selectedYear by remember { mutableIntStateOf(1) }
    var selectedSemester by remember { mutableIntStateOf(1) }
    val subjects = remember { mutableStateOf<List<SubjectEntity>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) } // Loading state

    val currentUser = authViewModel.currentUser.collectAsState().value

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            authViewModel.getUserInfo(
                user.uid,
                onUserDataSuccess = { data -> },
                onPreferencesSuccess = { preferences ->
                    preferences?.let {
                        selectedBranch = it.branch ?: "Computer Engineering"
                        selectedYear = it.year ?: 1
                        selectedSemester = it.semester ?: 1

                        // Start loading
                        isLoading = true

                        // Fetch subjects using AuthViewModel's method
                        authViewModel.fetchSubjects(selectedSemester,
                            onSuccess = { fetchedSubjects ->
                                subjects.value = fetchedSubjects
                                isLoading = false // End loading
                            },
                            onFailure = { exception ->
                                // Handle fetch failure
                                isLoading = false // End loading on failure
                            }
                        )
                    }
                },
                onFailure = { exception -> /* Handle failure */ }
            )
        }
    }

    Scaffold(
        topBar = {
            TopBar(navController)
        },
        content = { padding ->
            if (isLoading) {
                // Show loading indicator while fetching data
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (subjects.value.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(subjects.value) { index, subject ->
                        val backgroundColor = colors[index % colors.size]
                        ClassCard(subject, backgroundColor, navController)
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No subjects available.")
                }
            }
        }
    )
}







@Composable
fun ClassCard(
    subject: SubjectEntity,
    backgroundColor: Color,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .size(125.dp)
            .clickable {
                // Navigate to the subject-specific page dynamically
                val route = "subjectDetail/${subject.id}"
                navController.navigate(route)
            },
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = subject.name,
                style = MaterialTheme.typography.titleMedium,
                color = onlightSurf,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subject.description,
                style = MaterialTheme.typography.bodyMedium,
                color = onlightSurf,
                fontWeight = FontWeight.Normal,
            )
        }
    }
}



