package com.example.notez.pdfviewer


import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

fun getPdfDownloadUrl(filePath: String, callback: (String?) -> Unit) {
    val storageRef: StorageReference = FirebaseStorage.getInstance().reference
    val fileRef = storageRef.child(filePath)

    fileRef.downloadUrl.addOnSuccessListener { uri ->
        val fullUrl = uri.toString() // Get the full Firebase Storage URL
        // Properly encode the entire Firebase URL before embedding in the Google Drive viewer
        val encodedUrl = URLEncoder.encode(fullUrl, StandardCharsets.UTF_8.toString())
        callback(encodedUrl) // Pass the fully encoded URL to the callback
    }.addOnFailureListener { exception ->
        callback(null) // Handle failure case
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewPdfPage(
    navController: NavController,
    noteUrl: String  // This will be the encoded URL passed from NoteCard
) {
    val loading = remember { mutableStateOf(true) }
    val refreshKey = remember { mutableStateOf(0) }  // This will be the key to refresh the WebView

    Log.d("Received Encoded Note Url", noteUrl ?: "No URL passed")

    // Keep encoding the URL as needed.
    val encodedUrl = URLEncoder.encode(noteUrl, StandardCharsets.UTF_8.toString())

    // Construct the Google PDF Viewer URL with the encoded URL
    val pdfUrl = "https://drive.google.com/viewerng/viewer?embedded=true&url=$encodedUrl"
    Log.d("PDF Viewer Url", pdfUrl)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("View PDF") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // Increment the refreshKey to trigger WebView recreation
                        refreshKey.value++
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh PDF")
                    }
                }
            )
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                // Pass refreshKey to PdfViewer to force WebView recreation
                key(refreshKey.value) {
                    PdfViewer(
                        pdfUrl = pdfUrl,
                        modifier = Modifier.fillMaxSize(),
                        onLoadingStateChange = { isLoading ->
                            loading.value = isLoading
                        }
                    )
                }

                // Show loading spinner based on `loading` state
                if (loading.value) {
                    CircularProgressIndicator()  // Show progress bar while loading
                }
            }
        }
    )
}







@Composable
fun PdfViewer(
    pdfUrl: String,
    modifier: Modifier = Modifier,
    onLoadingStateChange: (Boolean) -> Unit
) {
    val isWebViewLoading = remember { mutableStateOf(true) }

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        isWebViewLoading.value = true
                        onLoadingStateChange(true)
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        isWebViewLoading.value = false
                        onLoadingStateChange(false)
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        super.onReceivedError(view, request, error)
                        // Display custom error message or retry logic here
                        view?.loadDataWithBaseURL(
                            null,
                            """
                            <html>
                                <body style="display:flex; align-items:center; justify-content:center; height:100vh;">
                                    <h3 style="color: red;">Failed to load PDF, please try again!</h3>
                                </body>
                            </html>
                            """.trimIndent(),
                            "text/html",
                            "UTF-8",
                            null
                        )
                        isWebViewLoading.value = false
                        onLoadingStateChange(false)
                    }
                }
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true

                // Load the Firebase URL
                loadUrl(pdfUrl)
            }
        },
        modifier = modifier.fillMaxSize()
    )

    // Show a loading indicator while the WebView is loading
    if (isWebViewLoading.value) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}
















