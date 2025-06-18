package fr.foodlens

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.vuzix.hud.actionmenu.ActionMenuActivity
import com.vuzix.sdk.barcode.BarcodeType2
import com.vuzix.sdk.barcode.ScanResult2
import com.vuzix.sdk.barcode.ScannerIntent

class ActivityIntent : ActionMenuActivity() {
    private var cameraToggle = false
    private lateinit var mTextEntryField: TextView;
    private val codeTypes: Array<String> = arrayOf(BarcodeType2.EAN_13.name, BarcodeType2.EAN_8.name)

    companion object {
        private const val REQUEST_CODE_SCAN = 90001
        private const val TAG = "barcodeSample"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intent)

        mTextEntryField = findViewById(R.id.scannedTextResult)
        val buttonScan = findViewById<Button>(R.id.btn_scan_barcode)
        buttonScan.requestFocusFromTouch()
        buttonScan.setOnClickListener { onScanClick() }
    }

    private fun onScanClick() {
        val scannerIntent = Intent(ScannerIntent.ACTION)
        scannerIntent.putExtra(ScannerIntent.EXTRA_BARCODE2_TYPES, codeTypes)
        if (cameraToggle) {
            scannerIntent.putExtra(ScannerIntent.EXTRA_CAMERA_ID, 0)
        } else {
            scannerIntent.putExtra(ScannerIntent.EXTRA_CAMERA_ID, 1)
        }
        cameraToggle = !cameraToggle
        try {
            startActivityForResult(scannerIntent, REQUEST_CODE_SCAN)
        } catch(activityNotFound: ActivityNotFoundException) {
            Toast.makeText(this, "Only m series", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_SCAN) {
            if (resultCode == Activity.RESULT_OK) {
                val scanResult = data?.getParcelableExtra<ScanResult2>(ScannerIntent.RESULT_EXTRA_SCAN_RESULT2)
                if (scanResult != null) {
                    mTextEntryField.text = scanResult.text
                } else {
                    mTextEntryField.text = "No data"
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}