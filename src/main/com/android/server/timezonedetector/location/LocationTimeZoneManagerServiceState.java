package com.android.server.timezonedetector.location;

import com.android.server.timezonedetector.LocationAlgorithmEvent;
import com.android.server.timezonedetector.location.LocationTimeZoneProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class LocationTimeZoneManagerServiceState {
    public final String mControllerState;
    public final List<String> mControllerStates;
    public final LocationAlgorithmEvent mLastEvent;
    public final List<LocationTimeZoneProvider.ProviderState> mPrimaryProviderStates;
    public final List<LocationTimeZoneProvider.ProviderState> mSecondaryProviderStates;

    public LocationTimeZoneManagerServiceState(Builder builder) {
        this.mControllerState = builder.mControllerState;
        this.mLastEvent = builder.mLastEvent;
        List<String> list = builder.mControllerStates;
        Objects.requireNonNull(list);
        this.mControllerStates = list;
        List<LocationTimeZoneProvider.ProviderState> list2 = builder.mPrimaryProviderStates;
        Objects.requireNonNull(list2);
        this.mPrimaryProviderStates = list2;
        List<LocationTimeZoneProvider.ProviderState> list3 = builder.mSecondaryProviderStates;
        Objects.requireNonNull(list3);
        this.mSecondaryProviderStates = list3;
    }

    public LocationAlgorithmEvent getLastEvent() {
        return this.mLastEvent;
    }

    public List<String> getControllerStates() {
        return this.mControllerStates;
    }

    public List<LocationTimeZoneProvider.ProviderState> getPrimaryProviderStates() {
        return Collections.unmodifiableList(this.mPrimaryProviderStates);
    }

    public List<LocationTimeZoneProvider.ProviderState> getSecondaryProviderStates() {
        return Collections.unmodifiableList(this.mSecondaryProviderStates);
    }

    public String toString() {
        return "LocationTimeZoneManagerServiceState{mControllerState=" + this.mControllerState + ", mLastEvent=" + this.mLastEvent + ", mControllerStates=" + this.mControllerStates + ", mPrimaryProviderStates=" + this.mPrimaryProviderStates + ", mSecondaryProviderStates=" + this.mSecondaryProviderStates + '}';
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        public String mControllerState;
        public List<String> mControllerStates;
        public LocationAlgorithmEvent mLastEvent;
        public List<LocationTimeZoneProvider.ProviderState> mPrimaryProviderStates;
        public List<LocationTimeZoneProvider.ProviderState> mSecondaryProviderStates;

        public Builder setControllerState(String str) {
            this.mControllerState = str;
            return this;
        }

        public Builder setLastEvent(LocationAlgorithmEvent locationAlgorithmEvent) {
            Objects.requireNonNull(locationAlgorithmEvent);
            this.mLastEvent = locationAlgorithmEvent;
            return this;
        }

        public Builder setStateChanges(List<String> list) {
            this.mControllerStates = new ArrayList(list);
            return this;
        }

        public Builder setPrimaryProviderStateChanges(List<LocationTimeZoneProvider.ProviderState> list) {
            this.mPrimaryProviderStates = new ArrayList(list);
            return this;
        }

        public Builder setSecondaryProviderStateChanges(List<LocationTimeZoneProvider.ProviderState> list) {
            this.mSecondaryProviderStates = new ArrayList(list);
            return this;
        }

        public LocationTimeZoneManagerServiceState build() {
            return new LocationTimeZoneManagerServiceState(this);
        }
    }
}
