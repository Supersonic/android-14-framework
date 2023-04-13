package android.media.p007tv.tuner;

import android.annotation.SystemApi;
@SystemApi
/* renamed from: android.media.tv.tuner.DemuxInfo */
/* loaded from: classes2.dex */
public class DemuxInfo {
    private int mFilterTypes;

    public DemuxInfo(int filterTypes) {
        setFilterTypes(filterTypes);
    }

    public int getFilterTypes() {
        return this.mFilterTypes;
    }

    public void setFilterTypes(int filterTypes) {
        this.mFilterTypes = filterTypes;
    }
}
