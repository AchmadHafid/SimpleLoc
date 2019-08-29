package io.github.achmadhafid.simpleloc

import android.location.Address
import android.location.Location
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationSettingsRequest

data class SimpleLocConfig(
    var isAutoStart: Boolean = false,
    var resolveAddress: Boolean = false,
    var locationRequest: LocationRequest = LocationRequest.create(),
    var onRunningListener: (isRestarted: Boolean) -> Unit = {},
    var onStoppedListener: (SimpleLocTracker.StopState) -> Unit = {},
    var onLocationFoundListener: (SimpleLocTracker, Location, List<Address>) -> Unit = { _, _, _ -> },
    var onPermissionRationaleCanceledListener: () -> Unit = {},
    var onOpenPermissionSettingCanceledListener: () -> Unit = {},
    var onLocationServiceRepairErrorListener: () -> Unit = {},
    var onUnresolvableErrorListener: (Exception) -> Unit = {}
)

fun simpleLocConfig(builder: SimpleLocConfig.() -> Unit): SimpleLocConfig =
    SimpleLocConfig().apply(builder)

fun SimpleLocConfig.request(locationRequest: LocationRequest.() -> Unit) {
    this.locationRequest.apply(locationRequest)
}

fun SimpleLocConfig.onRunning(callback: (isRestarted: Boolean) -> Unit) {
    onRunningListener = callback
}

fun SimpleLocConfig.onStopped(callback: (SimpleLocTracker.StopState) -> Unit) {
    onStoppedListener = callback
}

fun SimpleLocConfig.onLocationFound(callback: (SimpleLocTracker, Location, List<Address>) -> Unit) {
    onLocationFoundListener = callback
}

fun SimpleLocConfig.onPermissionRationaleCanceled(callback: () -> Unit) {
    onPermissionRationaleCanceledListener = callback
}

fun SimpleLocConfig.onOpenPermissionSettingCanceled(callback: () -> Unit) {
    onOpenPermissionSettingCanceledListener = callback
}

fun SimpleLocConfig.onLocationServiceRepairError(callback: () -> Unit) {
    onLocationServiceRepairErrorListener = callback
}

fun SimpleLocConfig.onUnresolvableError(callback: (Exception) -> Unit) {
    onUnresolvableErrorListener = callback
}

internal val SimpleLocConfig.settingsRequest
    get() = LocationSettingsRequest.Builder()
        .setAlwaysShow(true)
        .addLocationRequest(locationRequest)
        .build()
