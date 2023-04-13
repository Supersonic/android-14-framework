package android.service.timezone;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface ITimeZoneProviderManager extends IInterface {
    public static final String DESCRIPTOR = "android.service.timezone.ITimeZoneProviderManager";

    void onTimeZoneProviderEvent(TimeZoneProviderEvent timeZoneProviderEvent) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ITimeZoneProviderManager {
        @Override // android.service.timezone.ITimeZoneProviderManager
        public void onTimeZoneProviderEvent(TimeZoneProviderEvent timeZoneProviderEvent) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ITimeZoneProviderManager {
        static final int TRANSACTION_onTimeZoneProviderEvent = 1;

        public Stub() {
            attachInterface(this, ITimeZoneProviderManager.DESCRIPTOR);
        }

        public static ITimeZoneProviderManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ITimeZoneProviderManager.DESCRIPTOR);
            if (iin != null && (iin instanceof ITimeZoneProviderManager)) {
                return (ITimeZoneProviderManager) iin;
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
                    return "onTimeZoneProviderEvent";
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
                data.enforceInterface(ITimeZoneProviderManager.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ITimeZoneProviderManager.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            TimeZoneProviderEvent _arg0 = (TimeZoneProviderEvent) data.readTypedObject(TimeZoneProviderEvent.CREATOR);
                            data.enforceNoDataAvail();
                            onTimeZoneProviderEvent(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements ITimeZoneProviderManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ITimeZoneProviderManager.DESCRIPTOR;
            }

            @Override // android.service.timezone.ITimeZoneProviderManager
            public void onTimeZoneProviderEvent(TimeZoneProviderEvent timeZoneProviderEvent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITimeZoneProviderManager.DESCRIPTOR);
                    _data.writeTypedObject(timeZoneProviderEvent, 0);
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
