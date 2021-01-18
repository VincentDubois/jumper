package fr.iutlens.mmi.jumper

import android.graphics.Canvas
import android.graphics.Paint
import android.provider.SyncStateContract.Helpers.update
import android.util.Log
import android.view.MotionEvent
import android.view.View

class Pad(val margin: Float) : View.OnTouchListener {

    data class Button(val radius : Float, var x : Float = 0f, var y : Float = 0f,var pressed : Boolean = false){
        fun onTouch(event: MotionEvent){

            val i = event.actionIndex
            val dx = event.getX(i) - x
            val dy = event.getY(i) - y
            if (dx * dx + dy * dy < radius * radius) {
            when (event.actionMasked){
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> pressed = true
                MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> pressed = false
                }
            }
        }

        fun paint(canvas: Canvas) {
            if (x==0f) return

            canvas.drawCircle(x,y,radius, paint)
        }
    }

    companion object{
        val paint = Paint().apply {
            color = 0xaa777777.toInt()
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
    }


    private val buttons = HashMap<String,Button>().apply {
        this["left"] = Button(margin*2)
        this["right"] = Button(margin*2)
        this["jump"] = Button(margin*3)
    }



    fun update(w :Int, h : Int){
        buttons["right"]?.apply {
            x = w-margin*4
            y = h-margin*4
        }
        buttons["left"]?.apply {
            x = w-margin*10
            y = h-margin*4
        }
        buttons["jump"]?.apply {
            x = margin*4
            y = h-margin*4
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event != null) {
            Log.d("touch",""+event.x+" " +event.y+ " "+event.action+" "+event.actionMasked)
            buttons.values.forEach { it.onTouch(event) }
        }
        return true
    }

    fun paint(canvas: Canvas) {
        buttons.values.forEach { it.paint(canvas) }
    }

    operator fun get(button: String): Boolean {
        return buttons[button]?.pressed ?: false
    }

}
