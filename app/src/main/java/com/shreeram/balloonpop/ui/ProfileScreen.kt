package com.shreeram.balloonpop.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.shreeram.balloonpop.R
import com.shreeram.balloonpop.profile.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onProfileCreated: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)
    ) {
        Image(
            painter = painterResource(id = R.drawable.home_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
            alignment = Alignment.TopCenter
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Push content to the bottom white area
            Spacer(modifier = Modifier.weight(if (isLandscape) 1.5f else 1f))
            
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; error = null },
                label = { Text("Enter your name") },
                isError = error != null,
                singleLine = true,
                textStyle = MaterialTheme.typography.titleLarge.copy(
                    color = Color(0xFF003366),
                    fontWeight = FontWeight.ExtraBold
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF003366),
                    unfocusedBorderColor = Color(0xFF003366).copy(alpha = 0.5f),
                    focusedLabelColor = Color(0xFF003366),
                    cursorColor = Color(0xFF003366)
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (name.isNotBlank()) {
                        viewModel.createProfile(name) { profile ->
                            if (profile == null) {
                                error = "Invalid name or already exists"
                            } else {
                                onProfileCreated()
                            }
                        }
                    }
                }),
                modifier = Modifier.fillMaxWidth(if (isLandscape) 0.7f else 1f)
            )
            
            if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            SpriteButton(
                type = SpriteButtonType.PLAY,
                onClick = {
                    if (name.isNotBlank()) {
                        viewModel.createProfile(name) { profile ->
                            if (profile == null) {
                                error = "Invalid name or already exists"
                            } else {
                                onProfileCreated()
                            }
                        }
                    }
                },
                width = 84,
                height = 84
            )

            // Bottom margin
            Spacer(modifier = Modifier.height(if (isLandscape) 16.dp else 48.dp))
        }
    }
}
