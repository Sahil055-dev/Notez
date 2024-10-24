package com.example.notez.mainuipages

import android.net.Uri
import android.util.Log
import android.webkit.WebView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.notez.bookmark.BookmarkViewModel
import com.example.notez.database.AppDatabase
import com.example.notez.database.ModuleEntity
import com.example.notez.database.SubjectEntity
import com.example.notez.pdfviewer.PdfViewer
import com.example.notez.pdfviewer.getPdfDownloadUrl
import com.example.notez.ui.theme.colors
import com.example.notez.ui.theme.onlightSurf
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import org.koin.compose.koinInject
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesPage(
    navController: NavController,
    subjectId: Int,
    db: AppDatabase  // Add Room database as parameter
) {
    val modules = remember { mutableStateOf<List<ModuleEntity>>(emptyList()) }
    val subject = remember { mutableStateOf<SubjectEntity?>(null) }
    // Fetch modules for the subject from Room database
    LaunchedEffect(subjectId) {
        modules.value = db.moduleDao().getModulesForSubject(subjectId)
        subject.value =  db.subjectDao().getSubjectById(subjectId)// Fetch from Room
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Modules") },
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
                // Show Modules
                itemsIndexed(modules.value) { index, module ->
                    val backgroundColor = colors[index % colors.size]
                    val currentsubject = subject.value
                    if (currentsubject != null) {
                        ModuleCard(
                            module = module,
                            subject = currentsubject,
                            navController = navController,
                            backgroundColor = backgroundColor,
                            isUserUploaded = false  // Set to false for normal Notes
                        )
                    }
                }
            }
        }
    )
}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleNotesListPage(
    navController: NavController,
    moduleName: String,
    subjectName: String,
    bookmarkViewModel: BookmarkViewModel = koinInject() // Inject the ViewModel
) {
    // Data class to hold PDF URLs and metadata
    data class NoteData(val downloadUrl: String, val metadata: StorageMetadata?)

    val notes = remember { mutableStateOf<List<NoteData>>(emptyList()) }  // List of NoteData
    val loading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val bookmarks by bookmarkViewModel.bookmarks.observeAsState(emptyList()) // Observe bookmarks

    // Fetch PDFs for the module from Firebase Storage
    LaunchedEffect(moduleName) {
        val storageRef = FirebaseStorage.getInstance().reference
        val pdfRef = storageRef.child("Notes/$subjectName/$moduleName")

        pdfRef.listAll()
            .addOnSuccessListener { listResult ->
                listResult.items.forEach { storageReference ->
                    storageReference.downloadUrl.addOnSuccessListener { uri ->
                        storageReference.metadata.addOnSuccessListener { metadata ->
                            val noteData = NoteData(downloadUrl = uri.toString(), metadata = metadata)
                            notes.value = notes.value + noteData  // Add the URL and metadata to the list
                            loading.value = false
                        }.addOnFailureListener {
                            errorMessage.value = "Failed to load some metadata."
                            loading.value = false
                        }
                    }.addOnFailureListener {
                        errorMessage.value = "Failed to load some PDFs."
                        loading.value = false
                    }
                }
                if (listResult.items.isEmpty()) {
                    loading.value = false // Stop loading if there are no items
                }
            }
            .addOnFailureListener {
                errorMessage.value = "Error fetching PDF URLs."
                loading.value = false
            }
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
                notes.value.isEmpty() -> {
                    // Display "No notes available" when the list is empty
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No Notes found!")
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        itemsIndexed(notes.value) { index, noteData ->
                            val backgroundColor = colors[index % colors.size]
                            val isBookmarked = bookmarks.any { it.noteUrl == noteData.downloadUrl }

                            NoteCard(
                                noteUrl = noteData.downloadUrl,
                                metadata = noteData.metadata,
                                navController = navController,
                                backgroundColor = backgroundColor,
                                isBookmarked = isBookmarked,
                                onBookmarkClick = { shouldBookmark ->
                                    // Update the bookmark state
                                    if (shouldBookmark) {
                                        bookmarkViewModel.addBookmark(
                                            noteData.downloadUrl,
                                            noteData.metadata?.getCustomMetadata("fileName"),
                                            noteData.metadata?.creationTimeMillis?.let {
                                                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(it))
                                            },
                                            noteData.metadata?.getCustomMetadata("userName")
                                        )
                                    } else {
                                        bookmarks.find { it.noteUrl == noteData.downloadUrl }?.let { bookmark ->
                                            bookmarkViewModel.removeBookmark(bookmark)
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}














@Composable
fun NoteCard(
    noteUrl: String,
    metadata: StorageMetadata?,
    navController: NavController,
    backgroundColor: Color,
    isBookmarked: Boolean,
    onBookmarkClick: (Boolean) -> Unit  // Callback to toggle bookmark
) {
    val encodedUrl = URLEncoder.encode(noteUrl, StandardCharsets.UTF_8.toString())
    val Type = metadata?.getCustomMetadata("noteType")
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(105.dp)
            .clickable(onClick = {
                navController.navigate("viewPdfPage/$encodedUrl")
            }),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = metadata?.getCustomMetadata("fileName") ?: "Unknown File",
                    maxLines = 1,
                    fontSize = 18.sp,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.SemiBold,
                    color = onlightSurf
                )

                Spacer(modifier = Modifier.height(2.dp))
                if (Type != "Notes") {
                    Text(
                        text = "Uploaded by: ${metadata?.getCustomMetadata("userName") ?: "Unknown User"}",
                        color = Color(0xFF1E1E1E)
                    )

                    Text(
                        text = "Uploaded on: ${
                            metadata?.creationTimeMillis?.let {
                                SimpleDateFormat(
                                    "yyyy-MM-dd HH:mm",
                                    Locale.getDefault()
                                ).format(Date(it))
                            } ?: "Unknown Upload Date"
                        }",
                        color = Color(0xFF1E1E1E)
                    )
                }
            }

            // Toggle the bookmark icon based on isBookmarked state
            IconButton(onClick = { onBookmarkClick(!isBookmarked) }) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = if (isBookmarked) "Remove Bookmark" else "Add Bookmark",
                    tint = onlightSurf
                )
            }
        }
    }
}
















@Composable
fun ModuleCard(
    module: ModuleEntity,
    subject: SubjectEntity,
    navController: NavController,
    backgroundColor: Color,
    isUserUploaded: Boolean  // Pass a flag to differentiate between notes and user-uploaded notes
) {
    // Sanitize subject and module names (replace spaces with underscores)
    val sanitizedSubjectName = subject.name.replace(" ", "_")
    val sanitizedModuleName = module.moduleName.replace(" ", "_")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .size(100.dp)
            .clickable {
                // Navigate to the appropriate notes page with sanitized values
                val route = if (isUserUploaded) {
                    "userUploadedNotes/$sanitizedSubjectName/$sanitizedModuleName"
                } else {
                    "moduleNotes/$sanitizedSubjectName/$sanitizedModuleName"
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
                text = module.moduleName,  // Keep the UI display as the original name (with spaces)
                style = MaterialTheme.typography.titleMedium,
                color = onlightSurf,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}




