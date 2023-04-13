package com.android.server.locksettings;

import android.hardware.authsecret.IAuthSecret;
import android.os.IBinder;
import android.os.RemoteException;
import java.util.ArrayList;
/* loaded from: classes2.dex */
public class AuthSecretHidlAdapter implements IAuthSecret {
    public final android.hardware.authsecret.V1_0.IAuthSecret mImpl;

    public AuthSecretHidlAdapter(android.hardware.authsecret.V1_0.IAuthSecret iAuthSecret) {
        this.mImpl = iAuthSecret;
    }

    @Override // android.hardware.authsecret.IAuthSecret
    public void setPrimaryUserCredential(byte[] bArr) throws RemoteException {
        ArrayList<Byte> arrayList = new ArrayList<>(bArr.length);
        for (byte b : bArr) {
            arrayList.add(Byte.valueOf(b));
        }
        this.mImpl.primaryUserCredential(arrayList);
    }

    @Override // android.os.IInterface
    public IBinder asBinder() {
        throw new UnsupportedOperationException("AuthSecretHidlAdapter does not support asBinder");
    }
}
