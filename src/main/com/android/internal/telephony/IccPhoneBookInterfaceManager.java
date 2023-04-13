package com.android.internal.telephony;

import android.compat.annotation.UnsupportedAppUsage;
import android.content.ContentValues;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import com.android.internal.telephony.IccCardConstants;
import com.android.internal.telephony.uicc.AdnCapacity;
import com.android.internal.telephony.uicc.AdnRecord;
import com.android.internal.telephony.uicc.AdnRecordCache;
import com.android.internal.telephony.uicc.IccCardApplicationStatus;
import com.android.internal.telephony.uicc.IccConstants;
import com.android.internal.telephony.uicc.IccFileHandler;
import com.android.internal.telephony.uicc.IccRecords;
import com.android.internal.telephony.uicc.SimPhonebookRecordCache;
import com.android.internal.telephony.uicc.UiccController;
import com.android.internal.telephony.uicc.UiccProfile;
import com.android.telephony.Rlog;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
/* loaded from: classes.dex */
public class IccPhoneBookInterfaceManager {
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected static final boolean DBG = true;
    protected static final int EVENT_GET_SIZE_DONE = 1;
    protected static final int EVENT_LOAD_DONE = 2;
    protected static final int EVENT_UPDATE_DONE = 3;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected AdnRecordCache mAdnCache;
    @UnsupportedAppUsage
    protected Handler mBaseHandler = new Handler() { // from class: com.android.internal.telephony.IccPhoneBookInterfaceManager.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            AsyncResult asyncResult = (AsyncResult) message.obj;
            Request request = (Request) asyncResult.userObj;
            int i = message.what;
            int[] iArr = null;
            Object obj = null;
            if (i == 1) {
                if (asyncResult.exception == null) {
                    iArr = (int[]) asyncResult.result;
                    IccPhoneBookInterfaceManager iccPhoneBookInterfaceManager = IccPhoneBookInterfaceManager.this;
                    iccPhoneBookInterfaceManager.logd("GET_RECORD_SIZE Size " + iArr[0] + " total " + iArr[1] + " #record " + iArr[2]);
                } else {
                    IccPhoneBookInterfaceManager iccPhoneBookInterfaceManager2 = IccPhoneBookInterfaceManager.this;
                    iccPhoneBookInterfaceManager2.loge("EVENT_GET_SIZE_DONE: failed; ex=" + asyncResult.exception);
                }
                notifyPending(request, iArr);
            } else if (i == 2) {
                if (asyncResult.exception == null) {
                    obj = (List) asyncResult.result;
                } else {
                    IccPhoneBookInterfaceManager iccPhoneBookInterfaceManager3 = IccPhoneBookInterfaceManager.this;
                    iccPhoneBookInterfaceManager3.loge("EVENT_LOAD_DONE: Cannot load ADN records; ex=" + asyncResult.exception);
                }
                notifyPending(request, obj);
            } else if (i != 3) {
            } else {
                boolean z = asyncResult.exception == null;
                if (!z) {
                    IccPhoneBookInterfaceManager iccPhoneBookInterfaceManager4 = IccPhoneBookInterfaceManager.this;
                    iccPhoneBookInterfaceManager4.loge("EVENT_UPDATE_DONE - failed; ex=" + asyncResult.exception);
                }
                notifyPending(request, Boolean.valueOf(z));
            }
        }

