package android.hardware;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class CameraSessionStats implements Parcelable {
    public static final int CAMERA_API_LEVEL_1 = 1;
    public static final int CAMERA_API_LEVEL_2 = 2;
    public static final int CAMERA_FACING_BACK = 0;
    public static final int CAMERA_FACING_EXTERNAL = 2;
    public static final int CAMERA_FACING_FRONT = 1;
    public static final int CAMERA_STATE_ACTIVE = 1;
    public static final int CAMERA_STATE_CLOSED = 3;
    public static final int CAMERA_STATE_IDLE = 2;
    public static final int CAMERA_STATE_OPEN = 0;
    public static final Parcelable.Creator<CameraSessionStats> CREATOR = new Parcelable.Creator<CameraSessionStats>() { // from class: android.hardware.CameraSessionStats.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CameraSessionStats createFromParcel(Parcel in) {
            return new CameraSessionStats(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CameraSessionStats[] newArray(int size) {
            return new CameraSessionStats[size];
        }
    };
    private int mApiLevel;
    private String mCameraId;
    private String mClientName;
    private boolean mDeviceError;
    private int mFacing;
    private int mInternalReconfigure;
    private boolean mIsNdk;
    private int mLatencyMs;
    private float mMaxPreviewFps;
    private int mNewCameraState;
    private long mRequestCount;
    private long mResultErrorCount;
    private int mSessionType;
    private ArrayList<CameraStreamStats> mStreamStats;
    private String mUserTag;
    private int mVideoStabilizationMode;

    public CameraSessionStats() {
        this.mFacing = -1;
        this.mNewCameraState = -1;
        this.mApiLevel = -1;
        this.mIsNdk = false;
        this.mLatencyMs = -1;
        this.mMaxPreviewFps = 0.0f;
        this.mSessionType = -1;
        this.mInternalReconfigure = -1;
        this.mRequestCount = 0L;
        this.mResultErrorCount = 0L;
        this.mDeviceError = false;
        this.mStreamStats = new ArrayList<>();
        this.mVideoStabilizationMode = -1;
    }

    public CameraSessionStats(String cameraId, int facing, int newCameraState, String clientName, int apiLevel, boolean isNdk, int creationDuration, float maxPreviewFps, int sessionType, int internalReconfigure) {
        this.mCameraId = cameraId;
        this.mFacing = facing;
        this.mNewCameraState = newCameraState;
        this.mClientName = clientName;
        this.mApiLevel = apiLevel;
        this.mIsNdk = isNdk;
        this.mLatencyMs = creationDuration;
        this.mMaxPreviewFps = maxPreviewFps;
        this.mSessionType = sessionType;
        this.mInternalReconfigure = internalReconfigure;
        this.mStreamStats = new ArrayList<>();
    }

    private CameraSessionStats(Parcel in) {
        readFromParcel(in);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mCameraId);
        dest.writeInt(this.mFacing);
        dest.writeInt(this.mNewCameraState);
        dest.writeString(this.mClientName);
        dest.writeInt(this.mApiLevel);
        dest.writeBoolean(this.mIsNdk);
        dest.writeInt(this.mLatencyMs);
        dest.writeFloat(this.mMaxPreviewFps);
        dest.writeInt(this.mSessionType);
        dest.writeInt(this.mInternalReconfigure);
        dest.writeLong(this.mRequestCount);
        dest.writeLong(this.mResultErrorCount);
        dest.writeBoolean(this.mDeviceError);
        dest.writeTypedList(this.mStreamStats);
        dest.writeString(this.mUserTag);
        dest.writeInt(this.mVideoStabilizationMode);
    }

    public void readFromParcel(Parcel in) {
        this.mCameraId = in.readString();
        this.mFacing = in.readInt();
        this.mNewCameraState = in.readInt();
        this.mClientName = in.readString();
        this.mApiLevel = in.readInt();
        this.mIsNdk = in.readBoolean();
        this.mLatencyMs = in.readInt();
        this.mMaxPreviewFps = in.readFloat();
        this.mSessionType = in.readInt();
        this.mInternalReconfigure = in.readInt();
        this.mRequestCount = in.readLong();
        this.mResultErrorCount = in.readLong();
        this.mDeviceError = in.readBoolean();
        ArrayList<CameraStreamStats> streamStats = new ArrayList<>();
        in.readTypedList(streamStats, CameraStreamStats.CREATOR);
        this.mStreamStats = streamStats;
        this.mUserTag = in.readString();
        this.mVideoStabilizationMode = in.readInt();
    }

    public String getCameraId() {
        return this.mCameraId;
    }

    public int getFacing() {
        return this.mFacing;
    }

    public int getNewCameraState() {
        return this.mNewCameraState;
    }

    public String getClientName() {
        return this.mClientName;
    }

    public int getApiLevel() {
        return this.mApiLevel;
    }

    public boolean isNdk() {
        return this.mIsNdk;
    }

    public int getLatencyMs() {
        return this.mLatencyMs;
    }

    public float getMaxPreviewFps() {
        return this.mMaxPreviewFps;
    }

    public int getSessionType() {
        return this.mSessionType;
    }

    public int getInternalReconfigureCount() {
        return this.mInternalReconfigure;
    }

    public long getRequestCount() {
        return this.mRequestCount;
    }

    public long getResultErrorCount() {
        return this.mResultErrorCount;
    }

    public boolean getDeviceErrorFlag() {
        return this.mDeviceError;
    }

    public List<CameraStreamStats> getStreamStats() {
        return this.mStreamStats;
    }

    public String getUserTag() {
        return this.mUserTag;
    }

    public int getVideoStabilizationMode() {
        return this.mVideoStabilizationMode;
    }
}
