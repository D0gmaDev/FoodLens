package fr.foodlens

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

    /**
     * One-time initialization. Sets up the view
     * @param savedInstanceState - we have no saved state. Just pass through to superclass
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRetainInstance(true)
        requestPermissions()
    }

    /**
     * Make the permissions request to the system
     */
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

    /**
     * Called upon the permissions being granted. Notifies the permission listener.
     */
    @Synchronized
    private fun permissionsGranted() {
        if (listener != null) {
            listener!!.permissionsGranted()
        }
    }


    /**
     * Sets the listener on which we will call permissionsGranted()
     * @param listener pointer to the class implementing the PermissionsFragment.Listener
     */
    @Synchronized
    fun setListener(listener: Listener?) {
        this.listener = listener
    }

    /**
     * Required interface for any activity that requests a run-time permission
     *
     * @see [https://developer.android.com/training/permissions/requesting.html](https://developer.android.com/training/permissions/requesting.html)
     *
     * @param requestCode int: The request code passed in requestPermissions(android.app.Activity, String[], int)
     * @param permissions String: The requested permissions. Never null.
     * @param grantResults int: The grant results for the corresponding permissions which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
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

    /**
     * Define the interface of a permission fragment listener
     */
    interface Listener {
        fun permissionsGranted()
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 0
    }
}