@file:Suppress("TooManyFunctions", "unused")

package io.github.achmadhafid.simpleloc

import android.app.Activity
import android.content.Context
import android.location.Address
import android.location.Location
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.SettingsClient
import io.github.achmadhafid.zpack.ktx.d
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException

abstract class SimpleLocTrackerBase(
    internal val config: SimpleLocConfig
) : SimpleLocTracker, SimpleLocClientError, LifecycleObserver {

    protected var isEnabled = false
    override var isRunning: Boolean = false
        protected set(value) {
            if (value) isEnabled = true
            field = value
        }

    protected abstract val locationClient: FusedLocationProviderClient
    protected abstract val settingsClient: SettingsClient
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)

            fun onLocationFound(location: Location, addresses: List<Address>) {
                config.onLocationFoundListener(
                    this@SimpleLocTrackerBase,
                    location, addresses
                )
            }

            locationResult?.lastLocation?.let { location ->
                if (config.resolveAddress) {
                    launchCoroutines {
                        try {
                            val addresses = location.getAddresses(getContext())
                            onLocationFound(location, addresses)
                        } catch (exception: IOException) {
                            d("ERROR RESOLVING ADDRESSES")
                            onLocationFound(location, emptyList())
                        }
                    }
                } else {
                    onLocationFound(location, emptyList())
                }
            }
        }

        override fun onLocationAvailability(availability: LocationAvailability) {
            if (!availability.isLocationAvailable && isEnabled) {
                settingsClient.checkLocationSettings(config.settingsRequest)
                    .addOnFailureListener {
                        disable(SimpleLocTracker.StopState.STOPPED_BY_SYSTEM)
                    }
            }
        }
    }

    protected abstract fun getContext(): Context
    protected abstract fun disable(stopState: SimpleLocTracker.StopState)

    protected fun launchCoroutines(block: suspend CoroutineScope.() -> Unit) {
        lifecycleOwner?.lifecycleScope?.launch(block = block)
    }

    protected fun checkEnableAutoStart() {
        if (config.isAutoStart) {
            lifecycleOwner?.lifecycleScope
                ?.launchWhenCreated {
                    enable()
                }
        }
    }

    override fun disable(isForce: Boolean) {
        if (isForce) {
            isEnabled = false
            isRunning = false
            locationClient.removeLocationUpdates(locationCallback)
        } else {
            disable(SimpleLocTracker.StopState.STOPPED_BY_USER)
        }
    }

    protected fun startTracker(isRestarted: Boolean) {
        if (isRunning) return

        settingsClient.checkLocationSettings(config.settingsRequest)
            .addOnSuccessListener {
                locationClient.requestLocationUpdates(
                    config.locationRequest,
                    locationCallback,
                    Looper.myLooper()
                )
                isRunning = true
                attachObserver()
                config.onRunningListener(this, isRestarted)
            }
            .addOnFailureListener(this::onError)
    }

    protected fun stopTracker(stopState: SimpleLocTracker.StopState) {
        if (!isRunning) return
        isRunning = false

        locationClient.removeLocationUpdates(locationCallback)
        if (stopState != SimpleLocTracker.StopState.DESTROYED_BY_LIFECYCLE) {
            config.onStoppedListener(this, stopState)
        }
    }

    //region Start Error Handler

    override fun onError(exception: Exception) {
        when (exception) {
            is LocationServiceRepairError -> {
                isEnabled = false
                isRunning = false
                config.onLocationServiceRepairErrorListener(this)
            }
            else -> {
                isEnabled = false
                isRunning = false
                config.onUnresolvableErrorListener(this, exception)
            }
        }
    }

    protected fun resolveApiException(exception: ResolvableApiException, activity: Activity) {
        exception.startResolutionForResult(activity, config.requestCode)
    }

    //endregion
    //region Permission Setting Result Handler

    var isOpeningLocationPermissionSetting = false

    protected fun observeLocationPermissionSettingsResult() {
        attachObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun onResume() {
                if (isOpeningLocationPermissionSetting) {
                    isOpeningLocationPermissionSetting = false
                    launchCoroutines {
                        delay(CHECK_LOCATION_SETTING_RESULT_DELAY)
                        enable()
                    }
                }
            }
        })
    }

    //endregion
    //region Lifecycle Listener Handler

    protected var lifecycleOwner: LifecycleOwner? = null
        set(value) {
            value?.let {
                it.lifecycle.addObserver(object : LifecycleObserver{
                    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                    fun onDestroy() {
                        it.lifecycle.removeObserver(this)
                        detachObservers()
                        field = null
                        observers.clear()
                        //region if paused, disable flag then call callback
                        if (isEnabled) {
                            isEnabled = false
                            config.onStoppedListener(
                                this@SimpleLocTrackerBase,
                                SimpleLocTracker.StopState.DESTROYED_BY_LIFECYCLE
                            )
                        }
                        //endregion
                    }
                })
            }
            field = value
        }
    private val observers: MutableList<LifecycleObserver> = mutableListOf()

    protected abstract fun attachObserver()

    protected fun attachObserver(observer: LifecycleObserver) {
        lifecycleOwner?.let {
            it.lifecycle.addObserver(observer)
            observers.add(observer)
        }
    }

    protected abstract fun detachObserver()

    protected fun detachObserver(observer: LifecycleObserver) {
        lifecycleOwner?.lifecycle?.removeObserver(observer)
    }

    protected fun detachObservers() {
        observers.forEach {
            lifecycleOwner?.lifecycle?.removeObserver(it)
        }
    }

    //endregion

}
