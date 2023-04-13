package android.media;

import android.media.p007tv.interactive.TvInteractiveAppService;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IPlayer extends IInterface {
    void applyVolumeShaper(VolumeShaperConfiguration volumeShaperConfiguration, VolumeShaperOperation volumeShaperOperation) throws RemoteException;

    void pause() throws RemoteException;

    void setPan(float f) throws RemoteException;

    void setStartDelayMs(int i) throws RemoteException;

    void setVolume(float f) throws RemoteException;

    void start() throws RemoteException;

    void stop() throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IPlayer {
        @Override // android.media.IPlayer
        public void start() throws RemoteException {
        }

        @Override // android.media.IPlayer
        public void pause() throws RemoteException {
        }

        @Override // android.media.IPlayer
        public void stop() throws RemoteException {
        }

        @Override // android.media.IPlayer
        public void setVolume(float vol) throws RemoteException {
        }

        @Override // android.media.IPlayer
        public void setPan(float pan) throws RemoteException {
        }

        @Override // android.media.IPlayer
        public void setStartDelayMs(int delayMs) throws RemoteException {
        }

        @Override // android.media.IPlayer
        public void applyVolumeShaper(VolumeShaperConfiguration configuration, VolumeShaperOperation operation) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IPlayer {
        public static final String DESCRIPTOR = "android.media.IPlayer";
        static final int TRANSACTION_applyVolumeShaper = 7;
        static final int TRANSACTION_pause = 2;
        static final int TRANSACTION_setPan = 5;
        static final int TRANSACTION_setStartDelayMs = 6;
        static final int TRANSACTION_setVolume = 4;
        static final int TRANSACTION_start = 1;
        static final int TRANSACTION_stop = 3;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IPlayer asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IPlayer)) {
                return (IPlayer) iin;
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
                    return "start";
                case 2:
                    return TvInteractiveAppService.TIME_SHIFT_COMMAND_TYPE_PAUSE;
                case 3:
                    return "stop";
                case 4:
                    return "setVolume";
                case 5:
                    return "setPan";
                case 6:
                    return "setStartDelayMs";
                case 7:
                    return "applyVolumeShaper";
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
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            start();
                            break;
                        case 2:
                            pause();
                            break;
                        case 3:
                            stop();
                            break;
                        case 4:
                            float _arg0 = data.readFloat();
                            data.enforceNoDataAvail();
                            setVolume(_arg0);
                            break;
                        case 5:
                            float _arg02 = data.readFloat();
                            data.enforceNoDataAvail();
                            setPan(_arg02);
                            break;
                        case 6:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            setStartDelayMs(_arg03);
                            break;
                        case 7:
                            VolumeShaperConfiguration _arg04 = (VolumeShaperConfiguration) data.readTypedObject(VolumeShaperConfiguration.CREATOR);
                            VolumeShaperOperation _arg1 = (VolumeShaperOperation) data.readTypedObject(VolumeShaperOperation.CREATOR);
                            data.enforceNoDataAvail();
                            applyVolumeShaper(_arg04, _arg1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements IPlayer {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.media.IPlayer
            public void start() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IPlayer
            public void pause() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IPlayer
            public void stop() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IPlayer
            public void setVolume(float vol) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeFloat(vol);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IPlayer
            public void setPan(float pan) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeFloat(pan);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IPlayer
            public void setStartDelayMs(int delayMs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(delayMs);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IPlayer
            public void applyVolumeShaper(VolumeShaperConfiguration configuration, VolumeShaperOperation operation) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(configuration, 0);
                    _data.writeTypedObject(operation, 0);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 6;
        }
    }
}
