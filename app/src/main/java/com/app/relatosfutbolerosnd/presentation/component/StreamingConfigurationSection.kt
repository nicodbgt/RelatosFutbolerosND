package com.app.relatosfutbolerosnd.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun StreamingConfigurationSection(
    streamUrl: String,
    streamUrlError:String?,
    onStreamUrlChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.LiveTv,
                    contentDescription = "Configuración Streaming",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CONFIGURACIÓN DE STREAMING",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo URL RTMP
            OutlinedTextField(
                value = streamUrl,
                onValueChange = onStreamUrlChange,
                label = { Text("URL RTMP YouTube") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("rtmp://a.rtmp.youtube.com/live2/tu-clave") },
                isError  = streamUrlError != null,
                supportingText = { if(streamUrlError != null) Text (streamUrlError, color= MaterialTheme.colorScheme.error)}
            )

            // Tips
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "💡 Ejemplo: rtmp://a.rtmp.youtube.com/live2/tu-clave-secreta",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}