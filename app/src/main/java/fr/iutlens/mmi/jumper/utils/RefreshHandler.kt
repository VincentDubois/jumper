package fr.iutlens.mmi.jumper.utils

import android.os.Handler
import android.os.Message
import java.lang.ref.WeakReference

/**
 * Created by dubois on 27/12/2017.
 */
class RefreshHandler(animator: TimerAction?) : Handler() {
    var weak: WeakReference<TimerAction?>
    override fun handleMessage(msg: Message) {
        if (weak.get() == null) return
        weak.get()!!.update()
    }

    fun scheduleRefresh(delayMillis: Long) {
        this.removeMessages(0)
        sendMessageDelayed(obtainMessage(0), delayMillis)
    }

    val isRunning: Boolean
        get() = this.hasMessages(0)

    init {
        weak = WeakReference<TimerAction?>(animator)
    }
}