package android.net.vcn;

import android.content.Context;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.PersistableBundle;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.util.Preconditions;
import com.android.server.vcn.repackaged.util.PersistableBundleUtils;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes2.dex */
public final class VcnConfig implements Parcelable {
    private static final Set<Integer> ALLOWED_TRANSPORTS;
    public static final Parcelable.Creator<VcnConfig> CREATOR;
    private static final String GATEWAY_CONNECTION_CONFIGS_KEY = "mGatewayConnectionConfigs";
    private static final String IS_TEST_MODE_PROFILE_KEY = "mIsTestModeProfile";
    private static final String PACKAGE_NAME_KEY = "mPackageName";
    private static final Set<Integer> RESTRICTED_TRANSPORTS_DEFAULT;
    private static final String RESTRICTED_TRANSPORTS_KEY = "mRestrictedTransports";
    private static final String TAG = VcnConfig.class.getSimpleName();
    private final Set<VcnGatewayConnectionConfig> mGatewayConnectionConfigs;
    private final boolean mIsTestModeProfile;
    private final String mPackageName;
    private final Set<Integer> mRestrictedTransports;

    @Target({ElementType.TYPE_USE})
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface VcnUnderlyingNetworkTransport {
    }

    static {
        ArraySet arraySet = new ArraySet();
        ALLOWED_TRANSPORTS = arraySet;
        arraySet.add(1);
        arraySet.add(0);
        RESTRICTED_TRANSPORTS_DEFAULT = Collections.singleton(1);
        CREATOR = new Parcelable.Creator<VcnConfig>() { // from class: android.net.vcn.VcnConfig.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public VcnConfig createFromParcel(Parcel in) {
                return new VcnConfig((PersistableBundle) in.readParcelable(null, PersistableBundle.class));
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public VcnConfig[] newArray(int size) {
                return new VcnConfig[size];
            }
        };
    }

    private VcnConfig(String packageName, Set<VcnGatewayConnectionConfig> gatewayConnectionConfigs, Set<Integer> restrictedTransports, boolean isTestModeProfile) {
        this.mPackageName = packageName;
        this.mGatewayConnectionConfigs = Collections.unmodifiableSet(new ArraySet(gatewayConnectionConfigs));
        this.mRestrictedTransports = Collections.unmodifiableSet(new ArraySet(restrictedTransports));
        this.mIsTestModeProfile = isTestModeProfile;
        validate();
    }

    public VcnConfig(PersistableBundle in) {
        this.mPackageName = in.getString(PACKAGE_NAME_KEY);
        PersistableBundle gatewayConnectionConfigsBundle = in.getPersistableBundle(GATEWAY_CONNECTION_CONFIGS_KEY);
        this.mGatewayConnectionConfigs = new ArraySet(PersistableBundleUtils.toList(gatewayConnectionConfigsBundle, new PersistableBundleUtils.Deserializer() { // from class: android.net.vcn.VcnConfig$$ExternalSyntheticLambda1
            @Override // com.android.server.vcn.repackaged.util.PersistableBundleUtils.Deserializer
            public final Object fromPersistableBundle(PersistableBundle persistableBundle) {
                return new VcnGatewayConnectionConfig(persistableBundle);
            }
        }));
        PersistableBundle restrictedTransportsBundle = in.getPersistableBundle(RESTRICTED_TRANSPORTS_KEY);
        if (restrictedTransportsBundle == null) {
            this.mRestrictedTransports = RESTRICTED_TRANSPORTS_DEFAULT;
        } else {
            this.mRestrictedTransports = new ArraySet(PersistableBundleUtils.toList(restrictedTransportsBundle, PersistableBundleUtils.INTEGER_DESERIALIZER));
        }
        this.mIsTestModeProfile = in.getBoolean(IS_TEST_MODE_PROFILE_KEY);
        validate();
    }

    private void validate() {
        Objects.requireNonNull(this.mPackageName, "packageName was null");
        Preconditions.checkCollectionNotEmpty(this.mGatewayConnectionConfigs, "gatewayConnectionConfigs was empty");
        Iterator<Integer> iterator = this.mRestrictedTransports.iterator();
        while (iterator.hasNext()) {
            int transport = iterator.next().intValue();
            if (!ALLOWED_TRANSPORTS.contains(Integer.valueOf(transport))) {
                iterator.remove();
                Log.m104w(TAG, "Found invalid transport " + transport + " which might be from a new version of VcnConfig");
            }
        }
    }

    public String getProvisioningPackageName() {
        return this.mPackageName;
    }

    public Set<VcnGatewayConnectionConfig> getGatewayConnectionConfigs() {
        return Collections.unmodifiableSet(this.mGatewayConnectionConfigs);
    }

