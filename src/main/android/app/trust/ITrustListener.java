package android.app.trust;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.text.TextUtils;
import java.util.List;
/* loaded from: classes.dex */
public interface ITrustListener extends IInterface {
    void onTrustChanged(boolean z, boolean z2, int i, int i2, List<String> list) throws RemoteException;

    void onTrustError(CharSequence charSequence) throws RemoteException;

    void onTrustManagedChanged(boolean z, int i) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements ITrustListener {
        @Override // android.app.trust.ITrustListener
        public void onTrustChanged(boolean enabled, boolean newlyUnlocked, int userId, int flags, List<String> trustGrantedMessages) throws RemoteException {
        }

        @Override // android.app.trust.ITrustListener
        public void onTrustManagedChanged(boolean managed, int userId) throws RemoteException {
        }

        @Override // android.app.trust.ITrustListener
        public void onTrustError(CharSequence message) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements ITrustListener {
        public static final String DESCRIPTOR = "android.app.trust.ITrustListener";
        static final int TRANSACTION_onTrustChanged = 1;
        static final int TRANSACTION_onTrustError = 3;
        static final int TRANSACTION_onTrustManagedChanged = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ITrustListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ITrustListener)) {
                return (ITrustListener) iin;
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
                    return "onTrustChanged";
                case 2:
                    return "onTrustManagedChanged";
                case 3:
                    return "onTrustError";
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
                            boolean _arg1 = data.readBoolean();
                            int _arg2 = data.readInt();
                            int _arg3 = data.readInt();
                            List<String> _arg4 = data.createStringArrayList();
                            data.enforceNoDataAvail();
                            onTrustChanged(_arg0, _arg1, _arg2, _arg3, _arg4);
                            break;
                        case 2:
                            boolean _arg02 = data.readBoolean();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            onTrustManagedChanged(_arg02, _arg12);
                            break;
                        case 3:
                            CharSequence _arg03 = (CharSequence) data.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                            data.enforceNoDataAvail();
                            onTrustError(_arg03);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements ITrustListener {
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

            @Override // android.app.trust.ITrustListener
            public void onTrustChanged(boolean enabled, boolean newlyUnlocked, int userId, int flags, List<String> trustGrantedMessages) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    _data.writeBoolean(newlyUnlocked);
                    _data.writeInt(userId);
                    _data.writeInt(flags);
                    _data.writeStringList(trustGrantedMessages);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.trust.ITrustListener
            public void onTrustManagedChanged(boolean managed, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(managed);
                    _data.writeInt(userId);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.trust.ITrustListener
            public void onTrustError(CharSequence message) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (message != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(message, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 2;
        }
    }
}
