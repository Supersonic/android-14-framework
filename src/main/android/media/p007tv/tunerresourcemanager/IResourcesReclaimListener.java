package android.media.p007tv.tunerresourcemanager;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* renamed from: android.media.tv.tunerresourcemanager.IResourcesReclaimListener */
/* loaded from: classes2.dex */
public interface IResourcesReclaimListener extends IInterface {
    public static final String DESCRIPTOR = "android$media$tv$tunerresourcemanager$IResourcesReclaimListener".replace('$', '.');

    void onReclaimResources() throws RemoteException;

    /* renamed from: android.media.tv.tunerresourcemanager.IResourcesReclaimListener$Default */
    /* loaded from: classes2.dex */
    public static class Default implements IResourcesReclaimListener {
        @Override // android.media.p007tv.tunerresourcemanager.IResourcesReclaimListener
        public void onReclaimResources() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.media.tv.tunerresourcemanager.IResourcesReclaimListener$Stub */
    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IResourcesReclaimListener {
        static final int TRANSACTION_onReclaimResources = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IResourcesReclaimListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IResourcesReclaimListener)) {
                return (IResourcesReclaimListener) iin;
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
                            onReclaimResources();
                            reply.writeNoException();
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* renamed from: android.media.tv.tunerresourcemanager.IResourcesReclaimListener$Stub$Proxy */
        /* loaded from: classes2.dex */
        private static class Proxy implements IResourcesReclaimListener {
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

            @Override // android.media.p007tv.tunerresourcemanager.IResourcesReclaimListener
            public void onReclaimResources() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}
