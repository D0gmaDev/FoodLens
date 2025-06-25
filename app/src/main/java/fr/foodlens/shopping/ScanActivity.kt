package fr.foodlens.shopping

import android.app.Fragment
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.vuzix.sdk.barcode.BarcodeType2
import com.vuzix.sdk.barcode.ScanResult2
import com.vuzix.sdk.barcode.ScannerFragment
import fr.foodlens.R
import fr.foodlens.database.AppDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScanActivity : AppCompatActivity(), PermissionsFragment.Listener {

    private val barcodeTypes = arrayOf(BarcodeType2.EAN_13.name, BarcodeType2.EAN_8.name)

    private lateinit var scanInstructionsView: TextView
    private lateinit var mScannerListener: ScannerFragment.Listener2

    private var listId: Int = -1

    companion object {
        private const val TAG_PERMISSIONS_FRAGMENT = "permissions_cam"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        listId = intent.getIntExtra("listId", -1)

        if (listId == -1) {
            Toast.makeText(this, "No list ID provided", Toast.LENGTH_SHORT).show() // Should not happen
            finish()
            return
        }

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

        var permissionsFragment: PermissionsFragment? = fragmentManager.findFragmentByTag(TAG_PERMISSIONS_FRAGMENT) as PermissionsFragment?
        if (permissionsFragment == null) {
            permissionsFragment = PermissionsFragment()
            fragmentManager.beginTransaction().add(permissionsFragment, TAG_PERMISSIONS_FRAGMENT).commit()
        }

        permissionsFragment.setListener(this)

        scanInstructionsView = findViewById(R.id.scan_instructions)
        resetInstructions()

        createScannerListener()

        val scannerFragment: Fragment? =
            fragmentManager.findFragmentById(R.id.fragment_container)
        if (scannerFragment is ScannerFragment) {
            // hook new listener instance up to existing scanner fragment
            scannerFragment.setListener2(mScannerListener)
            scanInstructionsView.visibility = View.VISIBLE
        } else {
            // Hide the instructions until we have permission granted
            scanInstructionsView.visibility = View.GONE
        }
    }

    private fun showScanner() {
        val scannerFragment = ScannerFragment()
        val args = Bundle().apply {
            putStringArray(ScannerFragment.ARG_BARCODE2_TYPES, barcodeTypes)
            putBoolean(ScannerFragment.ARG_ZOOM_IN_MODE, true)
        }
        scannerFragment.setArguments(args)
        fragmentManager.beginTransaction().replace(R.id.fragment_container, scannerFragment)
            .commit()
        scannerFragment.setListener2(mScannerListener)
        scanInstructionsView.visibility = View.VISIBLE
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

    private fun onScanFragmentScanResult(bitmap: Bitmap?, results: Array<ScanResult2?>) {
        val scannerFragment = fragmentManager.findFragmentById(R.id.fragment_container) as ScannerFragment
        scannerFragment.setListener2(null)
        Log.d("ScanActivity", "onScanFragmentScanResult: $results results found")
        scanInstructionsView.text = "Scanning..."
        lifecycleScope.launch {
            val itemId = results[0]?.text?.toIntOrNull()
            if (itemId == null) {
                scanInstructionsView.text = "Invalid barcode scanned."
                delay(3000)
                scannerFragment.setListener2(mScannerListener)
                resetInstructions()
                return@launch
            }
            val itemList = AppDatabase.getDatabase(this@ScanActivity).shoppingListItemDao().getItemById(itemId, listId)
            if (itemList == null) {
                scanInstructionsView.text = "Item not found in the list."
                delay(3000)
                scannerFragment.setListener2(mScannerListener)
                resetInstructions()
                return@launch
            }
        }
//        showScanResult(bitmap, results[0])
    }



    private fun onScanFragmentError() {
        lifecycleScope.launch {
            scanInstructionsView.text = "Unable to scan, please try again."
            delay(5000)
            resetInstructions()
        }
    }

    fun resetInstructions() {
        scanInstructionsView.text = "Point the camera at a barcode to scan it."
        scanInstructionsView.visibility = View.VISIBLE
    }

    override fun permissionsGranted() {
        showScanner()
        Log.d("ScanActivity", "Permissions granted, showing scanner")
    }
}