package android.media.p007tv.tunerresourcemanager;

import android.media.p007tv.tunerresourcemanager.IResourcesReclaimListener;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* renamed from: android.media.tv.tunerresourcemanager.ITunerResourceManager */
/* loaded from: classes2.dex */
public interface ITunerResourceManager extends IInterface {
    public static final String DESCRIPTOR = "android$media$tv$tunerresourcemanager$ITunerResourceManager".replace('$', '.');

    boolean acquireLock(int i, long j) throws RemoteException;

    void clearResourceMap(int i) throws RemoteException;

    int getClientPriority(int i, int i2) throws RemoteException;

    int getConfigPriority(int i, boolean z) throws RemoteException;

    int getMaxNumberOfFrontends(int i) throws RemoteException;

    boolean hasUnusedFrontend(int i) throws RemoteException;

    boolean isHigherPriority(ResourceClientProfile resourceClientProfile, ResourceClientProfile resourceClientProfile2) throws RemoteException;

    boolean isLowestPriority(int i, int i2) throws RemoteException;

    void registerClientProfile(ResourceClientProfile resourceClientProfile, IResourcesReclaimListener iResourcesReclaimListener, int[] iArr) throws RemoteException;

    void releaseCasSession(int i, int i2) throws RemoteException;

    void releaseCiCam(int i, int i2) throws RemoteException;

    void releaseDemux(int i, int i2) throws RemoteException;

    void releaseDescrambler(int i, int i2) throws RemoteException;

    void releaseFrontend(int i, int i2) throws RemoteException;

    void releaseLnb(int i, int i2) throws RemoteException;

    boolean releaseLock(int i) throws RemoteException;

    boolean requestCasSession(CasSessionRequest casSessionRequest, int[] iArr) throws RemoteException;

    boolean requestCiCam(TunerCiCamRequest tunerCiCamRequest, int[] iArr) throws RemoteException;

    boolean requestDemux(TunerDemuxRequest tunerDemuxRequest, int[] iArr) throws RemoteException;

    boolean requestDescrambler(TunerDescramblerRequest tunerDescramblerRequest, int[] iArr) throws RemoteException;

    boolean requestFrontend(TunerFrontendRequest tunerFrontendRequest, int[] iArr) throws RemoteException;

    boolean requestLnb(TunerLnbRequest tunerLnbRequest, int[] iArr) throws RemoteException;

    void restoreResourceMap(int i) throws RemoteException;

    void setDemuxInfoList(TunerDemuxInfo[] tunerDemuxInfoArr) throws RemoteException;

    void setFrontendInfoList(TunerFrontendInfo[] tunerFrontendInfoArr) throws RemoteException;

    void setLnbInfoList(int[] iArr) throws RemoteException;

    boolean setMaxNumberOfFrontends(int i, int i2) throws RemoteException;

    void shareFrontend(int i, int i2) throws RemoteException;

    void storeResourceMap(int i) throws RemoteException;

    boolean transferOwner(int i, int i2, int i3) throws RemoteException;

    void unregisterClientProfile(int i) throws RemoteException;

    void updateCasInfo(int i, int i2) throws RemoteException;

    boolean updateClientPriority(int i, int i2, int i3) throws RemoteException;

    /* renamed from: android.media.tv.tunerresourcemanager.ITunerResourceManager$Default */
    /* loaded from: classes2.dex */
    public static class Default implements ITunerResourceManager {
        @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
        public void registerClientProfile(ResourceClientProfile profile, IResourcesReclaimListener listener, int[] clientId) throws RemoteException {
        }

        @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
        public void unregisterClientProfile(int clientId) throws RemoteException {
        }

        @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
        public boolean updateClientPriority(int clientId, int priority, int niceValue) throws RemoteException {
            return false;
        }

        @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
        public boolean hasUnusedFrontend(int frontendType) throws RemoteException {
            return false;
        }

