package com.android.location.provider;

import android.location.LocationRequest;
import android.location.provider.ProviderRequest;
import android.os.WorkSource;
import java.util.Collections;
import java.util.List;
/* loaded from: classes.dex */
public final class ProviderRequestUnbundled {
    public static final long INTERVAL_DISABLED = Long.MAX_VALUE;
    private final ProviderRequest mRequest;

    public ProviderRequestUnbundled(ProviderRequest request) {
        this.mRequest = request;
    }

    public boolean getReportLocation() {
        return this.mRequest.isActive();
    }

    public long getInterval() {
        return this.mRequest.getIntervalMillis();
    }

    public int getQuality() {
        return this.mRequest.getQuality();
    }

    public long getMaxUpdateDelayMillis() {
        return this.mRequest.getMaxUpdateDelayMillis();
    }

    public boolean isLowPower() {
        return this.mRequest.isLowPower();
    }

    public boolean isLocationSettingsIgnored() {
        return this.mRequest.isLocationSettingsIgnored();
    }

    @Deprecated
    public List<LocationRequestUnbundled> getLocationRequests() {
        if (!this.mRequest.isActive()) {
            return Collections.emptyList();
        }
        return Collections.singletonList(new LocationRequestUnbundled(new LocationRequest.Builder(this.mRequest.getIntervalMillis()).setQuality(this.mRequest.getQuality()).setLowPower(this.mRequest.isLowPower()).setLocationSettingsIgnored(this.mRequest.isLocationSettingsIgnored()).setWorkSource(this.mRequest.getWorkSource()).build()));
    }

    public WorkSource getWorkSource() {
        return this.mRequest.getWorkSource();
    }

    public String toString() {
        return this.mRequest.toString();
    }
}
