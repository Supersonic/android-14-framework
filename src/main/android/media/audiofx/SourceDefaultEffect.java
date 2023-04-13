package android.media.audiofx;

import android.app.ActivityThread;
import android.util.Log;
import java.util.UUID;
/* loaded from: classes2.dex */
public class SourceDefaultEffect extends DefaultEffect {
    private static final String TAG = "SourceDefaultEffect-JAVA";

    private final native void native_release(int i);

    private final native int native_setup(String str, String str2, int i, int i2, String str3, int[] iArr);

    static {
        System.loadLibrary("audioeffect_jni");
    }

    public SourceDefaultEffect(UUID type, UUID uuid, int priority, int source) {
        int[] id = new int[1];
        int initResult = native_setup(type.toString(), uuid.toString(), priority, source, ActivityThread.currentOpPackageName(), id);
        if (initResult != 0) {
            Log.m110e(TAG, "Error code " + initResult + " when initializing SourceDefaultEffect");
            switch (initResult) {
                case -5:
                    throw new UnsupportedOperationException("Effect library not loaded");
                case -4:
                    throw new IllegalArgumentException("Source, type uuid, or implementation uuid not supported.");
                default:
                    throw new RuntimeException("Cannot initialize effect engine for type: " + type + " Error: " + initResult);
            }
        }
        this.mId = id[0];
    }

    public void release() {
        native_release(this.mId);
    }

    protected void finalize() {
        release();
    }
}
