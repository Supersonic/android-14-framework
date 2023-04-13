package com.android.internal.telephony.metrics;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.SparseIntArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.RILUtils$$ExternalSyntheticLambda6;
import com.android.internal.telephony.data.KeepaliveStatus;
import com.android.internal.telephony.nano.PersistAtomsProto$CarrierIdMismatch;
import com.android.internal.telephony.nano.PersistAtomsProto$CellularDataServiceSwitch;
import com.android.internal.telephony.nano.PersistAtomsProto$CellularServiceState;
import com.android.internal.telephony.nano.PersistAtomsProto$DataCallSession;
import com.android.internal.telephony.nano.PersistAtomsProto$GbaEvent;
import com.android.internal.telephony.nano.PersistAtomsProto$ImsDedicatedBearerEvent;
import com.android.internal.telephony.nano.PersistAtomsProto$ImsDedicatedBearerListenerEvent;
import com.android.internal.telephony.nano.PersistAtomsProto$ImsRegistrationFeatureTagStats;
import com.android.internal.telephony.nano.PersistAtomsProto$ImsRegistrationServiceDescStats;
import com.android.internal.telephony.nano.PersistAtomsProto$ImsRegistrationStats;
import com.android.internal.telephony.nano.PersistAtomsProto$ImsRegistrationTermination;
import com.android.internal.telephony.nano.PersistAtomsProto$IncomingSms;
import com.android.internal.telephony.nano.PersistAtomsProto$NetworkRequestsV2;
import com.android.internal.telephony.nano.PersistAtomsProto$OutgoingShortCodeSms;
import com.android.internal.telephony.nano.PersistAtomsProto$OutgoingSms;
import com.android.internal.telephony.nano.PersistAtomsProto$PersistAtoms;
import com.android.internal.telephony.nano.PersistAtomsProto$PresenceNotifyEvent;
import com.android.internal.telephony.nano.PersistAtomsProto$RcsAcsProvisioningStats;
import com.android.internal.telephony.nano.PersistAtomsProto$RcsClientProvisioningStats;
import com.android.internal.telephony.nano.PersistAtomsProto$SipDelegateStats;
import com.android.internal.telephony.nano.PersistAtomsProto$SipMessageResponse;
import com.android.internal.telephony.nano.PersistAtomsProto$SipTransportFeatureTagStats;
import com.android.internal.telephony.nano.PersistAtomsProto$SipTransportSession;
import com.android.internal.telephony.nano.PersistAtomsProto$UceEventStats;
import com.android.internal.telephony.nano.PersistAtomsProto$UnmeteredNetworks;
import com.android.internal.telephony.nano.PersistAtomsProto$VoiceCallRatUsage;
import com.android.internal.telephony.nano.PersistAtomsProto$VoiceCallSession;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import com.android.internal.util.ArrayUtils;
import com.android.telephony.Rlog;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.IntBinaryOperator;
import java.util.function.IntPredicate;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
/* loaded from: classes.dex */
public class PersistAtomsStorage {
    private static final String TAG = "PersistAtomsStorage";
    private static final SecureRandom sRandom = new SecureRandom();
    @VisibleForTesting
    protected PersistAtomsProto$PersistAtoms mAtoms;
    private final Context mContext;
    private final Handler mHandler;
    private final HandlerThread mHandlerThread;
    private final int mMaxNumCarrierIdMismatches;
    private final int mMaxNumCellularDataSwitches;
    private final int mMaxNumCellularServiceStates;
    private final int mMaxNumDataCallSessions;
    private final int mMaxNumDedicatedBearerEventStats;
    private final int mMaxNumDedicatedBearerListenerEventStats;
    private final int mMaxNumGbaEventStats;
    private final int mMaxNumImsRegistrationFeatureStats;
    private final int mMaxNumImsRegistrationServiceDescStats;
    private final int mMaxNumImsRegistrationStats;
    private final int mMaxNumImsRegistrationTerminations;
    private final int mMaxNumPresenceNotifyEventStats;
    private final int mMaxNumRcsAcsProvisioningStats;
    private final int mMaxNumRcsClientProvisioningStats;
    private final int mMaxNumSipDelegateStats;
    private final int mMaxNumSipMessageResponseStats;
    private final int mMaxNumSipTransportFeatureTagStats;
    private final int mMaxNumSipTransportSessionStats;
    private final int mMaxNumSms;
    private final int mMaxNumUceEventStats;
    private final int mMaxNumVoiceCallSessions;
    private final int mMaxOutgoingShortCodeSms;
    @VisibleForTesting
    protected boolean mSaveImmediately;
    private Runnable mSaveRunnable = new Runnable() { // from class: com.android.internal.telephony.metrics.PersistAtomsStorage.1
        @Override // java.lang.Runnable
        public void run() {
            PersistAtomsStorage.this.saveAtomsToFileNow();
        }
    };
    private final VoiceCallRatTracker mVoiceCallRatTracker;

    public PersistAtomsStorage(Context context) {
        this.mContext = context;
        if (context.getPackageManager().hasSystemFeature("android.hardware.ram.low")) {
            Rlog.i(TAG, "Low RAM device");
            this.mMaxNumVoiceCallSessions = 10;
            this.mMaxNumSms = 5;
            this.mMaxNumCarrierIdMismatches = 8;
            this.mMaxNumDataCallSessions = 5;
            this.mMaxNumCellularServiceStates = 10;
            this.mMaxNumCellularDataSwitches = 5;
            this.mMaxNumImsRegistrationStats = 5;
            this.mMaxNumImsRegistrationTerminations = 5;
            this.mMaxNumImsRegistrationFeatureStats = 15;
            this.mMaxNumRcsClientProvisioningStats = 5;
            this.mMaxNumRcsAcsProvisioningStats = 5;
            this.mMaxNumSipMessageResponseStats = 10;
            this.mMaxNumSipTransportSessionStats = 10;
            this.mMaxNumSipDelegateStats = 5;
            this.mMaxNumSipTransportFeatureTagStats = 15;
            this.mMaxNumDedicatedBearerListenerEventStats = 5;
            this.mMaxNumDedicatedBearerEventStats = 5;
            this.mMaxNumImsRegistrationServiceDescStats = 15;
            this.mMaxNumUceEventStats = 5;
            this.mMaxNumPresenceNotifyEventStats = 10;
            this.mMaxNumGbaEventStats = 5;
            this.mMaxOutgoingShortCodeSms = 5;
        } else {
            this.mMaxNumVoiceCallSessions = 50;
            this.mMaxNumSms = 25;
            this.mMaxNumCarrierIdMismatches = 40;
            this.mMaxNumDataCallSessions = 15;
            this.mMaxNumCellularServiceStates = 50;
            this.mMaxNumCellularDataSwitches = 50;
            this.mMaxNumImsRegistrationStats = 10;
            this.mMaxNumImsRegistrationTerminations = 10;
            this.mMaxNumImsRegistrationFeatureStats = 25;
            this.mMaxNumRcsClientProvisioningStats = 10;
            this.mMaxNumRcsAcsProvisioningStats = 10;
            this.mMaxNumSipMessageResponseStats = 25;
            this.mMaxNumSipTransportSessionStats = 25;
            this.mMaxNumSipDelegateStats = 10;
            this.mMaxNumSipTransportFeatureTagStats = 25;
            this.mMaxNumDedicatedBearerListenerEventStats = 10;
            this.mMaxNumDedicatedBearerEventStats = 10;
            this.mMaxNumImsRegistrationServiceDescStats = 25;
            this.mMaxNumUceEventStats = 25;
            this.mMaxNumPresenceNotifyEventStats = 50;
            this.mMaxNumGbaEventStats = 10;
            this.mMaxOutgoingShortCodeSms = 10;
        }
        PersistAtomsProto$PersistAtoms loadAtomsFromFile = loadAtomsFromFile();
        this.mAtoms = loadAtomsFromFile;
        this.mVoiceCallRatTracker = VoiceCallRatTracker.fromProto(loadAtomsFromFile.voiceCallRatUsage);
        HandlerThread handlerThread = new HandlerThread("PersistAtomsThread");
        this.mHandlerThread = handlerThread;
        handlerThread.start();
        this.mHandler = new Handler(handlerThread.getLooper());
        this.mSaveImmediately = false;
    }

    public synchronized void addVoiceCallSession(PersistAtomsProto$VoiceCallSession persistAtomsProto$VoiceCallSession) {
        PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
        persistAtomsProto$PersistAtoms.voiceCallSession = (PersistAtomsProto$VoiceCallSession[]) insertAtRandomPlace(persistAtomsProto$PersistAtoms.voiceCallSession, persistAtomsProto$VoiceCallSession, this.mMaxNumVoiceCallSessions);
        saveAtomsToFile(30000);
        String str = TAG;
        Rlog.d(str, "Add new voice call session: " + persistAtomsProto$VoiceCallSession.toString());
    }

    public synchronized void addVoiceCallRatUsage(VoiceCallRatTracker voiceCallRatTracker) {
        this.mVoiceCallRatTracker.mergeWith(voiceCallRatTracker);
        this.mAtoms.voiceCallRatUsage = this.mVoiceCallRatTracker.toProto();
        saveAtomsToFile(30000);
    }

    public synchronized void addIncomingSms(PersistAtomsProto$IncomingSms persistAtomsProto$IncomingSms) {
        persistAtomsProto$IncomingSms.hashCode = SmsStats.getSmsHashCode(persistAtomsProto$IncomingSms);
        PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
        persistAtomsProto$PersistAtoms.incomingSms = (PersistAtomsProto$IncomingSms[]) insertAtRandomPlace(persistAtomsProto$PersistAtoms.incomingSms, persistAtomsProto$IncomingSms, this.mMaxNumSms);
        saveAtomsToFile(30000);
        String str = TAG;
        Rlog.d(str, "Add new incoming SMS atom: " + persistAtomsProto$IncomingSms.toString());
    }

    public synchronized void addOutgoingSms(PersistAtomsProto$OutgoingSms persistAtomsProto$OutgoingSms) {
        PersistAtomsProto$OutgoingSms[] persistAtomsProto$OutgoingSmsArr;
        int i;
        persistAtomsProto$OutgoingSms.hashCode = SmsStats.getSmsHashCode(persistAtomsProto$OutgoingSms);
        for (PersistAtomsProto$OutgoingSms persistAtomsProto$OutgoingSms2 : this.mAtoms.outgoingSms) {
            if (persistAtomsProto$OutgoingSms2.messageId == persistAtomsProto$OutgoingSms.messageId && (i = persistAtomsProto$OutgoingSms2.retryId) >= persistAtomsProto$OutgoingSms.retryId) {
                persistAtomsProto$OutgoingSms.retryId = i + 1;
            }
        }
        PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
        persistAtomsProto$PersistAtoms.outgoingSms = (PersistAtomsProto$OutgoingSms[]) insertAtRandomPlace(persistAtomsProto$PersistAtoms.outgoingSms, persistAtomsProto$OutgoingSms, this.mMaxNumSms);
        saveAtomsToFile(30000);
        Rlog.d(TAG, "Add new outgoing SMS atom: " + persistAtomsProto$OutgoingSms.toString());
    }

    public synchronized void addCellularServiceStateAndCellularDataServiceSwitch(PersistAtomsProto$CellularServiceState persistAtomsProto$CellularServiceState, PersistAtomsProto$CellularDataServiceSwitch persistAtomsProto$CellularDataServiceSwitch) {
        PersistAtomsProto$CellularServiceState find = find(persistAtomsProto$CellularServiceState);
        if (find != null) {
            find.totalTimeMillis += persistAtomsProto$CellularServiceState.totalTimeMillis;
            find.lastUsedMillis = getWallTimeMillis();
        } else {
            persistAtomsProto$CellularServiceState.lastUsedMillis = getWallTimeMillis();
            PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
            persistAtomsProto$PersistAtoms.cellularServiceState = (PersistAtomsProto$CellularServiceState[]) insertAtRandomPlace(persistAtomsProto$PersistAtoms.cellularServiceState, persistAtomsProto$CellularServiceState, this.mMaxNumCellularServiceStates);
        }
        if (persistAtomsProto$CellularDataServiceSwitch != null) {
            PersistAtomsProto$CellularDataServiceSwitch find2 = find(persistAtomsProto$CellularDataServiceSwitch);
            if (find2 != null) {
                find2.switchCount += persistAtomsProto$CellularDataServiceSwitch.switchCount;
                find2.lastUsedMillis = getWallTimeMillis();
            } else {
                persistAtomsProto$CellularDataServiceSwitch.lastUsedMillis = getWallTimeMillis();
                PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms2 = this.mAtoms;
                persistAtomsProto$PersistAtoms2.cellularDataServiceSwitch = (PersistAtomsProto$CellularDataServiceSwitch[]) insertAtRandomPlace(persistAtomsProto$PersistAtoms2.cellularDataServiceSwitch, persistAtomsProto$CellularDataServiceSwitch, this.mMaxNumCellularDataSwitches);
            }
        }
        saveAtomsToFile(30000);
    }

    public synchronized void addDataCallSession(PersistAtomsProto$DataCallSession persistAtomsProto$DataCallSession) {
        int findIndex = findIndex(persistAtomsProto$DataCallSession);
        if (findIndex >= 0) {
            PersistAtomsProto$DataCallSession persistAtomsProto$DataCallSession2 = this.mAtoms.dataCallSession[findIndex];
            persistAtomsProto$DataCallSession.ratSwitchCount += persistAtomsProto$DataCallSession2.ratSwitchCount;
            persistAtomsProto$DataCallSession.durationMinutes += persistAtomsProto$DataCallSession2.durationMinutes;
            persistAtomsProto$DataCallSession.handoverFailureCauses = IntStream.concat(Arrays.stream(persistAtomsProto$DataCallSession.handoverFailureCauses), Arrays.stream(persistAtomsProto$DataCallSession2.handoverFailureCauses)).limit(15L).toArray();
            persistAtomsProto$DataCallSession.handoverFailureRat = IntStream.concat(Arrays.stream(persistAtomsProto$DataCallSession.handoverFailureRat), Arrays.stream(persistAtomsProto$DataCallSession2.handoverFailureRat)).limit(15L).toArray();
            this.mAtoms.dataCallSession[findIndex] = persistAtomsProto$DataCallSession;
        } else {
            PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
            persistAtomsProto$PersistAtoms.dataCallSession = (PersistAtomsProto$DataCallSession[]) insertAtRandomPlace(persistAtomsProto$PersistAtoms.dataCallSession, persistAtomsProto$DataCallSession, this.mMaxNumDataCallSessions);
        }
        saveAtomsToFile(30000);
    }

