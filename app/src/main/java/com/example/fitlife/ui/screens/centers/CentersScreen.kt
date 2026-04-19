@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.fitlife.ui.screens.centers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import com.example.fitlife.R
import com.example.fitlife.data.local.entity.FitnessCenterEntity
import com.example.fitlife.data.local.entity.TypeEntity
import com.example.fitlife.viewmodel.CentersViewModel

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

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.screen_centers_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            item {
                Text(
                    text = stringResource(R.string.centers_found, centers.size),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                FiltersCard(
                    searchQuery = searchQuery,
                    onSearchChange = vm::setSearchQuery,
                    types = types,
                    selectedTypeId = selectedTypeId,
                    onSelectTypeId = vm::setSelectedTypeId,
                    minRating = minRating,
                    onMinRatingChange = { vm.setMinRating(it.toDouble()) },
                    serviceQuery = serviceQuery,
                    onServiceQueryChange = vm::setServiceQuery,
                    maxPriceText = maxPriceText,
                    onMaxPriceTextChange = vm::setMaxPriceText,
                    onClearFilters = vm::clearFilters
                )
            }

            if (centers.isEmpty()) {
                item {
                    NotFoundCard()
                }
            } else {
                items(centers, key = { it.id }) { center ->
                    CenterRow(
                        center = center,
                        onClick = { onOpenDetails(center.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FiltersCard(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    types: List<TypeEntity>,
    selectedTypeId: String?,
    onSelectTypeId: (String?) -> Unit,
    minRating: Double,
    onMinRatingChange: (Float) -> Unit,
    serviceQuery: String,
    onServiceQueryChange: (String) -> Unit,
    maxPriceText: String,
    onMaxPriceTextChange: (String) -> Unit,
    onClearFilters: () -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val minRatingText = String.format("%.1f", minRating)

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                label = { Text(stringResource(R.string.centers_search_by_name)) },
                placeholder = { Text(stringResource(R.string.centers_search_by_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            TextButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.centers_filters),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (expanded) {
                TypeDropdown(
                    types = types,
                    selectedTypeId = selectedTypeId,
                    onSelectTypeId = onSelectTypeId,
                    modifier = Modifier.fillMaxWidth()
                )

                Column {
                    Text(
                        text = stringResource(R.string.centers_min_rating, minRatingText),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Slider(
                        value = minRating.toFloat(),
                        onValueChange = onMinRatingChange,
                        valueRange = 0f..5f,
                        steps = 9,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = serviceQuery,
                        onValueChange = onServiceQueryChange,
                        label = { Text(stringResource(R.string.centers_service)) },
                        placeholder = { Text(stringResource(R.string.centers_service_placeholder)) },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = maxPriceText,
                        onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() }) {
                                onMaxPriceTextChange(newValue)
                            }
                        },
                        label = { Text(stringResource(R.string.centers_max_price)) },
                        placeholder = { Text("500") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(120.dp)
                    )
                }

                OutlinedButton(
                    onClick = onClearFilters,
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text(
                        text = stringResource(R.string.centers_clear),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun NotFoundCard() {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.centers_not_found_title),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.centers_not_found_message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
            .clickable { onClick() },
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = center.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = center.address,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = stringResource(R.string.center_rating, ratingText),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
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

        DropdownMenu(
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

            types.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.name) },
                    onClick = {
                        onSelectTypeId(type.id)
                        expanded = false
                    }
                )
            }
        }
    }
}