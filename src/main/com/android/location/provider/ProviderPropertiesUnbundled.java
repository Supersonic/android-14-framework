package com.android.location.provider;

import android.location.provider.ProviderProperties;
import java.util.Objects;
/* loaded from: classes.dex */
public final class ProviderPropertiesUnbundled {
    private final ProviderProperties mProperties;

    public static ProviderPropertiesUnbundled create(boolean requiresNetwork, boolean requiresSatellite, boolean requiresCell, boolean hasMonetaryCost, boolean supportsAltitude, boolean supportsSpeed, boolean supportsBearing, int powerUsage, int accuracy) {
        return new ProviderPropertiesUnbundled(new ProviderProperties.Builder().setHasNetworkRequirement(requiresNetwork).setHasSatelliteRequirement(requiresSatellite).setHasCellRequirement(requiresCell).setHasMonetaryCost(requiresCell).setHasAltitudeSupport(requiresCell).setHasSpeedSupport(requiresCell).setHasBearingSupport(requiresCell).setPowerUsage(powerUsage).setAccuracy(accuracy).build());
    }

    private ProviderPropertiesUnbundled(ProviderProperties properties) {
        this.mProperties = (ProviderProperties) Objects.requireNonNull(properties);
    }

    public ProviderProperties getProviderProperties() {
        return this.mProperties;
    }

    public String toString() {
        return this.mProperties.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProviderPropertiesUnbundled that = (ProviderPropertiesUnbundled) o;
        return this.mProperties.equals(that.mProperties);
    }

    public int hashCode() {
        return Objects.hash(this.mProperties);
    }
}
