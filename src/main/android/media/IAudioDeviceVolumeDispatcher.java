package android.media;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IAudioDeviceVolumeDispatcher extends IInterface {
    public static final String DESCRIPTOR = "android.media.IAudioDeviceVolumeDispatcher";

    void dispatchDeviceVolumeAdjusted(AudioDeviceAttributes audioDeviceAttributes, VolumeInfo volumeInfo, int i, int i2) throws RemoteException;

    void dispatchDeviceVolumeChanged(AudioDeviceAttributes audioDeviceAttributes, VolumeInfo volumeInfo) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IAudioDeviceVolumeDispatcher {
        @Override // android.media.IAudioDeviceVolumeDispatcher
        public void dispatchDeviceVolumeChanged(AudioDeviceAttributes device, VolumeInfo vol) throws RemoteException {
        }

        @Override // android.media.IAudioDeviceVolumeDispatcher
        public void dispatchDeviceVolumeAdjusted(AudioDeviceAttributes device, VolumeInfo vol, int direction, int mode) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IAudioDeviceVolumeDispatcher {
        static final int TRANSACTION_dispatchDeviceVolumeAdjusted = 2;
        static final int TRANSACTION_dispatchDeviceVolumeChanged = 1;

        public Stub() {
            attachInterface(this, IAudioDeviceVolumeDispatcher.DESCRIPTOR);
        }

        public static IAudioDeviceVolumeDispatcher asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IAudioDeviceVolumeDispatcher.DESCRIPTOR);
            if (iin != null && (iin instanceof IAudioDeviceVolumeDispatcher)) {
                return (IAudioDeviceVolumeDispatcher) iin;
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
                    return "dispatchDeviceVolumeChanged";
                case 2:
                    return "dispatchDeviceVolumeAdjusted";
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
                data.enforceInterface(IAudioDeviceVolumeDispatcher.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IAudioDeviceVolumeDispatcher.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            AudioDeviceAttributes _arg0 = (AudioDeviceAttributes) data.readTypedObject(AudioDeviceAttributes.CREATOR);
                            VolumeInfo _arg1 = (VolumeInfo) data.readTypedObject(VolumeInfo.CREATOR);
                            data.enforceNoDataAvail();
                            dispatchDeviceVolumeChanged(_arg0, _arg1);
                            break;
                        case 2:
                            AudioDeviceAttributes _arg02 = (AudioDeviceAttributes) data.readTypedObject(AudioDeviceAttributes.CREATOR);
                            VolumeInfo _arg12 = (VolumeInfo) data.readTypedObject(VolumeInfo.CREATOR);
                            int _arg2 = data.readInt();
                            int _arg3 = data.readInt();
                            data.enforceNoDataAvail();
                            dispatchDeviceVolumeAdjusted(_arg02, _arg12, _arg2, _arg3);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements IAudioDeviceVolumeDispatcher {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IAudioDeviceVolumeDispatcher.DESCRIPTOR;
            }

            @Override // android.media.IAudioDeviceVolumeDispatcher
            public void dispatchDeviceVolumeChanged(AudioDeviceAttributes device, VolumeInfo vol) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IAudioDeviceVolumeDispatcher.DESCRIPTOR);
                    _data.writeTypedObject(device, 0);
                    _data.writeTypedObject(vol, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioDeviceVolumeDispatcher
            public void dispatchDeviceVolumeAdjusted(AudioDeviceAttributes device, VolumeInfo vol, int direction, int mode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IAudioDeviceVolumeDispatcher.DESCRIPTOR);
                    _data.writeTypedObject(device, 0);
                    _data.writeTypedObject(vol, 0);
                    _data.writeInt(direction);
                    _data.writeInt(mode);
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
