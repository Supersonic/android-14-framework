package android.net.vcn.persistablebundleutils;

import android.net.InetAddresses;
import android.net.ipsec.ike.ChildSaProposal;
import android.net.ipsec.ike.IkeTrafficSelector;
import android.net.ipsec.ike.TunnelModeChildSessionParams;
import android.net.vcn.persistablebundleutils.TunnelModeChildSessionParamsUtils;
import android.p008os.PersistableBundle;
import android.system.OsConstants;
import android.util.Log;
import com.android.server.vcn.repackaged.util.PersistableBundleUtils;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class TunnelModeChildSessionParamsUtils {
    private static final String CONFIG_REQUESTS_KEY = "CONFIG_REQUESTS_KEY";
    private static final String HARD_LIFETIME_SEC_KEY = "HARD_LIFETIME_SEC_KEY";
    private static final String INBOUND_TS_KEY = "INBOUND_TS_KEY";
    private static final String OUTBOUND_TS_KEY = "OUTBOUND_TS_KEY";
    private static final String SA_PROPOSALS_KEY = "SA_PROPOSALS_KEY";
    private static final String SOFT_LIFETIME_SEC_KEY = "SOFT_LIFETIME_SEC_KEY";
    private static final String TAG = TunnelModeChildSessionParamsUtils.class.getSimpleName();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class ConfigRequest {
        private static final String IP6_PREFIX_LEN = "ip6PrefixLen";
        private static final int PREFIX_LEN_UNUSED = -1;
        private static final int TYPE_IPV4_ADDRESS = 1;
        private static final int TYPE_IPV4_DHCP = 5;
        private static final int TYPE_IPV4_DNS = 3;
        private static final int TYPE_IPV4_NETMASK = 6;
        private static final int TYPE_IPV6_ADDRESS = 2;
        private static final int TYPE_IPV6_DNS = 4;
        private static final String TYPE_KEY = "type";
        private static final String VALUE_KEY = "address";
        public final InetAddress address;
        public final int ip6PrefixLen;
        public final int type;

        ConfigRequest(TunnelModeChildSessionParams.TunnelModeChildConfigRequest config) {
            int prefixLen = -1;
            if (config instanceof TunnelModeChildSessionParams.ConfigRequestIpv4Address) {
                this.type = 1;
                this.address = ((TunnelModeChildSessionParams.ConfigRequestIpv4Address) config).getAddress();
            } else if (config instanceof TunnelModeChildSessionParams.ConfigRequestIpv6Address) {
                this.type = 2;
                Inet6Address address = ((TunnelModeChildSessionParams.ConfigRequestIpv6Address) config).getAddress();
                this.address = address;
                if (address != null) {
                    prefixLen = ((TunnelModeChildSessionParams.ConfigRequestIpv6Address) config).getPrefixLength();
                }
            } else if (config instanceof TunnelModeChildSessionParams.ConfigRequestIpv4DnsServer) {
                this.type = 3;
                this.address = null;
            } else if (config instanceof TunnelModeChildSessionParams.ConfigRequestIpv6DnsServer) {
                this.type = 4;
                this.address = null;
            } else if (config instanceof TunnelModeChildSessionParams.ConfigRequestIpv4DhcpServer) {
                this.type = 5;
                this.address = null;
            } else if (config instanceof TunnelModeChildSessionParams.ConfigRequestIpv4Netmask) {
                this.type = 6;
                this.address = null;
            } else {
                throw new IllegalStateException("Unknown TunnelModeChildConfigRequest");
            }
            this.ip6PrefixLen = prefixLen;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public ConfigRequest(PersistableBundle in) {
            Objects.requireNonNull(in, "PersistableBundle was null");
            this.type = in.getInt("type");
            this.ip6PrefixLen = in.getInt(IP6_PREFIX_LEN);
            String addressStr = in.getString("address");
            if (addressStr == null) {
                this.address = null;
            } else {
                this.address = InetAddresses.parseNumericAddress(addressStr);
            }
        }

        public PersistableBundle toPersistableBundle() {
            PersistableBundle result = new PersistableBundle();
            result.putInt("type", this.type);
            result.putInt(IP6_PREFIX_LEN, this.ip6PrefixLen);
            InetAddress inetAddress = this.address;
            if (inetAddress != null) {
                result.putString("address", inetAddress.getHostAddress());
            }
            return result;
        }
    }

    public static PersistableBundle toPersistableBundle(TunnelModeChildSessionParams params) {
        PersistableBundle result = new PersistableBundle();
        PersistableBundle saProposalBundle = PersistableBundleUtils.fromList(params.getSaProposals(), new PersistableBundleUtils.Serializer() { // from class: android.net.vcn.persistablebundleutils.TunnelModeChildSessionParamsUtils$$ExternalSyntheticLambda3
            @Override // com.android.server.vcn.repackaged.util.PersistableBundleUtils.Serializer
            public final PersistableBundle toPersistableBundle(Object obj) {
                return ChildSaProposalUtils.toPersistableBundle((ChildSaProposal) obj);
            }
        });
        result.putPersistableBundle(SA_PROPOSALS_KEY, saProposalBundle);
        PersistableBundle inTsBundle = PersistableBundleUtils.fromList(params.getInboundTrafficSelectors(), new PersistableBundleUtils.Serializer() { // from class: android.net.vcn.persistablebundleutils.TunnelModeChildSessionParamsUtils$$ExternalSyntheticLambda4
            @Override // com.android.server.vcn.repackaged.util.PersistableBundleUtils.Serializer
            public final PersistableBundle toPersistableBundle(Object obj) {
                return IkeTrafficSelectorUtils.toPersistableBundle((IkeTrafficSelector) obj);
            }
        });
        result.putPersistableBundle(INBOUND_TS_KEY, inTsBundle);
        PersistableBundle outTsBundle = PersistableBundleUtils.fromList(params.getOutboundTrafficSelectors(), new PersistableBundleUtils.Serializer() { // from class: android.net.vcn.persistablebundleutils.TunnelModeChildSessionParamsUtils$$ExternalSyntheticLambda4
            @Override // com.android.server.vcn.repackaged.util.PersistableBundleUtils.Serializer
            public final PersistableBundle toPersistableBundle(Object obj) {
                return IkeTrafficSelectorUtils.toPersistableBundle((IkeTrafficSelector) obj);
            }
        });
        result.putPersistableBundle(OUTBOUND_TS_KEY, outTsBundle);
        result.putInt(HARD_LIFETIME_SEC_KEY, params.getHardLifetimeSeconds());
        result.putInt(SOFT_LIFETIME_SEC_KEY, params.getSoftLifetimeSeconds());
        List<ConfigRequest> reqList = new ArrayList<>();
        for (TunnelModeChildSessionParams.TunnelModeChildConfigRequest req : params.getConfigurationRequests()) {
            reqList.add(new ConfigRequest(req));
        }
        PersistableBundle configReqListBundle = PersistableBundleUtils.fromList(reqList, new PersistableBundleUtils.Serializer() { // from class: android.net.vcn.persistablebundleutils.TunnelModeChildSessionParamsUtils$$ExternalSyntheticLambda5
            @Override // com.android.server.vcn.repackaged.util.PersistableBundleUtils.Serializer
            public final PersistableBundle toPersistableBundle(Object obj) {
                return ((TunnelModeChildSessionParamsUtils.ConfigRequest) obj).toPersistableBundle();
            }
        });
        result.putPersistableBundle(CONFIG_REQUESTS_KEY, configReqListBundle);
        return result;
    }

    private static List<IkeTrafficSelector> getTsFromPersistableBundle(PersistableBundle in, String key) {
        PersistableBundle tsBundle = in.getPersistableBundle(key);
        Objects.requireNonNull(tsBundle, "Value for key " + key + " was null");
        return PersistableBundleUtils.toList(tsBundle, new PersistableBundleUtils.Deserializer() { // from class: android.net.vcn.persistablebundleutils.TunnelModeChildSessionParamsUtils$$ExternalSyntheticLambda2
            @Override // com.android.server.vcn.repackaged.util.PersistableBundleUtils.Deserializer
            public final Object fromPersistableBundle(PersistableBundle persistableBundle) {
                return IkeTrafficSelectorUtils.fromPersistableBundle(persistableBundle);
            }
        });
    }

    public static TunnelModeChildSessionParams fromPersistableBundle(PersistableBundle in) {
        Objects.requireNonNull(in, "PersistableBundle was null");
        TunnelModeChildSessionParams.Builder builder = new TunnelModeChildSessionParams.Builder();
        PersistableBundle proposalBundle = in.getPersistableBundle(SA_PROPOSALS_KEY);
        Objects.requireNonNull(proposalBundle, "SA proposal was null");
        List<ChildSaProposal> proposals = PersistableBundleUtils.toList(proposalBundle, new PersistableBundleUtils.Deserializer() { // from class: android.net.vcn.persistablebundleutils.TunnelModeChildSessionParamsUtils$$ExternalSyntheticLambda0
            @Override // com.android.server.vcn.repackaged.util.PersistableBundleUtils.Deserializer
            public final Object fromPersistableBundle(PersistableBundle persistableBundle) {
                return ChildSaProposalUtils.fromPersistableBundle(persistableBundle);
            }
        });
        for (ChildSaProposal p : proposals) {
            builder.addSaProposal(p);
        }
        for (IkeTrafficSelector ts : getTsFromPersistableBundle(in, INBOUND_TS_KEY)) {
            builder.addInboundTrafficSelectors(ts);
        }
        for (IkeTrafficSelector ts2 : getTsFromPersistableBundle(in, OUTBOUND_TS_KEY)) {
            builder.addOutboundTrafficSelectors(ts2);
        }
        builder.setLifetimeSeconds(in.getInt(HARD_LIFETIME_SEC_KEY), in.getInt(SOFT_LIFETIME_SEC_KEY));
        PersistableBundle configReqListBundle = in.getPersistableBundle(CONFIG_REQUESTS_KEY);
        Objects.requireNonNull(configReqListBundle, "Config request list was null");
        List<ConfigRequest> reqList = PersistableBundleUtils.toList(configReqListBundle, new PersistableBundleUtils.Deserializer() { // from class: android.net.vcn.persistablebundleutils.TunnelModeChildSessionParamsUtils$$ExternalSyntheticLambda1
            @Override // com.android.server.vcn.repackaged.util.PersistableBundleUtils.Deserializer
            public final Object fromPersistableBundle(PersistableBundle persistableBundle) {
                return new TunnelModeChildSessionParamsUtils.ConfigRequest(persistableBundle);
            }
        });
        boolean hasIpv4AddressReq = false;
        boolean hasIpv4NetmaskReq = false;
        for (ConfigRequest req : reqList) {
            switch (req.type) {
                case 1:
                    hasIpv4AddressReq = true;
                    if (req.address == null) {
                        builder.addInternalAddressRequest(OsConstants.AF_INET);
                        break;
                    } else {
                        builder.addInternalAddressRequest((Inet4Address) req.address);
                        break;
                    }
                case 2:
                    if (req.address == null) {
                        builder.addInternalAddressRequest(OsConstants.AF_INET6);
                        break;
                    } else {
                        builder.addInternalAddressRequest((Inet6Address) req.address, req.ip6PrefixLen);
                        break;
                    }
                case 3:
                    if (req.address != null) {
                        Log.m104w(TAG, "Requesting a specific IPv4 DNS server is unsupported");
                    }
                    builder.addInternalDnsServerRequest(OsConstants.AF_INET);
                    break;
                case 4:
                    if (req.address != null) {
                        Log.m104w(TAG, "Requesting a specific IPv6 DNS server is unsupported");
                    }
                    builder.addInternalDnsServerRequest(OsConstants.AF_INET6);
                    break;
                case 5:
                    if (req.address != null) {
                        Log.m104w(TAG, "Requesting a specific IPv4 DHCP server is unsupported");
                    }
                    builder.addInternalDhcpServerRequest(OsConstants.AF_INET);
                    break;
                case 6:
                    hasIpv4NetmaskReq = true;
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized config request type: " + req.type);
            }
        }
        if (hasIpv4AddressReq != hasIpv4NetmaskReq) {
            Log.m104w(TAG, String.format("Expect IPv4 address request and IPv4 netmask request either both exist or both absent, but found hasIpv4AddressReq exists? %b, hasIpv4AddressReq exists? %b, ", Boolean.valueOf(hasIpv4AddressReq), Boolean.valueOf(hasIpv4NetmaskReq)));
        }
        return builder.build();
    }
}
