package com.android.internal.telephony.metrics;

import android.os.SystemClock;
import android.telephony.AccessNetworkUtils;
import android.telephony.NetworkRegistrationInfo;
import android.telephony.ServiceState;
import android.telephony.data.DataProfile;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.NetworkTypeController$$ExternalSyntheticLambda1;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.ServiceStateTracker;
import com.android.internal.telephony.data.DataNetworkController;
import com.android.internal.telephony.imsphone.ImsPhone;
import com.android.internal.telephony.metrics.ServiceStateStats;
import com.android.internal.telephony.nano.PersistAtomsProto$CellularDataServiceSwitch;
import com.android.internal.telephony.nano.PersistAtomsProto$CellularServiceState;
import com.android.telephony.Rlog;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;
/* loaded from: classes.dex */
public class ServiceStateStats extends DataNetworkController.DataNetworkControllerCallback {
    private static final String TAG = "ServiceStateStats";
    private final AtomicReference<TimestampedServiceState> mLastState;
    private final Phone mPhone;
    private final PersistAtomsStorage mStorage;

    public ServiceStateStats(Phone phone) {
        super(new NetworkTypeController$$ExternalSyntheticLambda1());
        this.mLastState = new AtomicReference<>(new TimestampedServiceState(null, 0L));
        this.mPhone = phone;
        this.mStorage = PhoneFactory.getMetricsCollector().getAtomsStorage();
    }

    public void conclude() {
        final long timeMillis = getTimeMillis();
        addServiceState(this.mLastState.getAndUpdate(new UnaryOperator() { // from class: com.android.internal.telephony.metrics.ServiceStateStats$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                ServiceStateStats.TimestampedServiceState lambda$conclude$0;
                lambda$conclude$0 = ServiceStateStats.lambda$conclude$0(timeMillis, (ServiceStateStats.TimestampedServiceState) obj);
                return lambda$conclude$0;
            }
        }), timeMillis);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ TimestampedServiceState lambda$conclude$0(long j, TimestampedServiceState timestampedServiceState) {
        return new TimestampedServiceState(timestampedServiceState.mServiceState, j);
    }

    public void onImsVoiceRegistrationChanged() {
        final long timeMillis = getTimeMillis();
        addServiceState(this.mLastState.getAndUpdate(new UnaryOperator() { // from class: com.android.internal.telephony.metrics.ServiceStateStats$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                ServiceStateStats.TimestampedServiceState lambda$onImsVoiceRegistrationChanged$1;
                lambda$onImsVoiceRegistrationChanged$1 = ServiceStateStats.this.lambda$onImsVoiceRegistrationChanged$1(timeMillis, (ServiceStateStats.TimestampedServiceState) obj);
                return lambda$onImsVoiceRegistrationChanged$1;
            }
        }), timeMillis);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ TimestampedServiceState lambda$onImsVoiceRegistrationChanged$1(long j, TimestampedServiceState timestampedServiceState) {
        if (timestampedServiceState.mServiceState == null) {
            return new TimestampedServiceState(null, j);
        }
        PersistAtomsProto$CellularServiceState copyOf = copyOf(timestampedServiceState.mServiceState);
        Phone phone = this.mPhone;
        copyOf.voiceRat = getVoiceRat(phone, getServiceStateForPhone(phone));
        return new TimestampedServiceState(copyOf, j);
    }

    public void registerDataNetworkControllerCallback() {
        this.mPhone.getDataNetworkController().registerDataNetworkControllerCallback(this);
    }

    @Override // com.android.internal.telephony.data.DataNetworkController.DataNetworkControllerCallback
    public void onInternetDataNetworkConnected(List<DataProfile> list) {
        onInternetDataNetworkChanged(true);
    }

    @Override // com.android.internal.telephony.data.DataNetworkController.DataNetworkControllerCallback
    public void onInternetDataNetworkDisconnected() {
        onInternetDataNetworkChanged(false);
    }

