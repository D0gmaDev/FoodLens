package fr.foodlens

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.vuzix.hud.actionmenu.ActionMenuActivity
import com.vuzix.sdk.speechrecognitionservice.VuzixSpeechClient
import fr.foodlens.fridge.FridgeActivity
import fr.foodlens.recipe.RecipeActivity
import fr.foodlens.shopping.ShoppingActivity
import fr.foodlens.speech.BasicVocabulary
import fr.foodlens.speech.GlobalVoiceCmdReceiver

class MainActivity : ActionMenuActivity() {
    private lateinit var vuzixSpeechClient: VuzixSpeechClient
    private lateinit var voiceReceiver: VoiceReceiver

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

        initializeVuzixSpeechClient()
    }

    private fun initializeVuzixSpeechClient() {
        try {
            vuzixSpeechClient = VuzixSpeechClient(this)
        } catch (e: RuntimeException) {
            if(e.message?.equals("Stub:")?:false){
                Log.d("Vuzix_Speech","Not a Vuzix device")
            } else {
                Log.d("Vuzix_Speech",e.toString())
            }
        }
        // On initialise l'actionneur
        voiceReceiver = VoiceReceiver(this, vuzixSpeechClient)
        val filter = IntentFilter(VuzixSpeechClient.ACTION_VOICE_COMMAND)
        registerReceiver(voiceReceiver, filter)
    }
}

class VoiceReceiver(
    private val activity: ActionMenuActivity,
    private val vuzixSpeechClient: VuzixSpeechClient
) : GlobalVoiceCmdReceiver(activity, vuzixSpeechClient) {

    init {
        initVoiceSpeechClient()
        vuzixSpeechClient.apply {
            insertPhrase(BasicVocabulary.SHOPPING)
            insertPhrase(BasicVocabulary.RECETTE)
            insertPhrase(BasicVocabulary.REFRIGERATOR)
        }
    }
}