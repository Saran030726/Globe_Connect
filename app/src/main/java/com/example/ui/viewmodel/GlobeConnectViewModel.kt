package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiFlavorsRepository
import com.example.data.db.GlobeConnectDatabase
import com.example.data.model.BookingEntity
import com.example.data.model.CustomItineraryEntity
import com.example.data.model.DestinationFlavors
import com.example.data.model.HotelEntity
import com.example.data.model.PlaceEntity
import com.example.data.model.StateEntity
import com.example.data.model.TravelOptionEntity
import com.example.data.model.VRExperienceEntity
import com.example.data.model.WishlistEntity
import com.example.data.repository.TourismRepository
import com.example.data.weather.DestinationWeather
import com.example.data.weather.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

enum class SortOption {
    DEFAULT,
    NAME_ASC,
    NAME_DESC
}

data class UserProfile(
    val name: String = "Saran",
    val email: String = "saran030726@gmail.com",
    val isAdmin: Boolean = false,
    val phone: String = "+91 98765 43210"
)

data class PackageBuilderState(
    val state: StateEntity? = null,
    val selectedMainSpots: List<PlaceEntity> = emptyList(),
    val includedHiddenSpots: List<PlaceEntity> = emptyList(), // 1-2 free included
    val premiumAddOnHiddenSpots: List<PlaceEntity> = emptyList(), // User added paid
    val selectedHotel: HotelEntity? = null,
    val hotelNights: Int = 2,
    val selectedTravelOption: TravelOptionEntity? = null,
    val travelersCount: Int = 2
) {
    // Dynamic price calculation
    val baseSightseeingFee: Int = selectedMainSpots.size * 250 * travelersCount
    val addOnHiddenFee: Int = premiumAddOnHiddenSpots.size * 499 * travelersCount
    val hotelFee: Int = (selectedHotel?.pricePerNight ?: 0) * hotelNights
    val travelFee: Int = (selectedTravelOption?.price ?: 0) * travelersCount
    val subtotal: Int = baseSightseeingFee + addOnHiddenFee + hotelFee + travelFee
    val taxAndFees: Int = if (subtotal > 0) (subtotal * 0.05).toInt() else 0
    val totalAmount: Int = subtotal + taxAndFees
}

data class CustomItineraryDraftState(
    val title: String = "Grand Indian Adventure",
    val startDate: String = "15 Nov 2026",
    val endDate: String = "22 Nov 2026",
    val durationDays: Int = 7,
    val travelersCount: Int = 2,
    val selectedDestinations: List<PlaceEntity> = emptyList(),
    val hotelType: String = "Heritage Palace & Luxury Resort",
    val travelMode: String = "Vande Bharat & Private SUV",
    val notes: String = "Cultural sightseeing and scenic sunset viewpoints",
    val filterStateId: Int? = null,
    val destinationSearchQuery: String = ""
) {
    val spotPassCost: Int = selectedDestinations.size * 250 * travelersCount
    val hotelNightCost: Int = when (hotelType) {
        "Heritage Palace & Luxury Resort" -> 5500
        "Boutique Heritage Haveli" -> 3800
        "Eco Nature Camp & Glamping" -> 2800
        else -> 2200
    }
    val hotelTotalCost: Int = hotelNightCost * durationDays
    val travelPerPersonCost: Int = when (travelMode) {
        "Flight & Chauffeur SUV" -> 8500
        "Vande Bharat & Private SUV" -> 4500
        "Luxury AC Sleeper Coach" -> 2200
        else -> 3000
    }
    val travelTotalCost: Int = travelPerPersonCost * travelersCount
    val subtotal: Int = spotPassCost + hotelTotalCost + travelTotalCost
    val taxAndFees: Int = if (subtotal > 0) (subtotal * 0.05).toInt() else 0
    val totalEstimatedCost: Int = subtotal + taxAndFees
}

