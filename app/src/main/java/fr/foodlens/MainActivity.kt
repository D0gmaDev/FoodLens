package fr.foodlens

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.speech.tts.Voice
import android.util.Log
import android.widget.Button
import androidx.core.content.PackageManagerCompat.LOG_TAG
import com.vuzix.hud.actionmenu.ActionMenuActivity
import com.vuzix.sdk.speechrecognitionservice.VuzixSpeechClient

class MainActivity : ActionMenuActivity() {
    private lateinit var vuzixClient: VuzixSpeechClient;
    private lateinit var voiceReceiver: VoiceCmdReceiver
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
 */
class VoiceCmdReceiver(
    private val activity: MainActivity,
) : BroadcastReceiver(){
    companion object{
        const val SCAN:String ="scan"
        const val RECETTE:String="recette"
        const val WAKE_UP="dis vuzix"
        const val SHUT_DOWN="stop vuziw"
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        val phrase = intent?.getStringExtra(VuzixSpeechClient.PHRASE_STRING_EXTRA)?:return
        when (phrase.lowercase()){
            SCAN->{
                val intentToChange = Intent(activity.context, ActivityIntent::class.java)
                activity.startActivity(intentToChange)
            }
            RECETTE->{
                val intentToChange = Intent(activity.context, RecetteAcitivity::class.java)
                activity.startActivity(intentToChange)
            }
        }
    }
}