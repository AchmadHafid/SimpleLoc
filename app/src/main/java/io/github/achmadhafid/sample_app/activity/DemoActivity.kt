package io.github.achmadhafid.sample_app.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
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
import io.github.achmadhafid.simpleloc.request
import io.github.achmadhafid.simpleloc.simpleLocTracker
import io.github.achmadhafid.simpleloc.toggle
import io.github.achmadhafid.zpack.ktx.bindView
import io.github.achmadhafid.zpack.ktx.setMaterialToolbar
import io.github.achmadhafid.zpack.ktx.toastShort
import io.github.achmadhafid.zpack.ktx.toggleTheme

class DemoActivity : AppCompatActivity(R.layout.activity_demo), SimpleLocClient {

    private val button: MaterialButton by bindView(R.id.btn)

    private val locationTracker = simpleLocTracker {
        resolveAddress = true
        request {
            priority        = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval        = 5 * 1000L
            fastestInterval = 3 * 1000L
        }
        onRunning { _, isRestarted ->
            toastShort("Location tracking " + if (isRestarted) "re-started" else "started")
            button.text = "Stop Tracking"
        }
        onStopped { _, state ->
            val message = when (state) {
                SimpleLocTracker.StopState.PAUSED_BY_LIFECYCLE    -> "Location tracking paused"
                SimpleLocTracker.StopState.DESTROYED_BY_LIFECYCLE -> "Location tracking destroyed by lifecycle"
                SimpleLocTracker.StopState.STOPPED_BY_SYSTEM      -> "Location tracking become unavailable".also {
                    button.text = "Start Tracking"
                }
                SimpleLocTracker.StopState.STOPPED_BY_USER        -> "Location tracking stopped by user".also {
                    button.text = "Start Tracking"
                }
            }
            toastShort(message)
        }
        onLocationFound { tracker, location, addresses ->
            toastShort("Location found, accuracy: ${location.accuracy}, total address: ${addresses.size}")
            if (location.accuracy < 20) {
                tracker.disable()
            }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setMaterialToolbar(R.id.toolbar)
        findViewById<MaterialButton>(R.id.btn).setOnClickListener {
            locationTracker.toggle()
        }
    }

    //region Toolbar Menu

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_toggle_theme -> {
            toggleTheme()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    //endregion

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

}
