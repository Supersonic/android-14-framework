package com.android.server.signedconfig;

import android.os.Build;
import android.util.Slog;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
/* loaded from: classes2.dex */
public class SignatureVerifier {
    public final PublicKey mDebugKey;
    public final SignedConfigEvent mEvent;
    public final PublicKey mProdKey;

    public SignatureVerifier(SignedConfigEvent signedConfigEvent) {
        this.mEvent = signedConfigEvent;
        this.mDebugKey = Build.IS_DEBUGGABLE ? createKey("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEmJKs4lSn+XRhMQmMid+Zbhbu13YrU1haIhVC5296InRu1x7A8PV1ejQyisBODGgRY6pqkAHRncBCYcgg5wIIJg==") : null;
        this.mProdKey = createKey("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE+lky6wKyGL6lE1VrD0YTMHwb0Xwc+tzC8MvnrzVxodvTpVY/jV7V+Zktcx+pry43XPABFRXtbhTo+qykhyBA1g==");
    }

    public static PublicKey createKey(String str) {
        try {
            try {
                return KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(str)));
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                Slog.e("SignedConfig", "Failed to construct public key", e);
                return null;
            }
        } catch (IllegalArgumentException e2) {
            Slog.e("SignedConfig", "Failed to base64 decode public key", e2);
            return null;
        }
    }

    public final boolean verifyWithPublicKey(PublicKey publicKey, byte[] bArr, byte[] bArr2) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA256withECDSA");
        signature.initVerify(publicKey);
        signature.update(bArr);
        return signature.verify(bArr2);
    }

    public boolean verifySignature(String str, String str2) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        try {
            byte[] decode = Base64.getDecoder().decode(str2);
            byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
            if (Build.IS_DEBUGGABLE) {
                PublicKey publicKey = this.mDebugKey;
                if (publicKey != null) {
                    if (verifyWithPublicKey(publicKey, bytes, decode)) {
                        Slog.i("SignedConfig", "Verified config using debug key");
                        this.mEvent.verifiedWith = 1;
                        return true;
                    }
                } else {
                    Slog.w("SignedConfig", "Debuggable build, but have no debug key");
                }
            }
            PublicKey publicKey2 = this.mProdKey;
            if (publicKey2 == null) {
                Slog.e("SignedConfig", "No prod key; construction failed?");
                this.mEvent.status = 9;
                return false;
            } else if (verifyWithPublicKey(publicKey2, bytes, decode)) {
                Slog.i("SignedConfig", "Verified config using production key");
                this.mEvent.verifiedWith = 2;
                return true;
            } else {
                this.mEvent.status = 7;
                return false;
            }
        } catch (IllegalArgumentException unused) {
            this.mEvent.status = 3;
            Slog.e("SignedConfig", "Failed to base64 decode signature");
            return false;
        }
    }
}
