package com.android.ims.internal;

import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telephony.ims.ImsCallForwardInfo;
import android.telephony.ims.ImsReasonInfo;
import android.telephony.ims.ImsSsData;
import android.telephony.ims.ImsSsInfo;
import com.android.ims.internal.IImsUt;
/* loaded from: classes4.dex */
public interface IImsUtListener extends IInterface {
    void lineIdentificationSupplementaryServiceResponse(int i, ImsSsInfo imsSsInfo) throws RemoteException;

    void onSupplementaryServiceIndication(ImsSsData imsSsData) throws RemoteException;

    void utConfigurationCallBarringQueried(IImsUt iImsUt, int i, ImsSsInfo[] imsSsInfoArr) throws RemoteException;

    void utConfigurationCallForwardQueried(IImsUt iImsUt, int i, ImsCallForwardInfo[] imsCallForwardInfoArr) throws RemoteException;

    void utConfigurationCallWaitingQueried(IImsUt iImsUt, int i, ImsSsInfo[] imsSsInfoArr) throws RemoteException;

    void utConfigurationQueried(IImsUt iImsUt, int i, Bundle bundle) throws RemoteException;

    void utConfigurationQueryFailed(IImsUt iImsUt, int i, ImsReasonInfo imsReasonInfo) throws RemoteException;

    void utConfigurationUpdateFailed(IImsUt iImsUt, int i, ImsReasonInfo imsReasonInfo) throws RemoteException;

