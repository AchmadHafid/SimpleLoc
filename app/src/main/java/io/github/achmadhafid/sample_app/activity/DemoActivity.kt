package io.github.achmadhafid.sample_app.activity

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.LocationRequest
import io.github.achmadhafid.sample_app.Dialog
import io.github.achmadhafid.sample_app.PREFERENCE_KEY_THEME
import io.github.achmadhafid.sample_app.R
import io.github.achmadhafid.sample_app.TRACKING_FASTEST_INTERVAL
import io.github.achmadhafid.sample_app.TRACKING_INTERVAL
import io.github.achmadhafid.sample_app.databinding.ActivityDemoBinding
import io.github.achmadhafid.simpleloc.SimpleLocClient
import io.github.achmadhafid.simpleloc.SimpleLocTracker
import io.github.achmadhafid.simpleloc.onLocationFound
import io.github.achmadhafid.simpleloc.onLocationPermissionResult
import io.github.achmadhafid.simpleloc.onLocationServiceRepairError
import io.github.achmadhafid.simpleloc.onLocationServiceRepairResult
import io.github.achmadhafid.simpleloc.onLocationSettingsUnavailable
import io.github.achmadhafid.simpleloc.onOpenPermissionSettingCanceled
import io.github.achmadhafid.simpleloc.onPermissionRationaleCanceled
import io.github.achmadhafid.simpleloc.onRunning
import io.github.achmadhafid.simpleloc.onStopped
import io.github.achmadhafid.simpleloc.onUnresolvableError
import io.github.achmadhafid.simpleloc.openInGMaps
import io.github.achmadhafid.simpleloc.simpleLocTracker
import io.github.achmadhafid.simpleloc.toggle
import io.github.achmadhafid.simpleloc.withRequest
import io.github.achmadhafid.simplepref.SimplePref
import io.github.achmadhafid.simplepref.simplePref
import io.github.achmadhafid.zpack.extension.toastShort
import io.github.achmadhafid.zpack.extension.toggleTheme
import io.github.achmadhafid.zpack.extension.view.invisible
import io.github.achmadhafid.zpack.extension.view.setTextRes
import io.github.achmadhafid.zpack.extension.view.visible
import io.github.achmadhafid.zpack.extension.view.visibleIf
import kotlinx.android.synthetic.main.activity_demo.btnShowLocation

class DemoActivity : AppCompatActivity(), SimpleLocClient, SimplePref {

    //region Preference

    private var appTheme: Int? by simplePref(PREFERENCE_KEY_THEME)
    private var isRestarting by simplePref { false }
    private var isRunning by simplePref { false }

    //endregion
    //region View Binding

    private val binding: ActivityDemoBinding by lazy {
        ActivityDemoBinding.inflate(layoutInflater)
    }

    //endregion
    //region Location Tracker

    private var currentLocation: Location? = null

    private val locationTracker = simpleLocTracker {
        resolveAddress = true
        withRequest {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = TRACKING_INTERVAL
            fastestInterval = TRACKING_FASTEST_INTERVAL
        }
        onRunning { _, isRestarted ->
            isRunning = true
            binding.btn.setTextRes(R.string.label_stop_tracking)
            toastShort(
                if (isRestarted) R.string.message_tracking_restarted
                else R.string.message_tracking_started
            )
        }
        onStopped { _, state ->
            isRunning = false
            btnShowLocation.invisible()

            when (state) {
                SimpleLocTracker.StopState.PAUSED_BY_LIFECYCLE -> {
                    toastShort(R.string.message_tracking_paused)
                }
                SimpleLocTracker.StopState.DESTROYED_BY_LIFECYCLE -> {
                    if (isRestarting) {
                        isRunning = true
                        return@onStopped
                    } else {
                        toastShort(R.string.message_tracking_destroyed_by_lifecycle)
                    }
                }
                SimpleLocTracker.StopState.STOPPED_BY_SYSTEM -> {
                    binding.btn.setTextRes(R.string.label_start_tracking)
                    toastShort(R.string.message_tracking_become_unavailable)
                }
                SimpleLocTracker.StopState.STOPPED_BY_USER -> {
                    binding.btn.setTextRes(R.string.label_start_tracking)
                    toastShort(R.string.message_tracking_stopped_by_user)
                }
            }
        }
        onLocationFound { _, location, _ ->
            currentLocation = location
            btnShowLocation.visible()
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
        onLocationSettingsUnavailable { _, isAirPlaneModeOn ->
            toastShort(
                if (isAirPlaneModeOn) R.string.message_location_air_plane_mode_on
                else R.string.message_location_settings_unavailable
            )
        }
        onUnresolvableError { _, exception ->
            toastShort(getString(R.string.message_location_setting_error, exception.message))
        }
    }

    //endregion
    //region Lifecycle Callback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.btn.setOnClickListener {
            locationTracker.toggle()
        }
        binding.btnShowLocation.setOnClickListener {
            currentLocation?.openInGMaps(this)
        }
        binding.btnShowLocation.visibleIf { currentLocation != null }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        isRestarting = true
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        isRestarting = false
        if (isRunning) locationTracker.enable()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_toggle_theme -> {
            appTheme = toggleTheme()
            true
        }
        else -> super.onOptionsItemSelected(item)
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

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        onLocationServiceRepairResult(requestCode, resultCode, locationTracker)
    }

    //endregion

}
