package tech.soc.soar.push

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import tech.soc.soar.R

class SoarFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val data = message.data
        val type = data["type"].orEmpty()

        when (type) {
            TYPE_NEW_ALERT -> {
                handleNewAlertMessage(
                    message = message,
                    data = data
                )
            }

            TYPE_APPROVAL_REQUEST -> {
                handleApprovalRequestMessage(
                    message = message,
                    data = data
                )
            }

            else -> {
                showNotification(
                    notificationId = System.currentTimeMillis().toInt(),
                    title = message.notification?.title ?: DEFAULT_TITLE,
                    body = message.notification?.body ?: DEFAULT_BODY
                )
            }
        }
    }

    private fun handleNewAlertMessage(
        message: RemoteMessage,
        data: Map<String, String>
    ) {
        val alertId = data["alert_id"]?.toLongOrNull()
        val spaceName = data["space_name"].orEmpty()

        if (
            spaceName.isNotBlank() &&
            PushForegroundState.isSpaceOpened(spaceName)
        ) {
            PushEventBus.emit(
                PushEvent.SpaceShouldRefresh(
                    spaceName = spaceName,
                    scrollToTop = true
                )
            )

            return
        }

        if (alertId != null && spaceName.isNotBlank()) {
            InAppNotificationCenter.recordNewAlert(
                spaceName = spaceName,
                alertId = alertId
            )
        }

        showNotification(
            notificationId = if (alertId != null) {
                SoarNotificationIds.newAlertNotificationId(alertId)
            } else {
                System.currentTimeMillis().toInt()
            },
            title = message.notification?.title ?: data["title"] ?: "New SOAR alert",
            body = message.notification?.body ?: data["body"] ?: "Open SOAR to view alert details."
        )
    }

    private fun handleApprovalRequestMessage(
        message: RemoteMessage,
        data: Map<String, String>
    ) {
        val responseRequestId = data["response_request_id"]?.toLongOrNull()
        val alertId = data["alert_id"]?.toLongOrNull()
        val spaceName = data["space_name"].orEmpty()
        if (
            responseRequestId != null &&
            alertId != null &&
            spaceName.isNotBlank()
        ) {
            InAppNotificationCenter.recordApprovalRequest(
                spaceName = spaceName,
                alertId = alertId,
                responseRequestId = responseRequestId
            )
        }

        if (
            alertId != null &&
            PushForegroundState.isResponsesOpened(alertId)
        ) {
            PushEventBus.emit(
                PushEvent.ResponsesShouldRefresh(
                    alertId = alertId,
                    scrollToTop = true
                )
            )

            return
        }

        showNotification(
            notificationId = if (responseRequestId != null) {
                SoarNotificationIds.approvalRequestNotificationId(responseRequestId)
            } else {
                System.currentTimeMillis().toInt()
            },
            title = message.notification?.title ?: data["title"] ?: "SOAR approval required",
            body = message.notification?.body
                ?: data["body"]
                ?: "A new active response request requires your approval."
        )
    }

    private fun showNotification(
        notificationId: Int,
        title: String,
        body: String
    ) {
        createNotificationChannel()

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val defaultSoundUri = RingtoneManager.getDefaultUri(
            RingtoneManager.TYPE_NOTIFICATION
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(body)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setVibrate(VIBRATION_PATTERN)
            .setDefaults(NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_VIBRATE)
            .build()

        NotificationManagerCompat.from(this)
            .notify(notificationId, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val defaultSoundUri = RingtoneManager.getDefaultUri(
            RingtoneManager.TYPE_NOTIFICATION
        )

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "SOAR alerts and response approval notifications"
            enableVibration(true)
            vibrationPattern = VIBRATION_PATTERN
            setSound(defaultSoundUri, audioAttributes)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }

        val notificationManager = getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        notificationManager.createNotificationChannel(channel)
    }

    private companion object {
        const val CHANNEL_ID = "soar_alerts"
        const val CHANNEL_NAME = "SOAR alerts"

        const val TYPE_NEW_ALERT = "new_alert"
        const val TYPE_APPROVAL_REQUEST = "approval_request"

        const val DEFAULT_TITLE = "SOAR"
        const val DEFAULT_BODY = "New notification"

        val VIBRATION_PATTERN = longArrayOf(
            0,
            250,
            150,
            250
        )
    }
}