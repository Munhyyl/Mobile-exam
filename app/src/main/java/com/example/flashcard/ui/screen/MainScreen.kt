package com.example.flashcard.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.flashcard.data.Flashcard
import com.example.flashcard.view.FlashcardViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: FlashcardViewModel, navController: NavController) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val scope = rememberCoroutineScope()

    Column(
        modifier = if (isLandscape) Modifier.fillMaxWidth().padding(8.dp) else Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = if (isLandscape) Arrangement.SpaceAround else Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val currentFlashcardAndSettings by viewModel.combinedFlashcardsAndSettings.collectAsState()

        val (flashcards, settings) = currentFlashcardAndSettings

        val currentIndex by remember { derivedStateOf { viewModel.currentFlashcardIndex } }
        val currentCard = if (flashcards.isNotEmpty() && currentIndex in flashcards.indices) {
            flashcards[currentIndex]
        } else {
            null
        }

        var showMongolian by remember { mutableStateOf(false) }
        var showDeleteDialog by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Flashcards") },
                    actions = {
                        IconButton(onClick = { navController.navigate("settings") }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { navController.navigate("add_edit/-1") }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Flashcard")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (currentCard != null) {
                    // Foreign word
                    if (settings.showForeign) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .combinedClickable(
                                    onClick = {
                                        if (!settings.showMongolian) {
                                            showMongolian = !showMongolian
                                        }
                                    },
                                    onLongClick = {
                                        navController.navigate("add_edit/${currentCard.id}")
                                    }
                                )
                        ) {
                            Text(
                                text = currentCard.foreignWord,
                                style = MaterialTheme.typography.headlineLarge,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }

                    // Mongolian word
                    if (settings.showMongolian || showMongolian) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .combinedClickable(
                                    onClick = { showMongolian = !showMongolian },
                                    onLongClick = {
                                        navController.navigate("add_edit/${currentCard.id}")
                                    }
                                )
                        ) {
                            Text(
                                text = currentCard.mongolianWord,
                                style = MaterialTheme.typography.headlineLarge,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IconButton(
                            onClick = { viewModel.previousFlashcard() },
                            enabled = flashcards.isNotEmpty()
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Previous")
                        }

                        IconButton(
                            onClick = { showDeleteDialog = true },
                            enabled = flashcards.isNotEmpty()
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }

                        IconButton(
                            onClick = { viewModel.nextFlashcard() },
                            enabled = flashcards.isNotEmpty()
                        ) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Next")
                        }
                    }
                } else {
                    Text(
                        text = "No flashcards available",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        if (showDeleteDialog && currentCard != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Flashcard") },
                text = { Text("Are you sure you want to delete this flashcard?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            scope.launch {
                                viewModel.deleteFlashcard(currentCard)
                                showDeleteDialog = false
                            }
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
