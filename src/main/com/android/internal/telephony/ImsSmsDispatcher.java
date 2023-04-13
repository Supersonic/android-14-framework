package com.android.internal.telephony;

import android.content.Context;
import android.os.Binder;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.telephony.CarrierConfigManager;
import android.telephony.SmsMessage;
import android.telephony.ims.ImsMmTelManager;
import android.telephony.ims.ImsReasonInfo;
import android.telephony.ims.RegistrationManager;
import android.telephony.ims.aidl.IImsSmsListener;
import android.telephony.ims.feature.MmTelFeature;
import com.android.ims.FeatureConnector;
import com.android.ims.ImsException;
import com.android.ims.ImsManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.GsmAlphabet;
import com.android.internal.telephony.ImsSmsDispatcher;
import com.android.internal.telephony.SMSDispatcher;
import com.android.internal.telephony.SmsDispatchersController;
import com.android.internal.telephony.SmsMessageBase;
import com.android.internal.telephony.metrics.TelephonyMetrics;
import com.android.internal.telephony.uicc.IccUtils;
import com.android.internal.telephony.util.SMSDispatcherUtil;
import com.android.telephony.Rlog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes.dex */
public class ImsSmsDispatcher extends SMSDispatcher {
    public static final int MAX_SEND_RETRIES_OVER_IMS = 3;
    private ImsMmTelManager.CapabilityCallback mCapabilityCallback;
    private Runnable mConnectRunnable;
    private FeatureConnectorFactory mConnectorFactory;
    private ImsManager mImsManager;
    private final FeatureConnector<ImsManager> mImsManagerConnector;
    private final IImsSmsListener mImsSmsListener;
    private volatile boolean mIsImsServiceUp;
    private volatile boolean mIsRegistered;
    private volatile boolean mIsSmsCapable;
    private final Object mLock;
    public List<Integer> mMemoryAvailableNotifierList;
    private TelephonyMetrics mMetrics;
    @VisibleForTesting
    public AtomicInteger mNextToken;
    private RegistrationManager.RegistrationCallback mRegistrationCallback;
    @VisibleForTesting
    public Map<Integer, SMSDispatcher.SmsTracker> mTrackers;

    @VisibleForTesting
    /* loaded from: classes.dex */
    public interface FeatureConnectorFactory {
        FeatureConnector<ImsManager> create(Context context, int i, String str, FeatureConnector.Listener<ImsManager> listener, Executor executor);
    }

    @Override // com.android.internal.telephony.SMSDispatcher
    protected boolean shouldBlockSmsForEcbm() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.internal.telephony.ImsSmsDispatcher$4 */
    /* loaded from: classes.dex */
    public class C00434 extends IImsSmsListener.Stub {
        C00434() {
        }

