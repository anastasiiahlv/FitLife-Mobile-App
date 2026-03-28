package com.example.fitlife.ui.screens.details

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitlife.data.local.entity.VisitEntity
import com.example.fitlife.viewmodel.CenterDetailsViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenterDetailsScreen(
    centerId: String,
    onBack: () -> Unit
) {
    val vm: CenterDetailsViewModel = viewModel()

    val detailsFlow = remember(centerId) { vm.details(centerId) }
    val details by detailsFlow.collectAsState()

    val visitsFlow = remember(centerId) { vm.visits(centerId) }
    val visits by visitsFlow.collectAsState(initial = emptyList())

    var visitComment by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    var editingVisit by remember { mutableStateOf<VisitEntity?>(null) }
    var editComment by remember { mutableStateOf("") }

    var visitToDelete by remember { mutableStateOf<VisitEntity?>(null) }

    val context = LocalContext.current

    var selectedVisitDateTime by remember {
        mutableLongStateOf(System.currentTimeMillis())
    }

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
                text = if (currentDetails.types.isEmpty()) "—"
                else currentDetails.types.joinToString(", ")
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
                if (currentDetails.isFavorite) "Remove from favorites"
                else "Add to favorites"

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

            Text(
                text = "Selected: ${formatVisitDate(selectedVisitDateTime)}",
                style = MaterialTheme.typography.bodyMedium
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        val calendar = Calendar.getInstance().apply {
                            timeInMillis = selectedVisitDateTime
                        }

                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                val updatedCalendar = Calendar.getInstance().apply {
                                    timeInMillis = selectedVisitDateTime
                                    set(Calendar.YEAR, year)
                                    set(Calendar.MONTH, month)
                                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                }
                                selectedVisitDateTime = updatedCalendar.timeInMillis
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }
                ) {
                    Text("Select date")
                }

                Button(
                    onClick = {
                        val calendar = Calendar.getInstance().apply {
                            timeInMillis = selectedVisitDateTime
                        }

                        TimePickerDialog(
                            context,
                            { _, hourOfDay, minute ->
                                val updatedCalendar = Calendar.getInstance().apply {
                                    timeInMillis = selectedVisitDateTime
                                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                                    set(Calendar.MINUTE, minute)
                                    set(Calendar.SECOND, 0)
                                    set(Calendar.MILLISECOND, 0)
                                }
                                selectedVisitDateTime = updatedCalendar.timeInMillis
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                        ).show()
                    }
                ) {
                    Text("Select time")
                }
            }

            OutlinedTextField(
                value = visitComment,
                onValueChange = { visitComment = it },
                label = { Text("Comment (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    vm.addVisit(centerId, selectedVisitDateTime, visitComment)
                    visitComment = ""
                    selectedVisitDateTime = System.currentTimeMillis()
                }
            ) {
                Text("Save visit")
            }

            Text(
                text = "Visit history",
                style = MaterialTheme.typography.titleMedium
            )

            if (visits.isEmpty()) {
                Text("No visits yet")
            } else {
                visits.forEach { visit ->
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = formatVisitDate(visit.visit_date),
                                style = MaterialTheme.typography.titleSmall
                            )

                            Text(
                                text = visit.comment ?: "No comment",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        editingVisit = visit
                                        editComment = visit.comment ?: ""
                                    }
                                ) {
                                    Text("Edit")
                                }

                                Button(
                                    onClick = {
                                        visitToDelete = visit
                                    }
                                ) {
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (editingVisit != null) {
            AlertDialog(
                onDismissRequest = {
                    editingVisit = null
                    editComment = ""
                },
                title = { Text("Edit visit comment") },
                text = {
                    OutlinedTextField(
                        value = editComment,
                        onValueChange = { editComment = it },
                        label = { Text("Comment") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            vm.updateVisitComment(
                                visitId = editingVisit!!.id,
                                comment = editComment
                            )
                            editingVisit = null
                            editComment = ""
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            editingVisit = null
                            editComment = ""
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (visitToDelete != null) {
            AlertDialog(
                onDismissRequest = {
                    visitToDelete = null
                },
                title = { Text("Delete visit") },
                text = {
                    Text("Are you sure you want to delete this visit?")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            vm.deleteVisit(visitToDelete!!.id)
                            visitToDelete = null
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            visitToDelete = null
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

private fun formatVisitDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}