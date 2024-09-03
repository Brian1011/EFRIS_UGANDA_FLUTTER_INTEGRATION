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