package com.example.notez.pdfviewer


import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

fun getPdfDownloadUrl(filePath: String, callback: (String?) -> Unit) {
    // Reference to Firebase Storage
    val storageRef: StorageReference = FirebaseStorage.getInstance().reference

    // Reference to the PDF file in Firebase Storage
    val fileRef = storageRef.child(filePath)

    // Get the download URL
    fileRef.downloadUrl.addOnSuccessListener { uri ->
        callback(uri.toString()) // Pass the URL back via the callback
    }.addOnFailureListener { exception ->
        callback(null) // If it fails, pass null
    }
}

@Composable
fun PdfViewer(
    pdfUrl: String,
    modifier: Modifier = Modifier,
    onLoadingStateChange: (Boolean) -> Unit,
    onWebViewCreated: (WebView) -> Unit // Pass WebView reference to parent
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

                loadUrl(pdfUrl)
                onWebViewCreated(this) // Pass the WebView instance back to parent
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










