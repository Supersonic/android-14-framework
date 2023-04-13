package com.android.server.credentials.metrics;
/* loaded from: classes.dex */
public enum ApiName {
    UNKNOWN(0),
    GET_CREDENTIAL(1),
    CREATE_CREDENTIAL(2),
    CLEAR_CREDENTIAL(3),
    IS_ENABLED_CREDENTIAL_PROVIDER_SERVICE(4);
    
    private final int mInnerMetricCode;

    ApiName(int i) {
        this.mInnerMetricCode = i;
    }

    public int getMetricCode() {
        return this.mInnerMetricCode;
    }
}
