package com.android.ims.rcs.uce.eab;

import android.app.AlarmManager;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.CarrierConfigManager;
import android.telephony.ims.ImsManager;
import android.telephony.ims.ImsRcsManager;
import android.telephony.ims.RcsContactUceCapability;
import android.telephony.ims.SipDetails;
import android.telephony.ims.aidl.IRcsUceControllerCallback;
import android.util.Log;
import com.android.ims.rcs.uce.UceController;
import com.android.ims.rcs.uce.eab.EabProvider;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public final class EabBulkCapabilityUpdater {
    private static final int NUM_SECS_IN_DAY = 86400;
    private static final Uri USER_EAB_SETTING = Uri.withAppendedPath(Telephony.SimInfo.CONTENT_URI, "ims_rcs_uce_enabled");
    private final String TAG;
    private final AlarmManager.OnAlarmListener mCapabilityExpiredListener;
    private final ContactChangedListener mContactProviderListener;
    private final Context mContext;
    private final EabContactSyncController mEabContactSyncController;
    private final EabControllerImpl mEabControllerImpl;
    private final EabSettingsListener mEabSettingListener;
    private final Handler mHandler;
    private boolean mIsCarrierConfigEnabled;
    private boolean mIsCarrierConfigListenerRegistered;
    private boolean mIsContactProviderListenerRegistered;
    private boolean mIsEabSettingListenerRegistered;
    private IRcsUceControllerCallback mRcsUceControllerCallback;
    private List<Uri> mRefreshContactList;
    private final int mSubId;
    private UceController.UceControllerCallback mUceControllerCallback;

    /* loaded from: classes.dex */
    private class CapabilityExpiredListener implements AlarmManager.OnAlarmListener {
        private CapabilityExpiredListener() {
        }

        @Override // android.app.AlarmManager.OnAlarmListener
        public void onAlarm() {
            Log.d(EabBulkCapabilityUpdater.this.TAG, "Capability expired.");
            try {
                List<Uri> expiredContactList = EabBulkCapabilityUpdater.this.getExpiredContactList();
                if (expiredContactList.size() > 0) {
                    EabBulkCapabilityUpdater.this.mUceControllerCallback.refreshCapabilities(EabBulkCapabilityUpdater.this.getExpiredContactList(), EabBulkCapabilityUpdater.this.mRcsUceControllerCallback);
                } else {
                    Log.d(EabBulkCapabilityUpdater.this.TAG, "expiredContactList is empty.");
                }
            } catch (RemoteException e) {
                Log.e(EabBulkCapabilityUpdater.this.TAG, "CapabilityExpiredListener RemoteException", e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ContactChangedListener extends ContentObserver {
        public ContactChangedListener(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            Log.d(EabBulkCapabilityUpdater.this.TAG, "Contact changed");
            EabBulkCapabilityUpdater.this.syncContactAndRefreshCapabilities();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class EabSettingsListener extends ContentObserver {
        public EabSettingsListener(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            boolean isUserEnableUce = EabBulkCapabilityUpdater.this.isUserEnableUce();
            Log.d(EabBulkCapabilityUpdater.this.TAG, "EAB user setting changed: " + isUserEnableUce);
            if (isUserEnableUce) {
                EabBulkCapabilityUpdater.this.mHandler.post(new SyncContactRunnable());
                return;
            }
            EabBulkCapabilityUpdater.this.unRegisterContactProviderListener();
            EabBulkCapabilityUpdater eabBulkCapabilityUpdater = EabBulkCapabilityUpdater.this;
            eabBulkCapabilityUpdater.cancelTimeAlert(eabBulkCapabilityUpdater.mContext);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SyncContactRunnable implements Runnable {
        private SyncContactRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            Log.d(EabBulkCapabilityUpdater.this.TAG, "Sync contact from contact provider");
            EabBulkCapabilityUpdater.this.syncContactAndRefreshCapabilities();
            EabBulkCapabilityUpdater.this.registerContactProviderListener();
            EabBulkCapabilityUpdater.this.registerEabUserSettingsListener();
        }
    }

    /* loaded from: classes.dex */
    private class retryRunnable implements Runnable {
        private retryRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            Log.d(EabBulkCapabilityUpdater.this.TAG, "Retry refreshCapabilities()");
            try {
                EabBulkCapabilityUpdater.this.mUceControllerCallback.refreshCapabilities(EabBulkCapabilityUpdater.this.mRefreshContactList, EabBulkCapabilityUpdater.this.mRcsUceControllerCallback);
            } catch (RemoteException e) {
                Log.e(EabBulkCapabilityUpdater.this.TAG, "refreshCapabilities RemoteException", e);
            }
        }
    }

    public EabBulkCapabilityUpdater(Context context, int subId, EabControllerImpl eabControllerImpl, EabContactSyncController eabContactSyncController, UceController.UceControllerCallback uceControllerCallback, Handler handler) {
        String simpleName = getClass().getSimpleName();
        this.TAG = simpleName;
        this.mIsContactProviderListenerRegistered = false;
        this.mIsEabSettingListenerRegistered = false;
        this.mIsCarrierConfigListenerRegistered = false;
        this.mIsCarrierConfigEnabled = false;
        this.mRcsUceControllerCallback = new IRcsUceControllerCallback() { // from class: com.android.ims.rcs.uce.eab.EabBulkCapabilityUpdater.1
            public void onCapabilitiesReceived(List<RcsContactUceCapability> contactCapabilities) {
                Log.d(EabBulkCapabilityUpdater.this.TAG, "onCapabilitiesReceived");
                EabBulkCapabilityUpdater.this.mEabControllerImpl.saveCapabilities(contactCapabilities);
            }

            public void onComplete(SipDetails details) {
                Log.d(EabBulkCapabilityUpdater.this.TAG, "onComplete");
            }

            public void onError(int errorCode, long retryAfterMilliseconds, SipDetails details) {
                Log.d(EabBulkCapabilityUpdater.this.TAG, "Refresh capabilities failed. Error code: " + errorCode + ", retryAfterMilliseconds: " + retryAfterMilliseconds);
                if (retryAfterMilliseconds != 0) {
                    EabBulkCapabilityUpdater.this.mHandler.postDelayed(new retryRunnable(), retryAfterMilliseconds);
                }
            }

            public IBinder asBinder() {
                return null;
            }
        };
        this.mContext = context;
        this.mSubId = subId;
        this.mEabControllerImpl = eabControllerImpl;
        this.mEabContactSyncController = eabContactSyncController;
        this.mUceControllerCallback = uceControllerCallback;
        this.mHandler = handler;
        this.mContactProviderListener = new ContactChangedListener(handler);
        this.mEabSettingListener = new EabSettingsListener(handler);
        this.mCapabilityExpiredListener = new CapabilityExpiredListener();
        Log.d(simpleName, "create EabBulkCapabilityUpdater() subId: " + subId);
        enableBulkCapability();
        updateExpiredTimeAlert();
    }

    private void enableBulkCapability() {
        boolean isUserEnableUce = isUserEnableUce();
        boolean isSupportBulkCapabilityExchange = getBooleanCarrierConfig("ims.rcs_bulk_capability_exchange_bool", this.mSubId);
        Log.d(this.TAG, "isUserEnableUce: " + isUserEnableUce + ", isSupportBulkCapabilityExchange: " + isSupportBulkCapabilityExchange);
        if (isUserEnableUce && isSupportBulkCapabilityExchange) {
            this.mHandler.post(new SyncContactRunnable());
            this.mIsCarrierConfigEnabled = true;
        } else if (!isUserEnableUce && isSupportBulkCapabilityExchange) {
            registerEabUserSettingsListener();
            this.mIsCarrierConfigEnabled = false;
        } else {
            Log.d(this.TAG, "Not support bulk capability exchange.");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void syncContactAndRefreshCapabilities() {
        this.mRefreshContactList = this.mEabContactSyncController.syncContactToEabProvider(this.mContext);
        Log.d(this.TAG, "refresh contacts number: " + this.mRefreshContactList.size());
        if (this.mUceControllerCallback == null) {
            Log.d(this.TAG, "mUceControllerCallback is null.");
            return;
        }
        try {
            if (this.mRefreshContactList.size() > 0) {
                this.mUceControllerCallback.refreshCapabilities(this.mRefreshContactList, this.mRcsUceControllerCallback);
            }
        } catch (RemoteException e) {
            Log.e(this.TAG, "mUceControllerCallback RemoteException.", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateExpiredTimeAlert() {
        boolean isUserEnableUce = isUserEnableUce();
        boolean isSupportBulkCapabilityExchange = getBooleanCarrierConfig("ims.rcs_bulk_capability_exchange_bool", this.mSubId);
        Log.d(this.TAG, " updateExpiredTimeAlert(), isUserEnableUce: " + isUserEnableUce + ", isSupportBulkCapabilityExchange: " + isSupportBulkCapabilityExchange);
        if (isUserEnableUce && isSupportBulkCapabilityExchange) {
            long expiredTimestamp = getLeastExpiredTimestamp();
            if (expiredTimestamp == Long.MAX_VALUE) {
                Log.d(this.TAG, "Can't find min timestamp in eab provider");
                return;
            }
            long expiredTimestamp2 = expiredTimestamp + this.mEabControllerImpl.getCapabilityCacheExpiration(this.mSubId);
            Log.d(this.TAG, "set time alert at " + expiredTimestamp2);
            cancelTimeAlert(this.mContext);
            setTimeAlert(this.mContext, expiredTimestamp2);
        }
    }

    private long getLeastExpiredTimestamp() {
        long timestamp;
        String selection = "(mechanism=1 AND presence_request_timestamp IS NOT NULL)  OR (mechanism=2 AND options_request_timestamp IS NOT NULL)  AND subscription_id=" + this.mSubId + " AND " + EabProvider.ContactColumns.RAW_CONTACT_ID + " IS NOT NULL  AND " + EabProvider.ContactColumns.DATA_ID + " IS NOT NULL ";
        long minTimestamp = Long.MAX_VALUE;
        Cursor result = this.mContext.getContentResolver().query(EabProvider.ALL_DATA_URI, null, selection, null, null);
        if (result != null) {
            while (result.moveToNext()) {
                int mechanism = result.getInt(result.getColumnIndex(EabProvider.EabCommonColumns.MECHANISM));
                if (mechanism == 1) {
                    timestamp = result.getLong(result.getColumnIndex(EabProvider.PresenceTupleColumns.REQUEST_TIMESTAMP));
                } else {
                    timestamp = result.getLong(result.getColumnIndex(EabProvider.OptionsColumns.REQUEST_TIMESTAMP));
                }
                if (timestamp < minTimestamp) {
                    minTimestamp = timestamp;
                }
            }
            result.close();
        } else {
            Log.d(this.TAG, "getLeastExpiredTimestamp() cursor is null");
        }
        return minTimestamp;
    }

    private void setTimeAlert(Context context, long wakeupTimeMs) {
        AlarmManager am = (AlarmManager) context.getSystemService(AlarmManager.class);
        int jitterTimeSec = (int) (Math.random() * 172800.0d);
        Log.d(this.TAG, " setTimeAlert: " + wakeupTimeMs + ", jitterTimeSec: " + jitterTimeSec);
        am.set(0, (1000 * wakeupTimeMs) + jitterTimeSec, this.TAG, this.mCapabilityExpiredListener, this.mHandler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelTimeAlert(Context context) {
        Log.d(this.TAG, "cancelTimeAlert.");
        AlarmManager am = (AlarmManager) context.getSystemService(AlarmManager.class);
        am.cancel(this.mCapabilityExpiredListener);
    }

    private boolean getBooleanCarrierConfig(String key, int subId) {
        CarrierConfigManager mConfigManager = (CarrierConfigManager) this.mContext.getSystemService(CarrierConfigManager.class);
        PersistableBundle b = null;
        if (mConfigManager != null) {
            b = mConfigManager.getConfigForSubId(subId);
        }
        if (b != null) {
            return b.getBoolean(key);
        }
        Log.w(this.TAG, "getConfigForSubId(subId) is null. Return the default value of " + key);
        return CarrierConfigManager.getDefaultConfig().getBoolean(key);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isUserEnableUce() {
        ImsManager manager = (ImsManager) this.mContext.getSystemService(ImsManager.class);
        if (manager == null) {
            Log.e(this.TAG, "ImsManager is null");
            return false;
        }
        try {
            ImsRcsManager rcsManager = manager.getImsRcsManager(this.mSubId);
            if (rcsManager != null) {
                return rcsManager.getUceAdapter().isUceSettingEnabled();
            }
            return false;
        } catch (Exception e) {
            Log.e(this.TAG, "hasUserEnabledUce: exception = " + e.getMessage());
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<Uri> getExpiredContactList() {
        List<Uri> refreshList = new ArrayList<>();
        long expiredTime = (System.currentTimeMillis() / 1000) + this.mEabControllerImpl.getCapabilityCacheExpiration(this.mSubId);
        String selection = "(mechanism=1 AND presence_request_timestamp<" + expiredTime + ")";
        Cursor result = this.mContext.getContentResolver().query(EabProvider.ALL_DATA_URI, null, selection + " OR (mechanism=2 AND options_request_timestamp<" + expiredTime + ")", null, null);
        while (result.moveToNext()) {
            String phoneNumber = result.getString(result.getColumnIndex(EabProvider.ContactColumns.PHONE_NUMBER));
            refreshList.add(Uri.parse(phoneNumber));
        }
        result.close();
        return refreshList;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onDestroy() {
        Log.d(this.TAG, "onDestroy");
        cancelTimeAlert(this.mContext);
        unRegisterContactProviderListener();
        unRegisterEabUserSettings();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerContactProviderListener() {
        Log.d(this.TAG, "registerContactProviderListener");
        this.mIsContactProviderListenerRegistered = true;
        this.mContext.getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, this.mContactProviderListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerEabUserSettingsListener() {
        Log.d(this.TAG, "registerEabUserSettingsListener");
        this.mIsEabSettingListenerRegistered = true;
        this.mContext.getContentResolver().registerContentObserver(USER_EAB_SETTING, true, this.mEabSettingListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unRegisterContactProviderListener() {
        Log.d(this.TAG, "unRegisterContactProviderListener");
        if (this.mIsContactProviderListenerRegistered) {
            this.mIsContactProviderListenerRegistered = false;
            this.mContext.getContentResolver().unregisterContentObserver(this.mContactProviderListener);
        }
    }

    private void unRegisterEabUserSettings() {
        Log.d(this.TAG, "unRegisterEabUserSettings");
        if (this.mIsEabSettingListenerRegistered) {
            this.mIsEabSettingListenerRegistered = false;
            this.mContext.getContentResolver().unregisterContentObserver(this.mEabSettingListener);
        }
    }

    public void setUceRequestCallback(UceController.UceControllerCallback uceControllerCallback) {
        this.mUceControllerCallback = uceControllerCallback;
    }

    public void onCarrierConfigChanged() {
        boolean isSupportBulkCapabilityExchange = getBooleanCarrierConfig("ims.rcs_bulk_capability_exchange_bool", this.mSubId);
        Log.d(this.TAG, "Carrier config changed. isCarrierConfigEnabled: " + this.mIsCarrierConfigEnabled + ", isSupportBulkCapabilityExchange: " + isSupportBulkCapabilityExchange);
        boolean z = this.mIsCarrierConfigEnabled;
        if (!z && isSupportBulkCapabilityExchange) {
            enableBulkCapability();
            updateExpiredTimeAlert();
            this.mIsCarrierConfigEnabled = true;
        } else if (z && !isSupportBulkCapabilityExchange) {
            onDestroy();
        }
    }
}
