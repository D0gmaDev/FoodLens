package fr.foodlens.shopping

import android.Manifest
import android.app.Fragment
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast


class PermissionsFragment : Fragment() {
    private var listener: Listener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRetainInstance(true)
        requestPermissions()
    }

    private fun requestPermissions() {
        if (context?.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            permissionsGranted()
        } else {
            requestPermissions(
                arrayOf<String?>(Manifest.permission.CAMERA),
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    @Synchronized
    private fun permissionsGranted() {
        if (listener != null) {
            listener!!.permissionsGranted()
        }
    }

    @Synchronized
    fun setListener(listener: Listener?) {
        this.listener = listener
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (permissions.size == 1) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionsGranted() // Permission was just granted by the user.
                } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    requestPermissions() // Ask for permission again
                } else {
                    // Permission was denied. Give the user a hint, and exit
                    val intent: Intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.setData(Uri.fromParts("package", context?.packageName, null))
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    activity?.finish()
                    Toast.makeText(
                        context,
                        "Autoriser la camera",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    interface Listener {
        fun permissionsGranted()
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 0
    }
}