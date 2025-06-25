package fr.foodlens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import fr.foodlens.fridge.FridgeActivity
import fr.foodlens.recipe.RecipeActivity
import fr.foodlens.shopping.ShoppingActivity
import fr.foodlens.speech.BasicVocabulary

class MainActivity : DefaultActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val shoppingListButton = findViewById<Button>(R.id.shoppingListButton)
        val recipeButton = findViewById<Button>(R.id.recipeButton)
        val fridgeButton = findViewById<Button>(R.id.fridgeButton)

        shoppingListButton.requestFocusFromTouch()

        shoppingListButton.setOnClickListener {
            val intent = Intent(this, ShoppingActivity::class.java)
            startActivity(intent)
        }

        recipeButton.setOnClickListener {
            val intent = Intent(this, RecipeActivity::class.java)
            startActivity(intent)
        }

        fridgeButton.setOnClickListener {
            val intent = Intent(this, FridgeActivity::class.java)
            startActivity(intent)
        }

        vuzixSpeechClient.apply {
            insertPhrase(BasicVocabulary.SHOPPING)
            insertPhrase(BasicVocabulary.RECETTE)
            insertPhrase(BasicVocabulary.REFRIGERATOR)
        }
    }
}