package com.android.ims.rcs.uce.presence.publish;

import android.telephony.ims.SipDetails;
import android.telephony.ims.aidl.IPublishResponseCallback;
import android.text.TextUtils;
import android.util.Log;
import com.android.ims.rcs.uce.presence.publish.PublishController;
import com.android.ims.rcs.uce.util.NetworkSipCode;
import com.android.ims.rcs.uce.util.UceUtils;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class PublishRequestResponse {
    private static final String LOG_TAG = UceUtils.getLogPrefix() + "PublishRequestResp";
    private Optional<Integer> mCmdErrorCode;
    private volatile boolean mNeedRetry;
    private Optional<Integer> mNetworkRespSipCode;
    private final String mPidfXml;
    private volatile PublishController.PublishControllerCallback mPublishCtrlCallback;
    private Optional<Integer> mReasonHeaderCause;
    private Optional<String> mReasonHeaderText;
    private Optional<String> mReasonPhrase;
    private IPublishResponseCallback mResponseCallback;
    private Instant mResponseTimestamp;
    private Optional<SipDetails> mSipDetails;
    private final long mTaskId;

    public PublishRequestResponse(PublishController.PublishControllerCallback publishCtrlCallback, long taskId, String pidfXml) {
        this.mResponseCallback = new IPublishResponseCallback.Stub() { // from class: com.android.ims.rcs.uce.presence.publish.PublishRequestResponse.1
            public void onCommandError(int code) {
                PublishRequestResponse.this.onCommandError(code);
            }

            public void onNetworkResponse(SipDetails details) {
                PublishRequestResponse.this.onNetworkResponse(details);
            }
        };
        this.mTaskId = taskId;
        this.mPidfXml = pidfXml;
        this.mPublishCtrlCallback = publishCtrlCallback;
        this.mCmdErrorCode = Optional.empty();
        this.mNetworkRespSipCode = Optional.empty();
        this.mReasonPhrase = Optional.empty();
        this.mReasonHeaderCause = Optional.empty();
        this.mReasonHeaderText = Optional.empty();
        this.mSipDetails = Optional.empty();
    }

    public PublishRequestResponse(String pidfXml, SipDetails details) {
        this.mResponseCallback = new IPublishResponseCallback.Stub() { // from class: com.android.ims.rcs.uce.presence.publish.PublishRequestResponse.1
            public void onCommandError(int code) {
                PublishRequestResponse.this.onCommandError(code);
            }

            public void onNetworkResponse(SipDetails details2) {
                PublishRequestResponse.this.onNetworkResponse(details2);
            }
        };
        this.mTaskId = 0L;
        this.mPublishCtrlCallback = null;
        this.mCmdErrorCode = Optional.empty();
        this.mPidfXml = pidfXml;
        this.mResponseTimestamp = Instant.now();
        this.mNetworkRespSipCode = Optional.of(Integer.valueOf(details.getResponseCode()));
        this.mReasonPhrase = Optional.ofNullable(details.getResponsePhrase());
        if (details.getReasonHeaderCause() != 0) {
            this.mReasonHeaderCause = Optional.of(Integer.valueOf(details.getReasonHeaderCause()));
        } else {
            this.mReasonHeaderCause = Optional.empty();
        }
        if (TextUtils.isEmpty(details.getReasonHeaderText())) {
            this.mReasonHeaderText = Optional.empty();
        } else {
            this.mReasonHeaderText = Optional.ofNullable(details.getReasonHeaderText());
        }
        this.mSipDetails = Optional.ofNullable(details);
    }

    public IPublishResponseCallback getResponseCallback() {
        return this.mResponseCallback;
    }

    public long getTaskId() {
        return this.mTaskId;
    }

    public Optional<Integer> getCmdErrorCode() {
        return this.mCmdErrorCode;
    }

    public Optional<Integer> getNetworkRespSipCode() {
        return this.mNetworkRespSipCode;
    }

    public Optional<String> getReasonPhrase() {
        return this.mReasonPhrase;
    }

    public Optional<Integer> getReasonHeaderCause() {
        return this.mReasonHeaderCause;
    }

    public Optional<String> getReasonHeaderText() {
        return this.mReasonHeaderText;
    }

    public Optional<SipDetails> getSipDetails() {
        return this.mSipDetails;
    }

    public Optional<Integer> getResponseSipCode() {
        return this.mReasonHeaderCause.isPresent() ? this.mReasonHeaderCause : this.mNetworkRespSipCode;
    }

    public Optional<String> getResponseReason() {
        return this.mReasonHeaderText.isPresent() ? this.mReasonHeaderText : this.mReasonPhrase;
    }

    public Instant getResponseTimestamp() {
        return this.mResponseTimestamp;
    }

    public String getPidfXml() {
        return this.mPidfXml;
    }

    public void onDestroy() {
        this.mPublishCtrlCallback = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCommandError(int errorCode) {
        this.mResponseTimestamp = Instant.now();
        this.mCmdErrorCode = Optional.of(Integer.valueOf(errorCode));
        updateRetryFlagByCommandError();
        PublishController.PublishControllerCallback ctrlCallback = this.mPublishCtrlCallback;
        if (ctrlCallback != null) {
            ctrlCallback.onRequestCommandError(this);
        } else {
            Log.d(LOG_TAG, "onCommandError: already destroyed. error code=" + errorCode);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNetworkResponse(SipDetails details) {
        if (details.getResponseCode() == 999) {
            onCommandError(10);
            return;
        }
        this.mResponseTimestamp = Instant.now();
        this.mNetworkRespSipCode = Optional.of(Integer.valueOf(details.getResponseCode()));
        this.mReasonPhrase = Optional.ofNullable(details.getResponsePhrase());
        if (details.getReasonHeaderCause() != 0) {
            this.mReasonHeaderCause = Optional.of(Integer.valueOf(details.getReasonHeaderCause()));
        }
        if (TextUtils.isEmpty(details.getReasonHeaderText())) {
            this.mReasonHeaderText = Optional.empty();
        } else {
            this.mReasonHeaderText = Optional.ofNullable(details.getReasonHeaderText());
        }
        this.mSipDetails = Optional.ofNullable(details);
        updateRetryFlagByNetworkResponse();
        PublishController.PublishControllerCallback ctrlCallback = this.mPublishCtrlCallback;
        if (ctrlCallback != null) {
            ctrlCallback.onRequestNetworkResp(this);
        } else {
            Log.d(LOG_TAG, "onNetworkResponse: already destroyed. sip code=" + details.getResponseCode());
        }
    }

    private void updateRetryFlagByCommandError() {
        switch (getCmdErrorCode().orElse(-1).intValue()) {
            case 4:
            case 5:
            case 6:
            case 9:
                this.mNeedRetry = true;
                return;
            case 7:
            case 8:
            default:
                return;
        }
    }

    private void updateRetryFlagByNetworkResponse() {
        this.mNeedRetry = false;
    }

    public boolean isRequestSuccess() {
        if (isCommandError()) {
            return false;
        }
        if (isCommandCodeNoChange()) {
            return true;
        }
        return getNetworkRespSipCode().filter(new Predicate() { // from class: com.android.ims.rcs.uce.presence.publish.PublishRequestResponse$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return PublishRequestResponse.lambda$isRequestSuccess$0((Integer) obj);
            }
        }).isPresent() && (!getReasonHeaderCause().isPresent() || getReasonHeaderCause().filter(new Predicate() { // from class: com.android.ims.rcs.uce.presence.publish.PublishRequestResponse$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return PublishRequestResponse.lambda$isRequestSuccess$1((Integer) obj);
            }
        }).isPresent());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$isRequestSuccess$0(Integer c) {
        return c.intValue() == 200;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$isRequestSuccess$1(Integer c) {
        return c.intValue() == 200;
    }

    private boolean isCommandError() {
        if (getCmdErrorCode().isPresent() && !isCommandCodeNoChange()) {
            return true;
        }
        return false;
    }

    private boolean isCommandCodeNoChange() {
        if (getCmdErrorCode().filter(new Predicate() { // from class: com.android.ims.rcs.uce.presence.publish.PublishRequestResponse$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return PublishRequestResponse.lambda$isCommandCodeNoChange$2((Integer) obj);
            }
        }).isPresent()) {
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$isCommandCodeNoChange$2(Integer code) {
        return code.intValue() == 10;
    }

    public boolean needRetry() {
        return this.mNeedRetry;
    }

    public int getPublishState() {
        if (isCommandError()) {
            return getPublishStateByCmdErrorCode();
        }
        return getPublishStateByNetworkResponse();
    }

    private int getPublishStateByCmdErrorCode() {
        if (getCmdErrorCode().orElse(-1).intValue() == 4) {
            return 5;
        }
        return 6;
    }

    private int getPublishStateByNetworkResponse() {
        int respSipCode;
        if (isCommandCodeNoChange()) {
            respSipCode = NetworkSipCode.SIP_CODE_OK;
        } else if (getReasonHeaderCause().isPresent()) {
            respSipCode = getReasonHeaderCause().get().intValue();
        } else {
            respSipCode = getNetworkRespSipCode().orElse(-1).intValue();
        }
        switch (respSipCode) {
            case NetworkSipCode.SIP_CODE_OK /* 200 */:
                return 1;
            case NetworkSipCode.SIP_CODE_FORBIDDEN /* 403 */:
            case NetworkSipCode.SIP_CODE_NOT_FOUND /* 404 */:
            case NetworkSipCode.SIP_CODE_SERVER_TIMEOUT /* 504 */:
                return 4;
            case NetworkSipCode.SIP_CODE_REQUEST_TIMEOUT /* 408 */:
                return 5;
            default:
                return 6;
        }
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("taskId=").append(this.mTaskId).append(", CmdErrorCode=").append(getCmdErrorCode().orElse(-1)).append(", NetworkRespSipCode=").append(getNetworkRespSipCode().orElse(-1)).append(", ReasonPhrase=").append(getReasonPhrase().orElse("")).append(", ReasonHeaderCause=").append(getReasonHeaderCause().orElse(-1)).append(", ReasonHeaderText=").append(getReasonHeaderText().orElse("")).append(", ResponseTimestamp=").append(this.mResponseTimestamp).append(", isRequestSuccess=").append(isRequestSuccess()).append(", needRetry=").append(this.mNeedRetry);
        return builder.toString();
    }
}
