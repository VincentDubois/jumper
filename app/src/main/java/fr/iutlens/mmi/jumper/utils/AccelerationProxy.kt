package fr.iutlens.mmi.jumper.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import java.util.*

/**
 * Created by dubois on 23/04/15.
 */
class AccelerationProxy(context: Context, private val mListener: AccelerationListener) : SensorEventListener {

    interface AccelerationListener {
        fun onAcceleration(accelDelta: Float, dt: Double)
    }

    private val mSensorManager: SensorManager
    private val mAccelerometer: Sensor
    private val mMagnetometer: Sensor
    private val mLastAccelerometer = FloatArray(3)
    private val mLastMagnetometer = FloatArray(3)
    private var mLastAccelerometerSet = false
    private var mLastMagnetometerSet = false
    private val mR = FloatArray(9)
    private val mOrientation = FloatArray(3)
    private val mPreviousAccelerometer = FloatArray(3)
    private val gravity = FloatArray(3)
    private var vertical_dist = 0f
    private var vertical_speed = 0f
    var lastTimestamp: Long = 0
    var previousTimestamp: Long = 0
    fun resume() {
        lastTimestamp = -1
        previousTimestamp = -1
        mLastAccelerometerSet = false
        mLastMagnetometerSet = false
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI)
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI)
    }

    fun pause() {
        mSensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    private fun length(array: FloatArray): Double {
        var sum = 0.0
        for (f in array) {
            sum += f * f.toDouble()
        }
        return Math.sqrt(sum)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor == mAccelerometer) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.size)
            mLastAccelerometerSet = true
        } else if (event.sensor == mMagnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.size)
            mLastMagnetometerSet = true
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer)
            //            SensorManager.getOrientation(mR, mOrientation);
//            mListener.onOrientationChanged(mOrientation,event.timestamp/1000000l);
            if (event.sensor == mAccelerometer) {
                previousTimestamp = lastTimestamp
                lastTimestamp = event.timestamp
                System.arraycopy(mLastAccelerometer, 0, mPreviousAccelerometer, 0, 3)
                gravity[0] = mR[0] * event.values[0] + mR[3] * event.values[1] + mR[6] * event.values[2]
                gravity[1] = mR[1] * event.values[0] + mR[4] * event.values[1] + mR[7] * event.values[2]
                gravity[2] = mR[2] * event.values[0] + mR[5] * event.values[1] + mR[8] * event.values[2]
                mLastAccelerometer[0] = gravity[0]
                mLastAccelerometer[1] = gravity[1]
                mLastAccelerometer[2] = gravity[2] - SensorManager.GRAVITY_EARTH
                Log.d("Accel", Arrays.toString(mLastAccelerometer))
                val dt = (lastTimestamp - previousTimestamp) / 1000000L
                if (previousTimestamp != -1L && dt <= 100) {
                    //Log.d("Accel",""+da);
                    mListener.onAcceleration(mLastAccelerometer[2], dt.toDouble())
                } else {
                    vertical_dist = 0f
                    vertical_speed = 0f
                }
            }
        }
    }

    init {
        mSensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }
}