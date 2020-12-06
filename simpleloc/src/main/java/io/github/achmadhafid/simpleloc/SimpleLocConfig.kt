package io.github.achmadhafid.simpleloc

import android.location.Address
import android.location.Location
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationSettingsRequest

data class SimpleLocConfig(
    var requestCode: Int = SIMPLE_LOC_REQUEST_CODE,
    var isAutoStart: Boolean = false,
    var backgroundLocationAccess: Boolean = false,
    var resolveAddress: Boolean = false,
    var locationRequest: LocationRequest = LocationRequest.create(),
    var onRunningListener: (SimpleLocTracker, isRestarted: Boolean) -> Unit = {_, _ ->},
    var onStoppedListener: (SimpleLocTracker, SimpleLocTracker.StopState) -> Unit = {_, _ -> },
    var onLocationFoundListener: (SimpleLocTracker, Location, List<Address>) -> Unit = { _, _, _ -> },
    var onPermissionRationaleCanceledListener: (SimpleLocTracker) -> Unit = {},
    var onOpenPermissionSettingCanceledListener: (SimpleLocTracker) -> Unit = {},
    var onLocationServiceRepairErrorListener: (SimpleLocTracker) -> Unit = {},
    var onUnresolvableErrorListener: (SimpleLocTracker, Exception) -> Unit = {_, _ ->}
)

fun simpleLocConfig(builder: SimpleLocConfig.() -> Unit): SimpleLocConfig =
    SimpleLocConfig().apply(builder)

fun SimpleLocConfig.withRequest(locationRequest: LocationRequest.() -> Unit) {
    this.locationRequest.apply(locationRequest)
}

fun SimpleLocConfig.onRunning(callback: (SimpleLocTracker, isRestarted: Boolean) -> Unit) {
    onRunningListener = callback
}

fun SimpleLocConfig.onStopped(callback: (SimpleLocTracker, SimpleLocTracker.StopState) -> Unit) {
    onStoppedListener = callback
}

fun SimpleLocConfig.onLocationFound(callback: (SimpleLocTracker, Location, List<Address>) -> Unit) {
    onLocationFoundListener = callback
}

fun SimpleLocConfig.onPermissionRationaleCanceled(callback: (SimpleLocTracker) -> Unit) {
    onPermissionRationaleCanceledListener = callback
}

fun SimpleLocConfig.onOpenPermissionSettingCanceled(callback: (SimpleLocTracker) -> Unit) {
    onOpenPermissionSettingCanceledListener = callback
}

fun SimpleLocConfig.onLocationServiceRepairError(callback: (SimpleLocTracker) -> Unit) {
    onLocationServiceRepairErrorListener = callback
}

fun SimpleLocConfig.onUnresolvableError(callback: (SimpleLocTracker, Exception) -> Unit) {
    onUnresolvableErrorListener = callback
}

internal val SimpleLocConfig.settingsRequest
    get() = LocationSettingsRequest.Builder()
        .setAlwaysShow(true)
        .addLocationRequest(locationRequest)
        .build()
