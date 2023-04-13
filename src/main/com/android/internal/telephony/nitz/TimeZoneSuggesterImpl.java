package com.android.internal.telephony.nitz;

import android.app.timezonedetector.TelephonyTimeZoneSuggestion;
import android.text.TextUtils;
import android.timezone.CountryTimeZones;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.NitzData;
import com.android.internal.telephony.NitzSignal;
import com.android.internal.telephony.NitzStateMachine;
import com.android.internal.telephony.nitz.NitzStateMachineImpl;
import com.android.internal.telephony.nitz.TimeZoneLookupHelper;
import com.android.telephony.Rlog;
import java.util.Objects;
@VisibleForTesting
/* loaded from: classes.dex */
public class TimeZoneSuggesterImpl implements NitzStateMachineImpl.TimeZoneSuggester {
    private final NitzStateMachine.DeviceState mDeviceState;
    private final TimeZoneLookupHelper mTimeZoneLookupHelper;

    @VisibleForTesting
    public TimeZoneSuggesterImpl(NitzStateMachine.DeviceState deviceState, TimeZoneLookupHelper timeZoneLookupHelper) {
        Objects.requireNonNull(deviceState);
        this.mDeviceState = deviceState;
        Objects.requireNonNull(timeZoneLookupHelper);
        this.mTimeZoneLookupHelper = timeZoneLookupHelper;
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x0046  */
    /* JADX WARN: Removed duplicated region for block: B:11:0x0047  */
    @Override // com.android.internal.telephony.nitz.NitzStateMachineImpl.TimeZoneSuggester
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public TelephonyTimeZoneSuggestion getTimeZoneSuggestion(int i, String str, NitzSignal nitzSignal) {
        TelephonyTimeZoneSuggestion build;
        if (nitzSignal != null) {
            try {
                NitzData nitzData = nitzSignal.getNitzData();
                if (nitzData.getEmulatorHostTimeZone() != null) {
                    build = new TelephonyTimeZoneSuggestion.Builder(i).setZoneId(nitzData.getEmulatorHostTimeZone().getID()).setMatchType(4).setQuality(1).addDebugInfo("Emulator time zone override: " + nitzData).build();
                    if (build == null) {
                        if (str == null) {
                            if (nitzSignal == null) {
                                build = TelephonyTimeZoneSuggestion.createEmptySuggestion(i, "getTimeZoneSuggestion: nitzSignal=null, countryIsoCode=null");
                            } else {
                                build = TelephonyTimeZoneSuggestion.createEmptySuggestion(i, "getTimeZoneSuggestion: nitzSignal=" + nitzSignal + ", countryIsoCode=null");
                            }
                        } else if (nitzSignal == null) {
                            if (str.isEmpty()) {
                                build = TelephonyTimeZoneSuggestion.createEmptySuggestion(i, "getTimeZoneSuggestion: nitzSignal=null, countryIsoCode=\"\"");
                            } else {
                                build = findTimeZoneFromNetworkCountryCode(i, str, this.mDeviceState.currentTimeMillis());
                            }
                        } else if (str.isEmpty()) {
                            build = findTimeZoneForTestNetwork(i, nitzSignal);
                        } else {
                            build = findTimeZoneFromCountryAndNitz(i, str, nitzSignal);
                        }
                    }
                    Objects.requireNonNull(build);
                    return build;
                }
            } catch (RuntimeException e) {
                String str2 = "getTimeZoneSuggestion: Error during lookup:  countryIsoCode=" + str + ", nitzSignal=" + nitzSignal + ", e=" + e.getMessage();
                TelephonyTimeZoneSuggestion createEmptySuggestion = TelephonyTimeZoneSuggestion.createEmptySuggestion(i, str2);
                Rlog.w("NitzStateMachineImpl", str2, e);
                return createEmptySuggestion;
            }
        }
        build = null;
        if (build == null) {
        }
        Objects.requireNonNull(build);
        return build;
    }

    private TelephonyTimeZoneSuggestion findTimeZoneForTestNetwork(int i, NitzSignal nitzSignal) {
        Objects.requireNonNull(nitzSignal);
        NitzData nitzData = nitzSignal.getNitzData();
        Objects.requireNonNull(nitzData);
        TelephonyTimeZoneSuggestion.Builder builder = new TelephonyTimeZoneSuggestion.Builder(i);
        builder.addDebugInfo("findTimeZoneForTestNetwork: nitzSignal=" + nitzSignal);
        CountryTimeZones.OffsetResult lookupByNitz = this.mTimeZoneLookupHelper.lookupByNitz(nitzData);
        if (lookupByNitz == null) {
            builder.addDebugInfo("findTimeZoneForTestNetwork: No zone found");
        } else {
            builder.setZoneId(lookupByNitz.getTimeZone().getID());
            builder.setMatchType(5);
            builder.setQuality(lookupByNitz.isOnlyMatch() ? 1 : 2);
            builder.addDebugInfo("findTimeZoneForTestNetwork: lookupResult=" + lookupByNitz);
        }
        return builder.build();
    }

    private TelephonyTimeZoneSuggestion findTimeZoneFromCountryAndNitz(int i, String str, NitzSignal nitzSignal) {
        Objects.requireNonNull(str);
        Objects.requireNonNull(nitzSignal);
        TelephonyTimeZoneSuggestion.Builder builder = new TelephonyTimeZoneSuggestion.Builder(i);
        builder.addDebugInfo("findTimeZoneFromCountryAndNitz: countryIsoCode=" + str + ", nitzSignal=" + nitzSignal);
        NitzData nitzData = nitzSignal.getNitzData();
        Objects.requireNonNull(nitzData);
        if (isNitzSignalOffsetInfoBogus(str, nitzData)) {
            builder.addDebugInfo("findTimeZoneFromCountryAndNitz: NITZ signal looks bogus");
            return builder.build();
        }
        CountryTimeZones.OffsetResult lookupByNitzCountry = this.mTimeZoneLookupHelper.lookupByNitzCountry(nitzData, str);
        if (lookupByNitzCountry != null) {
            builder.setZoneId(lookupByNitzCountry.getTimeZone().getID());
            builder.setMatchType(3);
            builder.setQuality(lookupByNitzCountry.isOnlyMatch() ? 1 : 2);
            builder.addDebugInfo("findTimeZoneFromCountryAndNitz: lookupResult=" + lookupByNitzCountry);
            return builder.build();
        }
        TimeZoneLookupHelper.CountryResult lookupByCountry = this.mTimeZoneLookupHelper.lookupByCountry(str, nitzData.getCurrentTimeInMillis());
        if (lookupByCountry == null) {
            builder.addDebugInfo("findTimeZoneFromCountryAndNitz: lookupByCountry() country not recognized");
            return builder.build();
        }
        int i2 = lookupByCountry.quality;
        if (i2 == 1 || i2 == 2) {
            builder.setZoneId(lookupByCountry.zoneId);
            builder.setMatchType(2);
            builder.setQuality(1);
            builder.addDebugInfo("findTimeZoneFromCountryAndNitz: high quality country-only suggestion: countryResult=" + lookupByCountry);
            return builder.build();
        }
        builder.addDebugInfo("findTimeZoneFromCountryAndNitz: country-only suggestion quality not high enough. countryResult=" + lookupByCountry);
        return builder.build();
    }

    private TelephonyTimeZoneSuggestion findTimeZoneFromNetworkCountryCode(int i, String str, long j) {
        Objects.requireNonNull(str);
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("countryIsoCode must not be empty");
        }
        TelephonyTimeZoneSuggestion.Builder builder = new TelephonyTimeZoneSuggestion.Builder(i);
        builder.addDebugInfo("findTimeZoneFromNetworkCountryCode: whenMillis=" + j + ", countryIsoCode=" + str);
        TimeZoneLookupHelper.CountryResult lookupByCountry = this.mTimeZoneLookupHelper.lookupByCountry(str, j);
        if (lookupByCountry != null) {
            builder.setZoneId(lookupByCountry.zoneId);
            int i2 = 2;
            builder.setMatchType(2);
            int i3 = lookupByCountry.quality;
            int i4 = 1;
            if (i3 != 1 && i3 != 2) {
                i4 = 3;
                if (i3 != 3) {
                    if (i3 != 4) {
                        throw new IllegalArgumentException("lookupResult.quality not recognized: countryIsoCode=" + str + ", whenMillis=" + j + ", lookupResult=" + lookupByCountry);
                    }
                }
                builder.setQuality(i2);
                builder.addDebugInfo("findTimeZoneFromNetworkCountryCode: lookupResult=" + lookupByCountry);
            }
            i2 = i4;
            builder.setQuality(i2);
            builder.addDebugInfo("findTimeZoneFromNetworkCountryCode: lookupResult=" + lookupByCountry);
        } else {
            builder.addDebugInfo("findTimeZoneFromNetworkCountryCode: Country not recognized?");
        }
        return builder.build();
    }

    private boolean isNitzSignalOffsetInfoBogus(String str, NitzData nitzData) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return (nitzData.getLocalOffsetMillis() == 0) && !countryUsesUtc(str, nitzData);
    }

    private boolean countryUsesUtc(String str, NitzData nitzData) {
        return this.mTimeZoneLookupHelper.countryUsesUtc(str, nitzData.getCurrentTimeInMillis());
    }
}
