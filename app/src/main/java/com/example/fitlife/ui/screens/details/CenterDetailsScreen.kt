package com.example.fitlife.ui.screens.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitlife.viewmodel.CenterDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenterDetailsScreen(
    centerId: String,
    onBack: () -> Unit
) {
    val vm: CenterDetailsViewModel = viewModel()
    val detailsFlow = remember(centerId) { vm.details(centerId) }
    val details by detailsFlow.collectAsState()

    var visitComment by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(details?.center?.name ?: "Details") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Back")
                    }
                }
            )
        }
    ) { padding ->

        val currentDetails = details

        if (currentDetails == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Text(
                    text = "Loading...",
                    modifier = Modifier.padding(16.dp)
                )
            }
            return@Scaffold
        }

        val c = currentDetails.center

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = c.name,
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = c.address,
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "Rating: ${String.format("%.1f", c.rating)}",
                style = MaterialTheme.typography.bodyMedium
            )

            if (!c.description.isNullOrBlank()) {
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = c.description ?: "",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = "Contacts & Schedule",
                style = MaterialTheme.typography.titleMedium
            )

            if (!c.phone.isNullOrBlank()) {
                Text("Phone: ${c.phone}")
            }

            if (!c.website.isNullOrBlank()) {
                Text("Website: ${c.website}")
            }

            if (!c.schedule.isNullOrBlank()) {
                Text("Schedule: ${c.schedule}")
            }

            Text(
                text = "Types",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = if (currentDetails.types.isEmpty()) {
                    "—"
                } else {
                    currentDetails.types.joinToString(", ")
                }
            )

            Text(
                text = "Services",
                style = MaterialTheme.typography.titleMedium
            )

            if (currentDetails.services.isEmpty()) {
                Text("—")
            } else {
                currentDetails.services.forEach { service ->
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(service.serviceName)
                            Text("${String.format("%.0f", service.price)} грн")
                        }
                    }
                }
            }

            val favLabel =
                if (currentDetails.isFavorite) {
                    "Remove from favorites"
                } else {
                    "Add to favorites"
                }

            Button(
                onClick = {
                    vm.toggleFavorite(centerId, currentDetails.isFavorite)
                }
            ) {
                Text(favLabel)
            }

            Text(
                text = "Add visit",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = visitComment,
                onValueChange = { visitComment = it },
                label = { Text("Comment (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    vm.addVisit(centerId, visitComment)
                    visitComment = ""
                }
            ) {
                Text("Save visit")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}