package fr.foodlens.speech

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.vuzix.hud.actionmenu.ActionMenuActivity
import com.vuzix.sdk.speechrecognitionservice.VuzixSpeechClient
import fr.foodlens.MainActivity
import fr.foodlens.fridge.FridgeActivity
import fr.foodlens.recipe.RecipeActivity
import fr.foodlens.shopping.ShoppingActivity

open class GlobalVoiceCmdReceiver (
    private val activity: ActionMenuActivity,
    private val vuzixSpeechClient: VuzixSpeechClient,
) : BroadcastReceiver(){

    init {
        initVoiceSpeechClient()
    }

    open fun initVoiceSpeechClient() {
        vuzixSpeechClient.apply {
            deleteAllPhrases()
            insertWakeWordPhrase(BasicVocabulary.WAKE_UP)
            insertVoiceOffPhrase(BasicVocabulary.SHUT_DOWN)
            insertPhrase(BasicVocabulary.HOME)
            insertPhrase(BasicVocabulary.GENERATION)
            insertPhrase(BasicVocabulary.DE)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val phrase = intent?.getStringExtra(VuzixSpeechClient.PHRASE_STRING_EXTRA) ?: return
        Log.d("global_receiver", "phrase reçue: $phrase")
        when (phrase.lowercase()){
            BasicVocabulary.SHOPPING -> {
                Log.d("global_receiver", "Phrase shopping reconnue")
                startActivity(ShoppingActivity::class.java)
            }
            BasicVocabulary.RECETTE->{
                Log.d("global_receiver", "Phrase recette reconnue")
                startActivity(RecipeActivity::class.java)
            }
            BasicVocabulary.HOME->{
                Log.d("global_receiver", "Phrase home reconnue")
                // Retour à l'écran d'accueil et suppression de l'historique
                Intent(activity, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                }.also { activity.startActivity(it) }
            }

            BasicVocabulary.REFRIGERATOR -> {
                Log.d("global_receiver", "Phrase frigo reconnue")
                startActivity(FridgeActivity::class.java)
            }

            else -> {
                Log.d("global_receiver", "Phrase non prise en charge : $phrase")
                Toast.makeText(activity, "Phrase non prise en charge : $phrase", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    protected fun startActivity(classToChange: Class<out Activity>){
        val intentToChange = Intent(activity, classToChange)
        activity.startActivity(intentToChange)
    }
}

object BasicVocabulary{
    const val WAKE_UP = "Hey FoodLens"
    const val SHUT_DOWN = "Au revoir FoodLens"
    const val SHOPPING = "courses"
    const val RECETTE="recette"
    const val HOME="home"
    const val GENERATION="generation"
    const val DE="de"
    const val REFRIGERATOR = "frigo"
}