package com.example.notez.mainuipages


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.notez.bookmark.BookmarkViewModel
import com.example.notez.database.AppDatabase
import com.example.notez.database.ModuleEntity
import com.example.notez.database.SubjectEntity
import com.example.notez.ui.theme.colors
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserUploadedNotesPage(
    navController: NavController,
    subjectId: Int,
    db: AppDatabase,
) {
    val modules = remember { mutableStateOf<List<ModuleEntity>>(emptyList()) }
    val subject = remember { mutableStateOf<SubjectEntity?>(null) }
    // Fetch user-uploaded modules from Room database
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
                            isUserUploaded = true  // Set to false for normal Notes
                        )
                    }
                }
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserUploadedNotesListPage(
    navController: NavController,
    moduleName: String,
    subjectName: String,
    bookmarkViewModel: BookmarkViewModel = koinInject() // Inject the ViewModel
) {
    data class NoteData(val downloadUrl: String, val metadata: StorageMetadata?)
    val notes = remember { mutableStateOf<List<NoteData>>(emptyList()) } // List of PDF URLs
    val loading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val bookmarks by bookmarkViewModel.bookmarks.observeAsState(emptyList()) // Observe bookmarks

    // Fetch user-uploaded PDFs from Firebase Storage
    LaunchedEffect(moduleName) {
        val storageRef = FirebaseStorage.getInstance().reference
        val pdfRef = storageRef.child("UserNotes/$subjectName/$moduleName")

        pdfRef.listAll()
            .addOnSuccessListener { listResult ->
                if (listResult.items.isEmpty()) {
                    loading.value = false // No items found, stop loading
                } else {
                    listResult.items.forEach { storageReference ->
                        // Fetch the download URL and metadata for each PDF
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
                title = { Text("User Uploaded Notes") },
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
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No Notes Found!")
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
                                metadata = noteData.metadata,  // User-uploaded notes may not have metadata
                                navController = navController,
                                backgroundColor = backgroundColor,
                                isBookmarked = isBookmarked,
                                onBookmarkClick = { shouldBookmark ->
                                    if (shouldBookmark) {
                                        bookmarkViewModel.addBookmark(noteData.downloadUrl,
                                            noteData.metadata?.getCustomMetadata("fileName"),
                                            noteData.metadata?.creationTimeMillis?.let {
                                                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
                                                    Date(it)
                                                )
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







