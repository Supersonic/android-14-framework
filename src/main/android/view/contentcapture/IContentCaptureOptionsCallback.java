package android.view.contentcapture;

import android.content.ContentCaptureOptions;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes4.dex */
public interface IContentCaptureOptionsCallback extends IInterface {
    public static final String DESCRIPTOR = "android.view.contentcapture.IContentCaptureOptionsCallback";

    void setContentCaptureOptions(ContentCaptureOptions contentCaptureOptions) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IContentCaptureOptionsCallback {
        @Override // android.view.contentcapture.IContentCaptureOptionsCallback
        public void setContentCaptureOptions(ContentCaptureOptions options) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IContentCaptureOptionsCallback {
        static final int TRANSACTION_setContentCaptureOptions = 1;

        public Stub() {
            attachInterface(this, IContentCaptureOptionsCallback.DESCRIPTOR);
        }

        public static IContentCaptureOptionsCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IContentCaptureOptionsCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IContentCaptureOptionsCallback)) {
                return (IContentCaptureOptionsCallback) iin;
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
                    return "setContentCaptureOptions";
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
                data.enforceInterface(IContentCaptureOptionsCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IContentCaptureOptionsCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            ContentCaptureOptions _arg0 = (ContentCaptureOptions) data.readTypedObject(ContentCaptureOptions.CREATOR);
                            data.enforceNoDataAvail();
                            setContentCaptureOptions(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IContentCaptureOptionsCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IContentCaptureOptionsCallback.DESCRIPTOR;
            }

            @Override // android.view.contentcapture.IContentCaptureOptionsCallback
            public void setContentCaptureOptions(ContentCaptureOptions options) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IContentCaptureOptionsCallback.DESCRIPTOR);
                    _data.writeTypedObject(options, 0);
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
