package android.filterpacks.performance;
/* loaded from: classes.dex */
public class Throughput {
    private final int mPeriodFrames;
    private final int mPeriodTime;
    private final int mPixels;
    private final int mTotalFrames;

    public Throughput(int totalFrames, int periodFrames, int periodTime, int pixels) {
        this.mTotalFrames = totalFrames;
        this.mPeriodFrames = periodFrames;
        this.mPeriodTime = periodTime;
        this.mPixels = pixels;
    }

    public int getTotalFrameCount() {
        return this.mTotalFrames;
    }

    public int getPeriodFrameCount() {
        return this.mPeriodFrames;
    }

    public int getPeriodTime() {
        return this.mPeriodTime;
    }

    public float getFramesPerSecond() {
        return this.mPeriodFrames / this.mPeriodTime;
    }

    public float getNanosPerPixel() {
        double frameTimeInNanos = (this.mPeriodTime / this.mPeriodFrames) * 1000000.0d;
        return (float) (frameTimeInNanos / this.mPixels);
    }

    public String toString() {
        return getFramesPerSecond() + " FPS";
    }
}
