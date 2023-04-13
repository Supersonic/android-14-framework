package android.media;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface ISoundDoseCallback extends IInterface {
    public static final String DESCRIPTOR = "android$media$ISoundDoseCallback".replace('$', '.');

    void onMomentaryExposure(float f, int i) throws RemoteException;

    void onNewCsdValue(float f, SoundDoseRecord[] soundDoseRecordArr) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements ISoundDoseCallback {
        @Override // android.media.ISoundDoseCallback
        public void onMomentaryExposure(float currentMel, int deviceId) throws RemoteException {
        }

        @Override // android.media.ISoundDoseCallback
        public void onNewCsdValue(float currentCsd, SoundDoseRecord[] records) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ISoundDoseCallback {
        static final int TRANSACTION_onMomentaryExposure = 1;
        static final int TRANSACTION_onNewCsdValue = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ISoundDoseCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ISoundDoseCallback)) {
                return (ISoundDoseCallback) iin;
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
                            float _arg0 = data.readFloat();
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            onMomentaryExposure(_arg0, _arg1);
                            break;
                        case 2:
                            float _arg02 = data.readFloat();
                            SoundDoseRecord[] _arg12 = (SoundDoseRecord[]) data.createTypedArray(SoundDoseRecord.CREATOR);
                            data.enforceNoDataAvail();
                            onNewCsdValue(_arg02, _arg12);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements ISoundDoseCallback {
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

            @Override // android.media.ISoundDoseCallback
            public void onMomentaryExposure(float currentMel, int deviceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeFloat(currentMel);
                    _data.writeInt(deviceId);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.ISoundDoseCallback
            public void onNewCsdValue(float currentCsd, SoundDoseRecord[] records) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeFloat(currentCsd);
                    _data.writeTypedArray(records, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }
    }
}
