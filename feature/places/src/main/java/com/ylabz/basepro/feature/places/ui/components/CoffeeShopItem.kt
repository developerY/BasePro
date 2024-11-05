package com.ylabz.basepro.feature.places.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.core.data.dto.ylep.BusinessInfo

@Composable
fun CoffeeShopItem(business: BusinessInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = business.name ?: "Unknown Coffee Shop", style = MaterialTheme.typography.titleMedium)
            Text(text = "Rating: ${business.rating ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
            Text(text = business.price ?: "", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            // Placeholder for image loading
            // Use Coil or similar library if you want to load images
        }
    }
}

@Composable
fun CoffeeShopList(coffeeShops: List<BusinessInfo>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(coffeeShops) { coffeeShop ->
            CoffeeShopItem(coffeeShop)
        }
    }
}

@Preview
@Composable
private fun CoffeeShopItemPreview() {
    Text(text = "Hello, Coffee Shop!")
    
}