        private void notifyPending(Request request, Object obj) {
            if (request != null) {
                synchronized (request) {
                    request.mResult = obj;
                    request.mStatus.set(true);
                    request.notifyAll();
                }
            }
        }
    };
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected Phone mPhone;
    protected SimPhonebookRecordCache mSimPbRecordCache;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class Request {
        Object mResult;
        AtomicBoolean mStatus;

        private Request() {
            this.mStatus = new AtomicBoolean(false);
            this.mResult = null;
        }
    }

    public IccPhoneBookInterfaceManager(Phone phone) {
        this.mPhone = phone;
        IccRecords iccRecords = phone.getIccRecords();
        if (iccRecords != null) {
            this.mAdnCache = iccRecords.getAdnCache();
        }
        this.mSimPbRecordCache = new SimPhonebookRecordCache(phone.getContext(), phone.getPhoneId(), phone.mCi);
    }

    public void dispose() {
        this.mSimPbRecordCache.dispose();
    }

    public void updateIccRecords(IccRecords iccRecords) {
        if (iccRecords != null) {
            this.mAdnCache = iccRecords.getAdnCache();
        } else {
            this.mAdnCache = null;
        }
    }

    @UnsupportedAppUsage
    protected void logd(String str) {
        Rlog.d("IccPhoneBookIM", "[IccPbInterfaceManager] " + str);
    }

    @UnsupportedAppUsage
    protected void loge(String str) {
        Rlog.e("IccPhoneBookIM", "[IccPbInterfaceManager] " + str);
    }

    private AdnRecord generateAdnRecordWithOldTagByContentValues(ContentValues contentValues) {
        if (contentValues == null) {
            return null;
        }
        String asString = contentValues.getAsString(IccProvider.STR_TAG);
        String asString2 = contentValues.getAsString(IccProvider.STR_NUMBER);
        String asString3 = contentValues.getAsString(IccProvider.STR_EMAILS);
        String asString4 = contentValues.getAsString(IccProvider.STR_ANRS);
        return new AdnRecord(asString, asString2, TextUtils.isEmpty(asString3) ? null : getEmailStringArray(asString3), TextUtils.isEmpty(asString4) ? null : getAnrStringArray(asString4));
    }

    private AdnRecord generateAdnRecordWithNewTagByContentValues(int i, int i2, ContentValues contentValues) {
        if (contentValues == null) {
            return null;
        }
        String asString = contentValues.getAsString(IccProvider.STR_NEW_TAG);
        String asString2 = contentValues.getAsString(IccProvider.STR_NEW_NUMBER);
        String asString3 = contentValues.getAsString(IccProvider.STR_NEW_EMAILS);
        String asString4 = contentValues.getAsString(IccProvider.STR_NEW_ANRS);
        return new AdnRecord(i, i2, asString, asString2, TextUtils.isEmpty(asString3) ? null : getEmailStringArray(asString3), TextUtils.isEmpty(asString4) ? null : getAnrStringArray(asString4));
    }

    public boolean updateAdnRecordsInEfBySearchForSubscriber(int i, ContentValues contentValues, String str) {
        if (this.mPhone.getContext().checkCallingOrSelfPermission("android.permission.WRITE_CONTACTS") != 0) {
            throw new SecurityException("Requires android.permission.WRITE_CONTACTS permission");
        }
        int updateEfForIccType = updateEfForIccType(i);
        logd("updateAdnRecordsWithContentValuesInEfBySearch: efid=" + updateEfForIccType + ", values = " + contentValues + ", pin2=" + str);
        checkThread();
        Request request = new Request();
        synchronized (request) {
            Message obtainMessage = this.mBaseHandler.obtainMessage(3, request);
            AdnRecord generateAdnRecordWithOldTagByContentValues = generateAdnRecordWithOldTagByContentValues(contentValues);
            if (usesPbCache(updateEfForIccType)) {
                this.mSimPbRecordCache.updateSimPbAdnBySearch(generateAdnRecordWithOldTagByContentValues, generateAdnRecordWithNewTagByContentValues(28474, 0, contentValues), obtainMessage);
                waitForResult(request);
                return ((Boolean) request.mResult).booleanValue();
            }
            AdnRecord generateAdnRecordWithNewTagByContentValues = generateAdnRecordWithNewTagByContentValues(updateEfForIccType, 0, contentValues);
            AdnRecordCache adnRecordCache = this.mAdnCache;
            if (adnRecordCache != null) {
                adnRecordCache.updateAdnBySearch(updateEfForIccType, generateAdnRecordWithOldTagByContentValues, generateAdnRecordWithNewTagByContentValues, str, obtainMessage);
                waitForResult(request);
                return ((Boolean) request.mResult).booleanValue();
            }
            loge("Failure while trying to update by search due to uninitialised adncache");
            return false;
        }
    }

    public boolean updateAdnRecordsInEfByIndex(int i, ContentValues contentValues, int i2, String str) {
        if (this.mPhone.getContext().checkCallingOrSelfPermission("android.permission.WRITE_CONTACTS") != 0) {
            throw new SecurityException("Requires android.permission.WRITE_CONTACTS permission");
        }
        logd("updateAdnRecordsInEfByIndex: efid=" + i + ", values = " + contentValues + " index=" + i2 + ", pin2=" + str);
        checkThread();
        Request request = new Request();
        synchronized (request) {
            Message obtainMessage = this.mBaseHandler.obtainMessage(3, request);
            if (usesPbCache(i)) {
                this.mSimPbRecordCache.updateSimPbAdnByRecordId(i2, generateAdnRecordWithNewTagByContentValues(28474, i2, contentValues), obtainMessage);
                waitForResult(request);
                return ((Boolean) request.mResult).booleanValue();
            }
            AdnRecord generateAdnRecordWithNewTagByContentValues = generateAdnRecordWithNewTagByContentValues(i, i2, contentValues);
            AdnRecordCache adnRecordCache = this.mAdnCache;
            if (adnRecordCache != null) {
                adnRecordCache.updateAdnByIndex(i, generateAdnRecordWithNewTagByContentValues, i2, str, obtainMessage);
                waitForResult(request);
                return ((Boolean) request.mResult).booleanValue();
            }
            loge("Failure while trying to update by index due to uninitialised adncache");
            return false;
        }
    }

    public int[] getAdnRecordsSize(int i) {
        logd("getAdnRecordsSize: efid=" + i);
        checkThread();
        Request request = new Request();
        synchronized (request) {
            Message obtainMessage = this.mBaseHandler.obtainMessage(1, request);
            IccFileHandler iccFileHandler = this.mPhone.getIccFileHandler();
            if (iccFileHandler != null) {
                iccFileHandler.getEFLinearRecordSize(i, obtainMessage);
                waitForResult(request);
            }
        }
        Object obj = request.mResult;
        return obj == null ? new int[3] : (int[]) obj;
    }

    public List<AdnRecord> getAdnRecordsInEf(int i) {
        if (this.mPhone.getContext().checkCallingOrSelfPermission("android.permission.READ_CONTACTS") != 0) {
            throw new SecurityException("Requires android.permission.READ_CONTACTS permission");
        }
        int updateEfForIccType = updateEfForIccType(i);
        logd("getAdnRecordsInEF: efid=0x" + Integer.toHexString(updateEfForIccType).toUpperCase(Locale.ROOT));
        checkThread();
        Request request = new Request();
        synchronized (request) {
            Message obtainMessage = this.mBaseHandler.obtainMessage(2, request);
            if (usesPbCache(updateEfForIccType)) {
                this.mSimPbRecordCache.requestLoadAllPbRecords(obtainMessage);
                waitForResult(request);
                return (List) request.mResult;
            }
            AdnRecordCache adnRecordCache = this.mAdnCache;
            if (adnRecordCache != null) {
                adnRecordCache.requestLoadAllAdnLike(updateEfForIccType, adnRecordCache.extensionEfForEf(updateEfForIccType), obtainMessage);
                waitForResult(request);
                return (List) request.mResult;
            }
            loge("Failure while trying to load from SIM due to uninitialised adncache");
            return null;
        }
    }

    @UnsupportedAppUsage
    protected void checkThread() {
        if (this.mBaseHandler.getLooper().equals(Looper.myLooper())) {
            loge("query() called on the main UI thread!");
            throw new IllegalStateException("You cannot call query on this provder from the main UI thread.");
        }
    }

    protected void waitForResult(Request request) {
        synchronized (request) {
            while (!request.mStatus.get()) {
                try {
                    request.wait();
                } catch (InterruptedException unused) {
                    logd("interrupted while trying to update by search");
                }
            }
        }
    }

    @UnsupportedAppUsage
    private int updateEfForIccType(int i) {
        return (i == 28474 && this.mPhone.getCurrentUiccAppType() == IccCardApplicationStatus.AppType.APPTYPE_USIM) ? IccConstants.EF_PBR : i;
    }

    private String[] getEmailStringArray(String str) {
        if (str != null) {
            return str.split(",");
        }
        return null;
    }

    private String[] getAnrStringArray(String str) {
        if (str != null) {
            return str.split(":");
        }
        return null;
    }

    public AdnCapacity getAdnRecordsCapacity() {
        logd("getAdnRecordsCapacity");
        if (this.mPhone.getContext().checkCallingOrSelfPermission("android.permission.READ_CONTACTS") != 0) {
            throw new SecurityException("Requires android.permission.READ_CONTACTS permission");
        }
        int phoneId = this.mPhone.getPhoneId();
        UiccProfile uiccProfileForPhone = UiccController.getInstance().getUiccProfileForPhone(phoneId);
        if (uiccProfileForPhone != null) {
            IccCardConstants.State state = uiccProfileForPhone.getState();
            if (state == IccCardConstants.State.READY || state == IccCardConstants.State.LOADED) {
                checkThread();
                AdnCapacity adnCapacity = this.mSimPbRecordCache.isEnabled() ? this.mSimPbRecordCache.getAdnCapacity() : null;
                if (adnCapacity == null) {
                    loge("Adn capacity is null");
                    return null;
                }
                logd("getAdnRecordsCapacity on slot " + phoneId + ": max adn=" + adnCapacity.getMaxAdnCount() + ", used adn=" + adnCapacity.getUsedAdnCount() + ", max email=" + adnCapacity.getMaxEmailCount() + ", used email=" + adnCapacity.getUsedEmailCount() + ", max anr=" + adnCapacity.getMaxAnrCount() + ", used anr=" + adnCapacity.getUsedAnrCount() + ", max name length=" + adnCapacity.getMaxNameLength() + ", max number length =" + adnCapacity.getMaxNumberLength() + ", max email length =" + adnCapacity.getMaxEmailLength() + ", max anr length =" + adnCapacity.getMaxAnrLength());
                return adnCapacity;
            }
            logd("No UICC when getAdnRecordsCapacity.");
        } else {
            logd("sim state is not ready when getAdnRecordsCapacity.");
        }
        return null;
    }

    private boolean usesPbCache(int i) {
        return this.mSimPbRecordCache.isEnabled() && (i == 20272 || i == 28474);
    }
}
