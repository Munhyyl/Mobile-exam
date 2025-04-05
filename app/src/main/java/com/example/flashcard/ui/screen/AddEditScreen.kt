package com.example.flashcard.ui.screen

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flashcard.data.Flashcard
import com.example.flashcard.view.FlashcardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    viewModel: FlashcardViewModel,
    navController: NavController,
    flashcardId: Int?
) {
    var mongolianWord by rememberSaveable { mutableStateOf("") }
    var foreignWord by rememberSaveable { mutableStateOf("") }
    var shouldNavigateBack by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Flow-оос утгыг collect хийж авах
    val currentFlashcard by produceState<Flashcard?>(initialValue = null) {
        flashcardId?.let { id ->
            if (id != -1) {
                viewModel.getFlashcardById(id).collectLatest { flashcard ->
                    withContext(Dispatchers.Main) {
                        value = flashcard
                    }
                }
            }
        }
    }

    // CurrentFlashcard өөрчлөгдөхөд утгыг шинэчилнэ
    LaunchedEffect(currentFlashcard) {
        currentFlashcard?.let {
            mongolianWord = it.mongolianWord
            foreignWord = it.foreignWord
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (currentFlashcard == null) "Add Flashcard" else "Edit Flashcard") },
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
            OutlinedTextField(
                value = mongolianWord,
                onValueChange = { mongolianWord = it },
                label = { Text("Mongolian Word") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = foreignWord,
                onValueChange = { foreignWord = it },
                label = { Text("Foreign Word") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (mongolianWord.isNotBlank() && foreignWord.isNotBlank()) {
                        coroutineScope.launch {
                            val flashcard = Flashcard(
                                id = currentFlashcard?.id ?: 0,
                                mongolianWord = mongolianWord,
                                foreignWord = foreignWord
                            )

                            if (currentFlashcard == null) {
                                viewModel.insertFlashcard(flashcard)
                            } else {
                                viewModel.updateFlashcard(flashcard)
                            }

                            shouldNavigateBack = true
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = mongolianWord.isNotBlank() && foreignWord.isNotBlank()
            ) {
                Text(if (currentFlashcard == null) "Add Flashcard" else "Update Flashcard")
            }

            LaunchedEffect(shouldNavigateBack) {
                if (shouldNavigateBack) {
                    navController.popBackStack()
                    shouldNavigateBack = false
                }
            }
        }
    }
}