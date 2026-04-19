package com.example.fitlife.ui.screens.details

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitlife.R
import com.example.fitlife.data.local.entity.VisitEntity
import com.example.fitlife.viewmodel.CenterDetailsViewModel
import com.google.android.gms.location.LocationServices
import org.osmdroid.util.GeoPoint
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

    val fileActionMessage by vm.fileActionMessage.collectAsState()

    var visitComment by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    var editingVisit by remember { mutableStateOf<VisitEntity?>(null) }
    var editComment by remember { mutableStateOf("") }

    var visitToDelete by remember { mutableStateOf<VisitEntity?>(null) }

    val context = LocalContext.current

    var selectedVisitDateTime by remember {
        mutableLongStateOf(System.currentTimeMillis())
    }

    var userLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var locationStatusMessage by remember { mutableStateOf<String?>(null) }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted

        if (granted) {
            fetchLastKnownLocation(
                context = context,
                onLocationReceived = { location ->
                    userLocation = GeoPoint(location.latitude, location.longitude)
                    locationStatusMessage = context.getString(R.string.details_location_found)
                },
                onLocationUnavailable = {
                    locationStatusMessage = context.getString(R.string.details_location_not_found)
                }
            )
        } else {
            locationStatusMessage =
                context.getString(R.string.details_location_permission_required)
        }
    }

    LaunchedEffect(fileActionMessage) {
        if (fileActionMessage != null) {
            kotlinx.coroutines.delay(3000)
            vm.clearFileActionMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(details?.center?.name ?: stringResource(R.string.common_details)) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text(stringResource(R.string.common_back))
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
                    text = stringResource(R.string.common_loading),
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
                text = stringResource(
                    R.string.center_rating,
                    String.format("%.1f", c.rating)
                ),
                style = MaterialTheme.typography.bodyMedium
            )

            if (!c.description.isNullOrBlank()) {
                Text(
                    text = stringResource(R.string.details_description),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = c.description ?: "",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = stringResource(R.string.details_contacts_schedule),
                style = MaterialTheme.typography.titleMedium
            )

            if (!c.phone.isNullOrBlank()) {
                Text(stringResource(R.string.details_phone, c.phone ?: ""))
            }

            if (!c.website.isNullOrBlank()) {
                Text(stringResource(R.string.details_website, c.website ?: ""))
            }

            if (!c.schedule.isNullOrBlank()) {
                Text(stringResource(R.string.details_schedule, c.schedule ?: ""))
            }

            Text(
                text = stringResource(R.string.details_types),
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = if (currentDetails.types.isEmpty()) {
                    stringResource(R.string.common_dash)
                } else {
                    currentDetails.types.joinToString(", ")
                }
            )

            Text(
                text = stringResource(R.string.details_services),
                style = MaterialTheme.typography.titleMedium
            )

            if (currentDetails.services.isEmpty()) {
                Text(stringResource(R.string.common_dash))
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
                            Text(
                                stringResource(
                                    R.string.details_currency_uah,
                                    String.format("%.0f", service.price)
                                )
                            )
                        }
                    }
                }
            }

            val favLabel = if (currentDetails.isFavorite) {
                stringResource(R.string.details_remove_from_favorites)
            } else {
                stringResource(R.string.details_add_to_favorites)
            }

            Button(
                onClick = {
                    vm.toggleFavorite(centerId, currentDetails.isFavorite)
                }
            ) {
                Text(favLabel)
            }

            LocationSection(
                centerName = c.name,
                centerAddress = c.address,
                centerLatitude = c.latitude,
                centerLongitude = c.longitude,
                userLocation = userLocation,
                hasLocationPermission = hasLocationPermission,
                locationStatusMessage = locationStatusMessage,
                onShowMyLocationClick = {
                    if (hasLocationPermission) {
                        fetchLastKnownLocation(
                            context = context,
                            onLocationReceived = { location ->
                                userLocation = GeoPoint(location.latitude, location.longitude)
                                locationStatusMessage =
                                    context.getString(R.string.details_location_found)
                            },
                            onLocationUnavailable = {
                                locationStatusMessage =
                                    context.getString(R.string.details_location_not_found)
                            }
                        )
                    } else {
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                }
            )

            Text(
                text = stringResource(R.string.details_add_visit),
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = stringResource(
                    R.string.details_selected_datetime,
                    formatVisitDate(selectedVisitDateTime)
                ),
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
                    Text(stringResource(R.string.details_select_date))
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
                    Text(stringResource(R.string.details_select_time))
                }
            }

            OutlinedTextField(
                value = visitComment,
                onValueChange = { visitComment = it },
                label = { Text(stringResource(R.string.details_comment_optional)) },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    vm.addVisit(centerId, selectedVisitDateTime, visitComment)
                    visitComment = ""
                    selectedVisitDateTime = System.currentTimeMillis()
                }
            ) {
                Text(stringResource(R.string.details_save_visit))
            }

            Text(
                text = stringResource(R.string.details_file_actions),
                style = MaterialTheme.typography.titleMedium
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { vm.exportVisitsToFile() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.details_export_visits))
                }

                Button(
                    onClick = { vm.importVisitsFromFile() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.details_import_visits))
                }
            }

            if (fileActionMessage != null) {
                Text(
                    text = fileActionMessage ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = stringResource(R.string.details_visit_history),
                style = MaterialTheme.typography.titleMedium
            )

            if (visits.isEmpty()) {
                Text(stringResource(R.string.details_no_visits))
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
                                text = visit.comment ?: stringResource(R.string.details_no_comment),
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
                                    Text(stringResource(R.string.common_edit))
                                }

                                Button(
                                    onClick = {
                                        visitToDelete = visit
                                    }
                                ) {
                                    Text(stringResource(R.string.common_delete))
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
                title = { Text(stringResource(R.string.details_edit_visit_comment)) },
                text = {
                    OutlinedTextField(
                        value = editComment,
                        onValueChange = { editComment = it },
                        label = { Text(stringResource(R.string.details_comment)) },
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
                        Text(stringResource(R.string.common_save))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            editingVisit = null
                            editComment = ""
                        }
                    ) {
                        Text(stringResource(R.string.common_cancel))
                    }
                }
            )
        }

        if (visitToDelete != null) {
            AlertDialog(
                onDismissRequest = {
                    visitToDelete = null
                },
                title = { Text(stringResource(R.string.details_delete_visit)) },
                text = {
                    Text(stringResource(R.string.details_delete_visit_confirm))
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            vm.deleteVisit(visitToDelete!!.id)
                            visitToDelete = null
                        }
                    ) {
                        Text(stringResource(R.string.common_delete))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            visitToDelete = null
                        }
                    ) {
                        Text(stringResource(R.string.common_cancel))
                    }
                }
            )
        }
    }
}

private fun fetchLastKnownLocation(
    context: Context,
    onLocationReceived: (Location) -> Unit,
    onLocationUnavailable: () -> Unit
) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    if (
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        onLocationUnavailable()
        return
    }

    fusedLocationClient.lastLocation
        .addOnSuccessListener { location ->
            if (location != null) {
                onLocationReceived(location)
            } else {
                onLocationUnavailable()
            }
        }
        .addOnFailureListener {
            onLocationUnavailable()
        }
}

private fun formatVisitDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}