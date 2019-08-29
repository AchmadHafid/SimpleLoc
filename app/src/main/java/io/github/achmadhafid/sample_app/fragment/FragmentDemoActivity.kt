package io.github.achmadhafid.sample_app.fragment

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import io.github.achmadhafid.sample_app.R
import io.github.achmadhafid.zpack.ktx.bindView

class FragmentDemoActivity : AppCompatActivity(R.layout.activity_fragment_demo) {

    private val btn by bindView<MaterialButton>(R.id.btn)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(supportFragmentManager) {
            addOnBackStackChangedListener {
                if (backStackEntryCount == 0) {
                    btn.text = "Show Fragment"
                } else {
                    btn.text = "Remove Fragment"
                }
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
}
