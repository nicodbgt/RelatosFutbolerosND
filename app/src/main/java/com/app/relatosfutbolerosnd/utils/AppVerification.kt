package com.app.relatosfutbolerosnd.utils

//import android.content.Context
//import android.content.pm.PackageManager
//import android.util.Log
//
//
//object AppVerification {
//
//    private const val TAG = "AppVerification"
//
//    fun verifyIntegration(context: Context): VerificationResult {
//        val issues = mutableListOf<String>()
//        val warnings = mutableListOf<String>()
//
//        Log.d(TAG, "Iniciando verificación de integración...")
//
//        // 1. Verificar dependencias en tiempo de compilación
//        if (!verifyDependencies()) {
//            issues.add("Faltan dependencias necesarias en build.gradle")
//        }
//
//        // 2. Verificar permisos en manifest
//        val manifestIssues = verifyManifestPermissions(context)
//        issues.addAll(manifestIssues)
//
//        // 3. Verificar servicios declarados
//        val serviceIssues = verifyServices(context)
//        issues.addAll(serviceIssues)
//
//        // 4. Verificar configuración de Hilt
//        if (!verifyHiltConfiguration()) {
//            warnings.add("Posibles problemas con configuración Hilt")
//        }
//
//        // 5. Verificar recursos
//        val resourceIssues = verifyResources(context)
//        issues.addAll(resourceIssues)
//
//        Log.d(TAG, "Verificación completada. Issues: ${issues.size}, Warnings: ${warnings.size}")
//
//        return VerificationResult(
//            isSuccessful = issues.isEmpty(),
//            issues = issues,
//            warnings = warnings
//        )
//    }
//
//    private fun verifyDependencies(): Boolean {
//        // Esta verificación se hace principalmente en tiempo de compilación
//        // Podemos verificar reflectivamente algunas clases clave
//        return try {
//            Class.forName("com.haishinkit.rtmp.RtmpConnection")
//            Class.forName("dagger.hilt.android.HiltAndroidApp")
//            Class.forName("androidx.compose.material3.MaterialTheme")
//            true
//        } catch (e: ClassNotFoundException) {
//            Log.e(TAG, "Dependencia faltante: ${e.message}")
//            false
//        }
//    }
//
//    private fun verifyManifestPermissions(context: Context): List<String> {
//        val issues = mutableListOf<String>()
//        val requiredPermissions = listOf(
//            "android.permission.CAMERA",
//            "android.permission.RECORD_AUDIO",
//            "android.permission.FOREGROUND_SERVICE",
//            "android.permission.INTERNET",
//            "android.permission.SYSTEM_ALERT_WINDOW" // Para overlay
//        )
//
//        requiredPermissions.forEach { permission ->
//            try {
//                val permissionInfo = context.packageManager.getPermissionInfo(permission, 0)
//                if (permission in dangerousPermissions) {
//                    // Verificar si el permiso peligroso está declarado
//                    val hasPermission = context.packageManager.checkPermission(
//                        permission,
//                        context.packageName
//                    ) == PackageManager.PERMISSION_GRANTED
//
//                    if (!hasPermission) {
//                        warnings.add("Permiso $permission no otorgado (se solicitará en runtime)")
//                    }
//                }
//            } catch (e: PackageManager.NameNotFoundException) {
//                issues.add("Permiso faltante en manifest: $permission")
//            }
//        }
//
//        return issues
//    }
//
//    private fun verifyServices(context: Context): List<String> {
//        val issues = mutableListOf<String>()
//        val requiredServices = listOf(
//            "com.relatos.futboleros.service.RtmpStreamingService",
//            "com.relatos.futboleros.service.MatchOverlayService"
//        )
//
//        requiredServices.forEach { serviceClass ->
//            try {
//                Class.forName(serviceClass)
//            } catch (e: ClassNotFoundException) {
//                issues.add("Servicio no encontrado: $serviceClass")
//            }
//        }
//
//        return issues
//    }
//
//    private fun verifyHiltConfiguration(): Boolean {
//        return try {
//            Class.forName("com.relatos.futboleros.di.AppModule")
//            Class.forName("com.relatos.futboleros.RelatosFutbolerosApplication")
//            true
//        } catch (e: ClassNotFoundException) {
//            Log.e(TAG, "Problema con configuración Hilt: ${e.message}")
//            false
//        }
//    }
//
//    private fun verifyResources(context: Context): List<String> {
//        val issues = mutableListOf<String>()
//
//        // Verificar recursos críticos
//        val requiredResources = listOf(
//            "ic_sports_soccer" to "drawable",
//            "ic_live_tv" to "drawable",
//            "app_name" to "string",
//            "theme" to "style"
//        )
//
//        requiredResources.forEach { (resourceName, resourceType) ->
//            val resourceId = context.resources.getIdentifier(resourceName, resourceType, context.packageName)
//            if (resourceId == 0) {
//                issues.add("Recurso faltante: $resourceType/$resourceName")
//            }
//        }
//
//        return issues
//    }
//
//    fun generateReport(result: VerificationResult): String {
//        return buildString {
//            append("=== INFORME DE VERIFICACIÓN ===\n")
//            append("Estado: ${if (result.isSuccessful) "✅ ÉXITO" else "❌ FALLIDO"}\n")
//            append("Problemas encontrados: ${result.issues.size}\n")
//            append("Advertencias: ${result.warnings.size}\n\n")
//
//            if (result.issues.isNotEmpty()) {
//                append("PROBLEMAS CRÍTICOS:\n")
//                result.issues.forEach { issue ->
//                    append("❌ $issue\n")
//                }
//                append("\n")
//            }
//
//            if (result.warnings.isNotEmpty()) {
//                append("ADVERTENCIAS:\n")
//                result.warnings.forEach { warning ->
//                    append("⚠️ $warning\n")
//                }
//            }
//
//            if (result.isSuccessful) {
//                append("\n🎉 ¡La aplicación está lista para usar!\n")
//                append("Funcionalidades verificadas:\n")
//                append("✅ Streaming RTMP\n")
//                append("✅ Overlay de marcador\n")
//                append("✅ Control de partido\n")
//                append("✅ Arquitectura MVVM\n")
//                append("✅ Inyección Hilt\n")
//            }
//        }
//    }
//
//    data class VerificationResult(
//        val isSuccessful: Boolean,
//        val issues: List<String>,
//        val warnings: List<String>
//    )
//}

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log

