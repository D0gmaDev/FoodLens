package fr.foodlens

import android.app.Fragment
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import com.vuzix.hud.actionmenu.ActionMenuActivity
import com.vuzix.sdk.barcode.BarcodeType2
import com.vuzix.sdk.barcode.ScanResult2
import com.vuzix.sdk.barcode.ScannerFragment
import java.io.IOException


class EmbeddedActivity : ActionMenuActivity(), PermissionsFragment.Listener {

    companion object {
        private const val TAG_PERMISSIONS_FRAGMENT: String = "permissions"
    }

    private val barcodeTypes = arrayOf<String?>(
        BarcodeType2.EAN_8.name,
        BarcodeType2.EAN_13.name
    )

    private var scanInstructionsView: View? = null
    private var mScannerListener: ScannerFragment.Listener2? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_embedded)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

        var permissionsFragment: PermissionsFragment? = fragmentManager.findFragmentByTag(TAG_PERMISSIONS_FRAGMENT) as PermissionsFragment?
        if (permissionsFragment == null) {
            permissionsFragment = PermissionsFragment()
            fragmentManager.beginTransaction().add(permissionsFragment, TAG_PERMISSIONS_FRAGMENT).commit()
        }

        // Register as a PermissionsFragment.Listener so our permissionsGranted() is called
        permissionsFragment.setListener(this)

        scanInstructionsView = findViewById<View?>(R.id.scan_instructions)

        createScannerListener()

        val scannerFragment: Fragment? =
            getFragmentManager().findFragmentById(R.id.fragment_container)
        if (scannerFragment is ScannerFragment) {
            // hook new listener instance up to existing scanner fragment
            scannerFragment.setListener2(mScannerListener)
            scanInstructionsView?.visibility = View.VISIBLE
        } else {
            // Hide the instructions until we have permission granted
            scanInstructionsView?.visibility = View.GONE
        }
    }

    /**
     * Called upon permissions being granted. This is the only way we show the scanner with API 23
     */
    override fun permissionsGranted() {
        showScanner()
    }

    private fun setScannerArgs(): Bundle {
        val args = Bundle()
        args.putStringArray(ScannerFragment.ARG_BARCODE2_TYPES, barcodeTypes)
        args.putBoolean(ScannerFragment.ARG_ZOOM_IN_MODE, true)
        return args
    }

    /**
     * Shows the scanner fragment in our activity
     */
    private fun showScanner() {
        val scannerFragment = ScannerFragment()
        scannerFragment.setArguments(setScannerArgs())
        fragmentManager.beginTransaction().replace(R.id.fragment_container, scannerFragment)
            .commit()
        scannerFragment.setListener2(mScannerListener)
        scanInstructionsView!!.visibility = View.VISIBLE
    }

    private fun createScannerListener() {
        try {
            mScannerListener = object : ScannerFragment.Listener2 {
                override fun onScan2Result(bitmap: Bitmap?, results: Array<ScanResult2?>) {
                    onScanFragmentScanResult(bitmap, results)
                }

                override fun onError() {
                    onScanFragmentError()
                }
            }
        } catch (e: NoClassDefFoundError) {
            Toast.makeText(this, "only m series", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    /**
     * This callback gives us the scan result.  This is relayed through mScannerListener.onScanResult
     *
     * This sample calls a helper class to display the result to the screen
     *
     * @param bitmap -  the bitmap in which barcodes were found
     * @param results -  an array of ScanResult
     */
    private fun onScanFragmentScanResult(bitmap: Bitmap?, results: Array<ScanResult2?>) {
        val scannerFragment = fragmentManager.findFragmentById(R.id.fragment_container) as ScannerFragment
        scannerFragment.setListener(null)
        showScanResult(bitmap, results[0])
    }

    /**
     * This callback gives us scan errors. This is relayed through mScannerListener.onError
     *
     * At a minimum the scanner fragment must be removed from the activity. This sample closes
     * the entire activity, since it has no other functionality
     */
    private fun onScanFragmentError() {
        finish()
        Toast.makeText(this, "erreur fragment scan", Toast.LENGTH_LONG).show()
    }

    /**
     * Helper method to show a scan result
     *
     * @param bitmap -  the bitmap in which barcodes were found
     * @param result -  an array of ScanResult
     */
    private fun showScanResult(bitmap: Bitmap?, result: ScanResult2?) {
        scanInstructionsView!!.setVisibility(View.GONE)
        val scanResultFragment = ScanResultFragment()
        val args = Bundle()
        args.putParcelable(ScanResultFragment.ARG_BITMAP, bitmap)
        args.putParcelable(ScanResultFragment.ARG_SCAN_RESULT, result)
        scanResultFragment.setArguments(args)
        fragmentManager.beginTransaction().replace(R.id.fragment_container, scanResultFragment)
            .commit()
//        beep()
    }

    /**
     * A best practice is to give some audible feedback during scan operations. This beeps.
     */
//    private fun beep() {
//        val player = MediaPlayer()
//        player.setAudioStreamType(AudioManager.STREAM_MUSIC)
//        player.setOnCompletionListener(OnCompletionListener { obj: MediaPlayer? -> obj!!.release() })
//        try {
//            val file = getResources().openRawResourceFd(R.raw.beep)
//            player.setDataSource(file.fileDescriptor, file.startOffset, file.getLength())
//            file.close()
//            player.setVolume(.1f, .1f)
//            player.prepare()
//            player.start()
//        } catch (e: IOException) {
//            player.release()
//        }
//    }

    /**
     * Basic control to return from the result fragment to the scanner fragment, or exit the app from the scanner
     */
    public override fun onBackPressed() {
        if (isScanResultShowing()) {
            showScanner()
            return
        }
        super.onBackPressed()
    }

    /**
     * Utility to determine if the scanner result fragment is showing
     * @return True if showing
     */
    private fun isScanResultShowing(): Boolean {
        return fragmentManager.findFragmentById(R.id.fragment_container) is ScanResultFragment
    }
}