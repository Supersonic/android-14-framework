package android.content.p001pm;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* renamed from: android.content.pm.IStagedApexObserver */
/* loaded from: classes.dex */
public interface IStagedApexObserver extends IInterface {
    public static final String DESCRIPTOR = "android$content$pm$IStagedApexObserver".replace('$', '.');

    void onApexStaged(ApexStagedEvent apexStagedEvent) throws RemoteException;

    /* renamed from: android.content.pm.IStagedApexObserver$Default */
    /* loaded from: classes.dex */
    public static class Default implements IStagedApexObserver {
        @Override // android.content.p001pm.IStagedApexObserver
        public void onApexStaged(ApexStagedEvent event) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.content.pm.IStagedApexObserver$Stub */
    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IStagedApexObserver {
        static final int TRANSACTION_onApexStaged = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IStagedApexObserver asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IStagedApexObserver)) {
                return (IStagedApexObserver) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            String descriptor = DESCRIPTOR;
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(descriptor);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(descriptor);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            ApexStagedEvent _arg0 = (ApexStagedEvent) data.readTypedObject(ApexStagedEvent.CREATOR);
                            data.enforceNoDataAvail();
                            onApexStaged(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* renamed from: android.content.pm.IStagedApexObserver$Stub$Proxy */
        /* loaded from: classes.dex */
        private static class Proxy implements IStagedApexObserver {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override // android.content.p001pm.IStagedApexObserver
            public void onApexStaged(ApexStagedEvent event) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(event, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }
    }
}
