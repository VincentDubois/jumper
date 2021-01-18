package fr.iutlens.mmi.jumper

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import fr.iutlens.mmi.jumper.utils.AccelerationProxy.AccelerationListener
import fr.iutlens.mmi.jumper.utils.RefreshHandler
import fr.iutlens.mmi.jumper.utils.SpriteSheet.Companion.register
import fr.iutlens.mmi.jumper.utils.TimerAction
import kotlin.math.abs

class GameView : View, TimerAction, AccelerationListener {
    private lateinit var timer: RefreshHandler
    private lateinit var level: Level
    private var current_pos = 0f
    private lateinit var hero: Hero
    private val prep = 0.0
    private lateinit var pad : Pad

    constructor(context: Context?) : super(context) {
        init(null, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    /**
     * Initialisation de la vue
     *
     * Tous les constructeurs (au-dessus) renvoient ici.
     *
     * @param attrs
     * @param defStyle
     */
    private fun init(attrs: AttributeSet?, defStyle: Int) {

        // Chargement des feuilles de sprites
        register(R.drawable.decor_running, 3, 4, this.context)
        level = Level(R.drawable.decor_running, null)
        register(R.drawable.running_rabbit, 3, 3, this.context)
        hero = Hero(R.drawable.running_rabbit, SPEED)


        pad = Pad(resources.getDimension(R.dimen.margin))

        // Gestion du rafraichissement de la vue. La méthode update (juste en dessous)
        // sera appelée toutes les 30 ms
        timer = RefreshHandler(this)

        // Un clic sur la vue lance (ou relance) l'animation
//        setOnClickListener { if (!timer.isRunning) timer.scheduleRefresh(30) }
        if (!timer.isRunning) timer.scheduleRefresh(30)
        setOnTouchListener(pad)
    }

    /**
     * Mise à jour (faite toutes les 30 ms)
     */
    override fun update() {
        if (this.isShown) { // Si la vue est visible
            timer.scheduleRefresh(30) // programme le prochain rafraichissement

            if (pad["right"]) current_pos += SPEED
            if (pad["left"]) current_pos -= SPEED

            if (pad["jump"]) hero.jump(1f)


            if (current_pos > level.length) current_pos = 0f
            hero.update(level.getFloor(current_pos + 1), level.getSlope(current_pos + 1))
            invalidate() // demande à rafraichir la vue
        }
    }

    /**
     * Méthode appelée (automatiquement) pour afficher la vue
     * C'est là que l'on dessine le décor et les sprites
     * @param canvas
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // On met une couleur de fond
        canvas.drawColor(-0x1000000)


        canvas.save()
        // On choisit la transformation à appliquer à la vue i.e. la position
        // de la "camera"
        setCamera(canvas)
        // Dessin des différents éléments
        level.paint(canvas, current_pos)
        val x = 1f
        val y = hero.y
        hero.paint(canvas, level.getX(x), level.getY(y))

        canvas.restore()

        pad.paint(canvas)
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w>0 && h>0) pad.update(w,h)
    }

    private fun setCamera(canvas: Canvas) {

        val scale = width / level.width

        // La suite de transfomations est à interpréter "à l'envers"
        canvas.translate(0f, height / 2.toFloat())

        // On mets à l'échelle calculée au dessus
        canvas.scale(scale, scale)

        // On centre sur la position actuelle de la voiture (qui se retrouve en 0,0 )
        canvas.translate(0f, -level.getY(hero.y))
    }

    override fun onAcceleration(accelDelta: Float, dt: Double) {
//        Log.d("onAcceleration", accelDelta+" "+dt);
        if (accelDelta > 0.5f) {
            hero.jump(abs(accelDelta))
        }
        /*        if (accelDelta<0)
            prep += -accelDelta;
            if (prep > hero.MAX_STRENGTH) {
                hero.jump((float) prep);
                prep = 0;
            }
        else {
            if (prep > 0.1) {
                hero.jump((float) prep);
            }
            prep = 0;
        }*/
    }

    companion object {
        const val SPEED = 0.1f
    }
}