package com.android.internal.telephony;

import android.content.Context;
import android.hardware.radio.RadioResponseInfo;
import android.hardware.radio.config.IRadioConfig;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.Trace;
import android.os.WorkSource;
import android.telephony.TelephonyManager;
import android.telephony.UiccSlotMapping;
import android.util.SparseArray;
import com.android.internal.telephony.imsphone.ImsRttTextHandler;
import com.android.internal.telephony.uicc.IccRecords;
import com.android.telephony.Rlog;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;
/* loaded from: classes.dex */
public class RadioConfig extends Handler {
    private static Context sContext;
    private static RadioConfig sRadioConfig;
    private final WorkSource mDefaultWorkSource;
    private final int[] mDeviceNrCapabilities;
    private final boolean mIsMobileNetworkSupported;
    private MockModem mMockModem;
    private final RadioConfigProxy mRadioConfigProxy;
    protected Registrant mSimSlotStatusRegistrant;
    private static final Object sLock = new Object();
    static final HalVersion RADIO_CONFIG_HAL_VERSION_UNKNOWN = new HalVersion(-1, -1);
    static final HalVersion RADIO_CONFIG_HAL_VERSION_1_0 = new HalVersion(1, 0);
    static final HalVersion RADIO_CONFIG_HAL_VERSION_1_1 = new HalVersion(1, 1);
    static final HalVersion RADIO_CONFIG_HAL_VERSION_1_3 = new HalVersion(1, 3);
    static final HalVersion RADIO_CONFIG_HAL_VERSION_2_0 = new HalVersion(2, 0);
    private final SparseArray<RILRequest> mRequestList = new SparseArray<>();
    private final AtomicLong mRadioConfigProxyCookie = new AtomicLong(0);

    private boolean isMobileDataCapable(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        return telephonyManager != null && telephonyManager.isDataCapable();
    }

    private RadioConfig(Context context, HalVersion halVersion) {
        this.mIsMobileNetworkSupported = isMobileDataCapable(context);
        this.mRadioConfigProxy = new RadioConfigProxy(this, halVersion);
        this.mDefaultWorkSource = new WorkSource(context.getApplicationInfo().uid, context.getPackageName());
        boolean z = context.getResources().getBoolean(17891846);
        boolean z2 = context.getResources().getBoolean(17891845);
        if (!z && !z2) {
            this.mDeviceNrCapabilities = new int[0];
            return;
        }
        ArrayList arrayList = new ArrayList();
        if (z2) {
            arrayList.add(1);
        }
        if (z) {
            arrayList.add(2);
        }
        this.mDeviceNrCapabilities = arrayList.stream().mapToInt(new RadioConfig$$ExternalSyntheticLambda0()).toArray();
    }

    public static RadioConfig getInstance() {
        RadioConfig radioConfig;
        synchronized (sLock) {
            radioConfig = sRadioConfig;
            if (radioConfig == null) {
                throw new RuntimeException("RadioConfig.getInstance can't be called before make()");
            }
        }
        return radioConfig;
    }

    public static RadioConfig make(Context context, HalVersion halVersion) {
        RadioConfig radioConfig;
        synchronized (sLock) {
            if (sRadioConfig != null) {
                throw new RuntimeException("RadioConfig.make() should only be called once");
            }
            sContext = context;
            radioConfig = new RadioConfig(context, halVersion);
            sRadioConfig = radioConfig;
        }
        return radioConfig;
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i = message.what;
        if (i != 1) {
            if (i == 2) {
                logd("handleMessage: EVENT_AIDL_SERVICE_DEAD mRadioConfigProxyCookie = " + this.mRadioConfigProxyCookie.get());
                resetProxyAndRequestList("EVENT_AIDL_SERVICE_DEAD", null);
                return;
            }
            return;
        }
        logd("handleMessage: EVENT_HIDL_SERVICE_DEAD cookie = " + message.obj + " mRadioConfigProxyCookie = " + this.mRadioConfigProxyCookie.get());
        if (((Long) message.obj).longValue() == this.mRadioConfigProxyCookie.get()) {
            resetProxyAndRequestList("EVENT_HIDL_SERVICE_DEAD", null);
        }
    }

