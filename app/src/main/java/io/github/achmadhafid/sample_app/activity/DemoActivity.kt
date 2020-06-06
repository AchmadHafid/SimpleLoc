package io.github.achmadhafid.sample_app.activity

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.LocationRequest
import io.github.achmadhafid.sample_app.Dialog
import io.github.achmadhafid.sample_app.R
import io.github.achmadhafid.sample_app.databinding.ActivityDemoBinding
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
import io.github.achmadhafid.simpleloc.openInGMaps
import io.github.achmadhafid.simpleloc.simpleLocTracker
import io.github.achmadhafid.simpleloc.toggle
import io.github.achmadhafid.simpleloc.withRequest
import io.github.achmadhafid.simplepref.SimplePref
import io.github.achmadhafid.simplepref.simplePref
import io.github.achmadhafid.zpack.extension.toastShort
import io.github.achmadhafid.zpack.extension.toggleTheme
import io.github.achmadhafid.zpack.extension.view.invisible
import io.github.achmadhafid.zpack.extension.view.onSingleClick
import io.github.achmadhafid.zpack.extension.view.visible
import io.github.achmadhafid.zpack.extension.view.visibleIf
import kotlinx.android.synthetic.main.activity_demo.btnShowLocation

@Suppress("MagicNumber")
class DemoActivity : AppCompatActivity(), SimpleLocClient, SimplePref {

    //region Preference

    private var appTheme: Int? by simplePref("app_theme")
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
            priority        = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval        = 5 * 1000L
            fastestInterval = 3 * 1000L
        }
        onRunning { _, isRestarted ->
            isRunning = true
            binding.btn.text = "Stop Tracking"
            toastShort("Location tracking " + if (isRestarted) "re-started" else "started")
        }
        onStopped { _, state ->
            isRunning = false
            btnShowLocation.invisible()

            val message = when (state) {
                SimpleLocTracker.StopState.PAUSED_BY_LIFECYCLE -> "Location tracking paused"
                SimpleLocTracker.StopState.DESTROYED_BY_LIFECYCLE -> {
                    if (isRestarting) {
                        isRunning = true
                        return@onStopped
                    } else {
                        "Location tracking destroyed by lifecycle"
                    }
                }
                SimpleLocTracker.StopState.STOPPED_BY_SYSTEM -> "Location tracking become unavailable".also {
                    binding.btn.text = "Start Tracking"
                }
                SimpleLocTracker.StopState.STOPPED_BY_USER -> "Location tracking stopped by user".also {
                    binding.btn.text = "Start Tracking"
                }
            }
            toastShort(message)
        }
        onLocationFound { _, location, _ ->
            currentLocation = location
            btnShowLocation.visible()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.btn.onSingleClick {
            locationTracker.toggle()
        }
        binding.btnShowLocation.onSingleClick {
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
