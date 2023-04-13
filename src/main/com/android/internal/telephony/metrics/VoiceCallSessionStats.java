package com.android.internal.telephony.metrics;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.telephony.AnomalyReporter;
import android.telephony.ServiceState;
import android.telephony.ims.ImsReasonInfo;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.Call;
import com.android.internal.telephony.CarrierServicesSmsFilter;
import com.android.internal.telephony.Connection;
import com.android.internal.telephony.GsmCdmaConnection;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.ServiceStateTracker;
import com.android.internal.telephony.imsphone.ImsPhoneConnection;
import com.android.internal.telephony.nano.PersistAtomsProto$VoiceCallSession;
import com.android.internal.telephony.uicc.UiccController;
import com.android.telephony.Rlog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class VoiceCallSessionStats {
    private static final String TAG = "VoiceCallSessionStats";
    private final Phone mPhone;
    private final int mPhoneId;
    private static final SparseIntArray CS_CODEC_MAP = buildGsmCdmaCodecMap();
    private static final SparseIntArray IMS_CODEC_MAP = buildImsCodecMap();
    private static final SparseIntArray CALL_DURATION_MAP = buildCallDurationMap();
    private static final UUID CONCURRENT_CALL_ANOMALY_UUID = UUID.fromString("76780b5a-623e-48a4-be3f-925e05177c9c");
    private final SparseArray<PersistAtomsProto$VoiceCallSession> mCallProtos = new SparseArray<>();
    private final SparseArray<LongSparseArray<Integer>> mCodecUsage = new SparseArray<>();
    private final VoiceCallRatTracker mRatUsage = new VoiceCallRatTracker();
    private final PersistAtomsStorage mAtomsStorage = PhoneFactory.getMetricsCollector().getAtomsStorage();
    private final UiccController mUiccController = UiccController.getInstance();

    private int getCodecQuality(int i) {
        switch (i) {
            case 1:
            case 3:
            case 4:
            case 5:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
                return 1;
            case 2:
            case 6:
            case 18:
                return 2;
            case 19:
                return 3;
            case 20:
                return 4;
            default:
                return 0;
        }
    }

    public VoiceCallSessionStats(int i, Phone phone) {
        this.mPhoneId = i;
        this.mPhone = phone;
    }

    public synchronized void onRilAcceptCall(List<Connection> list) {
        for (Connection connection : list) {
            acceptCall(connection);
        }
    }

    public synchronized void onRilDial(Connection connection) {
        addCall(connection);
    }

    public synchronized void onRilCallListChanged(List<GsmCdmaConnection> list) {
        for (GsmCdmaConnection gsmCdmaConnection : list) {
            int connectionId = getConnectionId(gsmCdmaConnection);
            if (!this.mCallProtos.contains(connectionId)) {
                if (gsmCdmaConnection.getDisconnectCause() == 0) {
                    addCall(gsmCdmaConnection);
                    checkCallSetup(gsmCdmaConnection, this.mCallProtos.get(connectionId));
                } else {
                    logd("onRilCallListChanged: skip adding disconnected connection, connectionId=%d", Integer.valueOf(connectionId));
                }
            } else {
                PersistAtomsProto$VoiceCallSession persistAtomsProto$VoiceCallSession = this.mCallProtos.get(connectionId);
                checkCallSetup(gsmCdmaConnection, persistAtomsProto$VoiceCallSession);
                if (gsmCdmaConnection.getDisconnectCause() != 0) {
                    persistAtomsProto$VoiceCallSession.bearerAtEnd = getBearer(gsmCdmaConnection);
                    persistAtomsProto$VoiceCallSession.disconnectReasonCode = gsmCdmaConnection.getDisconnectCause();
                    persistAtomsProto$VoiceCallSession.disconnectExtraCode = gsmCdmaConnection.getPreciseDisconnectCause();
                    persistAtomsProto$VoiceCallSession.disconnectExtraMessage = gsmCdmaConnection.getVendorDisconnectCause();
                    persistAtomsProto$VoiceCallSession.callDuration = classifyCallDuration(gsmCdmaConnection.getDurationMillis());
                    finishCall(connectionId);
                }
            }
        }
    }

    public synchronized void onImsDial(ImsPhoneConnection imsPhoneConnection) {
        addCall(imsPhoneConnection);
        if (imsPhoneConnection.hasRttTextStream()) {
            setRttStarted(imsPhoneConnection);
        }
    }

    public synchronized void onImsCallReceived(ImsPhoneConnection imsPhoneConnection) {
        addCall(imsPhoneConnection);
        if (imsPhoneConnection.hasRttTextStream()) {
            setRttStarted(imsPhoneConnection);
        }
    }

    public synchronized void onImsAcceptCall(List<Connection> list) {
        for (Connection connection : list) {
            acceptCall(connection);
        }
    }

    public synchronized void onImsCallStartFailed(ImsPhoneConnection imsPhoneConnection, ImsReasonInfo imsReasonInfo) {
        onImsCallTerminated(imsPhoneConnection, imsReasonInfo);
    }

    public synchronized void onImsCallTerminated(ImsPhoneConnection imsPhoneConnection, ImsReasonInfo imsReasonInfo) {
        if (imsPhoneConnection == null) {
            List<Integer> imsConnectionIds = getImsConnectionIds();
            if (imsConnectionIds.size() == 1) {
                loge("onImsCallTerminated: ending IMS call w/ conn=null", new Object[0]);
                finishImsCall(imsConnectionIds.get(0).intValue(), imsReasonInfo, 0L);
            } else {
                loge("onImsCallTerminated: %d IMS calls w/ conn=null", Integer.valueOf(imsConnectionIds.size()));
            }
        } else {
            int connectionId = getConnectionId(imsPhoneConnection);
            if (this.mCallProtos.contains(connectionId)) {
                finishImsCall(connectionId, imsReasonInfo, imsPhoneConnection.getDurationMillis());
            } else {
                loge("onImsCallTerminated: untracked connection, connectionId=%d", Integer.valueOf(connectionId));
                addCall(imsPhoneConnection);
                finishImsCall(connectionId, imsReasonInfo, imsPhoneConnection.getDurationMillis());
            }
        }
    }

    public synchronized void onRttStarted(ImsPhoneConnection imsPhoneConnection) {
        setRttStarted(imsPhoneConnection);
    }

    public synchronized void onAudioCodecChanged(Connection connection, int i) {
        int connectionId = getConnectionId(connection);
        PersistAtomsProto$VoiceCallSession persistAtomsProto$VoiceCallSession = this.mCallProtos.get(connectionId);
        if (persistAtomsProto$VoiceCallSession == null) {
            loge("onAudioCodecChanged: untracked connection, connectionId=%d", Integer.valueOf(connectionId));
            return;
        }
        int audioQualityToCodec = audioQualityToCodec(persistAtomsProto$VoiceCallSession.bearerAtEnd, i);
        persistAtomsProto$VoiceCallSession.codecBitmask |= 1 << audioQualityToCodec;
        if (this.mCodecUsage.contains(connectionId)) {
            this.mCodecUsage.get(connectionId).append(getTimeMillis(), Integer.valueOf(audioQualityToCodec));
        } else {
            LongSparseArray<Integer> longSparseArray = new LongSparseArray<>();
            longSparseArray.append(getTimeMillis(), Integer.valueOf(audioQualityToCodec));
            this.mCodecUsage.put(connectionId, longSparseArray);
        }
    }

    public synchronized void onVideoStateChange(ImsPhoneConnection imsPhoneConnection, int i) {
        int connectionId = getConnectionId(imsPhoneConnection);
        PersistAtomsProto$VoiceCallSession persistAtomsProto$VoiceCallSession = this.mCallProtos.get(connectionId);
        if (persistAtomsProto$VoiceCallSession == null) {
            loge("onVideoStateChange: untracked connection, connectionId=%d", Integer.valueOf(connectionId));
            return;
        }
        logd("onVideoStateChange: video state=%d, connectionId=%d", Integer.valueOf(i), Integer.valueOf(connectionId));
        if (i != 0) {
            persistAtomsProto$VoiceCallSession.videoEnabled = true;
        }
    }

    public synchronized void onMultipartyChange(ImsPhoneConnection imsPhoneConnection, boolean z) {
        int connectionId = getConnectionId(imsPhoneConnection);
        PersistAtomsProto$VoiceCallSession persistAtomsProto$VoiceCallSession = this.mCallProtos.get(connectionId);
        if (persistAtomsProto$VoiceCallSession == null) {
            loge("onMultipartyChange: untracked connection, connectionId=%d", Integer.valueOf(connectionId));
            return;
        }
        logd("onMultipartyChange: isMultiparty=%b, connectionId=%d", Boolean.valueOf(z), Integer.valueOf(connectionId));
        if (z) {
            persistAtomsProto$VoiceCallSession.isMultiparty = true;
        }
    }

    public synchronized void onCallStateChanged(Call call) {
        Iterator<Connection> it = call.getConnections().iterator();
        while (it.hasNext()) {
            Connection next = it.next();
            int connectionId = getConnectionId(next);
            PersistAtomsProto$VoiceCallSession persistAtomsProto$VoiceCallSession = this.mCallProtos.get(connectionId);
            if (persistAtomsProto$VoiceCallSession != null) {
                checkCallSetup(next, persistAtomsProto$VoiceCallSession);
            } else {
                loge("onCallStateChanged: untracked connection, connectionId=%d", Integer.valueOf(connectionId));
            }
        }
    }

    public synchronized void onRilSrvccStateChanged(int i) {
        ArrayList<Connection> arrayList;
        List<Integer> list;
        if (this.mPhone.getImsPhone() != null) {
            arrayList = this.mPhone.getImsPhone().getHandoverConnection();
        } else {
            loge("onRilSrvccStateChanged: ImsPhone is null", new Object[0]);
            arrayList = null;
        }
        if (arrayList == null) {
            list = getImsConnectionIds();
            loge("onRilSrvccStateChanged: ImsPhone has no handover, we have %d", Integer.valueOf(list.size()));
        } else {
            list = (List) arrayList.stream().map(new Function() { // from class: com.android.internal.telephony.metrics.VoiceCallSessionStats$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    int connectionId;
                    connectionId = VoiceCallSessionStats.getConnectionId((Connection) obj);
                    return Integer.valueOf(connectionId);
                }
            }).collect(Collectors.toList());
        }
        if (i == 1) {
            for (Integer num : list) {
                PersistAtomsProto$VoiceCallSession persistAtomsProto$VoiceCallSession = this.mCallProtos.get(num.intValue());
                persistAtomsProto$VoiceCallSession.srvccCompleted = true;
                persistAtomsProto$VoiceCallSession.bearerAtEnd = 1;
                Phone phone = this.mPhone;
                persistAtomsProto$VoiceCallSession.ratAtEnd = ServiceStateStats.getVoiceRat(phone, phone.getServiceState(), persistAtomsProto$VoiceCallSession.bearerAtEnd);
            }
        } else if (i == 2) {
            for (Integer num2 : list) {
                this.mCallProtos.get(num2.intValue()).srvccFailureCount++;
            }
        } else if (i == 3) {
            for (Integer num3 : list) {
                this.mCallProtos.get(num3.intValue()).srvccCancellationCount++;
            }
        }
    }

    public synchronized void onServiceStateChanged(ServiceState serviceState) {
        if (hasCalls()) {
            updateRatTracker(serviceState);
        }
    }

    private void acceptCall(Connection connection) {
        int connectionId = getConnectionId(connection);
        if (this.mCallProtos.contains(connectionId)) {
            logd("acceptCall: resetting setup info, connectionId=%d", Integer.valueOf(connectionId));
            this.mCallProtos.get(connectionId).setupBeginMillis = getTimeMillis();
            return;
        }
        loge("acceptCall: untracked connection, connectionId=%d", Integer.valueOf(connectionId));
    }

    private void addCall(Connection connection) {
        int connectionId = getConnectionId(connection);
        if (this.mCallProtos.contains(connectionId)) {
            loge("addCall: already tracked connection, connectionId=%d, connectionInfo=%s", Integer.valueOf(connectionId), connection);
            return;
        }
        int bearer = getBearer(connection);
        ServiceState serviceState = getServiceState();
        int voiceRat = ServiceStateStats.getVoiceRat(this.mPhone, serviceState, bearer);
        PersistAtomsProto$VoiceCallSession persistAtomsProto$VoiceCallSession = new PersistAtomsProto$VoiceCallSession();
        persistAtomsProto$VoiceCallSession.bearerAtStart = bearer;
        persistAtomsProto$VoiceCallSession.bearerAtEnd = bearer;
        persistAtomsProto$VoiceCallSession.direction = getDirection(connection);
        boolean z = true;
        persistAtomsProto$VoiceCallSession.setupFailed = true;
        persistAtomsProto$VoiceCallSession.disconnectReasonCode = connection.getDisconnectCause();
        persistAtomsProto$VoiceCallSession.disconnectExtraCode = connection.getPreciseDisconnectCause();
        persistAtomsProto$VoiceCallSession.disconnectExtraMessage = connection.getVendorDisconnectCause();
        persistAtomsProto$VoiceCallSession.ratAtStart = voiceRat;
        persistAtomsProto$VoiceCallSession.ratAtConnected = 0;
        persistAtomsProto$VoiceCallSession.ratAtEnd = voiceRat;
        persistAtomsProto$VoiceCallSession.ratSwitchCount = 0L;
        persistAtomsProto$VoiceCallSession.codecBitmask = 0L;
        persistAtomsProto$VoiceCallSession.simSlotIndex = this.mPhoneId;
        persistAtomsProto$VoiceCallSession.isMultiSim = SimSlotState.isMultiSim();
        persistAtomsProto$VoiceCallSession.isEsim = SimSlotState.isEsim(this.mPhoneId);
        persistAtomsProto$VoiceCallSession.carrierId = this.mPhone.getCarrierId();
        persistAtomsProto$VoiceCallSession.srvccCompleted = false;
        persistAtomsProto$VoiceCallSession.srvccFailureCount = 0L;
        persistAtomsProto$VoiceCallSession.srvccCancellationCount = 0L;
        persistAtomsProto$VoiceCallSession.rttEnabled = false;
        if (!connection.isEmergencyCall() && !connection.isNetworkIdentifiedEmergencyCall()) {
            z = false;
        }
        persistAtomsProto$VoiceCallSession.isEmergency = z;
        persistAtomsProto$VoiceCallSession.isRoaming = serviceState != null ? serviceState.getVoiceRoaming() : false;
        persistAtomsProto$VoiceCallSession.isMultiparty = connection.isMultiparty();
        persistAtomsProto$VoiceCallSession.lastKnownRat = voiceRat;
        if (getDirection(connection) == 2) {
            persistAtomsProto$VoiceCallSession.setupBeginMillis = 0L;
        } else {
            persistAtomsProto$VoiceCallSession.setupBeginMillis = getTimeMillis();
        }
        int audioQualityToCodec = audioQualityToCodec(bearer, connection.getAudioCodec());
        if (audioQualityToCodec != 0) {
            persistAtomsProto$VoiceCallSession.codecBitmask = 1 << audioQualityToCodec;
        }
        int size = this.mCallProtos.size();
        persistAtomsProto$VoiceCallSession.concurrentCallCountAtStart = size;
        if (size > 3) {
            AnomalyReporter.reportAnomaly(CONCURRENT_CALL_ANOMALY_UUID, "Anomalous number of concurrent calls");
        }
        this.mCallProtos.put(connectionId, persistAtomsProto$VoiceCallSession);
        updateRatTracker(serviceState);
    }

    private void finishCall(int i) {
        PersistAtomsProto$VoiceCallSession persistAtomsProto$VoiceCallSession = this.mCallProtos.get(i);
        if (persistAtomsProto$VoiceCallSession == null) {
            loge("finishCall: could not find call to be removed, connectionId=%d", Integer.valueOf(i));
            return;
        }
        if (persistAtomsProto$VoiceCallSession.setupFailed && persistAtomsProto$VoiceCallSession.setupBeginMillis != 0 && persistAtomsProto$VoiceCallSession.setupDurationMillis == 0) {
            persistAtomsProto$VoiceCallSession.setupDurationMillis = (int) (getTimeMillis() - persistAtomsProto$VoiceCallSession.setupBeginMillis);
        }
        this.mCallProtos.delete(i);
        persistAtomsProto$VoiceCallSession.concurrentCallCountAtEnd = this.mCallProtos.size();
        persistAtomsProto$VoiceCallSession.signalStrengthAtEnd = getSignalStrength(persistAtomsProto$VoiceCallSession.ratAtEnd);
        persistAtomsProto$VoiceCallSession.mainCodecQuality = finalizeMainCodecQuality(i);
        persistAtomsProto$VoiceCallSession.setupBeginMillis = 0L;
        if (persistAtomsProto$VoiceCallSession.disconnectExtraMessage == null) {
            persistAtomsProto$VoiceCallSession.disconnectExtraMessage = PhoneConfigurationManager.SSSS;
        }
        if (persistAtomsProto$VoiceCallSession.carrierId <= 0) {
            persistAtomsProto$VoiceCallSession.carrierId = this.mPhone.getCarrierId();
        }
        int voiceRat = ServiceStateStats.getVoiceRat(this.mPhone, getServiceState(), persistAtomsProto$VoiceCallSession.bearerAtEnd);
        if (persistAtomsProto$VoiceCallSession.ratAtEnd != voiceRat) {
            persistAtomsProto$VoiceCallSession.ratSwitchCount++;
            persistAtomsProto$VoiceCallSession.ratAtEnd = voiceRat;
            if (voiceRat != 0) {
                persistAtomsProto$VoiceCallSession.lastKnownRat = voiceRat;
            }
        }
        this.mAtomsStorage.addVoiceCallSession(persistAtomsProto$VoiceCallSession);
        if (hasCalls()) {
            return;
        }
        this.mRatUsage.conclude(getTimeMillis());
        this.mAtomsStorage.addVoiceCallRatUsage(this.mRatUsage);
        this.mRatUsage.clear();
    }

    private void setRttStarted(ImsPhoneConnection imsPhoneConnection) {
        int connectionId = getConnectionId(imsPhoneConnection);
        PersistAtomsProto$VoiceCallSession persistAtomsProto$VoiceCallSession = this.mCallProtos.get(connectionId);
        if (persistAtomsProto$VoiceCallSession == null) {
            loge("onRttStarted: untracked connection, connectionId=%d", Integer.valueOf(connectionId));
            return;
        }
        if (persistAtomsProto$VoiceCallSession.bearerAtStart != getBearer(imsPhoneConnection) || persistAtomsProto$VoiceCallSession.bearerAtEnd != getBearer(imsPhoneConnection)) {
            loge("onRttStarted: connection bearer mismatch but proceeding, connectionId=%d", Integer.valueOf(connectionId));
        }
        persistAtomsProto$VoiceCallSession.rttEnabled = true;
    }

    private Set<Integer> getConnectionIds() {
        HashSet hashSet = new HashSet();
        for (int i = 0; i < this.mCallProtos.size(); i++) {
            hashSet.add(Integer.valueOf(this.mCallProtos.keyAt(i)));
        }
        return hashSet;
    }

    private List<Integer> getImsConnectionIds() {
        ArrayList arrayList = new ArrayList(this.mCallProtos.size());
        for (int i = 0; i < this.mCallProtos.size(); i++) {
            if (this.mCallProtos.valueAt(i).bearerAtEnd == 2) {
                arrayList.add(Integer.valueOf(this.mCallProtos.keyAt(i)));
            }
        }
        return arrayList;
    }

    private boolean hasCalls() {
        return this.mCallProtos.size() > 0;
    }

    private void checkCallSetup(Connection connection, PersistAtomsProto$VoiceCallSession persistAtomsProto$VoiceCallSession) {
        if (persistAtomsProto$VoiceCallSession.setupBeginMillis != 0 && isSetupFinished(connection.getCall())) {
            persistAtomsProto$VoiceCallSession.setupDurationMillis = (int) (getTimeMillis() - persistAtomsProto$VoiceCallSession.setupBeginMillis);
            persistAtomsProto$VoiceCallSession.setupBeginMillis = 0L;
        }
        if (persistAtomsProto$VoiceCallSession.setupFailed && connection.getState() == Call.State.ACTIVE) {
            persistAtomsProto$VoiceCallSession.setupFailed = false;
            persistAtomsProto$VoiceCallSession.ratAtConnected = ServiceStateStats.getVoiceRat(this.mPhone, getServiceState(), persistAtomsProto$VoiceCallSession.bearerAtEnd);
            resetCodecList(connection);
        }
    }

    private void updateRatTracker(ServiceState serviceState) {
        this.mRatUsage.add(this.mPhone.getCarrierId(), ServiceStateStats.getVoiceRat(this.mPhone, serviceState), getTimeMillis(), getConnectionIds());
        for (int i = 0; i < this.mCallProtos.size(); i++) {
            PersistAtomsProto$VoiceCallSession valueAt = this.mCallProtos.valueAt(i);
            int voiceRat = ServiceStateStats.getVoiceRat(this.mPhone, serviceState, valueAt.bearerAtEnd);
            if (valueAt.ratAtEnd != voiceRat) {
                valueAt.ratSwitchCount++;
                valueAt.ratAtEnd = voiceRat;
                if (voiceRat != 0) {
                    valueAt.lastKnownRat = voiceRat;
                }
            }
            valueAt.bandAtEnd = voiceRat == 18 ? 0 : ServiceStateStats.getBand(serviceState);
        }
    }

    private void finishImsCall(int i, ImsReasonInfo imsReasonInfo, long j) {
        PersistAtomsProto$VoiceCallSession persistAtomsProto$VoiceCallSession = this.mCallProtos.get(i);
        persistAtomsProto$VoiceCallSession.bearerAtEnd = 2;
        persistAtomsProto$VoiceCallSession.disconnectReasonCode = imsReasonInfo.mCode;
        persistAtomsProto$VoiceCallSession.disconnectExtraCode = imsReasonInfo.mExtraCode;
        persistAtomsProto$VoiceCallSession.disconnectExtraMessage = ImsStats.filterExtraMessage(imsReasonInfo.mExtraMessage);
        persistAtomsProto$VoiceCallSession.callDuration = classifyCallDuration(j);
        finishCall(i);
    }

    private ServiceState getServiceState() {
        ServiceStateTracker serviceStateTracker = this.mPhone.getServiceStateTracker();
        if (serviceStateTracker != null) {
            return serviceStateTracker.getServiceState();
        }
        return null;
    }

    private static int getDirection(Connection connection) {
        return connection.isIncoming() ? 2 : 1;
    }

    private static int getBearer(Connection connection) {
        int phoneType = connection.getPhoneType();
        if (phoneType == 1 || phoneType == 2) {
            return 1;
        }
        if (phoneType != 5) {
            loge("getBearer: unknown phoneType=%d", Integer.valueOf(phoneType));
            return 0;
        }
        return 2;
    }

    private int getSignalStrength(int i) {
        if (i == 18) {
            return getSignalStrengthWifi();
        }
        return getSignalStrengthCellular();
    }

    private int getSignalStrengthWifi() {
        WifiManager wifiManager = (WifiManager) this.mPhone.getContext().getSystemService("wifi");
        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
        if (connectionInfo != null) {
            int calculateSignalLevel = wifiManager.calculateSignalLevel(connectionInfo.getRssi());
            int maxSignalLevel = wifiManager.getMaxSignalLevel();
            int i = (calculateSignalLevel * 4) / maxSignalLevel;
            logd("WiFi level: " + i + " (" + calculateSignalLevel + "/" + maxSignalLevel + ")", new Object[0]);
            return i;
        }
        return 0;
    }

    private int getSignalStrengthCellular() {
        return this.mPhone.getSignalStrength().getLevel();
    }

    private void resetCodecList(Connection connection) {
        int connectionId = getConnectionId(connection);
        LongSparseArray<Integer> longSparseArray = this.mCodecUsage.get(connectionId);
        if (longSparseArray != null) {
            int intValue = longSparseArray.valueAt(longSparseArray.size() - 1).intValue();
            LongSparseArray<Integer> longSparseArray2 = new LongSparseArray<>();
            longSparseArray2.append(getTimeMillis(), Integer.valueOf(intValue));
            this.mCodecUsage.put(connectionId, longSparseArray2);
        }
    }

    private int finalizeMainCodecQuality(int i) {
        if (this.mCodecUsage.contains(i)) {
            LongSparseArray<Integer> longSparseArray = this.mCodecUsage.get(i);
            this.mCodecUsage.delete(i);
            longSparseArray.put(getTimeMillis(), 0);
            long[] jArr = new long[5];
            int i2 = 0;
            int i3 = 0;
            while (i2 < longSparseArray.size() - 1) {
                int i4 = i2 + 1;
                long keyAt = longSparseArray.keyAt(i4) - longSparseArray.keyAt(i2);
                int codecQuality = getCodecQuality(longSparseArray.valueAt(i2).intValue());
                jArr[codecQuality] = jArr[codecQuality] + keyAt;
                i3 = (int) (i3 + keyAt);
                i2 = i4;
            }
            logd("Time per codec quality = " + Arrays.toString(jArr), new Object[0]);
            long j = (long) ((i3 * 70) / 100);
            long j2 = 0;
            for (int i5 = 4; i5 >= 0; i5--) {
                j2 += jArr[i5];
                if (j2 >= j) {
                    return i5;
                }
            }
            return 0;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.internal.telephony.metrics.VoiceCallSessionStats$1 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class C02661 {
        static final /* synthetic */ int[] $SwitchMap$com$android$internal$telephony$Call$State;

        static {
            int[] iArr = new int[Call.State.values().length];
            $SwitchMap$com$android$internal$telephony$Call$State = iArr;
            try {
                iArr[Call.State.ACTIVE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$Call$State[Call.State.ALERTING.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    private static boolean isSetupFinished(Call call) {
        if (call != null) {
            int i = C02661.$SwitchMap$com$android$internal$telephony$Call$State[call.getState().ordinal()];
            return i == 1 || i == 2;
        }
        return false;
    }

    private static int audioQualityToCodec(int i, int i2) {
        if (i != 1) {
            if (i == 2) {
                return IMS_CODEC_MAP.get(i2, 0);
            }
            loge("audioQualityToCodec: unknown bearer %d", Integer.valueOf(i));
            return 0;
        }
        return CS_CODEC_MAP.get(i2, 0);
    }

    private static int classifyCallDuration(long j) {
        int i = 0;
        if (j == 0) {
            return 0;
        }
        while (true) {
            SparseIntArray sparseIntArray = CALL_DURATION_MAP;
            if (i >= sparseIntArray.size()) {
                return 6;
            }
            if (j < sparseIntArray.keyAt(i)) {
                return sparseIntArray.valueAt(i);
            }
            i++;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int getConnectionId(Connection connection) {
        if (connection == null) {
            return 0;
        }
        return (int) connection.getCreateTime();
    }

    @VisibleForTesting
    protected long getTimeMillis() {
        return SystemClock.elapsedRealtime();
    }

    private static void logd(String str, Object... objArr) {
        Rlog.d(TAG, String.format(str, objArr));
    }

    private static void loge(String str, Object... objArr) {
        Rlog.e(TAG, String.format(str, objArr));
    }

    private static SparseIntArray buildGsmCdmaCodecMap() {
        SparseIntArray sparseIntArray = new SparseIntArray();
        sparseIntArray.put(1, 1);
        sparseIntArray.put(2, 2);
        sparseIntArray.put(3, 8);
        sparseIntArray.put(4, 9);
        sparseIntArray.put(5, 10);
        sparseIntArray.put(6, 4);
        sparseIntArray.put(7, 5);
        sparseIntArray.put(8, 6);
        sparseIntArray.put(9, 7);
        return sparseIntArray;
    }

    private static SparseIntArray buildImsCodecMap() {
        SparseIntArray sparseIntArray = new SparseIntArray();
        sparseIntArray.put(1, 1);
        sparseIntArray.put(2, 2);
        sparseIntArray.put(3, 3);
        sparseIntArray.put(4, 4);
        sparseIntArray.put(5, 5);
        sparseIntArray.put(6, 6);
        sparseIntArray.put(7, 7);
        sparseIntArray.put(8, 8);
        sparseIntArray.put(9, 9);
        sparseIntArray.put(10, 10);
        sparseIntArray.put(11, 11);
        sparseIntArray.put(12, 12);
        sparseIntArray.put(13, 13);
        sparseIntArray.put(14, 14);
        sparseIntArray.put(15, 15);
        sparseIntArray.put(16, 16);
        sparseIntArray.put(17, 17);
        sparseIntArray.put(18, 18);
        sparseIntArray.put(19, 19);
        sparseIntArray.put(20, 20);
        return sparseIntArray;
    }

    private static SparseIntArray buildCallDurationMap() {
        SparseIntArray sparseIntArray = new SparseIntArray();
        sparseIntArray.put(ServiceStateTracker.DEFAULT_GPRS_CHECK_PERIOD_MILLIS, 1);
        sparseIntArray.put(300000, 2);
        sparseIntArray.put(CarrierServicesSmsFilter.FILTER_COMPLETE_TIMEOUT_MS, 3);
        sparseIntArray.put(1800000, 4);
        sparseIntArray.put(3600000, 5);
        return sparseIntArray;
    }
}
