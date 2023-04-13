package com.android.internal.telephony.data;

import android.content.SharedPreferences;
import android.hardware.display.DisplayManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.Message;
import android.os.OutcomeReceiver;
import android.preference.PreferenceManager;
import android.telephony.CellIdentity;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityNr;
import android.telephony.CellIdentityTdscdma;
import android.telephony.CellIdentityWcdma;
import android.telephony.ModemActivityInfo;
import android.telephony.NetworkRegistrationInfo;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Pair;
import android.view.Display;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.GbaManager;
import com.android.internal.telephony.IndentingPrintWriter;
import com.android.internal.telephony.LocalLog;
import com.android.internal.telephony.NetworkTypeController$$ExternalSyntheticLambda1;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.TelephonyFacade;
import com.android.internal.telephony.data.LinkBandwidthEstimator;
import com.android.internal.telephony.imsphone.ImsRttTextHandler;
import com.android.internal.telephony.metrics.TelephonyMetrics;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class LinkBandwidthEstimator extends Handler {
    public static final int BW_STATS_COUNT_THRESHOLD = 5;
    public static final int LINK_RX = 1;
    public static final int LINK_TX = 0;
    @VisibleForTesting
    static final int MSG_ACTIVE_PHONE_CHANGED = 8;
    @VisibleForTesting
    static final int MSG_DATA_REG_STATE_OR_RAT_CHANGED = 9;
    @VisibleForTesting
    static final int MSG_DEFAULT_NETWORK_CHANGED = 4;
    @VisibleForTesting
    static final int MSG_MODEM_ACTIVITY_RETURNED = 3;
    @VisibleForTesting
    static final int MSG_NR_FREQUENCY_CHANGED = 6;
    @VisibleForTesting
    static final int MSG_NR_STATE_CHANGED = 7;
    @VisibleForTesting
    static final int MSG_SCREEN_STATE_CHANGED = 1;
    @VisibleForTesting
    static final int MSG_SIGNAL_STRENGTH_CHANGED = 5;
    @VisibleForTesting
    static final int MSG_TRAFFIC_STATS_POLL = 2;
    public static final int NUM_LINK_DIRECTION = 2;
    public static final int NUM_SIGNAL_LEVEL = 5;
    @VisibleForTesting
    static final int UNKNOWN_TAC = Integer.MAX_VALUE;
    private int mBandwidthUpdateDataRat;
    private String mBandwidthUpdatePlmn;
    private int mBandwidthUpdateSignalDbm;
    private int mBandwidthUpdateSignalLevel;
    private final ConnectivityManager mConnectivityManager;
    private int mDataActivity;
    private int mDataRat;
    private final ConnectivityManager.NetworkCallback mDefaultNetworkCallback;
    private final DisplayManager.DisplayListener mDisplayListener;
    private long mFilterUpdateTimeMs;
    private long mLastDrsOrRatChangeTimeMs;
    private long mLastMobileRxBytes;
    private long mLastMobileTxBytes;
    private long mLastModemPollTimeMs;
    private long mLastPlmnOrRatChangeTimeMs;
    private final Set<LinkBandwidthEstimatorCallback> mLinkBandwidthEstimatorCallbacks;
    private NetworkCapabilities mNetworkCapabilities;
    private final Map<NetworkKey, NetworkBandwidth> mNetworkMap;
    private final OutcomeReceiver<ModemActivityInfo, TelephonyManager.ModemActivityInfoException> mOutcomeReceiver;
    private final Phone mPhone;
    private NetworkBandwidth mPlaceholderNetwork;
    private String mPlmn;
    private long mRxBytesDeltaAcc;
    private BandwidthState mRxState;
    private int mSignalLevel;
    private int mSignalStrengthDbm;
    private int mTac;
    private final TelephonyCallback mTelephonyCallback;
    private final TelephonyFacade mTelephonyFacade;
    private final TelephonyManager mTelephonyManager;
    private long mTxBytesDeltaAcc;
    private BandwidthState mTxState;
    private static final String TAG = LinkBandwidthEstimator.class.getSimpleName();
    private static final int[][] BYTE_DELTA_THRESHOLD_KB = {new int[]{ImsRttTextHandler.MAX_BUFFERING_DELAY_MILLIS, 300, 400, 600, 1000}, new int[]{400, 600, 800, 1000, 1000}};
    private static final String[] AVG_BW_PER_RAT = {"GPRS:24,24", "EDGE:70,18", "UMTS:115,115", "CDMA:14,14", "CDMA - 1xRTT:30,30", "CDMA - EvDo rev. 0:750,48", "CDMA - EvDo rev. A:950,550", "HSDPA:4300,620", "HSUPA:4300,1800", "HSPA:4300,1800", "CDMA - EvDo rev. B:1500,550", "CDMA - eHRPD:750,48", "HSPA+:13000,3400", "TD_SCDMA:115,115", "LTE:30000,15000", "NR_NSA:47000,18000", "NR_NSA_MMWAVE:145000,60000", "NR:145000,60000", "NR_MMWAVE:145000,60000"};
    private static final Map<String, Pair<Integer, Integer>> AVG_BW_PER_RAT_MAP = new ArrayMap();
    private final LocalLog mLocalLog = new LocalLog(512);
    private boolean mScreenOn = false;
    private boolean mIsOnDefaultRoute = false;
    private boolean mIsOnActiveData = false;
    private boolean mLastTrafficValid = true;
    private ModemActivityInfo mLastModemActivityInfo = null;

    void logv(String str) {
    }

    /* loaded from: classes.dex */
    public static class LinkBandwidthEstimatorCallback extends DataCallback {
        public void onBandwidthChanged(int i, int i2) {
        }

        public void onDataActivityChanged(int i) {
        }

        public LinkBandwidthEstimatorCallback(Executor executor) {
            super(executor);
        }
    }

    private static void initAvgBwPerRatTable() {
        int i;
        int i2;
        for (String str : AVG_BW_PER_RAT) {
            String[] split = str.split(":");
            if (split.length == 2) {
                String[] split2 = split[1].split(",");
                int i3 = 14;
                if (split2.length == 2) {
                    try {
                        i = Integer.parseInt(split2[0]);
                        try {
                            i3 = Integer.parseInt(split2[1]);
                        } catch (NumberFormatException unused) {
                        }
                    } catch (NumberFormatException unused2) {
                        i = 14;
                    }
                    i2 = i3;
                    i3 = i;
                } else {
                    i2 = 14;
                }
                AVG_BW_PER_RAT_MAP.put(split[0], new Pair<>(Integer.valueOf(i3), Integer.valueOf(i2)));
            }
        }
    }

    public LinkBandwidthEstimator(Phone phone, TelephonyFacade telephonyFacade) {
        TelephonyCallbackImpl telephonyCallbackImpl = new TelephonyCallbackImpl();
        this.mTelephonyCallback = telephonyCallbackImpl;
        this.mDataRat = 0;
        this.mPlmn = PhoneConfigurationManager.SSSS;
        this.mBandwidthUpdateSignalDbm = -1;
        this.mBandwidthUpdateSignalLevel = -1;
        this.mBandwidthUpdateDataRat = 0;
        this.mBandwidthUpdatePlmn = PhoneConfigurationManager.SSSS;
        this.mTxState = new BandwidthState(0);
        this.mRxState = new BandwidthState(1);
        this.mDataActivity = 0;
        this.mLinkBandwidthEstimatorCallbacks = new ArraySet();
        DisplayManager.DisplayListener displayListener = new DisplayManager.DisplayListener() { // from class: com.android.internal.telephony.data.LinkBandwidthEstimator.1
            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayAdded(int i) {
            }

            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayRemoved(int i) {
            }

            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayChanged(int i) {
                LinkBandwidthEstimator linkBandwidthEstimator = LinkBandwidthEstimator.this;
                linkBandwidthEstimator.obtainMessage(1, Boolean.valueOf(linkBandwidthEstimator.isScreenOn())).sendToTarget();
            }
        };
        this.mDisplayListener = displayListener;
        this.mOutcomeReceiver = new OutcomeReceiver<ModemActivityInfo, TelephonyManager.ModemActivityInfoException>() { // from class: com.android.internal.telephony.data.LinkBandwidthEstimator.2
            @Override // android.os.OutcomeReceiver
            public void onResult(ModemActivityInfo modemActivityInfo) {
                LinkBandwidthEstimator.this.obtainMessage(3, modemActivityInfo).sendToTarget();
            }

            @Override // android.os.OutcomeReceiver
            public void onError(TelephonyManager.ModemActivityInfoException modemActivityInfoException) {
                String str = LinkBandwidthEstimator.TAG;
                Rlog.e(str, "error reading modem stats:" + modemActivityInfoException);
                LinkBandwidthEstimator.this.obtainMessage(3, null).sendToTarget();
            }
        };
        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() { // from class: com.android.internal.telephony.data.LinkBandwidthEstimator.3
            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                LinkBandwidthEstimator.this.obtainMessage(4, networkCapabilities).sendToTarget();
            }

            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onLost(Network network) {
                LinkBandwidthEstimator.this.obtainMessage(4, null).sendToTarget();
            }
        };
        this.mDefaultNetworkCallback = networkCallback;
        this.mNetworkMap = new ArrayMap();
        this.mPhone = phone;
        this.mTelephonyFacade = telephonyFacade;
        TelephonyManager createForSubscriptionId = ((TelephonyManager) phone.getContext().getSystemService(TelephonyManager.class)).createForSubscriptionId(phone.getSubId());
        this.mTelephonyManager = createForSubscriptionId;
        ConnectivityManager connectivityManager = (ConnectivityManager) phone.getContext().getSystemService(ConnectivityManager.class);
        this.mConnectivityManager = connectivityManager;
        ((DisplayManager) phone.getContext().getSystemService("display")).registerDisplayListener(displayListener, null);
        handleScreenStateChanged(isScreenOn());
        connectivityManager.registerDefaultNetworkCallback(networkCallback, this);
        createForSubscriptionId.registerTelephonyCallback(new HandlerExecutor(this), telephonyCallbackImpl);
        this.mPlaceholderNetwork = new NetworkBandwidth(PhoneConfigurationManager.SSSS);
        initAvgBwPerRatTable();
        registerNrStateFrequencyChange();
        phone.getServiceStateTracker().registerForDataRegStateOrRatChanged(1, this, 9, null);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        switch (message.what) {
            case 1:
                handleScreenStateChanged(((Boolean) message.obj).booleanValue());
                return;
            case 2:
                handleTrafficStatsPoll();
                return;
            case 3:
                handleModemActivityReturned((ModemActivityInfo) message.obj);
                return;
            case 4:
                handleDefaultNetworkChanged((NetworkCapabilities) message.obj);
                return;
            case 5:
                handleSignalStrengthChanged((SignalStrength) message.obj);
                return;
            case 6:
            case 7:
                updateStaticBwValueResetFilter();
                return;
            case 8:
                handleActivePhoneChanged(((Integer) message.obj).intValue());
                return;
            case 9:
                handleDrsOrRatChanged((AsyncResult) message.obj);
                return;
            default:
                String str = TAG;
                Rlog.e(str, "invalid message " + message.what);
                return;
        }
    }

    public void registerCallback(LinkBandwidthEstimatorCallback linkBandwidthEstimatorCallback) {
        this.mLinkBandwidthEstimatorCallbacks.add(linkBandwidthEstimatorCallback);
    }

    public void unregisterCallback(LinkBandwidthEstimatorCallback linkBandwidthEstimatorCallback) {
        this.mLinkBandwidthEstimatorCallbacks.remove(linkBandwidthEstimatorCallback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isScreenOn() {
        Display[] displays = ((DisplayManager) this.mPhone.getContext().getSystemService("display")).getDisplays();
        if (displays != null) {
            for (Display display : displays) {
                if (display.getState() == 2) {
                    return true;
                }
            }
        }
        return false;
    }

    private void handleScreenStateChanged(boolean z) {
        if (this.mScreenOn == z) {
            return;
        }
        this.mScreenOn = z;
        handleTrafficStatsPollConditionChanged();
    }

    private void handleDefaultNetworkChanged(NetworkCapabilities networkCapabilities) {
        this.mNetworkCapabilities = networkCapabilities;
        boolean hasTransport = networkCapabilities != null ? networkCapabilities.hasTransport(0) : false;
        if (this.mIsOnDefaultRoute == hasTransport) {
            return;
        }
        this.mIsOnDefaultRoute = hasTransport;
        logd("mIsOnDefaultRoute " + this.mIsOnDefaultRoute);
        handleTrafficStatsPollConditionChanged();
    }

    private void handleActivePhoneChanged(int i) {
        boolean z = i == this.mPhone.getSubId();
        if (this.mIsOnActiveData == z) {
            return;
        }
        this.mIsOnActiveData = z;
        logd("mIsOnActiveData " + this.mIsOnActiveData + " activeDataSubId " + i);
        handleTrafficStatsPollConditionChanged();
    }

    private void handleDrsOrRatChanged(AsyncResult asyncResult) {
        Pair pair = (Pair) asyncResult.result;
        logd("DrsOrRatChanged dataRegState " + pair.first + " rilRat " + pair.second);
        this.mLastDrsOrRatChangeTimeMs = this.mTelephonyFacade.getElapsedSinceBootMillis();
    }

    private void handleTrafficStatsPollConditionChanged() {
        removeMessages(2);
        if (this.mScreenOn && this.mIsOnDefaultRoute && this.mIsOnActiveData) {
            updateDataRatCellIdentityBandwidth();
            handleTrafficStatsPoll();
            return;
        }
        logd("Traffic status poll stopped");
        if (this.mDataActivity != 0) {
            this.mDataActivity = 0;
            this.mLinkBandwidthEstimatorCallbacks.forEach(new Consumer() { // from class: com.android.internal.telephony.data.LinkBandwidthEstimator$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    LinkBandwidthEstimator.this.lambda$handleTrafficStatsPollConditionChanged$1((LinkBandwidthEstimator.LinkBandwidthEstimatorCallback) obj);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleTrafficStatsPollConditionChanged$1(final LinkBandwidthEstimatorCallback linkBandwidthEstimatorCallback) {
        linkBandwidthEstimatorCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.LinkBandwidthEstimator$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                LinkBandwidthEstimator.this.lambda$handleTrafficStatsPollConditionChanged$0(linkBandwidthEstimatorCallback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleTrafficStatsPollConditionChanged$0(LinkBandwidthEstimatorCallback linkBandwidthEstimatorCallback) {
        linkBandwidthEstimatorCallback.onDataActivityChanged(this.mDataActivity);
    }

    private void handleTrafficStatsPoll() {
        invalidateTxRxSamples();
        long mobileTxBytes = this.mTelephonyFacade.getMobileTxBytes();
        long mobileRxBytes = this.mTelephonyFacade.getMobileRxBytes();
        long j = mobileTxBytes - this.mLastMobileTxBytes;
        long j2 = mobileRxBytes - this.mLastMobileRxBytes;
        sendEmptyMessageDelayed(2, 1000L);
        this.mLastMobileTxBytes = mobileTxBytes;
        this.mLastMobileRxBytes = mobileRxBytes;
        int i = (j > 0L ? 1 : (j == 0L ? 0 : -1));
        boolean z = i >= 0 && j2 >= 0;
        if (!this.mLastTrafficValid || !z) {
            this.mLastTrafficValid = z;
            Rlog.e(TAG, " run into invalid traffic count");
            return;
        }
        this.mTxBytesDeltaAcc += j;
        this.mRxBytesDeltaAcc += j2;
        boolean z2 = j >= ((long) Math.min(this.mTxState.mByteDeltaAccThr / 8, 20000)) || j2 >= ((long) Math.min(this.mRxState.mByteDeltaAccThr / 8, 20000)) || this.mTxBytesDeltaAcc >= ((long) this.mTxState.mByteDeltaAccThr) || this.mRxBytesDeltaAcc >= ((long) this.mRxState.mByteDeltaAccThr);
        long elapsedSinceBootMillis = this.mTelephonyFacade.getElapsedSinceBootMillis();
        if (elapsedSinceBootMillis - this.mLastModemPollTimeMs < 5000) {
            z2 = false;
        }
        if (z2) {
            logd("txByteDelta " + j + " rxByteDelta " + j2 + " txByteDeltaAcc " + this.mTxBytesDeltaAcc + " rxByteDeltaAcc " + this.mRxBytesDeltaAcc + " trigger modem activity request");
            updateDataRatCellIdentityBandwidth();
            makeRequestModemActivity();
            return;
        }
        final int i2 = (i <= 0 || j2 <= 0) ? j2 > 0 ? 1 : i > 0 ? 2 : 0 : 3;
        if (this.mDataActivity != i2) {
            this.mDataActivity = i2;
            this.mLinkBandwidthEstimatorCallbacks.forEach(new Consumer() { // from class: com.android.internal.telephony.data.LinkBandwidthEstimator$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    LinkBandwidthEstimator.lambda$handleTrafficStatsPoll$3(i2, (LinkBandwidthEstimator.LinkBandwidthEstimatorCallback) obj);
                }
            });
        }
        if (elapsedSinceBootMillis - this.mFilterUpdateTimeMs < 5100 || updateDataRatCellIdentityBandwidth()) {
            return;
        }
        updateTxRxBandwidthFilterSendToDataConnection();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$handleTrafficStatsPoll$3(final int i, final LinkBandwidthEstimatorCallback linkBandwidthEstimatorCallback) {
        linkBandwidthEstimatorCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.LinkBandwidthEstimator$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                LinkBandwidthEstimator.LinkBandwidthEstimatorCallback.this.onDataActivityChanged(i);
            }
        });
    }

    private void makeRequestModemActivity() {
        this.mLastModemPollTimeMs = this.mTelephonyFacade.getElapsedSinceBootMillis();
        this.mTelephonyManager.requestModemActivityInfo(new NetworkTypeController$$ExternalSyntheticLambda1(), this.mOutcomeReceiver);
    }

    private void handleModemActivityReturned(ModemActivityInfo modemActivityInfo) {
        updateBandwidthTxRxSamples(modemActivityInfo);
        updateTxRxBandwidthFilterSendToDataConnection();
        this.mLastModemActivityInfo = modemActivityInfo;
        resetByteDeltaAcc();
    }

    private void resetByteDeltaAcc() {
        this.mTxBytesDeltaAcc = 0L;
        this.mRxBytesDeltaAcc = 0L;
    }

    private void invalidateTxRxSamples() {
        this.mTxState.mBwSampleValid = false;
        this.mRxState.mBwSampleValid = false;
    }

    private void updateBandwidthTxRxSamples(ModemActivityInfo modemActivityInfo) {
        if (this.mLastModemActivityInfo == null || modemActivityInfo == null || this.mNetworkCapabilities == null || hasRecentDataRegStatePlmnOrRatChange()) {
            return;
        }
        long timestampMillis = modemActivityInfo.getTimestampMillis() - this.mLastModemActivityInfo.getTimestampMillis();
        if (timestampMillis > 10000 || timestampMillis <= 0) {
            return;
        }
        ModemActivityInfo delta = this.mLastModemActivityInfo.getDelta(modemActivityInfo);
        long modemTxTimeMs = getModemTxTimeMs(delta);
        long receiveTimeMillis = delta.getReceiveTimeMillis();
        long j = ((2 * modemTxTimeMs) > (3 * receiveTimeMillis) ? 1 : ((2 * modemTxTimeMs) == (3 * receiveTimeMillis) ? 0 : -1)) > 0 ? modemTxTimeMs + receiveTimeMillis : receiveTimeMillis;
        this.mTxState.updateBandwidthSample(this.mTxBytesDeltaAcc, modemTxTimeMs);
        this.mRxState.updateBandwidthSample(this.mRxBytesDeltaAcc, j);
        int linkUpstreamBandwidthKbps = this.mNetworkCapabilities.getLinkUpstreamBandwidthKbps();
        int linkDownstreamBandwidthKbps = this.mNetworkCapabilities.getLinkDownstreamBandwidthKbps();
        logd("UpdateBwSample dBm " + this.mSignalStrengthDbm + " level " + this.mSignalLevel + " rat " + getDataRatName(this.mDataRat) + " plmn " + this.mPlmn + " tac " + this.mTac + " reportedTxKbps " + linkUpstreamBandwidthKbps + " reportedRxKbps " + linkDownstreamBandwidthKbps + " txMs " + modemTxTimeMs + " rxMs " + receiveTimeMillis + " txKB " + (this.mTxBytesDeltaAcc / 1024) + " rxKB " + (this.mRxBytesDeltaAcc / 1024) + " txKBThr " + (this.mTxState.mByteDeltaAccThr / 1024) + " rxKBThr " + (this.mRxState.mByteDeltaAccThr / 1024));
    }

    private boolean hasRecentDataRegStatePlmnOrRatChange() {
        ModemActivityInfo modemActivityInfo = this.mLastModemActivityInfo;
        if (modemActivityInfo == null) {
            return false;
        }
        return this.mLastDrsOrRatChangeTimeMs > modemActivityInfo.getTimestampMillis() || this.mLastPlmnOrRatChangeTimeMs > this.mLastModemActivityInfo.getTimestampMillis();
    }

    private long getModemTxTimeMs(ModemActivityInfo modemActivityInfo) {
        long j = 0;
        for (int i = 0; i < ModemActivityInfo.getNumTxPowerLevels(); i++) {
            j += modemActivityInfo.getTransmitDurationMillisAtPowerLevel(i);
        }
        return j;
    }

    private void updateTxRxBandwidthFilterSendToDataConnection() {
        this.mFilterUpdateTimeMs = this.mTelephonyFacade.getElapsedSinceBootMillis();
        this.mTxState.updateBandwidthFilter();
        this.mRxState.updateBandwidthFilter();
        boolean z = (!this.mTxState.hasLargeBwChange() && !this.mRxState.hasLargeBwChange() && this.mBandwidthUpdateDataRat == this.mDataRat && this.mBandwidthUpdateSignalLevel == this.mSignalLevel && this.mBandwidthUpdatePlmn.equals(this.mPlmn)) ? false : true;
        if (isValidNetwork() && z) {
            BandwidthState bandwidthState = this.mTxState;
            bandwidthState.mLastReportedBwKbps = bandwidthState.mAvgUsedKbps < 0 ? -1 : bandwidthState.mFilterKbps;
            BandwidthState bandwidthState2 = this.mRxState;
            int i = bandwidthState2.mAvgUsedKbps >= 0 ? bandwidthState2.mFilterKbps : -1;
            bandwidthState2.mLastReportedBwKbps = i;
            sendLinkBandwidthToDataConnection(bandwidthState.mLastReportedBwKbps, i);
        }
        this.mBandwidthUpdateSignalDbm = this.mSignalStrengthDbm;
        this.mBandwidthUpdateSignalLevel = this.mSignalLevel;
        this.mBandwidthUpdateDataRat = this.mDataRat;
        this.mBandwidthUpdatePlmn = this.mPlmn;
        this.mTxState.calculateError();
        this.mRxState.calculateError();
    }

    private boolean isValidNetwork() {
        return (this.mPlmn.equals(PhoneConfigurationManager.SSSS) || this.mDataRat == 0) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class BandwidthState {
        int mAvgUsedKbps;
        int mBwSampleKbps;
        boolean mBwSampleValid;
        long mBwSampleValidTimeMs;
        int mByteDeltaAccThr = LinkBandwidthEstimator.BYTE_DELTA_THRESHOLD_KB[0][0];
        int mFilterKbps;
        int mLastReportedBwKbps;
        private final int mLink;
        int mStaticBwKbps;

        BandwidthState(int i) {
            this.mLink = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateBandwidthSample(long j, long j2) {
            updateByteCountThr();
            if (j >= this.mByteDeltaAccThr && j2 >= 200) {
                long j3 = (((j * 8) / j2) * 1000) / 1024;
                if (j3 > this.mStaticBwKbps * 15 || j3 < 0) {
                    return;
                }
                int i = (int) j3;
                this.mBwSampleValid = true;
                this.mBwSampleKbps = i;
                LinkBandwidthEstimator linkBandwidthEstimator = LinkBandwidthEstimator.this;
                String dataRatName = linkBandwidthEstimator.getDataRatName(linkBandwidthEstimator.mDataRat);
                LinkBandwidthEstimator linkBandwidthEstimator2 = LinkBandwidthEstimator.this;
                long j4 = i;
                linkBandwidthEstimator2.lookupNetwork(linkBandwidthEstimator2.mPlmn, dataRatName).update(j4, this.mLink, LinkBandwidthEstimator.this.mSignalLevel);
                LinkBandwidthEstimator linkBandwidthEstimator3 = LinkBandwidthEstimator.this;
                linkBandwidthEstimator3.lookupNetwork(linkBandwidthEstimator3.mPlmn, LinkBandwidthEstimator.this.mTac, dataRatName).update(j4, this.mLink, LinkBandwidthEstimator.this.mSignalLevel);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateBandwidthFilter() {
            int avgLinkBandwidthKbps = getAvgLinkBandwidthKbps();
            int i = this.mBwSampleValid ? this.mBwSampleKbps : avgLinkBandwidthKbps;
            long elapsedSinceBootMillis = LinkBandwidthEstimator.this.mTelephonyFacade.getElapsedSinceBootMillis();
            int i2 = (int) ((elapsedSinceBootMillis - this.mBwSampleValidTimeMs) / 1000);
            if (Math.abs(LinkBandwidthEstimator.this.mBandwidthUpdateSignalDbm - LinkBandwidthEstimator.this.mSignalStrengthDbm) <= 6 && LinkBandwidthEstimator.this.mBandwidthUpdatePlmn.equals(LinkBandwidthEstimator.this.mPlmn) && LinkBandwidthEstimator.this.mBandwidthUpdateDataRat == LinkBandwidthEstimator.this.mDataRat) {
                boolean z = this.mBwSampleValid;
            }
            if (this.mBwSampleValid) {
                this.mBwSampleValidTimeMs = elapsedSinceBootMillis;
            }
            if (i == this.mFilterKbps) {
                return;
            }
            int exp = i2 > 24 ? 0 : (int) (Math.exp((i2 * (-1.0d)) / 6) * 128.0d);
            if (exp == 0) {
                this.mFilterKbps = i;
                return;
            }
            this.mFilterKbps = (int) Math.min((((this.mFilterKbps * exp) + (i * 128)) - (i * exp)) / 128, 2147483647L);
            StringBuilder sb = new StringBuilder();
            LinkBandwidthEstimator linkBandwidthEstimator = LinkBandwidthEstimator.this;
            sb.append(this.mLink);
            sb.append(" lastSampleWeight=");
            sb.append(exp);
            sb.append("/");
            sb.append(128);
            sb.append(" filterInKbps=");
            sb.append(i);
            sb.append(" avgKbps=");
            sb.append(avgLinkBandwidthKbps);
            sb.append(" filterOutKbps=");
            sb.append(this.mFilterKbps);
            linkBandwidthEstimator.logv(sb.toString());
        }

        private int getAvgUsedLinkBandwidthKbps() {
            LinkBandwidthEstimator linkBandwidthEstimator = LinkBandwidthEstimator.this;
            String dataRatName = linkBandwidthEstimator.getDataRatName(linkBandwidthEstimator.mDataRat);
            LinkBandwidthEstimator linkBandwidthEstimator2 = LinkBandwidthEstimator.this;
            NetworkBandwidth lookupNetwork = linkBandwidthEstimator2.lookupNetwork(linkBandwidthEstimator2.mPlmn, LinkBandwidthEstimator.this.mTac, dataRatName);
            int count = lookupNetwork.getCount(this.mLink, LinkBandwidthEstimator.this.mSignalLevel);
            if (count >= 5) {
                return (int) (lookupNetwork.getValue(this.mLink, LinkBandwidthEstimator.this.mSignalLevel) / count);
            }
            LinkBandwidthEstimator linkBandwidthEstimator3 = LinkBandwidthEstimator.this;
            NetworkBandwidth lookupNetwork2 = linkBandwidthEstimator3.lookupNetwork(linkBandwidthEstimator3.mPlmn, dataRatName);
            int count2 = lookupNetwork2.getCount(this.mLink, LinkBandwidthEstimator.this.mSignalLevel);
            if (count2 >= 5) {
                return (int) (lookupNetwork2.getValue(this.mLink, LinkBandwidthEstimator.this.mSignalLevel) / count2);
            }
            return -1;
        }

        private int getAvgUsedBandwidthAdjacentThreeLevelKbps() {
            LinkBandwidthEstimator linkBandwidthEstimator = LinkBandwidthEstimator.this;
            String dataRatName = linkBandwidthEstimator.getDataRatName(linkBandwidthEstimator.mDataRat);
            LinkBandwidthEstimator linkBandwidthEstimator2 = LinkBandwidthEstimator.this;
            NetworkBandwidth lookupNetwork = linkBandwidthEstimator2.lookupNetwork(linkBandwidthEstimator2.mPlmn, dataRatName);
            int avgUsedBandwidthAtLevel = getAvgUsedBandwidthAtLevel(lookupNetwork, LinkBandwidthEstimator.this.mSignalLevel - 1);
            int avgUsedBandwidthAtLevel2 = getAvgUsedBandwidthAtLevel(lookupNetwork, LinkBandwidthEstimator.this.mSignalLevel + 1);
            if (avgUsedBandwidthAtLevel <= 0 || avgUsedBandwidthAtLevel2 <= 0) {
                int i = 0;
                long j = 0;
                for (int i2 = -1; i2 <= 1; i2++) {
                    int i3 = LinkBandwidthEstimator.this.mSignalLevel + i2;
                    if (i3 >= 0 && i3 < 5) {
                        i += lookupNetwork.getCount(this.mLink, i3);
                        j += lookupNetwork.getValue(this.mLink, i3);
                    }
                }
                if (i >= 5) {
                    return (int) (j / i);
                }
                return -1;
            }
            return (avgUsedBandwidthAtLevel + avgUsedBandwidthAtLevel2) / 2;
        }

        private int getAvgUsedBandwidthAtLevel(NetworkBandwidth networkBandwidth, int i) {
            int count;
            if (i < 0 || i >= 5 || (count = networkBandwidth.getCount(this.mLink, i)) < 5) {
                return -1;
            }
            return (int) (networkBandwidth.getValue(this.mLink, i) / count);
        }

        private int getCurrentCount() {
            LinkBandwidthEstimator linkBandwidthEstimator = LinkBandwidthEstimator.this;
            String dataRatName = linkBandwidthEstimator.getDataRatName(linkBandwidthEstimator.mDataRat);
            LinkBandwidthEstimator linkBandwidthEstimator2 = LinkBandwidthEstimator.this;
            return linkBandwidthEstimator2.lookupNetwork(linkBandwidthEstimator2.mPlmn, dataRatName).getCount(this.mLink, LinkBandwidthEstimator.this.mSignalLevel);
        }

        private int getAvgLinkBandwidthKbps() {
            int avgUsedLinkBandwidthKbps = getAvgUsedLinkBandwidthKbps();
            this.mAvgUsedKbps = avgUsedLinkBandwidthKbps;
            if (avgUsedLinkBandwidthKbps > 0) {
                return avgUsedLinkBandwidthKbps;
            }
            int avgUsedBandwidthAdjacentThreeLevelKbps = getAvgUsedBandwidthAdjacentThreeLevelKbps();
            this.mAvgUsedKbps = avgUsedBandwidthAdjacentThreeLevelKbps;
            return avgUsedBandwidthAdjacentThreeLevelKbps > 0 ? avgUsedBandwidthAdjacentThreeLevelKbps : this.mStaticBwKbps;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void resetBandwidthFilter() {
            this.mBwSampleValid = false;
            this.mFilterKbps = getAvgLinkBandwidthKbps();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateByteCountThr() {
            int i = this.mStaticBwKbps;
            if (i > 5000) {
                int calculateByteCountThreshold = calculateByteCountThreshold(getAvgUsedLinkBandwidthKbps(), GbaManager.REQUEST_TIMEOUT_MS);
                int i2 = LinkBandwidthEstimator.BYTE_DELTA_THRESHOLD_KB[this.mLink][LinkBandwidthEstimator.this.mSignalLevel] * 1024;
                this.mByteDeltaAccThr = i2;
                if (calculateByteCountThreshold > 0) {
                    int max = Math.max(calculateByteCountThreshold, i2);
                    this.mByteDeltaAccThr = max;
                    this.mByteDeltaAccThr = Math.min(max, 8192000);
                    return;
                }
                return;
            }
            int calculateByteCountThreshold2 = calculateByteCountThreshold(i, GbaManager.REQUEST_TIMEOUT_MS);
            this.mByteDeltaAccThr = calculateByteCountThreshold2;
            int max2 = Math.max(calculateByteCountThreshold2, 10240);
            this.mByteDeltaAccThr = max2;
            this.mByteDeltaAccThr = Math.min(max2, LinkBandwidthEstimator.BYTE_DELTA_THRESHOLD_KB[this.mLink][0] * 1024);
        }

        private int calculateByteCountThreshold(int i, int i2) {
            return (int) Math.min((((i / 8) * i2) * 3) / 8, 2147483647L);
        }

        public boolean hasLargeBwChange() {
            return this.mAvgUsedKbps > 0 && Math.abs(this.mLastReportedBwKbps - this.mFilterKbps) * 100 > this.mLastReportedBwKbps * 15;
        }

        public void calculateError() {
            if (!this.mBwSampleValid || getCurrentCount() <= 6 || this.mAvgUsedKbps <= 0) {
                return;
            }
            int calculateErrorPercent = calculateErrorPercent(this.mLastReportedBwKbps, this.mBwSampleKbps);
            int calculateErrorPercent2 = calculateErrorPercent(this.mAvgUsedKbps, this.mBwSampleKbps);
            int calculateErrorPercent3 = calculateErrorPercent(this.mFilterKbps, this.mBwSampleKbps);
            int calculateErrorPercent4 = calculateErrorPercent(this.mStaticBwKbps, this.mBwSampleKbps);
            TelephonyMetrics telephonyMetrics = TelephonyMetrics.getInstance();
            int i = this.mLink;
            int i2 = LinkBandwidthEstimator.this.mDataRat;
            LinkBandwidthEstimator linkBandwidthEstimator = LinkBandwidthEstimator.this;
            telephonyMetrics.writeBandwidthStats(i, i2, linkBandwidthEstimator.getNrMode(linkBandwidthEstimator.mDataRat), LinkBandwidthEstimator.this.mSignalLevel, calculateErrorPercent, calculateErrorPercent4, this.mBwSampleKbps);
            StringBuilder sb = new StringBuilder();
            LinkBandwidthEstimator linkBandwidthEstimator2 = LinkBandwidthEstimator.this;
            sb.append(this.mLink);
            sb.append(" sampKbps ");
            sb.append(this.mBwSampleKbps);
            sb.append(" filtKbps ");
            sb.append(this.mFilterKbps);
            sb.append(" reportKbps ");
            sb.append(this.mLastReportedBwKbps);
            sb.append(" avgUsedKbps ");
            sb.append(this.mAvgUsedKbps);
            sb.append(" csKbps ");
            sb.append(this.mStaticBwKbps);
            sb.append(" intErrPercent ");
            sb.append(calculateErrorPercent3);
            sb.append(" avgErrPercent ");
            sb.append(calculateErrorPercent2);
            sb.append(" extErrPercent ");
            sb.append(calculateErrorPercent);
            sb.append(" csErrPercent ");
            sb.append(calculateErrorPercent4);
            linkBandwidthEstimator2.logd(sb.toString());
        }

        private int calculateErrorPercent(int i, int i2) {
            return (int) Math.max(-10000L, Math.min(((i - i2) * 100) / i2, 10000L));
        }
    }

    private void updateByteCountThr() {
        this.mTxState.updateByteCountThr();
        this.mRxState.updateByteCountThr();
    }

    private void resetBandwidthFilter() {
        this.mTxState.resetBandwidthFilter();
        this.mRxState.resetBandwidthFilter();
    }

    private void sendLinkBandwidthToDataConnection(final int i, final int i2) {
        logv("send to DC tx " + i + " rx " + i2);
        this.mLinkBandwidthEstimatorCallbacks.forEach(new Consumer() { // from class: com.android.internal.telephony.data.LinkBandwidthEstimator$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                LinkBandwidthEstimator.lambda$sendLinkBandwidthToDataConnection$5(i, i2, (LinkBandwidthEstimator.LinkBandwidthEstimatorCallback) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$sendLinkBandwidthToDataConnection$5(final int i, final int i2, final LinkBandwidthEstimatorCallback linkBandwidthEstimatorCallback) {
        linkBandwidthEstimatorCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.LinkBandwidthEstimator$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                LinkBandwidthEstimator.LinkBandwidthEstimatorCallback.this.onBandwidthChanged(i, i2);
            }
        });
    }

    private void handleSignalStrengthChanged(SignalStrength signalStrength) {
        if (signalStrength == null) {
            return;
        }
        this.mSignalStrengthDbm = signalStrength.getDbm();
        this.mSignalLevel = signalStrength.getLevel();
        updateByteCountThr();
        if (!updateDataRatCellIdentityBandwidth() && Math.abs(this.mBandwidthUpdateSignalDbm - this.mSignalStrengthDbm) > 6) {
            updateTxRxBandwidthFilterSendToDataConnection();
        }
    }

    private void registerNrStateFrequencyChange() {
        this.mPhone.getServiceStateTracker().registerForNrStateChanged(this, 7, null);
        this.mPhone.getServiceStateTracker().registerForNrFrequencyChanged(this, 6, null);
    }

    public int getDataActivity() {
        return this.mDataActivity;
    }

    public String getDataRatName(int i) {
        return getDataRatName(i, getNrMode(i));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getNrMode(int i) {
        if (i == 13 && isNrNsaConnected()) {
            return this.mPhone.getServiceState().getNrFrequencyRange() == 4 ? 3 : 2;
        } else if (i == 20) {
            return this.mPhone.getServiceState().getNrFrequencyRange() == 4 ? 5 : 4;
        } else {
            return 1;
        }
    }

    public static String getDataRatName(int i, int i2) {
        if (i == 13 && (i2 == 2 || i2 == 3)) {
            return i2 == 2 ? "NR_NSA" : "NR_NSA_MMWAVE";
        } else if (i == 20) {
            return i2 == 4 ? TelephonyManager.getNetworkTypeName(i) : "NR_MMWAVE";
        } else {
            return TelephonyManager.getNetworkTypeName(i);
        }
    }

    private boolean isNrNsaConnected() {
        return this.mPhone.getServiceState().getNrState() == 3;
    }

    private boolean updateStaticBwValue(int i) {
        Pair<Integer, Integer> staticAvgBw = getStaticAvgBw(i);
        if (staticAvgBw == null) {
            this.mTxState.mStaticBwKbps = 14;
            this.mRxState.mStaticBwKbps = 14;
            return true;
        } else if (this.mTxState.mStaticBwKbps == ((Integer) staticAvgBw.second).intValue() && this.mRxState.mStaticBwKbps == ((Integer) staticAvgBw.first).intValue()) {
            return false;
        } else {
            this.mTxState.mStaticBwKbps = ((Integer) staticAvgBw.second).intValue();
            this.mRxState.mStaticBwKbps = ((Integer) staticAvgBw.first).intValue();
            return true;
        }
    }

    public Pair<Integer, Integer> getStaticAvgBw(int i) {
        String dataRatName = getDataRatName(i);
        Pair<Integer, Integer> pair = AVG_BW_PER_RAT_MAP.get(dataRatName);
        if (pair == null) {
            String str = TAG;
            Rlog.e(str, dataRatName + " is not found in Avg BW table");
        }
        return pair;
    }

    private void updateStaticBwValueResetFilter() {
        if (updateStaticBwValue(this.mDataRat)) {
            updateByteCountThr();
            resetBandwidthFilter();
            updateTxRxBandwidthFilterSendToDataConnection();
        }
    }

    private NetworkRegistrationInfo getDataNri() {
        return this.mPhone.getServiceState().getNetworkRegistrationInfo(2, 1);
    }

    private boolean updateDataRatCellIdentityBandwidth() {
        String plmn;
        boolean z;
        int accessNetworkTechnology;
        ServiceState serviceState = this.mPhone.getServiceState();
        CellIdentity currentCellIdentity = this.mPhone.getCurrentCellIdentity();
        this.mTac = getTac(currentCellIdentity);
        if (!TextUtils.isEmpty(serviceState.getOperatorNumeric())) {
            plmn = serviceState.getOperatorNumeric();
        } else {
            plmn = (currentCellIdentity == null || TextUtils.isEmpty(currentCellIdentity.getPlmn())) ? PhoneConfigurationManager.SSSS : currentCellIdentity.getPlmn();
        }
        boolean z2 = true;
        if (this.mPlmn.equals(plmn)) {
            z = false;
        } else {
            this.mPlmn = plmn;
            z = true;
        }
        NetworkRegistrationInfo dataNri = getDataNri();
        if (dataNri == null || (accessNetworkTechnology = dataNri.getAccessNetworkTechnology()) == this.mDataRat) {
            z2 = z;
        } else {
            this.mDataRat = accessNetworkTechnology;
            updateStaticBwValue(accessNetworkTechnology);
            updateByteCountThr();
        }
        if (z2) {
            resetBandwidthFilter();
            updateTxRxBandwidthFilterSendToDataConnection();
            this.mLastPlmnOrRatChangeTimeMs = this.mTelephonyFacade.getElapsedSinceBootMillis();
        }
        return z2;
    }

    private int getTac(CellIdentity cellIdentity) {
        if (cellIdentity instanceof CellIdentityLte) {
            return ((CellIdentityLte) cellIdentity).getTac();
        }
        if (cellIdentity instanceof CellIdentityNr) {
            return ((CellIdentityNr) cellIdentity).getTac();
        }
        if (cellIdentity instanceof CellIdentityWcdma) {
            return ((CellIdentityWcdma) cellIdentity).getLac();
        }
        if (cellIdentity instanceof CellIdentityTdscdma) {
            return ((CellIdentityTdscdma) cellIdentity).getLac();
        }
        if (cellIdentity instanceof CellIdentityGsm) {
            return ((CellIdentityGsm) cellIdentity).getLac();
        }
        return Integer.MAX_VALUE;
    }

    /* loaded from: classes.dex */
    private class TelephonyCallbackImpl extends TelephonyCallback implements TelephonyCallback.SignalStrengthsListener, TelephonyCallback.ActiveDataSubscriptionIdListener {
        private TelephonyCallbackImpl() {
        }

        @Override // android.telephony.TelephonyCallback.SignalStrengthsListener
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            LinkBandwidthEstimator.this.obtainMessage(5, signalStrength).sendToTarget();
        }

        @Override // android.telephony.TelephonyCallback.ActiveDataSubscriptionIdListener
        public void onActiveDataSubscriptionIdChanged(int i) {
            LinkBandwidthEstimator.this.obtainMessage(8, Integer.valueOf(i)).sendToTarget();
        }
    }

    void logd(String str) {
        this.mLocalLog.log(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class NetworkKey {
        private final String mDataRat;
        private final String mPlmn;
        private final int mTac;

        NetworkKey(String str, int i, String str2) {
            this.mPlmn = str;
            this.mTac = i;
            this.mDataRat = str2;
        }

        public boolean equals(Object obj) {
            if (obj != null && (obj instanceof NetworkKey) && hashCode() == obj.hashCode()) {
                if (this == obj) {
                    return true;
                }
                NetworkKey networkKey = (NetworkKey) obj;
                return this.mPlmn.equals(networkKey.mPlmn) && this.mTac == networkKey.mTac && this.mDataRat.equals(networkKey.mDataRat);
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.mPlmn, this.mDataRat, Integer.valueOf(this.mTac));
        }

        public String toString() {
            return "Plmn" + this.mPlmn + "Rat" + this.mDataRat + "Tac" + this.mTac;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public NetworkBandwidth lookupNetwork(String str, String str2) {
        return lookupNetwork(str, Integer.MAX_VALUE, str2);
    }

    @VisibleForTesting
    public NetworkBandwidth lookupNetwork(String str, int i, String str2) {
        if (str2.equals(TelephonyManager.getNetworkTypeName(0))) {
            return this.mPlaceholderNetwork;
        }
        NetworkKey networkKey = new NetworkKey(str, i, str2);
        NetworkBandwidth networkBandwidth = this.mNetworkMap.get(networkKey);
        if (networkBandwidth == null) {
            NetworkBandwidth networkBandwidth2 = new NetworkBandwidth(networkKey.toString());
            this.mNetworkMap.put(networkKey, networkBandwidth2);
            return networkBandwidth2;
        }
        return networkBandwidth;
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public class NetworkBandwidth {
        private final String mKey;

        NetworkBandwidth(String str) {
            this.mKey = str;
        }

        public void update(long j, int i, int i2) {
            SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(LinkBandwidthEstimator.this.mPhone.getContext());
            String valueKey = getValueKey(i, i2);
            String countKey = getCountKey(i, i2);
            SharedPreferences.Editor edit = defaultSharedPreferences.edit();
            long j2 = defaultSharedPreferences.getLong(valueKey, 0L);
            int i3 = defaultSharedPreferences.getInt(countKey, 0);
            edit.putLong(valueKey, j2 + j);
            edit.putInt(countKey, i3 + 1);
            edit.apply();
        }

        private String getValueKey(int i, int i2) {
            return getDataKey(i, i2) + "Data";
        }

        private String getCountKey(int i, int i2) {
            return getDataKey(i, i2) + "Count";
        }

        private String getDataKey(int i, int i2) {
            return this.mKey + "Link" + i + "Level" + i2;
        }

        public long getValue(int i, int i2) {
            return PreferenceManager.getDefaultSharedPreferences(LinkBandwidthEstimator.this.mPhone.getContext()).getLong(getValueKey(i, i2), 0L);
        }

        public int getCount(int i, int i2) {
            return PreferenceManager.getDefaultSharedPreferences(LinkBandwidthEstimator.this.mPhone.getContext()).getInt(getCountKey(i, i2), 0);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.mKey);
            sb.append("\n");
            int i = 0;
            while (i < 2) {
                sb.append(i == 0 ? "tx" : "rx");
                sb.append("\n avgKbps");
                for (int i2 = 0; i2 < 5; i2++) {
                    int count = getCount(i, i2);
                    int value = count == 0 ? 0 : (int) (getValue(i, i2) / count);
                    sb.append(" ");
                    sb.append(value);
                }
                sb.append("\n count");
                for (int i3 = 0; i3 < 5; i3++) {
                    int count2 = getCount(i, i3);
                    sb.append(" ");
                    sb.append(count2);
                }
                sb.append("\n");
                i++;
            }
            return sb.toString();
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, " ");
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.println("current PLMN " + this.mPlmn + " TAC " + this.mTac + " RAT " + getDataRatName(this.mDataRat));
        indentingPrintWriter.println("all networks visited since device boot");
        for (NetworkBandwidth networkBandwidth : this.mNetworkMap.values()) {
            indentingPrintWriter.println(networkBandwidth.toString());
        }
        try {
            this.mLocalLog.dump(fileDescriptor, indentingPrintWriter, strArr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println();
        indentingPrintWriter.flush();
    }
}
