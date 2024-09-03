# EFRIS FLUTTER INTEGRATION GUIDE
This document provides a guide on how to integrate Efris Android SDK into a Flutter project.
By the end of this guide, you will be able to:
- Integrate EFRIS Android SDK into a Flutter project
- Use EFRIS Android SDK to generate invoice number, verification code and QR code.
- Handle the response from EFRIS Android SDK

## Prerequisites
- Flutter SDK
- Android Studio
- EFRIS license key

## Step 1: Create Flutter Project
Create a new Flutter project using your IDE or the command line.

## Step 2: Add EFRIS SDK to your Flutter project
### Open android folder in Android Studio
Open the android folder of your Flutter project in Android Studio.
usually located at `your_project/android`

On android studio go to `File -> Open` and select the android folder of your Flutter project.
Open in a new window.

Give it some time to sync the project.
You can check the progress at the bottom of the IDE.

Once the project is synced, you should have the android project open in Android Studio.
Change the view of the project to `Project` view.
Similar to the image below.

![Android Studio]( )

### Add EFRIS SDK to your Flutter project
On the android project, navigate to the `app\src\main` folder.
Add a new folder called `jniLibs` to the app folder.

Create a new folder called `armeabi-v7a` inside the `jniLibs` folder.
Extract the contents of the EFRIS SDK zip file (32 bit) and copy the `libFiscalInvoice.so` file to the `armeabi-v7a` folder.

Create a new folder called `arm64-v8a` inside the `jniLibs` folder.
Extract the contents of the EFRIS SDK zip file (64 bit) and copy the `libFiscalInvoice.so` file to the `arm64-v8a` folder.

Note: Make sure to add the `libFiscalInvoice.so` file to the correct folder based on the architecture of the device you are targeting.

Your project structure should look like this:

```
your_project
├── android
│   ├── app
│   │   ├── src
│   │   │   ├── main
│   │   │   │   ├── jniLibs
│   │   │   │   │   ├── armeabi-v7a
│   │   │   │   │   │   ├── libFiscalInvoice.so
│   │   │   │   │   ├── arm64-v8a
│   │   │   │   │   │   ├── libFiscalInvoice.so
```

A screenshot of the project structure is shown below:

![Project Structure]( )

## Step 3: Kotlin Library Configuration
Under the `app\src\main\kotlin\` folder, create a new package called `com.aisino.taxcode` and add a new Kotlin file called `SKM.kt`.

copy the following code into the `SKM.kt` file:

```
package com.aisino.taxcode
import android.util.Log

object skm {
    //external fun GetInvoiceNumber(deviceNo: String): String
    @JvmStatic
    external fun GetInvoiceNumber(deviceNo: String): String
    external fun GetVerificationCode(invoiceNo: String): String
    external fun GetInvoiceQRCode(
        invoiceType: String,
        date: String,
        invoiceNo: String,
        totalCash: String,
        totalTax: String,
        mac: String,
        tin: String,
        customerTin: String,
        taxpayerName: String,
        customerName: String,
        productName: String
    ): String

    init {
        try {
            System.loadLibrary("FiscalInvoice")
            Log.d("skm", "FiscalInvoice library loaded successfully")
        } catch (e: UnsatisfiedLinkError) {
            Log.e("skm", "Failed to load FiscalInvoice library", e)
        }
    }
}
```

Sync and Build the project to ensure that there are no errors.
This will take some time to complete if it is the first time building the project.

## Step 4: Method Channel Configuration

### Create a new Kotlin file for the plugin
On the the `android\app\src\main\kotlin\com\example\your_project` folder, create a new Kotlin file called `FiscalInvoicePlugin.kt` and add the following code:

```
package com.example.your_project

import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import com.aisino.taxcode.skm
import android.util.Log

