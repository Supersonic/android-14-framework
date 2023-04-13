package android.window;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.view.SurfaceControl;
/* loaded from: classes4.dex */
public interface IDisplayAreaOrganizer extends IInterface {
    public static final String DESCRIPTOR = "android.window.IDisplayAreaOrganizer";

    void onDisplayAreaAppeared(DisplayAreaInfo displayAreaInfo, SurfaceControl surfaceControl) throws RemoteException;

    void onDisplayAreaInfoChanged(DisplayAreaInfo displayAreaInfo) throws RemoteException;

    void onDisplayAreaVanished(DisplayAreaInfo displayAreaInfo) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IDisplayAreaOrganizer {
        @Override // android.window.IDisplayAreaOrganizer
        public void onDisplayAreaAppeared(DisplayAreaInfo displayAreaInfo, SurfaceControl leash) throws RemoteException {
        }

        @Override // android.window.IDisplayAreaOrganizer
        public void onDisplayAreaVanished(DisplayAreaInfo displayAreaInfo) throws RemoteException {
        }

        @Override // android.window.IDisplayAreaOrganizer
        public void onDisplayAreaInfoChanged(DisplayAreaInfo displayAreaInfo) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IDisplayAreaOrganizer {
        static final int TRANSACTION_onDisplayAreaAppeared = 1;
        static final int TRANSACTION_onDisplayAreaInfoChanged = 3;
        static final int TRANSACTION_onDisplayAreaVanished = 2;

        public Stub() {
            attachInterface(this, IDisplayAreaOrganizer.DESCRIPTOR);
        }

        public static IDisplayAreaOrganizer asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IDisplayAreaOrganizer.DESCRIPTOR);
            if (iin != null && (iin instanceof IDisplayAreaOrganizer)) {
                return (IDisplayAreaOrganizer) iin;
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
                    return "onDisplayAreaAppeared";
                case 2:
                    return "onDisplayAreaVanished";
                case 3:
                    return "onDisplayAreaInfoChanged";
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
                data.enforceInterface(IDisplayAreaOrganizer.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IDisplayAreaOrganizer.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            DisplayAreaInfo _arg0 = (DisplayAreaInfo) data.readTypedObject(DisplayAreaInfo.CREATOR);
                            SurfaceControl _arg1 = (SurfaceControl) data.readTypedObject(SurfaceControl.CREATOR);
                            data.enforceNoDataAvail();
                            onDisplayAreaAppeared(_arg0, _arg1);
                            break;
                        case 2:
                            DisplayAreaInfo _arg02 = (DisplayAreaInfo) data.readTypedObject(DisplayAreaInfo.CREATOR);
                            data.enforceNoDataAvail();
                            onDisplayAreaVanished(_arg02);
                            break;
                        case 3:
                            DisplayAreaInfo _arg03 = (DisplayAreaInfo) data.readTypedObject(DisplayAreaInfo.CREATOR);
                            data.enforceNoDataAvail();
                            onDisplayAreaInfoChanged(_arg03);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IDisplayAreaOrganizer {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IDisplayAreaOrganizer.DESCRIPTOR;
            }

            @Override // android.window.IDisplayAreaOrganizer
            public void onDisplayAreaAppeared(DisplayAreaInfo displayAreaInfo, SurfaceControl leash) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IDisplayAreaOrganizer.DESCRIPTOR);
                    _data.writeTypedObject(displayAreaInfo, 0);
                    _data.writeTypedObject(leash, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.window.IDisplayAreaOrganizer
            public void onDisplayAreaVanished(DisplayAreaInfo displayAreaInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IDisplayAreaOrganizer.DESCRIPTOR);
                    _data.writeTypedObject(displayAreaInfo, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.window.IDisplayAreaOrganizer
            public void onDisplayAreaInfoChanged(DisplayAreaInfo displayAreaInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IDisplayAreaOrganizer.DESCRIPTOR);
                    _data.writeTypedObject(displayAreaInfo, 0);
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
