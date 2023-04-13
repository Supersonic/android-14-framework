package android.companion.virtual.audio;

import android.media.AudioPlaybackConfiguration;
import android.media.AudioRecordingConfiguration;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes.dex */
public interface IAudioConfigChangedCallback extends IInterface {
    public static final String DESCRIPTOR = "android.companion.virtual.audio.IAudioConfigChangedCallback";

    void onPlaybackConfigChanged(List<AudioPlaybackConfiguration> list) throws RemoteException;

    void onRecordingConfigChanged(List<AudioRecordingConfiguration> list) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IAudioConfigChangedCallback {
        @Override // android.companion.virtual.audio.IAudioConfigChangedCallback
        public void onPlaybackConfigChanged(List<AudioPlaybackConfiguration> configs) throws RemoteException {
        }

        @Override // android.companion.virtual.audio.IAudioConfigChangedCallback
        public void onRecordingConfigChanged(List<AudioRecordingConfiguration> configs) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IAudioConfigChangedCallback {
        static final int TRANSACTION_onPlaybackConfigChanged = 1;
        static final int TRANSACTION_onRecordingConfigChanged = 2;

        public Stub() {
            attachInterface(this, IAudioConfigChangedCallback.DESCRIPTOR);
        }

        public static IAudioConfigChangedCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IAudioConfigChangedCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IAudioConfigChangedCallback)) {
                return (IAudioConfigChangedCallback) iin;
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
                    return "onPlaybackConfigChanged";
                case 2:
                    return "onRecordingConfigChanged";
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
                data.enforceInterface(IAudioConfigChangedCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IAudioConfigChangedCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            List<AudioPlaybackConfiguration> _arg0 = data.createTypedArrayList(AudioPlaybackConfiguration.CREATOR);
                            data.enforceNoDataAvail();
                            onPlaybackConfigChanged(_arg0);
                            break;
                        case 2:
                            List<AudioRecordingConfiguration> _arg02 = data.createTypedArrayList(AudioRecordingConfiguration.CREATOR);
                            data.enforceNoDataAvail();
                            onRecordingConfigChanged(_arg02);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class Proxy implements IAudioConfigChangedCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IAudioConfigChangedCallback.DESCRIPTOR;
            }

            @Override // android.companion.virtual.audio.IAudioConfigChangedCallback
            public void onPlaybackConfigChanged(List<AudioPlaybackConfiguration> configs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IAudioConfigChangedCallback.DESCRIPTOR);
                    _data.writeTypedList(configs, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.companion.virtual.audio.IAudioConfigChangedCallback
            public void onRecordingConfigChanged(List<AudioRecordingConfiguration> configs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IAudioConfigChangedCallback.DESCRIPTOR);
                    _data.writeTypedList(configs, 0);
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