class FiscalInvoicePlugin: FlutterPlugin, MethodCallHandler {
    private lateinit var channel : MethodChannel

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "fiscal_invoice")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        try {
            when (call.method) {
                "getInvoiceNumber" -> {
                    val deviceNo = call.argument<String>("deviceNo")
                    result.success(skm.GetInvoiceNumber(deviceNo!!))
                }
                "getVerificationCode" -> {
                    val invoiceNo = call.argument<String>("invoiceNo")
                    result.success(skm.GetVerificationCode(invoiceNo!!))
                }
                "getInvoiceQRCode" -> {
                    val qrCode = skm.GetInvoiceQRCode(
                            call.argument<Any>("invoiceType")?.toString() ?: "",
                            call.argument<Any>("date")?.toString() ?: "",
                            call.argument<Any>("invoiceNo")?.toString() ?: "",
                            call.argument<Any>("totalCash")?.toString() ?: "",
                            call.argument<Any>("totalTax")?.toString() ?: "",
                            call.argument<Any>("mac")?.toString() ?: "",
                            call.argument<Any>("tin")?.toString() ?: "",
                            call.argument<Any>("customerTin")?.toString() ?: "",
                            call.argument<Any>("taxpayerName")?.toString() ?: "",
                            call.argument<Any>("customerName")?.toString() ?: "",
                            call.argument<Any>("productName")?.toString() ?: ""
                    )
                    result.success(qrCode)
                }
                else -> {
                    result.notImplemented()
                }
            }
        } catch (e: UnsatisfiedLinkError) {
            result.error("UNSATISFIED_LINK", "Native method not found", e.message)
        } catch (e: Exception) {
            result.error("NATIVE_CALL_FAILED", "Error calling native method", e.message)
        }

    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }
}

```

NOTE: please replace `your_project` with the name of your Flutter project.

### Modify MainActivity.kt file
On `MainActivity.kt` file, modify the code to add the FiscalInvoicePlugin to the FlutterEngine.:

```
package com.example.your_project

import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine

class MainActivity: FlutterActivity() {

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        flutterEngine.plugins.add(FiscalInvoicePlugin())
    }
}
```

NOTE: please replace `your_project` with the name of your Flutter project.


Sync and Build the project to ensure that there are no errors.
This will take some time to complete if it is the first time building the project.

At this point, the Android side of the integration is complete and you can now proceed to the Flutter side.
Your android project structure should look like as the image below:

![Android Project Structure]( )

### Create a new Dart file for the plugin
On lib folder of your Flutter project, create a new file called `fiscal_invoice.dart` and add the following code:

```
import 'package:flutter/services.dart';

class FiscalInvoice {
  static const MethodChannel _channel = MethodChannel('fiscal_invoice');

  static Future<String> getInvoiceNumber(String deviceNo) async {
    final String result =
        await _channel.invokeMethod('getInvoiceNumber', {'deviceNo': deviceNo});
    return result;
  }

  static Future<String> getVerificationCode(String invoiceNo) async {
    final String result = await _channel
        .invokeMethod('getVerificationCode', {'invoiceNo': invoiceNo});
    return result;
  }

  static Future<String> getInvoiceQRCode(
      String invoiceType,
      String date,
      String invoiceNo,
      String totalCash,
      String totalTax,
      String mac,
      String tin,
      String customerTin,
      String taxpayerName,
      String customerName,
      String productName) async {
    final String result = await _channel.invokeMethod('getInvoiceQRCode', {
      'invoiceType': invoiceType,
      'date': date,
      'invoiceNo': invoiceNo,
      'totalCash': totalCash,
      'totalTax': totalTax,
      'mac': mac,
      'tin': tin,
      'customerTin': customerTin,
      'taxpayerName': taxpayerName,
      'customerName': customerName,
      'productName': productName,
    });
    return result;
  }
}
```

## Step 5: Add EFRIS License Key
Create `assets` folder in the root of your Flutter project.
Add a new folder called `license` inside the assets folder.

Add the license file as `efris.license` inside the `license` folder.

Your project structure should look like this:

```
your_project
├── android
│   ├── app
│   │   ├── src
│   │   │   ├── main
│   │   │   │   ├── jniLibs
│   │   │   │   │   ├── armeabi-v7a
│   │   │   │   │   │   ├── libFiscalInvoice.so
│   │   │   │   │   ├── arm64-v8a
│   │   │   │   │   │   ├── libFiscalInvoice.so
├── lib
│   ├── fiscal_invoice.dart
├── assets
│   ├── license
│   │   ├── efris.license
```

NOTE: modify your pubspec.yaml file to include the assets folder:

```
flutter:
  assets:
    - assets/license/efris.license
```

or 

```
flutter:
  assets:
    - assets/
```