    private void clearRequestList(int i, boolean z) {
        synchronized (this.mRequestList) {
            int size = this.mRequestList.size();
            if (z) {
                logd("clearRequestList: mRequestList=" + size);
            }
            for (int i2 = 0; i2 < size; i2++) {
                RILRequest valueAt = this.mRequestList.valueAt(i2);
                if (z) {
                    logd(i2 + ": [" + valueAt.mSerial + "] " + RILUtils.requestToString(valueAt.mRequest));
                }
                valueAt.onError(i, null);
                valueAt.release();
            }
            this.mRequestList.clear();
        }
    }

    private void resetProxyAndRequestList(String str, Exception exc) {
        loge(str + ": " + exc);
        this.mRadioConfigProxy.clear();
        this.mRadioConfigProxyCookie.incrementAndGet();
        RILRequest.resetSerial();
        clearRequestList(1, false);
        getRadioConfigProxy(null);
    }

    public RadioConfigProxy getRadioConfigProxy(Message message) {
        if (!this.mIsMobileNetworkSupported) {
            if (message != null) {
                AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(1));
                message.sendToTarget();
            }
            this.mRadioConfigProxy.clear();
            return this.mRadioConfigProxy;
        } else if (!this.mRadioConfigProxy.isEmpty()) {
            return this.mRadioConfigProxy;
        } else {
            updateRadioConfigProxy();
            if (this.mRadioConfigProxy.isEmpty() && message != null) {
                AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(1));
                message.sendToTarget();
            }
            return this.mRadioConfigProxy;
        }
    }

    public boolean setModemService(String str) {
        IBinder serviceBinder;
        boolean z = true;
        if (str != null) {
            logd("Overriding connected service to MockModemService");
            this.mMockModem = null;
            MockModem mockModem = new MockModem(sContext, str);
            this.mMockModem = mockModem;
            mockModem.bindToMockModemService(9);
            int i = 0;
            do {
                serviceBinder = this.mMockModem.getServiceBinder(9);
                i++;
                if (serviceBinder == null) {
                    logd("Retry(" + i + ") Mock RadioConfig");
                    try {
                        Thread.sleep(300L);
                    } catch (InterruptedException unused) {
                    }
                }
                if (serviceBinder != null) {
                    break;
                }
            } while (i < 10);
            if (serviceBinder == null) {
                loge("Mock RadioConfig bind fail");
                z = false;
            }
            if (z) {
                resetProxyAndRequestList("EVENT_HIDL_SERVICE_DEAD", null);
            }
        }
        if (str == null || !z) {
            if (z) {
                logd("Unbinding to mock RadioConfig service");
            }
            if (this.mMockModem != null) {
                this.mMockModem = null;
                resetProxyAndRequestList("EVENT_AIDL_SERVICE_DEAD", null);
            }
        }
        return z;
    }

    private void updateRadioConfigProxy() {
        IBinder serviceBinder;
        MockModem mockModem = this.mMockModem;
        if (mockModem == null) {
            serviceBinder = ServiceManager.waitForDeclaredService(IRadioConfig.DESCRIPTOR + "/default");
        } else {
            serviceBinder = mockModem.getServiceBinder(9);
        }
        if (serviceBinder != null) {
            this.mRadioConfigProxy.setAidl(RADIO_CONFIG_HAL_VERSION_2_0, IRadioConfig.Stub.asInterface(serviceBinder));
        }
        if (this.mRadioConfigProxy.isEmpty()) {
            try {
                this.mRadioConfigProxy.setHidl(RADIO_CONFIG_HAL_VERSION_1_3, android.hardware.radio.config.V1_3.IRadioConfig.getService(true));
            } catch (RemoteException | NoSuchElementException e) {
                this.mRadioConfigProxy.clear();
                loge("getHidlRadioConfigProxy1_3: RadioConfigProxy getService: " + e);
            }
        }
        if (this.mRadioConfigProxy.isEmpty()) {
            try {
                this.mRadioConfigProxy.setHidl(RADIO_CONFIG_HAL_VERSION_1_1, android.hardware.radio.config.V1_1.IRadioConfig.getService(true));
            } catch (RemoteException | NoSuchElementException e2) {
                this.mRadioConfigProxy.clear();
                loge("getHidlRadioConfigProxy1_1: RadioConfigProxy getService | linkToDeath: " + e2);
            }
        }
        if (this.mRadioConfigProxy.isEmpty()) {
            try {
                this.mRadioConfigProxy.setHidl(RADIO_CONFIG_HAL_VERSION_1_0, android.hardware.radio.config.V1_0.IRadioConfig.getService(true));
            } catch (RemoteException | NoSuchElementException e3) {
                this.mRadioConfigProxy.clear();
                loge("getHidlRadioConfigProxy1_0: RadioConfigProxy getService | linkToDeath: " + e3);
            }
        }
        if (!this.mRadioConfigProxy.isEmpty()) {
            try {
                this.mRadioConfigProxy.linkToDeath(this.mRadioConfigProxyCookie.incrementAndGet());
                this.mRadioConfigProxy.setResponseFunctions(this);
                return;
            } catch (RemoteException unused) {
                this.mRadioConfigProxy.clear();
                loge("RadioConfigProxy: failed to linkToDeath() or setResponseFunction()");
            }
        }
        loge("getRadioConfigProxy: mRadioConfigProxy == null");
    }

    private RILRequest obtainRequest(int i, Message message, WorkSource workSource) {
        RILRequest obtain = RILRequest.obtain(i, message, workSource);
        Trace.asyncTraceForTrackBegin(2097152L, "RIL", RILUtils.requestToString(obtain.mRequest), obtain.mSerial);
        synchronized (this.mRequestList) {
            this.mRequestList.append(obtain.mSerial, obtain);
        }
        return obtain;
    }

    private RILRequest findAndRemoveRequestFromList(int i) {
        RILRequest rILRequest;
        synchronized (this.mRequestList) {
            rILRequest = this.mRequestList.get(i);
            if (rILRequest != null) {
                Trace.asyncTraceForTrackEnd(2097152L, "RIL", rILRequest.mSerial);
                this.mRequestList.remove(i);
            }
        }
        return rILRequest;
    }

    public RILRequest processResponse(RadioResponseInfo radioResponseInfo) {
        int i = radioResponseInfo.serial;
        int i2 = radioResponseInfo.error;
        int i3 = radioResponseInfo.type;
        if (i3 != 0) {
            loge("processResponse: Unexpected response type " + i3);
        }
        RILRequest findAndRemoveRequestFromList = findAndRemoveRequestFromList(i);
        if (findAndRemoveRequestFromList == null) {
            loge("processResponse: Unexpected response! serial: " + i + " error: " + i2);
            return null;
        }
        return findAndRemoveRequestFromList;
    }

    public RILRequest processResponse(android.hardware.radio.V1_0.RadioResponseInfo radioResponseInfo) {
        int i = radioResponseInfo.serial;
        int i2 = radioResponseInfo.error;
        int i3 = radioResponseInfo.type;
        if (i3 != 0) {
            loge("processResponse: Unexpected response type " + i3);
        }
        RILRequest findAndRemoveRequestFromList = findAndRemoveRequestFromList(i);
        if (findAndRemoveRequestFromList == null) {
            loge("processResponse: Unexpected response! serial: " + i + " error: " + i2);
            return null;
        }
        return findAndRemoveRequestFromList;
    }

    public RILRequest processResponse_1_6(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo) {
        int i = radioResponseInfo.serial;
        int i2 = radioResponseInfo.error;
        int i3 = radioResponseInfo.type;
        if (i3 != 0) {
            loge("processResponse: Unexpected response type " + i3);
        }
        RILRequest findAndRemoveRequestFromList = findAndRemoveRequestFromList(i);
        if (findAndRemoveRequestFromList == null) {
            loge("processResponse: Unexpected response! serial: " + i + " error: " + i2);
            return null;
        }
        return findAndRemoveRequestFromList;
    }

    public void getSimSlotsStatus(Message message) {
        RadioConfigProxy radioConfigProxy = getRadioConfigProxy(message);
        if (radioConfigProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(ImsRttTextHandler.MAX_BUFFERING_DELAY_MILLIS, message, this.mDefaultWorkSource);
        logd(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioConfigProxy.getSimSlotStatus(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            resetProxyAndRequestList("getSimSlotsStatus", e);
        }
    }

    public void setPreferredDataModem(int i, Message message) {
        RadioConfigProxy radioConfigProxy = getRadioConfigProxy(null);
        if (radioConfigProxy.isEmpty()) {
            return;
        }
        if (!isSetPreferredDataCommandSupported()) {
            if (message != null) {
                AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
                message.sendToTarget();
                return;
            }
            return;
        }
        RILRequest obtainRequest = obtainRequest(204, message, this.mDefaultWorkSource);
        logd(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioConfigProxy.setPreferredDataModem(obtainRequest.mSerial, i);
        } catch (RemoteException | RuntimeException e) {
            resetProxyAndRequestList("setPreferredDataModem", e);
        }
    }

    public void getPhoneCapability(Message message) {
        RadioConfigProxy radioConfigProxy = getRadioConfigProxy(null);
        if (radioConfigProxy.isEmpty()) {
            return;
        }
        if (radioConfigProxy.getVersion().less(RADIO_CONFIG_HAL_VERSION_1_1)) {
            if (message != null) {
                AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
                message.sendToTarget();
                return;
            }
            return;
        }
        RILRequest obtainRequest = obtainRequest(206, message, this.mDefaultWorkSource);
        logd(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioConfigProxy.getPhoneCapability(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            resetProxyAndRequestList("getPhoneCapability", e);
        }
    }

    public boolean isSetPreferredDataCommandSupported() {
        RadioConfigProxy radioConfigProxy = getRadioConfigProxy(null);
        return !radioConfigProxy.isEmpty() && radioConfigProxy.getVersion().greaterOrEqual(RADIO_CONFIG_HAL_VERSION_1_1);
    }

    public void setSimSlotsMapping(List<UiccSlotMapping> list, Message message) {
        RadioConfigProxy radioConfigProxy = getRadioConfigProxy(message);
        if (radioConfigProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(IccRecords.EVENT_SET_SMSS_RECORD_DONE, message, this.mDefaultWorkSource);
        logd(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " " + list);
        try {
            radioConfigProxy.setSimSlotsMapping(obtainRequest.mSerial, list);
        } catch (RemoteException | RuntimeException e) {
            resetProxyAndRequestList("setSimSlotsMapping", e);
        }
    }

    public void setNumOfLiveModems(int i, Message message) {
        RadioConfigProxy radioConfigProxy = getRadioConfigProxy(message);
        if (radioConfigProxy.isEmpty()) {
            return;
        }
        if (radioConfigProxy.getVersion().less(RADIO_CONFIG_HAL_VERSION_1_1)) {
            if (message != null) {
                AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
                message.sendToTarget();
                return;
            }
            return;
        }
        RILRequest obtainRequest = obtainRequest(207, message, this.mDefaultWorkSource);
        logd(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + ", numOfLiveModems = " + i);
        try {
            radioConfigProxy.setNumOfLiveModems(obtainRequest.mSerial, i);
        } catch (RemoteException | RuntimeException e) {
            resetProxyAndRequestList("setNumOfLiveModems", e);
        }
    }

    public void registerForSimSlotStatusChanged(Handler handler, int i, Object obj) {
        this.mSimSlotStatusRegistrant = new Registrant(handler, i, obj);
    }

    public void unregisterForSimSlotStatusChanged(Handler handler) {
        Registrant registrant = this.mSimSlotStatusRegistrant;
        if (registrant == null || registrant.getHandler() != handler) {
            return;
        }
        this.mSimSlotStatusRegistrant.clear();
        this.mSimSlotStatusRegistrant = null;
    }

    public void getHalDeviceCapabilities(Message message) {
        RadioConfigProxy radioConfigProxy = getRadioConfigProxy(Message.obtain(message));
        if (radioConfigProxy.isEmpty()) {
            return;
        }
        if (radioConfigProxy.getVersion().less(RADIO_CONFIG_HAL_VERSION_1_3)) {
            if (message != null) {
                logd("RIL_REQUEST_GET_HAL_DEVICE_CAPABILITIES > REQUEST_NOT_SUPPORTED");
                AsyncResult.forMessage(message, radioConfigProxy.getFullCapabilitySet(), CommandException.fromRilErrno(6));
                message.sendToTarget();
                return;
            }
            logd("RIL_REQUEST_GET_HAL_DEVICE_CAPABILITIES > REQUEST_NOT_SUPPORTED on complete message not set.");
            return;
        }
        RILRequest obtainRequest = obtainRequest(220, message, this.mDefaultWorkSource);
        logd(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioConfigProxy.getHalDeviceCapabilities(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            resetProxyAndRequestList("getHalDeviceCapabilities", e);
        }
    }

    public int[] getDeviceNrCapabilities() {
        return this.mDeviceNrCapabilities;
    }

    private static void logd(String str) {
        Rlog.d("RadioConfig", str);
    }

    private static void loge(String str) {
        Rlog.e("RadioConfig", str);
    }

    @Override // android.os.Handler
    public String toString() {
        return "RadioConfig[mRadioConfigProxy=" + this.mRadioConfigProxy + ']';
    }
}