        @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
        public boolean isLowestPriority(int clientId, int frontendType) throws RemoteException {
            return false;
        }

        @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
        public void setFrontendInfoList(TunerFrontendInfo[] infos) throws RemoteException {
        }

        @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
        public void updateCasInfo(int casSystemId, int maxSessionNum) throws RemoteException {
        }

        @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
        public void setDemuxInfoList(TunerDemuxInfo[] infos) throws RemoteException {
        }

        @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
        public void setLnbInfoList(int[] lnbIds) throws RemoteException {
        }

        @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
        public boolean requestFrontend(TunerFrontendRequest request, int[] frontendHandle) throws RemoteException {
            return false;
        }

        @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
        public boolean setMaxNumberOfFrontends(int frontendType, int maxNum) throws RemoteException {
            return false;
        }

        @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
        public int getMaxNumberOfFrontends(int frontendType) throws RemoteException {
            return 0;
        }

        @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
        public void shareFrontend(int selfClientId, int targetClientId) throws RemoteException {
        }

        @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
        public boolean transferOwner(int resourceType, int currentOwnerId, int newOwnerId) throws RemoteException {
            return false;
        }

        @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
        public boolean requestDemux(TunerDemuxRequest request, int[] demuxHandle) throws RemoteException {
            return false;
        }

        @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
        public boolean requestDescrambler(TunerDescramblerRequest request, int[] descramblerHandle) throws RemoteException {
            return false;
        }

        @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
        public boolean requestCasSession(CasSessionRequest request, int[] casSessionHandle) throws RemoteException {
            return false;
        }

        @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
        public boolean requestCiCam(TunerCiCamRequest request, int[] ciCamHandle) throws RemoteException {
            return false;
        }

        @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
        public boolean requestLnb(TunerLnbRequest request, int[] lnbHandle) throws RemoteException {
            return false;
        }

        @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
        public void releaseFrontend(int frontendHandle, int clientId) throws RemoteException {
        }

        @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
        public void releaseDemux(int demuxHandle, int clientId) throws RemoteException {
        }

        @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
        public void releaseDescrambler(int descramblerHandle, int clientId) throws RemoteException {
        }

        @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
        public void releaseCasSession(int casSessionHandle, int clientId) throws RemoteException {
        }

        @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
        public void releaseCiCam(int ciCamHandle, int clientId) throws RemoteException {
        }

        @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
        public void releaseLnb(int lnbHandle, int clientId) throws RemoteException {
        }

        @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
        public boolean isHigherPriority(ResourceClientProfile challengerProfile, ResourceClientProfile holderProfile) throws RemoteException {
            return false;
        }

        @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
        public void storeResourceMap(int resourceType) throws RemoteException {
        }

        @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
        public void clearResourceMap(int resourceType) throws RemoteException {
        }

        @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
        public void restoreResourceMap(int resourceType) throws RemoteException {
        }

        @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
        public boolean acquireLock(int clientId, long clientThreadId) throws RemoteException {
            return false;
        }

        @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
        public boolean releaseLock(int clientId) throws RemoteException {
            return false;
        }

        @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
        public int getClientPriority(int useCase, int pid) throws RemoteException {
            return 0;
        }

