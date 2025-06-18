package fr.foodlens

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView


class ScanResultImageView (context: Context, attrs: AttributeSet?, defStyleAttr: Int) : AppCompatImageView(context, attrs, defStyleAttr) {
    private var location: Array<Point?>? = null
    private val locationPaint: Paint

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    init {
        setAdjustViewBounds(true)

        // Setup a Paint object to draw on the scanned barcode with these attributes
        locationPaint = Paint()
        locationPaint.setColor(Color.GREEN)
        locationPaint.strokeWidth = 5f
        locationPaint.style = Paint.Style.STROKE
        locationPaint.strokeCap = Paint.Cap.ROUND
        locationPaint.isAntiAlias = true
    }

    /**
     * Mutator to set the location of the barcode within the image, and re-draw accordingly
     *
     * @param location  Location object defining the recognized barcode within the image
     */
    fun setLocation(location: Array<Point?>?) {
        this.location = location
        invalidate()
    }

    /**
     * Override this to draw a box around the barcode at the location it was found
     *
     * @param canvas Canvas upon which to draw
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (location != null) {
            val d = getDrawable()
            if (d is BitmapDrawable) {
                val b = d.bitmap
                val scale = width.toFloat() / b.getWidth()
                if (location != null && location!!.size > 1) {
                    for (iIndex in 1..<location!!.size) {
                        drawLine(canvas, location!![iIndex - 1]!!, location!![iIndex]!!, scale)
                    }
                    drawLine(canvas, location!![location!!.size - 1]!!, location!![0]!!, scale)
                }
            }
        }
    }

    /**
     * Utility to draw a single line onto the canvas
     *
     * @param canvas Canvas upon which to draw
     * @param p1 Point to start the line
     * @param p2 Point to end the line
     * @param scale float by which to scale the line. Converts from high-res bitmap dimensions to Canvas dimensions
     */
    private fun drawLine(canvas: Canvas, p1: Point, p2: Point, scale: Float) {
        canvas.drawLine(p1.x * scale, p1.y * scale, p2.x * scale, p2.y * scale, locationPaint)
    }
}