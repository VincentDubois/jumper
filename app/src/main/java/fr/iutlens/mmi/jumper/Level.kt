package fr.iutlens.mmi.jumper

import android.graphics.Canvas
import fr.iutlens.mmi.jumper.utils.SpriteSheet

/**
 * Created by dubois on 30/12/2017.
 */
class Level(sprite_id: Int, s: String?) {
    // Codes pour le décor
    //   (0)   rien
    // ( = )
    // o / \
    // < ^ >   comme les deux lignes au dessus, + barrière
    // * é è
    // + (13)  // élévation du niveau de base
    // - (14)  // baisse du niveau de base
    private val CODE = " (=)o/\\<^>*éè+-"
    private val SLOPE = "     +-    +-"
    private val def = "(==\\^///==^==^\\\\^==/)----<^é>++(//=^=> ++o-- *- o +(==^=)"
    private lateinit var sprite_id: IntArray
    private lateinit var baseline: IntArray
    private lateinit var slope: IntArray
    private val sprite: SpriteSheet
    private fun parse(s: String?) {
        var size = 0
        // Calcul de la longueur réelle du parcours (+- changent le niveau, pas la longueur)
        for (i in 0 until s!!.length) if (s[i] != '+' && s[i] != '-') ++size
        sprite_id = IntArray(size)
        baseline = IntArray(size)
        slope = IntArray(size)
        var pos = 0
        var current_baseline = 0
        for (i in 0 until s.length) {
            val code = CODE.indexOf(s[i])
            if (code < 13) {
                sprite_id[pos] = code - 1 // -1 correspond à vide
                val sl = SLOPE[code]
                when (sl) {
                    ' ' -> {
                        slope[pos] = 0
                        baseline[pos] = current_baseline
                    }
                    '+' -> {
                        slope[pos] = +1
                        ++current_baseline
                        baseline[pos] = current_baseline
                    }
                    '-' -> {
                        slope[pos] = -1
                        baseline[pos] = current_baseline
                        --current_baseline
                    }
                }
                ++pos
            } else {
                if (code == 13) ++current_baseline else if (code == 14) --current_baseline
            }
        }
    }

    fun getY(y: Float): Float {
        return (6 - y) * (sprite.h / 3)
    }

    fun getX(x: Float): Float {
        return x * sprite.w
    }

    val width: Float
        get() = (VISIBLE_TILES * sprite.w).toFloat()

    fun getFloor(pos: Float): Float {
        var pos = pos
        if (pos >= length) pos = length - 1.toFloat()
        val start = Math.floor(pos.toDouble()).toInt()
        val offset = -(pos - start)
        var result = baseline[start].toFloat()
        val s = slope[start]

        // prise en compte de l'effet de la pente(s)
        // sur la hauteur du sol en fonction de
        // la position sur la tuile (offset=o)
        //  s     o=0    o=1     formule
        // -------------------------------
        // +1  :* -1  -> 0       = -1+o
        // -1  :   0  -> -1      = -o
        //  0  :   0  -> 0       = 0
        //
        // * : les tuiles sont affichées à partir du point de le plus
        //     haut. Donc, pour une tuile montante va de 0 -> 1, mais le
        //     la référence (baseline) est celle d'arrivée. Donc un
        //     Décalage de 0 au début correspondrait à la hauteur d'arrivée,
        //     c'est à dire un cran trop haut. On corrige donc 0-1 -> 1-1,
        //     ce qui explique le -1 -> 0 (la hauteur à la fin de la tuile sera la
        //     baseline

        // Traduction des calculs précédents :
        if (s == +1) result -= 1 + offset
        if (s == -1) result += offset
        //        if (s ==  0) result +=  0;   // inutile, += 0 ne fait rien
        return result
    }

    fun paint(canvas: Canvas?, pos: Float) {
        canvas?.let {
            var pos = pos
            if (pos >= length) pos = length - 1.toFloat()
            val start = Math.floor(pos.toDouble()).toInt()
            val offset = -(pos - start)
            for (i in 0..VISIBLE_TILES) {
                val ndx = (i + start) % sprite_id.size
                val id = sprite_id[ndx]
                if (id != -1) sprite.paint(canvas, id,
                        getX(offset + i),
                        getY(baseline[ndx] + 1.toFloat()))
            }
        }
    }

    val length: Int
        get() = sprite_id.size

    fun getSlope(pos: Float): Float {
        var pos = pos
        if (pos >= length) pos = length - 1.toFloat()
        val start = Math.floor(pos.toDouble()).toInt()
        return slope[start].toFloat()
    }

    companion object {
        const val VISIBLE_TILES = 10
    }

    init {
        var s = s
        if (s == null) s = def
        parse(s)
        sprite = SpriteSheet.get(sprite_id)!!
    }
}