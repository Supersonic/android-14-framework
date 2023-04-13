package com.android.server.location.contexthub;

import android.hardware.location.ContextHubTransaction;
import android.hardware.location.NanoAppState;
import java.util.List;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public abstract class ContextHubServiceTransaction {
    public boolean mIsComplete;
    public final Long mNanoAppId;
    public final String mPackage;
    public final int mTransactionId;
    public final int mTransactionType;

    public void onQueryResponse(int i, List<NanoAppState> list) {
    }

    public abstract int onTransact();

    public void onTransactionComplete(int i) {
    }

    public ContextHubServiceTransaction(int i, int i2, String str) {
        this.mIsComplete = false;
        this.mTransactionId = i;
        this.mTransactionType = i2;
        this.mNanoAppId = null;
        this.mPackage = str;
    }

    public ContextHubServiceTransaction(int i, int i2, long j, String str) {
        this.mIsComplete = false;
        this.mTransactionId = i;
        this.mTransactionType = i2;
        this.mNanoAppId = Long.valueOf(j);
        this.mPackage = str;
    }

    public int getTransactionId() {
        return this.mTransactionId;
    }

    public int getTransactionType() {
        return this.mTransactionType;
    }

    public long getTimeout(TimeUnit timeUnit) {
        if (this.mTransactionType == 0) {
            return timeUnit.convert(30L, TimeUnit.SECONDS);
        }
        return timeUnit.convert(5L, TimeUnit.SECONDS);
    }

    public void setComplete() {
        this.mIsComplete = true;
    }

    public boolean isComplete() {
        return this.mIsComplete;
    }

    public String toString() {
        String str = ContextHubTransaction.typeToString(this.mTransactionType, true) + " (";
        if (this.mNanoAppId != null) {
            str = str + "appId = 0x" + Long.toHexString(this.mNanoAppId.longValue()) + ", ";
        }
        return str + "package = " + this.mPackage + ")";
    }
}
