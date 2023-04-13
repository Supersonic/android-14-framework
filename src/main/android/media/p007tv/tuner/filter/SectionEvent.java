package android.media.p007tv.tuner.filter;

import android.annotation.SystemApi;
@SystemApi
/* renamed from: android.media.tv.tuner.filter.SectionEvent */
/* loaded from: classes2.dex */
public class SectionEvent extends FilterEvent {
    private final long mDataLength;
    private final int mSectionNum;
    private final int mTableId;
    private final int mVersion;

    private SectionEvent(int tableId, int version, int sectionNum, long dataLength) {
        this.mTableId = tableId;
        this.mVersion = version;
        this.mSectionNum = sectionNum;
        this.mDataLength = dataLength;
    }

    public int getTableId() {
        return this.mTableId;
    }

    public int getVersion() {
        return this.mVersion;
    }

    public int getSectionNumber() {
        return this.mSectionNum;
    }

    @Deprecated
    public int getDataLength() {
        return (int) getDataLengthLong();
    }

    public long getDataLengthLong() {
        return this.mDataLength;
    }
}
