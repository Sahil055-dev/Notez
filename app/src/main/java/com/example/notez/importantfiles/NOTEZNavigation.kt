package com.example.notez.importantfiles


import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.notez.basicpages.IntroPage
import com.example.notez.basicpages.LoginPage
import com.example.notez.basicpages.ProfilePage
import com.example.notez.basicpages.SignUpPage
import com.example.notez.bookmark.BookmarkPage
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
import com.example.notez.pdfviewer.ViewPdfPage
import com.example.notez.uploadpages.UploadNotePage
import com.example.notez.uploadpages.UploadPage


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
            else -> {}
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
            UploadPage(navController, authViewModel)
        }
        
        composable(route = "profile") {
            ProfilePage(authViewModel = authViewModel, navController = navController)
        }
        
        composable(route = "chatbot") {
            Chatpage(viewModel = ChatViewModel(), navController = navController, authViewModel = authViewModel)
        }
        
        composable("bookmarkpage"){
            BookmarkPage(navController = navController)
        }

        // Subject detail page route
        composable(route = "subjectDetail/{subjectId}") { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getString("subjectId")?.toInt() ?: return@composable
            SubjectDetailPage(subjectId = subjectId, navController = navController, db = db)
        }

        // Notes page route
        composable(route = "notes/{subjectId}") { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getString("subjectId")?.toInt() ?: return@composable
            NotesPage(navController = navController, subjectId = subjectId, db)
        }

        // User uploaded notes page route
        composable(route = "userUploadedNotes/{subjectId}") { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getString("subjectId")?.toInt() ?: return@composable
            UserUploadedNotesPage(navController = navController, subjectId = subjectId, db)
        }

        // Module notes list page route
        composable(route = "moduleNotes/{sanitizedSubjectName}/{sanitizedModuleName}") { backStackEntry ->
            val moduleName = backStackEntry.arguments?.getString("sanitizedModuleName")?: return@composable
            val subjectName = backStackEntry.arguments?.getString("sanitizedSubjectName")?: return@composable
            ModuleNotesListPage(navController = navController, moduleName, subjectName)
        }

        // User uploaded notes list page route
        composable(route = "userUploadedNotes/{sanitizedSubjectName}/{sanitizedModuleName}") { backStackEntry ->
            val moduleName = backStackEntry.arguments?.getString("sanitizedModuleName")?: return@composable
            val subjectName = backStackEntry.arguments?.getString("sanitizedSubjectName")?: return@composable
            UserUploadedNotesListPage(navController = navController, moduleName, subjectName)
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

        composable(
            route = "viewPdfPage/{encodedUrl}",
            arguments = listOf(navArgument("encodedUrl") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedUrl = backStackEntry.arguments?.getString("encodedUrl")
            if (encodedUrl != null) {
                Log.d("URl in navigation :",encodedUrl)
                ViewPdfPage(navController = navController, noteUrl = encodedUrl)
            }
        }

    }
}











