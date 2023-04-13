package com.android.internal.telephony;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.compat.CompatChanges;
import android.compat.annotation.UnsupportedAppUsage;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.AsyncResult;
import android.os.Binder;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.provider.Telephony;
import android.service.carrier.CarrierMessagingServiceWrapper;
import android.telephony.AnomalyReporter;
import android.telephony.CarrierConfigManager;
import android.telephony.PhoneNumberUtils;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.EventLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.CommandException;
import com.android.internal.telephony.GsmAlphabet;
import com.android.internal.telephony.SMSDispatcher;
import com.android.internal.telephony.SmsHeader;
import com.android.internal.telephony.SmsMessageBase;
import com.android.internal.telephony.cdma.SmsMessage;
import com.android.internal.telephony.cdma.sms.UserData;
import com.android.internal.telephony.gsm.SmsMessage;
import com.android.internal.telephony.metrics.SmsStats;
import com.android.internal.telephony.nano.TelephonyProto$TelephonyEvent;
import com.android.internal.telephony.subscription.SubscriptionInfoInternal;
import com.android.internal.telephony.subscription.SubscriptionManagerService;
import com.android.internal.telephony.uicc.IccRecords;
import com.android.internal.telephony.uicc.UiccCardApplication;
import com.android.internal.telephony.util.NetworkStackConstants;
import com.android.internal.telephony.util.TelephonyUtils;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes.dex */
public abstract class SMSDispatcher extends Handler {
    protected static final int EVENT_GET_IMS_SERVICE = 16;
    protected static final int EVENT_ICC_CHANGED = 15;
    protected static final int EVENT_NEW_ICC_SMS = 14;
    protected static final int EVENT_NEW_SMS_STATUS_REPORT = 10;
    protected static final int EVENT_RETRY_SMMA = 11;
    protected static final int EVENT_SEND_RETRY = 3;
    protected static final int EVENT_SEND_SMS_COMPLETE = 2;
    protected static final String MAP_KEY_DATA = "data";
    protected static final String MAP_KEY_DEST_ADDR = "destAddr";
    protected static final String MAP_KEY_DEST_PORT = "destPort";
    protected static final String MAP_KEY_PDU = "pdu";
    protected static final String MAP_KEY_SC_ADDR = "scAddr";
    protected static final String MAP_KEY_SMSC = "smsc";
    protected static final String MAP_KEY_TEXT = "text";
    protected static final int MAX_SEND_RETRIES = 3;
    @VisibleForTesting
    public static final int SEND_RETRY_DELAY = 2000;
    private static final UUID sAnomalyNoResponseFromCarrierMessagingService = UUID.fromString("279d9fbc-462d-4fc2-802c-bf21ddd9dd90");
    private static final UUID sAnomalyUnexpectedCallback = UUID.fromString("0103b6d2-ad07-4d86-9102-14341b9074ef");
    private static int sConcatenatedRef = new Random().nextInt(CallFailCause.RADIO_UPLINK_FAILURE);
    private final BroadcastReceiver mBroadcastReceiver;
    @VisibleForTesting
    public int mCarrierMessagingTimeout;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected final CommandsInterface mCi;
    @UnsupportedAppUsage
    protected final Context mContext;
    protected final LocalLog mLocalLog;
    private int mMessageRef;
    private int mPendingTrackerCount;
    @UnsupportedAppUsage
    protected Phone mPhone;
    private final AtomicInteger mPremiumSmsRule;
    protected boolean mRPSmmaRetried;
    @UnsupportedAppUsage
    protected final ContentResolver mResolver;
    private final SettingsObserver mSettingsObserver;
    protected boolean mSmsCapable;
    protected SmsDispatchersController mSmsDispatchersController;
    protected final LocalLog mSmsOutgoingErrorCodes;
    protected boolean mSmsSendDisabled;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected final TelephonyManager mTelephonyManager;

