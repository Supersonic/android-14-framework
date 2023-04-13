package com.android.internal.telephony.metrics;

import android.app.StatsManager;
import android.content.Context;
import android.telephony.SubscriptionManager;
import android.util.StatsEvent;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.TelephonyStatsLog;
import com.android.internal.telephony.imsphone.ImsPhone;
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
import com.android.internal.telephony.nano.PersistAtomsProto$PresenceNotifyEvent;
import com.android.internal.telephony.nano.PersistAtomsProto$RcsAcsProvisioningStats;
import com.android.internal.telephony.nano.PersistAtomsProto$RcsClientProvisioningStats;
import com.android.internal.telephony.nano.PersistAtomsProto$SipDelegateStats;
import com.android.internal.telephony.nano.PersistAtomsProto$SipMessageResponse;
import com.android.internal.telephony.nano.PersistAtomsProto$SipTransportFeatureTagStats;
import com.android.internal.telephony.nano.PersistAtomsProto$SipTransportSession;
import com.android.internal.telephony.nano.PersistAtomsProto$UceEventStats;
import com.android.internal.telephony.nano.PersistAtomsProto$VoiceCallRatUsage;
import com.android.internal.telephony.nano.PersistAtomsProto$VoiceCallSession;
import com.android.internal.util.ConcurrentUtils;
import com.android.telephony.Rlog;
import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
/* loaded from: classes.dex */
public class MetricsCollector implements StatsManager.StatsPullAtomCallback {
    private static final long DURATION_BUCKET_MILLIS;
    private static final long MILLIS_PER_HOUR;
    private static final long MILLIS_PER_MINUTE;
    private static final long MILLIS_PER_SECOND;
    private static final long MIN_COOLDOWN_MILLIS;
    private static final String TAG = MetricsCollector.class.getSimpleName();
    private static final Random sRandom;
    private final AirplaneModeStats mAirplaneModeStats;
    private final Set<DataCallSessionStats> mOngoingDataCallStats;
    private final StatsManager mStatsManager;
    private final PersistAtomsStorage mStorage;

    static {
        long millis = Duration.ofHours(1L).toMillis();
        MILLIS_PER_HOUR = millis;
        long millis2 = Duration.ofMinutes(1L).toMillis();
        MILLIS_PER_MINUTE = millis2;
        MILLIS_PER_SECOND = Duration.ofSeconds(1L).toMillis();
        MIN_COOLDOWN_MILLIS = millis * 23;
        DURATION_BUCKET_MILLIS = millis2 * 5;
        sRandom = new Random();
    }

    public MetricsCollector(Context context) {
        this(context, new PersistAtomsStorage(context));
    }

    @VisibleForTesting
    public MetricsCollector(Context context, PersistAtomsStorage persistAtomsStorage) {
        this.mOngoingDataCallStats = ConcurrentHashMap.newKeySet();
        this.mStorage = persistAtomsStorage;
        StatsManager statsManager = (StatsManager) context.getSystemService("stats");
        this.mStatsManager = statsManager;
        if (statsManager != null) {
            registerAtom(10091);
            registerAtom(10090);
            registerAtom(10078);
            registerAtom(10079);
            registerAtom(10077);
            registerAtom(10076);
            registerAtom(10086);
            registerAtom(10087);
            registerAtom(10088);
            registerAtom(10089);
            registerAtom(10094);
            registerAtom(10093);
            registerAtom(10153);
            registerAtom(10133);
            registerAtom(10134);
            registerAtom(10135);
            registerAtom(10136);
            registerAtom(10137);
            registerAtom(10138);
            registerAtom(10139);
            registerAtom(10154);
            registerAtom(10140);
            registerAtom(10141);
            registerAtom(10142);
            registerAtom(10143);
            registerAtom(10144);
            registerAtom(10145);
            registerAtom(10146);
            registerAtom(10162);
            Rlog.d(TAG, "registered");
        } else {
            Rlog.e(TAG, "could not get StatsManager, atoms not registered");
        }
        this.mAirplaneModeStats = new AirplaneModeStats(context);
    }

    public int onPullAtom(int i, List<StatsEvent> list) {
        if (i != 10093) {
            if (i != 10094) {
                if (i != 10153) {
                    if (i != 10154) {
                        if (i != 10162) {
                            switch (i) {
                                case 10076:
                                    return pullVoiceCallSessions(list);
                                case 10077:
                                    return pullVoiceCallRatUsages(list);
                                case 10078:
                                    return pullSimSlotState(list);
                                case 10079:
                                    return pullSupportedRadioAccessFamily(list);
                                default:
                                    switch (i) {
                                        case 10086:
                                            return pullIncomingSms(list);
                                        case 10087:
                                            return pullOutgoingSms(list);
                                        case 10088:
                                            return pullCarrierIdTableVersion(list);
                                        case 10089:
                                            return pullDataCallSession(list);
                                        case 10090:
                                            return pullCellularServiceState(list);
                                        case 10091:
                                            return pullCellularDataServiceSwitch(list);
                                        default:
                                            switch (i) {
                                                case 10133:
                                                    return pullImsRegistrationFeatureTagStats(list);
                                                case 10134:
                                                    return pullRcsClientProvisioningStats(list);
                                                case 10135:
                                                    return pullRcsAcsProvisioningStats(list);
                                                case 10136:
                                                    return pullSipDelegateStats(list);
                                                case 10137:
                                                    return pullSipTransportFeatureTagStats(list);
                                                case 10138:
                                                    return pullSipMessageResponse(list);
                                                case 10139:
                                                    return pullSipTransportSession(list);
                                                case 10140:
                                                    return pullImsDedicatedBearerListenerEvent(list);
                                                case 10141:
                                                    return pullImsDedicatedBearerEvent(list);
                                                case 10142:
                                                    return pullImsRegistrationServiceDescStats(list);
                                                case 10143:
                                                    return pullUceEventStats(list);
                                                case 10144:
                                                    return pullPresenceNotifyEvent(list);
                                                case 10145:
                                                    return pullGbaEvent(list);
                                                case 10146:
                                                    return pullPerSimStatus(list);
                                                default:
                                                    Rlog.e(TAG, String.format("unexpected atom ID %d", Integer.valueOf(i)));
                                                    return 1;
                                            }
                                    }
                            }
                        }
                        return pullOutgoingShortCodeSms(list);
                    }
                    return pullDeviceTelephonyProperties(list);
                }
                return pullTelephonyNetworkRequestsV2(list);
            }
            return pullImsRegistrationStats(list);
        }
        return pullImsRegistrationTermination(list);
    }

    public PersistAtomsStorage getAtomsStorage() {
        return this.mStorage;
    }

    public void flushAtomsStorage() {
        concludeAll();
        this.mStorage.flushAtoms();
    }

    public void clearAtomsStorage() {
        concludeAll();
        this.mStorage.clearAtoms();
    }

    public void registerOngoingDataCallStat(DataCallSessionStats dataCallSessionStats) {
        this.mOngoingDataCallStats.add(dataCallSessionStats);
    }

    public void unregisterOngoingDataCallStat(DataCallSessionStats dataCallSessionStats) {
        this.mOngoingDataCallStats.remove(dataCallSessionStats);
    }

    private void concludeDataCallSessionStats() {
        for (DataCallSessionStats dataCallSessionStats : this.mOngoingDataCallStats) {
            dataCallSessionStats.conclude();
        }
    }

