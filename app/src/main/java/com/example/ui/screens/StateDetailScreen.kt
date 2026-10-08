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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardTravel
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.ViewInAr
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
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.HotelEntity
import com.example.data.model.PlaceEntity
import com.example.data.model.StateEntity
import com.example.data.model.TravelOptionEntity
import com.example.ui.components.AddTouristOrHiddenSpotDialog
import com.example.ui.components.PlaceCard
import com.example.ui.components.VRViewer360
import com.example.ui.util.NativeImageProvider
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldNature
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.viewmodel.SortOption

@Composable
fun StateDetailScreen(
    state: StateEntity,
    touristSpots: List<PlaceEntity>,
    hiddenSpots: List<PlaceEntity>,
    hotels: List<HotelEntity>,
    travelOptions: List<TravelOptionEntity>,
    onPlaceSelect: (Int) -> Unit,
    onWishlistToggle: (PlaceEntity) -> Unit,
    isPlaceWishlisted: (Int) -> Boolean,
    onStartPackageWithState: () -> Unit,
    onToggleSpotInTrip: (PlaceEntity) -> Unit,
    isSpotInTrip: (Int) -> Boolean,
    onBack: () -> Unit,
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
    availableStates: List<StateEntity> = emptyList(),
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddSpotDialog by remember { mutableStateOf(false) }
    val tabs = listOf("Tour Spots (${touristSpots.size})", "Hidden Spots (${hiddenSpots.size})", "Hotels (${hotels.size})", "Travel (${travelOptions.size})", "VR Experiences")

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var onlyVR by remember { mutableStateOf(false) }
    var sortOption by remember { mutableStateOf(SortOption.DEFAULT) }

    // Categories available in this state
    val categories = remember(touristSpots, hiddenSpots) {
        (touristSpots + hiddenSpots).map { it.category }.distinct().sorted()
    }

    // Filtered lists
    val currentPlacesList = when (selectedTab) {
        0 -> touristSpots
        1 -> hiddenSpots
        4 -> (touristSpots + hiddenSpots).filter { it.vrAvailable }
        else -> emptyList()
    }

    val filteredPlaces = currentPlacesList.filter { place ->
        val matchesSearch = place.name.contains(searchQuery, ignoreCase = true) ||
                place.category.contains(searchQuery, ignoreCase = true) ||
                place.codeInPdf.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == null || place.category == selectedCategory
        val matchesVR = !onlyVR || place.vrAvailable

        matchesSearch && matchesCategory && matchesVR
    }.let { list ->
        when (sortOption) {
            SortOption.NAME_ASC -> list.sortedBy { it.name }
            SortOption.NAME_DESC -> list.sortedByDescending { it.name }
            SortOption.DEFAULT -> list
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // State Hero Header with Native Photography
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .background(Color(0xFF0F172A))
        ) {
            AsyncImage(
                model = NativeImageProvider.getStateHeroUrl(state.name),
                contentDescription = state.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Cinematic gradient scrim
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color(0x88000000),
                                Color(0x33000000),
                                Color(0xF0091A2B)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0x66000000), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (onAddNewSpot != null) {
                            OutlinedButton(
                                onClick = { showAddSpotDialog = true },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xAAFFFFFF)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("state_add_spot_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (selectedTab == 1) "+ Hidden Gem" else "+ Spot",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // Package builder CTA button
                        Button(
                            onClick = onStartPackageWithState,
                            colors = ButtonDefaults.buttonColors(containerColor = AmberAccent),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("state_build_trip_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CardTravel,
                                contentDescription = null,
                                tint = NavyDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Build Trip",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyDark
                            )
                        }
                    }
                }

                Column {
                    Text(
                        text = state.name.uppercase(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = Color.White
                    )

                    Text(
                        text = state.description,
                        fontSize = 12.sp,
                        color = Color(0xFFE2E8F0),
                        lineHeight = 16.sp,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Stats strip: 100 Main + 50 Hidden + Season
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(color = Color(0x880F2942), shape = RoundedCornerShape(4.dp)) {
                            Text(
                                text = "${state.totalSpots} Main Spots",
                                fontSize = 11.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Surface(color = AmberContainer, shape = RoundedCornerShape(4.dp)) {
                            Text(
                                text = "💎 ${state.totalHidden} Hidden Spots",
                                fontSize = 11.sp,
                                color = Color(0xFF78350F),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Surface(color = Color(0x88000000), shape = RoundedCornerShape(4.dp)) {
                            Text(
                                text = "🗓️ ${state.bestSeason}",
                                fontSize = 11.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }

        // Section Tabs: [Tour Spots], [Hidden Spots], [Hotels], [Travel], [VR Experiences]
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = NavyPrimary,
            edgePadding = 12.dp
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    modifier = Modifier.testTag("state_tab_$index")
                )
            }
        }

        // Search & Filters for Places (Tabs 0, 1, 4)
        if (selectedTab in listOf(0, 1, 4)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Filter ${state.name} places by name or code...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("state_places_search_field")
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Filter & Sort row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        FilterChip(
                            selected = onlyVR,
                            onClick = { onlyVR = !onlyVR },
                            label = { Text("360° VR Only", fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.ViewInAr, contentDescription = null, modifier = Modifier.size(14.dp)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF2563EB),
                                selectedLabelColor = Color.White
                            )
                        )
                    }

                    item {
                        FilterChip(
                            selected = sortOption != SortOption.DEFAULT,
                            onClick = {
                                sortOption = when (sortOption) {
                                    SortOption.DEFAULT -> SortOption.NAME_ASC
                                    SortOption.NAME_ASC -> SortOption.NAME_DESC
                                    SortOption.NAME_DESC -> SortOption.DEFAULT
                                }
                            },
                            label = {
                                Text(
                                    text = when (sortOption) {
                                        SortOption.NAME_ASC -> "Sort: A to Z"
                                        SortOption.NAME_DESC -> "Sort: Z to A"
                                        else -> "Sort: PDF Order"
                                    },
                                    fontSize = 11.sp
                                )
                            },
                            leadingIcon = { Icon(Icons.Default.Sort, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        )
                    }

                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = {
                                selectedCategory = if (selectedCategory == cat) null else cat
                            },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        // Tab Content
        when (selectedTab) {
            0, 1, 4 -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Showing ${filteredPlaces.size} locations",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (onAddNewSpot != null) {
                                    Surface(
                                        color = if (selectedTab == 1) AmberContainer else MaterialTheme.colorScheme.primaryContainer,
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier
                                            .clickable { showAddSpotDialog = true }
                                            .testTag("state_inline_add_spot_chip")
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = null,
                                                tint = if (selectedTab == 1) Color(0xFF78350F) else MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = if (selectedTab == 1) "+ Hidden Gem" else "+ Tourist Spot",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (selectedTab == 1) Color(0xFF78350F) else MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (filteredPlaces.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = if (onlyVR) "No 360° VR spots matching current filter." else "No locations found matching '$searchQuery'.",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 14.sp
                                    )
                                    if (onAddNewSpot != null) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Button(
                                            onClick = { showAddSpotDialog = true },
                                            colors = ButtonDefaults.buttonColors(containerColor = AmberAccent),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = null, tint = NavyDark, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Add Destination Now", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    items(filteredPlaces) { place ->
                        PlaceCard(
                            place = place,
                            isWishlisted = isPlaceWishlisted(place.id),
                            onPlaceClick = { onPlaceSelect(place.id) },
                            onWishlistToggle = { onWishlistToggle(place) },
                            onAddToTripClick = { onToggleSpotInTrip(place) },
                            isSelectedInTrip = isSpotInTrip(place.id)
                        )
                    }
                }
            }

            2 -> {
                // Hotels Tab
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "Verified Hotels & Stays in ${state.name}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Bookable individually or integrated into Globe Connect trip packages",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    items(hotels) { hotel ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = hotel.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = hotel.type,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Surface(
                                        color = EmeraldContainer,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = null,
                                                tint = Color(0xFF047857),
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = "${hotel.rating}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF047857)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "📍 ${hotel.location}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "✨ ${hotel.amenities}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "₹${hotel.pricePerNight} / night",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Button(
                                        onClick = onStartPackageWithState,
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                    ) {
                                        Text("Add to Package", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            3 -> {
                // Travel Tab
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "Travel & Transit to ${state.name}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Connected via premier rail, air, and highway transit operators",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    items(travelOptions) { travel ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = travel.mode,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = travel.provider,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Text(
                                        text = "₹${travel.price} / person",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Details: ${travel.vehicleDetails}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = onStartPackageWithState,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Select in Trip Builder", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddSpotDialog && onAddNewSpot != null) {
        AddTouristOrHiddenSpotDialog(
            availableStates = if (availableStates.isNotEmpty()) availableStates else listOf(state),
            candidateTouristSpots = touristSpots,
            initialStateId = state.id,
            initialIsHidden = selectedTab == 1,
            onDismiss = { showAddSpotDialog = false },
            onAddSpot = onAddNewSpot
        )
    }
}
