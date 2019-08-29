package io.github.achmadhafid.sample_app

import io.github.achmadhafid.lottie_dialog.lottieConfirmationDialogBuilder
import io.github.achmadhafid.lottie_dialog.model.LottieDialogType
import io.github.achmadhafid.lottie_dialog.withAnimation
import io.github.achmadhafid.lottie_dialog.withContent
import io.github.achmadhafid.lottie_dialog.withNegativeButton
import io.github.achmadhafid.lottie_dialog.withPositiveButton
import io.github.achmadhafid.lottie_dialog.withTitle

object Dialog {

    val rationale = lottieConfirmationDialogBuilder {
        type = LottieDialogType.BOTTOM_SHEET
        withAnimation {
            fileRes    = R.raw.rationale
            paddingRes = R.dimen.lottie_dialog_animation_padding
        }
        withTitle("Location Required")
        withContent("Please enable location permission")
        withPositiveButton {
            textRes = android.R.string.ok
            iconRes = R.drawable.ic_check_black_18dp_svg
        }
        withNegativeButton {
            textRes = android.R.string.cancel
            iconRes = R.drawable.ic_close_black_18dp_svg
        }
    }

    val doNotAskAgain = lottieConfirmationDialogBuilder {
        type = LottieDialogType.BOTTOM_SHEET
        withAnimation(R.raw.do_not_ask_again)
        withTitle("Location Required")
        withContent("Please enable location permission on the next screen")
        withPositiveButton {
            textRes = android.R.string.ok
            iconRes = R.drawable.ic_check_black_18dp_svg
        }
        withNegativeButton {
            textRes = android.R.string.cancel
            iconRes = R.drawable.ic_close_black_18dp_svg
        }
    }

}
