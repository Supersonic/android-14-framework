package com.android.server.timedetector;

import android.app.time.TimeCapabilities;
import android.app.time.TimeCapabilitiesAndConfig;
import android.app.time.TimeConfiguration;
import android.os.UserHandle;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
/* loaded from: classes2.dex */
public final class ConfigurationInternal {
    public final boolean mAutoDetectionEnabledSetting;
    public final boolean mAutoDetectionSupported;
    public final Instant mAutoSuggestionLowerBound;
    public final Instant mManualSuggestionLowerBound;
    public final int[] mOriginPriorities;
    public final Instant mSuggestionUpperBound;
    public final int mSystemClockConfidenceThresholdMillis;
    public final int mSystemClockUpdateThresholdMillis;
    public final boolean mUserConfigAllowed;
    public final int mUserId;

    public ConfigurationInternal(Builder builder) {
        this.mAutoDetectionSupported = builder.mAutoDetectionSupported;
        this.mSystemClockUpdateThresholdMillis = builder.mSystemClockUpdateThresholdMillis;
        this.mSystemClockConfidenceThresholdMillis = builder.mSystemClockConfidenceThresholdMillis;
        Instant instant = builder.mAutoSuggestionLowerBound;
        Objects.requireNonNull(instant);
        this.mAutoSuggestionLowerBound = instant;
        Instant instant2 = builder.mManualSuggestionLowerBound;
        Objects.requireNonNull(instant2);
        this.mManualSuggestionLowerBound = instant2;
        Instant instant3 = builder.mSuggestionUpperBound;
        Objects.requireNonNull(instant3);
        this.mSuggestionUpperBound = instant3;
        int[] iArr = builder.mOriginPriorities;
        Objects.requireNonNull(iArr);
        this.mOriginPriorities = iArr;
        this.mAutoDetectionEnabledSetting = builder.mAutoDetectionEnabledSetting;
        this.mUserId = builder.mUserId;
        this.mUserConfigAllowed = builder.mUserConfigAllowed;
    }

    public boolean isAutoDetectionSupported() {
        return this.mAutoDetectionSupported;
    }

    public int getSystemClockUpdateThresholdMillis() {
        return this.mSystemClockUpdateThresholdMillis;
    }

    public int getSystemClockConfidenceThresholdMillis() {
        return this.mSystemClockConfidenceThresholdMillis;
    }

    public Instant getAutoSuggestionLowerBound() {
        return this.mAutoSuggestionLowerBound;
    }

    public Instant getManualSuggestionLowerBound() {
        return this.mManualSuggestionLowerBound;
    }

    public Instant getSuggestionUpperBound() {
        return this.mSuggestionUpperBound;
    }

    public int[] getAutoOriginPriorities() {
        return this.mOriginPriorities;
    }

    public boolean getAutoDetectionEnabledSetting() {
        return this.mAutoDetectionEnabledSetting;
    }

    public boolean getAutoDetectionEnabledBehavior() {
        return isAutoDetectionSupported() && this.mAutoDetectionEnabledSetting;
    }

    public int getUserId() {
        return this.mUserId;
    }

    public boolean isUserConfigAllowed() {
        return this.mUserConfigAllowed;
    }

    public TimeCapabilitiesAndConfig createCapabilitiesAndConfig(boolean z) {
        return new TimeCapabilitiesAndConfig(timeCapabilities(z), timeConfiguration());
    }

    public final TimeCapabilities timeCapabilities(boolean z) {
        TimeCapabilities.Builder builder = new TimeCapabilities.Builder(UserHandle.of(this.mUserId));
        boolean z2 = isUserConfigAllowed() || z;
        int i = 20;
        builder.setConfigureAutoDetectionEnabledCapability(!isAutoDetectionSupported() ? 10 : !z2 ? 20 : 40);
        if (z2) {
            i = getAutoDetectionEnabledBehavior() ? 30 : 40;
        }
        builder.setSetManualTimeCapability(i);
        return builder.build();
    }