    /* JADX INFO: Access modifiers changed from: protected */
    public static int getNotInServiceError(int i) {
        return i == 3 ? 2 : 4;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected abstract GsmAlphabet.TextEncodingDetails calculateLength(CharSequence charSequence, boolean z);

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract String getFormat();

    protected int getMaxSmsRetryCount() {
        return 3;
    }

    protected int getSmsRetryDelayValue() {
        return 2000;
    }

    protected abstract SmsMessageBase.SubmitPduBase getSubmitPdu(String str, String str2, int i, byte[] bArr, boolean z);

    protected abstract SmsMessageBase.SubmitPduBase getSubmitPdu(String str, String str2, int i, byte[] bArr, boolean z, int i2);

    protected abstract SmsMessageBase.SubmitPduBase getSubmitPdu(String str, String str2, String str3, boolean z, SmsHeader smsHeader, int i, int i2);

    protected abstract SmsMessageBase.SubmitPduBase getSubmitPdu(String str, String str2, String str3, boolean z, SmsHeader smsHeader, int i, int i2, int i3);

    /* JADX INFO: Access modifiers changed from: protected */
    @UnsupportedAppUsage
    public abstract void sendSms(SmsTracker smsTracker);

    protected abstract boolean shouldBlockSmsForEcbm();

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected static int getNextConcatenatedRef() {
        int i = sConcatenatedRef + 1;
        sConcatenatedRef = i;
        return i;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SMSDispatcher(Phone phone, SmsDispatchersController smsDispatchersController) {
        AtomicInteger atomicInteger = new AtomicInteger(1);
        this.mPremiumSmsRule = atomicInteger;
        this.mLocalLog = new LocalLog(16);
        this.mSmsOutgoingErrorCodes = new LocalLog(10);
        this.mRPSmmaRetried = false;
        this.mSmsCapable = true;
        this.mCarrierMessagingTimeout = CarrierServicesSmsFilter.FILTER_COMPLETE_TIMEOUT_MS;
        this.mMessageRef = -1;
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.internal.telephony.SMSDispatcher.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                Rlog.d("SMSDispatcher", "Received broadcast " + intent.getAction());
                if ("android.intent.action.SIM_STATE_CHANGED".equals(intent.getAction())) {
                    if (!intent.hasExtra("ss")) {
                        Rlog.d("SMSDispatcher", "Extra not found in intent.");
                    } else if (intent.getStringExtra("ss").equals("LOADED")) {
                        Rlog.d("SMSDispatcher", "SIM_STATE_CHANGED : SIM_LOADED");
                        Message obtainMessage = SMSDispatcher.this.obtainMessage(18);
                        obtainMessage.arg1 = SMSDispatcher.this.getSubId();
                        SMSDispatcher.this.sendMessage(obtainMessage);
                    }
                }
            }
        };
        this.mBroadcastReceiver = broadcastReceiver;
        this.mPhone = phone;
        this.mSmsDispatchersController = smsDispatchersController;
        Context context = phone.getContext();
        this.mContext = context;
        this.mResolver = context.getContentResolver();
        this.mCi = phone.mCi;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        this.mTelephonyManager = telephonyManager;
        SettingsObserver settingsObserver = new SettingsObserver(this, atomicInteger, context);
        this.mSettingsObserver = settingsObserver;
        context.getContentResolver().registerContentObserver(Settings.Global.getUriFor("sms_short_code_rule"), false, settingsObserver);
        this.mSmsCapable = context.getResources().getBoolean(17891804);
        this.mSmsSendDisabled = !telephonyManager.getSmsSendCapableForPhone(this.mPhone.getPhoneId(), this.mSmsCapable);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SIM_STATE_CHANGED");
        context.registerReceiver(broadcastReceiver, intentFilter);
        Rlog.d("SMSDispatcher", "SMSDispatcher: ctor mSmsCapable=" + this.mSmsCapable + " format=" + getFormat() + " mSmsSendDisabled=" + this.mSmsSendDisabled);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SettingsObserver extends ContentObserver {
        private final Context mContext;
        private final AtomicInteger mPremiumSmsRule;

        SettingsObserver(Handler handler, AtomicInteger atomicInteger, Context context) {
            super(handler);
            this.mPremiumSmsRule = atomicInteger;
            this.mContext = context;
            onChange(false);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            this.mPremiumSmsRule.set(Settings.Global.getInt(this.mContext.getContentResolver(), "sms_short_code_rule", 1));
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void dispose() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mSettingsObserver);
    }

    protected void handleStatusReport(Object obj) {
        Rlog.d("SMSDispatcher", "handleStatusReport() called with no subclass.");
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i = message.what;
        if (i == 17) {
            handleMessageRefStatus(message);
        } else if (i != 18) {
            int i2 = 8;
            switch (i) {
                case 2:
                    handleSendComplete((AsyncResult) message.obj);
                    return;
                case 3:
                    Rlog.d("SMSDispatcher", "SMS retry..");
                    sendRetrySms((SmsTracker) message.obj);
                    return;
                case 4:
                    handleReachSentLimit((SmsTracker[]) message.obj);
                    return;
                case 5:
                    for (SmsTracker smsTracker : (SmsTracker[]) message.obj) {
                        sendSms(smsTracker);
                    }
                    this.mPendingTrackerCount--;
                    return;
                case 6:
                    SmsTracker[] smsTrackerArr = (SmsTracker[]) message.obj;
                    int i3 = message.arg1;
                    if (i3 == 0) {
                        if (message.arg2 == 1) {
                            Rlog.d("SMSDispatcher", "SMSDispatcher: EVENT_STOP_SENDING - sending SHORT_CODE_NEVER_ALLOWED error code.");
                        } else {
                            Rlog.d("SMSDispatcher", "SMSDispatcher: EVENT_STOP_SENDING - sending SHORT_CODE_NOT_ALLOWED error code.");
                            i2 = 7;
                        }
                    } else if (i3 == 1) {
                        Rlog.d("SMSDispatcher", "SMSDispatcher: EVENT_STOP_SENDING - sending LIMIT_EXCEEDED error code.");
                        i2 = 5;
                    } else {
                        Rlog.e("SMSDispatcher", "SMSDispatcher: EVENT_STOP_SENDING - unexpected cases.");
                        i2 = 28;
                    }
                    handleSmsTrackersFailure(smsTrackerArr, i2, -1);
                    this.mPendingTrackerCount--;
                    return;
                case 7:
                    Rlog.d("SMSDispatcher", "SMSDispatcher: EVENT_SENDING_NOT_ALLOWED - sending SHORT_CODE_NEVER_ALLOWED error code.");
                    handleSmsTrackersFailure((SmsTracker[]) message.obj, 8, -1);
                    return;
                case 8:
                    handleConfirmShortCode(false, (SmsTracker[]) message.obj);
                    return;
                case 9:
                    handleConfirmShortCode(true, (SmsTracker[]) message.obj);
                    return;
                case 10:
                    handleStatusReport(message.obj);
                    return;
                default:
                    Rlog.e("SMSDispatcher", "handleMessage() ignoring message of unexpected type " + message.what);
                    return;
            }
        } else {
            int tpmrValueFromSIM = getTpmrValueFromSIM();
            this.mMessageRef = tpmrValueFromSIM;
            if (tpmrValueFromSIM == -1) {
                if (this.mPhone.isSubscriptionManagerServiceEnabled()) {
                    SubscriptionInfoInternal subscriptionInfoInternal = SubscriptionManagerService.getInstance().getSubscriptionInfoInternal(message.arg1);
                    if (subscriptionInfoInternal != null) {
                        this.mMessageRef = subscriptionInfoInternal.getLastUsedTPMessageReference();
                        return;
                    }
                    return;
                }
                this.mMessageRef = SubscriptionController.getInstance().getMessageRef(message.arg1);
            }
        }
    }

    private void handleMessageRefStatus(Message message) {
        AsyncResult asyncResult = (AsyncResult) message.obj;
        if (asyncResult.exception != null) {
            Rlog.e("SMSDispatcher", "Failed to update TP - Message reference value to SIM " + asyncResult.exception);
            return;
        }
        Rlog.d("SMSDispatcher", "TP - Message reference updated to SIM Successfully");
    }

    private void updateTPMessageReference() {
        updateSIMLastTPMRValue(this.mMessageRef);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
                    SubscriptionManagerService.getInstance().setLastUsedTPMessageReference(getSubId(), this.mMessageRef);
                } else {
                    SubscriptionController.getInstance().updateMessageRef(getSubId(), this.mMessageRef);
                }
            } catch (SecurityException e) {
                Rlog.e("SMSDispatcher", "Security Exception caused on messageRef updation to DB " + e.getMessage());
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    private void updateSIMLastTPMRValue(int i) {
        Message obtainMessage = obtainMessage(17);
        IccRecords iccRecords = getIccRecords();
        if (iccRecords != null) {
            iccRecords.setSmssTpmrValue(i, obtainMessage);
        }
    }

    private int getTpmrValueFromSIM() {
        IccRecords iccRecords = getIccRecords();
        if (iccRecords != null) {
            return iccRecords.getSmssTpmrValue();
        }
        return -1;
    }

    private IccRecords getIccRecords() {
        Phone phone = this.mPhone;
        if (phone == null || phone.getIccRecords() == null) {
            return null;
        }
        return this.mPhone.getIccRecords();
    }

    public int nextMessageRef() {
        if (isMessageRefIncrementViaTelephony()) {
            this.mMessageRef = (this.mMessageRef + 1) % CallFailCause.RADIO_UPLINK_FAILURE;
            updateTPMessageReference();
            return this.mMessageRef;
        }
        return 0;
    }

    public boolean isMessageRefIncrementViaTelephony() {
        boolean z;
        try {
            z = this.mContext.getResources().getBoolean(17891812);
        } catch (Resources.NotFoundException unused) {
            Rlog.e("SMSDispatcher", "isMessageRefIncrementViaTelephony NotFoundException Exception");
            z = false;
        }
        Rlog.i("SMSDispatcher", "bool.config_stk_sms_send_support= " + z);
        return z;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public abstract class SmsSender extends Handler {
        protected final CarrierMessagingServiceWrapper mCarrierMessagingServiceWrapper;
        private String mCarrierPackageName;
        protected volatile CarrierMessagingServiceWrapper.CarrierMessagingCallback mSenderCallback;

        public abstract SmsTracker getSmsTracker();

        public abstract SmsTracker[] getSmsTrackers();

        public abstract void onSendComplete(int i);

        /* renamed from: onServiceReady */
        public abstract void lambda$sendSmsByCarrierApp$1();

        protected SmsSender() {
            super(Looper.getMainLooper());
            this.mCarrierMessagingServiceWrapper = new CarrierMessagingServiceWrapper();
        }

        public synchronized void sendSmsByCarrierApp(String str, CarrierMessagingServiceWrapper.CarrierMessagingCallback carrierMessagingCallback) {
            this.mCarrierPackageName = str;
            this.mSenderCallback = carrierMessagingCallback;
            if (!this.mCarrierMessagingServiceWrapper.bindToCarrierMessagingService(SMSDispatcher.this.mContext, str, new Executor() { // from class: com.android.internal.telephony.SMSDispatcher$SmsSender$$ExternalSyntheticLambda0
                @Override // java.util.concurrent.Executor
                public final void execute(Runnable runnable) {
                    runnable.run();
                }
            }, new Runnable() { // from class: com.android.internal.telephony.SMSDispatcher$SmsSender$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    SMSDispatcher.SmsSender.this.lambda$sendSmsByCarrierApp$1();
                }
            })) {
                Rlog.e("SMSDispatcher", "bindService() for carrier messaging service failed");
                onSendComplete(1);
            } else {
                Rlog.d("SMSDispatcher", "bindService() for carrier messaging service succeeded");
                sendMessageDelayed(obtainMessage(1), SMSDispatcher.this.mCarrierMessagingTimeout);
            }
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 1) {
                SMSDispatcher sMSDispatcher = SMSDispatcher.this;
                sMSDispatcher.logWithLocalLog("handleMessage: No response from " + this.mCarrierPackageName + " for " + SMSDispatcher.this.mCarrierMessagingTimeout + " ms");
                UUID uuid = SMSDispatcher.sAnomalyNoResponseFromCarrierMessagingService;
                StringBuilder sb = new StringBuilder();
                sb.append("No response from ");
                sb.append(this.mCarrierPackageName);
                AnomalyReporter.reportAnomaly(uuid, sb.toString(), SMSDispatcher.this.mPhone.getCarrierId());
                onSendComplete(1);
                return;
            }
            SMSDispatcher sMSDispatcher2 = SMSDispatcher.this;
            sMSDispatcher2.logWithLocalLog("handleMessage: received unexpected message " + message.what);
        }

        public void removeTimeout() {
            removeMessages(1);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logWithLocalLog(String str) {
        this.mLocalLog.log(str);
        Rlog.d("SMSDispatcher", str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public final class TextSmsSender extends SmsSender {
        private final SmsTracker mTracker;

        public TextSmsSender(SmsTracker smsTracker) {
            super();
            this.mTracker = smsTracker;
        }

        @Override // com.android.internal.telephony.SMSDispatcher.SmsSender
        public synchronized void onServiceReady() {
            Rlog.d("SMSDispatcher", "TextSmsSender::onServiceReady");
            String str = (String) this.mTracker.getData().get(SMSDispatcher.MAP_KEY_TEXT);
            if (str != null) {
                try {
                    CarrierMessagingServiceWrapper carrierMessagingServiceWrapper = this.mCarrierMessagingServiceWrapper;
                    int subId = SMSDispatcher.this.getSubId();
                    SmsTracker smsTracker = this.mTracker;
                    carrierMessagingServiceWrapper.sendTextSms(str, subId, smsTracker.mDestAddress, smsTracker.mDeliveryIntent != null ? 1 : 0, new Executor() { // from class: com.android.internal.telephony.SMSDispatcher$TextSmsSender$$ExternalSyntheticLambda0
                        @Override // java.util.concurrent.Executor
                        public final void execute(Runnable runnable) {
                            runnable.run();
                        }
                    }, this.mSenderCallback);
                } catch (RuntimeException e) {
                    Rlog.e("SMSDispatcher", "TextSmsSender::onServiceReady: Exception sending the SMS: " + e.getMessage());
                    onSendComplete(1);
                }
            } else {
                Rlog.d("SMSDispatcher", "TextSmsSender::onServiceReady: text == null");
                onSendComplete(1);
            }
        }

        @Override // com.android.internal.telephony.SMSDispatcher.SmsSender
        public void onSendComplete(int i) {
            this.mSenderCallback.onSendSmsComplete(i, 0);
        }

        @Override // com.android.internal.telephony.SMSDispatcher.SmsSender
        public SmsTracker getSmsTracker() {
            return this.mTracker;
        }

        @Override // com.android.internal.telephony.SMSDispatcher.SmsSender
        public SmsTracker[] getSmsTrackers() {
            Rlog.e("SMSDispatcher", "getSmsTrackers: Unexpected call for TextSmsSender");
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public final class DataSmsSender extends SmsSender {
        private final SmsTracker mTracker;

        public DataSmsSender(SmsTracker smsTracker) {
            super();
            this.mTracker = smsTracker;
        }

        @Override // com.android.internal.telephony.SMSDispatcher.SmsSender
        public synchronized void onServiceReady() {
            Rlog.d("SMSDispatcher", "DataSmsSender::onServiceReady");
            HashMap<String, Object> data = this.mTracker.getData();
            byte[] bArr = (byte[]) data.get(SMSDispatcher.MAP_KEY_DATA);
            int intValue = ((Integer) data.get(SMSDispatcher.MAP_KEY_DEST_PORT)).intValue();
            if (bArr != null) {
                try {
                    CarrierMessagingServiceWrapper carrierMessagingServiceWrapper = this.mCarrierMessagingServiceWrapper;
                    int subId = SMSDispatcher.this.getSubId();
                    SmsTracker smsTracker = this.mTracker;
                    carrierMessagingServiceWrapper.sendDataSms(bArr, subId, smsTracker.mDestAddress, intValue, smsTracker.mDeliveryIntent != null ? 1 : 0, new Executor() { // from class: com.android.internal.telephony.SMSDispatcher$DataSmsSender$$ExternalSyntheticLambda0
                        @Override // java.util.concurrent.Executor
                        public final void execute(Runnable runnable) {
                            runnable.run();
                        }
                    }, this.mSenderCallback);
                } catch (RuntimeException e) {
                    Rlog.e("SMSDispatcher", "DataSmsSender::onServiceReady: Exception sending the SMS: " + e + " " + SmsController.formatCrossStackMessageId(this.mTracker.mMessageId));
                    onSendComplete(1);
                }
            } else {
                Rlog.d("SMSDispatcher", "DataSmsSender::onServiceReady: data == null");
                onSendComplete(1);
            }
        }

        @Override // com.android.internal.telephony.SMSDispatcher.SmsSender
        public void onSendComplete(int i) {
            this.mSenderCallback.onSendSmsComplete(i, 0);
        }

        @Override // com.android.internal.telephony.SMSDispatcher.SmsSender
        public SmsTracker getSmsTracker() {
            return this.mTracker;
        }

        @Override // com.android.internal.telephony.SMSDispatcher.SmsSender
        public SmsTracker[] getSmsTrackers() {
            Rlog.e("SMSDispatcher", "getSmsTrackers: Unexpected call for DataSmsSender");
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public final class SmsSenderCallback implements CarrierMessagingServiceWrapper.CarrierMessagingCallback {
        private boolean mCallbackCalled = false;
        private final SmsSender mSmsSender;

        public SmsSenderCallback(SmsSender smsSender) {
            this.mSmsSender = smsSender;
        }

        public void onSendSmsComplete(int i, int i2) {
            Rlog.d("SMSDispatcher", "onSendSmsComplete: result=" + i + " messageRef=" + i2);
            if (cleanupOnSendSmsComplete("onSendSmsComplete")) {
                return;
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                SMSDispatcher.this.processSendSmsResponse(this.mSmsSender.getSmsTracker(), i, i2);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void onSendMultipartSmsComplete(int i, int[] iArr) {
            Rlog.d("SMSDispatcher", "onSendMultipartSmsComplete: result=" + i + " messageRefs=" + Arrays.toString(iArr));
            if (cleanupOnSendSmsComplete("onSendMultipartSmsComplete")) {
                return;
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                SMSDispatcher.this.processSendMultipartSmsResponse(this.mSmsSender.getSmsTrackers(), i, iArr);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        private boolean cleanupOnSendSmsComplete(String str) {
            if (this.mCallbackCalled) {
                SMSDispatcher sMSDispatcher = SMSDispatcher.this;
                sMSDispatcher.logWithLocalLog(str + ": unexpected call");
                UUID uuid = SMSDispatcher.sAnomalyUnexpectedCallback;
                AnomalyReporter.reportAnomaly(uuid, "Unexpected " + str, SMSDispatcher.this.mPhone.getCarrierId());
                return true;
            }
            this.mCallbackCalled = true;
            this.mSmsSender.removeTimeout();
            this.mSmsSender.mCarrierMessagingServiceWrapper.disconnect();
            return false;
        }

        public void onReceiveSmsComplete(int i) {
            Rlog.e("SMSDispatcher", "Unexpected onReceiveSmsComplete call with result: " + i);
        }

        public void onSendMmsComplete(int i, byte[] bArr) {
            Rlog.e("SMSDispatcher", "Unexpected onSendMmsComplete call with result: " + i);
        }

        public void onDownloadMmsComplete(int i) {
            Rlog.e("SMSDispatcher", "Unexpected onDownloadMmsComplete call with result: " + i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void processSendSmsResponse(SmsTracker smsTracker, int i, int i2) {
        if (smsTracker == null) {
            Rlog.e("SMSDispatcher", "processSendSmsResponse: null tracker");
            return;
        }
        SmsResponse smsResponse = new SmsResponse(i2, null, -1, smsTracker.mMessageId);
        if (i == 0) {
            Rlog.d("SMSDispatcher", "processSendSmsResponse: Sending SMS by CarrierMessagingService succeeded. " + SmsController.formatCrossStackMessageId(smsTracker.mMessageId));
            sendMessage(obtainMessage(2, new AsyncResult(smsTracker, smsResponse, (Throwable) null)));
        } else if (i == 1) {
            Rlog.d("SMSDispatcher", "processSendSmsResponse: Sending SMS by CarrierMessagingService failed. Retry on carrier network. " + SmsController.formatCrossStackMessageId(smsTracker.mMessageId));
            sendSubmitPdu(smsTracker);
        } else if (i == 2) {
            Rlog.d("SMSDispatcher", "processSendSmsResponse: Sending SMS by CarrierMessagingService failed. " + SmsController.formatCrossStackMessageId(smsTracker.mMessageId));
            sendMessage(obtainMessage(2, new AsyncResult(smsTracker, smsResponse, new CommandException(CommandException.Error.GENERIC_FAILURE))));
        } else {
            Rlog.d("SMSDispatcher", "processSendSmsResponse: Unknown result " + i + " Retry on carrier network. " + SmsController.formatCrossStackMessageId(smsTracker.mMessageId));
            sendSubmitPdu(smsTracker);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class MultipartSmsSender extends SmsSender {
        private final List<String> mParts;
        public final SmsTracker[] mTrackers;

        MultipartSmsSender(ArrayList<String> arrayList, SmsTracker[] smsTrackerArr) {
            super();
            this.mParts = arrayList;
            this.mTrackers = smsTrackerArr;
        }

        @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
        void sendSmsByCarrierApp(String str, SmsSenderCallback smsSenderCallback) {
            super.sendSmsByCarrierApp(str, (CarrierMessagingServiceWrapper.CarrierMessagingCallback) smsSenderCallback);
        }

        @Override // com.android.internal.telephony.SMSDispatcher.SmsSender
        public synchronized void onServiceReady() {
            boolean z;
            Rlog.d("SMSDispatcher", "MultipartSmsSender::onServiceReady");
            SmsTracker[] smsTrackerArr = this.mTrackers;
            int length = smsTrackerArr.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    z = false;
                    break;
                } else if (smsTrackerArr[i].mDeliveryIntent != null) {
                    z = true;
                    break;
                } else {
                    i++;
                }
            }
            try {
                this.mCarrierMessagingServiceWrapper.sendMultipartTextSms(this.mParts, SMSDispatcher.this.getSubId(), this.mTrackers[0].mDestAddress, z ? 1 : 0, new Executor() { // from class: com.android.internal.telephony.SMSDispatcher$MultipartSmsSender$$ExternalSyntheticLambda0
                    @Override // java.util.concurrent.Executor
                    public final void execute(Runnable runnable) {
                        runnable.run();
                    }
                }, this.mSenderCallback);
            } catch (RuntimeException e) {
                Rlog.e("SMSDispatcher", "MultipartSmsSender::onServiceReady: Exception sending the SMS: " + e);
                onSendComplete(1);
            }
        }

        @Override // com.android.internal.telephony.SMSDispatcher.SmsSender
        public void onSendComplete(int i) {
            this.mSenderCallback.onSendMultipartSmsComplete(i, (int[]) null);
        }

        @Override // com.android.internal.telephony.SMSDispatcher.SmsSender
        public SmsTracker getSmsTracker() {
            Rlog.e("SMSDispatcher", "getSmsTracker: Unexpected call for MultipartSmsSender");
            return null;
        }

        @Override // com.android.internal.telephony.SMSDispatcher.SmsSender
        public SmsTracker[] getSmsTrackers() {
            return this.mTrackers;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processSendMultipartSmsResponse(SmsTracker[] smsTrackerArr, int i, int[] iArr) {
        if (smsTrackerArr == null) {
            Rlog.e("SMSDispatcher", "processSendMultipartSmsResponse: null trackers");
        } else if (i == 0) {
            Rlog.d("SMSDispatcher", "processSendMultipartSmsResponse: Sending SMS by CarrierMessagingService succeeded. " + SmsController.formatCrossStackMessageId(smsTrackerArr[0].mMessageId));
            int i2 = 0;
            while (i2 < smsTrackerArr.length) {
                sendMessage(obtainMessage(2, new AsyncResult(smsTrackerArr[i2], new SmsResponse((iArr == null || iArr.length <= i2) ? 0 : iArr[i2], null, -1), (Throwable) null)));
                i2++;
            }
        } else if (i == 1) {
            Rlog.d("SMSDispatcher", "processSendMultipartSmsResponse: Sending SMS by CarrierMessagingService failed. Retry on carrier network. " + SmsController.formatCrossStackMessageId(smsTrackerArr[0].mMessageId));
            sendSubmitPdu(smsTrackerArr);
        } else if (i == 2) {
            Rlog.d("SMSDispatcher", "processSendMultipartSmsResponse: Sending SMS by CarrierMessagingService failed. " + SmsController.formatCrossStackMessageId(smsTrackerArr[0].mMessageId));
            int i3 = 0;
            while (i3 < smsTrackerArr.length) {
                sendMessage(obtainMessage(2, new AsyncResult(smsTrackerArr[i3], new SmsResponse((iArr == null || iArr.length <= i3) ? 0 : iArr[i3], null, -1), new CommandException(CommandException.Error.GENERIC_FAILURE))));
                i3++;
            }
        } else {
            Rlog.d("SMSDispatcher", "processSendMultipartSmsResponse: Unknown result " + i + ". Retry on carrier network. " + SmsController.formatCrossStackMessageId(smsTrackerArr[0].mMessageId));
            sendSubmitPdu(smsTrackerArr);
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private void sendSubmitPdu(SmsTracker smsTracker) {
        sendSubmitPdu(new SmsTracker[]{smsTracker});
    }

    private void sendSubmitPdu(SmsTracker[] smsTrackerArr) {
        if (shouldBlockSmsForEcbm()) {
            Rlog.d("SMSDispatcher", "Block SMS in Emergency Callback mode");
            handleSmsTrackersFailure(smsTrackerArr, 29, -1);
            return;
        }
        sendRawPdu(smsTrackerArr);
    }

    protected void handleSendComplete(AsyncResult asyncResult) {
        SmsTracker smsTracker = (SmsTracker) asyncResult.userObj;
        PendingIntent pendingIntent = smsTracker.mSentIntent;
        SmsResponse smsResponse = (SmsResponse) asyncResult.result;
        if (smsResponse != null) {
            smsTracker.mMessageRef = smsResponse.mMessageRef;
        } else {
            Rlog.d("SMSDispatcher", "SmsResponse was null");
        }
        if (asyncResult.exception == null) {
            if (smsTracker.mDeliveryIntent != null) {
                this.mSmsDispatchersController.putDeliveryPendingTracker(smsTracker);
            }
            smsTracker.onSent(this.mContext);
            this.mPhone.notifySmsSent(smsTracker.mDestAddress);
            this.mPhone.getSmsStats().onOutgoingSms(smsTracker.mImsRetry > 0, "3gpp2".equals(getFormat()), false, 0, smsTracker.mMessageId, smsTracker.isFromDefaultSmsApplication(this.mContext), smsTracker.getInterval());
            return;
        }
        int state = this.mPhone.getServiceState().getState();
        int rilErrorToSmsManagerResult = rilErrorToSmsManagerResult(((CommandException) asyncResult.exception).getCommandError(), smsTracker);
        if (smsTracker.mImsRetry > 0 && state != 0) {
            smsTracker.mRetryCount = getMaxSmsRetryCount();
            Rlog.d("SMSDispatcher", "handleSendComplete: Skipping retry:  isIms()=" + isIms() + " mRetryCount=" + smsTracker.mRetryCount + " mImsRetry=" + smsTracker.mImsRetry + " mMessageRef=" + smsTracker.mMessageRef + " SS= " + this.mPhone.getServiceState().getState() + " " + SmsController.formatCrossStackMessageId(smsTracker.mMessageId));
        }
        if (!isIms() && state != 0) {
            smsTracker.onFailed(this.mContext, getNotInServiceError(state), -1);
            this.mPhone.getSmsStats().onOutgoingSms(smsTracker.mImsRetry > 0, "3gpp2".equals(getFormat()), false, getNotInServiceError(state), smsTracker.mMessageId, smsTracker.isFromDefaultSmsApplication(this.mContext), smsTracker.getInterval());
        } else if (rilErrorToSmsManagerResult == 101 && smsTracker.mRetryCount < getMaxSmsRetryCount()) {
            smsTracker.mRetryCount++;
            int i = smsResponse != null ? smsResponse.mErrorCode : -1;
            sendMessageDelayed(obtainMessage(3, smsTracker), getSmsRetryDelayValue());
            this.mPhone.getSmsStats().onOutgoingSms(smsTracker.mImsRetry > 0, "3gpp2".equals(getFormat()), false, 101, i, smsTracker.mMessageId, smsTracker.isFromDefaultSmsApplication(this.mContext), smsTracker.getInterval());
        } else {
            int i2 = smsResponse != null ? smsResponse.mErrorCode : -1;
            smsTracker.onFailed(this.mContext, rilErrorToSmsManagerResult, i2);
            this.mPhone.getSmsStats().onOutgoingSms(smsTracker.mImsRetry > 0, "3gpp2".equals(getFormat()), false, rilErrorToSmsManagerResult, i2, smsTracker.mMessageId, smsTracker.isFromDefaultSmsApplication(this.mContext), smsTracker.getInterval());
        }
    }

    private int rilErrorToSmsManagerResult(CommandException.Error error, SmsTracker smsTracker) {
        LocalLog localLog = this.mSmsOutgoingErrorCodes;
        localLog.log("rilError: " + error + ", MessageId: " + SmsController.formatCrossStackMessageId(smsTracker.mMessageId));
        ApplicationInfo appInfo = smsTracker.getAppInfo();
        if ((appInfo == null || !CompatChanges.isChangeEnabled(250017070L, appInfo.uid)) && (error == CommandException.Error.INVALID_RESPONSE || error == CommandException.Error.SIM_PIN2 || error == CommandException.Error.SIM_PUK2 || error == CommandException.Error.SUBSCRIPTION_NOT_AVAILABLE || error == CommandException.Error.SIM_ERR || error == CommandException.Error.INVALID_SIM_STATE || error == CommandException.Error.NO_SMS_TO_ACK || error == CommandException.Error.SIM_BUSY || error == CommandException.Error.SIM_FULL || error == CommandException.Error.NO_SUBSCRIPTION || error == CommandException.Error.NO_NETWORK_FOUND || error == CommandException.Error.DEVICE_IN_USE || error == CommandException.Error.ABORTED)) {
            return 1;
        }
        switch (C00592.$SwitchMap$com$android$internal$telephony$CommandException$Error[error.ordinal()]) {
            case 1:
                return 100;
            case 2:
                return 101;
            case 3:
                return CallFailCause.RECOVERY_ON_TIMER_EXPIRY;
            case 4:
                return 103;
            case 5:
                return 104;
            case 6:
                return 105;
            case 7:
                return 106;
            case 8:
                return 107;
            case 9:
                return 108;
            case 10:
                return 109;
            case 11:
                return 111;
            case 12:
                return TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_APN_TYPE_CONFLICT;
            case 13:
                return TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_INVALID_PCSCF_ADDR;
            case 14:
                return TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_INTERNAL_CALL_PREEMPT_BY_HIGH_PRIO_APN;
            case 15:
                return TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_EMM_ACCESS_BARRED;
            case 16:
                return TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_EMERGENCY_IFACE_ONLY;
            case 17:
                return TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_IFACE_MISMATCH;
            case 18:
                return TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_COMPANION_IFACE_IN_USE;
            case 19:
                return TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_IP_ADDRESS_MISMATCH;
            case 20:
                return TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_IFACE_AND_POL_FAMILY_MISMATCH;
            case 21:
                return 6;
            case 22:
                return TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_EMM_ACCESS_BARRED_INFINITE_RETRY;
            case 23:
                return TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_AUTH_FAILURE_ON_EMERGENCY_CALL;
            case 24:
                return TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_INVALID_DNS_ADDR;
            case 25:
                return 19;
            case 26:
                return 125;
            case 27:
                return 126;
            case 28:
                return 127;
            case 29:
                return 128;
            case 30:
                return 129;
            case 31:
                return 130;
            case 32:
                return 131;
            case 33:
                return UiccCardApplication.AUTH_CONTEXT_GBA_BOOTSTRAP;
            case 34:
                return 133;
            case 35:
                return 134;
            case 36:
                return NetworkStackConstants.ICMPV6_NEIGHBOR_SOLICITATION;
            case 37:
                return NetworkStackConstants.ICMPV6_NEIGHBOR_ADVERTISEMENT;
            case 38:
                return 137;
            default:
                Rlog.d("SMSDispatcher", "rilErrorToSmsManagerResult: " + error + " " + SmsController.formatCrossStackMessageId(smsTracker.mMessageId));
                return TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_INVALID_PCSCF_OR_DNS_ADDRESS;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.internal.telephony.SMSDispatcher$2 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class C00592 {
        static final /* synthetic */ int[] $SwitchMap$com$android$internal$telephony$CommandException$Error;

        static {
            int[] iArr = new int[CommandException.Error.values().length];
            $SwitchMap$com$android$internal$telephony$CommandException$Error = iArr;
            try {
                iArr[CommandException.Error.RADIO_NOT_AVAILABLE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.SMS_FAIL_RETRY.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.NETWORK_REJECT.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.INVALID_STATE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.INVALID_ARGUMENTS.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.NO_MEMORY.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.REQUEST_RATE_LIMITED.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.INVALID_SMS_FORMAT.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.SYSTEM_ERR.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.ENCODING_ERR.ordinal()] = 10;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.MODEM_ERR.ordinal()] = 11;
            } catch (NoSuchFieldError unused11) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.NETWORK_ERR.ordinal()] = 12;
            } catch (NoSuchFieldError unused12) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.INTERNAL_ERR.ordinal()] = 13;
            } catch (NoSuchFieldError unused13) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.REQUEST_NOT_SUPPORTED.ordinal()] = 14;
            } catch (NoSuchFieldError unused14) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.INVALID_MODEM_STATE.ordinal()] = 15;
            } catch (NoSuchFieldError unused15) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.NETWORK_NOT_READY.ordinal()] = 16;
            } catch (NoSuchFieldError unused16) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.OPERATION_NOT_ALLOWED.ordinal()] = 17;
            } catch (NoSuchFieldError unused17) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.NO_RESOURCES.ordinal()] = 18;
            } catch (NoSuchFieldError unused18) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.REQUEST_CANCELLED.ordinal()] = 19;
            } catch (NoSuchFieldError unused19) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.SIM_ABSENT.ordinal()] = 20;
            } catch (NoSuchFieldError unused20) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.FDN_CHECK_FAILURE.ordinal()] = 21;
            } catch (NoSuchFieldError unused21) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.SIMULTANEOUS_SMS_AND_CALL_NOT_ALLOWED.ordinal()] = 22;
            } catch (NoSuchFieldError unused22) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.ACCESS_BARRED.ordinal()] = 23;
            } catch (NoSuchFieldError unused23) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.BLOCKED_DUE_TO_CALL.ordinal()] = 24;
            } catch (NoSuchFieldError unused24) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.INVALID_SMSC_ADDRESS.ordinal()] = 25;
            } catch (NoSuchFieldError unused25) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.INVALID_RESPONSE.ordinal()] = 26;
            } catch (NoSuchFieldError unused26) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.SIM_PIN2.ordinal()] = 27;
            } catch (NoSuchFieldError unused27) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.SIM_PUK2.ordinal()] = 28;
            } catch (NoSuchFieldError unused28) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.SUBSCRIPTION_NOT_AVAILABLE.ordinal()] = 29;
            } catch (NoSuchFieldError unused29) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.SIM_ERR.ordinal()] = 30;
            } catch (NoSuchFieldError unused30) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.INVALID_SIM_STATE.ordinal()] = 31;
            } catch (NoSuchFieldError unused31) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.NO_SMS_TO_ACK.ordinal()] = 32;
            } catch (NoSuchFieldError unused32) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.SIM_BUSY.ordinal()] = 33;
            } catch (NoSuchFieldError unused33) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.SIM_FULL.ordinal()] = 34;
            } catch (NoSuchFieldError unused34) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.NO_SUBSCRIPTION.ordinal()] = 35;
            } catch (NoSuchFieldError unused35) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.NO_NETWORK_FOUND.ordinal()] = 36;
            } catch (NoSuchFieldError unused36) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.DEVICE_IN_USE.ordinal()] = 37;
            } catch (NoSuchFieldError unused37) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.ABORTED.ordinal()] = 38;
            } catch (NoSuchFieldError unused38) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @UnsupportedAppUsage
    public void sendData(String str, String str2, String str3, int i, byte[] bArr, PendingIntent pendingIntent, PendingIntent pendingIntent2, boolean z) {
        int nextMessageRef = nextMessageRef();
        SmsMessageBase.SubmitPduBase submitPdu = getSubmitPdu(str3, str2, i, bArr, pendingIntent2 != null, nextMessageRef);
        if (submitPdu != null) {
            SmsTracker smsTracker = getSmsTracker(str, getSmsTrackerMap(str2, str3, i, bArr, submitPdu), pendingIntent, pendingIntent2, getFormat(), null, false, null, false, true, z, 0L, nextMessageRef);
            if (sendSmsByCarrierApp(true, smsTracker)) {
                return;
            }
            sendSubmitPdu(smsTracker);
            return;
        }
        Rlog.e("SMSDispatcher", "SMSDispatcher.sendData(): getSubmitPdu() returned null");
        triggerSentIntentForFailure(pendingIntent);
    }

    public void sendText(String str, String str2, String str3, PendingIntent pendingIntent, PendingIntent pendingIntent2, Uri uri, String str4, boolean z, int i, boolean z2, int i2, boolean z3, long j) {
        sendText(str, str2, str3, pendingIntent, pendingIntent2, uri, str4, z, i, z2, i2, z3, j, false);
    }

    public void sendText(String str, String str2, String str3, PendingIntent pendingIntent, PendingIntent pendingIntent2, Uri uri, String str4, boolean z, int i, boolean z2, int i2, boolean z3, long j, boolean z4) {
        Rlog.d("SMSDispatcher", "sendText id: " + SmsController.formatCrossStackMessageId(j));
        int nextMessageRef = nextMessageRef();
        SmsMessageBase.SubmitPduBase submitPdu = getSubmitPdu(str2, str, str3, pendingIntent2 != null, null, i, i2, nextMessageRef);
        if (submitPdu != null) {
            SmsTracker smsTracker = getSmsTracker(str4, getSmsTrackerMap(str, str2, str3, submitPdu), pendingIntent, pendingIntent2, getFormat(), uri, z2, str3, true, z, i, i2, z3, j, nextMessageRef, z4);
            if (sendSmsByCarrierApp(false, smsTracker)) {
                return;
            }
            sendSubmitPdu(smsTracker);
            return;
        }
        Rlog.e("SMSDispatcher", "SmsDispatcher.sendText(): getSubmitPdu() returned null " + SmsController.formatCrossStackMessageId(j));
        triggerSentIntentForFailure(pendingIntent);
    }

    private void triggerSentIntentForFailure(PendingIntent pendingIntent) {
        if (pendingIntent != null) {
            try {
                pendingIntent.send(1);
            } catch (PendingIntent.CanceledException unused) {
                Rlog.e("SMSDispatcher", "Intent has been canceled!");
            }
        }
    }

    private void triggerSentIntentForFailure(List<PendingIntent> list) {
        if (list == null) {
            return;
        }
        for (PendingIntent pendingIntent : list) {
            triggerSentIntentForFailure(pendingIntent);
        }
    }

    private boolean sendSmsByCarrierApp(boolean z, SmsTracker smsTracker) {
        SmsSender textSmsSender;
        String carrierAppPackageName = getCarrierAppPackageName();
        if (carrierAppPackageName != null) {
            Rlog.d("SMSDispatcher", "Found carrier package " + carrierAppPackageName);
            if (z) {
                textSmsSender = new DataSmsSender(smsTracker);
            } else {
                textSmsSender = new TextSmsSender(smsTracker);
            }
            textSmsSender.sendSmsByCarrierApp(carrierAppPackageName, new SmsSenderCallback(textSmsSender));
            return true;
        }
        return false;
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PROTECTED)
    public void sendMultipartText(String str, String str2, ArrayList<String> arrayList, ArrayList<PendingIntent> arrayList2, ArrayList<PendingIntent> arrayList3, Uri uri, String str3, boolean z, int i, boolean z2, int i2, long j) {
        SMSDispatcher sMSDispatcher = this;
        ArrayList<String> arrayList4 = arrayList;
        ArrayList<PendingIntent> arrayList5 = arrayList2;
        ArrayList<PendingIntent> arrayList6 = arrayList3;
        String multipartMessageText = sMSDispatcher.getMultipartMessageText(arrayList4);
        int nextConcatenatedRef = getNextConcatenatedRef() & 255;
        int size = arrayList.size();
        boolean z3 = true;
        if (size < 1) {
            sMSDispatcher.triggerSentIntentForFailure(arrayList5);
            return;
        }
        GsmAlphabet.TextEncodingDetails[] textEncodingDetailsArr = new GsmAlphabet.TextEncodingDetails[size];
        int i3 = 0;
        for (int i4 = 0; i4 < size; i4++) {
            GsmAlphabet.TextEncodingDetails calculateLength = sMSDispatcher.calculateLength(arrayList4.get(i4), false);
            int i5 = calculateLength.codeUnitSize;
            if (i3 != i5 && (i3 == 0 || i3 == 1)) {
                i3 = i5;
            }
            textEncodingDetailsArr[i4] = calculateLength;
        }
        SmsTracker[] smsTrackerArr = new SmsTracker[size];
        AtomicInteger atomicInteger = new AtomicInteger(size);
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        int i6 = 0;
        while (i6 < size) {
            SmsHeader.ConcatRef concatRef = new SmsHeader.ConcatRef();
            concatRef.refNumber = nextConcatenatedRef;
            int i7 = i6 + 1;
            concatRef.seqNumber = i7;
            concatRef.msgCount = size;
            concatRef.isEightBits = z3;
            SmsHeader smsHeader = new SmsHeader();
            smsHeader.concatRef = concatRef;
            if (i3 == z3) {
                GsmAlphabet.TextEncodingDetails textEncodingDetails = textEncodingDetailsArr[i6];
                smsHeader.languageTable = textEncodingDetails.languageTable;
                smsHeader.languageShiftTable = textEncodingDetails.languageShiftTable;
            }
            PendingIntent pendingIntent = (arrayList5 == null || arrayList2.size() <= i6) ? null : arrayList5.get(i6);
            PendingIntent pendingIntent2 = (arrayList6 == null || arrayList3.size() <= i6) ? null : arrayList6.get(i6);
            int nextMessageRef = nextMessageRef();
            AtomicBoolean atomicBoolean2 = atomicBoolean;
            AtomicInteger atomicInteger2 = atomicInteger;
            SmsTracker[] smsTrackerArr2 = smsTrackerArr;
            int i8 = i3;
            GsmAlphabet.TextEncodingDetails[] textEncodingDetailsArr2 = textEncodingDetailsArr;
            int i9 = size;
            int i10 = nextConcatenatedRef;
            SmsTracker newSubmitPduTracker = getNewSubmitPduTracker(str3, str, str2, arrayList4.get(i6), smsHeader, i3, pendingIntent, pendingIntent2, i6 == size + (-1), atomicInteger2, atomicBoolean2, uri, multipartMessageText, i, z2, i2, j, nextMessageRef);
            smsTrackerArr2[i6] = newSubmitPduTracker;
            if (newSubmitPduTracker == null) {
                triggerSentIntentForFailure(arrayList2);
                return;
            }
            newSubmitPduTracker.mPersistMessage = z;
            arrayList4 = arrayList;
            arrayList6 = arrayList3;
            smsTrackerArr = smsTrackerArr2;
            sMSDispatcher = this;
            arrayList5 = arrayList2;
            i3 = i8;
            size = i9;
            i6 = i7;
            atomicBoolean = atomicBoolean2;
            atomicInteger = atomicInteger2;
            textEncodingDetailsArr = textEncodingDetailsArr2;
            z3 = true;
            nextConcatenatedRef = i10;
        }
        SmsTracker[] smsTrackerArr3 = smsTrackerArr;
        SMSDispatcher sMSDispatcher2 = sMSDispatcher;
        String carrierAppPackageName = getCarrierAppPackageName();
        if (carrierAppPackageName != null) {
            Rlog.d("SMSDispatcher", "Found carrier package " + carrierAppPackageName + " " + SmsController.formatCrossStackMessageId(sMSDispatcher2.getMultiTrackermessageId(smsTrackerArr3)));
            MultipartSmsSender multipartSmsSender = new MultipartSmsSender(arrayList, smsTrackerArr3);
            multipartSmsSender.sendSmsByCarrierApp(carrierAppPackageName, new SmsSenderCallback(multipartSmsSender));
            return;
        }
        Rlog.v("SMSDispatcher", "No carrier package. " + SmsController.formatCrossStackMessageId(sMSDispatcher2.getMultiTrackermessageId(smsTrackerArr3)));
        sMSDispatcher2.sendSubmitPdu(smsTrackerArr3);
    }

    private long getMultiTrackermessageId(SmsTracker[] smsTrackerArr) {
        if (smsTrackerArr.length == 0) {
            return 0L;
        }
        return smsTrackerArr[0].mMessageId;
    }

    private SmsTracker getNewSubmitPduTracker(String str, String str2, String str3, String str4, SmsHeader smsHeader, int i, PendingIntent pendingIntent, PendingIntent pendingIntent2, boolean z, AtomicInteger atomicInteger, AtomicBoolean atomicBoolean, Uri uri, String str5, int i2, boolean z2, int i3, long j, int i4) {
        int i5;
        boolean z3;
        if (isCdmaMo()) {
            UserData userData = new UserData();
            userData.payloadStr = str4;
            userData.userDataHeader = smsHeader;
            if (i == 1) {
                userData.msgEncoding = isAscii7bitSupportedForLongMessage() ? 2 : 9;
                Rlog.d("SMSDispatcher", "Message encoding for proper 7 bit: " + userData.msgEncoding);
            } else {
                userData.msgEncoding = 4;
            }
            userData.msgEncodingSet = true;
            if (pendingIntent2 == null || !z) {
                i5 = i2;
                z3 = false;
            } else {
                i5 = i2;
                z3 = true;
            }
            SmsMessage.SubmitPdu submitPdu = SmsMessage.getSubmitPdu(str2, userData, z3, i5);
            if (submitPdu != null) {
                return getSmsTracker(str, getSmsTrackerMap(str2, str3, str4, submitPdu), pendingIntent, pendingIntent2, getFormat(), atomicInteger, atomicBoolean, uri, smsHeader, !z || z2, str5, true, true, i2, i3, false, j, i4, false);
            }
            Rlog.e("SMSDispatcher", "CdmaSMSDispatcher.getNewSubmitPduTracker(): getSubmitPdu() returned null " + SmsController.formatCrossStackMessageId(j));
            return null;
        }
        SmsMessage.SubmitPdu submitPdu2 = com.android.internal.telephony.gsm.SmsMessage.getSubmitPdu(str3, str2, str4, pendingIntent2 != null, SmsHeader.toByteArray(smsHeader), i, smsHeader.languageTable, smsHeader.languageShiftTable, i3, i4);
        if (submitPdu2 != null) {
            return getSmsTracker(str, getSmsTrackerMap(str2, str3, str4, submitPdu2), pendingIntent, pendingIntent2, getFormat(), atomicInteger, atomicBoolean, uri, smsHeader, !z || z2, str5, true, false, i2, i3, false, j, i4, false);
        }
        Rlog.e("SMSDispatcher", "GsmSMSDispatcher.getNewSubmitPduTracker(): getSubmitPdu() returned null " + SmsController.formatCrossStackMessageId(j));
        return null;
    }

    @VisibleForTesting
    public void sendRawPdu(SmsTracker[] smsTrackerArr) {
        int i;
        PackageInfo packageInfo = null;
        if (this.mSmsSendDisabled) {
            Rlog.e("SMSDispatcher", "Device does not support sending sms.");
            i = 4;
        } else {
            int length = smsTrackerArr.length;
            int i2 = 0;
            while (true) {
                if (i2 >= length) {
                    i = 0;
                    break;
                } else if (smsTrackerArr[i2].getData().get(MAP_KEY_PDU) == null) {
                    Rlog.e("SMSDispatcher", "Empty PDU");
                    i = 3;
                    break;
                } else {
                    i2++;
                }
            }
            if (i == 0) {
                try {
                    packageInfo = this.mContext.createContextAsUser(UserHandle.of(smsTrackerArr[0].mUserId), 0).getPackageManager().getPackageInfo(smsTrackerArr[0].getAppPackageName(), 64);
                } catch (PackageManager.NameNotFoundException unused) {
                    Rlog.e("SMSDispatcher", "Can't get calling app package info: refusing to send SMS " + SmsController.formatCrossStackMessageId(getMultiTrackermessageId(smsTrackerArr)));
                    i = 1;
                }
            }
        }
        if (i != 0) {
            handleSmsTrackersFailure(smsTrackerArr, i, -1);
            return;
        }
        if (checkDestination(smsTrackerArr)) {
            if (!this.mSmsDispatchersController.getUsageMonitor().check(packageInfo.packageName, smsTrackerArr.length)) {
                sendMessage(obtainMessage(4, smsTrackerArr));
                return;
            }
            for (SmsTracker smsTracker : smsTrackerArr) {
                sendSms(smsTracker);
            }
        }
        if (this.mTelephonyManager.isEmergencyNumber(smsTrackerArr[0].mDestAddress)) {
            new AsyncEmergencyContactNotifier(this.mContext).execute(new Void[0]);
        }
    }

    boolean checkDestination(SmsTracker[] smsTrackerArr) {
        int checkDestination;
        if (this.mContext.checkCallingOrSelfPermission("android.permission.SEND_SMS_NO_CONFIRMATION") != 0 && !smsTrackerArr[0].mIsForVvm && !smsTrackerArr[0].mSkipShortCodeDestAddrCheck) {
            int i = this.mPremiumSmsRule.get();
            if (i == 1 || i == 3) {
                String simCountryIsoForPhone = TelephonyManager.getSimCountryIsoForPhone(this.mPhone.getPhoneId());
                if (simCountryIsoForPhone == null || simCountryIsoForPhone.length() != 2) {
                    Rlog.e("SMSDispatcher", "Can't get SIM country Iso: trying network country Iso " + SmsController.formatCrossStackMessageId(getMultiTrackermessageId(smsTrackerArr)));
                    simCountryIsoForPhone = this.mTelephonyManager.getNetworkCountryIso(this.mPhone.getPhoneId());
                }
                checkDestination = this.mSmsDispatchersController.getUsageMonitor().checkDestination(smsTrackerArr[0].mDestAddress, simCountryIsoForPhone);
            } else {
                checkDestination = 0;
            }
            if (i == 2 || i == 3) {
                String networkCountryIso = this.mTelephonyManager.getNetworkCountryIso(this.mPhone.getPhoneId());
                if (networkCountryIso == null || networkCountryIso.length() != 2) {
                    Rlog.e("SMSDispatcher", "Can't get Network country Iso: trying SIM country Iso " + SmsController.formatCrossStackMessageId(getMultiTrackermessageId(smsTrackerArr)));
                    networkCountryIso = TelephonyManager.getSimCountryIsoForPhone(this.mPhone.getPhoneId());
                }
                checkDestination = SmsUsageMonitor.mergeShortCodeCategories(checkDestination, this.mSmsDispatchersController.getUsageMonitor().checkDestination(smsTrackerArr[0].mDestAddress, networkCountryIso));
            }
            if (checkDestination != 0) {
                this.mPhone.getSmsStats().onOutgoingShortCodeSms(checkDestination, this.mSmsDispatchersController.getUsageMonitor().getShortCodeXmlFileVersion());
            }
            if (checkDestination != 0 && checkDestination != 1 && checkDestination != 2) {
                if (Settings.Global.getInt(this.mResolver, "device_provisioned", 0) == 0) {
                    Rlog.e("SMSDispatcher", "Can't send premium sms during Setup Wizard " + SmsController.formatCrossStackMessageId(getMultiTrackermessageId(smsTrackerArr)));
                    return false;
                }
                int premiumSmsPermission = this.mSmsDispatchersController.getUsageMonitor().getPremiumSmsPermission(smsTrackerArr[0].getAppPackageName());
                if (premiumSmsPermission == 0) {
                    premiumSmsPermission = 1;
                }
                if (premiumSmsPermission == 2) {
                    Rlog.w("SMSDispatcher", "User denied this app from sending to premium SMS " + SmsController.formatCrossStackMessageId(getMultiTrackermessageId(smsTrackerArr)));
                    sendMessage(obtainMessage(7, smsTrackerArr));
                    return false;
                } else if (premiumSmsPermission == 3) {
                    Rlog.d("SMSDispatcher", "User approved this app to send to premium SMS " + SmsController.formatCrossStackMessageId(getMultiTrackermessageId(smsTrackerArr)));
                    return true;
                } else {
                    sendMessage(obtainMessage(checkDestination == 3 ? 8 : 9, smsTrackerArr));
                    return false;
                }
            }
        }
        return true;
    }

    private boolean denyIfQueueLimitReached(SmsTracker[] smsTrackerArr) {
        int i = this.mPendingTrackerCount;
        if (i >= 5) {
            Rlog.e("SMSDispatcher", "Denied because queue limit reached " + SmsController.formatCrossStackMessageId(getMultiTrackermessageId(smsTrackerArr)));
            handleSmsTrackersFailure(smsTrackerArr, 5, -1);
            return true;
        }
        this.mPendingTrackerCount = i + 1;
        return false;
    }

    private CharSequence getAppLabel(String str, int i) {
        PackageManager packageManager = this.mContext.getPackageManager();
        try {
            return packageManager.getApplicationInfoAsUser(str, 0, UserHandle.of(i)).loadSafeLabel(packageManager);
        } catch (PackageManager.NameNotFoundException unused) {
            Rlog.e("SMSDispatcher", "PackageManager Name Not Found for package " + str);
            return str;
        }
    }

    protected void handleReachSentLimit(SmsTracker[] smsTrackerArr) {
        if (denyIfQueueLimitReached(smsTrackerArr)) {
            return;
        }
        CharSequence appLabel = getAppLabel(smsTrackerArr[0].getAppPackageName(), smsTrackerArr[0].mUserId);
        Resources system = Resources.getSystem();
        Spanned fromHtml = Html.fromHtml(system.getString(17041552, appLabel));
        ConfirmDialogListener confirmDialogListener = new ConfirmDialogListener(smsTrackerArr, null, 1);
        AlertDialog create = new AlertDialog.Builder(this.mContext).setTitle(17041554).setIcon(17301642).setMessage(fromHtml).setPositiveButton(system.getString(17041555), confirmDialogListener).setNegativeButton(system.getString(17041553), confirmDialogListener).setOnCancelListener(confirmDialogListener).create();
        create.getWindow().setType(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_MIP_FA_MOBILE_NODE_AUTHENTICATION_FAILURE);
        create.show();
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected void handleConfirmShortCode(boolean z, SmsTracker[] smsTrackerArr) {
        if (denyIfQueueLimitReached(smsTrackerArr)) {
            return;
        }
        int i = z ? 17041556 : 17041562;
        CharSequence appLabel = getAppLabel(smsTrackerArr[0].getAppPackageName(), smsTrackerArr[0].mUserId);
        Resources system = Resources.getSystem();
        Spanned fromHtml = Html.fromHtml(system.getString(17041560, appLabel, smsTrackerArr[0].mDestAddress));
        View inflate = ((LayoutInflater) this.mContext.getSystemService("layout_inflater")).inflate(17367336, (ViewGroup) null);
        ConfirmDialogListener confirmDialogListener = new ConfirmDialogListener(smsTrackerArr, (TextView) inflate.findViewById(16909532), 0);
        ((TextView) inflate.findViewById(16909527)).setText(fromHtml);
        ((TextView) ((ViewGroup) inflate.findViewById(16909528)).findViewById(16909529)).setText(i);
        ((CheckBox) inflate.findViewById(16909530)).setOnCheckedChangeListener(confirmDialogListener);
        AlertDialog create = new AlertDialog.Builder(this.mContext).setView(inflate).setPositiveButton(system.getString(17041557), confirmDialogListener).setNegativeButton(system.getString(17041559), confirmDialogListener).setOnCancelListener(confirmDialogListener).create();
        create.getWindow().setType(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_MIP_FA_MOBILE_NODE_AUTHENTICATION_FAILURE);
        create.show();
        confirmDialogListener.setPositiveButton(create.getButton(-1));
        confirmDialogListener.setNegativeButton(create.getButton(-2));
    }

    public void sendRetrySms(SmsTracker smsTracker) {
        SmsDispatchersController smsDispatchersController = this.mSmsDispatchersController;
        if (smsDispatchersController != null) {
            smsDispatchersController.sendRetrySms(smsTracker);
            return;
        }
        Rlog.e("SMSDispatcher", this.mSmsDispatchersController + " is null. Retry failed " + SmsController.formatCrossStackMessageId(smsTracker.mMessageId));
    }

    private void handleSmsTrackersFailure(SmsTracker[] smsTrackerArr, int i, int i2) {
        for (SmsTracker smsTracker : smsTrackerArr) {
            smsTracker.onFailed(this.mContext, i, i2);
        }
        if (smsTrackerArr.length > 0) {
            SmsStats smsStats = this.mPhone.getSmsStats();
            boolean isIms = isIms();
            boolean equals = "3gpp2".equals(getFormat());
            SmsTracker smsTracker2 = smsTrackerArr[0];
            smsStats.onOutgoingSms(isIms, equals, false, i, smsTracker2.mMessageId, smsTracker2.isFromDefaultSmsApplication(this.mContext), smsTrackerArr[0].getInterval());
        }
    }

    /* loaded from: classes.dex */
    public static class SmsTracker {
        private final UUID mAnomalyUnexpectedErrorFromRilUUID;
        private AtomicBoolean mAnyPartFailed;
        @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
        public final PackageInfo mAppInfo;
        private int mCarrierId;
        @UnsupportedAppUsage
        private final HashMap<String, Object> mData;
        @UnsupportedAppUsage
        public final PendingIntent mDeliveryIntent;
        @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
        public final String mDestAddress;
        public boolean mExpectMore;
        String mFormat;
        private String mFullMessageText;
        public int mImsRetry;
        private final boolean mIsForVvm;
        private Boolean mIsFromDefaultSmsApplication;
        private boolean mIsText;
        public final long mMessageId;
        @UnsupportedAppUsage
        public int mMessageRef;
        @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
        public Uri mMessageUri;
        @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
        private boolean mPersistMessage;
        public int mPriority;
        public int mRetryCount;
        @UnsupportedAppUsage
        public final PendingIntent mSentIntent;
        private boolean mSkipShortCodeDestAddrCheck;
        public final SmsHeader mSmsHeader;
        private int mSubId;
        @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
        private long mTimestamp;
        private AtomicInteger mUnsentPartCount;
        private final int mUserId;
        public boolean mUsesImsServiceForIms;
        public int mValidityPeriod;

        private SmsTracker(HashMap<String, Object> hashMap, PendingIntent pendingIntent, PendingIntent pendingIntent2, PackageInfo packageInfo, String str, String str2, AtomicInteger atomicInteger, AtomicBoolean atomicBoolean, Uri uri, SmsHeader smsHeader, boolean z, String str3, int i, boolean z2, boolean z3, int i2, int i3, int i4, boolean z4, long j, int i5, int i6, boolean z5) {
            this.mTimestamp = SystemClock.elapsedRealtime();
            this.mAnomalyUnexpectedErrorFromRilUUID = UUID.fromString("43043600-ea7a-44d2-9ae6-a58567ac7886");
            this.mData = hashMap;
            this.mSentIntent = pendingIntent;
            this.mDeliveryIntent = pendingIntent2;
            this.mRetryCount = 0;
            this.mAppInfo = packageInfo;
            this.mDestAddress = str;
            this.mFormat = str2;
            this.mExpectMore = z;
            this.mImsRetry = 0;
            this.mUsesImsServiceForIms = false;
            this.mMessageRef = i6;
            this.mUnsentPartCount = atomicInteger;
            this.mAnyPartFailed = atomicBoolean;
            this.mMessageUri = uri;
            this.mSmsHeader = smsHeader;
            this.mFullMessageText = str3;
            this.mSubId = i;
            this.mIsText = z2;
            this.mPersistMessage = z3;
            this.mUserId = i2;
            this.mPriority = i3;
            this.mValidityPeriod = i4;
            this.mIsForVvm = z4;
            this.mMessageId = j;
            this.mCarrierId = i5;
            this.mSkipShortCodeDestAddrCheck = z5;
        }

        public HashMap<String, Object> getData() {
            return this.mData;
        }

        public String getAppPackageName() {
            PackageInfo packageInfo = this.mAppInfo;
            if (packageInfo != null) {
                return packageInfo.packageName;
            }
            return null;
        }

        public ApplicationInfo getAppInfo() {
            PackageInfo packageInfo = this.mAppInfo;
            if (packageInfo == null) {
                return null;
            }
            return packageInfo.applicationInfo;
        }

        public boolean isFromDefaultSmsApplication(Context context) {
            if (this.mIsFromDefaultSmsApplication == null) {
                this.mIsFromDefaultSmsApplication = Boolean.valueOf(SmsApplication.isDefaultSmsApplicationAsUser(context, getAppPackageName(), TelephonyUtils.getSubscriptionUserHandle(context, this.mSubId)));
            }
            return this.mIsFromDefaultSmsApplication.booleanValue();
        }

        @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
        public void updateSentMessageStatus(Context context, int i) {
            if (this.mMessageUri != null) {
                ContentValues contentValues = new ContentValues(1);
                contentValues.put("status", Integer.valueOf(i));
                context.getContentResolver().update(this.mMessageUri, contentValues, null, null);
            }
        }

        private void updateMessageState(Context context, int i, int i2) {
            if (this.mMessageUri == null) {
                return;
            }
            ContentValues contentValues = new ContentValues(2);
            contentValues.put("type", Integer.valueOf(i));
            contentValues.put("error_code", Integer.valueOf(i2));
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                if (context.getContentResolver().update(this.mMessageUri, contentValues, null, null) != 1) {
                    Rlog.e("SMSDispatcher", "Failed to move message to " + i);
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public long getInterval() {
            return SystemClock.elapsedRealtime() - this.mTimestamp;
        }

        private Uri persistSentMessageIfRequired(Context context, int i, int i2) {
            if (this.mIsText && this.mPersistMessage && !isFromDefaultSmsApplication(context)) {
                StringBuilder sb = new StringBuilder();
                sb.append("Persist SMS into ");
                sb.append(i == 5 ? "FAILED" : "SENT");
                Rlog.d("SMSDispatcher", sb.toString());
                ContentValues contentValues = new ContentValues();
                contentValues.put("sub_id", Integer.valueOf(this.mSubId));
                contentValues.put("address", this.mDestAddress);
                contentValues.put("body", this.mFullMessageText);
                contentValues.put("date", Long.valueOf(System.currentTimeMillis()));
                contentValues.put("seen", (Integer) 1);
                contentValues.put("read", (Integer) 1);
                PackageInfo packageInfo = this.mAppInfo;
                String str = packageInfo != null ? packageInfo.packageName : null;
                if (!TextUtils.isEmpty(str)) {
                    contentValues.put("creator", str);
                }
                if (this.mDeliveryIntent != null) {
                    contentValues.put("status", (Integer) 32);
                }
                if (i2 != -1) {
                    contentValues.put("error_code", Integer.valueOf(i2));
                }
                long clearCallingIdentity = Binder.clearCallingIdentity();
                ContentResolver contentResolver = context.getContentResolver();
                try {
                    Uri insert = contentResolver.insert(Telephony.Sms.Sent.CONTENT_URI, contentValues);
                    if (insert != null && i == 5) {
                        ContentValues contentValues2 = new ContentValues(1);
                        contentValues2.put("type", (Integer) 5);
                        contentResolver.update(insert, contentValues2, null, null);
                    }
                    return insert;
                } catch (Exception e) {
                    Rlog.e("SMSDispatcher", "writeOutboxMessage: Failed to persist outbox message", e);
                    return null;
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            return null;
        }

        private void persistOrUpdateMessage(Context context, int i, int i2) {
            if (this.mMessageUri != null) {
                updateMessageState(context, i, i2);
            } else {
                this.mMessageUri = persistSentMessageIfRequired(context, i, i2);
            }
        }

        @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
        public void onFailed(Context context, int i, int i2) {
            AtomicBoolean atomicBoolean = this.mAnyPartFailed;
            if (atomicBoolean != null) {
                atomicBoolean.set(true);
            }
            AtomicInteger atomicInteger = this.mUnsentPartCount;
            boolean z = atomicInteger == null || atomicInteger.decrementAndGet() == 0;
            if (z) {
                persistOrUpdateMessage(context, 5, i2);
            }
            if (this.mSentIntent != null) {
                try {
                    Intent intent = new Intent();
                    Uri uri = this.mMessageUri;
                    if (uri != null) {
                        intent.putExtra("uri", uri.toString());
                    }
                    if (i2 != -1) {
                        intent.putExtra("errorCode", i2);
                    }
                    if (this.mUnsentPartCount != null && z) {
                        intent.putExtra("SendNextMsg", true);
                    }
                    long j = this.mMessageId;
                    if (j != 0) {
                        intent.putExtra("MessageId", j);
                    }
                    intent.putExtra("format", this.mFormat);
                    intent.putExtra("ims", this.mUsesImsServiceForIms);
                    this.mSentIntent.send(context, i, intent);
                } catch (PendingIntent.CanceledException unused) {
                    Rlog.e("SMSDispatcher", "Failed to send result " + SmsController.formatCrossStackMessageId(this.mMessageId));
                }
            }
            reportAnomaly(i, i2);
        }

        private void reportAnomaly(int i, int i2) {
            if (i == 2 || i == 29 || i == 4 || i == 5 || i == 7 || i == 8) {
                return;
            }
            Rlog.d("SMSDispatcher", "SMS failed with error " + i + ", errorCode " + i2);
            AnomalyReporter.reportAnomaly(generateUUID(i, i2), "SMS failed", this.mCarrierId);
        }

        private UUID generateUUID(int i, int i2) {
            return new UUID(this.mAnomalyUnexpectedErrorFromRilUUID.getMostSignificantBits(), this.mAnomalyUnexpectedErrorFromRilUUID.getLeastSignificantBits() + (i2 << 32) + i);
        }

        @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
        public void onSent(Context context) {
            AtomicInteger atomicInteger = this.mUnsentPartCount;
            boolean z = atomicInteger == null || atomicInteger.decrementAndGet() == 0;
            if (z) {
                AtomicBoolean atomicBoolean = this.mAnyPartFailed;
                persistOrUpdateMessage(context, (atomicBoolean == null || !atomicBoolean.get()) ? 2 : 5, -1);
            }
            if (this.mSentIntent != null) {
                try {
                    Intent intent = new Intent();
                    Uri uri = this.mMessageUri;
                    if (uri != null) {
                        intent.putExtra("uri", uri.toString());
                    }
                    if (this.mUnsentPartCount != null && z) {
                        intent.putExtra("SendNextMsg", true);
                    }
                    intent.putExtra("format", this.mFormat);
                    intent.putExtra("ims", this.mUsesImsServiceForIms);
                    this.mSentIntent.send(context, -1, intent);
                } catch (PendingIntent.CanceledException unused) {
                    Rlog.e("SMSDispatcher", "Failed to send result");
                }
            }
        }
    }

    protected SmsTracker getSmsTracker(String str, HashMap<String, Object> hashMap, PendingIntent pendingIntent, PendingIntent pendingIntent2, String str2, AtomicInteger atomicInteger, AtomicBoolean atomicBoolean, Uri uri, SmsHeader smsHeader, boolean z, String str3, boolean z2, boolean z3, int i, int i2, boolean z4, long j, int i3, boolean z5) {
        PackageInfo packageInfo;
        UserHandle userHandleForUid = UserHandle.getUserHandleForUid(Binder.getCallingUid());
        int identifier = userHandleForUid.getIdentifier();
        try {
            packageInfo = this.mContext.createContextAsUser(userHandleForUid, 0).getPackageManager().getPackageInfo(str, 64);
        } catch (PackageManager.NameNotFoundException unused) {
            packageInfo = null;
        }
        return new SmsTracker(hashMap, pendingIntent, pendingIntent2, packageInfo, PhoneNumberUtils.extractNetworkPortion((String) hashMap.get(MAP_KEY_DEST_ADDR)), str2, atomicInteger, atomicBoolean, uri, smsHeader, z, str3, getSubId(), z2, z3, identifier, i, i2, z4, j, this.mPhone.getCarrierId(), i3, z5);
    }

    protected SmsTracker getSmsTracker(String str, HashMap<String, Object> hashMap, PendingIntent pendingIntent, PendingIntent pendingIntent2, String str2, Uri uri, boolean z, String str3, boolean z2, boolean z3, boolean z4, long j, int i) {
        return getSmsTracker(str, hashMap, pendingIntent, pendingIntent2, str2, null, null, uri, null, z, str3, z2, z3, -1, -1, z4, j, i, false);
    }

    protected SmsTracker getSmsTracker(String str, HashMap<String, Object> hashMap, PendingIntent pendingIntent, PendingIntent pendingIntent2, String str2, Uri uri, boolean z, String str3, boolean z2, boolean z3, int i, int i2, boolean z4, long j, int i3, boolean z5) {
        return getSmsTracker(str, hashMap, pendingIntent, pendingIntent2, str2, null, null, uri, null, z, str3, z2, z3, i, i2, z4, j, i3, z5);
    }

    protected HashMap<String, Object> getSmsTrackerMap(String str, String str2, String str3, SmsMessageBase.SubmitPduBase submitPduBase) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(MAP_KEY_DEST_ADDR, str);
        hashMap.put(MAP_KEY_SC_ADDR, str2);
        hashMap.put(MAP_KEY_TEXT, str3);
        hashMap.put(MAP_KEY_SMSC, submitPduBase.encodedScAddress);
        hashMap.put(MAP_KEY_PDU, submitPduBase.encodedMessage);
        return hashMap;
    }

    protected HashMap<String, Object> getSmsTrackerMap(String str, String str2, int i, byte[] bArr, SmsMessageBase.SubmitPduBase submitPduBase) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(MAP_KEY_DEST_ADDR, str);
        hashMap.put(MAP_KEY_SC_ADDR, str2);
        hashMap.put(MAP_KEY_DEST_PORT, Integer.valueOf(i));
        hashMap.put(MAP_KEY_DATA, bArr);
        hashMap.put(MAP_KEY_SMSC, submitPduBase.encodedScAddress);
        hashMap.put(MAP_KEY_PDU, submitPduBase.encodedMessage);
        return hashMap;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class ConfirmDialogListener implements DialogInterface.OnClickListener, DialogInterface.OnCancelListener, CompoundButton.OnCheckedChangeListener {
        private int mConfirmationType;
        @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
        private Button mNegativeButton;
        @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
        private Button mPositiveButton;
        private boolean mRememberChoice;
        @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
        private final TextView mRememberUndoInstruction;
        private final SmsTracker[] mTrackers;

        ConfirmDialogListener(SmsTracker[] smsTrackerArr, TextView textView, int i) {
            this.mTrackers = smsTrackerArr;
            this.mRememberUndoInstruction = textView;
            this.mConfirmationType = i;
        }

        void setPositiveButton(Button button) {
            this.mPositiveButton = button;
        }

        void setNegativeButton(Button button) {
            this.mNegativeButton = button;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            int i2 = 1;
            if (i == -1) {
                Rlog.d("SMSDispatcher", "CONFIRM sending SMS");
                ApplicationInfo applicationInfo = this.mTrackers[0].mAppInfo.applicationInfo;
                EventLog.writeEvent((int) EventLogTags.EXP_DET_SMS_SENT_BY_USER, applicationInfo != null ? applicationInfo.uid : -1);
                SMSDispatcher sMSDispatcher = SMSDispatcher.this;
                sMSDispatcher.sendMessage(sMSDispatcher.obtainMessage(5, this.mTrackers));
                if (this.mRememberChoice) {
                    i2 = 3;
                }
            } else if (i == -2) {
                Rlog.d("SMSDispatcher", "DENY sending SMS");
                ApplicationInfo applicationInfo2 = this.mTrackers[0].mAppInfo.applicationInfo;
                EventLog.writeEvent((int) EventLogTags.EXP_DET_SMS_DENIED_BY_USER, applicationInfo2 != null ? applicationInfo2.uid : -1);
                Message obtainMessage = SMSDispatcher.this.obtainMessage(6, this.mTrackers);
                obtainMessage.arg1 = this.mConfirmationType;
                if (this.mRememberChoice) {
                    obtainMessage.arg2 = 1;
                    i2 = 2;
                }
                SMSDispatcher.this.sendMessage(obtainMessage);
            }
            SMSDispatcher.this.mSmsDispatchersController.setPremiumSmsPermission(this.mTrackers[0].getAppPackageName(), i2);
        }

        @Override // android.content.DialogInterface.OnCancelListener
        public void onCancel(DialogInterface dialogInterface) {
            Rlog.d("SMSDispatcher", "dialog dismissed: don't send SMS");
            Message obtainMessage = SMSDispatcher.this.obtainMessage(6, this.mTrackers);
            obtainMessage.arg1 = this.mConfirmationType;
            SMSDispatcher.this.sendMessage(obtainMessage);
        }

        @Override // android.widget.CompoundButton.OnCheckedChangeListener
        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            Rlog.d("SMSDispatcher", "remember this choice: " + z);
            this.mRememberChoice = z;
            if (z) {
                this.mPositiveButton.setText(17041558);
                this.mNegativeButton.setText(17041561);
                TextView textView = this.mRememberUndoInstruction;
                if (textView != null) {
                    textView.setText(17041564);
                    this.mRememberUndoInstruction.setPadding(0, 0, 0, 32);
                    return;
                }
                return;
            }
            this.mPositiveButton.setText(17041557);
            this.mNegativeButton.setText(17041559);
            TextView textView2 = this.mRememberUndoInstruction;
            if (textView2 != null) {
                textView2.setText(PhoneConfigurationManager.SSSS);
                this.mRememberUndoInstruction.setPadding(0, 0, 0, 0);
            }
        }
    }

    public boolean isIms() {
        SmsDispatchersController smsDispatchersController = this.mSmsDispatchersController;
        if (smsDispatchersController != null) {
            return smsDispatchersController.isIms();
        }
        Rlog.e("SMSDispatcher", "mSmsDispatchersController is null");
        return false;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private String getMultipartMessageText(ArrayList<String> arrayList) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = arrayList.iterator();
        while (it.hasNext()) {
            String next = it.next();
            if (next != null) {
                sb.append(next);
            }
        }
        return sb.toString();
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected String getCarrierAppPackageName() {
        CarrierPrivilegesTracker carrierPrivilegesTracker = this.mPhone.getCarrierPrivilegesTracker();
        if (carrierPrivilegesTracker == null) {
            return null;
        }
        List<String> carrierPackageNamesForIntent = carrierPrivilegesTracker.getCarrierPackageNamesForIntent(new Intent("android.service.carrier.CarrierMessagingService"));
        if (carrierPackageNamesForIntent != null && carrierPackageNamesForIntent.size() == 1) {
            return carrierPackageNamesForIntent.get(0);
        }
        return CarrierSmsUtils.getImsRcsPackageForIntent(this.mContext, this.mPhone, new Intent("android.service.carrier.CarrierMessagingService"));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public int getSubId() {
        return SubscriptionManager.getSubscriptionId(this.mPhone.getPhoneId());
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private void checkCallerIsPhoneOrCarrierApp() {
        int callingUid = Binder.getCallingUid();
        if (UserHandle.getAppId(callingUid) == 1001 || callingUid == 0) {
            return;
        }
        try {
            if (UserHandle.getAppId(this.mContext.getPackageManager().getApplicationInfo(getCarrierAppPackageName(), 0).uid) == UserHandle.getAppId(Binder.getCallingUid())) {
                return;
            }
            throw new SecurityException("Caller is not phone or carrier app!");
        } catch (PackageManager.NameNotFoundException unused) {
            throw new SecurityException("Caller is not phone or carrier app!");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isCdmaMo() {
        return this.mSmsDispatchersController.isCdmaMo();
    }

    private boolean isAscii7bitSupportedForLongMessage() {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            PersistableBundle configForSubId = ((CarrierConfigManager) this.mContext.getSystemService("carrier_config")).getConfigForSubId(this.mPhone.getSubId());
            if (configForSubId != null) {
                return configForSubId.getBoolean("ascii_7_bit_support_for_long_message_bool");
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return false;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        AndroidUtilIndentingPrintWriter androidUtilIndentingPrintWriter = new AndroidUtilIndentingPrintWriter(printWriter, "  ");
        androidUtilIndentingPrintWriter.println("SMSDispatcher");
        androidUtilIndentingPrintWriter.increaseIndent();
        androidUtilIndentingPrintWriter.println("mLocalLog:");
        androidUtilIndentingPrintWriter.increaseIndent();
        this.mLocalLog.dump(fileDescriptor, androidUtilIndentingPrintWriter, strArr);
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.println("mSmsOutgoingErrorCodes:");
        androidUtilIndentingPrintWriter.increaseIndent();
        this.mSmsOutgoingErrorCodes.dump(fileDescriptor, androidUtilIndentingPrintWriter, strArr);
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.decreaseIndent();
    }
}
