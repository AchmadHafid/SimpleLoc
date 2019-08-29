package io.github.achmadhafid.sample_app.service

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import io.github.achmadhafid.sample_app.R
import io.github.achmadhafid.simpleloc.SimpleLocClient
import io.github.achmadhafid.zpack.ktx.bindView
import io.github.achmadhafid.zpack.ktx.onSingleClick
import io.github.achmadhafid.zpack.ktx.startActivity
import io.github.achmadhafid.zpack.ktx.toastShort

class ServiceDemoActivity : AppCompatActivity(R.layout.activity_service_demo), SimpleLocClient,
    SharedPreferences.OnSharedPreferenceChangeListener {

    private val btn: MaterialButton by bindView(R.id.btn)
    private val tv: TextView by bindView(R.id.tv_location)

    private val sharedPreferences by lazy {
        getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        btn.onSingleClick {
            if (sharedPreferences.getInt(
                    "location_service_status",
                    DemoLocationService.STATUS_STOPPED
                ) == DemoLocationService.STATUS_RUNNING
            ) {
                stopLocationService()
            } else {
                startLocationService()
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationService()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(preferences: SharedPreferences, key: String) {
        if (key == "location_service_status") {
            when (preferences.getInt(key, DemoLocationService.STATUS_STOPPED)) {
                DemoLocationService.STATUS_STOPPED -> startLocationService()
                DemoLocationService.STATUS_PERMISSION_CANCELED -> toastShort("Permissions required")
                DemoLocationService.STATUS_REPAIR_ERROR -> toastShort("Location Service unavailable")
                DemoLocationService.STATUS_UNKNOWN_ERROR -> toastShort("Unknown Error")
            }
        }
    }

    private fun startLocationService() {
        startActivity<LocationServiceLauncherActivity>()
    }

    private fun stopLocationService() {
        stopService(Intent(this, DemoLocationService::class.java))
    }

}