    public synchronized boolean addCarrierIdMismatch(PersistAtomsProto$CarrierIdMismatch persistAtomsProto$CarrierIdMismatch) {
        if (find(persistAtomsProto$CarrierIdMismatch) != null) {
            return false;
        }
        PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
        PersistAtomsProto$CarrierIdMismatch[] persistAtomsProto$CarrierIdMismatchArr = persistAtomsProto$PersistAtoms.carrierIdMismatch;
        int length = persistAtomsProto$CarrierIdMismatchArr.length;
        int i = this.mMaxNumCarrierIdMismatches;
        if (length == i) {
            System.arraycopy(persistAtomsProto$CarrierIdMismatchArr, 1, persistAtomsProto$CarrierIdMismatchArr, 0, i - 1);
            this.mAtoms.carrierIdMismatch[this.mMaxNumCarrierIdMismatches - 1] = persistAtomsProto$CarrierIdMismatch;
        } else {
            persistAtomsProto$PersistAtoms.carrierIdMismatch = (PersistAtomsProto$CarrierIdMismatch[]) ArrayUtils.appendElement(PersistAtomsProto$CarrierIdMismatch.class, persistAtomsProto$CarrierIdMismatchArr, persistAtomsProto$CarrierIdMismatch, true);
        }
        saveAtomsToFile(30000);
        return true;
    }

    public synchronized void addImsRegistrationStats(PersistAtomsProto$ImsRegistrationStats persistAtomsProto$ImsRegistrationStats) {
        PersistAtomsProto$ImsRegistrationStats find = find(persistAtomsProto$ImsRegistrationStats);
        if (find != null) {
            find.registeredMillis += persistAtomsProto$ImsRegistrationStats.registeredMillis;
            find.voiceCapableMillis += persistAtomsProto$ImsRegistrationStats.voiceCapableMillis;
            find.voiceAvailableMillis += persistAtomsProto$ImsRegistrationStats.voiceAvailableMillis;
            find.smsCapableMillis += persistAtomsProto$ImsRegistrationStats.smsCapableMillis;
            find.smsAvailableMillis += persistAtomsProto$ImsRegistrationStats.smsAvailableMillis;
            find.videoCapableMillis += persistAtomsProto$ImsRegistrationStats.videoCapableMillis;
            find.videoAvailableMillis += persistAtomsProto$ImsRegistrationStats.videoAvailableMillis;
            find.utCapableMillis += persistAtomsProto$ImsRegistrationStats.utCapableMillis;
            find.utAvailableMillis += persistAtomsProto$ImsRegistrationStats.utAvailableMillis;
            find.lastUsedMillis = getWallTimeMillis();
        } else {
            persistAtomsProto$ImsRegistrationStats.lastUsedMillis = getWallTimeMillis();
            PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
            persistAtomsProto$PersistAtoms.imsRegistrationStats = (PersistAtomsProto$ImsRegistrationStats[]) insertAtRandomPlace(persistAtomsProto$PersistAtoms.imsRegistrationStats, persistAtomsProto$ImsRegistrationStats, this.mMaxNumImsRegistrationStats);
        }
        saveAtomsToFile(30000);
    }

    public synchronized void addImsRegistrationTermination(PersistAtomsProto$ImsRegistrationTermination persistAtomsProto$ImsRegistrationTermination) {
        PersistAtomsProto$ImsRegistrationTermination find = find(persistAtomsProto$ImsRegistrationTermination);
        if (find != null) {
            find.count += persistAtomsProto$ImsRegistrationTermination.count;
            find.lastUsedMillis = getWallTimeMillis();
        } else {
            persistAtomsProto$ImsRegistrationTermination.lastUsedMillis = getWallTimeMillis();
            PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
            persistAtomsProto$PersistAtoms.imsRegistrationTermination = (PersistAtomsProto$ImsRegistrationTermination[]) insertAtRandomPlace(persistAtomsProto$PersistAtoms.imsRegistrationTermination, persistAtomsProto$ImsRegistrationTermination, this.mMaxNumImsRegistrationTerminations);
        }
        saveAtomsToFile(30000);
    }

    public synchronized boolean setCarrierIdTableVersion(int i) {
        PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
        if (persistAtomsProto$PersistAtoms.carrierIdTableVersion < i) {
            persistAtomsProto$PersistAtoms.carrierIdTableVersion = i;
            saveAtomsToFile(30000);
            return true;
        }
        return false;
    }

    public synchronized void recordToggledAutoDataSwitch() {
        this.mAtoms.autoDataSwitchToggleCount++;
        saveAtomsToFile(30000);
    }

    public synchronized void addNetworkRequestsV2(PersistAtomsProto$NetworkRequestsV2 persistAtomsProto$NetworkRequestsV2) {
        PersistAtomsProto$NetworkRequestsV2 find = find(persistAtomsProto$NetworkRequestsV2);
        if (find != null) {
            find.requestCount += persistAtomsProto$NetworkRequestsV2.requestCount;
        } else {
            PersistAtomsProto$NetworkRequestsV2 persistAtomsProto$NetworkRequestsV22 = new PersistAtomsProto$NetworkRequestsV2();
            persistAtomsProto$NetworkRequestsV22.capability = persistAtomsProto$NetworkRequestsV2.capability;
            persistAtomsProto$NetworkRequestsV22.carrierId = persistAtomsProto$NetworkRequestsV2.carrierId;
            persistAtomsProto$NetworkRequestsV22.requestCount = persistAtomsProto$NetworkRequestsV2.requestCount;
            PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
            persistAtomsProto$PersistAtoms.networkRequestsV2 = (PersistAtomsProto$NetworkRequestsV2[]) ArrayUtils.appendElement(PersistAtomsProto$NetworkRequestsV2.class, persistAtomsProto$PersistAtoms.networkRequestsV2, persistAtomsProto$NetworkRequestsV22, true);
        }
        saveAtomsToFile(30000);
    }

    public synchronized void addImsRegistrationFeatureTagStats(PersistAtomsProto$ImsRegistrationFeatureTagStats persistAtomsProto$ImsRegistrationFeatureTagStats) {
        PersistAtomsProto$ImsRegistrationFeatureTagStats find = find(persistAtomsProto$ImsRegistrationFeatureTagStats);
        if (find != null) {
            find.registeredMillis += persistAtomsProto$ImsRegistrationFeatureTagStats.registeredMillis;
        } else {
            PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
            persistAtomsProto$PersistAtoms.imsRegistrationFeatureTagStats = (PersistAtomsProto$ImsRegistrationFeatureTagStats[]) insertAtRandomPlace(persistAtomsProto$PersistAtoms.imsRegistrationFeatureTagStats, persistAtomsProto$ImsRegistrationFeatureTagStats, this.mMaxNumImsRegistrationFeatureStats);
        }
        saveAtomsToFile(30000);
    }

    public synchronized void addRcsClientProvisioningStats(PersistAtomsProto$RcsClientProvisioningStats persistAtomsProto$RcsClientProvisioningStats) {
        PersistAtomsProto$RcsClientProvisioningStats find = find(persistAtomsProto$RcsClientProvisioningStats);
        if (find != null) {
            find.count++;
        } else {
            PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
            persistAtomsProto$PersistAtoms.rcsClientProvisioningStats = (PersistAtomsProto$RcsClientProvisioningStats[]) insertAtRandomPlace(persistAtomsProto$PersistAtoms.rcsClientProvisioningStats, persistAtomsProto$RcsClientProvisioningStats, this.mMaxNumRcsClientProvisioningStats);
        }
        saveAtomsToFile(30000);
    }

    public synchronized void addRcsAcsProvisioningStats(PersistAtomsProto$RcsAcsProvisioningStats persistAtomsProto$RcsAcsProvisioningStats) {
        PersistAtomsProto$RcsAcsProvisioningStats find = find(persistAtomsProto$RcsAcsProvisioningStats);
        if (find != null) {
            find.count++;
            find.stateTimerMillis += persistAtomsProto$RcsAcsProvisioningStats.stateTimerMillis;
        } else {
            persistAtomsProto$RcsAcsProvisioningStats.count = 1;
            PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
            persistAtomsProto$PersistAtoms.rcsAcsProvisioningStats = (PersistAtomsProto$RcsAcsProvisioningStats[]) insertAtRandomPlace(persistAtomsProto$PersistAtoms.rcsAcsProvisioningStats, persistAtomsProto$RcsAcsProvisioningStats, this.mMaxNumRcsAcsProvisioningStats);
        }
        saveAtomsToFile(30000);
    }

    public synchronized void addSipDelegateStats(PersistAtomsProto$SipDelegateStats persistAtomsProto$SipDelegateStats) {
        PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
        persistAtomsProto$PersistAtoms.sipDelegateStats = (PersistAtomsProto$SipDelegateStats[]) insertAtRandomPlace(persistAtomsProto$PersistAtoms.sipDelegateStats, persistAtomsProto$SipDelegateStats, this.mMaxNumSipDelegateStats);
        saveAtomsToFile(30000);
    }

    public synchronized void addSipTransportFeatureTagStats(PersistAtomsProto$SipTransportFeatureTagStats persistAtomsProto$SipTransportFeatureTagStats) {
        PersistAtomsProto$SipTransportFeatureTagStats find = find(persistAtomsProto$SipTransportFeatureTagStats);
        if (find != null) {
            find.associatedMillis += persistAtomsProto$SipTransportFeatureTagStats.associatedMillis;
        } else {
            PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
            persistAtomsProto$PersistAtoms.sipTransportFeatureTagStats = (PersistAtomsProto$SipTransportFeatureTagStats[]) insertAtRandomPlace(persistAtomsProto$PersistAtoms.sipTransportFeatureTagStats, persistAtomsProto$SipTransportFeatureTagStats, this.mMaxNumSipTransportFeatureTagStats);
        }
        saveAtomsToFile(30000);
    }

    public synchronized void addSipMessageResponse(PersistAtomsProto$SipMessageResponse persistAtomsProto$SipMessageResponse) {
        PersistAtomsProto$SipMessageResponse find = find(persistAtomsProto$SipMessageResponse);
        if (find != null) {
            find.count++;
        } else {
            PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
            persistAtomsProto$PersistAtoms.sipMessageResponse = (PersistAtomsProto$SipMessageResponse[]) insertAtRandomPlace(persistAtomsProto$PersistAtoms.sipMessageResponse, persistAtomsProto$SipMessageResponse, this.mMaxNumSipMessageResponseStats);
        }
        saveAtomsToFile(30000);
    }

    public synchronized void addCompleteSipTransportSession(PersistAtomsProto$SipTransportSession persistAtomsProto$SipTransportSession) {
        PersistAtomsProto$SipTransportSession find = find(persistAtomsProto$SipTransportSession);
        if (find != null) {
            find.sessionCount++;
            if (persistAtomsProto$SipTransportSession.isEndedGracefully) {
                find.endedGracefullyCount++;
            }
        } else {
            PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
            persistAtomsProto$PersistAtoms.sipTransportSession = (PersistAtomsProto$SipTransportSession[]) insertAtRandomPlace(persistAtomsProto$PersistAtoms.sipTransportSession, persistAtomsProto$SipTransportSession, this.mMaxNumSipTransportSessionStats);
        }
        saveAtomsToFile(30000);
    }

    public synchronized void addImsDedicatedBearerListenerEvent(PersistAtomsProto$ImsDedicatedBearerListenerEvent persistAtomsProto$ImsDedicatedBearerListenerEvent) {
        PersistAtomsProto$ImsDedicatedBearerListenerEvent find = find(persistAtomsProto$ImsDedicatedBearerListenerEvent);
        if (find != null) {
            find.eventCount++;
        } else {
            PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
            persistAtomsProto$PersistAtoms.imsDedicatedBearerListenerEvent = (PersistAtomsProto$ImsDedicatedBearerListenerEvent[]) insertAtRandomPlace(persistAtomsProto$PersistAtoms.imsDedicatedBearerListenerEvent, persistAtomsProto$ImsDedicatedBearerListenerEvent, this.mMaxNumDedicatedBearerListenerEventStats);
        }
        saveAtomsToFile(30000);
    }

    public synchronized void addImsDedicatedBearerEvent(PersistAtomsProto$ImsDedicatedBearerEvent persistAtomsProto$ImsDedicatedBearerEvent) {
        PersistAtomsProto$ImsDedicatedBearerEvent find = find(persistAtomsProto$ImsDedicatedBearerEvent);
        if (find != null) {
            find.count++;
        } else {
            PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
            persistAtomsProto$PersistAtoms.imsDedicatedBearerEvent = (PersistAtomsProto$ImsDedicatedBearerEvent[]) insertAtRandomPlace(persistAtomsProto$PersistAtoms.imsDedicatedBearerEvent, persistAtomsProto$ImsDedicatedBearerEvent, this.mMaxNumDedicatedBearerEventStats);
        }
        saveAtomsToFile(30000);
    }

    public synchronized void addImsRegistrationServiceDescStats(PersistAtomsProto$ImsRegistrationServiceDescStats persistAtomsProto$ImsRegistrationServiceDescStats) {
        PersistAtomsProto$ImsRegistrationServiceDescStats find = find(persistAtomsProto$ImsRegistrationServiceDescStats);
        if (find != null) {
            find.publishedMillis += persistAtomsProto$ImsRegistrationServiceDescStats.publishedMillis;
        } else {
            PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
            persistAtomsProto$PersistAtoms.imsRegistrationServiceDescStats = (PersistAtomsProto$ImsRegistrationServiceDescStats[]) insertAtRandomPlace(persistAtomsProto$PersistAtoms.imsRegistrationServiceDescStats, persistAtomsProto$ImsRegistrationServiceDescStats, this.mMaxNumImsRegistrationServiceDescStats);
        }
        saveAtomsToFile(30000);
    }

