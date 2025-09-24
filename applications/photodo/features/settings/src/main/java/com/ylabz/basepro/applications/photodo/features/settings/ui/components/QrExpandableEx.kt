package com.ylabz.basepro.applications.photodo.features.settings.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
// Removed unused imports like Column, Spacer, Card, CardDefaults, HorizontalDivider, KeyboardArrowUp/Down
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight // For navigation indication
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.ylabz.basepro.applications.photodo.features.settings.R
// Removed: import com.ylabz.basepro.feature.qrscanner.ui.QRCodeScannerScreen

// It's assumed you have a route defined for your QR Scanner, e.g.,
// object YourAppDestinations { const val QR_SCANNER_ROUTE = "qr_scanner_screen"; }

@Composable
fun QrScannerSettingsItem( // Renamed from QrExpandableEx
    modifier: Modifier = Modifier,
    onNavigate: () -> Unit // Callback to trigger navigation
) {
    ListItem(
        headlineContent = { Text(stringResource(R.string.settings_qr_scanner_title)) },
        leadingContent = {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = stringResource(R.string.settings_qr_scanner_cd),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingContent = {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null // Decorative
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onNavigate),
        colors = ListItemDefaults.colors(containerColor = Color.Transparent) // To blend if used inside a Card wrapper elsewhere, or if background is handled by parent
    )
}
