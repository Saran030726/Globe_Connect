package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.PlaceEntity
import com.example.data.model.StateEntity
import com.example.ui.viewmodel.PackageBuilderState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Globe Connect", appName)
  }

  @Test
  fun `test dynamic package calculation with included and addon hidden spots`() {
    val dummyState = StateEntity(
      id = 1,
      name = "Tamil Nadu",
      slug = "tamil-nadu",
      code = "TN",
      capital = "Chennai",
      bestSeason = "November to March",
      description = "Land of Temples",
      totalSpots = 100,
      totalHidden = 50
    )

    val spot1 = PlaceEntity(
      id = 1,
      stateId = 1,
      stateName = "Tamil Nadu",
      name = "Ooty",
      slug = "ooty",
      category = "Mountains & Valleys",
      codeInPdf = "001"
    )

    val includedHidden = PlaceEntity(
      id = 101,
      stateId = 1,
      stateName = "Tamil Nadu",
      name = "Kothagiri Longwood Shola",
      slug = "kothagiri-longwood-shola",
      category = "Hidden Discovery",
      isHidden = true,
      codeInPdf = "H49",
      associatedTouristSpotId = 1
    )

    val addonHidden = PlaceEntity(
      id = 102,
      stateId = 1,
      stateName = "Tamil Nadu",
      name = "Kothagiri Kodanad Viewpoint",
      slug = "kothagiri-kodanad-viewpoint",
      category = "Hidden Discovery",
      isHidden = true,
      codeInPdf = "H50",
      associatedTouristSpotId = 1
    )

    val packageState = PackageBuilderState(
      state = dummyState,
      selectedMainSpots = listOf(spot1),
      includedHiddenSpots = listOf(includedHidden),
      premiumAddOnHiddenSpots = listOf(addonHidden),
      hotelNights = 2,
      travelersCount = 2
    )

    // Sightseeing: 1 spot * 250 * 2 travelers = 500
    assertEquals(500, packageState.baseSightseeingFee)
    // Addon: 1 spot * 499 * 2 travelers = 998
    assertEquals(998, packageState.addOnHiddenFee)
    // Total subtotal = 1498 (hotel and travel are null)
    assertEquals(1498, packageState.subtotal)
    // Tax = 5% of 1498 = 74
    assertEquals(74, packageState.taxAndFees)
    // Total amount = 1572
    assertEquals(1572, packageState.totalAmount)
  }
}
