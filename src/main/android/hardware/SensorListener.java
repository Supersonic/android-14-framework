package android.hardware;
@Deprecated
/* loaded from: classes.dex */
public interface SensorListener {
    void onAccuracyChanged(int i, int i2);

    void onSensorChanged(int i, float[] fArr);
}
