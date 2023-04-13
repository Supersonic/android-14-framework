package com.android.server.locksettings.recoverablekeystore;

import android.os.RemoteException;
import android.os.ServiceSpecificException;
import android.security.keystore.recovery.TrustedRootCertificates;
import android.util.Log;
import android.util.Pair;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
/* loaded from: classes2.dex */
public class TestOnlyInsecureCertificateHelper {
    public X509Certificate getRootCertificate(String str) throws RemoteException {
        String defaultCertificateAliasIfEmpty = getDefaultCertificateAliasIfEmpty(str);
        if (isTestOnlyCertificateAlias(defaultCertificateAliasIfEmpty)) {
            return TrustedRootCertificates.getTestOnlyInsecureCertificate();
        }
        X509Certificate rootCertificate = TrustedRootCertificates.getRootCertificate(defaultCertificateAliasIfEmpty);
        if (rootCertificate != null) {
            return rootCertificate;
        }
        throw new ServiceSpecificException(28, "The provided root certificate alias is invalid");
    }

    public String getDefaultCertificateAliasIfEmpty(String str) {
        if (str == null || str.isEmpty()) {
            Log.e("TestCertHelper", "rootCertificateAlias is null or empty - use secure default value");
            return "GoogleCloudKeyVaultServiceV1";
        }
        return str;
    }

    public boolean isTestOnlyCertificateAlias(String str) {
        return "TEST_ONLY_INSECURE_CERTIFICATE_ALIAS".equals(str);
    }

    public boolean isValidRootCertificateAlias(String str) {
        return TrustedRootCertificates.getRootCertificates().containsKey(str) || isTestOnlyCertificateAlias(str);
    }

    public boolean doesCredentialSupportInsecureMode(int i, byte[] bArr) {
        if (bArr == null) {
            return false;
        }
        if (i == 4 || i == 3) {
            byte[] bytes = "INSECURE_PSWD_".getBytes();
            if (bArr.length < bytes.length) {
                return false;
            }
            for (int i2 = 0; i2 < bytes.length; i2++) {
                if (bArr[i2] != bytes[i2]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public Map<String, Pair<SecretKey, byte[]>> keepOnlyWhitelistedInsecureKeys(Map<String, Pair<SecretKey, byte[]>> map) {
        if (map == null) {
            return null;
        }
        HashMap hashMap = new HashMap();
        for (Map.Entry<String, Pair<SecretKey, byte[]>> entry : map.entrySet()) {
            String key = entry.getKey();
            if (key != null && key.startsWith("INSECURE_KEY_ALIAS_KEY_MATERIAL_IS_NOT_PROTECTED_")) {
                hashMap.put(entry.getKey(), Pair.create((SecretKey) entry.getValue().first, (byte[]) entry.getValue().second));
                Log.d("TestCertHelper", "adding key with insecure alias " + key + " to the recovery snapshot");
            }
        }
        return hashMap;
    }
}
