package com.app.relatosfutbolerosnd.presentation.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun PermissionDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Permiso de Overlay Requerido") },
        text = {
            Text("Para mostrar el marcador sobre otras aplicaciones, necesitas otorgar el permiso de overlay. " +
                    "Se abrirá la configuración del sistema para que habilites este permiso.")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("CONFIGURAR PERMISO")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCELAR")
            }
        }
    )
}