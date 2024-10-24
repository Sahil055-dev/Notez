package com.example.notez.uploadpages

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.notez.database.ModuleEntity
import com.example.notez.database.SubjectEntity
import com.example.notez.importantfiles.AuthViewModel
import com.example.notez.importantfiles.NoteMetadata
import com.example.notez.ui.theme.Primary
import com.example.notez.ui.theme.Secondary
import com.example.notez.ui.theme.Tertiary
import com.example.notez.ui.theme.darkfloatingcolor
import com.example.notez.ui.theme.lightfloatingcolor
import com.example.notez.ui.theme.ondarkSurf
import com.example.notez.ui.theme.onlightSurf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storageMetadata
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await



suspend fun uploadPdfToFirebaseStorage(
    fileUri: Uri,
    subject: String,
    module: String,
    noteType: String,
    userId: String,    // Add userId as a parameter
    userName: String,
    fileName: String
): String? {
    return try {
        val sanitizedNotetype = noteType.replace(" ", "_")
        val sanitizedSubject = subject.replace(" ", "_")
        val sanitizedModule = module.replace(" ", "_")

        val storageRef = FirebaseStorage.getInstance().reference
        val filePath = "$sanitizedNotetype/$sanitizedSubject/$sanitizedModule/${System.currentTimeMillis()}_$fileName"
        val fileRef = storageRef.child(filePath)

        // Upload the file and get the download URL
        fileRef.putFile(fileUri).await()
        val downloadUrl = fileRef.downloadUrl.await().toString()

        // Add metadata including userId and userName
        val updatedMetadata = storageMetadata {
            setCustomMetadata("subjectName", sanitizedSubject)
            setCustomMetadata("moduleName", sanitizedModule)
            setCustomMetadata("noteType", sanitizedNotetype)
            setCustomMetadata("userId", userId)         // Store userId in metadata
            setCustomMetadata("userName", userName)     // Store userName in metadata
            setCustomMetadata("fileName", fileName)     // Store the actual file name
            setCustomMetadata("downloadUrl", downloadUrl)
        }

        // Update the file's metadata
        fileRef.updateMetadata(updatedMetadata).await()

        // Return the download URL
        downloadUrl
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}




fun saveNoteMetadata(
    subjectName: String,
    moduleName: String,
    noteType: String,
) {
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser

    if (currentUser != null) {
        val userId = currentUser.uid
        val userName = currentUser.displayName ?: "Unknown User"

        // Create a map of note data, including fileName
        val noteData = hashMapOf(
            "subjectName" to subjectName,
            "module" to moduleName,
            "noteType" to noteType,
            "userName" to userName
        )

        db.collection("notes").add(noteData)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding document", e)
            }
    } else {
        Log.w("Firestore", "Error: No authenticated user found.")
    }
}


// Function to get the file name from the URI
fun getFileNameFromUri(context: Context, uri: Uri): String {
    var fileName = "UnknownFile.pdf"
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1) {
                fileName = it.getString(nameIndex)
            }
        }
    }
    return fileName
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadPage(
    navController: NavController,
    authViewModel: AuthViewModel,
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Upload", "My Uploads")
    val selectedColor = if(isSystemInDarkTheme()) ondarkSurf else onlightSurf


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Upload") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                modifier = Modifier.shadow(elevation = 12.dp)
            )
        },
        content = { padding ->
            Column(modifier = Modifier.padding(padding)) {

                // Use TabRow instead of ScrollableTabRow
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.fillMaxWidth(),
                    // Make the tabs centered in the available space
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title) },
                            selectedContentColor = Tertiary,
                            unselectedContentColor = selectedColor
                        )
                    }
                }

                // Tab content
                when (selectedTabIndex) {
                    0 -> UploadNotePage(
                        navController = navController,
                        paddingValues = padding,
                        authViewModel = authViewModel
                    ) { uploadUrl ->
                        Log.d("NotezNavigation", "Uploaded successfully with URL: $uploadUrl")
                    }
                    1 -> MyUploads(
                        paddingValues = padding,
                        authViewModel = authViewModel
                    )
                }
            }
        }
    )
}





