package com.android.internal.telephony.data;

import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.NetworkSpecifier;
import android.os.SystemClock;
import android.telephony.data.ApnSetting;
import android.telephony.data.DataProfile;
import android.telephony.data.TrafficDescriptor;
import com.android.internal.telephony.Phone;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
/* loaded from: classes.dex */
public class TelephonyNetworkRequest {
    public static final int CAPABILITY_ATTRIBUTE_APN_SETTING = 1;
    private static final Map<Integer, Integer> CAPABILITY_ATTRIBUTE_MAP = Map.ofEntries(new AbstractMap.SimpleImmutableEntry(0, 3), new AbstractMap.SimpleImmutableEntry(1, 3), new AbstractMap.SimpleImmutableEntry(2, 3), new AbstractMap.SimpleImmutableEntry(3, 3), new AbstractMap.SimpleImmutableEntry(4, 3), new AbstractMap.SimpleImmutableEntry(5, 7), new AbstractMap.SimpleImmutableEntry(9, 3), new AbstractMap.SimpleImmutableEntry(10, 3), new AbstractMap.SimpleImmutableEntry(12, 3), new AbstractMap.SimpleImmutableEntry(23, 3), new AbstractMap.SimpleImmutableEntry(29, 7), new AbstractMap.SimpleImmutableEntry(30, 3), new AbstractMap.SimpleImmutableEntry(31, 3), new AbstractMap.SimpleImmutableEntry(34, 4), new AbstractMap.SimpleImmutableEntry(35, 4));
    public static final int CAPABILITY_ATTRIBUTE_NONE = 0;
    public static final int CAPABILITY_ATTRIBUTE_TRAFFIC_DESCRIPTOR_DNN = 2;
    public static final int CAPABILITY_ATTRIBUTE_TRAFFIC_DESCRIPTOR_OS_APP_ID = 4;
    public static final int REQUEST_STATE_SATISFIED = 1;
    public static final int REQUEST_STATE_UNSATISFIED = 0;
    private DataNetwork mAttachedDataNetwork;
    private final int mCapabilitiesAttributes;
    private final long mCreatedTimeMillis;
    private final DataConfigManager mDataConfigManager;
    private DataEvaluation mEvaluation;
    private final NetworkRequest mNativeNetworkRequest;
    private final Phone mPhone;
    private int mPriority;
    private int mState;

    public TelephonyNetworkRequest(NetworkRequest networkRequest, Phone phone) {
        this.mPhone = phone;
        this.mNativeNetworkRequest = networkRequest;
        int i = 0;
        for (int i2 : networkRequest.getCapabilities()) {
            i |= CAPABILITY_ATTRIBUTE_MAP.getOrDefault(Integer.valueOf(i2), 0).intValue();
        }
        this.mCapabilitiesAttributes = i;
        this.mPriority = 0;
        this.mAttachedDataNetwork = null;
        this.mState = 0;
        this.mCreatedTimeMillis = SystemClock.elapsedRealtime();
        this.mDataConfigManager = phone.getDataNetworkController().getDataConfigManager();
        updatePriority();
    }

    public NetworkSpecifier getNetworkSpecifier() {
        return this.mNativeNetworkRequest.getNetworkSpecifier();
    }

    public int[] getCapabilities() {
        return this.mNativeNetworkRequest.getCapabilities();
    }

    public boolean hasCapability(int i) {
        return this.mNativeNetworkRequest.hasCapability(i);
    }

    public boolean canBeSatisfiedBy(NetworkCapabilities networkCapabilities) {
        return this.mNativeNetworkRequest.canBeSatisfiedBy(networkCapabilities);
    }

    public boolean hasAttribute(int i) {
        return (this.mCapabilitiesAttributes & i) == i;
    }

