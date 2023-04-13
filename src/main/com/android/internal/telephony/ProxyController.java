package com.android.internal.telephony;

import android.compat.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.PowerManager;
import android.telephony.RadioAccessFamily;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.CommandException;
import com.android.internal.telephony.data.PhoneSwitcher;
import com.android.telephony.Rlog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes.dex */
public class ProxyController {
    @VisibleForTesting
    public static final int EVENT_MULTI_SIM_CONFIG_CHANGED = 6;
    @VisibleForTesting
    static final int EVENT_START_RC_RESPONSE = 2;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private static ProxyController sProxyController;
    private Context mContext;
    private String[] mCurrentLogicalModemIds;
    private String[] mNewLogicalModemIds;
    private int[] mNewRadioAccessFamily;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private int[] mOldRadioAccessFamily;
    private PhoneSubInfoController mPhoneSubInfoController;
    private PhoneSwitcher mPhoneSwitcher;
    private Phone[] mPhones;
    private int mRadioAccessFamilyStatusCounter;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private int mRadioCapabilitySessionId;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private int[] mSetRadioAccessFamilyStatus;
    private SmsController mSmsController;
    private UiccPhoneBookController mUiccPhoneBookController;
    PowerManager.WakeLock mWakeLock;
    private boolean mTransactionFailed = false;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private AtomicInteger mUniqueIdGenerator = new AtomicInteger(new Random().nextInt());
    @VisibleForTesting
    public final Handler mHandler = new Handler() { // from class: com.android.internal.telephony.ProxyController.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            ProxyController proxyController = ProxyController.this;
            proxyController.logd("handleMessage msg.what=" + message.what);
            switch (message.what) {
                case 1:
                    ProxyController.this.onNotificationRadioCapabilityChanged(message);
                    return;
                case 2:
                    ProxyController.this.onStartRadioCapabilityResponse(message);
                    return;
                case 3:
                    ProxyController.this.onApplyRadioCapabilityResponse(message);
                    return;
                case 4:
                    ProxyController.this.onFinishRadioCapabilityResponse(message);
                    return;
                case 5:
                    ProxyController.this.onTimeoutRadioCapability(message);
                    return;
                case 6:
                    ProxyController.this.onMultiSimConfigChanged();
                    return;
                default:
                    return;
            }
        }
    };

    public static ProxyController getInstance(Context context) {
        if (sProxyController == null) {
            sProxyController = new ProxyController(context);
        }
        return sProxyController;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public static ProxyController getInstance() {
        return sProxyController;
    }

    private ProxyController(Context context) {
        int i = 0;
        logd("Constructor - Enter");
        this.mContext = context;
        this.mPhones = PhoneFactory.getPhones();
        this.mPhoneSwitcher = PhoneSwitcher.getInstance();
        this.mUiccPhoneBookController = new UiccPhoneBookController();
        this.mPhoneSubInfoController = new PhoneSubInfoController(this.mContext);
        this.mSmsController = new SmsController(this.mContext);
        Phone[] phoneArr = this.mPhones;
        this.mSetRadioAccessFamilyStatus = new int[phoneArr.length];
        this.mNewRadioAccessFamily = new int[phoneArr.length];
        this.mOldRadioAccessFamily = new int[phoneArr.length];
        this.mCurrentLogicalModemIds = new String[phoneArr.length];
        this.mNewLogicalModemIds = new String[phoneArr.length];
        PowerManager.WakeLock newWakeLock = ((PowerManager) context.getSystemService("power")).newWakeLock(1, "ProxyController");
        this.mWakeLock = newWakeLock;
        newWakeLock.setReferenceCounted(false);
        clearTransaction();
        while (true) {
            Phone[] phoneArr2 = this.mPhones;
            if (i < phoneArr2.length) {
                phoneArr2[i].registerForRadioCapabilityChanged(this.mHandler, 1, null);
                i++;
            } else {
                PhoneConfigurationManager.registerForMultiSimConfigChange(this.mHandler, 6, null);
                logd("Constructor - Exit");
                return;
            }
        }
    }

    public int getRadioAccessFamily(int i) {
        Phone[] phoneArr = this.mPhones;
        if (i >= phoneArr.length) {
            return 0;
        }
        return phoneArr[i].getRadioAccessFamily();
    }

    public boolean setRadioCapability(RadioAccessFamily[] radioAccessFamilyArr) {
        if (radioAccessFamilyArr.length != this.mPhones.length) {
            return false;
        }
        synchronized (this.mSetRadioAccessFamilyStatus) {
            for (int i = 0; i < this.mPhones.length; i++) {
                if (this.mSetRadioAccessFamilyStatus[i] != 0) {
                    loge("setRadioCapability: Phone[" + i + "] is not idle. Rejecting request.");
                    return false;
                }
            }
            boolean z = true;
            int i2 = 0;
            while (true) {
                Phone[] phoneArr = this.mPhones;
                if (i2 >= phoneArr.length) {
                    break;
                }
                if (phoneArr[i2].getRadioAccessFamily() != radioAccessFamilyArr[i2].getRadioAccessFamily()) {
                    z = false;
                }
                i2++;
            }
            if (z) {
                logd("setRadioCapability: Already in requested configuration, nothing to do.");
                return true;
            }
            clearTransaction();
            this.mWakeLock.acquire();
            return doSetRadioCapabilities(radioAccessFamilyArr);
        }
    }

    public SmsController getSmsController() {
        return this.mSmsController;
    }

    private boolean doSetRadioCapabilities(RadioAccessFamily[] radioAccessFamilyArr) {
        int andIncrement = this.mUniqueIdGenerator.getAndIncrement();
        this.mRadioCapabilitySessionId = andIncrement;
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(5, andIncrement, 0), 45000L);
        synchronized (this.mSetRadioAccessFamilyStatus) {
            logd("setRadioCapability: new request session id=" + this.mRadioCapabilitySessionId);
            resetRadioAccessFamilyStatusCounter();
            for (int i = 0; i < radioAccessFamilyArr.length; i++) {
                int phoneId = radioAccessFamilyArr[i].getPhoneId();
                logd("setRadioCapability: phoneId=" + phoneId + " status=STARTING");
                this.mSetRadioAccessFamilyStatus[phoneId] = 1;
                this.mOldRadioAccessFamily[phoneId] = this.mPhones[phoneId].getRadioAccessFamily();
                int radioAccessFamily = radioAccessFamilyArr[i].getRadioAccessFamily();
                this.mNewRadioAccessFamily[phoneId] = radioAccessFamily;
                this.mCurrentLogicalModemIds[phoneId] = this.mPhones[phoneId].getModemUuId();
                this.mNewLogicalModemIds[phoneId] = getLogicalModemIdFromRaf(radioAccessFamily);
                logd("setRadioCapability: mOldRadioAccessFamily[" + phoneId + "]=" + this.mOldRadioAccessFamily[phoneId]);
                logd("setRadioCapability: mNewRadioAccessFamily[" + phoneId + "]=" + this.mNewRadioAccessFamily[phoneId]);
                sendRadioCapabilityRequest(phoneId, this.mRadioCapabilitySessionId, 1, this.mOldRadioAccessFamily[phoneId], this.mCurrentLogicalModemIds[phoneId], 0, 2);
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onMultiSimConfigChanged() {
        int length = this.mPhones.length;
        Phone[] phones = PhoneFactory.getPhones();
        this.mPhones = phones;
        this.mSetRadioAccessFamilyStatus = Arrays.copyOf(this.mSetRadioAccessFamilyStatus, phones.length);
        this.mNewRadioAccessFamily = Arrays.copyOf(this.mNewRadioAccessFamily, this.mPhones.length);
        this.mOldRadioAccessFamily = Arrays.copyOf(this.mOldRadioAccessFamily, this.mPhones.length);
        this.mCurrentLogicalModemIds = (String[]) Arrays.copyOf(this.mCurrentLogicalModemIds, this.mPhones.length);
        this.mNewLogicalModemIds = (String[]) Arrays.copyOf(this.mNewLogicalModemIds, this.mPhones.length);
        clearTransaction();
        while (true) {
            Phone[] phoneArr = this.mPhones;
            if (length >= phoneArr.length) {
                return;
            }
            phoneArr[length].registerForRadioCapabilityChanged(this.mHandler, 1, null);
            length++;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onStartRadioCapabilityResponse(Message message) {
        synchronized (this.mSetRadioAccessFamilyStatus) {
            AsyncResult asyncResult = (AsyncResult) message.obj;
            Throwable th = asyncResult.exception;
            boolean z = true;
            if (th != null) {
                boolean z2 = (th instanceof CommandException) && ((CommandException) th).getCommandError() == CommandException.Error.REQUEST_NOT_SUPPORTED;
                if (TelephonyManager.getDefault().getPhoneCount() == 1 || z2) {
                    logd("onStartRadioCapabilityResponse got exception=" + asyncResult.exception);
                    this.mRadioCapabilitySessionId = this.mUniqueIdGenerator.getAndIncrement();
                    this.mContext.sendBroadcast(new Intent("android.intent.action.ACTION_SET_RADIO_CAPABILITY_FAILED"));
                    clearTransaction();
                    return;
                }
            }
            RadioCapability radioCapability = (RadioCapability) ((AsyncResult) message.obj).result;
            if (radioCapability != null && radioCapability.getSession() == this.mRadioCapabilitySessionId) {
                this.mRadioAccessFamilyStatusCounter--;
                int phoneId = radioCapability.getPhoneId();
                if (asyncResult.exception != null) {
                    logd("onStartRadioCapabilityResponse: Error response session=" + radioCapability.getSession());
                    logd("onStartRadioCapabilityResponse: phoneId=" + phoneId + " status=FAIL");
                    this.mSetRadioAccessFamilyStatus[phoneId] = 5;
                    this.mTransactionFailed = true;
                } else {
                    logd("onStartRadioCapabilityResponse: phoneId=" + phoneId + " status=STARTED");
                    this.mSetRadioAccessFamilyStatus[phoneId] = 2;
                }
                if (this.mRadioAccessFamilyStatusCounter == 0) {
                    HashSet hashSet = new HashSet(this.mNewLogicalModemIds.length);
                    for (String str : this.mNewLogicalModemIds) {
                        if (!hashSet.add(str)) {
                            this.mTransactionFailed = true;
                            Log.wtf("ProxyController", "ERROR: sending down the same id for different phones");
                        }
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append("onStartRadioCapabilityResponse: success=");
                    if (this.mTransactionFailed) {
                        z = false;
                    }
                    sb.append(z);
                    logd(sb.toString());
                    if (this.mTransactionFailed) {
                        issueFinish(this.mRadioCapabilitySessionId);
                    } else {
                        resetRadioAccessFamilyStatusCounter();
                        for (int i = 0; i < this.mPhones.length; i++) {
                            sendRadioCapabilityRequest(i, this.mRadioCapabilitySessionId, 2, this.mNewRadioAccessFamily[i], this.mNewLogicalModemIds[i], 0, 3);
                            logd("onStartRadioCapabilityResponse: phoneId=" + i + " status=APPLYING");
                            this.mSetRadioAccessFamilyStatus[i] = 3;
                        }
                    }
                }
                return;
            }
            logd("onStartRadioCapabilityResponse: Ignore session=" + this.mRadioCapabilitySessionId + " rc=" + radioCapability);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onApplyRadioCapabilityResponse(Message message) {
        RadioCapability radioCapability = (RadioCapability) ((AsyncResult) message.obj).result;
        if (radioCapability == null || radioCapability.getSession() != this.mRadioCapabilitySessionId) {
            logd("onApplyRadioCapabilityResponse: Ignore session=" + this.mRadioCapabilitySessionId + " rc=" + radioCapability);
            return;
        }
        logd("onApplyRadioCapabilityResponse: rc=" + radioCapability);
        if (((AsyncResult) message.obj).exception != null) {
            synchronized (this.mSetRadioAccessFamilyStatus) {
                logd("onApplyRadioCapabilityResponse: Error response session=" + radioCapability.getSession());
                int phoneId = radioCapability.getPhoneId();
                logd("onApplyRadioCapabilityResponse: phoneId=" + phoneId + " status=FAIL");
                this.mSetRadioAccessFamilyStatus[phoneId] = 5;
                this.mTransactionFailed = true;
            }
            return;
        }
        logd("onApplyRadioCapabilityResponse: Valid start expecting notification rc=" + radioCapability);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:23:0x00ba A[Catch: all -> 0x00d7, TryCatch #0 {, blocks: (B:9:0x0017, B:11:0x0033, B:12:0x0051, B:14:0x0053, B:16:0x0060, B:19:0x0068, B:21:0x00b3, B:23:0x00ba, B:24:0x00d5, B:20:0x0093), top: B:31:0x0017 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void onNotificationRadioCapabilityChanged(Message message) {
        int i;
        RadioCapability radioCapability = (RadioCapability) ((AsyncResult) message.obj).result;
        if (radioCapability == null || radioCapability.getSession() != this.mRadioCapabilitySessionId) {
            logd("onNotificationRadioCapabilityChanged: Ignore session=" + this.mRadioCapabilitySessionId + " rc=" + radioCapability);
            return;
        }
        synchronized (this.mSetRadioAccessFamilyStatus) {
            logd("onNotificationRadioCapabilityChanged: rc=" + radioCapability);
            if (radioCapability.getSession() != this.mRadioCapabilitySessionId) {
                logd("onNotificationRadioCapabilityChanged: Ignore session=" + this.mRadioCapabilitySessionId + " rc=" + radioCapability);
                return;
            }
            int phoneId = radioCapability.getPhoneId();
            if (((AsyncResult) message.obj).exception == null && radioCapability.getStatus() != 2) {
                logd("onNotificationRadioCapabilityChanged: phoneId=" + phoneId + " status=SUCCESS");
                this.mSetRadioAccessFamilyStatus[phoneId] = 4;
                this.mPhoneSwitcher.onRadioCapChanged(phoneId);
                this.mPhones[phoneId].radioCapabilityUpdated(radioCapability, true);
                i = this.mRadioAccessFamilyStatusCounter - 1;
                this.mRadioAccessFamilyStatusCounter = i;
                if (i == 0) {
                    logd("onNotificationRadioCapabilityChanged: APPLY URC success=" + this.mTransactionFailed);
                    issueFinish(this.mRadioCapabilitySessionId);
                }
            }
            logd("onNotificationRadioCapabilityChanged: phoneId=" + phoneId + " status=FAIL");
            this.mSetRadioAccessFamilyStatus[phoneId] = 5;
            this.mTransactionFailed = true;
            i = this.mRadioAccessFamilyStatusCounter - 1;
            this.mRadioAccessFamilyStatusCounter = i;
            if (i == 0) {
            }
        }
    }

    void onFinishRadioCapabilityResponse(Message message) {
        RadioCapability radioCapability = (RadioCapability) ((AsyncResult) message.obj).result;
        if (radioCapability == null || radioCapability.getSession() != this.mRadioCapabilitySessionId) {
            logd("onFinishRadioCapabilityResponse: Ignore session=" + this.mRadioCapabilitySessionId + " rc=" + radioCapability);
            return;
        }
        synchronized (this.mSetRadioAccessFamilyStatus) {
            logd(" onFinishRadioCapabilityResponse mRadioAccessFamilyStatusCounter=" + this.mRadioAccessFamilyStatusCounter);
            int i = this.mRadioAccessFamilyStatusCounter + (-1);
            this.mRadioAccessFamilyStatusCounter = i;
            if (i == 0) {
                completeRadioCapabilityTransaction();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTimeoutRadioCapability(Message message) {
        if (message.arg1 != this.mRadioCapabilitySessionId) {
            logd("RadioCapability timeout: Ignore msg.arg1=" + message.arg1 + "!= mRadioCapabilitySessionId=" + this.mRadioCapabilitySessionId);
            return;
        }
        synchronized (this.mSetRadioAccessFamilyStatus) {
            for (int i = 0; i < this.mPhones.length; i++) {
                logd("RadioCapability timeout: mSetRadioAccessFamilyStatus[" + i + "]=" + this.mSetRadioAccessFamilyStatus[i]);
            }
            int andIncrement = this.mUniqueIdGenerator.getAndIncrement();
            this.mRadioCapabilitySessionId = andIncrement;
            this.mRadioAccessFamilyStatusCounter = 0;
            this.mTransactionFailed = true;
            issueFinish(andIncrement);
        }
    }

    private void issueFinish(int i) {
        String str;
        synchronized (this.mSetRadioAccessFamilyStatus) {
            for (int i2 = 0; i2 < this.mPhones.length; i2++) {
                logd("issueFinish: phoneId=" + i2 + " sessionId=" + i + " mTransactionFailed=" + this.mTransactionFailed);
                this.mRadioAccessFamilyStatusCounter = this.mRadioAccessFamilyStatusCounter + 1;
                boolean z = this.mTransactionFailed;
                int i3 = z ? this.mOldRadioAccessFamily[i2] : this.mNewRadioAccessFamily[i2];
                if (z) {
                    str = this.mCurrentLogicalModemIds[i2];
                } else {
                    str = this.mNewLogicalModemIds[i2];
                }
                sendRadioCapabilityRequest(i2, i, 4, i3, str, z ? 2 : 1, 4);
                if (this.mTransactionFailed) {
                    logd("issueFinish: phoneId: " + i2 + " status: FAIL");
                    this.mSetRadioAccessFamilyStatus[i2] = 5;
                }
            }
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private void completeRadioCapabilityTransaction() {
        Intent intent;
        StringBuilder sb = new StringBuilder();
        sb.append("onFinishRadioCapabilityResponse: success=");
        sb.append(!this.mTransactionFailed);
        logd(sb.toString());
        int i = 0;
        if (!this.mTransactionFailed) {
            ArrayList<? extends Parcelable> arrayList = new ArrayList<>();
            while (true) {
                Phone[] phoneArr = this.mPhones;
                if (i >= phoneArr.length) {
                    break;
                }
                int radioAccessFamily = phoneArr[i].getRadioAccessFamily();
                logd("radioAccessFamily[" + i + "]=" + radioAccessFamily);
                arrayList.add(new RadioAccessFamily(i, radioAccessFamily));
                i++;
            }
            intent = new Intent("android.intent.action.ACTION_SET_RADIO_CAPABILITY_DONE");
            intent.putParcelableArrayListExtra("rafs", arrayList);
            this.mRadioCapabilitySessionId = this.mUniqueIdGenerator.getAndIncrement();
            clearTransaction();
        } else {
            Intent intent2 = new Intent("android.intent.action.ACTION_SET_RADIO_CAPABILITY_FAILED");
            this.mTransactionFailed = false;
            RadioAccessFamily[] radioAccessFamilyArr = new RadioAccessFamily[this.mPhones.length];
            while (i < this.mPhones.length) {
                radioAccessFamilyArr[i] = new RadioAccessFamily(i, this.mOldRadioAccessFamily[i]);
                i++;
            }
            doSetRadioCapabilities(radioAccessFamilyArr);
            intent = intent2;
        }
        this.mContext.sendBroadcast(intent, "android.permission.READ_PHONE_STATE");
    }

    private void clearTransaction() {
        logd("clearTransaction");
        synchronized (this.mSetRadioAccessFamilyStatus) {
            for (int i = 0; i < this.mPhones.length; i++) {
                logd("clearTransaction: phoneId=" + i + " status=IDLE");
                this.mSetRadioAccessFamilyStatus[i] = 0;
                this.mOldRadioAccessFamily[i] = 0;
                this.mNewRadioAccessFamily[i] = 0;
                this.mTransactionFailed = false;
            }
            if (isWakeLockHeld()) {
                this.mWakeLock.release();
            }
        }
    }

    @VisibleForTesting
    public boolean isWakeLockHeld() {
        boolean isHeld;
        synchronized (this.mSetRadioAccessFamilyStatus) {
            isHeld = this.mWakeLock.isHeld();
        }
        return isHeld;
    }

    private void resetRadioAccessFamilyStatusCounter() {
        this.mRadioAccessFamilyStatusCounter = this.mPhones.length;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private void sendRadioCapabilityRequest(int i, int i2, int i3, int i4, String str, int i5, int i6) {
        this.mPhones[i].setRadioCapability(new RadioCapability(i, i2, i3, i4, str, i5), this.mHandler.obtainMessage(i6));
    }

    public int getMaxRafSupported() {
        int[] iArr = new int[this.mPhones.length];
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        while (true) {
            Phone[] phoneArr = this.mPhones;
            if (i >= phoneArr.length) {
                return i2;
            }
            int bitCount = Integer.bitCount(phoneArr[i].getRadioAccessFamily());
            iArr[i] = bitCount;
            if (i3 < bitCount) {
                i2 = this.mPhones[i].getRadioAccessFamily();
                i3 = bitCount;
            }
            i++;
        }
    }

    public int getMinRafSupported() {
        int[] iArr = new int[this.mPhones.length];
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        while (true) {
            Phone[] phoneArr = this.mPhones;
            if (i >= phoneArr.length) {
                return i2;
            }
            int bitCount = Integer.bitCount(phoneArr[i].getRadioAccessFamily());
            iArr[i] = bitCount;
            if (i3 == 0 || i3 > bitCount) {
                i2 = this.mPhones[i].getRadioAccessFamily();
                i3 = bitCount;
            }
            i++;
        }
    }

    private String getLogicalModemIdFromRaf(int i) {
        int i2 = 0;
        while (true) {
            Phone[] phoneArr = this.mPhones;
            if (i2 >= phoneArr.length) {
                return null;
            }
            if (phoneArr[i2].getRadioAccessFamily() == i) {
                return this.mPhones[i2].getModemUuId();
            }
            i2++;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void logd(String str) {
        Rlog.d("ProxyController", str);
    }

    private void loge(String str) {
        Rlog.e("ProxyController", str);
    }
}
