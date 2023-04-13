package android.content.p001pm;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* renamed from: android.content.pm.IOnChecksumsReadyListener */
/* loaded from: classes.dex */
public interface IOnChecksumsReadyListener extends IInterface {
    public static final String DESCRIPTOR = "android.content.pm.IOnChecksumsReadyListener";

    void onChecksumsReady(List<ApkChecksum> list) throws RemoteException;

    /* renamed from: android.content.pm.IOnChecksumsReadyListener$Default */
    /* loaded from: classes.dex */
    public static class Default implements IOnChecksumsReadyListener {
        @Override // android.content.p001pm.IOnChecksumsReadyListener
        public void onChecksumsReady(List<ApkChecksum> checksums) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.content.pm.IOnChecksumsReadyListener$Stub */
    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IOnChecksumsReadyListener {
        static final int TRANSACTION_onChecksumsReady = 1;

        public Stub() {
            attachInterface(this, IOnChecksumsReadyListener.DESCRIPTOR);
        }

        public static IOnChecksumsReadyListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IOnChecksumsReadyListener.DESCRIPTOR);
            if (iin != null && (iin instanceof IOnChecksumsReadyListener)) {
                return (IOnChecksumsReadyListener) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "onChecksumsReady";
                default:
                    return null;
            }
        }

        @Override // android.p008os.Binder
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(IOnChecksumsReadyListener.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IOnChecksumsReadyListener.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            List<ApkChecksum> _arg0 = data.createTypedArrayList(ApkChecksum.CREATOR);
                            data.enforceNoDataAvail();
                            onChecksumsReady(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: android.content.pm.IOnChecksumsReadyListener$Stub$Proxy */
        /* loaded from: classes.dex */
        public static class Proxy implements IOnChecksumsReadyListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IOnChecksumsReadyListener.DESCRIPTOR;
            }

            @Override // android.content.p001pm.IOnChecksumsReadyListener
            public void onChecksumsReady(List<ApkChecksum> checksums) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IOnChecksumsReadyListener.DESCRIPTOR);
                    _data.writeTypedList(checksums, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 0;
        }
    }
}
