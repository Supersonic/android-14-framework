package com.android.server.security;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.security.attestationverification.AttestationVerificationManager;
import android.util.Log;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.security.AndroidKeystoreAttestationVerificationAttributes;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.json.JSONObject;
/* loaded from: classes2.dex */
public class AttestationVerificationPeerDeviceVerifier {
    public static final Set<String> ANDROID_SYSTEM_PACKAGE_NAME_SET;
    public static final boolean DEBUG;
    public final CertPathValidator mCertPathValidator;
    public final CertificateFactory mCertificateFactory;
    public final Context mContext;
    public final boolean mRevocationEnabled;
    public final LocalDate mTestLocalPatchDate;
    public final LocalDate mTestSystemDate;
    public final Set<TrustAnchor> mTrustAnchors;

    static {
        DEBUG = Build.IS_DEBUGGABLE && Log.isLoggable("AVF", 2);
        ANDROID_SYSTEM_PACKAGE_NAME_SET = Collections.singleton("AndroidSystem");
    }

    public AttestationVerificationPeerDeviceVerifier(Context context) throws Exception {
        Objects.requireNonNull(context);
        this.mContext = context;
        this.mCertificateFactory = CertificateFactory.getInstance("X.509");
        this.mCertPathValidator = CertPathValidator.getInstance("PKIX");
        this.mTrustAnchors = getTrustAnchors();
        this.mRevocationEnabled = true;
        this.mTestSystemDate = null;
        this.mTestLocalPatchDate = null;
    }

    @VisibleForTesting
    public AttestationVerificationPeerDeviceVerifier(Context context, Set<TrustAnchor> set, boolean z, LocalDate localDate, LocalDate localDate2) throws Exception {
        Objects.requireNonNull(context);
        this.mContext = context;
        this.mCertificateFactory = CertificateFactory.getInstance("X.509");
        this.mCertPathValidator = CertPathValidator.getInstance("PKIX");
        this.mTrustAnchors = set;
        this.mRevocationEnabled = z;
        this.mTestSystemDate = localDate;
        this.mTestLocalPatchDate = localDate2;
    }

    public int verifyAttestation(int i, Bundle bundle, byte[] bArr) {
        if (this.mCertificateFactory == null) {
            debugVerboseLog("Unable to access CertificateFactory");
            return 2;
        } else if (this.mCertPathValidator == null) {
            debugVerboseLog("Unable to access CertPathValidator");
            return 2;
        } else if (validateAttestationParameters(i, bundle)) {
            try {
                List<X509Certificate> certificates = getCertificates(bArr);
                validateCertificateChain(certificates);
                X509Certificate x509Certificate = certificates.get(0);
                AndroidKeystoreAttestationVerificationAttributes fromCertificate = AndroidKeystoreAttestationVerificationAttributes.fromCertificate(x509Certificate);
                if (checkAttestationForPeerDeviceProfile(fromCertificate)) {
                    return !checkLocalBindingRequirements(x509Certificate, fromCertificate, i, bundle) ? 2 : 1;
                }
                return 2;
            } catch (IOException | InvalidAlgorithmParameterException | CertPathValidatorException | CertificateException e) {
                debugVerboseLog("Unable to parse/validate Android Attestation certificate(s)", e);
                return 2;
            } catch (RuntimeException e2) {
                debugVerboseLog("Unexpected error", e2);
                return 2;
            }
        } else {
            return 2;
        }
    }

