package com.prattham.expenseManager.helper

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.prattham.expenseManager.listener.MyClickListener

class MyButton(
    private val context: Context, private val text: String, private val textSize: Int,
    private val imageResId: Int,
    private val color: Int,
    private val listener: MyClickListener
) {
    private var pos: Int = 0
    private var clickRegion: RectF? = null
    private val resources: Resources = context.resources

    fun onClick(x: Float, y: Float): Boolean {
        if (clickRegion != null && clickRegion!!.contains(x, y)) {
            listener.onClick(pos)
            return true
        }
        return false
    }

    fun onDraw(canvas: Canvas, rectF: RectF, pos: Int) {

        val paint = Paint()
        paint.color = color
        canvas.drawRect(rectF, paint)

        //TEXT

        paint.color = Color.WHITE
        paint.textSize = textSize.toFloat()

        val r = Rect()
        val canvasHeight = rectF.height()
        val canvasWidth = rectF.width()
        paint.textAlign = Paint.Align.LEFT
        paint.getTextBounds(text, 0, text.length, r)
        val x: Float
        val y: Float
        if (imageResId == 0) {
            x = canvasWidth / 2f - r.width() / 2f - r.left.toFloat()
            y = canvasHeight / 2f + r.height() / 2f - r.bottom.toFloat()
            canvas.drawText(text, rectF.left + x, rectF.top + y, paint)
        } else {
            val d = ContextCompat.getDrawable(context, imageResId)
            val bitmap = drawableToBitmap(d)
            canvas.drawBitmap(
                bitmap,
                (rectF.left + rectF.right) / 2,
                (rectF.top + rectF.bottom) / 2,
                paint
            )
        }
        clickRegion = rectF
        this.pos = pos

    }

    private fun drawableToBitmap(d: Drawable?): Bitmap {
        if (d is BitmapDrawable)
            return d.bitmap
        val bitmap =
            Bitmap.createBitmap(d!!.intrinsicHeight, d.intrinsicWidth, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        d.setBounds(0, 0, canvas.width, canvas.height)
        d.draw(canvas)
        return bitmap
    }


}
