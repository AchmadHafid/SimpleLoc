package io.github.achmadhafid.sample_app.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.gms.location.LocationRequest
import com.google.android.material.button.MaterialButton
import io.github.achmadhafid.sample_app.Dialog
import io.github.achmadhafid.sample_app.R
import io.github.achmadhafid.simpleloc.SimpleLocClient
import io.github.achmadhafid.simpleloc.SimpleLocTracker
import io.github.achmadhafid.simpleloc.onLocationFound
import io.github.achmadhafid.simpleloc.onLocationPermissionResult
import io.github.achmadhafid.simpleloc.onLocationServiceRepairError
import io.github.achmadhafid.simpleloc.onLocationServiceRepairResult
import io.github.achmadhafid.simpleloc.onOpenPermissionSettingCanceled
import io.github.achmadhafid.simpleloc.onPermissionRationaleCanceled
import io.github.achmadhafid.simpleloc.onRunning
import io.github.achmadhafid.simpleloc.onStopped
import io.github.achmadhafid.simpleloc.onUnresolvableError
import io.github.achmadhafid.simpleloc.withRequest
import io.github.achmadhafid.simpleloc.simpleLocTracker
import io.github.achmadhafid.simpleloc.toggle
import io.github.achmadhafid.zpack.delegate.fragmentView
import io.github.achmadhafid.zpack.ktx.toastShort

@Suppress("MagicNumber")
class LocationTrackerFragment : Fragment(R.layout.fragment_location_tracker), SimpleLocClient {

    //region View

    private val btn by fragmentView<MaterialButton>(R.id.btn)

    //endregion
    //region Location Tracker

    private val locationTracker = simpleLocTracker {
        isAutoStart    = true
        resolveAddress = true
        withRequest {
            priority        = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval        = 5 * 1000L
            fastestInterval = 3 * 1000L
        }
        onRunning { _, isRestarted ->
            btn?.text = "Stop Tracker"
            toastShort("Location tracking " + if (isRestarted) "re-started" else "started")
        }
        onStopped { _, state ->
            btn?.text = "Start Tracker"
            val message = when (state) {
                SimpleLocTracker.StopState.PAUSED_BY_LIFECYCLE    -> "Location tracking paused"
                SimpleLocTracker.StopState.DESTROYED_BY_LIFECYCLE -> "Location tracking destroyed by lifecycle"
                SimpleLocTracker.StopState.STOPPED_BY_SYSTEM      -> "Location tracking become unavailable"
                SimpleLocTracker.StopState.STOPPED_BY_USER        -> "Location tracking stopped by user"
            }
            toastShort(message)
        }
        onLocationFound { _, location, addresses ->
            toastShort("Location found, accuracy: ${location.accuracy}, total address: ${addresses.size}")
        }
        onPermissionRationaleCanceled {
            toastShort("Location Permissions canceled by user")
        }
        onOpenPermissionSettingCanceled {
            toastShort("User do not want to open permission setting")
        }
        onLocationServiceRepairError {
            toastShort("Location service is not available")
        }
        onUnresolvableError {_, exception ->
            toastShort("Location Setting ERROR: ${exception.message}")
        }
    }

    //endregion

    //region Lifecycle Callback

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn?.setOnClickListener { locationTracker.toggle() }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onLocationPermissionResult(
            requestCode, permissions, grantResults,
            locationTracker,
            Dialog.rationale, Dialog.doNotAskAgain
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        onLocationServiceRepairResult(requestCode, resultCode, locationTracker)
    }

    //endregion

}
