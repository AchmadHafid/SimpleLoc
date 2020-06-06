@file:Suppress("TooManyFunctions", "unused")

package io.github.achmadhafid.simpleloc

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleService
import io.github.achmadhafid.lottie_dialog.core.LottieConfirmationDialog

interface SimpleLocClient
internal interface SimpleLocClientError {
    fun onError(exception: Exception)
}

fun <T> T.simpleLocTracker(
    builder: SimpleLocConfig.() -> Unit
) where T : AppCompatActivity, T : SimpleLocClient =
    SimpleLocTrackerActivity(this, simpleLocConfig(builder))

fun <T> T.simpleLocTracker(
    builder: SimpleLocConfig.() -> Unit
) where T : Fragment, T : SimpleLocClient =
    SimpleLocTrackerFragment(this, simpleLocConfig(builder))

fun <T> T.simpleLocTracker(
    builder: SimpleLocConfig.() -> Unit
) where T : LifecycleService, T : SimpleLocClient =
    SimpleLocTrackerService(this, simpleLocConfig(builder))

@Suppress("LongParameterList")
fun <T> T.onLocationPermissionResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray,
    locationTracker: SimpleLocTrackerActivity,
    rationaleDialogBuilder: LottieConfirmationDialog.() -> Unit,
    doNotAskAgainDialogBuilder: LottieConfirmationDialog.() -> Unit
) where T : AppCompatActivity, T : SimpleLocClient {
    if (locationTracker.config.requestCode != requestCode) return

    onLocationPermissionResult(
        requestCode, permissions, grantResults,
        rationaleDialogBuilder, doNotAskAgainDialogBuilder
    ) { userResponse ->
        when (userResponse) {
            PermissionResultUserResponse.NO_RESPONSE -> locationTracker.enable()
            PermissionResultUserResponse.RATIONALE_DIALOG_POSITIVE -> {
            }
            PermissionResultUserResponse.RATIONALE_DIALOG_NEGATIVE,
            PermissionResultUserResponse.RATIONALE_DIALOG_CANCELED -> {
                locationTracker.config
                    .onPermissionRationaleCanceledListener(locationTracker)
            }
            PermissionResultUserResponse.DO_NOT_ASK_AGAIN_DIALOG_POSITIVE -> {
                locationTracker.isOpeningLocationPermissionSetting = true
            }
            PermissionResultUserResponse.DO_NOT_ASK_AGAIN_DIALOG_NEGATIVE,
            PermissionResultUserResponse.DO_NOT_ASK_AGAIN_DIALOG_CANCELED -> {
                locationTracker.config
                    .onOpenPermissionSettingCanceledListener(locationTracker)
            }
        }
    }
}

@Suppress("LongParameterList")
fun <T> T.onLocationPermissionResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray,
    locationTracker: SimpleLocTrackerFragment,
    rationaleDialogBuilder: LottieConfirmationDialog.() -> Unit,
    doNotAskAgainDialogBuilder: LottieConfirmationDialog.() -> Unit
) where T : Fragment, T : SimpleLocClient {
    if (locationTracker.config.requestCode != requestCode) return

    onLocationPermissionResult(
        requestCode, permissions, grantResults,
        rationaleDialogBuilder, doNotAskAgainDialogBuilder
    ) { userResponse ->
        when (userResponse) {
            PermissionResultUserResponse.NO_RESPONSE -> locationTracker.enable()
            PermissionResultUserResponse.RATIONALE_DIALOG_POSITIVE -> {
            }
            PermissionResultUserResponse.RATIONALE_DIALOG_NEGATIVE,
            PermissionResultUserResponse.RATIONALE_DIALOG_CANCELED -> {
                locationTracker.config
                    .onPermissionRationaleCanceledListener(locationTracker)
            }
            PermissionResultUserResponse.DO_NOT_ASK_AGAIN_DIALOG_POSITIVE -> {
                locationTracker.isOpeningLocationPermissionSetting = true
            }
            PermissionResultUserResponse.DO_NOT_ASK_AGAIN_DIALOG_NEGATIVE,
            PermissionResultUserResponse.DO_NOT_ASK_AGAIN_DIALOG_CANCELED -> {
                locationTracker.config
                    .onOpenPermissionSettingCanceledListener(locationTracker)
            }
        }
    }
}

fun <T> T.onLocationServiceRepairResult(
    requestCode: Int,
    resultCode: Int,
    locationTracker: SimpleLocTrackerActivity
) where T : AppCompatActivity, T : SimpleLocClient {
    if (locationTracker.config.requestCode != requestCode) return

    if (resultCode == Activity.RESULT_OK) {
        locationTracker.enable()
    } else {
        locationTracker.onError(LocationServiceRepairError)
    }
}

fun <T> T.onLocationServiceRepairResult(
    requestCode: Int,
    resultCode: Int,
    locationTracker: SimpleLocTrackerFragment
) where T : Fragment, T : SimpleLocClient {
    if (locationTracker.config.requestCode != requestCode) return

    if (resultCode == Activity.RESULT_OK) {
        locationTracker.enable()
    } else {
        locationTracker.onError(LocationServiceRepairError)
    }
}
