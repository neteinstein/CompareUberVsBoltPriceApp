package org.neteinstein.compareapp

import android.location.Address
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowGeocoder

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
@OptIn(ExperimentalCoroutinesApi::class)
class GeocodingTest {

    private lateinit var activity: MainActivity

    @Before
    fun setup() {
        activity = Robolectric.buildActivity(MainActivity::class.java)
            .create()
            .get()
    }

    @Test
    fun testGeocodeAddress_withValidAddress() = runTest {
        // Given
        val address = "Times Square, New York"
        val expectedLat = 40.758896
        val expectedLng = -73.985130
        
        val mockAddress = Address(java.util.Locale.US).apply {
            latitude = expectedLat
            longitude = expectedLng
        }
        ShadowGeocoder.setFromLocationName(address, listOf(mockAddress))

        // When
        val result = activity.geocodeAddress(address)

        // Then
        assertNotNull(result)
        assertEquals(expectedLat, result?.first, 0.000001)
        assertEquals(expectedLng, result?.second, 0.000001)
    }

    @Test
    fun testGeocodeAddress_withInvalidAddress() = runTest {
        // Given
        val address = "Invalid Address 12345 XYZABC"
        ShadowGeocoder.setFromLocationName(address, emptyList())

        // When
        val result = activity.geocodeAddress(address)

        // Then
        assertNull(result)
    }

    @Test
    fun testGeocodeAddress_withNullResult() = runTest {
        // Given
        val address = "Some Address"
        ShadowGeocoder.setFromLocationName(address, null)

        // When
        val result = activity.geocodeAddress(address)

        // Then
        assertNull(result)
    }

    @Test
    fun testGeocodeAddress_withMultipleResults() = runTest {
        // Given
        val address = "Springfield"
        val expectedLat = 37.208889
        val expectedLng = -93.292778
        
        val address1 = Address(java.util.Locale.US).apply {
            latitude = expectedLat
            longitude = expectedLng
        }
        val address2 = Address(java.util.Locale.US).apply {
            latitude = 39.799167
            longitude = -89.643889
        }
        ShadowGeocoder.setFromLocationName(address, listOf(address1, address2))

        // When
        val result = activity.geocodeAddress(address)

        // Then
        assertNotNull(result)
        // Should return the first result
        assertEquals(expectedLat, result?.first, 0.000001)
        assertEquals(expectedLng, result?.second, 0.000001)
    }

    @Test
    fun testGeocodeAddress_withEmptyString() = runTest {
        // Given
        val address = ""
        ShadowGeocoder.setFromLocationName(address, emptyList())

        // When
        val result = activity.geocodeAddress(address)

        // Then
        assertNull(result)
    }

    @Test
    fun testGeocodeAddress_withSpecialCharacters() = runTest {
        // Given
        val address = "123 Main St & 5th Ave, New York, NY"
        val expectedLat = 40.741895
        val expectedLng = -73.989308
        
        val mockAddress = Address(java.util.Locale.US).apply {
            latitude = expectedLat
            longitude = expectedLng
        }
        ShadowGeocoder.setFromLocationName(address, listOf(mockAddress))

        // When
        val result = activity.geocodeAddress(address)

        // Then
        assertNotNull(result)
        assertEquals(expectedLat, result?.first, 0.000001)
        assertEquals(expectedLng, result?.second, 0.000001)
    }

    @Test
    fun testGeocodeAddress_withInternationalAddress() = runTest {
        // Given
        val address = "Eiffel Tower, Paris, France"
        val expectedLat = 48.858370
        val expectedLng = 2.294481
        
        val mockAddress = Address(java.util.Locale.US).apply {
            latitude = expectedLat
            longitude = expectedLng
        }
        ShadowGeocoder.setFromLocationName(address, listOf(mockAddress))

        // When
        val result = activity.geocodeAddress(address)

        // Then
        assertNotNull(result)
        assertEquals(expectedLat, result?.first, 0.000001)
        assertEquals(expectedLng, result?.second, 0.000001)
    }
}
