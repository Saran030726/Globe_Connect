package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.PlaceEntity
import com.example.data.model.StateEntity
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandAmberContainer
import com.example.ui.theme.BrandIndigo
import com.example.ui.theme.BrandSunset
import com.example.ui.theme.BrandSunsetContainer
import com.example.ui.theme.BrandTeal
import com.example.ui.theme.EmeraldNature

@Composable
fun AddTouristOrHiddenSpotDialog(
    availableStates: List<StateEntity>,
    candidateTouristSpots: List<PlaceEntity>,
    initialStateId: Int? = null,
    initialIsHidden: Boolean = false,
    onDismiss: () -> Unit,
    onAddSpot: (
        name: String,
        stateId: Int,
        category: String,
        description: String,
        isHidden: Boolean,
        associatedTouristSpotId: Int?,
        bestSeason: String,
        duration: String,
        entryFee: String,
        latitude: Double?,
        longitude: Double?,
        vrAvailable: Boolean,
        imageUrl: String?
    ) -> Unit
) {
    val defaultState = availableStates.find { it.id == initialStateId } ?: availableStates.firstOrNull()

    var isHidden by remember { mutableStateOf(initialIsHidden) }
    var selectedState by remember { mutableStateOf<StateEntity?>(defaultState) }
    var spotName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Major Attraction") }
    var description by remember { mutableStateOf("") }
    var bestSeason by remember { mutableStateOf("October to March") }
    var duration by remember { mutableStateOf("2 - 3 hours") }
    var entryFee by remember { mutableStateOf("Standard Entry") }
    var latitudeText by remember { mutableStateOf("") }
    var longitudeText by remember { mutableStateOf("") }
    var vrAvailable by remember { mutableStateOf(false) }
    var imageUrl by remember { mutableStateOf("") }
    var associatedSpotId by remember { mutableStateOf<Int?>(null) }

    var stateDropdownExpanded by remember { mutableStateOf(false) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var associatedDropdownExpanded by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf(false) }

    val categories = listOf(
        "Major Attraction",
        "Pilgrimage & Heritage",
        "Waterfalls",
        "Mountains & Valleys",
        "Beaches & Coastal",
        "Wildlife & Nature",
        "Caves & Geotourism",
        "History & Heritage",
        "Lakes & Water Bodies",
        "Trekking & Eco-Trails",
        "Culture & Rural"
    )

    // Spots eligible to be parent tourist spots in this state
    val stateParentSpots = remember(candidateTouristSpots, selectedState) {
        if (selectedState == null) candidateTouristSpots.filter { !it.isHidden }
        else candidateTouristSpots.filter { !it.isHidden && it.stateId == selectedState?.id }
    }

    val selectedAssociatedSpot = remember(stateParentSpots, associatedSpotId) {
        stateParentSpots.find { it.id == associatedSpotId }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .heightIn(max = 700.dp)
            .testTag("add_spot_dialog"),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isHidden) BrandSunsetContainer else BrandAmberContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isHidden) Icons.Default.Explore else Icons.Default.Place,
                            contentDescription = null,
                            tint = if (isHidden) BrandSunset else BrandAmber,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isHidden) "Add Hidden Gem" else "Add Tourist Spot",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Contribute verified destinations to Globe Connect",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Type Switcher: Top Tourist Spot vs Hidden Gem
                Text(
                    text = "Destination Type",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (!isHidden) BrandAmberContainer else MaterialTheme.colorScheme.surfaceVariant,
                        border = if (!isHidden) androidx.compose.foundation.BorderStroke(1.5.dp, BrandAmber) else null,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { isHidden = false }
                            .testTag("type_tourist_spot_tab")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (!isHidden) BrandAmber else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "★ Tourist Spot",
                                fontSize = 12.sp,
                                fontWeight = if (!isHidden) FontWeight.Bold else FontWeight.Medium,
                                color = if (!isHidden) Color(0xFF78350F) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isHidden) BrandSunsetContainer else MaterialTheme.colorScheme.surfaceVariant,
                        border = if (isHidden) androidx.compose.foundation.BorderStroke(1.5.dp, BrandSunset) else null,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { isHidden = true }
                            .testTag("type_hidden_gem_tab")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "💎", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Hidden Gem",
                                fontSize = 12.sp,
                                fontWeight = if (isHidden) FontWeight.Bold else FontWeight.Medium,
                                color = if (isHidden) Color(0xFF7C2D12) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // State Selector Dropdown
                Text(
                    text = "State / Union Territory *",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Box {
                    OutlinedButton(
                        onClick = { stateDropdownExpanded = true },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("select_state_dropdown")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = selectedState?.name ?: "Select Indian State",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }

                    DropdownMenu(
                        expanded = stateDropdownExpanded,
                        onDismissRequest = { stateDropdownExpanded = false }
                    ) {
                        availableStates.forEach { st ->
                            DropdownMenuItem(
                                text = { Text(st.name, fontSize = 13.sp) },
                                onClick = {
                                    selectedState = st
                                    stateDropdownExpanded = false
                                    // Reset associated spot if state changed
                                    associatedSpotId = null
                                }
                            )
                        }
                    }
                }

                // If Hidden Gem: Associated Tourist Spot Dropdown
                if (isHidden) {
                    Text(
                        text = "Associated Main Tourist Spot (Optional Circuit Link)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Box {
                        OutlinedButton(
                            onClick = { associatedDropdownExpanded = true },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("select_associated_spot_dropdown")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = selectedAssociatedSpot?.name ?: "No Specific Parent Spot (Standalone)",
                                        fontSize = 12.sp
                                    )
                                }
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }

                        DropdownMenu(
                            expanded = associatedDropdownExpanded,
                            onDismissRequest = { associatedDropdownExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("None (Standalone Hidden Gem)", fontSize = 12.sp) },
                                onClick = {
                                    associatedSpotId = null
                                    associatedDropdownExpanded = false
                                }
                            )
                            stateParentSpots.take(30).forEach { p ->
                                DropdownMenuItem(
                                    text = { Text("${p.name} (${p.category})", fontSize = 12.sp) },
                                    onClick = {
                                        associatedSpotId = p.id
                                        associatedDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Spot Name Input
                OutlinedTextField(
                    value = spotName,
                    onValueChange = {
                        spotName = it
                        if (nameError && it.isNotBlank()) nameError = false
                    },
                    label = { Text("Spot Name *") },
                    placeholder = { Text(if (isHidden) "e.g. Whispering Pine Valley, Secret Waterfall" else "e.g. Sun Temple, Emerald Hilltop") },
                    isError = nameError,
                    supportingText = if (nameError) { { Text("Spot name is required") } } else null,
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_spot_name_input")
                )

                // Category Dropdown
                Text(
                    text = "Category *",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Box {
                    OutlinedButton(
                        onClick = { categoryDropdownExpanded = true },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("select_category_dropdown")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = selectedCategory, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }

                    DropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat, fontSize = 13.sp) },
                                onClick = {
                                    selectedCategory = cat
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & Travel Notes") },
                    placeholder = { Text("Describe the scenic beauty, historical significance, trekking paths, or cultural significance...") },
                    minLines = 3,
                    maxLines = 5,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_spot_desc_input")
                )

                // Best Season & Duration
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = bestSeason,
                        onValueChange = { bestSeason = it },
                        label = { Text("Best Season") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("add_spot_season_input")
                    )

                    OutlinedTextField(
                        value = duration,
                        onValueChange = { duration = it },
                        label = { Text("Duration") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("add_spot_duration_input")
                    )
                }

                // Entry Fee
                OutlinedTextField(
                    value = entryFee,
                    onValueChange = { entryFee = it },
                    label = { Text("Entry Fee") },
                    placeholder = { Text("e.g. Free Entry, ₹50") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_spot_fee_input")
                )

                // GPS Coordinates (Optional)
                Text(
                    text = "GPS Coordinates (Optional)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = latitudeText,
                        onValueChange = { latitudeText = it },
                        label = { Text("Latitude (° N)") },
                        placeholder = { Text("e.g. 13.0827") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("add_spot_lat_input")
                    )

                    OutlinedTextField(
                        value = longitudeText,
                        onValueChange = { longitudeText = it },
                        label = { Text("Longitude (° E)") },
                        placeholder = { Text("e.g. 80.2707") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("add_spot_lng_input")
                    )
                }

                // VR Available Switch
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ViewInAr,
                                contentDescription = null,
                                tint = BrandSunset,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "360° VR Spherical Tour Available",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Enable panoramic interactive view",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }

                        Switch(
                            checked = vrAvailable,
                            onCheckedChange = { vrAvailable = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = BrandSunset),
                            modifier = Modifier.testTag("add_spot_vr_toggle")
                        )
                    }
                }

                // Custom Image URL (Optional)
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Custom Photo URL (Optional)") },
                    placeholder = { Text("https://example.com/photo.jpg (leave empty for auto)") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_spot_image_url_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (spotName.isBlank()) {
                        nameError = true
                        return@Button
                    }
                    val st = selectedState ?: availableStates.firstOrNull() ?: return@Button
                    val lat = latitudeText.toDoubleOrNull()
                    val lng = longitudeText.toDoubleOrNull()

                    onAddSpot(
                        spotName.trim(),
                        st.id,
                        selectedCategory,
                        description.trim(),
                        isHidden,
                        if (isHidden) associatedSpotId else null,
                        bestSeason.ifBlank { "October to March" },
                        duration.ifBlank { "2 - 3 hours" },
                        entryFee.ifBlank { "Standard Entry" },
                        lat,
                        lng,
                        vrAvailable,
                        imageUrl.ifBlank { null }
                    )
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isHidden) BrandSunset else BrandAmber
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("add_spot_submit_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isHidden) "Add Hidden Gem" else "Add Tourist Spot",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("add_spot_cancel_btn")
            ) {
                Text("Cancel")
            }
        }
    )
}
