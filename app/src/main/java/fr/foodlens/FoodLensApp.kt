package fr.foodlens

import android.app.Application
import fr.foodlens.database.AppDatabase

class FoodLensApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppDatabase.getDatabase(this)
    }
}