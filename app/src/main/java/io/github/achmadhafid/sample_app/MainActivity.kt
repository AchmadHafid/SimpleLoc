package io.github.achmadhafid.sample_app

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import io.github.achmadhafid.sample_app.activity.DemoActivity
import io.github.achmadhafid.sample_app.databinding.ActivityMainBinding
import io.github.achmadhafid.sample_app.fragment.FragmentDemoActivity
import io.github.achmadhafid.sample_app.service.ServiceDemoActivity
import io.github.achmadhafid.simpleloc.SimpleLocClient
import io.github.achmadhafid.simplepref.SimplePref
import io.github.achmadhafid.simplepref.simplePref
import io.github.achmadhafid.zpack.extension.intent
import io.github.achmadhafid.zpack.extension.toggleTheme

class MainActivity : AppCompatActivity(), SimpleLocClient, SimplePref {

    //region Preference

    private var appTheme: Int? by simplePref(PREFERENCE_KEY_THEME)

    //endregion
    //region View Binding

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    //endregion
    //region Lifecycle Callback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.apply {
            btnDemoActivity.setOnClickListener {
                startActivity(intent<DemoActivity>())
            }
            btnDemoFragment.setOnClickListener {
                startActivity(intent<FragmentDemoActivity>())
            }
            btnDemoService.setOnClickListener {
                startActivity(intent<ServiceDemoActivity>())
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

    //endregion
}
