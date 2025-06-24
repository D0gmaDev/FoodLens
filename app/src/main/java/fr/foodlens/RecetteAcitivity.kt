package fr.foodlens

import android.os.Bundle
import android.os.PersistableBundle
import com.vuzix.hud.actionmenu.ActionMenuActivity

class RecetteAcitivity: ActionMenuActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recettes)
    }
}