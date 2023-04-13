package com.android.internal.telephony.uicc;

import android.compat.annotation.UnsupportedAppUsage;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.gsm.UsimPhoneBookManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
/* loaded from: classes.dex */
public class AdnRecordCache extends Handler implements IccConstants {
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private IccFileHandler mFh;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private UsimPhoneBookManager mUsimPhoneBookManager;
    SparseArray<ArrayList<AdnRecord>> mAdnLikeFiles = new SparseArray<>();
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    SparseArray<ArrayList<Message>> mAdnLikeWaiters = new SparseArray<>();
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    SparseArray<Message> mUserWriteResponse = new SparseArray<>();

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public int extensionEfForEf(int i) {
        if (i != 20272) {
            return i != 28480 ? i != 28489 ? i != 28615 ? i != 28474 ? i != 28475 ? (i & 20479) == i ? 0 : -1 : IccConstants.EF_EXT2 : IccConstants.EF_EXT1 : IccConstants.EF_EXT6 : IccConstants.EF_EXT3 : IccConstants.EF_EXT1;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AdnRecordCache(IccFileHandler iccFileHandler) {
        this.mFh = iccFileHandler;
        this.mUsimPhoneBookManager = new UsimPhoneBookManager(iccFileHandler, this);
    }

    public AdnRecordCache(IccFileHandler iccFileHandler, UsimPhoneBookManager usimPhoneBookManager) {
        this.mFh = iccFileHandler;
        this.mUsimPhoneBookManager = usimPhoneBookManager;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void reset() {
        this.mAdnLikeFiles.clear();
        this.mUsimPhoneBookManager.reset();
        clearWaiters();
        clearUserWriters();
    }

    private void clearWaiters() {
        int size = this.mAdnLikeWaiters.size();
        for (int i = 0; i < size; i++) {
            notifyWaiters(this.mAdnLikeWaiters.valueAt(i), new AsyncResult((Object) null, (Object) null, new RuntimeException("AdnCache reset")));
        }
        this.mAdnLikeWaiters.clear();
    }

    private void clearUserWriters() {
        int size = this.mUserWriteResponse.size();
        for (int i = 0; i < size; i++) {
            sendErrorResponse(this.mUserWriteResponse.valueAt(i), "AdnCace reset");
        }
        this.mUserWriteResponse.clear();
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public ArrayList<AdnRecord> getRecordsIfLoaded(int i) {
        return this.mAdnLikeFiles.get(i);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private void sendErrorResponse(Message message, String str) {
        if (message != null) {
            AsyncResult.forMessage(message).exception = new RuntimeException(str);
            message.sendToTarget();
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void updateAdnByIndex(int i, AdnRecord adnRecord, int i2, String str, Message message) {
        int extensionEfForEf = extensionEfForEf(i);
        if (extensionEfForEf < 0) {
            sendErrorResponse(message, "EF is not known ADN-like EF:0x" + Integer.toHexString(i).toUpperCase(Locale.ROOT));
        } else if (this.mUserWriteResponse.get(i) != null) {
            sendErrorResponse(message, "Have pending update for EF:0x" + Integer.toHexString(i).toUpperCase(Locale.ROOT));
        } else {
            this.mUserWriteResponse.put(i, message);
            new AdnRecordLoader(this.mFh).updateEF(adnRecord, i, extensionEfForEf, i2, str, obtainMessage(2, i, i2, adnRecord));
        }
    }

    public void updateAdnBySearch(int i, AdnRecord adnRecord, AdnRecord adnRecord2, String str, Message message) {
        ArrayList<AdnRecord> recordsIfLoaded;
        int i2;
        int extensionEfForEf = extensionEfForEf(i);
        if (extensionEfForEf < 0) {
            sendErrorResponse(message, "EF is not known ADN-like EF:0x" + Integer.toHexString(i).toUpperCase(Locale.ROOT));
            return;
        }
        if (i == 20272) {
            recordsIfLoaded = this.mUsimPhoneBookManager.loadEfFilesFromUsim();
        } else {
            recordsIfLoaded = getRecordsIfLoaded(i);
        }
        if (recordsIfLoaded == null) {
            sendErrorResponse(message, "Adn list not exist for EF:0x" + Integer.toHexString(i).toUpperCase(Locale.ROOT));
            return;
        }
        Iterator<AdnRecord> it = recordsIfLoaded.iterator();
        int i3 = 1;
        while (true) {
            if (!it.hasNext()) {
                i3 = -1;
                break;
            } else if (adnRecord.isEqual(it.next())) {
                break;
            } else {
                i3++;
            }
        }
        if (i3 == -1) {
            sendErrorResponse(message, "Adn record don't exist for " + adnRecord);
            return;
        }
        if (i == 20272) {
            AdnRecord adnRecord3 = recordsIfLoaded.get(i3 - 1);
            i2 = adnRecord3.mEfid;
            extensionEfForEf = adnRecord3.mExtRecord;
            int i4 = adnRecord3.mRecordNumber;
            adnRecord2.mEfid = i2;
            adnRecord2.mExtRecord = extensionEfForEf;
            adnRecord2.mRecordNumber = i4;
            i3 = i4;
        } else {
            i2 = i;
        }
        if (this.mUserWriteResponse.get(i2) != null) {
            sendErrorResponse(message, "Have pending update for EF:0x" + Integer.toHexString(i2).toUpperCase(Locale.ROOT));
            return;
        }
        this.mUserWriteResponse.put(i2, message);
        new AdnRecordLoader(this.mFh).updateEF(adnRecord2, i2, extensionEfForEf, i3, str, obtainMessage(2, i2, i3, adnRecord2));
    }

    public void requestLoadAllAdnLike(int i, int i2, Message message) {
        ArrayList<AdnRecord> recordsIfLoaded;
        if (i == 20272) {
            recordsIfLoaded = this.mUsimPhoneBookManager.loadEfFilesFromUsim();
        } else {
            recordsIfLoaded = getRecordsIfLoaded(i);
        }
        if (recordsIfLoaded != null) {
            if (message != null) {
                AsyncResult.forMessage(message).result = recordsIfLoaded;
                message.sendToTarget();
                return;
            }
            return;
        }
        ArrayList<Message> arrayList = this.mAdnLikeWaiters.get(i);
        if (arrayList != null) {
            arrayList.add(message);
            return;
        }
        ArrayList<Message> arrayList2 = new ArrayList<>();
        arrayList2.add(message);
        this.mAdnLikeWaiters.put(i, arrayList2);
        if (i2 >= 0) {
            new AdnRecordLoader(this.mFh).loadAllFromEF(i, i2, obtainMessage(1, i, 0));
        } else if (message != null) {
            AsyncResult forMessage = AsyncResult.forMessage(message);
            forMessage.exception = new RuntimeException("EF is not known ADN-like EF:0x" + Integer.toHexString(i).toUpperCase(Locale.ROOT));
            message.sendToTarget();
        }
    }

    private void notifyWaiters(ArrayList<Message> arrayList, AsyncResult asyncResult) {
        if (arrayList == null) {
            return;
        }
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            Message message = arrayList.get(i);
            AsyncResult.forMessage(message, asyncResult.result, asyncResult.exception);
            message.sendToTarget();
        }
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i = message.what;
        if (i == 1) {
            AsyncResult asyncResult = (AsyncResult) message.obj;
            int i2 = message.arg1;
            ArrayList<Message> arrayList = this.mAdnLikeWaiters.get(i2);
            this.mAdnLikeWaiters.delete(i2);
            if (asyncResult.exception == null) {
                this.mAdnLikeFiles.put(i2, (ArrayList) asyncResult.result);
            }
            notifyWaiters(arrayList, asyncResult);
        } else if (i != 2) {
        } else {
            AsyncResult asyncResult2 = (AsyncResult) message.obj;
            int i3 = message.arg1;
            int i4 = message.arg2;
            AdnRecord adnRecord = (AdnRecord) asyncResult2.userObj;
            if (asyncResult2.exception == null) {
                this.mAdnLikeFiles.get(i3).set(i4 - 1, adnRecord);
                this.mUsimPhoneBookManager.invalidateCache();
            }
            Message message2 = this.mUserWriteResponse.get(i3);
            this.mUserWriteResponse.delete(i3);
            if (message2 != null) {
                AsyncResult.forMessage(message2, (Object) null, asyncResult2.exception);
                message2.sendToTarget();
            }
        }
    }

    @VisibleForTesting
    protected void setAdnLikeWriters(int i, ArrayList<Message> arrayList) {
        this.mAdnLikeWaiters.put(IccConstants.EF_MBDN, arrayList);
    }

    @VisibleForTesting
    protected void setAdnLikeFiles(int i, ArrayList<AdnRecord> arrayList) {
        this.mAdnLikeFiles.put(IccConstants.EF_MBDN, arrayList);
    }

    @VisibleForTesting
    protected void setUserWriteResponse(int i, Message message) {
        this.mUserWriteResponse.put(IccConstants.EF_MBDN, message);
    }

    @VisibleForTesting
    protected UsimPhoneBookManager getUsimPhoneBookManager() {
        return this.mUsimPhoneBookManager;
    }
}
