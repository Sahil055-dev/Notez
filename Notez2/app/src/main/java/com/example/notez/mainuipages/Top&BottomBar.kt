package com.example.notez.mainuipages

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.notez.R
import com.example.notez.ui.theme.NotezTheme
import com.example.notez.ui.theme.OpenSans
import com.example.notez.ui.theme.Secondary
import com.example.notez.ui.theme.darkSurf
import com.example.notez.ui.theme.lightSurf
import com.example.notez.ui.theme.ondarkSurf
import com.example.notez.ui.theme.onlightSurf
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController){
    val imageRes = if (isSystemInDarkTheme()) {
        R.drawable.whitelogopng //  dark mode image
    } else {
        R.drawable.notezpnglogo //  light mode image
    }
    TopAppBar(
            title = {
                Image(
                    painter = painterResource(id = imageRes),
                    modifier = Modifier.size(width = 86.dp, height = 30.dp),
                    contentDescription = "Logo"
                )
            },

        modifier = Modifier.shadow(elevation = 12.dp),

        actions = {
            // Example: Add a settings icon on the right side of the TopAppBar
            IconButton(onClick = {
                navController.navigate("profile")
            }) {
                Icon(Icons.Default.Person, contentDescription = "Settings")
            }

        }
    )
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val navigationsplash = Color(0xFF9FD3DD)
    val darklightContainer = if (isSystemInDarkTheme()) darkSurf else lightSurf
    val ondarklightContainer = if (isSystemInDarkTheme()) ondarkSurf else onlightSurf
    NotezTheme {
        NavigationBar(containerColor = darklightContainer) {
            NavigationBarItem(
                icon = { Icon(Icons.Filled.Home, contentDescription = "Notes") },
                label = { Text("Home", fontFamily = OpenSans, color = ondarklightContainer ) },
                selected = currentRoute == "home",
                onClick = { navController.navigate("home") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    indicatorColor = navigationsplash,

                )
            )
            NavigationBarItem(
                icon = { Icon(Icons.Filled.Upload, contentDescription = "Assignments") },
                label = { Text("Uploads", fontFamily = OpenSans, color = ondarklightContainer ) },
                selected = currentRoute == "upload",
                onClick = { navController.navigate("upload") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black, // Color when selected
                    indicatorColor = navigationsplash, // Background for selected tab

                )

            )
            NavigationBarItem(
                icon = { Icon(Icons.Filled.Bookmark, contentDescription = "Bookmark") },
                label = { Text("Bookmark", fontFamily = OpenSans, color = ondarklightContainer ) },
                selected = currentRoute == "bookmark", // Make sure to have a route for this
                onClick = { navController.navigate("bookmark") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black, // Color when selected
                    indicatorColor = navigationsplash, // Background for selected tab

                )
            )
        }
    }
}




@Composable
fun BranchDropdownMenu(selectedBranch: String, onBranchSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val branches = listOf("Computer Engineering","Information Technology", "Mechanical Engineering", "Civil Engineering", "EXTC", "CSE-DS")
    var selectedText by remember { mutableStateOf(selectedBranch) }

    Box(modifier = Modifier
        .fillMaxWidth(0.92F)
        .wrapContentSize(Alignment.TopStart)) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = {},
            label = { androidx.compose.material3.Text("Select Branch") },
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
            modifier = Modifier.fillMaxWidth(0.85F)
        ) {
            branches.forEach { branch ->
                DropdownMenuItem(
                    text = { androidx.compose.material3.Text(branch) },
                    onClick = {
                        selectedText = branch
                        expanded = false
                        onBranchSelected(branch)
                    }
                )
            }
        }
    }
}

@Composable
fun YearDropdownMenu(selectedYear: Int, onYearSelected: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val years = listOf(1, 2, 3, 4) // Example years
    var selectedText by remember { mutableStateOf(selectedYear.toString()) }

    Box(modifier = Modifier
        .fillMaxWidth(0.92F)
        .wrapContentSize(Alignment.TopStart)) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = {},
            label = { androidx.compose.material3.Text("Select Year") },
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
            },
            colors = OutlinedTextFieldDefaults.colors(

         )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.85F)
        ) {
            years.forEach { year ->
                DropdownMenuItem(
                    text = { androidx.compose.material3.Text(year.toString()) },
                    onClick = {
                        selectedText = year.toString()
                        expanded = false
                        onYearSelected(year)
                    }
                )
            }
        }
    }
}


@Composable
fun SemesterDropdownMenu(
    selectedSemester: Int,
    semesters: List<Int>,
    onSemesterSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(selectedSemester.toString()) }

    Box(modifier = Modifier
        .fillMaxWidth(0.92F)
        .wrapContentSize(Alignment.TopStart)) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = {},
            label = { androidx.compose.material3.Text("Select Semester") },
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
            semesters.forEach { semester ->
                DropdownMenuItem(
                    text = { androidx.compose.material3.Text(semester.toString()) },
                    onClick = {
                        selectedText = semester.toString()
                        expanded = false
                        onSemesterSelected(semester)
                    }
                )
            }
        }
    }
}


@Composable
fun BouncingTypingIndicator() {
    val dotSize = 8.dp
    val delayUnit = 300 // Delay between each dot animation in milliseconds

    // Create a list of animation states for each dot
    val animations = List(3) { remember { Animatable(initialValue = 0f) } }

    // Launch the animation for each dot with a delay
    animations.forEachIndexed { index, animatable ->
        LaunchedEffect(animatable) {
            delay(index * delayUnit.toLong())
            launch {
                while (true) {
                    animatable.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 500, easing = LinearEasing)
                    )
                    animatable.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = 500, easing = LinearEasing)
                    )
                }
            }
        }
    }

    // Display the three dots with animated vertical displacement
    Row(
        modifier = Modifier.height(dotSize),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        animations.forEach { animatable ->
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .offset(y = (-10 * animatable.value).dp) // Use the animated value for bouncing
                    .background(color = Color.Gray, shape = CircleShape)
            )
        }
    }
}




