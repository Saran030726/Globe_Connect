package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AppScreen
import com.example.ui.components.GlobeBottomNavigation
import com.example.ui.components.GlobeTopAppBar
import com.example.ui.screens.AccountAdminScreen
import com.example.ui.screens.BookingSuccessScreen
import com.example.ui.screens.GlobalSearchScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PackageBuilderScreen
import com.example.ui.screens.PlaceDetailScreen
import com.example.ui.screens.StateDetailScreen
import com.example.ui.screens.StatesScreen
import com.example.ui.screens.VRExploreScreen
import com.example.ui.theme.GlobeConnectTheme
import com.example.ui.theme.NavyPrimary
import com.example.ui.viewmodel.GlobeConnectViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GlobeConnectTheme {
                GlobeConnectApp()
            }
        }
    }
}

@Composable
fun GlobeConnectApp(
    viewModel: GlobeConnectViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
    val screenStack = remember { mutableStateListOf(AppScreen.HOME) }

    fun navigateTo(screen: AppScreen) {
        if (currentScreen != screen) {
            screenStack.add(screen)
            currentScreen = screen
        }
    }

    fun navigateBack(): Boolean {
        if (screenStack.size > 1) {
            screenStack.removeAt(screenStack.size - 1)
            currentScreen = screenStack.last()
            return true
        }
        return false
    }

    BackHandler(enabled = screenStack.size > 1) {
        navigateBack()
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            if (currentScreen != AppScreen.HOME && currentScreen != AppScreen.GLOBAL_SEARCH && currentScreen != AppScreen.BOOKING_SUCCESS) {
                val title = when (currentScreen) {
                    AppScreen.HOME -> "GLOBE CONNECT"
                    AppScreen.STATES -> "All 29 States & UT"
                    AppScreen.STATE_DETAIL -> uiState.selectedState?.name ?: "Explore State"
                    AppScreen.PLACE_DETAIL -> uiState.selectedPlace?.name ?: "Destination"
                    AppScreen.TRIP_BUILDER -> "Build Your Trip"
                    AppScreen.VR_EXPLORE -> "360° VR Exploration"
                    AppScreen.ACCOUNT_ADMIN -> if (uiState.userProfile.isAdmin) "Admin Dashboard" else "My Account"
                    else -> "Globe Connect"
                }

                val subtitle = when (currentScreen) {
                    AppScreen.HOME -> "India Tourism Master"
                    AppScreen.STATE_DETAIL -> "Official 150 Places"
                    AppScreen.PLACE_DETAIL -> uiState.selectedPlace?.codeInPdf
                    else -> null
                }

                GlobeTopAppBar(
                    currentScreen = currentScreen,
                    title = title,
                    subtitle = subtitle,
                    isAdmin = uiState.userProfile.isAdmin,
                    onBackClick = if (screenStack.size > 1) { { navigateBack() } } else null,
                    onSearchClick = { navigateTo(AppScreen.GLOBAL_SEARCH) },
                    onAdminToggle = { viewModel.switchUserRole(!uiState.userProfile.isAdmin) }
                )
            }
        },
        bottomBar = {
            if (currentScreen != AppScreen.GLOBAL_SEARCH && currentScreen != AppScreen.BOOKING_SUCCESS) {
                GlobeBottomNavigation(
                    currentScreen = currentScreen,
                    onTabSelected = { tabScreen ->
                        screenStack.clear()
                        screenStack.add(tabScreen)
                        currentScreen = tabScreen
                    },
                    tripItemsCount = uiState.customItineraryDraft.selectedDestinations.size.coerceAtLeast(uiState.packageBuilder.selectedMainSpots.size),
                    isAdmin = uiState.userProfile.isAdmin
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (!uiState.isReady && uiState.states.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = NavyPrimary)
                }
            } else {
                when (currentScreen) {
                    AppScreen.HOME -> {
                        HomeScreen(
                            uiState = uiState,
                            onStateSelect = { stateId ->
                                viewModel.selectState(stateId)
                                navigateTo(AppScreen.STATE_DETAIL)
                            },
                            onPlaceSelect = { placeId ->
                                viewModel.selectPlace(placeId)
                                navigateTo(AppScreen.PLACE_DETAIL)
                            },
                            onSearchClick = { navigateTo(AppScreen.GLOBAL_SEARCH) },
                            onViewAllStatesClick = { navigateTo(AppScreen.STATES) },
                            onStartTripBuilder = {
                                if (uiState.packageBuilder.state == null && uiState.states.isNotEmpty()) {
                                    viewModel.initPackageForState(uiState.states.first())
                                }
                                navigateTo(AppScreen.TRIP_BUILDER)
                            },
                            onVRSelect = { placeId ->
                                viewModel.selectPlace(placeId)
                                navigateTo(AppScreen.VR_EXPLORE)
                            },
                            onWishlistToggle = { place -> viewModel.toggleWishlist(place) },
                            isPlaceWishlisted = { id -> viewModel.isWishlisted(id) },
                            onToggleSpotInTrip = { place -> viewModel.toggleMainSpotInPackage(place) },
                            isSpotInTrip = { id -> viewModel.isSpotInTrip(id) }
                        )
                    }

                    AppScreen.STATES -> {
                        StatesScreen(
                            states = uiState.states,
                            onStateSelect = { stateId ->
                                viewModel.selectState(stateId)
                                navigateTo(AppScreen.STATE_DETAIL)
                            }
                        )
                    }

                    AppScreen.STATE_DETAIL -> {
                        val state = uiState.selectedState ?: uiState.states.firstOrNull()
                        if (state != null) {
                            StateDetailScreen(
                                state = state,
                                touristSpots = uiState.stateTouristSpots,
                                hiddenSpots = uiState.stateHiddenSpots,
                                hotels = uiState.stateHotels,
                                travelOptions = uiState.stateTravelOptions,
                                onPlaceSelect = { placeId ->
                                    viewModel.selectPlace(placeId)
                                    navigateTo(AppScreen.PLACE_DETAIL)
                                },
                                onWishlistToggle = { place -> viewModel.toggleWishlist(place) },
                                isPlaceWishlisted = { id -> viewModel.isWishlisted(id) },
                                onStartPackageWithState = {
                                    viewModel.initPackageForState(state)
                                    navigateTo(AppScreen.TRIP_BUILDER)
                                },
                                onToggleSpotInTrip = { spot ->
                                    if (uiState.packageBuilder.state?.id != state.id) {
                                        viewModel.initPackageForState(state)
                                    }
                                    viewModel.toggleMainSpotInPackage(spot)
                                },
                                isSpotInTrip = { id ->
                                    uiState.packageBuilder.selectedMainSpots.any { it.id == id }
                                },
                                onAddNewSpot = { name, stateId, cat, desc, isHidden, assocId, season, dur, fee, lat, lng, vr, img ->
                                    viewModel.addNewSpot(name, stateId, cat, desc, isHidden, assocId, season, dur, fee, lat, lng, vr, img)
                                },
                                availableStates = uiState.states,
                                onBack = { navigateBack() }
                            )
                        }
                    }

                    AppScreen.PLACE_DETAIL -> {
                        val place = uiState.selectedPlace
                        if (place != null) {
                            PlaceDetailScreen(
                                place = place,
                                associatedHiddenSpots = uiState.selectedPlaceHiddenAssociations,
                                vrExperience = uiState.selectedPlaceVR,
                                isWishlisted = viewModel.isWishlisted(place.id),
                                isInTrip = uiState.packageBuilder.selectedMainSpots.any { it.id == place.id },
                                onWishlistToggle = { viewModel.toggleWishlist(place) },
                                onToggleTrip = {
                                    val st = uiState.states.find { it.id == place.stateId }
                                    if (st != null && uiState.packageBuilder.state?.id != st.id) {
                                        viewModel.initPackageForState(st)
                                    }
                                    if (place.isHidden) {
                                        viewModel.togglePremiumAddOn(place)
                                    } else {
                                        viewModel.toggleMainSpotInPackage(place)
                                    }
                                },
                                onSelectAssociatedPlace = { hiddenId ->
                                    viewModel.selectPlace(hiddenId)
                                    navigateTo(AppScreen.PLACE_DETAIL)
                                },
                                onBack = { navigateBack() },
                                weather = uiState.selectedPlaceWeather,
                                flavors = uiState.selectedPlaceFlavors,
                                isFlavorsLoading = uiState.isFlavorsLoading,
                                onRefreshFlavors = {
                                    viewModel.fetchLocalFlavorsForPlace(place)
                                }
                            )
                        }
                    }

                    AppScreen.TRIP_BUILDER -> {
                        PackageBuilderScreen(
                            packageState = uiState.packageBuilder,
                            customItineraryDraft = uiState.customItineraryDraft,
                            savedItineraries = uiState.savedItineraries,
                            availableStates = uiState.states,
                            stateTouristSpots = uiState.stateTouristSpots,
                            stateHiddenSpots = uiState.stateHiddenSpots,
                            stateHotels = uiState.stateHotels,
                            stateTravelOptions = uiState.stateTravelOptions,
                            onSelectState = { st ->
                                viewModel.selectState(st.id)
                                viewModel.initPackageForState(st)
                            },
                            onToggleDestination = { place -> viewModel.toggleDestinationInDraft(place) },
                            onRemoveDestination = { placeId -> viewModel.removeDestinationFromDraft(placeId) },
                            onSetDraftDates = { start, end, days -> viewModel.setDraftDates(start, end, days) },
                            onSetDraftTitle = { title -> viewModel.setDraftTitle(title) },
                            onSetDraftTravelers = { count -> viewModel.setDraftTravelers(count) },
                            onSetDraftPreferences = { hotel, travel ->
                                viewModel.setDraftHotelType(hotel)
                                viewModel.setDraftTravelMode(travel)
                            },
                            onSetDraftNotes = { notes -> viewModel.setDraftNotes(notes) },
                            onSaveCustomItinerary = { viewModel.saveCurrentItinerary() },
                            onDeleteSavedItinerary = { id -> viewModel.deleteCustomItinerary(id) },
                            onLoadItineraryIntoDraft = { itin -> viewModel.loadItineraryIntoDraft(itin) },
                            onClearDraft = { viewModel.clearDraftItinerary() },
                            onCheckout = { date ->
                                val success = viewModel.completeBooking(date)
                                if (success) {
                                    navigateTo(AppScreen.BOOKING_SUCCESS)
                                }
                            },
                            itineraryWeather = uiState.itineraryWeather,
                            isWeatherLoading = uiState.isWeatherLoading,
                            onRefreshWeather = { viewModel.refreshItineraryWeather() },
                            onAddNewSpot = { name, stateId, cat, desc, isHidden, assocId, season, dur, fee, lat, lng, vr, img ->
                                viewModel.addNewSpot(name, stateId, cat, desc, isHidden, assocId, season, dur, fee, lat, lng, vr, img)
                            }
                        )
                    }

                    AppScreen.VR_EXPLORE -> {
                        VRExploreScreen(
                            experiences = uiState.vrExperiences,
                            onPlaceSelect = { placeId ->
                                viewModel.selectPlace(placeId)
                                navigateTo(AppScreen.PLACE_DETAIL)
                            }
                        )
                    }

                    AppScreen.GLOBAL_SEARCH -> {
                        GlobalSearchScreen(
                            searchQuery = uiState.searchQuery,
                            searchResults = uiState.searchResults,
                            onQueryChange = { q -> viewModel.performSearch(q) },
                            onPlaceSelect = { placeId ->
                                viewModel.selectPlace(placeId)
                                navigateTo(AppScreen.PLACE_DETAIL)
                            },
                            onWishlistToggle = { place -> viewModel.toggleWishlist(place) },
                            isPlaceWishlisted = { id -> viewModel.isWishlisted(id) },
                            onBack = { navigateBack() }
                        )
                    }

                    AppScreen.ACCOUNT_ADMIN -> {
                        AccountAdminScreen(
                            uiState = uiState,
                            onRoleToggle = { isAdmin -> viewModel.switchUserRole(isAdmin) },
                            onPlaceSelect = { placeId ->
                                viewModel.selectPlace(placeId)
                                navigateTo(AppScreen.PLACE_DETAIL)
                            },
                            onAdminUpdatePlace = { p -> viewModel.adminUpdatePlace(p) },
                            onAdminDeletePlace = { p -> viewModel.adminDeletePlace(p) },
                            onAddNewSpot = { name, stateId, cat, desc, isHidden, assocId, season, dur, fee, lat, lng, vr, img ->
                                viewModel.addNewSpot(name, stateId, cat, desc, isHidden, assocId, season, dur, fee, lat, lng, vr, img)
                            }
                        )
                    }

                    AppScreen.BOOKING_SUCCESS -> {
                        val booking = uiState.lastCompletedBooking
                        if (booking != null) {
                            BookingSuccessScreen(
                                booking = booking,
                                onViewTripsClick = {
                                    screenStack.clear()
                                    screenStack.add(AppScreen.ACCOUNT_ADMIN)
                                    currentScreen = AppScreen.ACCOUNT_ADMIN
                                },
                                onPlanAnotherTripClick = {
                                    screenStack.clear()
                                    screenStack.add(AppScreen.HOME)
                                    currentScreen = AppScreen.HOME
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
