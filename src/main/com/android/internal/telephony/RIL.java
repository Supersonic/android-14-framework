package com.android.internal.telephony;

import android.compat.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.hardware.radio.V1_0.IRadio;
import android.hardware.radio.V1_0.RadioResponseInfo;
import android.hardware.radio.data.IRadioData;
import android.hardware.radio.ims.IRadioIms;
import android.hardware.radio.ims.ImsRegistration;
import android.hardware.radio.messaging.IRadioMessaging;
import android.hardware.radio.modem.IRadioModem;
import android.hardware.radio.network.IRadioNetwork;
import android.hardware.radio.satellite.IRadioSatellite;
import android.hardware.radio.sim.IRadioSim;
import android.hardware.radio.voice.IRadioVoice;
import android.internal.telephony.sysprop.TelephonyProperties;
import android.net.KeepalivePacketData;
import android.net.LinkProperties;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.IBinder;
import android.os.IHwBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.WorkSource;
import android.provider.Settings;
import android.telephony.AccessNetworkConstants;
import android.telephony.BarringInfo;
import android.telephony.CarrierRestrictionRules;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthNr;
import android.telephony.CellSignalStrengthTdscdma;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.ClientRequestStats;
import android.telephony.ImsiEncryptionInfo;
import android.telephony.ModemActivityInfo;
import android.telephony.NeighboringCellInfo;
import android.telephony.NetworkScanRequest;
import android.telephony.RadioAccessFamily;
import android.telephony.RadioAccessSpecifier;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.SignalThresholdInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyHistogram;
import android.telephony.TelephonyManager;
import android.telephony.data.DataProfile;
import android.telephony.data.NetworkSliceInfo;
import android.telephony.data.TrafficDescriptor;
import android.telephony.emergency.EmergencyNumber;
import android.telephony.gsm.SmsMessage;
import android.telephony.ims.feature.ConnectionFailureInfo;
import android.text.TextUtils;
import android.util.SparseArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.SMSDispatcher;
import com.android.internal.telephony.cat.BerTlv;
import com.android.internal.telephony.cdma.CdmaInformationRecords;
import com.android.internal.telephony.cdma.CdmaSmsBroadcastConfigInfo;
import com.android.internal.telephony.data.KeepaliveStatus;
import com.android.internal.telephony.emergency.EmergencyConstants;
import com.android.internal.telephony.gsm.SmsBroadcastConfigInfo;
import com.android.internal.telephony.imsphone.ImsCallInfo;
import com.android.internal.telephony.imsphone.ImsRttTextHandler;
import com.android.internal.telephony.metrics.ModemRestartStats;
import com.android.internal.telephony.metrics.TelephonyMetrics;
import com.android.internal.telephony.nano.TelephonyProto$TelephonyEvent;
import com.android.internal.telephony.uicc.IccCardApplicationStatus;
import com.android.internal.telephony.uicc.IccUtils;
import com.android.internal.telephony.uicc.SimPhonebookRecord;
import com.android.internal.telephony.uicc.UiccCardApplication;
import com.android.internal.telephony.util.NetworkStackConstants;
import com.android.internal.telephony.util.TelephonyUtils;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
/* loaded from: classes.dex */
public class RIL extends BaseCommands {
    public static final int FOR_ACK_WAKELOCK = 1;
    public static final int FOR_WAKELOCK = 0;
    public static final int INVALID_WAKELOCK = -1;
    public static final int MAX_SERVICE_IDX = 8;
    public static final int MIN_SERVICE_IDX = 0;
    @VisibleForTesting
    public final PowerManager.WakeLock mAckWakeLock;
    final int mAckWakeLockTimeout;
    volatile int mAckWlSequenceNum;
    private WorkSource mActiveWakelockWorkSource;
    private final ClientWakelockTracker mClientWakelockTracker;
    private final ConcurrentHashMap<Integer, HalVersion> mCompatOverrides;
    private DataIndication mDataIndication;
    private DataResponse mDataResponse;
    private final SparseArray<BinderServiceDeathRecipient> mDeathRecipients;
    private final SparseArray<Set<Integer>> mDisabledRadioServices;
    private Map<Integer, HalVersion> mHalVersion;
    private ImsIndication mImsIndication;
    private ImsResponse mImsResponse;
    private boolean mIsCellularSupported;
    boolean mIsRadioProxyInitialized;
    Object[] mLastNITZTimeInfo;
    int mLastRadioPowerResult;
    private MessagingIndication mMessagingIndication;
    private MessagingResponse mMessagingResponse;
    private TelephonyMetrics mMetrics;
    private MockModem mMockModem;
    private ModemIndication mModemIndication;
    private ModemResponse mModemResponse;
    private NetworkIndication mNetworkIndication;
    private NetworkResponse mNetworkResponse;
    final Integer mPhoneId;
    private WorkSource mRILDefaultWorkSource;
    private RadioBugDetector mRadioBugDetector;
    private RadioIndication mRadioIndication;
    private volatile IRadio mRadioProxy;
    private final RadioProxyDeathRecipient mRadioProxyDeathRecipient;
    private RadioResponse mRadioResponse;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    SparseArray<RILRequest> mRequestList;
    final RilHandler mRilHandler;
    private SatelliteIndication mSatelliteIndication;
    private SatelliteResponse mSatelliteResponse;
    private final SparseArray<AtomicLong> mServiceCookies;
    private SparseArray<RadioServiceProxy> mServiceProxies;
    private SimIndication mSimIndication;
    private SimResponse mSimResponse;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    AtomicBoolean mTestingEmergencyCall;
    private VoiceIndication mVoiceIndication;
    private VoiceResponse mVoiceResponse;
    @UnsupportedAppUsage
    @VisibleForTesting
    public final PowerManager.WakeLock mWakeLock;
    int mWakeLockCount;
    final int mWakeLockTimeout;
    volatile int mWlSequenceNum;
    public static final HalVersion RADIO_HAL_VERSION_UNSUPPORTED = HalVersion.UNSUPPORTED;
    public static final HalVersion RADIO_HAL_VERSION_UNKNOWN = HalVersion.UNKNOWN;
    public static final HalVersion RADIO_HAL_VERSION_1_0 = new HalVersion(1, 0);
    public static final HalVersion RADIO_HAL_VERSION_1_1 = new HalVersion(1, 1);
    public static final HalVersion RADIO_HAL_VERSION_1_2 = new HalVersion(1, 2);
    public static final HalVersion RADIO_HAL_VERSION_1_3 = new HalVersion(1, 3);
    public static final HalVersion RADIO_HAL_VERSION_1_4 = new HalVersion(1, 4);
    public static final HalVersion RADIO_HAL_VERSION_1_5 = new HalVersion(1, 5);
    public static final HalVersion RADIO_HAL_VERSION_1_6 = new HalVersion(1, 6);
    public static final HalVersion RADIO_HAL_VERSION_2_0 = new HalVersion(2, 0);
    public static final HalVersion RADIO_HAL_VERSION_2_1 = new HalVersion(2, 1);
    static SparseArray<TelephonyHistogram> sRilTimeHistograms = new SparseArray<>();
    static final String[] HIDL_SERVICE_NAME = {"slot1", "slot2", "slot3"};

    @Override // com.android.internal.telephony.CommandsInterface
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void invokeOemRilRequestRaw(byte[] bArr, Message message) {
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void invokeOemRilRequestStrings(String[] strArr, Message message) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isLogOrTrace() {
        return true;
    }

    public static List<TelephonyHistogram> getTelephonyRILTimingHistograms() {
        ArrayList arrayList;
        synchronized (sRilTimeHistograms) {
            arrayList = new ArrayList(sRilTimeHistograms.size());
            for (int i = 0; i < sRilTimeHistograms.size(); i++) {
                arrayList.add(new TelephonyHistogram(sRilTimeHistograms.valueAt(i)));
            }
        }
        return arrayList;
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public class RilHandler extends Handler {
        public RilHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 2) {
                synchronized (RIL.this.mRequestList) {
                    if (message.arg1 == RIL.this.mWlSequenceNum && RIL.this.clearWakeLock(0)) {
                        if (RIL.this.mRadioBugDetector != null) {
                            RIL.this.mRadioBugDetector.processWakelockTimeout();
                        }
                        int size = RIL.this.mRequestList.size();
                        Rlog.d("RILJ", "WAKE_LOCK_TIMEOUT  mRequestList=" + size);
                        for (int i2 = 0; i2 < size; i2++) {
                            RILRequest valueAt = RIL.this.mRequestList.valueAt(i2);
                            Rlog.d("RILJ", i2 + ": [" + valueAt.mSerial + "] " + RILUtils.requestToString(valueAt.mRequest));
                        }
                    }
                }
            } else if (i == 4) {
                if (message.arg1 == RIL.this.mAckWlSequenceNum) {
                    RIL.this.clearWakeLock(1);
                }
            } else if (i == 5) {
                RILRequest findAndRemoveRequestFromList = RIL.this.findAndRemoveRequestFromList(((Integer) message.obj).intValue());
                if (findAndRemoveRequestFromList == null) {
                    return;
                }
                if (findAndRemoveRequestFromList.mResult != null) {
                    AsyncResult.forMessage(findAndRemoveRequestFromList.mResult, RIL.getResponseForTimedOutRILRequest(findAndRemoveRequestFromList), (Throwable) null);
                    findAndRemoveRequestFromList.mResult.sendToTarget();
                    RIL.this.mMetrics.writeOnRilTimeoutResponse(RIL.this.mPhoneId.intValue(), findAndRemoveRequestFromList.mSerial, findAndRemoveRequestFromList.mRequest);
                }
                RIL.this.decrementWakeLock(findAndRemoveRequestFromList);
                findAndRemoveRequestFromList.release();
            } else if (i == 6) {
                int i3 = message.arg1;
                RIL ril = RIL.this;
                ril.riljLog("handleMessage: EVENT_RADIO_PROXY_DEAD cookie = " + message.obj + ", service = " + RIL.serviceToString(i3) + ", service cookie = " + RIL.this.mServiceCookies.get(i3));
                if (((Long) message.obj).longValue() == ((AtomicLong) RIL.this.mServiceCookies.get(i3)).get()) {
                    RIL ril2 = RIL.this;
                    ril2.mIsRadioProxyInitialized = false;
                    ril2.resetProxyAndRequestList(i3);
                }
            } else if (i == 7) {
                int i4 = message.arg1;
                long longValue = ((Long) message.obj).longValue();
                RIL ril3 = RIL.this;
                ril3.riljLog("handleMessage: EVENT_AIDL_PROXY_DEAD cookie = " + longValue + ", service = " + RIL.serviceToString(i4) + ", cookie = " + RIL.this.mServiceCookies.get(i4));
                if (longValue == ((AtomicLong) RIL.this.mServiceCookies.get(i4)).get()) {
                    RIL ril4 = RIL.this;
                    ril4.mIsRadioProxyInitialized = false;
                    ril4.resetProxyAndRequestList(i4);
                }
            }
        }
    }

    @VisibleForTesting
    public RadioBugDetector getRadioBugDetector() {
        if (this.mRadioBugDetector == null) {
            this.mRadioBugDetector = new RadioBugDetector(this.mContext, this.mPhoneId.intValue());
        }
        return this.mRadioBugDetector;
    }

