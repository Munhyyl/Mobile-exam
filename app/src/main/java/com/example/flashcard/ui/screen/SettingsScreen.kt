package com.example.flashcard.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flashcard.view.FlashcardViewModel
import com.example.flashcard.data.FlashcardSettings
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: FlashcardViewModel, navController: NavController) {
    val scope = rememberCoroutineScope()
    val settings by viewModel.settings.collectAsState(initial = FlashcardSettings())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Display Options", style = MaterialTheme.typography.titleLarge) // h6 орлож

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Show Mongolian Words")
                Switch(
                    checked = settings.showMongolian,
                    onCheckedChange = { newValue ->
                        scope.launch {
                            viewModel.updateSettings(newValue, settings.showForeign)
                        }
                    }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Show Foreign Words")
                Switch(
                    checked = settings.showForeign,
                    onCheckedChange = { newValue ->
                        scope.launch {
                            viewModel.updateSettings(settings.showMongolian, newValue)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                "Note: When both options are disabled, tap on the word to temporarily show it",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}