@Composable
fun UploadNotePage(
    navController: NavController,
    paddingValues: PaddingValues,
    authViewModel: AuthViewModel,
    onUploadComplete: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Observe user preferences
    val user by authViewModel.currentUser.collectAsState()
    val userPreferences by authViewModel.userPreferences.collectAsState()

    var selectedSubject by remember { mutableStateOf<SubjectEntity?>(null) }
    var selectedModule by remember { mutableStateOf<ModuleEntity?>(null) }

    // Remove the dropdown and set noteType to "UserNotes"
    val noteType = "UserNotes"

    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadUrl by remember { mutableStateOf<String?>(null) }

    val subjects = remember { mutableStateOf<List<SubjectEntity>>(emptyList()) }
    val modules = remember { mutableStateOf<List<ModuleEntity>>(emptyList()) }

    // Variable to change button text based on file selection
    var fileSelectedText by remember { mutableStateOf("Select File") }

    // Change button color based on file selection
    val butcolor = if (selectedFileUri != null) Color(0XFFcccdcf) else Primary

    // File picker launcher
    val activityResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
        fileSelectedText = if (uri != null) "Selected" else "Select File"
    }

    // Load subjects when user preferences change
    LaunchedEffect(userPreferences) {
        userPreferences?.let { prefs ->
            authViewModel.fetchSubjects(prefs.semester ?: 1, { fetchedSubjects ->
                subjects.value = fetchedSubjects
            }, { error ->
                Toast.makeText(context, "Failed to load subjects", Toast.LENGTH_SHORT).show()
            })
        }
    }

    Column(
        modifier = Modifier
            .padding(8.dp)
            .padding(paddingValues)
            .fillMaxHeight(1f),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Subject Dropdown
        DropdownMenuComponent(
            label = "Select Subject",
            selectedText = selectedSubject?.name ?: "Select Subject",
            items = subjects.value.map { it.name },
            onItemSelected = { selectedName ->
                val selected = subjects.value.find { it.name == selectedName }
                selectedSubject = selected
                selectedSubject?.let { subject ->
                    authViewModel.fetchModules(
                        subject.id,
                        { fetchedModules -> modules.value = fetchedModules },
                        { Toast.makeText(context, "Failed to load modules", Toast.LENGTH_SHORT).show() }
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Module Dropdown
        DropdownMenuComponent(
            label = "Select Module",
            selectedText = selectedModule?.moduleName ?: "Select Module",
            items = modules.value.map { it.moduleName },
            onItemSelected = { selectedName ->
                val selected = modules.value.find { it.moduleName == selectedName }
                selectedModule = selected
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Button to choose file
        Button(
            onClick = {
                activityResultLauncher.launch("application/pdf")
            },
            modifier = Modifier
                .fillMaxWidth(0.92F)
                .size(width = 25.dp, height = 45.dp),
            shape = RoundedCornerShape(25),
            colors = ButtonDefaults.buttonColors(containerColor = butcolor)
        ) {
            // Update text to "Selected" or "Select File" based on the file selection
            Text(fileSelectedText, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Upload Button
        if (isUploading) {
            LinearProgressIndicator()
        } else {
            Button(
                onClick = {
                    if (selectedFileUri != null && selectedSubject != null && selectedModule != null) {
                        coroutineScope.launch {
                            isUploading = true
                            val subjectName = selectedSubject?.name ?: "UnknownSubject"
                            val moduleName = selectedModule?.moduleName ?: "UnknownModule"
                            val userName = user?.displayName ?: "Anonymous"
                            val userId = user?.uid ?: "000"

                            // Get the actual file name from the URI
                            val fileName = getFileNameFromUri(context, selectedFileUri!!)

                            // Call uploadPdfToFirebaseStorage with userName and fileName
                            uploadUrl = uploadPdfToFirebaseStorage(
                                fileUri = selectedFileUri!!,
                                subject = subjectName,
                                module = moduleName,
                                noteType = noteType,
                                userName = userName,
                                userId = userId,
                                fileName = fileName,
                            )
                            if (uploadUrl != null) {
                                saveNoteMetadata(subjectName, moduleName, noteType)
                                Toast.makeText(context, "PDF Successfully Uploaded!", Toast.LENGTH_SHORT).show()
                                onUploadComplete(uploadUrl!!)
                                navController.popBackStack()
                            }
                            isUploading = false
                        }
                    } else {
                        Toast.makeText(context, "Please select a file, subject, and module first", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.92F)
                    .size(width = 25.dp, height = 50.dp),
                shape = RoundedCornerShape(25),
                colors = ButtonDefaults.buttonColors(Secondary)
            ) {
                Text("Upload", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.width(5.dp))
                Icon(imageVector = Icons.Filled.Upload, contentDescription = "Upload button")
            }
        }
    }
}








@Composable
fun MyUploads(
    paddingValues: PaddingValues,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val user by authViewModel.currentUser.collectAsState() // Observe current user
    var uploadedNotes by remember { mutableStateOf<List<NoteMetadata>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch the user's uploaded notes when the user changes
    LaunchedEffect(user) {
        user?.let { currentUser ->
            try {
                val notes = authViewModel.fetchUserUploadedNotes(
                    userId = currentUser.uid
                )
                uploadedNotes = notes
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
            } finally {
                isLoading = false
            }
        }
    }

    // UI layout code remains the same...
    Column(
        modifier = Modifier
            .padding(16.dp)
            .padding(paddingValues),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            Box( // Wrap with Box for centering
                modifier = Modifier.fillMaxWidth(), // Ensure Box fills width
                contentAlignment = Alignment.Center // Center content within Box
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(text = "We're fetching your data, wait a moment.")
                }
            }

        }

        if (errorMessage != null) {
            Text(text = "Error: $errorMessage", color = Color.Red)
        }

        if (uploadedNotes.isNotEmpty()) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(uploadedNotes) { note ->
                    UploadedNoteItem(
                        note = note,
                        onClickDownload = {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse(note.downloadUrl)
                            }
                            context.startActivity(intent)
                        },
                        onClickDelete = { noteType, subjectName, moduleName, fileName ->
                            authViewModel.deleteNote(
                                noteType = noteType,
                                subjectName = subjectName,
                                moduleName = moduleName,
                                fileName = fileName,
                                currentNotes = uploadedNotes,
                                onDeleteSuccess = { updatedNotes -> uploadedNotes = updatedNotes },
                                onDeleteFailure = { errorMessage = it.localizedMessage }
                            )
                        }
                    )
                }
            }
        } else if (!isLoading && errorMessage == null) {
            Text(text = "No uploads found")
        }
    }
}


@Composable
fun UploadedNoteItem(
    note: NoteMetadata,
    onClickDownload: () -> Unit,
    onClickDelete: (String, String, String, String) -> Unit // Pass individual components
) {
    var showMoreInfo by remember { mutableStateOf(false) }  // State to track if more info is shown

    val containerBackground = if (isSystemInDarkTheme()) darkfloatingcolor else lightfloatingcolor
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClickDownload),
        colors = CardDefaults.cardColors(
            containerColor = containerBackground
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            // Only file name and note type are initially visible
            Text(text = "File Name: ${note.fileName}", fontWeight = FontWeight.SemiBold)
            Text(text = "Uploaded in ${note.noteType}")
            Spacer(modifier = Modifier.height(8.dp))

            // Button to toggle more info visibility

            // Conditional block showing more information if `showMoreInfo` is true
            if (showMoreInfo) {
                Column {
                    Text(text = "Subject : ${note.subjectName}")
                    Text(text = "Module : ${note.moduleName}")
                    Text("Date : ${note.lastModifiedDate}")

                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onClickDownload,
                    shape = RoundedCornerShape(25)
                ) {
                    Icon(imageVector = Icons.Filled.Download, contentDescription = "Download")
                }

                Button(onClick = { showMoreInfo = !showMoreInfo }, shape = RoundedCornerShape(25)) {
                    Icon(imageVector = Icons.Filled.Info, contentDescription = "More Info" )
                }

                Button(
                    onClick = {
                        // Pass the noteType, subjectName, moduleName, and fileName for deletion
                        onClickDelete(note.noteType, note.subjectName, note.moduleName, note.fileName)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0XFFf75f54)),
                    shape = RoundedCornerShape(25)
                ) {
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}









@Composable
fun DropdownMenuComponent(
    label: String,
    selectedText: String,
    items: List<String>,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .fillMaxWidth()
        .wrapContentSize(Alignment.TopStart)) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}