object AppVerification {

    private const val TAG = "AppVerification"

    // Lista de permisos peligrosos que requieren solicitud en runtime
    private val dangerousPermissions = listOf(
        "android.permission.CAMERA",
        "android.permission.RECORD_AUDIO",
        "android.permission.SYSTEM_ALERT_WINDOW"
    )

    fun verifyIntegration(context: Context): VerificationResult {
        val issues = mutableListOf<String>()
        val warnings = mutableListOf<String>()

        Log.d(TAG, "Iniciando verificación de integración...")

        // 1. Verificar dependencias
        if (!verifyDependencies()) {
            issues.add("Faltan dependencias necesarias en build.gradle")
        }

        // 2. Verificar permisos en manifest
        val (manifestIssues, manifestWarnings) = verifyManifestPermissions(context)
        issues.addAll(manifestIssues)
        warnings.addAll(manifestWarnings)

        // 3. Verificar servicios
        val serviceIssues = verifyServices(context)
        issues.addAll(serviceIssues)

        // 4. Verificar Hilt
        if (!verifyHiltConfiguration()) {
            warnings.add("Posibles problemas con configuración Hilt")
        }

        // 5. Verificar recursos
        val resourceIssues = verifyResources(context)
        issues.addAll(resourceIssues)

        Log.d(TAG, "Verificación completada. Issues: ${issues.size}, Warnings: ${warnings.size}")

        return VerificationResult(
            isSuccessful = issues.isEmpty(),
            issues = issues,
            warnings = warnings
        )
    }

