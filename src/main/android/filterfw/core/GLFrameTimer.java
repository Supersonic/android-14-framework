package android.filterfw.core;
/* compiled from: GLFrame.java */
/* loaded from: classes.dex */
class GLFrameTimer {
    private static StopWatchMap mTimer = null;

    GLFrameTimer() {
    }

    public static StopWatchMap get() {
        if (mTimer == null) {
            mTimer = new StopWatchMap();
        }
        return mTimer;
    }
}
