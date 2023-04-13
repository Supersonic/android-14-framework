package com.android.ims.internal.uce.presence;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import com.android.ims.internal.uce.common.StatusCode;
import com.android.ims.internal.uce.common.UceLong;
import com.android.ims.internal.uce.presence.IPresenceListener;
/* loaded from: classes4.dex */
public interface IPresenceService extends IInterface {
    StatusCode addListener(int i, IPresenceListener iPresenceListener, UceLong uceLong) throws RemoteException;

    StatusCode getContactCap(int i, String str, int i2) throws RemoteException;

    StatusCode getContactListCap(int i, String[] strArr, int i2) throws RemoteException;

    StatusCode getVersion(int i) throws RemoteException;

    StatusCode publishMyCap(int i, PresCapInfo presCapInfo, int i2) throws RemoteException;

    StatusCode reenableService(int i, int i2) throws RemoteException;

    StatusCode removeListener(int i, UceLong uceLong) throws RemoteException;

    StatusCode setNewFeatureTag(int i, String str, PresServiceInfo presServiceInfo, int i2) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IPresenceService {
        @Override // com.android.ims.internal.uce.presence.IPresenceService
        public StatusCode getVersion(int presenceServiceHdl) throws RemoteException {
            return null;
        }

        @Override // com.android.ims.internal.uce.presence.IPresenceService
        public StatusCode addListener(int presenceServiceHdl, IPresenceListener presenceServiceListener, UceLong presenceServiceListenerHdl) throws RemoteException {
            return null;
        }

        @Override // com.android.ims.internal.uce.presence.IPresenceService
        public StatusCode removeListener(int presenceServiceHdl, UceLong presenceServiceListenerHdl) throws RemoteException {
            return null;
        }

        @Override // com.android.ims.internal.uce.presence.IPresenceService
        public StatusCode reenableService(int presenceServiceHdl, int userData) throws RemoteException {
            return null;
        }

        @Override // com.android.ims.internal.uce.presence.IPresenceService
        public StatusCode publishMyCap(int presenceServiceHdl, PresCapInfo myCapInfo, int userData) throws RemoteException {
            return null;
        }

        @Override // com.android.ims.internal.uce.presence.IPresenceService
        public StatusCode getContactCap(int presenceServiceHdl, String remoteUri, int userData) throws RemoteException {
            return null;
        }

        @Override // com.android.ims.internal.uce.presence.IPresenceService
        public StatusCode getContactListCap(int presenceServiceHdl, String[] remoteUriList, int userData) throws RemoteException {
            return null;
        }

        @Override // com.android.ims.internal.uce.presence.IPresenceService
        public StatusCode setNewFeatureTag(int presenceServiceHdl, String featureTag, PresServiceInfo serviceInfo, int userData) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IPresenceService {
        public static final String DESCRIPTOR = "com.android.ims.internal.uce.presence.IPresenceService";
        static final int TRANSACTION_addListener = 2;
        static final int TRANSACTION_getContactCap = 6;
        static final int TRANSACTION_getContactListCap = 7;
        static final int TRANSACTION_getVersion = 1;
        static final int TRANSACTION_publishMyCap = 5;
        static final int TRANSACTION_reenableService = 4;
        static final int TRANSACTION_removeListener = 3;
        static final int TRANSACTION_setNewFeatureTag = 8;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IPresenceService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IPresenceService)) {
                return (IPresenceService) iin;
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
                    return "getVersion";
                case 2:
                    return "addListener";
                case 3:
                    return "removeListener";
                case 4:
                    return "reenableService";
                case 5:
                    return "publishMyCap";
                case 6:
                    return "getContactCap";
                case 7:
                    return "getContactListCap";
                case 8:
                    return "setNewFeatureTag";
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
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            StatusCode _result = getVersion(_arg0);
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            IPresenceListener _arg1 = IPresenceListener.Stub.asInterface(data.readStrongBinder());
                            UceLong _arg2 = (UceLong) data.readTypedObject(UceLong.CREATOR);
                            data.enforceNoDataAvail();
                            StatusCode _result2 = addListener(_arg02, _arg1, _arg2);
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            reply.writeTypedObject(_arg2, 1);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            UceLong _arg12 = (UceLong) data.readTypedObject(UceLong.CREATOR);
                            data.enforceNoDataAvail();
                            StatusCode _result3 = removeListener(_arg03, _arg12);
                            reply.writeNoException();
                            reply.writeTypedObject(_result3, 1);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            StatusCode _result4 = reenableService(_arg04, _arg13);
                            reply.writeNoException();
                            reply.writeTypedObject(_result4, 1);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            PresCapInfo _arg14 = (PresCapInfo) data.readTypedObject(PresCapInfo.CREATOR);
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            StatusCode _result5 = publishMyCap(_arg05, _arg14, _arg22);
                            reply.writeNoException();
                            reply.writeTypedObject(_result5, 1);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            String _arg15 = data.readString();
                            int _arg23 = data.readInt();
                            data.enforceNoDataAvail();
                            StatusCode _result6 = getContactCap(_arg06, _arg15, _arg23);
                            reply.writeNoException();
                            reply.writeTypedObject(_result6, 1);
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            String[] _arg16 = data.createStringArray();
                            int _arg24 = data.readInt();
                            data.enforceNoDataAvail();
                            StatusCode _result7 = getContactListCap(_arg07, _arg16, _arg24);
                            reply.writeNoException();
                            reply.writeTypedObject(_result7, 1);
                            break;
                        case 8:
                            int _arg08 = data.readInt();
                            String _arg17 = data.readString();
                            int _arg3 = data.readInt();
                            data.enforceNoDataAvail();
                            StatusCode _result8 = setNewFeatureTag(_arg08, _arg17, (PresServiceInfo) data.readTypedObject(PresServiceInfo.CREATOR), _arg3);
                            reply.writeNoException();
                            reply.writeTypedObject(_result8, 1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IPresenceService {
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

            @Override // com.android.ims.internal.uce.presence.IPresenceService
            public StatusCode getVersion(int presenceServiceHdl) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(presenceServiceHdl);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    StatusCode _result = (StatusCode) _reply.readTypedObject(StatusCode.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.uce.presence.IPresenceService
            public StatusCode addListener(int presenceServiceHdl, IPresenceListener presenceServiceListener, UceLong presenceServiceListenerHdl) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(presenceServiceHdl);
                    _data.writeStrongInterface(presenceServiceListener);
                    _data.writeTypedObject(presenceServiceListenerHdl, 0);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    StatusCode _result = (StatusCode) _reply.readTypedObject(StatusCode.CREATOR);
                    if (_reply.readInt() != 0) {
                        presenceServiceListenerHdl.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.uce.presence.IPresenceService
            public StatusCode removeListener(int presenceServiceHdl, UceLong presenceServiceListenerHdl) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(presenceServiceHdl);
                    _data.writeTypedObject(presenceServiceListenerHdl, 0);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    StatusCode _result = (StatusCode) _reply.readTypedObject(StatusCode.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.uce.presence.IPresenceService
            public StatusCode reenableService(int presenceServiceHdl, int userData) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(presenceServiceHdl);
                    _data.writeInt(userData);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    StatusCode _result = (StatusCode) _reply.readTypedObject(StatusCode.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.uce.presence.IPresenceService
            public StatusCode publishMyCap(int presenceServiceHdl, PresCapInfo myCapInfo, int userData) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(presenceServiceHdl);
                    _data.writeTypedObject(myCapInfo, 0);
                    _data.writeInt(userData);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    StatusCode _result = (StatusCode) _reply.readTypedObject(StatusCode.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.uce.presence.IPresenceService
            public StatusCode getContactCap(int presenceServiceHdl, String remoteUri, int userData) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(presenceServiceHdl);
                    _data.writeString(remoteUri);
                    _data.writeInt(userData);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    StatusCode _result = (StatusCode) _reply.readTypedObject(StatusCode.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.uce.presence.IPresenceService
            public StatusCode getContactListCap(int presenceServiceHdl, String[] remoteUriList, int userData) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(presenceServiceHdl);
                    _data.writeStringArray(remoteUriList);
                    _data.writeInt(userData);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    StatusCode _result = (StatusCode) _reply.readTypedObject(StatusCode.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.uce.presence.IPresenceService
            public StatusCode setNewFeatureTag(int presenceServiceHdl, String featureTag, PresServiceInfo serviceInfo, int userData) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(presenceServiceHdl);
                    _data.writeString(featureTag);
                    _data.writeTypedObject(serviceInfo, 0);
                    _data.writeInt(userData);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    StatusCode _result = (StatusCode) _reply.readTypedObject(StatusCode.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 7;
        }
    }
}
