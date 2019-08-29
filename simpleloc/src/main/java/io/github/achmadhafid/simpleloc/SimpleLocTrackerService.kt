@file:Suppress("TooManyFunctions", "unused")

package io.github.achmadhafid.simpleloc

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.SettingsClient

class SimpleLocTrackerService(
    private val service: LifecycleService,
    config: SimpleLocConfig
) : SimpleLocTrackerBase(config) {

    override val locationClient: FusedLocationProviderClient by lazy { service.getLocationClient() }
    override val settingsClient: SettingsClient by lazy { service.getSettingsClient() }

    init {
        lifecycleOwner = service
        checkEnableAutoStart()
    }

    override fun getContext() = service

    override fun attachObserver() {
        attachObserver(this)
    }

    override fun detachObserver() {
        detachObserver(this)
    }

    //region Turn On

    override fun enable() {
        if (!isEnabled) {
            if (service.hasLocationPermission) {
                startTracker(false)
            } else {
                onError(IllegalStateException())
            }
        }
    }

    override fun onError(exception: Exception) {
        detachObserver()
        service.stopSelf()
    }

    //endregion
    //region Turn Off

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    internal fun onDestroy() {
        disable(SimpleLocTracker.StopState.DESTROYED_BY_LIFECYCLE)
    }

    override fun disable() {
        disable(SimpleLocTracker.StopState.STOPPED_BY_USER)
    }

    override fun disable(stopState: SimpleLocTracker.StopState) {
        if (!isEnabled) return

        isEnabled = false
        detachObserver()

        stopTracker(stopState)
        service.stopSelf()
    }

    //endregion

}
