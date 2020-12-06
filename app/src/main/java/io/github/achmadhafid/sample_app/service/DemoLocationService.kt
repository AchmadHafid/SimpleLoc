package io.github.achmadhafid.sample_app.service

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import com.google.android.gms.location.LocationRequest
import io.github.achmadhafid.sample_app.PREFERENCE_KEY_SERVICE_STATUS
import io.github.achmadhafid.sample_app.R
import io.github.achmadhafid.sample_app.TRACKING_FASTEST_INTERVAL
import io.github.achmadhafid.sample_app.TRACKING_INTERVAL
import io.github.achmadhafid.simpleloc.SimpleLocClient
import io.github.achmadhafid.simpleloc.SimpleLocTracker
import io.github.achmadhafid.simpleloc.onLocationFound
import io.github.achmadhafid.simpleloc.onRunning
import io.github.achmadhafid.simpleloc.onStopped
import io.github.achmadhafid.simpleloc.onUnresolvableError
import io.github.achmadhafid.simpleloc.simpleLocTracker
import io.github.achmadhafid.simpleloc.withRequest
import io.github.achmadhafid.simplepref.SimplePref
import io.github.achmadhafid.simplepref.simplePref
import io.github.achmadhafid.zpack.extension.atLeastOreo
import io.github.achmadhafid.zpack.extension.d
import io.github.achmadhafid.zpack.extension.notificationManagerCompat
import io.github.achmadhafid.zpack.extension.toastShort

class DemoLocationService : LifecycleService(), SimpleLocClient, SimplePref {

    //region Preference

    private var serviceStatus: Int? by simplePref(PREFERENCE_KEY_SERVICE_STATUS)

    //endregion
    //region Location Tracker

    private val locationTracker = simpleLocTracker {
        resolveAddress = true
        backgroundLocationAccess = true
        withRequest {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = TRACKING_INTERVAL
            fastestInterval = TRACKING_FASTEST_INTERVAL
        }
        onRunning { _, isRestarted ->
            serviceStatus = STATUS_RUNNING
            val message = if (isRestarted) {
                R.string.message_tracking_restarted
            } else R.string.message_tracking_started

            d(getString(message))
            toastShort(message)
        }
        onStopped { _, state ->
            serviceStatus = STATUS_STOPPED
            val message = when (state) {
                SimpleLocTracker.StopState.PAUSED_BY_LIFECYCLE -> R.string.message_tracking_paused
                SimpleLocTracker.StopState.DESTROYED_BY_LIFECYCLE -> R.string.message_tracking_destroyed_by_lifecycle
                SimpleLocTracker.StopState.STOPPED_BY_SYSTEM -> R.string.message_tracking_become_unavailable
                SimpleLocTracker.StopState.STOPPED_BY_USER -> R.string.message_tracking_stopped_by_user
            }

            d(getString(message))
            toastShort(message)
        }
        onLocationFound { _, location, addresses ->
            val message = getString(R.string.label_location_found, location.accuracy, addresses.size)

            d(message)
            toastShort(message)
        }
        onUnresolvableError { _, exception ->
            serviceStatus = STATUS_STOPPED
            val message = getString(R.string.message_location_setting_error, exception.message)

            d(message)
            toastShort(message)
        }
    }

    //endregion
    //region Notification

    private val notification by lazy {
        NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_my_location_24dp)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setOnlyAlertOnce(true)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_content))
            .build()
    }

    //endregion
    //region Lifecycle Callback

    override fun onCreate() {
        super.onCreate()

        @TargetApi(Build.VERSION_CODES.O)
        if (atLeastOreo()) {
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).let {
                notificationManagerCompat.createNotificationChannel(it.apply {
                    importance = NotificationManagerCompat.IMPORTANCE_MAX
                    description = getString(R.string.notification_channel_desc)
                })
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if (!isForeground) {
            startForeground(NOTIFICATION_ID, notification)
            isForeground = true
        }

        locationTracker.enable()

        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isForeground = false
        serviceStatus = STATUS_STOPPED
    }

    //endregion

    companion object {
        private var isForeground: Boolean = false

        const val STATUS_STOPPED = 1
        const val STATUS_RUNNING = 0
        const val STATUS_PERMISSION_CANCELED = -1
        const val STATUS_REPAIR_ERROR = -2
        const val STATUS_UNKNOWN_ERROR = -3
    }

}

private const val NOTIFICATION_ID = 9090
private const val NOTIFICATION_CHANNEL_ID = "location_tracking"
