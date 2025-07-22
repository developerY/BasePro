package com.ylabz.basepro.applications.bike.features.trips.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.ylabz.basepro.applications.bike.features.trips.R

@Composable
fun LoadingScreen() {
    Text(text = stringResource(R.string.state_loading), modifier = Modifier.fillMaxSize())
}

@Preview
@Composable
fun LoadingScreenPreview() {
    LoadingScreen()
}


@Composable
fun ErrorScreen(errorMessage: String, onRetry: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.state_error_generic_with_message, errorMessage),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = stringResource(R.string.action_retry),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .clickable { onRetry() }
                .padding(vertical = 8.dp)
        )
    }
}

@Preview
@Composable
fun ErrorScreenPreview() {
    ErrorScreen(errorMessage = stringResource(R.string.preview_error_something_went_wrong), onRetry = {})
}
