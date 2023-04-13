package com.android.internal.telephony.metrics;

import android.os.SystemClock;
import android.telephony.ServiceState;
import android.telephony.ims.ImsReasonInfo;
import android.telephony.ims.feature.MmTelFeature;
import android.util.Patterns;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.imsphone.ImsPhone;
import com.android.internal.telephony.nano.PersistAtomsProto$ImsRegistrationStats;
import com.android.internal.telephony.nano.PersistAtomsProto$ImsRegistrationTermination;
import com.android.telephony.Rlog;
import java.util.regex.Pattern;
/* loaded from: classes.dex */
public class ImsStats {
    private static final String TAG = "ImsStats";
    private PersistAtomsProto$ImsRegistrationStats mLastRegistrationStats;
    private long mLastTimestamp;
    private final ImsPhone mPhone;
    private static final Pattern PATTERN_UUID = Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");
    private static final Pattern PATTERN_URI = Pattern.compile("([a-zA-Z]{2,}:)" + Patterns.EMAIL_ADDRESS.pattern());
    private static final Pattern PATTERN_IPV4 = Pattern.compile("((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9]))");
    private static final Pattern PATTERN_IPV6 = Pattern.compile("([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,6}(:[0-9a-fA-F]{1,4}){1,6}|([0-9a-fA-F]{1,4}:){1,7}:|:(:[0-9a-fA-F]{1,4}){1,7}");
    private static final Pattern PATTERN_IMEI = Pattern.compile("(^|[^0-9])(?:[0-9]{8}-[0-9]{6}-[0-9][0-9]?|[0-9]{2}-[0-9]{6}-[0-9]{6}-[0-9][0-9]?|[0-9]{16})($|[^0-9])");
    private static final Pattern PATTERN_UNSEGMENTED_IMEI_IMSI = Pattern.compile("(^|[^0-9])[0-9]{15}($|[^0-9])");
    private static final Pattern PATTERN_HOSTNAME = Pattern.compile("([0-9a-zA-Z][0-9a-zA-Z_\\-]{0,61}[0-9a-zA-Z]\\.){2,}[a-zA-Z]{2,}");
    private static final Pattern PATTERN_UNKNOWN_ID = Pattern.compile("(^|[^0-9a-fA-F])(([-\\.]?0)*[1-9a-fA-F]([-\\.]?[0-9a-fA-F]){5,}|0*[1-9a-fA-F]([0-9a-fA-F]){4,})");
    private int mLastRegistrationState = 0;
    int mLastTransportType = -1;
    private MmTelFeature.MmTelCapabilities mLastAvailableFeatures = new MmTelFeature.MmTelCapabilities();
    private final MmTelFeature.MmTelCapabilities mLastWwanCapableFeatures = new MmTelFeature.MmTelCapabilities();
    private final MmTelFeature.MmTelCapabilities mLastWlanCapableFeatures = new MmTelFeature.MmTelCapabilities();
    private final PersistAtomsStorage mStorage = PhoneFactory.getMetricsCollector().getAtomsStorage();

    public ImsStats(ImsPhone imsPhone) {
        this.mPhone = imsPhone;
    }

