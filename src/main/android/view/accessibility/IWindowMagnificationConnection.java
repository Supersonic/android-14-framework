package android.view.accessibility;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.view.accessibility.IRemoteMagnificationAnimationCallback;
import android.view.accessibility.IWindowMagnificationConnectionCallback;
/* loaded from: classes4.dex */
public interface IWindowMagnificationConnection extends IInterface {
    public static final String DESCRIPTOR = "android.view.accessibility.IWindowMagnificationConnection";

    void disableWindowMagnification(int i, IRemoteMagnificationAnimationCallback iRemoteMagnificationAnimationCallback) throws RemoteException;

    void enableWindowMagnification(int i, float f, float f2, float f3, float f4, float f5, IRemoteMagnificationAnimationCallback iRemoteMagnificationAnimationCallback) throws RemoteException;

    void moveWindowMagnifier(int i, float f, float f2) throws RemoteException;

    void moveWindowMagnifierToPosition(int i, float f, float f2, IRemoteMagnificationAnimationCallback iRemoteMagnificationAnimationCallback) throws RemoteException;

    void removeMagnificationButton(int i) throws RemoteException;

    void setConnectionCallback(IWindowMagnificationConnectionCallback iWindowMagnificationConnectionCallback) throws RemoteException;

    void setScale(int i, float f) throws RemoteException;

    void showMagnificationButton(int i, int i2) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IWindowMagnificationConnection {
        @Override // android.view.accessibility.IWindowMagnificationConnection
        public void enableWindowMagnification(int displayId, float scale, float centerX, float centerY, float magnificationFrameOffsetRatioX, float magnificationFrameOffsetRatioY, IRemoteMagnificationAnimationCallback callback) throws RemoteException {
        }

        @Override // android.view.accessibility.IWindowMagnificationConnection
        public void setScale(int displayId, float scale) throws RemoteException {
        }

        @Override // android.view.accessibility.IWindowMagnificationConnection
        public void disableWindowMagnification(int displayId, IRemoteMagnificationAnimationCallback callback) throws RemoteException {
        }

        @Override // android.view.accessibility.IWindowMagnificationConnection
        public void moveWindowMagnifier(int displayId, float offsetX, float offsetY) throws RemoteException {
        }

        @Override // android.view.accessibility.IWindowMagnificationConnection
        public void moveWindowMagnifierToPosition(int displayId, float positionX, float positionY, IRemoteMagnificationAnimationCallback callback) throws RemoteException {
        }

        @Override // android.view.accessibility.IWindowMagnificationConnection
        public void showMagnificationButton(int displayId, int magnificationMode) throws RemoteException {
        }

        @Override // android.view.accessibility.IWindowMagnificationConnection
        public void removeMagnificationButton(int displayId) throws RemoteException {
        }

        @Override // android.view.accessibility.IWindowMagnificationConnection
        public void setConnectionCallback(IWindowMagnificationConnectionCallback callback) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IWindowMagnificationConnection {
        static final int TRANSACTION_disableWindowMagnification = 3;
        static final int TRANSACTION_enableWindowMagnification = 1;
        static final int TRANSACTION_moveWindowMagnifier = 4;
        static final int TRANSACTION_moveWindowMagnifierToPosition = 5;
        static final int TRANSACTION_removeMagnificationButton = 7;
        static final int TRANSACTION_setConnectionCallback = 8;
        static final int TRANSACTION_setScale = 2;
        static final int TRANSACTION_showMagnificationButton = 6;

        public Stub() {
            attachInterface(this, IWindowMagnificationConnection.DESCRIPTOR);
        }

        public static IWindowMagnificationConnection asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IWindowMagnificationConnection.DESCRIPTOR);
            if (iin != null && (iin instanceof IWindowMagnificationConnection)) {
                return (IWindowMagnificationConnection) iin;
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
                    return "enableWindowMagnification";
                case 2:
                    return "setScale";
                case 3:
                    return "disableWindowMagnification";
                case 4:
                    return "moveWindowMagnifier";
                case 5:
                    return "moveWindowMagnifierToPosition";
                case 6:
                    return "showMagnificationButton";
                case 7:
                    return "removeMagnificationButton";
                case 8:
                    return "setConnectionCallback";
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
                data.enforceInterface(IWindowMagnificationConnection.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IWindowMagnificationConnection.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            float _arg1 = data.readFloat();
                            float _arg2 = data.readFloat();
                            float _arg3 = data.readFloat();
                            float _arg4 = data.readFloat();
                            float _arg5 = data.readFloat();
                            IRemoteMagnificationAnimationCallback _arg6 = IRemoteMagnificationAnimationCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            enableWindowMagnification(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            float _arg12 = data.readFloat();
                            data.enforceNoDataAvail();
                            setScale(_arg02, _arg12);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            IRemoteMagnificationAnimationCallback _arg13 = IRemoteMagnificationAnimationCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            disableWindowMagnification(_arg03, _arg13);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            float _arg14 = data.readFloat();
                            float _arg22 = data.readFloat();
                            data.enforceNoDataAvail();
                            moveWindowMagnifier(_arg04, _arg14, _arg22);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            float _arg15 = data.readFloat();
                            float _arg23 = data.readFloat();
                            IRemoteMagnificationAnimationCallback _arg32 = IRemoteMagnificationAnimationCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            moveWindowMagnifierToPosition(_arg05, _arg15, _arg23, _arg32);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            int _arg16 = data.readInt();
                            data.enforceNoDataAvail();
                            showMagnificationButton(_arg06, _arg16);
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            data.enforceNoDataAvail();
                            removeMagnificationButton(_arg07);
                            break;
                        case 8:
                            IWindowMagnificationConnectionCallback _arg08 = IWindowMagnificationConnectionCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setConnectionCallback(_arg08);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IWindowMagnificationConnection {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IWindowMagnificationConnection.DESCRIPTOR;
            }

            @Override // android.view.accessibility.IWindowMagnificationConnection
            public void enableWindowMagnification(int displayId, float scale, float centerX, float centerY, float magnificationFrameOffsetRatioX, float magnificationFrameOffsetRatioY, IRemoteMagnificationAnimationCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IWindowMagnificationConnection.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeFloat(scale);
                    _data.writeFloat(centerX);
                    _data.writeFloat(centerY);
                    _data.writeFloat(magnificationFrameOffsetRatioX);
                    _data.writeFloat(magnificationFrameOffsetRatioY);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IWindowMagnificationConnection
            public void setScale(int displayId, float scale) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IWindowMagnificationConnection.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeFloat(scale);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IWindowMagnificationConnection
            public void disableWindowMagnification(int displayId, IRemoteMagnificationAnimationCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IWindowMagnificationConnection.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IWindowMagnificationConnection
            public void moveWindowMagnifier(int displayId, float offsetX, float offsetY) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IWindowMagnificationConnection.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeFloat(offsetX);
                    _data.writeFloat(offsetY);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IWindowMagnificationConnection
            public void moveWindowMagnifierToPosition(int displayId, float positionX, float positionY, IRemoteMagnificationAnimationCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IWindowMagnificationConnection.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeFloat(positionX);
                    _data.writeFloat(positionY);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IWindowMagnificationConnection
            public void showMagnificationButton(int displayId, int magnificationMode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IWindowMagnificationConnection.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeInt(magnificationMode);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IWindowMagnificationConnection
            public void removeMagnificationButton(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IWindowMagnificationConnection.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.accessibility.IWindowMagnificationConnection
            public void setConnectionCallback(IWindowMagnificationConnectionCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IWindowMagnificationConnection.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 7;
        }
    }
}
