package com.example

import com.example.data.model.CustomItineraryEntity
import com.example.data.model.PlaceEntity
import com.example.ui.viewmodel.CustomItineraryDraftState
import org.junit.Assert.*
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Unit tests validating Package Builder duration, date range validation, and Room persistence model.
 */
class ExampleUnitTest {

  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun `validate date range calculation where start date is strictly before end date`() {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.US)
    val startStr = "15 Nov 2026"
    val endStr = "22 Nov 2026"

    val startDate: Date = sdf.parse(startStr)!!
    val endDate: Date = sdf.parse(endStr)!!

    // Validation: Start date must be strictly before end date
    assertTrue("Start date must be before end date", startDate.before(endDate))
    assertFalse("End date cannot be before start date", endDate.before(startDate))

    // Duration calculation in days
    val diffMillis = endDate.time - startDate.time
    val durationDays = (diffMillis / (1000 * 60 * 60 * 24)).toInt()
    assertEquals(7, durationDays)
  }

  @Test
  fun `invalidate date range where start date is equal to or after end date`() {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.US)
    val startStr = "22 Nov 2026"
    val endStr = "15 Nov 2026"

    val startDate: Date = sdf.parse(startStr)!!
    val endDate: Date = sdf.parse(endStr)!!

    // Must fail validation
    val isValid = startDate.before(endDate)
    assertFalse("Invalid date range must be detected", isValid)
  }

  @Test
  fun `test custom itinerary draft state calculations`() {
    val spot1 = PlaceEntity(
      id = 1,
      stateId = 1,
      stateName = "Rajasthan",
      name = "Amber Palace",
      slug = "amber-palace",
      category = "Heritage Fort",
      codeInPdf = "RJ01"
    )
    val spot2 = PlaceEntity(
      id = 2,
      stateId = 1,
      stateName = "Rajasthan",
      name = "Hawa Mahal",
      slug = "hawa-mahal",
      category = "Palace & Heritage",
      codeInPdf = "RJ02"
    )

    val draft = CustomItineraryDraftState(
      title = "Rajasthan Heritage Odyssey",
      startDate = "10 Dec 2026",
      endDate = "15 Dec 2026",
      durationDays = 5,
      travelersCount = 2,
      selectedDestinations = listOf(spot1, spot2),
      hotelType = "Boutique Heritage Haveli",
      travelMode = "Vande Bharat & Private SUV"
    )

    // 2 spots * 250 fee * 2 travelers = 1000
    assertEquals(1000, draft.spotPassCost)
    // 5 days * 3800 hotel = 19000
    assertEquals(19000, draft.hotelTotalCost)
    // 2 travelers * 4500 travel = 9000
    assertEquals(9000, draft.travelTotalCost)

    // Subtotal = 1000 + 19000 + 9000 = 29000
    assertEquals(29000, draft.subtotal)
    // Tax 5% = 1450
    assertEquals(1450, draft.taxAndFees)
    // Total = 30450
    assertEquals(30450, draft.totalEstimatedCost)
  }

  @Test
  fun `test custom itinerary entity Room entity mapping`() {
    val entity = CustomItineraryEntity(
      id = 42L,
      title = "South India Coastal Explorer",
      startDate = "01 Jan 2027",
      endDate = "08 Jan 2027",
      durationDays = 7,
      travelersCount = 3,
      destinationIds = "101,102,105",
      destinationNames = "Varkala Cliff Beach, Munnar Tea Valley, Fort Kochi",
      statesVisited = "Kerala",
      hotelType = "Luxury Resort",
      travelMode = "Private Van",
      totalCost = 45000,
      notes = "Photography tour with family"
    )

    assertEquals(42L, entity.id)
    assertEquals(7, entity.durationDays)
    val ids = entity.destinationIds.split(",").map { it.trim().toInt() }
    assertEquals(3, ids.size)
    assertEquals(listOf(101, 102, 105), ids)
  }

  @Test
  fun `test weather code interpreter WMO mapping`() {
    val clear = com.example.data.weather.WeatherCodeInterpreter.interpret(0)
    assertEquals("Clear Sky", clear.title)
    assertEquals("☀️", clear.emoji)

    val rain = com.example.data.weather.WeatherCodeInterpreter.interpret(63)
    assertEquals("Moderate Rain", rain.title)
    assertEquals("🌧️", rain.emoji)

    val storm = com.example.data.weather.WeatherCodeInterpreter.interpret(95)
    assertEquals("Thunderstorm", storm.title)
    assertEquals("⛈️", storm.emoji)
  }

  @Test
  fun `test weather fallback generation for destinations`() {
    val fallback = com.example.data.weather.WeatherCodeInterpreter.createFallbackWeather(
      placeId = 1,
      placeName = "Taj Mahal",
      stateName = "Uttar Pradesh",
      latitude = 27.1751,
      longitude = 78.0421
    )

    assertEquals(1, fallback.placeId)
    assertEquals("Taj Mahal", fallback.placeName)
    assertTrue("Temperature should be non-zero", fallback.temperatureCelsius > 0)
    assertEquals(7, fallback.dailyForecast.size)
    assertEquals("Today", fallback.dailyForecast[0].dayOfWeek)
  }

  @Test
  fun `test state coordinates lookup`() {
    val (latRJ, lonRJ) = com.example.data.weather.WeatherCodeInterpreter.getStateCoordinates("Rajasthan")
    assertTrue(latRJ > 20.0)
    assertTrue(lonRJ > 70.0)

    val (latKL, lonKL) = com.example.data.weather.WeatherCodeInterpreter.getStateCoordinates("Kerala")
    assertTrue(latKL < 12.0)
  }

  @Test
  fun `test local flavors regional catalog for Rajasthan`() {
    val repo = com.example.data.api.GeminiFlavorsRepository()
    val flavors = repo.getRegionalFallbackFlavors(placeName = "Amber Palace", stateName = "Rajasthan")

    assertEquals("Amber Palace", flavors.placeName)
    assertEquals("Rajasthan", flavors.stateName)
    assertTrue(flavors.regionCuisineTitle.contains("Marwari", ignoreCase = true))
    assertTrue(flavors.signatureDishes.isNotEmpty())

    val dalBaati = flavors.signatureDishes.find { it.name.contains("Dal Baati", ignoreCase = true) }
    assertNotNull(dalBaati)
    assertTrue("Dal Baati should be vegetarian", dalBaati!!.isVegetarian)
    assertTrue("Should contain signature ingredients", dalBaati.signatureIngredients.isNotEmpty())
  }

  @Test
  fun `test local flavors regional catalog for Tamil Nadu and Madurai`() {
    val repo = com.example.data.api.GeminiFlavorsRepository()
    val flavors = repo.getRegionalFallbackFlavors(placeName = "Meenakshi Amman Temple", stateName = "Tamil Nadu")

    assertEquals("Tamil Nadu", flavors.stateName)
    assertTrue(flavors.signatureDishes.any { it.name.contains("Jigarthanda", ignoreCase = true) })
    assertTrue(flavors.signatureDishes.any { it.name.contains("Filter", ignoreCase = true) })
    assertTrue(flavors.streetFoodHotspots.isNotEmpty())
  }

  @Test
  fun `test creation of custom tourist spot and hidden gem entities`() {
    val touristSpot = PlaceEntity(
      id = 4351,
      stateId = 1,
      stateName = "Andhra Pradesh",
      name = "Gandikota Grand Gorge Viewpoint",
      slug = "gandikota-grand-gorge-viewpoint",
      description = "Spectacular sunrise point overlooking the Pennar river gorge.",
      category = "Major Attraction",
      vrAvailable = true,
      isHidden = false,
      codeInPdf = "T-4351",
      source = "USER_CONTRIBUTED"
    )

    assertEquals(4351, touristSpot.id)
    assertFalse(touristSpot.isHidden)
    assertEquals("T-4351", touristSpot.codeInPdf)
    assertEquals("USER_CONTRIBUTED", touristSpot.source)
    assertTrue(touristSpot.vrAvailable)

    // Hidden gem associated with the tourist spot
    val hiddenGem = PlaceEntity(
      id = 4352,
      stateId = 1,
      stateName = "Andhra Pradesh",
      name = "Mylavaram Secret Windmill Cliffs",
      slug = "mylavaram-secret-windmill-cliffs",
      description = "Hidden tranquil cliff edge with panoramic lake views and zero crowds.",
      category = "Mountains & Valleys",
      vrAvailable = false,
      isHidden = true,
      codeInPdf = "H-4352",
      source = "USER_CONTRIBUTED",
      associatedTouristSpotId = touristSpot.id
    )

    assertEquals(4352, hiddenGem.id)
    assertTrue(hiddenGem.isHidden)
    assertEquals("H-4352", hiddenGem.codeInPdf)
    assertEquals(touristSpot.id, hiddenGem.associatedTouristSpotId)
    assertEquals("USER_CONTRIBUTED", hiddenGem.source)
  }
}