    public synchronized void addUceEventStats(PersistAtomsProto$UceEventStats persistAtomsProto$UceEventStats) {
        PersistAtomsProto$UceEventStats find = find(persistAtomsProto$UceEventStats);
        if (find != null) {
            find.count++;
        } else {
            PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
            persistAtomsProto$PersistAtoms.uceEventStats = (PersistAtomsProto$UceEventStats[]) insertAtRandomPlace(persistAtomsProto$PersistAtoms.uceEventStats, persistAtomsProto$UceEventStats, this.mMaxNumUceEventStats);
        }
        saveAtomsToFile(30000);
    }

    public synchronized void addPresenceNotifyEvent(PersistAtomsProto$PresenceNotifyEvent persistAtomsProto$PresenceNotifyEvent) {
        PersistAtomsProto$PresenceNotifyEvent find = find(persistAtomsProto$PresenceNotifyEvent);
        if (find != null) {
            find.rcsCapsCount += persistAtomsProto$PresenceNotifyEvent.rcsCapsCount;
            find.mmtelCapsCount += persistAtomsProto$PresenceNotifyEvent.mmtelCapsCount;
            find.noCapsCount += persistAtomsProto$PresenceNotifyEvent.noCapsCount;
            find.count += persistAtomsProto$PresenceNotifyEvent.count;
        } else {
            PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
            persistAtomsProto$PersistAtoms.presenceNotifyEvent = (PersistAtomsProto$PresenceNotifyEvent[]) insertAtRandomPlace(persistAtomsProto$PersistAtoms.presenceNotifyEvent, persistAtomsProto$PresenceNotifyEvent, this.mMaxNumPresenceNotifyEventStats);
        }
        saveAtomsToFile(30000);
    }

    public synchronized void addGbaEvent(PersistAtomsProto$GbaEvent persistAtomsProto$GbaEvent) {
        PersistAtomsProto$GbaEvent find = find(persistAtomsProto$GbaEvent);
        if (find != null) {
            find.count++;
        } else {
            PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
            persistAtomsProto$PersistAtoms.gbaEvent = (PersistAtomsProto$GbaEvent[]) insertAtRandomPlace(persistAtomsProto$PersistAtoms.gbaEvent, persistAtomsProto$GbaEvent, this.mMaxNumGbaEventStats);
        }
        saveAtomsToFile(30000);
    }

    public synchronized void addUnmeteredNetworks(int i, int i2, long j) {
        PersistAtomsProto$UnmeteredNetworks findUnmeteredNetworks = findUnmeteredNetworks(i);
        boolean z = true;
        if (findUnmeteredNetworks == null) {
            PersistAtomsProto$UnmeteredNetworks persistAtomsProto$UnmeteredNetworks = new PersistAtomsProto$UnmeteredNetworks();
            persistAtomsProto$UnmeteredNetworks.phoneId = i;
            persistAtomsProto$UnmeteredNetworks.carrierId = i2;
            persistAtomsProto$UnmeteredNetworks.unmeteredNetworksBitmask = j;
            PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
            persistAtomsProto$PersistAtoms.unmeteredNetworks = (PersistAtomsProto$UnmeteredNetworks[]) ArrayUtils.appendElement(PersistAtomsProto$UnmeteredNetworks.class, persistAtomsProto$PersistAtoms.unmeteredNetworks, persistAtomsProto$UnmeteredNetworks, true);
        } else {
            if (findUnmeteredNetworks.carrierId != i2) {
                findUnmeteredNetworks.carrierId = i2;
                findUnmeteredNetworks.unmeteredNetworksBitmask = 0L;
            }
            long j2 = findUnmeteredNetworks.unmeteredNetworksBitmask;
            if ((j2 | j) != j2) {
                findUnmeteredNetworks.unmeteredNetworksBitmask = j2 | j;
            } else {
                z = false;
            }
        }
        if (z) {
            saveAtomsToFile(30000);
        }
    }

    public synchronized void addOutgoingShortCodeSms(PersistAtomsProto$OutgoingShortCodeSms persistAtomsProto$OutgoingShortCodeSms) {
        PersistAtomsProto$OutgoingShortCodeSms find = find(persistAtomsProto$OutgoingShortCodeSms);
        if (find != null) {
            find.shortCodeSmsCount++;
        } else {
            PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
            persistAtomsProto$PersistAtoms.outgoingShortCodeSms = (PersistAtomsProto$OutgoingShortCodeSms[]) insertAtRandomPlace(persistAtomsProto$PersistAtoms.outgoingShortCodeSms, persistAtomsProto$OutgoingShortCodeSms, this.mMaxOutgoingShortCodeSms);
        }
        saveAtomsToFile(30000);
    }

    public synchronized PersistAtomsProto$VoiceCallSession[] getVoiceCallSessions(long j) {
        long wallTimeMillis = getWallTimeMillis();
        PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
        if (wallTimeMillis - persistAtomsProto$PersistAtoms.voiceCallSessionPullTimestampMillis > j) {
            persistAtomsProto$PersistAtoms.voiceCallSessionPullTimestampMillis = getWallTimeMillis();
            PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms2 = this.mAtoms;
            PersistAtomsProto$VoiceCallSession[] persistAtomsProto$VoiceCallSessionArr = persistAtomsProto$PersistAtoms2.voiceCallSession;
            persistAtomsProto$PersistAtoms2.voiceCallSession = new PersistAtomsProto$VoiceCallSession[0];
            saveAtomsToFile(500);
            return persistAtomsProto$VoiceCallSessionArr;
        }
        return null;
    }

    public synchronized PersistAtomsProto$VoiceCallRatUsage[] getVoiceCallRatUsages(long j) {
        long wallTimeMillis = getWallTimeMillis();
        PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
        if (wallTimeMillis - persistAtomsProto$PersistAtoms.voiceCallRatUsagePullTimestampMillis > j) {
            persistAtomsProto$PersistAtoms.voiceCallRatUsagePullTimestampMillis = getWallTimeMillis();
            PersistAtomsProto$VoiceCallRatUsage[] persistAtomsProto$VoiceCallRatUsageArr = this.mAtoms.voiceCallRatUsage;
            this.mVoiceCallRatTracker.clear();
            this.mAtoms.voiceCallRatUsage = new PersistAtomsProto$VoiceCallRatUsage[0];
            saveAtomsToFile(500);
            return persistAtomsProto$VoiceCallRatUsageArr;
        }
        return null;
    }

    public synchronized PersistAtomsProto$IncomingSms[] getIncomingSms(long j) {
        long wallTimeMillis = getWallTimeMillis();
        PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
        if (wallTimeMillis - persistAtomsProto$PersistAtoms.incomingSmsPullTimestampMillis > j) {
            persistAtomsProto$PersistAtoms.incomingSmsPullTimestampMillis = getWallTimeMillis();
            PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms2 = this.mAtoms;
            PersistAtomsProto$IncomingSms[] persistAtomsProto$IncomingSmsArr = persistAtomsProto$PersistAtoms2.incomingSms;
            persistAtomsProto$PersistAtoms2.incomingSms = new PersistAtomsProto$IncomingSms[0];
            saveAtomsToFile(500);
            return persistAtomsProto$IncomingSmsArr;
        }
        return null;
    }

    public synchronized PersistAtomsProto$OutgoingSms[] getOutgoingSms(long j) {
        long wallTimeMillis = getWallTimeMillis();
        PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
        if (wallTimeMillis - persistAtomsProto$PersistAtoms.outgoingSmsPullTimestampMillis > j) {
            persistAtomsProto$PersistAtoms.outgoingSmsPullTimestampMillis = getWallTimeMillis();
            PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms2 = this.mAtoms;
            PersistAtomsProto$OutgoingSms[] persistAtomsProto$OutgoingSmsArr = persistAtomsProto$PersistAtoms2.outgoingSms;
            persistAtomsProto$PersistAtoms2.outgoingSms = new PersistAtomsProto$OutgoingSms[0];
            saveAtomsToFile(500);
            return persistAtomsProto$OutgoingSmsArr;
        }
        return null;
    }

    public synchronized PersistAtomsProto$DataCallSession[] getDataCallSessions(long j) {
        long wallTimeMillis = getWallTimeMillis();
        PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
        if (wallTimeMillis - persistAtomsProto$PersistAtoms.dataCallSessionPullTimestampMillis > j) {
            persistAtomsProto$PersistAtoms.dataCallSessionPullTimestampMillis = getWallTimeMillis();
            PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms2 = this.mAtoms;
            PersistAtomsProto$DataCallSession[] persistAtomsProto$DataCallSessionArr = persistAtomsProto$PersistAtoms2.dataCallSession;
            persistAtomsProto$PersistAtoms2.dataCallSession = new PersistAtomsProto$DataCallSession[0];
            saveAtomsToFile(500);
            for (PersistAtomsProto$DataCallSession persistAtomsProto$DataCallSession : persistAtomsProto$DataCallSessionArr) {
                sortBaseOnArray(persistAtomsProto$DataCallSession.handoverFailureCauses, persistAtomsProto$DataCallSession.handoverFailureRat);
            }
            return persistAtomsProto$DataCallSessionArr;
        }
        return null;
    }

    private void sortBaseOnArray(final int[] iArr, int[] iArr2) {
        if (iArr2.length != iArr.length) {
            return;
        }
        int[] array = IntStream.range(0, iArr.length).boxed().sorted(Comparator.comparingInt(new ToIntFunction() { // from class: com.android.internal.telephony.metrics.PersistAtomsStorage$$ExternalSyntheticLambda1
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                int lambda$sortBaseOnArray$0;
                lambda$sortBaseOnArray$0 = PersistAtomsStorage.lambda$sortBaseOnArray$0(iArr, (Integer) obj);
                return lambda$sortBaseOnArray$0;
            }
        })).mapToInt(new RILUtils$$ExternalSyntheticLambda6()).toArray();
        int[] copyOf = Arrays.copyOf(iArr, iArr.length);
        int[] copyOf2 = Arrays.copyOf(iArr2, iArr2.length);
        for (int i = 0; i < array.length; i++) {
            iArr[i] = copyOf[array[i]];
            iArr2[i] = copyOf2[array[i]];
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$sortBaseOnArray$0(int[] iArr, Integer num) {
        return iArr[num.intValue()];
    }

    public synchronized PersistAtomsProto$CellularServiceState[] getCellularServiceStates(long j) {
        long wallTimeMillis = getWallTimeMillis();
        PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
        if (wallTimeMillis - persistAtomsProto$PersistAtoms.cellularServiceStatePullTimestampMillis > j) {
            persistAtomsProto$PersistAtoms.cellularServiceStatePullTimestampMillis = getWallTimeMillis();
            PersistAtomsProto$CellularServiceState[] persistAtomsProto$CellularServiceStateArr = this.mAtoms.cellularServiceState;
            Arrays.stream(persistAtomsProto$CellularServiceStateArr).forEach(new Consumer() { // from class: com.android.internal.telephony.metrics.PersistAtomsStorage$$ExternalSyntheticLambda3
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((PersistAtomsProto$CellularServiceState) obj).lastUsedMillis = 0L;
                }
            });
            this.mAtoms.cellularServiceState = new PersistAtomsProto$CellularServiceState[0];
            saveAtomsToFile(500);
            return persistAtomsProto$CellularServiceStateArr;
        }
        return null;
    }

