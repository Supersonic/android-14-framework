package com.android.ims.rcs.uce.request;

import android.text.TextUtils;
import android.util.Log;
import com.android.ims.rcs.uce.util.UceUtils;
import java.util.Optional;
/* loaded from: classes.dex */
public class SubscriptionTerminatedHelper {
    private static final String LOG_TAG = UceUtils.getLogPrefix() + "SubscriptionTerminated";
    private static final String REASON_DEACTIVATED = "deactivated";
    private static final String REASON_GIVEUP = "giveup";
    private static final String REASON_NORESOURCE = "noresource";
    private static final String REASON_PROBATION = "probation";
    private static final String REASON_REJECTED = "rejected";
    private static final String REASON_TIMEOUT = "timeout";

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class TerminatedResult {
        private final Optional<Integer> mErrorCode;
        private final long mRetryAfterMillis;

        public TerminatedResult(Optional<Integer> errorCode, long retryAfterMillis) {
            this.mErrorCode = errorCode;
            this.mRetryAfterMillis = retryAfterMillis;
        }

        public Optional<Integer> getErrorCode() {
            return this.mErrorCode;
        }

        public long getRetryAfterMillis() {
            return this.mRetryAfterMillis;
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("TerminatedResult: ").append("errorCode=").append(this.mErrorCode).append(", retryAfterMillis=").append(this.mRetryAfterMillis);
            return builder.toString();
        }
    }

    public static TerminatedResult getAnalysisResult(String reason, long retryAfterMillis, boolean allCapsHaveReceived) {
        TerminatedResult result = null;
        if (TextUtils.isEmpty(reason)) {
            if (retryAfterMillis > 0) {
                result = new TerminatedResult(Optional.of(1), retryAfterMillis);
            }
        } else if (REASON_DEACTIVATED.equalsIgnoreCase(reason)) {
            long retry = getRequestRetryAfterMillis(retryAfterMillis);
            result = new TerminatedResult(Optional.of(1), retry);
        } else if (REASON_PROBATION.equalsIgnoreCase(reason)) {
            long retry2 = getRequestRetryAfterMillis(retryAfterMillis);
            result = new TerminatedResult(Optional.of(1), retry2);
        } else if (REASON_REJECTED.equalsIgnoreCase(reason)) {
            result = new TerminatedResult(Optional.of(5), 0L);
        } else if (REASON_TIMEOUT.equalsIgnoreCase(reason)) {
            if (retryAfterMillis > 0) {
                long retry3 = getRequestRetryAfterMillis(retryAfterMillis);
                result = new TerminatedResult(Optional.of(9), retry3);
            } else {
                result = !allCapsHaveReceived ? new TerminatedResult(Optional.of(9), 0L) : new TerminatedResult(Optional.empty(), 0L);
            }
        } else if (REASON_GIVEUP.equalsIgnoreCase(reason)) {
            long retry4 = getRequestRetryAfterMillis(retryAfterMillis);
            result = new TerminatedResult(Optional.of(5), retry4);
        } else if (REASON_NORESOURCE.equalsIgnoreCase(reason)) {
            result = new TerminatedResult(Optional.of(7), 0L);
        } else if (retryAfterMillis > 0) {
            long retry5 = getRequestRetryAfterMillis(retryAfterMillis);
            result = new TerminatedResult(Optional.of(1), retry5);
        }
        if (result == null) {
            result = new TerminatedResult(Optional.empty(), 0L);
        }
        Log.d(LOG_TAG, "getAnalysisResult: reason=" + reason + ", retry=" + retryAfterMillis + ", allCapsHaveReceived=" + allCapsHaveReceived + ", " + result);
        return result;
    }

    private static long getRequestRetryAfterMillis(long retryAfterMillis) {
        long minRetryAfterMillis = UceUtils.getMinimumRequestRetryAfterMillis();
        return retryAfterMillis < minRetryAfterMillis ? minRetryAfterMillis : retryAfterMillis;
    }
}