    private fun verifyManifestPermissions(context: Context): Pair<List<String>, List<String>> {
        val issues = mutableListOf<String>()
        val warnings = mutableListOf<String>()

        val requiredPermissions = listOf(
            "android.permission.CAMERA",
            "android.permission.RECORD_AUDIO",
            "android.permission.FOREGROUND_SERVICE",
            "android.permission.INTERNET",
            "android.permission.SYSTEM_ALERT_WINDOW"
        )

        requiredPermissions.forEach { permission ->
            try {
                // Verificar si el permiso está declarado en el manifest
                context.packageManager.getPermissionInfo(permission, 0)

                // Verificar si es un permiso peligroso
                if (permission in dangerousPermissions) { // ✅ Ahora dangerousPermissions está disponible
                    val hasPermission = context.packageManager.checkPermission(
                        permission,
                        context.packageName
                    ) == PackageManager.PERMISSION_GRANTED

                    if (!hasPermission) {
                        warnings.add("Permiso $permission no otorgado (se solicitará en runtime)")
                    }
                }
            } catch (e: PackageManager.NameNotFoundException) {
                issues.add("Permiso faltante en manifest: $permission")
            } catch (e: Exception) {
                Log.e(TAG, "Error verificando permiso $permission: ${e.message}")
                warnings.add("Error verificando permiso: $permission")
            }
        }

        return Pair(issues, warnings)
    }

    private fun verifyDependencies(): Boolean {
        return try {
            Class.forName("com.haishinkit.rtmp.RtmpConnection")
            Class.forName("dagger.hilt.android.HiltAndroidApp")
            Class.forName("androidx.compose.material3.MaterialTheme")
            true
        } catch (e: ClassNotFoundException) {
            Log.e(TAG, "Dependencia faltante: ${e.message}")
            false
        }
    }

    private fun verifyServices(context: Context): List<String> {
        val issues = mutableListOf<String>()
        val requiredServices = listOf(
            "com.relatos.futboleros.service.RtmpStreamingService",
            "com.relatos.futboleros.service.MatchOverlayService"
        )

        requiredServices.forEach { serviceClass ->
            try {
                Class.forName(serviceClass)
            } catch (e: ClassNotFoundException) {
                issues.add("Servicio no encontrado: $serviceClass")
            }
        }

        return issues
    }

    private fun verifyHiltConfiguration(): Boolean {
        return try {
            Class.forName("com.relatos.futboleros.di.AppModule")
            Class.forName("com.relatos.futboleros.RelatosFutbolerosApplication")
            true
        } catch (e: ClassNotFoundException) {
            Log.e(TAG, "Problema con configuración Hilt: ${e.message}")
            false
        }
    }

    private fun verifyResources(context: Context): List<String> {
        val issues = mutableListOf<String>()

        val requiredResources = listOf(
            "ic_sports_soccer" to "drawable",
            "ic_live_tv" to "drawable",
            "app_name" to "string",
            "theme" to "style"
        )

        requiredResources.forEach { (resourceName, resourceType) ->
            val resourceId = context.resources.getIdentifier(resourceName, resourceType, context.packageName)
            if (resourceId == 0) {
                issues.add("Recurso faltante: $resourceType/$resourceName")
            }
        }

        return issues
    }

    fun generateReport(result: VerificationResult): String {
        return buildString {
            append("=== INFORME DE VERIFICACIÓN ===\n")
            append("Estado: ${if (result.isSuccessful) "✅ ÉXITO" else "❌ FALLIDO"}\n")
            append("Problemas encontrados: ${result.issues.size}\n")
            append("Advertencias: ${result.warnings.size}\n\n")

            if (result.issues.isNotEmpty()) {
                append("PROBLEMAS CRÍTICOS:\n")
                result.issues.forEach { issue ->
                    append("❌ $issue\n")
                }
                append("\n")
            }

            if (result.warnings.isNotEmpty()) {
                append("ADVERTENCIAS:\n")
                result.warnings.forEach { warning ->
                    append("⚠️ $warning\n")
                }
            }

            if (result.isSuccessful) {
                append("\n🎉 ¡La aplicación está lista para usar!\n")
                append("Funcionalidades verificadas:\n")
                append("✅ Streaming RTMP\n")
                append("✅ Overlay de marcador\n")
                append("✅ Control de partido\n")
                append("✅ Arquitectura MVVM\n")
                append("✅ Inyección Hilt\n")
            }
        }
    }

    data class VerificationResult(
        val isSuccessful: Boolean,
        val issues: List<String>,
        val warnings: List<String>
    )
}