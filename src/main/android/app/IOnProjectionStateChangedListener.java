package android.app;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes.dex */
public interface IOnProjectionStateChangedListener extends IInterface {
    public static final String DESCRIPTOR = "android.app.IOnProjectionStateChangedListener";

    void onProjectionStateChanged(int i, List<String> list) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IOnProjectionStateChangedListener {
        @Override // android.app.IOnProjectionStateChangedListener
        public void onProjectionStateChanged(int activeProjectionTypes, List<String> projectingPackages) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IOnProjectionStateChangedListener {
        static final int TRANSACTION_onProjectionStateChanged = 1;

        public Stub() {
            attachInterface(this, IOnProjectionStateChangedListener.DESCRIPTOR);
        }

        public static IOnProjectionStateChangedListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IOnProjectionStateChangedListener.DESCRIPTOR);
            if (iin != null && (iin instanceof IOnProjectionStateChangedListener)) {
                return (IOnProjectionStateChangedListener) iin;
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
                    return "onProjectionStateChanged";
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
                data.enforceInterface(IOnProjectionStateChangedListener.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IOnProjectionStateChangedListener.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            List<String> _arg1 = data.createStringArrayList();
                            data.enforceNoDataAvail();
                            onProjectionStateChanged(_arg0, _arg1);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IOnProjectionStateChangedListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IOnProjectionStateChangedListener.DESCRIPTOR;
            }

            @Override // android.app.IOnProjectionStateChangedListener
            public void onProjectionStateChanged(int activeProjectionTypes, List<String> projectingPackages) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IOnProjectionStateChangedListener.DESCRIPTOR);
                    _data.writeInt(activeProjectionTypes);
                    _data.writeStringList(projectingPackages);
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
