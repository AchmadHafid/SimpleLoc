package io.github.achmadhafid.sample_app

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import io.github.achmadhafid.sample_app.activity.DemoActivity
import io.github.achmadhafid.sample_app.fragment.FragmentDemoActivity
import io.github.achmadhafid.sample_app.service.ServiceDemoActivity
import io.github.achmadhafid.simpleloc.SimpleLocClient
import io.github.achmadhafid.simplepref.SimplePref
import io.github.achmadhafid.simplepref.simplePref
import io.github.achmadhafid.zpack.ktx.onSingleClick
import io.github.achmadhafid.zpack.ktx.setMaterialToolbar
import io.github.achmadhafid.zpack.ktx.startActivity
import io.github.achmadhafid.zpack.ktx.toggleTheme

@Suppress("MagicNumber")
class MainActivity: AppCompatActivity(R.layout.activity_main), SimpleLocClient, SimplePref {

    //region Preference

    private var appTheme: Int? by simplePref("app_theme")

    //endregion
    //region Lifecycle Callback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setMaterialToolbar(R.id.toolbar)
        findViewById<MaterialButton>(R.id.btn_demo_activity).onSingleClick {
            startActivity<DemoActivity>()
        }
        findViewById<MaterialButton>(R.id.btn_demo_fragment).onSingleClick {
            startActivity<FragmentDemoActivity>()
        }
        findViewById<MaterialButton>(R.id.btn_demo_service).onSingleClick {
            startActivity<ServiceDemoActivity>()
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
