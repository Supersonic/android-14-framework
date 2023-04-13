package android.view;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes4.dex */
public interface IDockedStackListener extends IInterface {
    void onAdjustedForImeChanged(boolean z, long j) throws RemoteException;

    void onDividerVisibilityChanged(boolean z) throws RemoteException;

    void onDockSideChanged(int i) throws RemoteException;

    void onDockedStackExistsChanged(boolean z) throws RemoteException;

    void onDockedStackMinimizedChanged(boolean z, long j, boolean z2) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IDockedStackListener {
        @Override // android.view.IDockedStackListener
        public void onDividerVisibilityChanged(boolean visible) throws RemoteException {
        }

        @Override // android.view.IDockedStackListener
        public void onDockedStackExistsChanged(boolean exists) throws RemoteException {
        }

        @Override // android.view.IDockedStackListener
        public void onDockedStackMinimizedChanged(boolean minimized, long animDuration, boolean isHomeStackResizable) throws RemoteException {
        }

        @Override // android.view.IDockedStackListener
        public void onAdjustedForImeChanged(boolean adjustedForIme, long animDuration) throws RemoteException {
        }

        @Override // android.view.IDockedStackListener
        public void onDockSideChanged(int newDockSide) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IDockedStackListener {
        public static final String DESCRIPTOR = "android.view.IDockedStackListener";
        static final int TRANSACTION_onAdjustedForImeChanged = 4;
        static final int TRANSACTION_onDividerVisibilityChanged = 1;
        static final int TRANSACTION_onDockSideChanged = 5;
        static final int TRANSACTION_onDockedStackExistsChanged = 2;
        static final int TRANSACTION_onDockedStackMinimizedChanged = 3;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IDockedStackListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IDockedStackListener)) {
                return (IDockedStackListener) iin;
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
                    return "onDividerVisibilityChanged";
                case 2:
                    return "onDockedStackExistsChanged";
                case 3:
                    return "onDockedStackMinimizedChanged";
                case 4:
                    return "onAdjustedForImeChanged";
                case 5:
                    return "onDockSideChanged";
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
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            boolean _arg0 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onDividerVisibilityChanged(_arg0);
                            break;
                        case 2:
                            boolean _arg02 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onDockedStackExistsChanged(_arg02);
                            break;
                        case 3:
                            boolean _arg03 = data.readBoolean();
                            long _arg1 = data.readLong();
                            boolean _arg2 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onDockedStackMinimizedChanged(_arg03, _arg1, _arg2);
                            break;
                        case 4:
                            boolean _arg04 = data.readBoolean();
                            long _arg12 = data.readLong();
                            data.enforceNoDataAvail();
                            onAdjustedForImeChanged(_arg04, _arg12);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            onDockSideChanged(_arg05);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IDockedStackListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.view.IDockedStackListener
            public void onDividerVisibilityChanged(boolean visible) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(visible);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.IDockedStackListener
            public void onDockedStackExistsChanged(boolean exists) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(exists);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.IDockedStackListener
            public void onDockedStackMinimizedChanged(boolean minimized, long animDuration, boolean isHomeStackResizable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(minimized);
                    _data.writeLong(animDuration);
                    _data.writeBoolean(isHomeStackResizable);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.IDockedStackListener
            public void onAdjustedForImeChanged(boolean adjustedForIme, long animDuration) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(adjustedForIme);
                    _data.writeLong(animDuration);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.IDockedStackListener
            public void onDockSideChanged(int newDockSide) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(newDockSide);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 4;
        }
    }
}
