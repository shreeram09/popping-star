package com.shreeram.balloonpop.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.shreeram.balloonpop.profile.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onProfileCreated: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = name,
            onValueChange = { name = it; error = null },
            label = { Text("Enter your name") },
            isError = error != null,
            singleLine = true,
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
            modifier = Modifier.fillMaxWidth()
        )
        
        if (error != null) {
            Text(text = error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
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
            width = 72,
            height = 72
        )
    }
}
