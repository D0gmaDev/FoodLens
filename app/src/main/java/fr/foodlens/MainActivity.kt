package fr.foodlens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.vuzix.hud.actionmenu.ActionMenuActivity

class MainActivity : ActionMenuActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val launchIntentBtn = findViewById<Button>(R.id.launch_intent)
        launchIntentBtn.setOnClickListener {
            startActivity(Intent(this, ActivityIntent::class.java))
        }

        val launchEmbeddedBtn = findViewById<Button>(R.id.launch_embedded)
        launchEmbeddedBtn.setOnClickListener {
            startActivity(Intent(this, EmbeddedActivity::class.java))
        }
    }
}