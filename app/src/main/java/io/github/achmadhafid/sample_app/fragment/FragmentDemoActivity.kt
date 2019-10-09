package io.github.achmadhafid.sample_app.fragment

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import io.github.achmadhafid.sample_app.R
import io.github.achmadhafid.simplepref.SimplePref
import io.github.achmadhafid.simplepref.simplePref
import io.github.achmadhafid.zpack.ktx.bindView
import io.github.achmadhafid.zpack.ktx.setMaterialToolbar
import io.github.achmadhafid.zpack.ktx.toggleTheme

class FragmentDemoActivity : AppCompatActivity(R.layout.activity_fragment_demo), SimplePref {

    //region Preference

    private var appTheme: Int? by simplePref("app_theme")

    //endregion
    //region View

    private val btn by bindView<MaterialButton>(R.id.btn)
    
    //endregion

    //region Lifecycle Callback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setMaterialToolbar(R.id.toolbar)

        with(supportFragmentManager) {
            fun checkStack() {
                if (backStackEntryCount == 0) {
                    btn.text = "Show Fragment"
                } else {
                    btn.text = "Remove Fragment"
                }
            }
            checkStack()
            addOnBackStackChangedListener {
                checkStack()
            }
            btn.setOnClickListener {
                if (backStackEntryCount == 0) {
                    beginTransaction()
                        .replace(R.id.fragment_holder, LocationTrackerFragment())
                        .addToBackStack(LocationTrackerFragment::class.java.name)
                        .commit()
                } else {
                    popBackStack()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        supportFragmentManager.findFragmentById(R.id.fragment_holder)
            ?.onActivityResult(requestCode, resultCode, data)
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
