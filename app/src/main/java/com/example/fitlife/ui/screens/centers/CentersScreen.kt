@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.fitlife.ui.screens.centers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitlife.R
import com.example.fitlife.data.local.entity.FitnessCenterEntity
import com.example.fitlife.data.local.entity.TypeEntity
import com.example.fitlife.viewmodel.CentersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CentersScreen(
    onOpenDetails: (String) -> Unit
) {
    val vm: CentersViewModel = viewModel()

    val centers by vm.centers.collectAsState()
    val types by vm.types.collectAsState()

    val searchQuery by vm.searchQuery.collectAsState()
    val selectedTypeId by vm.selectedTypeId.collectAsState()
    val minRating by vm.minRating.collectAsState()
    val serviceQuery by vm.serviceQuery.collectAsState()
    val maxPriceText by vm.maxPriceText.collectAsState()

    val minRatingText = String.format("%.1f", minRating)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.screen_centers_title),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = vm::setSearchQuery,
            label = { Text(stringResource(R.string.centers_search_by_name)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        TypeDropdown(
            types = types,
            selectedTypeId = selectedTypeId,
            onSelectTypeId = vm::setSelectedTypeId,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.centers_min_rating, minRatingText)
        )
        Slider(
            value = minRating.toFloat(),
            onValueChange = { vm.setMinRating(it.toDouble()) },
            valueRange = 0f..5f,
            steps = 9,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = serviceQuery,
                onValueChange = vm::setServiceQuery,
                label = { Text(stringResource(R.string.centers_service)) },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = maxPriceText,
                onValueChange = vm::setMaxPriceText,
                label = { Text(stringResource(R.string.centers_max_price)) },
                singleLine = true,
                modifier = Modifier.width(140.dp)
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = vm::clearFilters) {
                Text(stringResource(R.string.centers_clear))
            }

            Text(
                text = stringResource(R.string.centers_found, centers.size),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(centers, key = { it.id }) { center ->
                CenterRow(
                    center = center,
                    onClick = { onOpenDetails(center.id) }
                )
            }
        }
    }
}

@Composable
private fun CenterRow(
    center: FitnessCenterEntity,
    onClick: () -> Unit
) {
    val ratingText = String.format("%.1f", center.rating)

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(center.name, style = MaterialTheme.typography.titleMedium)
            Text(center.address, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = stringResource(R.string.center_rating, ratingText),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun TypeDropdown(
    types: List<TypeEntity>,
    selectedTypeId: String?,
    onSelectTypeId: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val selectedName = types.firstOrNull { it.id == selectedTypeId }?.name
        ?: stringResource(R.string.centers_all_types)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedName,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.centers_type)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.centers_all_types)) },
                onClick = {
                    onSelectTypeId(null)
                    expanded = false
                }
            )

            types.forEach { t ->
                DropdownMenuItem(
                    text = { Text(t.name) },
                    onClick = {
                        onSelectTypeId(t.id)
                        expanded = false
                    }
                )
            }
        }
    }
}