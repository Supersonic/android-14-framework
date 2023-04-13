package android.service.euicc;

import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.service.euicc.IDeleteSubscriptionCallback;
import android.service.euicc.IDownloadSubscriptionCallback;
import android.service.euicc.IEraseSubscriptionsCallback;
import android.service.euicc.IEuiccServiceDumpResultCallback;
import android.service.euicc.IGetDefaultDownloadableSubscriptionListCallback;
import android.service.euicc.IGetDownloadableSubscriptionMetadataCallback;
import android.service.euicc.IGetEidCallback;
import android.service.euicc.IGetEuiccInfoCallback;
import android.service.euicc.IGetEuiccProfileInfoListCallback;
import android.service.euicc.IGetOtaStatusCallback;
import android.service.euicc.IOtaStatusChangedCallback;
import android.service.euicc.IRetainSubscriptionsForFactoryResetCallback;
import android.service.euicc.ISwitchToSubscriptionCallback;
import android.service.euicc.IUpdateSubscriptionNicknameCallback;
import android.telephony.euicc.DownloadableSubscription;
/* loaded from: classes3.dex */
public interface IEuiccService extends IInterface {
    void deleteSubscription(int i, String str, IDeleteSubscriptionCallback iDeleteSubscriptionCallback) throws RemoteException;

    void downloadSubscription(int i, int i2, DownloadableSubscription downloadableSubscription, boolean z, boolean z2, Bundle bundle, IDownloadSubscriptionCallback iDownloadSubscriptionCallback) throws RemoteException;

    void dump(IEuiccServiceDumpResultCallback iEuiccServiceDumpResultCallback) throws RemoteException;

    void eraseSubscriptions(int i, IEraseSubscriptionsCallback iEraseSubscriptionsCallback) throws RemoteException;

    void eraseSubscriptionsWithOptions(int i, int i2, IEraseSubscriptionsCallback iEraseSubscriptionsCallback) throws RemoteException;

    void getDefaultDownloadableSubscriptionList(int i, boolean z, IGetDefaultDownloadableSubscriptionListCallback iGetDefaultDownloadableSubscriptionListCallback) throws RemoteException;

    void getDownloadableSubscriptionMetadata(int i, int i2, DownloadableSubscription downloadableSubscription, boolean z, boolean z2, IGetDownloadableSubscriptionMetadataCallback iGetDownloadableSubscriptionMetadataCallback) throws RemoteException;

    void getEid(int i, IGetEidCallback iGetEidCallback) throws RemoteException;

    void getEuiccInfo(int i, IGetEuiccInfoCallback iGetEuiccInfoCallback) throws RemoteException;

    void getEuiccProfileInfoList(int i, IGetEuiccProfileInfoListCallback iGetEuiccProfileInfoListCallback) throws RemoteException;

    void getOtaStatus(int i, IGetOtaStatusCallback iGetOtaStatusCallback) throws RemoteException;

    void retainSubscriptionsForFactoryReset(int i, IRetainSubscriptionsForFactoryResetCallback iRetainSubscriptionsForFactoryResetCallback) throws RemoteException;

    void startOtaIfNecessary(int i, IOtaStatusChangedCallback iOtaStatusChangedCallback) throws RemoteException;

    void switchToSubscription(int i, int i2, String str, boolean z, ISwitchToSubscriptionCallback iSwitchToSubscriptionCallback, boolean z2) throws RemoteException;

