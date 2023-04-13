package android.gesture;

import java.io.DataInputStream;
import java.io.IOException;
/* loaded from: classes.dex */
public class GesturePoint {
    public final long timestamp;

    /* renamed from: x */
    public final float f58x;

    /* renamed from: y */
    public final float f59y;

    public GesturePoint(float x, float y, long t) {
        this.f58x = x;
        this.f59y = y;
        this.timestamp = t;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static GesturePoint deserialize(DataInputStream in) throws IOException {
        float x = in.readFloat();
        float y = in.readFloat();
        long timeStamp = in.readLong();
        return new GesturePoint(x, y, timeStamp);
    }

    public Object clone() {
        return new GesturePoint(this.f58x, this.f59y, this.timestamp);
    }
}
