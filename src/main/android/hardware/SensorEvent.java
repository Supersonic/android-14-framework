package android.hardware;
/* loaded from: classes.dex */
public class SensorEvent {
    public int accuracy;
    public boolean firstEventAfterDiscontinuity;
    public Sensor sensor;
    public long timestamp;
    public final float[] values;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SensorEvent(int valueSize) {
        this.values = new float[valueSize];
    }

    public SensorEvent(Sensor sensor, int accuracy, long timestamp, float[] values) {
        this.sensor = sensor;
        this.accuracy = accuracy;
        this.timestamp = timestamp;
        this.values = values;
    }
}
