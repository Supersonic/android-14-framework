package com.android.server.profcollect;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.android.server.profcollect.IProviderStatusCallback;
/* loaded from: classes2.dex */
public interface IProfCollectd extends IInterface {

    /* loaded from: classes2.dex */
    public static class Default implements IProfCollectd {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.android.server.profcollect.IProfCollectd
        public String get_supported_provider() throws RemoteException {
            return null;
        }

        @Override // com.android.server.profcollect.IProfCollectd
        public void process() throws RemoteException {
        }

        @Override // com.android.server.profcollect.IProfCollectd
        public void registerProviderStatusCallback(IProviderStatusCallback iProviderStatusCallback) throws RemoteException {
        }

        @Override // com.android.server.profcollect.IProfCollectd
        public String report() throws RemoteException {
            return null;
        }

        @Override // com.android.server.profcollect.IProfCollectd
        public void trace_once(String str) throws RemoteException {
        }
    }

    String get_supported_provider() throws RemoteException;

    void process() throws RemoteException;

    void registerProviderStatusCallback(IProviderStatusCallback iProviderStatusCallback) throws RemoteException;

    String report() throws RemoteException;

    void schedule() throws RemoteException;

    void terminate() throws RemoteException;

    void trace_once(String str) throws RemoteException;

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IProfCollectd {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, "com.android.server.profcollect.IProfCollectd");
        }

        public static IProfCollectd asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.android.server.profcollect.IProfCollectd");
            if (queryLocalInterface != null && (queryLocalInterface instanceof IProfCollectd)) {
                return (IProfCollectd) queryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface("com.android.server.profcollect.IProfCollectd");
            }
            if (i == 1598968902) {
                parcel2.writeString("com.android.server.profcollect.IProfCollectd");
                return true;
            }
            switch (i) {
                case 1:
                    schedule();
                    parcel2.writeNoException();
                    break;
                case 2:
                    terminate();
                    parcel2.writeNoException();
                    break;
                case 3:
                    String readString = parcel.readString();
                    parcel.enforceNoDataAvail();
                    trace_once(readString);
                    parcel2.writeNoException();
                    break;
                case 4:
                    process();
                    parcel2.writeNoException();
                    break;
                case 5:
                    String report = report();
                    parcel2.writeNoException();
                    parcel2.writeString(report);
                    break;
                case 6:
                    String str = get_supported_provider();
                    parcel2.writeNoException();
                    parcel2.writeString(str);
                    break;
                case 7:
                    IProviderStatusCallback asInterface = IProviderStatusCallback.Stub.asInterface(parcel.readStrongBinder());
                    parcel.enforceNoDataAvail();
                    registerProviderStatusCallback(asInterface);
                    parcel2.writeNoException();
                    break;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
            return true;
        }

        /* loaded from: classes2.dex */
        public static class Proxy implements IProfCollectd {
            public IBinder mRemote;

            public Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.android.server.profcollect.IProfCollectd
            public void trace_once(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.server.profcollect.IProfCollectd");
                    obtain.writeString(str);
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.android.server.profcollect.IProfCollectd
            public void process() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.server.profcollect.IProfCollectd");
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.android.server.profcollect.IProfCollectd
            public String report() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.server.profcollect.IProfCollectd");
                    this.mRemote.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.android.server.profcollect.IProfCollectd
            public String get_supported_provider() throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.server.profcollect.IProfCollectd");
                    this.mRemote.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.android.server.profcollect.IProfCollectd
            public void registerProviderStatusCallback(IProviderStatusCallback iProviderStatusCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain(asBinder());
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.android.server.profcollect.IProfCollectd");
                    obtain.writeStrongInterface(iProviderStatusCallback);
                    this.mRemote.transact(7, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }
    }
}
