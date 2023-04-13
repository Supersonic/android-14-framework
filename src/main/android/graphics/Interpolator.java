package android.graphics;

import android.p008os.SystemClock;
/* loaded from: classes.dex */
public class Interpolator {
    private int mFrameCount;
    private int mValueCount;
    private long native_instance;

    /* loaded from: classes.dex */
    public enum Result {
        NORMAL,
        FREEZE_START,
        FREEZE_END
    }

    private static native long nativeConstructor(int i, int i2);

    private static native void nativeDestructor(long j);

    private static native void nativeReset(long j, int i, int i2);

    private static native void nativeSetKeyFrame(long j, int i, int i2, float[] fArr, float[] fArr2);

    private static native void nativeSetRepeatMirror(long j, float f, boolean z);

    private static native int nativeTimeToValues(long j, int i, float[] fArr);

    public Interpolator(int valueCount) {
        this.mValueCount = valueCount;
        this.mFrameCount = 2;
        this.native_instance = nativeConstructor(valueCount, 2);
    }

    public Interpolator(int valueCount, int frameCount) {
        this.mValueCount = valueCount;
        this.mFrameCount = frameCount;
        this.native_instance = nativeConstructor(valueCount, frameCount);
    }

    public void reset(int valueCount) {
        reset(valueCount, 2);
    }

    public void reset(int valueCount, int frameCount) {
        this.mValueCount = valueCount;
        this.mFrameCount = frameCount;
        nativeReset(this.native_instance, valueCount, frameCount);
    }

    public final int getKeyFrameCount() {
        return this.mFrameCount;
    }

    public final int getValueCount() {
        return this.mValueCount;
    }

    public void setKeyFrame(int index, int msec, float[] values) {
        setKeyFrame(index, msec, values, null);
    }

    public void setKeyFrame(int index, int msec, float[] values, float[] blend) {
        if (index < 0 || index >= this.mFrameCount) {
            throw new IndexOutOfBoundsException();
        }
        if (values.length < this.mValueCount) {
            throw new ArrayStoreException();
        }
        if (blend != null && blend.length < 4) {
            throw new ArrayStoreException();
        }
        nativeSetKeyFrame(this.native_instance, index, msec, values, blend);
    }

    public void setRepeatMirror(float repeatCount, boolean mirror) {
        if (repeatCount >= 0.0f) {
            nativeSetRepeatMirror(this.native_instance, repeatCount, mirror);
        }
    }

    public Result timeToValues(float[] values) {
        return timeToValues((int) SystemClock.uptimeMillis(), values);
    }

    public Result timeToValues(int msec, float[] values) {
        if (values != null && values.length < this.mValueCount) {
            throw new ArrayStoreException();
        }
        switch (nativeTimeToValues(this.native_instance, msec, values)) {
            case 0:
                return Result.NORMAL;
            case 1:
                return Result.FREEZE_START;
            default:
                return Result.FREEZE_END;
        }
    }

    protected void finalize() throws Throwable {
        nativeDestructor(this.native_instance);
        this.native_instance = 0L;
    }
}
