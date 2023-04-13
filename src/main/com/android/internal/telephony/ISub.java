package com.android.internal.telephony;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.ParcelUuid;
import android.p008os.RemoteException;
import android.p008os.UserHandle;
import android.telephony.SubscriptionInfo;
import com.android.internal.telephony.ISetOpportunisticDataCallback;
import java.util.List;
/* loaded from: classes3.dex */
public interface ISub extends IInterface {
    int addSubInfo(String str, String str2, int i, int i2) throws RemoteException;

    void addSubscriptionsIntoGroup(int[] iArr, ParcelUuid parcelUuid, String str) throws RemoteException;

    boolean canDisablePhysicalSubscription() throws RemoteException;

    ParcelUuid createSubscriptionGroup(int[] iArr, String str) throws RemoteException;

    List<SubscriptionInfo> getAccessibleSubscriptionInfoList(String str) throws RemoteException;

    int getActiveDataSubscriptionId() throws RemoteException;

    int[] getActiveSubIdList(boolean z) throws RemoteException;

    int getActiveSubInfoCount(String str, String str2) throws RemoteException;

    int getActiveSubInfoCountMax() throws RemoteException;

    SubscriptionInfo getActiveSubscriptionInfo(int i, String str, String str2) throws RemoteException;

    SubscriptionInfo getActiveSubscriptionInfoForIccId(String str, String str2, String str3) throws RemoteException;

    SubscriptionInfo getActiveSubscriptionInfoForSimSlotIndex(int i, String str, String str2) throws RemoteException;

    List<SubscriptionInfo> getActiveSubscriptionInfoList(String str, String str2) throws RemoteException;

    List<SubscriptionInfo> getAllSubInfoList(String str, String str2) throws RemoteException;

    List<SubscriptionInfo> getAvailableSubscriptionInfoList(String str, String str2) throws RemoteException;

    int getDefaultDataSubId() throws RemoteException;

    int getDefaultSmsSubId() throws RemoteException;

    int getDefaultSubId() throws RemoteException;

    int getDefaultVoiceSubId() throws RemoteException;

    int getEnabledSubscriptionId(int i) throws RemoteException;

    List<SubscriptionInfo> getOpportunisticSubscriptions(String str, String str2) throws RemoteException;

    int getPhoneId(int i) throws RemoteException;

    String getPhoneNumber(int i, int i2, String str, String str2) throws RemoteException;

    String getPhoneNumberFromFirstAvailableSource(int i, String str, String str2) throws RemoteException;

    int getPreferredDataSubscriptionId() throws RemoteException;

    int getSlotIndex(int i) throws RemoteException;

    int getSubId(int i) throws RemoteException;

    List<SubscriptionInfo> getSubscriptionInfoListAssociatedWithUser(UserHandle userHandle) throws RemoteException;

    String getSubscriptionProperty(int i, String str, String str2, String str3) throws RemoteException;

    UserHandle getSubscriptionUserHandle(int i) throws RemoteException;

    List<SubscriptionInfo> getSubscriptionsInGroup(ParcelUuid parcelUuid, String str, String str2) throws RemoteException;

    boolean isActiveSubId(int i, String str, String str2) throws RemoteException;

    boolean isSubscriptionAssociatedWithUser(int i, UserHandle userHandle) throws RemoteException;

    boolean isSubscriptionEnabled(int i) throws RemoteException;

    boolean isSubscriptionManagerServiceEnabled() throws RemoteException;

    int removeSubInfo(String str, int i) throws RemoteException;

    void removeSubscriptionsFromGroup(int[] iArr, ParcelUuid parcelUuid, String str) throws RemoteException;

    void requestEmbeddedSubscriptionInfoListRefresh(int i) throws RemoteException;

    void restoreAllSimSpecificSettingsFromBackup(byte[] bArr) throws RemoteException;

    int setDataRoaming(int i, int i2) throws RemoteException;

    void setDefaultDataSubId(int i) throws RemoteException;

    void setDefaultSmsSubId(int i) throws RemoteException;

    void setDefaultVoiceSubId(int i) throws RemoteException;

    int setDeviceToDeviceStatusSharing(int i, int i2) throws RemoteException;

    int setDeviceToDeviceStatusSharingContacts(String str, int i) throws RemoteException;

    int setDisplayNameUsingSrc(String str, int i, int i2) throws RemoteException;

    int setDisplayNumber(String str, int i) throws RemoteException;

    int setIconTint(int i, int i2) throws RemoteException;

    int setOpportunistic(boolean z, int i, String str) throws RemoteException;

    void setPhoneNumber(int i, int i2, String str, String str2, String str3) throws RemoteException;

    void setPreferredDataSubscriptionId(int i, boolean z, ISetOpportunisticDataCallback iSetOpportunisticDataCallback) throws RemoteException;

    boolean setSubscriptionEnabled(boolean z, int i) throws RemoteException;

    int setSubscriptionProperty(int i, String str, String str2) throws RemoteException;

    int setSubscriptionUserHandle(UserHandle userHandle, int i) throws RemoteException;

    int setUiccApplicationsEnabled(boolean z, int i) throws RemoteException;

