package com.android.internal.telecom;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.text.TextUtils;
/* loaded from: classes2.dex */
public interface ICallDiagnosticServiceAdapter extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.telecom.ICallDiagnosticServiceAdapter";

    void clearDiagnosticMessage(String str, int i) throws RemoteException;

    void displayDiagnosticMessage(String str, int i, CharSequence charSequence) throws RemoteException;

    void overrideDisconnectMessage(String str, CharSequence charSequence) throws RemoteException;

    void sendDeviceToDeviceMessage(String str, int i, int i2) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements ICallDiagnosticServiceAdapter {
        @Override // com.android.internal.telecom.ICallDiagnosticServiceAdapter
        public void displayDiagnosticMessage(String callId, int messageId, CharSequence message) throws RemoteException {
        }

        @Override // com.android.internal.telecom.ICallDiagnosticServiceAdapter
        public void clearDiagnosticMessage(String callId, int messageId) throws RemoteException {
        }

        @Override // com.android.internal.telecom.ICallDiagnosticServiceAdapter
        public void sendDeviceToDeviceMessage(String callId, int message, int value) throws RemoteException {
        }

        @Override // com.android.internal.telecom.ICallDiagnosticServiceAdapter
        public void overrideDisconnectMessage(String callId, CharSequence message) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ICallDiagnosticServiceAdapter {
        static final int TRANSACTION_clearDiagnosticMessage = 2;
        static final int TRANSACTION_displayDiagnosticMessage = 1;
        static final int TRANSACTION_overrideDisconnectMessage = 4;
        static final int TRANSACTION_sendDeviceToDeviceMessage = 3;

        public Stub() {
            attachInterface(this, ICallDiagnosticServiceAdapter.DESCRIPTOR);
        }

        public static ICallDiagnosticServiceAdapter asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ICallDiagnosticServiceAdapter.DESCRIPTOR);
            if (iin != null && (iin instanceof ICallDiagnosticServiceAdapter)) {
                return (ICallDiagnosticServiceAdapter) iin;
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
                    return "displayDiagnosticMessage";
                case 2:
                    return "clearDiagnosticMessage";
                case 3:
                    return "sendDeviceToDeviceMessage";
                case 4:
                    return "overrideDisconnectMessage";
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
                data.enforceInterface(ICallDiagnosticServiceAdapter.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ICallDiagnosticServiceAdapter.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            int _arg1 = data.readInt();
                            CharSequence _arg2 = (CharSequence) data.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                            data.enforceNoDataAvail();
                            displayDiagnosticMessage(_arg0, _arg1, _arg2);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            clearDiagnosticMessage(_arg02, _arg12);
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            int _arg13 = data.readInt();
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            sendDeviceToDeviceMessage(_arg03, _arg13, _arg22);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            CharSequence _arg14 = (CharSequence) data.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                            data.enforceNoDataAvail();
                            overrideDisconnectMessage(_arg04, _arg14);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements ICallDiagnosticServiceAdapter {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ICallDiagnosticServiceAdapter.DESCRIPTOR;
            }

            @Override // com.android.internal.telecom.ICallDiagnosticServiceAdapter
            public void displayDiagnosticMessage(String callId, int messageId, CharSequence message) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICallDiagnosticServiceAdapter.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeInt(messageId);
                    if (message != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(message, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.ICallDiagnosticServiceAdapter
            public void clearDiagnosticMessage(String callId, int messageId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICallDiagnosticServiceAdapter.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeInt(messageId);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.ICallDiagnosticServiceAdapter
            public void sendDeviceToDeviceMessage(String callId, int message, int value) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICallDiagnosticServiceAdapter.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeInt(message);
                    _data.writeInt(value);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.ICallDiagnosticServiceAdapter
            public void overrideDisconnectMessage(String callId, CharSequence message) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICallDiagnosticServiceAdapter.DESCRIPTOR);
                    _data.writeString(callId);
                    if (message != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(message, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
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
