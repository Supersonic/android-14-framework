package com.android.internal.telephony;

import android.app.PendingIntent;
import android.net.Uri;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes3.dex */
public interface ISms extends IInterface {
    int checkSmsShortCodeDestination(int i, String str, String str2, String str3, String str4) throws RemoteException;

    void clearStorageMonitorMemoryStatusOverride(int i) throws RemoteException;

    boolean copyMessageToIccEfForSubscriber(int i, String str, int i2, byte[] bArr, byte[] bArr2) throws RemoteException;

    String createAppSpecificSmsToken(int i, String str, PendingIntent pendingIntent) throws RemoteException;

    String createAppSpecificSmsTokenWithPackageInfo(int i, String str, String str2, PendingIntent pendingIntent) throws RemoteException;

    boolean disableCellBroadcastForSubscriber(int i, int i2, int i3) throws RemoteException;

    boolean disableCellBroadcastRangeForSubscriber(int i, int i2, int i3, int i4) throws RemoteException;

    boolean enableCellBroadcastForSubscriber(int i, int i2, int i3) throws RemoteException;

    boolean enableCellBroadcastRangeForSubscriber(int i, int i2, int i3, int i4) throws RemoteException;

    List<SmsRawData> getAllMessagesFromIccEfForSubscriber(int i, String str) throws RemoteException;

    Bundle getCarrierConfigValuesForSubscriber(int i) throws RemoteException;

    String getImsSmsFormatForSubscriber(int i) throws RemoteException;

    int getPreferredSmsSubscription() throws RemoteException;

    int getPremiumSmsPermission(String str) throws RemoteException;

    int getPremiumSmsPermissionForSubscriber(int i, String str) throws RemoteException;

    int getSmsCapacityOnIccForSubscriber(int i) throws RemoteException;

    String getSmscAddressFromIccEfForSubscriber(int i, String str) throws RemoteException;

    void injectSmsPduForSubscriber(int i, byte[] bArr, String str, PendingIntent pendingIntent) throws RemoteException;

    boolean isImsSmsSupportedForSubscriber(int i) throws RemoteException;

    boolean isSMSPromptEnabled() throws RemoteException;

    boolean isSmsSimPickActivityNeeded(int i) throws RemoteException;

    boolean resetAllCellBroadcastRanges(int i) throws RemoteException;

    void sendDataForSubscriber(int i, String str, String str2, String str3, String str4, int i2, byte[] bArr, PendingIntent pendingIntent, PendingIntent pendingIntent2) throws RemoteException;

    void sendMultipartTextForSubscriber(int i, String str, String str2, String str3, String str4, List<String> list, List<PendingIntent> list2, List<PendingIntent> list3, boolean z, long j) throws RemoteException;

    void sendMultipartTextForSubscriberWithOptions(int i, String str, String str2, String str3, String str4, List<String> list, List<PendingIntent> list2, List<PendingIntent> list3, boolean z, int i2, boolean z2, int i3) throws RemoteException;

    void sendStoredMultipartText(int i, String str, String str2, Uri uri, String str3, List<PendingIntent> list, List<PendingIntent> list2) throws RemoteException;

    void sendStoredText(int i, String str, String str2, Uri uri, String str3, PendingIntent pendingIntent, PendingIntent pendingIntent2) throws RemoteException;

    void sendTextForSubscriber(int i, String str, String str2, String str3, String str4, String str5, PendingIntent pendingIntent, PendingIntent pendingIntent2, boolean z, long j) throws RemoteException;

    void sendTextForSubscriberWithOptions(int i, String str, String str2, String str3, String str4, String str5, PendingIntent pendingIntent, PendingIntent pendingIntent2, boolean z, int i2, boolean z2, int i3) throws RemoteException;

    void setPremiumSmsPermission(String str, int i) throws RemoteException;

    void setPremiumSmsPermissionForSubscriber(int i, String str, int i2) throws RemoteException;

    boolean setSmscAddressOnIccEfForSubscriber(String str, int i, String str2) throws RemoteException;

    void setStorageMonitorMemoryStatusOverride(int i, boolean z) throws RemoteException;

