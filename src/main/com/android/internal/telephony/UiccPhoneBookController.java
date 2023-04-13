package com.android.internal.telephony;

import android.compat.annotation.UnsupportedAppUsage;
import android.content.ContentValues;
import android.os.RemoteException;
import android.os.TelephonyServiceManager;
import android.telephony.TelephonyFrameworkInitializer;
import com.android.internal.telephony.IIccPhoneBook;
import com.android.internal.telephony.subscription.SubscriptionManagerService;
import com.android.internal.telephony.uicc.AdnCapacity;
import com.android.internal.telephony.uicc.AdnRecord;
import com.android.telephony.Rlog;
import java.util.List;
/* loaded from: classes.dex */
public class UiccPhoneBookController extends IIccPhoneBook.Stub {
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public UiccPhoneBookController() {
        TelephonyServiceManager.ServiceRegisterer iccPhoneBookServiceRegisterer = TelephonyFrameworkInitializer.getTelephonyServiceManager().getIccPhoneBookServiceRegisterer();
        if (iccPhoneBookServiceRegisterer.get() == null) {
            iccPhoneBookServiceRegisterer.register(this);
        }
    }

    @Override // com.android.internal.telephony.IIccPhoneBook
    public boolean updateAdnRecordsInEfBySearch(int i, String str, String str2, String str3, String str4, String str5) throws RemoteException {
        ContentValues contentValues = new ContentValues();
        contentValues.put(IccProvider.STR_TAG, str);
        contentValues.put(IccProvider.STR_NUMBER, str2);
        contentValues.put(IccProvider.STR_NEW_TAG, str3);
        contentValues.put(IccProvider.STR_NEW_NUMBER, str4);
        return updateAdnRecordsInEfBySearchForSubscriber(getDefaultSubscription(), i, contentValues, str5);
    }

    @Override // com.android.internal.telephony.IIccPhoneBook
    public boolean updateAdnRecordsInEfByIndexForSubscriber(int i, int i2, ContentValues contentValues, int i3, String str) throws RemoteException {
        IccPhoneBookInterfaceManager iccPhoneBookInterfaceManager = getIccPhoneBookInterfaceManager(i);
        if (iccPhoneBookInterfaceManager != null) {
            return iccPhoneBookInterfaceManager.updateAdnRecordsInEfByIndex(i2, contentValues, i3, str);
        }
        Rlog.e("UiccPhoneBookController", "updateAdnRecordsInEfByIndex iccPbkIntMgr is null for Subscription:" + i);
        return false;
    }

    @Override // com.android.internal.telephony.IIccPhoneBook
    public int[] getAdnRecordsSize(int i) throws RemoteException {
        return getAdnRecordsSizeForSubscriber(getDefaultSubscription(), i);
    }

    @Override // com.android.internal.telephony.IIccPhoneBook
    public int[] getAdnRecordsSizeForSubscriber(int i, int i2) throws RemoteException {
        IccPhoneBookInterfaceManager iccPhoneBookInterfaceManager = getIccPhoneBookInterfaceManager(i);
        if (iccPhoneBookInterfaceManager != null) {
            return iccPhoneBookInterfaceManager.getAdnRecordsSize(i2);
        }
        Rlog.e("UiccPhoneBookController", "getAdnRecordsSize iccPbkIntMgr is null for Subscription:" + i);
        return null;
    }

    @Override // com.android.internal.telephony.IIccPhoneBook
    public List<AdnRecord> getAdnRecordsInEf(int i) throws RemoteException {
        return getAdnRecordsInEfForSubscriber(getDefaultSubscription(), i);
    }

    @Override // com.android.internal.telephony.IIccPhoneBook
    public List<AdnRecord> getAdnRecordsInEfForSubscriber(int i, int i2) throws RemoteException {
        IccPhoneBookInterfaceManager iccPhoneBookInterfaceManager = getIccPhoneBookInterfaceManager(i);
        if (iccPhoneBookInterfaceManager != null) {
            return iccPhoneBookInterfaceManager.getAdnRecordsInEf(i2);
        }
        Rlog.e("UiccPhoneBookController", "getAdnRecordsInEf iccPbkIntMgr isnull for Subscription:" + i);
        return null;
    }

    @Override // com.android.internal.telephony.IIccPhoneBook
    public AdnCapacity getAdnRecordsCapacityForSubscriber(int i) throws RemoteException {
        IccPhoneBookInterfaceManager iccPhoneBookInterfaceManager = getIccPhoneBookInterfaceManager(i);
        if (iccPhoneBookInterfaceManager != null) {
            return iccPhoneBookInterfaceManager.getAdnRecordsCapacity();
        }
        Rlog.e("UiccPhoneBookController", "getAdnRecordsCapacity iccPbkIntMgr is null for Subscription:" + i);
        return null;
    }

    @Override // com.android.internal.telephony.IIccPhoneBook
    public boolean updateAdnRecordsInEfBySearchForSubscriber(int i, int i2, ContentValues contentValues, String str) throws RemoteException {
        IccPhoneBookInterfaceManager iccPhoneBookInterfaceManager = getIccPhoneBookInterfaceManager(i);
        if (iccPhoneBookInterfaceManager != null) {
            return iccPhoneBookInterfaceManager.updateAdnRecordsInEfBySearchForSubscriber(i2, contentValues, str);
        }
        Rlog.e("UiccPhoneBookController", "updateAdnRecordsInEfBySearchForSubscriber iccPbkIntMgr is null for Subscription:" + i);
        return false;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private IccPhoneBookInterfaceManager getIccPhoneBookInterfaceManager(int i) {
        int phoneId;
        if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
            phoneId = SubscriptionManagerService.getInstance().getPhoneId(i);
        } else {
            phoneId = SubscriptionController.getInstance().getPhoneId(i);
        }
        try {
            return PhoneFactory.getPhone(phoneId).getIccPhoneBookInterfaceManager();
        } catch (ArrayIndexOutOfBoundsException e) {
            Rlog.e("UiccPhoneBookController", "Exception is :" + e.toString() + " For subscription :" + i);
            e.printStackTrace();
            return null;
        } catch (NullPointerException e2) {
            Rlog.e("UiccPhoneBookController", "Exception is :" + e2.toString() + " For subscription :" + i);
            e2.printStackTrace();
            return null;
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private int getDefaultSubscription() {
        return PhoneFactory.getDefaultSubscription();
    }
}
