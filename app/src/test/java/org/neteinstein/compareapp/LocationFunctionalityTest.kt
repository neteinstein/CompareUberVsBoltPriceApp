package org.neteinstein.compareapp

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowGeocoder
import android.location.Address
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
@OptIn(ExperimentalCoroutinesApi::class)
class LocationFunctionalityTest {

    private lateinit var activity: MainActivity

    @Before
    fun setup() {
        activity = Robolectric.buildActivity(MainActivity::class.java)
            .create()
            .get()
    }

    @Test
    fun testReverseGeocode_withValidCoordinates() = runTest {
        // Given
        val latitude = 40.758896
        val longitude = -73.985130
        
        val address = Address(Locale.US).apply {
            this.latitude = latitude
            this.longitude = longitude
            setAddressLine(0, "Times Square, New York, NY, USA")
        }
        
        ShadowGeocoder.setFromLocation(latitude, longitude, listOf(address))

        // When
        val result = activity.reverseGeocode(latitude, longitude)

        // Then
        assertNotNull(result)
        assertTrue(result!!.contains("Times Square"))
    }

    @Test
    fun testReverseGeocode_withInvalidCoordinates() = runTest {
        // Given
        val latitude = 999.0
        val longitude = 999.0
        
        ShadowGeocoder.setFromLocation(latitude, longitude, emptyList())

        // When
        val result = activity.reverseGeocode(latitude, longitude)

        // Then
        assertNull(result)
    }

    @Test
    fun testCreateBoltDeepLinkWithCoordinates_successfulGeocodingForDropoff() = runTest {
        // Given
        val pickupCoords = Pair(40.758896, -73.985130)
        val dropoff = "Central Park, New York"
        
        val dropoffAddress = Address(Locale.US).apply {
            latitude = 40.785091
            longitude = -73.968285
        }
        
        ShadowGeocoder.setFromLocationName(dropoff, listOf(dropoffAddress))

        // When
        val deepLink = activity.createBoltDeepLinkWithCoordinates(pickupCoords, dropoff)

        // Then
        assertTrue(deepLink.startsWith("bolt://ride?"))
        assertTrue(deepLink.contains("pickup_lat=${pickupCoords.first}"))
        assertTrue(deepLink.contains("pickup_lng=${pickupCoords.second}"))
        assertTrue(deepLink.contains("destination_lat=${dropoffAddress.latitude}"))
        assertTrue(deepLink.contains("destination_lng=${dropoffAddress.longitude}"))
    }

    @Test
    fun testCreateBoltDeepLinkWithCoordinates_failedGeocodingForDropoff() = runTest {
        // Given
        val pickupCoords = Pair(40.758896, -73.985130)
        val dropoff = "Invalid Address 12345"
        
        ShadowGeocoder.setFromLocationName(dropoff, emptyList())

        // When
        val deepLink = activity.createBoltDeepLinkWithCoordinates(pickupCoords, dropoff)

        // Then
        assertTrue(deepLink.startsWith("bolt://ride?"))
        assertTrue(deepLink.contains("pickup_lat=${pickupCoords.first}"))
        assertTrue(deepLink.contains("pickup_lng=${pickupCoords.second}"))
        // Should fallback to address-based format for destination
        assertTrue(deepLink.contains("destination="))
    }

    @Test
    fun testCreateBoltDeepLinkWithCoordinates_coordinatesFormat() = runTest {
        // Given
        val expectedPickupLat = 12.345678
        val expectedPickupLng = -98.765432
        val pickupCoords = Pair(expectedPickupLat, expectedPickupLng)
        val dropoff = "Test Destination"
        val expectedDestLat = 23.456789
        val expectedDestLng = -87.654321
        
        val dropoffAddress = Address(Locale.US).apply {
            latitude = expectedDestLat
            longitude = expectedDestLng
        }
        
        ShadowGeocoder.setFromLocationName(dropoff, listOf(dropoffAddress))

        // When
        val deepLink = activity.createBoltDeepLinkWithCoordinates(pickupCoords, dropoff)

        // Then
        assertTrue(deepLink.contains("pickup_lat=$expectedPickupLat"))
        assertTrue(deepLink.contains("pickup_lng=$expectedPickupLng"))
        assertTrue(deepLink.contains("destination_lat=$expectedDestLat"))
        assertTrue(deepLink.contains("destination_lng=$expectedDestLng"))
    }
}