    boolean updateMessageOnIccEfForSubscriber(int i, String str, int i2, int i3, byte[] bArr) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ISms {
        @Override // com.android.internal.telephony.ISms
        public List<SmsRawData> getAllMessagesFromIccEfForSubscriber(int subId, String callingPkg) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.telephony.ISms
        public boolean updateMessageOnIccEfForSubscriber(int subId, String callingPkg, int messageIndex, int newStatus, byte[] pdu) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.telephony.ISms
        public boolean copyMessageToIccEfForSubscriber(int subId, String callingPkg, int status, byte[] pdu, byte[] smsc) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.telephony.ISms
        public void sendDataForSubscriber(int subId, String callingPkg, String callingattributionTag, String destAddr, String scAddr, int destPort, byte[] data, PendingIntent sentIntent, PendingIntent deliveryIntent) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ISms
        public void sendTextForSubscriber(int subId, String callingPkg, String callingAttributionTag, String destAddr, String scAddr, String text, PendingIntent sentIntent, PendingIntent deliveryIntent, boolean persistMessageForNonDefaultSmsApp, long messageId) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ISms
        public void sendTextForSubscriberWithOptions(int subId, String callingPkg, String callingAttributionTag, String destAddr, String scAddr, String text, PendingIntent sentIntent, PendingIntent deliveryIntent, boolean persistMessageForNonDefaultSmsApp, int priority, boolean expectMore, int validityPeriod) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ISms
        public void injectSmsPduForSubscriber(int subId, byte[] pdu, String format, PendingIntent receivedIntent) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ISms
        public void sendMultipartTextForSubscriber(int subId, String callingPkg, String callingAttributionTag, String destinationAddress, String scAddress, List<String> parts, List<PendingIntent> sentIntents, List<PendingIntent> deliveryIntents, boolean persistMessageForNonDefaultSmsApp, long messageId) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ISms
        public void sendMultipartTextForSubscriberWithOptions(int subId, String callingPkg, String callingAttributionTag, String destinationAddress, String scAddress, List<String> parts, List<PendingIntent> sentIntents, List<PendingIntent> deliveryIntents, boolean persistMessageForNonDefaultSmsApp, int priority, boolean expectMore, int validityPeriod) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ISms
        public boolean enableCellBroadcastForSubscriber(int subId, int messageIdentifier, int ranType) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.telephony.ISms
        public boolean disableCellBroadcastForSubscriber(int subId, int messageIdentifier, int ranType) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.telephony.ISms
        public boolean enableCellBroadcastRangeForSubscriber(int subId, int startMessageId, int endMessageId, int ranType) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.telephony.ISms
        public boolean disableCellBroadcastRangeForSubscriber(int subId, int startMessageId, int endMessageId, int ranType) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.telephony.ISms
        public int getPremiumSmsPermission(String packageName) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.telephony.ISms
        public int getPremiumSmsPermissionForSubscriber(int subId, String packageName) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.telephony.ISms
        public void setPremiumSmsPermission(String packageName, int permission) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ISms
        public void setPremiumSmsPermissionForSubscriber(int subId, String packageName, int permission) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ISms
        public boolean isImsSmsSupportedForSubscriber(int subId) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.telephony.ISms
        public boolean isSmsSimPickActivityNeeded(int subId) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.telephony.ISms
        public int getPreferredSmsSubscription() throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.telephony.ISms
        public String getImsSmsFormatForSubscriber(int subId) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.telephony.ISms
        public boolean isSMSPromptEnabled() throws RemoteException {
            return false;
        }

        @Override // com.android.internal.telephony.ISms
        public void sendStoredText(int subId, String callingPkg, String callingAttributionTag, Uri messageUri, String scAddress, PendingIntent sentIntent, PendingIntent deliveryIntent) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ISms
        public void sendStoredMultipartText(int subId, String callingPkg, String callingAttributeTag, Uri messageUri, String scAddress, List<PendingIntent> sentIntents, List<PendingIntent> deliveryIntents) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ISms
        public Bundle getCarrierConfigValuesForSubscriber(int subId) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.telephony.ISms
        public String createAppSpecificSmsToken(int subId, String callingPkg, PendingIntent intent) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.telephony.ISms
        public String createAppSpecificSmsTokenWithPackageInfo(int subId, String callingPkg, String prefixes, PendingIntent intent) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.telephony.ISms
        public void setStorageMonitorMemoryStatusOverride(int subId, boolean isStorageAvailable) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ISms
        public void clearStorageMonitorMemoryStatusOverride(int subId) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ISms
        public int checkSmsShortCodeDestination(int subId, String callingApk, String callingFeatureId, String destAddress, String countryIso) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.telephony.ISms
        public String getSmscAddressFromIccEfForSubscriber(int subId, String callingPackage) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.telephony.ISms
        public boolean setSmscAddressOnIccEfForSubscriber(String smsc, int subId, String callingPackage) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.telephony.ISms
        public int getSmsCapacityOnIccForSubscriber(int subId) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.telephony.ISms
        public boolean resetAllCellBroadcastRanges(int subId) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ISms {
        public static final String DESCRIPTOR = "com.android.internal.telephony.ISms";
        static final int TRANSACTION_checkSmsShortCodeDestination = 30;
        static final int TRANSACTION_clearStorageMonitorMemoryStatusOverride = 29;
        static final int TRANSACTION_copyMessageToIccEfForSubscriber = 3;
        static final int TRANSACTION_createAppSpecificSmsToken = 26;
        static final int TRANSACTION_createAppSpecificSmsTokenWithPackageInfo = 27;
        static final int TRANSACTION_disableCellBroadcastForSubscriber = 11;
        static final int TRANSACTION_disableCellBroadcastRangeForSubscriber = 13;
        static final int TRANSACTION_enableCellBroadcastForSubscriber = 10;
        static final int TRANSACTION_enableCellBroadcastRangeForSubscriber = 12;
        static final int TRANSACTION_getAllMessagesFromIccEfForSubscriber = 1;
        static final int TRANSACTION_getCarrierConfigValuesForSubscriber = 25;
        static final int TRANSACTION_getImsSmsFormatForSubscriber = 21;
        static final int TRANSACTION_getPreferredSmsSubscription = 20;
        static final int TRANSACTION_getPremiumSmsPermission = 14;
        static final int TRANSACTION_getPremiumSmsPermissionForSubscriber = 15;
        static final int TRANSACTION_getSmsCapacityOnIccForSubscriber = 33;
        static final int TRANSACTION_getSmscAddressFromIccEfForSubscriber = 31;
        static final int TRANSACTION_injectSmsPduForSubscriber = 7;
        static final int TRANSACTION_isImsSmsSupportedForSubscriber = 18;
        static final int TRANSACTION_isSMSPromptEnabled = 22;
        static final int TRANSACTION_isSmsSimPickActivityNeeded = 19;
        static final int TRANSACTION_resetAllCellBroadcastRanges = 34;
        static final int TRANSACTION_sendDataForSubscriber = 4;
        static final int TRANSACTION_sendMultipartTextForSubscriber = 8;
        static final int TRANSACTION_sendMultipartTextForSubscriberWithOptions = 9;
        static final int TRANSACTION_sendStoredMultipartText = 24;
        static final int TRANSACTION_sendStoredText = 23;
        static final int TRANSACTION_sendTextForSubscriber = 5;
        static final int TRANSACTION_sendTextForSubscriberWithOptions = 6;
        static final int TRANSACTION_setPremiumSmsPermission = 16;
        static final int TRANSACTION_setPremiumSmsPermissionForSubscriber = 17;
        static final int TRANSACTION_setSmscAddressOnIccEfForSubscriber = 32;
        static final int TRANSACTION_setStorageMonitorMemoryStatusOverride = 28;
        static final int TRANSACTION_updateMessageOnIccEfForSubscriber = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ISms asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ISms)) {
                return (ISms) iin;
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
                    return "getAllMessagesFromIccEfForSubscriber";
                case 2:
                    return "updateMessageOnIccEfForSubscriber";
                case 3:
                    return "copyMessageToIccEfForSubscriber";
                case 4:
                    return "sendDataForSubscriber";
                case 5:
                    return "sendTextForSubscriber";
                case 6:
                    return "sendTextForSubscriberWithOptions";
                case 7:
                    return "injectSmsPduForSubscriber";
                case 8:
                    return "sendMultipartTextForSubscriber";
                case 9:
                    return "sendMultipartTextForSubscriberWithOptions";
                case 10:
                    return "enableCellBroadcastForSubscriber";
                case 11:
                    return "disableCellBroadcastForSubscriber";
                case 12:
                    return "enableCellBroadcastRangeForSubscriber";
                case 13:
                    return "disableCellBroadcastRangeForSubscriber";
                case 14:
                    return "getPremiumSmsPermission";
                case 15:
                    return "getPremiumSmsPermissionForSubscriber";
                case 16:
                    return "setPremiumSmsPermission";
                case 17:
                    return "setPremiumSmsPermissionForSubscriber";
                case 18:
                    return "isImsSmsSupportedForSubscriber";
                case 19:
                    return "isSmsSimPickActivityNeeded";
                case 20:
                    return "getPreferredSmsSubscription";
                case 21:
                    return "getImsSmsFormatForSubscriber";
                case 22:
                    return "isSMSPromptEnabled";
                case 23:
                    return "sendStoredText";
                case 24:
                    return "sendStoredMultipartText";
                case 25:
                    return "getCarrierConfigValuesForSubscriber";
                case 26:
                    return "createAppSpecificSmsToken";
                case 27:
                    return "createAppSpecificSmsTokenWithPackageInfo";
                case 28:
                    return "setStorageMonitorMemoryStatusOverride";
                case 29:
                    return "clearStorageMonitorMemoryStatusOverride";
                case 30:
                    return "checkSmsShortCodeDestination";
                case 31:
                    return "getSmscAddressFromIccEfForSubscriber";
                case 32:
                    return "setSmscAddressOnIccEfForSubscriber";
                case 33:
                    return "getSmsCapacityOnIccForSubscriber";
                case 34:
                    return "resetAllCellBroadcastRanges";
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
                            String _arg1 = data.readString();
                            data.enforceNoDataAvail();
                            List<SmsRawData> _result = getAllMessagesFromIccEfForSubscriber(_arg0, _arg1);
                            reply.writeNoException();
                            reply.writeTypedList(_result, 1);
                            return true;
                        case 2:
                            int _arg02 = data.readInt();
                            String _arg12 = data.readString();
                            int _arg2 = data.readInt();
                            int _arg3 = data.readInt();
                            byte[] _arg4 = data.createByteArray();
                            data.enforceNoDataAvail();
                            boolean _result2 = updateMessageOnIccEfForSubscriber(_arg02, _arg12, _arg2, _arg3, _arg4);
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            return true;
                        case 3:
                            int _arg03 = data.readInt();
                            String _arg13 = data.readString();
                            int _arg22 = data.readInt();
                            byte[] _arg32 = data.createByteArray();
                            byte[] _arg42 = data.createByteArray();
                            data.enforceNoDataAvail();
                            boolean _result3 = copyMessageToIccEfForSubscriber(_arg03, _arg13, _arg22, _arg32, _arg42);
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            return true;
                        case 4:
                            int _arg04 = data.readInt();
                            String _arg14 = data.readString();
                            String _arg23 = data.readString();
                            String _arg33 = data.readString();
                            String _arg43 = data.readString();
                            int _arg5 = data.readInt();
                            byte[] _arg6 = data.createByteArray();
                            PendingIntent _arg7 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            PendingIntent _arg8 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            data.enforceNoDataAvail();
                            sendDataForSubscriber(_arg04, _arg14, _arg23, _arg33, _arg43, _arg5, _arg6, _arg7, _arg8);
                            reply.writeNoException();
                            return true;
                        case 5:
                            int _arg05 = data.readInt();
                            String _arg15 = data.readString();
                            String _arg24 = data.readString();
                            String _arg34 = data.readString();
                            String _arg44 = data.readString();
                            String _arg52 = data.readString();
                            PendingIntent _arg62 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            PendingIntent _arg72 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            boolean _arg82 = data.readBoolean();
                            long _arg9 = data.readLong();
                            data.enforceNoDataAvail();
                            sendTextForSubscriber(_arg05, _arg15, _arg24, _arg34, _arg44, _arg52, _arg62, _arg72, _arg82, _arg9);
                            reply.writeNoException();
                            return true;
                        case 6:
                            int _arg06 = data.readInt();
                            String _arg16 = data.readString();
                            String _arg25 = data.readString();
                            String _arg35 = data.readString();
                            String _arg45 = data.readString();
                            String _arg53 = data.readString();
                            PendingIntent _arg63 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            PendingIntent _arg73 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            boolean _arg83 = data.readBoolean();
                            int _arg92 = data.readInt();
                            boolean _arg10 = data.readBoolean();
                            int _arg11 = data.readInt();
                            data.enforceNoDataAvail();
                            sendTextForSubscriberWithOptions(_arg06, _arg16, _arg25, _arg35, _arg45, _arg53, _arg63, _arg73, _arg83, _arg92, _arg10, _arg11);
                            reply.writeNoException();
                            return true;
                        case 7:
                            int _arg07 = data.readInt();
                            byte[] _arg17 = data.createByteArray();
                            String _arg26 = data.readString();
                            PendingIntent _arg36 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            data.enforceNoDataAvail();
                            injectSmsPduForSubscriber(_arg07, _arg17, _arg26, _arg36);
                            reply.writeNoException();
                            return true;
                        case 8:
                            int _arg08 = data.readInt();
                            String _arg18 = data.readString();
                            String _arg27 = data.readString();
                            String _arg37 = data.readString();
                            String _arg46 = data.readString();
                            List<String> _arg54 = data.createStringArrayList();
                            List<PendingIntent> _arg64 = data.createTypedArrayList(PendingIntent.CREATOR);
                            List<PendingIntent> _arg74 = data.createTypedArrayList(PendingIntent.CREATOR);
                            boolean _arg84 = data.readBoolean();
                            long _arg93 = data.readLong();
                            data.enforceNoDataAvail();
                            sendMultipartTextForSubscriber(_arg08, _arg18, _arg27, _arg37, _arg46, _arg54, _arg64, _arg74, _arg84, _arg93);
                            reply.writeNoException();
                            return true;
                        case 9:
                            int _arg09 = data.readInt();
                            String _arg19 = data.readString();
                            String _arg28 = data.readString();
                            String _arg38 = data.readString();
                            String _arg47 = data.readString();
                            List<String> _arg55 = data.createStringArrayList();
                            List<PendingIntent> _arg65 = data.createTypedArrayList(PendingIntent.CREATOR);
                            List<PendingIntent> _arg75 = data.createTypedArrayList(PendingIntent.CREATOR);
                            boolean _arg85 = data.readBoolean();
                            int _arg94 = data.readInt();
                            boolean _arg102 = data.readBoolean();
                            int _arg112 = data.readInt();
                            data.enforceNoDataAvail();
                            sendMultipartTextForSubscriberWithOptions(_arg09, _arg19, _arg28, _arg38, _arg47, _arg55, _arg65, _arg75, _arg85, _arg94, _arg102, _arg112);
                            reply.writeNoException();
                            return true;
                        case 10:
                            int _arg010 = data.readInt();
                            int _arg110 = data.readInt();
                            int _arg29 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result4 = enableCellBroadcastForSubscriber(_arg010, _arg110, _arg29);
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            return true;
                        case 11:
                            int _arg011 = data.readInt();
                            int _arg111 = data.readInt();
                            int _arg210 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result5 = disableCellBroadcastForSubscriber(_arg011, _arg111, _arg210);
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            return true;
                        case 12:
                            int _arg012 = data.readInt();
                            int _arg113 = data.readInt();
                            int _arg211 = data.readInt();
                            int _arg39 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result6 = enableCellBroadcastRangeForSubscriber(_arg012, _arg113, _arg211, _arg39);
                            reply.writeNoException();
                            reply.writeBoolean(_result6);
                            return true;
                        case 13:
                            int _arg013 = data.readInt();
                            int _arg114 = data.readInt();
                            int _arg212 = data.readInt();
                            int _arg310 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result7 = disableCellBroadcastRangeForSubscriber(_arg013, _arg114, _arg212, _arg310);
                            reply.writeNoException();
                            reply.writeBoolean(_result7);
                            return true;
                        case 14:
                            String _arg014 = data.readString();
                            data.enforceNoDataAvail();
                            int _result8 = getPremiumSmsPermission(_arg014);
                            reply.writeNoException();
                            reply.writeInt(_result8);
                            return true;
                        case 15:
                            int _arg015 = data.readInt();
                            String _arg115 = data.readString();
                            data.enforceNoDataAvail();
                            int _result9 = getPremiumSmsPermissionForSubscriber(_arg015, _arg115);
                            reply.writeNoException();
                            reply.writeInt(_result9);
                            return true;
                        case 16:
                            String _arg016 = data.readString();
                            int _arg116 = data.readInt();
                            data.enforceNoDataAvail();
                            setPremiumSmsPermission(_arg016, _arg116);
                            reply.writeNoException();
                            return true;
                        case 17:
                            int _arg017 = data.readInt();
                            String _arg117 = data.readString();
                            int _arg213 = data.readInt();
                            data.enforceNoDataAvail();
                            setPremiumSmsPermissionForSubscriber(_arg017, _arg117, _arg213);
                            reply.writeNoException();
                            return true;
                        case 18:
                            int _arg018 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result10 = isImsSmsSupportedForSubscriber(_arg018);
                            reply.writeNoException();
                            reply.writeBoolean(_result10);
                            return true;
                        case 19:
                            int _arg019 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result11 = isSmsSimPickActivityNeeded(_arg019);
                            reply.writeNoException();
                            reply.writeBoolean(_result11);
                            return true;
                        case 20:
                            int _result12 = getPreferredSmsSubscription();
                            reply.writeNoException();
                            reply.writeInt(_result12);
                            return true;
                        case 21:
                            int _arg020 = data.readInt();
                            data.enforceNoDataAvail();
                            String _result13 = getImsSmsFormatForSubscriber(_arg020);
                            reply.writeNoException();
                            reply.writeString(_result13);
                            return true;
                        case 22:
                            boolean _result14 = isSMSPromptEnabled();
                            reply.writeNoException();
                            reply.writeBoolean(_result14);
                            return true;
                        case 23:
                            int _arg021 = data.readInt();
                            String _arg118 = data.readString();
                            String _arg214 = data.readString();
                            Uri _arg311 = (Uri) data.readTypedObject(Uri.CREATOR);
                            String _arg48 = data.readString();
                            PendingIntent _arg56 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            PendingIntent _arg66 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            data.enforceNoDataAvail();
                            sendStoredText(_arg021, _arg118, _arg214, _arg311, _arg48, _arg56, _arg66);
                            reply.writeNoException();
                            return true;
                        case 24:
                            int _arg022 = data.readInt();
                            String _arg119 = data.readString();
                            String _arg215 = data.readString();
                            Uri _arg312 = (Uri) data.readTypedObject(Uri.CREATOR);
                            String _arg49 = data.readString();
                            List<PendingIntent> _arg57 = data.createTypedArrayList(PendingIntent.CREATOR);
                            List<PendingIntent> _arg67 = data.createTypedArrayList(PendingIntent.CREATOR);
                            data.enforceNoDataAvail();
                            sendStoredMultipartText(_arg022, _arg119, _arg215, _arg312, _arg49, _arg57, _arg67);
                            reply.writeNoException();
                            return true;
                        case 25:
                            int _arg023 = data.readInt();
                            data.enforceNoDataAvail();
                            Bundle _result15 = getCarrierConfigValuesForSubscriber(_arg023);
                            reply.writeNoException();
                            reply.writeTypedObject(_result15, 1);
                            return true;
                        case 26:
                            int _arg024 = data.readInt();
                            String _arg120 = data.readString();
                            PendingIntent _arg216 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            data.enforceNoDataAvail();
                            String _result16 = createAppSpecificSmsToken(_arg024, _arg120, _arg216);
                            reply.writeNoException();
                            reply.writeString(_result16);
                            return true;
                        case 27:
                            int _arg025 = data.readInt();
                            String _arg121 = data.readString();
                            String _arg217 = data.readString();
                            PendingIntent _arg313 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            data.enforceNoDataAvail();
                            String _result17 = createAppSpecificSmsTokenWithPackageInfo(_arg025, _arg121, _arg217, _arg313);
                            reply.writeNoException();
                            reply.writeString(_result17);
                            return true;
                        case 28:
                            int _arg026 = data.readInt();
                            boolean _arg122 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setStorageMonitorMemoryStatusOverride(_arg026, _arg122);
                            reply.writeNoException();
                            return true;
                        case 29:
                            int _arg027 = data.readInt();
                            data.enforceNoDataAvail();
                            clearStorageMonitorMemoryStatusOverride(_arg027);
                            reply.writeNoException();
                            return true;
                        case 30:
                            int _arg028 = data.readInt();
                            String _arg123 = data.readString();
                            String _arg218 = data.readString();
                            String _arg314 = data.readString();
                            String _arg410 = data.readString();
                            data.enforceNoDataAvail();
                            int _result18 = checkSmsShortCodeDestination(_arg028, _arg123, _arg218, _arg314, _arg410);
                            reply.writeNoException();
                            reply.writeInt(_result18);
                            return true;
                        case 31:
                            int _arg029 = data.readInt();
                            String _arg124 = data.readString();
                            data.enforceNoDataAvail();
                            String _result19 = getSmscAddressFromIccEfForSubscriber(_arg029, _arg124);
                            reply.writeNoException();
                            reply.writeString(_result19);
                            return true;
                        case 32:
                            String _arg030 = data.readString();
                            int _arg125 = data.readInt();
                            String _arg219 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result20 = setSmscAddressOnIccEfForSubscriber(_arg030, _arg125, _arg219);
                            reply.writeNoException();
                            reply.writeBoolean(_result20);
                            return true;
                        case 33:
                            int _arg031 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result21 = getSmsCapacityOnIccForSubscriber(_arg031);
                            reply.writeNoException();
                            reply.writeInt(_result21);
                            return true;
                        case 34:
                            int _arg032 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result22 = resetAllCellBroadcastRanges(_arg032);
                            reply.writeNoException();
                            reply.writeBoolean(_result22);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class Proxy implements ISms {
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

            @Override // com.android.internal.telephony.ISms
            public List<SmsRawData> getAllMessagesFromIccEfForSubscriber(int subId, String callingPkg) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeString(callingPkg);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    List<SmsRawData> _result = _reply.createTypedArrayList(SmsRawData.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISms
            public boolean updateMessageOnIccEfForSubscriber(int subId, String callingPkg, int messageIndex, int newStatus, byte[] pdu) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeString(callingPkg);
                    _data.writeInt(messageIndex);
                    _data.writeInt(newStatus);
                    _data.writeByteArray(pdu);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISms
            public boolean copyMessageToIccEfForSubscriber(int subId, String callingPkg, int status, byte[] pdu, byte[] smsc) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeString(callingPkg);
                    _data.writeInt(status);
                    _data.writeByteArray(pdu);
                    _data.writeByteArray(smsc);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISms
            public void sendDataForSubscriber(int subId, String callingPkg, String callingattributionTag, String destAddr, String scAddr, int destPort, byte[] data, PendingIntent sentIntent, PendingIntent deliveryIntent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeString(callingPkg);
                    _data.writeString(callingattributionTag);
                    _data.writeString(destAddr);
                    _data.writeString(scAddr);
                    _data.writeInt(destPort);
                    _data.writeByteArray(data);
                    _data.writeTypedObject(sentIntent, 0);
                    _data.writeTypedObject(deliveryIntent, 0);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISms
            public void sendTextForSubscriber(int subId, String callingPkg, String callingAttributionTag, String destAddr, String scAddr, String text, PendingIntent sentIntent, PendingIntent deliveryIntent, boolean persistMessageForNonDefaultSmsApp, long messageId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    try {
                        _data.writeString(callingPkg);
                        try {
                            _data.writeString(callingAttributionTag);
                            try {
                                _data.writeString(destAddr);
                            } catch (Throwable th) {
                                th = th;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeString(scAddr);
                        try {
                            _data.writeString(text);
                            try {
                                _data.writeTypedObject(sentIntent, 0);
                                try {
                                    _data.writeTypedObject(deliveryIntent, 0);
                                } catch (Throwable th4) {
                                    th = th4;
                                    _reply.recycle();
                                    _data.recycle();
                                    throw th;
                                }
                            } catch (Throwable th5) {
                                th = th5;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th6) {
                            th = th6;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                        try {
                            _data.writeBoolean(persistMessageForNonDefaultSmsApp);
                            try {
                                _data.writeLong(messageId);
                                try {
                                    this.mRemote.transact(5, _data, _reply, 0);
                                    _reply.readException();
                                    _reply.recycle();
                                    _data.recycle();
                                } catch (Throwable th7) {
                                    th = th7;
                                    _reply.recycle();
                                    _data.recycle();
                                    throw th;
                                }
                            } catch (Throwable th8) {
                                th = th8;
                            }
                        } catch (Throwable th9) {
                            th = th9;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th10) {
                        th = th10;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th11) {
                    th = th11;
                }
            }

            @Override // com.android.internal.telephony.ISms
            public void sendTextForSubscriberWithOptions(int subId, String callingPkg, String callingAttributionTag, String destAddr, String scAddr, String text, PendingIntent sentIntent, PendingIntent deliveryIntent, boolean persistMessageForNonDefaultSmsApp, int priority, boolean expectMore, int validityPeriod) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeString(callingPkg);
                    try {
                        _data.writeString(callingAttributionTag);
                        try {
                            _data.writeString(destAddr);
                            try {
                                _data.writeString(scAddr);
                            } catch (Throwable th) {
                                th = th;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeString(text);
                        try {
                            _data.writeTypedObject(sentIntent, 0);
                            try {
                                _data.writeTypedObject(deliveryIntent, 0);
                                try {
                                    _data.writeBoolean(persistMessageForNonDefaultSmsApp);
                                } catch (Throwable th4) {
                                    th = th4;
                                    _reply.recycle();
                                    _data.recycle();
                                    throw th;
                                }
                            } catch (Throwable th5) {
                                th = th5;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th6) {
                            th = th6;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                        try {
                            _data.writeInt(priority);
                            try {
                                _data.writeBoolean(expectMore);
                                try {
                                    _data.writeInt(validityPeriod);
                                    try {
                                        this.mRemote.transact(6, _data, _reply, 0);
                                        _reply.readException();
                                        _reply.recycle();
                                        _data.recycle();
                                    } catch (Throwable th7) {
                                        th = th7;
                                        _reply.recycle();
                                        _data.recycle();
                                        throw th;
                                    }
                                } catch (Throwable th8) {
                                    th = th8;
                                }
                            } catch (Throwable th9) {
                                th = th9;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th10) {
                            th = th10;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th11) {
                        th = th11;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th12) {
                    th = th12;
                }
            }

            @Override // com.android.internal.telephony.ISms
            public void injectSmsPduForSubscriber(int subId, byte[] pdu, String format, PendingIntent receivedIntent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeByteArray(pdu);
                    _data.writeString(format);
                    _data.writeTypedObject(receivedIntent, 0);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISms
            public void sendMultipartTextForSubscriber(int subId, String callingPkg, String callingAttributionTag, String destinationAddress, String scAddress, List<String> parts, List<PendingIntent> sentIntents, List<PendingIntent> deliveryIntents, boolean persistMessageForNonDefaultSmsApp, long messageId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    try {
                        _data.writeString(callingPkg);
                        try {
                            _data.writeString(callingAttributionTag);
                            try {
                                _data.writeString(destinationAddress);
                            } catch (Throwable th) {
                                th = th;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeString(scAddress);
                        try {
                            _data.writeStringList(parts);
                            try {
                                _data.writeTypedList(sentIntents, 0);
                                try {
                                    _data.writeTypedList(deliveryIntents, 0);
                                } catch (Throwable th4) {
                                    th = th4;
                                    _reply.recycle();
                                    _data.recycle();
                                    throw th;
                                }
                            } catch (Throwable th5) {
                                th = th5;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th6) {
                            th = th6;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                        try {
                            _data.writeBoolean(persistMessageForNonDefaultSmsApp);
                            try {
                                _data.writeLong(messageId);
                                try {
                                    this.mRemote.transact(8, _data, _reply, 0);
                                    _reply.readException();
                                    _reply.recycle();
                                    _data.recycle();
                                } catch (Throwable th7) {
                                    th = th7;
                                    _reply.recycle();
                                    _data.recycle();
                                    throw th;
                                }
                            } catch (Throwable th8) {
                                th = th8;
                            }
                        } catch (Throwable th9) {
                            th = th9;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th10) {
                        th = th10;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th11) {
                    th = th11;
                }
            }

            @Override // com.android.internal.telephony.ISms
            public void sendMultipartTextForSubscriberWithOptions(int subId, String callingPkg, String callingAttributionTag, String destinationAddress, String scAddress, List<String> parts, List<PendingIntent> sentIntents, List<PendingIntent> deliveryIntents, boolean persistMessageForNonDefaultSmsApp, int priority, boolean expectMore, int validityPeriod) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeString(callingPkg);
                    try {
                        _data.writeString(callingAttributionTag);
                        try {
                            _data.writeString(destinationAddress);
                            try {
                                _data.writeString(scAddress);
                            } catch (Throwable th) {
                                th = th;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeStringList(parts);
                        try {
                            _data.writeTypedList(sentIntents, 0);
                            try {
                                _data.writeTypedList(deliveryIntents, 0);
                                try {
                                    _data.writeBoolean(persistMessageForNonDefaultSmsApp);
                                } catch (Throwable th4) {
                                    th = th4;
                                    _reply.recycle();
                                    _data.recycle();
                                    throw th;
                                }
                            } catch (Throwable th5) {
                                th = th5;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th6) {
                            th = th6;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                        try {
                            _data.writeInt(priority);
                            try {
                                _data.writeBoolean(expectMore);
                                try {
                                    _data.writeInt(validityPeriod);
                                    try {
                                        this.mRemote.transact(9, _data, _reply, 0);
                                        _reply.readException();
                                        _reply.recycle();
                                        _data.recycle();
                                    } catch (Throwable th7) {
                                        th = th7;
                                        _reply.recycle();
                                        _data.recycle();
                                        throw th;
                                    }
                                } catch (Throwable th8) {
                                    th = th8;
                                }
                            } catch (Throwable th9) {
                                th = th9;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th10) {
                            th = th10;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th11) {
                        th = th11;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th12) {
                    th = th12;
                }
            }

            @Override // com.android.internal.telephony.ISms
            public boolean enableCellBroadcastForSubscriber(int subId, int messageIdentifier, int ranType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeInt(messageIdentifier);
                    _data.writeInt(ranType);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISms
            public boolean disableCellBroadcastForSubscriber(int subId, int messageIdentifier, int ranType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeInt(messageIdentifier);
                    _data.writeInt(ranType);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISms
            public boolean enableCellBroadcastRangeForSubscriber(int subId, int startMessageId, int endMessageId, int ranType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeInt(startMessageId);
                    _data.writeInt(endMessageId);
                    _data.writeInt(ranType);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISms
            public boolean disableCellBroadcastRangeForSubscriber(int subId, int startMessageId, int endMessageId, int ranType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeInt(startMessageId);
                    _data.writeInt(endMessageId);
                    _data.writeInt(ranType);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISms
            public int getPremiumSmsPermission(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISms
            public int getPremiumSmsPermissionForSubscriber(int subId, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeString(packageName);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISms
            public void setPremiumSmsPermission(String packageName, int permission) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(permission);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISms
            public void setPremiumSmsPermissionForSubscriber(int subId, String packageName, int permission) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeString(packageName);
                    _data.writeInt(permission);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISms
            public boolean isImsSmsSupportedForSubscriber(int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISms
            public boolean isSmsSimPickActivityNeeded(int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISms
            public int getPreferredSmsSubscription() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISms
            public String getImsSmsFormatForSubscriber(int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISms
            public boolean isSMSPromptEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISms
            public void sendStoredText(int subId, String callingPkg, String callingAttributionTag, Uri messageUri, String scAddress, PendingIntent sentIntent, PendingIntent deliveryIntent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeString(callingPkg);
                    _data.writeString(callingAttributionTag);
                    _data.writeTypedObject(messageUri, 0);
                    _data.writeString(scAddress);
                    _data.writeTypedObject(sentIntent, 0);
                    _data.writeTypedObject(deliveryIntent, 0);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISms
            public void sendStoredMultipartText(int subId, String callingPkg, String callingAttributeTag, Uri messageUri, String scAddress, List<PendingIntent> sentIntents, List<PendingIntent> deliveryIntents) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeString(callingPkg);
                    _data.writeString(callingAttributeTag);
                    _data.writeTypedObject(messageUri, 0);
                    _data.writeString(scAddress);
                    _data.writeTypedList(sentIntents, 0);
                    _data.writeTypedList(deliveryIntents, 0);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISms
            public Bundle getCarrierConfigValuesForSubscriber(int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                    Bundle _result = (Bundle) _reply.readTypedObject(Bundle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISms
            public String createAppSpecificSmsToken(int subId, String callingPkg, PendingIntent intent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeString(callingPkg);
                    _data.writeTypedObject(intent, 0);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISms
            public String createAppSpecificSmsTokenWithPackageInfo(int subId, String callingPkg, String prefixes, PendingIntent intent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeString(callingPkg);
                    _data.writeString(prefixes);
                    _data.writeTypedObject(intent, 0);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISms
            public void setStorageMonitorMemoryStatusOverride(int subId, boolean isStorageAvailable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeBoolean(isStorageAvailable);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISms
            public void clearStorageMonitorMemoryStatusOverride(int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISms
            public int checkSmsShortCodeDestination(int subId, String callingApk, String callingFeatureId, String destAddress, String countryIso) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeString(callingApk);
                    _data.writeString(callingFeatureId);
                    _data.writeString(destAddress);
                    _data.writeString(countryIso);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISms
            public String getSmscAddressFromIccEfForSubscriber(int subId, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISms
            public boolean setSmscAddressOnIccEfForSubscriber(String smsc, int subId, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(smsc);
                    _data.writeInt(subId);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISms
            public int getSmsCapacityOnIccForSubscriber(int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISms
            public boolean resetAllCellBroadcastRanges(int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    this.mRemote.transact(34, _data, _reply, 0);
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
            return 33;
        }
    }
}
