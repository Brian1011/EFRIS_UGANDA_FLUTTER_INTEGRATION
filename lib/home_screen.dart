import 'package:efris_flutter/fiscal_invoice.dart';
import 'package:flutter/material.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('EFRIS Demo App'),
      ),
      body: Center(
        child: ElevatedButton(
          onPressed: () async {
            await FiscalInvoice.getInvoiceNumber('A2DSDAD123456568').then((value) {
              print(value);
            });
          },
          child: const Text('Get invoice'),
        ),
      ),
    );
  }
}
