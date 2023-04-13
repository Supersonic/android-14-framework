package com.android.internal.telephony;

import android.content.Intent;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface IWapPushManager extends IInterface {
    boolean addPackage(String str, String str2, String str3, String str4, int i, boolean z, boolean z2) throws RemoteException;

    boolean deletePackage(String str, String str2, String str3, String str4) throws RemoteException;

    int processMessage(String str, String str2, Intent intent) throws RemoteException;

    boolean updatePackage(String str, String str2, String str3, String str4, int i, boolean z, boolean z2) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IWapPushManager {
        @Override // com.android.internal.telephony.IWapPushManager
        public int processMessage(String app_id, String content_type, Intent intent) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.telephony.IWapPushManager
        public boolean addPackage(String x_app_id, String content_type, String package_name, String class_name, int app_type, boolean need_signature, boolean further_processing) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.telephony.IWapPushManager
        public boolean updatePackage(String x_app_id, String content_type, String package_name, String class_name, int app_type, boolean need_signature, boolean further_processing) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.telephony.IWapPushManager
        public boolean deletePackage(String x_app_id, String content_type, String package_name, String class_name) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IWapPushManager {
        public static final String DESCRIPTOR = "com.android.internal.telephony.IWapPushManager";
        static final int TRANSACTION_addPackage = 2;
        static final int TRANSACTION_deletePackage = 4;
        static final int TRANSACTION_processMessage = 1;
        static final int TRANSACTION_updatePackage = 3;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IWapPushManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IWapPushManager)) {
                return (IWapPushManager) iin;
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
                    return "processMessage";
                case 2:
                    return "addPackage";
                case 3:
                    return "updatePackage";
                case 4:
                    return "deletePackage";
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
                            String _arg0 = data.readString();
                            String _arg1 = data.readString();
                            Intent _arg2 = (Intent) data.readTypedObject(Intent.CREATOR);
                            data.enforceNoDataAvail();
                            int _result = processMessage(_arg0, _arg1, _arg2);
                            reply.writeNoException();
                            reply.writeInt(_result);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            String _arg12 = data.readString();
                            String _arg22 = data.readString();
                            String _arg3 = data.readString();
                            int _arg4 = data.readInt();
                            boolean _arg5 = data.readBoolean();
                            boolean _arg6 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result2 = addPackage(_arg02, _arg12, _arg22, _arg3, _arg4, _arg5, _arg6);
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            String _arg13 = data.readString();
                            String _arg23 = data.readString();
                            String _arg32 = data.readString();
                            int _arg42 = data.readInt();
                            boolean _arg52 = data.readBoolean();
                            boolean _arg62 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result3 = updatePackage(_arg03, _arg13, _arg23, _arg32, _arg42, _arg52, _arg62);
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            String _arg14 = data.readString();
                            String _arg24 = data.readString();
                            String _arg33 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result4 = deletePackage(_arg04, _arg14, _arg24, _arg33);
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IWapPushManager {
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

            @Override // com.android.internal.telephony.IWapPushManager
            public int processMessage(String app_id, String content_type, Intent intent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(app_id);
                    _data.writeString(content_type);
                    _data.writeTypedObject(intent, 0);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IWapPushManager
            public boolean addPackage(String x_app_id, String content_type, String package_name, String class_name, int app_type, boolean need_signature, boolean further_processing) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(x_app_id);
                    _data.writeString(content_type);
                    _data.writeString(package_name);
                    _data.writeString(class_name);
                    _data.writeInt(app_type);
                    _data.writeBoolean(need_signature);
                    _data.writeBoolean(further_processing);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IWapPushManager
            public boolean updatePackage(String x_app_id, String content_type, String package_name, String class_name, int app_type, boolean need_signature, boolean further_processing) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(x_app_id);
                    _data.writeString(content_type);
                    _data.writeString(package_name);
                    _data.writeString(class_name);
                    _data.writeInt(app_type);
                    _data.writeBoolean(need_signature);
                    _data.writeBoolean(further_processing);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IWapPushManager
            public boolean deletePackage(String x_app_id, String content_type, String package_name, String class_name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(x_app_id);
                    _data.writeString(content_type);
                    _data.writeString(package_name);
                    _data.writeString(class_name);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 3;
        }
    }
}
