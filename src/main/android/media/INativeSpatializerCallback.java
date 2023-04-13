package android.media;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface INativeSpatializerCallback extends IInterface {
    public static final String DESCRIPTOR = "android$media$INativeSpatializerCallback".replace('$', '.');

    void onLevelChanged(byte b) throws RemoteException;

    void onOutputChanged(int i) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements INativeSpatializerCallback {
        @Override // android.media.INativeSpatializerCallback
        public void onLevelChanged(byte level) throws RemoteException {
        }

        @Override // android.media.INativeSpatializerCallback
        public void onOutputChanged(int output) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements INativeSpatializerCallback {
        static final int TRANSACTION_onLevelChanged = 1;
        static final int TRANSACTION_onOutputChanged = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static INativeSpatializerCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof INativeSpatializerCallback)) {
                return (INativeSpatializerCallback) iin;
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
                            byte _arg0 = data.readByte();
                            data.enforceNoDataAvail();
                            onLevelChanged(_arg0);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            onOutputChanged(_arg02);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements INativeSpatializerCallback {
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

            @Override // android.media.INativeSpatializerCallback
            public void onLevelChanged(byte level) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeByte(level);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.INativeSpatializerCallback
            public void onOutputChanged(int output) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(output);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }
    }
}
