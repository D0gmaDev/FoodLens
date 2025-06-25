package fr.foodlens

import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import com.vuzix.hud.actionmenu.ActionMenuActivity
import com.vuzix.sdk.speechrecognitionservice.VuzixSpeechClient
import fr.foodlens.speech.GlobalVoiceCmdReceiver

open class DefaultActivity : ActionMenuActivity() {
    protected lateinit var vuzixSpeechClient: VuzixSpeechClient
    private lateinit var voiceReceiver: GlobalVoiceCmdReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            vuzixSpeechClient = VuzixSpeechClient(this)
        } catch (e: RuntimeException) {
            if (e.message?.equals("Stub:") ?: false) {
                Log.d("Vuzix_Speech", "Not a Vuzix device")
            } else {
                Log.d("Vuzix_Speech", e.toString())
            }
        }
        // On initialise l'actionneur
        voiceReceiver = GlobalVoiceCmdReceiver(this, vuzixSpeechClient)
        val filter = IntentFilter(VuzixSpeechClient.ACTION_VOICE_COMMAND)
        registerReceiver(voiceReceiver, filter)
    }
}