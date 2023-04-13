package android.companion.virtual;

import android.annotation.SystemApi;
import android.companion.virtual.VirtualDeviceParams;
import android.companion.virtual.sensor.IVirtualSensorCallback;
import android.companion.virtual.sensor.VirtualSensor;
import android.companion.virtual.sensor.VirtualSensorCallback;
import android.companion.virtual.sensor.VirtualSensorConfig;
import android.content.ComponentName;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.SharedMemory;
import android.p008os.UserHandle;
import android.util.ArraySet;
import android.util.SparseArray;
import android.util.SparseIntArray;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
@SystemApi
/* loaded from: classes.dex */
public final class VirtualDeviceParams implements Parcelable {
    public static final int ACTIVITY_POLICY_DEFAULT_ALLOWED = 0;
    public static final int ACTIVITY_POLICY_DEFAULT_BLOCKED = 1;
    public static final Parcelable.Creator<VirtualDeviceParams> CREATOR = new Parcelable.Creator<VirtualDeviceParams>() { // from class: android.companion.virtual.VirtualDeviceParams.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VirtualDeviceParams createFromParcel(Parcel in) {
            return new VirtualDeviceParams(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VirtualDeviceParams[] newArray(int size) {
            return new VirtualDeviceParams[size];
        }
    };
    public static final int DEVICE_POLICY_CUSTOM = 1;
    public static final int DEVICE_POLICY_DEFAULT = 0;
    public static final int LOCK_STATE_ALWAYS_UNLOCKED = 1;
    public static final int LOCK_STATE_DEFAULT = 0;
    public static final int NAVIGATION_POLICY_DEFAULT_ALLOWED = 0;
    public static final int NAVIGATION_POLICY_DEFAULT_BLOCKED = 1;
    public static final int POLICY_TYPE_AUDIO = 1;
    public static final int POLICY_TYPE_RECENTS = 2;
    public static final int POLICY_TYPE_SENSORS = 0;
    private final ArraySet<ComponentName> mAllowedActivities;
    private final ArraySet<ComponentName> mAllowedCrossTaskNavigations;
    private final int mAudioPlaybackSessionId;
    private final int mAudioRecordingSessionId;
    private final ArraySet<ComponentName> mBlockedActivities;
    private final ArraySet<ComponentName> mBlockedCrossTaskNavigations;
    private final int mDefaultActivityPolicy;
    private final int mDefaultNavigationPolicy;
    private final SparseIntArray mDevicePolicies;
    private final int mLockState;
    private final String mName;
    private final ArraySet<UserHandle> mUsersWithMatchingAccounts;
    private final IVirtualSensorCallback mVirtualSensorCallback;
    private final List<VirtualSensorConfig> mVirtualSensorConfigs;

    @Target({ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface ActivityPolicy {
    }

    @Target({ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface DevicePolicy {
    }

    @Target({ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface LockState {
    }

    @Target({ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface NavigationPolicy {
    }

    @Target({ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface PolicyType {
    }

    private VirtualDeviceParams(int lockState, Set<UserHandle> usersWithMatchingAccounts, Set<ComponentName> allowedCrossTaskNavigations, Set<ComponentName> blockedCrossTaskNavigations, int defaultNavigationPolicy, Set<ComponentName> allowedActivities, Set<ComponentName> blockedActivities, int defaultActivityPolicy, String name, SparseIntArray devicePolicies, List<VirtualSensorConfig> virtualSensorConfigs, IVirtualSensorCallback virtualSensorCallback, int audioPlaybackSessionId, int audioRecordingSessionId) {
        this.mLockState = lockState;
        this.mUsersWithMatchingAccounts = new ArraySet<>((Collection) Objects.requireNonNull(usersWithMatchingAccounts));
        this.mAllowedCrossTaskNavigations = new ArraySet<>((Collection) Objects.requireNonNull(allowedCrossTaskNavigations));
        this.mBlockedCrossTaskNavigations = new ArraySet<>((Collection) Objects.requireNonNull(blockedCrossTaskNavigations));
        this.mDefaultNavigationPolicy = defaultNavigationPolicy;
        this.mAllowedActivities = new ArraySet<>((Collection) Objects.requireNonNull(allowedActivities));
        this.mBlockedActivities = new ArraySet<>((Collection) Objects.requireNonNull(blockedActivities));
        this.mDefaultActivityPolicy = defaultActivityPolicy;
        this.mName = name;
        this.mDevicePolicies = (SparseIntArray) Objects.requireNonNull(devicePolicies);
        this.mVirtualSensorConfigs = (List) Objects.requireNonNull(virtualSensorConfigs);
        this.mVirtualSensorCallback = virtualSensorCallback;
        this.mAudioPlaybackSessionId = audioPlaybackSessionId;
        this.mAudioRecordingSessionId = audioRecordingSessionId;
    }

    private VirtualDeviceParams(Parcel parcel) {
        this.mLockState = parcel.readInt();
        this.mUsersWithMatchingAccounts = parcel.readArraySet(null);
        this.mAllowedCrossTaskNavigations = parcel.readArraySet(null);
        this.mBlockedCrossTaskNavigations = parcel.readArraySet(null);
        this.mDefaultNavigationPolicy = parcel.readInt();
        this.mAllowedActivities = parcel.readArraySet(null);
        this.mBlockedActivities = parcel.readArraySet(null);
        this.mDefaultActivityPolicy = parcel.readInt();
        this.mName = parcel.readString8();
        this.mDevicePolicies = parcel.readSparseIntArray();
        ArrayList arrayList = new ArrayList();
        this.mVirtualSensorConfigs = arrayList;
        parcel.readTypedList(arrayList, VirtualSensorConfig.CREATOR);
        this.mVirtualSensorCallback = IVirtualSensorCallback.Stub.asInterface(parcel.readStrongBinder());
        this.mAudioPlaybackSessionId = parcel.readInt();
        this.mAudioRecordingSessionId = parcel.readInt();
    }

    public int getLockState() {
        return this.mLockState;
    }

    public Set<UserHandle> getUsersWithMatchingAccounts() {
        return Collections.unmodifiableSet(this.mUsersWithMatchingAccounts);
    }

    public Set<ComponentName> getAllowedCrossTaskNavigations() {
        return Collections.unmodifiableSet(this.mAllowedCrossTaskNavigations);
    }

    public Set<ComponentName> getBlockedCrossTaskNavigations() {
        return Collections.unmodifiableSet(this.mBlockedCrossTaskNavigations);
    }

    public int getDefaultNavigationPolicy() {
        return this.mDefaultNavigationPolicy;
    }

    public Set<ComponentName> getAllowedActivities() {
        return Collections.unmodifiableSet(this.mAllowedActivities);
    }

    public Set<ComponentName> getBlockedActivities() {
        return Collections.unmodifiableSet(this.mBlockedActivities);
    }

    public int getDefaultActivityPolicy() {
        return this.mDefaultActivityPolicy;
    }

    public String getName() {
        return this.mName;
    }

    public int getDevicePolicy(int policyType) {
        return this.mDevicePolicies.get(policyType, 0);
    }

    public List<VirtualSensorConfig> getVirtualSensorConfigs() {
        return this.mVirtualSensorConfigs;
    }

    public IVirtualSensorCallback getVirtualSensorCallback() {
        return this.mVirtualSensorCallback;
    }

    public int getAudioPlaybackSessionId() {
        return this.mAudioPlaybackSessionId;
    }

    public int getAudioRecordingSessionId() {
        return this.mAudioRecordingSessionId;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mLockState);
        dest.writeArraySet(this.mUsersWithMatchingAccounts);
        dest.writeArraySet(this.mAllowedCrossTaskNavigations);
        dest.writeArraySet(this.mBlockedCrossTaskNavigations);
        dest.writeInt(this.mDefaultNavigationPolicy);
        dest.writeArraySet(this.mAllowedActivities);
        dest.writeArraySet(this.mBlockedActivities);
        dest.writeInt(this.mDefaultActivityPolicy);
        dest.writeString8(this.mName);
        dest.writeSparseIntArray(this.mDevicePolicies);
        dest.writeTypedList(this.mVirtualSensorConfigs);
        IVirtualSensorCallback iVirtualSensorCallback = this.mVirtualSensorCallback;
        dest.writeStrongBinder(iVirtualSensorCallback != null ? iVirtualSensorCallback.asBinder() : null);
        dest.writeInt(this.mAudioPlaybackSessionId);
        dest.writeInt(this.mAudioRecordingSessionId);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof VirtualDeviceParams) {
            VirtualDeviceParams that = (VirtualDeviceParams) o;
            int devicePoliciesCount = this.mDevicePolicies.size();
            if (devicePoliciesCount != that.mDevicePolicies.size()) {
                return false;
            }
            for (int i = 0; i < devicePoliciesCount; i++) {
                if (this.mDevicePolicies.keyAt(i) != that.mDevicePolicies.keyAt(i) || this.mDevicePolicies.valueAt(i) != that.mDevicePolicies.valueAt(i)) {
                    return false;
                }
            }
            int i2 = this.mLockState;
            return i2 == that.mLockState && this.mUsersWithMatchingAccounts.equals(that.mUsersWithMatchingAccounts) && Objects.equals(this.mAllowedCrossTaskNavigations, that.mAllowedCrossTaskNavigations) && Objects.equals(this.mBlockedCrossTaskNavigations, that.mBlockedCrossTaskNavigations) && this.mDefaultNavigationPolicy == that.mDefaultNavigationPolicy && Objects.equals(this.mAllowedActivities, that.mAllowedActivities) && Objects.equals(this.mBlockedActivities, that.mBlockedActivities) && this.mDefaultActivityPolicy == that.mDefaultActivityPolicy && Objects.equals(this.mName, that.mName) && this.mAudioPlaybackSessionId == that.mAudioPlaybackSessionId && this.mAudioRecordingSessionId == that.mAudioRecordingSessionId;
        }
        return false;
    }

    public int hashCode() {
        int hashCode = Objects.hash(Integer.valueOf(this.mLockState), this.mUsersWithMatchingAccounts, this.mAllowedCrossTaskNavigations, this.mBlockedCrossTaskNavigations, Integer.valueOf(this.mDefaultNavigationPolicy), this.mAllowedActivities, this.mBlockedActivities, Integer.valueOf(this.mDefaultActivityPolicy), this.mName, this.mDevicePolicies, Integer.valueOf(this.mAudioPlaybackSessionId), Integer.valueOf(this.mAudioRecordingSessionId));
        for (int i = 0; i < this.mDevicePolicies.size(); i++) {
            hashCode = (((hashCode * 31) + this.mDevicePolicies.keyAt(i)) * 31) + this.mDevicePolicies.valueAt(i);
        }
        return hashCode;
    }

    public String toString() {
        return "VirtualDeviceParams( mLockState=" + this.mLockState + " mUsersWithMatchingAccounts=" + this.mUsersWithMatchingAccounts + " mAllowedCrossTaskNavigations=" + this.mAllowedCrossTaskNavigations + " mBlockedCrossTaskNavigations=" + this.mBlockedCrossTaskNavigations + " mDefaultNavigationPolicy=" + this.mDefaultNavigationPolicy + " mAllowedActivities=" + this.mAllowedActivities + " mBlockedActivities=" + this.mBlockedActivities + " mDefaultActivityPolicy=" + this.mDefaultActivityPolicy + " mName=" + this.mName + " mDevicePolicies=" + this.mDevicePolicies + " mAudioPlaybackSessionId=" + this.mAudioPlaybackSessionId + " mAudioRecordingSessionId=" + this.mAudioRecordingSessionId + NavigationBarInflaterView.KEY_CODE_END;
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private String mName;
        private IVirtualSensorCallback mVirtualSensorCallback;
        private int mLockState = 0;
        private Set<UserHandle> mUsersWithMatchingAccounts = Collections.emptySet();
        private Set<ComponentName> mAllowedCrossTaskNavigations = Collections.emptySet();
        private Set<ComponentName> mBlockedCrossTaskNavigations = Collections.emptySet();
        private int mDefaultNavigationPolicy = 0;
        private boolean mDefaultNavigationPolicyConfigured = false;
        private Set<ComponentName> mBlockedActivities = Collections.emptySet();
        private Set<ComponentName> mAllowedActivities = Collections.emptySet();
        private int mDefaultActivityPolicy = 0;
        private boolean mDefaultActivityPolicyConfigured = false;
        private SparseIntArray mDevicePolicies = new SparseIntArray();
        private int mAudioPlaybackSessionId = 0;
        private int mAudioRecordingSessionId = 0;
        private List<VirtualSensorConfig> mVirtualSensorConfigs = new ArrayList();

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class VirtualSensorCallbackDelegate extends IVirtualSensorCallback.Stub {
            private final VirtualSensorCallback mCallback;
            private final Executor mExecutor;

            VirtualSensorCallbackDelegate(Executor executor, VirtualSensorCallback callback) {
                this.mCallback = callback;
                this.mExecutor = executor;
            }

            @Override // android.companion.virtual.sensor.IVirtualSensorCallback
            public void onConfigurationChanged(final VirtualSensor sensor, final boolean enabled, int samplingPeriodMicros, int batchReportLatencyMicros) {
                final Duration samplingPeriod = Duration.ofNanos(TimeUnit.MICROSECONDS.toNanos(samplingPeriodMicros));
                final Duration batchReportingLatency = Duration.ofNanos(TimeUnit.MICROSECONDS.toNanos(batchReportLatencyMicros));
                this.mExecutor.execute(new Runnable() { // from class: android.companion.virtual.VirtualDeviceParams$Builder$VirtualSensorCallbackDelegate$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        VirtualDeviceParams.Builder.VirtualSensorCallbackDelegate.this.lambda$onConfigurationChanged$0(sensor, enabled, samplingPeriod, batchReportingLatency);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onConfigurationChanged$0(VirtualSensor sensor, boolean enabled, Duration samplingPeriod, Duration batchReportingLatency) {
                this.mCallback.onConfigurationChanged(sensor, enabled, samplingPeriod, batchReportingLatency);
            }

            @Override // android.companion.virtual.sensor.IVirtualSensorCallback
            public void onDirectChannelCreated(final int channelHandle, final SharedMemory sharedMemory) {
                this.mExecutor.execute(new Runnable() { // from class: android.companion.virtual.VirtualDeviceParams$Builder$VirtualSensorCallbackDelegate$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        VirtualDeviceParams.Builder.VirtualSensorCallbackDelegate.this.lambda$onDirectChannelCreated$1(channelHandle, sharedMemory);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onDirectChannelCreated$1(int channelHandle, SharedMemory sharedMemory) {
                this.mCallback.onDirectChannelCreated(channelHandle, sharedMemory);
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onDirectChannelDestroyed$2(int channelHandle) {
                this.mCallback.onDirectChannelDestroyed(channelHandle);
            }

            @Override // android.companion.virtual.sensor.IVirtualSensorCallback
            public void onDirectChannelDestroyed(final int channelHandle) {
                this.mExecutor.execute(new Runnable() { // from class: android.companion.virtual.VirtualDeviceParams$Builder$VirtualSensorCallbackDelegate$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        VirtualDeviceParams.Builder.VirtualSensorCallbackDelegate.this.lambda$onDirectChannelDestroyed$2(channelHandle);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onDirectChannelConfigured$3(int channelHandle, VirtualSensor sensor, int rateLevel, int reportToken) {
                this.mCallback.onDirectChannelConfigured(channelHandle, sensor, rateLevel, reportToken);
            }

            @Override // android.companion.virtual.sensor.IVirtualSensorCallback
            public void onDirectChannelConfigured(final int channelHandle, final VirtualSensor sensor, final int rateLevel, final int reportToken) {
                this.mExecutor.execute(new Runnable() { // from class: android.companion.virtual.VirtualDeviceParams$Builder$VirtualSensorCallbackDelegate$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        VirtualDeviceParams.Builder.VirtualSensorCallbackDelegate.this.lambda$onDirectChannelConfigured$3(channelHandle, sensor, rateLevel, reportToken);
                    }
                });
            }
        }

        public Builder setLockState(int lockState) {
            this.mLockState = lockState;
            return this;
        }

        public Builder setUsersWithMatchingAccounts(Set<UserHandle> usersWithMatchingAccounts) {
            this.mUsersWithMatchingAccounts = (Set) Objects.requireNonNull(usersWithMatchingAccounts);
            return this;
        }

        public Builder setAllowedCrossTaskNavigations(Set<ComponentName> allowedCrossTaskNavigations) {
            if (this.mDefaultNavigationPolicyConfigured && this.mDefaultNavigationPolicy != 1) {
                throw new IllegalArgumentException("Allowed cross task navigation and blocked task navigation cannot  both be set.");
            }
            this.mDefaultNavigationPolicy = 1;
            this.mDefaultNavigationPolicyConfigured = true;
            this.mAllowedCrossTaskNavigations = (Set) Objects.requireNonNull(allowedCrossTaskNavigations);
            return this;
        }

        public Builder setBlockedCrossTaskNavigations(Set<ComponentName> blockedCrossTaskNavigations) {
            if (this.mDefaultNavigationPolicyConfigured && this.mDefaultNavigationPolicy != 0) {
                throw new IllegalArgumentException("Allowed cross task navigation and blocked task navigation cannot  be set.");
            }
            this.mDefaultNavigationPolicy = 0;
            this.mDefaultNavigationPolicyConfigured = true;
            this.mBlockedCrossTaskNavigations = (Set) Objects.requireNonNull(blockedCrossTaskNavigations);
            return this;
        }

        public Builder setAllowedActivities(Set<ComponentName> allowedActivities) {
            if (this.mDefaultActivityPolicyConfigured && this.mDefaultActivityPolicy != 1) {
                throw new IllegalArgumentException("Allowed activities and Blocked activities cannot both be set.");
            }
            this.mDefaultActivityPolicy = 1;
            this.mDefaultActivityPolicyConfigured = true;
            this.mAllowedActivities = (Set) Objects.requireNonNull(allowedActivities);
            return this;
        }

        public Builder setBlockedActivities(Set<ComponentName> blockedActivities) {
            if (this.mDefaultActivityPolicyConfigured && this.mDefaultActivityPolicy != 0) {
                throw new IllegalArgumentException("Allowed activities and Blocked activities cannot both be set.");
            }
            this.mDefaultActivityPolicy = 0;
            this.mDefaultActivityPolicyConfigured = true;
            this.mBlockedActivities = (Set) Objects.requireNonNull(blockedActivities);
            return this;
        }

        public Builder setName(String name) {
            this.mName = name;
            return this;
        }

        public Builder setDevicePolicy(int policyType, int devicePolicy) {
            this.mDevicePolicies.put(policyType, devicePolicy);
            return this;
        }

        public Builder addVirtualSensorConfig(VirtualSensorConfig virtualSensorConfig) {
            this.mVirtualSensorConfigs.add((VirtualSensorConfig) Objects.requireNonNull(virtualSensorConfig));
            return this;
        }

        public Builder setVirtualSensorCallback(Executor executor, VirtualSensorCallback callback) {
            this.mVirtualSensorCallback = new VirtualSensorCallbackDelegate((Executor) Objects.requireNonNull(executor), (VirtualSensorCallback) Objects.requireNonNull(callback));
            return this;
        }

        public Builder setAudioPlaybackSessionId(int playbackSessionId) {
            if (playbackSessionId < 0) {
                throw new IllegalArgumentException("Invalid playback audio session id");
            }
            this.mAudioPlaybackSessionId = playbackSessionId;
            return this;
        }

        public Builder setAudioRecordingSessionId(int recordingSessionId) {
            if (recordingSessionId < 0) {
                throw new IllegalArgumentException("Invalid recording audio session id");
            }
            this.mAudioRecordingSessionId = recordingSessionId;
            return this;
        }

        public VirtualDeviceParams build() {
            if (!this.mVirtualSensorConfigs.isEmpty()) {
                if (this.mDevicePolicies.get(0, 0) != 1) {
                    throw new IllegalArgumentException("DEVICE_POLICY_CUSTOM for POLICY_TYPE_SENSORS is required for creating virtual sensors.");
                }
                if (this.mVirtualSensorCallback == null) {
                    throw new IllegalArgumentException("VirtualSensorCallback is required for creating virtual sensors.");
                }
            }
            if ((this.mAudioPlaybackSessionId != 0 || this.mAudioRecordingSessionId != 0) && this.mDevicePolicies.get(1, 0) != 1) {
                throw new IllegalArgumentException("DEVICE_POLICY_CUSTOM for POLICY_TYPE_AUDIO is required for configuration of device-specific audio session ids.");
            }
            SparseArray<Set<String>> sensorNameByType = new SparseArray<>();
            for (int i = 0; i < this.mVirtualSensorConfigs.size(); i++) {
                VirtualSensorConfig config = this.mVirtualSensorConfigs.get(i);
                Set<String> sensorNames = sensorNameByType.get(config.getType(), new ArraySet<>());
                if (!sensorNames.add(config.getName())) {
                    throw new IllegalArgumentException("Sensor names must be unique for a particular sensor type.");
                }
                sensorNameByType.put(config.getType(), sensorNames);
            }
            return new VirtualDeviceParams(this.mLockState, this.mUsersWithMatchingAccounts, this.mAllowedCrossTaskNavigations, this.mBlockedCrossTaskNavigations, this.mDefaultNavigationPolicy, this.mAllowedActivities, this.mBlockedActivities, this.mDefaultActivityPolicy, this.mName, this.mDevicePolicies, this.mVirtualSensorConfigs, this.mVirtualSensorCallback, this.mAudioPlaybackSessionId, this.mAudioRecordingSessionId);
        }
    }
}
