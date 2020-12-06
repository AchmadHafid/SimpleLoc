package io.github.achmadhafid.sample_app

import io.github.achmadhafid.lottie_dialog.core.lottieConfirmationDialogBuilder
import io.github.achmadhafid.lottie_dialog.core.withAnimation
import io.github.achmadhafid.lottie_dialog.core.withContent
import io.github.achmadhafid.lottie_dialog.core.withNegativeButton
import io.github.achmadhafid.lottie_dialog.core.withPositiveButton
import io.github.achmadhafid.lottie_dialog.core.withTitle
import io.github.achmadhafid.lottie_dialog.model.LottieDialogType

object Dialog {

    val rationale = lottieConfirmationDialogBuilder {
        type = LottieDialogType.BOTTOM_SHEET
        withAnimation {
            fileRes = R.raw.rationale
            paddingRes = R.dimen.lottie_dialog_animation_padding
        }
        withTitle(R.string.dialog_location_required_title)
        withContent(R.string.dialog_location_required_content)
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
        withTitle(R.string.dialog_location_required_title)
        withContent(R.string.dialog_location_required_in_device_settings_content)
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
