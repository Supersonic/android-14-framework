package com.android.ims.rcs.uce;

import android.content.Context;
import android.util.Log;
import com.android.ims.rcs.uce.UceController;
import com.android.ims.rcs.uce.util.NetworkSipCode;
import com.android.ims.rcs.uce.util.UceUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
/* loaded from: classes.dex */
public class UceDeviceState {
    private static final int DEVICE_STATE_BAD_EVENT = 3;
    private static final Map<Integer, String> DEVICE_STATE_DESCRIPTION;
    private static final int DEVICE_STATE_FORBIDDEN = 1;
    private static final int DEVICE_STATE_NO_RETRY = 4;
    private static final int DEVICE_STATE_OK = 0;
    private static final int DEVICE_STATE_PROVISION_ERROR = 2;
    private static final String LOG_TAG = UceUtils.getLogPrefix() + "UceDeviceState";
    private final Context mContext;
    private int mDeviceState;
    private Optional<Integer> mErrorCode;
    private Optional<Instant> mExitStateTime;
    private Optional<Instant> mRequestRetryTime;
    private final int mSubId;
    private final UceController.UceControllerCallback mUceCtrlCallback;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface DeviceStateType {
    }

    static {
        HashMap hashMap = new HashMap();
        DEVICE_STATE_DESCRIPTION = hashMap;
        hashMap.put(0, "DEVICE_STATE_OK");
        hashMap.put(1, "DEVICE_STATE_FORBIDDEN");
        hashMap.put(2, "DEVICE_STATE_PROVISION_ERROR");
        hashMap.put(3, "DEVICE_STATE_BAD_EVENT");
        hashMap.put(4, "DEVICE_STATE_NO_RETRY");
    }

    /* loaded from: classes.dex */
    public static class DeviceStateResult {
        final int mDeviceState;
        final Optional<Integer> mErrorCode;
        final Optional<Instant> mExitStateTime;
        final Optional<Instant> mRequestRetryTime;

        public DeviceStateResult(int deviceState, Optional<Integer> errorCode, Optional<Instant> requestRetryTime, Optional<Instant> exitStateTime) {
            this.mDeviceState = deviceState;
            this.mErrorCode = errorCode;
            this.mRequestRetryTime = requestRetryTime;
            this.mExitStateTime = exitStateTime;
        }

        public boolean isRequestForbidden() {
            switch (this.mDeviceState) {
                case 1:
                case 2:
                case 3:
                    return true;
                default:
                    return false;
            }
        }

        public boolean isPublishRequestBlocked() {
            switch (this.mDeviceState) {
                case 4:
                    return true;
                default:
                    return false;
            }
        }

        public int getDeviceState() {
            return this.mDeviceState;
        }

        public Optional<Integer> getErrorCode() {
            return this.mErrorCode;
        }

        public Optional<Instant> getRequestRetryTime() {
            return this.mRequestRetryTime;
        }

        public long getRequestRetryAfterMillis() {
            if (this.mRequestRetryTime.isPresent()) {
                long retryAfter = ChronoUnit.MILLIS.between(Instant.now(), this.mRequestRetryTime.get());
                if (retryAfter < 0) {
                    return 0L;
                }
                return retryAfter;
            }
            return 0L;
        }

        public Optional<Instant> getExitStateTime() {
            return this.mExitStateTime;
        }

        public boolean isDeviceStateEqual(DeviceStateResult otherDeviceState) {
            if (this.mDeviceState == otherDeviceState.getDeviceState() && this.mErrorCode.equals(otherDeviceState.getErrorCode()) && this.mRequestRetryTime.equals(otherDeviceState.getRequestRetryTime()) && this.mExitStateTime.equals(otherDeviceState.getExitStateTime())) {
                return true;
            }
            return false;
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("DeviceState=").append((String) UceDeviceState.DEVICE_STATE_DESCRIPTION.get(Integer.valueOf(getDeviceState()))).append(", ErrorCode=").append(getErrorCode()).append(", RetryTime=").append(getRequestRetryTime()).append(", retryAfterMillis=").append(getRequestRetryAfterMillis()).append(", ExitStateTime=").append(getExitStateTime());
            return builder.toString();
        }
    }