    public boolean canBeSatisfiedBy(DataProfile dataProfile) {
        if (!hasAttribute(4) || getOsAppId() == null || dataProfile.getTrafficDescriptor() == null || !Arrays.equals(getOsAppId().getBytes(), dataProfile.getTrafficDescriptor().getOsAppId())) {
            if ((hasAttribute(1) || hasAttribute(2)) && dataProfile.getApnSetting() != null) {
                List list = (List) Arrays.stream(getCapabilities()).boxed().map(new Function() { // from class: com.android.internal.telephony.data.TelephonyNetworkRequest$$ExternalSyntheticLambda0
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        return Integer.valueOf(DataUtils.networkCapabilityToApnType(((Integer) obj).intValue()));
                    }
                }).filter(new Predicate() { // from class: com.android.internal.telephony.data.TelephonyNetworkRequest$$ExternalSyntheticLambda1
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$canBeSatisfiedBy$0;
                        lambda$canBeSatisfiedBy$0 = TelephonyNetworkRequest.lambda$canBeSatisfiedBy$0((Integer) obj);
                        return lambda$canBeSatisfiedBy$0;
                    }
                }).collect(Collectors.toList());
                if (list.contains(16384)) {
                    list.remove((Object) 17);
                }
                Stream stream = list.stream();
                final ApnSetting apnSetting = dataProfile.getApnSetting();
                Objects.requireNonNull(apnSetting);
                return stream.allMatch(new Predicate() { // from class: com.android.internal.telephony.data.TelephonyNetworkRequest$$ExternalSyntheticLambda2
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        return apnSetting.canHandleType(((Integer) obj).intValue());
                    }
                });
            }
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$canBeSatisfiedBy$0(Integer num) {
        return num.intValue() != 0;
    }

    public int getPriority() {
        return this.mPriority;
    }

    public void updatePriority() {
        IntStream stream = Arrays.stream(this.mNativeNetworkRequest.getCapabilities());
        final DataConfigManager dataConfigManager = this.mDataConfigManager;
        Objects.requireNonNull(dataConfigManager);
        this.mPriority = stream.map(new IntUnaryOperator() { // from class: com.android.internal.telephony.data.TelephonyNetworkRequest$$ExternalSyntheticLambda3
            @Override // java.util.function.IntUnaryOperator
            public final int applyAsInt(int i) {
                return DataConfigManager.this.getNetworkCapabilityPriority(i);
            }
        }).max().orElse(0);
    }

    public int getApnTypeNetworkCapability() {
        if (hasAttribute(1)) {
            Stream<Integer> filter = Arrays.stream(getCapabilities()).boxed().filter(new Predicate() { // from class: com.android.internal.telephony.data.TelephonyNetworkRequest$$ExternalSyntheticLambda5
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$getApnTypeNetworkCapability$1;
                    lambda$getApnTypeNetworkCapability$1 = TelephonyNetworkRequest.lambda$getApnTypeNetworkCapability$1((Integer) obj);
                    return lambda$getApnTypeNetworkCapability$1;
                }
            });
            DataConfigManager dataConfigManager = this.mDataConfigManager;
            Objects.requireNonNull(dataConfigManager);
            return filter.max(Comparator.comparingInt(new DataNetwork$$ExternalSyntheticLambda5(dataConfigManager))).orElse(-1).intValue();
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getApnTypeNetworkCapability$1(Integer num) {
        return DataUtils.networkCapabilityToApnType(num.intValue()) != 0;
    }

    public NetworkRequest getNativeNetworkRequest() {
        return this.mNativeNetworkRequest;
    }

    public void setAttachedNetwork(DataNetwork dataNetwork) {
        this.mAttachedDataNetwork = dataNetwork;
    }

    public DataNetwork getAttachedNetwork() {
        return this.mAttachedDataNetwork;
    }

    public void setState(int i) {
        this.mState = i;
    }

    public int getState() {
        return this.mState;
    }

    public void setEvaluation(DataEvaluation dataEvaluation) {
        this.mEvaluation = dataEvaluation;
    }

    public int getCapabilityDifferentiator() {
        if (hasCapability(29)) {
            int[] enterpriseIds = this.mNativeNetworkRequest.getEnterpriseIds();
            if (enterpriseIds.length > 0) {
                return enterpriseIds[0];
            }
        }
        return 0;
    }

    public boolean isMeteredRequest() {
        return this.mDataConfigManager.isAnyMeteredCapability(getCapabilities(), this.mPhone.getServiceState().getDataRoaming());
    }

    public TrafficDescriptor.OsAppId getOsAppId() {
        int intValue;
        if (hasAttribute(4) && (intValue = Arrays.stream(getCapabilities()).boxed().filter(new Predicate() { // from class: com.android.internal.telephony.data.TelephonyNetworkRequest$$ExternalSyntheticLambda4
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getOsAppId$2;
                lambda$getOsAppId$2 = TelephonyNetworkRequest.lambda$getOsAppId$2((Integer) obj);
                return lambda$getOsAppId$2;
            }
        }).findFirst().orElse(-1).intValue()) != -1) {
            int capabilityDifferentiator = getCapabilityDifferentiator();
            if (capabilityDifferentiator > 0) {
                return new TrafficDescriptor.OsAppId(TrafficDescriptor.OsAppId.ANDROID_OS_ID, DataUtils.networkCapabilityToString(intValue), capabilityDifferentiator);
            }
            return new TrafficDescriptor.OsAppId(TrafficDescriptor.OsAppId.ANDROID_OS_ID, DataUtils.networkCapabilityToString(intValue));
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getOsAppId$2(Integer num) {
        return (CAPABILITY_ATTRIBUTE_MAP.getOrDefault(num, 0).intValue() & 4) != 0;
    }

    private static String requestStateToString(int i) {
        if (i != 0) {
            if (i != 1) {
                return "UNKNOWN(" + i + ")";
            }
            return "SATISFIED";
        }
        return "UNSATISFIED";
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(this.mNativeNetworkRequest.toString());
        sb.append(", mPriority=");
        sb.append(this.mPriority);
        sb.append(", state=");
        sb.append(requestStateToString(this.mState));
        sb.append(", mAttachedDataNetwork=");
        DataNetwork dataNetwork = this.mAttachedDataNetwork;
        sb.append(dataNetwork != null ? dataNetwork.name() : null);
        sb.append(", isMetered=");
        sb.append(isMeteredRequest());
        sb.append(", created time=");
        sb.append(DataUtils.elapsedTimeToString(this.mCreatedTimeMillis));
        sb.append(", evaluation result=");
        sb.append(this.mEvaluation);
        sb.append("]");
        return sb.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return this.mNativeNetworkRequest.equals(((TelephonyNetworkRequest) obj).mNativeNetworkRequest);
    }

    public int hashCode() {
        return this.mNativeNetworkRequest.hashCode();
    }
}
