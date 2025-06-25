package fr.foodlens

import fr.foodlens.openfoodfacts.Product
import fr.foodlens.recipe.OllamaApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RecipeUnitTest {

    /**
     * This test checks if a sensible recipe can be successfully generated using Ollama,
     * given specific ingredients.
     *
     * @author David Marembert
     */
    @Test
    fun testGetRecipeByIngredients() {
        // Initialize the Ollama connection with an API URL
        OllamaApi.init("http://localhost:11434")

        val ingredients = listOf(
            Product(
                id = "1",
                name = "Pomme",
                genericName = null,
                quantity = "2",
                brands = "Marque A",
                imageUrl = "",
                categories = "Fruits",
                nutriScoreGrade = "a",
                keywords = emptyList()
            ),
            Product(
                id = "2",
                name = "Miel",
                genericName = null,
                quantity = "30ml",
                brands = "Marque B",
                imageUrl = "",
                categories = "Sucre",
                nutriScoreGrade = "d",
                keywords = emptyList()
            ),
            Product(
                id = "343",
                name = "Pate feuilletée",
                genericName = null,
                quantity = "1",
                brands = "Marque C",
                imageUrl = "",
                categories = "Pâtisserie",
                nutriScoreGrade = "c",
                keywords = emptyList()
            )
        )

        // Call the method to generate a recipe based on the ingredients
        val result = runBlocking { OllamaApi.generateRecipe(ingredients) }

        // Assert that the result is successful and contains expected data
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
        assert(result.getOrNull()?.title?.isNotEmpty() == true)
        assert(result.getOrNull()?.ingredients?.isNotEmpty() == true)
        assert(result.getOrNull()?.instructions?.isNotEmpty() == true)
    }

}