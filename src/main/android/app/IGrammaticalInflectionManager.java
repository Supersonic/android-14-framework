package android.app;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IGrammaticalInflectionManager extends IInterface {
    public static final String DESCRIPTOR = "android.app.IGrammaticalInflectionManager";

    void setRequestedApplicationGrammaticalGender(String str, int i, int i2) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IGrammaticalInflectionManager {
        @Override // android.app.IGrammaticalInflectionManager
        public void setRequestedApplicationGrammaticalGender(String appPackageName, int userId, int gender) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IGrammaticalInflectionManager {
        static final int TRANSACTION_setRequestedApplicationGrammaticalGender = 1;

        public Stub() {
            attachInterface(this, IGrammaticalInflectionManager.DESCRIPTOR);
        }

        public static IGrammaticalInflectionManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IGrammaticalInflectionManager.DESCRIPTOR);
            if (iin != null && (iin instanceof IGrammaticalInflectionManager)) {
                return (IGrammaticalInflectionManager) iin;
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
                    return "setRequestedApplicationGrammaticalGender";
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
                data.enforceInterface(IGrammaticalInflectionManager.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IGrammaticalInflectionManager.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            int _arg1 = data.readInt();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            setRequestedApplicationGrammaticalGender(_arg0, _arg1, _arg2);
                            reply.writeNoException();
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IGrammaticalInflectionManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IGrammaticalInflectionManager.DESCRIPTOR;
            }

            @Override // android.app.IGrammaticalInflectionManager
            public void setRequestedApplicationGrammaticalGender(String appPackageName, int userId, int gender) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IGrammaticalInflectionManager.DESCRIPTOR);
                    _data.writeString(appPackageName);
                    _data.writeInt(userId);
                    _data.writeInt(gender);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
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
