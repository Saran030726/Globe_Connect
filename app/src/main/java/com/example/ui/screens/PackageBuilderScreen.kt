package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.DirectionsTransit
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CustomItineraryEntity
import com.example.data.model.HotelEntity
import com.example.data.model.PlaceEntity
import com.example.data.model.StateEntity
import com.example.data.model.TravelOptionEntity
import com.example.data.weather.DestinationWeather
import com.example.ui.components.AddTouristOrHiddenSpotDialog
import com.example.ui.components.CalendarDateRangePickerDialog
import com.example.ui.components.ItineraryWeatherForecastSection
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandAmberContainer
import com.example.ui.theme.BrandIndigo
import com.example.ui.theme.BrandSunset
import com.example.ui.theme.BrandSunsetContainer
import com.example.ui.theme.BrandTeal
import com.example.ui.theme.BrandTealContainer
import com.example.ui.theme.CardBorder
import com.example.ui.theme.NavyDark
import com.example.ui.util.NativeImageProvider
import com.example.ui.viewmodel.CustomItineraryDraftState
import com.example.ui.viewmodel.PackageBuilderState
import kotlinx.coroutines.launch

@Composable
fun PackageBuilderScreen(
    packageState: PackageBuilderState,
    customItineraryDraft: CustomItineraryDraftState,
    savedItineraries: List<CustomItineraryEntity>,
    availableStates: List<StateEntity>,
    stateTouristSpots: List<PlaceEntity>,
    stateHiddenSpots: List<PlaceEntity>,
    stateHotels: List<HotelEntity>,
    stateTravelOptions: List<TravelOptionEntity>,
    onSelectState: (StateEntity) -> Unit,
    onToggleDestination: (PlaceEntity) -> Unit,
    onRemoveDestination: (Int) -> Unit,
    onSetDraftDates: (startDate: String, endDate: String, days: Int) -> Unit,
    onSetDraftTitle: (String) -> Unit,
    onSetDraftTravelers: (Int) -> Unit,
    onSetDraftPreferences: (hotelType: String, travelMode: String) -> Unit,
    onSetDraftNotes: (String) -> Unit,
    onSaveCustomItinerary: () -> Long,
    onDeleteSavedItinerary: (Long) -> Unit,
    onLoadItineraryIntoDraft: (CustomItineraryEntity) -> Unit,
    onClearDraft: () -> Unit,
    onCheckout: (String) -> Unit,
    itineraryWeather: List<DestinationWeather> = emptyList(),
    isWeatherLoading: Boolean = false,
    onRefreshWeather: () -> Unit = {},
    onAddNewSpot: ((
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
    ) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterStateId by remember { mutableStateOf<Int?>(availableStates.firstOrNull()?.id) }
    var deleteConfirmDialog by remember { mutableStateOf<CustomItineraryEntity?>(null) }
    var saveSuccessDialog by remember { mutableStateOf(false) }
    var showCalendarDialog by remember { mutableStateOf(false) }
    var showAddSpotDialog by remember { mutableStateOf(false) }
    var spotTypeFilter by remember { mutableIntStateOf(0) } // 0 = All, 1 = Tourist Spots, 2 = Hidden Gems

    val isDatesValid = remember(customItineraryDraft.startDate, customItineraryDraft.endDate) {
        try {
            val sdf = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.US)
            val d1 = sdf.parse(customItineraryDraft.startDate)
            val d2 = sdf.parse(customItineraryDraft.endDate)
            d1 != null && d2 != null && d1.before(d2)
        } catch (e: Exception) {
            customItineraryDraft.durationDays > 0
        }
    }

    // Aggregate places for destination selection
    val allActiveSpots = remember(stateTouristSpots, stateHiddenSpots) {
        stateTouristSpots + stateHiddenSpots
    }

    val filteredSpots = remember(allActiveSpots, searchQuery, spotTypeFilter) {
        val byType = when (spotTypeFilter) {
            1 -> allActiveSpots.filter { !it.isHidden }
            2 -> allActiveSpots.filter { it.isHidden }
            else -> allActiveSpots
        }
        if (searchQuery.isBlank()) {
            byType
        } else {
            byType.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.category.contains(searchQuery, ignoreCase = true) ||
                        it.stateName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Mode Tabs: Builder vs Saved Itineraries (Room DB)
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = BrandSunset
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Build Itinerary",
                                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        },
                        modifier = Modifier.testTag("tab_builder")
                    )

                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Saved (${savedItineraries.size})",
                                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        },
                        modifier = Modifier.testTag("tab_saved_itineraries")
                    )
                }
            }

            if (selectedTab == 0) {
                // ==================== TAB 0: BUILDER ====================
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header Status Summary
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            listOf(BrandSunset.copy(alpha = 0.08f), BrandAmber.copy(alpha = 0.15f))
                                        )
                                    )
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Custom Package Builder",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Select multiple destinations, choose dates & save to local Room database",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Surface(
                                        color = BrandSunsetContainer,
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = "₹${customItineraryDraft.totalEstimatedCost}",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF7C2D12),
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Section 1: Trip Identity & Dates
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = null,
                                            tint = BrandSunset,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "1. Trip Details & Travel Dates",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Surface(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "${customItineraryDraft.durationDays} Days",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BrandSunset,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Itinerary Title
                                OutlinedTextField(
                                    value = customItineraryDraft.title,
                                    onValueChange = onSetDraftTitle,
                                    label = { Text("Itinerary Name") },
                                    placeholder = { Text("e.g. Royal Forts & Coastal Sunset Circuit") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("itinerary_title_input")
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Quick Title Suggestion Chips
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    val suggestions = listOf(
                                        "Golden Triangle Heritage",
                                        "Kerala Backwaters & Hills",
                                        "Rajasthan Royal Circuit",
                                        "Goa & Konkan Coastline",
                                        "Himalayan Snow Valley"
                                    )
                                    items(suggestions) { s ->
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = if (customItineraryDraft.title == s) BrandSunsetContainer else MaterialTheme.colorScheme.surfaceVariant,
                                            modifier = Modifier.clickable { onSetDraftTitle(s) }
                                        ) {
                                            Text(
                                                text = s,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = if (customItineraryDraft.title == s) Color(0xFF7C2D12) else MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Interactive Calendar Date-Range Picker Card
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isDatesValid) BrandTealContainer.copy(alpha = 0.25f) else Color(0xFFFEE2E2),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isDatesValid) BrandTeal else Color(0xFFEF4444)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showCalendarDialog = true }
                                        .testTag("open_calendar_range_picker_card")
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.CalendarMonth,
                                                    contentDescription = null,
                                                    tint = if (isDatesValid) BrandTeal else Color(0xFFDC2626),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "Trip Calendar Window",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }

                                            Surface(
                                                color = if (isDatesValid) BrandTeal else Color(0xFFDC2626),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(
                                                    text = "${customItineraryDraft.durationDays} Days / ${customItineraryDraft.durationDays - 1} Nights",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = "DEPARTURE",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.outline
                                                )
                                                Text(
                                                    text = customItineraryDraft.startDate,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }

                                            Text(
                                                text = "→",
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = BrandSunset
                                            )

                                            Column(horizontalAlignment = Alignment.End) {
                                                Text(
                                                    text = "RETURN",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.outline
                                                )
                                                Text(
                                                    text = customItineraryDraft.endDate,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Live Validation status text
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(
                                                    imageVector = if (isDatesValid) Icons.Default.CheckCircle else Icons.Default.Close,
                                                    contentDescription = null,
                                                    tint = if (isDatesValid) BrandTeal else Color(0xFFDC2626),
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = if (isDatesValid) "Start date is before end date • Valid trip duration" else "Validation error: Start date must be before end date",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = if (isDatesValid) Color(0xFF042F2E) else Color(0xFF991B1B)
                                                )
                                            }

                                            Text(
                                                text = "Edit Calendar",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = BrandSunset
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Button to open full calendar range picker dialog
                                Button(
                                    onClick = { showCalendarDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("open_calendar_picker_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = null,
                                        tint = BrandSunset,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Open Calendar Date-Range Picker",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Quick Duration Presets Strip (Calculates duration and auto-validates)
                                Text(
                                    text = "Quick Duration Presets (Calculates End Date):",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(3, 5, 7, 10, 14).forEach { days ->
                                        val isSelected = customItineraryDraft.durationDays == days && isDatesValid
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = {
                                                // Calculate end date based on start date + days
                                                val sdf = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.US)
                                                val cal = java.util.Calendar.getInstance()
                                                try {
                                                    val start = sdf.parse(customItineraryDraft.startDate)
                                                    if (start != null) cal.time = start
                                                } catch (e: Exception) {
                                                    cal.add(java.util.Calendar.DAY_OF_MONTH, 14)
                                                }
                                                val startFormatted = sdf.format(cal.time)
                                                cal.add(java.util.Calendar.DAY_OF_MONTH, days)
                                                val endFormatted = sdf.format(cal.time)
                                                onSetDraftDates(startFormatted, endFormatted, days)
                                            },
                                            label = { Text("$days Days", fontSize = 11.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = BrandSunset,
                                                selectedLabelColor = Color.White
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Travelers Stepper
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.People,
                                            contentDescription = null,
                                            tint = BrandSunset,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Travelers: ${customItineraryDraft.travelersCount} Adults",
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp
                                        )
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        listOf(1, 2, 4, 6).forEach { count ->
                                            val isSelected = customItineraryDraft.travelersCount == count
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = if (isSelected) BrandSunset else MaterialTheme.colorScheme.surfaceVariant,
                                                modifier = Modifier.clickable { onSetDraftTravelers(count) }
                                            ) {
                                                Text(
                                                    text = "$count",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Section 2: Multi-Destination Selector (Multiple Destinations across India)
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Place,
                                            contentDescription = null,
                                            tint = BrandSunset,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "2. Select Multiple Destinations",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Surface(
                                        color = if (customItineraryDraft.selectedDestinations.isNotEmpty()) BrandSunsetContainer else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "${customItineraryDraft.selectedDestinations.size} Selected",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (customItineraryDraft.selectedDestinations.isNotEmpty()) Color(0xFF7C2D12) else MaterialTheme.colorScheme.outline,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Select any number of destinations across Indian states to form your custom circuit:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Selected Destinations Strip
                                if (customItineraryDraft.selectedDestinations.isNotEmpty()) {
                                    Text(
                                        text = "Current Itinerary Stops (${customItineraryDraft.selectedDestinations.size}):",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandSunset
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        contentPadding = PaddingValues(vertical = 4.dp)
                                    ) {
                                        items(customItineraryDraft.selectedDestinations) { place ->
                                            Surface(
                                                color = MaterialTheme.colorScheme.surfaceVariant,
                                                shape = RoundedCornerShape(12.dp),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, BrandSunsetContainer)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                                ) {
                                                    AsyncImage(
                                                        model = NativeImageProvider.getNativeImageUrl(place),
                                                        contentDescription = place.name,
                                                        contentScale = ContentScale.Crop,
                                                        modifier = Modifier
                                                            .size(24.dp)
                                                            .clip(CircleShape)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Column {
                                                        Text(
                                                            text = place.name,
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                        Text(
                                                            text = place.stateName,
                                                            fontSize = 9.sp,
                                                            color = MaterialTheme.colorScheme.outline
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    IconButton(
                                                        onClick = { onRemoveDestination(place.id) },
                                                        modifier = Modifier.size(20.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Close,
                                                            contentDescription = "Remove",
                                                            tint = Color(0xFFEF4444),
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                }

                                // Search Field
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    placeholder = { Text("Search places to add (e.g. Taj Mahal, Ooty, Hampi)...") },
                                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BrandSunset) },
                                    trailingIcon = {
                                        if (searchQuery.isNotEmpty()) {
                                            IconButton(onClick = { searchQuery = "" }) {
                                                Icon(Icons.Default.Close, contentDescription = "Clear")
                                            }
                                        }
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("itinerary_destination_search")
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // State Filter Chips
                                Text(
                                    text = "Filter Destinations by State:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(availableStates) { st ->
                                        val isSelected = selectedFilterStateId == st.id
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = {
                                                selectedFilterStateId = st.id
                                                onSelectState(st)
                                            },
                                            label = { Text(st.name, fontSize = 11.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = BrandSunset,
                                                selectedLabelColor = Color.White
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Destination Type Filter Chips (All vs Tourist Spots vs Hidden Gems) & Add Custom Spot
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.weight(1f, fill = false)
                                    ) {
                                        item {
                                            FilterChip(
                                                selected = spotTypeFilter == 0,
                                                onClick = { spotTypeFilter = 0 },
                                                label = { Text("All Spots (${allActiveSpots.size})", fontSize = 11.sp) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = BrandSunset,
                                                    selectedLabelColor = Color.White
                                                )
                                            )
                                        }
                                        item {
                                            FilterChip(
                                                selected = spotTypeFilter == 1,
                                                onClick = { spotTypeFilter = 1 },
                                                label = { Text("★ Tourist Spots (${stateTouristSpots.size})", fontSize = 11.sp) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = BrandAmber,
                                                    selectedLabelColor = Color.White
                                                )
                                            )
                                        }
                                        item {
                                            FilterChip(
                                                selected = spotTypeFilter == 2,
                                                onClick = { spotTypeFilter = 2 },
                                                label = { Text("💎 Hidden Gems (${stateHiddenSpots.size})", fontSize = 11.sp) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = BrandSunset,
                                                    selectedLabelColor = Color.White
                                                )
                                            )
                                        }
                                    }

                                    if (onAddNewSpot != null) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = AmberAccent,
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier
                                                .clickable { showAddSpotDialog = true }
                                                .testTag("itinerary_add_custom_spot_btn")
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = null,
                                                    tint = NavyDark,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(
                                                    text = "+ Spot",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = NavyDark
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // List of Places with Native Photos and Add Button
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (filteredSpots.isEmpty()) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 24.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = "No destinations matching the selected filter.",
                                                    fontSize = 12.sp,
                                                    color = MaterialTheme.colorScheme.outline
                                                )
                                                if (onAddNewSpot != null) {
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Button(
                                                        onClick = { showAddSpotDialog = true },
                                                        colors = ButtonDefaults.buttonColors(containerColor = AmberAccent),
                                                        shape = RoundedCornerShape(8.dp)
                                                    ) {
                                                        Icon(Icons.Default.Add, contentDescription = null, tint = NavyDark, modifier = Modifier.size(14.dp))
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text("Add New Destination Now", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    filteredSpots.take(35).forEach { spot ->
                                        val isAdded = customItineraryDraft.selectedDestinations.any { it.id == spot.id }
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = if (isAdded) BrandTealContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant,
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                if (isAdded) BrandTeal else CardBorder
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { onToggleDestination(spot) }
                                                .testTag("dest_item_${spot.id}")
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    AsyncImage(
                                                        model = NativeImageProvider.getNativeImageUrl(spot),
                                                        contentDescription = spot.name,
                                                        contentScale = ContentScale.Crop,
                                                        modifier = Modifier
                                                            .size(50.dp)
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .background(Color(0xFF0F172A))
                                                    )
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                    Column {
                                                        Text(
                                                            text = spot.name,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 13.sp,
                                                            color = MaterialTheme.colorScheme.onSurface,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                        Text(
                                                            text = "📍 ${spot.stateName} • ${spot.category}",
                                                            fontSize = 11.sp,
                                                            color = MaterialTheme.colorScheme.outline
                                                        )
                                                        if (spot.isHidden) {
                                                            Text(
                                                                text = "💎 Hidden Gem",
                                                                fontSize = 9.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color(0xFF7C2D12)
                                                            )
                                                        }
                                                    }
                                                }

                                                Surface(
                                                    color = if (isAdded) BrandTeal else BrandSunset,
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = if (isAdded) Icons.Default.Check else Icons.Default.Add,
                                                            contentDescription = null,
                                                            tint = Color.White,
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(3.dp))
                                                        Text(
                                                            text = if (isAdded) "Added" else "Add",
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color.White
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Weather Forecast Section for Selected Destinations in Itinerary
                    item {
                        ItineraryWeatherForecastSection(
                            weatherList = itineraryWeather,
                            isLoading = isWeatherLoading,
                            onRefresh = onRefreshWeather
                        )
                    }

                    // Section 3: Accommodation, Transport & Notes
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Hotel,
                                        contentDescription = null,
                                        tint = BrandSunset,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "3. Accommodation & Travel Logistics",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Hotel Tiers
                                Text(
                                    text = "Preferred Stay Tier:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                val hotelTiers = listOf(
                                    "Heritage Palace & Luxury Resort",
                                    "Boutique Heritage Haveli",
                                    "Eco Nature Camp & Glamping",
                                    "Comfort City Hotel"
                                )
                                hotelTiers.forEach { tier ->
                                    val isSelected = customItineraryDraft.hotelType == tier
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) BrandSunsetContainer else MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp)
                                            .clickable {
                                                onSetDraftPreferences(tier, customItineraryDraft.travelMode)
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = tier,
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) Color(0xFF7C2D12) else MaterialTheme.colorScheme.onSurface
                                            )
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = BrandSunset,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Travel Mode
                                Text(
                                    text = "Inter-City Transit Mode:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                val travelModes = listOf(
                                    "Vande Bharat & Private SUV",
                                    "Flight & Chauffeur SUV",
                                    "Luxury AC Sleeper Coach"
                                )
                                travelModes.forEach { mode ->
                                    val isSelected = customItineraryDraft.travelMode == mode
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) BrandSunsetContainer else MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp)
                                            .clickable {
                                                onSetDraftPreferences(customItineraryDraft.hotelType, mode)
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = mode,
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) Color(0xFF7C2D12) else MaterialTheme.colorScheme.onSurface
                                            )
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = BrandSunset,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Travel Notes Field
                                OutlinedTextField(
                                    value = customItineraryDraft.notes,
                                    onValueChange = onSetDraftNotes,
                                    label = { Text("Special Requests / Travel Notes") },
                                    placeholder = { Text("e.g. Vegetarian food requests, sunrise photography stops...") },
                                    maxLines = 3,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("itinerary_notes_input")
                                )
                            }
                        }
                    }

                    // Section 4: Live Price Breakdown & Room Database Persistence Action
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = "Estimated Budget & Summary",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                CostRow(
                                    label = "Destinations (${customItineraryDraft.selectedDestinations.size} stops × ${customItineraryDraft.travelersCount} pax)",
                                    value = "₹${customItineraryDraft.spotPassCost}"
                                )
                                CostRow(
                                    label = "Accommodation (${customItineraryDraft.durationDays} nights)",
                                    value = "₹${customItineraryDraft.hotelTotalCost}"
                                )
                                CostRow(
                                    label = "Transit & Logistics (${customItineraryDraft.travelersCount} travelers)",
                                    value = "₹${customItineraryDraft.travelTotalCost}"
                                )
                                CostRow(
                                    label = "Taxes & Service Fees (5%)",
                                    value = "₹${customItineraryDraft.taxAndFees}"
                                )

                                androidx.compose.material3.HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    color = CardBorder
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Estimated Total Budget",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                        Text(
                                            text = "All-inclusive tour estimate",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                    Text(
                                        text = "₹${customItineraryDraft.totalEstimatedCost}",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Black,
                                        color = BrandSunset
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // PRIMARY BUTTON: Save to Room Database
                                Button(
                                    onClick = {
                                        val id = onSaveCustomItinerary()
                                        saveSuccessDialog = true
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Itinerary '${customItineraryDraft.title}' saved to Room Database!")
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandSunset),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("save_itinerary_room_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Save,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Save Itinerary to Room Database",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // SECONDARY BUTTON: Book Itinerary
                                OutlinedButton(
                                    onClick = { onCheckout(customItineraryDraft.startDate) },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("book_itinerary_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = BrandIndigo,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Confirm & Book Package",
                                        fontWeight = FontWeight.Bold,
                                        color = BrandIndigo
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // ==================== TAB 1: SAVED ITINERARIES (ROOM DATABASE) ====================
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Saved Itineraries",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Persistent local SQLite records via Room Database",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }

                            Surface(
                                color = BrandSunsetContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "${savedItineraries.size} Saved",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF7C2D12),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    if (savedItineraries.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bookmark,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.size(54.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "No Saved Itineraries Yet",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Design a multi-destination travel itinerary in the 'Build Itinerary' tab and tap 'Save Itinerary' to persist it in Room database.",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.outline,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = { selectedTab = 0 },
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandSunset)
                                    ) {
                                        Text("Open Package Builder")
                                    }
                                }
                            }
                        }
                    } else {
                        items(savedItineraries) { itinerary ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("saved_itinerary_card_${itinerary.id}"),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = itinerary.title,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Black,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "🗓️ ${itinerary.startDate} – ${itinerary.endDate} • ${itinerary.durationDays} Days",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = BrandSunset
                                            )
                                        }

                                        Surface(
                                            color = BrandTealContainer,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "ROOM SAVED",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = BrandTeal,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "📍 Destinations: ${itinerary.destinationNames}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 16.sp
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "🏛️ States: ${itinerary.statesVisited}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "🏨 Stay: ${itinerary.hotelType} • 🚆 Transit: ${itinerary.travelMode} • 👥 ${itinerary.travelersCount} Travelers",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )

                                    if (itinerary.notes.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "📝 Note: ${itinerary.notes}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }

                                    androidx.compose.material3.HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 10.dp),
                                        color = CardBorder
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Estimated Budget",
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                            Text(
                                                text = "₹${itinerary.totalCost}",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Black,
                                                color = BrandSunset
                                            )
                                        }

                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            OutlinedButton(
                                                onClick = {
                                                    onLoadItineraryIntoDraft(itinerary)
                                                    selectedTab = 0
                                                },
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Load in Builder", fontSize = 11.sp)
                                            }

                                            IconButton(
                                                onClick = { deleteConfirmDialog = itinerary }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete",
                                                    tint = Color(0xFFEF4444),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }

    // Save Confirmation Dialog
    if (saveSuccessDialog) {
        AlertDialog(
            onDismissRequest = { saveSuccessDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = BrandTeal,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Itinerary Saved!")
                }
            },
            text = {
                Column {
                    Text(
                        text = "Your custom travel itinerary '${customItineraryDraft.title}' has been successfully stored in Room database SQLite storage.",
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• ${customItineraryDraft.selectedDestinations.size} Destinations included\n• ${customItineraryDraft.durationDays} Days duration\n• Estimated Budget: ₹${customItineraryDraft.totalEstimatedCost}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        saveSuccessDialog = false
                        selectedTab = 1 // Switch to saved itineraries tab!
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandSunset)
                ) {
                    Text("View Saved Itineraries")
                }
            },
            dismissButton = {
                TextButton(onClick = { saveSuccessDialog = false }) {
                    Text("Keep Editing")
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (deleteConfirmDialog != null) {
        val itin = deleteConfirmDialog!!
        AlertDialog(
            onDismissRequest = { deleteConfirmDialog = null },
            title = { Text("Delete Itinerary?") },
            text = {
                Text("Are you sure you want to permanently delete '${itin.title}' from the Room database?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteSavedItinerary(itin.id)
                        deleteConfirmDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Calendar Date-Range Picker Dialog
    if (showCalendarDialog) {
        CalendarDateRangePickerDialog(
            initialStartDate = customItineraryDraft.startDate,
            initialEndDate = customItineraryDraft.endDate,
            onDismissRequest = { showCalendarDialog = false },
            onDateRangeSelected = { start, end, duration ->
                onSetDraftDates(start, end, duration)
            }
        )
    }

    // Add Tourist Spot or Hidden Gem Dialog
    if (showAddSpotDialog && onAddNewSpot != null) {
        val currState = availableStates.find { it.id == selectedFilterStateId } ?: availableStates.firstOrNull()
        AddTouristOrHiddenSpotDialog(
            availableStates = availableStates,
            candidateTouristSpots = stateTouristSpots,
            initialStateId = currState?.id,
            initialIsHidden = spotTypeFilter == 2,
            onDismiss = { showAddSpotDialog = false },
            onAddSpot = { name, stateId, cat, desc, isHidden, assocId, season, dur, fee, lat, lng, vr, img ->
                onAddNewSpot(name, stateId, cat, desc, isHidden, assocId, season, dur, fee, lat, lng, vr, img)
            }
        )
    }
}

@Composable
private fun CostRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}
