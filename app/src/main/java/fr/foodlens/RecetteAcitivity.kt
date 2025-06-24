package fr.foodlens

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import com.vuzix.hud.actionmenu.ActionMenuActivity
import com.vuzix.sdk.speechrecognitionservice.VuzixSpeechClient
import fr.foodlens.speech.BasicVocabulary
import fr.foodlens.speech.GlobalVoiceCmdReceiver
import fr.foodlens.speech.VoiceSpeechManager

class RecetteAcitivity: ActionMenuActivity() {
    private lateinit var vuzixClient:VuzixSpeechClient
    private lateinit var voiceReceiver: VoiceCmdReceiver
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recettes)

        //Initialisation du client et de l'actionneur
        //On peut faire de même dans toutes les acttivités pour des tâches génériques
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
        VoiceSpeechManager.init(vuzixClient)
        vuzixClient.insertPhrase(BasicVocabulary.SCAN)
        vuzixClient.insertPhrase(BasicVocabulary.RECETTE)

        //On initialise l'actionneur
        voiceReceiver= VoiceCmdReceiver(this)
        val filter= IntentFilter(VuzixSpeechClient.ACTION_VOICE_COMMAND)
        registerReceiver(voiceReceiver,filter)
    }
    /**
     * Nous permet de traiter les commandes
     * qui proviennent de la reconnaissance vocale
     */
    class VoiceCmdReceiver(
        private val activity: RecetteAcitivity,
    ) : GlobalVoiceCmdReceiver(activity){
        //J'ajoute pas custom ici
    }
}

