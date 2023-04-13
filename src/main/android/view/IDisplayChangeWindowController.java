package android.view;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.view.IDisplayChangeWindowCallback;
import android.window.DisplayAreaInfo;
/* loaded from: classes4.dex */
public interface IDisplayChangeWindowController extends IInterface {
    public static final String DESCRIPTOR = "android.view.IDisplayChangeWindowController";

    void onDisplayChange(int i, int i2, int i3, DisplayAreaInfo displayAreaInfo, IDisplayChangeWindowCallback iDisplayChangeWindowCallback) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IDisplayChangeWindowController {
        @Override // android.view.IDisplayChangeWindowController
        public void onDisplayChange(int displayId, int fromRotation, int toRotation, DisplayAreaInfo newDisplayAreaInfo, IDisplayChangeWindowCallback callback) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IDisplayChangeWindowController {
        static final int TRANSACTION_onDisplayChange = 1;

        public Stub() {
            attachInterface(this, IDisplayChangeWindowController.DESCRIPTOR);
        }

        public static IDisplayChangeWindowController asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IDisplayChangeWindowController.DESCRIPTOR);
            if (iin != null && (iin instanceof IDisplayChangeWindowController)) {
                return (IDisplayChangeWindowController) iin;
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
                    return "onDisplayChange";
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
                data.enforceInterface(IDisplayChangeWindowController.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IDisplayChangeWindowController.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            int _arg1 = data.readInt();
                            int _arg2 = data.readInt();
                            DisplayAreaInfo _arg3 = (DisplayAreaInfo) data.readTypedObject(DisplayAreaInfo.CREATOR);
                            IDisplayChangeWindowCallback _arg4 = IDisplayChangeWindowCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onDisplayChange(_arg0, _arg1, _arg2, _arg3, _arg4);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class Proxy implements IDisplayChangeWindowController {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IDisplayChangeWindowController.DESCRIPTOR;
            }

            @Override // android.view.IDisplayChangeWindowController
            public void onDisplayChange(int displayId, int fromRotation, int toRotation, DisplayAreaInfo newDisplayAreaInfo, IDisplayChangeWindowCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IDisplayChangeWindowController.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeInt(fromRotation);
                    _data.writeInt(toRotation);
                    _data.writeTypedObject(newDisplayAreaInfo, 0);
                    _data.writeStrongInterface(callback);
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
