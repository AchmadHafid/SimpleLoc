@file:Suppress("TooManyFunctions", "unused")

package io.github.achmadhafid.simpleloc

import android.app.Activity
import android.app.Service
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest

internal fun Service.getLocationClient(): FusedLocationProviderClient =
    LocationServices.getFusedLocationProviderClient(this)

internal fun Service.getSettingsClient() =
    LocationServices.getSettingsClient(this)

internal fun Service.checkLocationSettings(request: LocationSettingsRequest) =
    getSettingsClient().checkLocationSettings(request)

internal fun Activity.getLocationClient(): FusedLocationProviderClient =
    LocationServices.getFusedLocationProviderClient(this)

internal fun Activity.getSettingsClient() =
    LocationServices.getSettingsClient(this)

internal fun Activity.checkLocationSettings(request: LocationSettingsRequest) =
    getSettingsClient().checkLocationSettings(request)

internal fun Fragment.getLocationClient(): FusedLocationProviderClient =
    LocationServices.getFusedLocationProviderClient(requireContext())

internal fun Fragment.getSettingsClient() =
    LocationServices.getSettingsClient(requireContext())

internal fun Fragment.checkLocationSettings(request: LocationSettingsRequest) =
    getSettingsClient().checkLocationSettings(request)
