package android.net.shared;

import android.net.InformationElementParcelable;
import android.net.Network;
import android.net.ProvisioningConfigurationParcelable;
import android.net.ScanResultInfoParcelable;
import android.net.StaticIpConfiguration;
import android.net.apf.ApfCapabilities;
import android.net.networkstack.aidl.dhcp.DhcpOption;
import android.net.shared.ProvisioningConfiguration;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Function;
/* loaded from: classes.dex */
public class ProvisioningConfiguration {
    private static final int DEFAULT_TIMEOUT_MS = 18000;
    public static final int IPV6_ADDR_GEN_MODE_EUI64 = 0;
    public static final int IPV6_ADDR_GEN_MODE_STABLE_PRIVACY = 2;
    private static final String TAG = "ProvisioningConfiguration";
    public static final int VERSION_ADDED_PROVISIONING_ENUM = 12;
    public ApfCapabilities mApfCapabilities;
    public List<DhcpOption> mDhcpOptions;
    public String mDisplayName;
    public boolean mEnablePreconnection;
    public int mIPv4ProvisioningMode;
    public int mIPv6AddrGenMode;
    public int mIPv6ProvisioningMode;
    public InitialConfiguration mInitialConfig;
    public Layer2Information mLayer2Info;
    public Network mNetwork;
    public int mProvisioningTimeoutMs;
    public int mRequestedPreDhcpActionMs;
    public ScanResultInfo mScanResultInfo;
    public StaticIpConfiguration mStaticIpConfig;
    public boolean mUniqueEui64AddressesOnly;
    public boolean mUsingIpReachabilityMonitor;
    public boolean mUsingMultinetworkPolicyTracker;

    @VisibleForTesting
    public static String ipv4ProvisioningModeToString(int i) {
        return i != 0 ? i != 1 ? i != 2 ? "unknown" : "dhcp" : "static" : "disabled";
    }

    @VisibleForTesting
    public static String ipv6ProvisioningModeToString(int i) {
        return i != 0 ? i != 1 ? i != 2 ? "unknown" : "link-local" : "slaac" : "disabled";
    }

    /* loaded from: classes.dex */
    public static class Builder {
        protected ProvisioningConfiguration mConfig = new ProvisioningConfiguration();

        public Builder withoutIPv4() {
            this.mConfig.mIPv4ProvisioningMode = 0;
            return this;
        }

        public Builder withoutIPv6() {
            this.mConfig.mIPv6ProvisioningMode = 0;
            return this;
        }

        public Builder withoutMultinetworkPolicyTracker() {
            this.mConfig.mUsingMultinetworkPolicyTracker = false;
            return this;
        }

        public Builder withoutIpReachabilityMonitor() {
            this.mConfig.mUsingIpReachabilityMonitor = false;
            return this;
        }

        public Builder withPreDhcpAction() {
            this.mConfig.mRequestedPreDhcpActionMs = ProvisioningConfiguration.DEFAULT_TIMEOUT_MS;
            return this;
        }

        public Builder withPreDhcpAction(int i) {
            this.mConfig.mRequestedPreDhcpActionMs = i;
            return this;
        }

        public Builder withPreconnection() {
            this.mConfig.mEnablePreconnection = true;
            return this;
        }

        public Builder withInitialConfiguration(InitialConfiguration initialConfiguration) {
            this.mConfig.mInitialConfig = initialConfiguration;
            return this;
        }

        public Builder withStaticConfiguration(StaticIpConfiguration staticIpConfiguration) {
            ProvisioningConfiguration provisioningConfiguration = this.mConfig;
            provisioningConfiguration.mIPv4ProvisioningMode = 1;
            provisioningConfiguration.mStaticIpConfig = staticIpConfiguration;
            return this;
        }

        public Builder withApfCapabilities(ApfCapabilities apfCapabilities) {
            this.mConfig.mApfCapabilities = apfCapabilities;
            return this;
        }

        public Builder withProvisioningTimeoutMs(int i) {
            this.mConfig.mProvisioningTimeoutMs = i;
            return this;
        }

        public Builder withRandomMacAddress() {
            this.mConfig.mIPv6AddrGenMode = 0;
            return this;
        }