    public void onServiceStateChanged(ServiceState serviceState) {
        long timeMillis = getTimeMillis();
        if (isModemOff(serviceState)) {
            addServiceState(this.mLastState.getAndSet(new TimestampedServiceState(null, timeMillis)), timeMillis);
            return;
        }
        PersistAtomsProto$CellularServiceState persistAtomsProto$CellularServiceState = new PersistAtomsProto$CellularServiceState();
        persistAtomsProto$CellularServiceState.voiceRat = getVoiceRat(this.mPhone, serviceState);
        persistAtomsProto$CellularServiceState.dataRat = getRat(serviceState, 2);
        persistAtomsProto$CellularServiceState.voiceRoamingType = serviceState.getVoiceRoamingType();
        persistAtomsProto$CellularServiceState.dataRoamingType = serviceState.getDataRoamingType();
        persistAtomsProto$CellularServiceState.isEndc = isEndc(serviceState);
        persistAtomsProto$CellularServiceState.simSlotIndex = this.mPhone.getPhoneId();
        persistAtomsProto$CellularServiceState.isMultiSim = SimSlotState.isMultiSim();
        persistAtomsProto$CellularServiceState.carrierId = this.mPhone.getCarrierId();
        persistAtomsProto$CellularServiceState.isEmergencyOnly = isEmergencyOnly(serviceState);
        persistAtomsProto$CellularServiceState.isInternetPdnUp = isInternetPdnUp(this.mPhone);
        TimestampedServiceState andSet = this.mLastState.getAndSet(new TimestampedServiceState(persistAtomsProto$CellularServiceState, timeMillis));
        addServiceStateAndSwitch(andSet, timeMillis, getDataServiceSwitch(andSet.mServiceState, persistAtomsProto$CellularServiceState));
    }

    private void addServiceState(TimestampedServiceState timestampedServiceState, long j) {
        addServiceStateAndSwitch(timestampedServiceState, j, null);
    }

    private void addServiceStateAndSwitch(TimestampedServiceState timestampedServiceState, long j, PersistAtomsProto$CellularDataServiceSwitch persistAtomsProto$CellularDataServiceSwitch) {
        if (timestampedServiceState.mServiceState == null) {
            return;
        }
        if (j >= timestampedServiceState.mTimestamp) {
            PersistAtomsProto$CellularServiceState copyOf = copyOf(timestampedServiceState.mServiceState);
            copyOf.totalTimeMillis = j - timestampedServiceState.mTimestamp;
            this.mStorage.addCellularServiceStateAndCellularDataServiceSwitch(copyOf, persistAtomsProto$CellularDataServiceSwitch);
            return;
        }
        Rlog.e(TAG, "addServiceState: durationMillis<0");
    }

    private PersistAtomsProto$CellularDataServiceSwitch getDataServiceSwitch(PersistAtomsProto$CellularServiceState persistAtomsProto$CellularServiceState, PersistAtomsProto$CellularServiceState persistAtomsProto$CellularServiceState2) {
        if (persistAtomsProto$CellularServiceState == null || persistAtomsProto$CellularServiceState.isMultiSim != persistAtomsProto$CellularServiceState2.isMultiSim || persistAtomsProto$CellularServiceState.carrierId != persistAtomsProto$CellularServiceState2.carrierId || persistAtomsProto$CellularServiceState.dataRat == persistAtomsProto$CellularServiceState2.dataRat) {
            return null;
        }
        PersistAtomsProto$CellularDataServiceSwitch persistAtomsProto$CellularDataServiceSwitch = new PersistAtomsProto$CellularDataServiceSwitch();
        persistAtomsProto$CellularDataServiceSwitch.ratFrom = persistAtomsProto$CellularServiceState.dataRat;
        persistAtomsProto$CellularDataServiceSwitch.ratTo = persistAtomsProto$CellularServiceState2.dataRat;
        persistAtomsProto$CellularDataServiceSwitch.isMultiSim = persistAtomsProto$CellularServiceState2.isMultiSim;
        persistAtomsProto$CellularDataServiceSwitch.simSlotIndex = persistAtomsProto$CellularServiceState2.simSlotIndex;
        persistAtomsProto$CellularDataServiceSwitch.carrierId = persistAtomsProto$CellularServiceState2.carrierId;
        persistAtomsProto$CellularDataServiceSwitch.switchCount = 1;
        return persistAtomsProto$CellularDataServiceSwitch;
    }

