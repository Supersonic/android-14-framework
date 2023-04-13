package com.android.server.credentials.metrics;
/* loaded from: classes.dex */
public enum ProviderStatusForMetrics {
    UNKNOWN(0),
    FINAL_FAILURE(4),
    QUERY_FAILURE(3),
    FINAL_SUCCESS(2),
    QUERY_SUCCESS(1);
    
    private final int mInnerMetricCode;

    ProviderStatusForMetrics(int i) {
        this.mInnerMetricCode = i;
    }

    public int getMetricCode() {
        return this.mInnerMetricCode;
    }
}
