package fr.iutlens.mmi.jumper

import android.graphics.Canvas
import fr.iutlens.mmi.jumper.utils.SpriteSheet

/**
 * Created by dubois on 30/12/2017.
 */
class Hero(sprite_id: Int, vx: Float) {
    private val BASELINE = 0.93f
    private val G = 0.2f
    private val IMPULSE = 2.5f
    private val sprite: SpriteSheet
    var y: Float
        private set
    private var vy: Float
    private val vx: Float
    private var jump: Float
    private var frame: Int
    private var cpt: Int

    fun update(floor: Float, slope: Float) {
        y += vy // inertie
        var altitude = y - floor
        if (altitude < 0) { // On est dans le sol : atterrissage
            vy = 0f //floor-y;
            y = floor
            altitude = 0f
        }
        if (altitude == 0f) { // en contact avec le sol
            if (jump != 0f) {
                vy = jump * IMPULSE * vx // On saute ?
                frame = 3
            } else {
//                vy = -G*vx;
                vy = (slope - G) * vx // On suit le sol...
                cpt = (cpt + 1) % SAME_FRAME
                if (cpt == 0) frame = (frame + 1) % 8
            }
        } else { // actuellement en vol
            vy -= G * vx // effet de la gravité
            frame = if (vy > 0) 3 else 5
            //            if (y < floor+slope*vx) y = floor+slope*vx; // atterrissage ?
        }
        jump = 0f
    }

    fun paint(canvas: Canvas?, x: Float, y: Float) {
        sprite.paint(canvas!!, frame, x - sprite.w / 2, y - sprite.h * BASELINE)
    }

    fun jump(strength: Float) {
        var strength = strength
        if (strength > MAX_STRENGTH) strength = MAX_STRENGTH
        if (strength > jump) jump = strength
    }

    companion object {
        const val SAME_FRAME = 3
        const val MAX_STRENGTH = 2f
    }

    init {
        sprite = SpriteSheet.get(sprite_id)!!
        y = 0f
        vy = 0f
        jump = 0f
        frame = 0
        cpt = 0
        this.vx = vx
    }
}