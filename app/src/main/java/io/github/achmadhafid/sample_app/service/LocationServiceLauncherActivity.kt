package io.github.achmadhafid.sample_app.service

import com.google.android.gms.location.LocationRequest
import io.github.achmadhafid.sample_app.Dialog
import io.github.achmadhafid.sample_app.PREFERENCE_KEY_SERVICE_STATUS
import io.github.achmadhafid.sample_app.TRACKING_FASTEST_INTERVAL
import io.github.achmadhafid.sample_app.TRACKING_INTERVAL
import io.github.achmadhafid.simpleloc.SimpleLocResolverActivity
import io.github.achmadhafid.simpleloc.SimpleLocTracker
import io.github.achmadhafid.simpleloc.onLocationServiceRepairError
import io.github.achmadhafid.simpleloc.onOpenPermissionSettingCanceled
import io.github.achmadhafid.simpleloc.onPermissionRationaleCanceled
import io.github.achmadhafid.simpleloc.onRunning
import io.github.achmadhafid.simpleloc.onUnresolvableError
import io.github.achmadhafid.simpleloc.withRequest
import io.github.achmadhafid.simpleloc.simpleLocTracker
import io.github.achmadhafid.simplepref.SimplePref
import io.github.achmadhafid.simplepref.simplePref
import io.github.achmadhafid.zpack.extension.startForegroundServiceCompat

class LocationServiceLauncherActivity : SimpleLocResolverActivity(), SimplePref {

    //region Preference

    private var serviceStatus: Int? by simplePref(PREFERENCE_KEY_SERVICE_STATUS)

    //endregion
    //region Dialog

    override val rationaleDialog = Dialog.rationale
    override val doNotAckAgainDialog = Dialog.doNotAskAgain

    //endregion
    //region Location Tracker

    override val locationTracker = simpleLocTracker {
        fun onCanceled(@Suppress("UNUSED_PARAMETER") tracker: SimpleLocTracker) {
            serviceStatus = DemoLocationService.STATUS_PERMISSION_CANCELED
            finish()
        }

        isAutoStart = true
        backgroundLocationAccess = true
        withRequest {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = TRACKING_INTERVAL
            fastestInterval = TRACKING_FASTEST_INTERVAL
        }
        onRunning { tracker, _ ->
            tracker.disable()
            startForegroundServiceCompat<DemoLocationService>()
            finish()
        }
        onPermissionRationaleCanceled {
            serviceStatus = DemoLocationService.STATUS_PERMISSION_CANCELED
            finish()
        }
        onOpenPermissionSettingCanceled(::onCanceled)
        onLocationServiceRepairError(::onCanceled)
        onUnresolvableError { _, _ ->
            serviceStatus = DemoLocationService.STATUS_UNKNOWN_ERROR
            finish()
        }
    }

    //endregion

}
