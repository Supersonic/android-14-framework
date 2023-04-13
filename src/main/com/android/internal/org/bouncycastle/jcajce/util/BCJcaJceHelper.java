package com.android.internal.org.bouncycastle.jcajce.util;

import com.android.internal.org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
/* loaded from: classes4.dex */
public class BCJcaJceHelper extends ProviderJcaJceHelper {
    private static volatile Provider bcProvider;

    private static synchronized Provider getBouncyCastleProvider() {
        synchronized (BCJcaJceHelper.class) {
            Provider system = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
            if (system instanceof BouncyCastleProvider) {
                return system;
            }
            if (bcProvider != null) {
                return bcProvider;
            }
            bcProvider = new BouncyCastleProvider();
            return bcProvider;
        }
    }

    public BCJcaJceHelper() {
        super(getBouncyCastleProvider());
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.util.ProviderJcaJceHelper, com.android.internal.org.bouncycastle.jcajce.util.JcaJceHelper
    public Cipher createCipher(String algorithm) throws NoSuchAlgorithmException, NoSuchPaddingException {
        try {
            return super.createCipher(algorithm);
        } catch (NoSuchAlgorithmException originalException) {
            return Cipher.getInstance(algorithm, getPrivateProvider());
        }
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.util.ProviderJcaJceHelper, com.android.internal.org.bouncycastle.jcajce.util.JcaJceHelper
    public SecretKeyFactory createSecretKeyFactory(String algorithm) throws NoSuchAlgorithmException {
        try {
            return super.createSecretKeyFactory(algorithm);
        } catch (NoSuchAlgorithmException originalException) {
            return SecretKeyFactory.getInstance(algorithm, getPrivateProvider());
        }
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.util.ProviderJcaJceHelper, com.android.internal.org.bouncycastle.jcajce.util.JcaJceHelper
    public Mac createMac(String algorithm) throws NoSuchAlgorithmException {
        try {
            return super.createMac(algorithm);
        } catch (NoSuchAlgorithmException originalException) {
            return Mac.getInstance(algorithm, getPrivateProvider());
        }
    }

    private Provider getPrivateProvider() {
        if (this.provider instanceof BouncyCastleProvider) {
            return ((BouncyCastleProvider) this.provider).getPrivateProvider();
        }
        throw new IllegalStateException("Internal error in BCJcaJceHelper");
    }
}
