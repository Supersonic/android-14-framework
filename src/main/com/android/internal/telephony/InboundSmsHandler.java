package com.android.internal.telephony;

import android.app.BroadcastOptions;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.compat.annotation.UnsupportedAppUsage;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.AsyncResult;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerWhitelistManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.StorageManager;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Pair;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.CarrierServicesSmsFilter;
import com.android.internal.telephony.InboundSmsHandler;
import com.android.internal.telephony.SmsConstants;
import com.android.internal.telephony.SmsDispatchersController;
import com.android.internal.telephony.SmsHeader;
import com.android.internal.telephony.metrics.TelephonyMetrics;
import com.android.internal.telephony.util.NotificationChannelController;
import com.android.internal.telephony.util.TelephonyUtils;
import com.android.telephony.Rlog;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
/* loaded from: classes.dex */
public abstract class InboundSmsHandler extends StateMachine {
    public static final int ADDRESS_COLUMN = 6;
    public static final int COUNT_COLUMN = 5;
    public static final int DATE_COLUMN = 3;
    protected static final boolean DBG = true;
    public static final int DELETED_FLAG_COLUMN = 10;
    public static final int DESTINATION_PORT_COLUMN = 2;
    public static final int DISPLAY_ADDRESS_COLUMN = 9;
    public static final int EVENT_BROADCAST_COMPLETE = 3;
    public static final int EVENT_BROADCAST_SMS = 2;
    public static final int EVENT_INJECT_SMS = 7;
    public static final int EVENT_NEW_SMS = 1;
    public static final int EVENT_RECEIVER_TIMEOUT = 9;
    public static final int EVENT_START_ACCEPTING_SMS = 6;
    public static final int EVENT_UPDATE_TRACKER = 8;
    public static final int ID_COLUMN = 7;
    public static final int MESSAGE_BODY_COLUMN = 8;
    @VisibleForTesting
    public static final int NOTIFICATION_ID_NEW_MESSAGE = 1;
    @VisibleForTesting
    public static final String NOTIFICATION_TAG = "InboundSmsHandler";
    public static final int PDU_COLUMN = 0;
    public static final int REFERENCE_NUMBER_COLUMN = 4;
    public static final String SELECT_BY_ID = "_id=?";
    public static final int SEQUENCE_COLUMN = 1;
    public static final int SOURCE_INJECTED_FROM_IMS = 1;
    public static final int SOURCE_INJECTED_FROM_UNKNOWN = 2;
    public static final int SOURCE_NOT_INJECTED = 0;
    public static final int SUBID_COLUMN = 11;
    protected static final boolean VDBG = false;
    private final int DELETE_PERMANENTLY;
    private final int MARK_DELETED;
    private LocalLog mCarrierServiceLocalLog;
    protected CellBroadcastServiceManager mCellBroadcastServiceManager;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected final Context mContext;
    private final DefaultState mDefaultState;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private final DeliveringState mDeliveringState;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private final IdleState mIdleState;
    private LocalLog mLocalLog;
    protected TelephonyMetrics mMetrics;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected Phone mPhone;
    PowerWhitelistManager mPowerWhitelistManager;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private final ContentResolver mResolver;
    private List<SmsFilter> mSmsFilters;
    private final boolean mSmsReceiveDisabled;
    private final StartupState mStartupState;
    protected SmsStorageMonitor mStorageMonitor;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private UserManager mUserManager;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private final WaitingState mWaitingState;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private final PowerManager.WakeLock mWakeLock;
    private int mWakeLockTimeout;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private final WapPushOverSms mWapPush;
    private static final String[] PDU_DELETED_FLAG_PROJECTION = {"pdu", "deleted"};
    private static final Map<Integer, Integer> PDU_DELETED_FLAG_PROJECTION_INDEX_MAPPING = Map.of(0, 0, 10, 1);
    private static final String[] PDU_SEQUENCE_PORT_PROJECTION = {"pdu", "sequence", "destination_port", "display_originating_addr", "date"};
    private static final Map<Integer, Integer> PDU_SEQUENCE_PORT_PROJECTION_INDEX_MAPPING = Map.of(0, 0, 1, 1, 2, 2, 9, 3, 3, 4);
    protected static final Uri sRawUri = Uri.withAppendedPath(Telephony.Sms.CONTENT_URI, "raw");
    protected static final Uri sRawUriPermanentDelete = Uri.withAppendedPath(Telephony.Sms.CONTENT_URI, "raw/permanentDelete");
    private static String ACTION_OPEN_SMS_APP = "com.android.internal.telephony.OPEN_DEFAULT_SMS_APP";
    @VisibleForTesting
    public static int sTimeoutDurationMillis = CarrierServicesSmsFilter.FILTER_COMPLETE_TIMEOUT_MS;

    @VisibleForTesting
    /* loaded from: classes.dex */
    public interface SmsFilter {
        boolean filterSms(byte[][] bArr, int i, InboundSmsTracker inboundSmsTracker, SmsBroadcastReceiver smsBroadcastReceiver, boolean z, boolean z2, List<SmsFilter> list);
    }

    /* JADX INFO: Access modifiers changed from: private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean isSkipNotifyFlagSet(int i) {
        return (i & 2) > 0;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected abstract void acknowledgeLastIncomingSms(boolean z, int i, Message message);

    protected abstract int dispatchMessageRadioSpecific(SmsMessageBase smsMessageBase, int i, int i2);

    protected abstract boolean is3gpp2();

    /* JADX INFO: Access modifiers changed from: protected */
    public InboundSmsHandler(String str, Context context, SmsStorageMonitor smsStorageMonitor, Phone phone, Looper looper) {
        super(str, looper);
        DefaultState defaultState = new DefaultState();
        this.mDefaultState = defaultState;
        StartupState startupState = new StartupState();
        this.mStartupState = startupState;
        IdleState idleState = new IdleState();
        this.mIdleState = idleState;
        DeliveringState deliveringState = new DeliveringState();
        this.mDeliveringState = deliveringState;
        WaitingState waitingState = new WaitingState();
        this.mWaitingState = waitingState;
        this.mMetrics = TelephonyMetrics.getInstance();
        this.mLocalLog = new LocalLog(64);
        this.mCarrierServiceLocalLog = new LocalLog(8);
        this.DELETE_PERMANENTLY = 1;
        this.MARK_DELETED = 2;
        this.mContext = context;
        this.mStorageMonitor = smsStorageMonitor;
        this.mPhone = phone;
        this.mResolver = context.getContentResolver();
        this.mWapPush = new WapPushOverSms(context);
        this.mSmsReceiveDisabled = !TelephonyManager.from(context).getSmsReceiveCapableForPhone(this.mPhone.getPhoneId(), context.getResources().getBoolean(17891804));
        PowerManager.WakeLock newWakeLock = ((PowerManager) context.getSystemService("power")).newWakeLock(1, str);
        this.mWakeLock = newWakeLock;
        newWakeLock.acquire();
        this.mUserManager = (UserManager) context.getSystemService("user");
        this.mPowerWhitelistManager = (PowerWhitelistManager) context.getSystemService("power_whitelist");
        this.mCellBroadcastServiceManager = new CellBroadcastServiceManager(context, phone);
        this.mSmsFilters = createDefaultSmsFilters();
        addState(defaultState);
        addState(startupState, defaultState);
        addState(idleState, defaultState);
        addState(deliveringState, defaultState);
        addState(waitingState, deliveringState);
        setInitialState(startupState);
        log("created InboundSmsHandler");
    }

