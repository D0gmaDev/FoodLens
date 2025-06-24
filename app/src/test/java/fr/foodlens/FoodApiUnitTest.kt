package fr.foodlens

import fr.foodlens.openfoodfacts.FoodApiClient
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FoodApiUnitTest {

    /**
     * This test checks if the FoodApiClient can successfully retrieve a product by its code.
     * It initializes the client with a mock API URL and asserts that the response contains
     * the expected product name.
     *
     * @author David Marembert
     */
    @Test
    fun testGetProductByCode() {
        // Initialize the FoodApiClient with a mock API URL
        FoodApiClient.init("https://world.openfoodfacts.org/")

        // Call the method to get a product by code
        val result = runBlocking { FoodApiClient.getProductByCode("3166296208869") }

        // Assert that the result is successful and contains expected data
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
        assertEquals("Paprika fumé", result.getOrNull()?.name)
    }

}