    public final List<X509Certificate> getCertificates(byte[] bArr) throws CertificateException {
        ArrayList arrayList = new ArrayList();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bArr);
        while (byteArrayInputStream.available() > 0) {
            arrayList.add((X509Certificate) this.mCertificateFactory.generateCertificate(byteArrayInputStream));
        }
        return arrayList;
    }

    public final boolean validateAttestationParameters(int i, Bundle bundle) {
        if (i != 2 && i != 3) {
            debugVerboseLog("Binding type is not supported: " + i);
            return false;
        } else if (bundle.size() < 1) {
            debugVerboseLog("At least 1 requirement is required.");
            return false;
        } else if (i == 2 && !bundle.containsKey("localbinding.public_key")) {
            debugVerboseLog("Requirements does not contain key: localbinding.public_key");
            return false;
        } else if (i != 3 || bundle.containsKey("localbinding.challenge")) {
            return true;
        } else {
            debugVerboseLog("Requirements does not contain key: localbinding.challenge");
            return false;
        }
    }

    public final void validateCertificateChain(List<X509Certificate> list) throws CertificateException, CertPathValidatorException, InvalidAlgorithmParameterException {
        if (list.size() < 2) {
            debugVerboseLog("Certificate chain less than 2 in size.");
            throw new CertificateException("Certificate chain less than 2 in size.");
        }
        CertPath generateCertPath = this.mCertificateFactory.generateCertPath(list);
        PKIXParameters pKIXParameters = new PKIXParameters(this.mTrustAnchors);
        if (this.mRevocationEnabled) {
            pKIXParameters.addCertPathChecker(new AndroidRevocationStatusListChecker());
        }
        pKIXParameters.setRevocationEnabled(false);
        this.mCertPathValidator.validate(generateCertPath, pKIXParameters);
    }

    public final Set<TrustAnchor> getTrustAnchors() throws CertPathValidatorException {
        HashSet hashSet = new HashSet();
        try {
            for (String str : getTrustAnchorResources()) {
                hashSet.add(new TrustAnchor((X509Certificate) this.mCertificateFactory.generateCertificate(new ByteArrayInputStream(getCertificateBytes(str))), null));
            }
            return Collections.unmodifiableSet(hashSet);
        } catch (CertificateException e) {
            e.printStackTrace();
            throw new CertPathValidatorException("Invalid trust anchor certificate.", e);
        }
    }

    public final byte[] getCertificateBytes(String str) {
        return str.replaceAll("\\s+", "\n").replaceAll("-BEGIN\\nCERTIFICATE-", "-BEGIN CERTIFICATE-").replaceAll("-END\\nCERTIFICATE-", "-END CERTIFICATE-").getBytes(StandardCharsets.UTF_8);
    }

    public final String[] getTrustAnchorResources() {
        return this.mContext.getResources().getStringArray(17236213);
    }

    public final boolean checkLocalBindingRequirements(X509Certificate x509Certificate, AndroidKeystoreAttestationVerificationAttributes androidKeystoreAttestationVerificationAttributes, int i, Bundle bundle) {
        if (i != 2) {
            if (i == 3) {
                if (!checkAttestationChallenge(androidKeystoreAttestationVerificationAttributes, bundle.getByteArray("localbinding.challenge"))) {
                    debugVerboseLog("Provided challenge does not match leaf certificate challenge.");
                    return false;
                }
            } else {
                throw new IllegalArgumentException("Unsupported local binding type " + AttestationVerificationManager.localBindingTypeToString(i));
            }
        } else if (!checkPublicKey(x509Certificate, bundle.getByteArray("localbinding.public_key"))) {
            debugVerboseLog("Provided public key does not match leaf certificate public key.");
            return false;
        }
        if (bundle.containsKey("android.key_owned_by_system")) {
            if (bundle.getBoolean("android.key_owned_by_system")) {
                if (checkOwnedBySystem(x509Certificate, androidKeystoreAttestationVerificationAttributes)) {
                    return true;
                }
                debugVerboseLog("Certificate public key is not owned by the AndroidSystem.");
                return false;
            }
            throw new IllegalArgumentException("The value of the requirement key android.key_owned_by_system cannot be false. You can remove the key if you don't want to verify it.");
        }
        return true;
    }

    public final boolean checkAttestationForPeerDeviceProfile(AndroidKeystoreAttestationVerificationAttributes androidKeystoreAttestationVerificationAttributes) {
        if (androidKeystoreAttestationVerificationAttributes.getAttestationVersion() < 3) {
            debugVerboseLog("Attestation version is not at least 3 (Keymaster 4).");
            return false;
        } else if (androidKeystoreAttestationVerificationAttributes.getKeymasterVersion() < 4) {
            debugVerboseLog("Keymaster version is not at least 4.");
            return false;
        } else if (androidKeystoreAttestationVerificationAttributes.getKeyOsVersion() < 100000) {
            debugVerboseLog("Android OS version is not 10+.");
            return false;
        } else if (!androidKeystoreAttestationVerificationAttributes.isAttestationHardwareBacked()) {
            debugVerboseLog("Key is not HW backed.");
            return false;
        } else if (!androidKeystoreAttestationVerificationAttributes.isKeymasterHardwareBacked()) {
            debugVerboseLog("Keymaster is not HW backed.");
            return false;
        } else if (androidKeystoreAttestationVerificationAttributes.getVerifiedBootState() != AndroidKeystoreAttestationVerificationAttributes.VerifiedBootState.VERIFIED) {
            debugVerboseLog("Boot state not Verified.");
            return false;
        } else {
            try {
                if (!androidKeystoreAttestationVerificationAttributes.isVerifiedBootLocked()) {
                    debugVerboseLog("Verified boot state is not locked.");
                    return false;
                } else if (!isValidPatchLevel(androidKeystoreAttestationVerificationAttributes.getKeyOsPatchLevel())) {
                    debugVerboseLog("OS patch level is not within valid range.");
                    return false;
                } else if (!isValidPatchLevel(androidKeystoreAttestationVerificationAttributes.getKeyBootPatchLevel())) {
                    debugVerboseLog("Boot patch level is not within valid range.");
                    return false;
                } else if (!isValidPatchLevel(androidKeystoreAttestationVerificationAttributes.getKeyVendorPatchLevel())) {
                    debugVerboseLog("Vendor patch level is not within valid range.");
                    return false;
                } else if (isValidPatchLevel(androidKeystoreAttestationVerificationAttributes.getKeyBootPatchLevel())) {
                    return true;
                } else {
                    debugVerboseLog("Boot patch level is not within valid range.");
                    return false;
                }
            } catch (IllegalStateException e) {
                debugVerboseLog("VerifiedBootLocked is not set.", e);
                return false;
            }
        }
    }

    public final boolean checkPublicKey(Certificate certificate, byte[] bArr) {
        return Arrays.equals(certificate.getPublicKey().getEncoded(), bArr);
    }

    public final boolean checkAttestationChallenge(AndroidKeystoreAttestationVerificationAttributes androidKeystoreAttestationVerificationAttributes, byte[] bArr) {
        return Arrays.equals(androidKeystoreAttestationVerificationAttributes.getAttestationChallenge().toByteArray(), bArr);
    }

    public final boolean checkOwnedBySystem(X509Certificate x509Certificate, AndroidKeystoreAttestationVerificationAttributes androidKeystoreAttestationVerificationAttributes) {
        Set<String> keySet = androidKeystoreAttestationVerificationAttributes.getApplicationPackageNameVersion().keySet();
        if (ANDROID_SYSTEM_PACKAGE_NAME_SET.equals(keySet)) {
            return true;
        }
        debugVerboseLog("Owner is not system, packages=" + keySet);
        return false;
    }

    /* JADX WARN: Code restructure failed: missing block: B:23:0x0063, code lost:
        if (java.time.temporal.ChronoUnit.MONTHS.between(r7, r8) <= 12) goto L23;
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0065, code lost:
        r1 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x0076, code lost:
        if (java.time.temporal.ChronoUnit.MONTHS.between(r8, r7) <= 12) goto L23;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x0079, code lost:
        return r1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final boolean isValidPatchLevel(int i) {
        LocalDate localDate = this.mTestSystemDate;
        if (localDate == null) {
            localDate = LocalDate.now(ZoneId.systemDefault());
        }
        boolean z = false;
        try {
            LocalDate localDate2 = this.mTestLocalPatchDate;
            if (localDate2 == null) {
                localDate2 = LocalDate.parse(Build.VERSION.SECURITY_PATCH);
            }
            if (ChronoUnit.MONTHS.between(localDate2, localDate) > 12) {
                return true;
            }
            String valueOf = String.valueOf(i);
            if (valueOf.length() != 6 && valueOf.length() != 8) {
                debugVerboseLog("Patch level is not in format YYYYMM or YYYYMMDD");
                return false;
            }
            LocalDate of = LocalDate.of(Integer.parseInt(valueOf.substring(0, 4)), Integer.parseInt(valueOf.substring(4, 6)), 1);
            if (of.compareTo((ChronoLocalDate) localDate2) <= 0) {
                if (of.compareTo((ChronoLocalDate) localDate2) >= 0) {
                    return true;
                }
            }
        } catch (Throwable unused) {
            debugVerboseLog("Build.VERSION.SECURITY_PATCH: " + Build.VERSION.SECURITY_PATCH + " is not in format YYYY-MM-DD");
            return false;
        }
    }

    /* loaded from: classes2.dex */
    public final class AndroidRevocationStatusListChecker extends PKIXCertPathChecker {
        public JSONObject mJsonStatusMap;
        public String mStatusUrl;

        @Override // java.security.cert.PKIXCertPathChecker
        public Set<String> getSupportedExtensions() {
            return null;
        }

        @Override // java.security.cert.PKIXCertPathChecker, java.security.cert.CertPathChecker
        public boolean isForwardCheckingSupported() {
            return false;
        }

        public AndroidRevocationStatusListChecker() {
        }

        @Override // java.security.cert.PKIXCertPathChecker, java.security.cert.CertPathChecker
        public void init(boolean z) throws CertPathValidatorException {
            String revocationListUrl = getRevocationListUrl();
            this.mStatusUrl = revocationListUrl;
            if (revocationListUrl == null || revocationListUrl.isEmpty()) {
                throw new CertPathValidatorException("R.string.vendor_required_attestation_revocation_list_url is empty.");
            }
            this.mJsonStatusMap = getStatusMap(this.mStatusUrl);
        }

        @Override // java.security.cert.PKIXCertPathChecker
        public void check(Certificate certificate, Collection<String> collection) throws CertPathValidatorException {
            String bigInteger = ((X509Certificate) certificate).getSerialNumber().toString(16);
            if (bigInteger == null) {
                throw new CertPathValidatorException("Certificate serial number can not be null.");
            }
            if (this.mJsonStatusMap.has(bigInteger)) {
                JSONObject jSONObject = this.mJsonStatusMap.getJSONObject(bigInteger);
                String string = jSONObject.getString("status");
                String string2 = jSONObject.getString("reason");
                throw new CertPathValidatorException("Invalid certificate with serial number " + bigInteger + " has status " + string + " because reason " + string2);
            }
        }

        public final JSONObject getStatusMap(String str) throws CertPathValidatorException {
            try {
                try {
                    InputStream openStream = new URL(str).openStream();
                    JSONObject jSONObject = new JSONObject(new String(openStream.readAllBytes(), StandardCharsets.UTF_8)).getJSONObject("entries");
                    openStream.close();
                    return jSONObject;
                } catch (Throwable th) {
                    throw new CertPathValidatorException("Unable to parse revocation status from " + this.mStatusUrl, th);
                }
            } catch (Throwable th2) {
                throw new CertPathValidatorException("Unable to get revocation status from " + this.mStatusUrl, th2);
            }
        }

        public final String getRevocationListUrl() {
            return AttestationVerificationPeerDeviceVerifier.this.mContext.getResources().getString(17041711);
        }
    }

    public static void debugVerboseLog(String str, Throwable th) {
        if (DEBUG) {
            Slog.v("AVF", str, th);
        }
    }

    public static void debugVerboseLog(String str) {
        if (DEBUG) {
            Slog.v("AVF", str);
        }
    }
}