    public synchronized void conclude() {
        MmTelFeature.MmTelCapabilities mmTelCapabilities;
        long timeMillis = getTimeMillis();
        if (this.mLastRegistrationState == 2) {
            PersistAtomsProto$ImsRegistrationStats copyOf = copyOf(this.mLastRegistrationStats);
            long j = timeMillis - this.mLastTimestamp;
            if (j < 1000) {
                logw("conclude: discarding transient stats, duration=%d", Long.valueOf(j));
            } else {
                copyOf.registeredMillis = j;
                copyOf.voiceAvailableMillis = this.mLastAvailableFeatures.isCapable(1) ? j : 0L;
                copyOf.videoAvailableMillis = this.mLastAvailableFeatures.isCapable(2) ? j : 0L;
                copyOf.utAvailableMillis = this.mLastAvailableFeatures.isCapable(4) ? j : 0L;
                copyOf.smsAvailableMillis = this.mLastAvailableFeatures.isCapable(8) ? j : 0L;
                if (copyOf.rat == 18) {
                    mmTelCapabilities = this.mLastWlanCapableFeatures;
                } else {
                    mmTelCapabilities = this.mLastWwanCapableFeatures;
                }
                copyOf.voiceCapableMillis = mmTelCapabilities.isCapable(1) ? j : 0L;
                copyOf.videoCapableMillis = mmTelCapabilities.isCapable(2) ? j : 0L;
                copyOf.utCapableMillis = mmTelCapabilities.isCapable(4) ? j : 0L;
                if (!mmTelCapabilities.isCapable(8)) {
                    j = 0;
                }
                copyOf.smsCapableMillis = j;
                this.mStorage.addImsRegistrationStats(copyOf);
            }
        }
        this.mLastTimestamp = timeMillis;
    }

    public synchronized void onImsCapabilitiesChanged(int i, MmTelFeature.MmTelCapabilities mmTelCapabilities) {
        boolean z;
        conclude();
        int convertRegistrationTechToNetworkType = convertRegistrationTechToNetworkType(i);
        boolean z2 = true;
        this.mLastTransportType = convertRegistrationTechToNetworkType == 18 ? 2 : 1;
        PersistAtomsProto$ImsRegistrationStats persistAtomsProto$ImsRegistrationStats = this.mLastRegistrationStats;
        if (persistAtomsProto$ImsRegistrationStats == null || persistAtomsProto$ImsRegistrationStats.rat == convertRegistrationTechToNetworkType) {
            z = false;
        } else {
            persistAtomsProto$ImsRegistrationStats.rat = convertRegistrationTechToNetworkType;
            z = true;
        }
        boolean isCapable = mmTelCapabilities.isCapable(1);
        if (this.mLastAvailableFeatures.isCapable(1) == isCapable) {
            z2 = false;
        }
        this.mLastAvailableFeatures = mmTelCapabilities;
        if ((z && isCapable) || z2) {
            this.mPhone.getDefaultPhone().getServiceStateTracker().getServiceStateStats().onImsVoiceRegistrationChanged();
        }
    }

    public synchronized void onSetFeatureResponse(int i, int i2, int i3) {
        MmTelFeature.MmTelCapabilities lastCapableFeaturesForTech = getLastCapableFeaturesForTech(i2);
        if (lastCapableFeaturesForTech != null) {
            conclude();
            if (i3 == 1) {
                lastCapableFeaturesForTech.addCapabilities(i);
            } else {
                lastCapableFeaturesForTech.removeCapabilities(i);
            }
        }
    }

    public synchronized void onImsRegistering(int i) {
        conclude();
        this.mLastTransportType = i;
        PersistAtomsProto$ImsRegistrationStats defaultImsRegistrationStats = getDefaultImsRegistrationStats();
        this.mLastRegistrationStats = defaultImsRegistrationStats;
        defaultImsRegistrationStats.rat = convertTransportTypeToNetworkType(i);
        this.mLastRegistrationState = 1;
    }

    public synchronized void onImsRegistered(int i) {
        conclude();
        this.mLastTransportType = i;
        if (this.mLastRegistrationStats == null) {
            this.mLastRegistrationStats = getDefaultImsRegistrationStats();
        }
        this.mLastRegistrationStats.rat = convertTransportTypeToNetworkType(i);
        this.mLastRegistrationState = 2;
    }

