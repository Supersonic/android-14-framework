package android.location;

import android.location.provider.ProviderProperties;
@Deprecated
/* loaded from: classes2.dex */
public class LocationProvider {
    @Deprecated
    public static final int AVAILABLE = 2;
    @Deprecated
    public static final int OUT_OF_SERVICE = 0;
    @Deprecated
    public static final int TEMPORARILY_UNAVAILABLE = 1;
    private final String mName;
    private final ProviderProperties mProperties;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LocationProvider(String name, ProviderProperties properties) {
        this.mName = name;
        this.mProperties = properties;
    }

    public String getName() {
        return this.mName;
    }

    public boolean meetsCriteria(Criteria criteria) {
        return propertiesMeetCriteria(this.mName, this.mProperties, criteria);
    }

    public static boolean propertiesMeetCriteria(String name, ProviderProperties properties, Criteria criteria) {
        if (LocationManager.PASSIVE_PROVIDER.equals(name) || properties == null) {
            return false;
        }
        if (criteria.getAccuracy() == 0 || criteria.getAccuracy() >= properties.getAccuracy()) {
            if (criteria.getPowerRequirement() == 0 || criteria.getPowerRequirement() >= properties.getPowerUsage()) {
                if (!criteria.isAltitudeRequired() || properties.hasAltitudeSupport()) {
                    if (!criteria.isSpeedRequired() || properties.hasSpeedSupport()) {
                        if (!criteria.isBearingRequired() || properties.hasBearingSupport()) {
                            return criteria.isCostAllowed() || !properties.hasMonetaryCost();
                        }
                        return false;
                    }
                    return false;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    public boolean requiresNetwork() {
        ProviderProperties providerProperties = this.mProperties;
        if (providerProperties == null) {
            return false;
        }
        return providerProperties.hasNetworkRequirement();
    }

    public boolean requiresSatellite() {
        ProviderProperties providerProperties = this.mProperties;
        if (providerProperties == null) {
            return false;
        }
        return providerProperties.hasSatelliteRequirement();
    }

    public boolean requiresCell() {
        ProviderProperties providerProperties = this.mProperties;
        if (providerProperties == null) {
            return false;
        }
        return providerProperties.hasCellRequirement();
    }

    public boolean hasMonetaryCost() {
        ProviderProperties providerProperties = this.mProperties;
        if (providerProperties == null) {
            return false;
        }
        return providerProperties.hasMonetaryCost();
    }

    public boolean supportsAltitude() {
        ProviderProperties providerProperties = this.mProperties;
        if (providerProperties == null) {
            return false;
        }
        return providerProperties.hasAltitudeSupport();
    }

    public boolean supportsSpeed() {
        ProviderProperties providerProperties = this.mProperties;
        if (providerProperties == null) {
            return false;
        }
        return providerProperties.hasSpeedSupport();
    }

    public boolean supportsBearing() {
        ProviderProperties providerProperties = this.mProperties;
        if (providerProperties == null) {
            return false;
        }
        return providerProperties.hasBearingSupport();
    }

    public int getPowerRequirement() {
        ProviderProperties providerProperties = this.mProperties;
        if (providerProperties == null) {
            return 3;
        }
        return providerProperties.getPowerUsage();
    }

    public int getAccuracy() {
        ProviderProperties providerProperties = this.mProperties;
        if (providerProperties == null) {
            return 2;
        }
        return providerProperties.getAccuracy();
    }
}
