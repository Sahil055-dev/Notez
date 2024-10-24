package com.example.notez.bookmark

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.notez.database.BookmarkEntity
import com.example.notez.ui.theme.colors
import com.example.notez.ui.theme.onlightSurf
import org.koin.compose.koinInject
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkPage(navController: NavController, bookmarkViewModel: BookmarkViewModel = koinInject()) {
    // Observe the bookmarks using LiveData with observeAsState
    val bookmarks by bookmarkViewModel.bookmarks.observeAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bookmarks") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { padding ->
            if (bookmarks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No bookmarks available")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(bookmarks) {index,bookmark  ->
                        val backgroundColor = colors[index % colors.size]
                        BookmarkCard(bookmark, navController, backgroundColor) {
                            // Remove the bookmark when the filled bookmark icon is clicked
                            bookmarkViewModel.removeBookmark(bookmark)
                        }
                    }
                }
            }
        }
    )
}







@Composable
fun BookmarkCard(
    bookmark: BookmarkEntity,
    navController: NavController,
    backgroundColor: Color,
    onRemoveClick: () -> Unit
) {
    // Encode URL for navigation
    val encodedUrl = URLEncoder.encode(bookmark.noteUrl, StandardCharsets.UTF_8.toString())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable {
                navController.navigate("viewPdfPage/$encodedUrl")
            },
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Information about the bookmark (file name, upload date, user)
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = bookmark.fileName ?: "Unknown File",
                    color = onlightSurf,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = bookmark.uploadDate ?: "Unknown Upload Date",
                    color = Color(0xFF4B4B4B)
                )

                Text(
                    text = "Uploaded by: ${bookmark.userName ?: "Unknown User"}",
                    color = Color(0xFF4B4B4B)
                )
            }

            // Remove bookmark icon (filled bookmark)
            IconButton(onClick = { onRemoveClick() }) {
                Icon(
                    imageVector = Icons.Default.Bookmark, // Filled bookmark icon
                    contentDescription = "Remove Bookmark",
                    tint = onlightSurf
                )
            }
        }
    }
}