    public Set<Integer> getRestrictedUnderlyingNetworkTransports() {
        return Collections.unmodifiableSet(this.mRestrictedTransports);
    }

    public boolean isTestModeProfile() {
        return this.mIsTestModeProfile;
    }

    public PersistableBundle toPersistableBundle() {
        PersistableBundle result = new PersistableBundle();
        result.putString(PACKAGE_NAME_KEY, this.mPackageName);
        PersistableBundle gatewayConnectionConfigsBundle = PersistableBundleUtils.fromList(new ArrayList(this.mGatewayConnectionConfigs), new PersistableBundleUtils.Serializer() { // from class: android.net.vcn.VcnConfig$$ExternalSyntheticLambda0
            @Override // com.android.server.vcn.repackaged.util.PersistableBundleUtils.Serializer
            public final PersistableBundle toPersistableBundle(Object obj) {
                return ((VcnGatewayConnectionConfig) obj).toPersistableBundle();
            }
        });
        result.putPersistableBundle(GATEWAY_CONNECTION_CONFIGS_KEY, gatewayConnectionConfigsBundle);
        PersistableBundle restrictedTransportsBundle = PersistableBundleUtils.fromList(new ArrayList(this.mRestrictedTransports), PersistableBundleUtils.INTEGER_SERIALIZER);
        result.putPersistableBundle(RESTRICTED_TRANSPORTS_KEY, restrictedTransportsBundle);
        result.putBoolean(IS_TEST_MODE_PROFILE_KEY, this.mIsTestModeProfile);
        return result;
    }

    public int hashCode() {
        return Objects.hash(this.mPackageName, this.mGatewayConnectionConfigs, this.mRestrictedTransports, Boolean.valueOf(this.mIsTestModeProfile));
    }

    public boolean equals(Object other) {
        if (other instanceof VcnConfig) {
            VcnConfig rhs = (VcnConfig) other;
            return this.mPackageName.equals(rhs.mPackageName) && this.mGatewayConnectionConfigs.equals(rhs.mGatewayConnectionConfigs) && this.mRestrictedTransports.equals(rhs.mRestrictedTransports) && this.mIsTestModeProfile == rhs.mIsTestModeProfile;
        }
        return false;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(toPersistableBundle(), flags);
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private final Set<VcnGatewayConnectionConfig> mGatewayConnectionConfigs = new ArraySet();
        private boolean mIsTestModeProfile;
        private final String mPackageName;
        private final Set<Integer> mRestrictedTransports;

        public Builder(Context context) {
            ArraySet arraySet = new ArraySet();
            this.mRestrictedTransports = arraySet;
            this.mIsTestModeProfile = false;
            Objects.requireNonNull(context, "context was null");
            this.mPackageName = context.getOpPackageName();
            arraySet.addAll(VcnConfig.RESTRICTED_TRANSPORTS_DEFAULT);
        }

        public Builder addGatewayConnectionConfig(VcnGatewayConnectionConfig gatewayConnectionConfig) {
            Objects.requireNonNull(gatewayConnectionConfig, "gatewayConnectionConfig was null");
            for (VcnGatewayConnectionConfig vcnGatewayConnectionConfig : this.mGatewayConnectionConfigs) {
                if (vcnGatewayConnectionConfig.getGatewayConnectionName().equals(gatewayConnectionConfig.getGatewayConnectionName())) {
                    throw new IllegalArgumentException("GatewayConnection for specified name already exists");
                }
            }
            this.mGatewayConnectionConfigs.add(gatewayConnectionConfig);
            return this;
        }

        private void validateRestrictedTransportsOrThrow(Set<Integer> restrictedTransports) {
            Objects.requireNonNull(restrictedTransports, "transports was null");
            for (Integer num : restrictedTransports) {
                int transport = num.intValue();
                if (!VcnConfig.ALLOWED_TRANSPORTS.contains(Integer.valueOf(transport))) {
                    throw new IllegalArgumentException("Invalid transport " + transport);
                }
            }
        }

        public Builder setRestrictedUnderlyingNetworkTransports(Set<Integer> transports) {
            validateRestrictedTransportsOrThrow(transports);
            this.mRestrictedTransports.clear();
            this.mRestrictedTransports.addAll(transports);
            return this;
        }

        public Builder setIsTestModeProfile() {
            this.mIsTestModeProfile = true;
            return this;
        }

        public VcnConfig build() {
            return new VcnConfig(this.mPackageName, this.mGatewayConnectionConfigs, this.mRestrictedTransports, this.mIsTestModeProfile);
        }
    }
}
