package android.app.contentsuggestions;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes.dex */
public interface IClassificationsCallback extends IInterface {
    public static final String DESCRIPTOR = "android.app.contentsuggestions.IClassificationsCallback";

    void onContentClassificationsAvailable(int i, List<ContentClassification> list) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IClassificationsCallback {
        @Override // android.app.contentsuggestions.IClassificationsCallback
        public void onContentClassificationsAvailable(int statusCode, List<ContentClassification> classifications) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IClassificationsCallback {
        static final int TRANSACTION_onContentClassificationsAvailable = 1;

        public Stub() {
            attachInterface(this, IClassificationsCallback.DESCRIPTOR);
        }

        public static IClassificationsCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IClassificationsCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IClassificationsCallback)) {
                return (IClassificationsCallback) iin;
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
                    return "onContentClassificationsAvailable";
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
                data.enforceInterface(IClassificationsCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IClassificationsCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            List<ContentClassification> _arg1 = data.createTypedArrayList(ContentClassification.CREATOR);
                            data.enforceNoDataAvail();
                            onContentClassificationsAvailable(_arg0, _arg1);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IClassificationsCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IClassificationsCallback.DESCRIPTOR;
            }

            @Override // android.app.contentsuggestions.IClassificationsCallback
            public void onContentClassificationsAvailable(int statusCode, List<ContentClassification> classifications) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IClassificationsCallback.DESCRIPTOR);
                    _data.writeInt(statusCode);
                    _data.writeTypedList(classifications, 0);
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
