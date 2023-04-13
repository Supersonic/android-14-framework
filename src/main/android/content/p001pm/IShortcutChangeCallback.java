package android.content.p001pm;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.p008os.UserHandle;
import java.util.List;
/* renamed from: android.content.pm.IShortcutChangeCallback */
/* loaded from: classes.dex */
public interface IShortcutChangeCallback extends IInterface {
    public static final String DESCRIPTOR = "android.content.pm.IShortcutChangeCallback";

    void onShortcutsAddedOrUpdated(String str, List<ShortcutInfo> list, UserHandle userHandle) throws RemoteException;

    void onShortcutsRemoved(String str, List<ShortcutInfo> list, UserHandle userHandle) throws RemoteException;

    /* renamed from: android.content.pm.IShortcutChangeCallback$Default */
    /* loaded from: classes.dex */
    public static class Default implements IShortcutChangeCallback {
        @Override // android.content.p001pm.IShortcutChangeCallback
        public void onShortcutsAddedOrUpdated(String packageName, List<ShortcutInfo> shortcuts, UserHandle user) throws RemoteException {
        }

        @Override // android.content.p001pm.IShortcutChangeCallback
        public void onShortcutsRemoved(String packageName, List<ShortcutInfo> shortcuts, UserHandle user) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.content.pm.IShortcutChangeCallback$Stub */
    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IShortcutChangeCallback {
        static final int TRANSACTION_onShortcutsAddedOrUpdated = 1;
        static final int TRANSACTION_onShortcutsRemoved = 2;

        public Stub() {
            attachInterface(this, IShortcutChangeCallback.DESCRIPTOR);
        }

        public static IShortcutChangeCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IShortcutChangeCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IShortcutChangeCallback)) {
                return (IShortcutChangeCallback) iin;
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
                    return "onShortcutsAddedOrUpdated";
                case 2:
                    return "onShortcutsRemoved";
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
                data.enforceInterface(IShortcutChangeCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IShortcutChangeCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            List<ShortcutInfo> _arg1 = data.createTypedArrayList(ShortcutInfo.CREATOR);
                            UserHandle _arg2 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            data.enforceNoDataAvail();
                            onShortcutsAddedOrUpdated(_arg0, _arg1, _arg2);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            List<ShortcutInfo> _arg12 = data.createTypedArrayList(ShortcutInfo.CREATOR);
                            UserHandle _arg22 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            data.enforceNoDataAvail();
                            onShortcutsRemoved(_arg02, _arg12, _arg22);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.content.pm.IShortcutChangeCallback$Stub$Proxy */
        /* loaded from: classes.dex */
        private static class Proxy implements IShortcutChangeCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IShortcutChangeCallback.DESCRIPTOR;
            }

            @Override // android.content.p001pm.IShortcutChangeCallback
            public void onShortcutsAddedOrUpdated(String packageName, List<ShortcutInfo> shortcuts, UserHandle user) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IShortcutChangeCallback.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeTypedList(shortcuts, 0);
                    _data.writeTypedObject(user, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.content.p001pm.IShortcutChangeCallback
            public void onShortcutsRemoved(String packageName, List<ShortcutInfo> shortcuts, UserHandle user) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IShortcutChangeCallback.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeTypedList(shortcuts, 0);
                    _data.writeTypedObject(user, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 1;
        }
    }
}
