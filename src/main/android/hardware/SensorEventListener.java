package android.hardware;
/* loaded from: classes.dex */
public interface SensorEventListener {
    void onAccuracyChanged(Sensor sensor, int i);

    void onSensorChanged(SensorEvent sensorEvent);
}
