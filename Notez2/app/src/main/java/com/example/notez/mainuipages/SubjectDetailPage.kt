package com.example.notez.mainuipages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.notez.database.AppDatabase
import com.example.notez.database.ModuleEntity
import com.example.notez.database.SubjectEntity
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import com.example.notez.ui.theme.colors
import com.example.notez.ui.theme.onlightSurf

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun SubjectDetailPage(
    navController: NavController,
    subjectId: Int,
    db: AppDatabase
) {
    val subject = remember { mutableStateOf<SubjectEntity?>(null) }
    val modules = remember { mutableStateOf<List<ModuleEntity>>(emptyList()) }

    // Fetch the subject and modules asynchronously
    LaunchedEffect(subjectId) {
        subject.value = db.subjectDao().getSubjectById(subjectId)
        modules.value = db.moduleDao().getModulesForSubject(subjectId)
    }

    // Define colors for cards


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text( "${subject.value?.name}") }, // Show subject name if available
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                modifier = Modifier.shadow(elevation = 12.dp)
            )
        },
        content = { padding ->
            // Display a list of SubjectDetailCards similar to ClassCard on HomePage
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Notes Card
                    SubjectDetailCard(
                        title = "Notes",
                        description = "View Expert Notes from Teachers",
                        backgroundColor = colors[0],
                        onClick = {
                            navController.navigate("notes/$subjectId")
                        }
                    )
                }
                item {
                    // User Uploaded Notes Card
                    SubjectDetailCard(
                        title = "User Uploaded Notes",
                        description = "Browse User-uploaded notes",
                        backgroundColor = colors[1],
                        onClick = {
                            navController.navigate("userUploadedNotes/$subjectId")
                        }
                    )
                }
                item {
                    // Past Year Papers Card
                    SubjectDetailCard(
                        title = "Past Year Papers",
                        description = "Access past year question papers",
                        backgroundColor = colors[2],
                        onClick = {
                            navController.navigate("pastYearPapers/$subjectId")
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun SubjectDetailCard(
    title: String,
    description: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(125.dp)
            .clickable(onClick = onClick),
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
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = onlightSurf,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = onlightSurf,
                fontWeight = FontWeight.Normal
            )
        }
    }
}



@Composable
fun ModuleCard(
    module: ModuleEntity,
    navController: NavController,
    backgroundColor: Color,
    isUserUploaded: Boolean = false // Pass a flag to differentiate between notes and user-uploaded notes
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .size(100.dp)
            .clickable {
                // Navigate to the appropriate notes page
                val route = if (isUserUploaded) {
                    "userUploadedNotesList/${module.id}"
                } else {
                    "moduleNotes/${module.id}"
                }
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
                text = module.moduleName,
                style = MaterialTheme.typography.titleMedium,
                color = onlightSurf,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}


//"moduleDetail/${module.id}