    /* JADX INFO: Access modifiers changed from: private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public static Object getResponseForTimedOutRILRequest(RILRequest rILRequest) {
        if (rILRequest != null && rILRequest.mRequest == 135) {
            return new ModemActivityInfo(0L, 0, 0, new int[ModemActivityInfo.getNumTxPowerLevels()], 0);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public final class RadioProxyDeathRecipient implements IHwBinder.DeathRecipient {
        RadioProxyDeathRecipient() {
        }

        public void serviceDied(long j) {
            RIL.this.riljLog("serviceDied");
            RilHandler rilHandler = RIL.this.mRilHandler;
            rilHandler.sendMessage(rilHandler.obtainMessage(6, 0, 0, Long.valueOf(j)));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class BinderServiceDeathRecipient implements IBinder.DeathRecipient {
        private IBinder mBinder;
        private final int mService;

        BinderServiceDeathRecipient(int i) {
            this.mService = i;
        }

        public void linkToDeath(IBinder iBinder) throws RemoteException {
            if (iBinder != null) {
                RIL ril = RIL.this;
                ril.riljLog("Linked to death for service " + RIL.serviceToString(this.mService));
                this.mBinder = iBinder;
                iBinder.linkToDeath(this, (int) ((AtomicLong) RIL.this.mServiceCookies.get(this.mService)).incrementAndGet());
                return;
            }
            RIL ril2 = RIL.this;
            ril2.riljLoge("Unable to link to death for service " + RIL.serviceToString(this.mService));
        }

        public synchronized void unlinkToDeath() {
            IBinder iBinder = this.mBinder;
            if (iBinder != null) {
                iBinder.unlinkToDeath(this, 0);
                this.mBinder = null;
            }
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            RIL ril = RIL.this;
            ril.riljLog("Service " + RIL.serviceToString(this.mService) + " has died.");
            RIL ril2 = RIL.this;
            RilHandler rilHandler = ril2.mRilHandler;
            rilHandler.sendMessage(rilHandler.obtainMessage(7, this.mService, 0, Long.valueOf(((AtomicLong) ril2.mServiceCookies.get(this.mService)).get())));
            unlinkToDeath();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void resetProxyAndRequestList(int i) {
        if (i == 0) {
            this.mRadioProxy = null;
        } else {
            this.mServiceProxies.get(i).clear();
        }
        this.mServiceCookies.get(i).incrementAndGet();
        setRadioState(2, true);
        RILRequest.resetSerial();
        clearRequestList(1, false);
        if (i == 0) {
            getRadioProxy(null);
        } else {
            getRadioServiceProxy(i, (Message) null);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public boolean setModemService(String str) {
        IBinder serviceBinder;
        boolean z = true;
        if (str != null) {
            riljLog("Binding to MockModemService");
            this.mMockModem = null;
            this.mMockModem = new MockModem(this.mContext, str, this.mPhoneId.intValue());
            if (this.mRadioProxy != null) {
                riljLog("Disable HIDL service");
                this.mDisabledRadioServices.get(0).add(this.mPhoneId);
            }
            this.mMockModem.bindAllMockModemService();
            int i = 0;
            while (true) {
                if (i > 8) {
                    break;
                }
                if (i != 0) {
                    int i2 = 0;
                    do {
                        serviceBinder = this.mMockModem.getServiceBinder(i);
                        i2++;
                        if (serviceBinder == null) {
                            riljLog("Retry(" + i2 + ") Service " + serviceToString(i));
                            try {
                                Thread.sleep(300L);
                            } catch (InterruptedException unused) {
                            }
                        }
                        if (serviceBinder != null) {
                            break;
                        }
                    } while (i2 < 10);
                    if (serviceBinder == null) {
                        riljLoge("Service " + serviceToString(i) + " bind fail");
                        z = false;
                        break;
                    }
                }
                i++;
            }
            if (z) {
                this.mIsRadioProxyInitialized = false;
                for (int i3 = 0; i3 <= 8; i3++) {
                    resetProxyAndRequestList(i3);
                }
            }
        }
        if (str == null || !z) {
            if (z) {
                riljLog("Unbinding to MockModemService");
            }
            if (this.mDisabledRadioServices.get(0).contains(this.mPhoneId)) {
                this.mDisabledRadioServices.get(0).clear();
            }
            if (this.mMockModem != null) {
                this.mMockModem = null;
                for (int i4 = 0; i4 <= 8; i4++) {
                    if (i4 == 0) {
                        if (isRadioVersion2_0()) {
                            this.mHalVersion.put(Integer.valueOf(i4), RADIO_HAL_VERSION_2_0);
                        } else {
                            this.mHalVersion.put(Integer.valueOf(i4), RADIO_HAL_VERSION_UNKNOWN);
                        }
                    } else if (isRadioServiceSupported(i4)) {
                        this.mHalVersion.put(Integer.valueOf(i4), RADIO_HAL_VERSION_UNKNOWN);
                    } else {
                        this.mHalVersion.put(Integer.valueOf(i4), RADIO_HAL_VERSION_UNSUPPORTED);
                    }
                    resetProxyAndRequestList(i4);
                }
            }
        }
        return z;
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public String getModemService() {
        MockModem mockModem = this.mMockModem;
        return mockModem != null ? mockModem.getServiceName() : "default";
    }

    @VisibleForTesting
    public void setCompatVersion(int i, HalVersion halVersion) {
        HalVersion compatVersion = getCompatVersion(i);
        if (compatVersion != null && halVersion.greaterOrEqual(compatVersion)) {
            riljLoge("setCompatVersion with equal or greater one, ignored, halVerion=" + halVersion + ", oldVerion=" + compatVersion);
            return;
        }
        this.mCompatOverrides.put(Integer.valueOf(i), halVersion);
    }

    @VisibleForTesting
    public HalVersion getCompatVersion(int i) {
        return this.mCompatOverrides.getOrDefault(Integer.valueOf(i), null);
    }

    @VisibleForTesting
    public synchronized IRadio getRadioProxy(Message message) {
        if (this.mHalVersion.containsKey(0) && this.mHalVersion.get(0).greaterOrEqual(RADIO_HAL_VERSION_2_0)) {
            return null;
        }
        if (SubscriptionManager.isValidPhoneId(this.mPhoneId.intValue())) {
            if (!this.mIsCellularSupported) {
                if (message != null) {
                    AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(1));
                    message.sendToTarget();
                }
                return null;
            } else if (this.mRadioProxy != null) {
                return this.mRadioProxy;
            } else {
                try {
                    if (this.mDisabledRadioServices.get(0).contains(this.mPhoneId)) {
                        riljLoge("getRadioProxy: mRadioProxy for " + HIDL_SERVICE_NAME[this.mPhoneId.intValue()] + " is disabled");
                    } else {
                        try {
                            this.mRadioProxy = android.hardware.radio.V1_6.IRadio.getService(HIDL_SERVICE_NAME[this.mPhoneId.intValue()], true);
                            this.mHalVersion.put(0, RADIO_HAL_VERSION_1_6);
                        } catch (NoSuchElementException unused) {
                        }
                        if (this.mRadioProxy == null) {
                            try {
                                this.mRadioProxy = android.hardware.radio.V1_5.IRadio.getService(HIDL_SERVICE_NAME[this.mPhoneId.intValue()], true);
                                this.mHalVersion.put(0, RADIO_HAL_VERSION_1_5);
                            } catch (NoSuchElementException unused2) {
                            }
                        }
                        if (this.mRadioProxy == null) {
                            try {
                                this.mRadioProxy = android.hardware.radio.V1_4.IRadio.getService(HIDL_SERVICE_NAME[this.mPhoneId.intValue()], true);
                                this.mHalVersion.put(0, RADIO_HAL_VERSION_1_4);
                            } catch (NoSuchElementException unused3) {
                            }
                        }
                        if (this.mRadioProxy == null) {
                            try {
                                this.mRadioProxy = android.hardware.radio.V1_3.IRadio.getService(HIDL_SERVICE_NAME[this.mPhoneId.intValue()], true);
                                this.mHalVersion.put(0, RADIO_HAL_VERSION_1_3);
                            } catch (NoSuchElementException unused4) {
                            }
                        }
                        if (this.mRadioProxy == null) {
                            try {
                                this.mRadioProxy = android.hardware.radio.V1_2.IRadio.getService(HIDL_SERVICE_NAME[this.mPhoneId.intValue()], true);
                                this.mHalVersion.put(0, RADIO_HAL_VERSION_1_2);
                            } catch (NoSuchElementException unused5) {
                            }
                        }
                        if (this.mRadioProxy == null) {
                            try {
                                this.mRadioProxy = android.hardware.radio.V1_1.IRadio.getService(HIDL_SERVICE_NAME[this.mPhoneId.intValue()], true);
                                this.mHalVersion.put(0, RADIO_HAL_VERSION_1_1);
                            } catch (NoSuchElementException unused6) {
                            }
                        }
                        if (this.mRadioProxy == null) {
                            try {
                                this.mRadioProxy = IRadio.getService(HIDL_SERVICE_NAME[this.mPhoneId.intValue()], true);
                                this.mHalVersion.put(0, RADIO_HAL_VERSION_1_0);
                            } catch (NoSuchElementException unused7) {
                            }
                        }
                        if (this.mRadioProxy != null) {
                            if (!this.mIsRadioProxyInitialized) {
                                this.mIsRadioProxyInitialized = true;
                                this.mRadioProxy.linkToDeath(this.mRadioProxyDeathRecipient, this.mServiceCookies.get(0).incrementAndGet());
                                this.mRadioProxy.setResponseFunctions(this.mRadioResponse, this.mRadioIndication);
                            }
                        } else {
                            this.mDisabledRadioServices.get(0).add(this.mPhoneId);
                            riljLoge("getRadioProxy: set mRadioProxy for " + HIDL_SERVICE_NAME[this.mPhoneId.intValue()] + " as disabled");
                        }
                    }
                } catch (RemoteException e) {
                    this.mRadioProxy = null;
                    riljLoge("RadioProxy getService/setResponseFunctions: " + e);
                }
                if (this.mRadioProxy == null) {
                    riljLoge("getRadioProxy: mRadioProxy == null");
                    if (message != null) {
                        AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(1));
                        message.sendToTarget();
                    }
                }
                return this.mRadioProxy;
            }
        }
        return null;
    }

    public <T extends RadioServiceProxy> T getRadioServiceProxy(Class<T> cls, Message message) {
        if (cls == RadioDataProxy.class) {
            return (T) getRadioServiceProxy(1, message);
        }
        if (cls == RadioMessagingProxy.class) {
            return (T) getRadioServiceProxy(2, message);
        }
        if (cls == RadioModemProxy.class) {
            return (T) getRadioServiceProxy(3, message);
        }
        if (cls == RadioNetworkProxy.class) {
            return (T) getRadioServiceProxy(4, message);
        }
        if (cls == RadioSimProxy.class) {
            return (T) getRadioServiceProxy(5, message);
        }
        if (cls == RadioVoiceProxy.class) {
            return (T) getRadioServiceProxy(6, message);
        }
        if (cls == RadioImsProxy.class) {
            return (T) getRadioServiceProxy(7, message);
        }
        if (cls == RadioSatelliteProxy.class) {
            return (T) getRadioServiceProxy(8, message);
        }
        riljLoge("getRadioServiceProxy: unrecognized " + cls);
        return null;
    }

    @VisibleForTesting
    public synchronized RadioServiceProxy getRadioServiceProxy(int i, Message message) {
        IBinder serviceBinder;
        IBinder serviceBinder2;
        IBinder serviceBinder3;
        IBinder serviceBinder4;
        IBinder serviceBinder5;
        IBinder serviceBinder6;
        IBinder serviceBinder7;
        IBinder serviceBinder8;
        if (!SubscriptionManager.isValidPhoneId(this.mPhoneId.intValue())) {
            return this.mServiceProxies.get(i);
        } else if (i >= 7 && !isRadioServiceSupported(i)) {
            return this.mServiceProxies.get(i);
        } else if (!this.mIsCellularSupported) {
            if (message != null) {
                AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(1));
                message.sendToTarget();
            }
            return this.mServiceProxies.get(i);
        } else {
            RadioServiceProxy radioServiceProxy = this.mServiceProxies.get(i);
            if (radioServiceProxy.isEmpty()) {
                try {
                    if (this.mMockModem == null && this.mDisabledRadioServices.get(i).contains(this.mPhoneId)) {
                        riljLoge("getRadioServiceProxy: " + serviceToString(i) + " for " + HIDL_SERVICE_NAME[this.mPhoneId.intValue()] + " is disabled");
                    } else {
                        switch (i) {
                            case 1:
                                MockModem mockModem = this.mMockModem;
                                if (mockModem == null) {
                                    serviceBinder = ServiceManager.waitForDeclaredService(IRadioData.DESCRIPTOR + "/" + HIDL_SERVICE_NAME[this.mPhoneId.intValue()]);
                                } else {
                                    serviceBinder = mockModem.getServiceBinder(1);
                                }
                                if (serviceBinder != null) {
                                    this.mHalVersion.put(Integer.valueOf(i), ((RadioDataProxy) radioServiceProxy).setAidl(this.mHalVersion.get(Integer.valueOf(i)), IRadioData.Stub.asInterface(serviceBinder)));
                                    break;
                                }
                                break;
                            case 2:
                                MockModem mockModem2 = this.mMockModem;
                                if (mockModem2 == null) {
                                    serviceBinder2 = ServiceManager.waitForDeclaredService(IRadioMessaging.DESCRIPTOR + "/" + HIDL_SERVICE_NAME[this.mPhoneId.intValue()]);
                                } else {
                                    serviceBinder2 = mockModem2.getServiceBinder(2);
                                }
                                if (serviceBinder2 != null) {
                                    this.mHalVersion.put(Integer.valueOf(i), ((RadioMessagingProxy) radioServiceProxy).setAidl(this.mHalVersion.get(Integer.valueOf(i)), IRadioMessaging.Stub.asInterface(serviceBinder2)));
                                    break;
                                }
                                break;
                            case 3:
                                MockModem mockModem3 = this.mMockModem;
                                if (mockModem3 == null) {
                                    serviceBinder3 = ServiceManager.waitForDeclaredService(IRadioModem.DESCRIPTOR + "/" + HIDL_SERVICE_NAME[this.mPhoneId.intValue()]);
                                } else {
                                    serviceBinder3 = mockModem3.getServiceBinder(3);
                                }
                                if (serviceBinder3 != null) {
                                    this.mHalVersion.put(Integer.valueOf(i), ((RadioModemProxy) radioServiceProxy).setAidl(this.mHalVersion.get(Integer.valueOf(i)), IRadioModem.Stub.asInterface(serviceBinder3)));
                                    break;
                                }
                                break;
                            case 4:
                                MockModem mockModem4 = this.mMockModem;
                                if (mockModem4 == null) {
                                    serviceBinder4 = ServiceManager.waitForDeclaredService(IRadioNetwork.DESCRIPTOR + "/" + HIDL_SERVICE_NAME[this.mPhoneId.intValue()]);
                                } else {
                                    serviceBinder4 = mockModem4.getServiceBinder(4);
                                }
                                if (serviceBinder4 != null) {
                                    this.mHalVersion.put(Integer.valueOf(i), ((RadioNetworkProxy) radioServiceProxy).setAidl(this.mHalVersion.get(Integer.valueOf(i)), IRadioNetwork.Stub.asInterface(serviceBinder4)));
                                    break;
                                }
                                break;
                            case 5:
                                MockModem mockModem5 = this.mMockModem;
                                if (mockModem5 == null) {
                                    serviceBinder5 = ServiceManager.waitForDeclaredService(IRadioSim.DESCRIPTOR + "/" + HIDL_SERVICE_NAME[this.mPhoneId.intValue()]);
                                } else {
                                    serviceBinder5 = mockModem5.getServiceBinder(5);
                                }
                                if (serviceBinder5 != null) {
                                    this.mHalVersion.put(Integer.valueOf(i), ((RadioSimProxy) radioServiceProxy).setAidl(this.mHalVersion.get(Integer.valueOf(i)), IRadioSim.Stub.asInterface(serviceBinder5)));
                                    break;
                                }
                                break;
                            case 6:
                                MockModem mockModem6 = this.mMockModem;
                                if (mockModem6 == null) {
                                    serviceBinder6 = ServiceManager.waitForDeclaredService(IRadioVoice.DESCRIPTOR + "/" + HIDL_SERVICE_NAME[this.mPhoneId.intValue()]);
                                } else {
                                    serviceBinder6 = mockModem6.getServiceBinder(6);
                                }
                                if (serviceBinder6 != null) {
                                    this.mHalVersion.put(Integer.valueOf(i), ((RadioVoiceProxy) radioServiceProxy).setAidl(this.mHalVersion.get(Integer.valueOf(i)), IRadioVoice.Stub.asInterface(serviceBinder6)));
                                    break;
                                }
                                break;
                            case 7:
                                MockModem mockModem7 = this.mMockModem;
                                if (mockModem7 == null) {
                                    serviceBinder7 = ServiceManager.waitForDeclaredService(IRadioIms.DESCRIPTOR + "/" + HIDL_SERVICE_NAME[this.mPhoneId.intValue()]);
                                } else {
                                    serviceBinder7 = mockModem7.getServiceBinder(7);
                                }
                                if (serviceBinder7 != null) {
                                    this.mHalVersion.put(Integer.valueOf(i), ((RadioImsProxy) radioServiceProxy).setAidl(this.mHalVersion.get(Integer.valueOf(i)), IRadioIms.Stub.asInterface(serviceBinder7)));
                                    break;
                                }
                                break;
                            case 8:
                                MockModem mockModem8 = this.mMockModem;
                                if (mockModem8 == null) {
                                    serviceBinder8 = ServiceManager.waitForDeclaredService(IRadioSatellite.DESCRIPTOR + "/" + HIDL_SERVICE_NAME[this.mPhoneId.intValue()]);
                                } else {
                                    serviceBinder8 = mockModem8.getServiceBinder(8);
                                }
                                if (serviceBinder8 != null) {
                                    this.mHalVersion.put(Integer.valueOf(i), ((RadioSatelliteProxy) radioServiceProxy).setAidl(this.mHalVersion.get(Integer.valueOf(i)), IRadioSatellite.Stub.asInterface(serviceBinder8)));
                                    break;
                                }
                                break;
                        }
                        if (radioServiceProxy.isEmpty() && this.mHalVersion.get(Integer.valueOf(i)).less(RADIO_HAL_VERSION_2_0)) {
                            try {
                                this.mHalVersion.put(Integer.valueOf(i), RADIO_HAL_VERSION_1_6);
                                radioServiceProxy.setHidl(this.mHalVersion.get(Integer.valueOf(i)), android.hardware.radio.V1_6.IRadio.getService(HIDL_SERVICE_NAME[this.mPhoneId.intValue()], true));
                            } catch (NoSuchElementException unused) {
                            }
                        }
                        if (radioServiceProxy.isEmpty() && this.mHalVersion.get(Integer.valueOf(i)).less(RADIO_HAL_VERSION_2_0)) {
                            try {
                                this.mHalVersion.put(Integer.valueOf(i), RADIO_HAL_VERSION_1_5);
                                radioServiceProxy.setHidl(this.mHalVersion.get(Integer.valueOf(i)), android.hardware.radio.V1_5.IRadio.getService(HIDL_SERVICE_NAME[this.mPhoneId.intValue()], true));
                            } catch (NoSuchElementException unused2) {
                            }
                        }
                        if (radioServiceProxy.isEmpty() && this.mHalVersion.get(Integer.valueOf(i)).less(RADIO_HAL_VERSION_2_0)) {
                            try {
                                this.mHalVersion.put(Integer.valueOf(i), RADIO_HAL_VERSION_1_4);
                                radioServiceProxy.setHidl(this.mHalVersion.get(Integer.valueOf(i)), android.hardware.radio.V1_4.IRadio.getService(HIDL_SERVICE_NAME[this.mPhoneId.intValue()], true));
                            } catch (NoSuchElementException unused3) {
                            }
                        }
                        if (radioServiceProxy.isEmpty() && this.mHalVersion.get(Integer.valueOf(i)).less(RADIO_HAL_VERSION_2_0)) {
                            try {
                                this.mHalVersion.put(Integer.valueOf(i), RADIO_HAL_VERSION_1_3);
                                radioServiceProxy.setHidl(this.mHalVersion.get(Integer.valueOf(i)), android.hardware.radio.V1_3.IRadio.getService(HIDL_SERVICE_NAME[this.mPhoneId.intValue()], true));
                            } catch (NoSuchElementException unused4) {
                            }
                        }
                        if (radioServiceProxy.isEmpty() && this.mHalVersion.get(Integer.valueOf(i)).less(RADIO_HAL_VERSION_2_0)) {
                            try {
                                this.mHalVersion.put(Integer.valueOf(i), RADIO_HAL_VERSION_1_2);
                                radioServiceProxy.setHidl(this.mHalVersion.get(Integer.valueOf(i)), android.hardware.radio.V1_2.IRadio.getService(HIDL_SERVICE_NAME[this.mPhoneId.intValue()], true));
                            } catch (NoSuchElementException unused5) {
                            }
                        }
                        if (radioServiceProxy.isEmpty() && this.mHalVersion.get(Integer.valueOf(i)).less(RADIO_HAL_VERSION_2_0)) {
                            try {
                                this.mHalVersion.put(Integer.valueOf(i), RADIO_HAL_VERSION_1_1);
                                radioServiceProxy.setHidl(this.mHalVersion.get(Integer.valueOf(i)), android.hardware.radio.V1_1.IRadio.getService(HIDL_SERVICE_NAME[this.mPhoneId.intValue()], true));
                            } catch (NoSuchElementException unused6) {
                            }
                        }
                        if (radioServiceProxy.isEmpty() && this.mHalVersion.get(Integer.valueOf(i)).less(RADIO_HAL_VERSION_2_0)) {
                            try {
                                this.mHalVersion.put(Integer.valueOf(i), RADIO_HAL_VERSION_1_0);
                                radioServiceProxy.setHidl(this.mHalVersion.get(Integer.valueOf(i)), IRadio.getService(HIDL_SERVICE_NAME[this.mPhoneId.intValue()], true));
                            } catch (NoSuchElementException unused7) {
                            }
                        }
                        if (!radioServiceProxy.isEmpty()) {
                            if (radioServiceProxy.isAidl()) {
                                switch (i) {
                                    case 1:
                                        this.mDeathRecipients.get(i).linkToDeath(((RadioDataProxy) radioServiceProxy).getAidl().asBinder());
                                        ((RadioDataProxy) radioServiceProxy).getAidl().setResponseFunctions(this.mDataResponse, this.mDataIndication);
                                        break;
                                    case 2:
                                        this.mDeathRecipients.get(i).linkToDeath(((RadioMessagingProxy) radioServiceProxy).getAidl().asBinder());
                                        ((RadioMessagingProxy) radioServiceProxy).getAidl().setResponseFunctions(this.mMessagingResponse, this.mMessagingIndication);
                                        break;
                                    case 3:
                                        this.mDeathRecipients.get(i).linkToDeath(((RadioModemProxy) radioServiceProxy).getAidl().asBinder());
                                        ((RadioModemProxy) radioServiceProxy).getAidl().setResponseFunctions(this.mModemResponse, this.mModemIndication);
                                        break;
                                    case 4:
                                        this.mDeathRecipients.get(i).linkToDeath(((RadioNetworkProxy) radioServiceProxy).getAidl().asBinder());
                                        ((RadioNetworkProxy) radioServiceProxy).getAidl().setResponseFunctions(this.mNetworkResponse, this.mNetworkIndication);
                                        break;
                                    case 5:
                                        this.mDeathRecipients.get(i).linkToDeath(((RadioSimProxy) radioServiceProxy).getAidl().asBinder());
                                        ((RadioSimProxy) radioServiceProxy).getAidl().setResponseFunctions(this.mSimResponse, this.mSimIndication);
                                        break;
                                    case 6:
                                        this.mDeathRecipients.get(i).linkToDeath(((RadioVoiceProxy) radioServiceProxy).getAidl().asBinder());
                                        ((RadioVoiceProxy) radioServiceProxy).getAidl().setResponseFunctions(this.mVoiceResponse, this.mVoiceIndication);
                                        break;
                                    case 7:
                                        this.mDeathRecipients.get(i).linkToDeath(((RadioImsProxy) radioServiceProxy).getAidl().asBinder());
                                        ((RadioImsProxy) radioServiceProxy).getAidl().setResponseFunctions(this.mImsResponse, this.mImsIndication);
                                        break;
                                    case 8:
                                        this.mDeathRecipients.get(i).linkToDeath(((RadioSatelliteProxy) radioServiceProxy).getAidl().asBinder());
                                        ((RadioSatelliteProxy) radioServiceProxy).getAidl().setResponseFunctions(this.mSatelliteResponse, this.mSatelliteIndication);
                                        break;
                                }
                            } else if (this.mHalVersion.get(Integer.valueOf(i)).greaterOrEqual(RADIO_HAL_VERSION_2_0)) {
                                throw new AssertionError("serviceProxy shouldn't be HIDL with HAL 2.0");
                            } else {
                                if (!this.mIsRadioProxyInitialized) {
                                    this.mIsRadioProxyInitialized = true;
                                    radioServiceProxy.getHidl().linkToDeath(this.mRadioProxyDeathRecipient, this.mServiceCookies.get(0).incrementAndGet());
                                    radioServiceProxy.getHidl().setResponseFunctions(this.mRadioResponse, this.mRadioIndication);
                                }
                            }
                        } else {
                            this.mDisabledRadioServices.get(i).add(this.mPhoneId);
                            this.mHalVersion.put(Integer.valueOf(i), RADIO_HAL_VERSION_UNKNOWN);
                            riljLoge("getRadioServiceProxy: set " + serviceToString(i) + " for " + HIDL_SERVICE_NAME[this.mPhoneId.intValue()] + " as disabled");
                        }
                    }
                } catch (RemoteException e) {
                    radioServiceProxy.clear();
                    riljLoge("ServiceProxy getService/setResponseFunctions: " + e);
                }
                if (radioServiceProxy.isEmpty()) {
                    riljLoge("getRadioServiceProxy: serviceProxy == null");
                    if (message != null) {
                        AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(1));
                        message.sendToTarget();
                    }
                }
                return radioServiceProxy;
            }
            return radioServiceProxy;
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public synchronized void onSlotActiveStatusChange(boolean z) {
        this.mIsRadioProxyInitialized = false;
        for (int i = 0; i <= 8; i++) {
            if (!z) {
                resetProxyAndRequestList(i);
            } else if (i == 0) {
                getRadioProxy(null);
            } else {
                getRadioServiceProxy(i, (Message) null);
            }
        }
    }

    @UnsupportedAppUsage
    public RIL(Context context, int i, int i2) {
        this(context, i, i2, null);
    }

    @UnsupportedAppUsage
    public RIL(Context context, int i, int i2, Integer num) {
        this(context, i, i2, num, null);
    }

    @VisibleForTesting
    public RIL(Context context, int i, int i2, Integer num, SparseArray<RadioServiceProxy> sparseArray) {
        super(context);
        this.mClientWakelockTracker = new ClientWakelockTracker();
        this.mHalVersion = new HashMap();
        this.mWlSequenceNum = 0;
        this.mAckWlSequenceNum = 0;
        this.mRequestList = new SparseArray<>();
        this.mLastRadioPowerResult = 0;
        this.mIsRadioProxyInitialized = false;
        this.mTestingEmergencyCall = new AtomicBoolean(false);
        this.mDisabledRadioServices = new SparseArray<>();
        this.mMetrics = TelephonyMetrics.getInstance();
        this.mRadioBugDetector = null;
        this.mRadioProxy = null;
        this.mServiceProxies = new SparseArray<>();
        this.mDeathRecipients = new SparseArray<>();
        this.mServiceCookies = new SparseArray<>();
        this.mCompatOverrides = new ConcurrentHashMap<>();
        riljLog("RIL: init allowedNetworkTypes=" + i + " cdmaSubscription=" + i2 + ")");
        this.mContext = context;
        this.mCdmaSubscription = i2;
        this.mAllowedNetworkTypesBitmask = i;
        this.mPhoneType = 0;
        Integer valueOf = Integer.valueOf(num == null ? 0 : num.intValue());
        this.mPhoneId = valueOf;
        if (isRadioBugDetectionEnabled()) {
            this.mRadioBugDetector = new RadioBugDetector(context, valueOf.intValue());
        }
        try {
            if (isRadioVersion2_0()) {
                this.mHalVersion.put(0, RADIO_HAL_VERSION_2_0);
            } else {
                this.mHalVersion.put(0, RADIO_HAL_VERSION_UNKNOWN);
            }
        } catch (SecurityException e) {
            if (sparseArray == null) {
                throw e;
            }
        }
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        this.mIsCellularSupported = telephonyManager.isVoiceCapable() || telephonyManager.isSmsCapable() || telephonyManager.isDataCapable();
        this.mRadioResponse = new RadioResponse(this);
        this.mRadioIndication = new RadioIndication(this);
        this.mDataResponse = new DataResponse(this);
        this.mDataIndication = new DataIndication(this);
        this.mImsResponse = new ImsResponse(this);
        this.mImsIndication = new ImsIndication(this);
        this.mMessagingResponse = new MessagingResponse(this);
        this.mMessagingIndication = new MessagingIndication(this);
        this.mModemResponse = new ModemResponse(this);
        this.mModemIndication = new ModemIndication(this);
        this.mNetworkResponse = new NetworkResponse(this);
        this.mNetworkIndication = new NetworkIndication(this);
        this.mSatelliteResponse = new SatelliteResponse(this);
        this.mSatelliteIndication = new SatelliteIndication(this);
        this.mSimResponse = new SimResponse(this);
        this.mSimIndication = new SimIndication(this);
        this.mVoiceResponse = new VoiceResponse(this);
        this.mVoiceIndication = new VoiceIndication(this);
        this.mRilHandler = new RilHandler();
        this.mRadioProxyDeathRecipient = new RadioProxyDeathRecipient();
        for (int i3 = 0; i3 <= 8; i3++) {
            if (i3 != 0) {
                try {
                    if (isRadioServiceSupported(i3)) {
                        this.mHalVersion.put(Integer.valueOf(i3), RADIO_HAL_VERSION_UNKNOWN);
                    } else {
                        this.mHalVersion.put(Integer.valueOf(i3), RADIO_HAL_VERSION_UNSUPPORTED);
                    }
                } catch (SecurityException e2) {
                    if (sparseArray == null) {
                        throw e2;
                    }
                }
                this.mDeathRecipients.put(i3, new BinderServiceDeathRecipient(i3));
            }
            this.mDisabledRadioServices.put(i3, new HashSet());
            this.mServiceCookies.put(i3, new AtomicLong(0L));
        }
        if (sparseArray == null) {
            this.mServiceProxies.put(1, new RadioDataProxy());
            this.mServiceProxies.put(2, new RadioMessagingProxy());
            this.mServiceProxies.put(3, new RadioModemProxy());
            this.mServiceProxies.put(4, new RadioNetworkProxy());
            this.mServiceProxies.put(5, new RadioSimProxy());
            this.mServiceProxies.put(6, new RadioVoiceProxy());
            this.mServiceProxies.put(7, new RadioImsProxy());
            this.mServiceProxies.put(8, new RadioSatelliteProxy());
        } else {
            this.mServiceProxies = sparseArray;
        }
        PowerManager powerManager = (PowerManager) context.getSystemService("power");
        PowerManager.WakeLock newWakeLock = powerManager.newWakeLock(1, "*telephony-radio*");
        this.mWakeLock = newWakeLock;
        newWakeLock.setReferenceCounted(false);
        PowerManager.WakeLock newWakeLock2 = powerManager.newWakeLock(1, "RILJ_ACK_WL");
        this.mAckWakeLock = newWakeLock2;
        newWakeLock2.setReferenceCounted(false);
        this.mWakeLockTimeout = TelephonyProperties.wake_lock_timeout().orElse(Integer.valueOf((int) ServiceStateTracker.DEFAULT_GPRS_CHECK_PERIOD_MILLIS)).intValue();
        this.mAckWakeLockTimeout = TelephonyProperties.wake_lock_timeout().orElse(Integer.valueOf((int) ImsRttTextHandler.MAX_BUFFERING_DELAY_MILLIS)).intValue();
        this.mWakeLockCount = 0;
        this.mRILDefaultWorkSource = new WorkSource(context.getApplicationInfo().uid, context.getPackageName());
        this.mActiveWakelockWorkSource = new WorkSource();
        TelephonyDevController.getInstance();
        if (sparseArray == null) {
            TelephonyDevController.registerRIL(this);
        }
        for (int i4 = 0; i4 <= 8; i4++) {
            if (i4 == 0) {
                getRadioProxy(null);
            } else if (sparseArray == null) {
                getRadioServiceProxy(i4, (Message) null);
            }
            riljLog("HAL version of " + serviceToString(i4) + ": " + this.mHalVersion.get(Integer.valueOf(i4)));
        }
    }

    private boolean isRadioVersion2_0() {
        for (int i = 1; i <= 8; i++) {
            if (isRadioServiceSupported(i)) {
                return true;
            }
        }
        return false;
    }

    private boolean isRadioServiceSupported(int i) {
        String str;
        if (i == 0) {
            return true;
        }
        switch (i) {
            case 1:
                str = IRadioData.DESCRIPTOR;
                break;
            case 2:
                str = IRadioMessaging.DESCRIPTOR;
                break;
            case 3:
                str = IRadioModem.DESCRIPTOR;
                break;
            case 4:
                str = IRadioNetwork.DESCRIPTOR;
                break;
            case 5:
                str = IRadioSim.DESCRIPTOR;
                break;
            case 6:
                str = IRadioVoice.DESCRIPTOR;
                break;
            case 7:
                str = IRadioIms.DESCRIPTOR;
                break;
            case 8:
                str = IRadioSatellite.DESCRIPTOR;
                break;
            default:
                str = PhoneConfigurationManager.SSSS;
                break;
        }
        if (str.equals(PhoneConfigurationManager.SSSS)) {
            return false;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append('/');
        sb.append(HIDL_SERVICE_NAME[this.mPhoneId.intValue()]);
        return ServiceManager.isDeclared(sb.toString());
    }

    private boolean isRadioBugDetectionEnabled() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "enable_radio_bug_detection", 1) != 0;
    }

    @Override // com.android.internal.telephony.BaseCommands, com.android.internal.telephony.CommandsInterface
    public void setOnNITZTime(Handler handler, int i, Object obj) {
        super.setOnNITZTime(handler, i, obj);
        if (this.mLastNITZTimeInfo != null) {
            this.mNITZTimeRegistrant.notifyRegistrant(new AsyncResult((Object) null, this.mLastNITZTimeInfo, (Throwable) null));
        }
    }

    private void addRequest(RILRequest rILRequest) {
        acquireWakeLock(rILRequest, 0);
        Trace.asyncTraceForTrackBegin(2097152L, "RIL", RILUtils.requestToString(rILRequest.mRequest), rILRequest.mSerial);
        synchronized (this.mRequestList) {
            rILRequest.mStartTimeMs = SystemClock.elapsedRealtime();
            this.mRequestList.append(rILRequest.mSerial, rILRequest);
        }
    }

    private RILRequest obtainRequest(int i, Message message, WorkSource workSource) {
        RILRequest obtain = RILRequest.obtain(i, message, workSource);
        addRequest(obtain);
        return obtain;
    }

    private RILRequest obtainRequest(int i, Message message, WorkSource workSource, Object... objArr) {
        RILRequest obtain = RILRequest.obtain(i, message, workSource, objArr);
        addRequest(obtain);
        return obtain;
    }

    private void handleRadioProxyExceptionForRR(int i, String str, Exception exc) {
        riljLoge(str + ": " + exc);
        exc.printStackTrace();
        this.mIsRadioProxyInitialized = false;
        resetProxyAndRequestList(i);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getIccCardStatus(Message message) {
        RadioSimProxy radioSimProxy = (RadioSimProxy) getRadioServiceProxy(RadioSimProxy.class, message);
        if (radioSimProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(1, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioSimProxy.getIccCardStatus(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(5, "getIccCardStatus", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getIccSlotsStatus(Message message) {
        Rlog.d("RILJ", "getIccSlotsStatus: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setLogicalToPhysicalSlotMapping(int[] iArr, Message message) {
        Rlog.d("RILJ", "setLogicalToPhysicalSlotMapping: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void supplyIccPin(String str, Message message) {
        supplyIccPinForApp(str, null, message);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void supplyIccPinForApp(String str, String str2, Message message) {
        RadioSimProxy radioSimProxy = (RadioSimProxy) getRadioServiceProxy(RadioSimProxy.class, message);
        if (radioSimProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(2, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " aid = " + str2);
        try {
            radioSimProxy.supplyIccPinForApp(obtainRequest.mSerial, RILUtils.convertNullToEmptyString(str), RILUtils.convertNullToEmptyString(str2));
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(5, "supplyIccPinForApp", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void supplyIccPuk(String str, String str2, Message message) {
        supplyIccPukForApp(str, str2, null, message);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void supplyIccPukForApp(String str, String str2, String str3, Message message) {
        RadioSimProxy radioSimProxy = (RadioSimProxy) getRadioServiceProxy(RadioSimProxy.class, message);
        if (radioSimProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(3, message, this.mRILDefaultWorkSource);
        String convertNullToEmptyString = RILUtils.convertNullToEmptyString(str);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " isPukEmpty = " + convertNullToEmptyString.isEmpty() + " aid = " + str3);
        try {
            radioSimProxy.supplyIccPukForApp(obtainRequest.mSerial, convertNullToEmptyString, RILUtils.convertNullToEmptyString(str2), RILUtils.convertNullToEmptyString(str3));
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(5, "supplyIccPukForApp", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void supplyIccPin2(String str, Message message) {
        supplyIccPin2ForApp(str, null, message);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void supplyIccPin2ForApp(String str, String str2, Message message) {
        RadioSimProxy radioSimProxy = (RadioSimProxy) getRadioServiceProxy(RadioSimProxy.class, message);
        if (radioSimProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(4, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " aid = " + str2);
        try {
            radioSimProxy.supplyIccPin2ForApp(obtainRequest.mSerial, RILUtils.convertNullToEmptyString(str), RILUtils.convertNullToEmptyString(str2));
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(5, "supplyIccPin2ForApp", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void supplyIccPuk2(String str, String str2, Message message) {
        supplyIccPuk2ForApp(str, str2, null, message);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void supplyIccPuk2ForApp(String str, String str2, String str3, Message message) {
        RadioSimProxy radioSimProxy = (RadioSimProxy) getRadioServiceProxy(RadioSimProxy.class, message);
        if (radioSimProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(5, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " aid = " + str3);
        try {
            radioSimProxy.supplyIccPuk2ForApp(obtainRequest.mSerial, RILUtils.convertNullToEmptyString(str), RILUtils.convertNullToEmptyString(str2), RILUtils.convertNullToEmptyString(str3));
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(5, "supplyIccPuk2ForApp", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void changeIccPin(String str, String str2, Message message) {
        changeIccPinForApp(str, str2, null, message);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void changeIccPinForApp(String str, String str2, String str3, Message message) {
        RadioSimProxy radioSimProxy = (RadioSimProxy) getRadioServiceProxy(RadioSimProxy.class, message);
        if (radioSimProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(6, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " oldPin = " + str + " newPin = " + str2 + " aid = " + str3);
        try {
            radioSimProxy.changeIccPinForApp(obtainRequest.mSerial, RILUtils.convertNullToEmptyString(str), RILUtils.convertNullToEmptyString(str2), RILUtils.convertNullToEmptyString(str3));
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(5, "changeIccPinForApp", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void changeIccPin2(String str, String str2, Message message) {
        changeIccPin2ForApp(str, str2, null, message);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void changeIccPin2ForApp(String str, String str2, String str3, Message message) {
        RadioSimProxy radioSimProxy = (RadioSimProxy) getRadioServiceProxy(RadioSimProxy.class, message);
        if (radioSimProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(7, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " oldPin = " + str + " newPin = " + str2 + " aid = " + str3);
        try {
            radioSimProxy.changeIccPin2ForApp(obtainRequest.mSerial, RILUtils.convertNullToEmptyString(str), RILUtils.convertNullToEmptyString(str2), RILUtils.convertNullToEmptyString(str3));
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(5, "changeIccPin2ForApp", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void supplyNetworkDepersonalization(String str, Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(8, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " netpin = " + str);
        try {
            radioNetworkProxy.supplyNetworkDepersonalization(obtainRequest.mSerial, RILUtils.convertNullToEmptyString(str));
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(4, "supplyNetworkDepersonalization", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void supplySimDepersonalization(IccCardApplicationStatus.PersoSubState persoSubState, String str, Message message) {
        RadioSimProxy radioSimProxy = (RadioSimProxy) getRadioServiceProxy(RadioSimProxy.class, message);
        if (radioSimProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(5).greaterOrEqual(RADIO_HAL_VERSION_1_5)) {
            RILRequest obtainRequest = obtainRequest(CommandsInterface.GSM_SMS_FAIL_CAUSE_USIM_APP_TOOLKIT_BUSY, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " controlKey = " + str + " persoType" + persoSubState);
            try {
                radioSimProxy.supplySimDepersonalization(obtainRequest.mSerial, persoSubState, RILUtils.convertNullToEmptyString(str));
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(5, "supplySimDepersonalization", e);
            }
        } else if (IccCardApplicationStatus.PersoSubState.PERSOSUBSTATE_SIM_NETWORK == persoSubState) {
            supplyNetworkDepersonalization(str, message);
        } else {
            Rlog.d("RILJ", "supplySimDepersonalization: REQUEST_NOT_SUPPORTED");
            if (message != null) {
                AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
                message.sendToTarget();
            }
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getCurrentCalls(Message message) {
        RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
        if (radioVoiceProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(9, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioVoiceProxy.getCurrentCalls(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(6, "getCurrentCalls", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void dial(String str, boolean z, EmergencyNumber emergencyNumber, boolean z2, int i, Message message) {
        dial(str, z, emergencyNumber, z2, i, null, message);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void enableModem(boolean z, Message message) {
        RadioModemProxy radioModemProxy = (RadioModemProxy) getRadioServiceProxy(RadioModemProxy.class, message);
        if (radioModemProxy.isEmpty()) {
            return;
        }
        if (!this.mHalVersion.get(3).greaterOrEqual(RADIO_HAL_VERSION_1_3)) {
            if (message != null) {
                AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
                message.sendToTarget();
                return;
            }
            return;
        }
        RILRequest obtainRequest = obtainRequest(146, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " enable = " + z);
        try {
            radioModemProxy.enableModem(obtainRequest.mSerial, z);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(3, "enableModem", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setSystemSelectionChannels(List<RadioAccessSpecifier> list, Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        if (!this.mHalVersion.get(4).greaterOrEqual(RADIO_HAL_VERSION_1_3)) {
            if (message != null) {
                AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
                message.sendToTarget();
                return;
            }
            return;
        }
        RILRequest obtainRequest = obtainRequest(210, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " setSystemSelectionChannels_1.3= " + list);
        try {
            radioNetworkProxy.setSystemSelectionChannels(obtainRequest.mSerial, list);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(4, "setSystemSelectionChannels", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getSystemSelectionChannels(Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        if (!this.mHalVersion.get(4).greaterOrEqual(RADIO_HAL_VERSION_1_6)) {
            if (message != null) {
                AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
                message.sendToTarget();
                return;
            }
            return;
        }
        RILRequest obtainRequest = obtainRequest(219, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " getSystemSelectionChannels");
        try {
            radioNetworkProxy.getSystemSelectionChannels(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(4, "getSystemSelectionChannels", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getModemStatus(Message message) {
        RadioModemProxy radioModemProxy = (RadioModemProxy) getRadioServiceProxy(RadioModemProxy.class, message);
        if (radioModemProxy.isEmpty()) {
            return;
        }
        if (!this.mHalVersion.get(3).greaterOrEqual(RADIO_HAL_VERSION_1_3)) {
            if (message != null) {
                AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
                message.sendToTarget();
                return;
            }
            return;
        }
        RILRequest obtainRequest = obtainRequest(147, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioModemProxy.getModemStackStatus(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(3, "getModemStatus", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void dial(String str, boolean z, EmergencyNumber emergencyNumber, boolean z2, int i, UUSInfo uUSInfo, Message message) {
        if (z && this.mHalVersion.get(6).greaterOrEqual(RADIO_HAL_VERSION_1_4) && emergencyNumber != null) {
            emergencyDial(str, emergencyNumber, z2, i, uUSInfo, message);
            return;
        }
        RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
        if (radioVoiceProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(10, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioVoiceProxy.dial(obtainRequest.mSerial, str, i, uUSInfo);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(6, "dial", e);
        }
    }

    private void emergencyDial(String str, EmergencyNumber emergencyNumber, boolean z, int i, UUSInfo uUSInfo, Message message) {
        RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
        if (radioVoiceProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(6).greaterOrEqual(RADIO_HAL_VERSION_1_4)) {
            RILRequest obtainRequest = obtainRequest(205, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioVoiceProxy.emergencyDial(obtainRequest.mSerial, RILUtils.convertNullToEmptyString(str), emergencyNumber, z, i, uUSInfo);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(6, "emergencyDial", e);
                return;
            }
        }
        riljLoge("emergencyDial is not supported with 1.4 below IRadio");
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getIMSI(Message message) {
        getIMSIForApp(null, message);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getIMSIForApp(String str, Message message) {
        RadioSimProxy radioSimProxy = (RadioSimProxy) getRadioServiceProxy(RadioSimProxy.class, message);
        if (radioSimProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(11, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + ">  " + RILUtils.requestToString(obtainRequest.mRequest) + " aid = " + str);
        try {
            radioSimProxy.getImsiForApp(obtainRequest.mSerial, RILUtils.convertNullToEmptyString(str));
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(5, "getImsiForApp", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void hangupConnection(int i, Message message) {
        RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
        if (radioVoiceProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(12, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " gsmIndex = " + i);
        try {
            radioVoiceProxy.hangup(obtainRequest.mSerial, i);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(6, "hangup", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void hangupWaitingOrBackground(Message message) {
        RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
        if (radioVoiceProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(13, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioVoiceProxy.hangupWaitingOrBackground(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(6, "hangupWaitingOrBackground", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void hangupForegroundResumeBackground(Message message) {
        RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
        if (radioVoiceProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(14, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioVoiceProxy.hangupForegroundResumeBackground(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(6, "hangupForegroundResumeBackground", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void switchWaitingOrHoldingAndActive(Message message) {
        RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
        if (radioVoiceProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(15, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioVoiceProxy.switchWaitingOrHoldingAndActive(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(6, "switchWaitingOrHoldingAndActive", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void conference(Message message) {
        RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
        if (radioVoiceProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(16, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioVoiceProxy.conference(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(6, "conference", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void rejectCall(Message message) {
        RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
        if (radioVoiceProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(17, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioVoiceProxy.rejectCall(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(6, "rejectCall", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getLastCallFailCause(Message message) {
        RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
        if (radioVoiceProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(18, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioVoiceProxy.getLastCallFailCause(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(6, "getLastCallFailCause", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getSignalStrength(Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(19, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioNetworkProxy.getSignalStrength(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(4, "getSignalStrength", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getVoiceRegistrationState(Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(20, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        HalVersion compatVersion = getCompatVersion(20);
        riljLog("getVoiceRegistrationState: overrideHalVersion=" + compatVersion);
        try {
            radioNetworkProxy.getVoiceRegistrationState(obtainRequest.mSerial, compatVersion);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(4, "getVoiceRegistrationState", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getDataRegistrationState(Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(21, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        HalVersion compatVersion = getCompatVersion(21);
        riljLog("getDataRegistrationState: overrideHalVersion=" + compatVersion);
        try {
            radioNetworkProxy.getDataRegistrationState(obtainRequest.mSerial, compatVersion);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(4, "getDataRegistrationState", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getOperator(Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(22, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioNetworkProxy.getOperator(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(4, "getOperator", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    @UnsupportedAppUsage
    public void setRadioPower(boolean z, boolean z2, boolean z3, Message message) {
        RadioModemProxy radioModemProxy = (RadioModemProxy) getRadioServiceProxy(RadioModemProxy.class, message);
        if (radioModemProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(23, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " on = " + z + " forEmergencyCall= " + z2 + " preferredForEmergencyCall=" + z3);
        try {
            radioModemProxy.setRadioPower(obtainRequest.mSerial, z, z2, z3);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(3, "setRadioPower", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void sendDtmf(char c, Message message) {
        RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
        if (radioVoiceProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(24, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            int i = obtainRequest.mSerial;
            radioVoiceProxy.sendDtmf(i, c + PhoneConfigurationManager.SSSS);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(6, "sendDtmf", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void sendSMS(String str, String str2, Message message) {
        RadioMessagingProxy radioMessagingProxy = (RadioMessagingProxy) getRadioServiceProxy(RadioMessagingProxy.class, message);
        if (radioMessagingProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(25, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioMessagingProxy.sendSms(obtainRequest.mSerial, str, str2);
            this.mMetrics.writeRilSendSms(this.mPhoneId.intValue(), obtainRequest.mSerial, 1, 1, getOutgoingSmsMessageId(message));
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(2, "sendSMS", e);
        }
    }

    public static long getOutgoingSmsMessageId(Message message) {
        if (message != null) {
            Object obj = message.obj;
            if (obj instanceof SMSDispatcher.SmsTracker) {
                return ((SMSDispatcher.SmsTracker) obj).mMessageId;
            }
            return 0L;
        }
        return 0L;
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void sendSMSExpectMore(String str, String str2, Message message) {
        RadioMessagingProxy radioMessagingProxy = (RadioMessagingProxy) getRadioServiceProxy(RadioMessagingProxy.class, message);
        if (radioMessagingProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(26, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioMessagingProxy.sendSmsExpectMore(obtainRequest.mSerial, str, str2);
            this.mMetrics.writeRilSendSms(this.mPhoneId.intValue(), obtainRequest.mSerial, 1, 1, getOutgoingSmsMessageId(message));
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(2, "sendSMSExpectMore", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setupDataCall(int i, DataProfile dataProfile, boolean z, boolean z2, int i2, LinkProperties linkProperties, int i3, NetworkSliceInfo networkSliceInfo, TrafficDescriptor trafficDescriptor, boolean z3, Message message) {
        int i4;
        RadioDataProxy radioDataProxy = (RadioDataProxy) getRadioServiceProxy(RadioDataProxy.class, message);
        if (!radioDataProxy.isEmpty()) {
            RILRequest obtainRequest = obtainRequest(27, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + ",reason=" + RILUtils.setupDataReasonToString(i2) + ",accessNetworkType=" + AccessNetworkConstants.AccessNetworkType.toString(i) + ",dataProfile=" + dataProfile + ",isRoaming=" + z + ",allowRoaming=" + z2 + ",linkProperties=" + linkProperties + ",pduSessionId=" + i3 + ",sliceInfo=" + networkSliceInfo + ",trafficDescriptor=" + trafficDescriptor + ",matchAllRuleAllowed=" + z3);
            try {
                try {
                } catch (RemoteException e) {
                    e = e;
                    i4 = 1;
                }
                try {
                    radioDataProxy.setupDataCall(obtainRequest.mSerial, this.mPhoneId.intValue(), i, dataProfile, z, z2, i2, linkProperties, i3, networkSliceInfo, trafficDescriptor, z3);
                    return;
                } catch (RemoteException e2) {
                    e = e2;
                    i4 = 1;
                    handleRadioProxyExceptionForRR(i4, "setupDataCall", e);
                    return;
                }
            } catch (RuntimeException e3) {
                riljLoge("setupDataCall RuntimeException: " + e3);
                processResponseInternal(1, obtainRequest.mSerial, 39, 0);
                processResponseDoneInternal(obtainRequest, 39, 0, null);
                return;
            }
        }
        riljLoge("setupDataCall: DataProxy is empty");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(1));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void iccIO(int i, int i2, String str, int i3, int i4, int i5, String str2, String str3, Message message) {
        iccIOForApp(i, i2, str, i3, i4, i5, str2, str3, null, message);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void iccIOForApp(int i, int i2, String str, int i3, int i4, int i5, String str2, String str3, String str4, Message message) {
        RadioSimProxy radioSimProxy = (RadioSimProxy) getRadioServiceProxy(RadioSimProxy.class, message);
        if (radioSimProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(28, message, this.mRILDefaultWorkSource);
        if (TelephonyUtils.IS_DEBUGGABLE) {
            riljLog(obtainRequest.serialString() + "> iccIO: " + RILUtils.requestToString(obtainRequest.mRequest) + " command = 0x" + Integer.toHexString(i) + " fileId = 0x" + Integer.toHexString(i2) + " path = " + str + " p1 = " + i3 + " p2 = " + i4 + " p3 =  data = " + str2 + " aid = " + str4);
        } else {
            riljLog(obtainRequest.serialString() + "> iccIO: " + RILUtils.requestToString(obtainRequest.mRequest));
        }
        try {
            radioSimProxy.iccIoForApp(obtainRequest.mSerial, i, i2, RILUtils.convertNullToEmptyString(str), i3, i4, i5, RILUtils.convertNullToEmptyString(str2), RILUtils.convertNullToEmptyString(str3), RILUtils.convertNullToEmptyString(str4));
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(5, "iccIoForApp", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void sendUSSD(String str, Message message) {
        RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
        if (radioVoiceProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(29, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " ussd = *******");
        try {
            radioVoiceProxy.sendUssd(obtainRequest.mSerial, RILUtils.convertNullToEmptyString(str));
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(6, "sendUssd", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void cancelPendingUssd(Message message) {
        RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
        if (radioVoiceProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(30, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioVoiceProxy.cancelPendingUssd(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(6, "cancelPendingUssd", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getCLIR(Message message) {
        RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
        if (radioVoiceProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(31, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioVoiceProxy.getClir(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(6, "getClir", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setCLIR(int i, Message message) {
        RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
        if (radioVoiceProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(32, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " clirMode = " + i);
        try {
            radioVoiceProxy.setClir(obtainRequest.mSerial, i);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(6, "setClir", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void queryCallForwardStatus(int i, int i2, String str, Message message) {
        RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
        if (radioVoiceProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(33, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " cfreason = " + i + " serviceClass = " + i2);
        try {
            radioVoiceProxy.getCallForwardStatus(obtainRequest.mSerial, i, i2, str);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(6, "getCallForwardStatus", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setCallForward(int i, int i2, int i3, String str, int i4, Message message) {
        RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
        if (radioVoiceProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(34, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " action = " + i + " cfReason = " + i2 + " serviceClass = " + i3 + " timeSeconds = " + i4);
        try {
            radioVoiceProxy.setCallForward(obtainRequest.mSerial, i, i2, i3, str, i4);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(6, "setCallForward", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void queryCallWaiting(int i, Message message) {
        RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
        if (radioVoiceProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(35, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " serviceClass = " + i);
        try {
            radioVoiceProxy.getCallWaiting(obtainRequest.mSerial, i);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(6, "getCallWaiting", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setCallWaiting(boolean z, int i, Message message) {
        RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
        if (radioVoiceProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(36, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " enable = " + z + " serviceClass = " + i);
        try {
            radioVoiceProxy.setCallWaiting(obtainRequest.mSerial, z, i);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(6, "setCallWaiting", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void acknowledgeLastIncomingGsmSms(boolean z, int i, Message message) {
        RadioMessagingProxy radioMessagingProxy = (RadioMessagingProxy) getRadioServiceProxy(RadioMessagingProxy.class, message);
        if (radioMessagingProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(37, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " success = " + z + " cause = " + i);
        try {
            radioMessagingProxy.acknowledgeLastIncomingGsmSms(obtainRequest.mSerial, z, i);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(2, "acknowledgeLastIncomingGsmSms", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void acceptCall(Message message) {
        RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
        if (radioVoiceProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(40, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioVoiceProxy.acceptCall(obtainRequest.mSerial);
            this.mMetrics.writeRilAnswer(this.mPhoneId.intValue(), obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(6, "acceptCall", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void deactivateDataCall(int i, int i2, Message message) {
        RadioDataProxy radioDataProxy = (RadioDataProxy) getRadioServiceProxy(RadioDataProxy.class, message);
        if (radioDataProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(41, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " cid = " + i + " reason = " + RILUtils.deactivateDataReasonToString(i2));
        try {
            radioDataProxy.deactivateDataCall(obtainRequest.mSerial, i, i2);
            this.mMetrics.writeRilDeactivateDataCall(this.mPhoneId.intValue(), obtainRequest.mSerial, i, i2);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(1, "deactivateDataCall", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void queryFacilityLock(String str, String str2, int i, Message message) {
        queryFacilityLockForApp(str, str2, i, null, message);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void queryFacilityLockForApp(String str, String str2, int i, String str3, Message message) {
        RadioSimProxy radioSimProxy = (RadioSimProxy) getRadioServiceProxy(RadioSimProxy.class, message);
        if (radioSimProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(42, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " facility = " + str + " serviceClass = " + i + " appId = " + str3);
        try {
            radioSimProxy.getFacilityLockForApp(obtainRequest.mSerial, RILUtils.convertNullToEmptyString(str), RILUtils.convertNullToEmptyString(str2), i, RILUtils.convertNullToEmptyString(str3));
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(5, "getFacilityLockForApp", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setFacilityLock(String str, boolean z, String str2, int i, Message message) {
        setFacilityLockForApp(str, z, str2, i, null, message);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setFacilityLockForApp(String str, boolean z, String str2, int i, String str3, Message message) {
        RadioSimProxy radioSimProxy = (RadioSimProxy) getRadioServiceProxy(RadioSimProxy.class, message);
        if (radioSimProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(43, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " facility = " + str + " lockstate = " + z + " serviceClass = " + i + " appId = " + str3);
        try {
            radioSimProxy.setFacilityLockForApp(obtainRequest.mSerial, RILUtils.convertNullToEmptyString(str), z, RILUtils.convertNullToEmptyString(str2), i, RILUtils.convertNullToEmptyString(str3));
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(5, "setFacilityLockForApp", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void changeBarringPassword(String str, String str2, String str3, Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(44, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + "facility = " + str);
        try {
            radioNetworkProxy.setBarringPassword(obtainRequest.mSerial, RILUtils.convertNullToEmptyString(str), RILUtils.convertNullToEmptyString(str2), RILUtils.convertNullToEmptyString(str3));
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(4, "changeBarringPassword", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getNetworkSelectionMode(Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(45, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioNetworkProxy.getNetworkSelectionMode(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(4, "getNetworkSelectionMode", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setNetworkSelectionModeAutomatic(Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(46, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioNetworkProxy.setNetworkSelectionModeAutomatic(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(4, "setNetworkSelectionModeAutomatic", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setNetworkSelectionModeManual(String str, int i, Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(47, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " operatorNumeric = " + str + ", ran = " + i);
        try {
            radioNetworkProxy.setNetworkSelectionModeManual(obtainRequest.mSerial, RILUtils.convertNullToEmptyString(str), i);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(4, "setNetworkSelectionModeManual", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getAvailableNetworks(Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(48, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioNetworkProxy.getAvailableNetworks(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(4, "getAvailableNetworks", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void startNetworkScan(NetworkScanRequest networkScanRequest, Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(4).greaterOrEqual(RADIO_HAL_VERSION_1_1)) {
            HalVersion compatVersion = getCompatVersion(142);
            riljLog("startNetworkScan: overrideHalVersion=" + compatVersion);
            RILRequest obtainRequest = obtainRequest(142, message, this.mRILDefaultWorkSource, networkScanRequest);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioNetworkProxy.startNetworkScan(obtainRequest.mSerial, networkScanRequest, compatVersion, message);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(4, "startNetworkScan", e);
                return;
            }
        }
        Rlog.d("RILJ", "startNetworkScan: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void stopNetworkScan(Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(4).greaterOrEqual(RADIO_HAL_VERSION_1_1)) {
            RILRequest obtainRequest = obtainRequest(143, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioNetworkProxy.stopNetworkScan(obtainRequest.mSerial);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(4, "stopNetworkScan", e);
                return;
            }
        }
        Rlog.d("RILJ", "stopNetworkScan: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void startDtmf(char c, Message message) {
        RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
        if (radioVoiceProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(49, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            int i = obtainRequest.mSerial;
            radioVoiceProxy.startDtmf(i, c + PhoneConfigurationManager.SSSS);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(6, "startDtmf", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void stopDtmf(Message message) {
        RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
        if (radioVoiceProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(50, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioVoiceProxy.stopDtmf(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(6, "stopDtmf", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void separateConnection(int i, Message message) {
        RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
        if (radioVoiceProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(52, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " gsmIndex = " + i);
        try {
            radioVoiceProxy.separateConnection(obtainRequest.mSerial, i);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(6, "separateConnection", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getBasebandVersion(Message message) {
        RadioModemProxy radioModemProxy = (RadioModemProxy) getRadioServiceProxy(RadioModemProxy.class, message);
        if (radioModemProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(51, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioModemProxy.getBasebandVersion(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(3, "getBasebandVersion", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setMute(boolean z, Message message) {
        RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
        if (radioVoiceProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(53, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " enableMute = " + z);
        try {
            radioVoiceProxy.setMute(obtainRequest.mSerial, z);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(6, "setMute", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getMute(Message message) {
        RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
        if (radioVoiceProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(54, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioVoiceProxy.getMute(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(6, "getMute", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void queryCLIP(Message message) {
        RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
        if (radioVoiceProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(55, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioVoiceProxy.getClip(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(6, "getClip", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    @Deprecated
    public void getPDPContextList(Message message) {
        getDataCallList(message);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getDataCallList(Message message) {
        RadioDataProxy radioDataProxy = (RadioDataProxy) getRadioServiceProxy(RadioDataProxy.class, message);
        if (radioDataProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(57, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioDataProxy.getDataCallList(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(1, "getDataCallList", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setSuppServiceNotifications(boolean z, Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(62, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " enable = " + z);
        try {
            radioNetworkProxy.setSuppServiceNotifications(obtainRequest.mSerial, z);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(4, "setSuppServiceNotifications", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void writeSmsToSim(int i, String str, String str2, Message message) {
        RadioMessagingProxy radioMessagingProxy = (RadioMessagingProxy) getRadioServiceProxy(RadioMessagingProxy.class, message);
        if (radioMessagingProxy.isEmpty()) {
            return;
        }
        try {
            radioMessagingProxy.writeSmsToSim(obtainRequest(63, message, this.mRILDefaultWorkSource).mSerial, i, RILUtils.convertNullToEmptyString(str), RILUtils.convertNullToEmptyString(str2));
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(2, "writeSmsToSim", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void deleteSmsOnSim(int i, Message message) {
        RadioMessagingProxy radioMessagingProxy = (RadioMessagingProxy) getRadioServiceProxy(RadioMessagingProxy.class, message);
        if (radioMessagingProxy.isEmpty()) {
            return;
        }
        try {
            radioMessagingProxy.deleteSmsOnSim(obtainRequest(64, message, this.mRILDefaultWorkSource).mSerial, i);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(2, "deleteSmsOnSim", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setBandMode(int i, Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(65, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " bandMode = " + i);
        try {
            radioNetworkProxy.setBandMode(obtainRequest.mSerial, i);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(4, "setBandMode", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void queryAvailableBandMode(Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(66, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioNetworkProxy.getAvailableBandModes(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(4, "queryAvailableBandMode", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void sendEnvelope(String str, Message message) {
        RadioSimProxy radioSimProxy = (RadioSimProxy) getRadioServiceProxy(RadioSimProxy.class, message);
        if (radioSimProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(69, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " contents = " + str);
        try {
            radioSimProxy.sendEnvelope(obtainRequest.mSerial, RILUtils.convertNullToEmptyString(str));
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(5, "sendEnvelope", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void sendTerminalResponse(String str, Message message) {
        RadioSimProxy radioSimProxy = (RadioSimProxy) getRadioServiceProxy(RadioSimProxy.class, message);
        if (radioSimProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(70, message, this.mRILDefaultWorkSource);
        StringBuilder sb = new StringBuilder();
        sb.append(obtainRequest.serialString());
        sb.append("> ");
        sb.append(RILUtils.requestToString(obtainRequest.mRequest));
        sb.append(" contents = ");
        sb.append(TelephonyUtils.IS_DEBUGGABLE ? str : RILUtils.convertToCensoredTerminalResponse(str));
        riljLog(sb.toString());
        try {
            radioSimProxy.sendTerminalResponseToSim(obtainRequest.mSerial, RILUtils.convertNullToEmptyString(str));
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(5, "sendTerminalResponse", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void sendEnvelopeWithStatus(String str, Message message) {
        RadioSimProxy radioSimProxy = (RadioSimProxy) getRadioServiceProxy(RadioSimProxy.class, message);
        if (radioSimProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(107, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " contents = " + str);
        try {
            radioSimProxy.sendEnvelopeWithStatus(obtainRequest.mSerial, RILUtils.convertNullToEmptyString(str));
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(5, "sendEnvelopeWithStatus", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void explicitCallTransfer(Message message) {
        RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
        if (radioVoiceProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(72, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioVoiceProxy.explicitCallTransfer(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(6, "explicitCallTransfer", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setPreferredNetworkType(int i, Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(73, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " networkType = " + i);
        this.mAllowedNetworkTypesBitmask = RadioAccessFamily.getRafFromNetworkType(i);
        this.mMetrics.writeSetPreferredNetworkType(this.mPhoneId.intValue(), i);
        try {
            radioNetworkProxy.setPreferredNetworkTypeBitmap(obtainRequest.mSerial, this.mAllowedNetworkTypesBitmask);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(4, "setPreferredNetworkType", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getPreferredNetworkType(Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(74, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioNetworkProxy.getAllowedNetworkTypesBitmap(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(4, "getPreferredNetworkType", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setAllowedNetworkTypesBitmap(int i, Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(4).less(RADIO_HAL_VERSION_1_6)) {
            setPreferredNetworkType(RadioAccessFamily.getNetworkTypeFromRaf(i), message);
            return;
        }
        RILRequest obtainRequest = obtainRequest(222, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        this.mAllowedNetworkTypesBitmask = i;
        try {
            radioNetworkProxy.setAllowedNetworkTypesBitmap(obtainRequest.mSerial, i);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(4, "setAllowedNetworkTypeBitmask", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getAllowedNetworkTypesBitmap(Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(223, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioNetworkProxy.getAllowedNetworkTypesBitmap(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(4, "getAllowedNetworkTypeBitmask", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setLocationUpdates(boolean z, WorkSource workSource, Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(76, message, getDefaultWorkSourceIfInvalid(workSource));
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " enable = " + z);
        try {
            radioNetworkProxy.setLocationUpdates(obtainRequest.mSerial, z);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(4, "setLocationUpdates", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void isNrDualConnectivityEnabled(Message message, WorkSource workSource) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(4).greaterOrEqual(RADIO_HAL_VERSION_1_6)) {
            RILRequest obtainRequest = obtainRequest(BerTlv.BER_EVENT_DOWNLOAD_TAG, message, getDefaultWorkSourceIfInvalid(workSource));
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioNetworkProxy.isNrDualConnectivityEnabled(obtainRequest.mSerial);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(4, "isNrDualConnectivityEnabled", e);
                return;
            }
        }
        Rlog.d("RILJ", "isNrDualConnectivityEnabled: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setNrDualConnectivityState(int i, Message message, WorkSource workSource) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(4).greaterOrEqual(RADIO_HAL_VERSION_1_6)) {
            RILRequest obtainRequest = obtainRequest(CommandsInterface.GSM_SMS_FAIL_CAUSE_USIM_DATA_DOWNLOAD_ERROR, message, getDefaultWorkSourceIfInvalid(workSource));
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " enable = " + i);
            try {
                radioNetworkProxy.setNrDualConnectivityState(obtainRequest.mSerial, (byte) i);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(4, "enableNrDualConnectivity", e);
                return;
            }
        }
        Rlog.d("RILJ", "enableNrDualConnectivity: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    private void setVoNrEnabled(boolean z) {
        SystemProperties.set("persist.radio.is_vonr_enabled_" + this.mPhoneId, String.valueOf(z));
    }

    private boolean isVoNrEnabled() {
        return SystemProperties.getBoolean("persist.radio.is_vonr_enabled_" + this.mPhoneId, true);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void isVoNrEnabled(Message message, WorkSource workSource) {
        if (this.mHalVersion.get(6).greaterOrEqual(RADIO_HAL_VERSION_2_0)) {
            RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
            if (radioVoiceProxy.isEmpty()) {
                return;
            }
            RILRequest obtainRequest = obtainRequest(226, message, getDefaultWorkSourceIfInvalid(workSource));
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioVoiceProxy.isVoNrEnabled(obtainRequest.mSerial);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(6, "isVoNrEnabled", e);
                return;
            }
        }
        boolean isVoNrEnabled = isVoNrEnabled();
        if (message != null) {
            AsyncResult.forMessage(message, Boolean.valueOf(isVoNrEnabled), (Throwable) null);
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setVoNrEnabled(boolean z, Message message, WorkSource workSource) {
        setVoNrEnabled(z);
        if (this.mHalVersion.get(6).greaterOrEqual(RADIO_HAL_VERSION_2_0)) {
            RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
            if (radioVoiceProxy.isEmpty()) {
                return;
            }
            RILRequest obtainRequest = obtainRequest(225, message, getDefaultWorkSourceIfInvalid(workSource));
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioVoiceProxy.setVoNrEnabled(obtainRequest.mSerial, z);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(6, "setVoNrEnabled", e);
                return;
            }
        }
        isNrDualConnectivityEnabled(null, workSource);
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, (Throwable) null);
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setCdmaSubscriptionSource(int i, Message message) {
        RadioSimProxy radioSimProxy = (RadioSimProxy) getRadioServiceProxy(RadioSimProxy.class, message);
        if (radioSimProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(77, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " cdmaSubscription = " + i);
        try {
            radioSimProxy.setCdmaSubscriptionSource(obtainRequest.mSerial, i);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(5, "setCdmaSubscriptionSource", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void queryCdmaRoamingPreference(Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(79, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioNetworkProxy.getCdmaRoamingPreference(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(4, "queryCdmaRoamingPreference", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setCdmaRoamingPreference(int i, Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(78, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " cdmaRoamingType = " + i);
        try {
            radioNetworkProxy.setCdmaRoamingPreference(obtainRequest.mSerial, i);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(4, "setCdmaRoamingPreference", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void queryTTYMode(Message message) {
        RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
        if (radioVoiceProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(81, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioVoiceProxy.getTtyMode(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(6, "getTtyMode", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setTTYMode(int i, Message message) {
        RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
        if (radioVoiceProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(80, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " ttyMode = " + i);
        try {
            radioVoiceProxy.setTtyMode(obtainRequest.mSerial, i);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(6, "setTtyMode", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setPreferredVoicePrivacy(boolean z, Message message) {
        RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
        if (radioVoiceProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(82, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " enable = " + z);
        try {
            radioVoiceProxy.setPreferredVoicePrivacy(obtainRequest.mSerial, z);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(6, "setPreferredVoicePrivacy", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getPreferredVoicePrivacy(Message message) {
        RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
        if (radioVoiceProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(83, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioVoiceProxy.getPreferredVoicePrivacy(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(6, "getPreferredVoicePrivacy", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void sendCDMAFeatureCode(String str, Message message) {
        RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
        if (radioVoiceProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(84, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " featureCode = " + Rlog.pii("RILJ", str));
        try {
            radioVoiceProxy.sendCdmaFeatureCode(obtainRequest.mSerial, RILUtils.convertNullToEmptyString(str));
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(6, "sendCdmaFeatureCode", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void sendBurstDtmf(String str, int i, int i2, Message message) {
        RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
        if (radioVoiceProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(85, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " dtmfString = " + str + " on = " + i + " off = " + i2);
        try {
            radioVoiceProxy.sendBurstDtmf(obtainRequest.mSerial, RILUtils.convertNullToEmptyString(str), i, i2);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(6, "sendBurstDtmf", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void sendCdmaSMSExpectMore(byte[] bArr, Message message) {
        RadioMessagingProxy radioMessagingProxy = (RadioMessagingProxy) getRadioServiceProxy(RadioMessagingProxy.class, message);
        if (radioMessagingProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(148, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioMessagingProxy.sendCdmaSmsExpectMore(obtainRequest.mSerial, bArr);
            if (this.mHalVersion.get(2).greaterOrEqual(RADIO_HAL_VERSION_1_5)) {
                this.mMetrics.writeRilSendSms(this.mPhoneId.intValue(), obtainRequest.mSerial, 2, 2, getOutgoingSmsMessageId(message));
            }
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(2, "sendCdmaSMSExpectMore", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void sendCdmaSms(byte[] bArr, Message message) {
        RadioMessagingProxy radioMessagingProxy = (RadioMessagingProxy) getRadioServiceProxy(RadioMessagingProxy.class, message);
        if (radioMessagingProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(87, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioMessagingProxy.sendCdmaSms(obtainRequest.mSerial, bArr);
            this.mMetrics.writeRilSendSms(this.mPhoneId.intValue(), obtainRequest.mSerial, 2, 2, getOutgoingSmsMessageId(message));
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(2, "sendCdmaSms", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void acknowledgeLastIncomingCdmaSms(boolean z, int i, Message message) {
        RadioMessagingProxy radioMessagingProxy = (RadioMessagingProxy) getRadioServiceProxy(RadioMessagingProxy.class, message);
        if (radioMessagingProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(88, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " success = " + z + " cause = " + i);
        try {
            radioMessagingProxy.acknowledgeLastIncomingCdmaSms(obtainRequest.mSerial, z, i);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(2, "acknowledgeLastIncomingCdmaSms", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getGsmBroadcastConfig(Message message) {
        RadioMessagingProxy radioMessagingProxy = (RadioMessagingProxy) getRadioServiceProxy(RadioMessagingProxy.class, message);
        if (radioMessagingProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(89, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioMessagingProxy.getGsmBroadcastConfig(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(2, "getGsmBroadcastConfig", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setGsmBroadcastConfig(SmsBroadcastConfigInfo[] smsBroadcastConfigInfoArr, Message message) {
        RadioMessagingProxy radioMessagingProxy = (RadioMessagingProxy) getRadioServiceProxy(RadioMessagingProxy.class, message);
        if (radioMessagingProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(90, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " with " + smsBroadcastConfigInfoArr.length + " configs : ");
        for (SmsBroadcastConfigInfo smsBroadcastConfigInfo : smsBroadcastConfigInfoArr) {
            riljLog(smsBroadcastConfigInfo.toString());
        }
        try {
            radioMessagingProxy.setGsmBroadcastConfig(obtainRequest.mSerial, smsBroadcastConfigInfoArr);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(2, "setGsmBroadcastConfig", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setGsmBroadcastActivation(boolean z, Message message) {
        RadioMessagingProxy radioMessagingProxy = (RadioMessagingProxy) getRadioServiceProxy(RadioMessagingProxy.class, message);
        if (radioMessagingProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(91, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " activate = " + z);
        try {
            radioMessagingProxy.setGsmBroadcastActivation(obtainRequest.mSerial, z);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(2, "setGsmBroadcastActivation", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getCdmaBroadcastConfig(Message message) {
        RadioMessagingProxy radioMessagingProxy = (RadioMessagingProxy) getRadioServiceProxy(RadioMessagingProxy.class, message);
        if (radioMessagingProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(92, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioMessagingProxy.getCdmaBroadcastConfig(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(2, "getCdmaBroadcastConfig", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setCdmaBroadcastConfig(CdmaSmsBroadcastConfigInfo[] cdmaSmsBroadcastConfigInfoArr, Message message) {
        RadioMessagingProxy radioMessagingProxy = (RadioMessagingProxy) getRadioServiceProxy(RadioMessagingProxy.class, message);
        if (radioMessagingProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(93, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " with " + cdmaSmsBroadcastConfigInfoArr.length + " configs : ");
        for (CdmaSmsBroadcastConfigInfo cdmaSmsBroadcastConfigInfo : cdmaSmsBroadcastConfigInfoArr) {
            riljLog(cdmaSmsBroadcastConfigInfo.toString());
        }
        try {
            radioMessagingProxy.setCdmaBroadcastConfig(obtainRequest.mSerial, cdmaSmsBroadcastConfigInfoArr);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(2, "setCdmaBroadcastConfig", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setCdmaBroadcastActivation(boolean z, Message message) {
        RadioMessagingProxy radioMessagingProxy = (RadioMessagingProxy) getRadioServiceProxy(RadioMessagingProxy.class, message);
        if (radioMessagingProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(94, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " activate = " + z);
        try {
            radioMessagingProxy.setCdmaBroadcastActivation(obtainRequest.mSerial, z);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(2, "setCdmaBroadcastActivation", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getCDMASubscription(Message message) {
        RadioSimProxy radioSimProxy = (RadioSimProxy) getRadioServiceProxy(RadioSimProxy.class, message);
        if (radioSimProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(95, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioSimProxy.getCdmaSubscription(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(5, "getCdmaSubscription", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void writeSmsToRuim(int i, byte[] bArr, Message message) {
        RadioMessagingProxy radioMessagingProxy = (RadioMessagingProxy) getRadioServiceProxy(RadioMessagingProxy.class, message);
        if (radioMessagingProxy.isEmpty()) {
            return;
        }
        try {
            radioMessagingProxy.writeSmsToRuim(obtainRequest(96, message, this.mRILDefaultWorkSource).mSerial, i, bArr);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(2, "writeSmsToRuim", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void deleteSmsOnRuim(int i, Message message) {
        RadioMessagingProxy radioMessagingProxy = (RadioMessagingProxy) getRadioServiceProxy(RadioMessagingProxy.class, message);
        if (radioMessagingProxy.isEmpty()) {
            return;
        }
        try {
            radioMessagingProxy.deleteSmsOnRuim(obtainRequest(97, message, this.mRILDefaultWorkSource).mSerial, i);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(2, "deleteSmsOnRuim", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getDeviceIdentity(Message message) {
        RadioModemProxy radioModemProxy = (RadioModemProxy) getRadioServiceProxy(RadioModemProxy.class, message);
        if (radioModemProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(98, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioModemProxy.getDeviceIdentity(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(3, "getDeviceIdentity", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getImei(Message message) {
        RadioModemProxy radioModemProxy = (RadioModemProxy) getRadioServiceProxy(RadioModemProxy.class, message);
        if (!radioModemProxy.isEmpty() && this.mHalVersion.get(4).greaterOrEqual(RADIO_HAL_VERSION_2_1)) {
            RILRequest obtainRequest = obtainRequest(152, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioModemProxy.getImei(obtainRequest.mSerial);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(3, "getImei", e);
                return;
            }
        }
        Rlog.e("RILJ", "getImei: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void exitEmergencyCallbackMode(Message message) {
        RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
        if (radioVoiceProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(99, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioVoiceProxy.exitEmergencyCallbackMode(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(6, "exitEmergencyCallbackMode", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getSmscAddress(Message message) {
        RadioMessagingProxy radioMessagingProxy = (RadioMessagingProxy) getRadioServiceProxy(RadioMessagingProxy.class, message);
        if (radioMessagingProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(100, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioMessagingProxy.getSmscAddress(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(2, "getSmscAddress", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setSmscAddress(String str, Message message) {
        RadioMessagingProxy radioMessagingProxy = (RadioMessagingProxy) getRadioServiceProxy(RadioMessagingProxy.class, message);
        if (radioMessagingProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(101, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " address = " + str);
        try {
            radioMessagingProxy.setSmscAddress(obtainRequest.mSerial, RILUtils.convertNullToEmptyString(str));
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(2, "setSmscAddress", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void reportSmsMemoryStatus(boolean z, Message message) {
        RadioMessagingProxy radioMessagingProxy = (RadioMessagingProxy) getRadioServiceProxy(RadioMessagingProxy.class, message);
        if (radioMessagingProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(CallFailCause.RECOVERY_ON_TIMER_EXPIRY, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " available = " + z);
        try {
            radioMessagingProxy.reportSmsMemoryStatus(obtainRequest.mSerial, z);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(2, "reportSmsMemoryStatus", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void reportStkServiceIsRunning(Message message) {
        RadioSimProxy radioSimProxy = (RadioSimProxy) getRadioServiceProxy(RadioSimProxy.class, message);
        if (radioSimProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(103, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioSimProxy.reportStkServiceIsRunning(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(5, "reportStkServiceIsRunning", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getCdmaSubscriptionSource(Message message) {
        RadioSimProxy radioSimProxy = (RadioSimProxy) getRadioServiceProxy(RadioSimProxy.class, message);
        if (radioSimProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(104, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioSimProxy.getCdmaSubscriptionSource(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(5, "getCdmaSubscriptionSource", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void acknowledgeIncomingGsmSmsWithPdu(boolean z, String str, Message message) {
        RadioMessagingProxy radioMessagingProxy = (RadioMessagingProxy) getRadioServiceProxy(RadioMessagingProxy.class, message);
        if (radioMessagingProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(106, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " success = " + z);
        try {
            radioMessagingProxy.acknowledgeIncomingGsmSmsWithPdu(obtainRequest.mSerial, z, RILUtils.convertNullToEmptyString(str));
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(2, "acknowledgeIncomingGsmSmsWithPdu", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getVoiceRadioTechnology(Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(108, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioNetworkProxy.getVoiceRadioTechnology(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(4, "getVoiceRadioTechnology", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getCellInfoList(Message message, WorkSource workSource) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(109, message, getDefaultWorkSourceIfInvalid(workSource));
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioNetworkProxy.getCellInfoList(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(4, "getCellInfoList", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setCellInfoListRate(int i, Message message, WorkSource workSource) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(110, message, getDefaultWorkSourceIfInvalid(workSource));
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " rateInMillis = " + i);
        try {
            radioNetworkProxy.setCellInfoListRate(obtainRequest.mSerial, i);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(4, "setCellInfoListRate", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setInitialAttachApn(DataProfile dataProfile, boolean z, Message message) {
        RadioDataProxy radioDataProxy = (RadioDataProxy) getRadioServiceProxy(RadioDataProxy.class, message);
        if (radioDataProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(111, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + dataProfile);
        try {
            radioDataProxy.setInitialAttachApn(obtainRequest.mSerial, dataProfile, z);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(1, "setInitialAttachApn", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getImsRegistrationState(Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_APN_TYPE_CONFLICT, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioNetworkProxy.getImsRegistrationState(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(4, "getImsRegistrationState", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void sendImsGsmSms(String str, String str2, int i, int i2, Message message) {
        RadioMessagingProxy radioMessagingProxy = (RadioMessagingProxy) getRadioServiceProxy(RadioMessagingProxy.class, message);
        if (radioMessagingProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_INVALID_PCSCF_ADDR, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioMessagingProxy.sendImsSms(obtainRequest.mSerial, str, str2, null, i, i2);
            this.mMetrics.writeRilSendSms(this.mPhoneId.intValue(), obtainRequest.mSerial, 3, 1, getOutgoingSmsMessageId(message));
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(2, "sendImsGsmSms", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void sendImsCdmaSms(byte[] bArr, int i, int i2, Message message) {
        RadioMessagingProxy radioMessagingProxy = (RadioMessagingProxy) getRadioServiceProxy(RadioMessagingProxy.class, message);
        if (radioMessagingProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_INVALID_PCSCF_ADDR, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioMessagingProxy.sendImsSms(obtainRequest.mSerial, null, null, bArr, i, i2);
            this.mMetrics.writeRilSendSms(this.mPhoneId.intValue(), obtainRequest.mSerial, 3, 2, getOutgoingSmsMessageId(message));
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(2, "sendImsCdmaSms", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void iccTransmitApduBasicChannel(int i, int i2, int i3, int i4, int i5, String str, Message message) {
        RadioSimProxy radioSimProxy = (RadioSimProxy) getRadioServiceProxy(RadioSimProxy.class, message);
        if (radioSimProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_INTERNAL_CALL_PREEMPT_BY_HIGH_PRIO_APN, message, this.mRILDefaultWorkSource);
        if (TelephonyUtils.IS_DEBUGGABLE) {
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + String.format(" cla = 0x%02X ins = 0x%02X", Integer.valueOf(i), Integer.valueOf(i2)) + String.format(" p1 = 0x%02X p2 = 0x%02X p3 = 0x%02X", Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i5)) + " data = " + str);
        } else {
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        }
        try {
            radioSimProxy.iccTransmitApduBasicChannel(obtainRequest.mSerial, i, i2, i3, i4, i5, str);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(5, "iccTransmitApduBasicChannel", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void iccOpenLogicalChannel(String str, int i, Message message) {
        RadioSimProxy radioSimProxy = (RadioSimProxy) getRadioServiceProxy(RadioSimProxy.class, message);
        if (radioSimProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_EMM_ACCESS_BARRED, message, this.mRILDefaultWorkSource);
        if (TelephonyUtils.IS_DEBUGGABLE) {
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " aid = " + str + " p2 = " + i);
        } else {
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        }
        try {
            radioSimProxy.iccOpenLogicalChannel(obtainRequest.mSerial, RILUtils.convertNullToEmptyString(str), i);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(5, "iccOpenLogicalChannel", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void iccCloseLogicalChannel(int i, boolean z, Message message) {
        RadioSimProxy radioSimProxy = (RadioSimProxy) getRadioServiceProxy(RadioSimProxy.class, message);
        if (radioSimProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_EMERGENCY_IFACE_ONLY, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " channel = " + i + " isEs10 = " + z);
        try {
            radioSimProxy.iccCloseLogicalChannel(obtainRequest.mSerial, i, z);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(5, "iccCloseLogicalChannel", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void iccTransmitApduLogicalChannel(int i, int i2, int i3, int i4, int i5, int i6, String str, boolean z, Message message) {
        if (i <= 0) {
            throw new RuntimeException("Invalid channel in iccTransmitApduLogicalChannel: " + i);
        }
        RadioSimProxy radioSimProxy = (RadioSimProxy) getRadioServiceProxy(RadioSimProxy.class, message);
        if (radioSimProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_IFACE_MISMATCH, message, this.mRILDefaultWorkSource);
        if (TelephonyUtils.IS_DEBUGGABLE) {
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + String.format(" channel = %d", Integer.valueOf(i)) + String.format(" cla = 0x%02X ins = 0x%02X", Integer.valueOf(i2), Integer.valueOf(i3)) + String.format(" p1 = 0x%02X p2 = 0x%02X p3 = 0x%02X", Integer.valueOf(i4), Integer.valueOf(i5), Integer.valueOf(i6)) + " isEs10Command = " + z + " data = " + str);
        } else {
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        }
        try {
            radioSimProxy.iccTransmitApduLogicalChannel(obtainRequest.mSerial, i, i2, i3, i4, i5, i6, str, z);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(5, "iccTransmitApduLogicalChannel", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void nvReadItem(int i, Message message, WorkSource workSource) {
        RadioModemProxy radioModemProxy = (RadioModemProxy) getRadioServiceProxy(RadioModemProxy.class, message);
        if (radioModemProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_COMPANION_IFACE_IN_USE, message, getDefaultWorkSourceIfInvalid(workSource));
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " itemId = " + i);
        try {
            radioModemProxy.nvReadItem(obtainRequest.mSerial, i);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(3, "nvReadItem", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void nvWriteItem(int i, String str, Message message, WorkSource workSource) {
        RadioModemProxy radioModemProxy = (RadioModemProxy) getRadioServiceProxy(RadioModemProxy.class, message);
        if (radioModemProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_IP_ADDRESS_MISMATCH, message, getDefaultWorkSourceIfInvalid(workSource));
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " itemId = " + i + " itemValue = " + str);
        try {
            radioModemProxy.nvWriteItem(obtainRequest.mSerial, i, RILUtils.convertNullToEmptyString(str));
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(3, "nvWriteItem", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void nvWriteCdmaPrl(byte[] bArr, Message message) {
        RadioModemProxy radioModemProxy = (RadioModemProxy) getRadioServiceProxy(RadioModemProxy.class, message);
        if (radioModemProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_IFACE_AND_POL_FAMILY_MISMATCH, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " PreferredRoamingList = 0x" + IccUtils.bytesToHexString(bArr));
        try {
            radioModemProxy.nvWriteCdmaPrl(obtainRequest.mSerial, bArr);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(3, "nvWriteCdmaPrl", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void nvResetConfig(int i, Message message) {
        RadioModemProxy radioModemProxy = (RadioModemProxy) getRadioServiceProxy(RadioModemProxy.class, message);
        if (radioModemProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_EMM_ACCESS_BARRED_INFINITE_RETRY, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " resetType = " + i);
        try {
            radioModemProxy.nvResetConfig(obtainRequest.mSerial, i);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(3, "nvResetConfig", e);
        }
    }

    @Override // com.android.internal.telephony.BaseCommands, com.android.internal.telephony.CommandsInterface
    public void setUiccSubscription(int i, int i2, int i3, int i4, Message message) {
        RadioSimProxy radioSimProxy = (RadioSimProxy) getRadioServiceProxy(RadioSimProxy.class, message);
        if (radioSimProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_AUTH_FAILURE_ON_EMERGENCY_CALL, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " slot = " + i + " appIndex = " + i2 + " subId = " + i3 + " subStatus = " + i4);
        try {
            radioSimProxy.setUiccSubscription(obtainRequest.mSerial, i, i2, i3, i4);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(5, "setUiccSubscription", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public boolean supportsEid() {
        return this.mHalVersion.get(0).greaterOrEqual(RADIO_HAL_VERSION_1_2);
    }

    @Override // com.android.internal.telephony.BaseCommands, com.android.internal.telephony.CommandsInterface
    public void setDataAllowed(boolean z, Message message) {
        RadioDataProxy radioDataProxy = (RadioDataProxy) getRadioServiceProxy(RadioDataProxy.class, message);
        if (radioDataProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_INVALID_DNS_ADDR, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " allowed = " + z);
        try {
            radioDataProxy.setDataAllowed(obtainRequest.mSerial, z);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(1, "setDataAllowed", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getHardwareConfig(Message message) {
        RadioModemProxy radioModemProxy = (RadioModemProxy) getRadioServiceProxy(RadioModemProxy.class, message);
        if (radioModemProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_INVALID_PCSCF_OR_DNS_ADDRESS, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioModemProxy.getHardwareConfig(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(3, "getHardwareConfig", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void requestIccSimAuthentication(int i, String str, String str2, Message message) {
        RadioSimProxy radioSimProxy = (RadioSimProxy) getRadioServiceProxy(RadioSimProxy.class, message);
        if (radioSimProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(125, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioSimProxy.requestIccSimAuthentication(obtainRequest.mSerial, i, RILUtils.convertNullToEmptyString(str), RILUtils.convertNullToEmptyString(str2));
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(5, "requestIccSimAuthentication", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setDataProfile(DataProfile[] dataProfileArr, boolean z, Message message) {
        RadioDataProxy radioDataProxy = (RadioDataProxy) getRadioServiceProxy(RadioDataProxy.class, message);
        if (radioDataProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(128, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " with data profiles : ");
        for (DataProfile dataProfile : dataProfileArr) {
            riljLog(dataProfile.toString());
        }
        try {
            radioDataProxy.setDataProfile(obtainRequest.mSerial, dataProfileArr, z);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(1, "setDataProfile", e);
        }
    }

    @Override // com.android.internal.telephony.BaseCommands, com.android.internal.telephony.CommandsInterface
    public void requestShutdown(Message message) {
        RadioModemProxy radioModemProxy = (RadioModemProxy) getRadioServiceProxy(RadioModemProxy.class, message);
        if (radioModemProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(129, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioModemProxy.requestShutdown(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(3, "requestShutdown", e);
        }
    }

    @Override // com.android.internal.telephony.BaseCommands, com.android.internal.telephony.CommandsInterface
    public void getRadioCapability(Message message) {
        RadioModemProxy radioModemProxy = (RadioModemProxy) getRadioServiceProxy(RadioModemProxy.class, message);
        if (radioModemProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(130, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioModemProxy.getRadioCapability(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(3, "getRadioCapability", e);
        }
    }

    @Override // com.android.internal.telephony.BaseCommands, com.android.internal.telephony.CommandsInterface
    public void setRadioCapability(RadioCapability radioCapability, Message message) {
        RadioModemProxy radioModemProxy = (RadioModemProxy) getRadioServiceProxy(RadioModemProxy.class, message);
        if (radioModemProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(131, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " RadioCapability = " + radioCapability.toString());
        try {
            radioModemProxy.setRadioCapability(obtainRequest.mSerial, radioCapability);
        } catch (Exception e) {
            handleRadioProxyExceptionForRR(3, "setRadioCapability", e);
        }
    }

    @Override // com.android.internal.telephony.BaseCommands, com.android.internal.telephony.CommandsInterface
    public void startLceService(int i, boolean z, Message message) {
        if (this.mHalVersion.get(0).greaterOrEqual(RADIO_HAL_VERSION_1_2)) {
            Rlog.d("RILJ", "startLceService: REQUEST_NOT_SUPPORTED");
            if (message != null) {
                AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
                message.sendToTarget();
                return;
            }
            return;
        }
        IRadio radioProxy = getRadioProxy(message);
        if (radioProxy != null) {
            RILRequest obtainRequest = obtainRequest(UiccCardApplication.AUTH_CONTEXT_GBA_BOOTSTRAP, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " reportIntervalMs = " + i + " pullMode = " + z);
            try {
                radioProxy.startLceService(obtainRequest.mSerial, i, z);
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(0, "startLceService", e);
            }
        }
    }

    @Override // com.android.internal.telephony.BaseCommands, com.android.internal.telephony.CommandsInterface
    public void stopLceService(Message message) {
        if (this.mHalVersion.get(0).greaterOrEqual(RADIO_HAL_VERSION_1_2)) {
            Rlog.d("RILJ", "stopLceService: REQUEST_NOT_SUPPORTED");
            if (message != null) {
                AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
                message.sendToTarget();
                return;
            }
            return;
        }
        IRadio radioProxy = getRadioProxy(message);
        if (radioProxy != null) {
            RILRequest obtainRequest = obtainRequest(133, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioProxy.stopLceService(obtainRequest.mSerial);
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(0, "stopLceService", e);
            }
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setDataThrottling(Message message, WorkSource workSource, int i, long j) {
        RadioDataProxy radioDataProxy = (RadioDataProxy) getRadioServiceProxy(RadioDataProxy.class, message);
        if (radioDataProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(1).greaterOrEqual(RADIO_HAL_VERSION_1_6)) {
            RILRequest obtainRequest = obtainRequest(NetworkStackConstants.VENDOR_SPECIFIC_IE_ID, message, getDefaultWorkSourceIfInvalid(workSource));
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " dataThrottlingAction = " + i + " completionWindowMillis " + j);
            try {
                radioDataProxy.setDataThrottling(obtainRequest.mSerial, (byte) i, j);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(1, "setDataThrottling", e);
                return;
            }
        }
        Rlog.d("RILJ", "setDataThrottling: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.BaseCommands, com.android.internal.telephony.CommandsInterface
    @Deprecated
    public void pullLceData(Message message) {
        if (this.mHalVersion.get(0).greaterOrEqual(RADIO_HAL_VERSION_1_2)) {
            Rlog.d("RILJ", "pullLceData: REQUEST_NOT_SUPPORTED");
            if (message != null) {
                AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
                message.sendToTarget();
                return;
            }
            return;
        }
        IRadio radioProxy = getRadioProxy(message);
        if (radioProxy != null) {
            RILRequest obtainRequest = obtainRequest(134, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioProxy.pullLceData(obtainRequest.mSerial);
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(0, "pullLceData", e);
            }
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getModemActivityInfo(Message message, WorkSource workSource) {
        RadioModemProxy radioModemProxy = (RadioModemProxy) getRadioServiceProxy(RadioModemProxy.class, message);
        if (radioModemProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(NetworkStackConstants.ICMPV6_NEIGHBOR_SOLICITATION, message, getDefaultWorkSourceIfInvalid(workSource));
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioModemProxy.getModemActivityInfo(obtainRequest.mSerial);
            this.mRilHandler.sendMessageDelayed(this.mRilHandler.obtainMessage(5, Integer.valueOf(obtainRequest.mSerial)), 2000L);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(3, "getModemActivityInfo", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setAllowedCarriers(CarrierRestrictionRules carrierRestrictionRules, Message message, WorkSource workSource) {
        Objects.requireNonNull(carrierRestrictionRules, "Carrier restriction cannot be null.");
        RadioSimProxy radioSimProxy = (RadioSimProxy) getRadioServiceProxy(RadioSimProxy.class, message);
        if (radioSimProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(NetworkStackConstants.ICMPV6_NEIGHBOR_ADVERTISEMENT, message, getDefaultWorkSourceIfInvalid(workSource));
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " params: " + carrierRestrictionRules);
        try {
            radioSimProxy.setAllowedCarriers(obtainRequest.mSerial, carrierRestrictionRules, message);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(5, "setAllowedCarriers", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getAllowedCarriers(Message message, WorkSource workSource) {
        RadioSimProxy radioSimProxy = (RadioSimProxy) getRadioServiceProxy(RadioSimProxy.class, message);
        if (radioSimProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(137, message, getDefaultWorkSourceIfInvalid(workSource));
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioSimProxy.getAllowedCarriers(obtainRequest.mSerial);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(5, "getAllowedCarriers", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void sendDeviceState(int i, boolean z, Message message) {
        RadioModemProxy radioModemProxy = (RadioModemProxy) getRadioServiceProxy(RadioModemProxy.class, message);
        if (radioModemProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(138, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " " + i + ":" + z);
        try {
            radioModemProxy.sendDeviceState(obtainRequest.mSerial, i, z);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(3, "sendDeviceState", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setUnsolResponseFilter(int i, Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(139, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " " + i);
        try {
            radioNetworkProxy.setIndicationFilter(obtainRequest.mSerial, i);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(4, "setIndicationFilter", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setSignalStrengthReportingCriteria(List<SignalThresholdInfo> list, Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(4).greaterOrEqual(RADIO_HAL_VERSION_1_2)) {
            RILRequest obtainRequest = obtainRequest(202, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioNetworkProxy.setSignalStrengthReportingCriteria(obtainRequest.mSerial, list);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(4, "setSignalStrengthReportingCriteria", e);
                return;
            }
        }
        riljLoge("setSignalStrengthReportingCriteria ignored on IRadio version less than 1.2");
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setLinkCapacityReportingCriteria(int i, int i2, int i3, int[] iArr, int[] iArr2, int i4, Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(4).greaterOrEqual(RADIO_HAL_VERSION_1_2)) {
            RILRequest obtainRequest = obtainRequest(203, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioNetworkProxy.setLinkCapacityReportingCriteria(obtainRequest.mSerial, i, i2, i3, iArr, iArr2, i4);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(4, "setLinkCapacityReportingCriteria", e);
                return;
            }
        }
        riljLoge("setLinkCapacityReportingCriteria ignored on IRadio version less than 1.2");
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setSimCardPower(int i, Message message, WorkSource workSource) {
        RadioSimProxy radioSimProxy = (RadioSimProxy) getRadioServiceProxy(RadioSimProxy.class, message);
        if (radioSimProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(SmsMessage.MAX_USER_DATA_BYTES, message, getDefaultWorkSourceIfInvalid(workSource));
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " " + i);
        try {
            radioSimProxy.setSimCardPower(obtainRequest.mSerial, i, message);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(5, "setSimCardPower", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setCarrierInfoForImsiEncryption(ImsiEncryptionInfo imsiEncryptionInfo, Message message) {
        Objects.requireNonNull(imsiEncryptionInfo, "ImsiEncryptionInfo cannot be null.");
        RadioSimProxy radioSimProxy = (RadioSimProxy) getRadioServiceProxy(RadioSimProxy.class, message);
        if (radioSimProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(5).greaterOrEqual(RADIO_HAL_VERSION_1_1)) {
            RILRequest obtainRequest = obtainRequest(141, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioSimProxy.setCarrierInfoForImsiEncryption(obtainRequest.mSerial, imsiEncryptionInfo);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(5, "setCarrierInfoForImsiEncryption", e);
                return;
            }
        }
        Rlog.d("RILJ", "setCarrierInfoForImsiEncryption: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void startNattKeepalive(int i, KeepalivePacketData keepalivePacketData, int i2, Message message) {
        Objects.requireNonNull(keepalivePacketData, "KeepaliveRequest cannot be null.");
        RadioDataProxy radioDataProxy = (RadioDataProxy) getRadioServiceProxy(RadioDataProxy.class, message);
        if (radioDataProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(1).greaterOrEqual(RADIO_HAL_VERSION_1_1)) {
            RILRequest obtainRequest = obtainRequest(144, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioDataProxy.startKeepalive(obtainRequest.mSerial, i, keepalivePacketData, i2, message);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(1, "startNattKeepalive", e);
                return;
            }
        }
        Rlog.d("RILJ", "startNattKeepalive: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void stopNattKeepalive(int i, Message message) {
        RadioDataProxy radioDataProxy = (RadioDataProxy) getRadioServiceProxy(RadioDataProxy.class, message);
        if (radioDataProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(1).greaterOrEqual(RADIO_HAL_VERSION_1_1)) {
            RILRequest obtainRequest = obtainRequest(145, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioDataProxy.stopKeepalive(obtainRequest.mSerial, i);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(1, "stopNattKeepalive", e);
                return;
            }
        }
        Rlog.d("RILJ", "stopNattKeepalive: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getIMEI(Message message) {
        throw new RuntimeException("getIMEI not expected to be called");
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getIMEISV(Message message) {
        throw new RuntimeException("getIMEISV not expected to be called");
    }

    @Override // com.android.internal.telephony.CommandsInterface
    @Deprecated
    public void getLastPdpFailCause(Message message) {
        throw new RuntimeException("getLastPdpFailCause not expected to be called");
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getLastDataCallFailCause(Message message) {
        throw new RuntimeException("getLastDataCallFailCause not expected to be called");
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void enableUiccApplications(boolean z, Message message) {
        RadioSimProxy radioSimProxy = (RadioSimProxy) getRadioServiceProxy(RadioSimProxy.class, message);
        if (radioSimProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(5).greaterOrEqual(RADIO_HAL_VERSION_1_5)) {
            RILRequest obtainRequest = obtainRequest(BerTlv.BER_PROACTIVE_COMMAND_TAG, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " " + z);
            try {
                radioSimProxy.enableUiccApplications(obtainRequest.mSerial, z);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(5, "enableUiccApplications", e);
                return;
            }
        }
        Rlog.d("RILJ", "enableUiccApplications: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void areUiccApplicationsEnabled(Message message) {
        RadioSimProxy radioSimProxy = (RadioSimProxy) getRadioServiceProxy(RadioSimProxy.class, message);
        if (radioSimProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(5).greaterOrEqual(RADIO_HAL_VERSION_1_5)) {
            RILRequest obtainRequest = obtainRequest(209, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioSimProxy.areUiccApplicationsEnabled(obtainRequest.mSerial);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(5, "areUiccApplicationsEnabled", e);
                return;
            }
        }
        Rlog.d("RILJ", "areUiccApplicationsEnabled: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public boolean canToggleUiccApplicationsEnablement() {
        return !((RadioSimProxy) getRadioServiceProxy(RadioSimProxy.class, (Message) null)).isEmpty() && this.mHalVersion.get(5).greaterOrEqual(RADIO_HAL_VERSION_1_5);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void resetRadio(Message message) {
        throw new RuntimeException("resetRadio not expected to be called");
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void handleCallSetupRequestFromSim(boolean z, Message message) {
        RadioVoiceProxy radioVoiceProxy = (RadioVoiceProxy) getRadioServiceProxy(RadioVoiceProxy.class, message);
        if (radioVoiceProxy.isEmpty()) {
            return;
        }
        RILRequest obtainRequest = obtainRequest(71, message, this.mRILDefaultWorkSource);
        riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
        try {
            radioVoiceProxy.handleStkCallSetupRequestFromSim(obtainRequest.mSerial, z);
        } catch (RemoteException | RuntimeException e) {
            handleRadioProxyExceptionForRR(6, "handleStkCallSetupRequestFromSim", e);
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getBarringInfo(Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(4).greaterOrEqual(RADIO_HAL_VERSION_1_5)) {
            RILRequest obtainRequest = obtainRequest(211, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioNetworkProxy.getBarringInfo(obtainRequest.mSerial);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(4, "getBarringInfo", e);
                return;
            }
        }
        Rlog.d("RILJ", "getBarringInfo: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void allocatePduSessionId(Message message) {
        RadioDataProxy radioDataProxy = (RadioDataProxy) getRadioServiceProxy(RadioDataProxy.class, message);
        if (radioDataProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(1).greaterOrEqual(RADIO_HAL_VERSION_1_6)) {
            RILRequest obtainRequest = obtainRequest(215, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioDataProxy.allocatePduSessionId(obtainRequest.mSerial);
                return;
            } catch (RemoteException e) {
                handleRadioProxyExceptionForRR(1, "allocatePduSessionId", e);
                return;
            }
        }
        AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
        message.sendToTarget();
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void releasePduSessionId(Message message, int i) {
        RadioDataProxy radioDataProxy = (RadioDataProxy) getRadioServiceProxy(RadioDataProxy.class, message);
        if (radioDataProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(1).greaterOrEqual(RADIO_HAL_VERSION_1_6)) {
            RILRequest obtainRequest = obtainRequest(216, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioDataProxy.releasePduSessionId(obtainRequest.mSerial, i);
                return;
            } catch (RemoteException e) {
                handleRadioProxyExceptionForRR(1, "releasePduSessionId", e);
                return;
            }
        }
        AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
        message.sendToTarget();
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void startHandover(Message message, int i) {
        RadioDataProxy radioDataProxy = (RadioDataProxy) getRadioServiceProxy(RadioDataProxy.class, message);
        if (radioDataProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(1).greaterOrEqual(RADIO_HAL_VERSION_1_6)) {
            RILRequest obtainRequest = obtainRequest(217, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioDataProxy.startHandover(obtainRequest.mSerial, i);
                return;
            } catch (RemoteException e) {
                handleRadioProxyExceptionForRR(1, "startHandover", e);
                return;
            }
        }
        Rlog.d("RILJ", "startHandover: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void cancelHandover(Message message, int i) {
        RadioDataProxy radioDataProxy = (RadioDataProxy) getRadioServiceProxy(RadioDataProxy.class, message);
        if (radioDataProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(1).greaterOrEqual(RADIO_HAL_VERSION_1_6)) {
            RILRequest obtainRequest = obtainRequest(218, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioDataProxy.cancelHandover(obtainRequest.mSerial, i);
                return;
            } catch (RemoteException e) {
                handleRadioProxyExceptionForRR(1, "cancelHandover", e);
                return;
            }
        }
        Rlog.d("RILJ", "cancelHandover: REQUEST_NOT_SUPPORTED");
        AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
        message.sendToTarget();
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getSlicingConfig(Message message) {
        RadioDataProxy radioDataProxy = (RadioDataProxy) getRadioServiceProxy(RadioDataProxy.class, message);
        if (radioDataProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(1).greaterOrEqual(RADIO_HAL_VERSION_1_6)) {
            RILRequest obtainRequest = obtainRequest(224, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioDataProxy.getSlicingConfig(obtainRequest.mSerial);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(1, "getSlicingConfig", e);
                return;
            }
        }
        Rlog.d("RILJ", "getSlicingConfig: REQUEST_NOT_SUPPORTED");
        AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
        message.sendToTarget();
    }

    @Override // com.android.internal.telephony.BaseCommands, com.android.internal.telephony.CommandsInterface
    public void getSimPhonebookRecords(Message message) {
        RadioSimProxy radioSimProxy = (RadioSimProxy) getRadioServiceProxy(RadioSimProxy.class, message);
        if (radioSimProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(5).greaterOrEqual(RADIO_HAL_VERSION_1_6)) {
            RILRequest obtainRequest = obtainRequest(150, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioSimProxy.getSimPhonebookRecords(obtainRequest.mSerial);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(5, "getSimPhonebookRecords", e);
                return;
            }
        }
        Rlog.d("RILJ", "getSimPhonebookRecords: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.BaseCommands, com.android.internal.telephony.CommandsInterface
    public void getSimPhonebookCapacity(Message message) {
        RadioSimProxy radioSimProxy = (RadioSimProxy) getRadioServiceProxy(RadioSimProxy.class, message);
        if (radioSimProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(5).greaterOrEqual(RADIO_HAL_VERSION_1_6)) {
            RILRequest obtainRequest = obtainRequest(149, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioSimProxy.getSimPhonebookCapacity(obtainRequest.mSerial);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(5, "getSimPhonebookCapacity", e);
                return;
            }
        }
        Rlog.d("RILJ", "getSimPhonebookCapacity: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.BaseCommands, com.android.internal.telephony.CommandsInterface
    public void updateSimPhonebookRecord(SimPhonebookRecord simPhonebookRecord, Message message) {
        RadioSimProxy radioSimProxy = (RadioSimProxy) getRadioServiceProxy(RadioSimProxy.class, message);
        if (radioSimProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(5).greaterOrEqual(RADIO_HAL_VERSION_1_6)) {
            RILRequest obtainRequest = obtainRequest(151, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " with " + simPhonebookRecord.toString());
            try {
                radioSimProxy.updateSimPhonebookRecords(obtainRequest.mSerial, simPhonebookRecord);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(5, "updateSimPhonebookRecords", e);
                return;
            }
        }
        Rlog.d("RILJ", "updateSimPhonebookRecords: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setUsageSetting(Message message, int i) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(4).greaterOrEqual(RADIO_HAL_VERSION_2_0)) {
            RILRequest obtainRequest = obtainRequest(227, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioNetworkProxy.setUsageSetting(obtainRequest.mSerial, i);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(4, "setUsageSetting", e);
                return;
            }
        }
        Rlog.d("RILJ", "setUsageSetting: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getUsageSetting(Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(4).greaterOrEqual(RADIO_HAL_VERSION_2_0)) {
            RILRequest obtainRequest = obtainRequest(228, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioNetworkProxy.getUsageSetting(obtainRequest.mSerial);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(4, "getUsageSetting", e);
                return;
            }
        }
        Rlog.d("RILJ", "getUsageSetting: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setSrvccCallInfo(SrvccConnection[] srvccConnectionArr, Message message) {
        RadioImsProxy radioImsProxy = (RadioImsProxy) getRadioServiceProxy(RadioImsProxy.class, message);
        if (radioImsProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(7).greaterOrEqual(RADIO_HAL_VERSION_2_0)) {
            RILRequest obtainRequest = obtainRequest(233, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioImsProxy.setSrvccCallInfo(obtainRequest.mSerial, RILUtils.convertToHalSrvccCall(srvccConnectionArr));
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(7, "setSrvccCallInfo", e);
                return;
            }
        }
        Rlog.d("RILJ", "setSrvccCallInfo: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void updateImsRegistrationInfo(int i, int i2, int i3, int i4, Message message) {
        RadioImsProxy radioImsProxy = (RadioImsProxy) getRadioServiceProxy(RadioImsProxy.class, message);
        if (radioImsProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(7).greaterOrEqual(RADIO_HAL_VERSION_2_0)) {
            RILRequest obtainRequest = obtainRequest(234, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " state=" + i + ", radioTech=" + i2 + ", suggested=" + i3 + ", cap=" + i4);
            ImsRegistration imsRegistration = new ImsRegistration();
            imsRegistration.regState = RILUtils.convertImsRegistrationState(i);
            imsRegistration.accessNetworkType = RILUtils.convertImsRegistrationTech(i2);
            imsRegistration.suggestedAction = i3;
            imsRegistration.capabilities = RILUtils.convertImsCapability(i4);
            try {
                radioImsProxy.updateImsRegistrationInfo(obtainRequest.mSerial, imsRegistration);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(7, "updateImsRegistrationInfo", e);
                return;
            }
        }
        Rlog.d("RILJ", "updateImsRegistrationInfo: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void startImsTraffic(int i, int i2, int i3, int i4, Message message) {
        RadioImsProxy radioImsProxy = (RadioImsProxy) getRadioServiceProxy(RadioImsProxy.class, message);
        if (radioImsProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(7).greaterOrEqual(RADIO_HAL_VERSION_2_0)) {
            RILRequest obtainRequest = obtainRequest(235, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + "{" + i + ", " + i2 + ", " + i3 + ", " + i4 + "}");
            try {
                radioImsProxy.startImsTraffic(obtainRequest.mSerial, i, RILUtils.convertImsTrafficType(i2), i3, RILUtils.convertImsTrafficDirection(i4));
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(7, "startImsTraffic", e);
                return;
            }
        }
        Rlog.d("RILJ", "startImsTraffic: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void stopImsTraffic(int i, Message message) {
        RadioImsProxy radioImsProxy = (RadioImsProxy) getRadioServiceProxy(RadioImsProxy.class, message);
        if (radioImsProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(7).greaterOrEqual(RADIO_HAL_VERSION_2_0)) {
            RILRequest obtainRequest = obtainRequest(236, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + "{" + i + "}");
            try {
                radioImsProxy.stopImsTraffic(obtainRequest.mSerial, i);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(7, "stopImsTraffic", e);
                return;
            }
        }
        Rlog.d("RILJ", "stopImsTraffic: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void triggerEpsFallback(int i, Message message) {
        RadioImsProxy radioImsProxy = (RadioImsProxy) getRadioServiceProxy(RadioImsProxy.class, message);
        if (radioImsProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(7).greaterOrEqual(RADIO_HAL_VERSION_2_0)) {
            RILRequest obtainRequest = obtainRequest(238, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " reason=" + i);
            try {
                radioImsProxy.triggerEpsFallback(obtainRequest.mSerial, i);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(7, "triggerEpsFallback", e);
                return;
            }
        }
        Rlog.d("RILJ", "triggerEpsFallback: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void sendAnbrQuery(int i, int i2, int i3, Message message) {
        RadioImsProxy radioImsProxy = (RadioImsProxy) getRadioServiceProxy(RadioImsProxy.class, message);
        if (radioImsProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(7).greaterOrEqual(RADIO_HAL_VERSION_2_0)) {
            RILRequest obtainRequest = obtainRequest(237, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioImsProxy.sendAnbrQuery(obtainRequest.mSerial, i, i2, i3);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(7, "sendAnbrQuery", e);
                return;
            }
        }
        Rlog.d("RILJ", "sendAnbrQuery: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setEmergencyMode(int i, Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(4).greaterOrEqual(RADIO_HAL_VERSION_2_1)) {
            RILRequest obtainRequest = obtainRequest(229, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " mode=" + EmergencyConstants.emergencyModeToString(i));
            try {
                radioNetworkProxy.setEmergencyMode(obtainRequest.mSerial, i);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(4, "setEmergencyMode", e);
                return;
            }
        }
        Rlog.d("RILJ", "setEmergencyMode: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void triggerEmergencyNetworkScan(int[] iArr, int i, Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(4).greaterOrEqual(RADIO_HAL_VERSION_2_1)) {
            RILRequest obtainRequest = obtainRequest(230, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " networkType=" + RILUtils.accessNetworkTypesToString(iArr) + ", scanType=" + RILUtils.scanTypeToString(i));
            try {
                radioNetworkProxy.triggerEmergencyNetworkScan(obtainRequest.mSerial, RILUtils.convertEmergencyNetworkScanTrigger(iArr, i));
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(4, "triggerEmergencyNetworkScan", e);
                return;
            }
        }
        Rlog.d("RILJ", "triggerEmergencyNetworkScan: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void cancelEmergencyNetworkScan(boolean z, Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(4).greaterOrEqual(RADIO_HAL_VERSION_2_1)) {
            RILRequest obtainRequest = obtainRequest(231, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " resetScan=" + z);
            try {
                radioNetworkProxy.cancelEmergencyNetworkScan(obtainRequest.mSerial, z);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(4, "cancelEmergencyNetworkScan", e);
                return;
            }
        }
        Rlog.d("RILJ", "cancelEmergencyNetworkScan: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void exitEmergencyMode(Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(4).greaterOrEqual(RADIO_HAL_VERSION_2_1)) {
            RILRequest obtainRequest = obtainRequest(232, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioNetworkProxy.exitEmergencyMode(obtainRequest.mSerial);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(4, "exitEmergencyMode", e);
                return;
            }
        }
        Rlog.d("RILJ", "exitEmergencyMode: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setNullCipherAndIntegrityEnabled(boolean z, Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(4).greaterOrEqual(RADIO_HAL_VERSION_2_1)) {
            RILRequest obtainRequest = obtainRequest(239, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioNetworkProxy.setNullCipherAndIntegrityEnabled(obtainRequest.mSerial, z);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(4, "setNullCipherAndIntegrityEnabled", e);
                return;
            }
        }
        Rlog.d("RILJ", "setNullCipherAndIntegrityEnabled: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void updateImsCallStatus(List<ImsCallInfo> list, Message message) {
        RadioImsProxy radioImsProxy = (RadioImsProxy) getRadioServiceProxy(RadioImsProxy.class, message);
        if (radioImsProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(7).greaterOrEqual(RADIO_HAL_VERSION_2_0)) {
            RILRequest obtainRequest = obtainRequest(240, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " " + list);
            try {
                radioImsProxy.updateImsCallStatus(obtainRequest.mSerial, RILUtils.convertImsCallInfo(list));
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(7, "updateImsCallStatus", e);
                return;
            }
        }
        Rlog.d("RILJ", "updateImsCallStatus: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setN1ModeEnabled(boolean z, Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(4).greaterOrEqual(RADIO_HAL_VERSION_2_1)) {
            RILRequest obtainRequest = obtainRequest(CallFailCause.FDN_BLOCKED, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest) + " enable=" + z);
            try {
                radioNetworkProxy.setN1ModeEnabled(obtainRequest.mSerial, z);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(4, "setN1ModeEnabled", e);
                return;
            }
        }
        Rlog.d("RILJ", "setN1ModeEnabled: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void isN1ModeEnabled(Message message) {
        RadioNetworkProxy radioNetworkProxy = (RadioNetworkProxy) getRadioServiceProxy(RadioNetworkProxy.class, message);
        if (radioNetworkProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(4).greaterOrEqual(RADIO_HAL_VERSION_2_1)) {
            RILRequest obtainRequest = obtainRequest(242, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioNetworkProxy.isN1ModeEnabled(obtainRequest.mSerial);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(4, "isN1ModeEnabled", e);
                return;
            }
        }
        Rlog.d("RILJ", "isN1ModeEnabled: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getSatelliteCapabilities(Message message) {
        RadioSatelliteProxy radioSatelliteProxy = (RadioSatelliteProxy) getRadioServiceProxy(RadioSatelliteProxy.class, message);
        if (radioSatelliteProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(8).greaterOrEqual(RADIO_HAL_VERSION_2_0)) {
            RILRequest obtainRequest = obtainRequest(CallFailCause.DIAL_MODIFIED_TO_SS, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioSatelliteProxy.getCapabilities(obtainRequest.mSerial);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(8, "getSatelliteCapabilities", e);
                return;
            }
        }
        Rlog.d("RILJ", "getSatelliteCapabilities: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setSatellitePower(Message message, boolean z) {
        RadioSatelliteProxy radioSatelliteProxy = (RadioSatelliteProxy) getRadioServiceProxy(RadioSatelliteProxy.class, message);
        if (radioSatelliteProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(8).greaterOrEqual(RADIO_HAL_VERSION_2_0)) {
            RILRequest obtainRequest = obtainRequest(CallFailCause.DIAL_MODIFIED_TO_DIAL, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioSatelliteProxy.setPower(obtainRequest.mSerial, z);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(8, "setSatellitePower", e);
                return;
            }
        }
        Rlog.d("RILJ", "setSatellitePower: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getSatellitePowerState(Message message) {
        RadioSatelliteProxy radioSatelliteProxy = (RadioSatelliteProxy) getRadioServiceProxy(RadioSatelliteProxy.class, message);
        if (radioSatelliteProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(8).greaterOrEqual(RADIO_HAL_VERSION_2_0)) {
            RILRequest obtainRequest = obtainRequest(CallFailCause.RADIO_OFF, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioSatelliteProxy.getPowerState(obtainRequest.mSerial);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(8, "getSatellitePowerState", e);
                return;
            }
        }
        Rlog.d("RILJ", "getSatellitePowerState: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void provisionSatelliteService(Message message, String str, String str2, String str3, int[] iArr) {
        RadioSatelliteProxy radioSatelliteProxy = (RadioSatelliteProxy) getRadioServiceProxy(RadioSatelliteProxy.class, message);
        if (radioSatelliteProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(8).greaterOrEqual(RADIO_HAL_VERSION_2_0)) {
            RILRequest obtainRequest = obtainRequest(248, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioSatelliteProxy.provisionService(obtainRequest.mSerial, str, str2, str3, iArr);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(8, "provisionSatelliteService", e);
                return;
            }
        }
        Rlog.d("RILJ", "provisionSatelliteService: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void addAllowedSatelliteContacts(Message message, String[] strArr) {
        RadioSatelliteProxy radioSatelliteProxy = (RadioSatelliteProxy) getRadioServiceProxy(RadioSatelliteProxy.class, message);
        if (radioSatelliteProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(8).greaterOrEqual(RADIO_HAL_VERSION_2_0)) {
            RILRequest obtainRequest = obtainRequest(CallFailCause.NO_VALID_SIM, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioSatelliteProxy.addAllowedSatelliteContacts(obtainRequest.mSerial, strArr);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(8, "addAllowedSatelliteContacts", e);
                return;
            }
        }
        Rlog.d("RILJ", "addAllowedSatelliteContacts: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void removeAllowedSatelliteContacts(Message message, String[] strArr) {
        RadioSatelliteProxy radioSatelliteProxy = (RadioSatelliteProxy) getRadioServiceProxy(RadioSatelliteProxy.class, message);
        if (radioSatelliteProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(8).greaterOrEqual(RADIO_HAL_VERSION_2_0)) {
            RILRequest obtainRequest = obtainRequest(CallFailCause.RADIO_INTERNAL_ERROR, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioSatelliteProxy.removeAllowedSatelliteContacts(obtainRequest.mSerial, strArr);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(8, "removeAllowedSatelliteContacts", e);
                return;
            }
        }
        Rlog.d("RILJ", "removeAllowedSatelliteContacts: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void sendSatelliteMessages(Message message, String[] strArr, String str, double d, double d2) {
        RadioSatelliteProxy radioSatelliteProxy = (RadioSatelliteProxy) getRadioServiceProxy(RadioSatelliteProxy.class, message);
        if (radioSatelliteProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(8).greaterOrEqual(RADIO_HAL_VERSION_2_0)) {
            RILRequest obtainRequest = obtainRequest(CallFailCause.NETWORK_RESP_TIMEOUT, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioSatelliteProxy.sendMessages(obtainRequest.mSerial, strArr, str, d, d2);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(8, "sendSatelliteMessages", e);
                return;
            }
        }
        Rlog.d("RILJ", "sendSatelliteMessages: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getPendingSatelliteMessages(Message message) {
        RadioSatelliteProxy radioSatelliteProxy = (RadioSatelliteProxy) getRadioServiceProxy(RadioSatelliteProxy.class, message);
        if (radioSatelliteProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(8).greaterOrEqual(RADIO_HAL_VERSION_2_0)) {
            RILRequest obtainRequest = obtainRequest(CallFailCause.NETWORK_REJECT, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioSatelliteProxy.getPendingMessages(obtainRequest.mSerial);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(8, "getPendingSatelliteMessages", e);
                return;
            }
        }
        Rlog.d("RILJ", "getPendingSatelliteMessages: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getSatelliteMode(Message message) {
        RadioSatelliteProxy radioSatelliteProxy = (RadioSatelliteProxy) getRadioServiceProxy(RadioSatelliteProxy.class, message);
        if (radioSatelliteProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(8).greaterOrEqual(RADIO_HAL_VERSION_2_0)) {
            RILRequest obtainRequest = obtainRequest(CallFailCause.RADIO_ACCESS_FAILURE, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioSatelliteProxy.getSatelliteMode(obtainRequest.mSerial);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(8, "getSatelliteMode", e);
                return;
            }
        }
        Rlog.d("RILJ", "getSatelliteMode: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setSatelliteIndicationFilter(Message message, int i) {
        RadioSatelliteProxy radioSatelliteProxy = (RadioSatelliteProxy) getRadioServiceProxy(RadioSatelliteProxy.class, message);
        if (radioSatelliteProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(8).greaterOrEqual(RADIO_HAL_VERSION_2_0)) {
            RILRequest obtainRequest = obtainRequest(CallFailCause.RADIO_LINK_FAILURE, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioSatelliteProxy.setIndicationFilter(obtainRequest.mSerial, i);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(8, "setSatelliteIndicationFilter", e);
                return;
            }
        }
        Rlog.d("RILJ", "setSatelliteIndicationFilter: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void isSatelliteSupported(Message message) {
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void startSendingSatellitePointingInfo(Message message) {
        RadioSatelliteProxy radioSatelliteProxy = (RadioSatelliteProxy) getRadioServiceProxy(RadioSatelliteProxy.class, message);
        if (radioSatelliteProxy.isEmpty()) {
            Rlog.d("RILJ", "startSendingSatellitePointingInfo: RADIO_NOT_AVAILABLE");
            if (message != null) {
                AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(1));
                message.sendToTarget();
            }
        }
        if (this.mHalVersion.get(8).greaterOrEqual(RADIO_HAL_VERSION_2_0)) {
            RILRequest obtainRequest = obtainRequest(255, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioSatelliteProxy.startSendingSatellitePointingInfo(obtainRequest.mSerial);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(8, "startSendingSatellitePointingInfo", e);
                return;
            }
        }
        Rlog.d("RILJ", "startSendingSatellitePointingInfo: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void stopSendingSatellitePointingInfo(Message message) {
        RadioSatelliteProxy radioSatelliteProxy = (RadioSatelliteProxy) getRadioServiceProxy(RadioSatelliteProxy.class, message);
        if (radioSatelliteProxy.isEmpty()) {
            Rlog.d("RILJ", "startSendingSatellitePointingInfo: RADIO_NOT_AVAILABLE");
            if (message != null) {
                AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(1));
                message.sendToTarget();
            }
        }
        if (this.mHalVersion.get(8).greaterOrEqual(RADIO_HAL_VERSION_2_0)) {
            RILRequest obtainRequest = obtainRequest(CallFailCause.RADIO_UPLINK_FAILURE, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioSatelliteProxy.stopSendingSatellitePointingInfo(obtainRequest.mSerial);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(8, "stopSendingSatellitePointingInfo", e);
                return;
            }
        }
        Rlog.d("RILJ", "stopSendingSatellitePointingInfo: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getMaxCharactersPerSatelliteTextMessage(Message message) {
        RadioSatelliteProxy radioSatelliteProxy = (RadioSatelliteProxy) getRadioServiceProxy(RadioSatelliteProxy.class, message);
        if (radioSatelliteProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(8).greaterOrEqual(RADIO_HAL_VERSION_2_0)) {
            RILRequest obtainRequest = obtainRequest(CallFailCause.RADIO_SETUP_FAILURE, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioSatelliteProxy.getMaxCharactersPerTextMessage(obtainRequest.mSerial);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(8, "getMaxCharactersPerSatelliteTextMessage", e);
                return;
            }
        }
        Rlog.d("RILJ", "getMaxCharactersPerSatelliteTextMessage: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void isSatelliteCommunicationAllowedForCurrentLocation(Message message) {
        Rlog.d("RILJ", "stopSendingSatellitePointingInfo: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void getTimeForNextSatelliteVisibility(Message message) {
        RadioSatelliteProxy radioSatelliteProxy = (RadioSatelliteProxy) getRadioServiceProxy(RadioSatelliteProxy.class, message);
        if (radioSatelliteProxy.isEmpty()) {
            return;
        }
        if (this.mHalVersion.get(8).greaterOrEqual(RADIO_HAL_VERSION_2_0)) {
            RILRequest obtainRequest = obtainRequest(CallFailCause.RADIO_RELEASE_NORMAL, message, this.mRILDefaultWorkSource);
            riljLog(obtainRequest.serialString() + "> " + RILUtils.requestToString(obtainRequest.mRequest));
            try {
                radioSatelliteProxy.getTimeForNextSatelliteVisibility(obtainRequest.mSerial);
                return;
            } catch (RemoteException | RuntimeException e) {
                handleRadioProxyExceptionForRR(8, "getTimeForNextSatelliteVisibility", e);
                return;
            }
        }
        Rlog.d("RILJ", "getTimeForNextSatelliteVisibility: REQUEST_NOT_SUPPORTED");
        if (message != null) {
            AsyncResult.forMessage(message, (Object) null, CommandException.fromRilErrno(6));
            message.sendToTarget();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void processIndication(int i, int i2) {
        if (i2 == 1) {
            sendAck(i);
            riljLog("Unsol response received; Sending ack to ril.cpp");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void processRequestAck(int i) {
        RILRequest rILRequest;
        synchronized (this.mRequestList) {
            rILRequest = this.mRequestList.get(i);
        }
        if (rILRequest == null) {
            Rlog.w("RILJ", "processRequestAck: Unexpected solicited ack response! serial: " + i);
            return;
        }
        decrementWakeLock(rILRequest);
        riljLog(rILRequest.serialString() + " Ack < " + RILUtils.requestToString(rILRequest.mRequest));
    }

    @VisibleForTesting
    public RILRequest processResponse(RadioResponseInfo radioResponseInfo) {
        return processResponseInternal(0, radioResponseInfo.serial, radioResponseInfo.error, radioResponseInfo.type);
    }

    @VisibleForTesting
    public RILRequest processResponse_1_6(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo) {
        return processResponseInternal(0, radioResponseInfo.serial, radioResponseInfo.error, radioResponseInfo.type);
    }

    public RILRequest processResponse(int i, android.hardware.radio.RadioResponseInfo radioResponseInfo) {
        return processResponseInternal(i, radioResponseInfo.serial, radioResponseInfo.error, radioResponseInfo.type);
    }

    private RILRequest processResponseInternal(int i, int i2, int i3, int i4) {
        RILRequest rILRequest;
        if (i4 == 1) {
            synchronized (this.mRequestList) {
                rILRequest = this.mRequestList.get(i2);
            }
            if (rILRequest == null) {
                Rlog.w("RILJ", "Unexpected solicited ack response! sn: " + i2);
            } else {
                decrementWakeLock(rILRequest);
                RadioBugDetector radioBugDetector = this.mRadioBugDetector;
                if (radioBugDetector != null) {
                    radioBugDetector.detectRadioBug(rILRequest.mRequest, i3);
                }
                riljLog(rILRequest.serialString() + " Ack from " + serviceToString(i) + " < " + RILUtils.requestToString(rILRequest.mRequest));
            }
            return rILRequest;
        }
        RILRequest findAndRemoveRequestFromList = findAndRemoveRequestFromList(i2);
        if (findAndRemoveRequestFromList == null) {
            Rlog.e("RILJ", "processResponse: Unexpected response! serial: " + i2 + " ,error: " + i3);
            return null;
        }
        Trace.asyncTraceForTrackEnd(2097152L, "RIL", findAndRemoveRequestFromList.mSerial);
        addToRilHistogram(findAndRemoveRequestFromList);
        RadioBugDetector radioBugDetector2 = this.mRadioBugDetector;
        if (radioBugDetector2 != null) {
            radioBugDetector2.detectRadioBug(findAndRemoveRequestFromList.mRequest, i3);
        }
        if (i4 == 2) {
            sendAck(i);
            riljLog("Response received from " + serviceToString(i) + " for " + findAndRemoveRequestFromList.serialString() + " " + RILUtils.requestToString(findAndRemoveRequestFromList.mRequest) + " Sending ack to ril.cpp");
        }
        int i5 = findAndRemoveRequestFromList.mRequest;
        if (i5 == 3 || i5 == 5) {
            if (this.mIccStatusChangedRegistrants != null) {
                riljLog("ON enter sim puk fakeSimStatusChanged: reg count=" + this.mIccStatusChangedRegistrants.size());
                this.mIccStatusChangedRegistrants.notifyRegistrants();
            }
        } else if (i5 == 129) {
            setRadioState(2, false);
        }
        if (i3 != 0) {
            int i6 = findAndRemoveRequestFromList.mRequest;
            if ((i6 == 2 || i6 == 4 || i6 == 43 || i6 == 6 || i6 == 7) && this.mIccStatusChangedRegistrants != null) {
                riljLog("ON some errors fakeSimStatusChanged: reg count=" + this.mIccStatusChangedRegistrants.size());
                this.mIccStatusChangedRegistrants.notifyRegistrants();
            }
        } else if (findAndRemoveRequestFromList.mRequest == 14 && this.mTestingEmergencyCall.getAndSet(false) && this.mEmergencyCallbackModeRegistrant != null) {
            riljLog("testing emergency call, notify ECM Registrants");
            this.mEmergencyCallbackModeRegistrant.notifyRegistrant();
        }
        return findAndRemoveRequestFromList;
    }

    @VisibleForTesting
    public void processResponseDone(RILRequest rILRequest, RadioResponseInfo radioResponseInfo, Object obj) {
        processResponseDoneInternal(rILRequest, radioResponseInfo.error, radioResponseInfo.type, obj);
    }

    @VisibleForTesting
    public void processResponseDone_1_6(RILRequest rILRequest, android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo, Object obj) {
        processResponseDoneInternal(rILRequest, radioResponseInfo.error, radioResponseInfo.type, obj);
    }

    @VisibleForTesting
    public void processResponseDone(RILRequest rILRequest, android.hardware.radio.RadioResponseInfo radioResponseInfo, Object obj) {
        processResponseDoneInternal(rILRequest, radioResponseInfo.error, radioResponseInfo.type, obj);
    }

    private void processResponseDoneInternal(RILRequest rILRequest, int i, int i2, Object obj) {
        if (i == 0) {
            riljLog(rILRequest.serialString() + "< " + RILUtils.requestToString(rILRequest.mRequest) + " " + retToString(rILRequest.mRequest, obj));
        } else {
            riljLog(rILRequest.serialString() + "< " + RILUtils.requestToString(rILRequest.mRequest) + " error " + i);
            rILRequest.onError(i, obj);
        }
        processResponseCleanUp(rILRequest, i, i2, obj);
    }

    @VisibleForTesting
    public void processResponseFallback(RILRequest rILRequest, RadioResponseInfo radioResponseInfo, Object obj) {
        if (radioResponseInfo.error == 6) {
            riljLog(rILRequest.serialString() + "< " + RILUtils.requestToString(rILRequest.mRequest) + " request not supported, falling back");
        }
        processResponseCleanUp(rILRequest, radioResponseInfo.error, radioResponseInfo.type, obj);
    }

    private void processResponseCleanUp(RILRequest rILRequest, int i, int i2, Object obj) {
        if (rILRequest != null) {
            this.mMetrics.writeOnRilSolicitedResponse(this.mPhoneId.intValue(), rILRequest.mSerial, i, rILRequest.mRequest, obj);
            if (i2 == 0) {
                decrementWakeLock(rILRequest);
            }
            rILRequest.release();
        }
    }

    private void sendAck(int i) {
        RILRequest obtain = RILRequest.obtain(800, null, this.mRILDefaultWorkSource);
        acquireWakeLock(obtain, 1);
        if (i == 0) {
            IRadio radioProxy = getRadioProxy(null);
            if (radioProxy != null) {
                try {
                    radioProxy.responseAcknowledgement();
                } catch (RemoteException | RuntimeException e) {
                    handleRadioProxyExceptionForRR(0, "sendAck", e);
                    riljLoge("sendAck: " + e);
                }
            } else {
                Rlog.e("RILJ", "Error trying to send ack, radioProxy = null");
            }
        } else {
            RadioServiceProxy radioServiceProxy = getRadioServiceProxy(i, (Message) null);
            if (!radioServiceProxy.isEmpty()) {
                try {
                    radioServiceProxy.responseAcknowledgement();
                } catch (RemoteException | RuntimeException e2) {
                    handleRadioProxyExceptionForRR(i, "sendAck", e2);
                    riljLoge("sendAck: " + e2);
                }
            } else {
                Rlog.e("RILJ", "Error trying to send ack, serviceProxy is empty");
            }
        }
        obtain.release();
    }

    private WorkSource getDefaultWorkSourceIfInvalid(WorkSource workSource) {
        return workSource == null ? this.mRILDefaultWorkSource : workSource;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private void acquireWakeLock(RILRequest rILRequest, int i) {
        synchronized (rILRequest) {
            if (rILRequest.mWakeLockType != -1) {
                Rlog.d("RILJ", "Failed to aquire wakelock for " + rILRequest.serialString());
                return;
            }
            if (i == 0) {
                synchronized (this.mWakeLock) {
                    this.mWakeLock.acquire();
                    this.mWakeLockCount++;
                    this.mWlSequenceNum++;
                    if (!this.mClientWakelockTracker.isClientActive(rILRequest.getWorkSourceClientId())) {
                        this.mActiveWakelockWorkSource.add(rILRequest.mWorkSource);
                        this.mWakeLock.setWorkSource(this.mActiveWakelockWorkSource);
                    }
                    this.mClientWakelockTracker.startTracking(rILRequest.mClientId, rILRequest.mRequest, rILRequest.mSerial, this.mWakeLockCount);
                    Message obtainMessage = this.mRilHandler.obtainMessage(2);
                    obtainMessage.arg1 = this.mWlSequenceNum;
                    this.mRilHandler.sendMessageDelayed(obtainMessage, this.mWakeLockTimeout);
                }
            } else if (i == 1) {
                synchronized (this.mAckWakeLock) {
                    this.mAckWakeLock.acquire();
                    this.mAckWlSequenceNum++;
                    Message obtainMessage2 = this.mRilHandler.obtainMessage(4);
                    obtainMessage2.arg1 = this.mAckWlSequenceNum;
                    this.mRilHandler.sendMessageDelayed(obtainMessage2, this.mAckWakeLockTimeout);
                }
            } else {
                Rlog.w("RILJ", "Acquiring Invalid Wakelock type " + i);
                return;
            }
            rILRequest.mWakeLockType = i;
        }
    }

    @VisibleForTesting
    public PowerManager.WakeLock getWakeLock(int i) {
        return i == 0 ? this.mWakeLock : this.mAckWakeLock;
    }

    @VisibleForTesting
    public RilHandler getRilHandler() {
        return this.mRilHandler;
    }

    @VisibleForTesting
    public SparseArray<RILRequest> getRilRequestList() {
        return this.mRequestList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void decrementWakeLock(RILRequest rILRequest) {
        synchronized (rILRequest) {
            int i = rILRequest.mWakeLockType;
            if (i != -1) {
                if (i == 0) {
                    synchronized (this.mWakeLock) {
                        ClientWakelockTracker clientWakelockTracker = this.mClientWakelockTracker;
                        String str = rILRequest.mClientId;
                        int i2 = rILRequest.mRequest;
                        int i3 = rILRequest.mSerial;
                        int i4 = this.mWakeLockCount;
                        clientWakelockTracker.stopTracking(str, i2, i3, i4 > 1 ? i4 - 1 : 0);
                        if (!this.mClientWakelockTracker.isClientActive(rILRequest.getWorkSourceClientId())) {
                            this.mActiveWakelockWorkSource.remove(rILRequest.mWorkSource);
                            this.mWakeLock.setWorkSource(this.mActiveWakelockWorkSource);
                        }
                        int i5 = this.mWakeLockCount;
                        if (i5 > 1) {
                            this.mWakeLockCount = i5 - 1;
                        } else {
                            this.mWakeLockCount = 0;
                            this.mWakeLock.release();
                        }
                    }
                } else if (i != 1) {
                    Rlog.w("RILJ", "Decrementing Invalid Wakelock type " + rILRequest.mWakeLockType);
                }
            }
            rILRequest.mWakeLockType = -1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean clearWakeLock(int i) {
        if (i == 0) {
            synchronized (this.mWakeLock) {
                if (this.mWakeLockCount != 0 || this.mWakeLock.isHeld()) {
                    Rlog.d("RILJ", "NOTE: mWakeLockCount is " + this.mWakeLockCount + "at time of clearing");
                    this.mWakeLockCount = 0;
                    this.mWakeLock.release();
                    this.mClientWakelockTracker.stopTrackingAll();
                    this.mActiveWakelockWorkSource = new WorkSource();
                    return true;
                }
                return false;
            }
        }
        synchronized (this.mAckWakeLock) {
            if (this.mAckWakeLock.isHeld()) {
                this.mAckWakeLock.release();
                return true;
            }
            return false;
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private void clearRequestList(int i, boolean z) {
        synchronized (this.mRequestList) {
            int size = this.mRequestList.size();
            if (z) {
                Rlog.d("RILJ", "clearRequestList  mWakeLockCount=" + this.mWakeLockCount + " mRequestList=" + size);
            }
            for (int i2 = 0; i2 < size; i2++) {
                RILRequest valueAt = this.mRequestList.valueAt(i2);
                if (z) {
                    Rlog.d("RILJ", i2 + ": [" + valueAt.mSerial + "] " + RILUtils.requestToString(valueAt.mRequest));
                }
                valueAt.onError(i, null);
                decrementWakeLock(valueAt);
                valueAt.release();
            }
            this.mRequestList.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @UnsupportedAppUsage
    public RILRequest findAndRemoveRequestFromList(int i) {
        RILRequest rILRequest;
        synchronized (this.mRequestList) {
            rILRequest = this.mRequestList.get(i);
            if (rILRequest != null) {
                this.mRequestList.remove(i);
            }
        }
        return rILRequest;
    }

    private void addToRilHistogram(RILRequest rILRequest) {
        int elapsedRealtime = (int) (SystemClock.elapsedRealtime() - rILRequest.mStartTimeMs);
        synchronized (sRilTimeHistograms) {
            TelephonyHistogram telephonyHistogram = sRilTimeHistograms.get(rILRequest.mRequest);
            if (telephonyHistogram == null) {
                telephonyHistogram = new TelephonyHistogram(1, rILRequest.mRequest, 5);
                sRilTimeHistograms.put(rILRequest.mRequest, telephonyHistogram);
            }
            telephonyHistogram.addTimeTaken(elapsedRealtime);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public RadioCapability makeStaticRadioCapability() {
        String string = this.mContext.getResources().getString(17039984);
        int rafTypeFromString = !TextUtils.isEmpty(string) ? RadioAccessFamily.rafTypeFromString(string) : 0;
        RadioCapability radioCapability = new RadioCapability(this.mPhoneId.intValue(), 0, 0, rafTypeFromString, PhoneConfigurationManager.SSSS, 1);
        riljLog("Faking RIL_REQUEST_GET_RADIO_CAPABILITY response using " + rafTypeFromString);
        return radioCapability;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @UnsupportedAppUsage
    public static String retToString(int i, Object obj) {
        if (obj == null || i == 11 || i == 115 || i == 117 || i == 38 || i == 39) {
            return PhoneConfigurationManager.SSSS;
        }
        int i2 = 0;
        int i3 = 1;
        if (obj instanceof int[]) {
            int[] iArr = (int[]) obj;
            int length = iArr.length;
            StringBuilder sb = new StringBuilder("{");
            if (length > 0) {
                sb.append(iArr[0]);
                while (i3 < length) {
                    sb.append(", ");
                    sb.append(iArr[i3]);
                    i3++;
                }
            }
            sb.append("}");
            return sb.toString();
        } else if (obj instanceof String[]) {
            String[] strArr = (String[]) obj;
            int length2 = strArr.length;
            StringBuilder sb2 = new StringBuilder("{");
            if (length2 > 0) {
                if (i == 98) {
                    sb2.append(Rlog.pii("RILJ", strArr[0]));
                } else {
                    sb2.append(strArr[0]);
                }
                while (i3 < length2) {
                    sb2.append(", ");
                    sb2.append(strArr[i3]);
                    i3++;
                }
            }
            sb2.append("}");
            return sb2.toString();
        } else if (i == 9) {
            StringBuilder sb3 = new StringBuilder("{");
            Iterator it = ((ArrayList) obj).iterator();
            while (it.hasNext()) {
                sb3.append("[");
                sb3.append((DriverCall) it.next());
                sb3.append("] ");
            }
            sb3.append("}");
            return sb3.toString();
        } else if (i == 75) {
            StringBuilder sb4 = new StringBuilder("{");
            Iterator it2 = ((ArrayList) obj).iterator();
            while (it2.hasNext()) {
                sb4.append("[");
                sb4.append((NeighboringCellInfo) it2.next());
                sb4.append("] ");
            }
            sb4.append("}");
            return sb4.toString();
        } else if (i == 33) {
            CallForwardInfo[] callForwardInfoArr = (CallForwardInfo[]) obj;
            int length3 = callForwardInfoArr.length;
            StringBuilder sb5 = new StringBuilder("{");
            while (i2 < length3) {
                sb5.append("[");
                sb5.append(callForwardInfoArr[i2]);
                sb5.append("] ");
                i2++;
            }
            sb5.append("}");
            return sb5.toString();
        } else if (i == 124) {
            StringBuilder sb6 = new StringBuilder(" ");
            Iterator it3 = ((ArrayList) obj).iterator();
            while (it3.hasNext()) {
                sb6.append("[");
                sb6.append((HardwareConfig) it3.next());
                sb6.append("] ");
            }
            return sb6.toString();
        } else if (i == 235 || i == 1108) {
            StringBuilder sb7 = new StringBuilder("{");
            Object[] objArr = (Object[]) obj;
            sb7.append(((Integer) objArr[0]).intValue());
            sb7.append(", ");
            Object obj2 = objArr[1];
            if (obj2 != null) {
                ConnectionFailureInfo connectionFailureInfo = (ConnectionFailureInfo) obj2;
                sb7.append(connectionFailureInfo.getReason());
                sb7.append(", ");
                sb7.append(connectionFailureInfo.getCauseCode());
                sb7.append(", ");
                sb7.append(connectionFailureInfo.getWaitTimeMillis());
            } else {
                sb7.append("null");
            }
            sb7.append("}");
            return sb7.toString();
        } else {
            try {
                if (obj.getClass().getMethod("toString", new Class[0]).getDeclaringClass() != Object.class) {
                    i2 = 1;
                }
            } catch (NoSuchMethodException e) {
                Rlog.e("RILJ", e.getMessage());
            }
            if (i2 != 0) {
                return obj.toString();
            }
            return RILUtils.convertToString(obj) + " [convertToString]";
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeMetricsCallRing(char[] cArr) {
        this.mMetrics.writeRilCallRing(this.mPhoneId.intValue(), cArr);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeMetricsSrvcc(int i) {
        this.mMetrics.writeRilSrvcc(this.mPhoneId.intValue(), i);
        PhoneFactory.getPhone(this.mPhoneId.intValue()).getVoiceCallSessionStats().onRilSrvccStateChanged(i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeMetricsModemRestartEvent(String str) {
        this.mMetrics.writeModemRestartEvent(this.mPhoneId.intValue(), str);
        if (this.mPhoneId.intValue() == 0) {
            ModemRestartStats.onModemRestart(str);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void notifyRegistrantsRilConnectionChanged(int i) {
        this.mRilVersion = i;
        RegistrantList registrantList = this.mRilConnectedRegistrants;
        if (registrantList != null) {
            registrantList.notifyRegistrants(new AsyncResult((Object) null, new Integer(i), (Throwable) null));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @UnsupportedAppUsage
    public void notifyRegistrantsCdmaInfoRec(CdmaInformationRecords cdmaInformationRecords) {
        Object obj = cdmaInformationRecords.record;
        if (obj instanceof CdmaInformationRecords.CdmaDisplayInfoRec) {
            if (this.mDisplayInfoRegistrants != null) {
                if (isLogOrTrace()) {
                    unsljLogRet(1027, cdmaInformationRecords.record);
                }
                this.mDisplayInfoRegistrants.notifyRegistrants(new AsyncResult((Object) null, cdmaInformationRecords.record, (Throwable) null));
            }
        } else if (obj instanceof CdmaInformationRecords.CdmaSignalInfoRec) {
            if (this.mSignalInfoRegistrants != null) {
                if (isLogOrTrace()) {
                    unsljLogRet(1027, cdmaInformationRecords.record);
                }
                this.mSignalInfoRegistrants.notifyRegistrants(new AsyncResult((Object) null, cdmaInformationRecords.record, (Throwable) null));
            }
        } else if (obj instanceof CdmaInformationRecords.CdmaNumberInfoRec) {
            if (this.mNumberInfoRegistrants != null) {
                if (isLogOrTrace()) {
                    unsljLogRet(1027, cdmaInformationRecords.record);
                }
                this.mNumberInfoRegistrants.notifyRegistrants(new AsyncResult((Object) null, cdmaInformationRecords.record, (Throwable) null));
            }
        } else if (obj instanceof CdmaInformationRecords.CdmaRedirectingNumberInfoRec) {
            if (this.mRedirNumInfoRegistrants != null) {
                if (isLogOrTrace()) {
                    unsljLogRet(1027, cdmaInformationRecords.record);
                }
                this.mRedirNumInfoRegistrants.notifyRegistrants(new AsyncResult((Object) null, cdmaInformationRecords.record, (Throwable) null));
            }
        } else if (obj instanceof CdmaInformationRecords.CdmaLineControlInfoRec) {
            if (this.mLineControlInfoRegistrants != null) {
                if (isLogOrTrace()) {
                    unsljLogRet(1027, cdmaInformationRecords.record);
                }
                this.mLineControlInfoRegistrants.notifyRegistrants(new AsyncResult((Object) null, cdmaInformationRecords.record, (Throwable) null));
            }
        } else if (obj instanceof CdmaInformationRecords.CdmaT53ClirInfoRec) {
            if (this.mT53ClirInfoRegistrants != null) {
                if (isLogOrTrace()) {
                    unsljLogRet(1027, cdmaInformationRecords.record);
                }
                this.mT53ClirInfoRegistrants.notifyRegistrants(new AsyncResult((Object) null, cdmaInformationRecords.record, (Throwable) null));
            }
        } else if (!(obj instanceof CdmaInformationRecords.CdmaT53AudioControlInfoRec) || this.mT53AudCntrlInfoRegistrants == null) {
        } else {
            if (isLogOrTrace()) {
                unsljLogRet(1027, cdmaInformationRecords.record);
            }
            this.mT53AudCntrlInfoRegistrants.notifyRegistrants(new AsyncResult((Object) null, cdmaInformationRecords.record, (Throwable) null));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @UnsupportedAppUsage
    public void riljLog(String str) {
        Rlog.d("RILJ", str + " [PHONE" + this.mPhoneId + "]");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void riljLoge(String str) {
        Rlog.e("RILJ", str + " [PHONE" + this.mPhoneId + "]");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isLogvOrTrace() {
        return Trace.isTagEnabled(2097152L);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @UnsupportedAppUsage
    public void unsljLog(int i) {
        String responseToString = RILUtils.responseToString(i);
        riljLog("[UNSL]< " + responseToString);
        Trace.instantForTrack(2097152L, "RIL", responseToString);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @UnsupportedAppUsage
    public void unsljLogMore(int i, String str) {
        String str2 = RILUtils.responseToString(i) + " " + str;
        riljLog("[UNSL]< " + str2);
        Trace.instantForTrack(2097152L, "RIL", str2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @UnsupportedAppUsage
    public void unsljLogRet(int i, Object obj) {
        String str = RILUtils.responseToString(i) + " " + retToString(i, obj);
        riljLog("[UNSL]< " + str);
        Trace.instantForTrack(2097152L, "RIL", str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @UnsupportedAppUsage
    public void unsljLogvRet(int i, Object obj) {
        Trace.instantForTrack(2097152L, "RIL", RILUtils.responseToString(i) + " " + retToString(i, obj));
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public void setPhoneType(int i) {
        riljLog("setPhoneType=" + i + " old value=" + this.mPhoneType);
        this.mPhoneType = i;
    }

    @Override // com.android.internal.telephony.BaseCommands, com.android.internal.telephony.CommandsInterface
    public void testingEmergencyCall() {
        riljLog("testingEmergencyCall");
        this.mTestingEmergencyCall.set(true);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("RIL: " + this);
        printWriter.println(" " + this.mServiceProxies.get(1));
        printWriter.println(" " + this.mServiceProxies.get(2));
        printWriter.println(" " + this.mServiceProxies.get(3));
        printWriter.println(" " + this.mServiceProxies.get(4));
        printWriter.println(" " + this.mServiceProxies.get(5));
        printWriter.println(" " + this.mServiceProxies.get(6));
        printWriter.println(" " + this.mServiceProxies.get(7));
        printWriter.println(" " + this.mServiceProxies.get(8));
        printWriter.println(" mWakeLock=" + this.mWakeLock);
        printWriter.println(" mWakeLockTimeout=" + this.mWakeLockTimeout);
        synchronized (this.mRequestList) {
            synchronized (this.mWakeLock) {
                printWriter.println(" mWakeLockCount=" + this.mWakeLockCount);
            }
            int size = this.mRequestList.size();
            printWriter.println(" mRequestList count=" + size);
            for (int i = 0; i < size; i++) {
                RILRequest valueAt = this.mRequestList.valueAt(i);
                printWriter.println("  [" + valueAt.mSerial + "] " + RILUtils.requestToString(valueAt.mRequest));
            }
        }
        printWriter.println(" mLastNITZTimeInfo=" + Arrays.toString(this.mLastNITZTimeInfo));
        printWriter.println(" mLastRadioPowerResult=" + this.mLastRadioPowerResult);
        printWriter.println(" mTestingEmergencyCall=" + this.mTestingEmergencyCall.get());
        this.mClientWakelockTracker.dumpClientRequestTracker(printWriter);
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public List<ClientRequestStats> getClientRequestStats() {
        return this.mClientWakelockTracker.getClientRequestStats();
    }

    public SignalStrength fixupSignalStrength10(SignalStrength signalStrength) {
        ServiceState serviceState;
        List cellSignalStrengths = signalStrength.getCellSignalStrengths(CellSignalStrengthGsm.class);
        if (!cellSignalStrengths.isEmpty()) {
            int i = 0;
            if (((CellSignalStrengthGsm) cellSignalStrengths.get(0)).isValid()) {
                CellSignalStrengthGsm cellSignalStrengthGsm = (CellSignalStrengthGsm) cellSignalStrengths.get(0);
                Phone phone = PhoneFactory.getPhone(this.mPhoneId.intValue());
                if (phone != null && (serviceState = phone.getServiceState()) != null) {
                    i = serviceState.getRilVoiceRadioTechnology();
                }
                if (i != 3 && i != 15) {
                    switch (i) {
                        case 9:
                        case 10:
                        case 11:
                            break;
                        default:
                            return signalStrength;
                    }
                }
                return new SignalStrength(new CellSignalStrengthCdma(), new CellSignalStrengthGsm(), new CellSignalStrengthWcdma(cellSignalStrengthGsm.getRssi(), cellSignalStrengthGsm.getBitErrorRate(), KeepaliveStatus.INVALID_HANDLE, KeepaliveStatus.INVALID_HANDLE), new CellSignalStrengthTdscdma(), new CellSignalStrengthLte(), new CellSignalStrengthNr());
            }
        }
        return signalStrength;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notifyBarringInfoChanged(BarringInfo barringInfo) {
        this.mLastBarringInfo = barringInfo;
        this.mBarringInfoChangedRegistrants.notifyRegistrants(new AsyncResult((Object) null, barringInfo, (Throwable) null));
    }

    @Override // com.android.internal.telephony.CommandsInterface
    public HalVersion getHalVersion(int i) {
        HalVersion halVersion = this.mHalVersion.get(Integer.valueOf(i));
        if (halVersion == null) {
            if (isRadioServiceSupported(i)) {
                return RADIO_HAL_VERSION_UNKNOWN;
            }
            return RADIO_HAL_VERSION_UNSUPPORTED;
        }
        return halVersion;
    }

    public static HalVersion getServiceHalVersion(int i) {
        if (i != 1) {
            if (i == 2) {
                return RADIO_HAL_VERSION_2_1;
            }
            return RADIO_HAL_VERSION_UNKNOWN;
        }
        return RADIO_HAL_VERSION_2_0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String serviceToString(int i) {
        switch (i) {
            case 0:
                return "RADIO";
            case 1:
                return "DATA";
            case 2:
                return "MESSAGING";
            case 3:
                return "MODEM";
            case 4:
                return "NETWORK";
            case 5:
                return "SIM";
            case 6:
                return "VOICE";
            case 7:
                return "IMS";
            case 8:
                return "SATELLITE";
            default:
                return "UNKNOWN:" + i;
        }
    }
}
