package com.example.notez

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.notez.database.AppDatabase
import com.example.notez.database.DatabaseInitializer
import com.example.notez.importantfiles.AuthViewModel
import com.example.notez.importantfiles.NotezNavigation
import com.example.notez.mainuipages.BottomNavigationBar
import com.example.notez.ui.theme.NotezTheme
import com.example.notez.ui.theme.darkfloatingcolor
import com.example.notez.ui.theme.lightfloatingcolor
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.compose.koinInject


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) // Initialize Firebase

        // Initialize the database
        initializeDatabase()

        setContent {
            NotezTheme { // Apply theme here
                NotezApp()
            }
        }
    }

    private fun initializeDatabase() {
        // Assuming you're using Koin to provide the database instance
        val db = get<AppDatabase>()
        val initializer = DatabaseInitializer(db)

        // Use a coroutine to initialize the database in the background
        CoroutineScope(Dispatchers.IO).launch {
            initializer.initialize()
        }
    }
}
@Composable
fun NotezApp() {
    // Use koinInject to get the AuthViewModel and AppDatabase instances
    val authViewModel: AuthViewModel = koinInject()
    val db: AppDatabase = koinInject()
    val navController = rememberNavController()
    val pagesWithoutBottomBars =  listOf("loginPage", "signupPage", "introPage", "profile", "viewPastYearPaper/{paperId}","chatbot")
    val pagesWithoutChatButton =  listOf("loginPage", "signupPage", "introPage", "chatbot")
    // State to track the current route
    val currentRoute = remember { mutableStateOf<String?>(null) }
    val floatingColor = if(isSystemInDarkTheme()) darkfloatingcolor else lightfloatingcolor
    // LaunchedEffect to update currentRoute when the back stack changes
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            currentRoute.value = backStackEntry.destination.route
        }
    }
        Scaffold(

            bottomBar = {
                if (currentRoute.value !in pagesWithoutBottomBars) {
                    BottomNavigationBar(navController) // Show BottomBar if not in pages without bars
                }
            },
            floatingActionButton = {
                if(currentRoute.value !in pagesWithoutChatButton){
                    FloatingActionButton(
                        onClick = { navController.navigate("chatbot")},
                        containerColor = floatingColor
                        ) {
                        Image(painter = painterResource(id =R.drawable.googlegeminiicon ),
                            contentDescription = "Gemini",
                            modifier = Modifier.size(32.dp))
                    }
                }
            }
        ) { innerPadding ->
            // Pass the padding to the content inside the Scaffold
            Box(modifier = Modifier.padding(innerPadding)) {
                NotezNavigation(navController = navController, authViewModel = authViewModel, db = db)
            }
        }
        SetStatusBarColor()
}



@Composable
fun SetStatusBarColor() {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = MaterialTheme.colorScheme.background.luminance() > 0.5f

    systemUiController.setStatusBarColor(
        color = MaterialTheme.colorScheme.surface, // Update this if you have a specific color for the status bar
        darkIcons = useDarkIcons
    )
}




