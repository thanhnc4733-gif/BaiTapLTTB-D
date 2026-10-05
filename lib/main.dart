import 'dart:io';

import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';

void main() {
  runApp(const StudentProfileApp());
}

class StudentProfileApp extends StatelessWidget {
  const StudentProfileApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        scaffoldBackgroundColor: const Color(0xFFF8FBFC),
        colorScheme: ColorScheme.fromSeed(
          seedColor: const Color(0xFF63A99D),
        ),
        useMaterial3: true,
      ),
      home: const StudentProfilePage(),
    );
  }
}

class StudentProfilePage extends StatefulWidget {
  const StudentProfilePage({super.key});

  @override
  State<StudentProfilePage> createState() => _StudentProfilePageState();
}

class _StudentProfilePageState extends State<StudentProfilePage> {
  final _nameController = TextEditingController(text: 'Nguyễn Minh Anh');
  final _studentIdController = TextEditingController(text: 'SV0123456');
  final _imagePicker = ImagePicker();

  XFile? _avatar;
  bool _isEditing = false;

  Future<void> _chooseAvatar() async {
    final image = await _imagePicker.pickImage(
      source: ImageSource.gallery,
      imageQuality: 85,
      maxWidth: 800,
    );

    if (image != null) {
      setState(() {
        _avatar = image;
      });
    }
  }

  @override
  void dispose() {
    _nameController.dispose();
    _studentIdController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 20),
          child: Column(
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  _outlineButton(
                    icon: Icons.arrow_back,
                    onPressed: () {},
                  ),
                  _outlineButton(
                    icon: _isEditing ? Icons.check : Icons.edit_outlined,
                    iconColor: const Color(0xFF63A99D),
                    onPressed: () {
                      setState(() {
                        _isEditing = !_isEditing;
                      });
                    },
                  ),
                ],
              ),
              Expanded(
                child: Center(
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      GestureDetector(
                        onTap: _isEditing ? _chooseAvatar : null,
                        child: CircleAvatar(
                          radius: 66,
                          backgroundColor: const Color(0xFFDCEAF5),
                          backgroundImage: _avatar == null
                              ? null
                              : FileImage(File(_avatar!.path)),
                          child: _avatar == null
                              ? const Icon(
                                  Icons.person,
                                  size: 80,
                                  color: Color(0xFF78909C),
                                )
                              : null,
                        ),
                      ),
                      if (_isEditing) ...[
                        const SizedBox(height: 10),
                        TextButton.icon(
                          onPressed: _chooseAvatar,
                          icon: const Icon(Icons.photo_library_outlined),
                          label: const Text('Đổi ảnh đại diện'),
                        ),
                        const SizedBox(height: 14),
                        _textField(
                          controller: _nameController,
                          label: 'Họ và tên',
                        ),
                        const SizedBox(height: 12),
                        _textField(
                          controller: _studentIdController,
                          label: 'Mã số sinh viên',
                        ),
                      ] else ...[
                        const SizedBox(height: 22),
                        Text(
                          _nameController.text,
                          textAlign: TextAlign.center,
                          style: const TextStyle(
                            fontSize: 25,
                            fontWeight: FontWeight.w700,
                            color: Color(0xFF171717),
                          ),
                        ),
                        const SizedBox(height: 5),
                        Text(
                          _studentIdController.text,
                          style: const TextStyle(
                            fontSize: 19,
                            color: Color(0xFF777777),
                          ),
                        ),
                      ],
                    ],
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _outlineButton({
    required IconData icon,
    required VoidCallback onPressed,
    Color iconColor = const Color(0xFF222222),
  }) {
    return SizedBox(
      width: 48,
      height: 48,
      child: OutlinedButton(
        onPressed: onPressed,
        style: OutlinedButton.styleFrom(
          padding: EdgeInsets.zero,
          side: const BorderSide(color: Color(0xFFD8E0E2)),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(11),
          ),
        ),
        child: Icon(icon, color: iconColor, size: 22),
      ),
    );
  }

  Widget _textField({
    required TextEditingController controller,
    required String label,
  }) {
    return SizedBox(
      width: 300,
      child: TextField(
        controller: controller,
        textAlign: TextAlign.center,
        decoration: InputDecoration(
          labelText: label,
          border: const OutlineInputBorder(),
        ),
      ),
    );
  }
}