[![Release](https://jitpack.io/v/AchmadHafid/SimpleLoc.svg)](https://jitpack.io/#AchmadHafid/SimpleLoc)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)

**Assalamu'alaikum brothers and sisters, peace be upon you!**

This library is intended to help you to use and manage location tracking inside your `Activity` / `Fragment` / `LifecycleService` easily.


![image](https://drive.google.com/uc?export=download&id=1rXcO_5b3zFFF-ztLOp82Yzc4kAlMU4sg)
<br />
[**Download Demo App Here**](https://github.com/AchmadHafid/SimpleLoc/releases/download/v1.3.0/SimpleLoc_v1.3.0_Demo.apk)


Key Features
--------
* Support __Android 10 & 11__ Location permission model updates (e.g. background location access)
* Configure location tracker using a simple __Kotlin__ __DSL__
* __Lifecycle aware__ location tracking. Automatically stop & restart the tracker based on lifecycle events.
* Manage __permission request state & UI__ out of the box! Powered by [**LottieDialog**](https://github.com/AchmadHafid/LottieDialog).
* Automatically check for location setting. Be confident that GPS is always enabled if you specify it in the config.
* Full control on every state changes when you need it.
* __Bonus 1__: It can resolve addresses automatically instead just providing LatLng location.
* __Bonus 2__: There is extension function on `Location` to open it directly on Google Maps app.



Compatibility
-------------

* This library is compatible from API 21 (Android 5.0 Lollipop) & AndroidX. <br />
* It use [**Fused Location API**](https://developers.google.com/location-context/fused-location-provider) behind the scene. <br />
* It also make use of [**LottieDailog**](https://github.com/AchmadHafid/LottieDialog) to provide UI related stuff.


Download
--------


Add jitpack repository into your root build.gradle

```groovy
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
    ...
  }
}
```

Add the dependency

```groovy
dependencies {
  ...
  implementation "com.github.AchmadHafid:SimpleLoc:1.3.0"
  ...
}
```


Quick Usage
-----------

<details>
  <summary>1. Extend SimpleLocClient interface </summary>
  <br />
  
  ```kotlin

class MainActivity : AppCompatActivity(R.layout.activity_main), SimpleLocClient
class MainFragment : Fragment(R.layout.fragmnet_main), SimpleLocClient
class MainService : LifecycleService(), SimpleLocClient

```
  
</details>
<details>
  <summary>2. Define location tracker as a class property</summary>
  <br />
  
  ```kotlin
  
  private val locationTracker = simpleLocTracker {
        isAutoStart = false // default, start it whenever you want by calling: locationTracker.enable(isForce = false /*default value, should be work in most cases*/)
        resolveAddress = false // default
        backgroundLocationAccess = false // default
        withRequest {
            // Customize your LocationRequest here
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = TRACKING_INTERVAL
            fastestInterval = TRACKING_FASTEST_INTERVAL
        }
        onRunning { tracker, isRestarted ->
            // This callback is called every time the tracker is started to run
            // check flag 'isRestarted' if you have concern with whether the tracker is running for the first time or
            // actually being restarted due to lifecycle events, for example:
            // LocationTrackerActivity -> Device Home Screen -> back to LocationTrackerActivity
        }
        onLocationFound { tracker, location, _ ->
            // This callback is called every time we received new location fix
            // You can use the extension function 'location.openInGMaps()' to open it in Google Maps app
        }
        onStopped { tracker, state ->
            // This callback is called when location tracking is stopped due to reasons you can inspect below
            when (state) {
                SimpleLocTracker.StopState.PAUSED_BY_LIFECYCLE -> {
                    // This one occurred when the host lifecycle received Lifecycle.Event.ON_STOP event
                }
                SimpleLocTracker.StopState.DESTROYED_BY_LIFECYCLE -> {
                    // This one occurred when the host lifecycle received Lifecycle.Event.ON_DESTROY event
                }
                SimpleLocTracker.StopState.STOPPED_BY_SYSTEM -> {
                    // This one occurred when location detection is disabled by system
                    // Some cause can be when user turned off location service or turned on air plane mode
                }
                SimpleLocTracker.StopState.STOPPED_BY_USER -> {
                    // This one occurred when you are manually called tracker.disable()
                }
            }
        }
        //region Below callbacks are related only when starting location tracker

        onPermissionRationaleCanceled {
            // This callback is called once when user canceled (pressed 'No' button) dialog permission request rationale
        }
        onOpenPermissionSettingCanceled {
            // When user have to go to Application settings screen in order to enable location service,
            // this library will automatically show rationale dialog related to this action.
            // If user choose not to open application settings screen, this callback will be called.
        }
        onLocationSettingsUnavailable { tracker, isAirPlaneModeOn ->
            // This callback is called when user have to enable location service manually.
            // For example, this can be happen when airplane mode is on. You can check 'isAirPlaneModeOn' flag for it.
        }
        onLocationServiceRepairError {
            // This callback is called when location service is unavailable and
            // Google Location Service SDK cannot help us to handle it
            // For example, this can be happen when we requested high accuracy location detection
            // on device with no GPS hardware.
        }
        onUnresolvableError { tracker, exception ->
            // This callback is called when we do not have any clue on what actually happen.
            // Consider it as an 'unknown exception'.
        }

        //endregion
    }
    
```
  
  
</details>
<details>
  <summary>3. Forward onActivityResult</summary>
  <br/>
  
  ```kotlin
  
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        onLocationServiceRepairResult(requestCode, resultCode, locationTracker)
    }
  
```
  
</details>
<details>
  <summary>4. Forward onRequestPermissionsResult</summary>
  <br/>
  
  ```kotlin
  
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onLocationPermissionResult(
            requestCode, permissions, grantResults,
            locationTracker,
            Dialog.rationale, Dialog.doNotAskAgain // use LottieDialog, see next step for the complete example
        )
    }
  
```
  
</details>
<details>
  <summary>5. Provide rationale dialog using LottieDialog</summary>
  <br/>
  
  ```kotlin
  
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
  
```
  
</details>

__Note:__ If you use it inside a `LifecycleService`, please see the sample app code on how to handle permission request and other UI related stuff.



__That's it! May this library ease your Android development task__


License
-------

    Copyright 2019 Achmad Hafid

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