        public Builder withStableMacAddress() {
            this.mConfig.mIPv6AddrGenMode = 2;
            return this;
        }

        public Builder withNetwork(Network network) {
            this.mConfig.mNetwork = network;
            return this;
        }

        public Builder withDisplayName(String str) {
            this.mConfig.mDisplayName = str;
            return this;
        }

        public Builder withScanResultInfo(ScanResultInfo scanResultInfo) {
            this.mConfig.mScanResultInfo = scanResultInfo;
            return this;
        }

        public Builder withLayer2Information(Layer2Information layer2Information) {
            this.mConfig.mLayer2Info = layer2Information;
            return this;
        }

        public Builder withDhcpOptions(List<DhcpOption> list) {
            this.mConfig.mDhcpOptions = list;
            return this;
        }

        public Builder withIpv6LinkLocalOnly() {
            this.mConfig.mIPv6ProvisioningMode = 2;
            return this;
        }

        public Builder withUniqueEui64AddressesOnly() {
            this.mConfig.mUniqueEui64AddressesOnly = true;
            return this;
        }

        public ProvisioningConfiguration build() {
            ProvisioningConfiguration provisioningConfiguration = this.mConfig;
            if (provisioningConfiguration.mIPv6ProvisioningMode == 2 && provisioningConfiguration.mIPv4ProvisioningMode != 0) {
                throw new IllegalArgumentException("IPv4 must be disabled in IPv6 link-localonly mode.");
            }
            return new ProvisioningConfiguration(provisioningConfiguration);
        }
    }

    /* loaded from: classes.dex */
    public static class ScanResultInfo {
        private final String mBssid;
        private final List<InformationElement> mInformationElements;
        private final String mSsid;

        /* loaded from: classes.dex */
        public static class InformationElement {
            private final int mId;
            private final byte[] mPayload;

            public InformationElement(int i, ByteBuffer byteBuffer) {
                this.mId = i;
                this.mPayload = ScanResultInfo.convertToByteArray(byteBuffer.asReadOnlyBuffer());
            }

            public int getId() {
                return this.mId;
            }

            public ByteBuffer getPayload() {
                return ByteBuffer.wrap(this.mPayload).asReadOnlyBuffer();
            }

            public boolean equals(Object obj) {
                if (obj == this) {
                    return true;
                }
                if (obj instanceof InformationElement) {
                    InformationElement informationElement = (InformationElement) obj;
                    return this.mId == informationElement.mId && Arrays.equals(this.mPayload, informationElement.mPayload);
                }
                return false;
            }

            public int hashCode() {
                return Objects.hash(Integer.valueOf(this.mId), Integer.valueOf(Arrays.hashCode(this.mPayload)));
            }

            public String toString() {
                return "ID: " + this.mId + ", " + Arrays.toString(this.mPayload);
            }

            public InformationElementParcelable toStableParcelable() {
                InformationElementParcelable informationElementParcelable = new InformationElementParcelable();
                informationElementParcelable.f14id = this.mId;
                byte[] bArr = this.mPayload;
                informationElementParcelable.payload = bArr != null ? (byte[]) bArr.clone() : null;
                return informationElementParcelable;
            }

            public static InformationElement fromStableParcelable(InformationElementParcelable informationElementParcelable) {
                if (informationElementParcelable == null) {
                    return null;
                }
                return new InformationElement(informationElementParcelable.f14id, ByteBuffer.wrap((byte[]) informationElementParcelable.payload.clone()).asReadOnlyBuffer());
            }
        }

        public ScanResultInfo(String str, String str2, List<InformationElement> list) {
            Objects.requireNonNull(str, "ssid must not be null.");
            Objects.requireNonNull(str2, "bssid must not be null.");
            this.mSsid = str;
            this.mBssid = str2;
            this.mInformationElements = Collections.unmodifiableList(new ArrayList(list));
        }

        public String getSsid() {
            return this.mSsid;
        }

        public String getBssid() {
            return this.mBssid;
        }

        public List<InformationElement> getInformationElements() {
            return this.mInformationElements;
        }

