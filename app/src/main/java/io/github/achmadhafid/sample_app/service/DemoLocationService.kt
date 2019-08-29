package io.github.achmadhafid.sample_app.service

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.edit
import androidx.lifecycle.LifecycleService
import com.google.android.gms.location.LocationRequest
import io.github.achmadhafid.sample_app.R
import io.github.achmadhafid.simpleloc.SimpleLocClient
import io.github.achmadhafid.simpleloc.SimpleLocTracker
import io.github.achmadhafid.simpleloc.onLocationFound
import io.github.achmadhafid.simpleloc.onRunning
import io.github.achmadhafid.simpleloc.onStopped
import io.github.achmadhafid.simpleloc.onUnresolvableError
import io.github.achmadhafid.simpleloc.request
import io.github.achmadhafid.simpleloc.simpleLocTracker
import io.github.achmadhafid.zpack.ktx.atLeastOreo
import io.github.achmadhafid.zpack.ktx.d
import io.github.achmadhafid.zpack.ktx.notificationManager

class DemoLocationService : LifecycleService(), SimpleLocClient {

    private val locationTracker = simpleLocTracker {
        resolveAddress = true
        request {
            priority        = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval        = 5 * 1000L
            fastestInterval = 3 * 1000L
        }
        onRunning { isRestarted ->
            onLocationTrackerRunning()
            d("Location tracking " + if (isRestarted) "re-started" else "started")
        }
        onStopped { state ->
            val message = when (state) {
                SimpleLocTracker.StopState.PAUSED_BY_LIFECYCLE    -> "Never happen"
                SimpleLocTracker.StopState.DESTROYED_BY_LIFECYCLE -> "Location tracking destroyed by lifecycle"
                SimpleLocTracker.StopState.STOPPED_BY_SYSTEM      -> "Location tracking become unavailable"
                SimpleLocTracker.StopState.STOPPED_BY_USER        -> "Location tracking stopped by user"
            }
            d(message)
            onLocationTrackerStopped()
        }
        onLocationFound { tracker, location, addresses ->
            d("Location found, accuracy: ${location.accuracy}, total address: ${addresses.size}")
            if (location.accuracy < 10) {
                tracker.disable()
            }
        }
        onUnresolvableError {
            d("Location Setting ERROR: ${it.message}")
            onLocationTrackerStopped()
        }
    }
    private val notification by lazy {
        NotificationCompat.Builder(this, "simpleloc")
            .setSmallIcon(R.drawable.ic_my_location_24dp)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setOnlyAlertOnce(true)
            .setContentTitle("Location Service Demo")
            .setContentText("Running location tracker")
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        @TargetApi(Build.VERSION_CODES.O)
        if (atLeastOreo()) {
            NotificationChannel(
                "simpleloc",
                "SimpleLoc Demo Service",
                NotificationManager.IMPORTANCE_HIGH
            ).let {
                notificationManager.createNotificationChannel(it.apply {
                    importance  = NotificationManager.IMPORTANCE_MAX
                    description = "SimpleLoc Foreground location service demo"
                })
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if (!isForeground) {
            startForeground(9090, notification)
            isForeground = true
        }

        locationTracker.enable()

        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isForeground = false
    }

    companion object {
        private var isForeground: Boolean = false

        const val STATUS_STOPPED             = 1
        const val STATUS_RUNNING             = 0
        const val STATUS_PERMISSION_CANCELED = -1
        const val STATUS_REPAIR_ERROR        = -2
        const val STATUS_UNKNOWN_ERROR       = -3
    }

    private val sharedPreferences by lazy {
        getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
    }

    private fun onLocationTrackerRunning() {
        sharedPreferences.edit {
            putInt("location_service_status", STATUS_RUNNING)
        }
    }

    private fun onLocationTrackerStopped() {
        sharedPreferences.edit {
            putInt("location_service_status", STATUS_STOPPED)
        }
    }

}
