package com.example.fitlife.ui.screens.centers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Text("Fitness Centers", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(12.dp))

        // 🔎 Search by name (TextField)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = vm::setSearchQuery,
            label = { Text("Search by name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // ⬇️ Type dropdown
        TypeDropdown(
            types = types,
            selectedTypeId = selectedTypeId,
            onSelectTypeId = vm::setSelectedTypeId,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // ⭐ Min rating slider
        Text("Min rating: ${String.format("%.1f", minRating)}")
        Slider(
            value = minRating.toFloat(),
            onValueChange = { vm.setMinRating(it.toDouble()) },
            valueRange = 0f..5f,
            steps = 9, // 0.5 крок
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // 🧾 Service + max price
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = serviceQuery,
                onValueChange = vm::setServiceQuery,
                label = { Text("Service") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = maxPriceText,
                onValueChange = vm::setMaxPriceText,
                label = { Text("Max price") },
                singleLine = true,
                modifier = Modifier.width(140.dp)
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = vm::clearFilters) { Text("Clear") }
            Text(
                text = "Found: ${centers.size}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        // 📋 List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(centers, key = { it.id }) { center ->
                CenterRow(center = center, onClick = { onOpenDetails(center.id) })
            }
        }
    }
}

@Composable
private fun CenterRow(
    center: FitnessCenterEntity,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(center.name, style = MaterialTheme.typography.titleMedium)
            Text(center.address, style = MaterialTheme.typography.bodyMedium)
            Text("Rating: ${String.format("%.1f", center.rating)}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TypeDropdown(
    types: List<TypeEntity>,
    selectedTypeId: String?,
    onSelectTypeId: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val selectedName = types.firstOrNull { it.id == selectedTypeId }?.name ?: "All types"

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Type") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("All types") },
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
