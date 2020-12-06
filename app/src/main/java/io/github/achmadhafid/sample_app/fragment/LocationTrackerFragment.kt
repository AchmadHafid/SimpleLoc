package io.github.achmadhafid.sample_app.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.location.LocationRequest
import io.github.achmadhafid.sample_app.Dialog
import io.github.achmadhafid.sample_app.R
import io.github.achmadhafid.sample_app.TRACKING_FASTEST_INTERVAL
import io.github.achmadhafid.sample_app.TRACKING_INTERVAL
import io.github.achmadhafid.sample_app.databinding.FragmentLocationTrackerBinding
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
import io.github.achmadhafid.simpleloc.simpleLocTracker
import io.github.achmadhafid.simpleloc.toggle
import io.github.achmadhafid.simpleloc.withRequest
import io.github.achmadhafid.zpack.delegate.viewLifecycleVar
import io.github.achmadhafid.zpack.extension.toastShort
import io.github.achmadhafid.zpack.extension.view.setTextRes

class LocationTrackerFragment : Fragment(), SimpleLocClient {

    //region View Binding

    private var _binding: FragmentLocationTrackerBinding? by viewLifecycleVar()
    private val binding get() = _binding!!

    //endregion
    //region Location Tracker

    private val locationTracker = simpleLocTracker {
        isAutoStart = true
        resolveAddress = true
        withRequest {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = TRACKING_INTERVAL
            fastestInterval = TRACKING_FASTEST_INTERVAL
        }
        onRunning { _, isRestarted ->
            binding.btn.setTextRes(R.string.label_stop_tracking)
            toastShort(
                if (isRestarted) R.string.message_tracking_restarted
                else R.string.message_tracking_started
            )
        }
        onStopped { _, state ->
            binding.btn.setTextRes(R.string.label_start_tracking)
            val message = when (state) {
                SimpleLocTracker.StopState.PAUSED_BY_LIFECYCLE -> R.string.message_tracking_paused
                SimpleLocTracker.StopState.DESTROYED_BY_LIFECYCLE -> R.string.message_tracking_destroyed_by_lifecycle
                SimpleLocTracker.StopState.STOPPED_BY_SYSTEM -> R.string.message_tracking_become_unavailable
                SimpleLocTracker.StopState.STOPPED_BY_USER -> R.string.message_tracking_stopped_by_user
            }
            toastShort(message)
        }
        onLocationFound { _, location, addresses ->
            toastShort(getString(R.string.label_location_found, location.accuracy, addresses.size))
        }
        onPermissionRationaleCanceled {
            toastShort(R.string.message_permission_canceled)
        }
        onOpenPermissionSettingCanceled {
            toastShort(R.string.message_permission_setting_canceled)
        }
        onLocationServiceRepairError {
            toastShort(R.string.message_location_service_is_not_available)
        }
        onUnresolvableError { _, exception ->
            toastShort(getString(R.string.message_location_setting_error, exception.message))
        }
    }

    //endregion

    //region Lifecycle Callback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationTrackerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btn.setOnClickListener { locationTracker.toggle() }
    }

    @Suppress("DEPRECATION")
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

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        onLocationServiceRepairResult(requestCode, resultCode, locationTracker)
    }

    //endregion

}
