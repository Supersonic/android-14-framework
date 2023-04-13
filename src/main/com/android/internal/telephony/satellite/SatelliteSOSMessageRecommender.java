package com.android.internal.telephony.satellite;

import android.os.AsyncResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ResultReceiver;
import android.provider.DeviceConfig;
import android.telecom.Connection;
import android.telephony.Rlog;
import android.telephony.ServiceState;
import android.telephony.ims.ImsReasonInfo;
import android.telephony.ims.ImsRegistrationAttributes;
import android.telephony.ims.RegistrationManager;
import android.telephony.satellite.ISatelliteProvisionStateCallback;
import android.util.Pair;
import com.android.ims.ImsException;
import com.android.ims.ImsManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.data.KeepaliveStatus;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes.dex */
public class SatelliteSOSMessageRecommender extends Handler {
    public static final long DEFAULT_EMERGENCY_CALL_TO_SOS_MSG_HYSTERESIS_TIMEOUT_MILLIS = 20000;
    public static final String EMERGENCY_CALL_TO_SOS_MSG_HYSTERESIS_TIMEOUT_MILLIS = "emergency_call_to_sos_msg_hysteresis_timeout_millis";
    protected static final int EVENT_CELLULAR_SERVICE_STATE_CHANGED = 2;
    protected static final int EVENT_TIME_OUT = 4;
    private AtomicInteger mCellularServiceState;
    protected int mCountOfTimerStarted;
    private Connection mEmergencyConnection;
    private final ISatelliteProvisionStateCallback mISatelliteProvisionStateCallback;
    private ImsManager mImsManager;
    private RegistrationManager.RegistrationCallback mImsRegistrationCallback;
    private AtomicBoolean mIsImsRegistered;
    private AtomicBoolean mIsSatelliteAllowedInCurrentLocation;
    private Phone mPhone;
    private final ResultReceiver mReceiverForRequestIsSatelliteAllowedForCurrentLocation;
    private final SatelliteController mSatelliteController;
    private final long mTimeoutMillis;

    private void reportMetrics(boolean z) {
    }

    private boolean shouldTrackCall(int i) {
        return (i == 4 || i == 6) ? false : true;
    }

    public SatelliteSOSMessageRecommender(Looper looper) {
        this(looper, SatelliteController.getInstance(), null, getEmergencyCallToSosMsgHysteresisTimeoutMillis());
    }

