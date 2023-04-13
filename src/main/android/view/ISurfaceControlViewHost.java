package android.view;

import android.content.res.Configuration;
import android.graphics.Rect;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.window.ISurfaceSyncGroup;
/* loaded from: classes4.dex */
public interface ISurfaceControlViewHost extends IInterface {
    public static final String DESCRIPTOR = "android.view.ISurfaceControlViewHost";

    ISurfaceSyncGroup getSurfaceSyncGroup() throws RemoteException;

    void onConfigurationChanged(Configuration configuration) throws RemoteException;

    void onDispatchDetachedFromWindow() throws RemoteException;

    void onInsetsChanged(InsetsState insetsState, Rect rect) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements ISurfaceControlViewHost {
        @Override // android.view.ISurfaceControlViewHost
        public void onConfigurationChanged(Configuration newConfig) throws RemoteException {
        }

        @Override // android.view.ISurfaceControlViewHost
        public void onDispatchDetachedFromWindow() throws RemoteException {
        }

        @Override // android.view.ISurfaceControlViewHost
        public void onInsetsChanged(InsetsState state, Rect insetFrame) throws RemoteException {
        }

        @Override // android.view.ISurfaceControlViewHost
        public ISurfaceSyncGroup getSurfaceSyncGroup() throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements ISurfaceControlViewHost {
        static final int TRANSACTION_getSurfaceSyncGroup = 4;
        static final int TRANSACTION_onConfigurationChanged = 1;
        static final int TRANSACTION_onDispatchDetachedFromWindow = 2;
        static final int TRANSACTION_onInsetsChanged = 3;

        public Stub() {
            attachInterface(this, ISurfaceControlViewHost.DESCRIPTOR);
        }

        public static ISurfaceControlViewHost asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ISurfaceControlViewHost.DESCRIPTOR);
            if (iin != null && (iin instanceof ISurfaceControlViewHost)) {
                return (ISurfaceControlViewHost) iin;
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
                    return "onConfigurationChanged";
                case 2:
                    return "onDispatchDetachedFromWindow";
                case 3:
                    return "onInsetsChanged";
                case 4:
                    return "getSurfaceSyncGroup";
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
                data.enforceInterface(ISurfaceControlViewHost.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ISurfaceControlViewHost.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            Configuration _arg0 = (Configuration) data.readTypedObject(Configuration.CREATOR);
                            data.enforceNoDataAvail();
                            onConfigurationChanged(_arg0);
                            break;
                        case 2:
                            onDispatchDetachedFromWindow();
                            break;
                        case 3:
                            InsetsState _arg02 = (InsetsState) data.readTypedObject(InsetsState.CREATOR);
                            Rect _arg1 = (Rect) data.readTypedObject(Rect.CREATOR);
                            data.enforceNoDataAvail();
                            onInsetsChanged(_arg02, _arg1);
                            break;
                        case 4:
                            ISurfaceSyncGroup _result = getSurfaceSyncGroup();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class Proxy implements ISurfaceControlViewHost {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISurfaceControlViewHost.DESCRIPTOR;
            }

            @Override // android.view.ISurfaceControlViewHost
            public void onConfigurationChanged(Configuration newConfig) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISurfaceControlViewHost.DESCRIPTOR);
                    _data.writeTypedObject(newConfig, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.ISurfaceControlViewHost
            public void onDispatchDetachedFromWindow() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISurfaceControlViewHost.DESCRIPTOR);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.ISurfaceControlViewHost
            public void onInsetsChanged(InsetsState state, Rect insetFrame) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISurfaceControlViewHost.DESCRIPTOR);
                    _data.writeTypedObject(state, 0);
                    _data.writeTypedObject(insetFrame, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.ISurfaceControlViewHost
            public ISurfaceSyncGroup getSurfaceSyncGroup() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISurfaceControlViewHost.DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    ISurfaceSyncGroup _result = ISurfaceSyncGroup.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
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