    void updateSubscriptionNickname(int i, String str, String str2, IUpdateSubscriptionNicknameCallback iUpdateSubscriptionNicknameCallback) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IEuiccService {
        @Override // android.service.euicc.IEuiccService
        public void downloadSubscription(int slotId, int portIndex, DownloadableSubscription subscription, boolean switchAfterDownload, boolean forceDeactivateSim, Bundle resolvedBundle, IDownloadSubscriptionCallback callback) throws RemoteException {
        }

        @Override // android.service.euicc.IEuiccService
        public void getDownloadableSubscriptionMetadata(int slotId, int portIndex, DownloadableSubscription subscription, boolean switchAfterDownload, boolean forceDeactivateSim, IGetDownloadableSubscriptionMetadataCallback callback) throws RemoteException {
        }

        @Override // android.service.euicc.IEuiccService
        public void getEid(int slotId, IGetEidCallback callback) throws RemoteException {
        }

        @Override // android.service.euicc.IEuiccService
        public void getOtaStatus(int slotId, IGetOtaStatusCallback callback) throws RemoteException {
        }

        @Override // android.service.euicc.IEuiccService
        public void startOtaIfNecessary(int slotId, IOtaStatusChangedCallback statusChangedCallback) throws RemoteException {
        }

        @Override // android.service.euicc.IEuiccService
        public void getEuiccProfileInfoList(int slotId, IGetEuiccProfileInfoListCallback callback) throws RemoteException {
        }

        @Override // android.service.euicc.IEuiccService
        public void getDefaultDownloadableSubscriptionList(int slotId, boolean forceDeactivateSim, IGetDefaultDownloadableSubscriptionListCallback callback) throws RemoteException {
        }

        @Override // android.service.euicc.IEuiccService
        public void getEuiccInfo(int slotId, IGetEuiccInfoCallback callback) throws RemoteException {
        }

        @Override // android.service.euicc.IEuiccService
        public void deleteSubscription(int slotId, String iccid, IDeleteSubscriptionCallback callback) throws RemoteException {
        }

        @Override // android.service.euicc.IEuiccService
        public void switchToSubscription(int slotId, int portIndex, String iccid, boolean forceDeactivateSim, ISwitchToSubscriptionCallback callback, boolean useLegacyApi) throws RemoteException {
        }

        @Override // android.service.euicc.IEuiccService
        public void updateSubscriptionNickname(int slotId, String iccid, String nickname, IUpdateSubscriptionNicknameCallback callback) throws RemoteException {
        }

        @Override // android.service.euicc.IEuiccService
        public void eraseSubscriptions(int slotId, IEraseSubscriptionsCallback callback) throws RemoteException {
        }

        @Override // android.service.euicc.IEuiccService
        public void eraseSubscriptionsWithOptions(int slotIndex, int options, IEraseSubscriptionsCallback callback) throws RemoteException {
        }

        @Override // android.service.euicc.IEuiccService
        public void retainSubscriptionsForFactoryReset(int slotId, IRetainSubscriptionsForFactoryResetCallback callback) throws RemoteException {
        }

        @Override // android.service.euicc.IEuiccService
        public void dump(IEuiccServiceDumpResultCallback callback) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IEuiccService {
        public static final String DESCRIPTOR = "android.service.euicc.IEuiccService";
        static final int TRANSACTION_deleteSubscription = 9;
        static final int TRANSACTION_downloadSubscription = 1;
        static final int TRANSACTION_dump = 15;
        static final int TRANSACTION_eraseSubscriptions = 12;
        static final int TRANSACTION_eraseSubscriptionsWithOptions = 13;
        static final int TRANSACTION_getDefaultDownloadableSubscriptionList = 7;
        static final int TRANSACTION_getDownloadableSubscriptionMetadata = 2;
        static final int TRANSACTION_getEid = 3;
        static final int TRANSACTION_getEuiccInfo = 8;
        static final int TRANSACTION_getEuiccProfileInfoList = 6;
        static final int TRANSACTION_getOtaStatus = 4;
        static final int TRANSACTION_retainSubscriptionsForFactoryReset = 14;
        static final int TRANSACTION_startOtaIfNecessary = 5;
        static final int TRANSACTION_switchToSubscription = 10;
        static final int TRANSACTION_updateSubscriptionNickname = 11;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IEuiccService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IEuiccService)) {
                return (IEuiccService) iin;
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
                    return "downloadSubscription";
                case 2:
                    return "getDownloadableSubscriptionMetadata";
                case 3:
                    return "getEid";
                case 4:
                    return "getOtaStatus";
                case 5:
                    return "startOtaIfNecessary";
                case 6:
                    return "getEuiccProfileInfoList";
                case 7:
                    return "getDefaultDownloadableSubscriptionList";
                case 8:
                    return "getEuiccInfo";
                case 9:
                    return "deleteSubscription";
                case 10:
                    return "switchToSubscription";
                case 11:
                    return "updateSubscriptionNickname";
                case 12:
                    return "eraseSubscriptions";
                case 13:
                    return "eraseSubscriptionsWithOptions";
                case 14:
                    return "retainSubscriptionsForFactoryReset";
                case 15:
                    return "dump";
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
                            int _arg1 = data.readInt();
                            DownloadableSubscription _arg2 = (DownloadableSubscription) data.readTypedObject(DownloadableSubscription.CREATOR);
                            boolean _arg3 = data.readBoolean();
                            boolean _arg4 = data.readBoolean();
                            Bundle _arg5 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            IDownloadSubscriptionCallback _arg6 = IDownloadSubscriptionCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            downloadSubscription(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            int _arg12 = data.readInt();
                            DownloadableSubscription _arg22 = (DownloadableSubscription) data.readTypedObject(DownloadableSubscription.CREATOR);
                            boolean _arg32 = data.readBoolean();
                            boolean _arg42 = data.readBoolean();
                            IGetDownloadableSubscriptionMetadataCallback _arg52 = IGetDownloadableSubscriptionMetadataCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getDownloadableSubscriptionMetadata(_arg02, _arg12, _arg22, _arg32, _arg42, _arg52);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            IGetEidCallback _arg13 = IGetEidCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getEid(_arg03, _arg13);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            IGetOtaStatusCallback _arg14 = IGetOtaStatusCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getOtaStatus(_arg04, _arg14);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            IOtaStatusChangedCallback _arg15 = IOtaStatusChangedCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            startOtaIfNecessary(_arg05, _arg15);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            IGetEuiccProfileInfoListCallback _arg16 = IGetEuiccProfileInfoListCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getEuiccProfileInfoList(_arg06, _arg16);
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            boolean _arg17 = data.readBoolean();
                            IGetDefaultDownloadableSubscriptionListCallback _arg23 = IGetDefaultDownloadableSubscriptionListCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getDefaultDownloadableSubscriptionList(_arg07, _arg17, _arg23);
                            break;
                        case 8:
                            int _arg08 = data.readInt();
                            IGetEuiccInfoCallback _arg18 = IGetEuiccInfoCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getEuiccInfo(_arg08, _arg18);
                            break;
                        case 9:
                            int _arg09 = data.readInt();
                            String _arg19 = data.readString();
                            IDeleteSubscriptionCallback _arg24 = IDeleteSubscriptionCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            deleteSubscription(_arg09, _arg19, _arg24);
                            break;
                        case 10:
                            int _arg010 = data.readInt();
                            int _arg110 = data.readInt();
                            String _arg25 = data.readString();
                            boolean _arg33 = data.readBoolean();
                            ISwitchToSubscriptionCallback _arg43 = ISwitchToSubscriptionCallback.Stub.asInterface(data.readStrongBinder());
                            boolean _arg53 = data.readBoolean();
                            data.enforceNoDataAvail();
                            switchToSubscription(_arg010, _arg110, _arg25, _arg33, _arg43, _arg53);
                            break;
                        case 11:
                            int _arg011 = data.readInt();
                            String _arg111 = data.readString();
                            String _arg26 = data.readString();
                            IUpdateSubscriptionNicknameCallback _arg34 = IUpdateSubscriptionNicknameCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            updateSubscriptionNickname(_arg011, _arg111, _arg26, _arg34);
                            break;
                        case 12:
                            int _arg012 = data.readInt();
                            IEraseSubscriptionsCallback _arg112 = IEraseSubscriptionsCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            eraseSubscriptions(_arg012, _arg112);
                            break;
                        case 13:
                            int _arg013 = data.readInt();
                            int _arg113 = data.readInt();
                            IEraseSubscriptionsCallback _arg27 = IEraseSubscriptionsCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            eraseSubscriptionsWithOptions(_arg013, _arg113, _arg27);
                            break;
                        case 14:
                            int _arg014 = data.readInt();
                            IRetainSubscriptionsForFactoryResetCallback _arg114 = IRetainSubscriptionsForFactoryResetCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            retainSubscriptionsForFactoryReset(_arg014, _arg114);
                            break;
                        case 15:
                            IEuiccServiceDumpResultCallback _arg015 = IEuiccServiceDumpResultCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            dump(_arg015);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IEuiccService {
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

            @Override // android.service.euicc.IEuiccService
            public void downloadSubscription(int slotId, int portIndex, DownloadableSubscription subscription, boolean switchAfterDownload, boolean forceDeactivateSim, Bundle resolvedBundle, IDownloadSubscriptionCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeInt(portIndex);
                    _data.writeTypedObject(subscription, 0);
                    _data.writeBoolean(switchAfterDownload);
                    _data.writeBoolean(forceDeactivateSim);
                    _data.writeTypedObject(resolvedBundle, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.euicc.IEuiccService
            public void getDownloadableSubscriptionMetadata(int slotId, int portIndex, DownloadableSubscription subscription, boolean switchAfterDownload, boolean forceDeactivateSim, IGetDownloadableSubscriptionMetadataCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeInt(portIndex);
                    _data.writeTypedObject(subscription, 0);
                    _data.writeBoolean(switchAfterDownload);
                    _data.writeBoolean(forceDeactivateSim);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.euicc.IEuiccService
            public void getEid(int slotId, IGetEidCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.euicc.IEuiccService
            public void getOtaStatus(int slotId, IGetOtaStatusCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.euicc.IEuiccService
            public void startOtaIfNecessary(int slotId, IOtaStatusChangedCallback statusChangedCallback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeStrongInterface(statusChangedCallback);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.euicc.IEuiccService
            public void getEuiccProfileInfoList(int slotId, IGetEuiccProfileInfoListCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.euicc.IEuiccService
            public void getDefaultDownloadableSubscriptionList(int slotId, boolean forceDeactivateSim, IGetDefaultDownloadableSubscriptionListCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeBoolean(forceDeactivateSim);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.euicc.IEuiccService
            public void getEuiccInfo(int slotId, IGetEuiccInfoCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.euicc.IEuiccService
            public void deleteSubscription(int slotId, String iccid, IDeleteSubscriptionCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeString(iccid);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.euicc.IEuiccService
            public void switchToSubscription(int slotId, int portIndex, String iccid, boolean forceDeactivateSim, ISwitchToSubscriptionCallback callback, boolean useLegacyApi) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeInt(portIndex);
                    _data.writeString(iccid);
                    _data.writeBoolean(forceDeactivateSim);
                    _data.writeStrongInterface(callback);
                    _data.writeBoolean(useLegacyApi);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.euicc.IEuiccService
            public void updateSubscriptionNickname(int slotId, String iccid, String nickname, IUpdateSubscriptionNicknameCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeString(iccid);
                    _data.writeString(nickname);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.euicc.IEuiccService
            public void eraseSubscriptions(int slotId, IEraseSubscriptionsCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.euicc.IEuiccService
            public void eraseSubscriptionsWithOptions(int slotIndex, int options, IEraseSubscriptionsCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotIndex);
                    _data.writeInt(options);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(13, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.euicc.IEuiccService
            public void retainSubscriptionsForFactoryReset(int slotId, IRetainSubscriptionsForFactoryResetCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotId);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(14, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.euicc.IEuiccService
            public void dump(IEuiccServiceDumpResultCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(15, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 14;
        }
    }
}
