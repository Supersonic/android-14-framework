package com.android.ims.internal.uce.options;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import com.android.ims.internal.uce.common.CapInfo;
import com.android.ims.internal.uce.common.StatusCode;
import com.android.ims.internal.uce.common.UceLong;
import com.android.ims.internal.uce.options.IOptionsListener;
/* loaded from: classes4.dex */
public interface IOptionsService extends IInterface {
    StatusCode addListener(int i, IOptionsListener iOptionsListener, UceLong uceLong) throws RemoteException;

    StatusCode getContactCap(int i, String str, int i2) throws RemoteException;

    StatusCode getContactListCap(int i, String[] strArr, int i2) throws RemoteException;

    StatusCode getMyInfo(int i, int i2) throws RemoteException;

    StatusCode getVersion(int i) throws RemoteException;

    StatusCode removeListener(int i, UceLong uceLong) throws RemoteException;

    StatusCode responseIncomingOptions(int i, int i2, int i3, String str, OptionsCapInfo optionsCapInfo, boolean z) throws RemoteException;

    StatusCode setMyInfo(int i, CapInfo capInfo, int i2) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IOptionsService {
        @Override // com.android.ims.internal.uce.options.IOptionsService
        public StatusCode getVersion(int optionsServiceHandle) throws RemoteException {
            return null;
        }

        @Override // com.android.ims.internal.uce.options.IOptionsService
        public StatusCode addListener(int optionsServiceHandle, IOptionsListener optionsListener, UceLong optionsServiceListenerHdl) throws RemoteException {
            return null;
        }

        @Override // com.android.ims.internal.uce.options.IOptionsService
        public StatusCode removeListener(int optionsServiceHandle, UceLong optionsServiceListenerHdl) throws RemoteException {
            return null;
        }

        @Override // com.android.ims.internal.uce.options.IOptionsService
        public StatusCode setMyInfo(int optionsServiceHandle, CapInfo capInfo, int reqUserData) throws RemoteException {
            return null;
        }

        @Override // com.android.ims.internal.uce.options.IOptionsService
        public StatusCode getMyInfo(int optionsServiceHandle, int reqUserdata) throws RemoteException {
            return null;
        }

        @Override // com.android.ims.internal.uce.options.IOptionsService
        public StatusCode getContactCap(int optionsServiceHandle, String remoteURI, int reqUserData) throws RemoteException {
            return null;
        }

        @Override // com.android.ims.internal.uce.options.IOptionsService
        public StatusCode getContactListCap(int optionsServiceHandle, String[] remoteURIList, int reqUserData) throws RemoteException {
            return null;
        }

        @Override // com.android.ims.internal.uce.options.IOptionsService
        public StatusCode responseIncomingOptions(int optionsServiceHandle, int tId, int sipResponseCode, String reasonPhrase, OptionsCapInfo capInfo, boolean bContactInBL) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IOptionsService {
        public static final String DESCRIPTOR = "com.android.ims.internal.uce.options.IOptionsService";
        static final int TRANSACTION_addListener = 2;
        static final int TRANSACTION_getContactCap = 6;
        static final int TRANSACTION_getContactListCap = 7;
        static final int TRANSACTION_getMyInfo = 5;
        static final int TRANSACTION_getVersion = 1;
        static final int TRANSACTION_removeListener = 3;
        static final int TRANSACTION_responseIncomingOptions = 8;
        static final int TRANSACTION_setMyInfo = 4;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IOptionsService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IOptionsService)) {
                return (IOptionsService) iin;
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
                    return "setMyInfo";
                case 5:
                    return "getMyInfo";
                case 6:
                    return "getContactCap";
                case 7:
                    return "getContactListCap";
                case 8:
                    return "responseIncomingOptions";
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
                            IOptionsListener _arg1 = IOptionsListener.Stub.asInterface(data.readStrongBinder());
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
                            CapInfo _arg13 = (CapInfo) data.readTypedObject(CapInfo.CREATOR);
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            StatusCode _result4 = setMyInfo(_arg04, _arg13, _arg22);
                            reply.writeNoException();
                            reply.writeTypedObject(_result4, 1);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            StatusCode _result5 = getMyInfo(_arg05, _arg14);
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
                            int _arg17 = data.readInt();
                            int _arg25 = data.readInt();
                            String _arg3 = data.readString();
                            OptionsCapInfo _arg4 = (OptionsCapInfo) data.readTypedObject(OptionsCapInfo.CREATOR);
                            boolean _arg5 = data.readBoolean();
                            data.enforceNoDataAvail();
                            StatusCode _result8 = responseIncomingOptions(_arg08, _arg17, _arg25, _arg3, _arg4, _arg5);
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
        private static class Proxy implements IOptionsService {
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

            @Override // com.android.ims.internal.uce.options.IOptionsService
            public StatusCode getVersion(int optionsServiceHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(optionsServiceHandle);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    StatusCode _result = (StatusCode) _reply.readTypedObject(StatusCode.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.uce.options.IOptionsService
            public StatusCode addListener(int optionsServiceHandle, IOptionsListener optionsListener, UceLong optionsServiceListenerHdl) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(optionsServiceHandle);
                    _data.writeStrongInterface(optionsListener);
                    _data.writeTypedObject(optionsServiceListenerHdl, 0);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    StatusCode _result = (StatusCode) _reply.readTypedObject(StatusCode.CREATOR);
                    if (_reply.readInt() != 0) {
                        optionsServiceListenerHdl.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.uce.options.IOptionsService
            public StatusCode removeListener(int optionsServiceHandle, UceLong optionsServiceListenerHdl) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(optionsServiceHandle);
                    _data.writeTypedObject(optionsServiceListenerHdl, 0);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    StatusCode _result = (StatusCode) _reply.readTypedObject(StatusCode.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.uce.options.IOptionsService
            public StatusCode setMyInfo(int optionsServiceHandle, CapInfo capInfo, int reqUserData) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(optionsServiceHandle);
                    _data.writeTypedObject(capInfo, 0);
                    _data.writeInt(reqUserData);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    StatusCode _result = (StatusCode) _reply.readTypedObject(StatusCode.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.uce.options.IOptionsService
            public StatusCode getMyInfo(int optionsServiceHandle, int reqUserdata) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(optionsServiceHandle);
                    _data.writeInt(reqUserdata);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    StatusCode _result = (StatusCode) _reply.readTypedObject(StatusCode.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.uce.options.IOptionsService
            public StatusCode getContactCap(int optionsServiceHandle, String remoteURI, int reqUserData) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(optionsServiceHandle);
                    _data.writeString(remoteURI);
                    _data.writeInt(reqUserData);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    StatusCode _result = (StatusCode) _reply.readTypedObject(StatusCode.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.uce.options.IOptionsService
            public StatusCode getContactListCap(int optionsServiceHandle, String[] remoteURIList, int reqUserData) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(optionsServiceHandle);
                    _data.writeStringArray(remoteURIList);
                    _data.writeInt(reqUserData);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    StatusCode _result = (StatusCode) _reply.readTypedObject(StatusCode.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.uce.options.IOptionsService
            public StatusCode responseIncomingOptions(int optionsServiceHandle, int tId, int sipResponseCode, String reasonPhrase, OptionsCapInfo capInfo, boolean bContactInBL) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(optionsServiceHandle);
                    _data.writeInt(tId);
                    _data.writeInt(sipResponseCode);
                    _data.writeString(reasonPhrase);
                    _data.writeTypedObject(capInfo, 0);
                    _data.writeBoolean(bContactInBL);
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