    @VisibleForTesting
    protected SatelliteSOSMessageRecommender(Looper looper, SatelliteController satelliteController, ImsManager imsManager, long j) {
        super(looper);
        this.mEmergencyConnection = null;
        this.mPhone = null;
        this.mCellularServiceState = new AtomicInteger();
        this.mIsImsRegistered = new AtomicBoolean();
        this.mIsSatelliteAllowedInCurrentLocation = new AtomicBoolean();
        this.mCountOfTimerStarted = 0;
        this.mImsRegistrationCallback = new RegistrationManager.RegistrationCallback() { // from class: com.android.internal.telephony.satellite.SatelliteSOSMessageRecommender.1
            @Override // android.telephony.ims.RegistrationManager.RegistrationCallback
            public void onRegistered(ImsRegistrationAttributes imsRegistrationAttributes) {
                SatelliteSOSMessageRecommender satelliteSOSMessageRecommender = SatelliteSOSMessageRecommender.this;
                satelliteSOSMessageRecommender.sendMessage(satelliteSOSMessageRecommender.obtainMessage(3, Boolean.TRUE));
            }

            @Override // android.telephony.ims.RegistrationManager.RegistrationCallback
            public void onUnregistered(ImsReasonInfo imsReasonInfo) {
                SatelliteSOSMessageRecommender satelliteSOSMessageRecommender = SatelliteSOSMessageRecommender.this;
                satelliteSOSMessageRecommender.sendMessage(satelliteSOSMessageRecommender.obtainMessage(3, Boolean.FALSE));
            }
        };
        this.mSatelliteController = satelliteController;
        this.mImsManager = imsManager;
        this.mTimeoutMillis = j;
        this.mISatelliteProvisionStateCallback = new ISatelliteProvisionStateCallback.Stub() { // from class: com.android.internal.telephony.satellite.SatelliteSOSMessageRecommender.2
            public void onSatelliteProvisionStateChanged(boolean z) {
                SatelliteSOSMessageRecommender.logd("onSatelliteProvisionStateChanged: provisioned=" + z);
                SatelliteSOSMessageRecommender satelliteSOSMessageRecommender = SatelliteSOSMessageRecommender.this;
                satelliteSOSMessageRecommender.sendMessage(satelliteSOSMessageRecommender.obtainMessage(5, Boolean.valueOf(z)));
            }
        };
        this.mReceiverForRequestIsSatelliteAllowedForCurrentLocation = new ResultReceiver(this) { // from class: com.android.internal.telephony.satellite.SatelliteSOSMessageRecommender.3
            @Override // android.os.ResultReceiver
            protected void onReceiveResult(int i, Bundle bundle) {
                if (i == 0) {
                    if (bundle.containsKey("satellite_communication_allowed")) {
                        boolean z = bundle.getBoolean("satellite_communication_allowed");
                        SatelliteSOSMessageRecommender.this.mIsSatelliteAllowedInCurrentLocation.set(z);
                        if (z) {
                            return;
                        }
                        SatelliteSOSMessageRecommender.logd("Satellite is not allowed for current location.");
                        SatelliteSOSMessageRecommender.this.cleanUpResources();
                        return;
                    }
                    SatelliteSOSMessageRecommender.loge("KEY_SATELLITE_COMMUNICATION_ALLOWED does not exist.");
                    SatelliteSOSMessageRecommender.this.mIsSatelliteAllowedInCurrentLocation.set(false);
                    SatelliteSOSMessageRecommender.this.cleanUpResources();
                    return;
                }
                SatelliteSOSMessageRecommender.loge("requestIsSatelliteCommunicationAllowedForCurrentLocation() resultCode=" + i);
                SatelliteSOSMessageRecommender.this.mIsSatelliteAllowedInCurrentLocation.set(false);
                SatelliteSOSMessageRecommender.this.cleanUpResources();
            }
        };
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        switch (message.what) {
            case 1:
                handleEmergencyCallStartedEvent((Pair) message.obj);
                return;
            case 2:
                handleCellularServiceStateChangedEvent((ServiceState) ((AsyncResult) message.obj).result);
                return;
            case 3:
                handleImsRegistrationStateChangedEvent(((Boolean) message.obj).booleanValue());
                return;
            case 4:
                handleTimeoutEvent();
                return;
            case 5:
                handleSatelliteProvisionStateChangedEvent(((Boolean) message.obj).booleanValue());
                return;
            case 6:
                handleEmergencyCallConnectionStateChangedEvent((Pair) message.obj);
                return;
            default:
                logd("handleMessage: unexpected message code: " + message.what);
                return;
        }
    }

    public void onEmergencyCallStarted(Connection connection, Phone phone) {
        if (!this.mSatelliteController.isSatelliteSupported()) {
            logd("onEmergencyCallStarted: satellite is not supported");
        } else {
            sendMessage(obtainMessage(1, new Pair(connection, phone)));
        }
    }

    public void onEmergencyCallConnectionStateChanged(String str, int i) {
        sendMessage(obtainMessage(6, new Pair(str, Integer.valueOf(i))));
    }

    private void handleEmergencyCallStartedEvent(Pair<Connection, Phone> pair) {
        this.mSatelliteController.requestIsSatelliteCommunicationAllowedForCurrentLocation(KeepaliveStatus.INVALID_HANDLE, this.mReceiverForRequestIsSatelliteAllowedForCurrentLocation);
        if (this.mPhone != null) {
            logd("handleEmergencyCallStartedEvent: new emergency call started while there is  an ongoing call");
            unregisterForInterestedStateChangedEvents(this.mPhone);
        }
        Phone phone = (Phone) pair.second;
        this.mPhone = phone;
        this.mEmergencyConnection = (Connection) pair.first;
        this.mCellularServiceState.set(phone.getServiceState().getState());
        this.mIsImsRegistered.set(this.mPhone.isImsRegistered());
        handleStateChangedEventForHysteresisTimer();
        registerForInterestedStateChangedEvents(this.mPhone);
    }

    private void handleSatelliteProvisionStateChangedEvent(boolean z) {
        if (z) {
            return;
        }
        cleanUpResources();
    }

    private void handleTimeoutEvent() {
        boolean z;
        if (!this.mIsImsRegistered.get() && !isCellularAvailable() && this.mIsSatelliteAllowedInCurrentLocation.get() && this.mSatelliteController.isSatelliteProvisioned(KeepaliveStatus.INVALID_HANDLE) && shouldTrackCall(this.mEmergencyConnection.getState())) {
            logd("handleTimeoutEvent: Sending EVENT_DISPLAY_SOS_MESSAGE to Dialer...");
            this.mEmergencyConnection.sendConnectionEvent("android.telecom.event.DISPLAY_SOS_MESSAGE", null);
            z = true;
        } else {
            z = false;
        }
        reportMetrics(z);
        cleanUpResources();
    }