        @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
        public int getConfigPriority(int useCase, boolean isForeground) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.media.tv.tunerresourcemanager.ITunerResourceManager$Stub */
    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ITunerResourceManager {
        static final int TRANSACTION_acquireLock = 30;
        static final int TRANSACTION_clearResourceMap = 28;
        static final int TRANSACTION_getClientPriority = 32;
        static final int TRANSACTION_getConfigPriority = 33;
        static final int TRANSACTION_getMaxNumberOfFrontends = 12;
        static final int TRANSACTION_hasUnusedFrontend = 4;
        static final int TRANSACTION_isHigherPriority = 26;
        static final int TRANSACTION_isLowestPriority = 5;
        static final int TRANSACTION_registerClientProfile = 1;
        static final int TRANSACTION_releaseCasSession = 23;
        static final int TRANSACTION_releaseCiCam = 24;
        static final int TRANSACTION_releaseDemux = 21;
        static final int TRANSACTION_releaseDescrambler = 22;
        static final int TRANSACTION_releaseFrontend = 20;
        static final int TRANSACTION_releaseLnb = 25;
        static final int TRANSACTION_releaseLock = 31;
        static final int TRANSACTION_requestCasSession = 17;
        static final int TRANSACTION_requestCiCam = 18;
        static final int TRANSACTION_requestDemux = 15;
        static final int TRANSACTION_requestDescrambler = 16;
        static final int TRANSACTION_requestFrontend = 10;
        static final int TRANSACTION_requestLnb = 19;
        static final int TRANSACTION_restoreResourceMap = 29;
        static final int TRANSACTION_setDemuxInfoList = 8;
        static final int TRANSACTION_setFrontendInfoList = 6;
        static final int TRANSACTION_setLnbInfoList = 9;
        static final int TRANSACTION_setMaxNumberOfFrontends = 11;
        static final int TRANSACTION_shareFrontend = 13;
        static final int TRANSACTION_storeResourceMap = 27;
        static final int TRANSACTION_transferOwner = 14;
        static final int TRANSACTION_unregisterClientProfile = 2;
        static final int TRANSACTION_updateCasInfo = 7;
        static final int TRANSACTION_updateClientPriority = 3;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ITunerResourceManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ITunerResourceManager)) {
                return (ITunerResourceManager) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            int[] _arg2;
            int[] _arg1;
            int[] _arg12;
            int[] _arg13;
            int[] _arg14;
            int[] _arg15;
            int[] _arg16;
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
                            ResourceClientProfile _arg0 = (ResourceClientProfile) data.readTypedObject(ResourceClientProfile.CREATOR);
                            IResourcesReclaimListener _arg17 = IResourcesReclaimListener.Stub.asInterface(data.readStrongBinder());
                            int _arg2_length = data.readInt();
                            if (_arg2_length < 0) {
                                _arg2 = null;
                            } else {
                                _arg2 = new int[_arg2_length];
                            }
                            data.enforceNoDataAvail();
                            registerClientProfile(_arg0, _arg17, _arg2);
                            reply.writeNoException();
                            reply.writeIntArray(_arg2);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            unregisterClientProfile(_arg02);
                            reply.writeNoException();
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            int _arg18 = data.readInt();
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result = updateClientPriority(_arg03, _arg18, _arg22);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result2 = hasUnusedFrontend(_arg04);
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            int _arg19 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result3 = isLowestPriority(_arg05, _arg19);
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            break;
                        case 6:
                            TunerFrontendInfo[] _arg06 = (TunerFrontendInfo[]) data.createTypedArray(TunerFrontendInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setFrontendInfoList(_arg06);
                            reply.writeNoException();
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            int _arg110 = data.readInt();
                            data.enforceNoDataAvail();
                            updateCasInfo(_arg07, _arg110);
                            reply.writeNoException();
                            break;
                        case 8:
                            TunerDemuxInfo[] _arg08 = (TunerDemuxInfo[]) data.createTypedArray(TunerDemuxInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setDemuxInfoList(_arg08);
                            reply.writeNoException();
                            break;
                        case 9:
                            int[] _arg09 = data.createIntArray();
                            data.enforceNoDataAvail();
                            setLnbInfoList(_arg09);
                            reply.writeNoException();
                            break;
                        case 10:
                            TunerFrontendRequest _arg010 = (TunerFrontendRequest) data.readTypedObject(TunerFrontendRequest.CREATOR);
                            int _arg1_length = data.readInt();
                            if (_arg1_length < 0) {
                                _arg1 = null;
                            } else {
                                _arg1 = new int[_arg1_length];
                            }
                            data.enforceNoDataAvail();
                            boolean _result4 = requestFrontend(_arg010, _arg1);
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            reply.writeIntArray(_arg1);
                            break;
                        case 11:
                            int _arg011 = data.readInt();
                            int _arg111 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result5 = setMaxNumberOfFrontends(_arg011, _arg111);
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            break;
                        case 12:
                            int _arg012 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result6 = getMaxNumberOfFrontends(_arg012);
                            reply.writeNoException();
                            reply.writeInt(_result6);
                            break;
                        case 13:
                            int _arg013 = data.readInt();
                            int _arg112 = data.readInt();
                            data.enforceNoDataAvail();
                            shareFrontend(_arg013, _arg112);
                            reply.writeNoException();
                            break;
                        case 14:
                            int _arg014 = data.readInt();
                            int _arg113 = data.readInt();
                            int _arg23 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result7 = transferOwner(_arg014, _arg113, _arg23);
                            reply.writeNoException();
                            reply.writeBoolean(_result7);
                            break;
                        case 15:
                            TunerDemuxRequest _arg015 = (TunerDemuxRequest) data.readTypedObject(TunerDemuxRequest.CREATOR);
                            int _arg1_length2 = data.readInt();
                            if (_arg1_length2 < 0) {
                                _arg12 = null;
                            } else {
                                _arg12 = new int[_arg1_length2];
                            }
                            data.enforceNoDataAvail();
                            boolean _result8 = requestDemux(_arg015, _arg12);
                            reply.writeNoException();
                            reply.writeBoolean(_result8);
                            reply.writeIntArray(_arg12);
                            break;
                        case 16:
                            TunerDescramblerRequest _arg016 = (TunerDescramblerRequest) data.readTypedObject(TunerDescramblerRequest.CREATOR);
                            int _arg1_length3 = data.readInt();
                            if (_arg1_length3 < 0) {
                                _arg13 = null;
                            } else {
                                _arg13 = new int[_arg1_length3];
                            }
                            data.enforceNoDataAvail();
                            boolean _result9 = requestDescrambler(_arg016, _arg13);
                            reply.writeNoException();
                            reply.writeBoolean(_result9);
                            reply.writeIntArray(_arg13);
                            break;
                        case 17:
                            CasSessionRequest _arg017 = (CasSessionRequest) data.readTypedObject(CasSessionRequest.CREATOR);
                            int _arg1_length4 = data.readInt();
                            if (_arg1_length4 < 0) {
                                _arg14 = null;
                            } else {
                                _arg14 = new int[_arg1_length4];
                            }
                            data.enforceNoDataAvail();
                            boolean _result10 = requestCasSession(_arg017, _arg14);
                            reply.writeNoException();
                            reply.writeBoolean(_result10);
                            reply.writeIntArray(_arg14);
                            break;
                        case 18:
                            TunerCiCamRequest _arg018 = (TunerCiCamRequest) data.readTypedObject(TunerCiCamRequest.CREATOR);
                            int _arg1_length5 = data.readInt();
                            if (_arg1_length5 < 0) {
                                _arg15 = null;
                            } else {
                                _arg15 = new int[_arg1_length5];
                            }
                            data.enforceNoDataAvail();
                            boolean _result11 = requestCiCam(_arg018, _arg15);
                            reply.writeNoException();
                            reply.writeBoolean(_result11);
                            reply.writeIntArray(_arg15);
                            break;
                        case 19:
                            TunerLnbRequest _arg019 = (TunerLnbRequest) data.readTypedObject(TunerLnbRequest.CREATOR);
                            int _arg1_length6 = data.readInt();
                            if (_arg1_length6 < 0) {
                                _arg16 = null;
                            } else {
                                _arg16 = new int[_arg1_length6];
                            }
                            data.enforceNoDataAvail();
                            boolean _result12 = requestLnb(_arg019, _arg16);
                            reply.writeNoException();
                            reply.writeBoolean(_result12);
                            reply.writeIntArray(_arg16);
                            break;
                        case 20:
                            int _arg020 = data.readInt();
                            int _arg114 = data.readInt();
                            data.enforceNoDataAvail();
                            releaseFrontend(_arg020, _arg114);
                            reply.writeNoException();
                            break;
                        case 21:
                            int _arg021 = data.readInt();
                            int _arg115 = data.readInt();
                            data.enforceNoDataAvail();
                            releaseDemux(_arg021, _arg115);
                            reply.writeNoException();
                            break;
                        case 22:
                            int _arg022 = data.readInt();
                            int _arg116 = data.readInt();
                            data.enforceNoDataAvail();
                            releaseDescrambler(_arg022, _arg116);
                            reply.writeNoException();
                            break;
                        case 23:
                            int _arg023 = data.readInt();
                            int _arg117 = data.readInt();
                            data.enforceNoDataAvail();
                            releaseCasSession(_arg023, _arg117);
                            reply.writeNoException();
                            break;
                        case 24:
                            int _arg024 = data.readInt();
                            int _arg118 = data.readInt();
                            data.enforceNoDataAvail();
                            releaseCiCam(_arg024, _arg118);
                            reply.writeNoException();
                            break;
                        case 25:
                            int _arg025 = data.readInt();
                            int _arg119 = data.readInt();
                            data.enforceNoDataAvail();
                            releaseLnb(_arg025, _arg119);
                            reply.writeNoException();
                            break;
                        case 26:
                            ResourceClientProfile _arg026 = (ResourceClientProfile) data.readTypedObject(ResourceClientProfile.CREATOR);
                            ResourceClientProfile _arg120 = (ResourceClientProfile) data.readTypedObject(ResourceClientProfile.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result13 = isHigherPriority(_arg026, _arg120);
                            reply.writeNoException();
                            reply.writeBoolean(_result13);
                            break;
                        case 27:
                            int _arg027 = data.readInt();
                            data.enforceNoDataAvail();
                            storeResourceMap(_arg027);
                            reply.writeNoException();
                            break;
                        case 28:
                            int _arg028 = data.readInt();
                            data.enforceNoDataAvail();
                            clearResourceMap(_arg028);
                            reply.writeNoException();
                            break;
                        case 29:
                            int _arg029 = data.readInt();
                            data.enforceNoDataAvail();
                            restoreResourceMap(_arg029);
                            reply.writeNoException();
                            break;
                        case 30:
                            int _arg030 = data.readInt();
                            long _arg121 = data.readLong();
                            data.enforceNoDataAvail();
                            boolean _result14 = acquireLock(_arg030, _arg121);
                            reply.writeNoException();
                            reply.writeBoolean(_result14);
                            break;
                        case 31:
                            int _arg031 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result15 = releaseLock(_arg031);
                            reply.writeNoException();
                            reply.writeBoolean(_result15);
                            break;
                        case 32:
                            int _arg032 = data.readInt();
                            int _arg122 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result16 = getClientPriority(_arg032, _arg122);
                            reply.writeNoException();
                            reply.writeInt(_result16);
                            break;
                        case 33:
                            int _arg033 = data.readInt();
                            boolean _arg123 = data.readBoolean();
                            data.enforceNoDataAvail();
                            int _result17 = getConfigPriority(_arg033, _arg123);
                            reply.writeNoException();
                            reply.writeInt(_result17);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.media.tv.tunerresourcemanager.ITunerResourceManager$Stub$Proxy */
        /* loaded from: classes2.dex */
        private static class Proxy implements ITunerResourceManager {
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

            @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
            public void registerClientProfile(ResourceClientProfile profile, IResourcesReclaimListener listener, int[] clientId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(profile, 0);
                    _data.writeStrongInterface(listener);
                    _data.writeInt(clientId.length);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    _reply.readIntArray(clientId);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
            public void unregisterClientProfile(int clientId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(clientId);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
            public boolean updateClientPriority(int clientId, int priority, int niceValue) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(clientId);
                    _data.writeInt(priority);
                    _data.writeInt(niceValue);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
            public boolean hasUnusedFrontend(int frontendType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(frontendType);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
            public boolean isLowestPriority(int clientId, int frontendType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(clientId);
                    _data.writeInt(frontendType);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
            public void setFrontendInfoList(TunerFrontendInfo[] infos) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedArray(infos, 0);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
            public void updateCasInfo(int casSystemId, int maxSessionNum) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(casSystemId);
                    _data.writeInt(maxSessionNum);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
            public void setDemuxInfoList(TunerDemuxInfo[] infos) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedArray(infos, 0);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
            public void setLnbInfoList(int[] lnbIds) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeIntArray(lnbIds);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
            public boolean requestFrontend(TunerFrontendRequest request, int[] frontendHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeInt(frontendHandle.length);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    _reply.readIntArray(frontendHandle);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
            public boolean setMaxNumberOfFrontends(int frontendType, int maxNum) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(frontendType);
                    _data.writeInt(maxNum);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
            public int getMaxNumberOfFrontends(int frontendType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(frontendType);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
            public void shareFrontend(int selfClientId, int targetClientId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(selfClientId);
                    _data.writeInt(targetClientId);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
            public boolean transferOwner(int resourceType, int currentOwnerId, int newOwnerId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(resourceType);
                    _data.writeInt(currentOwnerId);
                    _data.writeInt(newOwnerId);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
            public boolean requestDemux(TunerDemuxRequest request, int[] demuxHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeInt(demuxHandle.length);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    _reply.readIntArray(demuxHandle);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
            public boolean requestDescrambler(TunerDescramblerRequest request, int[] descramblerHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeInt(descramblerHandle.length);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    _reply.readIntArray(descramblerHandle);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
            public boolean requestCasSession(CasSessionRequest request, int[] casSessionHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeInt(casSessionHandle.length);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    _reply.readIntArray(casSessionHandle);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
            public boolean requestCiCam(TunerCiCamRequest request, int[] ciCamHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeInt(ciCamHandle.length);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    _reply.readIntArray(ciCamHandle);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
            public boolean requestLnb(TunerLnbRequest request, int[] lnbHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    _data.writeInt(lnbHandle.length);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    _reply.readIntArray(lnbHandle);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
            public void releaseFrontend(int frontendHandle, int clientId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(frontendHandle);
                    _data.writeInt(clientId);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
            public void releaseDemux(int demuxHandle, int clientId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(demuxHandle);
                    _data.writeInt(clientId);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
            public void releaseDescrambler(int descramblerHandle, int clientId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(descramblerHandle);
                    _data.writeInt(clientId);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
            public void releaseCasSession(int casSessionHandle, int clientId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(casSessionHandle);
                    _data.writeInt(clientId);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
            public void releaseCiCam(int ciCamHandle, int clientId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(ciCamHandle);
                    _data.writeInt(clientId);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
            public void releaseLnb(int lnbHandle, int clientId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(lnbHandle);
                    _data.writeInt(clientId);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
            public boolean isHigherPriority(ResourceClientProfile challengerProfile, ResourceClientProfile holderProfile) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(challengerProfile, 0);
                    _data.writeTypedObject(holderProfile, 0);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
            public void storeResourceMap(int resourceType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(resourceType);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
            public void clearResourceMap(int resourceType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(resourceType);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
            public void restoreResourceMap(int resourceType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(resourceType);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
            public boolean acquireLock(int clientId, long clientThreadId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(clientId);
                    _data.writeLong(clientThreadId);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
            public boolean releaseLock(int clientId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(clientId);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
            public int getClientPriority(int useCase, int pid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(useCase);
                    _data.writeInt(pid);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.tunerresourcemanager.ITunerResourceManager
            public int getConfigPriority(int useCase, boolean isForeground) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(useCase);
                    _data.writeBoolean(isForeground);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}
