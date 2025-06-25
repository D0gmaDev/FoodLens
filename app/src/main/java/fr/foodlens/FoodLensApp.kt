package fr.foodlens

import android.app.Application
import fr.foodlens.database.AppDatabase
import fr.foodlens.openfoodfacts.FoodApiClient

class FoodLensApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppDatabase.getDatabase(this)
        FoodApiClient.init("https://world.openfoodfacts.org/")
    }
}