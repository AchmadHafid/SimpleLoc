package io.github.achmadhafid.simpleloc

import android.location.Location
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import java.lang.ref.WeakReference

private class WeakLocationCallback(locationCallback: LocationCallback) : LocationCallback() {

    private val callback = WeakReference(locationCallback)

    override fun onLocationResult(locationResult: LocationResult?) {
        super.onLocationResult(locationResult)
        callback.get()?.onLocationResult(locationResult)
    }

    override fun onLocationAvailability(locationAvailability: LocationAvailability?) {
        super.onLocationAvailability(locationAvailability)
        callback.get()?.onLocationAvailability(locationAvailability)
    }

}

private fun createWeakLocationCallback(callback: (Location) -> Unit) =
    WeakLocationCallback(object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            locationResult?.lastLocation?.let { callback(it) }
        }
    })