    public void dispose() {
        quit();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.StateMachine
    public void onQuitting() {
        this.mWapPush.dispose();
        this.mCellBroadcastServiceManager.disable();
        while (this.mWakeLock.isHeld()) {
            this.mWakeLock.release();
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public Phone getPhone() {
        return this.mPhone;
    }

    @Override // com.android.internal.telephony.StateMachine
    protected String getWhatToString(int i) {
        switch (i) {
            case 1:
                return "EVENT_NEW_SMS";
            case 2:
                return "EVENT_BROADCAST_SMS";
            case 3:
                return "EVENT_BROADCAST_COMPLETE";
            case 4:
                return "EVENT_RETURN_TO_IDLE";
            case 5:
                return "EVENT_RELEASE_WAKELOCK";
            case 6:
                return "EVENT_START_ACCEPTING_SMS";
            case 7:
                return "EVENT_INJECT_SMS";
            case 8:
                return "EVENT_UPDATE_TRACKER";
            case 9:
                return "EVENT_RECEIVER_TIMEOUT";
            default:
                return "UNKNOWN EVENT " + i;
        }
    }

    /* loaded from: classes.dex */
    private class DefaultState extends State {
        private DefaultState() {
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public boolean processMessage(Message message) {
            int i = message.what;
            String str = "processMessage: unhandled message type " + InboundSmsHandler.this.getWhatToString(message.what) + " currState=" + InboundSmsHandler.this.getCurrentState().getName();
            if (TelephonyUtils.IS_DEBUGGABLE) {
                InboundSmsHandler.this.loge("---- Dumping InboundSmsHandler ----");
                InboundSmsHandler.this.loge("Total records=" + InboundSmsHandler.this.getLogRecCount());
                for (int max = Math.max(InboundSmsHandler.this.getLogRecSize() + (-20), 0); max < InboundSmsHandler.this.getLogRecSize(); max++) {
                    InboundSmsHandler.this.loge("Rec[%d]: %s\n" + max + InboundSmsHandler.this.getLogRec(max).toString());
                }
                InboundSmsHandler.this.loge("---- Dumped InboundSmsHandler ----");
                throw new RuntimeException(str);
            }
            InboundSmsHandler.this.loge(str);
            return true;
        }
    }

    /* loaded from: classes.dex */
    private class StartupState extends State {
        private StartupState() {
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public void enter() {
            InboundSmsHandler.this.log("StartupState.enter: entering StartupState");
            InboundSmsHandler.this.setWakeLockTimeout(0);
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public boolean processMessage(Message message) {
            InboundSmsHandler inboundSmsHandler = InboundSmsHandler.this;
            inboundSmsHandler.log("StartupState.processMessage: processing " + InboundSmsHandler.this.getWhatToString(message.what));
            int i = message.what;
            if (i != 1 && i != 2) {
                if (i == 6) {
                    InboundSmsHandler inboundSmsHandler2 = InboundSmsHandler.this;
                    inboundSmsHandler2.transitionTo(inboundSmsHandler2.mIdleState);
                    return true;
                } else if (i != 7) {
                    return false;
                }
            }
            InboundSmsHandler.this.deferMessage(message);
            return true;
        }
    }

    /* loaded from: classes.dex */
    private class IdleState extends State {
        private IdleState() {
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public void enter() {
            InboundSmsHandler.this.log("IdleState.enter: entering IdleState");
            InboundSmsHandler inboundSmsHandler = InboundSmsHandler.this;
            inboundSmsHandler.sendMessageDelayed(5, inboundSmsHandler.getWakeLockTimeout());
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public void exit() {
            InboundSmsHandler.this.mWakeLock.acquire();
            InboundSmsHandler.this.log("IdleState.exit: acquired wakelock, leaving IdleState");
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public boolean processMessage(Message message) {
            InboundSmsHandler inboundSmsHandler = InboundSmsHandler.this;
            inboundSmsHandler.log("IdleState.processMessage: processing " + InboundSmsHandler.this.getWhatToString(message.what));
            int i = message.what;
            if (i != 1 && i != 2) {
                if (i != 4) {
                    if (i == 5) {
                        InboundSmsHandler.this.mWakeLock.release();
                        if (InboundSmsHandler.this.mWakeLock.isHeld()) {
                            InboundSmsHandler.this.log("IdleState.processMessage: EVENT_RELEASE_WAKELOCK: mWakeLock is still held after release");
                        } else {
                            InboundSmsHandler.this.log("IdleState.processMessage: EVENT_RELEASE_WAKELOCK: mWakeLock released");
                        }
                    } else if (i != 7) {
                        return false;
                    }
                }
                return true;
            }
            InboundSmsHandler.this.deferMessage(message);
            InboundSmsHandler inboundSmsHandler2 = InboundSmsHandler.this;
            inboundSmsHandler2.transitionTo(inboundSmsHandler2.mDeliveringState);
            return true;
        }
    }

    /* loaded from: classes.dex */
    private class DeliveringState extends State {
        private DeliveringState() {
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public void enter() {
            InboundSmsHandler.this.log("DeliveringState.enter: entering DeliveringState");
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public void exit() {
            InboundSmsHandler.this.log("DeliveringState.exit: leaving DeliveringState");
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public boolean processMessage(Message message) {
            InboundSmsHandler inboundSmsHandler = InboundSmsHandler.this;
            inboundSmsHandler.log("DeliveringState.processMessage: processing " + InboundSmsHandler.this.getWhatToString(message.what));
            int i = message.what;
            if (i == 1) {
                InboundSmsHandler.this.handleNewSms((AsyncResult) message.obj);
                InboundSmsHandler.this.sendMessage(4);
                return true;
            } else if (i == 2) {
                if (InboundSmsHandler.this.processMessagePart((InboundSmsTracker) message.obj)) {
                    InboundSmsHandler inboundSmsHandler2 = InboundSmsHandler.this;
                    inboundSmsHandler2.sendMessage(inboundSmsHandler2.obtainMessage(8, message.obj));
                    InboundSmsHandler inboundSmsHandler3 = InboundSmsHandler.this;
                    inboundSmsHandler3.transitionTo(inboundSmsHandler3.mWaitingState);
                } else {
                    InboundSmsHandler.this.log("DeliveringState.processMessage: EVENT_BROADCAST_SMS: No broadcast sent. Return to IdleState");
                    InboundSmsHandler.this.sendMessage(4);
                }
                return true;
            } else if (i == 4) {
                InboundSmsHandler inboundSmsHandler4 = InboundSmsHandler.this;
                inboundSmsHandler4.transitionTo(inboundSmsHandler4.mIdleState);
                return true;
            } else if (i == 5) {
                InboundSmsHandler.this.mWakeLock.release();
                if (!InboundSmsHandler.this.mWakeLock.isHeld()) {
                    InboundSmsHandler.this.loge("mWakeLock released while delivering/broadcasting!");
                }
                return true;
            } else {
                if (i == 7) {
                    InboundSmsHandler.this.handleInjectSms((AsyncResult) message.obj, message.arg1 == 1, message.arg2);
                    InboundSmsHandler.this.sendMessage(4);
                    return true;
                } else if (i == 8) {
                    InboundSmsHandler inboundSmsHandler5 = InboundSmsHandler.this;
                    inboundSmsHandler5.logd("process tracker message in DeliveringState " + message.arg1);
                    return true;
                } else {
                    InboundSmsHandler inboundSmsHandler6 = InboundSmsHandler.this;
                    inboundSmsHandler6.logeWithLocalLog("Unhandled msg in delivering state, msg.what = " + InboundSmsHandler.this.getWhatToString(message.what));
                    return false;
                }
            }
        }
    }

    /* loaded from: classes.dex */
    private class WaitingState extends State {
        private InboundSmsTracker mLastDeliveredSmsTracker;

        private WaitingState() {
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public void enter() {
            InboundSmsHandler.this.log("WaitingState.enter: entering WaitingState");
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public void exit() {
            InboundSmsHandler.this.log("WaitingState.exit: leaving WaitingState");
            InboundSmsHandler.this.setWakeLockTimeout(GbaManager.RETRY_TIME_MS);
            InboundSmsHandler.this.mPhone.getIccSmsInterfaceManager().mDispatchersController.sendEmptyMessage(17);
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public boolean processMessage(Message message) {
            InboundSmsHandler inboundSmsHandler = InboundSmsHandler.this;
            inboundSmsHandler.log("WaitingState.processMessage: processing " + InboundSmsHandler.this.getWhatToString(message.what));
            int i = message.what;
            if (i == 2) {
                if (this.mLastDeliveredSmsTracker != null) {
                    InboundSmsHandler.this.logWithLocalLog("Defer sms broadcast due to undelivered sms,  messageCount = " + this.mLastDeliveredSmsTracker.getMessageCount() + " destPort = " + this.mLastDeliveredSmsTracker.getDestPort() + " timestamp = " + this.mLastDeliveredSmsTracker.getTimestamp() + " currentTimestamp = " + System.currentTimeMillis(), this.mLastDeliveredSmsTracker.getMessageId());
                }
                InboundSmsHandler.this.deferMessage(message);
                return true;
            } else if (i == 3) {
                this.mLastDeliveredSmsTracker = null;
                InboundSmsHandler.this.sendMessage(4);
                InboundSmsHandler inboundSmsHandler2 = InboundSmsHandler.this;
                inboundSmsHandler2.transitionTo(inboundSmsHandler2.mDeliveringState);
                return true;
            } else {
                if (i != 4) {
                    if (i != 8) {
                        if (i != 9) {
                            return false;
                        }
                        InboundSmsHandler.this.logeWithLocalLog("WaitingState.processMessage: received EVENT_RECEIVER_TIMEOUT");
                        InboundSmsTracker inboundSmsTracker = this.mLastDeliveredSmsTracker;
                        if (inboundSmsTracker != null) {
                            inboundSmsTracker.getSmsBroadcastReceiver(InboundSmsHandler.this).fakeNextAction();
                        }
                        return true;
                    }
                    this.mLastDeliveredSmsTracker = (InboundSmsTracker) message.obj;
                }
                return true;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void handleNewSms(AsyncResult asyncResult) {
        int i;
        if (asyncResult.exception != null) {
            loge("Exception processing incoming SMS: " + asyncResult.exception);
            return;
        }
        try {
            i = dispatchMessage(((SmsMessage) asyncResult.result).mWrappedSmsMessage, 0, 0);
        } catch (RuntimeException e) {
            loge("Exception dispatching message", e);
            i = 6;
        }
        if (i != -1) {
            notifyAndAcknowledgeLastIncomingSms(i == 1, i, null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:17:0x002b  */
    /* JADX WARN: Removed duplicated region for block: B:24:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r1v0 */
    /* JADX WARN: Type inference failed for: r1v1, types: [com.android.internal.telephony.SmsDispatchersController$SmsInjectionCallback] */
    /* JADX WARN: Type inference failed for: r1v3, types: [com.android.internal.telephony.SmsDispatchersController$SmsInjectionCallback] */
    /* JADX WARN: Type inference failed for: r2v0, types: [com.android.internal.telephony.InboundSmsHandler] */
    /* JADX WARN: Type inference failed for: r2v1, types: [com.android.internal.telephony.InboundSmsHandler] */
    /* JADX WARN: Type inference failed for: r2v2 */
    /* JADX WARN: Type inference failed for: r2v3, types: [int] */
    /* JADX WARN: Type inference failed for: r2v4 */
    /* JADX WARN: Type inference failed for: r2v6 */
    /* JADX WARN: Type inference failed for: r2v7 */
    /* JADX WARN: Type inference failed for: r2v8 */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void handleInjectSms(AsyncResult asyncResult, boolean z, int i) {
        ?? r1;
        Object obj = null;
        try {
            r1 = (SmsDispatchersController.SmsInjectionCallback) asyncResult.userObj;
        } catch (RuntimeException e) {
            e = e;
        }
        try {
            SmsMessage smsMessage = (SmsMessage) asyncResult.result;
            if (smsMessage == null) {
                loge("Null injected sms");
                this = 7;
            } else {
                this = dispatchMessage(smsMessage.mWrappedSmsMessage, z ? 1 : 2, i);
            }
        } catch (RuntimeException e2) {
            e = e2;
            obj = r1;
            this.loge("Exception dispatching message", e);
            this = 6;
            r1 = obj;
            if (r1 == 0) {
            }
        }
        if (r1 == 0) {
            r1.onSmsInjectedResult(this);
        }
    }

    private int dispatchMessage(SmsMessageBase smsMessageBase, int i, int i2) {
        if (smsMessageBase == null) {
            loge("dispatchSmsMessage: message is null");
            return 8;
        } else if (this.mSmsReceiveDisabled) {
            log("Received short message on device which doesn't support receiving SMS. Ignored.");
            return 1;
        } else {
            int dispatchMessageRadioSpecific = dispatchMessageRadioSpecific(smsMessageBase, i, i2);
            if (dispatchMessageRadioSpecific != 1 && dispatchMessageRadioSpecific != -1) {
                this.mMetrics.writeIncomingSmsError(this.mPhone.getPhoneId(), is3gpp2(), i, dispatchMessageRadioSpecific);
                this.mPhone.getSmsStats().onIncomingSmsError(is3gpp2(), i, dispatchMessageRadioSpecific);
            }
            return dispatchMessageRadioSpecific;
        }
    }

    private void notifyAndAcknowledgeLastIncomingSms(boolean z, int i, Message message) {
        if (!z) {
            Intent intent = new Intent("android.provider.Telephony.SMS_REJECTED");
            intent.putExtra("result", i);
            intent.putExtra(CallWaitingController.KEY_SUB_ID, this.mPhone.getSubId());
            this.mContext.sendBroadcast(intent, "android.permission.RECEIVE_SMS");
        }
        acknowledgeLastIncomingSms(z, i, message);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public int dispatchNormalMessage(SmsMessageBase smsMessageBase, int i) {
        int i2;
        InboundSmsTracker makeInboundSmsTracker;
        SmsHeader.PortAddrs portAddrs;
        SmsHeader.ConcatRef concatRef;
        SmsHeader userDataHeader = smsMessageBase.getUserDataHeader();
        if (userDataHeader == null || (concatRef = userDataHeader.concatRef) == null) {
            if (userDataHeader == null || (portAddrs = userDataHeader.portAddrs) == null) {
                i2 = -1;
            } else {
                int i3 = portAddrs.destPort;
                log("destination port: " + i3);
                i2 = i3;
            }
            makeInboundSmsTracker = TelephonyComponentFactory.getInstance().inject(InboundSmsTracker.class.getName()).makeInboundSmsTracker(this.mContext, smsMessageBase.getPdu(), smsMessageBase.getTimestampMillis(), i2, is3gpp2(), false, smsMessageBase.getOriginatingAddress(), smsMessageBase.getDisplayOriginatingAddress(), smsMessageBase.getMessageBody(), smsMessageBase.getMessageClass() == SmsConstants.MessageClass.CLASS_0, this.mPhone.getSubId(), i);
        } else {
            SmsHeader.PortAddrs portAddrs2 = userDataHeader.portAddrs;
            makeInboundSmsTracker = TelephonyComponentFactory.getInstance().inject(InboundSmsTracker.class.getName()).makeInboundSmsTracker(this.mContext, smsMessageBase.getPdu(), smsMessageBase.getTimestampMillis(), portAddrs2 != null ? portAddrs2.destPort : -1, is3gpp2(), smsMessageBase.getOriginatingAddress(), smsMessageBase.getDisplayOriginatingAddress(), concatRef.refNumber, concatRef.seqNumber, concatRef.msgCount, false, smsMessageBase.getMessageBody(), smsMessageBase.getMessageClass() == SmsConstants.MessageClass.CLASS_0, this.mPhone.getSubId(), i);
        }
        return addTrackerToRawTableAndSendMessage(makeInboundSmsTracker, makeInboundSmsTracker.getDestPort() == -1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int addTrackerToRawTableAndSendMessage(InboundSmsTracker inboundSmsTracker, boolean z) {
        int addTrackerToRawTable = addTrackerToRawTable(inboundSmsTracker, z);
        if (addTrackerToRawTable == 1) {
            sendMessage(2, inboundSmsTracker);
            return 1;
        } else if (addTrackerToRawTable != 5) {
            return addTrackerToRawTable;
        } else {
            return 1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean processMessagePart(InboundSmsTracker inboundSmsTracker) {
        String num;
        Cursor query;
        int i;
        long[] jArr;
        boolean z;
        byte[][] bArr;
        int realDestPort;
        int messageCount = inboundSmsTracker.getMessageCount();
        int destPort = inboundSmsTracker.getDestPort();
        String address = inboundSmsTracker.getAddress();
        if (messageCount <= 0) {
            loge("processMessagePart: returning false due to invalid message count " + messageCount, inboundSmsTracker.getMessageId());
            return false;
        }
        Cursor cursor = null;
        int i2 = 1;
        if (messageCount == 1) {
            long[] jArr2 = {inboundSmsTracker.getTimestamp()};
            i = destPort;
            bArr = new byte[][]{inboundSmsTracker.getPdu()};
            jArr = jArr2;
            z = BlockChecker.isBlocked(this.mContext, inboundSmsTracker.getDisplayAddress(), null);
        } else {
            try {
                try {
                    num = Integer.toString(inboundSmsTracker.getReferenceNumber());
                    query = this.mResolver.query(sRawUri, PDU_SEQUENCE_PORT_PROJECTION, inboundSmsTracker.getQueryForSegments(), new String[]{address, num, Integer.toString(inboundSmsTracker.getMessageCount())}, null);
                } catch (SQLException e) {
                    e = e;
                }
            } catch (Throwable th) {
                th = th;
            }
            try {
                int count = query.getCount();
                if (count < messageCount) {
                    log("processMessagePart: returning false. Only " + count + " of " + messageCount + " segments  have arrived. refNumber: " + num, inboundSmsTracker.getMessageId());
                    query.close();
                    return false;
                }
                byte[][] bArr2 = new byte[messageCount];
                long[] jArr3 = new long[messageCount];
                boolean z2 = false;
                while (query.moveToNext()) {
                    Map<Integer, Integer> map = PDU_SEQUENCE_PORT_PROJECTION_INDEX_MAPPING;
                    int i3 = query.getInt(map.get(Integer.valueOf(i2)).intValue()) - inboundSmsTracker.getIndexOffset();
                    if (i3 < messageCount && i3 >= 0) {
                        bArr2[i3] = HexDump.hexStringToByteArray(query.getString(map.get(0).intValue()));
                        if (i3 == 0 && !query.isNull(map.get(2).intValue()) && (realDestPort = InboundSmsTracker.getRealDestPort(query.getInt(map.get(2).intValue()))) != -1) {
                            destPort = realDestPort;
                        }
                        jArr3[i3] = query.getLong(map.get(3).intValue());
                        if (!z2) {
                            z2 = BlockChecker.isBlocked(this.mContext, query.getString(map.get(9).intValue()), null);
                        }
                        i2 = 1;
                    }
                    loge(String.format("processMessagePart: invalid seqNumber = %d, messageCount = %d", Integer.valueOf(i3 + inboundSmsTracker.getIndexOffset()), Integer.valueOf(messageCount)), inboundSmsTracker.getMessageId());
                    i2 = 1;
                }
                log("processMessagePart: all " + messageCount + " segments  received. refNumber: " + num, inboundSmsTracker.getMessageId());
                query.close();
                i = destPort;
                jArr = jArr3;
                z = z2;
                bArr = bArr2;
            } catch (SQLException e2) {
                e = e2;
                cursor = query;
                loge("processMessagePart: Can't access multipart SMS database, " + SmsController.formatCrossStackMessageId(inboundSmsTracker.getMessageId()), e);
                if (cursor != null) {
                    cursor.close();
                }
                return false;
            } catch (Throwable th2) {
                th = th2;
                cursor = query;
                if (cursor != null) {
                    cursor.close();
                }
                throw th;
            }
        }
        boolean z3 = i == 2948;
        String format = inboundSmsTracker.getFormat();
        List asList = Arrays.asList(bArr);
        if (asList.size() == 0 || asList.contains(null)) {
            StringBuilder sb = new StringBuilder();
            sb.append("processMessagePart: returning false due to ");
            sb.append(asList.size() == 0 ? "pduList.size() == 0" : "pduList.contains(null)");
            logeWithLocalLog(sb.toString(), inboundSmsTracker.getMessageId());
            this.mPhone.getSmsStats().onIncomingSmsError(is3gpp2(), inboundSmsTracker.getSource(), 7);
            return false;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (z3) {
            for (byte[] bArr3 : bArr) {
                if (format == "3gpp") {
                    SmsMessage createFromPdu = SmsMessage.createFromPdu(bArr3, "3gpp");
                    if (createFromPdu != null) {
                        bArr3 = createFromPdu.getUserData();
                    } else {
                        loge("processMessagePart: SmsMessage.createFromPdu returned null", inboundSmsTracker.getMessageId());
                        this.mMetrics.writeIncomingWapPush(this.mPhone.getPhoneId(), inboundSmsTracker.getSource(), "3gpp", jArr, false, inboundSmsTracker.getMessageId());
                        this.mPhone.getSmsStats().onIncomingSmsWapPush(inboundSmsTracker.getSource(), messageCount, 8, inboundSmsTracker.getMessageId());
                        return false;
                    }
                }
                byteArrayOutputStream.write(bArr3, 0, bArr3.length);
            }
        }
        SmsBroadcastReceiver smsBroadcastReceiver = inboundSmsTracker.getSmsBroadcastReceiver(this);
        if (!this.mUserManager.isUserUnlocked()) {
            log("processMessagePart: !isUserUnlocked; calling processMessagePartWithUserLocked. Port: " + i, inboundSmsTracker.getMessageId());
            return processMessagePartWithUserLocked(inboundSmsTracker, z3 ? new byte[][]{byteArrayOutputStream.toByteArray()} : bArr, i, smsBroadcastReceiver, z);
        } else if (z3) {
            int dispatchWapPdu = this.mWapPush.dispatchWapPdu(byteArrayOutputStream.toByteArray(), smsBroadcastReceiver, this, address, inboundSmsTracker.getSubId(), inboundSmsTracker.getMessageId());
            log("processMessagePart: dispatchWapPdu() returned " + dispatchWapPdu, inboundSmsTracker.getMessageId());
            this.mMetrics.writeIncomingWapPush(this.mPhone.getPhoneId(), inboundSmsTracker.getSource(), format, jArr, dispatchWapPdu == -1 || dispatchWapPdu == 1, inboundSmsTracker.getMessageId());
            this.mPhone.getSmsStats().onIncomingSmsWapPush(inboundSmsTracker.getSource(), messageCount, dispatchWapPdu, inboundSmsTracker.getMessageId());
            if (dispatchWapPdu == -1) {
                return true;
            }
            deleteFromRawTable(inboundSmsTracker.getDeleteWhere(), inboundSmsTracker.getDeleteWhereArgs(), 2);
            loge("processMessagePart: returning false as the ordered broadcast for WAP push was not sent", inboundSmsTracker.getMessageId());
            return false;
        } else {
            this.mMetrics.writeIncomingSmsSession(this.mPhone.getPhoneId(), inboundSmsTracker.getSource(), format, jArr, z, inboundSmsTracker.getMessageId());
            this.mPhone.getSmsStats().onIncomingSmsSuccess(is3gpp2(), inboundSmsTracker.getSource(), messageCount, z, inboundSmsTracker.getMessageId());
            if (filterSms(bArr, i, inboundSmsTracker, smsBroadcastReceiver, true, z)) {
                return true;
            }
            if (z) {
                deleteFromRawTable(inboundSmsTracker.getDeleteWhere(), inboundSmsTracker.getDeleteWhereArgs(), 1);
                log("processMessagePart: returning false as the phone number is blocked", inboundSmsTracker.getMessageId());
                return false;
            }
            dispatchSmsDeliveryIntent(bArr, format, i, smsBroadcastReceiver, inboundSmsTracker.isClass0(), inboundSmsTracker.getSubId(), inboundSmsTracker.getMessageId());
            return true;
        }
    }

    private boolean processMessagePartWithUserLocked(InboundSmsTracker inboundSmsTracker, byte[][] bArr, int i, SmsBroadcastReceiver smsBroadcastReceiver, boolean z) {
        if (i == 2948 && this.mWapPush.isWapPushForMms(bArr[0], this)) {
            showNewMessageNotification();
            return false;
        }
        if (i == -1) {
            if (filterSms(bArr, i, inboundSmsTracker, smsBroadcastReceiver, false, z)) {
                return true;
            }
            if (!z) {
                showNewMessageNotification();
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void showNewMessageNotification() {
        if (StorageManager.isFileEncrypted()) {
            log("Show new message notification.");
            ((NotificationManager) this.mContext.getSystemService("notification")).notify(NOTIFICATION_TAG, 1, new Notification.Builder(this.mContext).setSmallIcon(17301646).setAutoCancel(true).setVisibility(1).setDefaults(-1).setContentTitle(this.mContext.getString(17040843)).setContentText(this.mContext.getString(17040842)).setContentIntent(PendingIntent.getBroadcast(this.mContext, 0, new Intent(ACTION_OPEN_SMS_APP), 1140850688)).setChannelId(NotificationChannelController.CHANNEL_ID_SMS).build());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void cancelNewMessageNotification(Context context) {
        ((NotificationManager) context.getSystemService("notification")).cancel(NOTIFICATION_TAG, 1);
    }

    private List<SmsFilter> createDefaultSmsFilters() {
        ArrayList arrayList = new ArrayList(3);
        arrayList.add(new SmsFilter() { // from class: com.android.internal.telephony.InboundSmsHandler$$ExternalSyntheticLambda0
            @Override // com.android.internal.telephony.InboundSmsHandler.SmsFilter
            public final boolean filterSms(byte[][] bArr, int i, InboundSmsTracker inboundSmsTracker, InboundSmsHandler.SmsBroadcastReceiver smsBroadcastReceiver, boolean z, boolean z2, List list) {
                boolean lambda$createDefaultSmsFilters$0;
                lambda$createDefaultSmsFilters$0 = InboundSmsHandler.this.lambda$createDefaultSmsFilters$0(bArr, i, inboundSmsTracker, smsBroadcastReceiver, z, z2, list);
                return lambda$createDefaultSmsFilters$0;
            }
        });
        arrayList.add(new SmsFilter() { // from class: com.android.internal.telephony.InboundSmsHandler$$ExternalSyntheticLambda1
            @Override // com.android.internal.telephony.InboundSmsHandler.SmsFilter
            public final boolean filterSms(byte[][] bArr, int i, InboundSmsTracker inboundSmsTracker, InboundSmsHandler.SmsBroadcastReceiver smsBroadcastReceiver, boolean z, boolean z2, List list) {
                boolean lambda$createDefaultSmsFilters$1;
                lambda$createDefaultSmsFilters$1 = InboundSmsHandler.this.lambda$createDefaultSmsFilters$1(bArr, i, inboundSmsTracker, smsBroadcastReceiver, z, z2, list);
                return lambda$createDefaultSmsFilters$1;
            }
        });
        arrayList.add(new SmsFilter() { // from class: com.android.internal.telephony.InboundSmsHandler$$ExternalSyntheticLambda2
            @Override // com.android.internal.telephony.InboundSmsHandler.SmsFilter
            public final boolean filterSms(byte[][] bArr, int i, InboundSmsTracker inboundSmsTracker, InboundSmsHandler.SmsBroadcastReceiver smsBroadcastReceiver, boolean z, boolean z2, List list) {
                boolean lambda$createDefaultSmsFilters$2;
                lambda$createDefaultSmsFilters$2 = InboundSmsHandler.this.lambda$createDefaultSmsFilters$2(bArr, i, inboundSmsTracker, smsBroadcastReceiver, z, z2, list);
                return lambda$createDefaultSmsFilters$2;
            }
        });
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createDefaultSmsFilters$0(byte[][] bArr, int i, InboundSmsTracker inboundSmsTracker, SmsBroadcastReceiver smsBroadcastReceiver, boolean z, boolean z2, List list) {
        CarrierServicesSmsFilterCallback carrierServicesSmsFilterCallback = new CarrierServicesSmsFilterCallback(bArr, i, inboundSmsTracker, inboundSmsTracker.getFormat(), smsBroadcastReceiver, z, inboundSmsTracker.isClass0(), inboundSmsTracker.getSubId(), inboundSmsTracker.getMessageId(), z2, list);
        Context context = this.mContext;
        Phone phone = this.mPhone;
        String format = inboundSmsTracker.getFormat();
        if (new CarrierServicesSmsFilter(context, phone, bArr, i, format, carrierServicesSmsFilterCallback, getName() + "::CarrierServicesSmsFilter", this.mCarrierServiceLocalLog, inboundSmsTracker.getMessageId()).filter()) {
            log("SMS is being handled by carrier service", inboundSmsTracker.getMessageId());
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createDefaultSmsFilters$1(byte[][] bArr, int i, InboundSmsTracker inboundSmsTracker, SmsBroadcastReceiver smsBroadcastReceiver, boolean z, boolean z2, List list) {
        if (VisualVoicemailSmsFilter.filter(this.mContext, bArr, inboundSmsTracker.getFormat(), i, inboundSmsTracker.getSubId())) {
            logWithLocalLog("Visual voicemail SMS dropped", inboundSmsTracker.getMessageId());
            dropFilteredSms(inboundSmsTracker, smsBroadcastReceiver, z2);
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createDefaultSmsFilters$2(byte[][] bArr, int i, InboundSmsTracker inboundSmsTracker, SmsBroadcastReceiver smsBroadcastReceiver, boolean z, boolean z2, List list) {
        if (new MissedIncomingCallSmsFilter(this.mPhone).filter(bArr, inboundSmsTracker.getFormat())) {
            logWithLocalLog("Missed incoming call SMS received", inboundSmsTracker.getMessageId());
            dropFilteredSms(inboundSmsTracker, smsBroadcastReceiver, z2);
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dropFilteredSms(InboundSmsTracker inboundSmsTracker, SmsBroadcastReceiver smsBroadcastReceiver, boolean z) {
        if (z) {
            deleteFromRawTable(inboundSmsTracker.getDeleteWhere(), inboundSmsTracker.getDeleteWhereArgs(), 1);
            sendMessage(3);
            return;
        }
        dropSms(smsBroadcastReceiver);
    }

    private boolean filterSms(byte[][] bArr, int i, InboundSmsTracker inboundSmsTracker, SmsBroadcastReceiver smsBroadcastReceiver, boolean z, boolean z2) {
        return filterSms(bArr, i, inboundSmsTracker, smsBroadcastReceiver, z, z2, this.mSmsFilters);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean filterSms(byte[][] bArr, int i, InboundSmsTracker inboundSmsTracker, SmsBroadcastReceiver smsBroadcastReceiver, boolean z, boolean z2, List<SmsFilter> list) {
        ListIterator<SmsFilter> listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            if (listIterator.next().filterSms(bArr, i, inboundSmsTracker, smsBroadcastReceiver, z, z2, list.subList(listIterator.nextIndex(), list.size()))) {
                return true;
            }
        }
        return false;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void dispatchIntent(Intent intent, String str, String str2, Bundle bundle, SmsBroadcastReceiver smsBroadcastReceiver, UserHandle userHandle, int i) {
        int[] iArr;
        intent.addFlags(134217728);
        String action = intent.getAction();
        if ("android.provider.Telephony.SMS_DELIVER".equals(action) || "android.provider.Telephony.SMS_RECEIVED".equals(action) || "android.provider.Telephony.WAP_PUSH_DELIVER".equals(action) || "android.provider.Telephony.WAP_PUSH_RECEIVED".equals(action)) {
            intent.addFlags(268435456);
        }
        SubscriptionManager.putPhoneIdAndSubIdExtra(intent, this.mPhone.getPhoneId());
        if (SubscriptionManager.isValidSubscriptionId(i)) {
            SubscriptionManager.putSubscriptionIdExtra(intent, i);
        }
        if (userHandle.equals(UserHandle.ALL)) {
            List<UserHandle> userHandles = this.mUserManager.getUserHandles(false);
            ArrayList arrayList = new ArrayList();
            for (UserHandle userHandle2 : userHandles) {
                if (this.mUserManager.isUserRunning(userHandle2)) {
                    arrayList.add(userHandle2);
                } else if (userHandle2.equals(UserHandle.SYSTEM)) {
                    logeWithLocalLog("dispatchIntent: SYSTEM user is not running", smsBroadcastReceiver.mInboundSmsTracker.getMessageId());
                }
            }
            if (arrayList.isEmpty()) {
                iArr = new int[]{userHandle.getIdentifier()};
            } else {
                iArr = new int[arrayList.size()];
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    iArr[i2] = ((UserHandle) arrayList.get(i2)).getIdentifier();
                }
            }
            int[] iArr2 = iArr;
            for (int length = iArr2.length - 1; length >= 0; length--) {
                UserHandle of = UserHandle.of(iArr2[length]);
                if (iArr2[length] == UserHandle.SYSTEM.getIdentifier() || (!hasUserRestriction("no_sms", of) && !this.mUserManager.isManagedProfile(iArr2[length]))) {
                    try {
                        if (iArr2[length] == UserHandle.SYSTEM.getIdentifier()) {
                            smsBroadcastReceiver.setWaitingForIntent(intent);
                        }
                        Context context = this.mContext;
                        context.createPackageContextAsUser(context.getPackageName(), 0, of).sendOrderedBroadcast(intent, -1, str, str2, iArr2[length] == UserHandle.SYSTEM.getIdentifier() ? smsBroadcastReceiver : null, getHandler(), null, null, bundle);
                    } catch (PackageManager.NameNotFoundException unused) {
                    }
                }
            }
            return;
        }
        try {
            smsBroadcastReceiver.setWaitingForIntent(intent);
            Context context2 = this.mContext;
            context2.createPackageContextAsUser(context2.getPackageName(), 0, userHandle).sendOrderedBroadcast(intent, -1, str, str2, smsBroadcastReceiver, getHandler(), null, null, bundle);
        } catch (PackageManager.NameNotFoundException unused2) {
        }
    }

    private boolean hasUserRestriction(String str, UserHandle userHandle) {
        List userRestrictionSources = this.mUserManager.getUserRestrictionSources(str, userHandle);
        return (userRestrictionSources == null || userRestrictionSources.isEmpty()) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void deleteFromRawTable(String str, String[] strArr, int i) {
        int delete = this.mResolver.delete(i == 1 ? sRawUriPermanentDelete : sRawUri, str, strArr);
        if (delete == 0) {
            loge("No rows were deleted from raw table!");
            return;
        }
        log("Deleted " + delete + " rows from raw table.");
    }

    /* JADX INFO: Access modifiers changed from: private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public Bundle handleSmsWhitelisting(ComponentName componentName, boolean z) {
        String packageName;
        String str;
        BroadcastOptions broadcastOptions;
        if (componentName != null) {
            packageName = componentName.getPackageName();
            str = "sms-app";
        } else {
            packageName = this.mContext.getPackageName();
            str = "sms-broadcast";
        }
        if (z) {
            broadcastOptions = BroadcastOptions.makeBasic();
            broadcastOptions.setBackgroundActivityStartsAllowed(true);
            broadcastOptions.toBundle();
        } else {
            broadcastOptions = null;
        }
        long whitelistAppTemporarilyForEvent = this.mPowerWhitelistManager.whitelistAppTemporarilyForEvent(packageName, 1, 314, str);
        if (broadcastOptions == null) {
            broadcastOptions = BroadcastOptions.makeBasic();
        }
        broadcastOptions.setTemporaryAppAllowlist(whitelistAppTemporarilyForEvent, 0, 314, PhoneConfigurationManager.SSSS);
        return broadcastOptions.toBundle();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    public void dispatchSmsDeliveryIntent(byte[][] bArr, String str, int i, SmsBroadcastReceiver smsBroadcastReceiver, boolean z, int i2, long j) {
        Intent intent = new Intent();
        intent.putExtra("pdus", (Serializable) bArr);
        intent.putExtra("format", str);
        if (j != 0) {
            intent.putExtra("messageId", j);
        }
        UserHandle userHandle = null;
        if (i == -1) {
            intent.setAction("android.provider.Telephony.SMS_DELIVER");
            UserHandle subscriptionUserHandle = TelephonyUtils.getSubscriptionUserHandle(this.mContext, i2);
            ComponentName defaultSmsApplicationAsUser = SmsApplication.getDefaultSmsApplicationAsUser(this.mContext, true, subscriptionUserHandle);
            if (defaultSmsApplicationAsUser != null) {
                intent.setComponent(defaultSmsApplicationAsUser);
                logWithLocalLog("Delivering SMS to: " + defaultSmsApplicationAsUser.getPackageName() + " " + defaultSmsApplicationAsUser.getClassName(), j);
            } else {
                intent.setComponent(null);
            }
            if (this.mPhone.getAppSmsManager().handleSmsReceivedIntent(intent)) {
                dropSms(smsBroadcastReceiver);
                return;
            }
            userHandle = subscriptionUserHandle;
        } else {
            intent.setAction("android.intent.action.DATA_SMS_RECEIVED");
            intent.setData(Uri.parse("sms://localhost:" + i));
            intent.setComponent(null);
        }
        if (userHandle == null) {
            userHandle = UserHandle.SYSTEM;
        }
        dispatchIntent(intent, "android.permission.RECEIVE_SMS", "android:receive_sms", handleSmsWhitelisting(intent.getComponent(), z), smsBroadcastReceiver, userHandle, i2);
    }

    private boolean checkAndHandleDuplicate(InboundSmsTracker inboundSmsTracker) throws SQLException {
        Pair<String, String[]> exactMatchDupDetectQuery = inboundSmsTracker.getExactMatchDupDetectQuery();
        Cursor cursor = null;
        try {
            ContentResolver contentResolver = this.mResolver;
            Uri uri = sRawUri;
            String[] strArr = PDU_DELETED_FLAG_PROJECTION;
            Cursor query = contentResolver.query(uri, strArr, (String) exactMatchDupDetectQuery.first, (String[]) exactMatchDupDetectQuery.second, null);
            if (query != null) {
                try {
                    if (query.moveToNext()) {
                        if (query.getCount() != 1) {
                            logeWithLocalLog("checkAndHandleDuplicate: Exact match query returned " + query.getCount() + " rows", inboundSmsTracker.getMessageId());
                        }
                        if (query.getInt(PDU_DELETED_FLAG_PROJECTION_INDEX_MAPPING.get(10).intValue()) == 1) {
                            logWithLocalLog("checkAndHandleDuplicate: Discarding duplicate message/segment: " + inboundSmsTracker);
                            logDupPduMismatch(query, inboundSmsTracker);
                            query.close();
                            return true;
                        } else if (inboundSmsTracker.getMessageCount() == 1) {
                            deleteFromRawTable((String) exactMatchDupDetectQuery.first, (String[]) exactMatchDupDetectQuery.second, 1);
                            logWithLocalLog("checkAndHandleDuplicate: Replacing duplicate message: " + inboundSmsTracker);
                            logDupPduMismatch(query, inboundSmsTracker);
                        }
                    }
                } catch (Throwable th) {
                    th = th;
                    cursor = query;
                    throw th;
                }
            }
            if (query != null) {
                query.close();
            }
            if (inboundSmsTracker.getMessageCount() > 1) {
                Pair<String, String[]> inexactMatchDupDetectQuery = inboundSmsTracker.getInexactMatchDupDetectQuery();
                try {
                    cursor = this.mResolver.query(uri, strArr, (String) inexactMatchDupDetectQuery.first, (String[]) inexactMatchDupDetectQuery.second, null);
                    if (cursor != null && cursor.moveToNext()) {
                        if (cursor.getCount() != 1) {
                            logeWithLocalLog("checkAndHandleDuplicate: Inexact match query returned " + cursor.getCount() + " rows", inboundSmsTracker.getMessageId());
                        }
                        deleteFromRawTable((String) inexactMatchDupDetectQuery.first, (String[]) inexactMatchDupDetectQuery.second, 1);
                        logWithLocalLog("checkAndHandleDuplicate: Replacing duplicate message segment: " + inboundSmsTracker);
                        logDupPduMismatch(cursor, inboundSmsTracker);
                    }
                    if (cursor != null) {
                        cursor.close();
                        return false;
                    }
                    return false;
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
            return false;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private void logDupPduMismatch(Cursor cursor, InboundSmsTracker inboundSmsTracker) {
        String string = cursor.getString(PDU_DELETED_FLAG_PROJECTION_INDEX_MAPPING.get(0).intValue());
        byte[] pdu = inboundSmsTracker.getPdu();
        byte[] hexStringToByteArray = HexDump.hexStringToByteArray(string);
        if (Arrays.equals(hexStringToByteArray, inboundSmsTracker.getPdu())) {
            return;
        }
        logeWithLocalLog("Warning: dup message PDU of length " + pdu.length + " is different from existing PDU of length " + hexStringToByteArray.length, inboundSmsTracker.getMessageId());
    }

    private int addTrackerToRawTable(InboundSmsTracker inboundSmsTracker, boolean z) {
        if (z) {
            try {
                if (checkAndHandleDuplicate(inboundSmsTracker)) {
                    return 5;
                }
            } catch (SQLException e) {
                loge("addTrackerToRawTable: Can't access SMS database, " + SmsController.formatCrossStackMessageId(inboundSmsTracker.getMessageId()), e);
                return 10;
            }
        } else {
            log("addTrackerToRawTable: Skipped message de-duping logic", inboundSmsTracker.getMessageId());
        }
        String address = inboundSmsTracker.getAddress();
        String num = Integer.toString(inboundSmsTracker.getReferenceNumber());
        String num2 = Integer.toString(inboundSmsTracker.getMessageCount());
        Uri insert = this.mResolver.insert(sRawUri, inboundSmsTracker.getContentValues());
        log("addTrackerToRawTable: URI of new row: " + insert, inboundSmsTracker.getMessageId());
        try {
            long parseId = ContentUris.parseId(insert);
            if (inboundSmsTracker.getMessageCount() == 1) {
                inboundSmsTracker.setDeleteWhere(SELECT_BY_ID, new String[]{Long.toString(parseId)});
            } else {
                inboundSmsTracker.setDeleteWhere(inboundSmsTracker.getQueryForSegments(), new String[]{address, num, num2});
            }
            return 1;
        } catch (Exception e2) {
            loge("addTrackerToRawTable: error parsing URI for new row: " + insert + " " + SmsController.formatCrossStackMessageId(inboundSmsTracker.getMessageId()), e2);
            return 11;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isCurrentFormat3gpp2() {
        return 2 == TelephonyManager.getDefault().getCurrentPhoneType();
    }

    /* loaded from: classes.dex */
    public final class SmsBroadcastReceiver extends BroadcastReceiver {
        private long mBroadcastTimeMillis;
        @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
        private final String mDeleteWhere;
        @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
        private final String[] mDeleteWhereArgs;
        private final InboundSmsTracker mInboundSmsTracker;
        public Intent mWaitingForIntent;

        public synchronized void setWaitingForIntent(Intent intent) {
            this.mWaitingForIntent = intent;
            this.mBroadcastTimeMillis = System.currentTimeMillis();
            InboundSmsHandler.this.removeMessages(9);
            InboundSmsHandler.this.sendMessageDelayed(9, InboundSmsHandler.sTimeoutDurationMillis);
        }

        public SmsBroadcastReceiver(InboundSmsTracker inboundSmsTracker) {
            this.mDeleteWhere = inboundSmsTracker.getDeleteWhere();
            this.mDeleteWhereArgs = inboundSmsTracker.getDeleteWhereArgs();
            this.mInboundSmsTracker = inboundSmsTracker;
        }

        public void fakeNextAction() {
            if (this.mWaitingForIntent != null) {
                InboundSmsHandler inboundSmsHandler = InboundSmsHandler.this;
                inboundSmsHandler.logeWithLocalLog("fakeNextAction: " + this.mWaitingForIntent.getAction(), this.mInboundSmsTracker.getMessageId());
                handleAction(this.mWaitingForIntent, false);
                return;
            }
            InboundSmsHandler.this.logeWithLocalLog("fakeNextAction: mWaitingForIntent is null", this.mInboundSmsTracker.getMessageId());
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                InboundSmsHandler inboundSmsHandler = InboundSmsHandler.this;
                inboundSmsHandler.logeWithLocalLog("onReceive: received null intent, faking " + this.mWaitingForIntent, this.mInboundSmsTracker.getMessageId());
                return;
            }
            handleAction(intent, true);
        }

        private synchronized void handleAction(Intent intent, boolean z) {
            String action = intent.getAction();
            Intent intent2 = this.mWaitingForIntent;
            if (intent2 != null && intent2.getAction().equals(action)) {
                if (z) {
                    int currentTimeMillis = (int) (System.currentTimeMillis() - this.mBroadcastTimeMillis);
                    if (currentTimeMillis >= 5000) {
                        InboundSmsHandler inboundSmsHandler = InboundSmsHandler.this;
                        inboundSmsHandler.loge("Slow ordered broadcast completion time for " + action + ": " + currentTimeMillis + " ms");
                    } else {
                        InboundSmsHandler inboundSmsHandler2 = InboundSmsHandler.this;
                        inboundSmsHandler2.log("Ordered broadcast completed for " + action + " in: " + currentTimeMillis + " ms");
                    }
                }
                int intExtra = intent.getIntExtra("android.telephony.extra.SUBSCRIPTION_INDEX", -1);
                if (action.equals("android.provider.Telephony.SMS_DELIVER")) {
                    intent.setAction("android.provider.Telephony.SMS_RECEIVED");
                    intent.setComponent(null);
                    Bundle handleSmsWhitelisting = InboundSmsHandler.this.handleSmsWhitelisting(null, false);
                    setWaitingForIntent(intent);
                    InboundSmsHandler.this.dispatchIntent(intent, "android.permission.RECEIVE_SMS", "android:receive_sms", handleSmsWhitelisting, this, UserHandle.ALL, intExtra);
                } else if (action.equals("android.provider.Telephony.WAP_PUSH_DELIVER")) {
                    intent.setAction("android.provider.Telephony.WAP_PUSH_RECEIVED");
                    intent.setComponent(null);
                    InboundSmsHandler inboundSmsHandler3 = InboundSmsHandler.this;
                    long whitelistAppTemporarilyForEvent = inboundSmsHandler3.mPowerWhitelistManager.whitelistAppTemporarilyForEvent(inboundSmsHandler3.mContext.getPackageName(), 2, 315, "mms-broadcast");
                    BroadcastOptions makeBasic = BroadcastOptions.makeBasic();
                    makeBasic.setTemporaryAppAllowlist(whitelistAppTemporarilyForEvent, 0, 315, PhoneConfigurationManager.SSSS);
                    Bundle bundle = makeBasic.toBundle();
                    String type = intent.getType();
                    setWaitingForIntent(intent);
                    InboundSmsHandler.this.dispatchIntent(intent, WapPushOverSms.getPermissionForType(type), WapPushOverSms.getAppOpsStringPermissionForIntent(type), bundle, this, UserHandle.SYSTEM, intExtra);
                } else {
                    if (!"android.intent.action.DATA_SMS_RECEIVED".equals(action) && !"android.provider.Telephony.SMS_RECEIVED".equals(action) && !"android.provider.Telephony.WAP_PUSH_RECEIVED".equals(action)) {
                        InboundSmsHandler inboundSmsHandler4 = InboundSmsHandler.this;
                        inboundSmsHandler4.loge("unexpected BroadcastReceiver action: " + action);
                    }
                    if (z) {
                        int resultCode = getResultCode();
                        if (resultCode != -1 && resultCode != 1) {
                            InboundSmsHandler inboundSmsHandler5 = InboundSmsHandler.this;
                            inboundSmsHandler5.loge("a broadcast receiver set the result code to " + resultCode + ", deleting from raw table anyway!");
                        } else {
                            InboundSmsHandler.this.log("successful broadcast, deleting from raw table.");
                        }
                    }
                    InboundSmsHandler.this.deleteFromRawTable(this.mDeleteWhere, this.mDeleteWhereArgs, 2);
                    this.mWaitingForIntent = null;
                    InboundSmsHandler.this.removeMessages(9);
                    InboundSmsHandler.this.sendMessage(3);
                }
                return;
            }
            InboundSmsHandler inboundSmsHandler6 = InboundSmsHandler.this;
            StringBuilder sb = new StringBuilder();
            sb.append("handleAction: Received ");
            sb.append(action);
            sb.append(" when expecting ");
            sb.append(this.mWaitingForIntent);
            inboundSmsHandler6.logeWithLocalLog(sb.toString() == null ? "none" : this.mWaitingForIntent.getAction(), this.mInboundSmsTracker.getMessageId());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class CarrierServicesSmsFilterCallback implements CarrierServicesSmsFilter.CarrierServicesSmsFilterCallbackInterface {
        private final boolean mBlock;
        private final int mDestPort;
        private final boolean mIsClass0;
        private final long mMessageId;
        private final byte[][] mPdus;
        private final List<SmsFilter> mRemainingFilters;
        private final SmsBroadcastReceiver mSmsBroadcastReceiver;
        private final String mSmsFormat;
        private final int mSubId;
        private final InboundSmsTracker mTracker;
        private final boolean mUserUnlocked;

        CarrierServicesSmsFilterCallback(byte[][] bArr, int i, InboundSmsTracker inboundSmsTracker, String str, SmsBroadcastReceiver smsBroadcastReceiver, boolean z, boolean z2, int i2, long j, boolean z3, List<SmsFilter> list) {
            this.mPdus = bArr;
            this.mDestPort = i;
            this.mTracker = inboundSmsTracker;
            this.mSmsFormat = str;
            this.mSmsBroadcastReceiver = smsBroadcastReceiver;
            this.mUserUnlocked = z;
            this.mIsClass0 = z2;
            this.mSubId = i2;
            this.mMessageId = j;
            this.mBlock = z3;
            this.mRemainingFilters = list;
        }

        @Override // com.android.internal.telephony.CarrierServicesSmsFilter.CarrierServicesSmsFilterCallbackInterface
        public void onFilterComplete(int i) {
            InboundSmsHandler inboundSmsHandler = InboundSmsHandler.this;
            inboundSmsHandler.log("onFilterComplete: result is " + i, this.mTracker.getMessageId());
            if ((i & 1) != 0) {
                InboundSmsHandler.this.dropFilteredSms(this.mTracker, this.mSmsBroadcastReceiver, this.mBlock);
            } else if (InboundSmsHandler.filterSms(this.mPdus, this.mDestPort, this.mTracker, this.mSmsBroadcastReceiver, this.mUserUnlocked, this.mBlock, this.mRemainingFilters)) {
            } else {
                if (this.mBlock) {
                    if (this.mUserUnlocked) {
                        InboundSmsHandler.this.log("onFilterComplete: dropping message as the sender is blocked", this.mTracker.getMessageId());
                        InboundSmsHandler.this.dropFilteredSms(this.mTracker, this.mSmsBroadcastReceiver, this.mBlock);
                        return;
                    }
                    InboundSmsHandler.this.sendMessage(3);
                } else if (this.mUserUnlocked) {
                    InboundSmsHandler.this.dispatchSmsDeliveryIntent(this.mPdus, this.mSmsFormat, this.mDestPort, this.mSmsBroadcastReceiver, this.mIsClass0, this.mSubId, this.mMessageId);
                } else {
                    if (!InboundSmsHandler.this.isSkipNotifyFlagSet(i)) {
                        InboundSmsHandler.this.showNewMessageNotification();
                    }
                    InboundSmsHandler.this.sendMessage(3);
                }
            }
        }
    }

    private void dropSms(SmsBroadcastReceiver smsBroadcastReceiver) {
        deleteFromRawTable(smsBroadcastReceiver.mDeleteWhere, smsBroadcastReceiver.mDeleteWhereArgs, 2);
        sendMessage(3);
    }

    protected void logWithLocalLog(String str) {
        log(str);
        this.mLocalLog.log(str);
    }

    protected void logWithLocalLog(String str, long j) {
        log(str, j);
        LocalLog localLog = this.mLocalLog;
        localLog.log(str + ", " + SmsController.formatCrossStackMessageId(j));
    }

    protected void logeWithLocalLog(String str) {
        loge(str);
        this.mLocalLog.log(str);
    }

    protected void logeWithLocalLog(String str, long j) {
        loge(str, j);
        LocalLog localLog = this.mLocalLog;
        localLog.log(str + ", " + SmsController.formatCrossStackMessageId(j));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.StateMachine
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void log(String str) {
        Rlog.d(getName(), str);
    }

    protected void log(String str, long j) {
        log(str + ", " + SmsController.formatCrossStackMessageId(j));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.StateMachine
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void loge(String str) {
        Rlog.e(getName(), str);
    }

    protected void loge(String str, long j) {
        loge(str + ", " + SmsController.formatCrossStackMessageId(j));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.StateMachine
    public void loge(String str, Throwable th) {
        Rlog.e(getName(), str, th);
    }

    @Override // com.android.internal.telephony.StateMachine
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        indentingPrintWriter.println(getName() + " extends StateMachine:");
        indentingPrintWriter.increaseIndent();
        super.dump(fileDescriptor, indentingPrintWriter, strArr);
        CellBroadcastServiceManager cellBroadcastServiceManager = this.mCellBroadcastServiceManager;
        if (cellBroadcastServiceManager != null) {
            cellBroadcastServiceManager.dump(fileDescriptor, indentingPrintWriter, strArr);
        }
        indentingPrintWriter.println("mLocalLog:");
        indentingPrintWriter.increaseIndent();
        this.mLocalLog.dump(fileDescriptor, indentingPrintWriter, strArr);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("mCarrierServiceLocalLog:");
        indentingPrintWriter.increaseIndent();
        this.mCarrierServiceLocalLog.dump(fileDescriptor, indentingPrintWriter, strArr);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.decreaseIndent();
    }

    @VisibleForTesting
    public PowerManager.WakeLock getWakeLock() {
        return this.mWakeLock;
    }

    @VisibleForTesting
    public int getWakeLockTimeout() {
        return this.mWakeLockTimeout;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setWakeLockTimeout(int i) {
        this.mWakeLockTimeout = i;
    }

    @VisibleForTesting
    public void setSmsFiltersForTesting(List<SmsFilter> list) {
        if (list == null) {
            this.mSmsFilters = createDefaultSmsFilters();
        } else {
            this.mSmsFilters = list;
        }
    }

    /* loaded from: classes.dex */
    private static class NewMessageNotificationActionReceiver extends BroadcastReceiver {
        private NewMessageNotificationActionReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (InboundSmsHandler.ACTION_OPEN_SMS_APP.equals(intent.getAction()) && ((UserManager) context.getSystemService("user")).isUserUnlocked()) {
                context.startActivity(context.getPackageManager().getLaunchIntentForPackage(Telephony.Sms.getDefaultSmsPackage(context)));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public byte[] decodeHexString(String str) {
        if (str == null || str.length() % 2 == 1) {
            return null;
        }
        byte[] bArr = new byte[str.length() / 2];
        int i = 0;
        while (i < str.length()) {
            int i2 = i + 2;
            bArr[i / 2] = hexToByte(str.substring(i, i2));
            i = i2;
        }
        return bArr;
    }

    private byte hexToByte(String str) {
        int digit = toDigit(str.charAt(0));
        return (byte) ((digit << 4) + toDigit(str.charAt(1)));
    }

    private int toDigit(char c) {
        int digit = Character.digit(c, 16);
        if (digit == -1) {
            return 0;
        }
        return digit;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void registerNewMessageNotificationActionHandler(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_OPEN_SMS_APP);
        context.registerReceiver(new NewMessageNotificationActionReceiver(), intentFilter, 4);
    }

    /* loaded from: classes.dex */
    protected abstract class CbTestBroadcastReceiver extends BroadcastReceiver {
        protected final String mTestAction;

        protected abstract void handleTestAction(Intent intent);

        public CbTestBroadcastReceiver(String str) {
            this.mTestAction = str;
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            InboundSmsHandler inboundSmsHandler = InboundSmsHandler.this;
            inboundSmsHandler.logd("Received test intent action=" + intent.getAction());
            if (intent.getAction().equals(this.mTestAction)) {
                int phoneId = InboundSmsHandler.this.mPhone.getPhoneId();
                if (intent.getIntExtra("phone_id", phoneId) != phoneId) {
                    return;
                }
                handleTestAction(intent);
            }
        }
    }
}
