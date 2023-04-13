package android.media.p007tv.tuner.filter;

import android.annotation.SystemApi;
@SystemApi
/* renamed from: android.media.tv.tuner.filter.TemiEvent */
/* loaded from: classes2.dex */
public class TemiEvent extends FilterEvent {
    private final byte[] mDescrData;
    private final byte mDescrTag;
    private final long mPts;

    private TemiEvent(long pts, byte descrTag, byte[] descrData) {
        this.mPts = pts;
        this.mDescrTag = descrTag;
        this.mDescrData = descrData;
    }

    public long getPts() {
        return this.mPts;
    }

    public byte getDescriptorTag() {
        return this.mDescrTag;
    }

    public byte[] getDescriptorData() {
        return this.mDescrData;
    }
}
