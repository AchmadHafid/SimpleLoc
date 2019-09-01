@file:Suppress("TooManyFunctions", "unused")

package io.github.achmadhafid.simpleloc

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.SettingsClient

class SimpleLocTrackerActivity(
    private val activity: FragmentActivity,
    config: SimpleLocConfig
) : SimpleLocTrackerBase(config) {

    override val locationClient: FusedLocationProviderClient by lazy { activity.getLocationClient() }
    override val settingsClient: SettingsClient by lazy { activity.getSettingsClient() }

    init {
        lifecycleOwner = activity
        observeLocationPermissionSettingsResult()
        checkEnableAutoStart()
    }

    override fun getContext() = activity

    override fun attachObserver() {
        attachObserver(this)
    }

    override fun detachObserver() {
        detachObserver(this)
    }

    //region Turn On

    override fun enable(isForce: Boolean) {
        if (isForce) disable(isForce)

        if (!isEnabled) {
            activity.withLocationPermission(config.requestCode) {
                startTracker(false)
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    internal fun onStart() {
        if (isEnabled) {
            activity.withLocationPermission(config.requestCode) {
                startTracker(true)
            }
        }
    }

    override fun onError(exception: Exception) {
        when (exception) {
            is ResolvableApiException -> resolveApiException(exception, activity)
            else -> super.onError(exception)
        }
    }

    //endregion
    //region Turn Off

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    internal fun onStop() {
        disable(SimpleLocTracker.StopState.PAUSED_BY_LIFECYCLE)
    }

    override fun disable(stopState: SimpleLocTracker.StopState) {
        if (!isEnabled) return

        if (stopState != SimpleLocTracker.StopState.PAUSED_BY_LIFECYCLE) {
            isEnabled = false
            detachObserver()
        }

        stopTracker(stopState)
    }

    //endregion

}