    public final TimeConfiguration timeConfiguration() {
        return new TimeConfiguration.Builder().setAutoDetectionEnabled(getAutoDetectionEnabledSetting()).build();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ConfigurationInternal) {
            ConfigurationInternal configurationInternal = (ConfigurationInternal) obj;
            return this.mAutoDetectionSupported == configurationInternal.mAutoDetectionSupported && this.mAutoDetectionEnabledSetting == configurationInternal.mAutoDetectionEnabledSetting && this.mUserId == configurationInternal.mUserId && this.mUserConfigAllowed == configurationInternal.mUserConfigAllowed && this.mSystemClockUpdateThresholdMillis == configurationInternal.mSystemClockUpdateThresholdMillis && this.mAutoSuggestionLowerBound.equals(configurationInternal.mAutoSuggestionLowerBound) && this.mManualSuggestionLowerBound.equals(configurationInternal.mManualSuggestionLowerBound) && this.mSuggestionUpperBound.equals(configurationInternal.mSuggestionUpperBound) && Arrays.equals(this.mOriginPriorities, configurationInternal.mOriginPriorities);
        }
        return false;
    }

    public int hashCode() {
        return (Objects.hash(Boolean.valueOf(this.mAutoDetectionSupported), Boolean.valueOf(this.mAutoDetectionEnabledSetting), Integer.valueOf(this.mUserId), Boolean.valueOf(this.mUserConfigAllowed), Integer.valueOf(this.mSystemClockUpdateThresholdMillis), this.mAutoSuggestionLowerBound, this.mManualSuggestionLowerBound, this.mSuggestionUpperBound) * 31) + Arrays.hashCode(this.mOriginPriorities);
    }

    public String toString() {
        return "ConfigurationInternal{mAutoDetectionSupported=" + this.mAutoDetectionSupported + ", mSystemClockUpdateThresholdMillis=" + this.mSystemClockUpdateThresholdMillis + ", mSystemClockConfidenceThresholdMillis=" + this.mSystemClockConfidenceThresholdMillis + ", mAutoSuggestionLowerBound=" + this.mAutoSuggestionLowerBound + "(" + this.mAutoSuggestionLowerBound.toEpochMilli() + "), mManualSuggestionLowerBound=" + this.mManualSuggestionLowerBound + "(" + this.mManualSuggestionLowerBound.toEpochMilli() + "), mSuggestionUpperBound=" + this.mSuggestionUpperBound + "(" + this.mSuggestionUpperBound.toEpochMilli() + "), mOriginPriorities=" + ((String) Arrays.stream(this.mOriginPriorities).mapToObj(new IntFunction() { // from class: com.android.server.timedetector.ConfigurationInternal$$ExternalSyntheticLambda0
            @Override // java.util.function.IntFunction
            public final Object apply(int i) {
                return TimeDetectorStrategy.originToString(i);
            }
        }).collect(Collectors.joining(",", "[", "]"))) + ", mAutoDetectionEnabled=" + this.mAutoDetectionEnabledSetting + ", mUserId=" + this.mUserId + ", mUserConfigAllowed=" + this.mUserConfigAllowed + '}';
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        public boolean mAutoDetectionEnabledSetting;
        public boolean mAutoDetectionSupported;
        public Instant mAutoSuggestionLowerBound;
        public Instant mManualSuggestionLowerBound;
        public int[] mOriginPriorities;
        public Instant mSuggestionUpperBound;
        public int mSystemClockConfidenceThresholdMillis;
        public int mSystemClockUpdateThresholdMillis;
        public boolean mUserConfigAllowed;
        public final int mUserId;

        public Builder(int i) {
            this.mUserId = i;
        }

        public Builder setUserConfigAllowed(boolean z) {
            this.mUserConfigAllowed = z;
            return this;
        }

        public Builder setAutoDetectionSupported(boolean z) {
            this.mAutoDetectionSupported = z;
            return this;
        }

        public Builder setSystemClockUpdateThresholdMillis(int i) {
            this.mSystemClockUpdateThresholdMillis = i;
            return this;
        }

        public Builder setSystemClockConfidenceThresholdMillis(int i) {
            this.mSystemClockConfidenceThresholdMillis = i;
            return this;
        }

        public Builder setAutoSuggestionLowerBound(Instant instant) {
            Objects.requireNonNull(instant);
            this.mAutoSuggestionLowerBound = instant;
            return this;
        }

        public Builder setManualSuggestionLowerBound(Instant instant) {
            Objects.requireNonNull(instant);
            this.mManualSuggestionLowerBound = instant;
            return this;
        }

        public Builder setSuggestionUpperBound(Instant instant) {
            Objects.requireNonNull(instant);
            this.mSuggestionUpperBound = instant;
            return this;
        }

        public Builder setOriginPriorities(int... iArr) {
            Objects.requireNonNull(iArr);
            this.mOriginPriorities = iArr;
            return this;
        }

        public Builder setAutoDetectionEnabledSetting(boolean z) {
            this.mAutoDetectionEnabledSetting = z;
            return this;
        }

        public ConfigurationInternal build() {
            return new ConfigurationInternal(this);
        }
    }
}
