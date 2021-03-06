package io.github.achmadhafid.sample_app.fragment

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import io.github.achmadhafid.sample_app.PREFERENCE_KEY_THEME
import io.github.achmadhafid.sample_app.R
import io.github.achmadhafid.sample_app.databinding.ActivityFragmentDemoBinding
import io.github.achmadhafid.simplepref.SimplePref
import io.github.achmadhafid.simplepref.simplePref
import io.github.achmadhafid.zpack.extension.toggleTheme
import io.github.achmadhafid.zpack.extension.view.setTextRes

class FragmentDemoActivity : AppCompatActivity(), SimplePref {

    //region Preference

    private var appTheme: Int? by simplePref(PREFERENCE_KEY_THEME)

    //endregion
    //region View Binding

    private val binding: ActivityFragmentDemoBinding by lazy {
        ActivityFragmentDemoBinding.inflate(layoutInflater)
    }

    //endregion
    //region Lifecycle Callback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        with(supportFragmentManager) {
            fun checkStack() {
                binding.btn.setTextRes(
                    if (backStackEntryCount == 0) R.string.label_show_fragment
                    else R.string.label_remove_fragment
                )
            }
            checkStack()
            addOnBackStackChangedListener {
                checkStack()
            }
            binding.btn.setOnClickListener {
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

    @Suppress("DEPRECATION")
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
