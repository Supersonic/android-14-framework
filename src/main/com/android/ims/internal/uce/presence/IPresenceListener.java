package com.android.ims.internal.uce.presence;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import com.android.ims.internal.uce.common.StatusCode;
/* loaded from: classes4.dex */
public interface IPresenceListener extends IInterface {
    void capInfoReceived(String str, PresTupleInfo[] presTupleInfoArr) throws RemoteException;

    void cmdStatus(PresCmdStatus presCmdStatus) throws RemoteException;

    void getVersionCb(String str) throws RemoteException;

    void listCapInfoReceived(PresRlmiInfo presRlmiInfo, PresResInfo[] presResInfoArr) throws RemoteException;

    void publishTriggering(PresPublishTriggerType presPublishTriggerType) throws RemoteException;

    void serviceAvailable(StatusCode statusCode) throws RemoteException;

    void serviceUnAvailable(StatusCode statusCode) throws RemoteException;

    void sipResponseReceived(PresSipResponse presSipResponse) throws RemoteException;

    void unpublishMessageSent() throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IPresenceListener {
        @Override // com.android.ims.internal.uce.presence.IPresenceListener
        public void getVersionCb(String version) throws RemoteException {
        }

        @Override // com.android.ims.internal.uce.presence.IPresenceListener
        public void serviceAvailable(StatusCode statusCode) throws RemoteException {
        }

        @Override // com.android.ims.internal.uce.presence.IPresenceListener
        public void serviceUnAvailable(StatusCode statusCode) throws RemoteException {
        }

        @Override // com.android.ims.internal.uce.presence.IPresenceListener
        public void publishTriggering(PresPublishTriggerType publishTrigger) throws RemoteException {
        }

        @Override // com.android.ims.internal.uce.presence.IPresenceListener
        public void cmdStatus(PresCmdStatus cmdStatus) throws RemoteException {
        }

        @Override // com.android.ims.internal.uce.presence.IPresenceListener
        public void sipResponseReceived(PresSipResponse sipResponse) throws RemoteException {
        }

        @Override // com.android.ims.internal.uce.presence.IPresenceListener
        public void capInfoReceived(String presentityURI, PresTupleInfo[] tupleInfo) throws RemoteException {
        }

        @Override // com.android.ims.internal.uce.presence.IPresenceListener
        public void listCapInfoReceived(PresRlmiInfo rlmiInfo, PresResInfo[] resInfo) throws RemoteException {
        }

        @Override // com.android.ims.internal.uce.presence.IPresenceListener
        public void unpublishMessageSent() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IPresenceListener {
        public static final String DESCRIPTOR = "com.android.ims.internal.uce.presence.IPresenceListener";
        static final int TRANSACTION_capInfoReceived = 7;
        static final int TRANSACTION_cmdStatus = 5;
        static final int TRANSACTION_getVersionCb = 1;
        static final int TRANSACTION_listCapInfoReceived = 8;
        static final int TRANSACTION_publishTriggering = 4;
        static final int TRANSACTION_serviceAvailable = 2;
        static final int TRANSACTION_serviceUnAvailable = 3;
        static final int TRANSACTION_sipResponseReceived = 6;
        static final int TRANSACTION_unpublishMessageSent = 9;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IPresenceListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IPresenceListener)) {
                return (IPresenceListener) iin;
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
                    return "getVersionCb";
                case 2:
                    return "serviceAvailable";
                case 3:
                    return "serviceUnAvailable";
                case 4:
                    return "publishTriggering";
                case 5:
                    return "cmdStatus";
                case 6:
                    return "sipResponseReceived";
                case 7:
                    return "capInfoReceived";
                case 8:
                    return "listCapInfoReceived";
                case 9:
                    return "unpublishMessageSent";
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
                            data.enforceNoDataAvail();
                            getVersionCb(_arg0);
                            reply.writeNoException();
                            break;
                        case 2:
                            StatusCode _arg02 = (StatusCode) data.readTypedObject(StatusCode.CREATOR);
                            data.enforceNoDataAvail();
                            serviceAvailable(_arg02);
                            reply.writeNoException();
                            break;
                        case 3:
                            StatusCode _arg03 = (StatusCode) data.readTypedObject(StatusCode.CREATOR);
                            data.enforceNoDataAvail();
                            serviceUnAvailable(_arg03);
                            reply.writeNoException();
                            break;
                        case 4:
                            PresPublishTriggerType _arg04 = (PresPublishTriggerType) data.readTypedObject(PresPublishTriggerType.CREATOR);
                            data.enforceNoDataAvail();
                            publishTriggering(_arg04);
                            reply.writeNoException();
                            break;
                        case 5:
                            PresCmdStatus _arg05 = (PresCmdStatus) data.readTypedObject(PresCmdStatus.CREATOR);
                            data.enforceNoDataAvail();
                            cmdStatus(_arg05);
                            reply.writeNoException();
                            break;
                        case 6:
                            PresSipResponse _arg06 = (PresSipResponse) data.readTypedObject(PresSipResponse.CREATOR);
                            data.enforceNoDataAvail();
                            sipResponseReceived(_arg06);
                            reply.writeNoException();
                            break;
                        case 7:
                            String _arg07 = data.readString();
                            PresTupleInfo[] _arg1 = (PresTupleInfo[]) data.createTypedArray(PresTupleInfo.CREATOR);
                            data.enforceNoDataAvail();
                            capInfoReceived(_arg07, _arg1);
                            reply.writeNoException();
                            break;
                        case 8:
                            PresRlmiInfo _arg08 = (PresRlmiInfo) data.readTypedObject(PresRlmiInfo.CREATOR);
                            PresResInfo[] _arg12 = (PresResInfo[]) data.createTypedArray(PresResInfo.CREATOR);
                            data.enforceNoDataAvail();
                            listCapInfoReceived(_arg08, _arg12);
                            reply.writeNoException();
                            break;
                        case 9:
                            unpublishMessageSent();
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IPresenceListener {
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

            @Override // com.android.ims.internal.uce.presence.IPresenceListener
            public void getVersionCb(String version) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(version);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.uce.presence.IPresenceListener
            public void serviceAvailable(StatusCode statusCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(statusCode, 0);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.uce.presence.IPresenceListener
            public void serviceUnAvailable(StatusCode statusCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(statusCode, 0);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.uce.presence.IPresenceListener
            public void publishTriggering(PresPublishTriggerType publishTrigger) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(publishTrigger, 0);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.uce.presence.IPresenceListener
            public void cmdStatus(PresCmdStatus cmdStatus) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(cmdStatus, 0);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.uce.presence.IPresenceListener
            public void sipResponseReceived(PresSipResponse sipResponse) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(sipResponse, 0);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.uce.presence.IPresenceListener
            public void capInfoReceived(String presentityURI, PresTupleInfo[] tupleInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(presentityURI);
                    _data.writeTypedArray(tupleInfo, 0);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.uce.presence.IPresenceListener
            public void listCapInfoReceived(PresRlmiInfo rlmiInfo, PresResInfo[] resInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(rlmiInfo, 0);
                    _data.writeTypedArray(resInfo, 0);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.uce.presence.IPresenceListener
            public void unpublishMessageSent() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 8;
        }
    }
}
