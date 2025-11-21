package com.app.relatosfutbolerosnd.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsSoccer
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
fun MatchConfigurationSection(
    team1: String,
    team2: String,
    team1Error: String?,
    team2Error: String?,
    onTeam1Change: (String) -> Unit,
    onTeam2Change: (String) -> Unit,
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
                    imageVector = Icons.Default.SportsSoccer,
                    contentDescription = "Configuración Partido",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CONFIGURACIÓN DEL PARTIDO",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campos de equipos
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Equipo 1
                OutlinedTextField(
                    value = team1,
                    onValueChange = onTeam1Change,
                    label = { Text("Equipo Local") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    isError = team1Error != null,
                    supportingText = { if (team1Error != null) {Text(team1Error)}}
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "VS",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Equipo 2
                OutlinedTextField(
                    value = team2,
                    onValueChange = onTeam2Change,
                    label = { Text("Equipo Visitante") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    isError = team2Error != null,
                    supportingText = { if(team2Error != null) {Text(team2Error)}}
                )
            }

            // Marcador actual (solo lectura)
            if (team1.isNotBlank() && team2.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Marcador: $team1 0 - 0 $team2",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}