package android.permission;

import android.content.AttributionSourceState;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface IPermissionChecker extends IInterface {
    public static final String DESCRIPTOR = "android$permission$IPermissionChecker".replace('$', '.');
    public static final int PERMISSION_GRANTED = 0;
    public static final int PERMISSION_HARD_DENIED = 2;
    public static final int PERMISSION_SOFT_DENIED = 1;

    int checkOp(int i, AttributionSourceState attributionSourceState, String str, boolean z, boolean z2) throws RemoteException;

    int checkPermission(String str, AttributionSourceState attributionSourceState, String str2, boolean z, boolean z2, boolean z3, int i) throws RemoteException;

    void finishDataDelivery(int i, AttributionSourceState attributionSourceState, boolean z) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IPermissionChecker {
        @Override // android.permission.IPermissionChecker
        public int checkPermission(String permission, AttributionSourceState attributionSource, String message, boolean forDataDelivery, boolean startDataDelivery, boolean fromDatasource, int attributedOp) throws RemoteException {
            return 0;
        }

        @Override // android.permission.IPermissionChecker
        public void finishDataDelivery(int op, AttributionSourceState attributionSource, boolean fromDatasource) throws RemoteException {
        }

        @Override // android.permission.IPermissionChecker
        public int checkOp(int op, AttributionSourceState attributionSource, String message, boolean forDataDelivery, boolean startDataDelivery) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IPermissionChecker {
        static final int TRANSACTION_checkOp = 3;
        static final int TRANSACTION_checkPermission = 1;
        static final int TRANSACTION_finishDataDelivery = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IPermissionChecker asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IPermissionChecker)) {
                return (IPermissionChecker) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            String descriptor = DESCRIPTOR;
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(descriptor);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(descriptor);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            AttributionSourceState _arg1 = (AttributionSourceState) data.readTypedObject(AttributionSourceState.CREATOR);
                            String _arg2 = data.readString();
                            boolean _arg3 = data.readBoolean();
                            boolean _arg4 = data.readBoolean();
                            boolean _arg5 = data.readBoolean();
                            int _arg6 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result = checkPermission(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6);
                            reply.writeNoException();
                            reply.writeInt(_result);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            AttributionSourceState _arg12 = (AttributionSourceState) data.readTypedObject(AttributionSourceState.CREATOR);
                            boolean _arg22 = data.readBoolean();
                            data.enforceNoDataAvail();
                            finishDataDelivery(_arg02, _arg12, _arg22);
                            reply.writeNoException();
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            AttributionSourceState _arg13 = (AttributionSourceState) data.readTypedObject(AttributionSourceState.CREATOR);
                            String _arg23 = data.readString();
                            boolean _arg32 = data.readBoolean();
                            boolean _arg42 = data.readBoolean();
                            data.enforceNoDataAvail();
                            int _result2 = checkOp(_arg03, _arg13, _arg23, _arg32, _arg42);
                            reply.writeNoException();
                            reply.writeInt(_result2);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IPermissionChecker {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override // android.permission.IPermissionChecker
            public int checkPermission(String permission, AttributionSourceState attributionSource, String message, boolean forDataDelivery, boolean startDataDelivery, boolean fromDatasource, int attributedOp) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(permission);
                    _data.writeTypedObject(attributionSource, 0);
                    _data.writeString(message);
                    _data.writeBoolean(forDataDelivery);
                    _data.writeBoolean(startDataDelivery);
                    _data.writeBoolean(fromDatasource);
                    _data.writeInt(attributedOp);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionChecker
            public void finishDataDelivery(int op, AttributionSourceState attributionSource, boolean fromDatasource) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(op);
                    _data.writeTypedObject(attributionSource, 0);
                    _data.writeBoolean(fromDatasource);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.permission.IPermissionChecker
            public int checkOp(int op, AttributionSourceState attributionSource, String message, boolean forDataDelivery, boolean startDataDelivery) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(op);
                    _data.writeTypedObject(attributionSource, 0);
                    _data.writeString(message);
                    _data.writeBoolean(forDataDelivery);
                    _data.writeBoolean(startDataDelivery);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}