    public synchronized void onImsUnregistered(ImsReasonInfo imsReasonInfo) {
        conclude();
        PersistAtomsProto$ImsRegistrationTermination persistAtomsProto$ImsRegistrationTermination = new PersistAtomsProto$ImsRegistrationTermination();
        PersistAtomsProto$ImsRegistrationStats persistAtomsProto$ImsRegistrationStats = this.mLastRegistrationStats;
        if (persistAtomsProto$ImsRegistrationStats != null) {
            persistAtomsProto$ImsRegistrationTermination.carrierId = persistAtomsProto$ImsRegistrationStats.carrierId;
            persistAtomsProto$ImsRegistrationTermination.ratAtEnd = getRatAtEnd(persistAtomsProto$ImsRegistrationStats.rat);
        } else {
            persistAtomsProto$ImsRegistrationTermination.carrierId = this.mPhone.getDefaultPhone().getCarrierId();
            persistAtomsProto$ImsRegistrationTermination.ratAtEnd = 0;
        }
        persistAtomsProto$ImsRegistrationTermination.isMultiSim = SimSlotState.isMultiSim();
        persistAtomsProto$ImsRegistrationTermination.setupFailed = this.mLastRegistrationState != 2;
        persistAtomsProto$ImsRegistrationTermination.reasonCode = imsReasonInfo.getCode();
        persistAtomsProto$ImsRegistrationTermination.extraCode = imsReasonInfo.getExtraCode();
        persistAtomsProto$ImsRegistrationTermination.extraMessage = filterExtraMessage(imsReasonInfo.getExtraMessage());
        persistAtomsProto$ImsRegistrationTermination.count = 1;
        this.mStorage.addImsRegistrationTermination(persistAtomsProto$ImsRegistrationTermination);
        this.mLastRegistrationState = 0;
        this.mLastRegistrationStats = null;
        this.mLastAvailableFeatures = new MmTelFeature.MmTelCapabilities();
    }

    public synchronized void onServiceStateChanged(ServiceState serviceState) {
        PersistAtomsProto$ImsRegistrationStats persistAtomsProto$ImsRegistrationStats;
        if (this.mLastTransportType == 1 && (persistAtomsProto$ImsRegistrationStats = this.mLastRegistrationStats) != null) {
            persistAtomsProto$ImsRegistrationStats.rat = ServiceStateStats.getRat(serviceState, 2);
        }
    }

    public synchronized int getImsVoiceRadioTech() {
        if (this.mLastRegistrationStats != null && this.mLastAvailableFeatures.isCapable(1)) {
            return this.mLastRegistrationStats.rat;
        }
        return 0;
    }

    private int getRatAtEnd(int i) {
        return i == 18 ? i : getWwanPsRat();
    }

    private int convertTransportTypeToNetworkType(int i) {
        if (i != 1) {
            return i != 2 ? 0 : 18;
        }
        return getWwanPsRat();
    }

    private int getWwanPsRat() {
        return ServiceStateStats.getRat(this.mPhone.getServiceStateTracker().getServiceState(), 2);
    }

    private PersistAtomsProto$ImsRegistrationStats getDefaultImsRegistrationStats() {
        Phone defaultPhone = this.mPhone.getDefaultPhone();
        PersistAtomsProto$ImsRegistrationStats persistAtomsProto$ImsRegistrationStats = new PersistAtomsProto$ImsRegistrationStats();
        persistAtomsProto$ImsRegistrationStats.carrierId = defaultPhone.getCarrierId();
        persistAtomsProto$ImsRegistrationStats.simSlotIndex = defaultPhone.getPhoneId();
        return persistAtomsProto$ImsRegistrationStats;
    }

    private MmTelFeature.MmTelCapabilities getLastCapableFeaturesForTech(int i) {
        if (i != -1) {
            if (i == 1) {
                return this.mLastWlanCapableFeatures;
            }
            return this.mLastWwanCapableFeatures;
        }
        return null;
    }

    private int convertRegistrationTechToNetworkType(int i) {
        if (i != -1) {
            if (i != 0) {
                if (i != 1) {
                    if (i != 3) {
                        loge("convertRegistrationTechToNetworkType: unknown radio tech %d", Integer.valueOf(i));
                        return getWwanPsRat();
                    }
                    return 20;
                }
                return 18;
            }
            return 13;
        }
        return 0;
    }

