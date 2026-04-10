package com.example.fitlife.ui.screens.details

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitlife.R
import com.example.fitlife.data.local.entity.VisitEntity
import com.example.fitlife.viewmodel.CenterDetailsViewModel
import com.google.android.gms.location.LocationServices
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
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

    var userLocation by remember { mutableStateOf<GeoPoint?>(null) }
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
            fetchLastKnownLocation(context) { location ->
                userLocation = GeoPoint(location.latitude, location.longitude)
            }
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
        val centerPoint = GeoPoint(c.latitude, c.longitude)

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

            Text(
                text = stringResource(R.string.details_location),
                style = MaterialTheme.typography.titleMedium
            )

            OsmMapView(
                centerPoint = centerPoint,
                centerTitle = c.name,
                centerSnippet = c.address,
                userLocation = userLocation,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        if (hasLocationPermission) {
                            fetchLastKnownLocation(context) { location ->
                                userLocation = GeoPoint(location.latitude, location.longitude)
                            }
                        } else {
                            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    }
                ) {
                    Text(stringResource(R.string.details_show_my_location))
                }

                Button(
                    onClick = {
                        openNavigation(context, c.latitude, c.longitude)
                    }
                ) {
                    Text(stringResource(R.string.details_navigate))
                }
            }

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

@Composable
private fun OsmMapView(
    centerPoint: GeoPoint,
    centerTitle: String,
    centerSnippet: String,
    userLocation: GeoPoint?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val myLocationLabel = stringResource(R.string.details_map_my_location)

    val mapView = remember {
        MapView(context).apply {
            layoutParams = android.view.ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            setMultiTouchControls(true)
            controller.setZoom(15.0)
        }
    }

    DisposableEffect(mapView) {
        onDispose {
            mapView.onDetach()
        }
    }

    AndroidView(
        factory = {
            mapView
        },
        modifier = modifier,
        update = { map ->
            map.overlays.clear()
            map.controller.setCenter(centerPoint)

            val centerMarker = Marker(map).apply {
                position = centerPoint
                title = centerTitle
                subDescription = centerSnippet
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            }
            map.overlays.add(centerMarker)

            userLocation?.let { myPoint ->
                val userMarker = Marker(map).apply {
                    position = myPoint
                    title = myLocationLabel
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                }
                map.overlays.add(userMarker)
            }

            map.invalidate()
        }
    )
}

private fun fetchLastKnownLocation(
    context: Context,
    onLocationReceived: (Location) -> Unit
) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    if (
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }

    fusedLocationClient.lastLocation
        .addOnSuccessListener { location ->
            if (location != null) {
                onLocationReceived(location)
            }
        }
}

private fun openNavigation(
    context: Context,
    latitude: Double,
    longitude: Double
) {
    val uri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(intent)
}

private fun formatVisitDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}