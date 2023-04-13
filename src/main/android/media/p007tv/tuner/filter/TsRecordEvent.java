package android.media.p007tv.tuner.filter;

import android.annotation.SystemApi;
@SystemApi
/* renamed from: android.media.tv.tuner.filter.TsRecordEvent */
/* loaded from: classes2.dex */
public class TsRecordEvent extends FilterEvent {
    private final long mDataLength;
    private final int mFirstMbInSlice;
    private final int mPid;
    private final long mPts;
    private final int mScIndexMask;
    private final int mTsIndexMask;

    private TsRecordEvent(int pid, int tsIndexMask, int scIndexMask, long dataLength, long pts, int firstMbInSlice) {
        this.mPid = pid;
        this.mTsIndexMask = tsIndexMask;
        this.mScIndexMask = scIndexMask;
        this.mDataLength = dataLength;
        this.mPts = pts;
        this.mFirstMbInSlice = firstMbInSlice;
    }

    public int getPacketId() {
        return this.mPid;
    }

    public int getTsIndexMask() {
        return this.mTsIndexMask;
    }

    public int getScIndexMask() {
        return this.mScIndexMask;
    }

    public long getDataLength() {
        return this.mDataLength;
    }

    public long getPts() {
        return this.mPts;
    }

    public int getFirstMacroblockInSlice() {
        return this.mFirstMbInSlice;
    }
}
