package com.android.internal.telephony;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncResult;
import android.os.Binder;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserManager;
import android.telephony.DomainSelectionService;
import android.telephony.ServiceState;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import com.android.ims.FeatureConnector;
import com.android.ims.ImsManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.ImsSmsDispatcher;
import com.android.internal.telephony.SMSDispatcher;
import com.android.internal.telephony.SmsMessageBase;
import com.android.internal.telephony.cdma.CdmaInboundSmsHandler;
import com.android.internal.telephony.cdma.CdmaSMSDispatcher;
import com.android.internal.telephony.cdma.SmsMessage;
import com.android.internal.telephony.domainselection.DomainSelectionConnection;
import com.android.internal.telephony.domainselection.DomainSelectionResolver;
import com.android.internal.telephony.domainselection.EmergencySmsDomainSelectionConnection;
import com.android.internal.telephony.domainselection.SmsDomainSelectionConnection;
import com.android.internal.telephony.gsm.GsmInboundSmsHandler;
import com.android.internal.telephony.gsm.GsmSMSDispatcher;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class SmsDispatchersController extends Handler {
    protected static final int EVENT_SMS_HANDLER_EXITING_WAITING_STATE = 17;
    private final BroadcastReceiver mBroadcastReceiver;
    private SMSDispatcher mCdmaDispatcher;
    private CdmaInboundSmsHandler mCdmaInboundSmsHandler;
    private final CommandsInterface mCi;
    private final Context mContext;
    private long mCurrentWaitElapsedDuration;
    private long mCurrentWaitStartTime;
    private HashMap<Integer, SMSDispatcher.SmsTracker> mDeliveryPendingMapFor3GPP;
    private HashMap<Integer, SMSDispatcher.SmsTracker> mDeliveryPendingMapFor3GPP2;
    private DomainSelectionResolverProxy mDomainSelectionResolverProxy;
    private DomainSelectionConnectionHolder mDscHolder;
    private DomainSelectionConnectionHolder mEmergencyDscHolder;
    private SMSDispatcher mGsmDispatcher;
    private GsmInboundSmsHandler mGsmInboundSmsHandler;
    private boolean mIms;
    private ImsSmsDispatcher mImsSmsDispatcher;
    private String mImsSmsFormat;
    private long mLastInServiceTime;
    private Phone mPhone;
    private final SmsUsageMonitor mUsageMonitor;

    @VisibleForTesting
    /* loaded from: classes.dex */
    public interface DomainSelectionResolverProxy {
        DomainSelectionConnection getDomainSelectionConnection(Phone phone, int i, boolean z);

        boolean isDomainSelectionSupported();
    }

    /* loaded from: classes.dex */
    public interface SmsInjectionCallback {
        void onSmsInjectedResult(int i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class PendingRequest {
        public static final int TYPE_DATA = 1;
        public static final int TYPE_MULTIPART_TEXT = 3;
        public static final int TYPE_RETRY_SMS = 4;
        public static final int TYPE_TEXT = 2;
        public final String callingPackage;
        public final byte[] data;
        public final ArrayList<PendingIntent> deliveryIntents;
        public final String destAddr;
        public final int destPort;
        public final boolean expectMore;
        public final boolean isForVvm;
        public final long messageId;
        public final Uri messageUri;
        public final boolean persistMessage;
        public final int priority;
        public final String scAddr;
        public final ArrayList<PendingIntent> sentIntents;
        public final boolean skipShortCodeCheck;
        public final ArrayList<String> texts;
        public final SMSDispatcher.SmsTracker tracker;
        public final int type;
        public final int validityPeriod;

        PendingRequest(int i, SMSDispatcher.SmsTracker smsTracker, String str, String str2, String str3, ArrayList<PendingIntent> arrayList, ArrayList<PendingIntent> arrayList2, boolean z, byte[] bArr, int i2, ArrayList<String> arrayList3, Uri uri, boolean z2, int i3, boolean z3, int i4, long j, boolean z4) {
            this.type = i;
            this.tracker = smsTracker;
            this.callingPackage = str;
            this.destAddr = str2;
            this.scAddr = str3;
            this.sentIntents = arrayList;
            this.deliveryIntents = arrayList2;
            this.isForVvm = z;
            this.data = bArr;
            this.destPort = i2;
            this.texts = arrayList3;
            this.messageUri = uri;
            this.persistMessage = z2;
            this.priority = i3;
            this.expectMore = z3;
            this.validityPeriod = i4;
            this.messageId = j;
            this.skipShortCodeCheck = z4;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @VisibleForTesting
    /* loaded from: classes.dex */
    public class DomainSelectionConnectionHolder implements DomainSelectionConnection.DomainSelectionConnectionCallback {
        private DomainSelectionConnection mConnection;
        private final boolean mEmergency;
        private final List<PendingRequest> mPendingRequests = new ArrayList();

        DomainSelectionConnectionHolder(boolean z) {
            this.mEmergency = z;
        }

        public DomainSelectionConnection getConnection() {
            return this.mConnection;
        }

        public List<PendingRequest> getPendingRequests() {
            return this.mPendingRequests;
        }

        public boolean isDomainSelectionRequested() {
            return !this.mPendingRequests.isEmpty();
        }

        public boolean isEmergency() {
            return this.mEmergency;
        }

        public void clearAllRequests() {
            this.mPendingRequests.clear();
        }

        public void addRequest(PendingRequest pendingRequest) {
            this.mPendingRequests.add(pendingRequest);
        }

        public void setConnection(DomainSelectionConnection domainSelectionConnection) {
            this.mConnection = domainSelectionConnection;
        }

        @Override // com.android.internal.telephony.domainselection.DomainSelectionConnection.DomainSelectionConnectionCallback
        public void onSelectionTerminated(int i) {
            SmsDispatchersController.this.notifyDomainSelectionTerminated(this);
        }
    }

    public void putDeliveryPendingTracker(SMSDispatcher.SmsTracker smsTracker) {
        if (isCdmaFormat(smsTracker.mFormat)) {
            this.mDeliveryPendingMapFor3GPP2.put(Integer.valueOf(smsTracker.mMessageRef), smsTracker);
        } else {
            this.mDeliveryPendingMapFor3GPP.put(Integer.valueOf(smsTracker.mMessageRef), smsTracker);
        }
    }

    public SmsDispatchersController(Phone phone, SmsStorageMonitor smsStorageMonitor, SmsUsageMonitor smsUsageMonitor) {
        this(phone, smsStorageMonitor, smsUsageMonitor, phone.getLooper());
    }

    @VisibleForTesting
    public SmsDispatchersController(Phone phone, SmsStorageMonitor smsStorageMonitor, SmsUsageMonitor smsUsageMonitor, Looper looper) {
        super(looper);
        this.mLastInServiceTime = -1L;
        this.mCurrentWaitElapsedDuration = 0L;
        this.mCurrentWaitStartTime = -1L;
        this.mIms = false;
        this.mImsSmsFormat = "unknown";
        this.mDeliveryPendingMapFor3GPP = new HashMap<>();
        this.mDeliveryPendingMapFor3GPP2 = new HashMap<>();
        this.mDomainSelectionResolverProxy = new DomainSelectionResolverProxy() { // from class: com.android.internal.telephony.SmsDispatchersController.1
            @Override // com.android.internal.telephony.SmsDispatchersController.DomainSelectionResolverProxy
            public DomainSelectionConnection getDomainSelectionConnection(Phone phone2, int i, boolean z) {
                try {
                    return DomainSelectionResolver.getInstance().getDomainSelectionConnection(phone2, i, z);
                } catch (IllegalStateException unused) {
                    return null;
                }
            }

            @Override // com.android.internal.telephony.SmsDispatchersController.DomainSelectionResolverProxy
            public boolean isDomainSelectionSupported() {
                return DomainSelectionResolver.getInstance().isDomainSelectionSupported();
            }
        };
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.internal.telephony.SmsDispatchersController.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                Rlog.d("SmsDispatchersController", "Received broadcast " + intent.getAction());
                if ("android.intent.action.USER_UNLOCKED".equals(intent.getAction())) {
                    SmsDispatchersController smsDispatchersController = SmsDispatchersController.this;
                    smsDispatchersController.sendMessage(smsDispatchersController.obtainMessage(16));
                }
            }
        };
        this.mBroadcastReceiver = broadcastReceiver;
        Rlog.d("SmsDispatchersController", "SmsDispatchersController created");
        Context context = phone.getContext();
        this.mContext = context;
        this.mUsageMonitor = smsUsageMonitor;
        CommandsInterface commandsInterface = phone.mCi;
        this.mCi = commandsInterface;
        this.mPhone = phone;
        this.mImsSmsDispatcher = new ImsSmsDispatcher(phone, this, new ImsSmsDispatcher.FeatureConnectorFactory() { // from class: com.android.internal.telephony.SmsDispatchersController$$ExternalSyntheticLambda3
            @Override // com.android.internal.telephony.ImsSmsDispatcher.FeatureConnectorFactory
            public final FeatureConnector create(Context context2, int i, String str, FeatureConnector.Listener listener, Executor executor) {
                return ImsManager.getConnector(context2, i, str, listener, executor);
            }
        });
        this.mCdmaDispatcher = new CdmaSMSDispatcher(phone, this);
        this.mGsmInboundSmsHandler = GsmInboundSmsHandler.makeInboundSmsHandler(phone.getContext(), smsStorageMonitor, phone, looper);
        this.mCdmaInboundSmsHandler = CdmaInboundSmsHandler.makeInboundSmsHandler(phone.getContext(), smsStorageMonitor, phone, (CdmaSMSDispatcher) this.mCdmaDispatcher, looper);
        this.mGsmDispatcher = new GsmSMSDispatcher(phone, this, this.mGsmInboundSmsHandler);
        SmsBroadcastUndelivered.initialize(phone.getContext(), this.mGsmInboundSmsHandler, this.mCdmaInboundSmsHandler);
        InboundSmsHandler.registerNewMessageNotificationActionHandler(phone.getContext());
        commandsInterface.registerForOn(this, 11, null);
        commandsInterface.registerForImsNetworkStateChanged(this, 12, null);
        if (((UserManager) context.getSystemService("user")).isUserUnlocked()) {
            this.mPhone.registerForServiceStateChanged(this, 14, null);
            resetPartialSegmentWaitTimer();
            return;
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_UNLOCKED");
        context.registerReceiver(broadcastReceiver, intentFilter);
    }

    public void dispose() {
        this.mCi.unregisterForOn(this);
        this.mCi.unregisterForImsNetworkStateChanged(this);
        this.mPhone.unregisterForServiceStateChanged(this);
        this.mGsmDispatcher.dispose();
        this.mCdmaDispatcher.dispose();
        this.mGsmInboundSmsHandler.dispose();
        this.mCdmaInboundSmsHandler.dispose();
        finishDomainSelection(this.mDscHolder);
        finishDomainSelection(this.mEmergencyDscHolder);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        switch (message.what) {
            case 11:
            case 12:
                this.mCi.getImsRegistrationState(obtainMessage(13));
                return;
            case 13:
                AsyncResult asyncResult = (AsyncResult) message.obj;
                if (asyncResult.exception == null) {
                    updateImsInfo(asyncResult);
                    return;
                }
                Rlog.e("SmsDispatchersController", "IMS State query failed with exp " + asyncResult.exception);
                return;
            case 14:
            case 17:
                reevaluateTimerStatus();
                return;
            case 15:
                handlePartialSegmentTimerExpiry(((Long) message.obj).longValue());
                return;
            case 16:
                this.mPhone.registerForServiceStateChanged(this, 14, null);
                resetPartialSegmentWaitTimer();
                return;
            default:
                if (isCdmaMo()) {
                    this.mCdmaDispatcher.handleMessage(message);
                    return;
                } else {
                    this.mGsmDispatcher.handleMessage(message);
                    return;
                }
        }
    }

    private String getSmscAddressFromUSIM(String str) {
        IccSmsInterfaceManager iccSmsInterfaceManager = this.mPhone.getIccSmsInterfaceManager();
        if (iccSmsInterfaceManager != null) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return iccSmsInterfaceManager.getSmscAddressFromIccEf(str);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        Rlog.d("SmsDispatchersController", "getSmscAddressFromIccEf iccSmsIntMgr is null");
        return null;
    }

    private void reevaluateTimerStatus() {
        long currentTimeMillis = System.currentTimeMillis();
        removeMessages(15);
        long j = this.mLastInServiceTime;
        if (j != -1) {
            this.mCurrentWaitElapsedDuration += currentTimeMillis - j;
        }
        if (this.mCurrentWaitElapsedDuration > 86400000) {
            handlePartialSegmentTimerExpiry(this.mCurrentWaitStartTime);
        } else if (isInService()) {
            handleInService(currentTimeMillis);
        } else {
            handleOutOfService(currentTimeMillis);
        }
    }

    private void handleInService(long j) {
        if (this.mCurrentWaitStartTime == -1) {
            this.mCurrentWaitStartTime = j;
        }
        sendMessageDelayed(obtainMessage(15, Long.valueOf(this.mCurrentWaitStartTime)), 86400000 - this.mCurrentWaitElapsedDuration);
        this.mLastInServiceTime = j;
    }

    private void handleOutOfService(long j) {
        this.mLastInServiceTime = -1L;
    }

    private void handlePartialSegmentTimerExpiry(long j) {
        if (this.mGsmInboundSmsHandler.getCurrentState().getName().equals("WaitingState") || this.mCdmaInboundSmsHandler.getCurrentState().getName().equals("WaitingState")) {
            logd("handlePartialSegmentTimerExpiry: ignoring timer expiry as InboundSmsHandler is in WaitingState");
            return;
        }
        SmsBroadcastUndelivered.scanRawTable(this.mContext, j);
        resetPartialSegmentWaitTimer();
    }

    private void resetPartialSegmentWaitTimer() {
        long currentTimeMillis = System.currentTimeMillis();
        removeMessages(15);
        if (isInService()) {
            this.mCurrentWaitStartTime = currentTimeMillis;
            this.mLastInServiceTime = currentTimeMillis;
            sendMessageDelayed(obtainMessage(15, Long.valueOf(currentTimeMillis)), 86400000L);
        } else {
            this.mCurrentWaitStartTime = -1L;
            this.mLastInServiceTime = -1L;
        }
        this.mCurrentWaitElapsedDuration = 0L;
    }

    private boolean isInService() {
        ServiceState serviceState = this.mPhone.getServiceState();
        return serviceState != null && serviceState.getState() == 0;
    }

    private void setImsSmsFormat(int i) {
        if (i == 1) {
            this.mImsSmsFormat = "3gpp";
        } else if (i == 2) {
            this.mImsSmsFormat = "3gpp2";
        } else {
            this.mImsSmsFormat = "unknown";
        }
    }

    private void updateImsInfo(AsyncResult asyncResult) {
        int[] iArr = (int[]) asyncResult.result;
        boolean z = true;
        setImsSmsFormat(iArr[1]);
        if (iArr[0] != 1 || "unknown".equals(this.mImsSmsFormat)) {
            z = false;
        }
        this.mIms = z;
        Rlog.d("SmsDispatchersController", "IMS registration state: " + this.mIms + " format: " + this.mImsSmsFormat);
    }

    @VisibleForTesting
    public void injectSmsPdu(byte[] bArr, String str, boolean z, SmsInjectionCallback smsInjectionCallback) {
        injectSmsPdu(SmsMessage.createFromPdu(bArr, str), str, smsInjectionCallback, false, z, 0);
    }

    @VisibleForTesting
    public void setImsSmsDispatcher(ImsSmsDispatcher imsSmsDispatcher) {
        this.mImsSmsDispatcher = imsSmsDispatcher;
    }

    @VisibleForTesting
    public void injectSmsPdu(SmsMessage smsMessage, String str, SmsInjectionCallback smsInjectionCallback, boolean z, boolean z2, int i) {
        Rlog.d("SmsDispatchersController", "SmsDispatchersController:injectSmsPdu");
        try {
            if (smsMessage == null) {
                Rlog.e("SmsDispatchersController", "injectSmsPdu: createFromPdu returned null");
                smsInjectionCallback.onSmsInjectedResult(2);
            } else if (!z && smsMessage.getMessageClass() != SmsMessage.MessageClass.CLASS_1) {
                Rlog.e("SmsDispatchersController", "injectSmsPdu: not class 1");
                smsInjectionCallback.onSmsInjectedResult(2);
            } else {
                AsyncResult asyncResult = new AsyncResult(smsInjectionCallback, smsMessage, (Throwable) null);
                int i2 = 1;
                if (str.equals("3gpp")) {
                    Rlog.i("SmsDispatchersController", "SmsDispatchersController:injectSmsText Sending msg=" + smsMessage + ", format=" + str + "to mGsmInboundSmsHandler");
                    GsmInboundSmsHandler gsmInboundSmsHandler = this.mGsmInboundSmsHandler;
                    if (!z2) {
                        i2 = 0;
                    }
                    gsmInboundSmsHandler.sendMessage(7, i2, i, asyncResult);
                } else if (str.equals("3gpp2")) {
                    Rlog.i("SmsDispatchersController", "SmsDispatchersController:injectSmsText Sending msg=" + smsMessage + ", format=" + str + "to mCdmaInboundSmsHandler");
                    CdmaInboundSmsHandler cdmaInboundSmsHandler = this.mCdmaInboundSmsHandler;
                    if (!z2) {
                        i2 = 0;
                    }
                    cdmaInboundSmsHandler.sendMessage(7, i2, 0, asyncResult);
                } else {
                    Rlog.e("SmsDispatchersController", "Invalid pdu format: " + str);
                    smsInjectionCallback.onSmsInjectedResult(2);
                }
            }
        } catch (Exception e) {
            Rlog.e("SmsDispatchersController", "injectSmsPdu failed: ", e);
            smsInjectionCallback.onSmsInjectedResult(2);
        }
    }

    public boolean setImsManager(ImsManager imsManager) {
        GsmInboundSmsHandler gsmInboundSmsHandler = this.mGsmInboundSmsHandler;
        if (gsmInboundSmsHandler != null) {
            gsmInboundSmsHandler.setImsManager(imsManager);
            return true;
        }
        return false;
    }

    public void sendRetrySms(SMSDispatcher.SmsTracker smsTracker) {
        SmsDispatchersController smsDispatchersController;
        DomainSelectionConnectionHolder domainSelectionConnection;
        boolean z = false;
        if (smsTracker.mUsesImsServiceForIms) {
            smsDispatchersController = this;
        } else if (this.mDomainSelectionResolverProxy.isDomainSelectionSupported() && (domainSelectionConnection = getDomainSelectionConnection(false)) != null && domainSelectionConnection.getConnection() != null) {
            sendSmsUsingDomainSelection(domainSelectionConnection, new PendingRequest(4, smsTracker, null, null, null, null, null, false, null, 0, null, null, false, 0, false, 0, 0L, false), "sendRetrySms");
            return;
        } else {
            smsDispatchersController = this;
            if (smsDispatchersController.mImsSmsDispatcher.isAvailable()) {
                z = true;
            }
        }
        smsDispatchersController.sendRetrySms(smsTracker, z);
    }

    public void sendRetrySms(SMSDispatcher.SmsTracker smsTracker, boolean z) {
        String format;
        SMSDispatcher sMSDispatcher;
        SmsMessage.SubmitPdu submitPdu;
        String str = smsTracker.mFormat;
        if (z) {
            format = this.mImsSmsDispatcher.getFormat();
        } else if (2 == this.mPhone.getPhoneType()) {
            format = this.mCdmaDispatcher.getFormat();
        } else {
            format = this.mGsmDispatcher.getFormat();
        }
        Rlog.d("SmsDispatchersController", "old format(" + str + ") ==> new format (" + format + ")");
        if (!str.equals(format)) {
            HashMap<String, Object> data = smsTracker.getData();
            if (!data.containsKey("scAddr") || !data.containsKey("destAddr") || (!data.containsKey("text") && (!data.containsKey("data") || !data.containsKey("destPort")))) {
                Rlog.e("SmsDispatchersController", "sendRetrySms failed to re-encode per missing fields!");
                smsTracker.onFailed(this.mContext, 30, -1);
                return;
            }
            String str2 = (String) data.get("scAddr");
            String str3 = (String) data.get("destAddr");
            if (str3 == null) {
                Rlog.e("SmsDispatchersController", "sendRetrySms failed due to null destAddr");
                smsTracker.onFailed(this.mContext, 30, -1);
                return;
            }
            if (data.containsKey("text")) {
                String str4 = (String) data.get("text");
                StringBuilder sb = new StringBuilder();
                sb.append("sms failed was text with length: ");
                sb.append(str4 == null ? null : Integer.valueOf(str4.length()));
                Rlog.d("SmsDispatchersController", sb.toString());
                if (isCdmaFormat(format)) {
                    submitPdu = com.android.internal.telephony.cdma.SmsMessage.getSubmitPdu(str2, str3, str4, smsTracker.mDeliveryIntent != null, (SmsHeader) null);
                } else {
                    submitPdu = com.android.internal.telephony.gsm.SmsMessage.getSubmitPdu(str2, str3, str4, smsTracker.mDeliveryIntent != null, (byte[]) null, 0, 0, 0, -1, smsTracker.mMessageRef);
                }
                r15 = submitPdu;
            } else if (data.containsKey("data")) {
                byte[] bArr = (byte[]) data.get("data");
                Integer num = (Integer) data.get("destPort");
                StringBuilder sb2 = new StringBuilder();
                sb2.append("sms failed was data with length: ");
                sb2.append(bArr != null ? Integer.valueOf(bArr.length) : null);
                Rlog.d("SmsDispatchersController", sb2.toString());
                if (isCdmaFormat(format)) {
                    r15 = com.android.internal.telephony.cdma.SmsMessage.getSubmitPdu(str2, str3, num.intValue(), bArr, smsTracker.mDeliveryIntent != null);
                } else {
                    r15 = com.android.internal.telephony.gsm.SmsMessage.getSubmitPdu(str2, str3, num.intValue(), bArr, smsTracker.mDeliveryIntent != null, smsTracker.mMessageRef);
                }
            }
            if (r15 == null) {
                Rlog.e("SmsDispatchersController", String.format("sendRetrySms failed to encode message.scAddr: %s, destPort: %s", str2, data.get("destPort")));
                smsTracker.onFailed(this.mContext, 30, -1);
                return;
            }
            data.put("smsc", ((SmsMessageBase.SubmitPduBase) r15).encodedScAddress);
            data.put("pdu", ((SmsMessageBase.SubmitPduBase) r15).encodedMessage);
            smsTracker.mFormat = format;
        }
        if (z) {
            sMSDispatcher = this.mImsSmsDispatcher;
        } else {
            sMSDispatcher = isCdmaFormat(format) ? this.mCdmaDispatcher : this.mGsmDispatcher;
        }
        sMSDispatcher.sendSms(smsTracker);
    }

    public void reportSmsMemoryStatus(Message message) {
        Rlog.d("SmsDispatchersController", "reportSmsMemoryStatus: ");
        try {
            this.mImsSmsDispatcher.onMemoryAvailable();
            AsyncResult.forMessage(message, (Object) null, (Throwable) null);
            message.sendToTarget();
        } catch (Exception e) {
            Rlog.e("SmsDispatchersController", "reportSmsMemoryStatus Failed ", e);
            AsyncResult.forMessage(message, (Object) null, e);
            message.sendToTarget();
        }
    }

    public boolean isIms() {
        if (this.mImsSmsDispatcher.isAvailable()) {
            return true;
        }
        return this.mIms;
    }

    public String getImsSmsFormat() {
        return this.mImsSmsDispatcher.isAvailable() ? this.mImsSmsDispatcher.getFormat() : this.mImsSmsFormat;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isCdmaMo() {
        if (isIms()) {
            return isCdmaFormat(getImsSmsFormat());
        }
        return 2 == this.mPhone.getPhoneType();
    }

    public boolean isCdmaFormat(String str) {
        return this.mCdmaDispatcher.getFormat().equals(str);
    }

    @VisibleForTesting
    public void setDomainSelectionResolverProxy(DomainSelectionResolverProxy domainSelectionResolverProxy) {
        this.mDomainSelectionResolverProxy = domainSelectionResolverProxy;
    }

    private boolean isCdmaMo(int i) {
        if (i != 2) {
            return 2 == this.mPhone.getPhoneType();
        }
        return isCdmaFormat(this.mImsSmsDispatcher.getFormat());
    }

    @VisibleForTesting
    protected DomainSelectionConnectionHolder getDomainSelectionConnectionHolder(boolean z) {
        return z ? this.mEmergencyDscHolder : this.mDscHolder;
    }

    private DomainSelectionConnectionHolder getDomainSelectionConnection(boolean z) {
        DomainSelectionConnectionHolder domainSelectionConnectionHolder = getDomainSelectionConnectionHolder(z);
        DomainSelectionConnection connection = domainSelectionConnectionHolder != null ? domainSelectionConnectionHolder.getConnection() : null;
        if (connection == null && (connection = this.mDomainSelectionResolverProxy.getDomainSelectionConnection(this.mPhone, 2, z)) == null) {
            return null;
        }
        if (domainSelectionConnectionHolder == null) {
            domainSelectionConnectionHolder = new DomainSelectionConnectionHolder(z);
            if (z) {
                this.mEmergencyDscHolder = domainSelectionConnectionHolder;
            } else {
                this.mDscHolder = domainSelectionConnectionHolder;
            }
        }
        domainSelectionConnectionHolder.setConnection(connection);
        return domainSelectionConnectionHolder;
    }

    private void requestDomainSelection(final DomainSelectionConnectionHolder domainSelectionConnectionHolder) {
        DomainSelectionService.SelectionAttributes build = new DomainSelectionService.SelectionAttributes.Builder(this.mPhone.getPhoneId(), this.mPhone.getSubId(), 2).setEmergency(domainSelectionConnectionHolder.isEmergency()).build();
        if (domainSelectionConnectionHolder.isEmergency()) {
            ((EmergencySmsDomainSelectionConnection) domainSelectionConnectionHolder.getConnection()).requestDomainSelection(build, domainSelectionConnectionHolder).thenAcceptAsync(new Consumer() { // from class: com.android.internal.telephony.SmsDispatchersController$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    SmsDispatchersController.this.lambda$requestDomainSelection$0(domainSelectionConnectionHolder, (Integer) obj);
                }
            }, new Executor() { // from class: com.android.internal.telephony.SmsDispatchersController$$ExternalSyntheticLambda1
                @Override // java.util.concurrent.Executor
                public final void execute(Runnable runnable) {
                    SmsDispatchersController.this.post(runnable);
                }
            });
        } else {
            ((SmsDomainSelectionConnection) domainSelectionConnectionHolder.getConnection()).requestDomainSelection(build, domainSelectionConnectionHolder).thenAcceptAsync(new Consumer() { // from class: com.android.internal.telephony.SmsDispatchersController$$ExternalSyntheticLambda2
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    SmsDispatchersController.this.lambda$requestDomainSelection$1(domainSelectionConnectionHolder, (Integer) obj);
                }
            }, new Executor() { // from class: com.android.internal.telephony.SmsDispatchersController$$ExternalSyntheticLambda1
                @Override // java.util.concurrent.Executor
                public final void execute(Runnable runnable) {
                    SmsDispatchersController.this.post(runnable);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestDomainSelection$0(DomainSelectionConnectionHolder domainSelectionConnectionHolder, Integer num) {
        sendAllPendingRequests(domainSelectionConnectionHolder, num.intValue());
        finishDomainSelection(domainSelectionConnectionHolder);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestDomainSelection$1(DomainSelectionConnectionHolder domainSelectionConnectionHolder, Integer num) {
        sendAllPendingRequests(domainSelectionConnectionHolder, num.intValue());
        finishDomainSelection(domainSelectionConnectionHolder);
    }

    private void sendSmsUsingDomainSelection(DomainSelectionConnectionHolder domainSelectionConnectionHolder, PendingRequest pendingRequest, String str) {
        boolean isDomainSelectionRequested = domainSelectionConnectionHolder.isDomainSelectionRequested();
        domainSelectionConnectionHolder.addRequest(pendingRequest);
        if (isDomainSelectionRequested) {
            return;
        }
        requestDomainSelection(domainSelectionConnectionHolder);
    }

    private void finishDomainSelection(DomainSelectionConnectionHolder domainSelectionConnectionHolder) {
        DomainSelectionConnection connection = domainSelectionConnectionHolder != null ? domainSelectionConnectionHolder.getConnection() : null;
        if (connection != null) {
            connection.finishSelection();
        }
        if (domainSelectionConnectionHolder != null) {
            List<PendingRequest> pendingRequests = domainSelectionConnectionHolder.getPendingRequests();
            logd("finishDomainSelection: pendingRequests=" + pendingRequests.size());
            for (PendingRequest pendingRequest : pendingRequests) {
                triggerSentIntentForFailure(pendingRequest.sentIntents);
            }
            domainSelectionConnectionHolder.clearAllRequests();
            domainSelectionConnectionHolder.setConnection(null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyDomainSelectionTerminated(DomainSelectionConnectionHolder domainSelectionConnectionHolder) {
        List<PendingRequest> pendingRequests = domainSelectionConnectionHolder.getPendingRequests();
        logd("notifyDomainSelectionTerminated: pendingRequests=" + pendingRequests.size());
        for (PendingRequest pendingRequest : pendingRequests) {
            triggerSentIntentForFailure(pendingRequest.sentIntents);
        }
        domainSelectionConnectionHolder.setConnection(null);
        domainSelectionConnectionHolder.clearAllRequests();
    }

    private void sendAllPendingRequests(DomainSelectionConnectionHolder domainSelectionConnectionHolder, int i) {
        for (PendingRequest pendingRequest : domainSelectionConnectionHolder.getPendingRequests()) {
            int i2 = pendingRequest.type;
            if (i2 == 1) {
                sendData(i, pendingRequest);
            } else if (i2 == 2) {
                sendText(i, pendingRequest);
            } else if (i2 == 3) {
                sendMultipartText(i, pendingRequest);
            } else if (i2 == 4) {
                sendRetrySms(pendingRequest.tracker, i == 2);
            }
        }
        domainSelectionConnectionHolder.clearAllRequests();
    }

    private void sendData(int i, PendingRequest pendingRequest) {
        if (i == 2) {
            this.mImsSmsDispatcher.sendData(pendingRequest.callingPackage, pendingRequest.destAddr, pendingRequest.scAddr, pendingRequest.destPort, pendingRequest.data, pendingRequest.sentIntents.get(0), pendingRequest.deliveryIntents.get(0), pendingRequest.isForVvm);
        } else if (isCdmaMo(i)) {
            this.mCdmaDispatcher.sendData(pendingRequest.callingPackage, pendingRequest.destAddr, pendingRequest.scAddr, pendingRequest.destPort, pendingRequest.data, pendingRequest.sentIntents.get(0), pendingRequest.deliveryIntents.get(0), pendingRequest.isForVvm);
        } else {
            this.mGsmDispatcher.sendData(pendingRequest.callingPackage, pendingRequest.destAddr, pendingRequest.scAddr, pendingRequest.destPort, pendingRequest.data, pendingRequest.sentIntents.get(0), pendingRequest.deliveryIntents.get(0), pendingRequest.isForVvm);
        }
    }

    private void sendText(int i, PendingRequest pendingRequest) {
        if (i == 2) {
            this.mImsSmsDispatcher.sendText(pendingRequest.destAddr, pendingRequest.scAddr, pendingRequest.texts.get(0), pendingRequest.sentIntents.get(0), pendingRequest.deliveryIntents.get(0), pendingRequest.messageUri, pendingRequest.callingPackage, pendingRequest.persistMessage, pendingRequest.priority, false, pendingRequest.validityPeriod, pendingRequest.isForVvm, pendingRequest.messageId, pendingRequest.skipShortCodeCheck);
        } else if (isCdmaMo(i)) {
            this.mCdmaDispatcher.sendText(pendingRequest.destAddr, pendingRequest.scAddr, pendingRequest.texts.get(0), pendingRequest.sentIntents.get(0), pendingRequest.deliveryIntents.get(0), pendingRequest.messageUri, pendingRequest.callingPackage, pendingRequest.persistMessage, pendingRequest.priority, pendingRequest.expectMore, pendingRequest.validityPeriod, pendingRequest.isForVvm, pendingRequest.messageId, pendingRequest.skipShortCodeCheck);
        } else {
            this.mGsmDispatcher.sendText(pendingRequest.destAddr, pendingRequest.scAddr, pendingRequest.texts.get(0), pendingRequest.sentIntents.get(0), pendingRequest.deliveryIntents.get(0), pendingRequest.messageUri, pendingRequest.callingPackage, pendingRequest.persistMessage, pendingRequest.priority, pendingRequest.expectMore, pendingRequest.validityPeriod, pendingRequest.isForVvm, pendingRequest.messageId, pendingRequest.skipShortCodeCheck);
        }
    }

    private void sendMultipartText(int i, PendingRequest pendingRequest) {
        if (i == 2) {
            this.mImsSmsDispatcher.sendMultipartText(pendingRequest.destAddr, pendingRequest.scAddr, pendingRequest.texts, pendingRequest.sentIntents, pendingRequest.deliveryIntents, pendingRequest.messageUri, pendingRequest.callingPackage, pendingRequest.persistMessage, pendingRequest.priority, false, pendingRequest.validityPeriod, pendingRequest.messageId);
        } else if (isCdmaMo(i)) {
            this.mCdmaDispatcher.sendMultipartText(pendingRequest.destAddr, pendingRequest.scAddr, pendingRequest.texts, pendingRequest.sentIntents, pendingRequest.deliveryIntents, pendingRequest.messageUri, pendingRequest.callingPackage, pendingRequest.persistMessage, pendingRequest.priority, pendingRequest.expectMore, pendingRequest.validityPeriod, pendingRequest.messageId);
        } else {
            this.mGsmDispatcher.sendMultipartText(pendingRequest.destAddr, pendingRequest.scAddr, pendingRequest.texts, pendingRequest.sentIntents, pendingRequest.deliveryIntents, pendingRequest.messageUri, pendingRequest.callingPackage, pendingRequest.persistMessage, pendingRequest.priority, pendingRequest.expectMore, pendingRequest.validityPeriod, pendingRequest.messageId);
        }
    }

    private void triggerSentIntentForFailure(PendingIntent pendingIntent) {
        try {
            pendingIntent.send(1);
        } catch (PendingIntent.CanceledException unused) {
            logd("Intent has been canceled!");
        }
    }

    private void triggerSentIntentForFailure(List<PendingIntent> list) {
        for (PendingIntent pendingIntent : list) {
            triggerSentIntentForFailure(pendingIntent);
        }
    }

    private static <T> ArrayList<T> asArrayList(T t) {
        ArrayList<T> arrayList = new ArrayList<>();
        arrayList.add(t);
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void sendData(String str, String str2, String str3, int i, byte[] bArr, PendingIntent pendingIntent, PendingIntent pendingIntent2, boolean z) {
        DomainSelectionConnectionHolder domainSelectionConnection;
        String smscAddressFromUSIM = str3 == null ? getSmscAddressFromUSIM(str) : str3;
        if (this.mDomainSelectionResolverProxy.isDomainSelectionSupported() && (domainSelectionConnection = getDomainSelectionConnection(false)) != null && domainSelectionConnection.getConnection() != null) {
            sendSmsUsingDomainSelection(domainSelectionConnection, new PendingRequest(1, null, str, str2, smscAddressFromUSIM, asArrayList(pendingIntent), asArrayList(pendingIntent2), z, bArr, i, null, null, false, 0, false, 0, 0L, false), "sendData");
        } else if (this.mImsSmsDispatcher.isAvailable()) {
            this.mImsSmsDispatcher.sendData(str, str2, smscAddressFromUSIM, i, bArr, pendingIntent, pendingIntent2, z);
        } else if (isCdmaMo()) {
            this.mCdmaDispatcher.sendData(str, str2, smscAddressFromUSIM, i, bArr, pendingIntent, pendingIntent2, z);
        } else {
            this.mGsmDispatcher.sendData(str, str2, smscAddressFromUSIM, i, bArr, pendingIntent, pendingIntent2, z);
        }
    }

    public void sendText(String str, String str2, String str3, PendingIntent pendingIntent, PendingIntent pendingIntent2, Uri uri, String str4, boolean z, int i, boolean z2, int i2, boolean z3, long j) {
        sendText(str, str2, str3, pendingIntent, pendingIntent2, uri, str4, z, i, z2, i2, z3, j, false);
    }

    public void sendText(String str, String str2, String str3, PendingIntent pendingIntent, PendingIntent pendingIntent2, Uri uri, String str4, boolean z, int i, boolean z2, int i2, boolean z3, long j, boolean z4) {
        DomainSelectionConnectionHolder domainSelectionConnection;
        String smscAddressFromUSIM = str2 == null ? getSmscAddressFromUSIM(str4) : str2;
        if (this.mDomainSelectionResolverProxy.isDomainSelectionSupported() && (domainSelectionConnection = getDomainSelectionConnection(((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).isEmergencyNumber(str))) != null && domainSelectionConnection.getConnection() != null) {
            sendSmsUsingDomainSelection(domainSelectionConnection, new PendingRequest(2, null, str4, str, smscAddressFromUSIM, asArrayList(pendingIntent), asArrayList(pendingIntent2), z3, null, 0, asArrayList(str3), uri, z, i, z2, i2, j, z4), "sendText");
        } else if (this.mImsSmsDispatcher.isAvailable() || this.mImsSmsDispatcher.isEmergencySmsSupport(str)) {
            this.mImsSmsDispatcher.sendText(str, smscAddressFromUSIM, str3, pendingIntent, pendingIntent2, uri, str4, z, i, false, i2, z3, j, z4);
        } else if (isCdmaMo()) {
            this.mCdmaDispatcher.sendText(str, smscAddressFromUSIM, str3, pendingIntent, pendingIntent2, uri, str4, z, i, z2, i2, z3, j, z4);
        } else {
            this.mGsmDispatcher.sendText(str, smscAddressFromUSIM, str3, pendingIntent, pendingIntent2, uri, str4, z, i, z2, i2, z3, j, z4);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void sendMultipartText(String str, String str2, ArrayList<String> arrayList, ArrayList<PendingIntent> arrayList2, ArrayList<PendingIntent> arrayList3, Uri uri, String str3, boolean z, int i, boolean z2, int i2, long j) {
        DomainSelectionConnectionHolder domainSelectionConnection;
        String smscAddressFromUSIM = str2 == null ? getSmscAddressFromUSIM(str3) : str2;
        if (this.mDomainSelectionResolverProxy.isDomainSelectionSupported() && (domainSelectionConnection = getDomainSelectionConnection(false)) != null && domainSelectionConnection.getConnection() != null) {
            sendSmsUsingDomainSelection(domainSelectionConnection, new PendingRequest(3, null, str3, str, smscAddressFromUSIM, arrayList2, arrayList3, false, null, 0, arrayList, uri, z, i, z2, i2, j, false), "sendMultipartText");
        } else if (this.mImsSmsDispatcher.isAvailable()) {
            this.mImsSmsDispatcher.sendMultipartText(str, smscAddressFromUSIM, arrayList, arrayList2, arrayList3, uri, str3, z, i, false, i2, j);
        } else if (isCdmaMo()) {
            this.mCdmaDispatcher.sendMultipartText(str, smscAddressFromUSIM, arrayList, arrayList2, arrayList3, uri, str3, z, i, z2, i2, j);
        } else {
            this.mGsmDispatcher.sendMultipartText(str, smscAddressFromUSIM, arrayList, arrayList2, arrayList3, uri, str3, z, i, z2, i2, j);
        }
    }

    public int getPremiumSmsPermission(String str) {
        return this.mUsageMonitor.getPremiumSmsPermission(str);
    }

    public void setPremiumSmsPermission(String str, int i) {
        this.mUsageMonitor.setPremiumSmsPermission(str, i);
    }

    public SmsUsageMonitor getUsageMonitor() {
        return this.mUsageMonitor;
    }

    public void handleSmsStatusReport(String str, byte[] bArr) {
        boolean z = false;
        if (isCdmaFormat(str)) {
            com.android.internal.telephony.cdma.SmsMessage createFromPdu = com.android.internal.telephony.cdma.SmsMessage.createFromPdu(bArr);
            if (createFromPdu != null) {
                int i = createFromPdu.mMessageRef;
                SMSDispatcher.SmsTracker smsTracker = this.mDeliveryPendingMapFor3GPP2.get(Integer.valueOf(i));
                boolean z2 = smsTracker == null && (smsTracker = this.mDeliveryPendingMapFor3GPP.get(Integer.valueOf(i))) != null;
                if (smsTracker != null) {
                    int status = (createFromPdu.getStatus() >> 24) & 3;
                    if (status != 2) {
                        smsTracker.updateSentMessageStatus(this.mContext, status == 0 ? 0 : 64);
                        if (z2) {
                            this.mDeliveryPendingMapFor3GPP.remove(Integer.valueOf(i));
                        } else {
                            this.mDeliveryPendingMapFor3GPP2.remove(Integer.valueOf(i));
                        }
                    }
                    z = triggerDeliveryIntent(smsTracker, str, bArr);
                }
            }
        } else {
            com.android.internal.telephony.gsm.SmsMessage createFromPdu2 = com.android.internal.telephony.gsm.SmsMessage.createFromPdu(bArr);
            if (createFromPdu2 != null) {
                int i2 = createFromPdu2.mMessageRef;
                SMSDispatcher.SmsTracker smsTracker2 = this.mDeliveryPendingMapFor3GPP.get(Integer.valueOf(i2));
                if (smsTracker2 != null) {
                    int status2 = createFromPdu2.getStatus();
                    if (status2 >= 64 || status2 < 32) {
                        smsTracker2.updateSentMessageStatus(this.mContext, status2);
                        this.mDeliveryPendingMapFor3GPP.remove(Integer.valueOf(i2));
                    }
                    z = triggerDeliveryIntent(smsTracker2, str, bArr);
                }
            }
        }
        if (z) {
            return;
        }
        Rlog.e("SmsDispatchersController", "handleSmsStatusReport: can not handle the status report!");
    }

    private boolean triggerDeliveryIntent(SMSDispatcher.SmsTracker smsTracker, String str, byte[] bArr) {
        PendingIntent pendingIntent = smsTracker.mDeliveryIntent;
        Intent intent = new Intent();
        intent.putExtra("pdu", bArr);
        intent.putExtra("format", str);
        try {
            pendingIntent.send(this.mContext, -1, intent);
            return true;
        } catch (PendingIntent.CanceledException unused) {
            return false;
        }
    }

    public InboundSmsHandler getInboundSmsHandler(boolean z) {
        if (z) {
            return this.mCdmaInboundSmsHandler;
        }
        return this.mGsmInboundSmsHandler;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        this.mGsmInboundSmsHandler.dump(fileDescriptor, printWriter, strArr);
        this.mCdmaInboundSmsHandler.dump(fileDescriptor, printWriter, strArr);
        this.mGsmDispatcher.dump(fileDescriptor, printWriter, strArr);
        this.mCdmaDispatcher.dump(fileDescriptor, printWriter, strArr);
        this.mImsSmsDispatcher.dump(fileDescriptor, printWriter, strArr);
    }

    private void logd(String str) {
        Rlog.d("SmsDispatchersController", str);
    }
}
