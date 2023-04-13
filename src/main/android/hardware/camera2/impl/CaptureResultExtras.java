package android.hardware.camera2.impl;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes.dex */
public class CaptureResultExtras implements Parcelable {
    public static final Parcelable.Creator<CaptureResultExtras> CREATOR = new Parcelable.Creator<CaptureResultExtras>() { // from class: android.hardware.camera2.impl.CaptureResultExtras.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CaptureResultExtras createFromParcel(Parcel in) {
            return new CaptureResultExtras(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CaptureResultExtras[] newArray(int size) {
            return new CaptureResultExtras[size];
        }
    };
    private int afTriggerId;
    private String errorPhysicalCameraId;
    private int errorStreamId;
    private long frameNumber;
    private boolean hasReadoutTimestamp;
    private long lastCompletedRegularFrameNumber;
    private long lastCompletedReprocessFrameNumber;
    private long lastCompletedZslFrameNumber;
    private int partialResultCount;
    private int precaptureTriggerId;
    private long readoutTimestamp;
    private int requestId;
    private int subsequenceId;

    private CaptureResultExtras(Parcel in) {
        readFromParcel(in);
    }

    public CaptureResultExtras(int requestId, int subsequenceId, int afTriggerId, int precaptureTriggerId, long frameNumber, int partialResultCount, int errorStreamId, String errorPhysicalCameraId, long lastCompletedRegularFrameNumber, long lastCompletedReprocessFrameNumber, long lastCompletedZslFrameNumber, boolean hasReadoutTimestamp, long readoutTimestamp) {
        this.requestId = requestId;
        this.subsequenceId = subsequenceId;
        this.afTriggerId = afTriggerId;
        this.precaptureTriggerId = precaptureTriggerId;
        this.frameNumber = frameNumber;
        this.partialResultCount = partialResultCount;
        this.errorStreamId = errorStreamId;
        this.errorPhysicalCameraId = errorPhysicalCameraId;
        this.lastCompletedRegularFrameNumber = lastCompletedRegularFrameNumber;
        this.lastCompletedReprocessFrameNumber = lastCompletedReprocessFrameNumber;
        this.lastCompletedZslFrameNumber = lastCompletedZslFrameNumber;
        this.hasReadoutTimestamp = hasReadoutTimestamp;
        this.readoutTimestamp = readoutTimestamp;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.requestId);
        dest.writeInt(this.subsequenceId);
        dest.writeInt(this.afTriggerId);
        dest.writeInt(this.precaptureTriggerId);
        dest.writeLong(this.frameNumber);
        dest.writeInt(this.partialResultCount);
        dest.writeInt(this.errorStreamId);
        String str = this.errorPhysicalCameraId;
        if (str != null && !str.isEmpty()) {
            dest.writeBoolean(true);
            dest.writeString(this.errorPhysicalCameraId);
        } else {
            dest.writeBoolean(false);
        }
        dest.writeLong(this.lastCompletedRegularFrameNumber);
        dest.writeLong(this.lastCompletedReprocessFrameNumber);
        dest.writeLong(this.lastCompletedZslFrameNumber);
        dest.writeBoolean(this.hasReadoutTimestamp);
        if (this.hasReadoutTimestamp) {
            dest.writeLong(this.readoutTimestamp);
        }
    }

    public void readFromParcel(Parcel in) {
        this.requestId = in.readInt();
        this.subsequenceId = in.readInt();
        this.afTriggerId = in.readInt();
        this.precaptureTriggerId = in.readInt();
        this.frameNumber = in.readLong();
        this.partialResultCount = in.readInt();
        this.errorStreamId = in.readInt();
        boolean errorPhysicalCameraIdPresent = in.readBoolean();
        if (errorPhysicalCameraIdPresent) {
            this.errorPhysicalCameraId = in.readString();
        }
        this.lastCompletedRegularFrameNumber = in.readLong();
        this.lastCompletedReprocessFrameNumber = in.readLong();
        this.lastCompletedZslFrameNumber = in.readLong();
        boolean readBoolean = in.readBoolean();
        this.hasReadoutTimestamp = readBoolean;
        if (readBoolean) {
            this.readoutTimestamp = in.readLong();
        }
    }

    public String getErrorPhysicalCameraId() {
        return this.errorPhysicalCameraId;
    }

    public int getRequestId() {
        return this.requestId;
    }

    public int getSubsequenceId() {
        return this.subsequenceId;
    }

    public int getAfTriggerId() {
        return this.afTriggerId;
    }

    public int getPrecaptureTriggerId() {
        return this.precaptureTriggerId;
    }

    public long getFrameNumber() {
        return this.frameNumber;
    }

    public int getPartialResultCount() {
        return this.partialResultCount;
    }

    public int getErrorStreamId() {
        return this.errorStreamId;
    }

    public long getLastCompletedRegularFrameNumber() {
        return this.lastCompletedRegularFrameNumber;
    }

    public long getLastCompletedReprocessFrameNumber() {
        return this.lastCompletedReprocessFrameNumber;
    }

    public long getLastCompletedZslFrameNumber() {
        return this.lastCompletedZslFrameNumber;
    }

    public boolean hasReadoutTimestamp() {
        return this.hasReadoutTimestamp;
    }

    public long getReadoutTimestamp() {
        return this.readoutTimestamp;
    }
}
