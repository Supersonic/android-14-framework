package android.net;

import android.net.ipsec.ike.IkeDerAsn1DnIdentification;
import android.net.ipsec.ike.IkeFqdnIdentification;
import android.net.ipsec.ike.IkeIdentification;
import android.net.ipsec.ike.IkeIpv4AddrIdentification;
import android.net.ipsec.ike.IkeIpv6AddrIdentification;
import android.net.ipsec.ike.IkeKeyIdIdentification;
import android.net.ipsec.ike.IkeRfc822AddrIdentification;
import android.net.ipsec.ike.IkeSessionParams;
import android.net.ipsec.ike.IkeTunnelConnectionParams;
import android.security.keystore.KeyProperties;
import android.util.Log;
import com.android.internal.net.VpnProfile;
import com.android.internal.util.Preconditions;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class Ikev2VpnProfile extends PlatformVpnProfile {
    private static final String ANDROID_KEYSTORE_PROVIDER = "AndroidKeyStore";
    public static final List<String> DEFAULT_ALGORITHMS;
    private static final String EMPTY_CERT = "";
    private static final String MISSING_PARAM_MSG_TMPL = "Required parameter was not provided: %s";
    public static final String PREFIX_INLINE = "INLINE:";
    public static final String PREFIX_KEYSTORE_ALIAS = "KEYSTORE_ALIAS:";
    private static final String TAG = Ikev2VpnProfile.class.getSimpleName();
    private final List<String> mAllowedAlgorithms;
    private final boolean mAutomaticIpVersionSelectionEnabled;
    private final boolean mAutomaticNattKeepaliveTimerEnabled;
    private final IkeTunnelConnectionParams mIkeTunConnParams;
    private final boolean mIsBypassable;
    private final boolean mIsMetered;
    private final boolean mIsRestrictedToTestNetworks;
    private final int mMaxMtu;
    private final String mPassword;
    private final byte[] mPresharedKey;
    private final ProxyInfo mProxyInfo;
    private final PrivateKey mRsaPrivateKey;
    private final String mServerAddr;
    private final X509Certificate mServerRootCaCert;
    private final X509Certificate mUserCert;
    private final String mUserIdentity;
    private final String mUsername;

    static {
        List<String> algorithms = new ArrayList<>();
        addAlgorithmIfSupported(algorithms, "cbc(aes)");
        addAlgorithmIfSupported(algorithms, "rfc3686(ctr(aes))");
        addAlgorithmIfSupported(algorithms, "hmac(sha256)");
        addAlgorithmIfSupported(algorithms, "hmac(sha384)");
        addAlgorithmIfSupported(algorithms, "hmac(sha512)");
        addAlgorithmIfSupported(algorithms, "xcbc(aes)");
        addAlgorithmIfSupported(algorithms, "cmac(aes)");
        addAlgorithmIfSupported(algorithms, "rfc4106(gcm(aes))");
        addAlgorithmIfSupported(algorithms, "rfc7539esp(chacha20,poly1305)");
        DEFAULT_ALGORITHMS = Collections.unmodifiableList(algorithms);
    }

    private static void addAlgorithmIfSupported(List<String> algorithms, String ipSecAlgoName) {
        if (IpSecAlgorithm.getSupportedAlgorithms().contains(ipSecAlgoName)) {
            algorithms.add(ipSecAlgoName);
        }
    }

    private Ikev2VpnProfile(int type, String serverAddr, String userIdentity, byte[] presharedKey, X509Certificate serverRootCaCert, String username, String password, PrivateKey rsaPrivateKey, X509Certificate userCert, ProxyInfo proxyInfo, List<String> allowedAlgorithms, boolean isBypassable, boolean isMetered, int maxMtu, boolean restrictToTestNetworks, boolean excludeLocalRoutes, boolean requiresInternetValidation, IkeTunnelConnectionParams ikeTunConnParams, boolean automaticNattKeepaliveTimerEnabled, boolean automaticIpVersionSelectionEnabled) {
        super(type, excludeLocalRoutes, requiresInternetValidation);
        checkNotNull(allowedAlgorithms, MISSING_PARAM_MSG_TMPL, "Allowed Algorithms");
        this.mServerAddr = serverAddr;
        this.mUserIdentity = userIdentity;
        this.mPresharedKey = presharedKey == null ? null : Arrays.copyOf(presharedKey, presharedKey.length);
        this.mServerRootCaCert = serverRootCaCert;
        this.mUsername = username;
        this.mPassword = password;
        this.mRsaPrivateKey = rsaPrivateKey;
        this.mUserCert = userCert;
        this.mProxyInfo = proxyInfo != null ? new ProxyInfo(proxyInfo) : null;
        this.mAllowedAlgorithms = Collections.unmodifiableList(new ArrayList(allowedAlgorithms));
        if (excludeLocalRoutes && !isBypassable) {
            throw new IllegalArgumentException("Vpn must be bypassable if excludeLocalRoutes is set");
        }
        this.mIsBypassable = isBypassable;
        this.mIsMetered = isMetered;
        this.mMaxMtu = maxMtu;
        this.mIsRestrictedToTestNetworks = restrictToTestNetworks;
        this.mIkeTunConnParams = ikeTunConnParams;
        this.mAutomaticNattKeepaliveTimerEnabled = automaticNattKeepaliveTimerEnabled;
        this.mAutomaticIpVersionSelectionEnabled = automaticIpVersionSelectionEnabled;
        validate();
    }

    private void validate() {
        if (this.mMaxMtu < 1280) {
            throw new IllegalArgumentException("Max MTU must be at least1280");
        }
        if (this.mIkeTunConnParams != null) {
            return;
        }
        Preconditions.checkStringNotEmpty(this.mServerAddr, MISSING_PARAM_MSG_TMPL, "Server Address");
        Preconditions.checkStringNotEmpty(this.mUserIdentity, MISSING_PARAM_MSG_TMPL, "User Identity");
        switch (this.mType) {
            case 6:
                checkNotNull(this.mUsername, MISSING_PARAM_MSG_TMPL, "Username");
                checkNotNull(this.mPassword, MISSING_PARAM_MSG_TMPL, "Password");
                X509Certificate x509Certificate = this.mServerRootCaCert;
                if (x509Certificate != null) {
                    checkCert(x509Certificate);
                    break;
                }
                break;
            case 7:
                checkNotNull(this.mPresharedKey, MISSING_PARAM_MSG_TMPL, "Preshared Key");
                break;
            case 8:
                checkNotNull(this.mUserCert, MISSING_PARAM_MSG_TMPL, "User cert");
                checkNotNull(this.mRsaPrivateKey, MISSING_PARAM_MSG_TMPL, "RSA Private key");
                checkCert(this.mUserCert);
                X509Certificate x509Certificate2 = this.mServerRootCaCert;
                if (x509Certificate2 != null) {
                    checkCert(x509Certificate2);
                    break;
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid auth method set");
        }
        validateAllowedAlgorithms(this.mAllowedAlgorithms);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void validateAllowedAlgorithms(List<String> algorithmNames) {
        if (algorithmNames.contains("hmac(md5)") || algorithmNames.contains("hmac(sha1)")) {
            throw new IllegalArgumentException("Algorithm not supported for IKEv2 VPN profiles");
        }
        if (hasAeadAlgorithms(algorithmNames) || hasNormalModeAlgorithms(algorithmNames)) {
            return;
        }
        throw new IllegalArgumentException("Algorithm set missing support for Auth, Crypt or both");
    }

    public static boolean hasAeadAlgorithms(List<String> algorithmNames) {
        return algorithmNames.contains("rfc4106(gcm(aes))");
    }

    public static boolean hasNormalModeAlgorithms(List<String> algorithmNames) {
        boolean hasCrypt = algorithmNames.contains("cbc(aes)");
        boolean hasAuth = algorithmNames.contains("hmac(sha256)") || algorithmNames.contains("hmac(sha384)") || algorithmNames.contains("hmac(sha512)");
        return hasCrypt && hasAuth;
    }

    public String getServerAddr() {
        IkeTunnelConnectionParams ikeTunnelConnectionParams = this.mIkeTunConnParams;
        if (ikeTunnelConnectionParams == null) {
            return this.mServerAddr;
        }
        IkeSessionParams ikeSessionParams = ikeTunnelConnectionParams.getIkeSessionParams();
        return ikeSessionParams.getServerHostname();
    }

    public String getUserIdentity() {
        IkeTunnelConnectionParams ikeTunnelConnectionParams = this.mIkeTunConnParams;
        if (ikeTunnelConnectionParams == null) {
            return this.mUserIdentity;
        }
        IkeSessionParams ikeSessionParams = ikeTunnelConnectionParams.getIkeSessionParams();
        return getUserIdentityFromIkeSession(ikeSessionParams);
    }

    public byte[] getPresharedKey() {
        byte[] bArr;
        if (this.mIkeTunConnParams == null && (bArr = this.mPresharedKey) != null) {
            return Arrays.copyOf(bArr, bArr.length);
        }
        return null;
    }

    public X509Certificate getServerRootCaCert() {
        if (this.mIkeTunConnParams != null) {
            return null;
        }
        return this.mServerRootCaCert;
    }

    public String getUsername() {
        if (this.mIkeTunConnParams != null) {
            return null;
        }
        return this.mUsername;
    }

    public String getPassword() {
        if (this.mIkeTunConnParams != null) {
            return null;
        }
        return this.mPassword;
    }

    public PrivateKey getRsaPrivateKey() {
        if (this.mIkeTunConnParams != null) {
            return null;
        }
        return this.mRsaPrivateKey;
    }

    public X509Certificate getUserCert() {
        if (this.mIkeTunConnParams != null) {
            return null;
        }
        return this.mUserCert;
    }

    public ProxyInfo getProxyInfo() {
        return this.mProxyInfo;
    }

    public List<String> getAllowedAlgorithms() {
        return this.mIkeTunConnParams != null ? new ArrayList() : this.mAllowedAlgorithms;
    }

    public boolean isBypassable() {
        return this.mIsBypassable;
    }

    public boolean isMetered() {
        return this.mIsMetered;
    }

    public int getMaxMtu() {
        return this.mMaxMtu;
    }

    public IkeTunnelConnectionParams getIkeTunnelConnectionParams() {
        return this.mIkeTunConnParams;
    }

    public boolean isRestrictedToTestNetworks() {
        return this.mIsRestrictedToTestNetworks;
    }

    public boolean isAutomaticNattKeepaliveTimerEnabled() {
        return this.mAutomaticNattKeepaliveTimerEnabled;
    }

    public boolean isAutomaticIpVersionSelectionEnabled() {
        return this.mAutomaticIpVersionSelectionEnabled;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mType), this.mServerAddr, this.mUserIdentity, Integer.valueOf(Arrays.hashCode(this.mPresharedKey)), this.mServerRootCaCert, this.mUsername, this.mPassword, this.mRsaPrivateKey, this.mUserCert, this.mProxyInfo, this.mAllowedAlgorithms, Boolean.valueOf(this.mIsBypassable), Boolean.valueOf(this.mIsMetered), Integer.valueOf(this.mMaxMtu), Boolean.valueOf(this.mIsRestrictedToTestNetworks), Boolean.valueOf(this.mExcludeLocalRoutes), Boolean.valueOf(this.mRequiresInternetValidation), this.mIkeTunConnParams, Boolean.valueOf(this.mAutomaticNattKeepaliveTimerEnabled), Boolean.valueOf(this.mAutomaticIpVersionSelectionEnabled));
    }

    public boolean equals(Object obj) {
        if (obj instanceof Ikev2VpnProfile) {
            Ikev2VpnProfile other = (Ikev2VpnProfile) obj;
            return this.mType == other.mType && Objects.equals(this.mServerAddr, other.mServerAddr) && Objects.equals(this.mUserIdentity, other.mUserIdentity) && Arrays.equals(this.mPresharedKey, other.mPresharedKey) && Objects.equals(this.mServerRootCaCert, other.mServerRootCaCert) && Objects.equals(this.mUsername, other.mUsername) && Objects.equals(this.mPassword, other.mPassword) && Objects.equals(this.mRsaPrivateKey, other.mRsaPrivateKey) && Objects.equals(this.mUserCert, other.mUserCert) && Objects.equals(this.mProxyInfo, other.mProxyInfo) && Objects.equals(this.mAllowedAlgorithms, other.mAllowedAlgorithms) && this.mIsBypassable == other.mIsBypassable && this.mIsMetered == other.mIsMetered && this.mMaxMtu == other.mMaxMtu && this.mIsRestrictedToTestNetworks == other.mIsRestrictedToTestNetworks && this.mExcludeLocalRoutes == other.mExcludeLocalRoutes && this.mRequiresInternetValidation == other.mRequiresInternetValidation && Objects.equals(this.mIkeTunConnParams, other.mIkeTunConnParams) && this.mAutomaticNattKeepaliveTimerEnabled == other.mAutomaticNattKeepaliveTimerEnabled && this.mAutomaticIpVersionSelectionEnabled == other.mAutomaticIpVersionSelectionEnabled;
        }
        return false;
    }

    @Override // android.net.PlatformVpnProfile
    public VpnProfile toVpnProfile() throws IOException, GeneralSecurityException {
        VpnProfile profile = new VpnProfile("", this.mIsRestrictedToTestNetworks, this.mExcludeLocalRoutes, this.mRequiresInternetValidation, this.mIkeTunConnParams, this.mAutomaticNattKeepaliveTimerEnabled, this.mAutomaticIpVersionSelectionEnabled);
        profile.proxy = this.mProxyInfo;
        profile.isBypassable = this.mIsBypassable;
        profile.isMetered = this.mIsMetered;
        profile.maxMtu = this.mMaxMtu;
        profile.areAuthParamsInline = true;
        profile.saveLogin = true;
        if (this.mIkeTunConnParams != null) {
            profile.type = 9;
            return profile;
        }
        profile.type = this.mType;
        profile.server = getServerAddr();
        profile.ipsecIdentifier = getUserIdentity();
        profile.setAllowedAlgorithms(this.mAllowedAlgorithms);
        switch (this.mType) {
            case 6:
                profile.username = this.mUsername;
                profile.password = this.mPassword;
                X509Certificate x509Certificate = this.mServerRootCaCert;
                profile.ipsecCaCert = x509Certificate != null ? certificateToPemString(x509Certificate) : "";
                break;
            case 7:
                profile.ipsecSecret = encodeForIpsecSecret(this.mPresharedKey);
                break;
            case 8:
                profile.ipsecUserCert = certificateToPemString(this.mUserCert);
                profile.ipsecSecret = PREFIX_INLINE + encodeForIpsecSecret(this.mRsaPrivateKey.getEncoded());
                X509Certificate x509Certificate2 = this.mServerRootCaCert;
                profile.ipsecCaCert = x509Certificate2 != null ? certificateToPemString(x509Certificate2) : "";
                break;
            default:
                throw new IllegalArgumentException("Invalid auth method set");
        }
        return profile;
    }

    private static PrivateKey getPrivateKeyFromAndroidKeystore(String alias) {
        try {
            KeyStore keystore = KeyStore.getInstance("AndroidKeyStore");
            keystore.load(null);
            Key key = keystore.getKey(alias, null);
            if (!(key instanceof PrivateKey)) {
                throw new IllegalStateException("Unexpected key type returned from android keystore.");
            }
            return (PrivateKey) key;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load key from android keystore.", e);
        }
    }

    public static Ikev2VpnProfile fromVpnProfile(VpnProfile profile) throws GeneralSecurityException {
        Builder builder;
        PrivateKey key;
        if (profile.ikeTunConnParams == null) {
            builder = new Builder(profile.server, profile.ipsecIdentifier);
            builder.setAllowedAlgorithms(profile.getAllowedAlgorithms());
            switch (profile.type) {
                case 6:
                    builder.setAuthUsernamePassword(profile.username, profile.password, certificateFromPemString(profile.ipsecCaCert));
                    break;
                case 7:
                    builder.setAuthPsk(decodeFromIpsecSecret(profile.ipsecSecret));
                    break;
                case 8:
                    if (profile.ipsecSecret.startsWith(PREFIX_KEYSTORE_ALIAS)) {
                        String alias = profile.ipsecSecret.substring(PREFIX_KEYSTORE_ALIAS.length());
                        key = getPrivateKeyFromAndroidKeystore(alias);
                    } else if (profile.ipsecSecret.startsWith(PREFIX_INLINE)) {
                        key = getPrivateKey(profile.ipsecSecret.substring(PREFIX_INLINE.length()));
                    } else {
                        throw new IllegalArgumentException("Invalid RSA private key prefix");
                    }
                    X509Certificate userCert = certificateFromPemString(profile.ipsecUserCert);
                    X509Certificate serverRootCa = certificateFromPemString(profile.ipsecCaCert);
                    builder.setAuthDigitalSignature(userCert, key, serverRootCa);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid auth method set");
            }
        } else {
            builder = new Builder(profile.ikeTunConnParams);
        }
        builder.setProxy(profile.proxy);
        builder.setBypassable(profile.isBypassable);
        builder.setMetered(profile.isMetered);
        builder.setMaxMtu(profile.maxMtu);
        if (profile.isRestrictedToTestNetworks) {
            builder.restrictToTestNetworks();
        }
        if (profile.excludeLocalRoutes && !profile.isBypassable) {
            Log.m104w(TAG, "ExcludeLocalRoutes should only be set in the bypassable VPN");
        }
        builder.setLocalRoutesExcluded(profile.excludeLocalRoutes && profile.isBypassable);
        builder.setRequiresInternetValidation(profile.requiresInternetValidation);
        builder.setAutomaticNattKeepaliveTimerEnabled(profile.automaticNattKeepaliveTimerEnabled);
        builder.setAutomaticIpVersionSelectionEnabled(profile.automaticIpVersionSelectionEnabled);
        return builder.build();
    }

    public static boolean isValidVpnProfile(VpnProfile profile) {
        if (profile.server.isEmpty() || profile.ipsecIdentifier.isEmpty()) {
            return false;
        }
        switch (profile.type) {
            case 6:
                return (profile.username.isEmpty() || profile.password.isEmpty()) ? false : true;
            case 7:
                return !profile.ipsecSecret.isEmpty();
            case 8:
                return (profile.ipsecSecret.isEmpty() || profile.ipsecUserCert.isEmpty()) ? false : true;
            default:
                return false;
        }
    }

    public static String certificateToPemString(X509Certificate cert) throws IOException, CertificateEncodingException {
        if (cert == null) {
            return "";
        }
        return new String(android.security.Credentials.convertToPem(cert), StandardCharsets.US_ASCII);
    }

    private static X509Certificate certificateFromPemString(String certStr) throws CertificateException {
        if (certStr == null || "".equals(certStr)) {
            return null;
        }
        try {
            List<X509Certificate> certs = android.security.Credentials.convertFromPem(certStr.getBytes(StandardCharsets.US_ASCII));
            return certs.isEmpty() ? null : certs.get(0);
        } catch (IOException e) {
            throw new CertificateException(e);
        }
    }

    public static String encodeForIpsecSecret(byte[] secret) {
        checkNotNull(secret, MISSING_PARAM_MSG_TMPL, "secret");
        return Base64.getEncoder().encodeToString(secret);
    }

    private static byte[] decodeFromIpsecSecret(String encoded) {
        checkNotNull(encoded, MISSING_PARAM_MSG_TMPL, "encoded");
        return Base64.getDecoder().decode(encoded);
    }

    private static PrivateKey getPrivateKey(String keyStr) throws InvalidKeySpecException, NoSuchAlgorithmException {
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(decodeFromIpsecSecret(keyStr));
        KeyFactory keyFactory = KeyFactory.getInstance(KeyProperties.KEY_ALGORITHM_RSA);
        return keyFactory.generatePrivate(privateKeySpec);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void checkCert(X509Certificate cert) {
        try {
            certificateToPemString(cert);
        } catch (IOException | GeneralSecurityException e) {
            throw new IllegalArgumentException("Certificate could not be encoded");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static <T> T checkNotNull(T reference, String messageTemplate, Object... messageArgs) {
        return (T) Objects.requireNonNull(reference, String.format(messageTemplate, messageArgs));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void checkBuilderSetter(boolean constructedFromIkeTunConParams, String field) {
        if (constructedFromIkeTunConParams) {
            throw new IllegalArgumentException(field + " can't be set with IkeTunnelConnectionParams builder");
        }
    }

    private static String getUserIdentityFromIkeSession(IkeSessionParams params) {
        IkeIdentification ident = params.getLocalIdentification();
        if (ident instanceof IkeKeyIdIdentification) {
            return "@#" + new String(((IkeKeyIdIdentification) ident).keyId);
        }
        if (ident instanceof IkeRfc822AddrIdentification) {
            return "@@" + ((IkeRfc822AddrIdentification) ident).rfc822Name;
        }
        if (ident instanceof IkeFqdnIdentification) {
            return "@" + ((IkeFqdnIdentification) ident).fqdn;
        }
        if (ident instanceof IkeIpv4AddrIdentification) {
            return ((IkeIpv4AddrIdentification) ident).ipv4Address.getHostAddress();
        }
        if (ident instanceof IkeIpv6AddrIdentification) {
            return ((IkeIpv6AddrIdentification) ident).ipv6Address.getHostAddress();
        }
        if (ident instanceof IkeDerAsn1DnIdentification) {
            throw new IllegalArgumentException("Unspported ASN.1 encoded identities");
        }
        throw new IllegalArgumentException("Unknown IkeIdentification to get user identity");
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private final IkeTunnelConnectionParams mIkeTunConnParams;
        private String mPassword;
        private byte[] mPresharedKey;
        private ProxyInfo mProxyInfo;
        private PrivateKey mRsaPrivateKey;
        private final String mServerAddr;
        private X509Certificate mServerRootCaCert;
        private X509Certificate mUserCert;
        private final String mUserIdentity;
        private String mUsername;
        private int mType = -1;
        private List<String> mAllowedAlgorithms = Ikev2VpnProfile.DEFAULT_ALGORITHMS;
        private boolean mRequiresInternetValidation = false;
        private boolean mIsBypassable = false;
        private boolean mIsMetered = true;
        private int mMaxMtu = 1360;
        private boolean mIsRestrictedToTestNetworks = false;
        private boolean mExcludeLocalRoutes = false;
        private boolean mAutomaticNattKeepaliveTimerEnabled = false;
        private boolean mAutomaticIpVersionSelectionEnabled = false;

        public Builder(String serverAddr, String identity) {
            Ikev2VpnProfile.checkNotNull(serverAddr, Ikev2VpnProfile.MISSING_PARAM_MSG_TMPL, "serverAddr");
            Ikev2VpnProfile.checkNotNull(identity, Ikev2VpnProfile.MISSING_PARAM_MSG_TMPL, "identity");
            this.mServerAddr = serverAddr;
            this.mUserIdentity = identity;
            this.mIkeTunConnParams = null;
        }

        public Builder(IkeTunnelConnectionParams ikeTunConnParams) {
            Ikev2VpnProfile.checkNotNull(ikeTunConnParams, Ikev2VpnProfile.MISSING_PARAM_MSG_TMPL, "ikeTunConnParams");
            this.mIkeTunConnParams = ikeTunConnParams;
            this.mServerAddr = null;
            this.mUserIdentity = null;
        }

        private void resetAuthParams() {
            this.mPresharedKey = null;
            this.mServerRootCaCert = null;
            this.mUsername = null;
            this.mPassword = null;
            this.mRsaPrivateKey = null;
            this.mUserCert = null;
        }

        public Builder setAuthUsernamePassword(String user, String pass, X509Certificate serverRootCa) {
            Ikev2VpnProfile.checkNotNull(user, Ikev2VpnProfile.MISSING_PARAM_MSG_TMPL, "user");
            Ikev2VpnProfile.checkNotNull(pass, Ikev2VpnProfile.MISSING_PARAM_MSG_TMPL, "pass");
            Ikev2VpnProfile.checkBuilderSetter(this.mIkeTunConnParams != null, "authUsernamePassword");
            if (serverRootCa != null) {
                Ikev2VpnProfile.checkCert(serverRootCa);
            }
            resetAuthParams();
            this.mUsername = user;
            this.mPassword = pass;
            this.mServerRootCaCert = serverRootCa;
            this.mType = 6;
            return this;
        }

        public Builder setAuthDigitalSignature(X509Certificate userCert, PrivateKey key, X509Certificate serverRootCa) {
            Ikev2VpnProfile.checkNotNull(userCert, Ikev2VpnProfile.MISSING_PARAM_MSG_TMPL, "userCert");
            Ikev2VpnProfile.checkNotNull(key, Ikev2VpnProfile.MISSING_PARAM_MSG_TMPL, "key");
            Ikev2VpnProfile.checkBuilderSetter(this.mIkeTunConnParams != null, "authDigitalSignature");
            Ikev2VpnProfile.checkCert(userCert);
            if (serverRootCa != null) {
                Ikev2VpnProfile.checkCert(serverRootCa);
            }
            resetAuthParams();
            this.mUserCert = userCert;
            this.mRsaPrivateKey = key;
            this.mServerRootCaCert = serverRootCa;
            this.mType = 8;
            return this;
        }

        public Builder setAuthPsk(byte[] psk) {
            Ikev2VpnProfile.checkNotNull(psk, Ikev2VpnProfile.MISSING_PARAM_MSG_TMPL, "psk");
            Ikev2VpnProfile.checkBuilderSetter(this.mIkeTunConnParams != null, "authPsk");
            resetAuthParams();
            this.mPresharedKey = psk;
            this.mType = 7;
            return this;
        }

        public Builder setBypassable(boolean isBypassable) {
            this.mIsBypassable = isBypassable;
            return this;
        }

        public Builder setProxy(ProxyInfo proxy) {
            this.mProxyInfo = proxy;
            return this;
        }

        public Builder setMaxMtu(int mtu) {
            if (mtu < 1280) {
                throw new IllegalArgumentException("Max MTU must be at least 1280");
            }
            this.mMaxMtu = mtu;
            return this;
        }

        public Builder setRequiresInternetValidation(boolean requiresInternetValidation) {
            this.mRequiresInternetValidation = requiresInternetValidation;
            return this;
        }

        public Builder setMetered(boolean isMetered) {
            this.mIsMetered = isMetered;
            return this;
        }

        public Builder setAllowedAlgorithms(List<String> algorithmNames) {
            Ikev2VpnProfile.checkNotNull(algorithmNames, Ikev2VpnProfile.MISSING_PARAM_MSG_TMPL, "algorithmNames");
            Ikev2VpnProfile.checkBuilderSetter(this.mIkeTunConnParams != null, "algorithmNames");
            Ikev2VpnProfile.validateAllowedAlgorithms(algorithmNames);
            this.mAllowedAlgorithms = algorithmNames;
            return this;
        }

        public Builder restrictToTestNetworks() {
            this.mIsRestrictedToTestNetworks = true;
            return this;
        }

        public Builder setAutomaticNattKeepaliveTimerEnabled(boolean isEnabled) {
            this.mAutomaticNattKeepaliveTimerEnabled = isEnabled;
            return this;
        }

        public Builder setAutomaticIpVersionSelectionEnabled(boolean isEnabled) {
            this.mAutomaticIpVersionSelectionEnabled = isEnabled;
            return this;
        }

        public Builder setLocalRoutesExcluded(boolean excludeLocalRoutes) {
            this.mExcludeLocalRoutes = excludeLocalRoutes;
            return this;
        }

        public Ikev2VpnProfile build() {
            return new Ikev2VpnProfile(this.mType, this.mServerAddr, this.mUserIdentity, this.mPresharedKey, this.mServerRootCaCert, this.mUsername, this.mPassword, this.mRsaPrivateKey, this.mUserCert, this.mProxyInfo, this.mAllowedAlgorithms, this.mIsBypassable, this.mIsMetered, this.mMaxMtu, this.mIsRestrictedToTestNetworks, this.mExcludeLocalRoutes, this.mRequiresInternetValidation, this.mIkeTunConnParams, this.mAutomaticNattKeepaliveTimerEnabled, this.mAutomaticIpVersionSelectionEnabled);
        }
    }
}
