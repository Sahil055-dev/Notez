package com.example.notez.chatBot

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.notez.R
import com.example.notez.importantfiles.AuthViewModel
import com.example.notez.mainuipages.BouncingTypingIndicator
import com.example.notez.ui.theme.OpenSans
import com.example.notez.ui.theme.Secondary
import com.example.notez.ui.theme.Tertiary
import com.example.notez.ui.theme.darkContainer
import com.example.notez.ui.theme.darkTextfield
import com.example.notez.ui.theme.lightContainer
import com.example.notez.ui.theme.lightTextfield
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Chatpage(modifier: Modifier=Modifier, viewModel: ChatViewModel, navController: NavController, authViewModel: AuthViewModel,){
    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(text = "ChatBot",
                    style = TextStyle(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Blue,
                                Color.Red
                            )
                        )
                    ),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = OpenSans,
                    letterSpacing = 0.5f.sp


                    ) },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                modifier = Modifier.shadow(elevation = 12.dp)
                // Customize content color if needed
            )
        },
        content = {padding ->
            Column(modifier = Modifier
                .padding(padding)
                .fillMaxSize()) {
                MessageList(modifier = Modifier.weight(1f), messageList = viewModel.messageList, authViewModel = authViewModel)
                MessageInput(onMessageSend = {
                    viewModel.sendMessage(it)
                }
                )
            }
        }
    )

}



@Composable
fun MessageInput(onMessageSend : (String)->Unit){
    var message by remember {
        mutableStateOf("")
    }
    Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            value = message,
            onValueChange = {
                message = it
            },
            placeholder = { // This replaces the label and disappears when typing
                Text("Enter your Doubt here")
            },
            trailingIcon = {
                IconButton(onClick = {
                    if (message.isNotEmpty()) {
                        onMessageSend(message)
                        message = ""
                    }
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Secondary // Set custom color for the icon
                    )
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = if (isSystemInDarkTheme()) darkTextfield else lightTextfield, // Custom border color when focused
                unfocusedContainerColor = if (isSystemInDarkTheme()) darkTextfield else lightTextfield, // Custom border color when not focused
                unfocusedIndicatorColor = if (isSystemInDarkTheme()) darkTextfield else lightTextfield,
                focusedIndicatorColor = if (isSystemInDarkTheme()) darkTextfield else lightTextfield,
            ),
            shape = RoundedCornerShape(25)
        )

    }
}


@Composable
fun MessageList(modifier: Modifier=Modifier,messageList: List<MessageModel>,authViewModel: AuthViewModel,){
    val user by authViewModel.currentUser.collectAsState()
    val name = rememberSaveable { mutableStateOf(user?.displayName ?: "User") }

    if (messageList.isEmpty()){
        Column(
            modifier=modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Hello ${name.value}!",
                    style = TextStyle(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Magenta,
                                Color(0XFF35e0e6)

                            )
                        )
                    ),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = OpenSans,
                    letterSpacing = 0.5f.sp
                )

            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {

            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Ask Me Your Doubts..!", fontSize = 24.sp, color = Color.Gray,fontWeight = FontWeight.Bold,)
        }
     }
    } else{
        LazyColumn(
            modifier= modifier,
            reverseLayout = true
        )
        {
            items(messageList.reversed()){
                MessageRow(messageModel = it)
            }
        }
    }


}

@Composable
fun MessageRow(messageModel: MessageModel) {
    val isModel = messageModel.role == "model"

    // Define colors based on dark or light theme
    val userBoxColor = if (isSystemInDarkTheme()) Color(0xFF333333) else Color(0xFFE1E0E0) // Light gray for user
    val modelBoxColor = if (isSystemInDarkTheme()) Color(0xFF12444f) else Color(0xFF9EDAE2) // Blue for AI
    val textColor = if (isSystemInDarkTheme()) Color.White else Color.Black

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .align(if (isModel) Alignment.BottomStart else Alignment.BottomEnd)
                    .padding(
                        start = if (isModel) 8.dp else 70.dp,
                        end = if (isModel) 70.dp else 8.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    )
                    .clip(RoundedCornerShape(24.dp)) // Rounded corners
                    .background(if (isModel) modelBoxColor else userBoxColor) // Background color based on sender
                    .padding(16.dp)
            ) {
                if (messageModel.message == "typing...") {
                    // Show the TypingIndicator if the message is "typing..."
                    TypingIndicator()
                } else {
                    SelectionContainer {
                        Text(
                            text = messageModel.message,
                            fontWeight = FontWeight.SemiBold,
                            color = textColor // Text color based on theme
                        )
                    }
                }
            }
        }
    }
}




@Composable
fun TypingIndicator() {
    val dotSize = 8.dp
    val delayUnit = 300 // milliseconds
    val animationDuration = 800 // milliseconds

    // List of waiting messages
    val waitingMessages = listOf(
        "Generating response...",
        "Hold on, thinking...",
        "Please wait a moment...",
        "Just a second...",
        "Formulating an answer...",
        "Almost there..."
    )

    // Infinite transition for dots
    val infiniteTransition = rememberInfiniteTransition()

    // Animating each dot with staggered delay
    val dot1Scale = infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(animationDuration, delayMillis = 0, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val dot2Scale = infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(animationDuration, delayMillis = delayUnit, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val dot3Scale = infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(animationDuration, delayMillis = 2 * delayUnit, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // State to hold the current message index
    var currentMessageIndex by remember { mutableStateOf(0) }

    // Launched effect to cycle through waiting messages every 2 seconds
    LaunchedEffect(Unit) {
        while (true) {
            delay(2000L) // Change message every 2 seconds
            currentMessageIndex = (currentMessageIndex + 1) % waitingMessages.size
        }
    }

    // Row with both the waiting message and the dot animation
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // Display the waiting message
        Text(
            text = waitingMessages[currentMessageIndex],
            modifier = Modifier.padding(end = 8.dp),
            fontWeight = FontWeight.Medium
        )

        // Display the dot animation
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Dot 1
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .scale(dot1Scale.value)
                    .background(color = if (isSystemInDarkTheme()) Color.White else Color.Black, shape = CircleShape)
            )

            // Dot 2
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .scale(dot2Scale.value)
                    .background(color = if (isSystemInDarkTheme()) Color.White else Color.Black, shape = CircleShape)
            )

            // Dot 3
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .scale(dot3Scale.value)
                    .background(color = if (isSystemInDarkTheme()) Color.White else Color.Black, shape = CircleShape)
            )
        }
    }
}




