package fr.foodlens.recipe

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import fr.foodlens.DefaultActivity
import fr.foodlens.R
import fr.foodlens.database.AppDatabase
import kotlinx.coroutines.launch

class RecipeActivity : DefaultActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recipe)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.recipe)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lifecycleScope.launch {

            val ingredients = AppDatabase.getDatabase(this@RecipeActivity).fridgeItemDao().getAll()

            OllamaApi.generateRecipe(ingredients.map { it.toString() })
                .onSuccess { recipe ->
                    setContentView(R.layout.activity_recipe_result)

                    val title = findViewById<TextView>(R.id.recipeTitle)
                    title.text = recipe.title

                    val ingredients = findViewById<TextView>(R.id.listIngredients)
                    ingredients.text = recipe.ingredients.joinToString("\n") { "- $it" }

                    val instructions = findViewById<TextView>(R.id.listInstructions)
                    instructions.text = recipe.instructions
                        .mapIndexed { i, instruction -> "${i + 1}) $instruction" }
                        .joinToString("\n \n")

                }
                .onFailure {
                    Toast.makeText(this@RecipeActivity, "Erreur : $it", Toast.LENGTH_LONG).show()
                    Log.e("RecipeActivity", "Error generating recipe: ${it.message}", it)
                }
        }
    }
}