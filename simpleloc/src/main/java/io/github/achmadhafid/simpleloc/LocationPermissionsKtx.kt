@file:Suppress("MatchingDeclarationName", "TooManyFunctions", "unused")

package io.github.achmadhafid.simpleloc

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import io.github.achmadhafid.lottie_dialog.core.LottieConfirmationDialog
import io.github.achmadhafid.lottie_dialog.core.lottieConfirmationDialog
import io.github.achmadhafid.lottie_dialog.core.onCancel
import io.github.achmadhafid.lottie_dialog.core.withNegativeButton
import io.github.achmadhafid.lottie_dialog.core.withPositiveButton
import io.github.achmadhafid.lottie_dialog.model.onClick
import io.github.achmadhafid.zpack.extension.arePermissionsGranted
import io.github.achmadhafid.zpack.extension.belowMarshmallow
import io.github.achmadhafid.zpack.extension.openAppDetailSettings
import io.github.achmadhafid.zpack.extension.requestPermissionCompat
import io.github.achmadhafid.zpack.extension.shouldShowRequestPermissionRationales

private val LocationPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
    arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )
} else {
    arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
}

//region Permission Checker

val Context.hasLocationPermissions
    get() = arePermissionsGranted(LocationPermissions)

val Fragment.hasLocationPermissions
    get() = requireContext().arePermissionsGranted(LocationPermissions)

//endregion
//region Permission Request

fun AppCompatActivity.requestLocationPermission(requestCode: Int = SIMPLE_LOC_REQUEST_CODE) =
    requestPermissionCompat(LocationPermissions, requestCode)

@Suppress("DEPRECATION")
fun Fragment.requestLocationPermission(requestCode: Int = SIMPLE_LOC_REQUEST_CODE) =
    requestPermissions(LocationPermissions, requestCode)

fun <T> AppCompatActivity.withLocationPermission(
    requestCode: Int = SIMPLE_LOC_REQUEST_CODE,
    defaultReturnValue: T? = null,
    onGranted: () -> T?
) = when {
    belowMarshmallow() || hasLocationPermissions -> onGranted()
    else -> defaultReturnValue.also { requestLocationPermission(requestCode) }
}

fun AppCompatActivity.withLocationPermission(
    requestCode: Int = SIMPLE_LOC_REQUEST_CODE,
    onGranted: () -> Unit
) {
    withLocationPermission(requestCode, Unit, onGranted)
}

fun <T> Fragment.withLocationPermission(
    requestCode: Int = SIMPLE_LOC_REQUEST_CODE,
    defaultReturnValue: T? = null,
    onGranted: () -> T?
) = when {
    belowMarshmallow() || hasLocationPermissions -> onGranted()
    else -> defaultReturnValue.also { requestLocationPermission(requestCode) }
}

fun Fragment.withLocationPermission(
    requestCode: Int = SIMPLE_LOC_REQUEST_CODE,
    onGranted: () -> Unit
) {
    withLocationPermission(requestCode, Unit, onGranted)
}

//endregion
//region Permission Result

private const val OPEN_APP_SETTING_DELAY = 250L

enum class PermissionResultUserResponse {
    NO_RESPONSE, // permissions already granted
    RATIONALE_DIALOG_POSITIVE,
    RATIONALE_DIALOG_NEGATIVE,
    RATIONALE_DIALOG_CANCELED,
    DO_NOT_ASK_AGAIN_DIALOG_POSITIVE,
    DO_NOT_ASK_AGAIN_DIALOG_NEGATIVE,
    DO_NOT_ASK_AGAIN_DIALOG_CANCELED,
}

@Suppress("LongParameterList")
fun AppCompatActivity.onLocationPermissionResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray,
    rationaleDialogBuilder: LottieConfirmationDialog.() -> Unit,
    doNotAskAgainDialogBuilder: LottieConfirmationDialog.() -> Unit,
    callback: (PermissionResultUserResponse) -> Unit
) {
    when {
        grantResults.arePermissionsGranted -> callback(PermissionResultUserResponse.NO_RESPONSE)
        shouldShowRequestPermissionRationales(permissions) -> {
            lottieConfirmationDialog(0, rationaleDialogBuilder) {
                withPositiveButton {
                    onClick {
                        callback(PermissionResultUserResponse.RATIONALE_DIALOG_POSITIVE)
                        requestLocationPermission(requestCode)
                    }
                }
                withNegativeButton {
                    onClick {
                        callback(PermissionResultUserResponse.RATIONALE_DIALOG_NEGATIVE)
                    }
                }
                onCancel {
                    callback(PermissionResultUserResponse.RATIONALE_DIALOG_CANCELED)
                }
            }
        }
        else -> lottieConfirmationDialog(0, doNotAskAgainDialogBuilder) {
            withPositiveButton {
                actionDelay = OPEN_APP_SETTING_DELAY
                onClick {
                    callback(PermissionResultUserResponse.DO_NOT_ASK_AGAIN_DIALOG_POSITIVE)
                    openAppDetailSettings()
                }
            }
            withNegativeButton {
                onClick {
                    callback(PermissionResultUserResponse.DO_NOT_ASK_AGAIN_DIALOG_NEGATIVE)
                }
            }
            onCancel {
                callback(PermissionResultUserResponse.DO_NOT_ASK_AGAIN_DIALOG_CANCELED)
            }
        }
    }
}

@Suppress("LongParameterList")
fun Fragment.onLocationPermissionResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray,
    rationaleDialogBuilder: LottieConfirmationDialog.() -> Unit,
    doNotAskAgainDialogBuilder: LottieConfirmationDialog.() -> Unit,
    callback: (PermissionResultUserResponse) -> Unit
) {
    when {
        grantResults.arePermissionsGranted -> callback(PermissionResultUserResponse.NO_RESPONSE)
        shouldShowRequestPermissionRationales(permissions) -> {
            lottieConfirmationDialog(0, rationaleDialogBuilder) {
                withPositiveButton {
                    onClick {
                        callback(PermissionResultUserResponse.RATIONALE_DIALOG_POSITIVE)
                        requestLocationPermission(requestCode)
                    }
                }
                withNegativeButton {
                    onClick {
                        callback(PermissionResultUserResponse.RATIONALE_DIALOG_NEGATIVE)
                    }
                }
                onCancel {
                    callback(PermissionResultUserResponse.RATIONALE_DIALOG_CANCELED)
                }
            }
        }
        else -> lottieConfirmationDialog(0, doNotAskAgainDialogBuilder) {
            withPositiveButton {
                actionDelay = OPEN_APP_SETTING_DELAY
                onClick {
                    callback(PermissionResultUserResponse.DO_NOT_ASK_AGAIN_DIALOG_POSITIVE)
                    requireContext().openAppDetailSettings()
                }
            }
            withNegativeButton {
                onClick {
                    callback(PermissionResultUserResponse.DO_NOT_ASK_AGAIN_DIALOG_NEGATIVE)
                }
            }
            onCancel {
                callback(PermissionResultUserResponse.DO_NOT_ASK_AGAIN_DIALOG_CANCELED)
            }
        }
    }
}

//endregion
