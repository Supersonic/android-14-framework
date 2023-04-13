package android.graphics;
/* loaded from: classes.dex */
public class CornerPathEffect extends PathEffect {
    private static native long nativeCreate(float f);

    public CornerPathEffect(float radius) {
        this.native_instance = nativeCreate(radius);
    }
}
