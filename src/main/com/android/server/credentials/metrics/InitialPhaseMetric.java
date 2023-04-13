package com.android.server.credentials.metrics;
/* loaded from: classes.dex */
public class InitialPhaseMetric {
    public int mApiName = ApiName.UNKNOWN.getMetricCode();
    public int mCallerUid = -1;
    public int mSessionId = -1;
    public int mCountRequestClassType = -1;
    public long mCredentialServiceStartedTimeNanoseconds = -1;
    public long mCredentialServiceBeginQueryTimeNanoseconds = -1;

    public void setCredentialServiceStartedTimeNanoseconds(long j) {
        this.mCredentialServiceStartedTimeNanoseconds = j;
    }

    public void setCredentialServiceBeginQueryTimeNanoseconds(long j) {
        this.mCredentialServiceBeginQueryTimeNanoseconds = j;
    }

    public long getCredentialServiceStartedTimeNanoseconds() {
        return this.mCredentialServiceStartedTimeNanoseconds;
    }

    public void setApiName(int i) {
        this.mApiName = i;
    }

    public int getApiName() {
        return this.mApiName;
    }

    public void setCallerUid(int i) {
        this.mCallerUid = i;
    }

    public int getCallerUid() {
        return this.mCallerUid;
    }

    public void setSessionId(int i) {
        this.mSessionId = i;
    }

    public int getSessionId() {
        return this.mSessionId;
    }

    public void setCountRequestClassType(int i) {
        this.mCountRequestClassType = i;
    }

    public int getCountRequestClassType() {
        return this.mCountRequestClassType;
    }
}
