# SimpleLoc

[![Release](https://jitpack.io/v/AchmadHafid/SimpleLoc.svg)](https://jitpack.io/#AchmadHafid/SimpleLoc)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)

**Assalamu'alaikum brothers and sisters, peace be upon you!**

This library is intended to help you to use and manage location tracking inside your `Activity` / `Fragment` / `LifecycleService` easily.


![image](https://drive.google.com/uc?export=download&id=1rXcO_5b3zFFF-ztLOp82Yzc4kAlMU4sg)
<br />
[**Download Demo App Here**](https://github.com/AchmadHafid/SimpleLoc/releases/download/v1.0.3/SimpleLoc_v1.0.3_Demo.apk)


Key Features
--------
* __Kotlin__ with __DSL__ for coding pleasure!
* __Lifecycle aware__ location tracking. Just defined it in your `Activity` / `Fragment` / `LifecycleService` and you are good to go.
* Manage __permission request state & UI__ out of the box! More focus on your business use case, less boilerplate code.
* Automatically check for location setting. Be confident that GPS always enabled if you specicfy it in the config.
* Full control on every state changes when you need it.
* __Bonus 1__: It can resolve addresses automatically instead just providing location.
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
  implementation "com.github.AchmadHafid:SimpleLoc:1.0.6"
  ...
}
```


Quick Usage
-----------

```kotlin

// 1. Enable API by making class extend SimpleLocClient interface
class MainActivity : AppCompatActivity(R.layout.activity_main), SimpleLocClient {
//class MainFragment : Fragment(R.layout.fragmnet_main), SimpleLocClient {
//class MainService : LifecycleService() SimpleLocClient {

    // 2. Defined the location tracker using extension function
    private val locationTracker = simpleLocTracker {
      // Set configurations and callback handlers here, e.g:
      isAutoStart = true // run automatically
    }

    // 3. Optional, start it manually if you want
    private fun startLocationTracking() {
      locationTracker.enable()
    }

}
```

__Note:__ If you use it inside a `LifecycleService`, please see the sample app code on how to handle permission request and other UI related stuff.


Live Template
-----------
Add live template for this library to your Android Studio to get started quickly. <br />
[Download Live Template Here](https://github.com/AchmadHafid/SimpleLoc/releases/download/v1.0.3/SimpleLoc_v1.0.3_LiveTemplate.zip)
<br />
Import via: `File` > `Import Settings...` <br />

**Full API with detail documentation**
![image](https://drive.google.com/uc?export=download&id=1AZ-LsCzRMdZgOwzmbT1MhpKaYyvG9rdp)
<br />


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

