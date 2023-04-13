package com.android.internal.telephony.euicc;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import com.android.internal.telephony.euicc.IAuthenticateServerCallback;
import com.android.internal.telephony.euicc.ICancelSessionCallback;
import com.android.internal.telephony.euicc.IDeleteProfileCallback;
import com.android.internal.telephony.euicc.IDisableProfileCallback;
import com.android.internal.telephony.euicc.IGetAllProfilesCallback;
import com.android.internal.telephony.euicc.IGetDefaultSmdpAddressCallback;
import com.android.internal.telephony.euicc.IGetEuiccChallengeCallback;
import com.android.internal.telephony.euicc.IGetEuiccInfo1Callback;
import com.android.internal.telephony.euicc.IGetEuiccInfo2Callback;
import com.android.internal.telephony.euicc.IGetProfileCallback;
import com.android.internal.telephony.euicc.IGetRulesAuthTableCallback;
import com.android.internal.telephony.euicc.IGetSmdsAddressCallback;
import com.android.internal.telephony.euicc.IListNotificationsCallback;
import com.android.internal.telephony.euicc.ILoadBoundProfilePackageCallback;
import com.android.internal.telephony.euicc.IPrepareDownloadCallback;
import com.android.internal.telephony.euicc.IRemoveNotificationFromListCallback;
import com.android.internal.telephony.euicc.IResetMemoryCallback;
import com.android.internal.telephony.euicc.IRetrieveNotificationCallback;
import com.android.internal.telephony.euicc.IRetrieveNotificationListCallback;
import com.android.internal.telephony.euicc.ISetDefaultSmdpAddressCallback;
import com.android.internal.telephony.euicc.ISetNicknameCallback;
import com.android.internal.telephony.euicc.ISwitchToProfileCallback;
/* loaded from: classes3.dex */
public interface IEuiccCardController extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.telephony.euicc.IEuiccCardController";

    void authenticateServer(String str, String str2, String str3, byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4, IAuthenticateServerCallback iAuthenticateServerCallback) throws RemoteException;

    void cancelSession(String str, String str2, byte[] bArr, int i, ICancelSessionCallback iCancelSessionCallback) throws RemoteException;

    void deleteProfile(String str, String str2, String str3, IDeleteProfileCallback iDeleteProfileCallback) throws RemoteException;

    void disableProfile(String str, String str2, String str3, boolean z, IDisableProfileCallback iDisableProfileCallback) throws RemoteException;

    void getAllProfiles(String str, String str2, IGetAllProfilesCallback iGetAllProfilesCallback) throws RemoteException;

    void getDefaultSmdpAddress(String str, String str2, IGetDefaultSmdpAddressCallback iGetDefaultSmdpAddressCallback) throws RemoteException;

    void getEnabledProfile(String str, String str2, int i, IGetProfileCallback iGetProfileCallback) throws RemoteException;

    void getEuiccChallenge(String str, String str2, IGetEuiccChallengeCallback iGetEuiccChallengeCallback) throws RemoteException;

    void getEuiccInfo1(String str, String str2, IGetEuiccInfo1Callback iGetEuiccInfo1Callback) throws RemoteException;

    void getEuiccInfo2(String str, String str2, IGetEuiccInfo2Callback iGetEuiccInfo2Callback) throws RemoteException;

    void getProfile(String str, String str2, String str3, IGetProfileCallback iGetProfileCallback) throws RemoteException;

    void getRulesAuthTable(String str, String str2, IGetRulesAuthTableCallback iGetRulesAuthTableCallback) throws RemoteException;

    void getSmdsAddress(String str, String str2, IGetSmdsAddressCallback iGetSmdsAddressCallback) throws RemoteException;

    void listNotifications(String str, String str2, int i, IListNotificationsCallback iListNotificationsCallback) throws RemoteException;

    void loadBoundProfilePackage(String str, String str2, byte[] bArr, ILoadBoundProfilePackageCallback iLoadBoundProfilePackageCallback) throws RemoteException;

    void prepareDownload(String str, String str2, byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4, IPrepareDownloadCallback iPrepareDownloadCallback) throws RemoteException;

    void removeNotificationFromList(String str, String str2, int i, IRemoveNotificationFromListCallback iRemoveNotificationFromListCallback) throws RemoteException;

    void resetMemory(String str, String str2, int i, IResetMemoryCallback iResetMemoryCallback) throws RemoteException;

    void retrieveNotification(String str, String str2, int i, IRetrieveNotificationCallback iRetrieveNotificationCallback) throws RemoteException;

    void retrieveNotificationList(String str, String str2, int i, IRetrieveNotificationListCallback iRetrieveNotificationListCallback) throws RemoteException;

    void setDefaultSmdpAddress(String str, String str2, String str3, ISetDefaultSmdpAddressCallback iSetDefaultSmdpAddressCallback) throws RemoteException;

    void setNickname(String str, String str2, String str3, String str4, ISetNicknameCallback iSetNicknameCallback) throws RemoteException;

    void switchToProfile(String str, String str2, String str3, int i, boolean z, ISwitchToProfileCallback iSwitchToProfileCallback) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IEuiccCardController {
        @Override // com.android.internal.telephony.euicc.IEuiccCardController
        public void getAllProfiles(String callingPackage, String cardId, IGetAllProfilesCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.telephony.euicc.IEuiccCardController
        public void getProfile(String callingPackage, String cardId, String iccid, IGetProfileCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.telephony.euicc.IEuiccCardController
        public void getEnabledProfile(String callingPackage, String cardId, int portIndex, IGetProfileCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.telephony.euicc.IEuiccCardController
        public void disableProfile(String callingPackage, String cardId, String iccid, boolean refresh, IDisableProfileCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.telephony.euicc.IEuiccCardController
        public void switchToProfile(String callingPackage, String cardId, String iccid, int portIndex, boolean refresh, ISwitchToProfileCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.telephony.euicc.IEuiccCardController
        public void setNickname(String callingPackage, String cardId, String iccid, String nickname, ISetNicknameCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.telephony.euicc.IEuiccCardController
        public void deleteProfile(String callingPackage, String cardId, String iccid, IDeleteProfileCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.telephony.euicc.IEuiccCardController
        public void resetMemory(String callingPackage, String cardId, int options, IResetMemoryCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.telephony.euicc.IEuiccCardController
        public void getDefaultSmdpAddress(String callingPackage, String cardId, IGetDefaultSmdpAddressCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.telephony.euicc.IEuiccCardController
        public void getSmdsAddress(String callingPackage, String cardId, IGetSmdsAddressCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.telephony.euicc.IEuiccCardController
        public void setDefaultSmdpAddress(String callingPackage, String cardId, String address, ISetDefaultSmdpAddressCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.telephony.euicc.IEuiccCardController
        public void getRulesAuthTable(String callingPackage, String cardId, IGetRulesAuthTableCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.telephony.euicc.IEuiccCardController
        public void getEuiccChallenge(String callingPackage, String cardId, IGetEuiccChallengeCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.telephony.euicc.IEuiccCardController
        public void getEuiccInfo1(String callingPackage, String cardId, IGetEuiccInfo1Callback callback) throws RemoteException {
        }

        @Override // com.android.internal.telephony.euicc.IEuiccCardController
        public void getEuiccInfo2(String callingPackage, String cardId, IGetEuiccInfo2Callback callback) throws RemoteException {
        }

        @Override // com.android.internal.telephony.euicc.IEuiccCardController
        public void authenticateServer(String callingPackage, String cardId, String matchingId, byte[] serverSigned1, byte[] serverSignature1, byte[] euiccCiPkIdToBeUsed, byte[] serverCertificatein, IAuthenticateServerCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.telephony.euicc.IEuiccCardController
        public void prepareDownload(String callingPackage, String cardId, byte[] hashCc, byte[] smdpSigned2, byte[] smdpSignature2, byte[] smdpCertificate, IPrepareDownloadCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.telephony.euicc.IEuiccCardController
        public void loadBoundProfilePackage(String callingPackage, String cardId, byte[] boundProfilePackage, ILoadBoundProfilePackageCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.telephony.euicc.IEuiccCardController
        public void cancelSession(String callingPackage, String cardId, byte[] transactionId, int reason, ICancelSessionCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.telephony.euicc.IEuiccCardController
        public void listNotifications(String callingPackage, String cardId, int events, IListNotificationsCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.telephony.euicc.IEuiccCardController
        public void retrieveNotificationList(String callingPackage, String cardId, int events, IRetrieveNotificationListCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.telephony.euicc.IEuiccCardController
        public void retrieveNotification(String callingPackage, String cardId, int seqNumber, IRetrieveNotificationCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.telephony.euicc.IEuiccCardController
        public void removeNotificationFromList(String callingPackage, String cardId, int seqNumber, IRemoveNotificationFromListCallback callback) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IEuiccCardController {
        static final int TRANSACTION_authenticateServer = 16;
        static final int TRANSACTION_cancelSession = 19;
        static final int TRANSACTION_deleteProfile = 7;
        static final int TRANSACTION_disableProfile = 4;
        static final int TRANSACTION_getAllProfiles = 1;
        static final int TRANSACTION_getDefaultSmdpAddress = 9;
        static final int TRANSACTION_getEnabledProfile = 3;
        static final int TRANSACTION_getEuiccChallenge = 13;
        static final int TRANSACTION_getEuiccInfo1 = 14;
        static final int TRANSACTION_getEuiccInfo2 = 15;
        static final int TRANSACTION_getProfile = 2;
        static final int TRANSACTION_getRulesAuthTable = 12;
        static final int TRANSACTION_getSmdsAddress = 10;
        static final int TRANSACTION_listNotifications = 20;
        static final int TRANSACTION_loadBoundProfilePackage = 18;
        static final int TRANSACTION_prepareDownload = 17;
        static final int TRANSACTION_removeNotificationFromList = 23;
        static final int TRANSACTION_resetMemory = 8;
        static final int TRANSACTION_retrieveNotification = 22;
        static final int TRANSACTION_retrieveNotificationList = 21;
        static final int TRANSACTION_setDefaultSmdpAddress = 11;
        static final int TRANSACTION_setNickname = 6;
        static final int TRANSACTION_switchToProfile = 5;

        public Stub() {
            attachInterface(this, IEuiccCardController.DESCRIPTOR);
        }

        public static IEuiccCardController asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IEuiccCardController.DESCRIPTOR);
            if (iin != null && (iin instanceof IEuiccCardController)) {
                return (IEuiccCardController) iin;
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
                    return "getAllProfiles";
                case 2:
                    return "getProfile";
                case 3:
                    return "getEnabledProfile";
                case 4:
                    return "disableProfile";
                case 5:
                    return "switchToProfile";
                case 6:
                    return "setNickname";
                case 7:
                    return "deleteProfile";
                case 8:
                    return "resetMemory";
                case 9:
                    return "getDefaultSmdpAddress";
                case 10:
                    return "getSmdsAddress";
                case 11:
                    return "setDefaultSmdpAddress";
                case 12:
                    return "getRulesAuthTable";
                case 13:
                    return "getEuiccChallenge";
                case 14:
                    return "getEuiccInfo1";
                case 15:
                    return "getEuiccInfo2";
                case 16:
                    return "authenticateServer";
                case 17:
                    return "prepareDownload";
                case 18:
                    return "loadBoundProfilePackage";
                case 19:
                    return "cancelSession";
                case 20:
                    return "listNotifications";
                case 21:
                    return "retrieveNotificationList";
                case 22:
                    return "retrieveNotification";
                case 23:
                    return "removeNotificationFromList";
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
                data.enforceInterface(IEuiccCardController.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IEuiccCardController.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            String _arg1 = data.readString();
                            IGetAllProfilesCallback _arg2 = IGetAllProfilesCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getAllProfiles(_arg0, _arg1, _arg2);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            String _arg12 = data.readString();
                            String _arg22 = data.readString();
                            IGetProfileCallback _arg3 = IGetProfileCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getProfile(_arg02, _arg12, _arg22, _arg3);
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            String _arg13 = data.readString();
                            int _arg23 = data.readInt();
                            IGetProfileCallback _arg32 = IGetProfileCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getEnabledProfile(_arg03, _arg13, _arg23, _arg32);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            String _arg14 = data.readString();
                            String _arg24 = data.readString();
                            boolean _arg33 = data.readBoolean();
                            IDisableProfileCallback _arg4 = IDisableProfileCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            disableProfile(_arg04, _arg14, _arg24, _arg33, _arg4);
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            String _arg15 = data.readString();
                            String _arg25 = data.readString();
                            int _arg34 = data.readInt();
                            boolean _arg42 = data.readBoolean();
                            ISwitchToProfileCallback _arg5 = ISwitchToProfileCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            switchToProfile(_arg05, _arg15, _arg25, _arg34, _arg42, _arg5);
                            break;
                        case 6:
                            String _arg06 = data.readString();
                            String _arg16 = data.readString();
                            String _arg26 = data.readString();
                            String _arg35 = data.readString();
                            ISetNicknameCallback _arg43 = ISetNicknameCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setNickname(_arg06, _arg16, _arg26, _arg35, _arg43);
                            break;
                        case 7:
                            String _arg07 = data.readString();
                            String _arg17 = data.readString();
                            String _arg27 = data.readString();
                            IDeleteProfileCallback _arg36 = IDeleteProfileCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            deleteProfile(_arg07, _arg17, _arg27, _arg36);
                            break;
                        case 8:
                            String _arg08 = data.readString();
                            String _arg18 = data.readString();
                            int _arg28 = data.readInt();
                            IResetMemoryCallback _arg37 = IResetMemoryCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            resetMemory(_arg08, _arg18, _arg28, _arg37);
                            break;
                        case 9:
                            String _arg09 = data.readString();
                            String _arg19 = data.readString();
                            IGetDefaultSmdpAddressCallback _arg29 = IGetDefaultSmdpAddressCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getDefaultSmdpAddress(_arg09, _arg19, _arg29);
                            break;
                        case 10:
                            String _arg010 = data.readString();
                            String _arg110 = data.readString();
                            IGetSmdsAddressCallback _arg210 = IGetSmdsAddressCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getSmdsAddress(_arg010, _arg110, _arg210);
                            break;
                        case 11:
                            String _arg011 = data.readString();
                            String _arg111 = data.readString();
                            String _arg211 = data.readString();
                            ISetDefaultSmdpAddressCallback _arg38 = ISetDefaultSmdpAddressCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setDefaultSmdpAddress(_arg011, _arg111, _arg211, _arg38);
                            break;
                        case 12:
                            String _arg012 = data.readString();
                            String _arg112 = data.readString();
                            IGetRulesAuthTableCallback _arg212 = IGetRulesAuthTableCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getRulesAuthTable(_arg012, _arg112, _arg212);
                            break;
                        case 13:
                            String _arg013 = data.readString();
                            String _arg113 = data.readString();
                            IGetEuiccChallengeCallback _arg213 = IGetEuiccChallengeCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getEuiccChallenge(_arg013, _arg113, _arg213);
                            break;
                        case 14:
                            String _arg014 = data.readString();
                            String _arg114 = data.readString();
                            IGetEuiccInfo1Callback _arg214 = IGetEuiccInfo1Callback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getEuiccInfo1(_arg014, _arg114, _arg214);
                            break;
                        case 15:
                            String _arg015 = data.readString();
                            String _arg115 = data.readString();
                            IGetEuiccInfo2Callback _arg215 = IGetEuiccInfo2Callback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            getEuiccInfo2(_arg015, _arg115, _arg215);
                            break;
                        case 16:
                            String _arg016 = data.readString();
                            String _arg116 = data.readString();
                            String _arg216 = data.readString();
                            byte[] _arg39 = data.createByteArray();
                            byte[] _arg44 = data.createByteArray();
                            byte[] _arg52 = data.createByteArray();
                            byte[] _arg6 = data.createByteArray();
                            IAuthenticateServerCallback _arg7 = IAuthenticateServerCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            authenticateServer(_arg016, _arg116, _arg216, _arg39, _arg44, _arg52, _arg6, _arg7);
                            break;
                        case 17:
                            String _arg017 = data.readString();
                            String _arg117 = data.readString();
                            byte[] _arg217 = data.createByteArray();
                            byte[] _arg310 = data.createByteArray();
                            byte[] _arg45 = data.createByteArray();
                            byte[] _arg53 = data.createByteArray();
                            IPrepareDownloadCallback _arg62 = IPrepareDownloadCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            prepareDownload(_arg017, _arg117, _arg217, _arg310, _arg45, _arg53, _arg62);
                            break;
                        case 18:
                            String _arg018 = data.readString();
                            String _arg118 = data.readString();
                            byte[] _arg218 = data.createByteArray();
                            ILoadBoundProfilePackageCallback _arg311 = ILoadBoundProfilePackageCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            loadBoundProfilePackage(_arg018, _arg118, _arg218, _arg311);
                            break;
                        case 19:
                            String _arg019 = data.readString();
                            String _arg119 = data.readString();
                            byte[] _arg219 = data.createByteArray();
                            int _arg312 = data.readInt();
                            ICancelSessionCallback _arg46 = ICancelSessionCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            cancelSession(_arg019, _arg119, _arg219, _arg312, _arg46);
                            break;
                        case 20:
                            String _arg020 = data.readString();
                            String _arg120 = data.readString();
                            int _arg220 = data.readInt();
                            IListNotificationsCallback _arg313 = IListNotificationsCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            listNotifications(_arg020, _arg120, _arg220, _arg313);
                            break;
                        case 21:
                            String _arg021 = data.readString();
                            String _arg121 = data.readString();
                            int _arg221 = data.readInt();
                            IRetrieveNotificationListCallback _arg314 = IRetrieveNotificationListCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            retrieveNotificationList(_arg021, _arg121, _arg221, _arg314);
                            break;
                        case 22:
                            String _arg022 = data.readString();
                            String _arg122 = data.readString();
                            int _arg222 = data.readInt();
                            IRetrieveNotificationCallback _arg315 = IRetrieveNotificationCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            retrieveNotification(_arg022, _arg122, _arg222, _arg315);
                            break;
                        case 23:
                            String _arg023 = data.readString();
                            String _arg123 = data.readString();
                            int _arg223 = data.readInt();
                            IRemoveNotificationFromListCallback _arg316 = IRemoveNotificationFromListCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeNotificationFromList(_arg023, _arg123, _arg223, _arg316);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class Proxy implements IEuiccCardController {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IEuiccCardController.DESCRIPTOR;
            }

            @Override // com.android.internal.telephony.euicc.IEuiccCardController
            public void getAllProfiles(String callingPackage, String cardId, IGetAllProfilesCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IEuiccCardController.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(cardId);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccCardController
            public void getProfile(String callingPackage, String cardId, String iccid, IGetProfileCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IEuiccCardController.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(cardId);
                    _data.writeString(iccid);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccCardController
            public void getEnabledProfile(String callingPackage, String cardId, int portIndex, IGetProfileCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IEuiccCardController.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(cardId);
                    _data.writeInt(portIndex);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccCardController
            public void disableProfile(String callingPackage, String cardId, String iccid, boolean refresh, IDisableProfileCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IEuiccCardController.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(cardId);
                    _data.writeString(iccid);
                    _data.writeBoolean(refresh);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccCardController
            public void switchToProfile(String callingPackage, String cardId, String iccid, int portIndex, boolean refresh, ISwitchToProfileCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IEuiccCardController.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(cardId);
                    _data.writeString(iccid);
                    _data.writeInt(portIndex);
                    _data.writeBoolean(refresh);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccCardController
            public void setNickname(String callingPackage, String cardId, String iccid, String nickname, ISetNicknameCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IEuiccCardController.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(cardId);
                    _data.writeString(iccid);
                    _data.writeString(nickname);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccCardController
            public void deleteProfile(String callingPackage, String cardId, String iccid, IDeleteProfileCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IEuiccCardController.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(cardId);
                    _data.writeString(iccid);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccCardController
            public void resetMemory(String callingPackage, String cardId, int options, IResetMemoryCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IEuiccCardController.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(cardId);
                    _data.writeInt(options);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccCardController
            public void getDefaultSmdpAddress(String callingPackage, String cardId, IGetDefaultSmdpAddressCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IEuiccCardController.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(cardId);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccCardController
            public void getSmdsAddress(String callingPackage, String cardId, IGetSmdsAddressCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IEuiccCardController.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(cardId);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccCardController
            public void setDefaultSmdpAddress(String callingPackage, String cardId, String address, ISetDefaultSmdpAddressCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IEuiccCardController.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(cardId);
                    _data.writeString(address);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccCardController
            public void getRulesAuthTable(String callingPackage, String cardId, IGetRulesAuthTableCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IEuiccCardController.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(cardId);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccCardController
            public void getEuiccChallenge(String callingPackage, String cardId, IGetEuiccChallengeCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IEuiccCardController.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(cardId);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(13, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccCardController
            public void getEuiccInfo1(String callingPackage, String cardId, IGetEuiccInfo1Callback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IEuiccCardController.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(cardId);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(14, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccCardController
            public void getEuiccInfo2(String callingPackage, String cardId, IGetEuiccInfo2Callback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IEuiccCardController.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(cardId);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(15, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccCardController
            public void authenticateServer(String callingPackage, String cardId, String matchingId, byte[] serverSigned1, byte[] serverSignature1, byte[] euiccCiPkIdToBeUsed, byte[] serverCertificatein, IAuthenticateServerCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IEuiccCardController.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(cardId);
                    _data.writeString(matchingId);
                    _data.writeByteArray(serverSigned1);
                    _data.writeByteArray(serverSignature1);
                    _data.writeByteArray(euiccCiPkIdToBeUsed);
                    _data.writeByteArray(serverCertificatein);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(16, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccCardController
            public void prepareDownload(String callingPackage, String cardId, byte[] hashCc, byte[] smdpSigned2, byte[] smdpSignature2, byte[] smdpCertificate, IPrepareDownloadCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IEuiccCardController.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(cardId);
                    _data.writeByteArray(hashCc);
                    _data.writeByteArray(smdpSigned2);
                    _data.writeByteArray(smdpSignature2);
                    _data.writeByteArray(smdpCertificate);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(17, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccCardController
            public void loadBoundProfilePackage(String callingPackage, String cardId, byte[] boundProfilePackage, ILoadBoundProfilePackageCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IEuiccCardController.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(cardId);
                    _data.writeByteArray(boundProfilePackage);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(18, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccCardController
            public void cancelSession(String callingPackage, String cardId, byte[] transactionId, int reason, ICancelSessionCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IEuiccCardController.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(cardId);
                    _data.writeByteArray(transactionId);
                    _data.writeInt(reason);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(19, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccCardController
            public void listNotifications(String callingPackage, String cardId, int events, IListNotificationsCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IEuiccCardController.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(cardId);
                    _data.writeInt(events);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(20, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccCardController
            public void retrieveNotificationList(String callingPackage, String cardId, int events, IRetrieveNotificationListCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IEuiccCardController.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(cardId);
                    _data.writeInt(events);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(21, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccCardController
            public void retrieveNotification(String callingPackage, String cardId, int seqNumber, IRetrieveNotificationCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IEuiccCardController.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(cardId);
                    _data.writeInt(seqNumber);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(22, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccCardController
            public void removeNotificationFromList(String callingPackage, String cardId, int seqNumber, IRemoveNotificationFromListCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IEuiccCardController.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(cardId);
                    _data.writeInt(seqNumber);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(23, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 22;
        }
    }
}
