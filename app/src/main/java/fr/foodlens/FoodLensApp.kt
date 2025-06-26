package fr.foodlens

import android.app.Application
import fr.foodlens.database.AppDatabase
import fr.foodlens.openfoodfacts.FoodApiClient
import fr.foodlens.recipe.OllamaApi

class FoodLensApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppDatabase.getDatabase(this)
        FoodApiClient.init("https://world.openfoodfacts.org/")
        OllamaApi.init("http://10.0.2.2:11434")
    }
}