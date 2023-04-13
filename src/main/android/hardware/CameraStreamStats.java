package android.hardware;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.Log;
/* loaded from: classes.dex */
public class CameraStreamStats implements Parcelable {
    public static final Parcelable.Creator<CameraStreamStats> CREATOR = new Parcelable.Creator<CameraStreamStats>() { // from class: android.hardware.CameraStreamStats.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CameraStreamStats createFromParcel(Parcel in) {
            try {
                CameraStreamStats streamStats = new CameraStreamStats(in);
                return streamStats;
            } catch (Exception e) {
                Log.m109e(CameraStreamStats.TAG, "Exception creating CameraStreamStats from parcel", e);
                return null;
            }
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CameraStreamStats[] newArray(int size) {
            return new CameraStreamStats[size];
        }
    };
    public static final int HISTOGRAM_TYPE_CAPTURE_LATENCY = 1;
    public static final int HISTOGRAM_TYPE_UNKNOWN = 0;
    private static final String TAG = "CameraStreamStats";
    private int mColorSpace;
    private int mDataSpace;
    private long mDynamicRangeProfile;
    private long mErrorCount;
    private int mFormat;
    private int mHeight;
    private float[] mHistogramBins;
    private long[] mHistogramCounts;
    private int mHistogramType;
    private int mMaxAppBuffers;
    private int mMaxHalBuffers;
    private float mMaxPreviewFps;
    private long mRequestCount;
    private int mStartLatencyMs;
    private long mStreamUseCase;
    private long mUsage;
    private int mWidth;

    public CameraStreamStats() {
        this.mWidth = 0;
        this.mHeight = 0;
        this.mFormat = 0;
        this.mMaxPreviewFps = 0.0f;
        this.mDataSpace = 0;
        this.mUsage = 0L;
        this.mRequestCount = 0L;
        this.mErrorCount = 0L;
        this.mStartLatencyMs = 0;
        this.mMaxHalBuffers = 0;
        this.mMaxAppBuffers = 0;
        this.mHistogramType = 0;
        this.mDynamicRangeProfile = 1L;
        this.mStreamUseCase = 0L;
        this.mColorSpace = -1;
    }

    public CameraStreamStats(int width, int height, int format, float maxPreviewFps, int dataSpace, long usage, long requestCount, long errorCount, int startLatencyMs, int maxHalBuffers, int maxAppBuffers, long dynamicRangeProfile, long streamUseCase, int colorSpace) {
        this.mWidth = width;
        this.mHeight = height;
        this.mFormat = format;
        this.mMaxPreviewFps = maxPreviewFps;
        this.mDataSpace = dataSpace;
        this.mUsage = usage;
        this.mRequestCount = requestCount;
        this.mErrorCount = errorCount;
        this.mStartLatencyMs = startLatencyMs;
        this.mMaxHalBuffers = maxHalBuffers;
        this.mMaxAppBuffers = maxAppBuffers;
        this.mHistogramType = 0;
        this.mDynamicRangeProfile = dynamicRangeProfile;
        this.mStreamUseCase = streamUseCase;
        this.mColorSpace = colorSpace;
    }

    private CameraStreamStats(Parcel in) {
        readFromParcel(in);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mWidth);
        dest.writeInt(this.mHeight);
        dest.writeInt(this.mFormat);
        dest.writeFloat(this.mMaxPreviewFps);
        dest.writeInt(this.mDataSpace);
        dest.writeLong(this.mUsage);
        dest.writeLong(this.mRequestCount);
        dest.writeLong(this.mErrorCount);
        dest.writeInt(this.mStartLatencyMs);
        dest.writeInt(this.mMaxHalBuffers);
        dest.writeInt(this.mMaxAppBuffers);
        dest.writeInt(this.mHistogramType);
        dest.writeFloatArray(this.mHistogramBins);
        dest.writeLongArray(this.mHistogramCounts);
        dest.writeLong(this.mDynamicRangeProfile);
        dest.writeLong(this.mStreamUseCase);
        dest.writeInt(this.mColorSpace);
    }

    public void readFromParcel(Parcel in) {
        this.mWidth = in.readInt();
        this.mHeight = in.readInt();
        this.mFormat = in.readInt();
        this.mMaxPreviewFps = in.readFloat();
        this.mDataSpace = in.readInt();
        this.mUsage = in.readLong();
        this.mRequestCount = in.readLong();
        this.mErrorCount = in.readLong();
        this.mStartLatencyMs = in.readInt();
        this.mMaxHalBuffers = in.readInt();
        this.mMaxAppBuffers = in.readInt();
        this.mHistogramType = in.readInt();
        this.mHistogramBins = in.createFloatArray();
        this.mHistogramCounts = in.createLongArray();
        this.mDynamicRangeProfile = in.readLong();
        this.mStreamUseCase = in.readLong();
        this.mColorSpace = in.readInt();
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public int getFormat() {
        return this.mFormat;
    }

    public float getMaxPreviewFps() {
        return this.mMaxPreviewFps;
    }

    public int getDataSpace() {
        return this.mDataSpace;
    }

    public long getUsage() {
        return this.mUsage;
    }

    public long getRequestCount() {
        return this.mRequestCount;
    }

    public long getErrorCount() {
        return this.mErrorCount;
    }

    public int getStartLatencyMs() {
        return this.mStartLatencyMs;
    }

    public int getMaxHalBuffers() {
        return this.mMaxHalBuffers;
    }

    public int getMaxAppBuffers() {
        return this.mMaxAppBuffers;
    }

    public int getHistogramType() {
        return this.mHistogramType;
    }

    public float[] getHistogramBins() {
        return this.mHistogramBins;
    }

    public long[] getHistogramCounts() {
        return this.mHistogramCounts;
    }

    public long getDynamicRangeProfile() {
        return this.mDynamicRangeProfile;
    }

    public int getColorSpace() {
        return this.mColorSpace;
    }

    public long getStreamUseCase() {
        return this.mStreamUseCase;
    }
}