    private void handleEmergencyCallConnectionStateChangedEvent(Pair<String, Integer> pair) {
        if (this.mEmergencyConnection == null) {
            return;
        }
        String str = (String) pair.first;
        int intValue = ((Integer) pair.second).intValue();
        if (!this.mEmergencyConnection.getTelecomCallId().equals(str)) {
            loge("handleEmergencyCallConnectionStateChangedEvent: unexpected state changed event , mEmergencyConnection=" + this.mEmergencyConnection + ", callId=" + str + ", state=" + intValue);
            cleanUpResources();
        } else if (shouldTrackCall(intValue)) {
        } else {
            reportMetrics(false);
            cleanUpResources();
        }
    }

    private void handleImsRegistrationStateChangedEvent(boolean z) {
        if (z != this.mIsImsRegistered.get()) {
            this.mIsImsRegistered.set(z);
            handleStateChangedEventForHysteresisTimer();
        }
    }

    private void handleCellularServiceStateChangedEvent(ServiceState serviceState) {
        int state = serviceState.getState();
        if (this.mCellularServiceState.get() != state) {
            this.mCellularServiceState.set(state);
            handleStateChangedEventForHysteresisTimer();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cleanUpResources() {
        stopTimer();
        Phone phone = this.mPhone;
        if (phone != null) {
            unregisterForInterestedStateChangedEvents(phone);
            this.mPhone = null;
        }
        this.mEmergencyConnection = null;
        this.mCountOfTimerStarted = 0;
    }

    private void registerForInterestedStateChangedEvents(Phone phone) {
        this.mSatelliteController.registerForSatelliteProvisionStateChanged(KeepaliveStatus.INVALID_HANDLE, this.mISatelliteProvisionStateCallback);
        phone.registerForServiceStateChanged(this, 2, null);
        registerForImsRegistrationStateChanged(phone);
    }

    private void registerForImsRegistrationStateChanged(Phone phone) {
        ImsManager imsManager = this.mImsManager;
        if (imsManager == null) {
            imsManager = ImsManager.getInstance(phone.getContext(), phone.getPhoneId());
        }
        try {
            imsManager.addRegistrationCallback(this.mImsRegistrationCallback, new Executor() { // from class: com.android.internal.telephony.satellite.SatelliteSOSMessageRecommender$$ExternalSyntheticLambda0
                @Override // java.util.concurrent.Executor
                public final void execute(Runnable runnable) {
                    SatelliteSOSMessageRecommender.this.post(runnable);
                }
            });
        } catch (ImsException e) {
            loge("registerForImsRegistrationStateChanged: ex=" + e);
        }
    }

    private void unregisterForInterestedStateChangedEvents(Phone phone) {
        this.mSatelliteController.unregisterForSatelliteProvisionStateChanged(KeepaliveStatus.INVALID_HANDLE, this.mISatelliteProvisionStateCallback);
        phone.unregisterForServiceStateChanged(this);
        unregisterForImsRegistrationStateChanged(phone);
    }

    private void unregisterForImsRegistrationStateChanged(Phone phone) {
        ImsManager imsManager = this.mImsManager;
        if (imsManager == null) {
            imsManager = ImsManager.getInstance(phone.getContext(), phone.getPhoneId());
        }
        imsManager.removeRegistrationListener(this.mImsRegistrationCallback);
    }

    private boolean isCellularAvailable() {
        return this.mCellularServiceState.get() == 0 || this.mCellularServiceState.get() == 2;
    }

    private void handleStateChangedEventForHysteresisTimer() {
        if (!this.mIsImsRegistered.get() && !isCellularAvailable()) {
            startTimer();
        } else {
            stopTimer();
        }
    }

    private void startTimer() {
        if (hasMessages(4)) {
            return;
        }
        sendMessageDelayed(obtainMessage(4), this.mTimeoutMillis);
        this.mCountOfTimerStarted++;
    }

    private void stopTimer() {
        removeMessages(4);
    }

    private static long getEmergencyCallToSosMsgHysteresisTimeoutMillis() {
        return DeviceConfig.getLong("telephony", EMERGENCY_CALL_TO_SOS_MSG_HYSTERESIS_TIMEOUT_MILLIS, (long) DEFAULT_EMERGENCY_CALL_TO_SOS_MSG_HYSTERESIS_TIMEOUT_MILLIS);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void logd(String str) {
        Rlog.d("SatelliteSOSMessageRecommender", str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void loge(String str) {
        Rlog.e("SatelliteSOSMessageRecommender", str);
    }
}
