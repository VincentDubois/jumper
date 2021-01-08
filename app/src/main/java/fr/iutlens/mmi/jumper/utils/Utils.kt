package fr.iutlens.mmi.jumper.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable

object Utils {
    fun getStringResourceByName(context: Context, aString: String?): String {
        val packageName = context.packageName
        val resId = context.resources.getIdentifier(aString, "string", packageName)
        return context.getString(resId)
    }

    fun loadImage(context: Context, id: Int): Bitmap {

//		Drawable blankDrawable = context.getResources().getDrawable(id);
//		Bitmap b =((BitmapDrawable)blankDrawable).getBitmap();
        return BitmapFactory.decodeResource(context.resources, id)
    }

    fun loadImages(context: Context, id1: Int, id2: Int): Bitmap {
        val blankDrawable = context.resources.getDrawable(id1)
        val b = (blankDrawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val c = Canvas(b)
        c.drawBitmap(loadImage(context, id2), 0f, 0f, null)
        return b
    }
}