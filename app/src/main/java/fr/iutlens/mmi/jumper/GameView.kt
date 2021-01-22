package fr.iutlens.mmi.jumper

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import fr.iutlens.mmi.jumper.utils.RefreshHandler
import fr.iutlens.mmi.jumper.utils.SpriteSheet.Companion.register
import fr.iutlens.mmi.jumper.utils.TimerAction

class GameView : View, TimerAction {
    private lateinit var timer: RefreshHandler
    private lateinit var level: Level
    private lateinit var hero: Hero
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

        // Chargement et utilisation des feuilles de sprites
        register(R.drawable.decor_running, 3, 4*3, this.context)
        level = Level(R.drawable.decor_running, null)

        register(R.drawable.running_rabbit, 3, 3, this.context)
        hero = Hero(R.drawable.running_rabbit)

        hero.y = level.getFloor(hero.x)


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

            // gestion des actions

            hero.vx = when {
                pad["right"] -> Hero.SPEED
                pad["left"] -> -Hero.SPEED
                else -> 0f
            }

            if (pad["jump"]) hero.jump(1f)

            // Déplacement du héro
            hero.update(level)

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
        canvas.drawColor(0xFF000000.toInt())

        // On sauvegarde la transformation "naturelle" (0,0) en haut à gauche, 1 = 1 pixel
        canvas.save()
        // On choisit la transformation à appliquer à la vue i.e. la position
        // de la "camera"
        setCamera(canvas)
        // Dessin des différents éléments
        level.paint(canvas, hero.x)
        hero.paint(canvas, level.getX(Level.TILES_LEFT.toFloat()), level.getY(hero.y))

        //On reprend la transformation initiale pour dessiner le pad
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

        // On centre sur la position actuelle  (qui se retrouve en 0,0 )
        canvas.translate(0f, -level.getY(hero.y))
    }
}