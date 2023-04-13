package android.media;

import android.media.audio.common.AudioConfigBase;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IAudioPolicyServiceClient extends IInterface {
    public static final String DESCRIPTOR = "android$media$IAudioPolicyServiceClient".replace('$', '.');

    void onAudioPatchListUpdate() throws RemoteException;

    void onAudioPortListUpdate() throws RemoteException;

    void onAudioVolumeGroupChanged(int i, int i2) throws RemoteException;

    void onDynamicPolicyMixStateUpdate(String str, int i) throws RemoteException;

    void onRecordingConfigurationUpdate(int i, RecordClientInfo recordClientInfo, AudioConfigBase audioConfigBase, EffectDescriptor[] effectDescriptorArr, AudioConfigBase audioConfigBase2, EffectDescriptor[] effectDescriptorArr2, int i2, int i3) throws RemoteException;

    void onRoutingUpdated() throws RemoteException;

    void onVolumeRangeInitRequest() throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IAudioPolicyServiceClient {
        @Override // android.media.IAudioPolicyServiceClient
        public void onAudioVolumeGroupChanged(int group, int flags) throws RemoteException {
        }

        @Override // android.media.IAudioPolicyServiceClient
        public void onAudioPortListUpdate() throws RemoteException {
        }

        @Override // android.media.IAudioPolicyServiceClient
        public void onAudioPatchListUpdate() throws RemoteException {
        }

        @Override // android.media.IAudioPolicyServiceClient
        public void onDynamicPolicyMixStateUpdate(String regId, int state) throws RemoteException {
        }

        @Override // android.media.IAudioPolicyServiceClient
        public void onRecordingConfigurationUpdate(int event, RecordClientInfo clientInfo, AudioConfigBase clientConfig, EffectDescriptor[] clientEffects, AudioConfigBase deviceConfig, EffectDescriptor[] effects, int patchHandle, int source) throws RemoteException {
        }

        @Override // android.media.IAudioPolicyServiceClient
        public void onRoutingUpdated() throws RemoteException {
        }

        @Override // android.media.IAudioPolicyServiceClient
        public void onVolumeRangeInitRequest() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IAudioPolicyServiceClient {
        static final int TRANSACTION_onAudioPatchListUpdate = 3;
        static final int TRANSACTION_onAudioPortListUpdate = 2;
        static final int TRANSACTION_onAudioVolumeGroupChanged = 1;
        static final int TRANSACTION_onDynamicPolicyMixStateUpdate = 4;
        static final int TRANSACTION_onRecordingConfigurationUpdate = 5;
        static final int TRANSACTION_onRoutingUpdated = 6;
        static final int TRANSACTION_onVolumeRangeInitRequest = 7;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAudioPolicyServiceClient asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IAudioPolicyServiceClient)) {
                return (IAudioPolicyServiceClient) iin;
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
                            int _arg0 = data.readInt();
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            onAudioVolumeGroupChanged(_arg0, _arg1);
                            break;
                        case 2:
                            onAudioPortListUpdate();
                            break;
                        case 3:
                            onAudioPatchListUpdate();
                            break;
                        case 4:
                            String _arg02 = data.readString();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            onDynamicPolicyMixStateUpdate(_arg02, _arg12);
                            break;
                        case 5:
                            int _arg03 = data.readInt();
                            RecordClientInfo _arg13 = (RecordClientInfo) data.readTypedObject(RecordClientInfo.CREATOR);
                            AudioConfigBase _arg2 = (AudioConfigBase) data.readTypedObject(AudioConfigBase.CREATOR);
                            EffectDescriptor[] _arg3 = (EffectDescriptor[]) data.createTypedArray(EffectDescriptor.CREATOR);
                            AudioConfigBase _arg4 = (AudioConfigBase) data.readTypedObject(AudioConfigBase.CREATOR);
                            EffectDescriptor[] _arg5 = (EffectDescriptor[]) data.createTypedArray(EffectDescriptor.CREATOR);
                            int _arg6 = data.readInt();
                            int _arg7 = data.readInt();
                            data.enforceNoDataAvail();
                            onRecordingConfigurationUpdate(_arg03, _arg13, _arg2, _arg3, _arg4, _arg5, _arg6, _arg7);
                            break;
                        case 6:
                            onRoutingUpdated();
                            break;
                        case 7:
                            onVolumeRangeInitRequest();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IAudioPolicyServiceClient {
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

            @Override // android.media.IAudioPolicyServiceClient
            public void onAudioVolumeGroupChanged(int group, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(group);
                    _data.writeInt(flags);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioPolicyServiceClient
            public void onAudioPortListUpdate() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioPolicyServiceClient
            public void onAudioPatchListUpdate() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioPolicyServiceClient
            public void onDynamicPolicyMixStateUpdate(String regId, int state) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(regId);
                    _data.writeInt(state);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioPolicyServiceClient
            public void onRecordingConfigurationUpdate(int event, RecordClientInfo clientInfo, AudioConfigBase clientConfig, EffectDescriptor[] clientEffects, AudioConfigBase deviceConfig, EffectDescriptor[] effects, int patchHandle, int source) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(event);
                    _data.writeTypedObject(clientInfo, 0);
                    _data.writeTypedObject(clientConfig, 0);
                    _data.writeTypedArray(clientEffects, 0);
                    _data.writeTypedObject(deviceConfig, 0);
                    _data.writeTypedArray(effects, 0);
                    _data.writeInt(patchHandle);
                    _data.writeInt(source);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioPolicyServiceClient
            public void onRoutingUpdated() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioPolicyServiceClient
            public void onVolumeRangeInitRequest() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }
    }
}
