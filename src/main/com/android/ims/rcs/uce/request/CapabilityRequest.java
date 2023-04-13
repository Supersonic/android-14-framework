package com.android.ims.rcs.uce.request;

import android.net.Uri;
import android.telephony.ims.RcsContactUceCapability;
import android.util.Log;
import com.android.ims.rcs.uce.UceDeviceState;
import com.android.ims.rcs.uce.eab.EabCapabilityResult;
import com.android.ims.rcs.uce.presence.pidfparser.PidfParserUtils;
import com.android.ims.rcs.uce.request.UceRequestManager;
import com.android.ims.rcs.uce.util.UceUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public abstract class CapabilityRequest implements UceRequest {
    private static final String LOG_TAG = UceUtils.getLogPrefix() + "CapabilityRequest";
    protected volatile long mCoordinatorId;
    protected volatile boolean mIsFinished;
    protected final UceRequestManager.RequestManagerCallback mRequestManagerCallback;
    protected final CapabilityRequestResponse mRequestResponse;
    protected final int mRequestType;
    protected volatile boolean mSkipGettingFromCache;
    protected final int mSubId;
    protected final long mTaskId;
    protected final List<Uri> mUriList;

    protected abstract void requestCapabilities(List<Uri> list);

    public CapabilityRequest(int subId, int type, UceRequestManager.RequestManagerCallback callback) {
        this.mSubId = subId;
        this.mRequestType = type;
        this.mUriList = new ArrayList();
        this.mRequestManagerCallback = callback;
        this.mRequestResponse = new CapabilityRequestResponse();
        this.mTaskId = UceUtils.generateTaskId();
    }

    public CapabilityRequest(int subId, int type, UceRequestManager.RequestManagerCallback callback, CapabilityRequestResponse requestResponse) {
        this.mSubId = subId;
        this.mRequestType = type;
        this.mUriList = new ArrayList();
        this.mRequestManagerCallback = callback;
        this.mRequestResponse = requestResponse;
        this.mTaskId = UceUtils.generateTaskId();
    }

    @Override // com.android.ims.rcs.uce.request.UceRequest
    public void setRequestCoordinatorId(long coordinatorId) {
        this.mCoordinatorId = coordinatorId;
    }

    @Override // com.android.ims.rcs.uce.request.UceRequest
    public long getRequestCoordinatorId() {
        return this.mCoordinatorId;
    }

    @Override // com.android.ims.rcs.uce.request.UceRequest
    public long getTaskId() {
        return this.mTaskId;
    }

    @Override // com.android.ims.rcs.uce.request.UceRequest
    public void onFinish() {
        this.mIsFinished = true;
        this.mRequestManagerCallback.removeRequestTimeoutTimer(this.mTaskId);
    }

    @Override // com.android.ims.rcs.uce.request.UceRequest
    public void setContactUri(List<Uri> uris) {
        this.mUriList.addAll(uris);
        this.mRequestResponse.setRequestContacts(uris);
    }

    public List<Uri> getContactUri() {
        return Collections.unmodifiableList(this.mUriList);
    }

    public void setSkipGettingFromCache(boolean skipFromCache) {
        this.mSkipGettingFromCache = skipFromCache;
    }

    private boolean isSkipGettingFromCache() {
        return this.mSkipGettingFromCache;
    }

    public CapabilityRequestResponse getRequestResponse() {
        return this.mRequestResponse;
    }

    @Override // com.android.ims.rcs.uce.request.UceRequest
    public void executeRequest() {
        if (!isRequestAllowed()) {
            logd("executeRequest: The request is not allowed.");
            this.mRequestManagerCallback.notifyRequestError(this.mCoordinatorId, this.mTaskId);
            return;
        }
        List<EabCapabilityResult> eabResultList = getCapabilitiesFromCache();
        List<RcsContactUceCapability> cachedCapList = isSkipGettingFromCache() ? Collections.EMPTY_LIST : getUnexpiredCapabilities(eabResultList);
        this.mRequestResponse.addCachedCapabilities(cachedCapList);
        logd("executeRequest: cached capabilities size=" + cachedCapList.size());
        List<Uri> expiredUris = getRequestingFromNetworkUris(cachedCapList);
        List<RcsContactUceCapability> throttlingUris = getFromThrottlingList(expiredUris, eabResultList);
        this.mRequestResponse.addCachedCapabilities(throttlingUris);
        logd("executeRequest: contacts in throttling list size=" + throttlingUris.size());
        if (!cachedCapList.isEmpty() || !throttlingUris.isEmpty()) {
            this.mRequestManagerCallback.notifyCachedCapabilitiesUpdated(this.mCoordinatorId, this.mTaskId);
        }
        List<Uri> requestCapUris = getRequestingFromNetworkUris(cachedCapList, throttlingUris);
        logd("executeRequest: requestCapUris size=" + requestCapUris.size());
        if (requestCapUris.isEmpty()) {
            this.mRequestManagerCallback.notifyNoNeedRequestFromNetwork(this.mCoordinatorId, this.mTaskId);
        } else {
            requestCapabilities(requestCapUris);
        }
    }

    private boolean isRequestAllowed() {
        List<Uri> list = this.mUriList;
        if (list == null || list.isEmpty()) {
            logw("isRequestAllowed: uri is empty");
            this.mRequestResponse.setRequestInternalError(1);
            return false;
        } else if (this.mIsFinished) {
            logw("isRequestAllowed: This request is finished");
            this.mRequestResponse.setRequestInternalError(1);
            return false;
        } else {
            UceDeviceState.DeviceStateResult deviceStateResult = this.mRequestManagerCallback.getDeviceState();
            if (deviceStateResult.isRequestForbidden()) {
                logw("isRequestAllowed: The device is disallowed.");
                this.mRequestResponse.setRequestInternalError(deviceStateResult.getErrorCode().orElse(1).intValue());
                return false;
            }
            return true;
        }
    }

    private List<EabCapabilityResult> getCapabilitiesFromCache() {
        List<EabCapabilityResult> resultList = null;
        int i = this.mRequestType;
        if (i == 1) {
            resultList = this.mRequestManagerCallback.getCapabilitiesFromCacheIncludingExpired(this.mUriList);
        } else if (i == 2) {
            Uri uri = this.mUriList.get(0);
            EabCapabilityResult eabResult = this.mRequestManagerCallback.getAvailabilityFromCacheIncludingExpired(uri);
            resultList = new ArrayList();
            resultList.add(eabResult);
        }
        if (resultList == null) {
            return Collections.emptyList();
        }
        return resultList;
    }

    private List<RcsContactUceCapability> getUnexpiredCapabilities(List<EabCapabilityResult> list) {
        return (List) list.stream().filter(new CapabilityRequest$$ExternalSyntheticLambda0()).filter(new Predicate() { // from class: com.android.ims.rcs.uce.request.CapabilityRequest$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return CapabilityRequest.lambda$getUnexpiredCapabilities$0((EabCapabilityResult) obj);
            }
        }).map(new CapabilityRequest$$ExternalSyntheticLambda2()).filter(new CapabilityRequest$$ExternalSyntheticLambda3()).collect(Collectors.toList());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$getUnexpiredCapabilities$0(EabCapabilityResult result) {
        return result.getStatus() == 0;
    }

    private List<Uri> getRequestingFromNetworkUris(final List<RcsContactUceCapability> cachedCapList) {
        return (List) this.mUriList.stream().filter(new Predicate() { // from class: com.android.ims.rcs.uce.request.CapabilityRequest$$ExternalSyntheticLambda6
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean noneMatch;
                noneMatch = cachedCapList.stream().noneMatch(new Predicate() { // from class: com.android.ims.rcs.uce.request.CapabilityRequest$$ExternalSyntheticLambda4
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj2) {
                        boolean equals;
                        equals = ((RcsContactUceCapability) obj2).getContactUri().equals(r1);
                        return equals;
                    }
                });
                return noneMatch;
            }
        }).collect(Collectors.toList());
    }

    private List<Uri> getRequestingFromNetworkUris(List<RcsContactUceCapability> cachedCapList, List<RcsContactUceCapability> throttlingUris) {
        List<RcsContactUceCapability> notNetworkQueryList = new ArrayList<>(cachedCapList);
        notNetworkQueryList.addAll(throttlingUris);
        return getRequestingFromNetworkUris(notNetworkQueryList);
    }

    private List<RcsContactUceCapability> getFromThrottlingList(List<Uri> expiredUris, List<EabCapabilityResult> eabResultList) {
        final List<RcsContactUceCapability> resultList = new ArrayList<>();
        final List<RcsContactUceCapability> notFoundFromCacheList = new ArrayList<>();
        List<Uri> throttlingUris = this.mRequestManagerCallback.getInThrottlingListUris(expiredUris);
        List<EabCapabilityResult> throttlingUriFoundInEab = new ArrayList<>();
        for (Uri uri : throttlingUris) {
            Iterator<EabCapabilityResult> it = eabResultList.iterator();
            while (true) {
                if (it.hasNext()) {
                    EabCapabilityResult eabResult = it.next();
                    if (eabResult.getContact().equals(uri)) {
                        throttlingUriFoundInEab.add(eabResult);
                        break;
                    }
                }
            }
        }
        throttlingUriFoundInEab.forEach(new Consumer() { // from class: com.android.ims.rcs.uce.request.CapabilityRequest$$ExternalSyntheticLambda5
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                CapabilityRequest.lambda$getFromThrottlingList$3(resultList, notFoundFromCacheList, (EabCapabilityResult) obj);
            }
        });
        if (!notFoundFromCacheList.isEmpty()) {
            resultList.addAll(notFoundFromCacheList);
        }
        logd("getFromThrottlingList: requesting uris in the list size=" + throttlingUris.size() + ", generate non-RCS size=" + notFoundFromCacheList.size());
        return resultList;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$getFromThrottlingList$3(List resultList, List notFoundFromCacheList, EabCapabilityResult eabResult) {
        if (eabResult.getStatus() == 0 || eabResult.getStatus() == 2) {
            resultList.add(eabResult.getContactCapabilities());
        } else {
            notFoundFromCacheList.add(PidfParserUtils.getNotFoundContactCapabilities(eabResult.getContact()));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setupRequestTimeoutTimer() {
        long timeoutAfterMs = UceUtils.getCapRequestTimeoutAfterMillis();
        logd("setupRequestTimeoutTimer(ms): " + timeoutAfterMs);
        this.mRequestManagerCallback.setRequestTimeoutTimer(this.mCoordinatorId, this.mTaskId, timeoutAfterMs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void logd(String log) {
        Log.d(LOG_TAG, getLogPrefix().append(log).toString());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void logw(String log) {
        Log.w(LOG_TAG, getLogPrefix().append(log).toString());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void logi(String log) {
        Log.i(LOG_TAG, getLogPrefix().append(log).toString());
    }

    private StringBuilder getLogPrefix() {
        StringBuilder builder = new StringBuilder("[");
        builder.append(this.mSubId).append("][taskId=").append(this.mTaskId).append("] ");
        return builder;
    }
}
