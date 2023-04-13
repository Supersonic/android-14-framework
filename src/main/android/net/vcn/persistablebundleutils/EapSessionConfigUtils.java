package android.net.vcn.persistablebundleutils;

import android.net.eap.EapSessionConfig;
import android.p008os.PersistableBundle;
import com.android.server.vcn.repackaged.util.PersistableBundleUtils;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class EapSessionConfigUtils {
    private static final String EAP_AKA_CONFIG_KEY = "EAP_AKA_CONFIG_KEY";
    private static final String EAP_AKA_PRIME_CONFIG_KEY = "EAP_AKA_PRIME_CONFIG_KEY";
    private static final String EAP_ID_KEY = "EAP_ID_KEY";
    private static final String EAP_MSCHAP_V2_CONFIG_KEY = "EAP_MSCHAP_V2_CONFIG_KEY";
    private static final String EAP_SIM_CONFIG_KEY = "EAP_SIM_CONFIG_KEY";
    private static final String EAP_TTLS_CONFIG_KEY = "EAP_TTLS_CONFIG_KEY";

    public static PersistableBundle toPersistableBundle(EapSessionConfig config) {
        PersistableBundle result = new PersistableBundle();
        result.putPersistableBundle(EAP_ID_KEY, PersistableBundleUtils.fromByteArray(config.getEapIdentity()));
        if (config.getEapSimConfig() != null) {
            result.putPersistableBundle(EAP_SIM_CONFIG_KEY, EapSimConfigUtils.toPersistableBundle(config.getEapSimConfig()));
        }
        if (config.getEapTtlsConfig() != null) {
            result.putPersistableBundle(EAP_TTLS_CONFIG_KEY, EapTtlsConfigUtils.toPersistableBundle(config.getEapTtlsConfig()));
        }
        if (config.getEapAkaConfig() != null) {
            result.putPersistableBundle(EAP_AKA_CONFIG_KEY, EapAkaConfigUtils.toPersistableBundle(config.getEapAkaConfig()));
        }
        if (config.getEapMsChapV2Config() != null) {
            result.putPersistableBundle(EAP_MSCHAP_V2_CONFIG_KEY, EapMsChapV2ConfigUtils.toPersistableBundle(config.getEapMsChapV2Config()));
        }
        if (config.getEapAkaPrimeConfig() != null) {
            result.putPersistableBundle(EAP_AKA_PRIME_CONFIG_KEY, EapAkaPrimeConfigUtils.toPersistableBundle(config.getEapAkaPrimeConfig()));
        }
        return result;
    }

    public static EapSessionConfig fromPersistableBundle(PersistableBundle in) {
        Objects.requireNonNull(in, "PersistableBundle was null");
        EapSessionConfig.Builder builder = new EapSessionConfig.Builder();
        PersistableBundle eapIdBundle = in.getPersistableBundle(EAP_ID_KEY);
        Objects.requireNonNull(eapIdBundle, "EAP ID was null");
        builder.setEapIdentity(PersistableBundleUtils.toByteArray(eapIdBundle));
        PersistableBundle simBundle = in.getPersistableBundle(EAP_SIM_CONFIG_KEY);
        if (simBundle != null) {
            EapSimConfigUtils.setBuilderByReadingPersistableBundle(simBundle, builder);
        }
        PersistableBundle ttlsBundle = in.getPersistableBundle(EAP_TTLS_CONFIG_KEY);
        if (ttlsBundle != null) {
            EapTtlsConfigUtils.setBuilderByReadingPersistableBundle(ttlsBundle, builder);
        }
        PersistableBundle akaBundle = in.getPersistableBundle(EAP_AKA_CONFIG_KEY);
        if (akaBundle != null) {
            EapAkaConfigUtils.setBuilderByReadingPersistableBundle(akaBundle, builder);
        }
        PersistableBundle msChapV2Bundle = in.getPersistableBundle(EAP_MSCHAP_V2_CONFIG_KEY);
        if (msChapV2Bundle != null) {
            EapMsChapV2ConfigUtils.setBuilderByReadingPersistableBundle(msChapV2Bundle, builder);
        }
        PersistableBundle akaPrimeBundle = in.getPersistableBundle(EAP_AKA_PRIME_CONFIG_KEY);
        if (akaPrimeBundle != null) {
            EapAkaPrimeConfigUtils.setBuilderByReadingPersistableBundle(akaPrimeBundle, builder);
        }
        return builder.build();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class EapMethodConfigUtils {
        private static final String METHOD_TYPE = "METHOD_TYPE";

        private EapMethodConfigUtils() {
        }

        public static PersistableBundle toPersistableBundle(EapSessionConfig.EapMethodConfig config) {
            PersistableBundle result = new PersistableBundle();
            result.putInt(METHOD_TYPE, config.getMethodType());
            return result;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class EapUiccConfigUtils extends EapMethodConfigUtils {
        static final String APP_TYPE_KEY = "APP_TYPE_KEY";
        static final String SUB_ID_KEY = "SUB_ID_KEY";

        private EapUiccConfigUtils() {
            super();
        }

        protected static PersistableBundle toPersistableBundle(EapSessionConfig.EapUiccConfig config) {
            PersistableBundle result = EapMethodConfigUtils.toPersistableBundle(config);
            result.putInt(SUB_ID_KEY, config.getSubId());
            result.putInt(APP_TYPE_KEY, config.getAppType());
            return result;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class EapSimConfigUtils extends EapUiccConfigUtils {
        private EapSimConfigUtils() {
            super();
        }

        public static PersistableBundle toPersistableBundle(EapSessionConfig.EapSimConfig config) {
            return EapUiccConfigUtils.toPersistableBundle((EapSessionConfig.EapUiccConfig) config);
        }

        public static void setBuilderByReadingPersistableBundle(PersistableBundle in, EapSessionConfig.Builder builder) {
            Objects.requireNonNull(in, "PersistableBundle was null");
            builder.setEapSimConfig(in.getInt("SUB_ID_KEY"), in.getInt("APP_TYPE_KEY"));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class EapAkaConfigUtils extends EapUiccConfigUtils {
        private EapAkaConfigUtils() {
            super();
        }

        public static PersistableBundle toPersistableBundle(EapSessionConfig.EapAkaConfig config) {
            return EapUiccConfigUtils.toPersistableBundle((EapSessionConfig.EapUiccConfig) config);
        }

        public static void setBuilderByReadingPersistableBundle(PersistableBundle in, EapSessionConfig.Builder builder) {
            Objects.requireNonNull(in, "PersistableBundle was null");
            builder.setEapAkaConfig(in.getInt("SUB_ID_KEY"), in.getInt("APP_TYPE_KEY"));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class EapAkaPrimeConfigUtils extends EapAkaConfigUtils {
        private static final String ALL_MISMATCHED_NETWORK_KEY = "ALL_MISMATCHED_NETWORK_KEY";
        private static final String NETWORK_NAME_KEY = "NETWORK_NAME_KEY";

        private EapAkaPrimeConfigUtils() {
            super();
        }

        public static PersistableBundle toPersistableBundle(EapSessionConfig.EapAkaPrimeConfig config) {
            PersistableBundle result = EapUiccConfigUtils.toPersistableBundle((EapSessionConfig.EapUiccConfig) config);
            result.putString(NETWORK_NAME_KEY, config.getNetworkName());
            result.putBoolean(ALL_MISMATCHED_NETWORK_KEY, config.allowsMismatchedNetworkNames());
            return result;
        }

        public static void setBuilderByReadingPersistableBundle(PersistableBundle in, EapSessionConfig.Builder builder) {
            Objects.requireNonNull(in, "PersistableBundle was null");
            builder.setEapAkaPrimeConfig(in.getInt("SUB_ID_KEY"), in.getInt("APP_TYPE_KEY"), in.getString(NETWORK_NAME_KEY), in.getBoolean(ALL_MISMATCHED_NETWORK_KEY));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class EapMsChapV2ConfigUtils extends EapMethodConfigUtils {
        private static final String PASSWORD_KEY = "PASSWORD_KEY";
        private static final String USERNAME_KEY = "USERNAME_KEY";

        private EapMsChapV2ConfigUtils() {
            super();
        }

        public static PersistableBundle toPersistableBundle(EapSessionConfig.EapMsChapV2Config config) {
            PersistableBundle result = EapMethodConfigUtils.toPersistableBundle(config);
            result.putString(USERNAME_KEY, config.getUsername());
            result.putString(PASSWORD_KEY, config.getPassword());
            return result;
        }

        public static void setBuilderByReadingPersistableBundle(PersistableBundle in, EapSessionConfig.Builder builder) {
            Objects.requireNonNull(in, "PersistableBundle was null");
            builder.setEapMsChapV2Config(in.getString(USERNAME_KEY), in.getString(PASSWORD_KEY));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class EapTtlsConfigUtils extends EapMethodConfigUtils {
        private static final String EAP_SESSION_CONFIG_KEY = "EAP_SESSION_CONFIG_KEY";
        private static final String TRUST_CERT_KEY = "TRUST_CERT_KEY";

        private EapTtlsConfigUtils() {
            super();
        }

        public static PersistableBundle toPersistableBundle(EapSessionConfig.EapTtlsConfig config) {
            PersistableBundle result = EapMethodConfigUtils.toPersistableBundle(config);
            try {
                if (config.getServerCaCert() != null) {
                    PersistableBundle caBundle = PersistableBundleUtils.fromByteArray(config.getServerCaCert().getEncoded());
                    result.putPersistableBundle(TRUST_CERT_KEY, caBundle);
                }
                result.putPersistableBundle(EAP_SESSION_CONFIG_KEY, EapSessionConfigUtils.toPersistableBundle(config.getInnerEapSessionConfig()));
                return result;
            } catch (CertificateEncodingException e) {
                throw new IllegalStateException("Fail to encode the certificate");
            }
        }

        public static void setBuilderByReadingPersistableBundle(PersistableBundle in, EapSessionConfig.Builder builder) {
            Objects.requireNonNull(in, "PersistableBundle was null");
            PersistableBundle caBundle = in.getPersistableBundle(TRUST_CERT_KEY);
            X509Certificate caCert = null;
            if (caBundle != null) {
                caCert = CertUtils.certificateFromByteArray(PersistableBundleUtils.toByteArray(caBundle));
            }
            PersistableBundle eapSessionConfigBundle = in.getPersistableBundle(EAP_SESSION_CONFIG_KEY);
            Objects.requireNonNull(eapSessionConfigBundle, "Inner EAP Session Config was null");
            EapSessionConfig eapSessionConfig = EapSessionConfigUtils.fromPersistableBundle(eapSessionConfigBundle);
            builder.setEapTtlsConfig(caCert, eapSessionConfig);
        }
    }
}
