package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.VRExperienceEntity
import com.example.ui.components.PanoramaHotspot
import com.example.ui.components.SphericalPanoramaViewer
import com.example.ui.components.VRViewer360
import com.example.ui.theme.NavyPrimary
import com.example.ui.util.NativeImageProvider

@Composable
fun VRExploreScreen(
    experiences: List<VRExperienceEntity>,
    onPlaceSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeExperience by remember(experiences) {
        mutableStateOf(experiences.firstOrNull())
    }
    var projectionMode by remember { mutableStateOf(0) } // 0: Spherical 360° Projection, 1: Street View VR Portal

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = NavyPrimary)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ViewInAr,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "360° Spherical Projection & Virtual Tour",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Immerse yourself in India's top heritage wonders using equirectangular spherical projection. Drag to orbit 360°, pinch to zoom, or enable gyroscope tilt tracking.",
                        fontSize = 12.sp,
                        color = Color(0xFFCBD5E1),
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Projection Mode Selector Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (projectionMode == 0) Color(0xFFF59E0B) else Color(0x33FFFFFF),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { projectionMode = 0 }
                                .testTag("spherical_mode_tab")
                        ) {
                            Text(
                                text = "🌐 Spherical 360° View",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (projectionMode == 0) Color(0xFF78350F) else Color.White,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (projectionMode == 1) Color(0xFFF59E0B) else Color(0x33FFFFFF),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { projectionMode = 1 }
                                .testTag("streetview_mode_tab")
                        ) {
                            Text(
                                text = "🧭 Street View VR",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (projectionMode == 1) Color(0xFF78350F) else Color.White,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Active Interactive Canvas
        if (activeExperience != null) {
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Currently Viewing",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (projectionMode == 0) "Spherical Mesh Mode" else "VR Portal Mode",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))

                    if (projectionMode == 0) {
                        val exp = activeExperience!!
                        SphericalPanoramaViewer(
                            imageUrl = NativeImageProvider.getVRPanoramaUrl(exp.spotName),
                            destinationTitle = exp.spotName,
                            destinationSubtitle = "${exp.title} • Spherical 360° Projection",
                            hotspots = listOf(
                                PanoramaHotspot("1", "Central Vista", "Main monument vantage point", 0f, 0f),
                                PanoramaHotspot("2", "Architecture Detail", "Intricate facade and courtyard", -8f, 50f)
                            )
                        )
                    } else {
                        VRViewer360(vrExperience = activeExperience!!)
                    }
                }
            }
        }

        // Available Panoramas List
        item {
            Text(
                text = "Available 360° Panoramas (${experiences.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(experiences) { exp ->
            val isCurrent = activeExperience?.id == exp.id
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { activeExperience = exp }
                    .testTag("vr_item_${exp.id}"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCurrent) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        AsyncImage(
                            model = NativeImageProvider.getVRPreviewUrl(exp),
                            contentDescription = exp.spotName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0F172A))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = exp.spotName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = exp.title,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Street View • ${exp.provider}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }

                    Surface(
                        color = if (isCurrent) NavyPrimary else Color(0xFF2563EB),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (isCurrent) "Viewing" else "Launch 360°",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}