    public UceDeviceState(int subId, Context context, UceController.UceControllerCallback uceCtrlCallback) {
        this.mSubId = subId;
        this.mContext = context;
        this.mUceCtrlCallback = uceCtrlCallback;
        boolean restoreFromPref = false;
        Optional<DeviceStateResult> deviceState = UceUtils.restoreDeviceState(context, subId);
        if (deviceState.isPresent()) {
            restoreFromPref = true;
            this.mDeviceState = deviceState.get().getDeviceState();
            this.mErrorCode = deviceState.get().getErrorCode();
            this.mRequestRetryTime = deviceState.get().getRequestRetryTime();
            this.mExitStateTime = deviceState.get().getExitStateTime();
        } else {
            this.mDeviceState = 0;
            this.mErrorCode = Optional.empty();
            this.mRequestRetryTime = Optional.empty();
            this.mExitStateTime = Optional.empty();
        }
        logd("UceDeviceState: restore from sharedPref=" + restoreFromPref + ", " + getCurrentState());
    }

    public synchronized void checkSendResetDeviceStateTimer() {
        logd("checkSendResetDeviceStateTimer: time=" + this.mExitStateTime);
        if (this.mExitStateTime.isPresent()) {
            long expirySec = ChronoUnit.SECONDS.between(Instant.now(), this.mExitStateTime.get());
            if (expirySec < 0) {
                expirySec = 0;
            }
            this.mUceCtrlCallback.setupResetDeviceStateTimer(expirySec);
        }
    }

    public synchronized DeviceStateResult getCurrentState() {
        return new DeviceStateResult(this.mDeviceState, this.mErrorCode, this.mRequestRetryTime, this.mExitStateTime);
    }

    public synchronized void refreshDeviceState(int sipCode, String reason, int requestType) {
        logd("refreshDeviceState: sipCode=" + sipCode + ", reason=" + reason + ", requestResponseType=" + UceController.REQUEST_TYPE_DESCRIPTION.get(Integer.valueOf(requestType)));
        DeviceStateResult previousState = getCurrentState();
        switch (sipCode) {
            case NetworkSipCode.SIP_CODE_OK /* 200 */:
            case NetworkSipCode.SIP_CODE_ACCEPTED /* 202 */:
                resetInternal();
                break;
            case NetworkSipCode.SIP_CODE_FORBIDDEN /* 403 */:
            case NetworkSipCode.SIP_CODE_SERVER_TIMEOUT /* 504 */:
                if (requestType == 1) {
                    setDeviceState(2);
                    updateErrorCode(sipCode, reason, requestType);
                    removeRequestRetryTime();
                    removeExitStateTimer();
                    break;
                }
                break;
            case NetworkSipCode.SIP_CODE_NOT_FOUND /* 404 */:
                if (requestType == 1) {
                    setDeviceState(2);
                    updateErrorCode(sipCode, reason, requestType);
                    removeRequestRetryTime();
                    removeExitStateTimer();
                    break;
                }
                break;
            case NetworkSipCode.SIP_CODE_REQUEST_ENTITY_TOO_LARGE /* 413 */:
            case NetworkSipCode.SIP_CODE_TEMPORARILY_UNAVAILABLE /* 480 */:
            case NetworkSipCode.SIP_CODE_BUSY /* 486 */:
            case NetworkSipCode.SIP_CODE_SERVER_INTERNAL_ERROR /* 500 */:
            case NetworkSipCode.SIP_CODE_SERVICE_UNAVAILABLE /* 503 */:
            case NetworkSipCode.SIP_CODE_BUSY_EVERYWHERE /* 600 */:
            case NetworkSipCode.SIP_CODE_DECLINE /* 603 */:
                if (requestType == 1) {
                    setDeviceState(4);
                    removeRequestRetryTime();
                    removeExitStateTimer();
                    break;
                }
                break;
            case NetworkSipCode.SIP_CODE_BAD_EVENT /* 489 */:
                if (UceUtils.isRequestForbiddenBySip489(this.mContext, this.mSubId)) {
                    setDeviceState(3);
                    updateErrorCode(sipCode, reason, requestType);
                    setupRequestRetryTime();
                    setupExitStateTimer();
                    break;
                }
                break;
        }
        DeviceStateResult currentState = getCurrentState();
        if (!currentState.isRequestForbidden()) {
            removeDeviceStateFromPreference();
        } else if (!currentState.isDeviceStateEqual(previousState)) {
            saveDeviceStateToPreference(currentState);
        }
        logd("refreshDeviceState: previous: " + previousState + ", current: " + currentState);
    }

