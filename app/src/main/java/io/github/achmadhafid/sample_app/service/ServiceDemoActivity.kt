package io.github.achmadhafid.sample_app.service

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import io.github.achmadhafid.sample_app.PREFERENCE_KEY_SERVICE_STATUS
import io.github.achmadhafid.sample_app.PREFERENCE_KEY_THEME
import io.github.achmadhafid.sample_app.R
import io.github.achmadhafid.sample_app.databinding.ActivityServiceDemoBinding
import io.github.achmadhafid.simpleloc.SimpleLocClient
import io.github.achmadhafid.simplepref.SimplePref
import io.github.achmadhafid.simplepref.livedata.simplePrefLiveData
import io.github.achmadhafid.simplepref.simplePref
import io.github.achmadhafid.zpack.extension.intent
import io.github.achmadhafid.zpack.extension.stopService
import io.github.achmadhafid.zpack.extension.toastShort
import io.github.achmadhafid.zpack.extension.toggleTheme
import io.github.achmadhafid.zpack.extension.view.setTextRes

class ServiceDemoActivity : AppCompatActivity(), SimpleLocClient, SimplePref {

    //region Preference

    private var appTheme: Int? by simplePref(PREFERENCE_KEY_THEME)
    private val serviceStatus: Int? by simplePref(PREFERENCE_KEY_SERVICE_STATUS)
    private var keepServiceAlive by simplePref { false }

    //endregion
    //region View Binding

    private val binding: ActivityServiceDemoBinding by lazy {
        ActivityServiceDemoBinding.inflate(layoutInflater)
    }

    //endregion
    //region Lifecycle Callback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.btn.setOnClickListener {
            if (serviceStatus == DemoLocationService.STATUS_RUNNING)
                stopService<DemoLocationService>()
            else startActivity(intent<LocationServiceLauncherActivity>())
        }

        simplePrefLiveData(serviceStatus, ::serviceStatus).observe(this) {
            binding.btn.setTextRes(
                if (it == DemoLocationService.STATUS_RUNNING) R.string.label_stop_location_service
                else R.string.label_start_location_service
            )
            when (it) {
                DemoLocationService.STATUS_PERMISSION_CANCELED -> toastShort(R.string.message_permission_required)
                DemoLocationService.STATUS_REPAIR_ERROR -> toastShort(R.string.message_location_service_unavailable)
                DemoLocationService.STATUS_UNKNOWN_ERROR -> toastShort(R.string.message_error_unknown)
            }
        }
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

    override fun onStart() {
        super.onStart()
        keepServiceAlive = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        keepServiceAlive = true
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!keepServiceAlive) {
            stopService<DemoLocationService>()
        }
    }

    //endregion

}
