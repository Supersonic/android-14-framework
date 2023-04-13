package android.view.accessibility;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes4.dex */
public interface IAccessibilityManagerClient extends IInterface {
    void notifyServicesStateChanged(long j) throws RemoteException;

    void setFocusAppearance(int i, int i2) throws RemoteException;

    void setRelevantEventTypes(int i) throws RemoteException;

    void setState(int i) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IAccessibilityManagerClient {
        @Override // android.view.accessibility.IAccessibilityManagerClient
        public void setState(int stateFlags) throws RemoteException {
        }

        @Override // android.view.accessibility.IAccessibilityManagerClient
        public void notifyServicesStateChanged(long updatedUiTimeout) throws RemoteException {
        }

        @Override // android.view.accessibility.IAccessibilityManagerClient
        public void setRelevantEventTypes(int eventTypes) throws RemoteException {
        }

        @Override // android.view.accessibility.IAccessibilityManagerClient
        public void setFocusAppearance(int strokeWidth, int color) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IAccessibilityManagerClient {
        public static final String DESCRIPTOR = "android.view.accessibility.IAccessibilityManagerClient";
        static final int TRANSACTION_notifyServicesStateChanged = 2;
        static final int TRANSACTION_setFocusAppearance = 4;
        static final int TRANSACTION_setRelevantEventTypes = 3;
        static final int TRANSACTION_setState = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAccessibilityManagerClient asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IAccessibilityManagerClient)) {
                return (IAccessibilityManagerClient) iin;
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
                    return "setState";
                case 2:
                    return "notifyServicesStateChanged";
                case 3:
                    return "setRelevantEventTypes";
                case 4:
                    return "setFocusAppearance";
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
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            setState(_arg0);
                            break;
                        case 2:
                            long _arg02 = data.readLong();
                            data.enforceNoDataAvail();
                            notifyServicesStateChanged(_arg02);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            setRelevantEventTypes(_arg03);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            setFocusAppearance(_arg04, _arg1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IAccessibilityManagerClient {
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

            @Override // android.view.accessibility.IAccessibilityManagerClient
            public void setState(int stateFlags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(stateFlags);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManagerClient
            public void notifyServicesStateChanged(long updatedUiTimeout) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(updatedUiTimeout);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManagerClient
            public void setRelevantEventTypes(int eventTypes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(eventTypes);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IAccessibilityManagerClient
            public void setFocusAppearance(int strokeWidth, int color) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(strokeWidth);
                    _data.writeInt(color);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 3;
        }
    }
}
