package com.example.fitlife.ui.screens.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitlife.R
import com.example.fitlife.viewmodel.FavoritesViewModel

@Composable
fun FavoritesScreen(
    onOpenDetails: (String) -> Unit
) {
    val vm: FavoritesViewModel = viewModel()
    val favoriteCenters by vm.favoriteCenters.collectAsState()

    if (favoriteCenters.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.favorites_empty),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.favorites_title),
                style = MaterialTheme.typography.headlineMedium
            )
        }

        items(favoriteCenters, key = { it.id }) { center ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenDetails(center.id) }
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = center.name,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = center.address,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = stringResource(
                            R.string.center_rating,
                            String.format("%.1f", center.rating)
                        ),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}