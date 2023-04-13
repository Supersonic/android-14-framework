package android.telephony.ims.aidl;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telephony.ims.SrvccCall;
import java.util.List;
/* loaded from: classes3.dex */
public interface ISrvccStartedCallback extends IInterface {
    public static final String DESCRIPTOR = "android.telephony.ims.aidl.ISrvccStartedCallback";

    void onSrvccCallNotified(List<SrvccCall> list) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ISrvccStartedCallback {
        @Override // android.telephony.ims.aidl.ISrvccStartedCallback
        public void onSrvccCallNotified(List<SrvccCall> profiles) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ISrvccStartedCallback {
        static final int TRANSACTION_onSrvccCallNotified = 1;

        public Stub() {
            attachInterface(this, ISrvccStartedCallback.DESCRIPTOR);
        }

        public static ISrvccStartedCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ISrvccStartedCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof ISrvccStartedCallback)) {
                return (ISrvccStartedCallback) iin;
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
                    return "onSrvccCallNotified";
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
                data.enforceInterface(ISrvccStartedCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ISrvccStartedCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            List<SrvccCall> _arg0 = data.createTypedArrayList(SrvccCall.CREATOR);
                            data.enforceNoDataAvail();
                            onSrvccCallNotified(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements ISrvccStartedCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISrvccStartedCallback.DESCRIPTOR;
            }

            @Override // android.telephony.ims.aidl.ISrvccStartedCallback
            public void onSrvccCallNotified(List<SrvccCall> profiles) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISrvccStartedCallback.DESCRIPTOR);
                    _data.writeTypedList(profiles, 0);
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
