@file:Suppress("MatchingDeclarationName")

package io.github.achmadhafid.simpleloc

internal const val MAX_ADDRESS = 99
internal const val SIMPLE_LOC_REQUEST_CODE = 6578
internal const val CHECK_LOCATION_SETTING_RESULT_DELAY = 250L

internal object LocationServiceRepairError : Exception()

// TODO 06/12/2020 handle permission request scheme while in-use
// 1. give client option whether asking foreground only or all the time
// 2. when request all the time, add callback when user only select while in-user and add helper flow to handle it
// TODO 06/12/2020 support offline location tracking
// TODO 06/12/2020 update documentation
// TODO 06/12/2020 update sample app

// TODO 06/12/2020 when targeting API 30:
// 1. Create separate location permission request for FOREGROUND & BACKGROUND location access
//    https://developer.android.com/training/location/permissions#request-location-access-runtime
// 2. Create education dialog before asking BACKGROUND location access
//    https://developer.android.com/training/location/permissions#request-background-location
