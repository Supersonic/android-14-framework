package com.android.internal.telephony;

import android.annotation.TargetApi;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.util.TelephonyUtils;
import com.android.telephony.Rlog;
@TargetApi(8)
/* loaded from: classes.dex */
public class RilWakelockInfo {
    private final String LOG_TAG = RilWakelockInfo.class.getSimpleName();
    private int mConcurrentRequests;
    private long mLastAggregatedTime;
    private long mRequestTime;
    private long mResponseTime;
    private int mRilRequestSent;
    private int mTokenNumber;
    private long mWakelockTimeAttributedSoFar;

    @VisibleForTesting
    public int getConcurrentRequests() {
        return this.mConcurrentRequests;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RilWakelockInfo(int i, int i2, int i3, long j) {
        int validateConcurrentRequests = validateConcurrentRequests(i3);
        this.mRilRequestSent = i;
        this.mTokenNumber = i2;
        this.mConcurrentRequests = validateConcurrentRequests;
        this.mRequestTime = j;
        this.mWakelockTimeAttributedSoFar = 0L;
        this.mLastAggregatedTime = j;
    }

    private int validateConcurrentRequests(int i) {
        if (i <= 0) {
            if (TelephonyUtils.IS_DEBUGGABLE) {
                IllegalArgumentException illegalArgumentException = new IllegalArgumentException("concurrentRequests should always be greater than 0.");
                Rlog.e(this.LOG_TAG, illegalArgumentException.toString());
                throw illegalArgumentException;
            }
            return 1;
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getTokenNumber() {
        return this.mTokenNumber;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getRilRequestSent() {
        return this.mRilRequestSent;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setResponseTime(long j) {
        updateTime(j);
        this.mResponseTime = j;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateConcurrentRequests(int i, long j) {
        int validateConcurrentRequests = validateConcurrentRequests(i);
        updateTime(j);
        this.mConcurrentRequests = validateConcurrentRequests;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void updateTime(long j) {
        this.mWakelockTimeAttributedSoFar += (j - this.mLastAggregatedTime) / this.mConcurrentRequests;
        this.mLastAggregatedTime = j;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long getWakelockTimeAttributedToClient() {
        return this.mWakelockTimeAttributedSoFar;
    }

    public String toString() {
        return "WakelockInfo{rilRequestSent=" + this.mRilRequestSent + ", tokenNumber=" + this.mTokenNumber + ", requestTime=" + this.mRequestTime + ", responseTime=" + this.mResponseTime + ", mWakelockTimeAttributed=" + this.mWakelockTimeAttributedSoFar + '}';
    }
}
