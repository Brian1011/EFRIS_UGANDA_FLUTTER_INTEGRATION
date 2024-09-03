package com.example.efris_flutter

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
        Log.d("FiscalInvoicePlugin", "Method called: ${call.method}")
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