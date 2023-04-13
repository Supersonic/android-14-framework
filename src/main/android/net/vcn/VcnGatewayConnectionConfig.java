package android.net.vcn;

import android.net.ipsec.ike.IkeTunnelConnectionParams;
import android.net.vcn.VcnCellUnderlyingNetworkTemplate;
import android.net.vcn.VcnWifiUnderlyingNetworkTemplate;
import android.net.vcn.persistablebundleutils.TunnelConnectionParamsUtils;
import android.p008os.PersistableBundle;
import android.util.ArraySet;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.Preconditions;
import com.android.server.vcn.repackaged.util.PersistableBundleUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
/* loaded from: classes2.dex */
public final class VcnGatewayConnectionConfig {
    public static final Set<Integer> ALLOWED_CAPABILITIES;
    private static final Set<Integer> ALLOWED_GATEWAY_OPTIONS;
    private static final int DEFAULT_MAX_MTU = 1500;
    private static final long[] DEFAULT_RETRY_INTERVALS_MS;
    public static final List<VcnUnderlyingNetworkTemplate> DEFAULT_UNDERLYING_NETWORK_TEMPLATES;
    private static final String EXPOSED_CAPABILITIES_KEY = "mExposedCapabilities";
    private static final String GATEWAY_CONNECTION_NAME_KEY = "mGatewayConnectionName";
    private static final String GATEWAY_OPTIONS_KEY = "mGatewayOptions";
    private static final String MAX_MTU_KEY = "mMaxMtu";
    private static final int MAX_RETRY_INTERVAL_COUNT = 10;
    private static final long MINIMUM_REPEATING_RETRY_INTERVAL_MS;
    static final int MIN_MTU_V6 = 1280;
    public static final int MIN_UDP_PORT_4500_NAT_TIMEOUT_SECONDS = 120;
    private static final String MIN_UDP_PORT_4500_NAT_TIMEOUT_SECONDS_KEY = "mMinUdpPort4500NatTimeoutSeconds";
    public static final int MIN_UDP_PORT_4500_NAT_TIMEOUT_UNSET = -1;
    private static final String RETRY_INTERVAL_MS_KEY = "mRetryIntervalsMs";
    private static final String TUNNEL_CONNECTION_PARAMS_KEY = "mTunnelConnectionParams";
    public static final String UNDERLYING_NETWORK_TEMPLATES_KEY = "mUnderlyingNetworkTemplates";
    public static final int VCN_GATEWAY_OPTION_ENABLE_DATA_STALL_RECOVERY_WITH_MOBILITY = 0;
    private final SortedSet<Integer> mExposedCapabilities;
    private final String mGatewayConnectionName;
    private final Set<Integer> mGatewayOptions;
    private final int mMaxMtu;
    private final int mMinUdpPort4500NatTimeoutSeconds;
    private final long[] mRetryIntervalsMs;
    private IkeTunnelConnectionParams mTunnelConnectionParams;
    private final List<VcnUnderlyingNetworkTemplate> mUnderlyingNetworkTemplates;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface VcnGatewayOption {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface VcnSupportedCapability {
    }

    static {
        Set<Integer> allowedCaps = new ArraySet<>();
        allowedCaps.add(0);
        allowedCaps.add(1);
        allowedCaps.add(2);
        allowedCaps.add(3);
        allowedCaps.add(4);
        allowedCaps.add(5);
        allowedCaps.add(7);
        allowedCaps.add(8);
        allowedCaps.add(9);
        allowedCaps.add(10);
        allowedCaps.add(12);
        allowedCaps.add(23);
        ALLOWED_CAPABILITIES = Collections.unmodifiableSet(allowedCaps);
        ArraySet arraySet = new ArraySet();
        ALLOWED_GATEWAY_OPTIONS = arraySet;
        arraySet.add(0);
        MINIMUM_REPEATING_RETRY_INTERVAL_MS = TimeUnit.MINUTES.toMillis(15L);
        DEFAULT_RETRY_INTERVALS_MS = new long[]{TimeUnit.SECONDS.toMillis(1L), TimeUnit.SECONDS.toMillis(2L), TimeUnit.SECONDS.toMillis(5L), TimeUnit.SECONDS.toMillis(30L), TimeUnit.MINUTES.toMillis(1L), TimeUnit.MINUTES.toMillis(5L), TimeUnit.MINUTES.toMillis(15L)};
        ArrayList arrayList = new ArrayList();
        DEFAULT_UNDERLYING_NETWORK_TEMPLATES = arrayList;
        arrayList.add(new VcnCellUnderlyingNetworkTemplate.Builder().setOpportunistic(1).build());
        arrayList.add(new VcnWifiUnderlyingNetworkTemplate.Builder().build());
        arrayList.add(new VcnCellUnderlyingNetworkTemplate.Builder().build());
    }

    private VcnGatewayConnectionConfig(String gatewayConnectionName, IkeTunnelConnectionParams tunnelConnectionParams, Set<Integer> exposedCapabilities, List<VcnUnderlyingNetworkTemplate> underlyingNetworkTemplates, long[] retryIntervalsMs, int maxMtu, int minUdpPort4500NatTimeoutSeconds, Set<Integer> gatewayOptions) {
        this.mGatewayConnectionName = gatewayConnectionName;
        this.mTunnelConnectionParams = tunnelConnectionParams;
        this.mExposedCapabilities = new TreeSet(exposedCapabilities);
        this.mRetryIntervalsMs = retryIntervalsMs;
        this.mMaxMtu = maxMtu;
        this.mMinUdpPort4500NatTimeoutSeconds = minUdpPort4500NatTimeoutSeconds;
        this.mGatewayOptions = Collections.unmodifiableSet(new ArraySet(gatewayOptions));
        ArrayList arrayList = new ArrayList(underlyingNetworkTemplates);
        this.mUnderlyingNetworkTemplates = arrayList;
        if (arrayList.isEmpty()) {
            arrayList.addAll(DEFAULT_UNDERLYING_NETWORK_TEMPLATES);
        }
        validate();
    }

    public VcnGatewayConnectionConfig(PersistableBundle in) {
        PersistableBundle tunnelConnectionParamsBundle = in.getPersistableBundle(TUNNEL_CONNECTION_PARAMS_KEY);
        Objects.requireNonNull(tunnelConnectionParamsBundle, "tunnelConnectionParamsBundle was null");
        PersistableBundle exposedCapsBundle = in.getPersistableBundle(EXPOSED_CAPABILITIES_KEY);
        this.mGatewayConnectionName = in.getString(GATEWAY_CONNECTION_NAME_KEY);
        this.mTunnelConnectionParams = TunnelConnectionParamsUtils.fromPersistableBundle(tunnelConnectionParamsBundle);
        this.mExposedCapabilities = new TreeSet(PersistableBundleUtils.toList(exposedCapsBundle, PersistableBundleUtils.INTEGER_DESERIALIZER));
        PersistableBundle networkTemplatesBundle = in.getPersistableBundle(UNDERLYING_NETWORK_TEMPLATES_KEY);
        if (networkTemplatesBundle == null) {
            this.mUnderlyingNetworkTemplates = new ArrayList(DEFAULT_UNDERLYING_NETWORK_TEMPLATES);
        } else {
            this.mUnderlyingNetworkTemplates = PersistableBundleUtils.toList(networkTemplatesBundle, new PersistableBundleUtils.Deserializer() { // from class: android.net.vcn.VcnGatewayConnectionConfig$$ExternalSyntheticLambda1
                @Override // com.android.server.vcn.repackaged.util.PersistableBundleUtils.Deserializer
                public final Object fromPersistableBundle(PersistableBundle persistableBundle) {
                    return VcnUnderlyingNetworkTemplate.fromPersistableBundle(persistableBundle);
                }
            });
        }
        PersistableBundle gatewayOptionsBundle = in.getPersistableBundle(GATEWAY_OPTIONS_KEY);
        if (gatewayOptionsBundle == null) {
            this.mGatewayOptions = Collections.emptySet();
        } else {
            this.mGatewayOptions = new ArraySet(PersistableBundleUtils.toList(gatewayOptionsBundle, PersistableBundleUtils.INTEGER_DESERIALIZER));
        }
        this.mRetryIntervalsMs = in.getLongArray(RETRY_INTERVAL_MS_KEY);
        this.mMaxMtu = in.getInt(MAX_MTU_KEY);
        this.mMinUdpPort4500NatTimeoutSeconds = in.getInt(MIN_UDP_PORT_4500_NAT_TIMEOUT_SECONDS_KEY, -1);
        validate();
    }

    private void validate() {
        Objects.requireNonNull(this.mGatewayConnectionName, "gatewayConnectionName was null");
        Objects.requireNonNull(this.mTunnelConnectionParams, "tunnel connection parameter was null");
        SortedSet<Integer> sortedSet = this.mExposedCapabilities;
        boolean z = true;
        Preconditions.checkArgument((sortedSet == null || sortedSet.isEmpty()) ? false : true, "exposedCapsBundle was null or empty");
        for (Integer cap : getAllExposedCapabilities()) {
            checkValidCapability(cap.intValue());
        }
        validateNetworkTemplateList(this.mUnderlyingNetworkTemplates);
        Objects.requireNonNull(this.mRetryIntervalsMs, "retryIntervalsMs was null");
        validateRetryInterval(this.mRetryIntervalsMs);
        Preconditions.checkArgument(this.mMaxMtu >= 1280, "maxMtu must be at least IPv6 min MTU (1280)");
        int i = this.mMinUdpPort4500NatTimeoutSeconds;
        if (i != -1 && i < 120) {
            z = false;
        }
        Preconditions.checkArgument(z, "minUdpPort4500NatTimeoutSeconds must be at least 120s");
        for (Integer num : this.mGatewayOptions) {
            int option = num.intValue();
            validateGatewayOption(option);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void checkValidCapability(int capability) {
        Preconditions.checkArgument(ALLOWED_CAPABILITIES.contains(Integer.valueOf(capability)), "NetworkCapability " + capability + "out of range");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void validateRetryInterval(long[] retryIntervalsMs) {
        Preconditions.checkArgument(retryIntervalsMs != null && retryIntervalsMs.length > 0 && retryIntervalsMs.length <= 10, "retryIntervalsMs was null, empty or exceed max interval count");
        long repeatingInterval = retryIntervalsMs[retryIntervalsMs.length - 1];
        if (repeatingInterval < MINIMUM_REPEATING_RETRY_INTERVAL_MS) {
            throw new IllegalArgumentException("Repeating retry interval was too short, must be a minimum of 15 minutes: " + repeatingInterval);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void validateNetworkTemplateList(List<VcnUnderlyingNetworkTemplate> networkPriorityRules) {
        Objects.requireNonNull(networkPriorityRules, "networkPriorityRules is null");
        Set<VcnUnderlyingNetworkTemplate> existingRules = new ArraySet<>();
        for (VcnUnderlyingNetworkTemplate rule : networkPriorityRules) {
            Objects.requireNonNull(rule, "Found null value VcnUnderlyingNetworkTemplate");
            if (!existingRules.add(rule)) {
                throw new IllegalArgumentException("Found duplicate VcnUnderlyingNetworkTemplate");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void validateGatewayOption(int option) {
        if (!ALLOWED_GATEWAY_OPTIONS.contains(Integer.valueOf(option))) {
            throw new IllegalArgumentException("Invalid vcn gateway option: " + option);
        }
    }

    public String getGatewayConnectionName() {
        return this.mGatewayConnectionName;
    }

    public IkeTunnelConnectionParams getTunnelConnectionParams() {
        return this.mTunnelConnectionParams;
    }

    public int[] getExposedCapabilities() {
        return ArrayUtils.convertToIntArray(new ArrayList(this.mExposedCapabilities));
    }

    @Deprecated
    public Set<Integer> getAllExposedCapabilities() {
        return Collections.unmodifiableSet(this.mExposedCapabilities);
    }

    public List<VcnUnderlyingNetworkTemplate> getVcnUnderlyingNetworkPriorities() {
        return new ArrayList(this.mUnderlyingNetworkTemplates);
    }

    public long[] getRetryIntervalsMillis() {
        long[] jArr = this.mRetryIntervalsMs;
        return Arrays.copyOf(jArr, jArr.length);
    }

    public int getMaxMtu() {
        return this.mMaxMtu;
    }

    public int getMinUdpPort4500NatTimeoutSeconds() {
        return this.mMinUdpPort4500NatTimeoutSeconds;
    }

    public boolean hasGatewayOption(int option) {
        validateGatewayOption(option);
        return this.mGatewayOptions.contains(Integer.valueOf(option));
    }

    public PersistableBundle toPersistableBundle() {
        PersistableBundle result = new PersistableBundle();
        PersistableBundle tunnelConnectionParamsBundle = TunnelConnectionParamsUtils.toPersistableBundle(this.mTunnelConnectionParams);
        PersistableBundle exposedCapsBundle = PersistableBundleUtils.fromList(new ArrayList(this.mExposedCapabilities), PersistableBundleUtils.INTEGER_SERIALIZER);
        PersistableBundle networkTemplatesBundle = PersistableBundleUtils.fromList(this.mUnderlyingNetworkTemplates, new PersistableBundleUtils.Serializer() { // from class: android.net.vcn.VcnGatewayConnectionConfig$$ExternalSyntheticLambda0
            @Override // com.android.server.vcn.repackaged.util.PersistableBundleUtils.Serializer
            public final PersistableBundle toPersistableBundle(Object obj) {
                return ((VcnUnderlyingNetworkTemplate) obj).toPersistableBundle();
            }
        });
        PersistableBundle gatewayOptionsBundle = PersistableBundleUtils.fromList(new ArrayList(this.mGatewayOptions), PersistableBundleUtils.INTEGER_SERIALIZER);
        result.putString(GATEWAY_CONNECTION_NAME_KEY, this.mGatewayConnectionName);
        result.putPersistableBundle(TUNNEL_CONNECTION_PARAMS_KEY, tunnelConnectionParamsBundle);
        result.putPersistableBundle(EXPOSED_CAPABILITIES_KEY, exposedCapsBundle);
        result.putPersistableBundle(UNDERLYING_NETWORK_TEMPLATES_KEY, networkTemplatesBundle);
        result.putPersistableBundle(GATEWAY_OPTIONS_KEY, gatewayOptionsBundle);
        result.putLongArray(RETRY_INTERVAL_MS_KEY, this.mRetryIntervalsMs);
        result.putInt(MAX_MTU_KEY, this.mMaxMtu);
        result.putInt(MIN_UDP_PORT_4500_NAT_TIMEOUT_SECONDS_KEY, this.mMinUdpPort4500NatTimeoutSeconds);
        return result;
    }

    public int hashCode() {
        return Objects.hash(this.mGatewayConnectionName, this.mTunnelConnectionParams, this.mExposedCapabilities, this.mUnderlyingNetworkTemplates, Integer.valueOf(Arrays.hashCode(this.mRetryIntervalsMs)), Integer.valueOf(this.mMaxMtu), Integer.valueOf(this.mMinUdpPort4500NatTimeoutSeconds), this.mGatewayOptions);
    }

    public boolean equals(Object other) {
        if (other instanceof VcnGatewayConnectionConfig) {
            VcnGatewayConnectionConfig rhs = (VcnGatewayConnectionConfig) other;
            return this.mGatewayConnectionName.equals(rhs.mGatewayConnectionName) && this.mTunnelConnectionParams.equals(rhs.mTunnelConnectionParams) && this.mExposedCapabilities.equals(rhs.mExposedCapabilities) && this.mUnderlyingNetworkTemplates.equals(rhs.mUnderlyingNetworkTemplates) && Arrays.equals(this.mRetryIntervalsMs, rhs.mRetryIntervalsMs) && this.mMaxMtu == rhs.mMaxMtu && this.mMinUdpPort4500NatTimeoutSeconds == rhs.mMinUdpPort4500NatTimeoutSeconds && this.mGatewayOptions.equals(rhs.mGatewayOptions);
        }
        return false;
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private final String mGatewayConnectionName;
        private final IkeTunnelConnectionParams mTunnelConnectionParams;
        private final Set<Integer> mExposedCapabilities = new ArraySet();
        private final List<VcnUnderlyingNetworkTemplate> mUnderlyingNetworkTemplates = new ArrayList(VcnGatewayConnectionConfig.DEFAULT_UNDERLYING_NETWORK_TEMPLATES);
        private long[] mRetryIntervalsMs = VcnGatewayConnectionConfig.DEFAULT_RETRY_INTERVALS_MS;
        private int mMaxMtu = 1500;
        private int mMinUdpPort4500NatTimeoutSeconds = -1;
        private final Set<Integer> mGatewayOptions = new ArraySet();

        public Builder(String gatewayConnectionName, IkeTunnelConnectionParams tunnelConnectionParams) {
            Objects.requireNonNull(gatewayConnectionName, "gatewayConnectionName was null");
            Objects.requireNonNull(tunnelConnectionParams, "tunnelConnectionParams was null");
            if (!tunnelConnectionParams.getIkeSessionParams().hasIkeOption(2)) {
                throw new IllegalArgumentException("MOBIKE must be configured for the provided IkeSessionParams");
            }
            this.mGatewayConnectionName = gatewayConnectionName;
            this.mTunnelConnectionParams = tunnelConnectionParams;
        }

        public Builder addExposedCapability(int exposedCapability) {
            VcnGatewayConnectionConfig.checkValidCapability(exposedCapability);
            this.mExposedCapabilities.add(Integer.valueOf(exposedCapability));
            return this;
        }

        public Builder removeExposedCapability(int exposedCapability) {
            VcnGatewayConnectionConfig.checkValidCapability(exposedCapability);
            this.mExposedCapabilities.remove(Integer.valueOf(exposedCapability));
            return this;
        }

        public Builder setVcnUnderlyingNetworkPriorities(List<VcnUnderlyingNetworkTemplate> underlyingNetworkTemplates) {
            VcnGatewayConnectionConfig.validateNetworkTemplateList(underlyingNetworkTemplates);
            this.mUnderlyingNetworkTemplates.clear();
            if (underlyingNetworkTemplates.isEmpty()) {
                this.mUnderlyingNetworkTemplates.addAll(VcnGatewayConnectionConfig.DEFAULT_UNDERLYING_NETWORK_TEMPLATES);
            } else {
                this.mUnderlyingNetworkTemplates.addAll(underlyingNetworkTemplates);
            }
            return this;
        }

        public Builder setRetryIntervalsMillis(long[] retryIntervalsMs) {
            VcnGatewayConnectionConfig.validateRetryInterval(retryIntervalsMs);
            this.mRetryIntervalsMs = retryIntervalsMs;
            return this;
        }

        public Builder setMaxMtu(int maxMtu) {
            Preconditions.checkArgument(maxMtu >= 1280, "maxMtu must be at least IPv6 min MTU (1280)");
            this.mMaxMtu = maxMtu;
            return this;
        }

        public Builder setMinUdpPort4500NatTimeoutSeconds(int minUdpPort4500NatTimeoutSeconds) {
            Preconditions.checkArgument(minUdpPort4500NatTimeoutSeconds >= 120, "Timeout must be at least 120s");
            this.mMinUdpPort4500NatTimeoutSeconds = minUdpPort4500NatTimeoutSeconds;
            return this;
        }

        public Builder addGatewayOption(int option) {
            VcnGatewayConnectionConfig.validateGatewayOption(option);
            this.mGatewayOptions.add(Integer.valueOf(option));
            return this;
        }

        public Builder removeGatewayOption(int option) {
            VcnGatewayConnectionConfig.validateGatewayOption(option);
            this.mGatewayOptions.remove(Integer.valueOf(option));
            return this;
        }

        public VcnGatewayConnectionConfig build() {
            return new VcnGatewayConnectionConfig(this.mGatewayConnectionName, this.mTunnelConnectionParams, this.mExposedCapabilities, this.mUnderlyingNetworkTemplates, this.mRetryIntervalsMs, this.mMaxMtu, this.mMinUdpPort4500NatTimeoutSeconds, this.mGatewayOptions);
        }
    }
}
