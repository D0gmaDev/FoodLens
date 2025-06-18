package fr.foodlens

import android.app.Fragment
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.vuzix.sdk.barcode.ScanResult2


class ScanResultFragment : Fragment() {
    /**
     * Inflate the correct layout upon creation
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState  If non-null, this fragment is being re-constructed from a previous saved state as given here.
     *
     * @return - Returns the View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_result, container, false)
    }

    /**
     * Once our view is created, we will show the image with the scan result
     *
     * @param view - The new view
     * @param savedInstanceState - required argument that we ignore
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val bitmap: ScanResultImageView =
            view.findViewById<View?>(R.id.bitmap) as ScanResultImageView
        val text = view.findViewById<View?>(R.id.text) as TextView
        // The arguments Bundle gives us the bitmap that was taken upon recognition of a barcode, and
        // the text extracted from the barcode within the image
        val args: Bundle? = getArguments()
        if (args != null) {
            val scanResult = args.getParcelable<ScanResult2?>(ARG_SCAN_RESULT)
            bitmap.setImageBitmap(args.getParcelable<Parcelable?>(ARG_BITMAP) as Bitmap?)
            bitmap.setLocation(scanResult!!.getResultPoints())
            text.setText(scanResult.getText())
        }
    }

    companion object {
        const val ARG_BITMAP: String = "bitmap"
        const val ARG_SCAN_RESULT: String = "scan_result"
    }
}