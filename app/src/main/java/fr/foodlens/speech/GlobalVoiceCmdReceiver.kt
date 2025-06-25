package fr.foodlens.speech

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.vuzix.hud.actionmenu.ActionMenuActivity
import com.vuzix.sdk.speechrecognitionservice.VuzixSpeechClient
import fr.foodlens.ActivityIntent
import fr.foodlens.MainActivity
import android.util.Log
import fr.foodlens.RecetteAcitivity

open class GlobalVoiceCmdReceiver (
    private val activity: ActionMenuActivity,
) : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        val phrase = intent?.getStringExtra(VuzixSpeechClient.PHRASE_STRING_EXTRA)?:return
        Log.d("global_receiver","phrase reçue: $phrase")
        when (phrase.lowercase()){
            BasicVocabulary.SCAN->{
                startActivity(ActivityIntent::class.java)
            }
            BasicVocabulary.RECETTE->{
                startActivity(RecetteAcitivity::class.java)
            }
            BasicVocabulary.HOME->{
                startActivity(MainActivity::class.java)
            }
        }
    }

    //Va nous servir à traiter les cas non génériques
    open fun handleCustomPhrase(phrase:String, context:Context){}

    protected fun startActivity(classToChange: Class<out Activity>){
        val intentToChange = Intent(activity, classToChange)
        activity.startActivity(intentToChange)
    }
}

/**
 * Sert à initialiser tout les mots basiques contenus par nos speechClient génériques
 */
object VoiceSpeechManager{
    fun init(voiceSpeechClient: VuzixSpeechClient){
        voiceSpeechClient.deleteAllPhrases()
        voiceSpeechClient.insertWakeWordPhrase(BasicVocabulary.WAKE_UP)
        voiceSpeechClient.insertVoiceOffPhrase(BasicVocabulary.SHUT_DOWN)
        voiceSpeechClient.insertPhrase(BasicVocabulary.HOME)
        voiceSpeechClient.insertPhrase(BasicVocabulary.GENERATION)
        voiceSpeechClient.insertPhrase(BasicVocabulary.DE)
    }
}

object BasicVocabulary{
    const val WAKE_UP="dis vuzix"
    const val SHUT_DOWN="stop vuziw"
    const val SCAN="scan"
    const val RECETTE="recette"
    const val HOME="home"
    const val GENERATION="generation"
    const val DE="de"
}