package com.android.ims.rcs.uce;

import android.content.Context;
import android.net.Uri;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.RemoteException;
import android.telephony.ims.RcsContactUceCapability;
import android.telephony.ims.SipDetails;
import android.telephony.ims.aidl.IOptionsRequestCallback;
import android.telephony.ims.aidl.IRcsUceControllerCallback;
import android.telephony.ims.aidl.IRcsUcePublishStateCallback;
import android.util.IndentingPrintWriter;
import android.util.LocalLog;
import android.util.Log;
import com.android.ims.RcsFeatureManager;
import com.android.ims.SomeArgs;
import com.android.ims.rcs.uce.UceController;
import com.android.ims.rcs.uce.UceDeviceState;
import com.android.ims.rcs.uce.eab.EabCapabilityResult;
import com.android.ims.rcs.uce.eab.EabController;
import com.android.ims.rcs.uce.eab.EabControllerImpl;
import com.android.ims.rcs.uce.options.OptionsController;
import com.android.ims.rcs.uce.options.OptionsControllerImpl;
import com.android.ims.rcs.uce.presence.publish.PublishController;
import com.android.ims.rcs.uce.presence.publish.PublishControllerImpl;
import com.android.ims.rcs.uce.presence.subscribe.SubscribeController;
import com.android.ims.rcs.uce.presence.subscribe.SubscribeControllerImpl;
import com.android.ims.rcs.uce.request.UceRequestManager;
import com.android.ims.rcs.uce.util.UceUtils;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class UceController {
    private static final String LOG_TAG = UceUtils.getLogPrefix() + "UceController";
    private static final int RCS_STATE_CONNECTED = 2;
    private static final int RCS_STATE_CONNECTING = 1;
    private static final int RCS_STATE_DISCONNECTED = 0;
    public static final int REQUEST_TYPE_CAPABILITY = 2;
    public static final Map<Integer, String> REQUEST_TYPE_DESCRIPTION;
    public static final int REQUEST_TYPE_PUBLISH = 1;
    private final CachedCapabilityEvent mCachedCapabilityEvent;
    private RcsFeatureManager.CapabilityExchangeEventCallback mCapabilityEventListener;
    private final Context mContext;
    private ControllerFactory mControllerFactory;
    private UceControllerCallback mCtrlCallback;
    private UceDeviceState mDeviceState;
    private EabController mEabController;
    private volatile boolean mIsDestroyedFlag;
    private final LocalLog mLocalLog;
    private volatile Looper mLooper;
    private OptionsController mOptionsController;
    private PublishController mPublishController;
    private volatile int mRcsConnectedState;
    private RcsFeatureManager mRcsFeatureManager;
    private UceRequestManager mRequestManager;
    private RequestManagerFactory mRequestManagerFactory;
    private final int mSubId;
    private SubscribeController mSubscribeController;

    /* loaded from: classes.dex */
    public interface ControllerFactory {
        EabController createEabController(Context context, int i, UceControllerCallback uceControllerCallback, Looper looper);

        OptionsController createOptionsController(Context context, int i);

        PublishController createPublishController(Context context, int i, UceControllerCallback uceControllerCallback, Looper looper);

        SubscribeController createSubscribeController(Context context, int i);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    @interface RcsConnectedState {
    }

    /* loaded from: classes.dex */
    public interface RequestManagerFactory {
        UceRequestManager createRequestManager(Context context, int i, Looper looper, UceControllerCallback uceControllerCallback);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface RequestType {
    }

    /* loaded from: classes.dex */
    public interface UceControllerCallback {
        void clearResetDeviceStateTimer();

        EabCapabilityResult getAvailabilityFromCache(Uri uri);

        EabCapabilityResult getAvailabilityFromCacheIncludingExpired(Uri uri);

        List<EabCapabilityResult> getCapabilitiesFromCache(List<Uri> list);

        List<EabCapabilityResult> getCapabilitiesFromCacheIncludingExpired(List<Uri> list);

        RcsContactUceCapability getDeviceCapabilities(int i);

        UceDeviceState.DeviceStateResult getDeviceState();

        void refreshCapabilities(List<Uri> list, IRcsUceControllerCallback iRcsUceControllerCallback) throws RemoteException;

        void refreshDeviceState(int i, String str, int i2);

        void resetDeviceState();

        void saveCapabilities(List<RcsContactUceCapability> list);

        void setupResetDeviceStateTimer(long j);
    }

    static {
        HashMap hashMap = new HashMap();
        REQUEST_TYPE_DESCRIPTION = hashMap;
        hashMap.put(1, "REQUEST_TYPE_PUBLISH");
        hashMap.put(2, "REQUEST_TYPE_CAPABILITY");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ UceRequestManager lambda$new$0(Context context, int subId, Looper looper, UceControllerCallback callback) {
        return new UceRequestManager(context, subId, looper, callback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class CachedCapabilityEvent {
        private Optional<Integer> mRequestPublishCapabilitiesEvent = Optional.empty();
        private Optional<Boolean> mUnpublishEvent = Optional.empty();
        private Optional<SipDetails> mPublishUpdatedEvent = Optional.empty();
        private Optional<SomeArgs> mRemoteCapabilityRequestEvent = Optional.empty();

        public synchronized void setRequestPublishCapabilitiesEvent(int triggerType) {
            this.mRequestPublishCapabilitiesEvent = Optional.of(Integer.valueOf(triggerType));
        }

        public synchronized void setOnUnpublishEvent() {
            this.mUnpublishEvent = Optional.of(Boolean.TRUE);
        }

        public synchronized void setOnPublishUpdatedEvent(SipDetails details) {
            this.mPublishUpdatedEvent = Optional.of(details);
        }

        public synchronized void setRemoteCapabilityRequestEvent(Uri contactUri, List<String> remoteCapabilities, IOptionsRequestCallback callback) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = contactUri;
            args.arg2 = remoteCapabilities;
            args.arg3 = callback;
            this.mRemoteCapabilityRequestEvent = Optional.of(args);
        }

        public synchronized Optional<Integer> getRequestPublishEvent() {
            return this.mRequestPublishCapabilitiesEvent;
        }

        public synchronized Optional<Boolean> getUnpublishEvent() {
            return this.mUnpublishEvent;
        }

        public synchronized Optional<SipDetails> getPublishUpdatedEvent() {
            return this.mPublishUpdatedEvent;
        }

        public synchronized Optional<SomeArgs> getRemoteCapabilityRequestEvent() {
            return this.mRemoteCapabilityRequestEvent;
        }

        public synchronized void clear() {
            this.mRequestPublishCapabilitiesEvent = Optional.empty();
            this.mUnpublishEvent = Optional.empty();
            this.mPublishUpdatedEvent = Optional.empty();
            this.mRemoteCapabilityRequestEvent.ifPresent(new Consumer() { // from class: com.android.ims.rcs.uce.UceController$CachedCapabilityEvent$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((SomeArgs) obj).recycle();
                }
            });
            this.mRemoteCapabilityRequestEvent = Optional.empty();
        }
    }

    public UceController(Context context, int subId) {
        this.mRequestManagerFactory = new RequestManagerFactory() { // from class: com.android.ims.rcs.uce.UceController$$ExternalSyntheticLambda0
            @Override // com.android.ims.rcs.uce.UceController.RequestManagerFactory
            public final UceRequestManager createRequestManager(Context context2, int i, Looper looper, UceController.UceControllerCallback uceControllerCallback) {
                return UceController.lambda$new$0(context2, i, looper, uceControllerCallback);
            }
        };
        this.mControllerFactory = new ControllerFactory() { // from class: com.android.ims.rcs.uce.UceController.1
            @Override // com.android.ims.rcs.uce.UceController.ControllerFactory
            public EabController createEabController(Context context2, int subId2, UceControllerCallback c, Looper looper) {
                return new EabControllerImpl(context2, subId2, c, looper);
            }

            @Override // com.android.ims.rcs.uce.UceController.ControllerFactory
            public PublishController createPublishController(Context context2, int subId2, UceControllerCallback c, Looper looper) {
                return new PublishControllerImpl(context2, subId2, c, looper);
            }

            @Override // com.android.ims.rcs.uce.UceController.ControllerFactory
            public SubscribeController createSubscribeController(Context context2, int subId2) {
                return new SubscribeControllerImpl(context2, subId2);
            }

            @Override // com.android.ims.rcs.uce.UceController.ControllerFactory
            public OptionsController createOptionsController(Context context2, int subId2) {
                return new OptionsControllerImpl(context2, subId2);
            }
        };
        this.mLocalLog = new LocalLog(20);
        this.mCtrlCallback = new UceControllerCallback() { // from class: com.android.ims.rcs.uce.UceController.2
            @Override // com.android.ims.rcs.uce.UceController.UceControllerCallback
            public List<EabCapabilityResult> getCapabilitiesFromCache(List<Uri> uris) {
                return UceController.this.mEabController.getCapabilities(uris);
            }

            @Override // com.android.ims.rcs.uce.UceController.UceControllerCallback
            public List<EabCapabilityResult> getCapabilitiesFromCacheIncludingExpired(List<Uri> uris) {
                return UceController.this.mEabController.getCapabilitiesIncludingExpired(uris);
            }

            @Override // com.android.ims.rcs.uce.UceController.UceControllerCallback
            public EabCapabilityResult getAvailabilityFromCache(Uri contactUri) {
                return UceController.this.mEabController.getAvailability(contactUri);
            }

            @Override // com.android.ims.rcs.uce.UceController.UceControllerCallback
            public EabCapabilityResult getAvailabilityFromCacheIncludingExpired(Uri contactUri) {
                return UceController.this.mEabController.getAvailabilityIncludingExpired(contactUri);
            }

            @Override // com.android.ims.rcs.uce.UceController.UceControllerCallback
            public void saveCapabilities(List<RcsContactUceCapability> contactCapabilities) {
                UceController.this.mEabController.saveCapabilities(contactCapabilities);
            }

            @Override // com.android.ims.rcs.uce.UceController.UceControllerCallback
            public RcsContactUceCapability getDeviceCapabilities(int mechanism) {
                return UceController.this.mPublishController.getDeviceCapabilities(mechanism);
            }

            @Override // com.android.ims.rcs.uce.UceController.UceControllerCallback
            public void refreshDeviceState(int sipCode, String reason, int type) {
                UceController.this.mDeviceState.refreshDeviceState(sipCode, reason, type);
            }

            @Override // com.android.ims.rcs.uce.UceController.UceControllerCallback
            public void resetDeviceState() {
                UceController.this.mDeviceState.resetDeviceState();
            }

            @Override // com.android.ims.rcs.uce.UceController.UceControllerCallback
            public UceDeviceState.DeviceStateResult getDeviceState() {
                return UceController.this.mDeviceState.getCurrentState();
            }

            @Override // com.android.ims.rcs.uce.UceController.UceControllerCallback
            public void setupResetDeviceStateTimer(long resetAfterSec) {
                UceController.this.mPublishController.setupResetDeviceStateTimer(resetAfterSec);
            }

            @Override // com.android.ims.rcs.uce.UceController.UceControllerCallback
            public void clearResetDeviceStateTimer() {
                UceController.this.mPublishController.clearResetDeviceStateTimer();
            }

            @Override // com.android.ims.rcs.uce.UceController.UceControllerCallback
            public void refreshCapabilities(List<Uri> contactNumbers, IRcsUceControllerCallback callback) throws RemoteException {
                UceController.this.logd("refreshCapabilities: " + contactNumbers.size());
                UceController.this.requestCapabilitiesInternal(contactNumbers, true, callback);
            }
        };
        this.mCapabilityEventListener = new RcsFeatureManager.CapabilityExchangeEventCallback() { // from class: com.android.ims.rcs.uce.UceController.3
            @Override // com.android.ims.RcsFeatureManager.CapabilityExchangeEventCallback
            public void onRequestPublishCapabilities(int triggerType) {
                if (UceController.this.isRcsConnecting()) {
                    UceController.this.mCachedCapabilityEvent.setRequestPublishCapabilitiesEvent(triggerType);
                } else {
                    UceController.this.onRequestPublishCapabilitiesFromService(triggerType);
                }
            }

            @Override // com.android.ims.RcsFeatureManager.CapabilityExchangeEventCallback
            public void onUnpublish() {
                if (UceController.this.isRcsConnecting()) {
                    UceController.this.mCachedCapabilityEvent.setOnUnpublishEvent();
                } else {
                    UceController.this.onUnpublish();
                }
            }

            @Override // com.android.ims.RcsFeatureManager.CapabilityExchangeEventCallback
            public void onPublishUpdated(SipDetails details) {
                if (UceController.this.isRcsConnecting()) {
                    UceController.this.mCachedCapabilityEvent.setOnPublishUpdatedEvent(details);
                } else {
                    UceController.this.lambda$handleCachedCapabilityEvent$3(details);
                }
            }

            @Override // com.android.ims.RcsFeatureManager.CapabilityExchangeEventCallback
            public void onRemoteCapabilityRequest(Uri contactUri, List<String> remoteCapabilities, IOptionsRequestCallback cb) {
                if (contactUri == null || remoteCapabilities == null || cb == null) {
                    UceController.this.logw("onRemoteCapabilityRequest: parameter cannot be null");
                } else if (UceController.this.isRcsConnecting()) {
                    UceController.this.mCachedCapabilityEvent.setRemoteCapabilityRequestEvent(contactUri, remoteCapabilities, cb);
                } else {
                    UceController.this.retrieveOptionsCapabilitiesForRemote(contactUri, remoteCapabilities, cb);
                }
            }
        };
        this.mSubId = subId;
        this.mContext = context;
        this.mCachedCapabilityEvent = new CachedCapabilityEvent();
        this.mRcsConnectedState = 0;
        logi("create");
        initLooper();
        initControllers();
        initRequestManager();
        initUceDeviceState();
    }

    public UceController(Context context, int subId, UceDeviceState deviceState, ControllerFactory controllerFactory, RequestManagerFactory requestManagerFactory) {
        this.mRequestManagerFactory = new RequestManagerFactory() { // from class: com.android.ims.rcs.uce.UceController$$ExternalSyntheticLambda0
            @Override // com.android.ims.rcs.uce.UceController.RequestManagerFactory
            public final UceRequestManager createRequestManager(Context context2, int i, Looper looper, UceController.UceControllerCallback uceControllerCallback) {
                return UceController.lambda$new$0(context2, i, looper, uceControllerCallback);
            }
        };
        this.mControllerFactory = new ControllerFactory() { // from class: com.android.ims.rcs.uce.UceController.1
            @Override // com.android.ims.rcs.uce.UceController.ControllerFactory
            public EabController createEabController(Context context2, int subId2, UceControllerCallback c, Looper looper) {
                return new EabControllerImpl(context2, subId2, c, looper);
            }

            @Override // com.android.ims.rcs.uce.UceController.ControllerFactory
            public PublishController createPublishController(Context context2, int subId2, UceControllerCallback c, Looper looper) {
                return new PublishControllerImpl(context2, subId2, c, looper);
            }

            @Override // com.android.ims.rcs.uce.UceController.ControllerFactory
            public SubscribeController createSubscribeController(Context context2, int subId2) {
                return new SubscribeControllerImpl(context2, subId2);
            }

            @Override // com.android.ims.rcs.uce.UceController.ControllerFactory
            public OptionsController createOptionsController(Context context2, int subId2) {
                return new OptionsControllerImpl(context2, subId2);
            }
        };
        this.mLocalLog = new LocalLog(20);
        this.mCtrlCallback = new UceControllerCallback() { // from class: com.android.ims.rcs.uce.UceController.2
            @Override // com.android.ims.rcs.uce.UceController.UceControllerCallback
            public List<EabCapabilityResult> getCapabilitiesFromCache(List<Uri> uris) {
                return UceController.this.mEabController.getCapabilities(uris);
            }

            @Override // com.android.ims.rcs.uce.UceController.UceControllerCallback
            public List<EabCapabilityResult> getCapabilitiesFromCacheIncludingExpired(List<Uri> uris) {
                return UceController.this.mEabController.getCapabilitiesIncludingExpired(uris);
            }

            @Override // com.android.ims.rcs.uce.UceController.UceControllerCallback
            public EabCapabilityResult getAvailabilityFromCache(Uri contactUri) {
                return UceController.this.mEabController.getAvailability(contactUri);
            }

            @Override // com.android.ims.rcs.uce.UceController.UceControllerCallback
            public EabCapabilityResult getAvailabilityFromCacheIncludingExpired(Uri contactUri) {
                return UceController.this.mEabController.getAvailabilityIncludingExpired(contactUri);
            }

            @Override // com.android.ims.rcs.uce.UceController.UceControllerCallback
            public void saveCapabilities(List<RcsContactUceCapability> contactCapabilities) {
                UceController.this.mEabController.saveCapabilities(contactCapabilities);
            }

            @Override // com.android.ims.rcs.uce.UceController.UceControllerCallback
            public RcsContactUceCapability getDeviceCapabilities(int mechanism) {
                return UceController.this.mPublishController.getDeviceCapabilities(mechanism);
            }

            @Override // com.android.ims.rcs.uce.UceController.UceControllerCallback
            public void refreshDeviceState(int sipCode, String reason, int type) {
                UceController.this.mDeviceState.refreshDeviceState(sipCode, reason, type);
            }

            @Override // com.android.ims.rcs.uce.UceController.UceControllerCallback
            public void resetDeviceState() {
                UceController.this.mDeviceState.resetDeviceState();
            }

            @Override // com.android.ims.rcs.uce.UceController.UceControllerCallback
            public UceDeviceState.DeviceStateResult getDeviceState() {
                return UceController.this.mDeviceState.getCurrentState();
            }

            @Override // com.android.ims.rcs.uce.UceController.UceControllerCallback
            public void setupResetDeviceStateTimer(long resetAfterSec) {
                UceController.this.mPublishController.setupResetDeviceStateTimer(resetAfterSec);
            }

            @Override // com.android.ims.rcs.uce.UceController.UceControllerCallback
            public void clearResetDeviceStateTimer() {
                UceController.this.mPublishController.clearResetDeviceStateTimer();
            }

            @Override // com.android.ims.rcs.uce.UceController.UceControllerCallback
            public void refreshCapabilities(List<Uri> contactNumbers, IRcsUceControllerCallback callback) throws RemoteException {
                UceController.this.logd("refreshCapabilities: " + contactNumbers.size());
                UceController.this.requestCapabilitiesInternal(contactNumbers, true, callback);
            }
        };
        this.mCapabilityEventListener = new RcsFeatureManager.CapabilityExchangeEventCallback() { // from class: com.android.ims.rcs.uce.UceController.3
            @Override // com.android.ims.RcsFeatureManager.CapabilityExchangeEventCallback
            public void onRequestPublishCapabilities(int triggerType) {
                if (UceController.this.isRcsConnecting()) {
                    UceController.this.mCachedCapabilityEvent.setRequestPublishCapabilitiesEvent(triggerType);
                } else {
                    UceController.this.onRequestPublishCapabilitiesFromService(triggerType);
                }
            }

            @Override // com.android.ims.RcsFeatureManager.CapabilityExchangeEventCallback
            public void onUnpublish() {
                if (UceController.this.isRcsConnecting()) {
                    UceController.this.mCachedCapabilityEvent.setOnUnpublishEvent();
                } else {
                    UceController.this.onUnpublish();
                }
            }

            @Override // com.android.ims.RcsFeatureManager.CapabilityExchangeEventCallback
            public void onPublishUpdated(SipDetails details) {
                if (UceController.this.isRcsConnecting()) {
                    UceController.this.mCachedCapabilityEvent.setOnPublishUpdatedEvent(details);
                } else {
                    UceController.this.lambda$handleCachedCapabilityEvent$3(details);
                }
            }

            @Override // com.android.ims.RcsFeatureManager.CapabilityExchangeEventCallback
            public void onRemoteCapabilityRequest(Uri contactUri, List<String> remoteCapabilities, IOptionsRequestCallback cb) {
                if (contactUri == null || remoteCapabilities == null || cb == null) {
                    UceController.this.logw("onRemoteCapabilityRequest: parameter cannot be null");
                } else if (UceController.this.isRcsConnecting()) {
                    UceController.this.mCachedCapabilityEvent.setRemoteCapabilityRequestEvent(contactUri, remoteCapabilities, cb);
                } else {
                    UceController.this.retrieveOptionsCapabilitiesForRemote(contactUri, remoteCapabilities, cb);
                }
            }
        };
        this.mSubId = subId;
        this.mContext = context;
        this.mDeviceState = deviceState;
        this.mControllerFactory = controllerFactory;
        this.mRequestManagerFactory = requestManagerFactory;
        this.mCachedCapabilityEvent = new CachedCapabilityEvent();
        this.mRcsConnectedState = 0;
        initLooper();
        initControllers();
        initRequestManager();
    }

    private void initLooper() {
        HandlerThread handlerThread = new HandlerThread("UceControllerHandlerThread");
        handlerThread.start();
        this.mLooper = handlerThread.getLooper();
    }

    private void initControllers() {
        this.mEabController = this.mControllerFactory.createEabController(this.mContext, this.mSubId, this.mCtrlCallback, this.mLooper);
        this.mPublishController = this.mControllerFactory.createPublishController(this.mContext, this.mSubId, this.mCtrlCallback, this.mLooper);
        this.mSubscribeController = this.mControllerFactory.createSubscribeController(this.mContext, this.mSubId);
        this.mOptionsController = this.mControllerFactory.createOptionsController(this.mContext, this.mSubId);
    }

    private void initRequestManager() {
        UceRequestManager createRequestManager = this.mRequestManagerFactory.createRequestManager(this.mContext, this.mSubId, this.mLooper, this.mCtrlCallback);
        this.mRequestManager = createRequestManager;
        createRequestManager.setSubscribeController(this.mSubscribeController);
        this.mRequestManager.setOptionsController(this.mOptionsController);
    }

    private void initUceDeviceState() {
        UceDeviceState uceDeviceState = new UceDeviceState(this.mSubId, this.mContext, this.mCtrlCallback);
        this.mDeviceState = uceDeviceState;
        uceDeviceState.checkSendResetDeviceStateTimer();
    }

    public void onRcsConnected(RcsFeatureManager manager) {
        logi("onRcsConnected");
        this.mRcsConnectedState = 1;
        this.mRcsFeatureManager = manager;
        manager.addCapabilityEventCallback(this.mCapabilityEventListener);
        this.mEabController.onRcsConnected(manager);
        this.mPublishController.onRcsConnected(manager);
        this.mSubscribeController.onRcsConnected(manager);
        this.mOptionsController.onRcsConnected(manager);
        this.mRcsConnectedState = 2;
        handleCachedCapabilityEvent();
    }

    public void onRcsDisconnected() {
        logi("onRcsDisconnected");
        this.mRcsConnectedState = 0;
        RcsFeatureManager rcsFeatureManager = this.mRcsFeatureManager;
        if (rcsFeatureManager != null) {
            rcsFeatureManager.removeCapabilityEventCallback(this.mCapabilityEventListener);
            this.mRcsFeatureManager = null;
        }
        this.mEabController.onRcsDisconnected();
        this.mPublishController.onRcsDisconnected();
        this.mSubscribeController.onRcsDisconnected();
        this.mOptionsController.onRcsDisconnected();
    }

    public void onDestroy() {
        logi("onDestroy");
        this.mIsDestroyedFlag = true;
        RcsFeatureManager rcsFeatureManager = this.mRcsFeatureManager;
        if (rcsFeatureManager != null) {
            rcsFeatureManager.removeCapabilityEventCallback(this.mCapabilityEventListener);
            this.mRcsFeatureManager = null;
        }
        this.mRequestManager.onDestroy();
        this.mEabController.onDestroy();
        this.mPublishController.onDestroy();
        this.mSubscribeController.onDestroy();
        this.mOptionsController.onDestroy();
        this.mLooper.quitSafely();
    }

    public void onCarrierConfigChanged() {
        this.mEabController.onCarrierConfigChanged();
        this.mPublishController.onCarrierConfigChanged();
        this.mSubscribeController.onCarrierConfigChanged();
        this.mOptionsController.onCarrierConfigChanged();
    }

    private void handleCachedCapabilityEvent() {
        Optional<Integer> requestPublishEvent = this.mCachedCapabilityEvent.getRequestPublishEvent();
        requestPublishEvent.ifPresent(new Consumer() { // from class: com.android.ims.rcs.uce.UceController$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                UceController.this.lambda$handleCachedCapabilityEvent$1((Integer) obj);
            }
        });
        Optional<Boolean> unpublishEvent = this.mCachedCapabilityEvent.getUnpublishEvent();
        unpublishEvent.ifPresent(new Consumer() { // from class: com.android.ims.rcs.uce.UceController$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                UceController.this.lambda$handleCachedCapabilityEvent$2((Boolean) obj);
            }
        });
        Optional<SipDetails> publishUpdatedEvent = this.mCachedCapabilityEvent.getPublishUpdatedEvent();
        publishUpdatedEvent.ifPresent(new Consumer() { // from class: com.android.ims.rcs.uce.UceController$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                UceController.this.lambda$handleCachedCapabilityEvent$3((SipDetails) obj);
            }
        });
        Optional<SomeArgs> remoteRequest = this.mCachedCapabilityEvent.getRemoteCapabilityRequestEvent();
        remoteRequest.ifPresent(new Consumer() { // from class: com.android.ims.rcs.uce.UceController$$ExternalSyntheticLambda4
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                UceController.this.lambda$handleCachedCapabilityEvent$4((SomeArgs) obj);
            }
        });
        this.mCachedCapabilityEvent.clear();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleCachedCapabilityEvent$1(Integer triggerType) {
        onRequestPublishCapabilitiesFromService(triggerType.intValue());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleCachedCapabilityEvent$2(Boolean unpublish) {
        onUnpublish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleCachedCapabilityEvent$4(SomeArgs args) {
        Uri contactUri = (Uri) args.arg1;
        List<String> remoteCapabilities = (List) args.arg2;
        IOptionsRequestCallback callback = (IOptionsRequestCallback) args.arg3;
        retrieveOptionsCapabilitiesForRemote(contactUri, remoteCapabilities, callback);
    }

    public void setUceControllerCallback(UceControllerCallback callback) {
        this.mCtrlCallback = callback;
    }

    public void requestCapabilities(List<Uri> uriList, IRcsUceControllerCallback c) throws RemoteException {
        requestCapabilitiesInternal(uriList, false, c);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestCapabilitiesInternal(List<Uri> uriList, boolean skipFromCache, IRcsUceControllerCallback c) throws RemoteException {
        if (uriList == null || uriList.isEmpty() || c == null) {
            logw("requestCapabilities: parameter is empty");
            if (c != null) {
                c.onError(1, 0L, (SipDetails) null);
            }
        } else if (isUnavailable()) {
            logw("requestCapabilities: controller is unavailable");
            c.onError(1, 0L, (SipDetails) null);
        } else {
            UceDeviceState.DeviceStateResult deviceStateResult = this.mDeviceState.getCurrentState();
            if (deviceStateResult.isRequestForbidden()) {
                int deviceState = deviceStateResult.getDeviceState();
                int errorCode = deviceStateResult.getErrorCode().orElse(1).intValue();
                long retryAfterMillis = deviceStateResult.getRequestRetryAfterMillis();
                logw("requestCapabilities: The device is disallowed, deviceState= " + deviceState + ", errorCode=" + errorCode + ", retryAfterMillis=" + retryAfterMillis);
                c.onError(errorCode, retryAfterMillis, (SipDetails) null);
                return;
            }
            logd("requestCapabilities: size=" + uriList.size());
            this.mRequestManager.sendCapabilityRequest(uriList, skipFromCache, c);
        }
    }

    public void requestAvailability(Uri uri, IRcsUceControllerCallback c) throws RemoteException {
        if (uri == null || c == null) {
            logw("requestAvailability: parameter is empty");
            if (c != null) {
                c.onError(1, 0L, (SipDetails) null);
            }
        } else if (isUnavailable()) {
            logw("requestAvailability: controller is unavailable");
            c.onError(1, 0L, (SipDetails) null);
        } else {
            UceDeviceState.DeviceStateResult deviceStateResult = this.mDeviceState.getCurrentState();
            if (deviceStateResult.isRequestForbidden()) {
                int deviceState = deviceStateResult.getDeviceState();
                int errorCode = deviceStateResult.getErrorCode().orElse(1).intValue();
                long retryAfterMillis = deviceStateResult.getRequestRetryAfterMillis();
                logw("requestAvailability: The device is disallowed, deviceState= " + deviceState + ", errorCode=" + errorCode + ", retryAfterMillis=" + retryAfterMillis);
                c.onError(errorCode, retryAfterMillis, (SipDetails) null);
                return;
            }
            logd("requestAvailability");
            this.mRequestManager.sendAvailabilityRequest(uri, c);
        }
    }

    public void onRequestPublishCapabilitiesFromService(int triggerType) {
        logd("onRequestPublishCapabilitiesFromService: " + triggerType);
        this.mDeviceState.resetDeviceState();
        this.mPublishController.requestPublishCapabilitiesFromService(triggerType);
    }

    public void onUnpublish() {
        logi("onUnpublish");
        this.mPublishController.onUnpublish();
    }

    /* renamed from: onPublishUpdated */
    public void lambda$handleCachedCapabilityEvent$3(SipDetails details) {
        logi("onPublishUpdated");
        this.mPublishController.onPublishUpdated(details);
    }

    public void retrieveOptionsCapabilitiesForRemote(Uri contactUri, List<String> remoteCapabilities, IOptionsRequestCallback c) {
        logi("retrieveOptionsCapabilitiesForRemote");
        this.mRequestManager.retrieveCapabilitiesForRemote(contactUri, remoteCapabilities, c);
    }

    public void registerPublishStateCallback(IRcsUcePublishStateCallback c, boolean supportPublishingState) {
        this.mPublishController.registerPublishStateCallback(c, supportPublishingState);
    }

    public void unregisterPublishStateCallback(IRcsUcePublishStateCallback c) {
        this.mPublishController.unregisterPublishStateCallback(c);
    }

    public int getUcePublishState(boolean isSupportPublishingState) {
        return this.mPublishController.getUcePublishState(isSupportPublishingState);
    }

    public RcsContactUceCapability addRegistrationOverrideCapabilities(Set<String> featureTags) {
        return this.mPublishController.addRegistrationOverrideCapabilities(featureTags);
    }

    public RcsContactUceCapability removeRegistrationOverrideCapabilities(Set<String> featureTags) {
        return this.mPublishController.removeRegistrationOverrideCapabilities(featureTags);
    }

    public RcsContactUceCapability clearRegistrationOverrideCapabilities() {
        return this.mPublishController.clearRegistrationOverrideCapabilities();
    }

    public RcsContactUceCapability getLatestRcsContactUceCapability() {
        return this.mPublishController.getLatestRcsContactUceCapability();
    }

    public String getLastPidfXml() {
        return this.mPublishController.getLastPidfXml();
    }

    public void removeRequestDisallowedStatus() {
        logd("removeRequestDisallowedStatus");
        this.mDeviceState.resetDeviceState();
        this.mRequestManager.resetThrottlingList();
    }

    public void setCapabilitiesRequestTimeout(long timeoutAfterMs) {
        logd("setCapabilitiesRequestTimeout: " + timeoutAfterMs);
        UceUtils.setCapRequestTimeoutAfterMillis(timeoutAfterMs);
    }

    public int getSubId() {
        return this.mSubId;
    }

    public boolean isUnavailable() {
        if (!isRcsConnected() || this.mIsDestroyedFlag) {
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isRcsConnecting() {
        return this.mRcsConnectedState == 1;
    }

    private boolean isRcsConnected() {
        return this.mRcsConnectedState == 2;
    }

    public void dump(PrintWriter printWriter) {
        PrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        indentingPrintWriter.println("UceController[subId: " + this.mSubId + "]:");
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.println("Log:");
        indentingPrintWriter.increaseIndent();
        this.mLocalLog.dump(indentingPrintWriter);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("---");
        this.mPublishController.dump(indentingPrintWriter);
        indentingPrintWriter.decreaseIndent();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logd(String log) {
        Log.d(LOG_TAG, getLogPrefix().append(log).toString());
        this.mLocalLog.log("[D] " + log);
    }

    private void logi(String log) {
        Log.i(LOG_TAG, getLogPrefix().append(log).toString());
        this.mLocalLog.log("[I] " + log);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logw(String log) {
        Log.w(LOG_TAG, getLogPrefix().append(log).toString());
        this.mLocalLog.log("[W] " + log);
    }

    private StringBuilder getLogPrefix() {
        StringBuilder builder = new StringBuilder("[");
        builder.append(this.mSubId);
        builder.append("] ");
        return builder;
    }
}
