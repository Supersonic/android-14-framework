package android.media.p007tv.tuner.filter;

import android.annotation.SystemApi;
@SystemApi
/* renamed from: android.media.tv.tuner.filter.MmtpRecordEvent */
/* loaded from: classes2.dex */
public class MmtpRecordEvent extends FilterEvent {
    private final long mDataLength;
    private final int mFirstMbInSlice;
    private final int mMpuSequenceNumber;
    private final long mPts;
    private final int mScHevcIndexMask;
    private final int mTsIndexMask;

    private MmtpRecordEvent(int scHevcIndexMask, long dataLength, int mpuSequenceNumber, long pts, int firstMbInSlice, int tsIndexMask) {
        this.mScHevcIndexMask = scHevcIndexMask;
        this.mDataLength = dataLength;
        this.mMpuSequenceNumber = mpuSequenceNumber;
        this.mPts = pts;
        this.mFirstMbInSlice = firstMbInSlice;
        this.mTsIndexMask = tsIndexMask;
    }

    public int getScHevcIndexMask() {
        return this.mScHevcIndexMask;
    }

    public long getDataLength() {
        return this.mDataLength;
    }

    public int getMpuSequenceNumber() {
        return this.mMpuSequenceNumber;
    }

    public long getPts() {
        return this.mPts;
    }

    public int getFirstMacroblockInSlice() {
        return this.mFirstMbInSlice;
    }

    public int getTsIndexMask() {
        return this.mTsIndexMask;
    }
}
