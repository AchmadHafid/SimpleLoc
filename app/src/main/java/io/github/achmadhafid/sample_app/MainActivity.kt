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
import io.github.achmadhafid.zpack.ktx.onSingleClick
import io.github.achmadhafid.zpack.ktx.setMaterialToolbar
import io.github.achmadhafid.zpack.ktx.startActivity
import io.github.achmadhafid.zpack.ktx.toggleTheme

class MainActivity: AppCompatActivity(), SimpleLocClient, SimplePref {

    //region Preference

    private var appTheme: Int? by simplePref("app_theme")

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
        setMaterialToolbar(R.id.toolbar)

        binding.apply {
            btnDemoActivity.onSingleClick {
                startActivity<DemoActivity>()
            }
            btnDemoFragment.onSingleClick {
                startActivity<FragmentDemoActivity>()
            }
            btnDemoService.onSingleClick {
                startActivity<ServiceDemoActivity>()
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
