package android.hardware.usb;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IDisplayPortAltModeInfoListener extends IInterface {
    public static final String DESCRIPTOR = "android.hardware.usb.IDisplayPortAltModeInfoListener";

    void onDisplayPortAltModeInfoChanged(String str, DisplayPortAltModeInfo displayPortAltModeInfo) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IDisplayPortAltModeInfoListener {
        @Override // android.hardware.usb.IDisplayPortAltModeInfoListener
        public void onDisplayPortAltModeInfoChanged(String portId, DisplayPortAltModeInfo DisplayPortAltModeInfo) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IDisplayPortAltModeInfoListener {
        static final int TRANSACTION_onDisplayPortAltModeInfoChanged = 1;

        public Stub() {
            attachInterface(this, IDisplayPortAltModeInfoListener.DESCRIPTOR);
        }

        public static IDisplayPortAltModeInfoListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IDisplayPortAltModeInfoListener.DESCRIPTOR);
            if (iin != null && (iin instanceof IDisplayPortAltModeInfoListener)) {
                return (IDisplayPortAltModeInfoListener) iin;
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
                    return "onDisplayPortAltModeInfoChanged";
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
                data.enforceInterface(IDisplayPortAltModeInfoListener.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IDisplayPortAltModeInfoListener.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            DisplayPortAltModeInfo _arg1 = (DisplayPortAltModeInfo) data.readTypedObject(DisplayPortAltModeInfo.CREATOR);
                            data.enforceNoDataAvail();
                            onDisplayPortAltModeInfoChanged(_arg0, _arg1);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IDisplayPortAltModeInfoListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IDisplayPortAltModeInfoListener.DESCRIPTOR;
            }

            @Override // android.hardware.usb.IDisplayPortAltModeInfoListener
            public void onDisplayPortAltModeInfoChanged(String portId, DisplayPortAltModeInfo DisplayPortAltModeInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IDisplayPortAltModeInfoListener.DESCRIPTOR);
                    _data.writeString(portId);
                    _data.writeTypedObject(DisplayPortAltModeInfo, 0);
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
