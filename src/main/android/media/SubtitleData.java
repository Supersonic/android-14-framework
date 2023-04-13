package android.media;

import android.p008os.Parcel;
/* loaded from: classes2.dex */
public final class SubtitleData {
    private static final String TAG = "SubtitleData";
    private byte[] mData;
    private long mDurationUs;
    private long mStartTimeUs;
    private int mTrackIndex;

    public SubtitleData(Parcel parcel) {
        if (!parseParcel(parcel)) {
            throw new IllegalArgumentException("parseParcel() fails");
        }
    }

    public SubtitleData(int trackIndex, long startTimeUs, long durationUs, byte[] data) {
        if (data == null) {
            throw new IllegalArgumentException("null data is not allowed");
        }
        this.mTrackIndex = trackIndex;
        this.mStartTimeUs = startTimeUs;
        this.mDurationUs = durationUs;
        this.mData = data;
    }

    public int getTrackIndex() {
        return this.mTrackIndex;
    }

    public long getStartTimeUs() {
        return this.mStartTimeUs;
    }

    public long getDurationUs() {
        return this.mDurationUs;
    }

    public byte[] getData() {
        return this.mData;
    }

    private boolean parseParcel(Parcel parcel) {
        parcel.setDataPosition(0);
        if (parcel.dataAvail() == 0) {
            return false;
        }
        this.mTrackIndex = parcel.readInt();
        this.mStartTimeUs = parcel.readLong();
        this.mDurationUs = parcel.readLong();
        byte[] bArr = new byte[parcel.readInt()];
        this.mData = bArr;
        parcel.readByteArray(bArr);
        return true;
    }
}
