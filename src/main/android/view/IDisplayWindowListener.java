package android.view;

import android.content.res.Configuration;
import android.graphics.Rect;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes4.dex */
public interface IDisplayWindowListener extends IInterface {
    public static final String DESCRIPTOR = "android.view.IDisplayWindowListener";

    void onDisplayAdded(int i) throws RemoteException;

    void onDisplayConfigurationChanged(int i, Configuration configuration) throws RemoteException;

    void onDisplayRemoved(int i) throws RemoteException;

    void onFixedRotationFinished(int i) throws RemoteException;

    void onFixedRotationStarted(int i, int i2) throws RemoteException;

    void onKeepClearAreasChanged(int i, List<Rect> list, List<Rect> list2) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IDisplayWindowListener {
        @Override // android.view.IDisplayWindowListener
        public void onDisplayAdded(int displayId) throws RemoteException {
        }

        @Override // android.view.IDisplayWindowListener
        public void onDisplayConfigurationChanged(int displayId, Configuration newConfig) throws RemoteException {
        }

        @Override // android.view.IDisplayWindowListener
        public void onDisplayRemoved(int displayId) throws RemoteException {
        }

        @Override // android.view.IDisplayWindowListener
        public void onFixedRotationStarted(int displayId, int newRotation) throws RemoteException {
        }

        @Override // android.view.IDisplayWindowListener
        public void onFixedRotationFinished(int displayId) throws RemoteException {
        }

        @Override // android.view.IDisplayWindowListener
        public void onKeepClearAreasChanged(int displayId, List<Rect> restricted, List<Rect> unrestricted) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IDisplayWindowListener {
        static final int TRANSACTION_onDisplayAdded = 1;
        static final int TRANSACTION_onDisplayConfigurationChanged = 2;
        static final int TRANSACTION_onDisplayRemoved = 3;
        static final int TRANSACTION_onFixedRotationFinished = 5;
        static final int TRANSACTION_onFixedRotationStarted = 4;
        static final int TRANSACTION_onKeepClearAreasChanged = 6;

        public Stub() {
            attachInterface(this, IDisplayWindowListener.DESCRIPTOR);
        }

        public static IDisplayWindowListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IDisplayWindowListener.DESCRIPTOR);
            if (iin != null && (iin instanceof IDisplayWindowListener)) {
                return (IDisplayWindowListener) iin;
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
                    return "onDisplayAdded";
                case 2:
                    return "onDisplayConfigurationChanged";
                case 3:
                    return "onDisplayRemoved";
                case 4:
                    return "onFixedRotationStarted";
                case 5:
                    return "onFixedRotationFinished";
                case 6:
                    return "onKeepClearAreasChanged";
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
                data.enforceInterface(IDisplayWindowListener.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IDisplayWindowListener.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            onDisplayAdded(_arg0);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            Configuration _arg1 = (Configuration) data.readTypedObject(Configuration.CREATOR);
                            data.enforceNoDataAvail();
                            onDisplayConfigurationChanged(_arg02, _arg1);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            onDisplayRemoved(_arg03);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            onFixedRotationStarted(_arg04, _arg12);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            onFixedRotationFinished(_arg05);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            List<Rect> _arg13 = data.createTypedArrayList(Rect.CREATOR);
                            List<Rect> _arg2 = data.createTypedArrayList(Rect.CREATOR);
                            data.enforceNoDataAvail();
                            onKeepClearAreasChanged(_arg06, _arg13, _arg2);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class Proxy implements IDisplayWindowListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IDisplayWindowListener.DESCRIPTOR;
            }

            @Override // android.view.IDisplayWindowListener
            public void onDisplayAdded(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IDisplayWindowListener.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.IDisplayWindowListener
            public void onDisplayConfigurationChanged(int displayId, Configuration newConfig) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IDisplayWindowListener.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeTypedObject(newConfig, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.IDisplayWindowListener
            public void onDisplayRemoved(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IDisplayWindowListener.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.IDisplayWindowListener
            public void onFixedRotationStarted(int displayId, int newRotation) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IDisplayWindowListener.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeInt(newRotation);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.IDisplayWindowListener
            public void onFixedRotationFinished(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IDisplayWindowListener.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.IDisplayWindowListener
            public void onKeepClearAreasChanged(int displayId, List<Rect> restricted, List<Rect> unrestricted) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IDisplayWindowListener.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeTypedList(restricted, 0);
                    _data.writeTypedList(unrestricted, 0);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 5;
        }
    }
}