    void utConfigurationUpdated(IImsUt iImsUt, int i) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IImsUtListener {
        @Override // com.android.ims.internal.IImsUtListener
        public void utConfigurationUpdated(IImsUt ut, int id) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsUtListener
        public void utConfigurationUpdateFailed(IImsUt ut, int id, ImsReasonInfo error) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsUtListener
        public void utConfigurationQueried(IImsUt ut, int id, Bundle ssInfo) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsUtListener
        public void utConfigurationQueryFailed(IImsUt ut, int id, ImsReasonInfo error) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsUtListener
        public void lineIdentificationSupplementaryServiceResponse(int id, ImsSsInfo config) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsUtListener
        public void utConfigurationCallBarringQueried(IImsUt ut, int id, ImsSsInfo[] cbInfo) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsUtListener
        public void utConfigurationCallForwardQueried(IImsUt ut, int id, ImsCallForwardInfo[] cfInfo) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsUtListener
        public void utConfigurationCallWaitingQueried(IImsUt ut, int id, ImsSsInfo[] cwInfo) throws RemoteException {
        }

        @Override // com.android.ims.internal.IImsUtListener
        public void onSupplementaryServiceIndication(ImsSsData ssData) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IImsUtListener {
        public static final String DESCRIPTOR = "com.android.ims.internal.IImsUtListener";
        static final int TRANSACTION_lineIdentificationSupplementaryServiceResponse = 5;
        static final int TRANSACTION_onSupplementaryServiceIndication = 9;
        static final int TRANSACTION_utConfigurationCallBarringQueried = 6;
        static final int TRANSACTION_utConfigurationCallForwardQueried = 7;
        static final int TRANSACTION_utConfigurationCallWaitingQueried = 8;
        static final int TRANSACTION_utConfigurationQueried = 3;
        static final int TRANSACTION_utConfigurationQueryFailed = 4;
        static final int TRANSACTION_utConfigurationUpdateFailed = 2;
        static final int TRANSACTION_utConfigurationUpdated = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IImsUtListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IImsUtListener)) {
                return (IImsUtListener) iin;
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
                    return "utConfigurationUpdated";
                case 2:
                    return "utConfigurationUpdateFailed";
                case 3:
                    return "utConfigurationQueried";
                case 4:
                    return "utConfigurationQueryFailed";
                case 5:
                    return "lineIdentificationSupplementaryServiceResponse";
                case 6:
                    return "utConfigurationCallBarringQueried";
                case 7:
                    return "utConfigurationCallForwardQueried";
                case 8:
                    return "utConfigurationCallWaitingQueried";
                case 9:
                    return "onSupplementaryServiceIndication";
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
                            IImsUt _arg0 = IImsUt.Stub.asInterface(data.readStrongBinder());
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            utConfigurationUpdated(_arg0, _arg1);
                            break;
                        case 2:
                            IImsUt _arg02 = IImsUt.Stub.asInterface(data.readStrongBinder());
                            int _arg12 = data.readInt();
                            ImsReasonInfo _arg2 = (ImsReasonInfo) data.readTypedObject(ImsReasonInfo.CREATOR);
                            data.enforceNoDataAvail();
                            utConfigurationUpdateFailed(_arg02, _arg12, _arg2);
                            break;
                        case 3:
                            IImsUt _arg03 = IImsUt.Stub.asInterface(data.readStrongBinder());
                            int _arg13 = data.readInt();
                            Bundle _arg22 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            utConfigurationQueried(_arg03, _arg13, _arg22);
                            break;
                        case 4:
                            IImsUt _arg04 = IImsUt.Stub.asInterface(data.readStrongBinder());
                            int _arg14 = data.readInt();
                            ImsReasonInfo _arg23 = (ImsReasonInfo) data.readTypedObject(ImsReasonInfo.CREATOR);
                            data.enforceNoDataAvail();
                            utConfigurationQueryFailed(_arg04, _arg14, _arg23);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            ImsSsInfo _arg15 = (ImsSsInfo) data.readTypedObject(ImsSsInfo.CREATOR);
                            data.enforceNoDataAvail();
                            lineIdentificationSupplementaryServiceResponse(_arg05, _arg15);
                            break;
                        case 6:
                            IImsUt _arg06 = IImsUt.Stub.asInterface(data.readStrongBinder());
                            int _arg16 = data.readInt();
                            ImsSsInfo[] _arg24 = (ImsSsInfo[]) data.createTypedArray(ImsSsInfo.CREATOR);
                            data.enforceNoDataAvail();
                            utConfigurationCallBarringQueried(_arg06, _arg16, _arg24);
                            break;
                        case 7:
                            IImsUt _arg07 = IImsUt.Stub.asInterface(data.readStrongBinder());
                            int _arg17 = data.readInt();
                            ImsCallForwardInfo[] _arg25 = (ImsCallForwardInfo[]) data.createTypedArray(ImsCallForwardInfo.CREATOR);
                            data.enforceNoDataAvail();
                            utConfigurationCallForwardQueried(_arg07, _arg17, _arg25);
                            break;
                        case 8:
                            IImsUt _arg08 = IImsUt.Stub.asInterface(data.readStrongBinder());
                            int _arg18 = data.readInt();
                            ImsSsInfo[] _arg26 = (ImsSsInfo[]) data.createTypedArray(ImsSsInfo.CREATOR);
                            data.enforceNoDataAvail();
                            utConfigurationCallWaitingQueried(_arg08, _arg18, _arg26);
                            break;
                        case 9:
                            ImsSsData _arg09 = (ImsSsData) data.readTypedObject(ImsSsData.CREATOR);
                            data.enforceNoDataAvail();
                            onSupplementaryServiceIndication(_arg09);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IImsUtListener {
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

            @Override // com.android.ims.internal.IImsUtListener
            public void utConfigurationUpdated(IImsUt ut, int id) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(ut);
                    _data.writeInt(id);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsUtListener
            public void utConfigurationUpdateFailed(IImsUt ut, int id, ImsReasonInfo error) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(ut);
                    _data.writeInt(id);
                    _data.writeTypedObject(error, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsUtListener
            public void utConfigurationQueried(IImsUt ut, int id, Bundle ssInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(ut);
                    _data.writeInt(id);
                    _data.writeTypedObject(ssInfo, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsUtListener
            public void utConfigurationQueryFailed(IImsUt ut, int id, ImsReasonInfo error) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(ut);
                    _data.writeInt(id);
                    _data.writeTypedObject(error, 0);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsUtListener
            public void lineIdentificationSupplementaryServiceResponse(int id, ImsSsInfo config) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(id);
                    _data.writeTypedObject(config, 0);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsUtListener
            public void utConfigurationCallBarringQueried(IImsUt ut, int id, ImsSsInfo[] cbInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(ut);
                    _data.writeInt(id);
                    _data.writeTypedArray(cbInfo, 0);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsUtListener
            public void utConfigurationCallForwardQueried(IImsUt ut, int id, ImsCallForwardInfo[] cfInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(ut);
                    _data.writeInt(id);
                    _data.writeTypedArray(cfInfo, 0);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsUtListener
            public void utConfigurationCallWaitingQueried(IImsUt ut, int id, ImsSsInfo[] cwInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(ut);
                    _data.writeInt(id);
                    _data.writeTypedArray(cwInfo, 0);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.ims.internal.IImsUtListener
            public void onSupplementaryServiceIndication(ImsSsData ssData) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(ssData, 0);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
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