data class GlobeUiState(
    val isReady: Boolean = false,
    val states: List<StateEntity> = emptyList(),
    val selectedState: StateEntity? = null,
    val stateTouristSpots: List<PlaceEntity> = emptyList(),
    val stateHiddenSpots: List<PlaceEntity> = emptyList(),
    val selectedPlace: PlaceEntity? = null,
    val selectedPlaceHiddenAssociations: List<PlaceEntity> = emptyList(),
    val selectedPlaceVR: VRExperienceEntity? = null,
    val stateHotels: List<HotelEntity> = emptyList(),
    val stateTravelOptions: List<TravelOptionEntity> = emptyList(),
    val vrPlaces: List<PlaceEntity> = emptyList(),
    val vrExperiences: List<VRExperienceEntity> = emptyList(),
    val searchQuery: String = "",
    val searchResults: List<PlaceEntity> = emptyList(),
    val categoryFilter: String? = null,
    val seasonFilter: String? = null,
    val onlyVRFilter: Boolean = false,
    val sortOption: SortOption = SortOption.DEFAULT,
    val packageBuilder: PackageBuilderState = PackageBuilderState(),
    val customItineraryDraft: CustomItineraryDraftState = CustomItineraryDraftState(),
    val savedItineraries: List<CustomItineraryEntity> = emptyList(),
    val lastSavedItineraryId: Long? = null,
    val userProfile: UserProfile = UserProfile(),
    val bookingHistory: List<BookingEntity> = emptyList(),
    val wishlist: List<WishlistEntity> = emptyList(),
    val lastCompletedBooking: BookingEntity? = null,
    val importReportSummary: String = "",
    val totalPlacesCount: Int = 4350,
    val itineraryWeather: List<DestinationWeather> = emptyList(),
    val isWeatherLoading: Boolean = false,
    val selectedPlaceWeather: DestinationWeather? = null,
    val selectedPlaceFlavors: DestinationFlavors? = null,
    val isFlavorsLoading: Boolean = false
)

class GlobeConnectViewModel(application: Application) : AndroidViewModel(application) {

    private val database = GlobeConnectDatabase.getInstance(application)
    val repository = TourismRepository(database)
    private val weatherRepository = WeatherRepository()
    private val flavorsRepository = GeminiFlavorsRepository()

    private val _uiState = MutableStateFlow(GlobeUiState())
    val uiState: StateFlow<GlobeUiState> = _uiState.asStateFlow()

    init {
        bootstrapData()
        observeWishlist()
        observeBookings()
        observeStates()
        observeVR()
        observeItineraries()
    }

    private fun bootstrapData() {
        viewModelScope.launch {
            repository.initializeDatabaseIfNeeded(getApplication())
            val count = repository.getTotalPlacesCount()
            val report = repository.getImportReport(getApplication())
            _uiState.update { it.copy(isReady = true, totalPlacesCount = count, importReportSummary = report) }
        }
    }

    private fun observeStates() {
        viewModelScope.launch {
            repository.getAllStates().collectLatest { statesList ->
                _uiState.update { it.copy(states = statesList) }
            }
        }
    }

    private fun observeVR() {
        viewModelScope.launch {
            repository.getVRAvailablePlaces().collectLatest { vrPlacesList ->
                _uiState.update { it.copy(vrPlaces = vrPlacesList) }
            }
        }
        viewModelScope.launch {
            repository.getAllVRExperiences().collectLatest { vrList ->
                _uiState.update { it.copy(vrExperiences = vrList) }
            }
        }
    }

    private fun observeWishlist() {
        viewModelScope.launch {
            repository.getWishlist().collectLatest { list ->
                _uiState.update { it.copy(wishlist = list) }
            }
        }
    }

    private fun observeBookings() {
        viewModelScope.launch {
            repository.getAllBookings().collectLatest { list ->
                _uiState.update { it.copy(bookingHistory = list) }
            }
        }
    }

