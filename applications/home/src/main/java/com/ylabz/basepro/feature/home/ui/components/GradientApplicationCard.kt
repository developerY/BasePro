package com.ylabz.basepro.feature.home.ui.components

import android.R.attr.description
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.feature.home.ui.components.colors.randomPastelFamilyBrush

@Composable
fun GradientApplicationCard(
    appModel: AppModel,
    navTo : ((String) -> Unit)? = null  // optional launch callback
) {
    // Pick a random gradient once per card, then remember it
    // Only generate once per card instance
    val pastelBrush = remember { randomPastelFamilyBrush() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        // Clip to the same shape so the gradient corners match the card
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(pastelBrush)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = appModel.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = appModel.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = appModel.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    // Only show the button if onLaunch is provided
                    navTo?.let { safeOnLaunch  ->
                        Button(onClick = {safeOnLaunch(appModel.path)}) {
                            Text("Launch")
                        }
                    }
                }
            }
        }
    }
}
