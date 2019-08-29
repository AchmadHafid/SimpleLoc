package io.github.achmadhafid.sample_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import io.github.achmadhafid.sample_app.activity.DemoActivity
import io.github.achmadhafid.sample_app.fragment.FragmentDemoActivity
import io.github.achmadhafid.sample_app.service.ServiceDemoActivity
import io.github.achmadhafid.zpack.ktx.bindView
import io.github.achmadhafid.zpack.ktx.onSingleClick
import io.github.achmadhafid.zpack.ktx.startActivity

class MainActivity: AppCompatActivity(R.layout.activity_main) {

    private val btnActivity: MaterialButton by bindView(R.id.btn_demo_activity)
    private val btnFragment: MaterialButton by bindView(R.id.btn_demo_fragment)
    private val btnService: MaterialButton by bindView(R.id.btn_demo_service)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        btnActivity.onSingleClick {
            startActivity<DemoActivity>()
        }
        btnFragment.onSingleClick {
            startActivity<FragmentDemoActivity>()
        }
        btnService.onSingleClick {
            startActivity<ServiceDemoActivity>()
        }
    }
}