    fun selectState(stateId: Int) {
        viewModelScope.launch {
            val state = _uiState.value.states.find { it.id == stateId }
            _uiState.update { it.copy(selectedState = state, categoryFilter = null, onlyVRFilter = false) }

            // Observe tourist spots
            repository.getTouristSpotsByState(stateId).collectLatest { spots ->
                _uiState.update { it.copy(stateTouristSpots = spots) }
            }
        }
        viewModelScope.launch {
            repository.getHiddenSpotsByState(stateId).collectLatest { hidden ->
                _uiState.update { it.copy(stateHiddenSpots = hidden) }
            }
        }
        viewModelScope.launch {
            repository.getHotelsByState(stateId).collectLatest { hotels ->
                _uiState.update { it.copy(stateHotels = hotels) }
            }
        }
        viewModelScope.launch {
            repository.getTravelOptionsByState(stateId).collectLatest { options ->
                _uiState.update { it.copy(stateTravelOptions = options) }
            }
        }
    }

    fun selectPlace(placeId: Int) {
        viewModelScope.launch {
            val place = repository.getPlaceByIdDirect(placeId)
            _uiState.update { it.copy(selectedPlace = place) }

            if (place != null) {
                fetchWeatherForPlace(place)
                fetchLocalFlavorsForPlace(place)
                // If it's a tourist spot, fetch associated hidden spots
                repository.getAssociatedHiddenSpots(place.id).collectLatest { hiddenList ->
                    _uiState.update { it.copy(selectedPlaceHiddenAssociations = hiddenList) }
                }
            }
        }
        viewModelScope.launch {
            repository.getVRExperience(placeId).collectLatest { vr ->
                _uiState.update { it.copy(selectedPlaceVR = vr) }
            }
        }
    }

