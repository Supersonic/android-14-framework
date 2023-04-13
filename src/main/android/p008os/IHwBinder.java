package android.p008os;

import android.annotation.SystemApi;
@SystemApi
/* renamed from: android.os.IHwBinder */
/* loaded from: classes3.dex */
public interface IHwBinder {

    /* renamed from: android.os.IHwBinder$DeathRecipient */
    /* loaded from: classes3.dex */
    public interface DeathRecipient {
        void serviceDied(long j);
    }

    boolean linkToDeath(DeathRecipient deathRecipient, long j);

    IHwInterface queryLocalInterface(String str);

    void transact(int i, HwParcel hwParcel, HwParcel hwParcel2, int i2) throws RemoteException;

    boolean unlinkToDeath(DeathRecipient deathRecipient);
}
