@file:Suppress("unused")

package io.github.achmadhafid.simpleloc

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

suspend fun Location.getAddresses(context: Context): List<Address> =
    withContext(Dispatchers.IO) {
        @Suppress("BlockingMethodInNonBlockingContext")
        Geocoder(context, Locale.getDefault())
            .getFromLocation(latitude, longitude, MAX_ADDRESS)
    }

fun Location.openInGMaps(context: Context) {
    val uri = Uri.parse("geo:$latitude,$longitude")
    val mapIntent = Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage("com.google.android.apps.maps")
    }
    if (mapIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(mapIntent)
    }
}
