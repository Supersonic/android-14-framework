package com.android.server.timezonedetector;

import android.app.timezonedetector.ManualTimeZoneSuggestion;
import android.app.timezonedetector.TelephonyTimeZoneSuggestion;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class MetricsTimeZoneDetectorState {
    public final ConfigurationInternal mConfigurationInternal;
    public final String mDeviceTimeZoneId;
    public final int mDeviceTimeZoneIdOrdinal;
    public final MetricsTimeZoneSuggestion mLatestGeolocationSuggestion;
    public final MetricsTimeZoneSuggestion mLatestManualSuggestion;
    public final MetricsTimeZoneSuggestion mLatestTelephonySuggestion;

    public MetricsTimeZoneDetectorState(ConfigurationInternal configurationInternal, int i, String str, MetricsTimeZoneSuggestion metricsTimeZoneSuggestion, MetricsTimeZoneSuggestion metricsTimeZoneSuggestion2, MetricsTimeZoneSuggestion metricsTimeZoneSuggestion3) {
        Objects.requireNonNull(configurationInternal);
        this.mConfigurationInternal = configurationInternal;
        this.mDeviceTimeZoneIdOrdinal = i;
        this.mDeviceTimeZoneId = str;
        this.mLatestManualSuggestion = metricsTimeZoneSuggestion;
        this.mLatestTelephonySuggestion = metricsTimeZoneSuggestion2;
        this.mLatestGeolocationSuggestion = metricsTimeZoneSuggestion3;
    }

    public static MetricsTimeZoneDetectorState create(OrdinalGenerator<String> ordinalGenerator, ConfigurationInternal configurationInternal, String str, ManualTimeZoneSuggestion manualTimeZoneSuggestion, TelephonyTimeZoneSuggestion telephonyTimeZoneSuggestion, LocationAlgorithmEvent locationAlgorithmEvent) {
        boolean isEnhancedMetricsCollectionEnabled = configurationInternal.isEnhancedMetricsCollectionEnabled();
        String str2 = isEnhancedMetricsCollectionEnabled ? str : null;
        Objects.requireNonNull(str);
        return new MetricsTimeZoneDetectorState(configurationInternal, ordinalGenerator.ordinal(str), str2, createMetricsTimeZoneSuggestion(ordinalGenerator, manualTimeZoneSuggestion, isEnhancedMetricsCollectionEnabled), createMetricsTimeZoneSuggestion(ordinalGenerator, telephonyTimeZoneSuggestion, isEnhancedMetricsCollectionEnabled), locationAlgorithmEvent != null ? createMetricsTimeZoneSuggestion(ordinalGenerator, locationAlgorithmEvent.getSuggestion(), isEnhancedMetricsCollectionEnabled) : null);
    }

    public boolean isTelephonyDetectionSupported() {
        return this.mConfigurationInternal.isTelephonyDetectionSupported();
    }

    public boolean isGeoDetectionSupported() {
        return this.mConfigurationInternal.isGeoDetectionSupported();
    }

    public boolean isTelephonyTimeZoneFallbackSupported() {
        return this.mConfigurationInternal.isTelephonyFallbackSupported();
    }

    public boolean getGeoDetectionRunInBackgroundEnabled() {
        return this.mConfigurationInternal.getGeoDetectionRunInBackgroundEnabledSetting();
    }

    public boolean isEnhancedMetricsCollectionEnabled() {
        return this.mConfigurationInternal.isEnhancedMetricsCollectionEnabled();
    }

    public boolean getUserLocationEnabledSetting() {
        return this.mConfigurationInternal.getLocationEnabledSetting();
    }

    public boolean getGeoDetectionEnabledSetting() {
        return this.mConfigurationInternal.getGeoDetectionEnabledSetting();
    }

    public boolean getAutoDetectionEnabledSetting() {
        return this.mConfigurationInternal.getAutoDetectionEnabledSetting();
    }

    public int getDetectionMode() {
        int detectionMode = this.mConfigurationInternal.getDetectionMode();
        int i = 1;
        if (detectionMode != 1) {
            i = 2;
            if (detectionMode != 2) {
                i = 3;
                if (detectionMode != 3) {
                    return 0;
                }
            }
        }
        return i;
    }

    public int getDeviceTimeZoneIdOrdinal() {
        return this.mDeviceTimeZoneIdOrdinal;
    }

    public String getDeviceTimeZoneId() {
        return this.mDeviceTimeZoneId;
    }

    public MetricsTimeZoneSuggestion getLatestManualSuggestion() {
        return this.mLatestManualSuggestion;
    }

    public MetricsTimeZoneSuggestion getLatestTelephonySuggestion() {
        return this.mLatestTelephonySuggestion;
    }

    public MetricsTimeZoneSuggestion getLatestGeolocationSuggestion() {
        return this.mLatestGeolocationSuggestion;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || MetricsTimeZoneDetectorState.class != obj.getClass()) {
            return false;
        }
        MetricsTimeZoneDetectorState metricsTimeZoneDetectorState = (MetricsTimeZoneDetectorState) obj;
        return this.mDeviceTimeZoneIdOrdinal == metricsTimeZoneDetectorState.mDeviceTimeZoneIdOrdinal && Objects.equals(this.mDeviceTimeZoneId, metricsTimeZoneDetectorState.mDeviceTimeZoneId) && this.mConfigurationInternal.equals(metricsTimeZoneDetectorState.mConfigurationInternal) && Objects.equals(this.mLatestManualSuggestion, metricsTimeZoneDetectorState.mLatestManualSuggestion) && Objects.equals(this.mLatestTelephonySuggestion, metricsTimeZoneDetectorState.mLatestTelephonySuggestion) && Objects.equals(this.mLatestGeolocationSuggestion, metricsTimeZoneDetectorState.mLatestGeolocationSuggestion);
    }

    public int hashCode() {
        return Objects.hash(this.mConfigurationInternal, Integer.valueOf(this.mDeviceTimeZoneIdOrdinal), this.mDeviceTimeZoneId, this.mLatestManualSuggestion, this.mLatestTelephonySuggestion, this.mLatestGeolocationSuggestion);
    }

    public String toString() {
        return "MetricsTimeZoneDetectorState{mConfigurationInternal=" + this.mConfigurationInternal + ", mDeviceTimeZoneIdOrdinal=" + this.mDeviceTimeZoneIdOrdinal + ", mDeviceTimeZoneId=" + this.mDeviceTimeZoneId + ", mLatestManualSuggestion=" + this.mLatestManualSuggestion + ", mLatestTelephonySuggestion=" + this.mLatestTelephonySuggestion + ", mLatestGeolocationSuggestion=" + this.mLatestGeolocationSuggestion + '}';
    }

    public static MetricsTimeZoneSuggestion createMetricsTimeZoneSuggestion(OrdinalGenerator<String> ordinalGenerator, ManualTimeZoneSuggestion manualTimeZoneSuggestion, boolean z) {
        if (manualTimeZoneSuggestion == null) {
            return null;
        }
        String zoneId = manualTimeZoneSuggestion.getZoneId();
        return MetricsTimeZoneSuggestion.createCertain(z ? new String[]{zoneId} : null, new int[]{ordinalGenerator.ordinal(zoneId)});
    }

    public static MetricsTimeZoneSuggestion createMetricsTimeZoneSuggestion(OrdinalGenerator<String> ordinalGenerator, TelephonyTimeZoneSuggestion telephonyTimeZoneSuggestion, boolean z) {
        if (telephonyTimeZoneSuggestion == null) {
            return null;
        }
        String zoneId = telephonyTimeZoneSuggestion.getZoneId();
        if (zoneId == null) {
            return MetricsTimeZoneSuggestion.createUncertain();
        }
        return MetricsTimeZoneSuggestion.createCertain(z ? new String[]{zoneId} : null, new int[]{ordinalGenerator.ordinal(zoneId)});
    }

    public static MetricsTimeZoneSuggestion createMetricsTimeZoneSuggestion(OrdinalGenerator<String> ordinalGenerator, GeolocationTimeZoneSuggestion geolocationTimeZoneSuggestion, boolean z) {
        if (geolocationTimeZoneSuggestion == null) {
            return null;
        }
        List<String> zoneIds = geolocationTimeZoneSuggestion.getZoneIds();
        if (zoneIds == null) {
            return MetricsTimeZoneSuggestion.createUncertain();
        }
        return MetricsTimeZoneSuggestion.createCertain(z ? (String[]) zoneIds.toArray(new String[0]) : null, ordinalGenerator.ordinals(zoneIds));
    }

    /* loaded from: classes2.dex */
    public static final class MetricsTimeZoneSuggestion {
        public final int[] mZoneIdOrdinals;
        public final String[] mZoneIds;

        public MetricsTimeZoneSuggestion(String[] strArr, int[] iArr) {
            this.mZoneIds = strArr;
            this.mZoneIdOrdinals = iArr;
        }

        public static MetricsTimeZoneSuggestion createUncertain() {
            return new MetricsTimeZoneSuggestion(null, null);
        }

        public static MetricsTimeZoneSuggestion createCertain(String[] strArr, int[] iArr) {
            return new MetricsTimeZoneSuggestion(strArr, iArr);
        }

        public boolean isCertain() {
            return this.mZoneIdOrdinals != null;
        }

        public int[] getZoneIdOrdinals() {
            return this.mZoneIdOrdinals;
        }

        public String[] getZoneIds() {
            return this.mZoneIds;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || MetricsTimeZoneSuggestion.class != obj.getClass()) {
                return false;
            }
            MetricsTimeZoneSuggestion metricsTimeZoneSuggestion = (MetricsTimeZoneSuggestion) obj;
            return Arrays.equals(this.mZoneIdOrdinals, metricsTimeZoneSuggestion.mZoneIdOrdinals) && Arrays.equals(this.mZoneIds, metricsTimeZoneSuggestion.mZoneIds);
        }

        public int hashCode() {
            return (Arrays.hashCode(this.mZoneIds) * 31) + Arrays.hashCode(this.mZoneIdOrdinals);
        }

        public String toString() {
            return "MetricsTimeZoneSuggestion{mZoneIdOrdinals=" + Arrays.toString(this.mZoneIdOrdinals) + ", mZoneIds=" + Arrays.toString(this.mZoneIds) + '}';
        }
    }
}
