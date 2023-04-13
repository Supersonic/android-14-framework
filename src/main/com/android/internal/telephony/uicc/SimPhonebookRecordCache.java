package com.android.internal.telephony.uicc;

import android.content.Context;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Message;
import android.telephony.Rlog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.CommandsInterface;
import com.android.internal.telephony.RadioInterfaceCapabilityController;
import com.android.internal.telephony.uicc.SimPhonebookRecord;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class SimPhonebookRecordCache extends Handler {
    @VisibleForTesting
    static final boolean ENABLE_INFLATE_WITH_EMPTY_RECORDS = true;
    private final CommandsInterface mCi;
    private Context mContext;
    private int mPhoneId;
    private String LOG_TAG = "SimPhonebookRecordCache";
    private AtomicReference<AdnCapacity> mAdnCapacity = new AtomicReference<>(null);
    private Object mReadLock = new Object();
    private final ConcurrentSkipListMap<Integer, AdnRecord> mSimPbRecords = new ConcurrentSkipListMap<>();
    private final List<UpdateRequest> mUpdateRequests = Collections.synchronizedList(new ArrayList());
    private AtomicBoolean mIsCacheInvalidated = new AtomicBoolean(false);
    private AtomicBoolean mIsRecordLoading = new AtomicBoolean(false);
    private AtomicBoolean mIsInRetry = new AtomicBoolean(false);
    private AtomicBoolean mIsInitialized = new AtomicBoolean(false);
    ArrayList<Message> mAdnLoadingWaiters = new ArrayList<>();
    boolean mIsUpdateDone = false;

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ AdnRecord lambda$populateAdnRecords$1(AdnRecord adnRecord) {
        return adnRecord;
    }

    @VisibleForTesting
    public void clear() {
    }

    public SimPhonebookRecordCache(Context context, int i, CommandsInterface commandsInterface) {
        this.mCi = commandsInterface;
        this.mPhoneId = i;
        this.mContext = context;
        this.LOG_TAG += "[" + i + "]";
        commandsInterface.registerForSimPhonebookChanged(this, 1, null);
        commandsInterface.registerForIccRefresh(this, 6, null);
        commandsInterface.registerForSimPhonebookRecordsReceived(this, 2, null);
    }

    public boolean isEnabled() {
        return this.mIsInitialized.get() || RadioInterfaceCapabilityController.getInstance().getCapabilities().contains("CAPABILITY_SIM_PHONEBOOK_IN_MODEM");
    }

    public void dispose() {
        reset();
        this.mCi.unregisterForSimPhonebookChanged(this);
        this.mCi.unregisterForIccRefresh(this);
        this.mCi.unregisterForSimPhonebookRecordsReceived(this);
    }

    private void reset() {
        this.mAdnCapacity.set(null);
        this.mSimPbRecords.clear();
        this.mIsCacheInvalidated.set(false);
        this.mIsRecordLoading.set(false);
        this.mIsInRetry.set(false);
        this.mIsInitialized.set(false);
        this.mIsUpdateDone = false;
    }

    private void sendErrorResponse(Message message, String str) {
        if (message != null) {
            AsyncResult.forMessage(message).exception = new RuntimeException(str);
            message.sendToTarget();
        }
    }

    private void notifyAndClearWaiters() {
        synchronized (this.mReadLock) {
            Iterator<Message> it = this.mAdnLoadingWaiters.iterator();
            while (it.hasNext()) {
                Message next = it.next();
                if (next != null) {
                    AsyncResult.forMessage(next, new ArrayList(this.mSimPbRecords.values()), (Throwable) null);
                    next.sendToTarget();
                }
            }
            this.mAdnLoadingWaiters.clear();
        }
    }

    private void sendResponsesToWaitersWithError() {
        synchronized (this.mReadLock) {
            this.mReadLock.notify();
            Iterator<Message> it = this.mAdnLoadingWaiters.iterator();
            while (it.hasNext()) {
                sendErrorResponse(it.next(), "Query adn record failed");
            }
            this.mAdnLoadingWaiters.clear();
        }
    }

    private void getSimPhonebookCapacity() {
        logd("Start to getSimPhonebookCapacity");
        this.mCi.getSimPhonebookCapacity(obtainMessage(4));
    }

    public AdnCapacity getAdnCapacity() {
        return this.mAdnCapacity.get();
    }

    private void fillCache() {
        synchronized (this.mReadLock) {
            fillCacheWithoutWaiting();
            try {
                this.mReadLock.wait();
            } catch (InterruptedException unused) {
                loge("Interrupted Exception in queryAdnRecord");
            }
        }
    }

    private void fillCacheWithoutWaiting() {
        logd("Start to queryAdnRecord");
        if (this.mIsRecordLoading.compareAndSet(false, true)) {
            this.mCi.getSimPhonebookRecords(obtainMessage(3));
        } else {
            logd("The loading is ongoing");
        }
    }

    public void requestLoadAllPbRecords(Message message) {
        if (message == null && !this.mIsInitialized.get()) {
            logd("Try to enforce flushing cache");
            fillCacheWithoutWaiting();
            return;
        }
        synchronized (this.mReadLock) {
            this.mAdnLoadingWaiters.add(message);
            int size = this.mAdnLoadingWaiters.size();
            boolean isAdnCapacityInvalid = isAdnCapacityInvalid();
            if (isAdnCapacityInvalid) {
                getSimPhonebookCapacity();
            }
            if (size <= 1 && !this.mIsInRetry.get() && this.mIsInitialized.get() && !isAdnCapacityInvalid) {
                if (!this.mIsRecordLoading.get() && !this.mIsInRetry.get()) {
                    logd("ADN cache has already filled in");
                    if (!this.mIsCacheInvalidated.get()) {
                        notifyAndClearWaiters();
                        return;
                    }
                }
                fillCache();
                return;
            }
            logd("Add to the pending list as pending size = " + size + " is retrying = " + this.mIsInRetry.get() + " IsInitialized = " + this.mIsInitialized.get());
        }
    }

    private boolean isAdnCapacityInvalid() {
        return getAdnCapacity() == null || !getAdnCapacity().isSimValid();
    }

    @VisibleForTesting
    public boolean isLoading() {
        return this.mIsRecordLoading.get();
    }

    @VisibleForTesting
    public List<AdnRecord> getAdnRecords() {
        return (List) this.mSimPbRecords.values().stream().collect(Collectors.toList());
    }

    private void notifyAdnLoadingWaiters() {
        synchronized (this.mReadLock) {
            this.mReadLock.notify();
        }
        notifyAndClearWaiters();
    }

    public void updateSimPbAdnByRecordId(int i, AdnRecord adnRecord, Message message) {
        if (adnRecord == null) {
            sendErrorResponse(message, "There is an invalid new Adn for update");
        } else if (!this.mSimPbRecords.containsKey(Integer.valueOf(i))) {
            sendErrorResponse(message, "There is an invalid old Adn for update");
        } else {
            updateSimPhonebookByNewAdn(i, adnRecord, message);
        }
    }

    public void updateSimPbAdnBySearch(AdnRecord adnRecord, AdnRecord adnRecord2, Message message) {
        int i;
        if (adnRecord2 == null) {
            sendErrorResponse(message, "There is an invalid new Adn for update");
            return;
        }
        if (adnRecord != null && !adnRecord.isEmpty()) {
            for (AdnRecord adnRecord3 : this.mSimPbRecords.values()) {
                if (adnRecord.isEqual(adnRecord3)) {
                    i = adnRecord3.getRecId();
                    break;
                }
            }
        }
        i = -1;
        if (i == -1 && this.mAdnCapacity.get() != null && this.mAdnCapacity.get().isSimFull()) {
            sendErrorResponse(message, "SIM Phonebook record is full");
        } else {
            updateSimPhonebookByNewAdn(i, adnRecord2, message);
        }
    }

    private void updateSimPhonebookByNewAdn(int i, AdnRecord adnRecord, Message message) {
        logd("update sim contact for record ID = " + i);
        UpdateRequest updateRequest = new UpdateRequest(i, adnRecord, new SimPhonebookRecord.Builder().setRecordId(i == -1 ? 0 : i).setAlphaTag(adnRecord.getAlphaTag()).setNumber(adnRecord.getNumber()).setEmails(adnRecord.getEmails()).setAdditionalNumbers(adnRecord.getAdditionalNumbers()).build(), message);
        this.mUpdateRequests.add(updateRequest);
        boolean isAdnCapacityInvalid = isAdnCapacityInvalid();
        if (isAdnCapacityInvalid) {
            getSimPhonebookCapacity();
        }
        if (this.mIsRecordLoading.get() || this.mIsInRetry.get() || this.mUpdateRequests.size() > 1 || !this.mIsInitialized.get() || isAdnCapacityInvalid) {
            logd("It is pending on update as  mIsRecordLoading = " + this.mIsRecordLoading.get() + " mIsInRetry = " + this.mIsInRetry.get() + " pending size = " + this.mUpdateRequests.size() + " mIsInitialized = " + this.mIsInitialized.get());
            return;
        }
        updateSimPhonebook(updateRequest);
    }

    private void updateSimPhonebook(UpdateRequest updateRequest) {
        logd("update Sim phonebook");
        this.mCi.updateSimPhonebookRecord(updateRequest.phonebookRecord, obtainMessage(5, updateRequest));
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        switch (message.what) {
            case 1:
                logd("EVENT_PHONEBOOK_CHANGED");
                handlePhonebookChanged();
                return;
            case 2:
                logd("EVENT_PHONEBOOK_RECORDS_RECEIVED");
                AsyncResult asyncResult = (AsyncResult) message.obj;
                if (asyncResult.exception != null) {
                    loge("Unexpected exception happened");
                    asyncResult.result = null;
                }
                handlePhonebookRecordReceived((ReceivedPhonebookRecords) asyncResult.result);
                return;
            case 3:
                logd("EVENT_GET_PHONEBOOK_RECORDS_DONE");
                AsyncResult asyncResult2 = (AsyncResult) message.obj;
                if (asyncResult2 == null || asyncResult2.exception == null) {
                    return;
                }
                loge("Failed to gain phonebook records");
                invalidateSimPbCache();
                if (this.mIsInRetry.get()) {
                    return;
                }
                sendGettingPhonebookRecordsRetry(0);
                return;
            case 4:
                logd("EVENT_GET_PHONEBOOK_CAPACITY_DONE");
                AsyncResult asyncResult3 = (AsyncResult) message.obj;
                if (asyncResult3 != null && asyncResult3.exception == null) {
                    handlePhonebookCapacityChanged((AdnCapacity) asyncResult3.result);
                    return;
                }
                if (!isAdnCapacityInvalid()) {
                    this.mAdnCapacity.set(new AdnCapacity());
                }
                invalidateSimPbCache();
                return;
            case 5:
                logd("EVENT_UPDATE_PHONEBOOK_RECORD_DONE");
                handleUpdatePhonebookRecordDone((AsyncResult) message.obj);
                return;
            case 6:
                logd("EVENT_SIM_REFRESH");
                AsyncResult asyncResult4 = (AsyncResult) message.obj;
                if (asyncResult4.exception == null) {
                    handleSimRefresh((IccRefreshResponse) asyncResult4.result);
                    return;
                }
                logd("SIM refresh Exception: " + asyncResult4.exception);
                return;
            case 7:
                int i = message.arg1;
                logd("EVENT_GET_PHONEBOOK_RECORDS_RETRY cnt = " + i);
                if (i < 3) {
                    this.mIsRecordLoading.set(false);
                    fillCacheWithoutWaiting();
                    sendGettingPhonebookRecordsRetry(i + 1);
                    return;
                }
                responseToWaitersWithErrorOrSuccess(false);
                return;
            default:
                loge("Unexpected event: " + message.what);
                return;
        }
    }

    private void responseToWaitersWithErrorOrSuccess(boolean z) {
        logd("responseToWaitersWithErrorOrSuccess success = " + z);
        this.mIsRecordLoading.set(false);
        this.mIsInRetry.set(false);
        if (z) {
            notifyAdnLoadingWaiters();
        } else {
            sendResponsesToWaitersWithError();
        }
        tryFireUpdatePendingList();
    }

    private void handlePhonebookChanged() {
        if (this.mUpdateRequests.isEmpty()) {
            getSimPhonebookCapacity();
        } else {
            logd("Do nothing in the midst of multiple update");
        }
    }

    private void handlePhonebookCapacityChanged(AdnCapacity adnCapacity) {
        AdnCapacity adnCapacity2 = this.mAdnCapacity.get();
        if (adnCapacity == null) {
            adnCapacity = new AdnCapacity();
        }
        this.mAdnCapacity.set(adnCapacity);
        if (adnCapacity2 == null) {
            inflateWithEmptyRecords(adnCapacity);
            if (!adnCapacity.isSimEmpty()) {
                this.mIsCacheInvalidated.set(true);
                fillCacheWithoutWaiting();
            } else if (adnCapacity.isSimValid()) {
                notifyAdnLoadingWaiters();
                tryFireUpdatePendingList();
            } else {
                logd("ADN capacity is invalid");
            }
            this.mIsInitialized.set(true);
            return;
        }
        if (adnCapacity.isSimValid() && adnCapacity.isSimEmpty()) {
            this.mIsCacheInvalidated.set(false);
            notifyAdnLoadingWaiters();
            tryFireUpdatePendingList();
        } else if (!this.mIsUpdateDone && !adnCapacity.isSimEmpty()) {
            invalidateSimPbCache();
            fillCacheWithoutWaiting();
        }
        this.mIsUpdateDone = false;
    }

    private void inflateWithEmptyRecords(AdnCapacity adnCapacity) {
        logd("inflateWithEmptyRecords");
        if (adnCapacity == null || !this.mSimPbRecords.isEmpty()) {
            return;
        }
        for (int i = 1; i <= adnCapacity.getMaxAdnCount(); i++) {
            this.mSimPbRecords.putIfAbsent(Integer.valueOf(i), new AdnRecord(28474, i, null, null, null, null));
        }
    }

    private void handlePhonebookRecordReceived(ReceivedPhonebookRecords receivedPhonebookRecords) {
        if (receivedPhonebookRecords != null) {
            if (receivedPhonebookRecords.isOk()) {
                logd("Partial data is received");
                populateAdnRecords(receivedPhonebookRecords.getPhonebookRecords());
                return;
            } else if (receivedPhonebookRecords.isCompleted()) {
                logd("The whole loading process is finished");
                populateAdnRecords(receivedPhonebookRecords.getPhonebookRecords());
                this.mIsRecordLoading.set(false);
                this.mIsInRetry.set(false);
                this.mIsCacheInvalidated.set(false);
                notifyAdnLoadingWaiters();
                tryFireUpdatePendingList();
                return;
            } else if (receivedPhonebookRecords.isRetryNeeded() && !this.mIsInRetry.get()) {
                logd("Start to retry as aborted");
                sendGettingPhonebookRecordsRetry(0);
                return;
            } else {
                loge("Error happened");
                responseToWaitersWithErrorOrSuccess(true);
                return;
            }
        }
        loge("No records there");
        responseToWaitersWithErrorOrSuccess(true);
    }

    private void handleUpdatePhonebookRecordDone(AsyncResult asyncResult) {
        RuntimeException runtimeException;
        UpdateRequest updateRequest = (UpdateRequest) asyncResult.userObj;
        this.mIsUpdateDone = true;
        if (asyncResult.exception == null) {
            int i = updateRequest.myRecordId;
            AdnRecord adnRecord = updateRequest.adnRecord;
            int i2 = ((int[]) asyncResult.result)[0];
            logd("my record ID = " + i + " new record ID = " + i2);
            if (i == -1 || i == i2) {
                if (!adnRecord.isEmpty()) {
                    addOrChangeSimPbRecord(adnRecord, i2);
                } else {
                    deleteSimPbRecord(i2);
                }
                runtimeException = null;
            } else {
                runtimeException = new RuntimeException("The record ID for update doesn't match");
            }
        } else {
            runtimeException = new RuntimeException("Update adn record failed", asyncResult.exception);
        }
        if (this.mUpdateRequests.contains(updateRequest)) {
            this.mUpdateRequests.remove(updateRequest);
            updateRequest.responseResult(runtimeException);
        } else {
            loge("this update request isn't found");
        }
        tryFireUpdatePendingList();
    }

    private void tryFireUpdatePendingList() {
        if (this.mUpdateRequests.isEmpty()) {
            return;
        }
        updateSimPhonebook(this.mUpdateRequests.get(0));
    }

    private void handleSimRefresh(IccRefreshResponse iccRefreshResponse) {
        int i;
        if (iccRefreshResponse != null) {
            int i2 = iccRefreshResponse.refreshResult;
            if ((i2 == 0 && ((i = iccRefreshResponse.efId) == 20272 || i == 28474)) || i2 == 1) {
                invalidateSimPbCache();
                getSimPhonebookCapacity();
                return;
            }
            return;
        }
        logd("IccRefreshResponse received is null");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ AdnRecord lambda$populateAdnRecords$0(SimPhonebookRecord simPhonebookRecord) {
        return new AdnRecord(28474, simPhonebookRecord.getRecordId(), simPhonebookRecord.getAlphaTag(), simPhonebookRecord.getNumber(), simPhonebookRecord.getEmails(), simPhonebookRecord.getAdditionalNumbers());
    }

    private void populateAdnRecords(List<SimPhonebookRecord> list) {
        if (list != null) {
            this.mSimPbRecords.putAll((Map) list.stream().map(new Function() { // from class: com.android.internal.telephony.uicc.SimPhonebookRecordCache$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    AdnRecord lambda$populateAdnRecords$0;
                    lambda$populateAdnRecords$0 = SimPhonebookRecordCache.lambda$populateAdnRecords$0((SimPhonebookRecord) obj);
                    return lambda$populateAdnRecords$0;
                }
            }).collect(Collectors.toMap(new Function() { // from class: com.android.internal.telephony.uicc.SimPhonebookRecordCache$$ExternalSyntheticLambda1
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return Integer.valueOf(((AdnRecord) obj).getRecId());
                }
            }, new Function() { // from class: com.android.internal.telephony.uicc.SimPhonebookRecordCache$$ExternalSyntheticLambda2
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    AdnRecord lambda$populateAdnRecords$1;
                    lambda$populateAdnRecords$1 = SimPhonebookRecordCache.lambda$populateAdnRecords$1((AdnRecord) obj);
                    return lambda$populateAdnRecords$1;
                }
            })));
        }
    }

    private void sendGettingPhonebookRecordsRetry(int i) {
        if (hasMessages(7)) {
            removeMessages(7);
        }
        this.mIsInRetry.set(true);
        sendMessageDelayed(obtainMessage(7, 1, 0), 3000L);
    }

    private void addOrChangeSimPbRecord(AdnRecord adnRecord, int i) {
        logd("Record number for the added or changed ADN is " + i);
        adnRecord.setRecId(i);
        this.mSimPbRecords.replace(Integer.valueOf(i), adnRecord);
    }

    private void deleteSimPbRecord(int i) {
        logd("Record number for the deleted ADN is " + i);
        this.mSimPbRecords.replace(Integer.valueOf(i), new AdnRecord(28474, i, null, null, null, null));
    }

    private void invalidateSimPbCache() {
        logd("invalidateSimPbCache");
        this.mIsCacheInvalidated.set(true);
        this.mSimPbRecords.replaceAll(new BiFunction() { // from class: com.android.internal.telephony.uicc.SimPhonebookRecordCache$$ExternalSyntheticLambda3
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                AdnRecord lambda$invalidateSimPbCache$2;
                lambda$invalidateSimPbCache$2 = SimPhonebookRecordCache.lambda$invalidateSimPbCache$2((Integer) obj, (AdnRecord) obj2);
                return lambda$invalidateSimPbCache$2;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ AdnRecord lambda$invalidateSimPbCache$2(Integer num, AdnRecord adnRecord) {
        return new AdnRecord(28474, num.intValue(), null, null, null, null);
    }

    private void logd(String str) {
        Rlog.d(this.LOG_TAG, str);
    }

    private void loge(String str) {
        Rlog.e(this.LOG_TAG, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class UpdateRequest {
        private AdnRecord adnRecord;
        private int myRecordId;
        private SimPhonebookRecord phonebookRecord;
        private Message response;

        UpdateRequest(int i, AdnRecord adnRecord, SimPhonebookRecord simPhonebookRecord, Message message) {
            this.myRecordId = i;
            this.adnRecord = adnRecord;
            this.phonebookRecord = simPhonebookRecord;
            this.response = message;
        }

        void responseResult(Exception exc) {
            Message message = this.response;
            if (message != null) {
                AsyncResult.forMessage(message, (Object) null, exc);
                this.response.sendToTarget();
            }
        }
    }
}
