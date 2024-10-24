package com.example.notez.mainuipages

import android.webkit.WebView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.notez.database.AppDatabase
import com.example.notez.database.CurriculumNoteEntity
import com.example.notez.database.ModuleEntity
import com.example.notez.database.SubjectEntity
import com.example.notez.database.UserUploadedNoteEntity
import com.example.notez.pdfviewer.PdfViewer
import com.example.notez.pdfviewer.getPdfDownloadUrl
import com.example.notez.ui.theme.colors
import com.example.notez.ui.theme.onlightSurf
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesPage(
    navController: NavController,
    subjectId: Int,
    db: AppDatabase
) {
    val modules = remember { mutableStateOf<List<ModuleEntity>>(emptyList()) }
    val subject = remember { mutableStateOf<SubjectEntity?>(null) }
    val notes = remember { mutableStateOf<List<CurriculumNoteEntity>>(emptyList()) }

    // Fetch modules and subject data from Room
    LaunchedEffect(subjectId) {
        modules.value = db.moduleDao().getModulesForSubject(subjectId)
        subject.value = db.subjectDao().getSubjectById(subjectId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${subject.value?.name} Notes") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(modules.value) { index, module ->
                    val backgroundColor = colors[index % colors.size]
                    val isUserUploaded = false
                    ModuleCard(module, navController, backgroundColor,isUserUploaded)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleNotesListPage(
    navController: NavController,
    moduleId: Int,
    db: AppDatabase
) {
    val notes = remember { mutableStateOf<List<CurriculumNoteEntity>>(emptyList()) }

    // Fetch notes for the module from the Room database
    LaunchedEffect(moduleId) {
        notes.value = db.curriculumNoteDao().getNotes(moduleId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Module Notes") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(notes.value) {index, note ->
                    val backgroundColor = colors[index % colors.size]
                    NoteCard(note, navController, backgroundColor)
                }
            }
        }
    )
}

@Composable
fun NoteCard(note: CurriculumNoteEntity, navController: NavController, backgroundColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .size(100.dp)
            .clickable(onClick = { navController.navigate("viewPdf/${note.id}") }),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)

    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = note.title,
                style = MaterialTheme.typography.titleMedium,
                color = onlightSurf,
                fontWeight = FontWeight.SemiBold,
            )
            Text(text = "View PDF", color = Color.Blue)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewPdfPage(
    navController: NavController,
    noteId: Int,
    db: AppDatabase
) {
    val note = remember { mutableStateOf<CurriculumNoteEntity?>(null) }
    val pdfUrl = remember { mutableStateOf<String?>(null) }
    val loading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val isWebViewLoading = remember { mutableStateOf(true) }
    val webViewRef = remember { mutableStateOf<WebView?>(null) }

    // Fetch the note from the Room database
    LaunchedEffect(noteId) {
        val fetchedNote = db.curriculumNoteDao().getNoteById(noteId)  // Corrected method for single entity
        if (fetchedNote != null) {
            note.value = fetchedNote

            // Get the PDF URL from Firebase Storage
            getPdfDownloadUrl(fetchedNote.contentUrl) { url ->
                if (url != null) {
                    val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                    pdfUrl.value = "https://drive.google.com/viewerng/viewer?embedded=true&url=$encodedUrl"
                } else {
                    errorMessage.value = "Failed to load the document from Firebase."
                }
                loading.value = false
            }
        } else {
            errorMessage.value = "Note not found in the database."
            loading.value = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(note.value?.title ?: "View PDF") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        webViewRef.value?.apply {
                            clearCache(true)
                            clearHistory()
                            pdfUrl.value?.let { loadUrl(it) }
                        }
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh PDF")
                    }
                }
            )
        },
        content = { padding ->
            when {
                loading.value -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                errorMessage.value != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(errorMessage.value ?: "Unknown error", color = Color.Red)
                    }
                }
                pdfUrl.value != null -> {
                    PdfViewer(
                        pdfUrl = pdfUrl.value ?: "",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        onLoadingStateChange = { isLoading -> isWebViewLoading.value = isLoading },
                        onWebViewCreated = { webViewRef.value = it }
                    )
                }
            }
        }
    )
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserUploadedNotesPage(
    navController: NavController,
    subjectId: Int,
    db: AppDatabase
) {
    val modules = remember { mutableStateOf<List<ModuleEntity>>(emptyList()) }
    val subject = remember { mutableStateOf<SubjectEntity?>(null) }

    LaunchedEffect(subjectId) {
        modules.value = db.moduleDao().getModulesForSubject(subjectId)
        subject.value = db.subjectDao().getSubjectById(subjectId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${subject.value?.name} User Uploaded Notes") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(modules.value) { index, module ->
                    val backgroundColor = colors[index % colors.size]
                    val isUserUploaded = true
                    ModuleCard(module, navController, backgroundColor, isUserUploaded)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserUploadedNotesListPage(
    navController: NavController,
    moduleId: Int,
    db: AppDatabase
) {
    val userUploadedNotes = remember { mutableStateOf<List<UserUploadedNoteEntity>>(emptyList()) }

    // Fetch user-uploaded notes for the module from the Room database
    LaunchedEffect(moduleId) {
        userUploadedNotes.value = db.userUploadedNoteDao().getUserUploadedNotes(moduleId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Uploaded Notes") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(userUploadedNotes.value) { index, note ->
                    val backgroundColor = colors[index % colors.size]
                    UserUploadedNoteCard( note, navController,  backgroundColor)
                }
            }
        }
    )
}

@Composable
fun UserUploadedNoteCard(note: UserUploadedNoteEntity, navController: NavController, backgroundColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .size(100.dp)
            .clickable(onClick = { navController.navigate("viewUserUploadedPdf/${note.id}") }),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = note.title,
                style = MaterialTheme.typography.titleMedium,
                color = onlightSurf,
                fontWeight = FontWeight.SemiBold,
            )
            Text(text = "View PDF", color = Color.Blue)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewUserUploadedPdfPage(
    navController: NavController,
    noteId: Int,
    db: AppDatabase
) {
    val note = remember { mutableStateOf<UserUploadedNoteEntity?>(null) }
    val pdfUrl = remember { mutableStateOf<String?>(null) }
    val loading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val isWebViewLoading = remember { mutableStateOf(true) }
    val webViewRef = remember { mutableStateOf<WebView?>(null) }

    // Fetch the user-uploaded note from the Room database
    LaunchedEffect(noteId) {
        val fetchedNote = db.userUploadedNoteDao().getNoteById(noteId)  // Corrected method for single entity
        if (fetchedNote != null) {
            note.value = fetchedNote

            // Get the PDF URL from Firebase Storage
            getPdfDownloadUrl(fetchedNote.contentUrl) { url ->
                if (url != null) {
                    val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                    pdfUrl.value = "https://drive.google.com/viewerng/viewer?embedded=true&url=$encodedUrl"
                } else {
                    errorMessage.value = "Failed to load the document from Firebase."
                }
                loading.value = false
            }
        } else {
            errorMessage.value = "Note not found in the database."
            loading.value = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(note.value?.title ?: "View PDF") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        webViewRef.value?.apply {
                            clearCache(true)
                            clearHistory()
                            pdfUrl.value?.let { loadUrl(it) }
                        }
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh PDF")
                    }
                }
            )
        },
        content = { padding ->
            when {
                loading.value -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                errorMessage.value != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(errorMessage.value ?: "Unknown error", color = Color.Red)
                    }
                }
                pdfUrl.value != null -> {
                    PdfViewer(
                        pdfUrl = pdfUrl.value ?: "",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        onLoadingStateChange = { isLoading -> isWebViewLoading.value = isLoading },
                        onWebViewCreated = { webViewRef.value = it }
                    )
                }
            }
        }
    )
}


