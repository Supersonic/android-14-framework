package android.media.audiopolicy;

import android.media.AudioFocusInfo;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IAudioPolicyCallback extends IInterface {
    void notifyAudioFocusAbandon(AudioFocusInfo audioFocusInfo) throws RemoteException;

    void notifyAudioFocusGrant(AudioFocusInfo audioFocusInfo, int i) throws RemoteException;

    void notifyAudioFocusLoss(AudioFocusInfo audioFocusInfo, boolean z) throws RemoteException;

    void notifyAudioFocusRequest(AudioFocusInfo audioFocusInfo, int i) throws RemoteException;

    void notifyMixStateUpdate(String str, int i) throws RemoteException;

    void notifyUnregistration() throws RemoteException;

    void notifyVolumeAdjust(int i) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IAudioPolicyCallback {
        @Override // android.media.audiopolicy.IAudioPolicyCallback
        public void notifyAudioFocusGrant(AudioFocusInfo afi, int requestResult) throws RemoteException {
        }

        @Override // android.media.audiopolicy.IAudioPolicyCallback
        public void notifyAudioFocusLoss(AudioFocusInfo afi, boolean wasNotified) throws RemoteException {
        }

        @Override // android.media.audiopolicy.IAudioPolicyCallback
        public void notifyAudioFocusRequest(AudioFocusInfo afi, int requestResult) throws RemoteException {
        }

        @Override // android.media.audiopolicy.IAudioPolicyCallback
        public void notifyAudioFocusAbandon(AudioFocusInfo afi) throws RemoteException {
        }

        @Override // android.media.audiopolicy.IAudioPolicyCallback
        public void notifyMixStateUpdate(String regId, int state) throws RemoteException {
        }

        @Override // android.media.audiopolicy.IAudioPolicyCallback
        public void notifyVolumeAdjust(int adjustment) throws RemoteException {
        }

        @Override // android.media.audiopolicy.IAudioPolicyCallback
        public void notifyUnregistration() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IAudioPolicyCallback {
        public static final String DESCRIPTOR = "android.media.audiopolicy.IAudioPolicyCallback";
        static final int TRANSACTION_notifyAudioFocusAbandon = 4;
        static final int TRANSACTION_notifyAudioFocusGrant = 1;
        static final int TRANSACTION_notifyAudioFocusLoss = 2;
        static final int TRANSACTION_notifyAudioFocusRequest = 3;
        static final int TRANSACTION_notifyMixStateUpdate = 5;
        static final int TRANSACTION_notifyUnregistration = 7;
        static final int TRANSACTION_notifyVolumeAdjust = 6;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAudioPolicyCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IAudioPolicyCallback)) {
                return (IAudioPolicyCallback) iin;
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
                    return "notifyAudioFocusGrant";
                case 2:
                    return "notifyAudioFocusLoss";
                case 3:
                    return "notifyAudioFocusRequest";
                case 4:
                    return "notifyAudioFocusAbandon";
                case 5:
                    return "notifyMixStateUpdate";
                case 6:
                    return "notifyVolumeAdjust";
                case 7:
                    return "notifyUnregistration";
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
                            AudioFocusInfo _arg0 = (AudioFocusInfo) data.readTypedObject(AudioFocusInfo.CREATOR);
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyAudioFocusGrant(_arg0, _arg1);
                            break;
                        case 2:
                            AudioFocusInfo _arg02 = (AudioFocusInfo) data.readTypedObject(AudioFocusInfo.CREATOR);
                            boolean _arg12 = data.readBoolean();
                            data.enforceNoDataAvail();
                            notifyAudioFocusLoss(_arg02, _arg12);
                            break;
                        case 3:
                            AudioFocusInfo _arg03 = (AudioFocusInfo) data.readTypedObject(AudioFocusInfo.CREATOR);
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyAudioFocusRequest(_arg03, _arg13);
                            break;
                        case 4:
                            AudioFocusInfo _arg04 = (AudioFocusInfo) data.readTypedObject(AudioFocusInfo.CREATOR);
                            data.enforceNoDataAvail();
                            notifyAudioFocusAbandon(_arg04);
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyMixStateUpdate(_arg05, _arg14);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyVolumeAdjust(_arg06);
                            break;
                        case 7:
                            notifyUnregistration();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements IAudioPolicyCallback {
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

            @Override // android.media.audiopolicy.IAudioPolicyCallback
            public void notifyAudioFocusGrant(AudioFocusInfo afi, int requestResult) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(afi, 0);
                    _data.writeInt(requestResult);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.audiopolicy.IAudioPolicyCallback
            public void notifyAudioFocusLoss(AudioFocusInfo afi, boolean wasNotified) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(afi, 0);
                    _data.writeBoolean(wasNotified);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.audiopolicy.IAudioPolicyCallback
            public void notifyAudioFocusRequest(AudioFocusInfo afi, int requestResult) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(afi, 0);
                    _data.writeInt(requestResult);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.audiopolicy.IAudioPolicyCallback
            public void notifyAudioFocusAbandon(AudioFocusInfo afi) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(afi, 0);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.audiopolicy.IAudioPolicyCallback
            public void notifyMixStateUpdate(String regId, int state) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(regId);
                    _data.writeInt(state);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.audiopolicy.IAudioPolicyCallback
            public void notifyVolumeAdjust(int adjustment) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(adjustment);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.audiopolicy.IAudioPolicyCallback
            public void notifyUnregistration() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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
