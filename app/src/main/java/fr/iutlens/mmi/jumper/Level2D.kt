package fr.iutlens.mmi.jumper

import android.graphics.Canvas
import fr.iutlens.mmi.jumper.utils.SpriteSheet
import kotlin.math.floor

class Level2D(sprite_id: Int, level: String?) {
    private val sprite: SpriteSheet = SpriteSheet[sprite_id]!!
    val width: Float by lazy { (Level.VISIBLE_TILES * sprite.w).toFloat() }
    val columns = ArrayList<Column>()
    val length: Int
        get() = columns.size


    class Column(val floor: Int){
        val platform = ArrayList<Int>()
        val sprite = HashMap<Int,Int>().also {
            it[floor] = 0
        }

        fun add(height : Int){
            val h = height+ (if (platform.isEmpty()) floor else platform.last())

            platform.add(h)
            sprite[h] = 0
        }

        fun getBounds(y : Float) : IntRange{
            var f = floor
            if (y<f) return Int.MIN_VALUE..f
            if (platform.isEmpty()) return f..Int.MAX_VALUE
            var i = 0
            while(i < platform.size &&y>platform[i] ) {
                f = platform[i]
                ++i
            }
            val c = if (i < platform.size) platform[i] else Int.MAX_VALUE
            return f..c
        }

    }

    fun parse(level : String){
        var col : Column? = null
        var doAdd = false
        for(c in level){
            if (c == '+'){
                doAdd = true
            } else {
                val h = c-'0'
                if (doAdd){
                    col?.add(h)
                    doAdd = false
                } else {
                    col = Column(h)
                    columns.add((col))
                }
            }
        }

        // Calcul des bords des plateformes :
        // 0 : pas de connection
        // 1 : connecté à gauche
        // 2 : connecté à droite
        // 3 : connecté des deux cotés
        for(i in 0 until columns.size){
            val c = columns[i].sprite
            val left = columns[(i+columns.size - 1)%columns.size]
            left.sprite.keys.filter(c::contains).forEach { c[it] = 1  }
            val right = columns[(i + 1)%columns.size]
            right.sprite.keys.forEach { ndx ->
                val value = c[ndx]?.plus(2)
                if (value != null) c[ndx] = value
            }
        }
    }


    fun getFloor(x: Float, y: Float): Float {
        val inter = columns[floor(x).toInt()].getBounds(y)
        return if (inter.first == Int.MIN_VALUE) inter.last.toFloat() else inter.first.toFloat()
    }



    fun getFloor(x: Float): Float {
        return columns[floor(x).toInt()].floor.toFloat()
    }

    fun paint(canvas: Canvas, pos: Float) {
        val start = floor(pos).toInt()
        val offset = -(pos - start)
        for (i in 0..VISIBLE_TILES) {
            val ndx = (i + start+columns.size- TILES_LEFT) % columns.size
            val column = columns[ndx]
            for(entry in column.sprite){
                val id = SPRITE_ID[entry.value]
                sprite.paint(canvas, id,
                        getX(offset + i),
                        getY(entry.key.toFloat()))
            }
        }
    }

    fun getY(y: Float): Float {
        return (4 - y) * (sprite.h)
    }

    fun getX(x: Float): Float {
        return x * sprite.w
    }

    fun getSlope(fl: Float): Float {
        return 0f
    }


    init {
        parse(level ?: defaultLevel)
    }

    companion object {
        const val VISIBLE_TILES = 10
        const val TILES_LEFT = 4

        const val defaultLevel = "454444455555444+34+34+344444044444005555500"+
                "44445555566666655555444+34+34+3444+44+44+4444+34+34+34488888000"+
                "88887777666555500666555+25+25+25+25555+25+25+25544444"

        val SPRITE_ID = arrayOf(12,5,3,4)
    }

}
