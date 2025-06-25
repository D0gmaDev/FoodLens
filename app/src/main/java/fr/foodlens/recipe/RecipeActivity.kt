package fr.foodlens.recipe

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import fr.foodlens.R
import fr.foodlens.openfoodfacts.Product
import kotlinx.coroutines.launch

class RecipeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recipe)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.recipe)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val loadingBar = findViewById<android.widget.ProgressBar>(R.id.loadingBar)
        val loadingText = findViewById<android.widget.TextView>(R.id.loadingText)
        val recipeText = findViewById<android.widget.TextView>(R.id.recipeText)

        lifecycleScope.launch {

            val produit = """
                - 2 pommes
                - 1 banane
                - 100g de farine
                - 2 oeufs
                - 50g de sucre
            """.trimIndent()

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
            )

            OllamaApi.generateRecipe(ingredients)
                .onSuccess {
                    loadingBar.visibility = View.GONE
                    loadingText.visibility = View.GONE

                    recipeText.visibility = View.VISIBLE
                    recipeText.text = it.title
                }
                .onFailure {
                    recipeText.visibility = View.VISIBLE
                    recipeText.text = it.message
                    Log.e("RecipeActivity", "Error generating recipe: ${it.message}", it)
                }
        }
    }
}