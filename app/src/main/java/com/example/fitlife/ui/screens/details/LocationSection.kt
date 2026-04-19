package com.example.fitlife.ui.screens.details

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.fitlife.R
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun LocationSection(
    centerName: String,
    centerAddress: String,
    centerLatitude: Double,
    centerLongitude: Double,
    userLocation: GeoPoint?,
    hasLocationPermission: Boolean,
    locationStatusMessage: String?,
    onShowMyLocationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val centerPoint = GeoPoint(centerLatitude, centerLongitude)
    val context = LocalContext.current

    ElevatedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.details_location),
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = stringResource(R.string.details_location_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 2.dp
            ) {
                OsmMapView(
                    centerPoint = centerPoint,
                    centerTitle = centerName,
                    centerSnippet = centerAddress,
                    userLocation = userLocation,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                )
            }

            if (!hasLocationPermission) {
                Text(
                    text = stringResource(R.string.details_location_permission_required),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (!locationStatusMessage.isNullOrBlank()) {
                Text(
                    text = locationStatusMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onShowMyLocationClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.details_show_my_location))
                }

                Button(
                    onClick = {
                        openRouteInMap(
                            context = context,
                            latitude = centerLatitude,
                            longitude = centerLongitude
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.details_open_route))
                }
            }
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
        factory = { mapView },
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

private fun openRouteInMap(
    context: Context,
    latitude: Double,
    longitude: Double
) {
    val uri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(intent)
}