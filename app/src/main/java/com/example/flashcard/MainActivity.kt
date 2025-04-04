package com.example.flashcard

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.flashcard.data.AppDatabase
import com.example.flashcard.data.SettingsDataStore
import com.example.flashcard.repository.FlashcardRepository
import com.example.flashcard.ui.screen.AddEditScreen
import com.example.flashcard.ui.screen.MainScreen
import com.example.flashcard.ui.screen.SettingsScreen
import com.example.flashcard.ui.theme.FlashcardAppTheme

import com.example.flashcard.view.FlashcardViewModel
import java.util.Calendar

class MainActivity : ComponentActivity() {
    private val viewModel: FlashcardViewModel by viewModels {
        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "flashcard-db"
        ).build()

        val repository = FlashcardRepository(database.flashcardDao())
        val settingsDataStore = SettingsDataStore(applicationContext)

        FlashcardViewModelFactory(repository, settingsDataStore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up notification channel
        createNotificationChannel()

        setContent {
            FlashcardAppTheme {
                FlashcardApp(viewModel)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "flashcard_channel",
                "Flashcard Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        fun getViewModelFactory(context: Context): FlashcardViewModelFactory {
            val database = Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "flashcard-db"
            ).build()

            val repository = FlashcardRepository(database.flashcardDao())
            val settingsDataStore = SettingsDataStore(context)

            return FlashcardViewModelFactory(repository, settingsDataStore)
        }
    }
}

@Composable
fun FlashcardApp(viewModel: FlashcardViewModel) {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(viewModel, navController)
        }
        composable("add_edit/{flashcardId}") { backStackEntry ->
            val flashcardId = backStackEntry.arguments?.getString("flashcardId")?.toIntOrNull()
            AddEditScreen(viewModel, navController, flashcardId)
        }
        composable("settings") {
            SettingsScreen(viewModel, navController)
        }
    }

    // Schedule daily notification
    LaunchedEffect(Unit) {
        scheduleDailyNotification(context)
    }
}

private fun scheduleDailyNotification(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, NotificationReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Set alarm to trigger at 9 AM every day
    val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, 9)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }

    // If it's already past 9 AM, set for next day
    if (calendar.timeInMillis <= System.currentTimeMillis()) {
        calendar.add(Calendar.DAY_OF_YEAR, 1)
    }

    alarmManager.setRepeating(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        AlarmManager.INTERVAL_DAY,
        pendingIntent
    )
}