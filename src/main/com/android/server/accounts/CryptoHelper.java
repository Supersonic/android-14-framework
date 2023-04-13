package com.android.server.accounts;

import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
/* loaded from: classes.dex */
public class CryptoHelper {
    public static CryptoHelper sInstance;
    public final SecretKey mEncryptionKey = KeyGenerator.getInstance("AES").generateKey();
    public final SecretKey mMacKey = KeyGenerator.getInstance("HMACSHA256").generateKey();

    public static synchronized CryptoHelper getInstance() throws NoSuchAlgorithmException {
        CryptoHelper cryptoHelper;
        synchronized (CryptoHelper.class) {
            if (sInstance == null) {
                sInstance = new CryptoHelper();
            }
            cryptoHelper = sInstance;
        }
        return cryptoHelper;
    }

    public Bundle encryptBundle(Bundle bundle) throws GeneralSecurityException {
        Objects.requireNonNull(bundle, "Cannot encrypt null bundle.");
        Parcel obtain = Parcel.obtain();
        bundle.writeToParcel(obtain, 0);
        byte[] marshall = obtain.marshall();
        obtain.recycle();
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(1, this.mEncryptionKey);
        byte[] doFinal = cipher.doFinal(marshall);
        byte[] iv = cipher.getIV();
        byte[] createMac = createMac(doFinal, iv);
        Bundle bundle2 = new Bundle();
        bundle2.putByteArray("cipher", doFinal);
        bundle2.putByteArray("mac", createMac);
        bundle2.putByteArray("iv", iv);
        return bundle2;
    }

    public Bundle decryptBundle(Bundle bundle) throws GeneralSecurityException {
        Objects.requireNonNull(bundle, "Cannot decrypt null bundle.");
        byte[] byteArray = bundle.getByteArray("iv");
        byte[] byteArray2 = bundle.getByteArray("cipher");
        if (!verifyMac(byteArray2, byteArray, bundle.getByteArray("mac"))) {
            Log.w("Account", "Escrow mac mismatched!");
            return null;
        }
        IvParameterSpec ivParameterSpec = new IvParameterSpec(byteArray);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(2, this.mEncryptionKey, ivParameterSpec);
        byte[] doFinal = cipher.doFinal(byteArray2);
        Parcel obtain = Parcel.obtain();
        obtain.unmarshall(doFinal, 0, doFinal.length);
        obtain.setDataPosition(0);
        Bundle bundle2 = new Bundle();
        bundle2.readFromParcel(obtain);
        obtain.recycle();
        return bundle2;
    }

    public final boolean verifyMac(byte[] bArr, byte[] bArr2, byte[] bArr3) throws GeneralSecurityException {
        if (bArr == null || bArr.length == 0 || bArr3 == null || bArr3.length == 0) {
            if (Log.isLoggable("Account", 2)) {
                Log.v("Account", "Cipher or MAC is empty!");
                return false;
            }
            return false;
        }
        return constantTimeArrayEquals(bArr3, createMac(bArr, bArr2));
    }

    public final byte[] createMac(byte[] bArr, byte[] bArr2) throws GeneralSecurityException {
        Mac mac = Mac.getInstance("HMACSHA256");
        mac.init(this.mMacKey);
        mac.update(bArr);
        mac.update(bArr2);
        return mac.doFinal();
    }

    public static boolean constantTimeArrayEquals(byte[] bArr, byte[] bArr2) {
        if (bArr == null || bArr2 == null) {
            return bArr == bArr2;
        } else if (bArr.length != bArr2.length) {
            return false;
        } else {
            boolean z = true;
            for (int i = 0; i < bArr2.length; i++) {
                z &= bArr[i] == bArr2[i];
            }
            return z;
        }
    }
}
