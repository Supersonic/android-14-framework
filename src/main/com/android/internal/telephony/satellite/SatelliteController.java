package com.android.internal.telephony.satellite;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncResult;
import android.os.Binder;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.ICancellationSignal;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.telephony.Rlog;
import android.telephony.SubscriptionManager;
import android.telephony.satellite.ISatelliteDatagramCallback;
import android.telephony.satellite.ISatellitePositionUpdateCallback;
import android.telephony.satellite.ISatelliteProvisionStateCallback;
import android.telephony.satellite.ISatelliteStateCallback;
import android.telephony.satellite.SatelliteCapabilities;
import android.telephony.satellite.SatelliteDatagram;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.IIntegerConsumer;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.SubscriptionController;
import com.android.internal.telephony.data.KeepaliveStatus;
import com.android.internal.telephony.satellite.SatelliteController;
import com.android.internal.telephony.subscription.SubscriptionManagerService;
import com.android.internal.util.FunctionalUtils;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class SatelliteController extends Handler {
    private static SatelliteController sInstance;
    BluetoothAdapter mBluetoothAdapter;
    private final Context mContext;
    private final DatagramController mDatagramController;
    boolean mDisabledBTFlag;
    boolean mDisabledWifiFlag;
    private boolean mIsSatelliteDemoModeEnabled;
    private Boolean mIsSatelliteSupported;
    private final Object mIsSatelliteSupportedLock;
    private boolean mNeedsSatellitePointing;
    private final Object mNeedsSatellitePointingLock;
    private final PointingAppController mPointingAppController;
    private final SatelliteModemInterface mSatelliteModemInterface;
    private final ConcurrentHashMap<Integer, Consumer<Integer>> mSatelliteProvisionCallbacks;
    private final ConcurrentHashMap<Integer, SatelliteProvisionStateChangedHandler> mSatelliteProvisionStateChangedHandlers;
    private final SatelliteSessionController mSatelliteSessionController;
    private final ConcurrentHashMap<Integer, SatelliteStateListenerHandler> mSatelliteStateListenerHandlers;
    private final ResultReceiver mSatelliteSupportedReceiver;
    WifiManager mWifiManager;

    public static SatelliteController getInstance() {
        if (sInstance == null) {
            loge("SatelliteController was not yet initialized.");
        }
        return sInstance;
    }

    public static void make(Context context) {
        if (sInstance == null) {
            HandlerThread handlerThread = new HandlerThread("SatelliteController");
            handlerThread.start();
            sInstance = new SatelliteController(context, handlerThread.getLooper());
        }
    }

    @VisibleForTesting
    protected SatelliteController(Context context, Looper looper) {
        super(looper);
        this.mBluetoothAdapter = null;
        this.mWifiManager = null;
        this.mDisabledBTFlag = false;
        this.mDisabledWifiFlag = false;
        this.mSatelliteProvisionCallbacks = new ConcurrentHashMap<>();
        this.mSatelliteProvisionStateChangedHandlers = new ConcurrentHashMap<>();
        this.mSatelliteStateListenerHandlers = new ConcurrentHashMap<>();
        this.mIsSatelliteSupported = null;
        this.mIsSatelliteSupportedLock = new Object();
        this.mIsSatelliteDemoModeEnabled = false;
        this.mNeedsSatellitePointing = false;
        this.mNeedsSatellitePointingLock = new Object();
        this.mContext = context;
        this.mSatelliteModemInterface = SatelliteModemInterface.make(context);
        this.mSatelliteSessionController = SatelliteSessionController.make(context);
        this.mPointingAppController = PointingAppController.make(context);
        this.mDatagramController = DatagramController.make(context, looper);
        this.mSatelliteSupportedReceiver = new ResultReceiver(this) { // from class: com.android.internal.telephony.satellite.SatelliteController.1
            @Override // android.os.ResultReceiver
            protected void onReceiveResult(int i, Bundle bundle) {
                if (i == 0 && bundle.containsKey("satellite_supported")) {
                    synchronized (SatelliteController.this.mIsSatelliteSupportedLock) {
                        SatelliteController.this.mIsSatelliteSupported = Boolean.valueOf(bundle.getBoolean("satellite_supported"));
                    }
                    return;
                }
                synchronized (SatelliteController.this.mIsSatelliteSupportedLock) {
                    SatelliteController.this.mIsSatelliteSupported = null;
                }
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class SatelliteControllerHandlerRequest {
        public Object argument;
        public Phone phone;
        public Object result;

        SatelliteControllerHandlerRequest(Object obj, Phone phone) {
            this.argument = obj;
            this.phone = phone;
        }
    }

    /* loaded from: classes.dex */
    private static final class RequestSatelliteEnabledArgument {
        public Consumer<Integer> callback;
        public boolean enabled;

        RequestSatelliteEnabledArgument(boolean z, Consumer<Integer> consumer) {
            this.enabled = z;
            this.callback = consumer;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class ProvisionSatelliteServiceArgument {
        public Consumer<Integer> callback;
        public int subId;
        public String token;

        ProvisionSatelliteServiceArgument(String str, Consumer<Integer> consumer, int i) {
            this.token = str;
            this.callback = consumer;
            this.subId = i;
        }
    }

    /* loaded from: classes.dex */
    public static final class SatellitePositionUpdateArgument {
        public ISatellitePositionUpdateCallback callback;
        public Consumer<Integer> errorCallback;
        public int subId;

        SatellitePositionUpdateArgument(Consumer<Integer> consumer, ISatellitePositionUpdateCallback iSatellitePositionUpdateCallback, int i) {
            this.errorCallback = consumer;
            this.callback = iSatellitePositionUpdateCallback;
            this.subId = i;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class SatelliteProvisionStateChangedHandler extends Handler {
        public static final int EVENT_PROVISION_STATE_CHANGED = 1;
        private final ConcurrentHashMap<IBinder, ISatelliteProvisionStateCallback> mListeners;
        private final int mSubId;

        SatelliteProvisionStateChangedHandler(Looper looper, int i) {
            super(looper);
            this.mListeners = new ConcurrentHashMap<>();
            this.mSubId = i;
        }

        public void addListener(ISatelliteProvisionStateCallback iSatelliteProvisionStateCallback) {
            this.mListeners.put(iSatelliteProvisionStateCallback.asBinder(), iSatelliteProvisionStateCallback);
        }

        public void removeListener(ISatelliteProvisionStateCallback iSatelliteProvisionStateCallback) {
            this.mListeners.remove(iSatelliteProvisionStateCallback.asBinder());
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 1) {
                final boolean booleanValue = ((Boolean) ((AsyncResult) message.obj).userObj).booleanValue();
                SatelliteController.logd("Received EVENT_PROVISION_STATE_CHANGED for subId=" + this.mSubId + ", provisioned=" + booleanValue);
                this.mListeners.values().forEach(new Consumer() { // from class: com.android.internal.telephony.satellite.SatelliteController$SatelliteProvisionStateChangedHandler$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        SatelliteController.SatelliteProvisionStateChangedHandler.lambda$handleMessage$0(booleanValue, (ISatelliteProvisionStateCallback) obj);
                    }
                });
                setSatelliteProvisioned(booleanValue);
                return;
            }
            SatelliteController.loge("SatelliteProvisionStateChangedHandler unknown event: " + message.what);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$handleMessage$0(boolean z, ISatelliteProvisionStateCallback iSatelliteProvisionStateCallback) {
            try {
                iSatelliteProvisionStateCallback.onSatelliteProvisionStateChanged(z);
            } catch (RemoteException e) {
                SatelliteController.logd("EVENT_PROVISION_STATE_CHANGED RemoteException: " + e);
            }
        }

        private void setSatelliteProvisioned(boolean z) {
            int i = this.mSubId;
            if (i != Integer.MAX_VALUE) {
                SubscriptionManager.setSubscriptionProperty(i, "satellite_enabled", z ? "1" : "0");
            }
        }
    }

    /* loaded from: classes.dex */
    private static final class SatelliteStateListenerHandler extends Handler {
        public static final int EVENT_PENDING_DATAGRAM_COUNT = 2;
        public static final int EVENT_SATELLITE_MODEM_STATE_CHANGE = 1;
        private final ConcurrentHashMap<IBinder, ISatelliteStateCallback> mListeners;
        private final int mSubId;

        SatelliteStateListenerHandler(Looper looper, int i) {
            super(looper);
            this.mSubId = i;
            this.mListeners = new ConcurrentHashMap<>();
        }

        public void addListener(ISatelliteStateCallback iSatelliteStateCallback) {
            this.mListeners.put(iSatelliteStateCallback.asBinder(), iSatelliteStateCallback);
        }

        public void removeListener(ISatelliteStateCallback iSatelliteStateCallback) {
            this.mListeners.remove(iSatelliteStateCallback.asBinder());
        }

        public boolean hasListeners() {
            return !this.mListeners.isEmpty();
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                final int intValue = ((Integer) ((AsyncResult) message.obj).result).intValue();
                SatelliteController.logd("Received EVENT_SATELLITE_MODEM_STATE_CHANGE for subId=" + this.mSubId + ", state=" + intValue);
                this.mListeners.values().forEach(new Consumer() { // from class: com.android.internal.telephony.satellite.SatelliteController$SatelliteStateListenerHandler$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        SatelliteController.SatelliteStateListenerHandler.lambda$handleMessage$0(intValue, (ISatelliteStateCallback) obj);
                    }
                });
            } else if (i == 2) {
                final int intValue2 = ((Integer) ((AsyncResult) message.obj).result).intValue();
                SatelliteController.logd("Received EVENT_PENDING_DATAGRAM_COUNT for subId=" + this.mSubId + ", count=" + intValue2);
                this.mListeners.values().forEach(new Consumer() { // from class: com.android.internal.telephony.satellite.SatelliteController$SatelliteStateListenerHandler$$ExternalSyntheticLambda1
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        SatelliteController.SatelliteStateListenerHandler.lambda$handleMessage$1(intValue2, (ISatelliteStateCallback) obj);
                    }
                });
            } else {
                SatelliteController.loge("SatelliteStateListenerHandler unknown event: " + message.what);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$handleMessage$0(int i, ISatelliteStateCallback iSatelliteStateCallback) {
            try {
                iSatelliteStateCallback.onSatelliteModemStateChanged(i);
            } catch (RemoteException e) {
                SatelliteController.logd("EVENT_SATELLITE_MODEM_STATE_CHANGE RemoteException: " + e);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$handleMessage$1(int i, ISatelliteStateCallback iSatelliteStateCallback) {
            try {
                iSatelliteStateCallback.onPendingDatagramCount(i);
            } catch (RemoteException e) {
                SatelliteController.logd("EVENT_PENDING_DATAGRAM_COUNT RemoteException: " + e);
            }
        }
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i = 6;
        switch (message.what) {
            case 1:
                SatelliteControllerHandlerRequest satelliteControllerHandlerRequest = (SatelliteControllerHandlerRequest) message.obj;
                this.mPointingAppController.startSatellitePositionUpdates(obtainMessage(2, satelliteControllerHandlerRequest), satelliteControllerHandlerRequest.phone);
                return;
            case 2:
                handleStartSatellitePositionUpdatesDone((AsyncResult) message.obj);
                return;
            case 3:
                SatelliteControllerHandlerRequest satelliteControllerHandlerRequest2 = (SatelliteControllerHandlerRequest) message.obj;
                this.mPointingAppController.stopSatellitePositionUpdates(obtainMessage(4, satelliteControllerHandlerRequest2), satelliteControllerHandlerRequest2.phone);
                return;
            case 4:
                AsyncResult asyncResult = (AsyncResult) message.obj;
                ((Consumer) ((SatelliteControllerHandlerRequest) asyncResult.userObj).argument).accept(Integer.valueOf(SatelliteServiceUtils.getSatelliteError(asyncResult, "stopSatellitePositionUpdates")));
                return;
            case 5:
                SatelliteControllerHandlerRequest satelliteControllerHandlerRequest3 = (SatelliteControllerHandlerRequest) message.obj;
                Message obtainMessage = obtainMessage(6, satelliteControllerHandlerRequest3);
                if (this.mSatelliteModemInterface.isSatelliteServiceSupported()) {
                    this.mSatelliteModemInterface.requestMaxCharactersPerMOTextMessage(obtainMessage);
                    return;
                }
                Phone phone = satelliteControllerHandlerRequest3.phone;
                if (phone != null) {
                    phone.getMaxCharactersPerSatelliteTextMessage(obtainMessage);
                    return;
                }
                loge("getMaxCharactersPerSatelliteTextMessage: No phone object");
                ((ResultReceiver) satelliteControllerHandlerRequest3.argument).send(6, null);
                return;
            case 6:
                AsyncResult asyncResult2 = (AsyncResult) message.obj;
                SatelliteControllerHandlerRequest satelliteControllerHandlerRequest4 = (SatelliteControllerHandlerRequest) asyncResult2.userObj;
                int satelliteError = SatelliteServiceUtils.getSatelliteError(asyncResult2, "getMaxCharactersPerSatelliteTextMessage");
                Bundle bundle = new Bundle();
                if (satelliteError == 0) {
                    Object obj = asyncResult2.result;
                    if (obj == null) {
                        loge("getMaxCharactersPerSatelliteTextMessage: result is null");
                        ((ResultReceiver) satelliteControllerHandlerRequest4.argument).send(i, bundle);
                        return;
                    }
                    bundle.putInt("max_characters_per_satellite_text", ((int[]) obj)[0]);
                }
                i = satelliteError;
                ((ResultReceiver) satelliteControllerHandlerRequest4.argument).send(i, bundle);
                return;
            case 7:
                SatelliteControllerHandlerRequest satelliteControllerHandlerRequest5 = (SatelliteControllerHandlerRequest) message.obj;
                ProvisionSatelliteServiceArgument provisionSatelliteServiceArgument = (ProvisionSatelliteServiceArgument) satelliteControllerHandlerRequest5.argument;
                if (this.mSatelliteProvisionCallbacks.containsKey(Integer.valueOf(provisionSatelliteServiceArgument.subId))) {
                    provisionSatelliteServiceArgument.callback.accept(14);
                    notifyRequester(satelliteControllerHandlerRequest5);
                    return;
                }
                this.mSatelliteProvisionCallbacks.put(Integer.valueOf(provisionSatelliteServiceArgument.subId), provisionSatelliteServiceArgument.callback);
                Message obtainMessage2 = obtainMessage(8, satelliteControllerHandlerRequest5);
                if (this.mSatelliteModemInterface.isSatelliteServiceSupported()) {
                    this.mSatelliteModemInterface.provisionSatelliteService(provisionSatelliteServiceArgument.token, obtainMessage2);
                    return;
                }
                Phone phone2 = satelliteControllerHandlerRequest5.phone;
                if (phone2 != null) {
                    phone2.provisionSatelliteService(obtainMessage2, provisionSatelliteServiceArgument.token);
                    return;
                }
                loge("provisionSatelliteService: No phone object");
                provisionSatelliteServiceArgument.callback.accept(6);
                notifyRequester(satelliteControllerHandlerRequest5);
                return;
            case 8:
                AsyncResult asyncResult3 = (AsyncResult) message.obj;
                SatelliteControllerHandlerRequest satelliteControllerHandlerRequest6 = (SatelliteControllerHandlerRequest) asyncResult3.userObj;
                handleEventProvisionSatelliteServiceDone((ProvisionSatelliteServiceArgument) satelliteControllerHandlerRequest6.argument, SatelliteServiceUtils.getSatelliteError(asyncResult3, "provisionSatelliteService"));
                notifyRequester(satelliteControllerHandlerRequest6);
                return;
            case 9:
                SatelliteControllerHandlerRequest satelliteControllerHandlerRequest7 = (SatelliteControllerHandlerRequest) message.obj;
                ProvisionSatelliteServiceArgument provisionSatelliteServiceArgument2 = (ProvisionSatelliteServiceArgument) satelliteControllerHandlerRequest7.argument;
                Message obtainMessage3 = obtainMessage(10, satelliteControllerHandlerRequest7);
                if (this.mSatelliteModemInterface.isSatelliteServiceSupported()) {
                    this.mSatelliteModemInterface.deprovisionSatelliteService(provisionSatelliteServiceArgument2.token, obtainMessage3);
                    return;
                }
                Phone phone3 = satelliteControllerHandlerRequest7.phone;
                if (phone3 != null) {
                    phone3.deprovisionSatelliteService(obtainMessage3, provisionSatelliteServiceArgument2.token);
                    return;
                }
                loge("deprovisionSatelliteService: No phone object");
                Consumer<Integer> consumer = provisionSatelliteServiceArgument2.callback;
                if (consumer != null) {
                    consumer.accept(6);
                    return;
                }
                return;
            case 10:
                AsyncResult asyncResult4 = (AsyncResult) message.obj;
                handleEventDeprovisionSatelliteServiceDone((ProvisionSatelliteServiceArgument) ((SatelliteControllerHandlerRequest) asyncResult4.userObj).argument, SatelliteServiceUtils.getSatelliteError(asyncResult4, "deprovisionSatelliteService"));
                return;
            case 11:
                SatelliteControllerHandlerRequest satelliteControllerHandlerRequest8 = (SatelliteControllerHandlerRequest) message.obj;
                RequestSatelliteEnabledArgument requestSatelliteEnabledArgument = (RequestSatelliteEnabledArgument) satelliteControllerHandlerRequest8.argument;
                Message obtainMessage4 = obtainMessage(12, satelliteControllerHandlerRequest8);
                if (requestSatelliteEnabledArgument.enabled) {
                    if (this.mBluetoothAdapter == null) {
                        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    }
                    if (this.mWifiManager == null) {
                        this.mWifiManager = (WifiManager) this.mContext.getSystemService(WifiManager.class);
                    }
                    if (this.mBluetoothAdapter.isEnabled()) {
                        this.mBluetoothAdapter.disable();
                        this.mDisabledBTFlag = true;
                    }
                    if (this.mWifiManager.isWifiEnabled()) {
                        this.mWifiManager.setWifiEnabled(false);
                        this.mDisabledWifiFlag = true;
                    }
                }
                if (this.mSatelliteModemInterface.isSatelliteServiceSupported()) {
                    this.mSatelliteModemInterface.requestSatelliteEnabled(requestSatelliteEnabledArgument.enabled, obtainMessage4);
                    return;
                }
                Phone phone4 = satelliteControllerHandlerRequest8.phone;
                if (phone4 != null) {
                    phone4.setSatellitePower(obtainMessage4, requestSatelliteEnabledArgument.enabled);
                    return;
                }
                loge("requestSatelliteEnabled: No phone object");
                requestSatelliteEnabledArgument.callback.accept(6);
                return;
            case 12:
                AsyncResult asyncResult5 = (AsyncResult) message.obj;
                RequestSatelliteEnabledArgument requestSatelliteEnabledArgument2 = (RequestSatelliteEnabledArgument) ((SatelliteControllerHandlerRequest) asyncResult5.userObj).argument;
                int satelliteError2 = SatelliteServiceUtils.getSatelliteError(asyncResult5, "setSatelliteEnabled");
                if (satelliteError2 == 0) {
                    if (!requestSatelliteEnabledArgument2.enabled) {
                        if (this.mBluetoothAdapter == null) {
                            this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        }
                        if (this.mWifiManager == null) {
                            this.mWifiManager = (WifiManager) this.mContext.getSystemService(WifiManager.class);
                        }
                        if (!this.mBluetoothAdapter.isEnabled() && this.mDisabledBTFlag) {
                            this.mBluetoothAdapter.enable();
                        }
                        if (!this.mWifiManager.isWifiEnabled() && this.mDisabledWifiFlag) {
                            this.mWifiManager.setWifiEnabled(true);
                        }
                    }
                    if (this.mNeedsSatellitePointing) {
                        this.mPointingAppController.startPointingUI(false);
                    }
                }
                requestSatelliteEnabledArgument2.callback.accept(Integer.valueOf(satelliteError2));
                return;
            case 13:
                SatelliteControllerHandlerRequest satelliteControllerHandlerRequest9 = (SatelliteControllerHandlerRequest) message.obj;
                Message obtainMessage5 = obtainMessage(14, satelliteControllerHandlerRequest9);
                if (this.mSatelliteModemInterface.isSatelliteServiceSupported()) {
                    this.mSatelliteModemInterface.requestIsSatelliteEnabled(obtainMessage5);
                    return;
                }
                Phone phone5 = satelliteControllerHandlerRequest9.phone;
                if (phone5 != null) {
                    phone5.isSatellitePowerOn(obtainMessage5);
                    return;
                }
                loge("isSatelliteEnabled: No phone object");
                ((ResultReceiver) satelliteControllerHandlerRequest9.argument).send(6, null);
                return;
            case 14:
                AsyncResult asyncResult6 = (AsyncResult) message.obj;
                SatelliteControllerHandlerRequest satelliteControllerHandlerRequest10 = (SatelliteControllerHandlerRequest) asyncResult6.userObj;
                int satelliteError3 = SatelliteServiceUtils.getSatelliteError(asyncResult6, "isSatelliteEnabled");
                Bundle bundle2 = new Bundle();
                if (satelliteError3 == 0) {
                    Object obj2 = asyncResult6.result;
                    if (obj2 == null) {
                        loge("isSatelliteEnabled: result is null");
                        ((ResultReceiver) satelliteControllerHandlerRequest10.argument).send(i, bundle2);
                        return;
                    }
                    bundle2.putBoolean("satellite_enabled", ((int[]) obj2)[0] == 1);
                }
                i = satelliteError3;
                ((ResultReceiver) satelliteControllerHandlerRequest10.argument).send(i, bundle2);
                return;
            case 15:
                SatelliteControllerHandlerRequest satelliteControllerHandlerRequest11 = (SatelliteControllerHandlerRequest) message.obj;
                Message obtainMessage6 = obtainMessage(16, satelliteControllerHandlerRequest11);
                if (this.mSatelliteModemInterface.isSatelliteServiceSupported()) {
                    this.mSatelliteModemInterface.requestIsSatelliteSupported(obtainMessage6);
                    return;
                }
                Phone phone6 = satelliteControllerHandlerRequest11.phone;
                if (phone6 != null) {
                    phone6.isSatelliteSupported(obtainMessage6);
                    return;
                }
                loge("isSatelliteSupported: No phone object");
                ((ResultReceiver) satelliteControllerHandlerRequest11.argument).send(6, null);
                return;
            case 16:
                AsyncResult asyncResult7 = (AsyncResult) message.obj;
                SatelliteControllerHandlerRequest satelliteControllerHandlerRequest12 = (SatelliteControllerHandlerRequest) asyncResult7.userObj;
                int satelliteError4 = SatelliteServiceUtils.getSatelliteError(asyncResult7, "isSatelliteSupported");
                Bundle bundle3 = new Bundle();
                if (satelliteError4 == 0) {
                    Object obj3 = asyncResult7.result;
                    if (obj3 == null) {
                        loge("isSatelliteSupported: result is null");
                        ((ResultReceiver) satelliteControllerHandlerRequest12.argument).send(i, bundle3);
                        return;
                    }
                    boolean booleanValue = ((Boolean) obj3).booleanValue();
                    bundle3.putBoolean("satellite_supported", booleanValue);
                    synchronized (this.mIsSatelliteSupportedLock) {
                        this.mIsSatelliteSupported = Boolean.valueOf(booleanValue);
                    }
                } else {
                    synchronized (this.mIsSatelliteSupportedLock) {
                        this.mIsSatelliteSupported = null;
                    }
                }
                i = satelliteError4;
                ((ResultReceiver) satelliteControllerHandlerRequest12.argument).send(i, bundle3);
                return;
            case 17:
                SatelliteControllerHandlerRequest satelliteControllerHandlerRequest13 = (SatelliteControllerHandlerRequest) message.obj;
                Message obtainMessage7 = obtainMessage(18, satelliteControllerHandlerRequest13);
                if (this.mSatelliteModemInterface.isSatelliteServiceSupported()) {
                    this.mSatelliteModemInterface.requestSatelliteCapabilities(obtainMessage7);
                    return;
                }
                Phone phone7 = satelliteControllerHandlerRequest13.phone;
                if (phone7 != null) {
                    phone7.getSatelliteCapabilities(obtainMessage7);
                    return;
                }
                loge("getSatelliteCapabilities: No phone object");
                ((ResultReceiver) satelliteControllerHandlerRequest13.argument).send(6, null);
                return;
            case 18:
                AsyncResult asyncResult8 = (AsyncResult) message.obj;
                SatelliteControllerHandlerRequest satelliteControllerHandlerRequest14 = (SatelliteControllerHandlerRequest) asyncResult8.userObj;
                int satelliteError5 = SatelliteServiceUtils.getSatelliteError(asyncResult8, "getSatelliteCapabilities");
                Bundle bundle4 = new Bundle();
                if (satelliteError5 == 0) {
                    Object obj4 = asyncResult8.result;
                    if (obj4 == null) {
                        loge("getSatelliteCapabilities: result is null");
                        ((ResultReceiver) satelliteControllerHandlerRequest14.argument).send(i, bundle4);
                        return;
                    }
                    SatelliteCapabilities satelliteCapabilities = (SatelliteCapabilities) obj4;
                    synchronized (this.mNeedsSatellitePointingLock) {
                        this.mNeedsSatellitePointing = satelliteCapabilities.needsPointingToSatellite();
                    }
                    bundle4.putParcelable("satellite_capabilities", satelliteCapabilities);
                }
                i = satelliteError5;
                ((ResultReceiver) satelliteControllerHandlerRequest14.argument).send(i, bundle4);
                return;
            case 19:
                SatelliteControllerHandlerRequest satelliteControllerHandlerRequest15 = (SatelliteControllerHandlerRequest) message.obj;
                this.mDatagramController.pollPendingSatelliteDatagrams(obtainMessage(20, satelliteControllerHandlerRequest15), satelliteControllerHandlerRequest15.phone);
                return;
            case 20:
                AsyncResult asyncResult9 = (AsyncResult) message.obj;
                ((Consumer) ((SatelliteControllerHandlerRequest) asyncResult9.userObj).argument).accept(Integer.valueOf(SatelliteServiceUtils.getSatelliteError(asyncResult9, "pollPendingSatelliteDatagrams")));
                return;
            case 21:
                SatelliteControllerHandlerRequest satelliteControllerHandlerRequest16 = (SatelliteControllerHandlerRequest) message.obj;
                Message obtainMessage8 = obtainMessage(22, satelliteControllerHandlerRequest16);
                if (this.mSatelliteModemInterface.isSatelliteServiceSupported()) {
                    this.mSatelliteModemInterface.requestIsSatelliteCommunicationAllowedForCurrentLocation(obtainMessage8);
                    return;
                }
                Phone phone8 = satelliteControllerHandlerRequest16.phone;
                if (phone8 != null) {
                    phone8.isSatelliteCommunicationAllowedForCurrentLocation(obtainMessage8);
                    return;
                }
                loge("isSatelliteCommunicationAllowedForCurrentLocation: No phone object");
                ((ResultReceiver) satelliteControllerHandlerRequest16.argument).send(6, null);
                return;
            case 22:
                AsyncResult asyncResult10 = (AsyncResult) message.obj;
                SatelliteControllerHandlerRequest satelliteControllerHandlerRequest17 = (SatelliteControllerHandlerRequest) asyncResult10.userObj;
                int satelliteError6 = SatelliteServiceUtils.getSatelliteError(asyncResult10, "isSatelliteCommunicationAllowedForCurrentLocation");
                Bundle bundle5 = new Bundle();
                if (satelliteError6 == 0) {
                    Object obj5 = asyncResult10.result;
                    if (obj5 == null) {
                        loge("isSatelliteCommunicationAllowedForCurrentLocation: result is null");
                        ((ResultReceiver) satelliteControllerHandlerRequest17.argument).send(i, bundle5);
                        return;
                    }
                    bundle5.putBoolean("satellite_communication_allowed", ((Boolean) obj5).booleanValue());
                }
                i = satelliteError6;
                ((ResultReceiver) satelliteControllerHandlerRequest17.argument).send(i, bundle5);
                return;
            case 23:
                SatelliteControllerHandlerRequest satelliteControllerHandlerRequest18 = (SatelliteControllerHandlerRequest) message.obj;
                Message obtainMessage9 = obtainMessage(24, satelliteControllerHandlerRequest18);
                if (this.mSatelliteModemInterface.isSatelliteServiceSupported()) {
                    this.mSatelliteModemInterface.requestTimeForNextSatelliteVisibility(obtainMessage9);
                    return;
                }
                Phone phone9 = satelliteControllerHandlerRequest18.phone;
                if (phone9 != null) {
                    phone9.requestTimeForNextSatelliteVisibility(obtainMessage9);
                    return;
                }
                loge("requestTimeForNextSatelliteVisibility: No phone object");
                ((ResultReceiver) satelliteControllerHandlerRequest18.argument).send(6, null);
                return;
            case 24:
                AsyncResult asyncResult11 = (AsyncResult) message.obj;
                SatelliteControllerHandlerRequest satelliteControllerHandlerRequest19 = (SatelliteControllerHandlerRequest) asyncResult11.userObj;
                int satelliteError7 = SatelliteServiceUtils.getSatelliteError(asyncResult11, "requestTimeForNextSatelliteVisibility");
                Bundle bundle6 = new Bundle();
                if (satelliteError7 == 0) {
                    Object obj6 = asyncResult11.result;
                    if (obj6 == null) {
                        loge("requestTimeForNextSatelliteVisibility: result is null");
                        ((ResultReceiver) satelliteControllerHandlerRequest19.argument).send(i, bundle6);
                        return;
                    }
                    bundle6.putInt("satellite_next_visibility", ((int[]) obj6)[0]);
                }
                i = satelliteError7;
                ((ResultReceiver) satelliteControllerHandlerRequest19.argument).send(i, bundle6);
                return;
            default:
                Log.w("SatelliteController", "SatelliteControllerHandler: unexpected message code: " + message.what);
                return;
        }
    }

    private void notifyRequester(SatelliteControllerHandlerRequest satelliteControllerHandlerRequest) {
        synchronized (satelliteControllerHandlerRequest) {
            satelliteControllerHandlerRequest.notifyAll();
        }
    }

    public void requestSatelliteEnabled(int i, boolean z, IIntegerConsumer iIntegerConsumer) {
        Objects.requireNonNull(iIntegerConsumer);
        Consumer ignoreRemoteException = FunctionalUtils.ignoreRemoteException(new SatelliteController$$ExternalSyntheticLambda0(iIntegerConsumer));
        if (!isSatelliteSupported()) {
            ignoreRemoteException.accept(20);
        } else if (!isSatelliteProvisioned(SatelliteServiceUtils.getValidSatelliteSubId(i, this.mContext))) {
            ignoreRemoteException.accept(13);
        } else {
            sendRequestAsync(11, new RequestSatelliteEnabledArgument(z, ignoreRemoteException), SatelliteServiceUtils.getPhone());
        }
    }

    public void requestIsSatelliteEnabled(int i, ResultReceiver resultReceiver) {
        if (!isSatelliteSupported()) {
            resultReceiver.send(20, null);
        } else if (!isSatelliteProvisioned(SatelliteServiceUtils.getValidSatelliteSubId(i, this.mContext))) {
            resultReceiver.send(13, null);
        } else {
            sendRequest(13, resultReceiver, SatelliteServiceUtils.getPhone());
        }
    }

    public void requestSatelliteDemoModeEnabled(int i, boolean z, IIntegerConsumer iIntegerConsumer) {
        Objects.requireNonNull(iIntegerConsumer);
        Consumer ignoreRemoteException = FunctionalUtils.ignoreRemoteException(new SatelliteController$$ExternalSyntheticLambda0(iIntegerConsumer));
        if (!isSatelliteSupported()) {
            ignoreRemoteException.accept(20);
        } else if (!isSatelliteProvisioned(SatelliteServiceUtils.getValidSatelliteSubId(i, this.mContext))) {
            ignoreRemoteException.accept(13);
        } else {
            this.mIsSatelliteDemoModeEnabled = z;
            ignoreRemoteException.accept(0);
        }
    }

    public void requestIsSatelliteDemoModeEnabled(int i, ResultReceiver resultReceiver) {
        if (!isSatelliteSupported()) {
            resultReceiver.send(20, null);
        } else if (!isSatelliteProvisioned(SatelliteServiceUtils.getValidSatelliteSubId(i, this.mContext))) {
            resultReceiver.send(13, null);
        } else {
            Bundle bundle = new Bundle();
            bundle.putBoolean("demo_mode_enabled", this.mIsSatelliteDemoModeEnabled);
            resultReceiver.send(0, bundle);
        }
    }

    public void requestIsSatelliteSupported(int i, ResultReceiver resultReceiver) {
        synchronized (this.mIsSatelliteSupportedLock) {
            if (this.mIsSatelliteSupported != null) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("satellite_supported", this.mIsSatelliteSupported.booleanValue());
                resultReceiver.send(6, bundle);
                return;
            }
            SatelliteServiceUtils.getValidSatelliteSubId(i, this.mContext);
            sendRequestAsync(15, resultReceiver, SatelliteServiceUtils.getPhone());
        }
    }

    public void requestSatelliteCapabilities(int i, ResultReceiver resultReceiver) {
        if (!isSatelliteSupported()) {
            resultReceiver.send(20, null);
            return;
        }
        SatelliteServiceUtils.getValidSatelliteSubId(i, this.mContext);
        sendRequestAsync(17, resultReceiver, SatelliteServiceUtils.getPhone());
    }

    public void startSatellitePositionUpdates(int i, IIntegerConsumer iIntegerConsumer, ISatellitePositionUpdateCallback iSatellitePositionUpdateCallback) {
        Objects.requireNonNull(iIntegerConsumer);
        Consumer ignoreRemoteException = FunctionalUtils.ignoreRemoteException(new SatelliteController$$ExternalSyntheticLambda0(iIntegerConsumer));
        if (!isSatelliteSupported()) {
            ignoreRemoteException.accept(20);
            return;
        }
        int validSatelliteSubId = SatelliteServiceUtils.getValidSatelliteSubId(i, this.mContext);
        if (!isSatelliteProvisioned(validSatelliteSubId)) {
            ignoreRemoteException.accept(13);
            return;
        }
        Phone phone = SatelliteServiceUtils.getPhone();
        this.mPointingAppController.registerForSatellitePositionUpdates(validSatelliteSubId, iSatellitePositionUpdateCallback, phone);
        sendRequestAsync(1, new SatellitePositionUpdateArgument(ignoreRemoteException, iSatellitePositionUpdateCallback, validSatelliteSubId), phone);
    }

    public void stopSatellitePositionUpdates(int i, IIntegerConsumer iIntegerConsumer, ISatellitePositionUpdateCallback iSatellitePositionUpdateCallback) {
        Objects.requireNonNull(iIntegerConsumer);
        Consumer<Integer> ignoreRemoteException = FunctionalUtils.ignoreRemoteException(new SatelliteController$$ExternalSyntheticLambda0(iIntegerConsumer));
        if (!isSatelliteSupported()) {
            ignoreRemoteException.accept(20);
            return;
        }
        int validSatelliteSubId = SatelliteServiceUtils.getValidSatelliteSubId(i, this.mContext);
        if (!isSatelliteProvisioned(validSatelliteSubId)) {
            ignoreRemoteException.accept(13);
            return;
        }
        Phone phone = SatelliteServiceUtils.getPhone();
        this.mPointingAppController.unregisterForSatellitePositionUpdates(validSatelliteSubId, ignoreRemoteException, iSatellitePositionUpdateCallback, phone);
        sendRequestAsync(3, ignoreRemoteException, phone);
    }

    public void requestMaxSizePerSendingDatagram(int i, ResultReceiver resultReceiver) {
        if (!isSatelliteSupported()) {
            resultReceiver.send(20, null);
        } else if (!isSatelliteProvisioned(SatelliteServiceUtils.getValidSatelliteSubId(i, this.mContext))) {
            resultReceiver.send(13, null);
        } else {
            sendRequestAsync(5, resultReceiver, SatelliteServiceUtils.getPhone());
        }
    }

    public ICancellationSignal provisionSatelliteService(int i, final String str, IIntegerConsumer iIntegerConsumer) {
        Objects.requireNonNull(iIntegerConsumer);
        Consumer ignoreRemoteException = FunctionalUtils.ignoreRemoteException(new SatelliteController$$ExternalSyntheticLambda0(iIntegerConsumer));
        if (!isSatelliteSupported()) {
            ignoreRemoteException.accept(20);
            return null;
        }
        final int validSatelliteSubId = SatelliteServiceUtils.getValidSatelliteSubId(i, this.mContext);
        final Phone phone = SatelliteServiceUtils.getPhone();
        if (this.mSatelliteProvisionCallbacks.containsKey(Integer.valueOf(validSatelliteSubId))) {
            ignoreRemoteException.accept(14);
            return null;
        } else if (isSatelliteProvisioned(validSatelliteSubId)) {
            ignoreRemoteException.accept(0);
            return null;
        } else {
            sendRequestAsync(7, new ProvisionSatelliteServiceArgument(str, ignoreRemoteException, validSatelliteSubId), phone);
            ICancellationSignal createTransport = CancellationSignal.createTransport();
            CancellationSignal.fromTransport(createTransport).setOnCancelListener(new CancellationSignal.OnCancelListener() { // from class: com.android.internal.telephony.satellite.SatelliteController$$ExternalSyntheticLambda1
                @Override // android.os.CancellationSignal.OnCancelListener
                public final void onCancel() {
                    SatelliteController.this.lambda$provisionSatelliteService$0(str, validSatelliteSubId, phone);
                }
            });
            return createTransport;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$provisionSatelliteService$0(String str, int i, Phone phone) {
        sendRequestAsync(9, new ProvisionSatelliteServiceArgument(str, null, i), phone);
    }

    public void deprovisionSatelliteService(int i, String str, IIntegerConsumer iIntegerConsumer) {
        Objects.requireNonNull(iIntegerConsumer);
        Consumer ignoreRemoteException = FunctionalUtils.ignoreRemoteException(new SatelliteController$$ExternalSyntheticLambda0(iIntegerConsumer));
        if (!isSatelliteSupported()) {
            ignoreRemoteException.accept(20);
            return;
        }
        int validSatelliteSubId = SatelliteServiceUtils.getValidSatelliteSubId(i, this.mContext);
        if (!isSatelliteProvisioned(validSatelliteSubId)) {
            ignoreRemoteException.accept(0);
            return;
        }
        sendRequestAsync(9, new ProvisionSatelliteServiceArgument(str, ignoreRemoteException, validSatelliteSubId), SatelliteServiceUtils.getPhone());
    }

    public int registerForSatelliteProvisionStateChanged(int i, ISatelliteProvisionStateCallback iSatelliteProvisionStateCallback) {
        if (isSatelliteSupported()) {
            int validSatelliteSubId = SatelliteServiceUtils.getValidSatelliteSubId(i, this.mContext);
            Phone phone = SatelliteServiceUtils.getPhone();
            SatelliteProvisionStateChangedHandler satelliteProvisionStateChangedHandler = this.mSatelliteProvisionStateChangedHandlers.get(Integer.valueOf(validSatelliteSubId));
            if (satelliteProvisionStateChangedHandler == null) {
                satelliteProvisionStateChangedHandler = new SatelliteProvisionStateChangedHandler(Looper.getMainLooper(), validSatelliteSubId);
                if (this.mSatelliteModemInterface.isSatelliteServiceSupported()) {
                    this.mSatelliteModemInterface.registerForSatelliteProvisionStateChanged(satelliteProvisionStateChangedHandler, 1, null);
                } else {
                    phone.registerForSatelliteProvisionStateChanged(satelliteProvisionStateChangedHandler, 1, null);
                }
            }
            if (iSatelliteProvisionStateCallback != null) {
                satelliteProvisionStateChangedHandler.addListener(iSatelliteProvisionStateCallback);
            }
            this.mSatelliteProvisionStateChangedHandlers.put(Integer.valueOf(validSatelliteSubId), satelliteProvisionStateChangedHandler);
            return 0;
        }
        return 20;
    }

    public void unregisterForSatelliteProvisionStateChanged(int i, ISatelliteProvisionStateCallback iSatelliteProvisionStateCallback) {
        SatelliteProvisionStateChangedHandler satelliteProvisionStateChangedHandler = this.mSatelliteProvisionStateChangedHandlers.get(Integer.valueOf(SatelliteServiceUtils.getValidSatelliteSubId(i, this.mContext)));
        if (satelliteProvisionStateChangedHandler != null) {
            satelliteProvisionStateChangedHandler.removeListener(iSatelliteProvisionStateCallback);
        }
    }

    public void requestIsSatelliteProvisioned(int i, ResultReceiver resultReceiver) {
        if (!isSatelliteSupported()) {
            resultReceiver.send(20, null);
            return;
        }
        int validSatelliteSubId = SatelliteServiceUtils.getValidSatelliteSubId(i, this.mContext);
        Bundle bundle = new Bundle();
        bundle.putBoolean("satellite_provisioned", isSatelliteProvisioned(validSatelliteSubId));
        resultReceiver.send(0, bundle);
    }

    public int registerForSatelliteModemStateChanged(int i, ISatelliteStateCallback iSatelliteStateCallback) {
        int validSatelliteSubId = SatelliteServiceUtils.getValidSatelliteSubId(i, this.mContext);
        Phone phone = SatelliteServiceUtils.getPhone();
        SatelliteStateListenerHandler satelliteStateListenerHandler = this.mSatelliteStateListenerHandlers.get(Integer.valueOf(validSatelliteSubId));
        if (satelliteStateListenerHandler == null) {
            satelliteStateListenerHandler = new SatelliteStateListenerHandler(Looper.getMainLooper(), validSatelliteSubId);
            if (this.mSatelliteModemInterface.isSatelliteServiceSupported()) {
                this.mSatelliteModemInterface.registerForSatelliteModemStateChanged(satelliteStateListenerHandler, 1, null);
                this.mSatelliteModemInterface.registerForPendingDatagramCount(satelliteStateListenerHandler, 2, null);
            } else {
                phone.registerForSatelliteModemStateChanged(satelliteStateListenerHandler, 1, null);
                phone.registerForPendingDatagramCount(satelliteStateListenerHandler, 2, null);
            }
        }
        satelliteStateListenerHandler.addListener(iSatelliteStateCallback);
        this.mSatelliteStateListenerHandlers.put(Integer.valueOf(validSatelliteSubId), satelliteStateListenerHandler);
        return 0;
    }

    public void unregisterForSatelliteModemStateChanged(int i, ISatelliteStateCallback iSatelliteStateCallback) {
        int validSatelliteSubId = SatelliteServiceUtils.getValidSatelliteSubId(i, this.mContext);
        SatelliteStateListenerHandler satelliteStateListenerHandler = this.mSatelliteStateListenerHandlers.get(Integer.valueOf(validSatelliteSubId));
        if (satelliteStateListenerHandler != null) {
            satelliteStateListenerHandler.removeListener(iSatelliteStateCallback);
            if (satelliteStateListenerHandler.hasListeners()) {
                return;
            }
            this.mSatelliteStateListenerHandlers.remove(Integer.valueOf(validSatelliteSubId));
            if (this.mSatelliteModemInterface.isSatelliteServiceSupported()) {
                this.mSatelliteModemInterface.unregisterForSatelliteModemStateChanged(satelliteStateListenerHandler);
                this.mSatelliteModemInterface.unregisterForPendingDatagramCount(satelliteStateListenerHandler);
                return;
            }
            Phone phone = SatelliteServiceUtils.getPhone();
            if (phone != null) {
                phone.unregisterForSatelliteModemStateChanged(satelliteStateListenerHandler);
                phone.unregisterForPendingDatagramCount(satelliteStateListenerHandler);
            }
        }
    }

    public int registerForSatelliteDatagram(int i, int i2, ISatelliteDatagramCallback iSatelliteDatagramCallback) {
        return this.mDatagramController.registerForSatelliteDatagram(i, i2, iSatelliteDatagramCallback);
    }

    public void unregisterForSatelliteDatagram(int i, ISatelliteDatagramCallback iSatelliteDatagramCallback) {
        this.mDatagramController.unregisterForSatelliteDatagram(i, iSatelliteDatagramCallback);
    }

    public void pollPendingSatelliteDatagrams(int i, IIntegerConsumer iIntegerConsumer) {
        Objects.requireNonNull(iIntegerConsumer);
        Consumer ignoreRemoteException = FunctionalUtils.ignoreRemoteException(new SatelliteController$$ExternalSyntheticLambda0(iIntegerConsumer));
        if (!isSatelliteProvisioned(SatelliteServiceUtils.getValidSatelliteSubId(i, this.mContext))) {
            ignoreRemoteException.accept(13);
        } else {
            sendRequestAsync(19, ignoreRemoteException, SatelliteServiceUtils.getPhone());
        }
    }

    public void sendSatelliteDatagram(int i, int i2, SatelliteDatagram satelliteDatagram, boolean z, IIntegerConsumer iIntegerConsumer) {
        Objects.requireNonNull(iIntegerConsumer);
        Consumer<Integer> ignoreRemoteException = FunctionalUtils.ignoreRemoteException(new SatelliteController$$ExternalSyntheticLambda0(iIntegerConsumer));
        if (!isSatelliteProvisioned(SatelliteServiceUtils.getValidSatelliteSubId(i, this.mContext))) {
            ignoreRemoteException.accept(13);
            return;
        }
        if (this.mNeedsSatellitePointing) {
            this.mPointingAppController.startPointingUI(z);
        }
        this.mDatagramController.sendSatelliteDatagram(i2, satelliteDatagram, z, this.mIsSatelliteDemoModeEnabled, ignoreRemoteException);
    }

    public void requestIsSatelliteCommunicationAllowedForCurrentLocation(int i, ResultReceiver resultReceiver) {
        if (!isSatelliteSupported()) {
            resultReceiver.send(20, null);
        } else {
            sendRequest(21, resultReceiver, SatelliteServiceUtils.getPhone());
        }
    }

    public void requestTimeForNextSatelliteVisibility(int i, ResultReceiver resultReceiver) {
        if (!isSatelliteSupported()) {
            resultReceiver.send(20, null);
        } else if (!isSatelliteProvisioned(SatelliteServiceUtils.getValidSatelliteSubId(i, this.mContext))) {
            resultReceiver.send(13, null);
        } else {
            sendRequestAsync(23, resultReceiver, SatelliteServiceUtils.getPhone());
        }
    }

    private void handleEventProvisionSatelliteServiceDone(ProvisionSatelliteServiceArgument provisionSatelliteServiceArgument, int i) {
        logd("handleEventProvisionSatelliteServiceDone: result=" + i + ", subId=" + provisionSatelliteServiceArgument.subId);
        Consumer<Integer> remove = this.mSatelliteProvisionCallbacks.remove(Integer.valueOf(provisionSatelliteServiceArgument.subId));
        if (remove == null) {
            loge("handleEventProvisionSatelliteServiceDone: callback is null for subId=" + provisionSatelliteServiceArgument.subId);
            return;
        }
        remove.accept(Integer.valueOf(i));
        if (i == 0) {
            setSatelliteProvisioned(provisionSatelliteServiceArgument.subId, true);
        }
        registerForSatelliteProvisionStateChanged(provisionSatelliteServiceArgument.subId, null);
    }

    private void handleEventDeprovisionSatelliteServiceDone(ProvisionSatelliteServiceArgument provisionSatelliteServiceArgument, int i) {
        if (provisionSatelliteServiceArgument == null) {
            loge("handleEventDeprovisionSatelliteServiceDone: arg is null");
            return;
        }
        logd("handleEventDeprovisionSatelliteServiceDone: result=" + i + ", subId=" + provisionSatelliteServiceArgument.subId);
        Consumer<Integer> consumer = provisionSatelliteServiceArgument.callback;
        if (consumer != null) {
            consumer.accept(Integer.valueOf(i));
        }
        if (i == 0) {
            setSatelliteProvisioned(provisionSatelliteServiceArgument.subId, false);
        }
    }

    private void handleStartSatellitePositionUpdatesDone(AsyncResult asyncResult) {
        SatelliteControllerHandlerRequest satelliteControllerHandlerRequest = (SatelliteControllerHandlerRequest) asyncResult.userObj;
        SatellitePositionUpdateArgument satellitePositionUpdateArgument = (SatellitePositionUpdateArgument) satelliteControllerHandlerRequest.argument;
        int satelliteError = SatelliteServiceUtils.getSatelliteError(asyncResult, "handleStartSatellitePositionUpdatesDone");
        satellitePositionUpdateArgument.errorCallback.accept(Integer.valueOf(satelliteError));
        if (satelliteError != 0) {
            this.mPointingAppController.setStartedSatellitePositionUpdates(false);
            this.mPointingAppController.unregisterForSatellitePositionUpdates(satellitePositionUpdateArgument.subId, satellitePositionUpdateArgument.errorCallback, satellitePositionUpdateArgument.callback, satelliteControllerHandlerRequest.phone);
            return;
        }
        this.mPointingAppController.setStartedSatellitePositionUpdates(true);
    }

    private synchronized void setSatelliteProvisioned(int i, boolean z) {
        if (i != Integer.MAX_VALUE) {
            SubscriptionManager.setSubscriptionProperty(i, "satellite_enabled", z ? "1" : "0");
        }
    }

    public boolean isSatelliteSupported() {
        synchronized (this.mIsSatelliteSupportedLock) {
            Boolean bool = this.mIsSatelliteSupported;
            if (bool != null) {
                return bool.booleanValue();
            }
            requestIsSatelliteSupported(KeepaliveStatus.INVALID_HANDLE, this.mSatelliteSupportedReceiver);
            return false;
        }
    }

    private void sendRequestAsync(int i, Object obj, Phone phone) {
        obtainMessage(i, new SatelliteControllerHandlerRequest(obj, phone)).sendToTarget();
    }

    private Object sendRequest(int i, Object obj, Phone phone) {
        Object obj2;
        if (Looper.myLooper() == getLooper()) {
            throw new RuntimeException("This method will deadlock if called from the main thread");
        }
        SatelliteControllerHandlerRequest satelliteControllerHandlerRequest = new SatelliteControllerHandlerRequest(obj, phone);
        obtainMessage(i, satelliteControllerHandlerRequest).sendToTarget();
        synchronized (satelliteControllerHandlerRequest) {
            while (true) {
                obj2 = satelliteControllerHandlerRequest.result;
                if (obj2 == null) {
                    try {
                        satelliteControllerHandlerRequest.wait();
                    } catch (InterruptedException unused) {
                    }
                }
            }
        }
        return obj2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @VisibleForTesting
    public boolean isSatelliteProvisioned(int i) {
        String subscriptionProperty;
        long clearCallingIdentity = Binder.clearCallingIdentity();
        if (i != Integer.MAX_VALUE) {
            try {
                if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
                    subscriptionProperty = SubscriptionManagerService.getInstance().getSubscriptionProperty(i, "satellite_enabled", this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
                } else {
                    subscriptionProperty = SubscriptionController.getInstance().getSubscriptionProperty(i, "satellite_enabled");
                }
                if (subscriptionProperty != null) {
                    return Integer.parseInt(subscriptionProperty) == 1;
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void logd(String str) {
        Rlog.d("SatelliteController", str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void loge(String str) {
        Rlog.e("SatelliteController", str);
    }
}