    private void concludeImsStats() {
        for (Phone phone : getPhonesIfAny()) {
            ImsPhone imsPhone = (ImsPhone) phone.getImsPhone();
            if (imsPhone != null) {
                imsPhone.getImsStats().conclude();
            }
        }
    }

    private void concludeServiceStateStats() {
        for (Phone phone : getPhonesIfAny()) {
            phone.getServiceStateTracker().getServiceStateStats().conclude();
        }
    }

    private void concludeAll() {
        concludeDataCallSessionStats();
        concludeImsStats();
        concludeServiceStateStats();
        RcsStats rcsStats = RcsStats.getInstance();
        if (rcsStats != null) {
            rcsStats.concludeSipTransportFeatureTagsStat();
            rcsStats.onFlushIncompleteRcsAcsProvisioningStats();
            rcsStats.onFlushIncompleteImsRegistrationServiceDescStats();
            rcsStats.onFlushIncompleteImsRegistrationFeatureTagStats();
        }
    }

    private static int pullSimSlotState(List<StatsEvent> list) {
        try {
            SimSlotState currentState = SimSlotState.getCurrentState();
            list.add(TelephonyStatsLog.buildStatsEvent(10078, currentState.numActiveSlots, currentState.numActiveSims, currentState.numActiveEsims));
            return 0;
        } catch (RuntimeException unused) {
            return 1;
        }
    }

    private static int pullSupportedRadioAccessFamily(List<StatsEvent> list) {
        Phone[] phones;
        if (getPhonesIfAny().length == 0) {
            return 1;
        }
        long j = 0;
        for (int i = 0; i < PhoneFactory.getPhones().length; i++) {
            j |= phones[i].getRadioAccessFamily();
        }
        list.add(TelephonyStatsLog.buildStatsEvent(10079, j));
        return 0;
    }

    private static int pullCarrierIdTableVersion(List<StatsEvent> list) {
        Phone[] phonesIfAny = getPhonesIfAny();
        if (phonesIfAny.length == 0) {
            return 1;
        }
        list.add(TelephonyStatsLog.buildStatsEvent(10088, phonesIfAny[0].getCarrierIdListVersion()));
        return 0;
    }

