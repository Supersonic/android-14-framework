package com.android.server.vcn;

import android.content.Context;
import android.os.Looper;
import java.util.Objects;
/* loaded from: classes2.dex */
public class VcnContext {
    public final Context mContext;
    public final boolean mIsInTestMode;
    public final Looper mLooper;
    public final VcnNetworkProvider mVcnNetworkProvider;

    public VcnContext(Context context, Looper looper, VcnNetworkProvider vcnNetworkProvider, boolean z) {
        Objects.requireNonNull(context, "Missing context");
        this.mContext = context;
        Objects.requireNonNull(looper, "Missing looper");
        this.mLooper = looper;
        Objects.requireNonNull(vcnNetworkProvider, "Missing networkProvider");
        this.mVcnNetworkProvider = vcnNetworkProvider;
        this.mIsInTestMode = z;
    }

    public Context getContext() {
        return this.mContext;
    }

    public Looper getLooper() {
        return this.mLooper;
    }

    public VcnNetworkProvider getVcnNetworkProvider() {
        return this.mVcnNetworkProvider;
    }

    public boolean isInTestMode() {
        return this.mIsInTestMode;
    }

    public void ensureRunningOnLooperThread() {
        if (getLooper().getThread() != Thread.currentThread()) {
            throw new IllegalStateException("Not running on VcnMgmtSvc thread");
        }
    }
}