    int setUsageSetting(int i, int i2, String str) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ISub {
        @Override // com.android.internal.telephony.ISub
        public List<SubscriptionInfo> getAllSubInfoList(String callingPackage, String callingFeatureId) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.telephony.ISub
        public SubscriptionInfo getActiveSubscriptionInfo(int subId, String callingPackage, String callingFeatureId) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.telephony.ISub
        public SubscriptionInfo getActiveSubscriptionInfoForIccId(String iccId, String callingPackage, String callingFeatureId) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.telephony.ISub
        public SubscriptionInfo getActiveSubscriptionInfoForSimSlotIndex(int slotIndex, String callingPackage, String callingFeatureId) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.telephony.ISub
        public List<SubscriptionInfo> getActiveSubscriptionInfoList(String callingPackage, String callingFeatureId) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.telephony.ISub
        public int getActiveSubInfoCount(String callingPackage, String callingFeatureId) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.telephony.ISub
        public int getActiveSubInfoCountMax() throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.telephony.ISub
        public List<SubscriptionInfo> getAvailableSubscriptionInfoList(String callingPackage, String callingFeatureId) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.telephony.ISub
        public List<SubscriptionInfo> getAccessibleSubscriptionInfoList(String callingPackage) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.telephony.ISub
        public void requestEmbeddedSubscriptionInfoListRefresh(int cardId) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ISub
        public int addSubInfo(String uniqueId, String displayName, int slotIndex, int subscriptionType) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.telephony.ISub
        public int removeSubInfo(String uniqueId, int subscriptionType) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.telephony.ISub
        public int setIconTint(int subId, int tint) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.telephony.ISub
        public int setDisplayNameUsingSrc(String displayName, int subId, int nameSource) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.telephony.ISub
        public int setDisplayNumber(String number, int subId) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.telephony.ISub
        public int setDataRoaming(int roaming, int subId) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.telephony.ISub
        public int setOpportunistic(boolean opportunistic, int subId, String callingPackage) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.telephony.ISub
        public ParcelUuid createSubscriptionGroup(int[] subIdList, String callingPackage) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.telephony.ISub
        public void setPreferredDataSubscriptionId(int subId, boolean needValidation, ISetOpportunisticDataCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ISub
        public int getPreferredDataSubscriptionId() throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.telephony.ISub
        public List<SubscriptionInfo> getOpportunisticSubscriptions(String callingPackage, String callingFeatureId) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.telephony.ISub
        public void removeSubscriptionsFromGroup(int[] subIdList, ParcelUuid groupUuid, String callingPackage) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ISub
        public void addSubscriptionsIntoGroup(int[] subIdList, ParcelUuid groupUuid, String callingPackage) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ISub
        public List<SubscriptionInfo> getSubscriptionsInGroup(ParcelUuid groupUuid, String callingPackage, String callingFeatureId) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.telephony.ISub
        public int getSlotIndex(int subId) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.telephony.ISub
        public int getSubId(int slotIndex) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.telephony.ISub
        public int getDefaultSubId() throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.telephony.ISub
        public int getPhoneId(int subId) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.telephony.ISub
        public int getDefaultDataSubId() throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.telephony.ISub
        public void setDefaultDataSubId(int subId) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ISub
        public int getDefaultVoiceSubId() throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.telephony.ISub
        public void setDefaultVoiceSubId(int subId) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ISub
        public int getDefaultSmsSubId() throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.telephony.ISub
        public void setDefaultSmsSubId(int subId) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ISub
        public int[] getActiveSubIdList(boolean visibleOnly) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.telephony.ISub
        public int setSubscriptionProperty(int subId, String propKey, String propValue) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.telephony.ISub
        public String getSubscriptionProperty(int subId, String propKey, String callingPackage, String callingFeatureId) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.telephony.ISub
        public boolean setSubscriptionEnabled(boolean enable, int subId) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.telephony.ISub
        public boolean isSubscriptionEnabled(int subId) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.telephony.ISub
        public int getEnabledSubscriptionId(int slotIndex) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.telephony.ISub
        public boolean isActiveSubId(int subId, String callingPackage, String callingFeatureId) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.telephony.ISub
        public int getActiveDataSubscriptionId() throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.telephony.ISub
        public boolean canDisablePhysicalSubscription() throws RemoteException {
            return false;
        }

        @Override // com.android.internal.telephony.ISub
        public int setUiccApplicationsEnabled(boolean enabled, int subscriptionId) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.telephony.ISub
        public int setDeviceToDeviceStatusSharing(int sharing, int subId) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.telephony.ISub
        public int setDeviceToDeviceStatusSharingContacts(String contacts, int subscriptionId) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.telephony.ISub
        public String getPhoneNumber(int subId, int source, String callingPackage, String callingFeatureId) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.telephony.ISub
        public String getPhoneNumberFromFirstAvailableSource(int subId, String callingPackage, String callingFeatureId) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.telephony.ISub
        public void setPhoneNumber(int subId, int source, String number, String callingPackage, String callingFeatureId) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ISub
        public int setUsageSetting(int usageSetting, int subId, String callingPackage) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.telephony.ISub
        public int setSubscriptionUserHandle(UserHandle userHandle, int subId) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.telephony.ISub
        public UserHandle getSubscriptionUserHandle(int subId) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.telephony.ISub
        public boolean isSubscriptionAssociatedWithUser(int subscriptionId, UserHandle userHandle) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.telephony.ISub
        public List<SubscriptionInfo> getSubscriptionInfoListAssociatedWithUser(UserHandle userHandle) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.telephony.ISub
        public boolean isSubscriptionManagerServiceEnabled() throws RemoteException {
            return false;
        }

        @Override // com.android.internal.telephony.ISub
        public void restoreAllSimSpecificSettingsFromBackup(byte[] data) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ISub {
        public static final String DESCRIPTOR = "com.android.internal.telephony.ISub";
        static final int TRANSACTION_addSubInfo = 11;
        static final int TRANSACTION_addSubscriptionsIntoGroup = 23;
        static final int TRANSACTION_canDisablePhysicalSubscription = 43;
        static final int TRANSACTION_createSubscriptionGroup = 18;
        static final int TRANSACTION_getAccessibleSubscriptionInfoList = 9;
        static final int TRANSACTION_getActiveDataSubscriptionId = 42;
        static final int TRANSACTION_getActiveSubIdList = 35;
        static final int TRANSACTION_getActiveSubInfoCount = 6;
        static final int TRANSACTION_getActiveSubInfoCountMax = 7;
        static final int TRANSACTION_getActiveSubscriptionInfo = 2;
        static final int TRANSACTION_getActiveSubscriptionInfoForIccId = 3;
        static final int TRANSACTION_getActiveSubscriptionInfoForSimSlotIndex = 4;
        static final int TRANSACTION_getActiveSubscriptionInfoList = 5;
        static final int TRANSACTION_getAllSubInfoList = 1;
        static final int TRANSACTION_getAvailableSubscriptionInfoList = 8;
        static final int TRANSACTION_getDefaultDataSubId = 29;
        static final int TRANSACTION_getDefaultSmsSubId = 33;
        static final int TRANSACTION_getDefaultSubId = 27;
        static final int TRANSACTION_getDefaultVoiceSubId = 31;
        static final int TRANSACTION_getEnabledSubscriptionId = 40;
        static final int TRANSACTION_getOpportunisticSubscriptions = 21;
        static final int TRANSACTION_getPhoneId = 28;
        static final int TRANSACTION_getPhoneNumber = 47;
        static final int TRANSACTION_getPhoneNumberFromFirstAvailableSource = 48;
        static final int TRANSACTION_getPreferredDataSubscriptionId = 20;
        static final int TRANSACTION_getSlotIndex = 25;
        static final int TRANSACTION_getSubId = 26;
        static final int TRANSACTION_getSubscriptionInfoListAssociatedWithUser = 54;
        static final int TRANSACTION_getSubscriptionProperty = 37;
        static final int TRANSACTION_getSubscriptionUserHandle = 52;
        static final int TRANSACTION_getSubscriptionsInGroup = 24;
        static final int TRANSACTION_isActiveSubId = 41;
        static final int TRANSACTION_isSubscriptionAssociatedWithUser = 53;
        static final int TRANSACTION_isSubscriptionEnabled = 39;
        static final int TRANSACTION_isSubscriptionManagerServiceEnabled = 55;
        static final int TRANSACTION_removeSubInfo = 12;
        static final int TRANSACTION_removeSubscriptionsFromGroup = 22;
        static final int TRANSACTION_requestEmbeddedSubscriptionInfoListRefresh = 10;
        static final int TRANSACTION_restoreAllSimSpecificSettingsFromBackup = 56;
        static final int TRANSACTION_setDataRoaming = 16;
        static final int TRANSACTION_setDefaultDataSubId = 30;
        static final int TRANSACTION_setDefaultSmsSubId = 34;
        static final int TRANSACTION_setDefaultVoiceSubId = 32;
        static final int TRANSACTION_setDeviceToDeviceStatusSharing = 45;
        static final int TRANSACTION_setDeviceToDeviceStatusSharingContacts = 46;
        static final int TRANSACTION_setDisplayNameUsingSrc = 14;
        static final int TRANSACTION_setDisplayNumber = 15;
        static final int TRANSACTION_setIconTint = 13;
        static final int TRANSACTION_setOpportunistic = 17;
        static final int TRANSACTION_setPhoneNumber = 49;
        static final int TRANSACTION_setPreferredDataSubscriptionId = 19;
        static final int TRANSACTION_setSubscriptionEnabled = 38;
        static final int TRANSACTION_setSubscriptionProperty = 36;
        static final int TRANSACTION_setSubscriptionUserHandle = 51;
        static final int TRANSACTION_setUiccApplicationsEnabled = 44;
        static final int TRANSACTION_setUsageSetting = 50;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ISub asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ISub)) {
                return (ISub) iin;
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
                    return "getAllSubInfoList";
                case 2:
                    return "getActiveSubscriptionInfo";
                case 3:
                    return "getActiveSubscriptionInfoForIccId";
                case 4:
                    return "getActiveSubscriptionInfoForSimSlotIndex";
                case 5:
                    return "getActiveSubscriptionInfoList";
                case 6:
                    return "getActiveSubInfoCount";
                case 7:
                    return "getActiveSubInfoCountMax";
                case 8:
                    return "getAvailableSubscriptionInfoList";
                case 9:
                    return "getAccessibleSubscriptionInfoList";
                case 10:
                    return "requestEmbeddedSubscriptionInfoListRefresh";
                case 11:
                    return "addSubInfo";
                case 12:
                    return "removeSubInfo";
                case 13:
                    return "setIconTint";
                case 14:
                    return "setDisplayNameUsingSrc";
                case 15:
                    return "setDisplayNumber";
                case 16:
                    return "setDataRoaming";
                case 17:
                    return "setOpportunistic";
                case 18:
                    return "createSubscriptionGroup";
                case 19:
                    return "setPreferredDataSubscriptionId";
                case 20:
                    return "getPreferredDataSubscriptionId";
                case 21:
                    return "getOpportunisticSubscriptions";
                case 22:
                    return "removeSubscriptionsFromGroup";
                case 23:
                    return "addSubscriptionsIntoGroup";
                case 24:
                    return "getSubscriptionsInGroup";
                case 25:
                    return "getSlotIndex";
                case 26:
                    return "getSubId";
                case 27:
                    return "getDefaultSubId";
                case 28:
                    return "getPhoneId";
                case 29:
                    return "getDefaultDataSubId";
                case 30:
                    return "setDefaultDataSubId";
                case 31:
                    return "getDefaultVoiceSubId";
                case 32:
                    return "setDefaultVoiceSubId";
                case 33:
                    return "getDefaultSmsSubId";
                case 34:
                    return "setDefaultSmsSubId";
                case 35:
                    return "getActiveSubIdList";
                case 36:
                    return "setSubscriptionProperty";
                case 37:
                    return "getSubscriptionProperty";
                case 38:
                    return "setSubscriptionEnabled";
                case 39:
                    return "isSubscriptionEnabled";
                case 40:
                    return "getEnabledSubscriptionId";
                case 41:
                    return "isActiveSubId";
                case 42:
                    return "getActiveDataSubscriptionId";
                case 43:
                    return "canDisablePhysicalSubscription";
                case 44:
                    return "setUiccApplicationsEnabled";
                case 45:
                    return "setDeviceToDeviceStatusSharing";
                case 46:
                    return "setDeviceToDeviceStatusSharingContacts";
                case 47:
                    return "getPhoneNumber";
                case 48:
                    return "getPhoneNumberFromFirstAvailableSource";
                case 49:
                    return "setPhoneNumber";
                case 50:
                    return "setUsageSetting";
                case 51:
                    return "setSubscriptionUserHandle";
                case 52:
                    return "getSubscriptionUserHandle";
                case 53:
                    return "isSubscriptionAssociatedWithUser";
                case 54:
                    return "getSubscriptionInfoListAssociatedWithUser";
                case 55:
                    return "isSubscriptionManagerServiceEnabled";
                case 56:
                    return "restoreAllSimSpecificSettingsFromBackup";
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
                            data.enforceNoDataAvail();
                            List<SubscriptionInfo> _result = getAllSubInfoList(_arg0, _arg1);
                            reply.writeNoException();
                            reply.writeTypedList(_result, 1);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            String _arg12 = data.readString();
                            String _arg2 = data.readString();
                            data.enforceNoDataAvail();
                            SubscriptionInfo _result2 = getActiveSubscriptionInfo(_arg02, _arg12, _arg2);
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            String _arg13 = data.readString();
                            String _arg22 = data.readString();
                            data.enforceNoDataAvail();
                            SubscriptionInfo _result3 = getActiveSubscriptionInfoForIccId(_arg03, _arg13, _arg22);
                            reply.writeNoException();
                            reply.writeTypedObject(_result3, 1);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            String _arg14 = data.readString();
                            String _arg23 = data.readString();
                            data.enforceNoDataAvail();
                            SubscriptionInfo _result4 = getActiveSubscriptionInfoForSimSlotIndex(_arg04, _arg14, _arg23);
                            reply.writeNoException();
                            reply.writeTypedObject(_result4, 1);
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            String _arg15 = data.readString();
                            data.enforceNoDataAvail();
                            List<SubscriptionInfo> _result5 = getActiveSubscriptionInfoList(_arg05, _arg15);
                            reply.writeNoException();
                            reply.writeTypedList(_result5, 1);
                            break;
                        case 6:
                            String _arg06 = data.readString();
                            String _arg16 = data.readString();
                            data.enforceNoDataAvail();
                            int _result6 = getActiveSubInfoCount(_arg06, _arg16);
                            reply.writeNoException();
                            reply.writeInt(_result6);
                            break;
                        case 7:
                            int _result7 = getActiveSubInfoCountMax();
                            reply.writeNoException();
                            reply.writeInt(_result7);
                            break;
                        case 8:
                            String _arg07 = data.readString();
                            String _arg17 = data.readString();
                            data.enforceNoDataAvail();
                            List<SubscriptionInfo> _result8 = getAvailableSubscriptionInfoList(_arg07, _arg17);
                            reply.writeNoException();
                            reply.writeTypedList(_result8, 1);
                            break;
                        case 9:
                            String _arg08 = data.readString();
                            data.enforceNoDataAvail();
                            List<SubscriptionInfo> _result9 = getAccessibleSubscriptionInfoList(_arg08);
                            reply.writeNoException();
                            reply.writeTypedList(_result9, 1);
                            break;
                        case 10:
                            int _arg09 = data.readInt();
                            data.enforceNoDataAvail();
                            requestEmbeddedSubscriptionInfoListRefresh(_arg09);
                            break;
                        case 11:
                            String _arg010 = data.readString();
                            String _arg18 = data.readString();
                            int _arg24 = data.readInt();
                            int _arg3 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result10 = addSubInfo(_arg010, _arg18, _arg24, _arg3);
                            reply.writeNoException();
                            reply.writeInt(_result10);
                            break;
                        case 12:
                            String _arg011 = data.readString();
                            int _arg19 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result11 = removeSubInfo(_arg011, _arg19);
                            reply.writeNoException();
                            reply.writeInt(_result11);
                            break;
                        case 13:
                            int _arg012 = data.readInt();
                            int _arg110 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result12 = setIconTint(_arg012, _arg110);
                            reply.writeNoException();
                            reply.writeInt(_result12);
                            break;
                        case 14:
                            String _arg013 = data.readString();
                            int _arg111 = data.readInt();
                            int _arg25 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result13 = setDisplayNameUsingSrc(_arg013, _arg111, _arg25);
                            reply.writeNoException();
                            reply.writeInt(_result13);
                            break;
                        case 15:
                            String _arg014 = data.readString();
                            int _arg112 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result14 = setDisplayNumber(_arg014, _arg112);
                            reply.writeNoException();
                            reply.writeInt(_result14);
                            break;
                        case 16:
                            int _arg015 = data.readInt();
                            int _arg113 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result15 = setDataRoaming(_arg015, _arg113);
                            reply.writeNoException();
                            reply.writeInt(_result15);
                            break;
                        case 17:
                            boolean _arg016 = data.readBoolean();
                            int _arg114 = data.readInt();
                            String _arg26 = data.readString();
                            data.enforceNoDataAvail();
                            int _result16 = setOpportunistic(_arg016, _arg114, _arg26);
                            reply.writeNoException();
                            reply.writeInt(_result16);
                            break;
                        case 18:
                            int[] _arg017 = data.createIntArray();
                            String _arg115 = data.readString();
                            data.enforceNoDataAvail();
                            ParcelUuid _result17 = createSubscriptionGroup(_arg017, _arg115);
                            reply.writeNoException();
                            reply.writeTypedObject(_result17, 1);
                            break;
                        case 19:
                            int _arg018 = data.readInt();
                            boolean _arg116 = data.readBoolean();
                            ISetOpportunisticDataCallback _arg27 = ISetOpportunisticDataCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setPreferredDataSubscriptionId(_arg018, _arg116, _arg27);
                            reply.writeNoException();
                            break;
                        case 20:
                            int _result18 = getPreferredDataSubscriptionId();
                            reply.writeNoException();
                            reply.writeInt(_result18);
                            break;
                        case 21:
                            String _arg019 = data.readString();
                            String _arg117 = data.readString();
                            data.enforceNoDataAvail();
                            List<SubscriptionInfo> _result19 = getOpportunisticSubscriptions(_arg019, _arg117);
                            reply.writeNoException();
                            reply.writeTypedList(_result19, 1);
                            break;
                        case 22:
                            int[] _arg020 = data.createIntArray();
                            ParcelUuid _arg118 = (ParcelUuid) data.readTypedObject(ParcelUuid.CREATOR);
                            String _arg28 = data.readString();
                            data.enforceNoDataAvail();
                            removeSubscriptionsFromGroup(_arg020, _arg118, _arg28);
                            reply.writeNoException();
                            break;
                        case 23:
                            int[] _arg021 = data.createIntArray();
                            ParcelUuid _arg119 = (ParcelUuid) data.readTypedObject(ParcelUuid.CREATOR);
                            String _arg29 = data.readString();
                            data.enforceNoDataAvail();
                            addSubscriptionsIntoGroup(_arg021, _arg119, _arg29);
                            reply.writeNoException();
                            break;
                        case 24:
                            ParcelUuid _arg022 = (ParcelUuid) data.readTypedObject(ParcelUuid.CREATOR);
                            String _arg120 = data.readString();
                            String _arg210 = data.readString();
                            data.enforceNoDataAvail();
                            List<SubscriptionInfo> _result20 = getSubscriptionsInGroup(_arg022, _arg120, _arg210);
                            reply.writeNoException();
                            reply.writeTypedList(_result20, 1);
                            break;
                        case 25:
                            int _arg023 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result21 = getSlotIndex(_arg023);
                            reply.writeNoException();
                            reply.writeInt(_result21);
                            break;
                        case 26:
                            int _arg024 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result22 = getSubId(_arg024);
                            reply.writeNoException();
                            reply.writeInt(_result22);
                            break;
                        case 27:
                            int _result23 = getDefaultSubId();
                            reply.writeNoException();
                            reply.writeInt(_result23);
                            break;
                        case 28:
                            int _arg025 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result24 = getPhoneId(_arg025);
                            reply.writeNoException();
                            reply.writeInt(_result24);
                            break;
                        case 29:
                            int _result25 = getDefaultDataSubId();
                            reply.writeNoException();
                            reply.writeInt(_result25);
                            break;
                        case 30:
                            int _arg026 = data.readInt();
                            data.enforceNoDataAvail();
                            setDefaultDataSubId(_arg026);
                            reply.writeNoException();
                            break;
                        case 31:
                            int _result26 = getDefaultVoiceSubId();
                            reply.writeNoException();
                            reply.writeInt(_result26);
                            break;
                        case 32:
                            int _arg027 = data.readInt();
                            data.enforceNoDataAvail();
                            setDefaultVoiceSubId(_arg027);
                            reply.writeNoException();
                            break;
                        case 33:
                            int _result27 = getDefaultSmsSubId();
                            reply.writeNoException();
                            reply.writeInt(_result27);
                            break;
                        case 34:
                            int _arg028 = data.readInt();
                            data.enforceNoDataAvail();
                            setDefaultSmsSubId(_arg028);
                            reply.writeNoException();
                            break;
                        case 35:
                            boolean _arg029 = data.readBoolean();
                            data.enforceNoDataAvail();
                            int[] _result28 = getActiveSubIdList(_arg029);
                            reply.writeNoException();
                            reply.writeIntArray(_result28);
                            break;
                        case 36:
                            int _arg030 = data.readInt();
                            String _arg121 = data.readString();
                            String _arg211 = data.readString();
                            data.enforceNoDataAvail();
                            int _result29 = setSubscriptionProperty(_arg030, _arg121, _arg211);
                            reply.writeNoException();
                            reply.writeInt(_result29);
                            break;
                        case 37:
                            int _arg031 = data.readInt();
                            String _arg122 = data.readString();
                            String _arg212 = data.readString();
                            String _arg32 = data.readString();
                            data.enforceNoDataAvail();
                            String _result30 = getSubscriptionProperty(_arg031, _arg122, _arg212, _arg32);
                            reply.writeNoException();
                            reply.writeString(_result30);
                            break;
                        case 38:
                            boolean _arg032 = data.readBoolean();
                            int _arg123 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result31 = setSubscriptionEnabled(_arg032, _arg123);
                            reply.writeNoException();
                            reply.writeBoolean(_result31);
                            break;
                        case 39:
                            int _arg033 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result32 = isSubscriptionEnabled(_arg033);
                            reply.writeNoException();
                            reply.writeBoolean(_result32);
                            break;
                        case 40:
                            int _arg034 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result33 = getEnabledSubscriptionId(_arg034);
                            reply.writeNoException();
                            reply.writeInt(_result33);
                            break;
                        case 41:
                            int _arg035 = data.readInt();
                            String _arg124 = data.readString();
                            String _arg213 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result34 = isActiveSubId(_arg035, _arg124, _arg213);
                            reply.writeNoException();
                            reply.writeBoolean(_result34);
                            break;
                        case 42:
                            int _result35 = getActiveDataSubscriptionId();
                            reply.writeNoException();
                            reply.writeInt(_result35);
                            break;
                        case 43:
                            boolean _result36 = canDisablePhysicalSubscription();
                            reply.writeNoException();
                            reply.writeBoolean(_result36);
                            break;
                        case 44:
                            boolean _arg036 = data.readBoolean();
                            int _arg125 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result37 = setUiccApplicationsEnabled(_arg036, _arg125);
                            reply.writeNoException();
                            reply.writeInt(_result37);
                            break;
                        case 45:
                            int _arg037 = data.readInt();
                            int _arg126 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result38 = setDeviceToDeviceStatusSharing(_arg037, _arg126);
                            reply.writeNoException();
                            reply.writeInt(_result38);
                            break;
                        case 46:
                            String _arg038 = data.readString();
                            int _arg127 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result39 = setDeviceToDeviceStatusSharingContacts(_arg038, _arg127);
                            reply.writeNoException();
                            reply.writeInt(_result39);
                            break;
                        case 47:
                            int _arg039 = data.readInt();
                            int _arg128 = data.readInt();
                            String _arg214 = data.readString();
                            String _arg33 = data.readString();
                            data.enforceNoDataAvail();
                            String _result40 = getPhoneNumber(_arg039, _arg128, _arg214, _arg33);
                            reply.writeNoException();
                            reply.writeString(_result40);
                            break;
                        case 48:
                            int _arg040 = data.readInt();
                            String _arg129 = data.readString();
                            String _arg215 = data.readString();
                            data.enforceNoDataAvail();
                            String _result41 = getPhoneNumberFromFirstAvailableSource(_arg040, _arg129, _arg215);
                            reply.writeNoException();
                            reply.writeString(_result41);
                            break;
                        case 49:
                            int _arg041 = data.readInt();
                            int _arg130 = data.readInt();
                            String _arg216 = data.readString();
                            String _arg34 = data.readString();
                            String _arg4 = data.readString();
                            data.enforceNoDataAvail();
                            setPhoneNumber(_arg041, _arg130, _arg216, _arg34, _arg4);
                            reply.writeNoException();
                            break;
                        case 50:
                            int _arg042 = data.readInt();
                            int _arg131 = data.readInt();
                            String _arg217 = data.readString();
                            data.enforceNoDataAvail();
                            int _result42 = setUsageSetting(_arg042, _arg131, _arg217);
                            reply.writeNoException();
                            reply.writeInt(_result42);
                            break;
                        case 51:
                            UserHandle _arg043 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            int _arg132 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result43 = setSubscriptionUserHandle(_arg043, _arg132);
                            reply.writeNoException();
                            reply.writeInt(_result43);
                            break;
                        case 52:
                            int _arg044 = data.readInt();
                            data.enforceNoDataAvail();
                            UserHandle _result44 = getSubscriptionUserHandle(_arg044);
                            reply.writeNoException();
                            reply.writeTypedObject(_result44, 1);
                            break;
                        case 53:
                            int _arg045 = data.readInt();
                            UserHandle _arg133 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result45 = isSubscriptionAssociatedWithUser(_arg045, _arg133);
                            reply.writeNoException();
                            reply.writeBoolean(_result45);
                            break;
                        case 54:
                            UserHandle _arg046 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            data.enforceNoDataAvail();
                            List<SubscriptionInfo> _result46 = getSubscriptionInfoListAssociatedWithUser(_arg046);
                            reply.writeNoException();
                            reply.writeTypedList(_result46, 1);
                            break;
                        case 55:
                            boolean _result47 = isSubscriptionManagerServiceEnabled();
                            reply.writeNoException();
                            reply.writeBoolean(_result47);
                            break;
                        case 56:
                            byte[] _arg047 = data.createByteArray();
                            data.enforceNoDataAvail();
                            restoreAllSimSpecificSettingsFromBackup(_arg047);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class Proxy implements ISub {
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

            @Override // com.android.internal.telephony.ISub
            public List<SubscriptionInfo> getAllSubInfoList(String callingPackage, String callingFeatureId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(callingFeatureId);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    List<SubscriptionInfo> _result = _reply.createTypedArrayList(SubscriptionInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public SubscriptionInfo getActiveSubscriptionInfo(int subId, String callingPackage, String callingFeatureId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeString(callingPackage);
                    _data.writeString(callingFeatureId);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    SubscriptionInfo _result = (SubscriptionInfo) _reply.readTypedObject(SubscriptionInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public SubscriptionInfo getActiveSubscriptionInfoForIccId(String iccId, String callingPackage, String callingFeatureId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iccId);
                    _data.writeString(callingPackage);
                    _data.writeString(callingFeatureId);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    SubscriptionInfo _result = (SubscriptionInfo) _reply.readTypedObject(SubscriptionInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public SubscriptionInfo getActiveSubscriptionInfoForSimSlotIndex(int slotIndex, String callingPackage, String callingFeatureId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotIndex);
                    _data.writeString(callingPackage);
                    _data.writeString(callingFeatureId);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    SubscriptionInfo _result = (SubscriptionInfo) _reply.readTypedObject(SubscriptionInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public List<SubscriptionInfo> getActiveSubscriptionInfoList(String callingPackage, String callingFeatureId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(callingFeatureId);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    List<SubscriptionInfo> _result = _reply.createTypedArrayList(SubscriptionInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public int getActiveSubInfoCount(String callingPackage, String callingFeatureId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(callingFeatureId);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public int getActiveSubInfoCountMax() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public List<SubscriptionInfo> getAvailableSubscriptionInfoList(String callingPackage, String callingFeatureId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(callingFeatureId);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    List<SubscriptionInfo> _result = _reply.createTypedArrayList(SubscriptionInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public List<SubscriptionInfo> getAccessibleSubscriptionInfoList(String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    List<SubscriptionInfo> _result = _reply.createTypedArrayList(SubscriptionInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public void requestEmbeddedSubscriptionInfoListRefresh(int cardId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(cardId);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public int addSubInfo(String uniqueId, String displayName, int slotIndex, int subscriptionType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(uniqueId);
                    _data.writeString(displayName);
                    _data.writeInt(slotIndex);
                    _data.writeInt(subscriptionType);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public int removeSubInfo(String uniqueId, int subscriptionType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(uniqueId);
                    _data.writeInt(subscriptionType);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public int setIconTint(int subId, int tint) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeInt(tint);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public int setDisplayNameUsingSrc(String displayName, int subId, int nameSource) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(displayName);
                    _data.writeInt(subId);
                    _data.writeInt(nameSource);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public int setDisplayNumber(String number, int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(number);
                    _data.writeInt(subId);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public int setDataRoaming(int roaming, int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(roaming);
                    _data.writeInt(subId);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public int setOpportunistic(boolean opportunistic, int subId, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(opportunistic);
                    _data.writeInt(subId);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public ParcelUuid createSubscriptionGroup(int[] subIdList, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeIntArray(subIdList);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    ParcelUuid _result = (ParcelUuid) _reply.readTypedObject(ParcelUuid.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public void setPreferredDataSubscriptionId(int subId, boolean needValidation, ISetOpportunisticDataCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeBoolean(needValidation);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public int getPreferredDataSubscriptionId() throws RemoteException {
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

            @Override // com.android.internal.telephony.ISub
            public List<SubscriptionInfo> getOpportunisticSubscriptions(String callingPackage, String callingFeatureId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeString(callingFeatureId);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                    List<SubscriptionInfo> _result = _reply.createTypedArrayList(SubscriptionInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public void removeSubscriptionsFromGroup(int[] subIdList, ParcelUuid groupUuid, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeIntArray(subIdList);
                    _data.writeTypedObject(groupUuid, 0);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public void addSubscriptionsIntoGroup(int[] subIdList, ParcelUuid groupUuid, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeIntArray(subIdList);
                    _data.writeTypedObject(groupUuid, 0);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public List<SubscriptionInfo> getSubscriptionsInGroup(ParcelUuid groupUuid, String callingPackage, String callingFeatureId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(groupUuid, 0);
                    _data.writeString(callingPackage);
                    _data.writeString(callingFeatureId);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                    List<SubscriptionInfo> _result = _reply.createTypedArrayList(SubscriptionInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public int getSlotIndex(int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public int getSubId(int slotIndex) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotIndex);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public int getDefaultSubId() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public int getPhoneId(int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public int getDefaultDataSubId() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public void setDefaultDataSubId(int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public int getDefaultVoiceSubId() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public void setDefaultVoiceSubId(int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public int getDefaultSmsSubId() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public void setDefaultSmsSubId(int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public int[] getActiveSubIdList(boolean visibleOnly) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(visibleOnly);
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public int setSubscriptionProperty(int subId, String propKey, String propValue) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeString(propKey);
                    _data.writeString(propValue);
                    this.mRemote.transact(36, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public String getSubscriptionProperty(int subId, String propKey, String callingPackage, String callingFeatureId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeString(propKey);
                    _data.writeString(callingPackage);
                    _data.writeString(callingFeatureId);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public boolean setSubscriptionEnabled(boolean enable, int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enable);
                    _data.writeInt(subId);
                    this.mRemote.transact(38, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public boolean isSubscriptionEnabled(int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    this.mRemote.transact(39, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public int getEnabledSubscriptionId(int slotIndex) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotIndex);
                    this.mRemote.transact(40, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public boolean isActiveSubId(int subId, String callingPackage, String callingFeatureId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeString(callingPackage);
                    _data.writeString(callingFeatureId);
                    this.mRemote.transact(41, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public int getActiveDataSubscriptionId() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(42, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public boolean canDisablePhysicalSubscription() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(43, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public int setUiccApplicationsEnabled(boolean enabled, int subscriptionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    _data.writeInt(subscriptionId);
                    this.mRemote.transact(44, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public int setDeviceToDeviceStatusSharing(int sharing, int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sharing);
                    _data.writeInt(subId);
                    this.mRemote.transact(45, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public int setDeviceToDeviceStatusSharingContacts(String contacts, int subscriptionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(contacts);
                    _data.writeInt(subscriptionId);
                    this.mRemote.transact(46, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public String getPhoneNumber(int subId, int source, String callingPackage, String callingFeatureId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeInt(source);
                    _data.writeString(callingPackage);
                    _data.writeString(callingFeatureId);
                    this.mRemote.transact(47, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public String getPhoneNumberFromFirstAvailableSource(int subId, String callingPackage, String callingFeatureId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeString(callingPackage);
                    _data.writeString(callingFeatureId);
                    this.mRemote.transact(48, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public void setPhoneNumber(int subId, int source, String number, String callingPackage, String callingFeatureId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeInt(source);
                    _data.writeString(number);
                    _data.writeString(callingPackage);
                    _data.writeString(callingFeatureId);
                    this.mRemote.transact(49, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public int setUsageSetting(int usageSetting, int subId, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(usageSetting);
                    _data.writeInt(subId);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(50, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public int setSubscriptionUserHandle(UserHandle userHandle, int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(userHandle, 0);
                    _data.writeInt(subId);
                    this.mRemote.transact(51, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public UserHandle getSubscriptionUserHandle(int subId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    this.mRemote.transact(52, _data, _reply, 0);
                    _reply.readException();
                    UserHandle _result = (UserHandle) _reply.readTypedObject(UserHandle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public boolean isSubscriptionAssociatedWithUser(int subscriptionId, UserHandle userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subscriptionId);
                    _data.writeTypedObject(userHandle, 0);
                    this.mRemote.transact(53, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public List<SubscriptionInfo> getSubscriptionInfoListAssociatedWithUser(UserHandle userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(userHandle, 0);
                    this.mRemote.transact(54, _data, _reply, 0);
                    _reply.readException();
                    List<SubscriptionInfo> _result = _reply.createTypedArrayList(SubscriptionInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public boolean isSubscriptionManagerServiceEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(55, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ISub
            public void restoreAllSimSpecificSettingsFromBackup(byte[] data) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(data);
                    this.mRemote.transact(56, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 55;
        }
    }
}
