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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.soc.soar.R
import tech.soc.soar.shared.data.push.local.InAppNotificationStorage

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
                    data = data
                )
            }

            TYPE_APPROVAL_REQUEST -> {
                handleApprovalRequestMessage(
                    data = data
                )
            }

            else -> {
                showNotification(
                    notificationId = System.currentTimeMillis().toInt(),
                    title = data["title"] ?: DEFAULT_TITLE,
                    body = data["body"] ?: DEFAULT_BODY
                )
            }
        }
    }

    private fun handleNewAlertMessage(
        data: Map<String, String>
    ) {
        val accountUid = data["account_uid"].orEmpty()
        val alertId = data["alert_id"]?.toLongOrNull()
        val spaceName = data["space_name"].orEmpty()
        val eventId = data["event_id"] ?: "new_alert:$alertId"

        recordNewAlertMarker(
            accountUid = accountUid,
            eventId = eventId,
            spaceName = spaceName,
            alertId = alertId
        )

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

        showNotification(
            notificationId = if (alertId != null) {
                SoarNotificationIds.newAlertNotificationId(alertId)
            } else {
                System.currentTimeMillis().toInt()
            },
            title = data["title"] ?: "New SOAR alert",
            body = data["body"] ?: "Open SOAR to view alert details."
        )
    }

    private fun handleApprovalRequestMessage(
        data: Map<String, String>
    ) {
        val accountUid = data["account_uid"].orEmpty()
        val responseRequestId = data["response_request_id"]?.toLongOrNull()
        val alertId = data["alert_id"]?.toLongOrNull()
        val spaceName = data["space_name"].orEmpty()
        val eventId = data["event_id"] ?: "approval_request:$responseRequestId"

        recordApprovalRequestMarker(
            accountUid = accountUid,
            eventId = eventId,
            spaceName = spaceName,
            alertId = alertId,
            responseRequestId = responseRequestId
        )

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
            title = data["title"] ?: "SOAR approval required",
            body = data["body"] ?: "A new active response request requires your approval."
        )
    }

    private fun recordNewAlertMarker(
        accountUid: String,
        eventId: String,
        spaceName: String,
        alertId: Long?
    ) {
        if (
            accountUid.isBlank() ||
            eventId.isBlank() ||
            spaceName.isBlank() ||
            alertId == null
        ) {
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            InAppNotificationStorage.recordNewAlert(
                context = applicationContext,
                accountUid = accountUid,
                eventId = eventId,
                spaceName = spaceName,
                alertId = alertId
            )

            InAppNotificationCenter.recordNewAlert(
                spaceName = spaceName,
                alertId = alertId
            )
        }
    }

    private fun recordApprovalRequestMarker(
        accountUid: String,
        eventId: String,
        spaceName: String,
        alertId: Long?,
        responseRequestId: Long?
    ) {
        if (
            accountUid.isBlank() ||
            eventId.isBlank() ||
            spaceName.isBlank() ||
            alertId == null ||
            responseRequestId == null
        ) {
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            InAppNotificationStorage.recordApprovalRequest(
                context = applicationContext,
                accountUid = accountUid,
                eventId = eventId,
                spaceName = spaceName,
                alertId = alertId,
                responseRequestId = responseRequestId
            )

            InAppNotificationCenter.recordApprovalRequest(
                spaceName = spaceName,
                alertId = alertId,
                responseRequestId = responseRequestId
            )
        }
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