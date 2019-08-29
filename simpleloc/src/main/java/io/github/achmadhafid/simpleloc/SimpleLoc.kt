@file:Suppress("MatchingDeclarationName")

package io.github.achmadhafid.simpleloc

internal const val MAX_ADDRESS = 99
internal const val REQUEST_CODE_PERMISSION = 6676
internal const val REQUEST_CODE_REPAIR = 6677
internal const val CHECK_LOCATION_SETTING_RESULT_DELAY = 250L

internal object LocationServiceRepairError : Exception()
