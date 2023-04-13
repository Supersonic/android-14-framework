package android.media.p007tv.tuner.filter;

import android.annotation.SystemApi;
@SystemApi
/* renamed from: android.media.tv.tuner.filter.IpPayloadEvent */
/* loaded from: classes2.dex */
public class IpPayloadEvent extends FilterEvent {
    private final int mDataLength;

    private IpPayloadEvent(int dataLength) {
        this.mDataLength = dataLength;
    }

    public int getDataLength() {
        return this.mDataLength;
    }
}