        public String toString() {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("SSID: ");
            stringBuffer.append(this.mSsid);
            stringBuffer.append(", BSSID: ");
            stringBuffer.append(this.mBssid);
            stringBuffer.append(", Information Elements: {");
            for (InformationElement informationElement : this.mInformationElements) {
                stringBuffer.append("[");
                stringBuffer.append(informationElement.toString());
                stringBuffer.append("]");
            }
            stringBuffer.append("}");
            return stringBuffer.toString();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof ScanResultInfo) {
                ScanResultInfo scanResultInfo = (ScanResultInfo) obj;
                return Objects.equals(this.mSsid, scanResultInfo.mSsid) && Objects.equals(this.mBssid, scanResultInfo.mBssid) && this.mInformationElements.equals(scanResultInfo.mInformationElements);
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.mSsid, this.mBssid, this.mInformationElements);
        }

        public ScanResultInfoParcelable toStableParcelable() {
            ScanResultInfoParcelable scanResultInfoParcelable = new ScanResultInfoParcelable();
            scanResultInfoParcelable.ssid = this.mSsid;
            scanResultInfoParcelable.bssid = this.mBssid;
            scanResultInfoParcelable.informationElements = (InformationElementParcelable[]) ParcelableUtil.toParcelableArray(this.mInformationElements, new Function() { // from class: android.net.shared.ProvisioningConfiguration$ScanResultInfo$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return ((ProvisioningConfiguration.ScanResultInfo.InformationElement) obj).toStableParcelable();
                }
            }, InformationElementParcelable.class);
            return scanResultInfoParcelable;
        }

        public static ScanResultInfo fromStableParcelable(ScanResultInfoParcelable scanResultInfoParcelable) {
            if (scanResultInfoParcelable == null) {
                return null;
            }
            ArrayList arrayList = new ArrayList();
            arrayList.addAll(ParcelableUtil.fromParcelableArray(scanResultInfoParcelable.informationElements, new Function() { // from class: android.net.shared.ProvisioningConfiguration$ScanResultInfo$$ExternalSyntheticLambda1
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return ProvisioningConfiguration.ScanResultInfo.InformationElement.fromStableParcelable((InformationElementParcelable) obj);
                }
            }));
            return new ScanResultInfo(scanResultInfoParcelable.ssid, scanResultInfoParcelable.bssid, arrayList);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static byte[] convertToByteArray(ByteBuffer byteBuffer) {
            byte[] bArr = new byte[byteBuffer.limit()];
            ByteBuffer asReadOnlyBuffer = byteBuffer.asReadOnlyBuffer();
            try {
                try {
                    asReadOnlyBuffer.position(0);
                    asReadOnlyBuffer.get(bArr);
                    return bArr;
                } catch (BufferUnderflowException unused) {
                    Log.wtf(ProvisioningConfiguration.TAG, "Buffer under flow exception should never happen.");
                    return bArr;
                }
            } catch (Throwable unused2) {
                return bArr;
            }
        }
    }

    public ProvisioningConfiguration() {
        this.mUniqueEui64AddressesOnly = false;
        this.mEnablePreconnection = false;
        this.mUsingMultinetworkPolicyTracker = true;
        this.mUsingIpReachabilityMonitor = true;
        this.mProvisioningTimeoutMs = DEFAULT_TIMEOUT_MS;
        this.mIPv6AddrGenMode = 2;
        this.mNetwork = null;
        this.mDisplayName = null;
        this.mIPv4ProvisioningMode = 2;
        this.mIPv6ProvisioningMode = 1;
    }

    public ProvisioningConfiguration(ProvisioningConfiguration provisioningConfiguration) {
        this.mUniqueEui64AddressesOnly = false;
        this.mEnablePreconnection = false;
        this.mUsingMultinetworkPolicyTracker = true;
        this.mUsingIpReachabilityMonitor = true;
        this.mProvisioningTimeoutMs = DEFAULT_TIMEOUT_MS;
        this.mIPv6AddrGenMode = 2;
        this.mNetwork = null;
        this.mDisplayName = null;
        this.mIPv4ProvisioningMode = 2;
        this.mIPv6ProvisioningMode = 1;
        this.mUniqueEui64AddressesOnly = provisioningConfiguration.mUniqueEui64AddressesOnly;
        this.mEnablePreconnection = provisioningConfiguration.mEnablePreconnection;
        this.mUsingMultinetworkPolicyTracker = provisioningConfiguration.mUsingMultinetworkPolicyTracker;
        this.mUsingIpReachabilityMonitor = provisioningConfiguration.mUsingIpReachabilityMonitor;
        this.mRequestedPreDhcpActionMs = provisioningConfiguration.mRequestedPreDhcpActionMs;
        this.mInitialConfig = InitialConfiguration.copy(provisioningConfiguration.mInitialConfig);
        this.mStaticIpConfig = provisioningConfiguration.mStaticIpConfig != null ? new StaticIpConfiguration(provisioningConfiguration.mStaticIpConfig) : null;
        this.mApfCapabilities = provisioningConfiguration.mApfCapabilities;
        this.mProvisioningTimeoutMs = provisioningConfiguration.mProvisioningTimeoutMs;
        this.mIPv6AddrGenMode = provisioningConfiguration.mIPv6AddrGenMode;
        this.mNetwork = provisioningConfiguration.mNetwork;
        this.mDisplayName = provisioningConfiguration.mDisplayName;
        this.mScanResultInfo = provisioningConfiguration.mScanResultInfo;
        this.mLayer2Info = provisioningConfiguration.mLayer2Info;
        this.mDhcpOptions = provisioningConfiguration.mDhcpOptions;
        this.mIPv4ProvisioningMode = provisioningConfiguration.mIPv4ProvisioningMode;
        this.mIPv6ProvisioningMode = provisioningConfiguration.mIPv6ProvisioningMode;
    }

    public ProvisioningConfigurationParcelable toStableParcelable() {
        ProvisioningConfigurationParcelable provisioningConfigurationParcelable = new ProvisioningConfigurationParcelable();
        int i = this.mIPv4ProvisioningMode;
        provisioningConfigurationParcelable.enableIPv4 = i != 0;
        provisioningConfigurationParcelable.ipv4ProvisioningMode = i;
        int i2 = this.mIPv6ProvisioningMode;
        provisioningConfigurationParcelable.enableIPv6 = i2 != 0;
        provisioningConfigurationParcelable.ipv6ProvisioningMode = i2;
        provisioningConfigurationParcelable.uniqueEui64AddressesOnly = this.mUniqueEui64AddressesOnly;
        provisioningConfigurationParcelable.enablePreconnection = this.mEnablePreconnection;
        provisioningConfigurationParcelable.usingMultinetworkPolicyTracker = this.mUsingMultinetworkPolicyTracker;
        provisioningConfigurationParcelable.usingIpReachabilityMonitor = this.mUsingIpReachabilityMonitor;
        provisioningConfigurationParcelable.requestedPreDhcpActionMs = this.mRequestedPreDhcpActionMs;
        InitialConfiguration initialConfiguration = this.mInitialConfig;
        provisioningConfigurationParcelable.initialConfig = initialConfiguration == null ? null : initialConfiguration.toStableParcelable();
        provisioningConfigurationParcelable.staticIpConfig = this.mStaticIpConfig == null ? null : new StaticIpConfiguration(this.mStaticIpConfig);
        provisioningConfigurationParcelable.apfCapabilities = this.mApfCapabilities;
        provisioningConfigurationParcelable.provisioningTimeoutMs = this.mProvisioningTimeoutMs;
        provisioningConfigurationParcelable.ipv6AddrGenMode = this.mIPv6AddrGenMode;
        provisioningConfigurationParcelable.network = this.mNetwork;
        provisioningConfigurationParcelable.displayName = this.mDisplayName;
        ScanResultInfo scanResultInfo = this.mScanResultInfo;
        provisioningConfigurationParcelable.scanResultInfo = scanResultInfo == null ? null : scanResultInfo.toStableParcelable();
        Layer2Information layer2Information = this.mLayer2Info;
        provisioningConfigurationParcelable.layer2Info = layer2Information == null ? null : layer2Information.toStableParcelable();
        provisioningConfigurationParcelable.options = this.mDhcpOptions != null ? new ArrayList(this.mDhcpOptions) : null;
        return provisioningConfigurationParcelable;
    }

    public static ProvisioningConfiguration fromStableParcelable(ProvisioningConfigurationParcelable provisioningConfigurationParcelable, int i) {
        if (provisioningConfigurationParcelable == null) {
            return null;
        }
        ProvisioningConfiguration provisioningConfiguration = new ProvisioningConfiguration();
        provisioningConfiguration.mUniqueEui64AddressesOnly = provisioningConfigurationParcelable.uniqueEui64AddressesOnly;
        provisioningConfiguration.mEnablePreconnection = provisioningConfigurationParcelable.enablePreconnection;
        provisioningConfiguration.mUsingMultinetworkPolicyTracker = provisioningConfigurationParcelable.usingMultinetworkPolicyTracker;
        provisioningConfiguration.mUsingIpReachabilityMonitor = provisioningConfigurationParcelable.usingIpReachabilityMonitor;
        provisioningConfiguration.mRequestedPreDhcpActionMs = provisioningConfigurationParcelable.requestedPreDhcpActionMs;
        provisioningConfiguration.mInitialConfig = InitialConfiguration.fromStableParcelable(provisioningConfigurationParcelable.initialConfig);
        provisioningConfiguration.mStaticIpConfig = provisioningConfigurationParcelable.staticIpConfig == null ? null : new StaticIpConfiguration(provisioningConfigurationParcelable.staticIpConfig);
        provisioningConfiguration.mApfCapabilities = provisioningConfigurationParcelable.apfCapabilities;
        provisioningConfiguration.mProvisioningTimeoutMs = provisioningConfigurationParcelable.provisioningTimeoutMs;
        provisioningConfiguration.mIPv6AddrGenMode = provisioningConfigurationParcelable.ipv6AddrGenMode;
        provisioningConfiguration.mNetwork = provisioningConfigurationParcelable.network;
        provisioningConfiguration.mDisplayName = provisioningConfigurationParcelable.displayName;
        provisioningConfiguration.mScanResultInfo = ScanResultInfo.fromStableParcelable(provisioningConfigurationParcelable.scanResultInfo);
        provisioningConfiguration.mLayer2Info = Layer2Information.fromStableParcelable(provisioningConfigurationParcelable.layer2Info);
        provisioningConfiguration.mDhcpOptions = provisioningConfigurationParcelable.options != null ? new ArrayList(provisioningConfigurationParcelable.options) : null;
        if (i < 12) {
            provisioningConfiguration.mIPv4ProvisioningMode = provisioningConfigurationParcelable.enableIPv4 ? 2 : 0;
            provisioningConfiguration.mIPv6ProvisioningMode = provisioningConfigurationParcelable.enableIPv6 ? 1 : 0;
        } else {
            provisioningConfiguration.mIPv4ProvisioningMode = provisioningConfigurationParcelable.ipv4ProvisioningMode;
            provisioningConfiguration.mIPv6ProvisioningMode = provisioningConfigurationParcelable.ipv6ProvisioningMode;
        }
        return provisioningConfiguration;
    }

    public String toString() {
        String ipv4ProvisioningModeToString = ipv4ProvisioningModeToString(this.mIPv4ProvisioningMode);
        String ipv6ProvisioningModeToString = ipv6ProvisioningModeToString(this.mIPv6ProvisioningMode);
        StringJoiner stringJoiner = new StringJoiner(", ", getClass().getSimpleName() + "{", "}");
        StringJoiner add = stringJoiner.add("mUniqueEui64AddressesOnly: " + this.mUniqueEui64AddressesOnly);
        StringJoiner add2 = add.add("mEnablePreconnection: " + this.mEnablePreconnection);
        StringJoiner add3 = add2.add("mUsingMultinetworkPolicyTracker: " + this.mUsingMultinetworkPolicyTracker);
        StringJoiner add4 = add3.add("mUsingIpReachabilityMonitor: " + this.mUsingIpReachabilityMonitor);
        StringJoiner add5 = add4.add("mRequestedPreDhcpActionMs: " + this.mRequestedPreDhcpActionMs);
        StringJoiner add6 = add5.add("mInitialConfig: " + this.mInitialConfig);
        StringJoiner add7 = add6.add("mStaticIpConfig: " + this.mStaticIpConfig);
        StringJoiner add8 = add7.add("mApfCapabilities: " + this.mApfCapabilities);
        StringJoiner add9 = add8.add("mProvisioningTimeoutMs: " + this.mProvisioningTimeoutMs);
        StringJoiner add10 = add9.add("mIPv6AddrGenMode: " + this.mIPv6AddrGenMode);
        StringJoiner add11 = add10.add("mNetwork: " + this.mNetwork);
        StringJoiner add12 = add11.add("mDisplayName: " + this.mDisplayName);
        StringJoiner add13 = add12.add("mScanResultInfo: " + this.mScanResultInfo);
        StringJoiner add14 = add13.add("mLayer2Info: " + this.mLayer2Info);
        StringJoiner add15 = add14.add("mDhcpOptions: " + this.mDhcpOptions);
        StringJoiner add16 = add15.add("mIPv4ProvisioningMode: " + ipv4ProvisioningModeToString);
        return add16.add("mIPv6ProvisioningMode: " + ipv6ProvisioningModeToString).toString();
    }

    private static boolean dhcpOptionEquals(DhcpOption dhcpOption, DhcpOption dhcpOption2) {
        if (dhcpOption == dhcpOption2) {
            return true;
        }
        if (dhcpOption == null || dhcpOption2 == null) {
            return false;
        }
        return dhcpOption.type == dhcpOption2.type && Arrays.equals(dhcpOption.value, dhcpOption2.value);
    }

    private static boolean dhcpOptionListEquals(List<DhcpOption> list, List<DhcpOption> list2) {
        if (list == list2) {
            return true;
        }
        if (list == null || list2 == null || list.size() != list2.size()) {
            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            if (!dhcpOptionEquals(list.get(i), list2.get(i))) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(Object obj) {
        if (obj instanceof ProvisioningConfiguration) {
            ProvisioningConfiguration provisioningConfiguration = (ProvisioningConfiguration) obj;
            return this.mUniqueEui64AddressesOnly == provisioningConfiguration.mUniqueEui64AddressesOnly && this.mEnablePreconnection == provisioningConfiguration.mEnablePreconnection && this.mUsingMultinetworkPolicyTracker == provisioningConfiguration.mUsingMultinetworkPolicyTracker && this.mUsingIpReachabilityMonitor == provisioningConfiguration.mUsingIpReachabilityMonitor && this.mRequestedPreDhcpActionMs == provisioningConfiguration.mRequestedPreDhcpActionMs && Objects.equals(this.mInitialConfig, provisioningConfiguration.mInitialConfig) && Objects.equals(this.mStaticIpConfig, provisioningConfiguration.mStaticIpConfig) && Objects.equals(this.mApfCapabilities, provisioningConfiguration.mApfCapabilities) && this.mProvisioningTimeoutMs == provisioningConfiguration.mProvisioningTimeoutMs && this.mIPv6AddrGenMode == provisioningConfiguration.mIPv6AddrGenMode && Objects.equals(this.mNetwork, provisioningConfiguration.mNetwork) && Objects.equals(this.mDisplayName, provisioningConfiguration.mDisplayName) && Objects.equals(this.mScanResultInfo, provisioningConfiguration.mScanResultInfo) && Objects.equals(this.mLayer2Info, provisioningConfiguration.mLayer2Info) && dhcpOptionListEquals(this.mDhcpOptions, provisioningConfiguration.mDhcpOptions) && this.mIPv4ProvisioningMode == provisioningConfiguration.mIPv4ProvisioningMode && this.mIPv6ProvisioningMode == provisioningConfiguration.mIPv6ProvisioningMode;
        }
        return false;
    }

    public boolean isValid() {
        InitialConfiguration initialConfiguration = this.mInitialConfig;
        return initialConfiguration == null || initialConfiguration.isValid();
    }
}
