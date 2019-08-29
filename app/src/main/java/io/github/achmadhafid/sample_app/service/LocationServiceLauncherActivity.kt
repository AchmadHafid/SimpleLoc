package io.github.achmadhafid.sample_app.service

import android.content.Context
import androidx.core.content.edit
import com.google.android.gms.location.LocationRequest
import io.github.achmadhafid.sample_app.Dialog
import io.github.achmadhafid.sample_app.R
import io.github.achmadhafid.simpleloc.SimpleLocResolverActivity
import io.github.achmadhafid.simpleloc.onLocationServiceRepairError
import io.github.achmadhafid.simpleloc.onOpenPermissionSettingCanceled
import io.github.achmadhafid.simpleloc.onPermissionRationaleCanceled
import io.github.achmadhafid.simpleloc.onRunning
import io.github.achmadhafid.simpleloc.onUnresolvableError
import io.github.achmadhafid.simpleloc.request
import io.github.achmadhafid.simpleloc.simpleLocTracker
import io.github.achmadhafid.zpack.ktx.startForegroundServiceCompat

class LocationServiceLauncherActivity : SimpleLocResolverActivity() {

    private val sharedPreferences by lazy {
        getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
    }

    override val rationaleDialog     = Dialog.rationale
    override val doNotAckAgainDialog = Dialog.doNotAskAgain
    override val locationTracker = simpleLocTracker {
        isAutoStart = true
        request {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 5 * 1000L
            fastestInterval = 3 * 1000L
        }
        onRunning {_, _ -> onStartLocationService() }
        onPermissionRationaleCanceled { onCanceled() }
        onOpenPermissionSettingCanceled { onCanceled() }
        onLocationServiceRepairError {
            sharedPreferences.edit {
                putInt("location_service_status", DemoLocationService.STATUS_REPAIR_ERROR)
            }
            finish()
        }
        onUnresolvableError {_, _ ->
            sharedPreferences.edit {
                putInt("location_service_status", DemoLocationService.STATUS_UNKNOWN_ERROR)
            }
            finish()
        }
    }

    private fun onStartLocationService() {
        sharedPreferences.edit {
            putInt("location_service_status", DemoLocationService.STATUS_RUNNING)
        }
        locationTracker.disable()
        startForegroundServiceCompat<DemoLocationService>()
        finish()
    }

    private fun onCanceled() {
        sharedPreferences.edit {
            putInt("location_service_status", DemoLocationService.STATUS_PERMISSION_CANCELED)
        }
        finish()
    }

}