    private int pullVoiceCallRatUsages(final List<StatsEvent> list) {
        PersistAtomsProto$VoiceCallRatUsage[] voiceCallRatUsages = this.mStorage.getVoiceCallRatUsages(MIN_COOLDOWN_MILLIS);
        if (voiceCallRatUsages != null) {
            Arrays.stream(voiceCallRatUsages).sorted(Comparator.comparingLong(new ToLongFunction() { // from class: com.android.internal.telephony.metrics.MetricsCollector$$ExternalSyntheticLambda23
                @Override // java.util.function.ToLongFunction
                public final long applyAsLong(Object obj) {
                    long lambda$pullVoiceCallRatUsages$0;
                    lambda$pullVoiceCallRatUsages$0 = MetricsCollector.lambda$pullVoiceCallRatUsages$0((PersistAtomsProto$VoiceCallRatUsage) obj);
                    return lambda$pullVoiceCallRatUsages$0;
                }
            })).filter(new Predicate() { // from class: com.android.internal.telephony.metrics.MetricsCollector$$ExternalSyntheticLambda24
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$pullVoiceCallRatUsages$1;
                    lambda$pullVoiceCallRatUsages$1 = MetricsCollector.lambda$pullVoiceCallRatUsages$1((PersistAtomsProto$VoiceCallRatUsage) obj);
                    return lambda$pullVoiceCallRatUsages$1;
                }
            }).forEach(new Consumer() { // from class: com.android.internal.telephony.metrics.MetricsCollector$$ExternalSyntheticLambda25
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    MetricsCollector.lambda$pullVoiceCallRatUsages$2(list, (PersistAtomsProto$VoiceCallRatUsage) obj);
                }
            });
            Rlog.d(TAG, String.format("%d out of %d VOICE_CALL_RAT_USAGE pulled", Integer.valueOf(list.size()), Integer.valueOf(voiceCallRatUsages.length)));
            return 0;
        }
        Rlog.w(TAG, "VOICE_CALL_RAT_USAGE pull too frequent, skipping");
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ long lambda$pullVoiceCallRatUsages$0(PersistAtomsProto$VoiceCallRatUsage persistAtomsProto$VoiceCallRatUsage) {
        return (persistAtomsProto$VoiceCallRatUsage.carrierId << 32) | persistAtomsProto$VoiceCallRatUsage.rat;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$pullVoiceCallRatUsages$1(PersistAtomsProto$VoiceCallRatUsage persistAtomsProto$VoiceCallRatUsage) {
        return persistAtomsProto$VoiceCallRatUsage.callCount >= 5;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$pullVoiceCallRatUsages$2(List list, PersistAtomsProto$VoiceCallRatUsage persistAtomsProto$VoiceCallRatUsage) {
        list.add(buildStatsEvent(persistAtomsProto$VoiceCallRatUsage));
    }

    private int pullVoiceCallSessions(final List<StatsEvent> list) {
        PersistAtomsProto$VoiceCallSession[] voiceCallSessions = this.mStorage.getVoiceCallSessions(MIN_COOLDOWN_MILLIS);
        if (voiceCallSessions != null) {
            Arrays.stream(voiceCallSessions).forEach(new Consumer() { // from class: com.android.internal.telephony.metrics.MetricsCollector$$ExternalSyntheticLambda19
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    MetricsCollector.lambda$pullVoiceCallSessions$3(list, (PersistAtomsProto$VoiceCallSession) obj);
                }
            });
            return 0;
        }
        Rlog.w(TAG, "VOICE_CALL_SESSION pull too frequent, skipping");
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$pullVoiceCallSessions$3(List list, PersistAtomsProto$VoiceCallSession persistAtomsProto$VoiceCallSession) {
        list.add(buildStatsEvent(persistAtomsProto$VoiceCallSession));
    }

    private int pullIncomingSms(final List<StatsEvent> list) {
        PersistAtomsProto$IncomingSms[] incomingSms = this.mStorage.getIncomingSms(MIN_COOLDOWN_MILLIS);
        if (incomingSms != null) {
            Arrays.stream(incomingSms).forEach(new Consumer() { // from class: com.android.internal.telephony.metrics.MetricsCollector$$ExternalSyntheticLambda22
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    MetricsCollector.lambda$pullIncomingSms$4(list, (PersistAtomsProto$IncomingSms) obj);
                }
            });
            return 0;
        }
        Rlog.w(TAG, "INCOMING_SMS pull too frequent, skipping");
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$pullIncomingSms$4(List list, PersistAtomsProto$IncomingSms persistAtomsProto$IncomingSms) {
        list.add(buildStatsEvent(persistAtomsProto$IncomingSms));
    }

    private int pullOutgoingSms(final List<StatsEvent> list) {
        PersistAtomsProto$OutgoingSms[] outgoingSms = this.mStorage.getOutgoingSms(MIN_COOLDOWN_MILLIS);
        if (outgoingSms != null) {
            Arrays.stream(outgoingSms).forEach(new Consumer() { // from class: com.android.internal.telephony.metrics.MetricsCollector$$ExternalSyntheticLambda20
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    MetricsCollector.lambda$pullOutgoingSms$5(list, (PersistAtomsProto$OutgoingSms) obj);
                }
            });
            return 0;
        }
        Rlog.w(TAG, "OUTGOING_SMS pull too frequent, skipping");
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$pullOutgoingSms$5(List list, PersistAtomsProto$OutgoingSms persistAtomsProto$OutgoingSms) {
        list.add(buildStatsEvent(persistAtomsProto$OutgoingSms));
    }

    private int pullDataCallSession(final List<StatsEvent> list) {
        concludeDataCallSessionStats();
        PersistAtomsProto$DataCallSession[] dataCallSessions = this.mStorage.getDataCallSessions(MIN_COOLDOWN_MILLIS);
        if (dataCallSessions != null) {
            Arrays.stream(dataCallSessions).forEach(new Consumer() { // from class: com.android.internal.telephony.metrics.MetricsCollector$$ExternalSyntheticLambda14
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    MetricsCollector.lambda$pullDataCallSession$6(list, (PersistAtomsProto$DataCallSession) obj);
                }
            });
            return 0;
        }
        Rlog.w(TAG, "DATA_CALL_SESSION pull too frequent, skipping");
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$pullDataCallSession$6(List list, PersistAtomsProto$DataCallSession persistAtomsProto$DataCallSession) {
        list.add(buildStatsEvent(persistAtomsProto$DataCallSession));
    }

    private int pullCellularDataServiceSwitch(final List<StatsEvent> list) {
        PersistAtomsProto$CellularDataServiceSwitch[] cellularDataServiceSwitches = this.mStorage.getCellularDataServiceSwitches(MIN_COOLDOWN_MILLIS);
        if (cellularDataServiceSwitches != null) {
            Arrays.stream(cellularDataServiceSwitches).forEach(new Consumer() { // from class: com.android.internal.telephony.metrics.MetricsCollector$$ExternalSyntheticLambda7
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    MetricsCollector.lambda$pullCellularDataServiceSwitch$7(list, (PersistAtomsProto$CellularDataServiceSwitch) obj);
                }
            });
            return 0;
        }
        Rlog.w(TAG, "CELLULAR_DATA_SERVICE_SWITCH pull too frequent, skipping");
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$pullCellularDataServiceSwitch$7(List list, PersistAtomsProto$CellularDataServiceSwitch persistAtomsProto$CellularDataServiceSwitch) {
        list.add(buildStatsEvent(persistAtomsProto$CellularDataServiceSwitch));
    }

    private int pullCellularServiceState(final List<StatsEvent> list) {
        concludeServiceStateStats();
        PersistAtomsProto$CellularServiceState[] cellularServiceStates = this.mStorage.getCellularServiceStates(MIN_COOLDOWN_MILLIS);
        if (cellularServiceStates != null) {
            Arrays.stream(cellularServiceStates).forEach(new Consumer() { // from class: com.android.internal.telephony.metrics.MetricsCollector$$ExternalSyntheticLambda4
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    MetricsCollector.lambda$pullCellularServiceState$8(list, (PersistAtomsProto$CellularServiceState) obj);
                }
            });
            return 0;
        }
        Rlog.w(TAG, "CELLULAR_SERVICE_STATE pull too frequent, skipping");
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$pullCellularServiceState$8(List list, PersistAtomsProto$CellularServiceState persistAtomsProto$CellularServiceState) {
        list.add(buildStatsEvent(persistAtomsProto$CellularServiceState));
    }

    private int pullImsRegistrationStats(final List<StatsEvent> list) {
        concludeImsStats();
        PersistAtomsProto$ImsRegistrationStats[] imsRegistrationStats = this.mStorage.getImsRegistrationStats(MIN_COOLDOWN_MILLIS);
        if (imsRegistrationStats != null) {
            Arrays.stream(imsRegistrationStats).forEach(new Consumer() { // from class: com.android.internal.telephony.metrics.MetricsCollector$$ExternalSyntheticLambda18
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    MetricsCollector.lambda$pullImsRegistrationStats$9(list, (PersistAtomsProto$ImsRegistrationStats) obj);
                }
            });
            return 0;
        }
        Rlog.w(TAG, "IMS_REGISTRATION_STATS pull too frequent, skipping");
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$pullImsRegistrationStats$9(List list, PersistAtomsProto$ImsRegistrationStats persistAtomsProto$ImsRegistrationStats) {
        list.add(buildStatsEvent(persistAtomsProto$ImsRegistrationStats));
    }

    private int pullImsRegistrationTermination(final List<StatsEvent> list) {
        PersistAtomsProto$ImsRegistrationTermination[] imsRegistrationTerminations = this.mStorage.getImsRegistrationTerminations(MIN_COOLDOWN_MILLIS);
        if (imsRegistrationTerminations != null) {
            Arrays.stream(imsRegistrationTerminations).forEach(new Consumer() { // from class: com.android.internal.telephony.metrics.MetricsCollector$$ExternalSyntheticLambda21
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    MetricsCollector.lambda$pullImsRegistrationTermination$10(list, (PersistAtomsProto$ImsRegistrationTermination) obj);
                }
            });
            return 0;
        }
        Rlog.w(TAG, "IMS_REGISTRATION_TERMINATION pull too frequent, skipping");
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$pullImsRegistrationTermination$10(List list, PersistAtomsProto$ImsRegistrationTermination persistAtomsProto$ImsRegistrationTermination) {
        list.add(buildStatsEvent(persistAtomsProto$ImsRegistrationTermination));
    }

    private int pullTelephonyNetworkRequestsV2(final List<StatsEvent> list) {
        PersistAtomsProto$NetworkRequestsV2[] networkRequestsV2 = this.mStorage.getNetworkRequestsV2(MIN_COOLDOWN_MILLIS);
        if (networkRequestsV2 != null) {
            Arrays.stream(networkRequestsV2).forEach(new Consumer() { // from class: com.android.internal.telephony.metrics.MetricsCollector$$ExternalSyntheticLambda8
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    MetricsCollector.lambda$pullTelephonyNetworkRequestsV2$11(list, (PersistAtomsProto$NetworkRequestsV2) obj);
                }
            });
            return 0;
        }
        Rlog.w(TAG, "TELEPHONY_NETWORK_REQUESTS_V2 pull too frequent, skipping");
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$pullTelephonyNetworkRequestsV2$11(List list, PersistAtomsProto$NetworkRequestsV2 persistAtomsProto$NetworkRequestsV2) {
        list.add(buildStatsEvent(persistAtomsProto$NetworkRequestsV2));
    }

    private int pullDeviceTelephonyProperties(List<StatsEvent> list) {
        Phone[] phonesIfAny = getPhonesIfAny();
        if (phonesIfAny.length == 0) {
            return 1;
        }
        list.add(TelephonyStatsLog.buildStatsEvent(10154, true, Arrays.stream(phonesIfAny).anyMatch(new Predicate() { // from class: com.android.internal.telephony.metrics.MetricsCollector$$ExternalSyntheticLambda15
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$pullDeviceTelephonyProperties$12;
                lambda$pullDeviceTelephonyProperties$12 = MetricsCollector.lambda$pullDeviceTelephonyProperties$12((Phone) obj);
                return lambda$pullDeviceTelephonyProperties$12;
            }
        }), this.mStorage.getAutoDataSwitchToggleCount(), Arrays.stream(phonesIfAny).anyMatch(new Predicate() { // from class: com.android.internal.telephony.metrics.MetricsCollector$$ExternalSyntheticLambda16
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ((Phone) obj).isManagedProfile();
            }
        })));
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$pullDeviceTelephonyProperties$12(Phone phone) {
        return phone.getSubId() != SubscriptionManager.getDefaultDataSubscriptionId() && phone.getDataSettingsManager().isMobileDataPolicyEnabled(3);
    }

    private int pullImsRegistrationFeatureTagStats(final List<StatsEvent> list) {
        RcsStats.getInstance().onFlushIncompleteImsRegistrationFeatureTagStats();
        PersistAtomsProto$ImsRegistrationFeatureTagStats[] imsRegistrationFeatureTagStats = this.mStorage.getImsRegistrationFeatureTagStats(MIN_COOLDOWN_MILLIS);
        if (imsRegistrationFeatureTagStats != null) {
            Arrays.stream(imsRegistrationFeatureTagStats).forEach(new Consumer() { // from class: com.android.internal.telephony.metrics.MetricsCollector$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    MetricsCollector.lambda$pullImsRegistrationFeatureTagStats$13(list, (PersistAtomsProto$ImsRegistrationFeatureTagStats) obj);
                }
            });
            return 0;
        }
        Rlog.w(TAG, "IMS_REGISTRATION_FEATURE_TAG_STATS pull too frequent, skipping");
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$pullImsRegistrationFeatureTagStats$13(List list, PersistAtomsProto$ImsRegistrationFeatureTagStats persistAtomsProto$ImsRegistrationFeatureTagStats) {
        list.add(buildStatsEvent(persistAtomsProto$ImsRegistrationFeatureTagStats));
    }

    private int pullRcsClientProvisioningStats(final List<StatsEvent> list) {
        PersistAtomsProto$RcsClientProvisioningStats[] rcsClientProvisioningStats = this.mStorage.getRcsClientProvisioningStats(MIN_COOLDOWN_MILLIS);
        if (rcsClientProvisioningStats != null) {
            Arrays.stream(rcsClientProvisioningStats).forEach(new Consumer() { // from class: com.android.internal.telephony.metrics.MetricsCollector$$ExternalSyntheticLambda6
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    MetricsCollector.lambda$pullRcsClientProvisioningStats$14(list, (PersistAtomsProto$RcsClientProvisioningStats) obj);
                }
            });
            return 0;
        }
        Rlog.w(TAG, "RCS_CLIENT_PROVISIONING_STATS pull too frequent, skipping");
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$pullRcsClientProvisioningStats$14(List list, PersistAtomsProto$RcsClientProvisioningStats persistAtomsProto$RcsClientProvisioningStats) {
        list.add(buildStatsEvent(persistAtomsProto$RcsClientProvisioningStats));
    }

    private int pullRcsAcsProvisioningStats(final List<StatsEvent> list) {
        RcsStats.getInstance().onFlushIncompleteRcsAcsProvisioningStats();
        PersistAtomsProto$RcsAcsProvisioningStats[] rcsAcsProvisioningStats = this.mStorage.getRcsAcsProvisioningStats(MIN_COOLDOWN_MILLIS);
        if (rcsAcsProvisioningStats != null) {
            Arrays.stream(rcsAcsProvisioningStats).forEach(new Consumer() { // from class: com.android.internal.telephony.metrics.MetricsCollector$$ExternalSyntheticLambda13
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    MetricsCollector.lambda$pullRcsAcsProvisioningStats$15(list, (PersistAtomsProto$RcsAcsProvisioningStats) obj);
                }
            });
            return 0;
        }
        Rlog.w(TAG, "RCS_ACS_PROVISIONING_STATS pull too frequent, skipping");
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$pullRcsAcsProvisioningStats$15(List list, PersistAtomsProto$RcsAcsProvisioningStats persistAtomsProto$RcsAcsProvisioningStats) {
        list.add(buildStatsEvent(persistAtomsProto$RcsAcsProvisioningStats));
    }

    private int pullSipDelegateStats(final List<StatsEvent> list) {
        PersistAtomsProto$SipDelegateStats[] sipDelegateStats = this.mStorage.getSipDelegateStats(MIN_COOLDOWN_MILLIS);
        if (sipDelegateStats != null) {
            Arrays.stream(sipDelegateStats).forEach(new Consumer() { // from class: com.android.internal.telephony.metrics.MetricsCollector$$ExternalSyntheticLambda5
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    MetricsCollector.lambda$pullSipDelegateStats$16(list, (PersistAtomsProto$SipDelegateStats) obj);
                }
            });
            return 0;
        }
        Rlog.w(TAG, "SIP_DELEGATE_STATS pull too frequent, skipping");
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$pullSipDelegateStats$16(List list, PersistAtomsProto$SipDelegateStats persistAtomsProto$SipDelegateStats) {
        list.add(buildStatsEvent(persistAtomsProto$SipDelegateStats));
    }

    private int pullSipTransportFeatureTagStats(final List<StatsEvent> list) {
        RcsStats.getInstance().concludeSipTransportFeatureTagsStat();
        PersistAtomsProto$SipTransportFeatureTagStats[] sipTransportFeatureTagStats = this.mStorage.getSipTransportFeatureTagStats(MIN_COOLDOWN_MILLIS);
        if (sipTransportFeatureTagStats != null) {
            Arrays.stream(sipTransportFeatureTagStats).forEach(new Consumer() { // from class: com.android.internal.telephony.metrics.MetricsCollector$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    MetricsCollector.lambda$pullSipTransportFeatureTagStats$17(list, (PersistAtomsProto$SipTransportFeatureTagStats) obj);
                }
            });
            return 0;
        }
        Rlog.w(TAG, "SIP_DELEGATE_STATS pull too frequent, skipping");
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$pullSipTransportFeatureTagStats$17(List list, PersistAtomsProto$SipTransportFeatureTagStats persistAtomsProto$SipTransportFeatureTagStats) {
        list.add(buildStatsEvent(persistAtomsProto$SipTransportFeatureTagStats));
    }

    private int pullSipMessageResponse(final List<StatsEvent> list) {
        PersistAtomsProto$SipMessageResponse[] sipMessageResponse = this.mStorage.getSipMessageResponse(MIN_COOLDOWN_MILLIS);
        if (sipMessageResponse != null) {
            Arrays.stream(sipMessageResponse).forEach(new Consumer() { // from class: com.android.internal.telephony.metrics.MetricsCollector$$ExternalSyntheticLambda10
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    MetricsCollector.lambda$pullSipMessageResponse$18(list, (PersistAtomsProto$SipMessageResponse) obj);
                }
            });
            return 0;
        }
        Rlog.w(TAG, "RCS_SIP_MESSAGE_RESPONSE pull too frequent, skipping");
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$pullSipMessageResponse$18(List list, PersistAtomsProto$SipMessageResponse persistAtomsProto$SipMessageResponse) {
        list.add(buildStatsEvent(persistAtomsProto$SipMessageResponse));
    }

    private int pullSipTransportSession(final List<StatsEvent> list) {
        PersistAtomsProto$SipTransportSession[] sipTransportSession = this.mStorage.getSipTransportSession(MIN_COOLDOWN_MILLIS);
        if (sipTransportSession != null) {
            Arrays.stream(sipTransportSession).forEach(new Consumer() { // from class: com.android.internal.telephony.metrics.MetricsCollector$$ExternalSyntheticLambda9
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    MetricsCollector.lambda$pullSipTransportSession$19(list, (PersistAtomsProto$SipTransportSession) obj);
                }
            });
            return 0;
        }
        Rlog.w(TAG, "RCS_SIP_TRANSPORT_SESSION pull too frequent, skipping");
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$pullSipTransportSession$19(List list, PersistAtomsProto$SipTransportSession persistAtomsProto$SipTransportSession) {
        list.add(buildStatsEvent(persistAtomsProto$SipTransportSession));
    }

    private int pullImsDedicatedBearerListenerEvent(final List<StatsEvent> list) {
        PersistAtomsProto$ImsDedicatedBearerListenerEvent[] imsDedicatedBearerListenerEvent = this.mStorage.getImsDedicatedBearerListenerEvent(MIN_COOLDOWN_MILLIS);
        if (imsDedicatedBearerListenerEvent != null) {
            Arrays.stream(imsDedicatedBearerListenerEvent).forEach(new Consumer() { // from class: com.android.internal.telephony.metrics.MetricsCollector$$ExternalSyntheticLambda11
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    MetricsCollector.lambda$pullImsDedicatedBearerListenerEvent$20(list, (PersistAtomsProto$ImsDedicatedBearerListenerEvent) obj);
                }
            });
            return 0;
        }
        Rlog.w(TAG, "IMS_DEDICATED_BEARER_LISTENER_EVENT pull too frequent, skipping");
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$pullImsDedicatedBearerListenerEvent$20(List list, PersistAtomsProto$ImsDedicatedBearerListenerEvent persistAtomsProto$ImsDedicatedBearerListenerEvent) {
        list.add(buildStatsEvent(persistAtomsProto$ImsDedicatedBearerListenerEvent));
    }

    private int pullImsDedicatedBearerEvent(final List<StatsEvent> list) {
        PersistAtomsProto$ImsDedicatedBearerEvent[] imsDedicatedBearerEvent = this.mStorage.getImsDedicatedBearerEvent(MIN_COOLDOWN_MILLIS);
        if (imsDedicatedBearerEvent != null) {
            Arrays.stream(imsDedicatedBearerEvent).forEach(new Consumer() { // from class: com.android.internal.telephony.metrics.MetricsCollector$$ExternalSyntheticLambda2
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    MetricsCollector.lambda$pullImsDedicatedBearerEvent$21(list, (PersistAtomsProto$ImsDedicatedBearerEvent) obj);
                }
            });
            return 0;
        }
        Rlog.w(TAG, "IMS_DEDICATED_BEARER_EVENT pull too frequent, skipping");
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$pullImsDedicatedBearerEvent$21(List list, PersistAtomsProto$ImsDedicatedBearerEvent persistAtomsProto$ImsDedicatedBearerEvent) {
        list.add(buildStatsEvent(persistAtomsProto$ImsDedicatedBearerEvent));
    }

    private int pullImsRegistrationServiceDescStats(final List<StatsEvent> list) {
        RcsStats.getInstance().onFlushIncompleteImsRegistrationServiceDescStats();
        PersistAtomsProto$ImsRegistrationServiceDescStats[] imsRegistrationServiceDescStats = this.mStorage.getImsRegistrationServiceDescStats(MIN_COOLDOWN_MILLIS);
        if (imsRegistrationServiceDescStats != null) {
            Arrays.stream(imsRegistrationServiceDescStats).forEach(new Consumer() { // from class: com.android.internal.telephony.metrics.MetricsCollector$$ExternalSyntheticLambda26
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    MetricsCollector.lambda$pullImsRegistrationServiceDescStats$22(list, (PersistAtomsProto$ImsRegistrationServiceDescStats) obj);
                }
            });
            return 0;
        }
        Rlog.w(TAG, "IMS_REGISTRATION_SERVICE_DESC_STATS pull too frequent, skipping");
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$pullImsRegistrationServiceDescStats$22(List list, PersistAtomsProto$ImsRegistrationServiceDescStats persistAtomsProto$ImsRegistrationServiceDescStats) {
        list.add(buildStatsEvent(persistAtomsProto$ImsRegistrationServiceDescStats));
    }

    private int pullUceEventStats(final List<StatsEvent> list) {
        PersistAtomsProto$UceEventStats[] uceEventStats = this.mStorage.getUceEventStats(MIN_COOLDOWN_MILLIS);
        if (uceEventStats != null) {
            Arrays.stream(uceEventStats).forEach(new Consumer() { // from class: com.android.internal.telephony.metrics.MetricsCollector$$ExternalSyntheticLambda17
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    MetricsCollector.lambda$pullUceEventStats$23(list, (PersistAtomsProto$UceEventStats) obj);
                }
            });
            return 0;
        }
        Rlog.w(TAG, "UCE_EVENT_STATS pull too frequent, skipping");
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$pullUceEventStats$23(List list, PersistAtomsProto$UceEventStats persistAtomsProto$UceEventStats) {
        list.add(buildStatsEvent(persistAtomsProto$UceEventStats));
    }

    private int pullPresenceNotifyEvent(final List<StatsEvent> list) {
        PersistAtomsProto$PresenceNotifyEvent[] presenceNotifyEvent = this.mStorage.getPresenceNotifyEvent(MIN_COOLDOWN_MILLIS);
        if (presenceNotifyEvent != null) {
            Arrays.stream(presenceNotifyEvent).forEach(new Consumer() { // from class: com.android.internal.telephony.metrics.MetricsCollector$$ExternalSyntheticLambda12
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    MetricsCollector.lambda$pullPresenceNotifyEvent$24(list, (PersistAtomsProto$PresenceNotifyEvent) obj);
                }
            });
            return 0;
        }
        Rlog.w(TAG, "PRESENCE_NOTIFY_EVENT pull too frequent, skipping");
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$pullPresenceNotifyEvent$24(List list, PersistAtomsProto$PresenceNotifyEvent persistAtomsProto$PresenceNotifyEvent) {
        list.add(buildStatsEvent(persistAtomsProto$PresenceNotifyEvent));
    }

    private int pullGbaEvent(final List<StatsEvent> list) {
        PersistAtomsProto$GbaEvent[] gbaEvent = this.mStorage.getGbaEvent(MIN_COOLDOWN_MILLIS);
        if (gbaEvent != null) {
            Arrays.stream(gbaEvent).forEach(new Consumer() { // from class: com.android.internal.telephony.metrics.MetricsCollector$$ExternalSyntheticLambda3
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    MetricsCollector.lambda$pullGbaEvent$25(list, (PersistAtomsProto$GbaEvent) obj);
                }
            });
            return 0;
        }
        Rlog.w(TAG, "GBA_EVENT pull too frequent, skipping");
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$pullGbaEvent$25(List list, PersistAtomsProto$GbaEvent persistAtomsProto$GbaEvent) {
        list.add(buildStatsEvent(persistAtomsProto$GbaEvent));
    }

    private int pullPerSimStatus(List<StatsEvent> list) {
        int i;
        Phone[] phonesIfAny = getPhonesIfAny();
        int length = phonesIfAny.length;
        int i2 = 1;
        int i3 = 0;
        while (i3 < length) {
            Phone phone = phonesIfAny[i3];
            PerSimStatus currentState = PerSimStatus.getCurrentState(phone);
            if (currentState == null) {
                i = i3;
            } else {
                i = i3;
                list.add(TelephonyStatsLog.buildStatsEvent(10146, phone.getPhoneId(), currentState.carrierId, currentState.phoneNumberSourceUicc, currentState.phoneNumberSourceCarrier, currentState.phoneNumberSourceIms, currentState.advancedCallingSettingEnabled, currentState.voWiFiSettingEnabled, currentState.voWiFiModeSetting, currentState.voWiFiRoamingModeSetting, currentState.vtSettingEnabled, currentState.dataRoamingEnabled, currentState.preferredNetworkType, currentState.disabled2g, currentState.pin1Enabled, currentState.minimumVoltageClass, currentState.userModifiedApnTypes, currentState.unmeteredNetworks));
                i2 = 0;
            }
            i3 = i + 1;
        }
        return i2;
    }

    private int pullOutgoingShortCodeSms(final List<StatsEvent> list) {
        PersistAtomsProto$OutgoingShortCodeSms[] outgoingShortCodeSms = this.mStorage.getOutgoingShortCodeSms(MIN_COOLDOWN_MILLIS);
        if (outgoingShortCodeSms != null) {
            Arrays.stream(outgoingShortCodeSms).forEach(new Consumer() { // from class: com.android.internal.telephony.metrics.MetricsCollector$$ExternalSyntheticLambda27
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    MetricsCollector.lambda$pullOutgoingShortCodeSms$26(list, (PersistAtomsProto$OutgoingShortCodeSms) obj);
                }
            });
            return 0;
        }
        Rlog.w(TAG, "OUTGOING_SHORT_CODE_SMS pull too frequent, skipping");
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$pullOutgoingShortCodeSms$26(List list, PersistAtomsProto$OutgoingShortCodeSms persistAtomsProto$OutgoingShortCodeSms) {
        list.add(buildStatsEvent(persistAtomsProto$OutgoingShortCodeSms));
    }

    private void registerAtom(int i) {
        this.mStatsManager.setPullAtomCallback(i, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this);
    }

    private static StatsEvent buildStatsEvent(PersistAtomsProto$CellularDataServiceSwitch persistAtomsProto$CellularDataServiceSwitch) {
        return TelephonyStatsLog.buildStatsEvent(10091, persistAtomsProto$CellularDataServiceSwitch.ratFrom, persistAtomsProto$CellularDataServiceSwitch.ratTo, persistAtomsProto$CellularDataServiceSwitch.simSlotIndex, persistAtomsProto$CellularDataServiceSwitch.isMultiSim, persistAtomsProto$CellularDataServiceSwitch.carrierId, persistAtomsProto$CellularDataServiceSwitch.switchCount);
    }

    private static StatsEvent buildStatsEvent(PersistAtomsProto$CellularServiceState persistAtomsProto$CellularServiceState) {
        return TelephonyStatsLog.buildStatsEvent(10090, persistAtomsProto$CellularServiceState.voiceRat, persistAtomsProto$CellularServiceState.dataRat, persistAtomsProto$CellularServiceState.voiceRoamingType, persistAtomsProto$CellularServiceState.dataRoamingType, persistAtomsProto$CellularServiceState.isEndc, persistAtomsProto$CellularServiceState.simSlotIndex, persistAtomsProto$CellularServiceState.isMultiSim, persistAtomsProto$CellularServiceState.carrierId, roundAndConvertMillisToSeconds(persistAtomsProto$CellularServiceState.totalTimeMillis), persistAtomsProto$CellularServiceState.isEmergencyOnly, persistAtomsProto$CellularServiceState.isInternetPdnUp);
    }

    private static StatsEvent buildStatsEvent(PersistAtomsProto$VoiceCallRatUsage persistAtomsProto$VoiceCallRatUsage) {
        return TelephonyStatsLog.buildStatsEvent(10077, persistAtomsProto$VoiceCallRatUsage.carrierId, persistAtomsProto$VoiceCallRatUsage.rat, roundAndConvertMillisToSeconds(persistAtomsProto$VoiceCallRatUsage.totalDurationMillis), persistAtomsProto$VoiceCallRatUsage.callCount);
    }

    private static StatsEvent buildStatsEvent(PersistAtomsProto$VoiceCallSession persistAtomsProto$VoiceCallSession) {
        return TelephonyStatsLog.buildStatsEvent(10076, persistAtomsProto$VoiceCallSession.bearerAtStart, persistAtomsProto$VoiceCallSession.bearerAtEnd, persistAtomsProto$VoiceCallSession.direction, 0, persistAtomsProto$VoiceCallSession.setupFailed, persistAtomsProto$VoiceCallSession.disconnectReasonCode, persistAtomsProto$VoiceCallSession.disconnectExtraCode, persistAtomsProto$VoiceCallSession.disconnectExtraMessage, persistAtomsProto$VoiceCallSession.ratAtStart, persistAtomsProto$VoiceCallSession.ratAtEnd, persistAtomsProto$VoiceCallSession.ratSwitchCount, persistAtomsProto$VoiceCallSession.codecBitmask, persistAtomsProto$VoiceCallSession.concurrentCallCountAtStart, persistAtomsProto$VoiceCallSession.concurrentCallCountAtEnd, persistAtomsProto$VoiceCallSession.simSlotIndex, persistAtomsProto$VoiceCallSession.isMultiSim, persistAtomsProto$VoiceCallSession.isEsim, persistAtomsProto$VoiceCallSession.carrierId, persistAtomsProto$VoiceCallSession.srvccCompleted, persistAtomsProto$VoiceCallSession.srvccFailureCount, persistAtomsProto$VoiceCallSession.srvccCancellationCount, persistAtomsProto$VoiceCallSession.rttEnabled, persistAtomsProto$VoiceCallSession.isEmergency, persistAtomsProto$VoiceCallSession.isRoaming, sRandom.nextInt(), persistAtomsProto$VoiceCallSession.signalStrengthAtEnd, persistAtomsProto$VoiceCallSession.bandAtEnd, persistAtomsProto$VoiceCallSession.setupDurationMillis, persistAtomsProto$VoiceCallSession.mainCodecQuality, persistAtomsProto$VoiceCallSession.videoEnabled, persistAtomsProto$VoiceCallSession.ratAtConnected, persistAtomsProto$VoiceCallSession.isMultiparty, persistAtomsProto$VoiceCallSession.callDuration, persistAtomsProto$VoiceCallSession.lastKnownRat);
    }

    private static StatsEvent buildStatsEvent(PersistAtomsProto$IncomingSms persistAtomsProto$IncomingSms) {
        return TelephonyStatsLog.buildStatsEvent(10086, persistAtomsProto$IncomingSms.smsFormat, persistAtomsProto$IncomingSms.smsTech, persistAtomsProto$IncomingSms.rat, persistAtomsProto$IncomingSms.smsType, persistAtomsProto$IncomingSms.totalParts, persistAtomsProto$IncomingSms.receivedParts, persistAtomsProto$IncomingSms.blocked, persistAtomsProto$IncomingSms.error, persistAtomsProto$IncomingSms.isRoaming, persistAtomsProto$IncomingSms.simSlotIndex, persistAtomsProto$IncomingSms.isMultiSim, persistAtomsProto$IncomingSms.isEsim, persistAtomsProto$IncomingSms.carrierId, persistAtomsProto$IncomingSms.messageId, persistAtomsProto$IncomingSms.count, persistAtomsProto$IncomingSms.isManagedProfile);
    }

    private static StatsEvent buildStatsEvent(PersistAtomsProto$OutgoingSms persistAtomsProto$OutgoingSms) {
        return TelephonyStatsLog.buildStatsEvent(10087, persistAtomsProto$OutgoingSms.smsFormat, persistAtomsProto$OutgoingSms.smsTech, persistAtomsProto$OutgoingSms.rat, persistAtomsProto$OutgoingSms.sendResult, persistAtomsProto$OutgoingSms.errorCode, persistAtomsProto$OutgoingSms.isRoaming, persistAtomsProto$OutgoingSms.isFromDefaultApp, persistAtomsProto$OutgoingSms.simSlotIndex, persistAtomsProto$OutgoingSms.isMultiSim, persistAtomsProto$OutgoingSms.isEsim, persistAtomsProto$OutgoingSms.carrierId, persistAtomsProto$OutgoingSms.messageId, persistAtomsProto$OutgoingSms.retryId, persistAtomsProto$OutgoingSms.intervalMillis, persistAtomsProto$OutgoingSms.count, persistAtomsProto$OutgoingSms.sendErrorCode, persistAtomsProto$OutgoingSms.networkErrorCode, persistAtomsProto$OutgoingSms.isManagedProfile);
    }

    private static StatsEvent buildStatsEvent(PersistAtomsProto$DataCallSession persistAtomsProto$DataCallSession) {
        return TelephonyStatsLog.buildStatsEvent(10089, persistAtomsProto$DataCallSession.dimension, persistAtomsProto$DataCallSession.isMultiSim, persistAtomsProto$DataCallSession.isEsim, 0, persistAtomsProto$DataCallSession.apnTypeBitmask, persistAtomsProto$DataCallSession.carrierId, persistAtomsProto$DataCallSession.isRoaming, persistAtomsProto$DataCallSession.ratAtEnd, persistAtomsProto$DataCallSession.oosAtEnd, persistAtomsProto$DataCallSession.ratSwitchCount, persistAtomsProto$DataCallSession.isOpportunistic, persistAtomsProto$DataCallSession.ipType, persistAtomsProto$DataCallSession.setupFailed, persistAtomsProto$DataCallSession.failureCause, persistAtomsProto$DataCallSession.suggestedRetryMillis, persistAtomsProto$DataCallSession.deactivateReason, roundAndConvertMillisToMinutes(persistAtomsProto$DataCallSession.durationMinutes * MILLIS_PER_MINUTE), persistAtomsProto$DataCallSession.ongoing, persistAtomsProto$DataCallSession.bandAtEnd, persistAtomsProto$DataCallSession.handoverFailureCauses, persistAtomsProto$DataCallSession.handoverFailureRat, persistAtomsProto$DataCallSession.isNonDds);
    }

    private static StatsEvent buildStatsEvent(PersistAtomsProto$ImsRegistrationStats persistAtomsProto$ImsRegistrationStats) {
        return TelephonyStatsLog.buildStatsEvent(10094, persistAtomsProto$ImsRegistrationStats.carrierId, persistAtomsProto$ImsRegistrationStats.simSlotIndex, persistAtomsProto$ImsRegistrationStats.rat, roundAndConvertMillisToSeconds(persistAtomsProto$ImsRegistrationStats.registeredMillis), roundAndConvertMillisToSeconds(persistAtomsProto$ImsRegistrationStats.voiceCapableMillis), roundAndConvertMillisToSeconds(persistAtomsProto$ImsRegistrationStats.voiceAvailableMillis), roundAndConvertMillisToSeconds(persistAtomsProto$ImsRegistrationStats.smsCapableMillis), roundAndConvertMillisToSeconds(persistAtomsProto$ImsRegistrationStats.smsAvailableMillis), roundAndConvertMillisToSeconds(persistAtomsProto$ImsRegistrationStats.videoCapableMillis), roundAndConvertMillisToSeconds(persistAtomsProto$ImsRegistrationStats.videoAvailableMillis), roundAndConvertMillisToSeconds(persistAtomsProto$ImsRegistrationStats.utCapableMillis), roundAndConvertMillisToSeconds(persistAtomsProto$ImsRegistrationStats.utAvailableMillis));
    }

    private static StatsEvent buildStatsEvent(PersistAtomsProto$ImsRegistrationTermination persistAtomsProto$ImsRegistrationTermination) {
        return TelephonyStatsLog.buildStatsEvent(10093, persistAtomsProto$ImsRegistrationTermination.carrierId, persistAtomsProto$ImsRegistrationTermination.isMultiSim, persistAtomsProto$ImsRegistrationTermination.ratAtEnd, persistAtomsProto$ImsRegistrationTermination.setupFailed, persistAtomsProto$ImsRegistrationTermination.reasonCode, persistAtomsProto$ImsRegistrationTermination.extraCode, persistAtomsProto$ImsRegistrationTermination.extraMessage, persistAtomsProto$ImsRegistrationTermination.count);
    }

    private static StatsEvent buildStatsEvent(PersistAtomsProto$NetworkRequestsV2 persistAtomsProto$NetworkRequestsV2) {
        return TelephonyStatsLog.buildStatsEvent(10153, persistAtomsProto$NetworkRequestsV2.carrierId, persistAtomsProto$NetworkRequestsV2.capability, persistAtomsProto$NetworkRequestsV2.requestCount);
    }

    private static StatsEvent buildStatsEvent(PersistAtomsProto$ImsRegistrationFeatureTagStats persistAtomsProto$ImsRegistrationFeatureTagStats) {
        return TelephonyStatsLog.buildStatsEvent(10133, persistAtomsProto$ImsRegistrationFeatureTagStats.carrierId, persistAtomsProto$ImsRegistrationFeatureTagStats.slotId, persistAtomsProto$ImsRegistrationFeatureTagStats.featureTagName, persistAtomsProto$ImsRegistrationFeatureTagStats.registrationTech, roundAndConvertMillisToSeconds(persistAtomsProto$ImsRegistrationFeatureTagStats.registeredMillis));
    }

    private static StatsEvent buildStatsEvent(PersistAtomsProto$RcsClientProvisioningStats persistAtomsProto$RcsClientProvisioningStats) {
        return TelephonyStatsLog.buildStatsEvent(10134, persistAtomsProto$RcsClientProvisioningStats.carrierId, persistAtomsProto$RcsClientProvisioningStats.slotId, persistAtomsProto$RcsClientProvisioningStats.event, persistAtomsProto$RcsClientProvisioningStats.count);
    }

    private static StatsEvent buildStatsEvent(PersistAtomsProto$RcsAcsProvisioningStats persistAtomsProto$RcsAcsProvisioningStats) {
        return TelephonyStatsLog.buildStatsEvent(10135, persistAtomsProto$RcsAcsProvisioningStats.carrierId, persistAtomsProto$RcsAcsProvisioningStats.slotId, persistAtomsProto$RcsAcsProvisioningStats.responseCode, persistAtomsProto$RcsAcsProvisioningStats.responseType, persistAtomsProto$RcsAcsProvisioningStats.isSingleRegistrationEnabled, persistAtomsProto$RcsAcsProvisioningStats.count, roundAndConvertMillisToSeconds(persistAtomsProto$RcsAcsProvisioningStats.stateTimerMillis));
    }

    private static StatsEvent buildStatsEvent(PersistAtomsProto$SipDelegateStats persistAtomsProto$SipDelegateStats) {
        return TelephonyStatsLog.buildStatsEvent(10136, persistAtomsProto$SipDelegateStats.dimension, persistAtomsProto$SipDelegateStats.carrierId, persistAtomsProto$SipDelegateStats.slotId, roundAndConvertMillisToSeconds(persistAtomsProto$SipDelegateStats.uptimeMillis), persistAtomsProto$SipDelegateStats.destroyReason);
    }

    private static StatsEvent buildStatsEvent(PersistAtomsProto$SipTransportFeatureTagStats persistAtomsProto$SipTransportFeatureTagStats) {
        return TelephonyStatsLog.buildStatsEvent(10137, persistAtomsProto$SipTransportFeatureTagStats.carrierId, persistAtomsProto$SipTransportFeatureTagStats.slotId, persistAtomsProto$SipTransportFeatureTagStats.featureTagName, persistAtomsProto$SipTransportFeatureTagStats.sipTransportDeniedReason, persistAtomsProto$SipTransportFeatureTagStats.sipTransportDeregisteredReason, roundAndConvertMillisToSeconds(persistAtomsProto$SipTransportFeatureTagStats.associatedMillis));
    }

    private static StatsEvent buildStatsEvent(PersistAtomsProto$SipMessageResponse persistAtomsProto$SipMessageResponse) {
        return TelephonyStatsLog.buildStatsEvent(10138, persistAtomsProto$SipMessageResponse.carrierId, persistAtomsProto$SipMessageResponse.slotId, persistAtomsProto$SipMessageResponse.sipMessageMethod, persistAtomsProto$SipMessageResponse.sipMessageResponse, persistAtomsProto$SipMessageResponse.sipMessageDirection, persistAtomsProto$SipMessageResponse.messageError, persistAtomsProto$SipMessageResponse.count);
    }

    private static StatsEvent buildStatsEvent(PersistAtomsProto$SipTransportSession persistAtomsProto$SipTransportSession) {
        return TelephonyStatsLog.buildStatsEvent(10139, persistAtomsProto$SipTransportSession.carrierId, persistAtomsProto$SipTransportSession.slotId, persistAtomsProto$SipTransportSession.sessionMethod, persistAtomsProto$SipTransportSession.sipMessageDirection, persistAtomsProto$SipTransportSession.sipResponse, persistAtomsProto$SipTransportSession.sessionCount, persistAtomsProto$SipTransportSession.endedGracefullyCount);
    }

    private static StatsEvent buildStatsEvent(PersistAtomsProto$ImsDedicatedBearerListenerEvent persistAtomsProto$ImsDedicatedBearerListenerEvent) {
        return TelephonyStatsLog.buildStatsEvent(10140, persistAtomsProto$ImsDedicatedBearerListenerEvent.carrierId, persistAtomsProto$ImsDedicatedBearerListenerEvent.slotId, persistAtomsProto$ImsDedicatedBearerListenerEvent.ratAtEnd, persistAtomsProto$ImsDedicatedBearerListenerEvent.qci, persistAtomsProto$ImsDedicatedBearerListenerEvent.dedicatedBearerEstablished, persistAtomsProto$ImsDedicatedBearerListenerEvent.eventCount);
    }

    private static StatsEvent buildStatsEvent(PersistAtomsProto$ImsDedicatedBearerEvent persistAtomsProto$ImsDedicatedBearerEvent) {
        return TelephonyStatsLog.buildStatsEvent(10141, persistAtomsProto$ImsDedicatedBearerEvent.carrierId, persistAtomsProto$ImsDedicatedBearerEvent.slotId, persistAtomsProto$ImsDedicatedBearerEvent.ratAtEnd, persistAtomsProto$ImsDedicatedBearerEvent.qci, persistAtomsProto$ImsDedicatedBearerEvent.bearerState, persistAtomsProto$ImsDedicatedBearerEvent.localConnectionInfoReceived, persistAtomsProto$ImsDedicatedBearerEvent.remoteConnectionInfoReceived, persistAtomsProto$ImsDedicatedBearerEvent.hasListeners, persistAtomsProto$ImsDedicatedBearerEvent.count);
    }

    private static StatsEvent buildStatsEvent(PersistAtomsProto$ImsRegistrationServiceDescStats persistAtomsProto$ImsRegistrationServiceDescStats) {
        return TelephonyStatsLog.buildStatsEvent(10142, persistAtomsProto$ImsRegistrationServiceDescStats.carrierId, persistAtomsProto$ImsRegistrationServiceDescStats.slotId, persistAtomsProto$ImsRegistrationServiceDescStats.serviceIdName, persistAtomsProto$ImsRegistrationServiceDescStats.serviceIdVersion, persistAtomsProto$ImsRegistrationServiceDescStats.registrationTech, roundAndConvertMillisToSeconds(persistAtomsProto$ImsRegistrationServiceDescStats.publishedMillis));
    }

    private static StatsEvent buildStatsEvent(PersistAtomsProto$UceEventStats persistAtomsProto$UceEventStats) {
        return TelephonyStatsLog.buildStatsEvent(10143, persistAtomsProto$UceEventStats.carrierId, persistAtomsProto$UceEventStats.slotId, persistAtomsProto$UceEventStats.type, persistAtomsProto$UceEventStats.successful, persistAtomsProto$UceEventStats.commandCode, persistAtomsProto$UceEventStats.networkResponse, persistAtomsProto$UceEventStats.count);
    }

    private static StatsEvent buildStatsEvent(PersistAtomsProto$PresenceNotifyEvent persistAtomsProto$PresenceNotifyEvent) {
        return TelephonyStatsLog.buildStatsEvent(10144, persistAtomsProto$PresenceNotifyEvent.carrierId, persistAtomsProto$PresenceNotifyEvent.slotId, persistAtomsProto$PresenceNotifyEvent.reason, persistAtomsProto$PresenceNotifyEvent.contentBodyReceived, persistAtomsProto$PresenceNotifyEvent.rcsCapsCount, persistAtomsProto$PresenceNotifyEvent.mmtelCapsCount, persistAtomsProto$PresenceNotifyEvent.noCapsCount, persistAtomsProto$PresenceNotifyEvent.count);
    }

    private static StatsEvent buildStatsEvent(PersistAtomsProto$GbaEvent persistAtomsProto$GbaEvent) {
        return TelephonyStatsLog.buildStatsEvent(10145, persistAtomsProto$GbaEvent.carrierId, persistAtomsProto$GbaEvent.slotId, persistAtomsProto$GbaEvent.successful, persistAtomsProto$GbaEvent.failedReason, persistAtomsProto$GbaEvent.count);
    }

    private static StatsEvent buildStatsEvent(PersistAtomsProto$OutgoingShortCodeSms persistAtomsProto$OutgoingShortCodeSms) {
        return TelephonyStatsLog.buildStatsEvent(10162, persistAtomsProto$OutgoingShortCodeSms.category, persistAtomsProto$OutgoingShortCodeSms.xmlVersion, persistAtomsProto$OutgoingShortCodeSms.shortCodeSmsCount);
    }

    private static Phone[] getPhonesIfAny() {
        try {
            return PhoneFactory.getPhones();
        } catch (IllegalStateException unused) {
            return new Phone[0];
        }
    }

    private static int roundAndConvertMillisToSeconds(long j) {
        long j2 = DURATION_BUCKET_MILLIS;
        return (int) ((Math.round(j / j2) * j2) / MILLIS_PER_SECOND);
    }

    private static int roundAndConvertMillisToMinutes(long j) {
        long j2 = DURATION_BUCKET_MILLIS;
        return (int) ((Math.round(j / j2) * j2) / MILLIS_PER_MINUTE);
    }
}
