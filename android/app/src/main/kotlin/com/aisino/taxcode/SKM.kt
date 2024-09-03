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