    public synchronized void resetDeviceState() {
        DeviceStateResult previousState = getCurrentState();
        resetInternal();
        DeviceStateResult currentState = getCurrentState();
        removeDeviceStateFromPreference();
        logd("resetDeviceState: previous=" + previousState + ", current=" + currentState);
    }

    private void resetInternal() {
        setDeviceState(0);
        resetErrorCode();
        removeRequestRetryTime();
        removeExitStateTimer();
    }

    private void setDeviceState(int latestState) {
        if (this.mDeviceState != latestState) {
            this.mDeviceState = latestState;
        }
    }

    private void updateErrorCode(int sipCode, String reason, int requestType) {
        Optional<Integer> newErrorCode = Optional.of(Integer.valueOf(NetworkSipCode.getCapabilityErrorFromSipCode(sipCode, reason, requestType)));
        if (!this.mErrorCode.equals(newErrorCode)) {
            this.mErrorCode = newErrorCode;
        }
    }

    private void resetErrorCode() {
        if (this.mErrorCode.isPresent()) {
            this.mErrorCode = Optional.empty();
        }
    }

    private void setupRequestRetryTime() {
        if (!this.mRequestRetryTime.isPresent() || this.mRequestRetryTime.get().isAfter(Instant.now())) {
            long retryInterval = UceUtils.getRequestRetryInterval(this.mContext, this.mSubId);
            this.mRequestRetryTime = Optional.of(Instant.now().plusMillis(retryInterval));
        }
    }

    private void removeRequestRetryTime() {
        if (this.mRequestRetryTime.isPresent()) {
            this.mRequestRetryTime = Optional.empty();
        }
    }

    private void setupExitStateTimer() {
        if (!this.mExitStateTime.isPresent()) {
            long expirySec = UceUtils.getNonRcsCapabilitiesCacheExpiration(this.mContext, this.mSubId);
            this.mExitStateTime = Optional.of(Instant.now().plusSeconds(expirySec));
            logd("setupExitStateTimer: expirationSec=" + expirySec + ", time=" + this.mExitStateTime);
            this.mUceCtrlCallback.setupResetDeviceStateTimer(expirySec);
        }
    }

    private void removeExitStateTimer() {
        if (this.mExitStateTime.isPresent()) {
            this.mExitStateTime = Optional.empty();
            this.mUceCtrlCallback.clearResetDeviceStateTimer();
        }
    }

    private void saveDeviceStateToPreference(DeviceStateResult deviceState) {
        boolean result = UceUtils.saveDeviceStateToPreference(this.mContext, this.mSubId, deviceState);
        logd("saveDeviceStateToPreference: result=" + result + ", state= " + deviceState);
    }

    private void removeDeviceStateFromPreference() {
        boolean result = UceUtils.removeDeviceStateFromPreference(this.mContext, this.mSubId);
        logd("removeDeviceStateFromPreference: result=" + result);
    }

    private void logd(String log) {
        Log.d(LOG_TAG, getLogPrefix().append(log).toString());
    }

    private StringBuilder getLogPrefix() {
        StringBuilder builder = new StringBuilder("[");
        builder.append(this.mSubId);
        builder.append("] ");
        return builder;
    }
}
