package android.media.p007tv.tuner.filter;

import android.annotation.SystemApi;
@SystemApi
/* renamed from: android.media.tv.tuner.filter.IpCidChangeEvent */
/* loaded from: classes2.dex */
public final class IpCidChangeEvent extends FilterEvent {
    private final int mCid;

    private IpCidChangeEvent(int cid) {
        this.mCid = cid;
    }

    public int getIpCid() {
        return this.mCid;
    }
}
