package io.github.achmadhafid.simpleloc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import io.github.achmadhafid.lottie_dialog.core.LottieConfirmationDialog

abstract class SimpleLocResolverActivity : AppCompatActivity(R.layout.simpleloc_resolver_activity), SimpleLocClient {

    protected abstract val rationaleDialog: LottieConfirmationDialog.() -> Unit
    protected abstract val doNotAckAgainDialog: LottieConfirmationDialog.() -> Unit
    protected abstract val locationTracker: SimpleLocTrackerActivity

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onLocationPermissionResult(
            requestCode, permissions, grantResults,
            locationTracker,
            rationaleDialog, doNotAckAgainDialog
        )
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        onLocationServiceRepairResult(requestCode, resultCode, locationTracker)
    }

}