    private static ServiceState getServiceStateForPhone(Phone phone) {
        ServiceStateTracker serviceStateTracker = phone.getServiceStateTracker();
        if (serviceStateTracker != null) {
            return serviceStateTracker.getServiceState();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getBand(Phone phone) {
        return getBand(getServiceStateForPhone(phone));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Removed duplicated region for block: B:32:0x0067  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x0086 A[RETURN] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static int getBand(ServiceState serviceState) {
        int operatingBandForArfcn;
        if (serviceState == null) {
            Rlog.w(TAG, "getBand: serviceState=null");
            return 0;
        }
        int channelNumber = serviceState.getChannelNumber();
        int rat = getRat(serviceState, 2);
        if (rat == 0) {
            rat = serviceState.getVoiceNetworkType();
        }
        if (rat != 1 && rat != 2) {
            if (rat != 3) {
                if (rat != 13) {
                    if (rat != 15) {
                        if (rat != 16) {
                            if (rat != 19) {
                                if (rat == 20) {
                                    operatingBandForArfcn = AccessNetworkUtils.getOperatingBandForNrarfcn(channelNumber);
                                } else {
                                    switch (rat) {
                                        case 8:
                                        case 9:
                                        case 10:
                                            break;
                                        default:
                                            String str = TAG;
                                            Rlog.w(str, "getBand: unknown WWAN RAT " + rat);
                                            operatingBandForArfcn = 0;
                                            break;
                                    }
                                }
                                if (operatingBandForArfcn != -1) {
                                    String str2 = TAG;
                                    Rlog.w(str2, "getBand: band invalid for rat=" + rat + " ch=" + channelNumber);
                                    return 0;
                                }
                                return operatingBandForArfcn;
                            }
                        }
                    }
                }
                operatingBandForArfcn = AccessNetworkUtils.getOperatingBandForEarfcn(channelNumber);
                if (operatingBandForArfcn != -1) {
                }
            }
            operatingBandForArfcn = AccessNetworkUtils.getOperatingBandForUarfcn(channelNumber);
            if (operatingBandForArfcn != -1) {
            }
        }
        operatingBandForArfcn = AccessNetworkUtils.getOperatingBandForArfcn(channelNumber);
        if (operatingBandForArfcn != -1) {
        }
    }

    private static PersistAtomsProto$CellularServiceState copyOf(PersistAtomsProto$CellularServiceState persistAtomsProto$CellularServiceState) {
        PersistAtomsProto$CellularServiceState persistAtomsProto$CellularServiceState2 = new PersistAtomsProto$CellularServiceState();
        persistAtomsProto$CellularServiceState2.voiceRat = persistAtomsProto$CellularServiceState.voiceRat;
        persistAtomsProto$CellularServiceState2.dataRat = persistAtomsProto$CellularServiceState.dataRat;
        persistAtomsProto$CellularServiceState2.voiceRoamingType = persistAtomsProto$CellularServiceState.voiceRoamingType;
        persistAtomsProto$CellularServiceState2.dataRoamingType = persistAtomsProto$CellularServiceState.dataRoamingType;
        persistAtomsProto$CellularServiceState2.isEndc = persistAtomsProto$CellularServiceState.isEndc;
        persistAtomsProto$CellularServiceState2.simSlotIndex = persistAtomsProto$CellularServiceState.simSlotIndex;
        persistAtomsProto$CellularServiceState2.isMultiSim = persistAtomsProto$CellularServiceState.isMultiSim;
        persistAtomsProto$CellularServiceState2.carrierId = persistAtomsProto$CellularServiceState.carrierId;
        persistAtomsProto$CellularServiceState2.totalTimeMillis = persistAtomsProto$CellularServiceState.totalTimeMillis;
        persistAtomsProto$CellularServiceState2.isEmergencyOnly = persistAtomsProto$CellularServiceState.isEmergencyOnly;
        persistAtomsProto$CellularServiceState2.isInternetPdnUp = persistAtomsProto$CellularServiceState.isInternetPdnUp;
        return persistAtomsProto$CellularServiceState2;
    }

    private static boolean isModemOff(ServiceState serviceState) {
        return serviceState.getVoiceRegState() == 3;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getVoiceRat(Phone phone, ServiceState serviceState) {
        return getVoiceRat(phone, serviceState, 0);
    }

    @VisibleForTesting
    public static int getVoiceRat(Phone phone, ServiceState serviceState, int i) {
        int imsVoiceRadioTech;
        if (serviceState == null) {
            return 0;
        }
        ImsPhone imsPhone = (ImsPhone) phone.getImsPhone();
        if (i != 1 && imsPhone != null && (imsVoiceRadioTech = imsPhone.getImsStats().getImsVoiceRadioTech()) != 0) {
            if (imsVoiceRadioTech == 18 || getRat(serviceState, 2) != 0) {
                return imsVoiceRadioTech;
            }
        }
        if (i == 2) {
            return 0;
        }
        return getRat(serviceState, 1);
    }

    public static int getRat(ServiceState serviceState, int i) {
        NetworkRegistrationInfo networkRegistrationInfo = serviceState.getNetworkRegistrationInfo(i, 1);
        if (networkRegistrationInfo == null || !networkRegistrationInfo.isInService()) {
            return 0;
        }
        return networkRegistrationInfo.getAccessNetworkTechnology();
    }

    private static boolean isEmergencyOnly(ServiceState serviceState) {
        NetworkRegistrationInfo networkRegistrationInfo = serviceState.getNetworkRegistrationInfo(1, 1);
        return (networkRegistrationInfo == null || networkRegistrationInfo.isInService() || !networkRegistrationInfo.isEmergencyEnabled()) ? false : true;
    }

    private static boolean isEndc(ServiceState serviceState) {
        if (getRat(serviceState, 2) != 13) {
            return false;
        }
        int nrState = serviceState.getNrState();
        return nrState == 3 || nrState == 2;
    }

    private static boolean isInternetPdnUp(Phone phone) {
        DataNetworkController dataNetworkController = phone.getDataNetworkController();
        return dataNetworkController != null && dataNetworkController.getInternetDataNetworkState() == 2;
    }

    private void onInternetDataNetworkChanged(final boolean z) {
        final long timeMillis = getTimeMillis();
        addServiceState(this.mLastState.getAndUpdate(new UnaryOperator() { // from class: com.android.internal.telephony.metrics.ServiceStateStats$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                ServiceStateStats.TimestampedServiceState lambda$onInternetDataNetworkChanged$2;
                lambda$onInternetDataNetworkChanged$2 = ServiceStateStats.lambda$onInternetDataNetworkChanged$2(timeMillis, z, (ServiceStateStats.TimestampedServiceState) obj);
                return lambda$onInternetDataNetworkChanged$2;
            }
        }), timeMillis);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ TimestampedServiceState lambda$onInternetDataNetworkChanged$2(long j, boolean z, TimestampedServiceState timestampedServiceState) {
        if (timestampedServiceState.mServiceState == null) {
            return new TimestampedServiceState(null, j);
        }
        PersistAtomsProto$CellularServiceState copyOf = copyOf(timestampedServiceState.mServiceState);
        copyOf.isInternetPdnUp = z;
        return new TimestampedServiceState(copyOf, j);
    }

    @VisibleForTesting
    protected long getTimeMillis() {
        return SystemClock.elapsedRealtime();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class TimestampedServiceState {
        private final PersistAtomsProto$CellularServiceState mServiceState;
        private final long mTimestamp;

        TimestampedServiceState(PersistAtomsProto$CellularServiceState persistAtomsProto$CellularServiceState, long j) {
            this.mServiceState = persistAtomsProto$CellularServiceState;
            this.mTimestamp = j;
        }
    }
}
