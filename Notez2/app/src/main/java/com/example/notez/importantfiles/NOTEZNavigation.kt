package com.example.notez.importantfiles


import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.notez.basicpages.IntroPage
import com.example.notez.basicpages.LoginPage
import com.example.notez.basicpages.ProfilePage
import com.example.notez.basicpages.SignUpPage
import com.example.notez.chatBot.ChatViewModel
import com.example.notez.chatBot.Chatpage
import com.example.notez.database.AppDatabase
import com.example.notez.mainuipages.Home
import com.example.notez.mainuipages.ModuleNotesListPage
import com.example.notez.mainuipages.NotesPage
import com.example.notez.mainuipages.PastYearPapersPage
import com.example.notez.mainuipages.SubjectDetailPage
import com.example.notez.mainuipages.UserUploadedNotesListPage
import com.example.notez.mainuipages.UserUploadedNotesPage
import com.example.notez.mainuipages.ViewPastYearPaperPage
import com.example.notez.mainuipages.ViewPdfPage
import com.example.notez.mainuipages.ViewUserUploadedPdfPage
import com.example.notez.uploadpages.UploadNotePage


@Composable
fun NotezNavigation(navController: NavHostController, authViewModel: AuthViewModel, db: AppDatabase) {
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                navController.navigate("home") {
                    popUpTo("introPage") { inclusive = true }
                }
            }
            is AuthState.Idle -> {
                navController.navigate("introPage") {
                    popUpTo("home") { inclusive = true }
                }
            }
            is AuthState.Loading -> { /* Show loading state if needed */ }
            is AuthState.Error -> { /* Handle error if needed */ }
        }
    }

    NavHost(navController = navController, startDestination = "introPage") {
        composable(route = "introPage") {
            IntroPage(navController, authViewModel)
        }
        composable(route = "loginPage") {
            LoginPage(navController, authViewModel)
        }
        composable(route = "signupPage") {
            SignUpPage(navController, authViewModel)
        }
        composable(route = "home") {
            Home(navController = navController, authViewModel = authViewModel, db = db)
        }
        composable(route = "upload") {
            // Provide an optional completion handler to handle what happens after upload
            UploadNotePage(navController = navController, authViewModel = authViewModel) { uploadUrl ->
                Log.d("NotezNavigation", "Uploaded successfully with URL: $uploadUrl")
            }
        }
        composable(route = "profile") {
            ProfilePage(authViewModel = authViewModel, navController = navController)
        }
        composable(route = "chatbot") {
            Chatpage(viewModel = ChatViewModel(), navController = navController, authViewModel = authViewModel)
        }

        // Subject detail page route
        composable(route = "subjectDetail/{subjectId}") { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getString("subjectId")?.toInt() ?: return@composable
            SubjectDetailPage(subjectId = subjectId, navController = navController, db = db)
        }

        // Notes page route
        composable(route = "notes/{subjectId}") { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getString("subjectId")?.toInt() ?: return@composable
            NotesPage(navController = navController, subjectId = subjectId, db = db)
        }

        // User uploaded notes page route
        composable(route = "userUploadedNotes/{subjectId}") { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getString("subjectId")?.toInt() ?: return@composable
            UserUploadedNotesPage(navController = navController, subjectId = subjectId, db = db)
        }

        // Module notes list page route
        composable(route = "moduleNotes/{moduleId}") { backStackEntry ->
            val moduleId = backStackEntry.arguments?.getString("moduleId")?.toInt() ?: return@composable
            ModuleNotesListPage(navController = navController, moduleId = moduleId, db = db)
        }

        // User uploaded notes list page route
        composable(route = "userUploadedNotesList/{moduleId}") { backStackEntry ->
            val moduleId = backStackEntry.arguments?.getString("moduleId")?.toInt() ?: return@composable
            UserUploadedNotesListPage(navController = navController, moduleId = moduleId, db = db)
        }

        // Past Year Papers
        composable(route = "pastYearPapers/{subjectId}") { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getString("subjectId")?.toInt() ?: return@composable
            PastYearPapersPage(navController = navController, subjectId = subjectId, db = db)
        }

        composable(route = "viewPastYearPaper/{paperId}") { backStackEntry ->
            val paperId = backStackEntry.arguments?.getString("paperId")?.toInt() ?: return@composable
            ViewPastYearPaperPage(navController = navController, paperId = paperId, db = db)
        }

        // View specific note PDF
        composable(route = "viewPdf/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")?.toInt() ?: return@composable
            ViewPdfPage(navController = navController, noteId = noteId, db = db)
        }

        // View specific user-uploaded note PDF
        composable(route = "viewUserUploadedPdf/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")?.toInt() ?: return@composable
            ViewUserUploadedPdfPage(navController = navController, noteId = noteId, db = db)
        }
    }
}