        public void onMemoryAvailableResult(int i, int i2, int i3) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                ImsSmsDispatcher imsSmsDispatcher = ImsSmsDispatcher.this;
                imsSmsDispatcher.logd("onMemoryAvailableResult token=" + i + " status=" + i2 + " networkReasonCode=" + i3);
                if (!ImsSmsDispatcher.this.mMemoryAvailableNotifierList.contains(Integer.valueOf(i))) {
                    ImsSmsDispatcher.this.loge("onMemoryAvailableResult Invalid token");
                    return;
                }
                ImsSmsDispatcher.this.mMemoryAvailableNotifierList.remove(Integer.valueOf(i));
                if (i2 == 3) {
                    ImsSmsDispatcher imsSmsDispatcher2 = ImsSmsDispatcher.this;
                    if (!imsSmsDispatcher2.mRPSmmaRetried) {
                        imsSmsDispatcher2.sendMessageDelayed(imsSmsDispatcher2.obtainMessage(11), 2000L);
                        ImsSmsDispatcher.this.mRPSmmaRetried = true;
                    } else {
                        imsSmsDispatcher2.mRPSmmaRetried = false;
                    }
                } else {
                    ImsSmsDispatcher.this.mRPSmmaRetried = false;
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void onSendSmsResult(int i, int i2, int i3, int i4, int i5) {
            int i6;
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                ImsSmsDispatcher.this.logd("onSendSmsResult token=" + i + " messageRef=" + i2 + " status=" + i3 + " reason=" + i4 + " networkReasonCode=" + i5);
                SMSDispatcher.SmsTracker smsTracker = ImsSmsDispatcher.this.mTrackers.get(Integer.valueOf(i));
                ImsSmsDispatcher.this.mMetrics.writeOnImsServiceSmsSolicitedResponse(ImsSmsDispatcher.this.mPhone.getPhoneId(), i3, i4, smsTracker != null ? smsTracker.mMessageId : 0L);
                if (smsTracker == null) {
                    throw new IllegalArgumentException("Invalid token.");
                }
                smsTracker.mMessageRef = i2;
                if (i3 == 1) {
                    if (smsTracker.mDeliveryIntent != null) {
                        ImsSmsDispatcher.this.mSmsDispatchersController.putDeliveryPendingTracker(smsTracker);
                    }
                    smsTracker.onSent(ImsSmsDispatcher.this.mContext);
                    ImsSmsDispatcher.this.mTrackers.remove(Integer.valueOf(i));
                    ImsSmsDispatcher.this.mPhone.notifySmsSent(smsTracker.mDestAddress);
                } else if (i3 == 2) {
                    smsTracker.onFailed(ImsSmsDispatcher.this.mContext, i4, i5);
                    ImsSmsDispatcher.this.mTrackers.remove(Integer.valueOf(i));
                } else if (i3 == 3) {
                    int maxRetryCountOverIms = ImsSmsDispatcher.this.getMaxRetryCountOverIms();
                    if (smsTracker.mRetryCount < ImsSmsDispatcher.this.getMaxSmsRetryCount()) {
                        if (maxRetryCountOverIms < ImsSmsDispatcher.this.getMaxSmsRetryCount() && (i6 = smsTracker.mRetryCount) >= maxRetryCountOverIms) {
                            smsTracker.mRetryCount = i6 + 1;
                            ImsSmsDispatcher.this.mTrackers.remove(Integer.valueOf(i));
                            ImsSmsDispatcher.this.fallbackToPstn(smsTracker);
                        } else {
                            smsTracker.mRetryCount++;
                            ImsSmsDispatcher imsSmsDispatcher = ImsSmsDispatcher.this;
                            imsSmsDispatcher.sendMessageDelayed(imsSmsDispatcher.obtainMessage(3, smsTracker), ImsSmsDispatcher.this.getSmsRetryDelayValue());
                        }
                    } else {
                        smsTracker.onFailed(ImsSmsDispatcher.this.mContext, i4, i5);
                        ImsSmsDispatcher.this.mTrackers.remove(Integer.valueOf(i));
                    }
                } else if (i3 == 4) {
                    smsTracker.mRetryCount++;
                    ImsSmsDispatcher.this.mTrackers.remove(Integer.valueOf(i));
                    ImsSmsDispatcher.this.fallbackToPstn(smsTracker);
                }
                ImsSmsDispatcher.this.mPhone.getSmsStats().onOutgoingSms(true, "3gpp2".equals(ImsSmsDispatcher.this.getFormat()), i3 == 4, i4, i5, smsTracker.mMessageId, smsTracker.isFromDefaultSmsApplication(ImsSmsDispatcher.this.mContext), smsTracker.getInterval());
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void onSmsStatusReportReceived(int i, String str, byte[] bArr) throws RemoteException {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                ImsSmsDispatcher.this.logd("Status report received.");
                SmsMessage createFromPdu = SmsMessage.createFromPdu(bArr, str);
                if (createFromPdu == null || createFromPdu.mWrappedSmsMessage == null) {
                    throw new RemoteException("Status report received with a PDU that could not be parsed.");
                }
                ImsSmsDispatcher.this.mSmsDispatchersController.handleSmsStatusReport(str, bArr);
                try {
                    ImsSmsDispatcher.this.getImsManager().acknowledgeSmsReport(i, createFromPdu.mWrappedSmsMessage.mMessageRef, 1);
                } catch (ImsException e) {
                    ImsSmsDispatcher imsSmsDispatcher = ImsSmsDispatcher.this;
                    imsSmsDispatcher.loge("Failed to acknowledgeSmsReport(). Error: " + e.getMessage());
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void onSmsReceived(final int i, String str, byte[] bArr) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                ImsSmsDispatcher.this.logd("SMS received.");
                final SmsMessage createFromPdu = SmsMessage.createFromPdu(bArr, str);
                ImsSmsDispatcher.this.mSmsDispatchersController.injectSmsPdu(createFromPdu, str, new SmsDispatchersController.SmsInjectionCallback() { // from class: com.android.internal.telephony.ImsSmsDispatcher$4$$ExternalSyntheticLambda0
                    @Override // com.android.internal.telephony.SmsDispatchersController.SmsInjectionCallback
                    public final void onSmsInjectedResult(int i2) {
                        ImsSmsDispatcher.C00434.this.lambda$onSmsReceived$0(createFromPdu, i, i2);
                    }
                }, true, true, i);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSmsReceived$0(SmsMessage smsMessage, int i, int i2) {
            ImsSmsDispatcher imsSmsDispatcher = ImsSmsDispatcher.this;
            imsSmsDispatcher.logd("SMS handled result: " + i2);
            if (i2 != -1) {
                int i3 = 1;
                if (i2 != 1) {
                    i3 = 3;
                    if (i2 != 3) {
                        i3 = 4;
                        if (i2 != 4) {
                            i3 = 2;
                        }
                    }
                }
                if (smsMessage != null) {
                    try {
                        if (smsMessage.mWrappedSmsMessage != null) {
                            ImsSmsDispatcher.this.getImsManager().acknowledgeSms(i, smsMessage.mWrappedSmsMessage.mMessageRef, i3);
                        }
                    } catch (ImsException e) {
                        ImsSmsDispatcher imsSmsDispatcher2 = ImsSmsDispatcher.this;
                        imsSmsDispatcher2.loge("Failed to acknowledgeSms(). Error: " + e.getMessage());
                        return;
                    }
                }
                ImsSmsDispatcher.this.logw("SMS Received with a PDU that could not be parsed.");
                ImsSmsDispatcher.this.getImsManager().acknowledgeSms(i, 0, i3);
            }
        }
    }

    @Override // com.android.internal.telephony.SMSDispatcher, android.os.Handler
    public void handleMessage(Message message) {
        int i = message.what;
        if (i == 3) {
            logd("SMS retry..");
            sendSms((SMSDispatcher.SmsTracker) message.obj);
        } else if (i == 11) {
            logd("SMMA Notification retry..");
            onMemoryAvailable();
        } else {
            super.handleMessage(message);
        }
    }

    public ImsSmsDispatcher(Phone phone, SmsDispatchersController smsDispatchersController, FeatureConnectorFactory featureConnectorFactory) {
        super(phone, smsDispatchersController);
        this.mMemoryAvailableNotifierList = new ArrayList();
        this.mTrackers = new ConcurrentHashMap();
        this.mNextToken = new AtomicInteger();
        this.mLock = new Object();
        this.mMetrics = TelephonyMetrics.getInstance();
        this.mConnectRunnable = new Runnable() { // from class: com.android.internal.telephony.ImsSmsDispatcher.1
            @Override // java.lang.Runnable
            public void run() {
                ImsSmsDispatcher.this.mImsManagerConnector.connect();
            }
        };
        this.mRegistrationCallback = new RegistrationManager.RegistrationCallback() { // from class: com.android.internal.telephony.ImsSmsDispatcher.2
            @Override // android.telephony.ims.RegistrationManager.RegistrationCallback
            public void onRegistered(int i) {
                ImsSmsDispatcher imsSmsDispatcher = ImsSmsDispatcher.this;
                imsSmsDispatcher.logd("onImsConnected imsRadioTech=" + i);
                synchronized (ImsSmsDispatcher.this.mLock) {
                    ImsSmsDispatcher.this.mIsRegistered = true;
                }
            }

            @Override // android.telephony.ims.RegistrationManager.RegistrationCallback
            public void onRegistering(int i) {
                ImsSmsDispatcher imsSmsDispatcher = ImsSmsDispatcher.this;
                imsSmsDispatcher.logd("onImsProgressing imsRadioTech=" + i);
                synchronized (ImsSmsDispatcher.this.mLock) {
                    ImsSmsDispatcher.this.mIsRegistered = false;
                }
            }

            @Override // android.telephony.ims.RegistrationManager.RegistrationCallback
            public void onUnregistered(ImsReasonInfo imsReasonInfo) {
                ImsSmsDispatcher imsSmsDispatcher = ImsSmsDispatcher.this;
                imsSmsDispatcher.logd("onImsDisconnected imsReasonInfo=" + imsReasonInfo);
                synchronized (ImsSmsDispatcher.this.mLock) {
                    ImsSmsDispatcher.this.mIsRegistered = false;
                }
            }
        };
        this.mCapabilityCallback = new ImsMmTelManager.CapabilityCallback() { // from class: com.android.internal.telephony.ImsSmsDispatcher.3
            @Override // android.telephony.ims.ImsMmTelManager.CapabilityCallback
            public void onCapabilitiesStatusChanged(MmTelFeature.MmTelCapabilities mmTelCapabilities) {
                synchronized (ImsSmsDispatcher.this.mLock) {
                    ImsSmsDispatcher.this.mIsSmsCapable = mmTelCapabilities.isCapable(8);
                }
            }
        };
        this.mImsSmsListener = new C00434();
        this.mConnectorFactory = featureConnectorFactory;
        this.mImsManagerConnector = featureConnectorFactory.create(this.mContext, this.mPhone.getPhoneId(), "ImsSmsDispatcher", new FeatureConnector.Listener<ImsManager>() { // from class: com.android.internal.telephony.ImsSmsDispatcher.5
            public void connectionReady(ImsManager imsManager, int i) throws ImsException {
                ImsSmsDispatcher.this.logd("ImsManager: connection ready.");
                synchronized (ImsSmsDispatcher.this.mLock) {
                    ImsSmsDispatcher.this.mImsManager = imsManager;
                    ImsSmsDispatcher.this.setListeners();
                    ImsSmsDispatcher.this.mIsImsServiceUp = true;
                    ImsSmsDispatcher imsSmsDispatcher = ImsSmsDispatcher.this;
                    imsSmsDispatcher.mSmsDispatchersController.setImsManager(imsSmsDispatcher.mImsManager);
                }
            }

            public void connectionUnavailable(int i) {
                ImsSmsDispatcher imsSmsDispatcher = ImsSmsDispatcher.this;
                imsSmsDispatcher.logd("ImsManager: connection unavailable, reason=" + i);
                if (i == 3) {
                    ImsSmsDispatcher.this.loge("connectionUnavailable: unexpected, received server error");
                    ImsSmsDispatcher imsSmsDispatcher2 = ImsSmsDispatcher.this;
                    imsSmsDispatcher2.removeCallbacks(imsSmsDispatcher2.mConnectRunnable);
                    ImsSmsDispatcher imsSmsDispatcher3 = ImsSmsDispatcher.this;
                    imsSmsDispatcher3.postDelayed(imsSmsDispatcher3.mConnectRunnable, 5000L);
                }
                synchronized (ImsSmsDispatcher.this.mLock) {
                    ImsSmsDispatcher.this.mImsManager = null;
                    ImsSmsDispatcher.this.mIsImsServiceUp = false;
                    ImsSmsDispatcher.this.mSmsDispatchersController.setImsManager(null);
                }
            }
        }, new ImsSmsDispatcher$$ExternalSyntheticLambda0(this));
        post(this.mConnectRunnable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setListeners() throws ImsException {
        getImsManager().addRegistrationCallback(this.mRegistrationCallback, new ImsSmsDispatcher$$ExternalSyntheticLambda0(this));
        getImsManager().addCapabilitiesCallback(this.mCapabilityCallback, new ImsSmsDispatcher$$ExternalSyntheticLambda0(this));
        getImsManager().setSmsListener(getSmsListener());
        getImsManager().onSmsReady();
    }

    private boolean isLteService() {
        return this.mPhone.getServiceState().getRilDataRadioTechnology() == 14 && this.mPhone.getServiceState().getDataRegistrationState() == 0;
    }

    private boolean isLimitedLteService() {
        return this.mPhone.getServiceState().getRilVoiceRadioTechnology() == 14 && this.mPhone.getServiceState().isEmergencyOnly();
    }

    private boolean isEmergencySmsPossible() {
        return isLteService() || isLimitedLteService();
    }

    public boolean isEmergencySmsSupport(String str) {
        boolean z = false;
        if (!this.mTelephonyManager.isEmergencyNumber(str)) {
            logi(Rlog.pii("ImsSmsDispatcher", str) + " is not emergency number");
            return false;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            CarrierConfigManager carrierConfigManager = (CarrierConfigManager) this.mContext.getSystemService("carrier_config");
            if (carrierConfigManager == null) {
                loge("configManager is null");
                return false;
            }
            PersistableBundle configForSubId = carrierConfigManager.getConfigForSubId(getSubId());
            if (configForSubId == null) {
                loge("PersistableBundle is null");
                return false;
            }
            boolean z2 = configForSubId.getBoolean("support_emergency_sms_over_ims_bool");
            boolean isEmergencySmsPossible = isEmergencySmsPossible();
            logi("isEmergencySmsSupport emergencySmsCarrierSupport: " + z2 + " destAddr: " + Rlog.pii("ImsSmsDispatcher", str) + " mIsImsServiceUp: " + this.mIsImsServiceUp + " lteOrLimitedLte: " + isEmergencySmsPossible);
            if (z2 && this.mIsImsServiceUp && isEmergencySmsPossible) {
                z = true;
            }
            return z;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean isAvailable() {
        boolean z;
        synchronized (this.mLock) {
            logd("isAvailable: up=" + this.mIsImsServiceUp + ", reg= " + this.mIsRegistered + ", cap= " + this.mIsSmsCapable);
            z = this.mIsImsServiceUp && this.mIsRegistered && this.mIsSmsCapable;
        }
        return z;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.SMSDispatcher
    public String getFormat() {
        if (this.mLock == null) {
            return "unknown";
        }
        try {
            return getImsManager().getSmsFormat();
        } catch (ImsException e) {
            this.loge("Failed to get sms format. Error: " + e.getMessage());
            return "unknown";
        }
    }

    @Override // com.android.internal.telephony.SMSDispatcher
    public int getMaxSmsRetryCount() {
        PersistableBundle configForSubId;
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) this.mContext.getSystemService("carrier_config");
        int i = (carrierConfigManager == null || (configForSubId = carrierConfigManager.getConfigForSubId(getSubId())) == null) ? 3 : configForSubId.getInt("imssms.sms_max_retry_count_int");
        Rlog.d("ImsSmsDispatcher", "Retry Count: " + i);
        return i;
    }

    @VisibleForTesting
    public int getMaxRetryCountOverIms() {
        PersistableBundle configForSubId;
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) this.mContext.getSystemService("carrier_config");
        int i = (carrierConfigManager == null || (configForSubId = carrierConfigManager.getConfigForSubId(getSubId())) == null) ? 3 : configForSubId.getInt("imssms.sms_max_retry_over_ims_count_int");
        Rlog.d("ImsSmsDispatcher", "Retry Count Over Ims: " + i);
        return i;
    }

    @Override // com.android.internal.telephony.SMSDispatcher
    public int getSmsRetryDelayValue() {
        PersistableBundle configForSubId;
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) this.mContext.getSystemService("carrier_config");
        int i = (carrierConfigManager == null || (configForSubId = carrierConfigManager.getConfigForSubId(getSubId())) == null) ? 2000 : configForSubId.getInt("imssms.sms_over_ims_send_retry_delay_millis_int");
        Rlog.d("ImsSmsDispatcher", "Retry delay: " + i);
        return i;
    }

    @Override // com.android.internal.telephony.SMSDispatcher
    protected SmsMessageBase.SubmitPduBase getSubmitPdu(String str, String str2, String str3, boolean z, SmsHeader smsHeader, int i, int i2) {
        return SMSDispatcherUtil.getSubmitPdu(isCdmaMo(), str, str2, str3, z, smsHeader, i, i2);
    }

    @Override // com.android.internal.telephony.SMSDispatcher
    protected SmsMessageBase.SubmitPduBase getSubmitPdu(String str, String str2, int i, byte[] bArr, boolean z) {
        return SMSDispatcherUtil.getSubmitPdu(isCdmaMo(), str, str2, i, bArr, z);
    }

    @Override // com.android.internal.telephony.SMSDispatcher
    protected SmsMessageBase.SubmitPduBase getSubmitPdu(String str, String str2, String str3, boolean z, SmsHeader smsHeader, int i, int i2, int i3) {
        return SMSDispatcherUtil.getSubmitPdu(isCdmaMo(), str, str2, str3, z, smsHeader, i, i2, i3);
    }

    @Override // com.android.internal.telephony.SMSDispatcher
    protected SmsMessageBase.SubmitPduBase getSubmitPdu(String str, String str2, int i, byte[] bArr, boolean z, int i2) {
        return SMSDispatcherUtil.getSubmitPdu(isCdmaMo(), str, str2, i, bArr, z, i2);
    }

    @Override // com.android.internal.telephony.SMSDispatcher
    protected GsmAlphabet.TextEncodingDetails calculateLength(CharSequence charSequence, boolean z) {
        return SMSDispatcherUtil.calculateLength(isCdmaMo(), charSequence, z);
    }

    public void onMemoryAvailable() {
        logd("onMemoryAvailable ");
        int incrementAndGet = this.mNextToken.incrementAndGet();
        try {
            logd("onMemoryAvailable: token = " + incrementAndGet);
            this.mMemoryAvailableNotifierList.add(Integer.valueOf(incrementAndGet));
            getImsManager().onMemoryAvailable(incrementAndGet);
        } catch (ImsException e) {
            loge("onMemoryAvailable failed: " + e.getMessage());
            if (this.mMemoryAvailableNotifierList.contains(Integer.valueOf(incrementAndGet))) {
                this.mMemoryAvailableNotifierList.remove(Integer.valueOf(incrementAndGet));
            }
        }
    }

    @Override // com.android.internal.telephony.SMSDispatcher
    public void sendSms(SMSDispatcher.SmsTracker smsTracker) {
        logd("sendSms:  mRetryCount=" + smsTracker.mRetryCount + " mMessageRef=" + smsTracker.mMessageRef + " SS=" + this.mPhone.getServiceState().getState());
        smsTracker.mUsesImsServiceForIms = true;
        HashMap<String, Object> data = smsTracker.getData();
        byte[] bArr = (byte[]) data.get("pdu");
        byte[] bArr2 = (byte[]) data.get("smsc");
        boolean z = smsTracker.mRetryCount > 0;
        String format = getFormat();
        if ("3gpp".equals(format) && z) {
            byte b = bArr[0];
            if ((b & 1) == 1) {
                bArr[0] = (byte) (b | 4);
                bArr[1] = (byte) smsTracker.mMessageRef;
            }
        }
        int incrementAndGet = this.mNextToken.incrementAndGet();
        this.mTrackers.put(Integer.valueOf(incrementAndGet), smsTracker);
        try {
            getImsManager().sendSms(incrementAndGet, smsTracker.mMessageRef, format, bArr2 != null ? IccUtils.bytesToHexString(bArr2) : null, z, bArr);
            this.mMetrics.writeImsServiceSendSms(this.mPhone.getPhoneId(), format, 1, smsTracker.mMessageId);
        } catch (ImsException e) {
            loge("sendSms failed. Falling back to PSTN. Error: " + e.getMessage());
            this.mTrackers.remove(Integer.valueOf(incrementAndGet));
            fallbackToPstn(smsTracker);
            this.mMetrics.writeImsServiceSendSms(this.mPhone.getPhoneId(), format, 4, smsTracker.mMessageId);
            this.mPhone.getSmsStats().onOutgoingSms(true, "3gpp2".equals(format), true, 15, smsTracker.mMessageId, smsTracker.isFromDefaultSmsApplication(this.mContext), smsTracker.getInterval());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ImsManager getImsManager() throws ImsException {
        ImsManager imsManager;
        synchronized (this.mLock) {
            imsManager = this.mImsManager;
            if (imsManager == null) {
                throw new ImsException("ImsManager not up", 106);
            }
        }
        return imsManager;
    }

    @VisibleForTesting
    public void fallbackToPstn(SMSDispatcher.SmsTracker smsTracker) {
        smsTracker.mMessageRef = nextMessageRef();
        this.mSmsDispatchersController.sendRetrySms(smsTracker);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.SMSDispatcher
    public boolean isCdmaMo() {
        return this.mSmsDispatchersController.isCdmaFormat(getFormat());
    }

    @VisibleForTesting
    public IImsSmsListener getSmsListener() {
        return this.mImsSmsListener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logd(String str) {
        Rlog.d("ImsSmsDispatcher [" + getPhoneId(this.mPhone) + "]", str);
    }

    private void logi(String str) {
        Rlog.i("ImsSmsDispatcher [" + getPhoneId(this.mPhone) + "]", str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logw(String str) {
        Rlog.w("ImsSmsDispatcher [" + getPhoneId(this.mPhone) + "]", str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loge(String str) {
        Rlog.e("ImsSmsDispatcher [" + getPhoneId(this.mPhone) + "]", str);
    }

    private static String getPhoneId(Phone phone) {
        return phone != null ? Integer.toString(phone.getPhoneId()) : "?";
    }
}
