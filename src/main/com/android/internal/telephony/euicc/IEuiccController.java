package com.android.internal.telephony.euicc;

import android.app.PendingIntent;
import android.content.Intent;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telephony.euicc.DownloadableSubscription;
import android.telephony.euicc.EuiccInfo;
import java.util.List;
/* loaded from: classes3.dex */
public interface IEuiccController extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.telephony.euicc.IEuiccController";

    void continueOperation(int i, Intent intent, Bundle bundle) throws RemoteException;

    void deleteSubscription(int i, int i2, String str, PendingIntent pendingIntent) throws RemoteException;

    void downloadSubscription(int i, DownloadableSubscription downloadableSubscription, boolean z, String str, Bundle bundle, PendingIntent pendingIntent) throws RemoteException;

    void eraseSubscriptions(int i, PendingIntent pendingIntent) throws RemoteException;

    void eraseSubscriptionsWithOptions(int i, int i2, PendingIntent pendingIntent) throws RemoteException;

    void getDefaultDownloadableSubscriptionList(int i, String str, PendingIntent pendingIntent) throws RemoteException;

    void getDownloadableSubscriptionMetadata(int i, DownloadableSubscription downloadableSubscription, String str, PendingIntent pendingIntent) throws RemoteException;

    String getEid(int i, String str) throws RemoteException;

    EuiccInfo getEuiccInfo(int i) throws RemoteException;

    int getOtaStatus(int i) throws RemoteException;

    List<String> getSupportedCountries(boolean z) throws RemoteException;

    boolean hasCarrierPrivilegesForPackageOnAnyPhone(String str) throws RemoteException;

    boolean isCompatChangeEnabled(String str, long j) throws RemoteException;

    boolean isSimPortAvailable(int i, int i2, String str) throws RemoteException;

    boolean isSupportedCountry(String str) throws RemoteException;

    void retainSubscriptionsForFactoryReset(int i, PendingIntent pendingIntent) throws RemoteException;

    void setSupportedCountries(boolean z, List<String> list) throws RemoteException;

    void switchToSubscription(int i, int i2, String str, PendingIntent pendingIntent) throws RemoteException;

    void switchToSubscriptionWithPort(int i, int i2, int i3, String str, PendingIntent pendingIntent) throws RemoteException;

    void updateSubscriptionNickname(int i, int i2, String str, String str2, PendingIntent pendingIntent) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IEuiccController {
        @Override // com.android.internal.telephony.euicc.IEuiccController
        public void continueOperation(int cardId, Intent resolutionIntent, Bundle resolutionExtras) throws RemoteException {
        }

        @Override // com.android.internal.telephony.euicc.IEuiccController
        public void getDownloadableSubscriptionMetadata(int cardId, DownloadableSubscription subscription, String callingPackage, PendingIntent callbackIntent) throws RemoteException {
        }

        @Override // com.android.internal.telephony.euicc.IEuiccController
        public void getDefaultDownloadableSubscriptionList(int cardId, String callingPackage, PendingIntent callbackIntent) throws RemoteException {
        }

        @Override // com.android.internal.telephony.euicc.IEuiccController
        public String getEid(int cardId, String callingPackage) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.telephony.euicc.IEuiccController
        public int getOtaStatus(int cardId) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.telephony.euicc.IEuiccController
        public void downloadSubscription(int cardId, DownloadableSubscription subscription, boolean switchAfterDownload, String callingPackage, Bundle resolvedBundle, PendingIntent callbackIntent) throws RemoteException {
        }

        @Override // com.android.internal.telephony.euicc.IEuiccController
        public EuiccInfo getEuiccInfo(int cardId) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.telephony.euicc.IEuiccController
        public void deleteSubscription(int cardId, int subscriptionId, String callingPackage, PendingIntent callbackIntent) throws RemoteException {
        }

        @Override // com.android.internal.telephony.euicc.IEuiccController
        public void switchToSubscription(int cardId, int subscriptionId, String callingPackage, PendingIntent callbackIntent) throws RemoteException {
        }

        @Override // com.android.internal.telephony.euicc.IEuiccController
        public void switchToSubscriptionWithPort(int cardId, int subscriptionId, int portIndex, String callingPackage, PendingIntent callbackIntent) throws RemoteException {
        }

        @Override // com.android.internal.telephony.euicc.IEuiccController
        public void updateSubscriptionNickname(int cardId, int subscriptionId, String nickname, String callingPackage, PendingIntent callbackIntent) throws RemoteException {
        }

        @Override // com.android.internal.telephony.euicc.IEuiccController
        public void eraseSubscriptions(int cardId, PendingIntent callbackIntent) throws RemoteException {
        }

        @Override // com.android.internal.telephony.euicc.IEuiccController
        public void eraseSubscriptionsWithOptions(int cardId, int options, PendingIntent callbackIntent) throws RemoteException {
        }

        @Override // com.android.internal.telephony.euicc.IEuiccController
        public void retainSubscriptionsForFactoryReset(int cardId, PendingIntent callbackIntent) throws RemoteException {
        }

        @Override // com.android.internal.telephony.euicc.IEuiccController
        public void setSupportedCountries(boolean isSupported, List<String> countriesList) throws RemoteException {
        }

        @Override // com.android.internal.telephony.euicc.IEuiccController
        public List<String> getSupportedCountries(boolean isSupported) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.telephony.euicc.IEuiccController
        public boolean isSupportedCountry(String countryIso) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.telephony.euicc.IEuiccController
        public boolean isSimPortAvailable(int cardId, int portIndex, String callingPackage) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.telephony.euicc.IEuiccController
        public boolean hasCarrierPrivilegesForPackageOnAnyPhone(String callingPackage) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.telephony.euicc.IEuiccController
        public boolean isCompatChangeEnabled(String callingPackage, long changeId) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IEuiccController {
        static final int TRANSACTION_continueOperation = 1;
        static final int TRANSACTION_deleteSubscription = 8;
        static final int TRANSACTION_downloadSubscription = 6;
        static final int TRANSACTION_eraseSubscriptions = 12;
        static final int TRANSACTION_eraseSubscriptionsWithOptions = 13;
        static final int TRANSACTION_getDefaultDownloadableSubscriptionList = 3;
        static final int TRANSACTION_getDownloadableSubscriptionMetadata = 2;
        static final int TRANSACTION_getEid = 4;
        static final int TRANSACTION_getEuiccInfo = 7;
        static final int TRANSACTION_getOtaStatus = 5;
        static final int TRANSACTION_getSupportedCountries = 16;
        static final int TRANSACTION_hasCarrierPrivilegesForPackageOnAnyPhone = 19;
        static final int TRANSACTION_isCompatChangeEnabled = 20;
        static final int TRANSACTION_isSimPortAvailable = 18;
        static final int TRANSACTION_isSupportedCountry = 17;
        static final int TRANSACTION_retainSubscriptionsForFactoryReset = 14;
        static final int TRANSACTION_setSupportedCountries = 15;
        static final int TRANSACTION_switchToSubscription = 9;
        static final int TRANSACTION_switchToSubscriptionWithPort = 10;
        static final int TRANSACTION_updateSubscriptionNickname = 11;

        public Stub() {
            attachInterface(this, IEuiccController.DESCRIPTOR);
        }

        public static IEuiccController asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IEuiccController.DESCRIPTOR);
            if (iin != null && (iin instanceof IEuiccController)) {
                return (IEuiccController) iin;
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
                    return "continueOperation";
                case 2:
                    return "getDownloadableSubscriptionMetadata";
                case 3:
                    return "getDefaultDownloadableSubscriptionList";
                case 4:
                    return "getEid";
                case 5:
                    return "getOtaStatus";
                case 6:
                    return "downloadSubscription";
                case 7:
                    return "getEuiccInfo";
                case 8:
                    return "deleteSubscription";
                case 9:
                    return "switchToSubscription";
                case 10:
                    return "switchToSubscriptionWithPort";
                case 11:
                    return "updateSubscriptionNickname";
                case 12:
                    return "eraseSubscriptions";
                case 13:
                    return "eraseSubscriptionsWithOptions";
                case 14:
                    return "retainSubscriptionsForFactoryReset";
                case 15:
                    return "setSupportedCountries";
                case 16:
                    return "getSupportedCountries";
                case 17:
                    return "isSupportedCountry";
                case 18:
                    return "isSimPortAvailable";
                case 19:
                    return "hasCarrierPrivilegesForPackageOnAnyPhone";
                case 20:
                    return "isCompatChangeEnabled";
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
                data.enforceInterface(IEuiccController.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IEuiccController.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            Intent _arg1 = (Intent) data.readTypedObject(Intent.CREATOR);
                            Bundle _arg2 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            continueOperation(_arg0, _arg1, _arg2);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            DownloadableSubscription _arg12 = (DownloadableSubscription) data.readTypedObject(DownloadableSubscription.CREATOR);
                            String _arg22 = data.readString();
                            PendingIntent _arg3 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            data.enforceNoDataAvail();
                            getDownloadableSubscriptionMetadata(_arg02, _arg12, _arg22, _arg3);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            String _arg13 = data.readString();
                            PendingIntent _arg23 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            data.enforceNoDataAvail();
                            getDefaultDownloadableSubscriptionList(_arg03, _arg13, _arg23);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            String _arg14 = data.readString();
                            data.enforceNoDataAvail();
                            String _result = getEid(_arg04, _arg14);
                            reply.writeNoException();
                            reply.writeString(_result);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result2 = getOtaStatus(_arg05);
                            reply.writeNoException();
                            reply.writeInt(_result2);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            DownloadableSubscription _arg15 = (DownloadableSubscription) data.readTypedObject(DownloadableSubscription.CREATOR);
                            boolean _arg24 = data.readBoolean();
                            String _arg32 = data.readString();
                            Bundle _arg4 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            PendingIntent _arg5 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            data.enforceNoDataAvail();
                            downloadSubscription(_arg06, _arg15, _arg24, _arg32, _arg4, _arg5);
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            data.enforceNoDataAvail();
                            EuiccInfo _result3 = getEuiccInfo(_arg07);
                            reply.writeNoException();
                            reply.writeTypedObject(_result3, 1);
                            break;
                        case 8:
                            int _arg08 = data.readInt();
                            int _arg16 = data.readInt();
                            String _arg25 = data.readString();
                            PendingIntent _arg33 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            data.enforceNoDataAvail();
                            deleteSubscription(_arg08, _arg16, _arg25, _arg33);
                            break;
                        case 9:
                            int _arg09 = data.readInt();
                            int _arg17 = data.readInt();
                            String _arg26 = data.readString();
                            PendingIntent _arg34 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            data.enforceNoDataAvail();
                            switchToSubscription(_arg09, _arg17, _arg26, _arg34);
                            break;
                        case 10:
                            int _arg010 = data.readInt();
                            int _arg18 = data.readInt();
                            int _arg27 = data.readInt();
                            String _arg35 = data.readString();
                            PendingIntent _arg42 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            data.enforceNoDataAvail();
                            switchToSubscriptionWithPort(_arg010, _arg18, _arg27, _arg35, _arg42);
                            break;
                        case 11:
                            int _arg011 = data.readInt();
                            int _arg19 = data.readInt();
                            String _arg28 = data.readString();
                            String _arg36 = data.readString();
                            PendingIntent _arg43 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            data.enforceNoDataAvail();
                            updateSubscriptionNickname(_arg011, _arg19, _arg28, _arg36, _arg43);
                            break;
                        case 12:
                            int _arg012 = data.readInt();
                            PendingIntent _arg110 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            data.enforceNoDataAvail();
                            eraseSubscriptions(_arg012, _arg110);
                            break;
                        case 13:
                            int _arg013 = data.readInt();
                            int _arg111 = data.readInt();
                            PendingIntent _arg29 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            data.enforceNoDataAvail();
                            eraseSubscriptionsWithOptions(_arg013, _arg111, _arg29);
                            break;
                        case 14:
                            int _arg014 = data.readInt();
                            PendingIntent _arg112 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            data.enforceNoDataAvail();
                            retainSubscriptionsForFactoryReset(_arg014, _arg112);
                            break;
                        case 15:
                            boolean _arg015 = data.readBoolean();
                            List<String> _arg113 = data.createStringArrayList();
                            data.enforceNoDataAvail();
                            setSupportedCountries(_arg015, _arg113);
                            reply.writeNoException();
                            break;
                        case 16:
                            boolean _arg016 = data.readBoolean();
                            data.enforceNoDataAvail();
                            List<String> _result4 = getSupportedCountries(_arg016);
                            reply.writeNoException();
                            reply.writeStringList(_result4);
                            break;
                        case 17:
                            String _arg017 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result5 = isSupportedCountry(_arg017);
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            break;
                        case 18:
                            int _arg018 = data.readInt();
                            int _arg114 = data.readInt();
                            String _arg210 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result6 = isSimPortAvailable(_arg018, _arg114, _arg210);
                            reply.writeNoException();
                            reply.writeBoolean(_result6);
                            break;
                        case 19:
                            String _arg019 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result7 = hasCarrierPrivilegesForPackageOnAnyPhone(_arg019);
                            reply.writeNoException();
                            reply.writeBoolean(_result7);
                            break;
                        case 20:
                            String _arg020 = data.readString();
                            long _arg115 = data.readLong();
                            data.enforceNoDataAvail();
                            boolean _result8 = isCompatChangeEnabled(_arg020, _arg115);
                            reply.writeNoException();
                            reply.writeBoolean(_result8);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class Proxy implements IEuiccController {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IEuiccController.DESCRIPTOR;
            }

            @Override // com.android.internal.telephony.euicc.IEuiccController
            public void continueOperation(int cardId, Intent resolutionIntent, Bundle resolutionExtras) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IEuiccController.DESCRIPTOR);
                    _data.writeInt(cardId);
                    _data.writeTypedObject(resolutionIntent, 0);
                    _data.writeTypedObject(resolutionExtras, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccController
            public void getDownloadableSubscriptionMetadata(int cardId, DownloadableSubscription subscription, String callingPackage, PendingIntent callbackIntent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IEuiccController.DESCRIPTOR);
                    _data.writeInt(cardId);
                    _data.writeTypedObject(subscription, 0);
                    _data.writeString(callingPackage);
                    _data.writeTypedObject(callbackIntent, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccController
            public void getDefaultDownloadableSubscriptionList(int cardId, String callingPackage, PendingIntent callbackIntent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IEuiccController.DESCRIPTOR);
                    _data.writeInt(cardId);
                    _data.writeString(callingPackage);
                    _data.writeTypedObject(callbackIntent, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccController
            public String getEid(int cardId, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IEuiccController.DESCRIPTOR);
                    _data.writeInt(cardId);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccController
            public int getOtaStatus(int cardId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IEuiccController.DESCRIPTOR);
                    _data.writeInt(cardId);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccController
            public void downloadSubscription(int cardId, DownloadableSubscription subscription, boolean switchAfterDownload, String callingPackage, Bundle resolvedBundle, PendingIntent callbackIntent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IEuiccController.DESCRIPTOR);
                    _data.writeInt(cardId);
                    _data.writeTypedObject(subscription, 0);
                    _data.writeBoolean(switchAfterDownload);
                    _data.writeString(callingPackage);
                    _data.writeTypedObject(resolvedBundle, 0);
                    _data.writeTypedObject(callbackIntent, 0);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccController
            public EuiccInfo getEuiccInfo(int cardId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IEuiccController.DESCRIPTOR);
                    _data.writeInt(cardId);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    EuiccInfo _result = (EuiccInfo) _reply.readTypedObject(EuiccInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccController
            public void deleteSubscription(int cardId, int subscriptionId, String callingPackage, PendingIntent callbackIntent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IEuiccController.DESCRIPTOR);
                    _data.writeInt(cardId);
                    _data.writeInt(subscriptionId);
                    _data.writeString(callingPackage);
                    _data.writeTypedObject(callbackIntent, 0);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccController
            public void switchToSubscription(int cardId, int subscriptionId, String callingPackage, PendingIntent callbackIntent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IEuiccController.DESCRIPTOR);
                    _data.writeInt(cardId);
                    _data.writeInt(subscriptionId);
                    _data.writeString(callingPackage);
                    _data.writeTypedObject(callbackIntent, 0);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccController
            public void switchToSubscriptionWithPort(int cardId, int subscriptionId, int portIndex, String callingPackage, PendingIntent callbackIntent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IEuiccController.DESCRIPTOR);
                    _data.writeInt(cardId);
                    _data.writeInt(subscriptionId);
                    _data.writeInt(portIndex);
                    _data.writeString(callingPackage);
                    _data.writeTypedObject(callbackIntent, 0);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccController
            public void updateSubscriptionNickname(int cardId, int subscriptionId, String nickname, String callingPackage, PendingIntent callbackIntent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IEuiccController.DESCRIPTOR);
                    _data.writeInt(cardId);
                    _data.writeInt(subscriptionId);
                    _data.writeString(nickname);
                    _data.writeString(callingPackage);
                    _data.writeTypedObject(callbackIntent, 0);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccController
            public void eraseSubscriptions(int cardId, PendingIntent callbackIntent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IEuiccController.DESCRIPTOR);
                    _data.writeInt(cardId);
                    _data.writeTypedObject(callbackIntent, 0);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccController
            public void eraseSubscriptionsWithOptions(int cardId, int options, PendingIntent callbackIntent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IEuiccController.DESCRIPTOR);
                    _data.writeInt(cardId);
                    _data.writeInt(options);
                    _data.writeTypedObject(callbackIntent, 0);
                    this.mRemote.transact(13, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccController
            public void retainSubscriptionsForFactoryReset(int cardId, PendingIntent callbackIntent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IEuiccController.DESCRIPTOR);
                    _data.writeInt(cardId);
                    _data.writeTypedObject(callbackIntent, 0);
                    this.mRemote.transact(14, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccController
            public void setSupportedCountries(boolean isSupported, List<String> countriesList) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IEuiccController.DESCRIPTOR);
                    _data.writeBoolean(isSupported);
                    _data.writeStringList(countriesList);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccController
            public List<String> getSupportedCountries(boolean isSupported) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IEuiccController.DESCRIPTOR);
                    _data.writeBoolean(isSupported);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccController
            public boolean isSupportedCountry(String countryIso) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IEuiccController.DESCRIPTOR);
                    _data.writeString(countryIso);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccController
            public boolean isSimPortAvailable(int cardId, int portIndex, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IEuiccController.DESCRIPTOR);
                    _data.writeInt(cardId);
                    _data.writeInt(portIndex);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccController
            public boolean hasCarrierPrivilegesForPackageOnAnyPhone(String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IEuiccController.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.euicc.IEuiccController
            public boolean isCompatChangeEnabled(String callingPackage, long changeId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IEuiccController.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeLong(changeId);
                    this.mRemote.transact(20, _data, _reply, 0);
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
            return 19;
        }
    }
}
