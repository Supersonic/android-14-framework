package com.android.ims.rcs.uce.presence.publish;

import android.util.Log;
import com.android.ims.rcs.uce.util.UceUtils;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class PublishProcessorState {
    private static final String LOG_TAG = UceUtils.getLogPrefix() + "PublishProcessorState";
    private volatile boolean mIsPublishing;
    private final Object mLock = new Object();
    private final PendingRequest mPendingRequest = new PendingRequest();
    private final PublishThrottle mPublishThrottle;
    private long mTaskId;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class PendingRequest {
        private boolean mPendingFlag;
        private final Object mLock = new Object();
        private Optional<Integer> mTriggerType = Optional.empty();

        public void setPendingRequest(int triggerType) {
            synchronized (this.mLock) {
                this.mPendingFlag = true;
                this.mTriggerType = Optional.of(Integer.valueOf(triggerType));
            }
        }

        public void clearPendingRequest() {
            synchronized (this.mLock) {
                this.mPendingFlag = false;
                this.mTriggerType = Optional.empty();
            }
        }

        public boolean hasPendingRequest() {
            boolean z;
            synchronized (this.mLock) {
                z = this.mPendingFlag;
            }
            return z;
        }

        public Optional<Integer> getPendingRequestTriggerType() {
            Optional<Integer> optional;
            synchronized (this.mLock) {
                optional = this.mTriggerType;
            }
            return optional;
        }
    }

    /* loaded from: classes.dex */
    private static class PublishThrottle {
        private static final int PUBLISH_MAXIMUM_NUM_RETRIES = 3;
        private static final int RETRY_BASE_PERIOD_MIN = 1;
        private Optional<Instant> mLastPublishedTime;
        private Optional<Instant> mPublishAllowedTime;
        private long mRcsPublishThrottle;
        private int mRetryCount;
        private int mSubId;

        public PublishThrottle(int subId) {
            this.mSubId = subId;
            resetState();
        }

        public void setLastPublishedTime(Instant lastPublishedTime) {
            this.mLastPublishedTime = Optional.of(lastPublishedTime);
        }

        public void increaseRetryCount() {
            int i = this.mRetryCount;
            if (i < 3) {
                this.mRetryCount = i + 1;
            }
            calcLatestPublishAllowedTime();
        }

        public void resetRetryCount() {
            this.mRetryCount = 0;
            calcLatestPublishAllowedTime();
        }

        public void resetState() {
            this.mLastPublishedTime = Optional.empty();
            this.mPublishAllowedTime = Optional.empty();
            this.mRcsPublishThrottle = UceUtils.getRcsPublishThrottle(this.mSubId);
            Log.d(PublishProcessorState.LOG_TAG, "RcsPublishThrottle=" + this.mRcsPublishThrottle);
        }

        public boolean isReachMaximumRetries() {
            return this.mRetryCount >= 3;
        }

        public void updatePublishThrottle(int publishThrottle) {
            this.mRcsPublishThrottle = publishThrottle;
            calcLatestPublishAllowedTime();
        }

        public boolean isPublishAllowedAtThisTime() {
            if (!this.mPublishAllowedTime.isPresent()) {
                return false;
            }
            return !Instant.now().isBefore(this.mPublishAllowedTime.get());
        }

        public void updatePublishingAllowedTime(int triggerType) {
            if (triggerType == 1) {
                this.mRetryCount = 0;
                this.mPublishAllowedTime = Optional.of(Instant.now());
            } else if (triggerType != 2) {
                resetRetryCount();
            }
        }

        public Optional<Long> getPublishingDelayTime() {
            if (!this.mPublishAllowedTime.isPresent()) {
                return Optional.empty();
            }
            long delayTime = ChronoUnit.MILLIS.between(Instant.now(), this.mPublishAllowedTime.get());
            if (delayTime < 0) {
                delayTime = 0;
            }
            return Optional.of(Long.valueOf(delayTime));
        }

        private void calcLatestPublishAllowedTime() {
            long retryDelay = getNextRetryDelayTime();
            if (!this.mLastPublishedTime.isPresent()) {
                this.mPublishAllowedTime = Optional.of(Instant.now().plus((TemporalAmount) Duration.ofMillis(retryDelay)));
                Log.d(PublishProcessorState.LOG_TAG, "calcLatestPublishAllowedTime: The last published time is empty");
            } else {
                Instant lastPublishedTime = this.mLastPublishedTime.get();
                Instant defaultAllowedTime = lastPublishedTime.plus((TemporalAmount) Duration.ofMillis(this.mRcsPublishThrottle));
                if (retryDelay == 0) {
                    this.mPublishAllowedTime = Optional.of(defaultAllowedTime);
                } else {
                    Instant retryDelayTime = Instant.now().plus((TemporalAmount) Duration.ofMillis(retryDelay));
                    this.mPublishAllowedTime = Optional.of(retryDelayTime.isAfter(defaultAllowedTime) ? retryDelayTime : defaultAllowedTime);
                }
            }
            Log.d(PublishProcessorState.LOG_TAG, "calcLatestPublishAllowedTime: " + this.mPublishAllowedTime.get());
        }

        private long getNextRetryDelayTime() {
            int i = this.mRetryCount;
            if (i == 0) {
                return 0L;
            }
            int power = i - 1;
            Double delayTime = Double.valueOf(Math.pow(2.0d, power) * 1.0d);
            return TimeUnit.MINUTES.toMillis(delayTime.longValue());
        }
    }

    public PublishProcessorState(int subId) {
        this.mPublishThrottle = new PublishThrottle(subId);
    }

    public long generatePublishTaskId() {
        long generateTaskId;
        synchronized (this.mLock) {
            generateTaskId = UceUtils.generateTaskId();
            this.mTaskId = generateTaskId;
        }
        return generateTaskId;
    }

    public long getCurrentTaskId() {
        long j;
        synchronized (this.mLock) {
            j = this.mTaskId;
        }
        return j;
    }

    public void setPublishingFlag(boolean flag) {
        this.mIsPublishing = flag;
    }

    public boolean isPublishingNow() {
        return this.mIsPublishing;
    }

    public void setPendingRequest(int triggerType) {
        this.mPendingRequest.setPendingRequest(triggerType);
    }

    public void clearPendingRequest() {
        this.mPendingRequest.clearPendingRequest();
    }

    public boolean hasPendingRequest() {
        return this.mPendingRequest.hasPendingRequest();
    }

    public Optional<Integer> getPendingRequestTriggerType() {
        return this.mPendingRequest.getPendingRequestTriggerType();
    }

    public void setLastPublishedTime(Instant lastPublishedTime) {
        synchronized (this.mLock) {
            this.mPublishThrottle.setLastPublishedTime(lastPublishedTime);
        }
    }

    public void increaseRetryCount() {
        synchronized (this.mLock) {
            this.mPublishThrottle.increaseRetryCount();
        }
    }

    public void resetRetryCount() {
        synchronized (this.mLock) {
            this.mPublishThrottle.resetRetryCount();
        }
    }

    public void resetState() {
        synchronized (this.mLock) {
            this.mPublishThrottle.resetState();
        }
    }

    public boolean isReachMaximumRetries() {
        boolean isReachMaximumRetries;
        synchronized (this.mLock) {
            isReachMaximumRetries = this.mPublishThrottle.isReachMaximumRetries();
        }
        return isReachMaximumRetries;
    }

    public boolean isPublishAllowedAtThisTime() {
        boolean isPublishAllowedAtThisTime;
        synchronized (this.mLock) {
            isPublishAllowedAtThisTime = this.mPublishThrottle.isPublishAllowedAtThisTime();
        }
        return isPublishAllowedAtThisTime;
    }

    public void updatePublishingAllowedTime(int triggerType) {
        synchronized (this.mLock) {
            this.mPublishThrottle.updatePublishingAllowedTime(triggerType);
        }
    }

    public Optional<Long> getPublishingDelayTime() {
        Optional<Long> publishingDelayTime;
        synchronized (this.mLock) {
            publishingDelayTime = this.mPublishThrottle.getPublishingDelayTime();
        }
        return publishingDelayTime;
    }

    public void updatePublishThrottle(int publishThrottle) {
        synchronized (this.mLock) {
            this.mPublishThrottle.updatePublishThrottle(publishThrottle);
        }
    }

    public void onRcsDisconnected() {
        synchronized (this.mLock) {
            setPublishingFlag(false);
            clearPendingRequest();
            this.mPublishThrottle.resetState();
        }
    }
}
