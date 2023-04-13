package android.media;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface ISpatializerHeadToSoundStagePoseCallback extends IInterface {
    public static final String DESCRIPTOR = "android.media.ISpatializerHeadToSoundStagePoseCallback";

    void dispatchPoseChanged(float[] fArr) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements ISpatializerHeadToSoundStagePoseCallback {
        @Override // android.media.ISpatializerHeadToSoundStagePoseCallback
        public void dispatchPoseChanged(float[] pose) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ISpatializerHeadToSoundStagePoseCallback {
        static final int TRANSACTION_dispatchPoseChanged = 1;

        public Stub() {
            attachInterface(this, ISpatializerHeadToSoundStagePoseCallback.DESCRIPTOR);
        }

        public static ISpatializerHeadToSoundStagePoseCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ISpatializerHeadToSoundStagePoseCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof ISpatializerHeadToSoundStagePoseCallback)) {
                return (ISpatializerHeadToSoundStagePoseCallback) iin;
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
                    return "dispatchPoseChanged";
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
                data.enforceInterface(ISpatializerHeadToSoundStagePoseCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ISpatializerHeadToSoundStagePoseCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            float[] _arg0 = data.createFloatArray();
                            data.enforceNoDataAvail();
                            dispatchPoseChanged(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements ISpatializerHeadToSoundStagePoseCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISpatializerHeadToSoundStagePoseCallback.DESCRIPTOR;
            }

            @Override // android.media.ISpatializerHeadToSoundStagePoseCallback
            public void dispatchPoseChanged(float[] pose) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISpatializerHeadToSoundStagePoseCallback.DESCRIPTOR);
                    _data.writeFloatArray(pose);
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
