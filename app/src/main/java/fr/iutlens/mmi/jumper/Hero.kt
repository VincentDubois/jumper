package fr.iutlens.mmi.jumper

import android.graphics.Canvas
import fr.iutlens.mmi.jumper.utils.SpriteSheet

/**
 * Created by dubois on 30/12/2017.
 */
class Hero(sprite_id: Int) {
    private val sprite: SpriteSheet = SpriteSheet[sprite_id]!!
    var y = 0f
    var x = 0f
    var vx = 0f
    private var vy = 0f
    private var jump = 0f
    private var frame = 0
    private var cpt = 0

    fun update(level : Level) {

        x += vx
        y += vy // inertie

        if (x > level.length) x = 0f // On boucle
        if (x < 0) x = level.length.toFloat() // dans les deux sens

        val floor = level.getFloor(x,y-vy+0.1f)

        var altitude = y - floor
        if (altitude < 0) { // On est dans le sol : atterrissage
            vy = 0f //floor-y;
            y = floor
            altitude = 0f
        }
        if (altitude == 0f) { // en contact avec le sol
            if (jump != 0f) {
                vy = jump * IMPULSE * SPEED // On saute ?
                frame = 3
            } else {
//                vy = -G*vx;
                cpt = (cpt + 1) % SAME_FRAME
                if (cpt == 0) frame = (frame + 1) % 8
            }
        } else { // actuellement en vol
            vy -= G *  SPEED// effet de la gravité
            frame = if (vy > 0) 3 else 5
        }
        jump = 0f
    }

    fun paint(canvas: Canvas?, x: Float, y: Float) {
        sprite.paint(canvas!!, frame, x - sprite.w / 2, y - sprite.h * BASELINE)
    }

    fun jump(strength: Float) {
        jump = strength
    }

    companion object {
        const val SAME_FRAME = 3
        const val BASELINE = 0.93f
        const val G = 0.2f
        const val IMPULSE = 4f
        const val SPEED = 0.1f
    }

}