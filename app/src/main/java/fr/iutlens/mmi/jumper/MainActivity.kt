package fr.iutlens.mmi.jumper

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import fr.iutlens.mmi.jumper.utils.AccelerationProxy

/***
 * Crédits image : https://pixabay.com/fr/lapin-dessin-animé-jeu-élément-1582176/
 *
 *
 *
 */
class MainActivity : AppCompatActivity() {
    private var proxy: AccelerationProxy? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // On récupère la vue du jeu
        val game: GameView = findViewById(R.id.gameView)

        // On configure le jeu pour recevoir les changements d'orientation
        proxy = AccelerationProxy(this, game)
    }

    protected override fun onResume() {
        super.onResume()
        proxy!!.resume() // On relance l'accéléromètre
    }

    protected override fun onPause() {
        super.onPause()
        proxy!!.pause() // On mets en pause l'accéléromètre
    }
}