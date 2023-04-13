package android.net.vcn.persistablebundleutils;

import android.net.InetAddresses;
import android.net.eap.EapSessionConfig;
import android.net.ipsec.ike.IkeSaProposal;
import android.net.ipsec.ike.IkeSessionParams;
import android.net.vcn.persistablebundleutils.IkeSessionParamsUtils;
import android.p008os.PersistableBundle;
import android.system.OsConstants;
import android.util.ArraySet;
import android.util.Log;
import com.android.server.vcn.repackaged.util.PersistableBundleUtils;
import java.net.InetAddress;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.ToIntFunction;
/* loaded from: classes2.dex */
public final class IkeSessionParamsUtils {
    private static final String CONFIG_REQUESTS_KEY = "CONFIG_REQUESTS_KEY";
    private static final String DPD_DELAY_SEC_KEY = "DPD_DELAY_SEC_KEY";
    private static final String ENCAP_TYPE_KEY = "ENCAP_TYPE_KEY";
    private static final String HARD_LIFETIME_SEC_KEY = "HARD_LIFETIME_SEC_KEY";
    private static final Set<Integer> IKE_OPTIONS;
    private static final String IKE_OPTIONS_KEY = "IKE_OPTIONS_KEY";
    public static final int IKE_OPTION_AUTOMATIC_ADDRESS_FAMILY_SELECTION = 6;
    public static final int IKE_OPTION_AUTOMATIC_NATT_KEEPALIVES = 7;
    private static final String IP_VERSION_KEY = "IP_VERSION_KEY";
    private static final String LOCAL_AUTH_KEY = "LOCAL_AUTH_KEY";
    private static final String LOCAL_ID_KEY = "LOCAL_ID_KEY";
    private static final String NATT_KEEPALIVE_DELAY_SEC_KEY = "NATT_KEEPALIVE_DELAY_SEC_KEY";
    private static final String REMOTE_AUTH_KEY = "REMOTE_AUTH_KEY";
    private static final String REMOTE_ID_KEY = "REMOTE_ID_KEY";
    private static final String RETRANS_TIMEOUTS_KEY = "RETRANS_TIMEOUTS_KEY";
    private static final String SA_PROPOSALS_KEY = "SA_PROPOSALS_KEY";
    private static final String SERVER_HOST_NAME_KEY = "SERVER_HOST_NAME_KEY";
    private static final String SOFT_LIFETIME_SEC_KEY = "SOFT_LIFETIME_SEC_KEY";
    private static final String TAG = IkeSessionParamsUtils.class.getSimpleName();

    static {
        ArraySet arraySet = new ArraySet();
        IKE_OPTIONS = arraySet;
        arraySet.add(0);
        arraySet.add(1);
        arraySet.add(2);
        arraySet.add(3);
        arraySet.add(4);
        arraySet.add(5);
        arraySet.add(6);
        arraySet.add(7);
        arraySet.add(8);
    }

    public static boolean isIkeOptionValid(int option) {
        try {
            new IkeSessionParams.Builder().addIkeOption(option);
            return true;
        } catch (IllegalArgumentException e) {
            Log.m112d(TAG, "Option not supported; discarding: " + option);
            return false;
        }
    }

