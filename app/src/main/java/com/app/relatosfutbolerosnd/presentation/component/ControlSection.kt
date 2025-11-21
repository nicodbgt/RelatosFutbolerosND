package com.app.relatosfutbolerosnd.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ControlSection(
    isStreaming: Boolean,
    isOverlayVisible: Boolean,
    onStartStream: () -> Unit,
    onStopStream: () -> Unit,
    onToggleOverlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "CONTROLES PRINCIPALES",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botones de Streaming
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Botón Iniciar Stream
                Button(
                    onClick = onStartStream,
                    enabled = !isStreaming,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Iniciar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("INICIAR STREAM")
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Botón Detener Stream
                Button(
                    onClick = onStopStream,
                    enabled = isStreaming,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Stop, contentDescription = "Detener")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("DETENER STREAM")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Overlay
            Button(
                onClick = onToggleOverlay,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isOverlayVisible) MaterialTheme.colorScheme.secondary
                    else MaterialTheme.colorScheme.surface
                )
            ) {
                Icon(
                    if (isOverlayVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = "Overlay"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (isOverlayVisible) "OCULTAR MARCADOR" else "MOSTRAR MARCADOR"
                )
            }

            // Estado actual
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = when {
                    isStreaming && isOverlayVisible -> "✅ Transmitiendo con marcador visible"
                    isStreaming -> "📹 Transmitiendo (marcador oculto)"
                    else -> "⏸️ Listo para transmitir"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}