    private static PersistAtomsProto$ImsRegistrationStats copyOf(PersistAtomsProto$ImsRegistrationStats persistAtomsProto$ImsRegistrationStats) {
        PersistAtomsProto$ImsRegistrationStats persistAtomsProto$ImsRegistrationStats2 = new PersistAtomsProto$ImsRegistrationStats();
        persistAtomsProto$ImsRegistrationStats2.carrierId = persistAtomsProto$ImsRegistrationStats.carrierId;
        persistAtomsProto$ImsRegistrationStats2.simSlotIndex = persistAtomsProto$ImsRegistrationStats.simSlotIndex;
        persistAtomsProto$ImsRegistrationStats2.rat = persistAtomsProto$ImsRegistrationStats.rat;
        persistAtomsProto$ImsRegistrationStats2.registeredMillis = persistAtomsProto$ImsRegistrationStats.registeredMillis;
        persistAtomsProto$ImsRegistrationStats2.voiceCapableMillis = persistAtomsProto$ImsRegistrationStats.voiceCapableMillis;
        persistAtomsProto$ImsRegistrationStats2.voiceAvailableMillis = persistAtomsProto$ImsRegistrationStats.voiceAvailableMillis;
        persistAtomsProto$ImsRegistrationStats2.smsCapableMillis = persistAtomsProto$ImsRegistrationStats.smsCapableMillis;
        persistAtomsProto$ImsRegistrationStats2.smsAvailableMillis = persistAtomsProto$ImsRegistrationStats.smsAvailableMillis;
        persistAtomsProto$ImsRegistrationStats2.videoCapableMillis = persistAtomsProto$ImsRegistrationStats.videoCapableMillis;
        persistAtomsProto$ImsRegistrationStats2.videoAvailableMillis = persistAtomsProto$ImsRegistrationStats.videoAvailableMillis;
        persistAtomsProto$ImsRegistrationStats2.utCapableMillis = persistAtomsProto$ImsRegistrationStats.utCapableMillis;
        persistAtomsProto$ImsRegistrationStats2.utAvailableMillis = persistAtomsProto$ImsRegistrationStats.utAvailableMillis;
        return persistAtomsProto$ImsRegistrationStats2;
    }

    @VisibleForTesting
    protected long getTimeMillis() {
        return SystemClock.elapsedRealtime();
    }

    public static String filterExtraMessage(String str) {
        if (str == null) {
            return PhoneConfigurationManager.SSSS;
        }
        String replaceAll = PATTERN_UNKNOWN_ID.matcher(PATTERN_UNSEGMENTED_IMEI_IMSI.matcher(PATTERN_IMEI.matcher(PATTERN_IPV6.matcher(PATTERN_IPV4.matcher(PATTERN_HOSTNAME.matcher(PATTERN_URI.matcher(PATTERN_UUID.matcher(str).replaceAll("<UUID_REDACTED>")).replaceAll("$1<REDACTED>")).replaceAll("<HOSTNAME_REDACTED>")).replaceAll("<IPV4_REDACTED>")).replaceAll("<IPV6_REDACTED>")).replaceAll("$1<IMEI_REDACTED>$2")).replaceAll("$1<IMEI_IMSI_REDACTED>$2")).replaceAll("$1<ID_REDACTED>");
        return replaceAll.length() > 128 ? replaceAll.substring(0, 128) : replaceAll;
    }

    private void logw(String str, Object... objArr) {
        String str2 = TAG;
        Rlog.w(str2, "[" + this.mPhone.getPhoneId() + "] " + String.format(str, objArr));
    }

    private void loge(String str, Object... objArr) {
        String str2 = TAG;
        Rlog.e(str2, "[" + this.mPhone.getPhoneId() + "] " + String.format(str, objArr));
    }
}