    public static PersistableBundle toPersistableBundle(IkeSessionParams params) {
        if (params.getNetwork() != null || params.getIke3gppExtension() != null) {
            throw new IllegalStateException("Cannot convert a IkeSessionParams with a caller configured network or with 3GPP extension enabled");
        }
        PersistableBundle result = new PersistableBundle();
        result.putString(SERVER_HOST_NAME_KEY, params.getServerHostname());
        PersistableBundle saProposalBundle = PersistableBundleUtils.fromList(params.getSaProposals(), new PersistableBundleUtils.Serializer() { // from class: android.net.vcn.persistablebundleutils.IkeSessionParamsUtils$$ExternalSyntheticLambda0
            @Override // com.android.server.vcn.repackaged.util.PersistableBundleUtils.Serializer
            public final PersistableBundle toPersistableBundle(Object obj) {
                return IkeSaProposalUtils.toPersistableBundle((IkeSaProposal) obj);
            }
        });
        result.putPersistableBundle(SA_PROPOSALS_KEY, saProposalBundle);
        result.putPersistableBundle(LOCAL_ID_KEY, IkeIdentificationUtils.toPersistableBundle(params.getLocalIdentification()));
        result.putPersistableBundle(REMOTE_ID_KEY, IkeIdentificationUtils.toPersistableBundle(params.getRemoteIdentification()));
        result.putPersistableBundle(LOCAL_AUTH_KEY, AuthConfigUtils.toPersistableBundle(params.getLocalAuthConfig()));
        result.putPersistableBundle(REMOTE_AUTH_KEY, AuthConfigUtils.toPersistableBundle(params.getRemoteAuthConfig()));
        List<ConfigRequest> reqList = new ArrayList<>();
        for (IkeSessionParams.IkeConfigRequest req : params.getConfigurationRequests()) {
            reqList.add(new ConfigRequest(req));
        }
        PersistableBundle configReqListBundle = PersistableBundleUtils.fromList(reqList, new PersistableBundleUtils.Serializer() { // from class: android.net.vcn.persistablebundleutils.IkeSessionParamsUtils$$ExternalSyntheticLambda1
            @Override // com.android.server.vcn.repackaged.util.PersistableBundleUtils.Serializer
            public final PersistableBundle toPersistableBundle(Object obj) {
                return ((IkeSessionParamsUtils.ConfigRequest) obj).toPersistableBundle();
            }
        });
        result.putPersistableBundle(CONFIG_REQUESTS_KEY, configReqListBundle);
        result.putIntArray(RETRANS_TIMEOUTS_KEY, params.getRetransmissionTimeoutsMillis());
        result.putInt(HARD_LIFETIME_SEC_KEY, params.getHardLifetimeSeconds());
        result.putInt(SOFT_LIFETIME_SEC_KEY, params.getSoftLifetimeSeconds());
        result.putInt(DPD_DELAY_SEC_KEY, params.getDpdDelaySeconds());
        result.putInt(NATT_KEEPALIVE_DELAY_SEC_KEY, params.getNattKeepAliveDelaySeconds());
        result.putInt(IP_VERSION_KEY, params.getIpVersion());
        result.putInt(ENCAP_TYPE_KEY, params.getEncapType());
        List<Integer> enabledIkeOptions = new ArrayList<>();
        for (Integer num : IKE_OPTIONS) {
            int option = num.intValue();
            if (isIkeOptionValid(option) && params.hasIkeOption(option)) {
                enabledIkeOptions.add(Integer.valueOf(option));
            }
        }
        int[] optionArray = enabledIkeOptions.stream().mapToInt(new ToIntFunction() { // from class: android.net.vcn.persistablebundleutils.IkeSessionParamsUtils$$ExternalSyntheticLambda2
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                int intValue;
                intValue = ((Integer) obj).intValue();
                return intValue;
            }
        }).toArray();
        result.putIntArray(IKE_OPTIONS_KEY, optionArray);
        return result;
    }

    public static IkeSessionParams fromPersistableBundle(PersistableBundle in) {
        Objects.requireNonNull(in, "PersistableBundle is null");
        IkeSessionParams.Builder builder = new IkeSessionParams.Builder();
        builder.setServerHostname(in.getString(SERVER_HOST_NAME_KEY));
        PersistableBundle proposalBundle = in.getPersistableBundle(SA_PROPOSALS_KEY);
        Objects.requireNonNull(in, "SA Proposals was null");
        List<IkeSaProposal> saProposals = PersistableBundleUtils.toList(proposalBundle, new PersistableBundleUtils.Deserializer() { // from class: android.net.vcn.persistablebundleutils.IkeSessionParamsUtils$$ExternalSyntheticLambda3
            @Override // com.android.server.vcn.repackaged.util.PersistableBundleUtils.Deserializer
            public final Object fromPersistableBundle(PersistableBundle persistableBundle) {
                return IkeSaProposalUtils.fromPersistableBundle(persistableBundle);
            }
        });
        for (IkeSaProposal proposal : saProposals) {
            builder.addSaProposal(proposal);
        }
        builder.setLocalIdentification(IkeIdentificationUtils.fromPersistableBundle(in.getPersistableBundle(LOCAL_ID_KEY)));
        builder.setRemoteIdentification(IkeIdentificationUtils.fromPersistableBundle(in.getPersistableBundle(REMOTE_ID_KEY)));
        AuthConfigUtils.setBuilderByReadingPersistableBundle(in.getPersistableBundle(LOCAL_AUTH_KEY), in.getPersistableBundle(REMOTE_AUTH_KEY), builder);
        builder.setRetransmissionTimeoutsMillis(in.getIntArray(RETRANS_TIMEOUTS_KEY));
        builder.setLifetimeSeconds(in.getInt(HARD_LIFETIME_SEC_KEY), in.getInt(SOFT_LIFETIME_SEC_KEY));
        builder.setDpdDelaySeconds(in.getInt(DPD_DELAY_SEC_KEY));
        builder.setNattKeepAliveDelaySeconds(in.getInt(NATT_KEEPALIVE_DELAY_SEC_KEY));
        builder.setIpVersion(in.getInt(IP_VERSION_KEY));
        builder.setEncapType(in.getInt(ENCAP_TYPE_KEY));
        PersistableBundle configReqListBundle = in.getPersistableBundle(CONFIG_REQUESTS_KEY);
        Objects.requireNonNull(configReqListBundle, "Config request list was null");
        List<ConfigRequest> reqList = PersistableBundleUtils.toList(configReqListBundle, new PersistableBundleUtils.Deserializer() { // from class: android.net.vcn.persistablebundleutils.IkeSessionParamsUtils$$ExternalSyntheticLambda4
            @Override // com.android.server.vcn.repackaged.util.PersistableBundleUtils.Deserializer
            public final Object fromPersistableBundle(PersistableBundle persistableBundle) {
                return new IkeSessionParamsUtils.ConfigRequest(persistableBundle);
            }
        });
        for (ConfigRequest req : reqList) {
            switch (req.type) {
                case 1:
                    if (req.address == null) {
                        builder.addPcscfServerRequest(OsConstants.AF_INET);
                        break;
                    } else {
                        builder.addPcscfServerRequest(req.address);
                        break;
                    }
                case 2:
                    if (req.address == null) {
                        builder.addPcscfServerRequest(OsConstants.AF_INET6);
                        break;
                    } else {
                        builder.addPcscfServerRequest(req.address);
                        break;
                    }
                default:
                    throw new IllegalArgumentException("Unrecognized config request type: " + req.type);
            }
        }
        for (Integer num : IKE_OPTIONS) {
            int option = num.intValue();
            if (isIkeOptionValid(option)) {
                builder.removeIkeOption(option);
            }
        }
        int[] optionArray = in.getIntArray(IKE_OPTIONS_KEY);
        for (int option2 : optionArray) {
            if (isIkeOptionValid(option2)) {
                builder.addIkeOption(option2);
            }
        }
        return builder.build();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class AuthConfigUtils {
        private static final String AUTH_METHOD_KEY = "AUTH_METHOD_KEY";
        private static final int IKE_AUTH_METHOD_EAP = 3;
        private static final int IKE_AUTH_METHOD_PSK = 1;
        private static final int IKE_AUTH_METHOD_PUB_KEY_SIGNATURE = 2;

        private AuthConfigUtils() {
        }

        public static PersistableBundle toPersistableBundle(IkeSessionParams.IkeAuthConfig authConfig) {
            if (authConfig instanceof IkeSessionParams.IkeAuthPskConfig) {
                IkeSessionParams.IkeAuthPskConfig config = (IkeSessionParams.IkeAuthPskConfig) authConfig;
                return IkeAuthPskConfigUtils.toPersistableBundle(config, createPersistableBundle(1));
            } else if (authConfig instanceof IkeSessionParams.IkeAuthDigitalSignLocalConfig) {
                IkeSessionParams.IkeAuthDigitalSignLocalConfig config2 = (IkeSessionParams.IkeAuthDigitalSignLocalConfig) authConfig;
                return IkeAuthDigitalSignConfigUtils.toPersistableBundle(config2, createPersistableBundle(2));
            } else if (authConfig instanceof IkeSessionParams.IkeAuthDigitalSignRemoteConfig) {
                IkeSessionParams.IkeAuthDigitalSignRemoteConfig config3 = (IkeSessionParams.IkeAuthDigitalSignRemoteConfig) authConfig;
                return IkeAuthDigitalSignConfigUtils.toPersistableBundle(config3, createPersistableBundle(2));
            } else if (authConfig instanceof IkeSessionParams.IkeAuthEapConfig) {
                IkeSessionParams.IkeAuthEapConfig config4 = (IkeSessionParams.IkeAuthEapConfig) authConfig;
                return IkeAuthEapConfigUtils.toPersistableBundle(config4, createPersistableBundle(3));
            } else {
                throw new IllegalStateException("Invalid IkeAuthConfig subclass");
            }
        }

        private static PersistableBundle createPersistableBundle(int type) {
            PersistableBundle result = new PersistableBundle();
            result.putInt(AUTH_METHOD_KEY, type);
            return result;
        }

        public static void setBuilderByReadingPersistableBundle(PersistableBundle localAuthBundle, PersistableBundle remoteAuthBundle, IkeSessionParams.Builder builder) {
            Objects.requireNonNull(localAuthBundle, "localAuthBundle was null");
            Objects.requireNonNull(remoteAuthBundle, "remoteAuthBundle was null");
            int localMethodType = localAuthBundle.getInt(AUTH_METHOD_KEY);
            int remoteMethodType = remoteAuthBundle.getInt(AUTH_METHOD_KEY);
            switch (localMethodType) {
                case 1:
                    if (remoteMethodType != 1) {
                        throw new IllegalArgumentException("Expect remote auth method to be PSK based, but was " + remoteMethodType);
                    }
                    IkeAuthPskConfigUtils.setBuilderByReadingPersistableBundle(localAuthBundle, remoteAuthBundle, builder);
                    return;
                case 2:
                    if (remoteMethodType != 2) {
                        throw new IllegalArgumentException("Expect remote auth method to be digital signature based, but was " + remoteMethodType);
                    }
                    IkeAuthDigitalSignConfigUtils.setBuilderByReadingPersistableBundle(localAuthBundle, remoteAuthBundle, builder);
                    return;
                case 3:
                    if (remoteMethodType != 2) {
                        throw new IllegalArgumentException("When using EAP for local authentication, expect remote auth method to be digital signature based, but was " + remoteMethodType);
                    }
                    IkeAuthEapConfigUtils.setBuilderByReadingPersistableBundle(localAuthBundle, remoteAuthBundle, builder);
                    return;
                default:
                    throw new IllegalArgumentException("Invalid EAP method type " + localMethodType);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class IkeAuthPskConfigUtils {
        private static final String PSK_KEY = "PSK_KEY";

        private IkeAuthPskConfigUtils() {
        }

        public static PersistableBundle toPersistableBundle(IkeSessionParams.IkeAuthPskConfig config, PersistableBundle result) {
            result.putPersistableBundle(PSK_KEY, PersistableBundleUtils.fromByteArray(config.getPsk()));
            return result;
        }

        public static void setBuilderByReadingPersistableBundle(PersistableBundle localAuthBundle, PersistableBundle remoteAuthBundle, IkeSessionParams.Builder builder) {
            Objects.requireNonNull(localAuthBundle, "localAuthBundle was null");
            Objects.requireNonNull(remoteAuthBundle, "remoteAuthBundle was null");
            PersistableBundle localPskBundle = localAuthBundle.getPersistableBundle(PSK_KEY);
            PersistableBundle remotePskBundle = remoteAuthBundle.getPersistableBundle(PSK_KEY);
            Objects.requireNonNull(localAuthBundle, "Local PSK was null");
            Objects.requireNonNull(remoteAuthBundle, "Remote PSK was null");
            byte[] localPsk = PersistableBundleUtils.toByteArray(localPskBundle);
            byte[] remotePsk = PersistableBundleUtils.toByteArray(remotePskBundle);
            if (!Arrays.equals(localPsk, remotePsk)) {
                throw new IllegalArgumentException("Local PSK and remote PSK are different");
            }
            builder.setAuthPsk(localPsk);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class IkeAuthDigitalSignConfigUtils {
        private static final String END_CERT_KEY = "END_CERT_KEY";
        private static final String INTERMEDIATE_CERTS_KEY = "INTERMEDIATE_CERTS_KEY";
        private static final String PRIVATE_KEY_KEY = "PRIVATE_KEY_KEY";
        private static final String TRUST_CERT_KEY = "TRUST_CERT_KEY";

        private IkeAuthDigitalSignConfigUtils() {
        }

        public static PersistableBundle toPersistableBundle(IkeSessionParams.IkeAuthDigitalSignLocalConfig config, PersistableBundle result) {
            try {
                result.putPersistableBundle(END_CERT_KEY, PersistableBundleUtils.fromByteArray(config.getClientEndCertificate().getEncoded()));
                List<X509Certificate> certList = config.getIntermediateCertificates();
                List<byte[]> encodedCertList = new ArrayList<>(certList.size());
                for (X509Certificate cert : certList) {
                    encodedCertList.add(cert.getEncoded());
                }
                PersistableBundle certsBundle = PersistableBundleUtils.fromList(encodedCertList, new PersistableBundleUtils.Serializer() { // from class: android.net.vcn.persistablebundleutils.IkeSessionParamsUtils$IkeAuthDigitalSignConfigUtils$$ExternalSyntheticLambda1
                    @Override // com.android.server.vcn.repackaged.util.PersistableBundleUtils.Serializer
                    public final PersistableBundle toPersistableBundle(Object obj) {
                        return PersistableBundleUtils.fromByteArray((byte[]) obj);
                    }
                });
                result.putPersistableBundle(INTERMEDIATE_CERTS_KEY, certsBundle);
                result.putPersistableBundle(PRIVATE_KEY_KEY, PersistableBundleUtils.fromByteArray(config.getPrivateKey().getEncoded()));
                return result;
            } catch (CertificateEncodingException e) {
                throw new IllegalArgumentException("Fail to encode certificate");
            }
        }

        public static PersistableBundle toPersistableBundle(IkeSessionParams.IkeAuthDigitalSignRemoteConfig config, PersistableBundle result) {
            try {
                X509Certificate caCert = config.getRemoteCaCert();
                if (caCert != null) {
                    result.putPersistableBundle(TRUST_CERT_KEY, PersistableBundleUtils.fromByteArray(caCert.getEncoded()));
                }
                return result;
            } catch (CertificateEncodingException e) {
                throw new IllegalArgumentException("Fail to encode the certificate");
            }
        }

        public static void setBuilderByReadingPersistableBundle(PersistableBundle localAuthBundle, PersistableBundle remoteAuthBundle, IkeSessionParams.Builder builder) {
            Objects.requireNonNull(localAuthBundle, "localAuthBundle was null");
            Objects.requireNonNull(remoteAuthBundle, "remoteAuthBundle was null");
            PersistableBundle endCertBundle = localAuthBundle.getPersistableBundle(END_CERT_KEY);
            Objects.requireNonNull(endCertBundle, "End cert was null");
            byte[] encodedCert = PersistableBundleUtils.toByteArray(endCertBundle);
            X509Certificate endCert = CertUtils.certificateFromByteArray(encodedCert);
            PersistableBundle certsBundle = localAuthBundle.getPersistableBundle(INTERMEDIATE_CERTS_KEY);
            Objects.requireNonNull(certsBundle, "Intermediate certs was null");
            List<byte[]> encodedCertList = PersistableBundleUtils.toList(certsBundle, new PersistableBundleUtils.Deserializer() { // from class: android.net.vcn.persistablebundleutils.IkeSessionParamsUtils$IkeAuthDigitalSignConfigUtils$$ExternalSyntheticLambda0
                @Override // com.android.server.vcn.repackaged.util.PersistableBundleUtils.Deserializer
                public final Object fromPersistableBundle(PersistableBundle persistableBundle) {
                    return PersistableBundleUtils.toByteArray(persistableBundle);
                }
            });
            List<X509Certificate> certList = new ArrayList<>(encodedCertList.size());
            for (byte[] encoded : encodedCertList) {
                certList.add(CertUtils.certificateFromByteArray(encoded));
            }
            PersistableBundle privateKeyBundle = localAuthBundle.getPersistableBundle(PRIVATE_KEY_KEY);
            Objects.requireNonNull(privateKeyBundle, "PrivateKey bundle was null");
            PrivateKey privateKey = CertUtils.privateKeyFromByteArray(PersistableBundleUtils.toByteArray(privateKeyBundle));
            PersistableBundle trustCertBundle = remoteAuthBundle.getPersistableBundle(TRUST_CERT_KEY);
            X509Certificate caCert = null;
            if (trustCertBundle != null) {
                byte[] encodedCaCert = PersistableBundleUtils.toByteArray(trustCertBundle);
                caCert = CertUtils.certificateFromByteArray(encodedCaCert);
            }
            builder.setAuthDigitalSignature(caCert, endCert, certList, privateKey);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class IkeAuthEapConfigUtils {
        private static final String EAP_CONFIG_KEY = "EAP_CONFIG_KEY";

        private IkeAuthEapConfigUtils() {
        }

        public static PersistableBundle toPersistableBundle(IkeSessionParams.IkeAuthEapConfig config, PersistableBundle result) {
            result.putPersistableBundle(EAP_CONFIG_KEY, EapSessionConfigUtils.toPersistableBundle(config.getEapConfig()));
            return result;
        }

        public static void setBuilderByReadingPersistableBundle(PersistableBundle localAuthBundle, PersistableBundle remoteAuthBundle, IkeSessionParams.Builder builder) {
            PersistableBundle eapBundle = localAuthBundle.getPersistableBundle(EAP_CONFIG_KEY);
            Objects.requireNonNull(eapBundle, "EAP Config was null");
            EapSessionConfig eapConfig = EapSessionConfigUtils.fromPersistableBundle(eapBundle);
            PersistableBundle trustCertBundle = remoteAuthBundle.getPersistableBundle("TRUST_CERT_KEY");
            X509Certificate serverCaCert = null;
            if (trustCertBundle != null) {
                byte[] encodedCaCert = PersistableBundleUtils.toByteArray(trustCertBundle);
                serverCaCert = CertUtils.certificateFromByteArray(encodedCaCert);
            }
            builder.setAuthEap(serverCaCert, eapConfig);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class ConfigRequest {
        private static final String ADDRESS_KEY = "address";
        private static final int IPV4_P_CSCF_ADDRESS = 1;
        private static final int IPV6_P_CSCF_ADDRESS = 2;
        private static final String TYPE_KEY = "type";
        public final InetAddress address;
        public final int type;

        ConfigRequest(IkeSessionParams.IkeConfigRequest config) {
            if (config instanceof IkeSessionParams.ConfigRequestIpv4PcscfServer) {
                this.type = 1;
                this.address = ((IkeSessionParams.ConfigRequestIpv4PcscfServer) config).getAddress();
            } else if (config instanceof IkeSessionParams.ConfigRequestIpv6PcscfServer) {
                this.type = 2;
                this.address = ((IkeSessionParams.ConfigRequestIpv6PcscfServer) config).getAddress();
            } else {
                throw new IllegalStateException("Unknown TunnelModeChildConfigRequest");
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public ConfigRequest(PersistableBundle in) {
            Objects.requireNonNull(in, "PersistableBundle was null");
            this.type = in.getInt("type");
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
            InetAddress inetAddress = this.address;
            if (inetAddress != null) {
                result.putString("address", inetAddress.getHostAddress());
            }
            return result;
        }
    }
}
