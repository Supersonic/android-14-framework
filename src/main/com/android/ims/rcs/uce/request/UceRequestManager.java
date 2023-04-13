package com.android.ims.rcs.uce.request;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.telephony.ims.RcsContactUceCapability;
import android.telephony.ims.SipDetails;
import android.telephony.ims.aidl.IOptionsRequestCallback;
import android.telephony.ims.aidl.IRcsUceControllerCallback;
import android.text.TextUtils;
import android.util.Log;
import com.android.i18n.phonenumbers.NumberParseException;
import com.android.i18n.phonenumbers.PhoneNumberUtil;
import com.android.i18n.phonenumbers.Phonenumber;
import com.android.ims.SomeArgs;
import com.android.ims.rcs.uce.UceController;
import com.android.ims.rcs.uce.UceDeviceState;
import com.android.ims.rcs.uce.eab.EabCapabilityResult;
import com.android.ims.rcs.uce.options.OptionsController;
import com.android.ims.rcs.uce.presence.subscribe.SubscribeController;
import com.android.ims.rcs.uce.request.OptionsRequestCoordinator;
import com.android.ims.rcs.uce.request.RemoteOptionsCoordinator;
import com.android.ims.rcs.uce.request.SubscribeRequestCoordinator;
import com.android.ims.rcs.uce.util.UceUtils;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class UceRequestManager {
    private static final boolean FEATURE_SHORTCUT_QUEUE_FOR_CACHED_CAPS = true;
    private static final String LOG_TAG = UceUtils.getLogPrefix() + "UceRequestManager";
    private static UceUtilsProxy sUceUtilsProxy = new UceUtilsProxy() { // from class: com.android.ims.rcs.uce.request.UceRequestManager.1
        @Override // com.android.ims.rcs.uce.request.UceRequestManager.UceUtilsProxy
        public boolean isPresenceCapExchangeEnabled(Context context, int subId) {
            return UceUtils.isPresenceCapExchangeEnabled(context, subId);
        }

        @Override // com.android.ims.rcs.uce.request.UceRequestManager.UceUtilsProxy
        public boolean isPresenceSupported(Context context, int subId) {
            return UceUtils.isPresenceSupported(context, subId);
        }

        @Override // com.android.ims.rcs.uce.request.UceRequestManager.UceUtilsProxy
        public boolean isSipOptionsSupported(Context context, int subId) {
            return UceUtils.isSipOptionsSupported(context, subId);
        }

        @Override // com.android.ims.rcs.uce.request.UceRequestManager.UceUtilsProxy
        public boolean isPresenceGroupSubscribeEnabled(Context context, int subId) {
            return UceUtils.isPresenceGroupSubscribeEnabled(context, subId);
        }

        @Override // com.android.ims.rcs.uce.request.UceRequestManager.UceUtilsProxy
        public int getRclMaxNumberEntries(int subId) {
            return UceUtils.getRclMaxNumberEntries(subId);
        }

        @Override // com.android.ims.rcs.uce.request.UceRequestManager.UceUtilsProxy
        public boolean isNumberBlocked(Context context, String phoneNumber) {
            return UceUtils.isNumberBlocked(context, phoneNumber);
        }
    };
    private final Context mContext;
    private UceController.UceControllerCallback mControllerCallback;
    private final UceRequestHandler mHandler;
    private volatile boolean mIsDestroyed;
    private OptionsController mOptionsCtrl;
    private RequestManagerCallback mRequestMgrCallback = new RequestManagerCallback() { // from class: com.android.ims.rcs.uce.request.UceRequestManager.2
        @Override // com.android.ims.rcs.uce.request.UceRequestManager.RequestManagerCallback
        public void notifySendingRequest(long coordinatorId, long taskId, long delayTimeMs) {
            UceRequestManager.this.mHandler.sendRequestMessage(Long.valueOf(coordinatorId), Long.valueOf(taskId), delayTimeMs);
        }

        @Override // com.android.ims.rcs.uce.request.UceRequestManager.RequestManagerCallback
        public List<EabCapabilityResult> getCapabilitiesFromCache(List<Uri> uriList) {
            return UceRequestManager.this.mControllerCallback.getCapabilitiesFromCache(uriList);
        }

        @Override // com.android.ims.rcs.uce.request.UceRequestManager.RequestManagerCallback
        public List<EabCapabilityResult> getCapabilitiesFromCacheIncludingExpired(List<Uri> uris) {
            return UceRequestManager.this.mControllerCallback.getCapabilitiesFromCacheIncludingExpired(uris);
        }

        @Override // com.android.ims.rcs.uce.request.UceRequestManager.RequestManagerCallback
        public EabCapabilityResult getAvailabilityFromCache(Uri uri) {
            return UceRequestManager.this.mControllerCallback.getAvailabilityFromCache(uri);
        }

        @Override // com.android.ims.rcs.uce.request.UceRequestManager.RequestManagerCallback
        public EabCapabilityResult getAvailabilityFromCacheIncludingExpired(Uri uri) {
            return UceRequestManager.this.mControllerCallback.getAvailabilityFromCacheIncludingExpired(uri);
        }

        @Override // com.android.ims.rcs.uce.request.UceRequestManager.RequestManagerCallback
        public void saveCapabilities(List<RcsContactUceCapability> contactCapabilities) {
            UceRequestManager.this.mControllerCallback.saveCapabilities(contactCapabilities);
        }

        @Override // com.android.ims.rcs.uce.request.UceRequestManager.RequestManagerCallback
        public RcsContactUceCapability getDeviceCapabilities(int mechanism) {
            return UceRequestManager.this.mControllerCallback.getDeviceCapabilities(mechanism);
        }

        @Override // com.android.ims.rcs.uce.request.UceRequestManager.RequestManagerCallback
        public UceDeviceState.DeviceStateResult getDeviceState() {
            return UceRequestManager.this.mControllerCallback.getDeviceState();
        }

        @Override // com.android.ims.rcs.uce.request.UceRequestManager.RequestManagerCallback
        public void refreshDeviceState(int sipCode, String reason) {
            UceRequestManager.this.mControllerCallback.refreshDeviceState(sipCode, reason, 2);
        }

        @Override // com.android.ims.rcs.uce.request.UceRequestManager.RequestManagerCallback
        public void notifyRequestError(long requestCoordinatorId, long taskId) {
            UceRequestManager.this.mHandler.sendRequestUpdatedMessage(Long.valueOf(requestCoordinatorId), Long.valueOf(taskId), 0);
        }

        @Override // com.android.ims.rcs.uce.request.UceRequestManager.RequestManagerCallback
        public void notifyCommandError(long requestCoordinatorId, long taskId) {
            UceRequestManager.this.mHandler.sendRequestUpdatedMessage(Long.valueOf(requestCoordinatorId), Long.valueOf(taskId), 1);
        }

        @Override // com.android.ims.rcs.uce.request.UceRequestManager.RequestManagerCallback
        public void notifyNetworkResponse(long requestCoordinatorId, long taskId) {
            UceRequestManager.this.mHandler.sendRequestUpdatedMessage(Long.valueOf(requestCoordinatorId), Long.valueOf(taskId), 2);
        }

        @Override // com.android.ims.rcs.uce.request.UceRequestManager.RequestManagerCallback
        public void notifyTerminated(long requestCoordinatorId, long taskId) {
            UceRequestManager.this.mHandler.sendRequestUpdatedMessage(Long.valueOf(requestCoordinatorId), Long.valueOf(taskId), 6);
        }

        @Override // com.android.ims.rcs.uce.request.UceRequestManager.RequestManagerCallback
        public void notifyResourceTerminated(long requestCoordinatorId, long taskId) {
            UceRequestManager.this.mHandler.sendRequestUpdatedMessage(Long.valueOf(requestCoordinatorId), Long.valueOf(taskId), 4);
        }

        @Override // com.android.ims.rcs.uce.request.UceRequestManager.RequestManagerCallback
        public void notifyCapabilitiesUpdated(long requestCoordinatorId, long taskId) {
            UceRequestManager.this.mHandler.sendRequestUpdatedMessage(Long.valueOf(requestCoordinatorId), Long.valueOf(taskId), 3);
        }

        @Override // com.android.ims.rcs.uce.request.UceRequestManager.RequestManagerCallback
        public void notifyCachedCapabilitiesUpdated(long requestCoordinatorId, long taskId) {
            UceRequestManager.this.mHandler.sendRequestUpdatedMessage(Long.valueOf(requestCoordinatorId), Long.valueOf(taskId), 5);
        }

        @Override // com.android.ims.rcs.uce.request.UceRequestManager.RequestManagerCallback
        public void notifyNoNeedRequestFromNetwork(long requestCoordinatorId, long taskId) {
            UceRequestManager.this.mHandler.sendRequestUpdatedMessage(Long.valueOf(requestCoordinatorId), Long.valueOf(taskId), 7);
        }

        @Override // com.android.ims.rcs.uce.request.UceRequestManager.RequestManagerCallback
        public void notifyRemoteRequestDone(long requestCoordinatorId, long taskId) {
            UceRequestManager.this.mHandler.sendRequestUpdatedMessage(Long.valueOf(requestCoordinatorId), Long.valueOf(taskId), 8);
        }

        @Override // com.android.ims.rcs.uce.request.UceRequestManager.RequestManagerCallback
        public void setRequestTimeoutTimer(long coordinatorId, long taskId, long timeoutAfterMs) {
            UceRequestManager.this.mHandler.sendRequestTimeoutTimerMessage(Long.valueOf(coordinatorId), Long.valueOf(taskId), Long.valueOf(timeoutAfterMs));
        }

        @Override // com.android.ims.rcs.uce.request.UceRequestManager.RequestManagerCallback
        public void removeRequestTimeoutTimer(long taskId) {
            UceRequestManager.this.mHandler.removeRequestTimeoutTimer(Long.valueOf(taskId));
        }

        @Override // com.android.ims.rcs.uce.request.UceRequestManager.RequestManagerCallback
        public void notifyUceRequestFinished(long requestCoordinatorId, long taskId) {
            UceRequestManager.this.mHandler.sendRequestFinishedMessage(Long.valueOf(requestCoordinatorId), Long.valueOf(taskId));
        }

        @Override // com.android.ims.rcs.uce.request.UceRequestManager.RequestManagerCallback
        public void notifyRequestCoordinatorFinished(long requestCoordinatorId) {
            UceRequestManager.this.mHandler.sendRequestCoordinatorFinishedMessage(Long.valueOf(requestCoordinatorId));
        }

        @Override // com.android.ims.rcs.uce.request.UceRequestManager.RequestManagerCallback
        public List<Uri> getInThrottlingListUris(List<Uri> uriList) {
            return UceRequestManager.this.mThrottlingList.getInThrottlingListUris(uriList);
        }

        @Override // com.android.ims.rcs.uce.request.UceRequestManager.RequestManagerCallback
        public void addToThrottlingList(List<Uri> uriList, int sipCode) {
            UceRequestManager.this.mThrottlingList.addToThrottlingList(uriList, sipCode);
        }
    };
    private final UceRequestRepository mRequestRepository;
    private final int mSubId;
    private SubscribeController mSubscribeCtrl;
    private final ContactThrottlingList mThrottlingList;

    /* loaded from: classes.dex */
    public interface RequestManagerCallback {
        void addToThrottlingList(List<Uri> list, int i);

        EabCapabilityResult getAvailabilityFromCache(Uri uri);

        EabCapabilityResult getAvailabilityFromCacheIncludingExpired(Uri uri);

        List<EabCapabilityResult> getCapabilitiesFromCache(List<Uri> list);

        List<EabCapabilityResult> getCapabilitiesFromCacheIncludingExpired(List<Uri> list);

        RcsContactUceCapability getDeviceCapabilities(int i);

        UceDeviceState.DeviceStateResult getDeviceState();

        List<Uri> getInThrottlingListUris(List<Uri> list);

        void notifyCachedCapabilitiesUpdated(long j, long j2);

        void notifyCapabilitiesUpdated(long j, long j2);

        void notifyCommandError(long j, long j2);

        void notifyNetworkResponse(long j, long j2);

        void notifyNoNeedRequestFromNetwork(long j, long j2);

        void notifyRemoteRequestDone(long j, long j2);

        void notifyRequestCoordinatorFinished(long j);

        void notifyRequestError(long j, long j2);

        void notifyResourceTerminated(long j, long j2);

        void notifySendingRequest(long j, long j2, long j3);

        void notifyTerminated(long j, long j2);

        void notifyUceRequestFinished(long j, long j2);

        void refreshDeviceState(int i, String str);

        void removeRequestTimeoutTimer(long j);

        void saveCapabilities(List<RcsContactUceCapability> list);

        void setRequestTimeoutTimer(long j, long j2, long j3);
    }

    /* loaded from: classes.dex */
    public interface UceUtilsProxy {
        int getRclMaxNumberEntries(int i);

        boolean isNumberBlocked(Context context, String str);

        boolean isPresenceCapExchangeEnabled(Context context, int i);

        boolean isPresenceGroupSubscribeEnabled(Context context, int i);

        boolean isPresenceSupported(Context context, int i);

        boolean isSipOptionsSupported(Context context, int i);
    }

    public void setsUceUtilsProxy(UceUtilsProxy uceUtilsProxy) {
        sUceUtilsProxy = uceUtilsProxy;
    }

    public UceRequestManager(Context context, int subId, Looper looper, UceController.UceControllerCallback c) {
        this.mSubId = subId;
        this.mContext = context;
        this.mControllerCallback = c;
        this.mHandler = new UceRequestHandler(this, looper);
        this.mThrottlingList = new ContactThrottlingList(subId);
        this.mRequestRepository = new UceRequestRepository(subId, this.mRequestMgrCallback);
        logi("create");
    }

    public UceRequestManager(Context context, int subId, Looper looper, UceController.UceControllerCallback c, UceRequestRepository requestRepository) {
        this.mSubId = subId;
        this.mContext = context;
        this.mControllerCallback = c;
        this.mHandler = new UceRequestHandler(this, looper);
        this.mRequestRepository = requestRepository;
        this.mThrottlingList = new ContactThrottlingList(subId);
    }

    public void setOptionsController(OptionsController controller) {
        this.mOptionsCtrl = controller;
    }

    public void setSubscribeController(SubscribeController controller) {
        this.mSubscribeCtrl = controller;
    }

    public void onDestroy() {
        logi("onDestroy");
        this.mIsDestroyed = FEATURE_SHORTCUT_QUEUE_FOR_CACHED_CAPS;
        this.mHandler.onDestroy();
        this.mThrottlingList.reset();
        this.mRequestRepository.onDestroy();
    }

    public void resetThrottlingList() {
        this.mThrottlingList.reset();
    }

    public void sendCapabilityRequest(List<Uri> uriList, boolean skipFromCache, IRcsUceControllerCallback callback) throws RemoteException {
        if (this.mIsDestroyed) {
            callback.onError(1, 0L, (SipDetails) null);
        } else {
            sendRequestInternal(1, uriList, skipFromCache, callback);
        }
    }

    public void sendAvailabilityRequest(Uri uri, IRcsUceControllerCallback callback) throws RemoteException {
        if (this.mIsDestroyed) {
            callback.onError(1, 0L, (SipDetails) null);
        } else {
            sendRequestInternal(2, Collections.singletonList(uri), false, callback);
        }
    }

    private void sendRequestInternal(int type, List<Uri> uriList, boolean skipFromCache, IRcsUceControllerCallback callback) throws RemoteException {
        UceRequestCoordinator requestCoordinator = null;
        List<Uri> nonCachedUris = uriList;
        if (!skipFromCache) {
            nonCachedUris = sendCachedCapInfoToRequester(type, uriList, callback);
            if (uriList.size() != nonCachedUris.size()) {
                logd("sendRequestInternal: shortcut queue for caps - request reduced from " + uriList.size() + " entries to " + nonCachedUris.size() + " entries");
            } else {
                logd("sendRequestInternal: shortcut queue for caps - no cached caps.");
            }
            if (nonCachedUris.isEmpty()) {
                logd("sendRequestInternal: shortcut complete, sending success result");
                callback.onComplete((SipDetails) null);
                return;
            }
        }
        if (sUceUtilsProxy.isPresenceCapExchangeEnabled(this.mContext, this.mSubId) && sUceUtilsProxy.isPresenceSupported(this.mContext, this.mSubId)) {
            requestCoordinator = createSubscribeRequestCoordinator(type, nonCachedUris, skipFromCache, callback);
        } else if (sUceUtilsProxy.isSipOptionsSupported(this.mContext, this.mSubId)) {
            requestCoordinator = createOptionsRequestCoordinator(type, nonCachedUris, callback);
        }
        if (requestCoordinator == null) {
            logw("sendRequestInternal: Neither Presence nor OPTIONS are supported");
            callback.onError(2, 0L, (SipDetails) null);
            return;
        }
        StringBuilder builder = new StringBuilder("sendRequestInternal: ");
        builder.append("requestType=").append(type).append(", requestCoordinatorId=").append(requestCoordinator.getCoordinatorId()).append(", taskId={").append((String) requestCoordinator.getActivatedRequestTaskIds().stream().map(new UceRequestManager$$ExternalSyntheticLambda0()).collect(Collectors.joining(","))).append("}");
        logd(builder.toString());
        addRequestCoordinator(requestCoordinator);
    }

    private List<Uri> sendCachedCapInfoToRequester(int type, List<Uri> uriList, IRcsUceControllerCallback callback) {
        List<Uri> nonCachedUris = new ArrayList<>(uriList);
        List<RcsContactUceCapability> numbersWithCachedCaps = getCapabilitiesFromCache(type, nonCachedUris);
        try {
            if (!numbersWithCachedCaps.isEmpty()) {
                logd("sendCachedCapInfoToRequester: cached caps found for " + numbersWithCachedCaps.size() + " entries. Notifying requester.");
                callback.onCapabilitiesReceived(numbersWithCachedCaps);
            }
        } catch (RemoteException e) {
            logw("sendCachedCapInfoToRequester, error sending cap info back to requester: " + e);
        }
        for (final RcsContactUceCapability c : numbersWithCachedCaps) {
            nonCachedUris.removeIf(new Predicate() { // from class: com.android.ims.rcs.uce.request.UceRequestManager$$ExternalSyntheticLambda4
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean equals;
                    equals = c.getContactUri().equals((Uri) obj);
                    return equals;
                }
            });
        }
        return nonCachedUris;
    }

    private List<RcsContactUceCapability> getCapabilitiesFromCache(int requestType, List<Uri> uriList) {
        List<EabCapabilityResult> resultList = Collections.emptyList();
        if (requestType == 1) {
            resultList = this.mRequestMgrCallback.getCapabilitiesFromCache(uriList);
        } else if (requestType == 2) {
            resultList = Collections.singletonList(this.mRequestMgrCallback.getAvailabilityFromCache(uriList.get(0)));
        }
        return (List) resultList.stream().filter(new CapabilityRequest$$ExternalSyntheticLambda0()).filter(new Predicate() { // from class: com.android.ims.rcs.uce.request.UceRequestManager$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return UceRequestManager.lambda$getCapabilitiesFromCache$1((EabCapabilityResult) obj);
            }
        }).map(new CapabilityRequest$$ExternalSyntheticLambda2()).filter(new CapabilityRequest$$ExternalSyntheticLambda3()).collect(Collectors.toList());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$getCapabilitiesFromCache$1(EabCapabilityResult result) {
        if (result.getStatus() == 0) {
            return FEATURE_SHORTCUT_QUEUE_FOR_CACHED_CAPS;
        }
        return false;
    }

    private UceRequestCoordinator createSubscribeRequestCoordinator(final int type, List<Uri> uriList, final boolean skipFromCache, IRcsUceControllerCallback callback) {
        SubscribeRequestCoordinator.Builder builder;
        if (!sUceUtilsProxy.isPresenceGroupSubscribeEnabled(this.mContext, this.mSubId)) {
            final List<UceRequest> requestList = new ArrayList<>();
            uriList.forEach(new Consumer() { // from class: com.android.ims.rcs.uce.request.UceRequestManager$$ExternalSyntheticLambda3
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    UceRequestManager.this.lambda$createSubscribeRequestCoordinator$2(type, skipFromCache, requestList, (Uri) obj);
                }
            });
            builder = new SubscribeRequestCoordinator.Builder(this.mSubId, requestList, this.mRequestMgrCallback);
            builder.setCapabilitiesCallback(callback);
        } else {
            List<UceRequest> requestList2 = new ArrayList<>();
            int rclMaxNumber = sUceUtilsProxy.getRclMaxNumberEntries(this.mSubId);
            int numRequestCoordinators = uriList.size() / rclMaxNumber;
            for (int count = 0; count < numRequestCoordinators; count++) {
                List<Uri> subUriList = new ArrayList<>();
                for (int index = 0; index < rclMaxNumber; index++) {
                    subUriList.add(uriList.get((count * rclMaxNumber) + index));
                }
                requestList2.add(createSubscribeRequest(type, subUriList, skipFromCache));
            }
            List<Uri> subUriList2 = new ArrayList<>();
            for (int i = numRequestCoordinators * rclMaxNumber; i < uriList.size(); i++) {
                subUriList2.add(uriList.get(i));
            }
            requestList2.add(createSubscribeRequest(type, subUriList2, skipFromCache));
            SubscribeRequestCoordinator.Builder builder2 = new SubscribeRequestCoordinator.Builder(this.mSubId, requestList2, this.mRequestMgrCallback);
            builder2.setCapabilitiesCallback(callback);
            builder = builder2;
        }
        return builder.build();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createSubscribeRequestCoordinator$2(int type, boolean skipFromCache, List requestList, Uri uri) {
        List<Uri> individualUri = Collections.singletonList(uri);
        List<RcsContactUceCapability> capabilities = getCapabilitiesFromCache(type, individualUri);
        if (!capabilities.isEmpty()) {
            RcsContactUceCapability capability = capabilities.get(0);
            Uri entityUri = capability.getEntityUri();
            if (entityUri != null) {
                individualUri = Collections.singletonList(entityUri);
            } else if (UceUtils.isSipUriForPresenceSubscribeEnabled(this.mContext, this.mSubId)) {
                individualUri = Collections.singletonList(getSipUriFromUri(uri));
            }
        } else if (UceUtils.isSipUriForPresenceSubscribeEnabled(this.mContext, this.mSubId)) {
            individualUri = Collections.singletonList(getSipUriFromUri(uri));
        }
        requestList.add(createSubscribeRequest(type, individualUri, skipFromCache));
    }

    private UceRequestCoordinator createOptionsRequestCoordinator(final int type, List<Uri> uriList, IRcsUceControllerCallback callback) {
        final List<UceRequest> requestList = new ArrayList<>();
        uriList.forEach(new Consumer() { // from class: com.android.ims.rcs.uce.request.UceRequestManager$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                UceRequestManager.this.lambda$createOptionsRequestCoordinator$3(type, requestList, (Uri) obj);
            }
        });
        OptionsRequestCoordinator.Builder builder = new OptionsRequestCoordinator.Builder(this.mSubId, requestList, this.mRequestMgrCallback);
        builder.setCapabilitiesCallback(callback);
        return builder.build();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createOptionsRequestCoordinator$3(int type, List requestList, Uri uri) {
        List<Uri> individualUri = Collections.singletonList(uri);
        requestList.add(createOptionsRequest(type, individualUri, false));
    }

    private CapabilityRequest createSubscribeRequest(int type, List<Uri> uriList, boolean skipFromCache) {
        CapabilityRequest request = new SubscribeRequest(this.mSubId, type, this.mRequestMgrCallback, this.mSubscribeCtrl);
        request.setContactUri(uriList);
        request.setSkipGettingFromCache(skipFromCache);
        return request;
    }

    private CapabilityRequest createOptionsRequest(int type, List<Uri> uriList, boolean skipFromCache) {
        CapabilityRequest request = new OptionsRequest(this.mSubId, type, this.mRequestMgrCallback, this.mOptionsCtrl);
        request.setContactUri(uriList);
        request.setSkipGettingFromCache(skipFromCache);
        return request;
    }

    public void retrieveCapabilitiesForRemote(Uri contactUri, List<String> remoteCapabilities, IOptionsRequestCallback requestCallback) {
        RemoteOptionsRequest request = new RemoteOptionsRequest(this.mSubId, this.mRequestMgrCallback);
        request.setContactUri(Collections.singletonList(contactUri));
        request.setRemoteFeatureTags(remoteCapabilities);
        String number = getNumberFromUri(contactUri);
        if (!TextUtils.isEmpty(number)) {
            request.setIsRemoteNumberBlocked(sUceUtilsProxy.isNumberBlocked(this.mContext, number));
        }
        RemoteOptionsCoordinator.Builder CoordBuilder = new RemoteOptionsCoordinator.Builder(this.mSubId, Collections.singletonList(request), this.mRequestMgrCallback);
        CoordBuilder.setOptionsRequestCallback(requestCallback);
        RemoteOptionsCoordinator requestCoordinator = CoordBuilder.build();
        StringBuilder builder = new StringBuilder("retrieveCapabilitiesForRemote: ");
        builder.append("requestCoordinatorId ").append(requestCoordinator.getCoordinatorId()).append(", taskId={").append((String) requestCoordinator.getActivatedRequestTaskIds().stream().map(new UceRequestManager$$ExternalSyntheticLambda0()).collect(Collectors.joining(","))).append("}");
        logd(builder.toString());
        addRequestCoordinator(requestCoordinator);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class UceRequestHandler extends Handler {
        private static final int EVENT_COORDINATOR_FINISHED = 5;
        private static Map<Integer, String> EVENT_DESCRIPTION = null;
        private static final int EVENT_EXECUTE_REQUEST = 1;
        private static final int EVENT_REQUEST_FINISHED = 4;
        private static final int EVENT_REQUEST_TIMEOUT = 3;
        private static final int EVENT_REQUEST_UPDATED = 2;
        private final Map<Long, SomeArgs> mRequestTimeoutTimers;
        private final WeakReference<UceRequestManager> mUceRequestMgrRef;

        public UceRequestHandler(UceRequestManager requestManager, Looper looper) {
            super(looper);
            this.mRequestTimeoutTimers = new HashMap();
            this.mUceRequestMgrRef = new WeakReference<>(requestManager);
        }

        public void sendRequestMessage(Long coordinatorId, Long taskId, long delayTimeMs) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = coordinatorId;
            args.arg2 = taskId;
            Message message = obtainMessage();
            message.what = 1;
            message.obj = args;
            sendMessageDelayed(message, delayTimeMs);
        }

        public void sendRequestUpdatedMessage(Long coordinatorId, Long taskId, int requestEvent) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = coordinatorId;
            args.arg2 = taskId;
            args.argi1 = requestEvent;
            Message message = obtainMessage();
            message.what = 2;
            message.obj = args;
            sendMessage(message);
        }

        public void sendRequestTimeoutTimerMessage(Long coordId, Long taskId, Long timeoutAfterMs) {
            synchronized (this.mRequestTimeoutTimers) {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = coordId;
                args.arg2 = taskId;
                this.mRequestTimeoutTimers.put(taskId, args);
                Message message = obtainMessage();
                message.what = 3;
                message.obj = args;
                sendMessageDelayed(message, timeoutAfterMs.longValue());
            }
        }

        public void removeRequestTimeoutTimer(Long taskId) {
            synchronized (this.mRequestTimeoutTimers) {
                SomeArgs args = this.mRequestTimeoutTimers.remove(taskId);
                if (args == null) {
                    return;
                }
                Log.d(UceRequestManager.LOG_TAG, "removeRequestTimeoutTimer: taskId=" + taskId);
                removeMessages(3, args);
                args.recycle();
            }
        }

        public void sendRequestFinishedMessage(Long coordinatorId, Long taskId) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = coordinatorId;
            args.arg2 = taskId;
            Message message = obtainMessage();
            message.what = 4;
            message.obj = args;
            sendMessage(message);
        }

        public void sendRequestCoordinatorFinishedMessage(Long coordinatorId) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = coordinatorId;
            Message message = obtainMessage();
            message.what = 5;
            message.obj = args;
            sendMessage(message);
        }

        public void onDestroy() {
            removeCallbacksAndMessages(null);
            synchronized (this.mRequestTimeoutTimers) {
                this.mRequestTimeoutTimers.forEach(new BiConsumer() { // from class: com.android.ims.rcs.uce.request.UceRequestManager$UceRequestHandler$$ExternalSyntheticLambda0
                    @Override // java.util.function.BiConsumer
                    public final void accept(Object obj, Object obj2) {
                        Long l = (Long) obj;
                        ((SomeArgs) obj2).recycle();
                    }
                });
                this.mRequestTimeoutTimers.clear();
            }
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            UceRequestManager requestManager = this.mUceRequestMgrRef.get();
            if (requestManager == null) {
                return;
            }
            SomeArgs args = (SomeArgs) msg.obj;
            Long coordinatorId = (Long) args.arg1;
            Long taskId = (Long) Optional.ofNullable(args.arg2).orElse(-1L);
            Integer requestEvent = (Integer) Optional.ofNullable(Integer.valueOf(args.argi1)).orElse(-1);
            args.recycle();
            requestManager.logd("handleMessage: " + EVENT_DESCRIPTION.get(Integer.valueOf(msg.what)) + ", coordinatorId=" + coordinatorId + ", taskId=" + taskId);
            switch (msg.what) {
                case 1:
                    UceRequest request = requestManager.getUceRequest(taskId);
                    if (request == null) {
                        requestManager.logw("handleMessage: cannot find request, taskId=" + taskId);
                        return;
                    } else {
                        request.executeRequest();
                        return;
                    }
                case 2:
                    UceRequestCoordinator requestCoordinator = requestManager.getRequestCoordinator(coordinatorId);
                    if (requestCoordinator == null) {
                        requestManager.logw("handleMessage: cannot find UceRequestCoordinator");
                        return;
                    } else {
                        requestCoordinator.onRequestUpdated(taskId.longValue(), requestEvent.intValue());
                        return;
                    }
                case 3:
                    UceRequestCoordinator requestCoordinator2 = requestManager.getRequestCoordinator(coordinatorId);
                    if (requestCoordinator2 == null) {
                        requestManager.logw("handleMessage: cannot find UceRequestCoordinator");
                        return;
                    }
                    synchronized (this.mRequestTimeoutTimers) {
                        this.mRequestTimeoutTimers.remove(taskId);
                    }
                    requestCoordinator2.onRequestUpdated(taskId.longValue(), 9);
                    return;
                case 4:
                    requestManager.notifyRepositoryRequestFinished(taskId);
                    return;
                case 5:
                    UceRequestCoordinator requestCoordinator3 = requestManager.removeRequestCoordinator(coordinatorId);
                    if (requestCoordinator3 != null) {
                        requestCoordinator3.onFinish();
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        static {
            HashMap hashMap = new HashMap();
            EVENT_DESCRIPTION = hashMap;
            hashMap.put(1, "EXECUTE_REQUEST");
            EVENT_DESCRIPTION.put(2, "REQUEST_UPDATE");
            EVENT_DESCRIPTION.put(3, "REQUEST_TIMEOUT");
            EVENT_DESCRIPTION.put(4, "REQUEST_FINISHED");
            EVENT_DESCRIPTION.put(5, "REMOVE_COORDINATOR");
        }
    }

    private void addRequestCoordinator(UceRequestCoordinator coordinator) {
        this.mRequestRepository.addRequestCoordinator(coordinator);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public UceRequestCoordinator removeRequestCoordinator(Long coordinatorId) {
        return this.mRequestRepository.removeRequestCoordinator(coordinatorId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public UceRequestCoordinator getRequestCoordinator(Long coordinatorId) {
        return this.mRequestRepository.getRequestCoordinator(coordinatorId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public UceRequest getUceRequest(Long taskId) {
        return this.mRequestRepository.getUceRequest(taskId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyRepositoryRequestFinished(Long taskId) {
        this.mRequestRepository.notifyRequestFinished(taskId);
    }

    private Uri getSipUriFromUri(Uri uri) {
        String[] numberParts = uri.getSchemeSpecificPart().split("[@;:]");
        String number = numberParts[0];
        TelephonyManager manager = (TelephonyManager) this.mContext.getSystemService(TelephonyManager.class);
        if (manager.getIsimDomain() == null) {
            return uri;
        }
        String simCountryIso = manager.getSimCountryIso();
        if (TextUtils.isEmpty(simCountryIso)) {
            return uri;
        }
        String simCountryIso2 = simCountryIso.toUpperCase();
        PhoneNumberUtil util = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber phoneNumber = util.parse(number, simCountryIso2);
            number = util.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
            String sipUri = "sip:" + number + "@" + manager.getIsimDomain();
            Uri convertedUri = Uri.parse(sipUri);
            return convertedUri;
        } catch (NumberParseException e) {
            Log.w(LOG_TAG, "formatNumber: could not format " + number + ", error: " + e);
            return uri;
        }
    }

    public UceRequestHandler getUceRequestHandler() {
        return this.mHandler;
    }

    public RequestManagerCallback getRequestManagerCallback() {
        return this.mRequestMgrCallback;
    }

    private void logi(String log) {
        Log.i(LOG_TAG, getLogPrefix().append(log).toString());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logd(String log) {
        Log.d(LOG_TAG, getLogPrefix().append(log).toString());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logw(String log) {
        Log.w(LOG_TAG, getLogPrefix().append(log).toString());
    }

    private StringBuilder getLogPrefix() {
        StringBuilder builder = new StringBuilder("[");
        builder.append(this.mSubId);
        builder.append("] ");
        return builder;
    }

    private String getNumberFromUri(Uri uri) {
        if (uri == null) {
            return null;
        }
        String number = uri.getSchemeSpecificPart();
        String[] numberParts = number.split("[@;:]");
        if (numberParts.length == 0) {
            return null;
        }
        return numberParts[0];
    }
}