    fun performSearch(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.trim().isEmpty()) {
            _uiState.update { it.copy(searchResults = emptyList()) }
            return
        }
        viewModelScope.launch {
            repository.searchPlaces(query).collectLatest { results ->
                _uiState.update { it.copy(searchResults = results) }
            }
        }
    }

    fun setCategoryFilter(category: String?) {
        _uiState.update { it.copy(categoryFilter = category) }
    }

    fun setSeasonFilter(season: String?) {
        _uiState.update { it.copy(seasonFilter = season) }
    }

    fun toggleVRFilter() {
        _uiState.update { it.copy(onlyVRFilter = !it.onlyVRFilter) }
    }

    fun setSortOption(sort: SortOption) {
        _uiState.update { it.copy(sortOption = sort) }
    }

    fun toggleWishlist(place: PlaceEntity) {
        viewModelScope.launch {
            repository.toggleWishlist(place)
        }
    }

    fun isWishlisted(placeId: Int): Boolean {
        return _uiState.value.wishlist.any { it.placeId == placeId }
    }

    fun isSpotInTrip(placeId: Int): Boolean {
        val pb = _uiState.value.packageBuilder
        return pb.selectedMainSpots.any { it.id == placeId } ||
                pb.includedHiddenSpots.any { it.id == placeId } ||
                pb.premiumAddOnHiddenSpots.any { it.id == placeId }
    }

    // --- Package Builder Actions ---

    fun initPackageForState(state: StateEntity) {
        _uiState.update {
            it.copy(
                packageBuilder = PackageBuilderState(
                    state = state,
                    hotelNights = 2,
                    travelersCount = 2
                )
            )
        }
    }

    fun toggleMainSpotInPackage(spot: PlaceEntity) {
        viewModelScope.launch {
            val currentList = _uiState.value.packageBuilder.selectedMainSpots.toMutableList()
            val includedHidden = _uiState.value.packageBuilder.includedHiddenSpots.toMutableList()

            if (currentList.any { it.id == spot.id }) {
                currentList.removeAll { it.id == spot.id }
                // Remove auto-included hidden spots associated with this spot
                includedHidden.removeAll { it.associatedTouristSpotId == spot.id }
            } else {
                currentList.add(spot)
                // Automatically include 1 or 2 associated hidden spots per prompt specification!
                val associated = repository.getAssociatedHiddenSpotsDirect(spot.id)
                val toAdd = associated.take(2).filter { h -> !includedHidden.any { it.id == h.id } }
                includedHidden.addAll(toAdd)
            }

            _uiState.update {
                it.copy(
                    packageBuilder = it.packageBuilder.copy(
                        selectedMainSpots = currentList,
                        includedHiddenSpots = includedHidden
                    )
                )
            }
        }
    }

    fun togglePremiumAddOn(hiddenSpot: PlaceEntity) {
        val currentAddOns = _uiState.value.packageBuilder.premiumAddOnHiddenSpots.toMutableList()
        if (currentAddOns.any { it.id == hiddenSpot.id }) {
            currentAddOns.removeAll { it.id == hiddenSpot.id }
        } else {
            currentAddOns.add(hiddenSpot)
        }
        _uiState.update {
            it.copy(packageBuilder = it.packageBuilder.copy(premiumAddOnHiddenSpots = currentAddOns))
        }
    }

    fun selectHotelForPackage(hotel: HotelEntity) {
        _uiState.update {
            it.copy(packageBuilder = it.packageBuilder.copy(selectedHotel = hotel))
        }
    }

    fun selectTravelForPackage(travel: TravelOptionEntity) {
        _uiState.update {
            it.copy(packageBuilder = it.packageBuilder.copy(selectedTravelOption = travel))
        }
    }

    fun setHotelNights(nights: Int) {
        _uiState.update {
            it.copy(packageBuilder = it.packageBuilder.copy(hotelNights = nights.coerceIn(1, 14)))
        }
    }

    fun setTravelersCount(count: Int) {
        _uiState.update {
            it.copy(packageBuilder = it.packageBuilder.copy(travelersCount = count.coerceIn(1, 10)))
        }
    }

    fun completeBooking(travelDate: String): Boolean {
        val pkg = _uiState.value.packageBuilder
        if (pkg.state == null || pkg.selectedMainSpots.isEmpty()) return false

        val bookingRef = "GC-" + UUID.randomUUID().toString().substring(0, 8).uppercase()
        val booking = BookingEntity(
            bookingRef = bookingRef,
            stateName = pkg.state.name,
            touristSpotNames = pkg.selectedMainSpots.joinToString(", ") { it.name },
            hiddenSpotNames = (pkg.includedHiddenSpots + pkg.premiumAddOnHiddenSpots).joinToString(", ") { it.name },
            hotelName = pkg.selectedHotel?.name ?: "Self Arranged / No Hotel",
            travelMode = pkg.selectedTravelOption?.mode ?: "Self Arranged",
            totalAmount = pkg.totalAmount,
            travelDate = travelDate,
            travelersCount = pkg.travelersCount
        )

        viewModelScope.launch {
            val id = repository.createBooking(booking)
            val savedBooking = booking.copy(id = id)
            _uiState.update {
                it.copy(
                    lastCompletedBooking = savedBooking,
                    packageBuilder = PackageBuilderState() // Reset builder
                )
            }
        }
        return true
    }

    fun switchUserRole(isAdmin: Boolean) {
        _uiState.update {
            it.copy(
                userProfile = it.userProfile.copy(
                    isAdmin = isAdmin,
                    name = if (isAdmin) "Admin - Globe Connect" else "Saran"
                )
            )
        }
    }

    // Admin Dataset Management
    fun adminUpdatePlace(place: PlaceEntity) {
        viewModelScope.launch {
            repository.adminUpdatePlace(place)
            // Refresh counts
            val count = repository.getTotalPlacesCount()
            _uiState.update { it.copy(totalPlacesCount = count) }
        }
    }

    fun adminDeletePlace(place: PlaceEntity) {
        viewModelScope.launch {
            repository.adminDeletePlace(place)
            val count = repository.getTotalPlacesCount()
            _uiState.update { it.copy(totalPlacesCount = count) }
        }
    }

    fun addNewSpot(
        name: String,
        stateId: Int,
        category: String,
        description: String,
        isHidden: Boolean,
        associatedTouristSpotId: Int? = null,
        bestSeason: String = "October to March",
        duration: String = "2 - 3 hours",
        entryFee: String = "Standard Entry",
        latitude: Double? = null,
        longitude: Double? = null,
        vrAvailable: Boolean = false,
        imageUrl: String? = null
    ) {
        viewModelScope.launch {
            val state = _uiState.value.states.find { it.id == stateId }
            val stateName = state?.name ?: "India"
            val nextId = repository.getMaxPlaceId() + 1
            val slug = name.lowercase().trim().replace(Regex("[^a-z0-9]+"), "-")
            val codeInPdf = (if (isHidden) "H-" else "T-") + String.format(java.util.Locale.US, "%04d", nextId)

            val newPlace = PlaceEntity(
                id = nextId,
                stateId = stateId,
                stateName = stateName,
                districtId = null,
                cityId = null,
                name = name.trim(),
                slug = slug,
                description = description.ifBlank { "Detailed information is being updated." },
                category = category,
                imageUrl = imageUrl,
                latitude = latitude,
                longitude = longitude,
                bestSeason = bestSeason,
                duration = duration,
                entryFee = entryFee,
                vrAvailable = vrAvailable,
                isHidden = isHidden,
                codeInPdf = codeInPdf,
                source = "USER_CONTRIBUTED",
                associatedTouristSpotId = if (isHidden) associatedTouristSpotId else null
            )

            repository.adminInsertPlace(newPlace)
            val count = repository.getTotalPlacesCount()
            _uiState.update { it.copy(totalPlacesCount = count) }

            // If the currently selected state matches, refresh state places
            if (_uiState.value.selectedState?.id == stateId) {
                selectState(stateId)
            }
        }
    }

    private fun observeItineraries() {
        viewModelScope.launch {
            repository.getAllCustomItineraries().collectLatest { list ->
                _uiState.update { it.copy(savedItineraries = list) }
            }
        }
    }

    // --- State-Managed Multi-Destination Itinerary Actions ---

    fun toggleDestinationInDraft(place: PlaceEntity) {
        val current = _uiState.value.customItineraryDraft.selectedDestinations.toMutableList()
        if (current.any { it.id == place.id }) {
            current.removeAll { it.id == place.id }
        } else {
            current.add(place)
        }
        _uiState.update {
            it.copy(customItineraryDraft = it.customItineraryDraft.copy(selectedDestinations = current))
        }
        refreshItineraryWeather()
    }

    fun removeDestinationFromDraft(placeId: Int) {
        val current = _uiState.value.customItineraryDraft.selectedDestinations.filter { it.id != placeId }
        _uiState.update {
            it.copy(customItineraryDraft = it.customItineraryDraft.copy(selectedDestinations = current))
        }
        refreshItineraryWeather()
    }

    fun setDraftDates(startDate: String, endDate: String, days: Int) {
        _uiState.update {
            it.copy(customItineraryDraft = it.customItineraryDraft.copy(
                startDate = startDate,
                endDate = endDate,
                durationDays = days.coerceAtLeast(1)
            ))
        }
    }

    fun setDraftTitle(title: String) {
        _uiState.update {
            it.copy(customItineraryDraft = it.customItineraryDraft.copy(title = title))
        }
    }

    fun setDraftTravelers(count: Int) {
        _uiState.update {
            it.copy(customItineraryDraft = it.customItineraryDraft.copy(travelersCount = count.coerceAtLeast(1)))
        }
    }

    fun setDraftHotelType(hotelType: String) {
        _uiState.update {
            it.copy(customItineraryDraft = it.customItineraryDraft.copy(hotelType = hotelType))
        }
    }

    fun setDraftTravelMode(travelMode: String) {
        _uiState.update {
            it.copy(customItineraryDraft = it.customItineraryDraft.copy(travelMode = travelMode))
        }
    }

    fun setDraftNotes(notes: String) {
        _uiState.update {
            it.copy(customItineraryDraft = it.customItineraryDraft.copy(notes = notes))
        }
    }

    fun setDraftFilterState(stateId: Int?) {
        _uiState.update {
            it.copy(customItineraryDraft = it.customItineraryDraft.copy(filterStateId = stateId))
        }
    }

    fun setDraftSearchQuery(query: String) {
        _uiState.update {
            it.copy(customItineraryDraft = it.customItineraryDraft.copy(destinationSearchQuery = query))
        }
    }

    fun clearDraftItinerary() {
        _uiState.update {
            it.copy(
                customItineraryDraft = CustomItineraryDraftState(),
                itineraryWeather = emptyList(),
                isWeatherLoading = false
            )
        }
    }

    fun saveCurrentItinerary(): Long {
        val draft = _uiState.value.customItineraryDraft
        val destNames = if (draft.selectedDestinations.isNotEmpty()) {
            draft.selectedDestinations.joinToString(", ") { it.name }
        } else {
            "Classic Indian Heritage Circuit"
        }
        val destIds = draft.selectedDestinations.joinToString(",") { it.id.toString() }
        val statesVisited = draft.selectedDestinations.map { it.stateName }.distinct().joinToString(", ")
            .ifEmpty { "All-India Discovery" }

        val entity = CustomItineraryEntity(
            title = draft.title.ifBlank { "Custom India Tour (${draft.durationDays} Days)" },
            startDate = draft.startDate,
            endDate = draft.endDate,
            durationDays = draft.durationDays,
            travelersCount = draft.travelersCount,
            destinationNames = destNames,
            destinationIds = destIds,
            statesVisited = statesVisited,
            totalCost = draft.totalEstimatedCost,
            hotelType = draft.hotelType,
            travelMode = draft.travelMode,
            notes = draft.notes,
            status = "SAVED"
        )

        var newId: Long = 0
        viewModelScope.launch {
            newId = repository.saveCustomItinerary(entity)
            _uiState.update { it.copy(lastSavedItineraryId = newId) }
        }
        return newId
    }

    fun deleteCustomItinerary(id: Long) {
        viewModelScope.launch {
            repository.deleteCustomItinerary(id)
        }
    }

    fun loadItineraryIntoDraft(itinerary: CustomItineraryEntity) {
        viewModelScope.launch {
            val placeIds = itinerary.destinationIds.split(",").mapNotNull { it.trim().toIntOrNull() }
            val places = placeIds.mapNotNull { repository.getPlaceByIdDirect(it) }
            _uiState.update {
                it.copy(
                    customItineraryDraft = CustomItineraryDraftState(
                        title = itinerary.title,
                        startDate = itinerary.startDate,
                        endDate = itinerary.endDate,
                        durationDays = itinerary.durationDays,
                        travelersCount = itinerary.travelersCount,
                        selectedDestinations = places,
                        hotelType = itinerary.hotelType,
                        travelMode = itinerary.travelMode,
                        notes = itinerary.notes
                    )
                )
            }
            refreshItineraryWeather()
        }
    }

    // --- Weather Integration (External API) ---

    fun refreshItineraryWeather() {
        val destinations = _uiState.value.customItineraryDraft.selectedDestinations
        if (destinations.isEmpty()) {
            _uiState.update { it.copy(itineraryWeather = emptyList(), isWeatherLoading = false) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isWeatherLoading = true) }
            val weather = weatherRepository.getWeatherForDestinations(destinations)
            _uiState.update { it.copy(itineraryWeather = weather, isWeatherLoading = false) }
        }
    }

    fun fetchWeatherForPlace(place: PlaceEntity) {
        viewModelScope.launch {
            val weather = weatherRepository.getWeatherForPlace(place)
            _uiState.update { it.copy(selectedPlaceWeather = weather) }
        }
    }

    // --- Local Flavors Discovery (Gemini API) ---

    fun fetchLocalFlavorsForPlace(place: PlaceEntity) {
        viewModelScope.launch {
            _uiState.update { it.copy(isFlavorsLoading = true) }
            val flavors = flavorsRepository.getLocalFlavors(
                placeName = place.name,
                stateName = place.stateName,
                category = place.category
            )
            _uiState.update { it.copy(selectedPlaceFlavors = flavors, isFlavorsLoading = false) }
        }
    }
}
