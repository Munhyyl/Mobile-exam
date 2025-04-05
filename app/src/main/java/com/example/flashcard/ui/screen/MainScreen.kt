package com.example.flashcard.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flashcard.data.Flashcard
import com.example.flashcard.data.FlashcardSettings
import com.example.flashcard.view.FlashcardViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: FlashcardViewModel, navController: NavController) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val scope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }

    val currentFlashcard by viewModel.getCurrentFlashcard().collectAsState(initial = null)
    val settings by viewModel.settings.collectAsState(initial = FlashcardSettings())
    val flashcards by viewModel.allFlashcards.collectAsState(initial = emptyList())
    val currentIndex by viewModel.currentFlashcardIndex.collectAsState(initial = 0)

    // LaunchedEffect-ийн логикыг сайжруулж, зөвхөн шаардлагатай үед дуудах
    LaunchedEffect(flashcards, currentIndex) {
        println("Flashcards size: ${flashcards.size}, Current Index: $currentIndex")
    }

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
            FloatingActionButton(
                onClick = { navController.navigate("add_edit/-1") },
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Flashcard")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (currentFlashcard != null) {
                var showForeign by remember { mutableStateOf(settings.showForeign) }
                var showMongolian by remember { mutableStateOf(settings.showMongolian) }

                // Хоёр үг хоёулаа харагдахгүй үед товшилтоор ээлжлэн харуулах
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .combinedClickable(
                            onClick = {
                                if (!settings.showForeign && !settings.showMongolian) {
                                    if (!showForeign && !showMongolian) {
                                        showForeign = true // Эхний товшилтоор гадаад үгийг харуул
                                    } else if (showForeign && !showMongolian) {
                                        showForeign = false
                                        showMongolian = true // Хоёр дахь товшилтоор монгол үгийг харуул
                                    } else {
                                        showForeign = false
                                        showMongolian = false // Гурав дахь товшилтоор хоёуланг нуу
                                    }
                                } else if (!settings.showForeign && settings.showMongolian) {
                                    showForeign = !showForeign // Гадаад үгийг түр харуулах/нуух
                                } else if (settings.showForeign && !settings.showMongolian) {
                                    showMongolian = !showMongolian // Монгол үгийг түр харуулах/нуух
                                }
                            },
                            onLongClick = { navController.navigate("add_edit/${currentFlashcard?.id}") }
                        )
                ) {
                    when {
                        // Хоёр үг хоёулаа харагдахгүй, анхны төлөв
                        !showForeign && !showMongolian -> {
                            Text(
                                text = "Tap to reveal",
                                style = MaterialTheme.typography.headlineLarge,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        // Зөвхөн гадаад үг харагдаж байна
                        showForeign && !showMongolian -> {
                            Text(
                                text = currentFlashcard!!.foreignWord,
                                style = MaterialTheme.typography.headlineLarge,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        // Зөвхөн монгол үг харагдаж байна
                        !showForeign && showMongolian -> {
                            Text(
                                text = currentFlashcard!!.mongolianWord,
                                style = MaterialTheme.typography.headlineLarge,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        // Хоёр үг хоёулаа харагдаж байна
                        showForeign && showMongolian -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = currentFlashcard!!.foreignWord,
                                    style = MaterialTheme.typography.headlineLarge
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = currentFlashcard!!.mongolianWord,
                                    style = MaterialTheme.typography.headlineLarge
                                )
                            }
                        }
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
                        onClick = { navController.navigate("add_edit/${currentFlashcard?.id}") },
                        enabled = flashcards.isNotEmpty()
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Flashcard")
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

    if (showDeleteDialog && currentFlashcard != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Flashcard") },
            text = { Text("Are you sure you want to delete this flashcard?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            currentFlashcard?.let { viewModel.deleteFlashcard(it) }
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