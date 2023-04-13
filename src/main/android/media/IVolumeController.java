package android.media;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IVolumeController extends IInterface {
    void dismiss() throws RemoteException;

    void displayCsdWarning(int i, int i2) throws RemoteException;

    void displaySafeVolumeWarning(int i) throws RemoteException;

    void masterMuteChanged(int i) throws RemoteException;

    void setA11yMode(int i) throws RemoteException;

    void setLayoutDirection(int i) throws RemoteException;

    void volumeChanged(int i, int i2) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IVolumeController {
        @Override // android.media.IVolumeController
        public void displaySafeVolumeWarning(int flags) throws RemoteException {
        }

        @Override // android.media.IVolumeController
        public void volumeChanged(int streamType, int flags) throws RemoteException {
        }

        @Override // android.media.IVolumeController
        public void masterMuteChanged(int flags) throws RemoteException {
        }

        @Override // android.media.IVolumeController
        public void setLayoutDirection(int layoutDirection) throws RemoteException {
        }

        @Override // android.media.IVolumeController
        public void dismiss() throws RemoteException {
        }

        @Override // android.media.IVolumeController
        public void setA11yMode(int mode) throws RemoteException {
        }

        @Override // android.media.IVolumeController
        public void displayCsdWarning(int warning, int displayDurationMs) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IVolumeController {
        public static final String DESCRIPTOR = "android.media.IVolumeController";
        static final int TRANSACTION_dismiss = 5;
        static final int TRANSACTION_displayCsdWarning = 7;
        static final int TRANSACTION_displaySafeVolumeWarning = 1;
        static final int TRANSACTION_masterMuteChanged = 3;
        static final int TRANSACTION_setA11yMode = 6;
        static final int TRANSACTION_setLayoutDirection = 4;
        static final int TRANSACTION_volumeChanged = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IVolumeController asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IVolumeController)) {
                return (IVolumeController) iin;
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
                    return "displaySafeVolumeWarning";
                case 2:
                    return "volumeChanged";
                case 3:
                    return "masterMuteChanged";
                case 4:
                    return "setLayoutDirection";
                case 5:
                    return "dismiss";
                case 6:
                    return "setA11yMode";
                case 7:
                    return "displayCsdWarning";
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
                            displaySafeVolumeWarning(_arg0);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            volumeChanged(_arg02, _arg1);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            masterMuteChanged(_arg03);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            setLayoutDirection(_arg04);
                            break;
                        case 5:
                            dismiss();
                            break;
                        case 6:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            setA11yMode(_arg05);
                            break;
                        case 7:
                            int _arg06 = data.readInt();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            displayCsdWarning(_arg06, _arg12);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements IVolumeController {
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

            @Override // android.media.IVolumeController
            public void displaySafeVolumeWarning(int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flags);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IVolumeController
            public void volumeChanged(int streamType, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(streamType);
                    _data.writeInt(flags);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IVolumeController
            public void masterMuteChanged(int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flags);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IVolumeController
            public void setLayoutDirection(int layoutDirection) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(layoutDirection);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IVolumeController
            public void dismiss() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IVolumeController
            public void setA11yMode(int mode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mode);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IVolumeController
            public void displayCsdWarning(int warning, int displayDurationMs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(warning);
                    _data.writeInt(displayDurationMs);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 6;
        }
    }
}
