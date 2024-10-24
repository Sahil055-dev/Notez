package com.example.notez.uploadpages

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.notez.database.ModuleEntity
import com.example.notez.database.SubjectEntity
import com.example.notez.importantfiles.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

suspend fun uploadPdfToFirebaseStorage(fileUri: Uri, fileName: String): String? {
    return try {
        val storageRef = FirebaseStorage.getInstance().reference
        val fileRef = storageRef.child("notes/$fileName.pdf")
        val uploadTask = fileRef.putFile(fileUri).await()
        val downloadUrl = fileRef.downloadUrl.await().toString()
        downloadUrl  // Return the URL of the uploaded file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}



fun saveNoteMetadata(subject: String, module: String, noteType: String, downloadUrl: String) {
    val db = FirebaseFirestore.getInstance()
    val noteData = hashMapOf(
        "subject" to subject,
        "module" to module,
        "noteType" to noteType,
        "downloadUrl" to downloadUrl
    )

    db.collection("notes").add(noteData)
        .addOnSuccessListener { documentReference ->
            Log.d("Firestore", "DocumentSnapshot written with ID: ${documentReference.id}")
        }
        .addOnFailureListener { e ->
            Log.w("Firestore", "Error adding document", e)
        }
}


suspend fun getNotesForModule(subject: String, module: String): List<String> {
    val db = FirebaseFirestore.getInstance()
    val notesQuery = db.collection("notes")
        .whereEqualTo("subject", subject)
        .whereEqualTo("module", module)
        .get().await()

    return notesQuery.documents.map { it.getString("downloadUrl") ?: "" }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadNotePage(
    navController: NavController,
    authViewModel: AuthViewModel, // Use AuthViewModel to fetch subjects and modules
    onUploadComplete: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Observe user preferences (branch, year, semester) from AuthViewModel
    val userPreferences by authViewModel.userPreferences.collectAsState()

    var selectedSubject by remember { mutableStateOf<SubjectEntity?>(null) }
    var selectedModule by remember { mutableStateOf<ModuleEntity?>(null) }
    var noteType by remember { mutableStateOf("Notes") } // Default to "Notes"

    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadUrl by remember { mutableStateOf<String?>(null) }

    val subjects = remember { mutableStateOf<List<SubjectEntity>>(emptyList()) }
    val modules = remember { mutableStateOf<List<ModuleEntity>>(emptyList()) }

    // Dropdown expanded states
    var isSubjectDropdownExpanded by remember { mutableStateOf(false) }
    var isModuleDropdownExpanded by remember { mutableStateOf(false) }
    var isNoteTypeDropdownExpanded by remember { mutableStateOf(false) }

    // File picker launcher
    val activityResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
    }

    // Load subjects when user preferences change
    LaunchedEffect(userPreferences) {
        userPreferences?.let { prefs ->
            // Fetch subjects based on the user's semester
            authViewModel.fetchSubjects(prefs.semester ?: 1, { fetchedSubjects ->
                subjects.value = fetchedSubjects
            }, { error ->
                Toast.makeText(context, "Failed to load subjects", Toast.LENGTH_SHORT).show()
            })
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Upload Note")

        Spacer(modifier = Modifier.height(16.dp))

        // Subject Dropdown
        ExposedDropdownMenuBox(
            expanded = isSubjectDropdownExpanded,
            onExpandedChange = { isSubjectDropdownExpanded = !isSubjectDropdownExpanded }
        ) {
            OutlinedTextField(
                value = selectedSubject?.name ?: "Select Subject",
                onValueChange = { /* Nothing to change manually */ },
                label = { Text("Subject") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )
            ExposedDropdownMenu(
                expanded = isSubjectDropdownExpanded,
                onDismissRequest = { isSubjectDropdownExpanded = false }
            ) {
                subjects.value.forEach { subject ->
                    DropdownMenuItem(
                        onClick = {
                            selectedSubject = subject
                            isSubjectDropdownExpanded = false
                            // Fetch modules for the selected subject
                            authViewModel.fetchModules(
                                subject.id,
                                { fetchedModules -> modules.value = fetchedModules }
                            ) { error ->
                                Toast.makeText(
                                    context,
                                    "Failed to load modules",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        text = {
                            Text(subject.name) // Pass Text as part of the 'text' parameter
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Module Dropdown
        ExposedDropdownMenuBox(
            expanded = isModuleDropdownExpanded,
            onExpandedChange = { isModuleDropdownExpanded = !isModuleDropdownExpanded }
        ) {
            OutlinedTextField(
                value = selectedModule?.moduleName ?: "Select Module",
                onValueChange = { /* Nothing to change manually */ },
                label = { Text("Module") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )
            ExposedDropdownMenu(
                expanded = isModuleDropdownExpanded,
                onDismissRequest = { isModuleDropdownExpanded = false }
            ) {
                modules.value.forEach { module ->
                    DropdownMenuItem(
                        onClick = {
                            selectedModule = module
                            isModuleDropdownExpanded = false
                        },
                        text = {
                            Text(module.moduleName) // Pass Text as part of the 'text' parameter
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dropdown to select Notes or UserUploadedNotes
        ExposedDropdownMenuBox(
            expanded = isNoteTypeDropdownExpanded,
            onExpandedChange = { isNoteTypeDropdownExpanded = !isNoteTypeDropdownExpanded }
        ) {
            OutlinedTextField(
                value = noteType,
                onValueChange = { /* Nothing to change manually */ },
                label = { Text("Upload To") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )
            ExposedDropdownMenu(
                expanded = isNoteTypeDropdownExpanded,
                onDismissRequest = { isNoteTypeDropdownExpanded = false }
            ) {
                DropdownMenuItem(
                    onClick = {
                        noteType = "Notes"
                        isNoteTypeDropdownExpanded = false
                    },
                    text = {
                        Text("Notes") // Wrap the text content in the 'text' parameter
                    }
                )

                DropdownMenuItem(
                    onClick = {
                        noteType = "UserUploadedNotes"
                        isNoteTypeDropdownExpanded = false
                    },
                    text = {
                        Text("User Uploaded Notes") // Wrap the text content in the 'text' parameter
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Button to choose file
        Button(onClick = {
            activityResultLauncher.launch("application/pdf")
        }) {
            Text("Select File")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Upload Button
        if (isUploading) {
            CircularProgressIndicator()
        } else {
            Button(onClick = {
                if (selectedFileUri != null && selectedSubject != null && selectedModule != null) {
                    coroutineScope.launch {
                        isUploading = true
                        val fileName = "${selectedSubject?.name}-${selectedModule?.moduleName}-${System.currentTimeMillis()}"
                        uploadUrl = uploadPdfToFirebaseStorage(selectedFileUri!!, fileName)
                        if (uploadUrl != null) {
                            saveNoteMetadata(
                                selectedSubject!!.name,
                                selectedModule!!.moduleName,
                                noteType,
                                uploadUrl!!
                            )
                            onUploadComplete(uploadUrl!!)
                            navController.popBackStack() // Navigate back on completion
                        }
                        isUploading = false
                    }
                } else {
                    Toast.makeText(context, "Please select a file, subject, and module first", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Upload")
            }
        }
    }
}



