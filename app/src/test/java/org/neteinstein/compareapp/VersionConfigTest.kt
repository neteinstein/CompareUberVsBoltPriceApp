package org.neteinstein.compareapp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for version configuration.
 * Validates that the version follows the MAJOR.MINOR.PATCH format.
 */
class VersionConfigTest {

    @Test
    fun testVersionName_followsSemanticVersioning() {
        // Given
        val versionName = BuildConfig.VERSION_NAME

        // Then - Version should follow MAJOR.MINOR.PATCH format (e.g., "1.0.0")
        val parts = versionName.split(".")
        assertTrue("Version should have at least 3 parts (MAJOR.MINOR.PATCH)", parts.size >= 3)
        
        // Verify each part is a number
        parts.take(3).forEach { part ->
            assertTrue("Each version part should be a number: $part", part.toIntOrNull() != null)
        }
    }

    @Test
    fun testVersionCode_isPositiveInteger() {
        // Given
        val versionCode = BuildConfig.VERSION_CODE

        // Then - Version code should be a positive integer
        assertTrue("Version code should be positive", versionCode > 0)
    }

    @Test
    fun testVersionName_format() {
        // Given
        val versionName = BuildConfig.VERSION_NAME

        // Then - Should match semantic versioning pattern
        val semanticVersionPattern = Regex("""^\d+\.\d+\.\d+$""")
        assertTrue(
            "Version name should follow MAJOR.MINOR.PATCH format (e.g., 1.0.0)",
            semanticVersionPattern.matches(versionName)
        )
    }
}
