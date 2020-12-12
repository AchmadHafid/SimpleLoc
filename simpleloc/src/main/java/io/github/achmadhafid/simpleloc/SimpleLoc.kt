@file:Suppress("MatchingDeclarationName")

package io.github.achmadhafid.simpleloc

internal const val MAX_ADDRESS = 99
internal const val SIMPLE_LOC_REQUEST_CODE = 6578
internal const val CHECK_LOCATION_SETTING_RESULT_DELAY = 250L

internal object LocationServiceRepairError : Exception()

// TODO 06/12/2020 when targeting API 30:
// 2. Create education dialog before asking BACKGROUND location access
//    https://developer.android.com/training/location/permissions#request-background-location
