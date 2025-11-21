package com.app.relatosfutbolerosnd.presentation.screen

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.app.relatosfutbolerosnd.presentation.viewmodel.StreamingViewModel
import com.app.relatosfutbolerosnd.ui.theme.RelatosFutbolerosNDTheme
import com.app.relatosfutbolerosnd.utils.AppVerification
import com.app.relatosfutbolerosnd.utils.PermissionHelper
import com.haishinkit.BuildConfig
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // 1. El ViewModel se declara como una propiedad de la Activity
    private val viewModel: StreamingViewModel by viewModels()

    private val overlayPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // No es necesario verificar el resultado aquí, el ViewModel lo hará
        }

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.values.all { it }
            if (allGranted) {
                // 2. Ahora se puede acceder al viewModel de la clase
                viewModel.startStreaming()
            } else {
                viewModel.setError("Se necesitan permisos de cámara y micrófono.")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BuildConfig.DEBUG) {
            val result = AppVerification.verifyIntegration(this)
            val report = AppVerification.generateReport(result)
            Log.d("AppVerification", report)
            if (!result.isSuccessful) {
                result.issues.forEach { issue -> Log.e("AppVerification", "ISSUE: $issue") }
            }
        }

        setContent {
            RelatosFutbolerosNDTheme {
                MainScreen(
                    viewModel = viewModel, // Pasamos la instancia correcta
                    onRequestOverlayPermission = {
                        openOverlayPermissionSettings()
                    }
                )
            }
        }
    }

    private fun openOverlayPermissionSettings() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
            data = Uri.parse("package:$packageName")
        }
        overlayPermissionLauncher.launch(intent)
    }
}

private fun StreamingViewModel.setError(string: String) {}
