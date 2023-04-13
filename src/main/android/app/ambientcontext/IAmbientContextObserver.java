package android.app.ambientcontext;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes.dex */
public interface IAmbientContextObserver extends IInterface {
    public static final String DESCRIPTOR = "android.app.ambientcontext.IAmbientContextObserver";

    void onEvents(List<AmbientContextEvent> list) throws RemoteException;

    void onRegistrationComplete(int i) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IAmbientContextObserver {
        @Override // android.app.ambientcontext.IAmbientContextObserver
        public void onEvents(List<AmbientContextEvent> events) throws RemoteException {
        }

        @Override // android.app.ambientcontext.IAmbientContextObserver
        public void onRegistrationComplete(int statusCode) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IAmbientContextObserver {
        static final int TRANSACTION_onEvents = 1;
        static final int TRANSACTION_onRegistrationComplete = 2;

        public Stub() {
            attachInterface(this, IAmbientContextObserver.DESCRIPTOR);
        }

        public static IAmbientContextObserver asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IAmbientContextObserver.DESCRIPTOR);
            if (iin != null && (iin instanceof IAmbientContextObserver)) {
                return (IAmbientContextObserver) iin;
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
                    return "onEvents";
                case 2:
                    return "onRegistrationComplete";
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
                data.enforceInterface(IAmbientContextObserver.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IAmbientContextObserver.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            List<AmbientContextEvent> _arg0 = data.createTypedArrayList(AmbientContextEvent.CREATOR);
                            data.enforceNoDataAvail();
                            onEvents(_arg0);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            onRegistrationComplete(_arg02);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class Proxy implements IAmbientContextObserver {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IAmbientContextObserver.DESCRIPTOR;
            }

            @Override // android.app.ambientcontext.IAmbientContextObserver
            public void onEvents(List<AmbientContextEvent> events) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IAmbientContextObserver.DESCRIPTOR);
                    _data.writeTypedList(events, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.ambientcontext.IAmbientContextObserver
            public void onRegistrationComplete(int statusCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IAmbientContextObserver.DESCRIPTOR);
                    _data.writeInt(statusCode);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 1;
        }
    }
}
