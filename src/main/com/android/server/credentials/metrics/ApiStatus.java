package com.android.server.credentials.metrics;
/* loaded from: classes.dex */
public enum ApiStatus {
    SUCCESS(1),
    FAILURE(2),
    CLIENT_CANCELED(4),
    USER_CANCELED(3);
    
    private final int mInnerMetricCode;

    ApiStatus(int i) {
        this.mInnerMetricCode = i;
    }

    public int getMetricCode() {
        return this.mInnerMetricCode;
    }
}
