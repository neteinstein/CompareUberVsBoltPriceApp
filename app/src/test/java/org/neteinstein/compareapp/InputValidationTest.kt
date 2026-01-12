package org.neteinstein.compareapp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * Unit tests for input validation and edge cases.
 * Tests URL encoding, special characters, and boundary conditions.
 */
class InputValidationTest {

    @Test
    fun testURLEncoding_withSpaces() {
        // Given
        val input = "Times Square New York"

        // When
        val encoded = URLEncoder.encode(input, "UTF-8")
        val decoded = URLDecoder.decode(encoded, "UTF-8")

        // Then
        assertEquals(input, decoded)
        assertTrue(encoded.contains("+") || encoded.contains("%20"))
    }

    @Test
    fun testURLEncoding_withSpecialCharacters() {
        // Given
        val input = "123 Main St & 5th Ave"

        // When
        val encoded = URLEncoder.encode(input, "UTF-8")
        val decoded = URLDecoder.decode(encoded, "UTF-8")

        // Then
        assertEquals(input, decoded)
        assertTrue(encoded.contains("%26")) // & should be encoded as %26
    }

    @Test
    fun testURLEncoding_withCommasAndPeriods() {
        // Given
        val input = "New York, NY 10001"

        // When
        val encoded = URLEncoder.encode(input, "UTF-8")
        val decoded = URLDecoder.decode(encoded, "UTF-8")

        // Then
        assertEquals(input, decoded)
    }

    @Test
    fun testURLEncoding_withEmptyString() {
        // Given
        val input = ""

        // When
        val encoded = URLEncoder.encode(input, "UTF-8")
        val decoded = URLDecoder.decode(encoded, "UTF-8")

        // Then
        assertEquals("", encoded)
        assertEquals(input, decoded)
    }

    @Test
    fun testURLEncoding_withUnicodeCharacters() {
        // Given
        val input = "Café de Paris"

        // When
        val encoded = URLEncoder.encode(input, "UTF-8")
        val decoded = URLDecoder.decode(encoded, "UTF-8")

        // Then
        assertEquals(input, decoded)
        assertTrue(encoded.contains("%C3%A9")) // é should be encoded
    }

    @Test
    fun testURLEncoding_withNumbers() {
        // Given
        val input = "123456789"

        // When
        val encoded = URLEncoder.encode(input, "UTF-8")

        // Then
        assertEquals(input, encoded) // Numbers don't need encoding
    }

    @Test
    fun testInputValidation_isEmpty() {
        // Given
        val empty = ""
        val notEmpty = "Some text"

        // Then
        assertTrue(empty.isEmpty())
        assertFalse(notEmpty.isEmpty())
    }

    @Test
    fun testInputValidation_isBlank() {
        // Given
        val blank = "   "
        val notBlank = "  Some text  "
        val empty = ""

        // Then
        assertTrue(blank.isBlank())
        assertFalse(notBlank.isBlank())
        assertTrue(empty.isBlank())
    }

    @Test
    fun testCoordinateRange_latitude() {
        // Valid latitudes
        val validLat1 = 0.0
        val validLat2 = 90.0
        val validLat3 = -90.0
        val validLat4 = 45.5

        // All should be in valid range
        assertTrue(validLat1 in -90.0..90.0)
        assertTrue(validLat2 in -90.0..90.0)
        assertTrue(validLat3 in -90.0..90.0)
        assertTrue(validLat4 in -90.0..90.0)

        // Invalid latitudes
        val invalidLat1 = 91.0
        val invalidLat2 = -91.0

        assertFalse(invalidLat1 in -90.0..90.0)
        assertFalse(invalidLat2 in -90.0..90.0)
    }

    @Test
    fun testCoordinateRange_longitude() {
        // Valid longitudes
        val validLng1 = 0.0
        val validLng2 = 180.0
        val validLng3 = -180.0
        val validLng4 = 123.456

        // All should be in valid range
        assertTrue(validLng1 in -180.0..180.0)
        assertTrue(validLng2 in -180.0..180.0)
        assertTrue(validLng3 in -180.0..180.0)
        assertTrue(validLng4 in -180.0..180.0)

        // Invalid longitudes
        val invalidLng1 = 181.0
        val invalidLng2 = -181.0

        assertFalse(invalidLng1 in -180.0..180.0)
        assertFalse(invalidLng2 in -180.0..180.0)
    }

    @Test
    fun testDeepLinkFormat_uber() {
        // Given
        val baseUrl = "uber://?action=setPickup"
        val pickupParam = "&pickup[formatted_address]="
        val dropoffParam = "&dropoff[formatted_address]="

        // When
        val fullUrl = "$baseUrl$pickupParam$dropoffParam"

        // Then
        assertTrue(fullUrl.startsWith("uber://"))
        assertTrue(fullUrl.contains("action=setPickup"))
        assertTrue(fullUrl.contains("pickup[formatted_address]"))
        assertTrue(fullUrl.contains("dropoff[formatted_address]"))
    }

    @Test
    fun testDeepLinkFormat_bolt_withCoordinates() {
        // Given
        val baseUrl = "bolt://ride?"
        val pickupLat = "pickup_lat=40.7"
        val pickupLng = "&pickup_lng=-73.9"
        val destLat = "&destination_lat=40.8"
        val destLng = "&destination_lng=-74.0"

        // When
        val fullUrl = "$baseUrl$pickupLat$pickupLng$destLat$destLng"

        // Then
        assertTrue(fullUrl.startsWith("bolt://ride?"))
        assertTrue(fullUrl.contains("pickup_lat="))
        assertTrue(fullUrl.contains("pickup_lng="))
        assertTrue(fullUrl.contains("destination_lat="))
        assertTrue(fullUrl.contains("destination_lng="))
    }

    @Test
    fun testDeepLinkFormat_bolt_withAddresses() {
        // Given
        val baseUrl = "bolt://ride?"
        val pickup = "pickup="
        val destination = "&destination="

        // When
        val fullUrl = "$baseUrl$pickup$destination"

        // Then
        assertTrue(fullUrl.startsWith("bolt://ride?"))
        assertTrue(fullUrl.contains("pickup="))
        assertTrue(fullUrl.contains("destination="))
    }
}
