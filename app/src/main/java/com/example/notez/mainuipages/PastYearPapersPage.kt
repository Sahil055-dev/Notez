package com.example.notez.mainuipages

import android.util.Log
import android.webkit.WebView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.notez.database.AppDatabase
import com.example.notez.database.PastYearPaperEntity
import com.example.notez.pdfviewer.PdfViewer
import com.example.notez.pdfviewer.getPdfDownloadUrl
import com.example.notez.ui.theme.colors
import com.example.notez.ui.theme.onlightSurf
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PastYearPapersPage(
    navController: NavController,
    subjectId: Int,
    db: AppDatabase
) {
    val pastYearPapers = remember { mutableStateOf<List<PastYearPaperEntity>>(emptyList()) }

    // Fetch the past year papers asynchronously
    LaunchedEffect(subjectId) {
        pastYearPapers.value = db.pastYearPaperDao().getPastYearPapersBySubject(subjectId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Past Year Papers") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                modifier = Modifier.shadow(elevation = 12.dp)
            )
        },
        content = { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                if (pastYearPapers.value.isNotEmpty()) {
                    itemsIndexed(pastYearPapers.value) { index, pastYearPaper ->
                        // Display each past paper in a card
                        val backgroundColor = colors[index % colors.size]
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .size(100.dp)
                                .padding(6.dp)
                                .shadow(16.dp)
                                .clickable {
                                    // Navigate to the detailed view to show the PDF
                                    navController.navigate("viewPastYearPaper/${pastYearPaper.id}")
                                },
                            colors = CardDefaults.cardColors(containerColor = backgroundColor)
                        ) {
                            Text(
                                text = "${pastYearPaper.month} ${pastYearPaper.year}",
                                modifier = Modifier.padding(16.dp),
                                fontSize = 18.sp,
                                color = onlightSurf,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                } else {
                    item {
                        Text(
                            text = "No past year papers available",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    )
}






@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewPastYearPaperPage(
    navController: NavController,
    paperId: Int,
    db: AppDatabase
) {
    val pastYearPaper = remember { mutableStateOf<PastYearPaperEntity?>(null) }
    val pdfUrl = remember { mutableStateOf<String?>(null) }
    val loading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val isWebViewLoading = remember { mutableStateOf(true) } // Track WebView loading status
    val webViewRef = remember { mutableStateOf<WebView?>(null) } // Mutable state for WebView reference

    // Fetch the specific past year paper from the Room database
    LaunchedEffect(paperId) {
        val paper = db.pastYearPaperDao().getPastYearPaperById(paperId)
        if (paper != null) {
            pastYearPaper.value = paper

            // Get the PDF URL from Firebase Storage using the relative path
            getPdfDownloadUrl(paper.pdfUrl) { url ->
                if (url != null) {
                    // Encode the URL for Google Drive PDF Viewer
                    pdfUrl.value = "https://drive.google.com/viewerng/viewer?embedded=true&url=$url"
                    Log.d("pdfUrl ","$pdfUrl")
                } else {
                    errorMessage.value = "Failed to load the document from Firebase."
                }
                loading.value = false
            }
        } else {
            // Handle case where the paper is not found in the database
            errorMessage.value = "Paper not found in the database."
            loading.value = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Past Year Paper ${pastYearPaper.value?.month} ${pastYearPaper.value?.year}") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // Reload the WebView by clearing cache and reloading the URL
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
                    // Show loading indicator while fetching the PDF URL
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
                    // Show error message if something went wrong
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
                    // Use the centralized PdfViewer composable to display the PDF
                    PdfViewer(
                        pdfUrl = pdfUrl.value ?: "",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        onLoadingStateChange = { isLoading ->
                            isWebViewLoading.value = isLoading
                        }
                    )

                }
            }
        }
    )
}

















// Log.d("Firebase PDF URL", url)

