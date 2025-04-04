package com.example.flashcard

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import junit.framework.TestCase.assertTrue
import org.junit.Test

class NotificationReceiverTest {

    @Test
    fun testNotificationTrigger() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val receiver = NotificationReceiver()
        val intent = Intent(context, NotificationReceiver::class.java)

        // Trigger notification
        receiver.onReceive(context, intent)

        // Check if notification exists
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notifications = notificationManager.activeNotifications

        assertTrue(notifications.any { it.id == 1 })
    }
}
