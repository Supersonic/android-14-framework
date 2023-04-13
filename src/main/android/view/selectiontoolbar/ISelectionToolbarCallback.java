package android.view.selectiontoolbar;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes4.dex */
public interface ISelectionToolbarCallback extends IInterface {
    public static final String DESCRIPTOR = "android.view.selectiontoolbar.ISelectionToolbarCallback";

    void onError(int i) throws RemoteException;

    void onMenuItemClicked(ToolbarMenuItem toolbarMenuItem) throws RemoteException;

    void onShown(WidgetInfo widgetInfo) throws RemoteException;

    void onToolbarShowTimeout() throws RemoteException;

    void onWidgetUpdated(WidgetInfo widgetInfo) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements ISelectionToolbarCallback {
        @Override // android.view.selectiontoolbar.ISelectionToolbarCallback
        public void onShown(WidgetInfo info) throws RemoteException {
        }

        @Override // android.view.selectiontoolbar.ISelectionToolbarCallback
        public void onWidgetUpdated(WidgetInfo info) throws RemoteException {
        }

        @Override // android.view.selectiontoolbar.ISelectionToolbarCallback
        public void onToolbarShowTimeout() throws RemoteException {
        }

        @Override // android.view.selectiontoolbar.ISelectionToolbarCallback
        public void onMenuItemClicked(ToolbarMenuItem item) throws RemoteException {
        }

        @Override // android.view.selectiontoolbar.ISelectionToolbarCallback
        public void onError(int errorCode) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements ISelectionToolbarCallback {
        static final int TRANSACTION_onError = 5;
        static final int TRANSACTION_onMenuItemClicked = 4;
        static final int TRANSACTION_onShown = 1;
        static final int TRANSACTION_onToolbarShowTimeout = 3;
        static final int TRANSACTION_onWidgetUpdated = 2;

        public Stub() {
            attachInterface(this, ISelectionToolbarCallback.DESCRIPTOR);
        }

        public static ISelectionToolbarCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ISelectionToolbarCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof ISelectionToolbarCallback)) {
                return (ISelectionToolbarCallback) iin;
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
                    return "onShown";
                case 2:
                    return "onWidgetUpdated";
                case 3:
                    return "onToolbarShowTimeout";
                case 4:
                    return "onMenuItemClicked";
                case 5:
                    return "onError";
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
                data.enforceInterface(ISelectionToolbarCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ISelectionToolbarCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            WidgetInfo _arg0 = (WidgetInfo) data.readTypedObject(WidgetInfo.CREATOR);
                            data.enforceNoDataAvail();
                            onShown(_arg0);
                            break;
                        case 2:
                            WidgetInfo _arg02 = (WidgetInfo) data.readTypedObject(WidgetInfo.CREATOR);
                            data.enforceNoDataAvail();
                            onWidgetUpdated(_arg02);
                            break;
                        case 3:
                            onToolbarShowTimeout();
                            break;
                        case 4:
                            ToolbarMenuItem _arg03 = (ToolbarMenuItem) data.readTypedObject(ToolbarMenuItem.CREATOR);
                            data.enforceNoDataAvail();
                            onMenuItemClicked(_arg03);
                            break;
                        case 5:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            onError(_arg04);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements ISelectionToolbarCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISelectionToolbarCallback.DESCRIPTOR;
            }

            @Override // android.view.selectiontoolbar.ISelectionToolbarCallback
            public void onShown(WidgetInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISelectionToolbarCallback.DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.selectiontoolbar.ISelectionToolbarCallback
            public void onWidgetUpdated(WidgetInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISelectionToolbarCallback.DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.selectiontoolbar.ISelectionToolbarCallback
            public void onToolbarShowTimeout() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISelectionToolbarCallback.DESCRIPTOR);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.selectiontoolbar.ISelectionToolbarCallback
            public void onMenuItemClicked(ToolbarMenuItem item) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISelectionToolbarCallback.DESCRIPTOR);
                    _data.writeTypedObject(item, 0);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.view.selectiontoolbar.ISelectionToolbarCallback
            public void onError(int errorCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISelectionToolbarCallback.DESCRIPTOR);
                    _data.writeInt(errorCode);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 4;
        }
    }
}
