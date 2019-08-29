@file:Suppress("TooManyFunctions", "unused")

package io.github.achmadhafid.simpleloc

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.SettingsClient

class SimpleLocTrackerFragment(
    private val fragment: Fragment,
    config: SimpleLocConfig
) : SimpleLocTrackerBase(config) {

    override val locationClient: FusedLocationProviderClient by lazy { fragment.getLocationClient() }
    override val settingsClient: SettingsClient by lazy { fragment.getSettingsClient() }

    init {
        fragment.viewLifecycleOwnerLiveData.observe(fragment, Observer {
            fragment.viewLifecycleOwnerLiveData.removeObservers(fragment)
            lifecycleOwner = it
            observeLocationPermissionSettingsResult()
            checkEnableAutoStart()
        })
    }

    override fun getContext() = fragment.requireContext()

    override fun attachObserver() {
        attachObserver(this)
    }

    override fun detachObserver() {
        detachObserver(this)
    }

    //region Turn On

    override fun enable() {
        if (!isEnabled) {
            fragment.withLocationPermission {
                startTracker(false)
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    internal fun onStart() {
        if (isEnabled) {
            fragment.withLocationPermission {
                startTracker(true)
            }
        }
    }

    override fun onError(exception: Exception) {
        when (exception) {
            is ResolvableApiException -> resolveApiException(exception, fragment.requireActivity())
            else -> super.onError(exception)
        }
    }

    //endregion
    //region Turn Off

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    internal fun onStop() {
        disable(SimpleLocTracker.StopState.PAUSED_BY_LIFECYCLE)
    }

    override fun disable() {
        disable(SimpleLocTracker.StopState.STOPPED_BY_USER)
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
