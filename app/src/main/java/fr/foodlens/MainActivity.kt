package fr.foodlens

import android.content.Intent
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import fr.foodlens.fridge.FridgeActivity
import fr.foodlens.recipe.RecipeActivity
import fr.foodlens.shopping.ShoppingActivity
import android.util.Log
import android.widget.Button
import com.vuzix.hud.actionmenu.ActionMenuActivity
import com.vuzix.sdk.speechrecognitionservice.VuzixSpeechClient

class MainActivity : ActionMenuActivity() {
    private lateinit var vuzixClient: VuzixSpeechClient;
    private lateinit var voiceReceiver: VoiceCmdReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val shoppingListButton = findViewById<Button>(R.id.shoppingListButton)
        val recipeButton = findViewById<Button>(R.id.recipeButton)
        val fridgeButton = findViewById<Button>(R.id.fridgeButton)

        shoppingListButton.requestFocus()

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
    }
}

        //Initialisation du client et de l'actionneur
        try{
            vuzixClient = VuzixSpeechClient(this)
        }catch (e:RuntimeException){
            if(e.message?.equals("Stub:")?:false){
                Log.d("Vuzix_Speech","Not a Vuzix device")
            }
            else{
                Log.d("Vuzix_Speech",e.toString())
            }
        }
        vuzixClient.deleteAllPhrases()
        vuzixClient.insertWakeWordPhrase(VoiceCmdReceiver.WAKE_UP)
        vuzixClient.insertVoiceOffPhrase(VoiceCmdReceiver.SHUT_DOWN)
        vuzixClient.insertPhrase(VoiceCmdReceiver.SCAN)
        vuzixClient.insertPhrase(VoiceCmdReceiver.RECETTE)

        //On initialise l'actionneur
        voiceReceiver= VoiceCmdReceiver(this)
        val filter=IntentFilter(VuzixSpeechClient.ACTION_VOICE_COMMAND)
        registerReceiver(voiceReceiver,filter)

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
    /**
     * Nous permet de traiter les commandes
     * qui proviennent de la reconnaissance vocale

    class VoiceCmdReceiver(
        private val activity: MainActivity,
    ) : GlobalVoiceCmdReceiver(activity) {

    fun onVoiceCommand(phrase: String) {
    when (phrase.lowercase()) {
    "scan externe" -> {
    activity.startActivity(Intent(activity, ActivityIntent::class.java))
    }
    "scan embarqué" -> {
    activity.startActivity(Intent(activity, EmbeddedActivity::class.java))
    }
    "scan le code barre" -> {
    // Exemple : tu peux lancer une activité spécifique ou afficher un log
    Log.d("VoiceCmd", "Commande vocale : scan le code barre")
    activity.startActivity(Intent(activity, ActivityIntent::class.java))
    }
    else -> {
    Log.d("VoiceCmd", "Commande non reconnue : $phrase")
    }
    }
    }
    }
    } */

    class VoiceCmdReceiver(
        private val activity: MainActivity,
    ) : BroadcastReceiver() {
        companion object {
            const val SCAN: String = "scan"
            const val RECETTE: String = "recette"
            const val HOME = "home"
            const val WAKE_UP = "dis vuzix"
            const val SHUT_DOWN = "stop vuziw"
        }

        override fun onReceive(context: Context?, intent: Intent?) {
            val phrase = intent?.getStringExtra(VuzixSpeechClient.PHRASE_STRING_EXTRA) ?: return
            when (phrase.lowercase()) {
                SCAN -> {
                    val intentToChange = Intent(activity, ActivityIntent::class.java)
                    activity.startActivity(intentToChange)
                }

                RECETTE -> {
                    val intentToChange = Intent(activity, RecetteActivity::class.java)
                    activity.startActivity(intentToChange)
                }

                HOME -> {
                    val intentToChange = Intent(activity, MainActivity::class.java)
                    activity.startActivity(intentToChange)
                }

                else -> {
                    Log.d("VoiceCmd", "Commande non reconnue : $phrase")
                }
            }
        }
    }