    public synchronized PersistAtomsProto$CellularDataServiceSwitch[] getCellularDataServiceSwitches(long j) {
        long wallTimeMillis = getWallTimeMillis();
        PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
        if (wallTimeMillis - persistAtomsProto$PersistAtoms.cellularDataServiceSwitchPullTimestampMillis > j) {
            persistAtomsProto$PersistAtoms.cellularDataServiceSwitchPullTimestampMillis = getWallTimeMillis();
            PersistAtomsProto$CellularDataServiceSwitch[] persistAtomsProto$CellularDataServiceSwitchArr = this.mAtoms.cellularDataServiceSwitch;
            Arrays.stream(persistAtomsProto$CellularDataServiceSwitchArr).forEach(new Consumer() { // from class: com.android.internal.telephony.metrics.PersistAtomsStorage$$ExternalSyntheticLambda2
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((PersistAtomsProto$CellularDataServiceSwitch) obj).lastUsedMillis = 0L;
                }
            });
            this.mAtoms.cellularDataServiceSwitch = new PersistAtomsProto$CellularDataServiceSwitch[0];
            saveAtomsToFile(500);
            return persistAtomsProto$CellularDataServiceSwitchArr;
        }
        return null;
    }

    public synchronized PersistAtomsProto$ImsRegistrationStats[] getImsRegistrationStats(long j) {
        long wallTimeMillis = getWallTimeMillis();
        PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
        long j2 = wallTimeMillis - persistAtomsProto$PersistAtoms.imsRegistrationStatsPullTimestampMillis;
        if (j2 > j) {
            persistAtomsProto$PersistAtoms.imsRegistrationStatsPullTimestampMillis = getWallTimeMillis();
            PersistAtomsProto$ImsRegistrationStats[] persistAtomsProto$ImsRegistrationStatsArr = this.mAtoms.imsRegistrationStats;
            Arrays.stream(persistAtomsProto$ImsRegistrationStatsArr).forEach(new Consumer() { // from class: com.android.internal.telephony.metrics.PersistAtomsStorage$$ExternalSyntheticLambda9
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((PersistAtomsProto$ImsRegistrationStats) obj).lastUsedMillis = 0L;
                }
            });
            this.mAtoms.imsRegistrationStats = new PersistAtomsProto$ImsRegistrationStats[0];
            saveAtomsToFile(500);
            return normalizeData(persistAtomsProto$ImsRegistrationStatsArr, j2);
        }
        return null;
    }

    public synchronized PersistAtomsProto$ImsRegistrationTermination[] getImsRegistrationTerminations(long j) {
        long wallTimeMillis = getWallTimeMillis();
        PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
        if (wallTimeMillis - persistAtomsProto$PersistAtoms.imsRegistrationTerminationPullTimestampMillis > j) {
            persistAtomsProto$PersistAtoms.imsRegistrationTerminationPullTimestampMillis = getWallTimeMillis();
            PersistAtomsProto$ImsRegistrationTermination[] persistAtomsProto$ImsRegistrationTerminationArr = this.mAtoms.imsRegistrationTermination;
            Arrays.stream(persistAtomsProto$ImsRegistrationTerminationArr).forEach(new Consumer() { // from class: com.android.internal.telephony.metrics.PersistAtomsStorage$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((PersistAtomsProto$ImsRegistrationTermination) obj).lastUsedMillis = 0L;
                }
            });
            this.mAtoms.imsRegistrationTermination = new PersistAtomsProto$ImsRegistrationTermination[0];
            saveAtomsToFile(500);
            return persistAtomsProto$ImsRegistrationTerminationArr;
        }
        return null;
    }

    public synchronized PersistAtomsProto$NetworkRequestsV2[] getNetworkRequestsV2(long j) {
        long wallTimeMillis = getWallTimeMillis();
        PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
        if (wallTimeMillis - persistAtomsProto$PersistAtoms.networkRequestsV2PullTimestampMillis > j) {
            persistAtomsProto$PersistAtoms.networkRequestsV2PullTimestampMillis = getWallTimeMillis();
            PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms2 = this.mAtoms;
            PersistAtomsProto$NetworkRequestsV2[] persistAtomsProto$NetworkRequestsV2Arr = persistAtomsProto$PersistAtoms2.networkRequestsV2;
            persistAtomsProto$PersistAtoms2.networkRequestsV2 = new PersistAtomsProto$NetworkRequestsV2[0];
            saveAtomsToFile(500);
            return persistAtomsProto$NetworkRequestsV2Arr;
        }
        return null;
    }

    public synchronized int getAutoDataSwitchToggleCount() {
        int i;
        PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
        i = persistAtomsProto$PersistAtoms.autoDataSwitchToggleCount;
        if (i > 0) {
            persistAtomsProto$PersistAtoms.autoDataSwitchToggleCount = 0;
            saveAtomsToFile(500);
        }
        return i;
    }

    public synchronized PersistAtomsProto$ImsRegistrationFeatureTagStats[] getImsRegistrationFeatureTagStats(long j) {
        long wallTimeMillis = getWallTimeMillis();
        PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
        if (wallTimeMillis - persistAtomsProto$PersistAtoms.rcsAcsProvisioningStatsPullTimestampMillis > j) {
            persistAtomsProto$PersistAtoms.imsRegistrationFeatureTagStatsPullTimestampMillis = getWallTimeMillis();
            PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms2 = this.mAtoms;
            PersistAtomsProto$ImsRegistrationFeatureTagStats[] persistAtomsProto$ImsRegistrationFeatureTagStatsArr = persistAtomsProto$PersistAtoms2.imsRegistrationFeatureTagStats;
            persistAtomsProto$PersistAtoms2.imsRegistrationFeatureTagStats = new PersistAtomsProto$ImsRegistrationFeatureTagStats[0];
            saveAtomsToFile(500);
            return persistAtomsProto$ImsRegistrationFeatureTagStatsArr;
        }
        return null;
    }

    public synchronized PersistAtomsProto$RcsClientProvisioningStats[] getRcsClientProvisioningStats(long j) {
        long wallTimeMillis = getWallTimeMillis();
        PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
        if (wallTimeMillis - persistAtomsProto$PersistAtoms.rcsClientProvisioningStatsPullTimestampMillis > j) {
            persistAtomsProto$PersistAtoms.rcsClientProvisioningStatsPullTimestampMillis = getWallTimeMillis();
            PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms2 = this.mAtoms;
            PersistAtomsProto$RcsClientProvisioningStats[] persistAtomsProto$RcsClientProvisioningStatsArr = persistAtomsProto$PersistAtoms2.rcsClientProvisioningStats;
            persistAtomsProto$PersistAtoms2.rcsClientProvisioningStats = new PersistAtomsProto$RcsClientProvisioningStats[0];
            saveAtomsToFile(500);
            return persistAtomsProto$RcsClientProvisioningStatsArr;
        }
        return null;
    }

    public synchronized PersistAtomsProto$RcsAcsProvisioningStats[] getRcsAcsProvisioningStats(long j) {
        long wallTimeMillis = getWallTimeMillis();
        PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
        long j2 = wallTimeMillis - persistAtomsProto$PersistAtoms.rcsAcsProvisioningStatsPullTimestampMillis;
        if (j2 > j) {
            persistAtomsProto$PersistAtoms.rcsAcsProvisioningStatsPullTimestampMillis = getWallTimeMillis();
            PersistAtomsProto$RcsAcsProvisioningStats[] persistAtomsProto$RcsAcsProvisioningStatsArr = this.mAtoms.rcsAcsProvisioningStats;
            for (PersistAtomsProto$RcsAcsProvisioningStats persistAtomsProto$RcsAcsProvisioningStats : persistAtomsProto$RcsAcsProvisioningStatsArr) {
                if (j2 > 86400000) {
                    persistAtomsProto$RcsAcsProvisioningStats.stateTimerMillis = normalizeDurationTo24H(persistAtomsProto$RcsAcsProvisioningStats.stateTimerMillis, j2);
                }
            }
            this.mAtoms.rcsAcsProvisioningStats = new PersistAtomsProto$RcsAcsProvisioningStats[0];
            saveAtomsToFile(500);
            return persistAtomsProto$RcsAcsProvisioningStatsArr;
        }
        return null;
    }

    public synchronized PersistAtomsProto$SipDelegateStats[] getSipDelegateStats(long j) {
        long wallTimeMillis = getWallTimeMillis();
        PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
        long j2 = wallTimeMillis - persistAtomsProto$PersistAtoms.sipDelegateStatsPullTimestampMillis;
        if (j2 > j) {
            persistAtomsProto$PersistAtoms.sipDelegateStatsPullTimestampMillis = getWallTimeMillis();
            PersistAtomsProto$SipDelegateStats[] persistAtomsProto$SipDelegateStatsArr = this.mAtoms.sipDelegateStats;
            for (PersistAtomsProto$SipDelegateStats persistAtomsProto$SipDelegateStats : persistAtomsProto$SipDelegateStatsArr) {
                if (j2 > 86400000) {
                    persistAtomsProto$SipDelegateStats.uptimeMillis = normalizeDurationTo24H(persistAtomsProto$SipDelegateStats.uptimeMillis, j2);
                }
            }
            this.mAtoms.sipDelegateStats = new PersistAtomsProto$SipDelegateStats[0];
            saveAtomsToFile(500);
            return persistAtomsProto$SipDelegateStatsArr;
        }
        return null;
    }

    public synchronized PersistAtomsProto$SipTransportFeatureTagStats[] getSipTransportFeatureTagStats(long j) {
        long wallTimeMillis = getWallTimeMillis();
        PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
        long j2 = wallTimeMillis - persistAtomsProto$PersistAtoms.sipTransportFeatureTagStatsPullTimestampMillis;
        if (j2 > j) {
            persistAtomsProto$PersistAtoms.sipTransportFeatureTagStatsPullTimestampMillis = getWallTimeMillis();
            PersistAtomsProto$SipTransportFeatureTagStats[] persistAtomsProto$SipTransportFeatureTagStatsArr = this.mAtoms.sipTransportFeatureTagStats;
            for (PersistAtomsProto$SipTransportFeatureTagStats persistAtomsProto$SipTransportFeatureTagStats : persistAtomsProto$SipTransportFeatureTagStatsArr) {
                if (j2 > 86400000) {
                    persistAtomsProto$SipTransportFeatureTagStats.associatedMillis = normalizeDurationTo24H(persistAtomsProto$SipTransportFeatureTagStats.associatedMillis, j2);
                }
            }
            this.mAtoms.sipTransportFeatureTagStats = new PersistAtomsProto$SipTransportFeatureTagStats[0];
            saveAtomsToFile(500);
            return persistAtomsProto$SipTransportFeatureTagStatsArr;
        }
        return null;
    }

    public synchronized PersistAtomsProto$SipMessageResponse[] getSipMessageResponse(long j) {
        long wallTimeMillis = getWallTimeMillis();
        PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
        if (wallTimeMillis - persistAtomsProto$PersistAtoms.sipMessageResponsePullTimestampMillis > j) {
            persistAtomsProto$PersistAtoms.sipMessageResponsePullTimestampMillis = getWallTimeMillis();
            PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms2 = this.mAtoms;
            PersistAtomsProto$SipMessageResponse[] persistAtomsProto$SipMessageResponseArr = persistAtomsProto$PersistAtoms2.sipMessageResponse;
            persistAtomsProto$PersistAtoms2.sipMessageResponse = new PersistAtomsProto$SipMessageResponse[0];
            saveAtomsToFile(500);
            return persistAtomsProto$SipMessageResponseArr;
        }
        return null;
    }

    public synchronized PersistAtomsProto$SipTransportSession[] getSipTransportSession(long j) {
        long wallTimeMillis = getWallTimeMillis();
        PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
        if (wallTimeMillis - persistAtomsProto$PersistAtoms.sipTransportSessionPullTimestampMillis > j) {
            persistAtomsProto$PersistAtoms.sipTransportSessionPullTimestampMillis = getWallTimeMillis();
            PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms2 = this.mAtoms;
            PersistAtomsProto$SipTransportSession[] persistAtomsProto$SipTransportSessionArr = persistAtomsProto$PersistAtoms2.sipTransportSession;
            persistAtomsProto$PersistAtoms2.sipTransportSession = new PersistAtomsProto$SipTransportSession[0];
            saveAtomsToFile(500);
            return persistAtomsProto$SipTransportSessionArr;
        }
        return null;
    }

    public synchronized PersistAtomsProto$ImsDedicatedBearerListenerEvent[] getImsDedicatedBearerListenerEvent(long j) {
        long wallTimeMillis = getWallTimeMillis();
        PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
        if (wallTimeMillis - persistAtomsProto$PersistAtoms.imsDedicatedBearerListenerEventPullTimestampMillis > j) {
            persistAtomsProto$PersistAtoms.imsDedicatedBearerListenerEventPullTimestampMillis = getWallTimeMillis();
            PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms2 = this.mAtoms;
            PersistAtomsProto$ImsDedicatedBearerListenerEvent[] persistAtomsProto$ImsDedicatedBearerListenerEventArr = persistAtomsProto$PersistAtoms2.imsDedicatedBearerListenerEvent;
            persistAtomsProto$PersistAtoms2.imsDedicatedBearerListenerEvent = new PersistAtomsProto$ImsDedicatedBearerListenerEvent[0];
            saveAtomsToFile(500);
            return persistAtomsProto$ImsDedicatedBearerListenerEventArr;
        }
        return null;
    }

    public synchronized PersistAtomsProto$ImsDedicatedBearerEvent[] getImsDedicatedBearerEvent(long j) {
        long wallTimeMillis = getWallTimeMillis();
        PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
        if (wallTimeMillis - persistAtomsProto$PersistAtoms.imsDedicatedBearerEventPullTimestampMillis > j) {
            persistAtomsProto$PersistAtoms.imsDedicatedBearerEventPullTimestampMillis = getWallTimeMillis();
            PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms2 = this.mAtoms;
            PersistAtomsProto$ImsDedicatedBearerEvent[] persistAtomsProto$ImsDedicatedBearerEventArr = persistAtomsProto$PersistAtoms2.imsDedicatedBearerEvent;
            persistAtomsProto$PersistAtoms2.imsDedicatedBearerEvent = new PersistAtomsProto$ImsDedicatedBearerEvent[0];
            saveAtomsToFile(500);
            return persistAtomsProto$ImsDedicatedBearerEventArr;
        }
        return null;
    }

    public synchronized PersistAtomsProto$ImsRegistrationServiceDescStats[] getImsRegistrationServiceDescStats(long j) {
        long wallTimeMillis = getWallTimeMillis();
        PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
        long j2 = wallTimeMillis - persistAtomsProto$PersistAtoms.imsRegistrationServiceDescStatsPullTimestampMillis;
        if (j2 > j) {
            persistAtomsProto$PersistAtoms.imsRegistrationServiceDescStatsPullTimestampMillis = getWallTimeMillis();
            PersistAtomsProto$ImsRegistrationServiceDescStats[] persistAtomsProto$ImsRegistrationServiceDescStatsArr = this.mAtoms.imsRegistrationServiceDescStats;
            for (PersistAtomsProto$ImsRegistrationServiceDescStats persistAtomsProto$ImsRegistrationServiceDescStats : persistAtomsProto$ImsRegistrationServiceDescStatsArr) {
                if (j2 > 86400000) {
                    persistAtomsProto$ImsRegistrationServiceDescStats.publishedMillis = normalizeDurationTo24H(persistAtomsProto$ImsRegistrationServiceDescStats.publishedMillis, j2);
                }
            }
            this.mAtoms.imsRegistrationServiceDescStats = new PersistAtomsProto$ImsRegistrationServiceDescStats[0];
            saveAtomsToFile(500);
            return persistAtomsProto$ImsRegistrationServiceDescStatsArr;
        }
        return null;
    }

    public synchronized PersistAtomsProto$UceEventStats[] getUceEventStats(long j) {
        long wallTimeMillis = getWallTimeMillis();
        PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
        if (wallTimeMillis - persistAtomsProto$PersistAtoms.uceEventStatsPullTimestampMillis > j) {
            persistAtomsProto$PersistAtoms.uceEventStatsPullTimestampMillis = getWallTimeMillis();
            PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms2 = this.mAtoms;
            PersistAtomsProto$UceEventStats[] persistAtomsProto$UceEventStatsArr = persistAtomsProto$PersistAtoms2.uceEventStats;
            persistAtomsProto$PersistAtoms2.uceEventStats = new PersistAtomsProto$UceEventStats[0];
            saveAtomsToFile(500);
            return persistAtomsProto$UceEventStatsArr;
        }
        return null;
    }

    public synchronized PersistAtomsProto$PresenceNotifyEvent[] getPresenceNotifyEvent(long j) {
        long wallTimeMillis = getWallTimeMillis();
        PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
        if (wallTimeMillis - persistAtomsProto$PersistAtoms.presenceNotifyEventPullTimestampMillis > j) {
            persistAtomsProto$PersistAtoms.presenceNotifyEventPullTimestampMillis = getWallTimeMillis();
            PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms2 = this.mAtoms;
            PersistAtomsProto$PresenceNotifyEvent[] persistAtomsProto$PresenceNotifyEventArr = persistAtomsProto$PersistAtoms2.presenceNotifyEvent;
            persistAtomsProto$PersistAtoms2.presenceNotifyEvent = new PersistAtomsProto$PresenceNotifyEvent[0];
            saveAtomsToFile(500);
            return persistAtomsProto$PresenceNotifyEventArr;
        }
        return null;
    }

    public synchronized PersistAtomsProto$GbaEvent[] getGbaEvent(long j) {
        long wallTimeMillis = getWallTimeMillis();
        PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
        if (wallTimeMillis - persistAtomsProto$PersistAtoms.gbaEventPullTimestampMillis > j) {
            persistAtomsProto$PersistAtoms.gbaEventPullTimestampMillis = getWallTimeMillis();
            PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms2 = this.mAtoms;
            PersistAtomsProto$GbaEvent[] persistAtomsProto$GbaEventArr = persistAtomsProto$PersistAtoms2.gbaEvent;
            persistAtomsProto$PersistAtoms2.gbaEvent = new PersistAtomsProto$GbaEvent[0];
            saveAtomsToFile(500);
            return persistAtomsProto$GbaEventArr;
        }
        return null;
    }

    public synchronized long getUnmeteredNetworks(int i, int i2) {
        PersistAtomsProto$UnmeteredNetworks findUnmeteredNetworks = findUnmeteredNetworks(i);
        long j = 0;
        if (findUnmeteredNetworks == null) {
            return 0L;
        }
        if (findUnmeteredNetworks.carrierId == i2) {
            j = findUnmeteredNetworks.unmeteredNetworksBitmask;
        }
        PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
        persistAtomsProto$PersistAtoms.unmeteredNetworks = (PersistAtomsProto$UnmeteredNetworks[]) sanitizeAtoms((PersistAtomsProto$UnmeteredNetworks[]) ArrayUtils.removeElement(PersistAtomsProto$UnmeteredNetworks.class, persistAtomsProto$PersistAtoms.unmeteredNetworks, findUnmeteredNetworks), PersistAtomsProto$UnmeteredNetworks.class);
        saveAtomsToFile(500);
        return j;
    }

    public synchronized PersistAtomsProto$OutgoingShortCodeSms[] getOutgoingShortCodeSms(long j) {
        long wallTimeMillis = getWallTimeMillis();
        PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = this.mAtoms;
        if (wallTimeMillis - persistAtomsProto$PersistAtoms.outgoingShortCodeSmsPullTimestampMillis > j) {
            persistAtomsProto$PersistAtoms.outgoingShortCodeSmsPullTimestampMillis = getWallTimeMillis();
            PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms2 = this.mAtoms;
            PersistAtomsProto$OutgoingShortCodeSms[] persistAtomsProto$OutgoingShortCodeSmsArr = persistAtomsProto$PersistAtoms2.outgoingShortCodeSms;
            persistAtomsProto$PersistAtoms2.outgoingShortCodeSms = new PersistAtomsProto$OutgoingShortCodeSms[0];
            saveAtomsToFile(500);
            return persistAtomsProto$OutgoingShortCodeSmsArr;
        }
        return null;
    }

    public synchronized void flushAtoms() {
        saveAtomsToFile(0);
    }

    public synchronized void clearAtoms() {
        this.mAtoms = makeNewPersistAtoms();
        saveAtomsToFile(0);
    }

    private PersistAtomsProto$PersistAtoms loadAtomsFromFile() {
        try {
            PersistAtomsProto$PersistAtoms parseFrom = PersistAtomsProto$PersistAtoms.parseFrom(Files.readAllBytes(this.mContext.getFileStreamPath("persist_atoms.pb").toPath()));
            if (!Build.FINGERPRINT.equals(parseFrom.buildFingerprint)) {
                Rlog.d(TAG, "Build changed");
                return makeNewPersistAtoms();
            }
            parseFrom.voiceCallRatUsage = (PersistAtomsProto$VoiceCallRatUsage[]) sanitizeAtoms(parseFrom.voiceCallRatUsage, PersistAtomsProto$VoiceCallRatUsage.class);
            parseFrom.voiceCallSession = (PersistAtomsProto$VoiceCallSession[]) sanitizeAtoms(parseFrom.voiceCallSession, PersistAtomsProto$VoiceCallSession.class, this.mMaxNumVoiceCallSessions);
            parseFrom.incomingSms = (PersistAtomsProto$IncomingSms[]) sanitizeAtoms(parseFrom.incomingSms, PersistAtomsProto$IncomingSms.class, this.mMaxNumSms);
            parseFrom.outgoingSms = (PersistAtomsProto$OutgoingSms[]) sanitizeAtoms(parseFrom.outgoingSms, PersistAtomsProto$OutgoingSms.class, this.mMaxNumSms);
            parseFrom.carrierIdMismatch = (PersistAtomsProto$CarrierIdMismatch[]) sanitizeAtoms(parseFrom.carrierIdMismatch, PersistAtomsProto$CarrierIdMismatch.class, this.mMaxNumCarrierIdMismatches);
            parseFrom.dataCallSession = (PersistAtomsProto$DataCallSession[]) sanitizeAtoms(parseFrom.dataCallSession, PersistAtomsProto$DataCallSession.class, this.mMaxNumDataCallSessions);
            parseFrom.cellularServiceState = (PersistAtomsProto$CellularServiceState[]) sanitizeAtoms(parseFrom.cellularServiceState, PersistAtomsProto$CellularServiceState.class, this.mMaxNumCellularServiceStates);
            parseFrom.cellularDataServiceSwitch = (PersistAtomsProto$CellularDataServiceSwitch[]) sanitizeAtoms(parseFrom.cellularDataServiceSwitch, PersistAtomsProto$CellularDataServiceSwitch.class, this.mMaxNumCellularDataSwitches);
            parseFrom.imsRegistrationStats = (PersistAtomsProto$ImsRegistrationStats[]) sanitizeAtoms(parseFrom.imsRegistrationStats, PersistAtomsProto$ImsRegistrationStats.class, this.mMaxNumImsRegistrationStats);
            parseFrom.imsRegistrationTermination = (PersistAtomsProto$ImsRegistrationTermination[]) sanitizeAtoms(parseFrom.imsRegistrationTermination, PersistAtomsProto$ImsRegistrationTermination.class, this.mMaxNumImsRegistrationTerminations);
            parseFrom.networkRequestsV2 = (PersistAtomsProto$NetworkRequestsV2[]) sanitizeAtoms(parseFrom.networkRequestsV2, PersistAtomsProto$NetworkRequestsV2.class);
            parseFrom.imsRegistrationFeatureTagStats = (PersistAtomsProto$ImsRegistrationFeatureTagStats[]) sanitizeAtoms(parseFrom.imsRegistrationFeatureTagStats, PersistAtomsProto$ImsRegistrationFeatureTagStats.class, this.mMaxNumImsRegistrationFeatureStats);
            parseFrom.rcsClientProvisioningStats = (PersistAtomsProto$RcsClientProvisioningStats[]) sanitizeAtoms(parseFrom.rcsClientProvisioningStats, PersistAtomsProto$RcsClientProvisioningStats.class, this.mMaxNumRcsClientProvisioningStats);
            parseFrom.rcsAcsProvisioningStats = (PersistAtomsProto$RcsAcsProvisioningStats[]) sanitizeAtoms(parseFrom.rcsAcsProvisioningStats, PersistAtomsProto$RcsAcsProvisioningStats.class, this.mMaxNumRcsAcsProvisioningStats);
            parseFrom.sipDelegateStats = (PersistAtomsProto$SipDelegateStats[]) sanitizeAtoms(parseFrom.sipDelegateStats, PersistAtomsProto$SipDelegateStats.class, this.mMaxNumSipDelegateStats);
            parseFrom.sipTransportFeatureTagStats = (PersistAtomsProto$SipTransportFeatureTagStats[]) sanitizeAtoms(parseFrom.sipTransportFeatureTagStats, PersistAtomsProto$SipTransportFeatureTagStats.class, this.mMaxNumSipTransportFeatureTagStats);
            parseFrom.sipMessageResponse = (PersistAtomsProto$SipMessageResponse[]) sanitizeAtoms(parseFrom.sipMessageResponse, PersistAtomsProto$SipMessageResponse.class, this.mMaxNumSipMessageResponseStats);
            parseFrom.sipTransportSession = (PersistAtomsProto$SipTransportSession[]) sanitizeAtoms(parseFrom.sipTransportSession, PersistAtomsProto$SipTransportSession.class, this.mMaxNumSipTransportSessionStats);
            parseFrom.imsDedicatedBearerListenerEvent = (PersistAtomsProto$ImsDedicatedBearerListenerEvent[]) sanitizeAtoms(parseFrom.imsDedicatedBearerListenerEvent, PersistAtomsProto$ImsDedicatedBearerListenerEvent.class, this.mMaxNumDedicatedBearerListenerEventStats);
            parseFrom.imsDedicatedBearerEvent = (PersistAtomsProto$ImsDedicatedBearerEvent[]) sanitizeAtoms(parseFrom.imsDedicatedBearerEvent, PersistAtomsProto$ImsDedicatedBearerEvent.class, this.mMaxNumDedicatedBearerEventStats);
            parseFrom.imsRegistrationServiceDescStats = (PersistAtomsProto$ImsRegistrationServiceDescStats[]) sanitizeAtoms(parseFrom.imsRegistrationServiceDescStats, PersistAtomsProto$ImsRegistrationServiceDescStats.class, this.mMaxNumImsRegistrationServiceDescStats);
            parseFrom.uceEventStats = (PersistAtomsProto$UceEventStats[]) sanitizeAtoms(parseFrom.uceEventStats, PersistAtomsProto$UceEventStats.class, this.mMaxNumUceEventStats);
            parseFrom.presenceNotifyEvent = (PersistAtomsProto$PresenceNotifyEvent[]) sanitizeAtoms(parseFrom.presenceNotifyEvent, PersistAtomsProto$PresenceNotifyEvent.class, this.mMaxNumPresenceNotifyEventStats);
            parseFrom.gbaEvent = (PersistAtomsProto$GbaEvent[]) sanitizeAtoms(parseFrom.gbaEvent, PersistAtomsProto$GbaEvent.class, this.mMaxNumGbaEventStats);
            parseFrom.unmeteredNetworks = (PersistAtomsProto$UnmeteredNetworks[]) sanitizeAtoms(parseFrom.unmeteredNetworks, PersistAtomsProto$UnmeteredNetworks.class);
            parseFrom.outgoingShortCodeSms = (PersistAtomsProto$OutgoingShortCodeSms[]) sanitizeAtoms(parseFrom.outgoingShortCodeSms, PersistAtomsProto$OutgoingShortCodeSms.class, this.mMaxOutgoingShortCodeSms);
            parseFrom.voiceCallRatUsagePullTimestampMillis = sanitizeTimestamp(parseFrom.voiceCallRatUsagePullTimestampMillis);
            parseFrom.voiceCallSessionPullTimestampMillis = sanitizeTimestamp(parseFrom.voiceCallSessionPullTimestampMillis);
            parseFrom.incomingSmsPullTimestampMillis = sanitizeTimestamp(parseFrom.incomingSmsPullTimestampMillis);
            parseFrom.outgoingSmsPullTimestampMillis = sanitizeTimestamp(parseFrom.outgoingSmsPullTimestampMillis);
            parseFrom.dataCallSessionPullTimestampMillis = sanitizeTimestamp(parseFrom.dataCallSessionPullTimestampMillis);
            parseFrom.cellularServiceStatePullTimestampMillis = sanitizeTimestamp(parseFrom.cellularServiceStatePullTimestampMillis);
            parseFrom.cellularDataServiceSwitchPullTimestampMillis = sanitizeTimestamp(parseFrom.cellularDataServiceSwitchPullTimestampMillis);
            parseFrom.imsRegistrationStatsPullTimestampMillis = sanitizeTimestamp(parseFrom.imsRegistrationStatsPullTimestampMillis);
            parseFrom.imsRegistrationTerminationPullTimestampMillis = sanitizeTimestamp(parseFrom.imsRegistrationTerminationPullTimestampMillis);
            parseFrom.networkRequestsV2PullTimestampMillis = sanitizeTimestamp(parseFrom.networkRequestsV2PullTimestampMillis);
            parseFrom.imsRegistrationFeatureTagStatsPullTimestampMillis = sanitizeTimestamp(parseFrom.imsRegistrationFeatureTagStatsPullTimestampMillis);
            parseFrom.rcsClientProvisioningStatsPullTimestampMillis = sanitizeTimestamp(parseFrom.rcsClientProvisioningStatsPullTimestampMillis);
            parseFrom.rcsAcsProvisioningStatsPullTimestampMillis = sanitizeTimestamp(parseFrom.rcsAcsProvisioningStatsPullTimestampMillis);
            parseFrom.sipDelegateStatsPullTimestampMillis = sanitizeTimestamp(parseFrom.sipDelegateStatsPullTimestampMillis);
            parseFrom.sipTransportFeatureTagStatsPullTimestampMillis = sanitizeTimestamp(parseFrom.sipTransportFeatureTagStatsPullTimestampMillis);
            parseFrom.sipMessageResponsePullTimestampMillis = sanitizeTimestamp(parseFrom.sipMessageResponsePullTimestampMillis);
            parseFrom.sipTransportSessionPullTimestampMillis = sanitizeTimestamp(parseFrom.sipTransportSessionPullTimestampMillis);
            parseFrom.imsDedicatedBearerListenerEventPullTimestampMillis = sanitizeTimestamp(parseFrom.imsDedicatedBearerListenerEventPullTimestampMillis);
            parseFrom.imsDedicatedBearerEventPullTimestampMillis = sanitizeTimestamp(parseFrom.imsDedicatedBearerEventPullTimestampMillis);
            parseFrom.imsRegistrationServiceDescStatsPullTimestampMillis = sanitizeTimestamp(parseFrom.imsRegistrationServiceDescStatsPullTimestampMillis);
            parseFrom.uceEventStatsPullTimestampMillis = sanitizeTimestamp(parseFrom.uceEventStatsPullTimestampMillis);
            parseFrom.presenceNotifyEventPullTimestampMillis = sanitizeTimestamp(parseFrom.presenceNotifyEventPullTimestampMillis);
            parseFrom.gbaEventPullTimestampMillis = sanitizeTimestamp(parseFrom.gbaEventPullTimestampMillis);
            parseFrom.outgoingShortCodeSmsPullTimestampMillis = sanitizeTimestamp(parseFrom.outgoingShortCodeSmsPullTimestampMillis);
            return parseFrom;
        } catch (NoSuchFileException unused) {
            Rlog.d(TAG, "PersistAtoms file not found");
            return makeNewPersistAtoms();
        } catch (IOException | NullPointerException e) {
            Rlog.e(TAG, "cannot load/parse PersistAtoms", e);
            return makeNewPersistAtoms();
        }
    }

    private synchronized void saveAtomsToFile(int i) {
        this.mHandler.removeCallbacks(this.mSaveRunnable);
        if (i <= 0 || this.mSaveImmediately || !this.mHandler.postDelayed(this.mSaveRunnable, i)) {
            saveAtomsToFileNow();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void saveAtomsToFileNow() {
        FileOutputStream openFileOutput;
        try {
            openFileOutput = this.mContext.openFileOutput("persist_atoms.pb", 0);
        } catch (IOException e) {
            Rlog.e(TAG, "cannot save PersistAtoms", e);
        }
        try {
            openFileOutput.write(MessageNano.toByteArray(this.mAtoms));
            openFileOutput.close();
        } catch (Throwable th) {
            if (openFileOutput != null) {
                try {
                    openFileOutput.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    private PersistAtomsProto$CellularServiceState find(PersistAtomsProto$CellularServiceState persistAtomsProto$CellularServiceState) {
        PersistAtomsProto$CellularServiceState[] persistAtomsProto$CellularServiceStateArr;
        for (PersistAtomsProto$CellularServiceState persistAtomsProto$CellularServiceState2 : this.mAtoms.cellularServiceState) {
            if (persistAtomsProto$CellularServiceState2.voiceRat == persistAtomsProto$CellularServiceState.voiceRat && persistAtomsProto$CellularServiceState2.dataRat == persistAtomsProto$CellularServiceState.dataRat && persistAtomsProto$CellularServiceState2.voiceRoamingType == persistAtomsProto$CellularServiceState.voiceRoamingType && persistAtomsProto$CellularServiceState2.dataRoamingType == persistAtomsProto$CellularServiceState.dataRoamingType && persistAtomsProto$CellularServiceState2.isEndc == persistAtomsProto$CellularServiceState.isEndc && persistAtomsProto$CellularServiceState2.simSlotIndex == persistAtomsProto$CellularServiceState.simSlotIndex && persistAtomsProto$CellularServiceState2.isMultiSim == persistAtomsProto$CellularServiceState.isMultiSim && persistAtomsProto$CellularServiceState2.carrierId == persistAtomsProto$CellularServiceState.carrierId && persistAtomsProto$CellularServiceState2.isEmergencyOnly == persistAtomsProto$CellularServiceState.isEmergencyOnly && persistAtomsProto$CellularServiceState2.isInternetPdnUp == persistAtomsProto$CellularServiceState.isInternetPdnUp) {
                return persistAtomsProto$CellularServiceState2;
            }
        }
        return null;
    }

    private PersistAtomsProto$CellularDataServiceSwitch find(PersistAtomsProto$CellularDataServiceSwitch persistAtomsProto$CellularDataServiceSwitch) {
        PersistAtomsProto$CellularDataServiceSwitch[] persistAtomsProto$CellularDataServiceSwitchArr;
        for (PersistAtomsProto$CellularDataServiceSwitch persistAtomsProto$CellularDataServiceSwitch2 : this.mAtoms.cellularDataServiceSwitch) {
            if (persistAtomsProto$CellularDataServiceSwitch2.ratFrom == persistAtomsProto$CellularDataServiceSwitch.ratFrom && persistAtomsProto$CellularDataServiceSwitch2.ratTo == persistAtomsProto$CellularDataServiceSwitch.ratTo && persistAtomsProto$CellularDataServiceSwitch2.simSlotIndex == persistAtomsProto$CellularDataServiceSwitch.simSlotIndex && persistAtomsProto$CellularDataServiceSwitch2.isMultiSim == persistAtomsProto$CellularDataServiceSwitch.isMultiSim && persistAtomsProto$CellularDataServiceSwitch2.carrierId == persistAtomsProto$CellularDataServiceSwitch.carrierId) {
                return persistAtomsProto$CellularDataServiceSwitch2;
            }
        }
        return null;
    }

    private PersistAtomsProto$CarrierIdMismatch find(PersistAtomsProto$CarrierIdMismatch persistAtomsProto$CarrierIdMismatch) {
        PersistAtomsProto$CarrierIdMismatch[] persistAtomsProto$CarrierIdMismatchArr;
        for (PersistAtomsProto$CarrierIdMismatch persistAtomsProto$CarrierIdMismatch2 : this.mAtoms.carrierIdMismatch) {
            if (persistAtomsProto$CarrierIdMismatch2.mccMnc.equals(persistAtomsProto$CarrierIdMismatch.mccMnc) && persistAtomsProto$CarrierIdMismatch2.gid1.equals(persistAtomsProto$CarrierIdMismatch.gid1) && persistAtomsProto$CarrierIdMismatch2.spn.equals(persistAtomsProto$CarrierIdMismatch.spn) && persistAtomsProto$CarrierIdMismatch2.pnn.equals(persistAtomsProto$CarrierIdMismatch.pnn)) {
                return persistAtomsProto$CarrierIdMismatch2;
            }
        }
        return null;
    }

    private PersistAtomsProto$ImsRegistrationStats find(PersistAtomsProto$ImsRegistrationStats persistAtomsProto$ImsRegistrationStats) {
        PersistAtomsProto$ImsRegistrationStats[] persistAtomsProto$ImsRegistrationStatsArr;
        for (PersistAtomsProto$ImsRegistrationStats persistAtomsProto$ImsRegistrationStats2 : this.mAtoms.imsRegistrationStats) {
            if (persistAtomsProto$ImsRegistrationStats2.carrierId == persistAtomsProto$ImsRegistrationStats.carrierId && persistAtomsProto$ImsRegistrationStats2.simSlotIndex == persistAtomsProto$ImsRegistrationStats.simSlotIndex && persistAtomsProto$ImsRegistrationStats2.rat == persistAtomsProto$ImsRegistrationStats.rat) {
                return persistAtomsProto$ImsRegistrationStats2;
            }
        }
        return null;
    }

    private PersistAtomsProto$ImsRegistrationTermination find(PersistAtomsProto$ImsRegistrationTermination persistAtomsProto$ImsRegistrationTermination) {
        PersistAtomsProto$ImsRegistrationTermination[] persistAtomsProto$ImsRegistrationTerminationArr;
        for (PersistAtomsProto$ImsRegistrationTermination persistAtomsProto$ImsRegistrationTermination2 : this.mAtoms.imsRegistrationTermination) {
            if (persistAtomsProto$ImsRegistrationTermination2.carrierId == persistAtomsProto$ImsRegistrationTermination.carrierId && persistAtomsProto$ImsRegistrationTermination2.isMultiSim == persistAtomsProto$ImsRegistrationTermination.isMultiSim && persistAtomsProto$ImsRegistrationTermination2.ratAtEnd == persistAtomsProto$ImsRegistrationTermination.ratAtEnd && persistAtomsProto$ImsRegistrationTermination2.setupFailed == persistAtomsProto$ImsRegistrationTermination.setupFailed && persistAtomsProto$ImsRegistrationTermination2.reasonCode == persistAtomsProto$ImsRegistrationTermination.reasonCode && persistAtomsProto$ImsRegistrationTermination2.extraCode == persistAtomsProto$ImsRegistrationTermination.extraCode && persistAtomsProto$ImsRegistrationTermination2.extraMessage.equals(persistAtomsProto$ImsRegistrationTermination.extraMessage)) {
                return persistAtomsProto$ImsRegistrationTermination2;
            }
        }
        return null;
    }

    private PersistAtomsProto$NetworkRequestsV2 find(PersistAtomsProto$NetworkRequestsV2 persistAtomsProto$NetworkRequestsV2) {
        PersistAtomsProto$NetworkRequestsV2[] persistAtomsProto$NetworkRequestsV2Arr;
        for (PersistAtomsProto$NetworkRequestsV2 persistAtomsProto$NetworkRequestsV22 : this.mAtoms.networkRequestsV2) {
            if (persistAtomsProto$NetworkRequestsV22.carrierId == persistAtomsProto$NetworkRequestsV2.carrierId && persistAtomsProto$NetworkRequestsV22.capability == persistAtomsProto$NetworkRequestsV2.capability) {
                return persistAtomsProto$NetworkRequestsV22;
            }
        }
        return null;
    }

    private int findIndex(PersistAtomsProto$DataCallSession persistAtomsProto$DataCallSession) {
        int i = 0;
        while (true) {
            PersistAtomsProto$DataCallSession[] persistAtomsProto$DataCallSessionArr = this.mAtoms.dataCallSession;
            if (i >= persistAtomsProto$DataCallSessionArr.length) {
                return -1;
            }
            if (persistAtomsProto$DataCallSessionArr[i].dimension == persistAtomsProto$DataCallSession.dimension) {
                return i;
            }
            i++;
        }
    }

    private PersistAtomsProto$ImsDedicatedBearerListenerEvent find(PersistAtomsProto$ImsDedicatedBearerListenerEvent persistAtomsProto$ImsDedicatedBearerListenerEvent) {
        PersistAtomsProto$ImsDedicatedBearerListenerEvent[] persistAtomsProto$ImsDedicatedBearerListenerEventArr;
        for (PersistAtomsProto$ImsDedicatedBearerListenerEvent persistAtomsProto$ImsDedicatedBearerListenerEvent2 : this.mAtoms.imsDedicatedBearerListenerEvent) {
            if (persistAtomsProto$ImsDedicatedBearerListenerEvent2.carrierId == persistAtomsProto$ImsDedicatedBearerListenerEvent.carrierId && persistAtomsProto$ImsDedicatedBearerListenerEvent2.slotId == persistAtomsProto$ImsDedicatedBearerListenerEvent.slotId && persistAtomsProto$ImsDedicatedBearerListenerEvent2.ratAtEnd == persistAtomsProto$ImsDedicatedBearerListenerEvent.ratAtEnd && persistAtomsProto$ImsDedicatedBearerListenerEvent2.qci == persistAtomsProto$ImsDedicatedBearerListenerEvent.qci && persistAtomsProto$ImsDedicatedBearerListenerEvent2.dedicatedBearerEstablished == persistAtomsProto$ImsDedicatedBearerListenerEvent.dedicatedBearerEstablished) {
                return persistAtomsProto$ImsDedicatedBearerListenerEvent2;
            }
        }
        return null;
    }

    private PersistAtomsProto$ImsDedicatedBearerEvent find(PersistAtomsProto$ImsDedicatedBearerEvent persistAtomsProto$ImsDedicatedBearerEvent) {
        PersistAtomsProto$ImsDedicatedBearerEvent[] persistAtomsProto$ImsDedicatedBearerEventArr;
        for (PersistAtomsProto$ImsDedicatedBearerEvent persistAtomsProto$ImsDedicatedBearerEvent2 : this.mAtoms.imsDedicatedBearerEvent) {
            if (persistAtomsProto$ImsDedicatedBearerEvent2.carrierId == persistAtomsProto$ImsDedicatedBearerEvent.carrierId && persistAtomsProto$ImsDedicatedBearerEvent2.slotId == persistAtomsProto$ImsDedicatedBearerEvent.slotId && persistAtomsProto$ImsDedicatedBearerEvent2.ratAtEnd == persistAtomsProto$ImsDedicatedBearerEvent.ratAtEnd && persistAtomsProto$ImsDedicatedBearerEvent2.qci == persistAtomsProto$ImsDedicatedBearerEvent.qci && persistAtomsProto$ImsDedicatedBearerEvent2.bearerState == persistAtomsProto$ImsDedicatedBearerEvent.bearerState && persistAtomsProto$ImsDedicatedBearerEvent2.localConnectionInfoReceived == persistAtomsProto$ImsDedicatedBearerEvent.localConnectionInfoReceived && persistAtomsProto$ImsDedicatedBearerEvent2.remoteConnectionInfoReceived == persistAtomsProto$ImsDedicatedBearerEvent.remoteConnectionInfoReceived && persistAtomsProto$ImsDedicatedBearerEvent2.hasListeners == persistAtomsProto$ImsDedicatedBearerEvent.hasListeners) {
                return persistAtomsProto$ImsDedicatedBearerEvent2;
            }
        }
        return null;
    }

    private PersistAtomsProto$ImsRegistrationFeatureTagStats find(PersistAtomsProto$ImsRegistrationFeatureTagStats persistAtomsProto$ImsRegistrationFeatureTagStats) {
        PersistAtomsProto$ImsRegistrationFeatureTagStats[] persistAtomsProto$ImsRegistrationFeatureTagStatsArr;
        for (PersistAtomsProto$ImsRegistrationFeatureTagStats persistAtomsProto$ImsRegistrationFeatureTagStats2 : this.mAtoms.imsRegistrationFeatureTagStats) {
            if (persistAtomsProto$ImsRegistrationFeatureTagStats2.carrierId == persistAtomsProto$ImsRegistrationFeatureTagStats.carrierId && persistAtomsProto$ImsRegistrationFeatureTagStats2.slotId == persistAtomsProto$ImsRegistrationFeatureTagStats.slotId && persistAtomsProto$ImsRegistrationFeatureTagStats2.featureTagName == persistAtomsProto$ImsRegistrationFeatureTagStats.featureTagName && persistAtomsProto$ImsRegistrationFeatureTagStats2.registrationTech == persistAtomsProto$ImsRegistrationFeatureTagStats.registrationTech) {
                return persistAtomsProto$ImsRegistrationFeatureTagStats2;
            }
        }
        return null;
    }

    private PersistAtomsProto$RcsClientProvisioningStats find(PersistAtomsProto$RcsClientProvisioningStats persistAtomsProto$RcsClientProvisioningStats) {
        PersistAtomsProto$RcsClientProvisioningStats[] persistAtomsProto$RcsClientProvisioningStatsArr;
        for (PersistAtomsProto$RcsClientProvisioningStats persistAtomsProto$RcsClientProvisioningStats2 : this.mAtoms.rcsClientProvisioningStats) {
            if (persistAtomsProto$RcsClientProvisioningStats2.carrierId == persistAtomsProto$RcsClientProvisioningStats.carrierId && persistAtomsProto$RcsClientProvisioningStats2.slotId == persistAtomsProto$RcsClientProvisioningStats.slotId && persistAtomsProto$RcsClientProvisioningStats2.event == persistAtomsProto$RcsClientProvisioningStats.event) {
                return persistAtomsProto$RcsClientProvisioningStats2;
            }
        }
        return null;
    }

    private PersistAtomsProto$RcsAcsProvisioningStats find(PersistAtomsProto$RcsAcsProvisioningStats persistAtomsProto$RcsAcsProvisioningStats) {
        PersistAtomsProto$RcsAcsProvisioningStats[] persistAtomsProto$RcsAcsProvisioningStatsArr;
        for (PersistAtomsProto$RcsAcsProvisioningStats persistAtomsProto$RcsAcsProvisioningStats2 : this.mAtoms.rcsAcsProvisioningStats) {
            if (persistAtomsProto$RcsAcsProvisioningStats2.carrierId == persistAtomsProto$RcsAcsProvisioningStats.carrierId && persistAtomsProto$RcsAcsProvisioningStats2.slotId == persistAtomsProto$RcsAcsProvisioningStats.slotId && persistAtomsProto$RcsAcsProvisioningStats2.responseCode == persistAtomsProto$RcsAcsProvisioningStats.responseCode && persistAtomsProto$RcsAcsProvisioningStats2.responseType == persistAtomsProto$RcsAcsProvisioningStats.responseType && persistAtomsProto$RcsAcsProvisioningStats2.isSingleRegistrationEnabled == persistAtomsProto$RcsAcsProvisioningStats.isSingleRegistrationEnabled) {
                return persistAtomsProto$RcsAcsProvisioningStats2;
            }
        }
        return null;
    }

    private PersistAtomsProto$SipMessageResponse find(PersistAtomsProto$SipMessageResponse persistAtomsProto$SipMessageResponse) {
        PersistAtomsProto$SipMessageResponse[] persistAtomsProto$SipMessageResponseArr;
        for (PersistAtomsProto$SipMessageResponse persistAtomsProto$SipMessageResponse2 : this.mAtoms.sipMessageResponse) {
            if (persistAtomsProto$SipMessageResponse2.carrierId == persistAtomsProto$SipMessageResponse.carrierId && persistAtomsProto$SipMessageResponse2.slotId == persistAtomsProto$SipMessageResponse.slotId && persistAtomsProto$SipMessageResponse2.sipMessageMethod == persistAtomsProto$SipMessageResponse.sipMessageMethod && persistAtomsProto$SipMessageResponse2.sipMessageResponse == persistAtomsProto$SipMessageResponse.sipMessageResponse && persistAtomsProto$SipMessageResponse2.sipMessageDirection == persistAtomsProto$SipMessageResponse.sipMessageDirection && persistAtomsProto$SipMessageResponse2.messageError == persistAtomsProto$SipMessageResponse.messageError) {
                return persistAtomsProto$SipMessageResponse2;
            }
        }
        return null;
    }

    private PersistAtomsProto$SipTransportSession find(PersistAtomsProto$SipTransportSession persistAtomsProto$SipTransportSession) {
        PersistAtomsProto$SipTransportSession[] persistAtomsProto$SipTransportSessionArr;
        for (PersistAtomsProto$SipTransportSession persistAtomsProto$SipTransportSession2 : this.mAtoms.sipTransportSession) {
            if (persistAtomsProto$SipTransportSession2.carrierId == persistAtomsProto$SipTransportSession.carrierId && persistAtomsProto$SipTransportSession2.slotId == persistAtomsProto$SipTransportSession.slotId && persistAtomsProto$SipTransportSession2.sessionMethod == persistAtomsProto$SipTransportSession.sessionMethod && persistAtomsProto$SipTransportSession2.sipMessageDirection == persistAtomsProto$SipTransportSession.sipMessageDirection && persistAtomsProto$SipTransportSession2.sipResponse == persistAtomsProto$SipTransportSession.sipResponse) {
                return persistAtomsProto$SipTransportSession2;
            }
        }
        return null;
    }

    private PersistAtomsProto$ImsRegistrationServiceDescStats find(PersistAtomsProto$ImsRegistrationServiceDescStats persistAtomsProto$ImsRegistrationServiceDescStats) {
        PersistAtomsProto$ImsRegistrationServiceDescStats[] persistAtomsProto$ImsRegistrationServiceDescStatsArr;
        for (PersistAtomsProto$ImsRegistrationServiceDescStats persistAtomsProto$ImsRegistrationServiceDescStats2 : this.mAtoms.imsRegistrationServiceDescStats) {
            if (persistAtomsProto$ImsRegistrationServiceDescStats2.carrierId == persistAtomsProto$ImsRegistrationServiceDescStats.carrierId && persistAtomsProto$ImsRegistrationServiceDescStats2.slotId == persistAtomsProto$ImsRegistrationServiceDescStats.slotId && persistAtomsProto$ImsRegistrationServiceDescStats2.serviceIdName == persistAtomsProto$ImsRegistrationServiceDescStats.serviceIdName && persistAtomsProto$ImsRegistrationServiceDescStats2.serviceIdVersion == persistAtomsProto$ImsRegistrationServiceDescStats.serviceIdVersion && persistAtomsProto$ImsRegistrationServiceDescStats2.registrationTech == persistAtomsProto$ImsRegistrationServiceDescStats.registrationTech) {
                return persistAtomsProto$ImsRegistrationServiceDescStats2;
            }
        }
        return null;
    }

    private PersistAtomsProto$UceEventStats find(PersistAtomsProto$UceEventStats persistAtomsProto$UceEventStats) {
        PersistAtomsProto$UceEventStats[] persistAtomsProto$UceEventStatsArr;
        for (PersistAtomsProto$UceEventStats persistAtomsProto$UceEventStats2 : this.mAtoms.uceEventStats) {
            if (persistAtomsProto$UceEventStats2.carrierId == persistAtomsProto$UceEventStats.carrierId && persistAtomsProto$UceEventStats2.slotId == persistAtomsProto$UceEventStats.slotId && persistAtomsProto$UceEventStats2.type == persistAtomsProto$UceEventStats.type && persistAtomsProto$UceEventStats2.successful == persistAtomsProto$UceEventStats.successful && persistAtomsProto$UceEventStats2.commandCode == persistAtomsProto$UceEventStats.commandCode && persistAtomsProto$UceEventStats2.networkResponse == persistAtomsProto$UceEventStats.networkResponse) {
                return persistAtomsProto$UceEventStats2;
            }
        }
        return null;
    }

    private PersistAtomsProto$PresenceNotifyEvent find(PersistAtomsProto$PresenceNotifyEvent persistAtomsProto$PresenceNotifyEvent) {
        PersistAtomsProto$PresenceNotifyEvent[] persistAtomsProto$PresenceNotifyEventArr;
        for (PersistAtomsProto$PresenceNotifyEvent persistAtomsProto$PresenceNotifyEvent2 : this.mAtoms.presenceNotifyEvent) {
            if (persistAtomsProto$PresenceNotifyEvent2.carrierId == persistAtomsProto$PresenceNotifyEvent.carrierId && persistAtomsProto$PresenceNotifyEvent2.slotId == persistAtomsProto$PresenceNotifyEvent.slotId && persistAtomsProto$PresenceNotifyEvent2.reason == persistAtomsProto$PresenceNotifyEvent.reason && persistAtomsProto$PresenceNotifyEvent2.contentBodyReceived == persistAtomsProto$PresenceNotifyEvent.contentBodyReceived) {
                return persistAtomsProto$PresenceNotifyEvent2;
            }
        }
        return null;
    }

    private PersistAtomsProto$GbaEvent find(PersistAtomsProto$GbaEvent persistAtomsProto$GbaEvent) {
        PersistAtomsProto$GbaEvent[] persistAtomsProto$GbaEventArr;
        for (PersistAtomsProto$GbaEvent persistAtomsProto$GbaEvent2 : this.mAtoms.gbaEvent) {
            if (persistAtomsProto$GbaEvent2.carrierId == persistAtomsProto$GbaEvent.carrierId && persistAtomsProto$GbaEvent2.slotId == persistAtomsProto$GbaEvent.slotId && persistAtomsProto$GbaEvent2.successful == persistAtomsProto$GbaEvent.successful && persistAtomsProto$GbaEvent2.failedReason == persistAtomsProto$GbaEvent.failedReason) {
                return persistAtomsProto$GbaEvent2;
            }
        }
        return null;
    }

    private PersistAtomsProto$SipTransportFeatureTagStats find(PersistAtomsProto$SipTransportFeatureTagStats persistAtomsProto$SipTransportFeatureTagStats) {
        PersistAtomsProto$SipTransportFeatureTagStats[] persistAtomsProto$SipTransportFeatureTagStatsArr;
        for (PersistAtomsProto$SipTransportFeatureTagStats persistAtomsProto$SipTransportFeatureTagStats2 : this.mAtoms.sipTransportFeatureTagStats) {
            if (persistAtomsProto$SipTransportFeatureTagStats2.carrierId == persistAtomsProto$SipTransportFeatureTagStats.carrierId && persistAtomsProto$SipTransportFeatureTagStats2.slotId == persistAtomsProto$SipTransportFeatureTagStats.slotId && persistAtomsProto$SipTransportFeatureTagStats2.featureTagName == persistAtomsProto$SipTransportFeatureTagStats.featureTagName && persistAtomsProto$SipTransportFeatureTagStats2.sipTransportDeregisteredReason == persistAtomsProto$SipTransportFeatureTagStats.sipTransportDeregisteredReason && persistAtomsProto$SipTransportFeatureTagStats2.sipTransportDeniedReason == persistAtomsProto$SipTransportFeatureTagStats.sipTransportDeniedReason) {
                return persistAtomsProto$SipTransportFeatureTagStats2;
            }
        }
        return null;
    }

    private PersistAtomsProto$UnmeteredNetworks findUnmeteredNetworks(int i) {
        PersistAtomsProto$UnmeteredNetworks[] persistAtomsProto$UnmeteredNetworksArr;
        for (PersistAtomsProto$UnmeteredNetworks persistAtomsProto$UnmeteredNetworks : this.mAtoms.unmeteredNetworks) {
            if (persistAtomsProto$UnmeteredNetworks.phoneId == i) {
                return persistAtomsProto$UnmeteredNetworks;
            }
        }
        return null;
    }

    private PersistAtomsProto$OutgoingShortCodeSms find(PersistAtomsProto$OutgoingShortCodeSms persistAtomsProto$OutgoingShortCodeSms) {
        PersistAtomsProto$OutgoingShortCodeSms[] persistAtomsProto$OutgoingShortCodeSmsArr;
        for (PersistAtomsProto$OutgoingShortCodeSms persistAtomsProto$OutgoingShortCodeSms2 : this.mAtoms.outgoingShortCodeSms) {
            if (persistAtomsProto$OutgoingShortCodeSms2.category == persistAtomsProto$OutgoingShortCodeSms.category && persistAtomsProto$OutgoingShortCodeSms2.xmlVersion == persistAtomsProto$OutgoingShortCodeSms.xmlVersion) {
                return persistAtomsProto$OutgoingShortCodeSms2;
            }
        }
        return null;
    }

    private static <T> T[] insertAtRandomPlace(T[] tArr, T t, int i) {
        int length = tArr.length + 1;
        boolean z = length > i;
        if (!z) {
            i = length;
        }
        T[] tArr2 = (T[]) Arrays.copyOf(tArr, i);
        if (length == 1) {
            tArr2[0] = t;
        } else if (z) {
            if ((t instanceof PersistAtomsProto$OutgoingSms) || (t instanceof PersistAtomsProto$IncomingSms)) {
                mergeSmsOrEvictInFullStorage(tArr2, t);
            } else {
                tArr2[findItemToEvict(tArr)] = t;
            }
        } else {
            int nextInt = sRandom.nextInt(length);
            tArr2[length - 1] = tArr2[nextInt];
            tArr2[nextInt] = t;
        }
        return tArr2;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static <T> void mergeSmsOrEvictInFullStorage(T[] tArr, T t) {
        new SparseIntArray();
        int i = -1;
        int i2 = KeepaliveStatus.INVALID_HANDLE;
        for (int i3 = 0; i3 < tArr.length; i3++) {
            if (areSmsMergeable(tArr[i3], t)) {
                tArr[i3] = mergeSms(tArr[i3], t);
                return;
            }
            int smsCount = getSmsCount(tArr[i3]);
            if (smsCount < i2) {
                i = i3;
                i2 = smsCount;
            }
        }
        tArr[i] = t;
    }

    private static <T> int getSmsHashCode(T t) {
        return t instanceof PersistAtomsProto$OutgoingSms ? ((PersistAtomsProto$OutgoingSms) t).hashCode : ((PersistAtomsProto$IncomingSms) t).hashCode;
    }

    private static <T> int getSmsCount(T t) {
        return t instanceof PersistAtomsProto$OutgoingSms ? ((PersistAtomsProto$OutgoingSms) t).count : ((PersistAtomsProto$IncomingSms) t).count;
    }

    private static <T> boolean areSmsMergeable(T t, T t2) {
        return getSmsHashCode(t) == getSmsHashCode(t2);
    }

    private static <T> T mergeSms(T t, T t2) {
        if (t instanceof PersistAtomsProto$OutgoingSms) {
            PersistAtomsProto$OutgoingSms persistAtomsProto$OutgoingSms = (PersistAtomsProto$OutgoingSms) t;
            PersistAtomsProto$OutgoingSms persistAtomsProto$OutgoingSms2 = (PersistAtomsProto$OutgoingSms) t2;
            long j = persistAtomsProto$OutgoingSms.intervalMillis;
            int i = persistAtomsProto$OutgoingSms.count;
            long j2 = persistAtomsProto$OutgoingSms2.intervalMillis;
            int i2 = persistAtomsProto$OutgoingSms2.count;
            persistAtomsProto$OutgoingSms.intervalMillis = ((j * i) + (j2 * i2)) / (i + i2);
            persistAtomsProto$OutgoingSms.count = i + i2;
        } else if (t instanceof PersistAtomsProto$IncomingSms) {
            ((PersistAtomsProto$IncomingSms) t).count += ((PersistAtomsProto$IncomingSms) t2).count;
        }
        return t;
    }

    private static <T> int findItemToEvict(T[] tArr) {
        if (tArr instanceof PersistAtomsProto$CellularServiceState[]) {
            final PersistAtomsProto$CellularServiceState[] persistAtomsProto$CellularServiceStateArr = (PersistAtomsProto$CellularServiceState[]) tArr;
            return IntStream.range(0, persistAtomsProto$CellularServiceStateArr.length).reduce(new IntBinaryOperator() { // from class: com.android.internal.telephony.metrics.PersistAtomsStorage$$ExternalSyntheticLambda4
                @Override // java.util.function.IntBinaryOperator
                public final int applyAsInt(int i, int i2) {
                    int lambda$findItemToEvict$5;
                    lambda$findItemToEvict$5 = PersistAtomsStorage.lambda$findItemToEvict$5(persistAtomsProto$CellularServiceStateArr, i, i2);
                    return lambda$findItemToEvict$5;
                }
            }).getAsInt();
        } else if (tArr instanceof PersistAtomsProto$CellularDataServiceSwitch[]) {
            final PersistAtomsProto$CellularDataServiceSwitch[] persistAtomsProto$CellularDataServiceSwitchArr = (PersistAtomsProto$CellularDataServiceSwitch[]) tArr;
            return IntStream.range(0, persistAtomsProto$CellularDataServiceSwitchArr.length).reduce(new IntBinaryOperator() { // from class: com.android.internal.telephony.metrics.PersistAtomsStorage$$ExternalSyntheticLambda5
                @Override // java.util.function.IntBinaryOperator
                public final int applyAsInt(int i, int i2) {
                    int lambda$findItemToEvict$6;
                    lambda$findItemToEvict$6 = PersistAtomsStorage.lambda$findItemToEvict$6(persistAtomsProto$CellularDataServiceSwitchArr, i, i2);
                    return lambda$findItemToEvict$6;
                }
            }).getAsInt();
        } else if (tArr instanceof PersistAtomsProto$ImsRegistrationStats[]) {
            final PersistAtomsProto$ImsRegistrationStats[] persistAtomsProto$ImsRegistrationStatsArr = (PersistAtomsProto$ImsRegistrationStats[]) tArr;
            return IntStream.range(0, persistAtomsProto$ImsRegistrationStatsArr.length).reduce(new IntBinaryOperator() { // from class: com.android.internal.telephony.metrics.PersistAtomsStorage$$ExternalSyntheticLambda6
                @Override // java.util.function.IntBinaryOperator
                public final int applyAsInt(int i, int i2) {
                    int lambda$findItemToEvict$7;
                    lambda$findItemToEvict$7 = PersistAtomsStorage.lambda$findItemToEvict$7(persistAtomsProto$ImsRegistrationStatsArr, i, i2);
                    return lambda$findItemToEvict$7;
                }
            }).getAsInt();
        } else if (tArr instanceof PersistAtomsProto$ImsRegistrationTermination[]) {
            final PersistAtomsProto$ImsRegistrationTermination[] persistAtomsProto$ImsRegistrationTerminationArr = (PersistAtomsProto$ImsRegistrationTermination[]) tArr;
            return IntStream.range(0, persistAtomsProto$ImsRegistrationTerminationArr.length).reduce(new IntBinaryOperator() { // from class: com.android.internal.telephony.metrics.PersistAtomsStorage$$ExternalSyntheticLambda7
                @Override // java.util.function.IntBinaryOperator
                public final int applyAsInt(int i, int i2) {
                    int lambda$findItemToEvict$8;
                    lambda$findItemToEvict$8 = PersistAtomsStorage.lambda$findItemToEvict$8(persistAtomsProto$ImsRegistrationTerminationArr, i, i2);
                    return lambda$findItemToEvict$8;
                }
            }).getAsInt();
        } else {
            if (tArr instanceof PersistAtomsProto$VoiceCallSession[]) {
                final PersistAtomsProto$VoiceCallSession[] persistAtomsProto$VoiceCallSessionArr = (PersistAtomsProto$VoiceCallSession[]) tArr;
                int[] array = IntStream.range(0, persistAtomsProto$VoiceCallSessionArr.length).filter(new IntPredicate() { // from class: com.android.internal.telephony.metrics.PersistAtomsStorage$$ExternalSyntheticLambda8
                    @Override // java.util.function.IntPredicate
                    public final boolean test(int i) {
                        boolean lambda$findItemToEvict$9;
                        lambda$findItemToEvict$9 = PersistAtomsStorage.lambda$findItemToEvict$9(persistAtomsProto$VoiceCallSessionArr, i);
                        return lambda$findItemToEvict$9;
                    }
                }).toArray();
                if (array.length > 0) {
                    return array[sRandom.nextInt(array.length)];
                }
            }
            return sRandom.nextInt(tArr.length);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$findItemToEvict$5(PersistAtomsProto$CellularServiceState[] persistAtomsProto$CellularServiceStateArr, int i, int i2) {
        return persistAtomsProto$CellularServiceStateArr[i].lastUsedMillis < persistAtomsProto$CellularServiceStateArr[i2].lastUsedMillis ? i : i2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$findItemToEvict$6(PersistAtomsProto$CellularDataServiceSwitch[] persistAtomsProto$CellularDataServiceSwitchArr, int i, int i2) {
        return persistAtomsProto$CellularDataServiceSwitchArr[i].lastUsedMillis < persistAtomsProto$CellularDataServiceSwitchArr[i2].lastUsedMillis ? i : i2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$findItemToEvict$7(PersistAtomsProto$ImsRegistrationStats[] persistAtomsProto$ImsRegistrationStatsArr, int i, int i2) {
        return persistAtomsProto$ImsRegistrationStatsArr[i].lastUsedMillis < persistAtomsProto$ImsRegistrationStatsArr[i2].lastUsedMillis ? i : i2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$findItemToEvict$8(PersistAtomsProto$ImsRegistrationTermination[] persistAtomsProto$ImsRegistrationTerminationArr, int i, int i2) {
        return persistAtomsProto$ImsRegistrationTerminationArr[i].lastUsedMillis < persistAtomsProto$ImsRegistrationTerminationArr[i2].lastUsedMillis ? i : i2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$findItemToEvict$9(PersistAtomsProto$VoiceCallSession[] persistAtomsProto$VoiceCallSessionArr, int i) {
        return !persistAtomsProto$VoiceCallSessionArr[i].isEmergency;
    }

    private <T> T[] sanitizeAtoms(T[] tArr, Class<T> cls) {
        return (T[]) ArrayUtils.emptyIfNull(tArr, cls);
    }

    private <T> T[] sanitizeAtoms(T[] tArr, Class<T> cls, int i) {
        T[] tArr2 = (T[]) sanitizeAtoms(tArr, cls);
        return tArr2.length > i ? (T[]) Arrays.copyOf(tArr2, i) : tArr2;
    }

    private long sanitizeTimestamp(long j) {
        return j <= 0 ? getWallTimeMillis() : j;
    }

    private PersistAtomsProto$ImsRegistrationStats[] normalizeData(PersistAtomsProto$ImsRegistrationStats[] persistAtomsProto$ImsRegistrationStatsArr, long j) {
        for (int i = 0; i < persistAtomsProto$ImsRegistrationStatsArr.length; i++) {
            PersistAtomsProto$ImsRegistrationStats persistAtomsProto$ImsRegistrationStats = persistAtomsProto$ImsRegistrationStatsArr[i];
            persistAtomsProto$ImsRegistrationStats.registeredMillis = normalizeDurationTo24H(persistAtomsProto$ImsRegistrationStats.registeredMillis, j);
            PersistAtomsProto$ImsRegistrationStats persistAtomsProto$ImsRegistrationStats2 = persistAtomsProto$ImsRegistrationStatsArr[i];
            persistAtomsProto$ImsRegistrationStats2.voiceCapableMillis = normalizeDurationTo24H(persistAtomsProto$ImsRegistrationStats2.voiceCapableMillis, j);
            PersistAtomsProto$ImsRegistrationStats persistAtomsProto$ImsRegistrationStats3 = persistAtomsProto$ImsRegistrationStatsArr[i];
            persistAtomsProto$ImsRegistrationStats3.voiceAvailableMillis = normalizeDurationTo24H(persistAtomsProto$ImsRegistrationStats3.voiceAvailableMillis, j);
            PersistAtomsProto$ImsRegistrationStats persistAtomsProto$ImsRegistrationStats4 = persistAtomsProto$ImsRegistrationStatsArr[i];
            persistAtomsProto$ImsRegistrationStats4.smsCapableMillis = normalizeDurationTo24H(persistAtomsProto$ImsRegistrationStats4.smsCapableMillis, j);
            PersistAtomsProto$ImsRegistrationStats persistAtomsProto$ImsRegistrationStats5 = persistAtomsProto$ImsRegistrationStatsArr[i];
            persistAtomsProto$ImsRegistrationStats5.smsAvailableMillis = normalizeDurationTo24H(persistAtomsProto$ImsRegistrationStats5.smsAvailableMillis, j);
            PersistAtomsProto$ImsRegistrationStats persistAtomsProto$ImsRegistrationStats6 = persistAtomsProto$ImsRegistrationStatsArr[i];
            persistAtomsProto$ImsRegistrationStats6.videoCapableMillis = normalizeDurationTo24H(persistAtomsProto$ImsRegistrationStats6.videoCapableMillis, j);
            PersistAtomsProto$ImsRegistrationStats persistAtomsProto$ImsRegistrationStats7 = persistAtomsProto$ImsRegistrationStatsArr[i];
            persistAtomsProto$ImsRegistrationStats7.videoAvailableMillis = normalizeDurationTo24H(persistAtomsProto$ImsRegistrationStats7.videoAvailableMillis, j);
            PersistAtomsProto$ImsRegistrationStats persistAtomsProto$ImsRegistrationStats8 = persistAtomsProto$ImsRegistrationStatsArr[i];
            persistAtomsProto$ImsRegistrationStats8.utCapableMillis = normalizeDurationTo24H(persistAtomsProto$ImsRegistrationStats8.utCapableMillis, j);
            PersistAtomsProto$ImsRegistrationStats persistAtomsProto$ImsRegistrationStats9 = persistAtomsProto$ImsRegistrationStatsArr[i];
            persistAtomsProto$ImsRegistrationStats9.utAvailableMillis = normalizeDurationTo24H(persistAtomsProto$ImsRegistrationStats9.utAvailableMillis, j);
        }
        return persistAtomsProto$ImsRegistrationStatsArr;
    }

    private long normalizeDurationTo24H(long j, long j2) {
        return (((j / 1000) * 86400) / (j2 < 1000 ? 1L : j2 / 1000)) * 1000;
    }

    private PersistAtomsProto$PersistAtoms makeNewPersistAtoms() {
        PersistAtomsProto$PersistAtoms persistAtomsProto$PersistAtoms = new PersistAtomsProto$PersistAtoms();
        long wallTimeMillis = getWallTimeMillis();
        persistAtomsProto$PersistAtoms.buildFingerprint = Build.FINGERPRINT;
        persistAtomsProto$PersistAtoms.voiceCallRatUsagePullTimestampMillis = wallTimeMillis;
        persistAtomsProto$PersistAtoms.voiceCallSessionPullTimestampMillis = wallTimeMillis;
        persistAtomsProto$PersistAtoms.incomingSmsPullTimestampMillis = wallTimeMillis;
        persistAtomsProto$PersistAtoms.outgoingSmsPullTimestampMillis = wallTimeMillis;
        persistAtomsProto$PersistAtoms.carrierIdTableVersion = -1;
        persistAtomsProto$PersistAtoms.dataCallSessionPullTimestampMillis = wallTimeMillis;
        persistAtomsProto$PersistAtoms.cellularServiceStatePullTimestampMillis = wallTimeMillis;
        persistAtomsProto$PersistAtoms.cellularDataServiceSwitchPullTimestampMillis = wallTimeMillis;
        persistAtomsProto$PersistAtoms.imsRegistrationStatsPullTimestampMillis = wallTimeMillis;
        persistAtomsProto$PersistAtoms.imsRegistrationTerminationPullTimestampMillis = wallTimeMillis;
        persistAtomsProto$PersistAtoms.networkRequestsPullTimestampMillis = wallTimeMillis;
        persistAtomsProto$PersistAtoms.networkRequestsV2PullTimestampMillis = wallTimeMillis;
        persistAtomsProto$PersistAtoms.imsRegistrationFeatureTagStatsPullTimestampMillis = wallTimeMillis;
        persistAtomsProto$PersistAtoms.rcsClientProvisioningStatsPullTimestampMillis = wallTimeMillis;
        persistAtomsProto$PersistAtoms.rcsAcsProvisioningStatsPullTimestampMillis = wallTimeMillis;
        persistAtomsProto$PersistAtoms.sipDelegateStatsPullTimestampMillis = wallTimeMillis;
        persistAtomsProto$PersistAtoms.sipTransportFeatureTagStatsPullTimestampMillis = wallTimeMillis;
        persistAtomsProto$PersistAtoms.sipMessageResponsePullTimestampMillis = wallTimeMillis;
        persistAtomsProto$PersistAtoms.sipTransportSessionPullTimestampMillis = wallTimeMillis;
        persistAtomsProto$PersistAtoms.imsDedicatedBearerListenerEventPullTimestampMillis = wallTimeMillis;
        persistAtomsProto$PersistAtoms.imsDedicatedBearerEventPullTimestampMillis = wallTimeMillis;
        persistAtomsProto$PersistAtoms.imsRegistrationServiceDescStatsPullTimestampMillis = wallTimeMillis;
        persistAtomsProto$PersistAtoms.uceEventStatsPullTimestampMillis = wallTimeMillis;
        persistAtomsProto$PersistAtoms.presenceNotifyEventPullTimestampMillis = wallTimeMillis;
        persistAtomsProto$PersistAtoms.gbaEventPullTimestampMillis = wallTimeMillis;
        persistAtomsProto$PersistAtoms.outgoingShortCodeSmsPullTimestampMillis = wallTimeMillis;
        Rlog.d(TAG, "created new PersistAtoms");
        return persistAtomsProto$PersistAtoms;
    }

    @VisibleForTesting
    protected long getWallTimeMillis() {
        return System.currentTimeMillis();
    }
}
