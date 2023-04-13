package com.android.internal.telephony.data;

import android.content.Context;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.TelephonyNetworkSpecifier;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.SubscriptionManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.CallFailCause;
import com.android.internal.telephony.IndentingPrintWriter;
import com.android.internal.telephony.LocalLog;
import com.android.internal.telephony.NetworkFactory;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.metrics.NetworkRequestsStats;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
public class TelephonyNetworkFactory extends NetworkFactory {
    protected static final boolean DBG = true;
    @VisibleForTesting
    public static final int EVENT_ACTIVE_PHONE_SWITCH = 1;
    @VisibleForTesting
    public static final int EVENT_SUBSCRIPTION_CHANGED = 2;
    public final String LOG_TAG;
    private AccessNetworksManager mAccessNetworksManager;
    @VisibleForTesting
    public final Handler mInternalHandler;
    private final LocalLog mLocalLog;
    private final Map<TelephonyNetworkRequest, Integer> mNetworkRequests;
    private final Phone mPhone;
    private final PhoneSwitcher mPhoneSwitcher;
    private int mSubscriptionId;
    private final SubscriptionManager.OnSubscriptionsChangedListener mSubscriptionsChangedListener;

    private static int getAction(boolean z, boolean z2) {
        if (z || !z2) {
            return (!z || z2) ? 0 : 2;
        }
        return 1;
    }

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public TelephonyNetworkFactory(Looper looper, Phone phone) {
        super(looper, r0, "TelephonyNetworkFactory[" + phone.getPhoneId() + "]", null);
        Context context = phone.getContext();
        this.mLocalLog = new LocalLog(CallFailCause.RADIO_UPLINK_FAILURE);
        this.mNetworkRequests = new HashMap();
        SubscriptionManager.OnSubscriptionsChangedListener onSubscriptionsChangedListener = new SubscriptionManager.OnSubscriptionsChangedListener() { // from class: com.android.internal.telephony.data.TelephonyNetworkFactory.1
            @Override // android.telephony.SubscriptionManager.OnSubscriptionsChangedListener
            public void onSubscriptionsChanged() {
                TelephonyNetworkFactory.this.mInternalHandler.sendEmptyMessage(2);
            }
        };
        this.mSubscriptionsChangedListener = onSubscriptionsChangedListener;
        this.mPhone = phone;
        InternalHandler internalHandler = new InternalHandler(looper);
        this.mInternalHandler = internalHandler;
        this.mAccessNetworksManager = phone.getAccessNetworksManager();
        setCapabilityFilter(makeNetworkFilterByPhoneId(phone.getPhoneId()));
        setScoreFilter(50);
        PhoneSwitcher phoneSwitcher = PhoneSwitcher.getInstance();
        this.mPhoneSwitcher = phoneSwitcher;
        this.LOG_TAG = "TelephonyNetworkFactory[" + phone.getPhoneId() + "]";
        phoneSwitcher.registerForActivePhoneSwitch(internalHandler, 1, null);
        this.mSubscriptionId = -1;
        SubscriptionManager.from(phone.getContext()).addOnSubscriptionsChangedListener(onSubscriptionsChangedListener);
        register();
    }

    private NetworkCapabilities makeNetworkFilterByPhoneId(int i) {
        return makeNetworkFilter(SubscriptionManager.getSubscriptionId(i));
    }

    @VisibleForTesting
    public NetworkCapabilities makeNetworkFilter(int i) {
        return new NetworkCapabilities.Builder().addTransportType(0).addCapability(0).addCapability(1).addCapability(2).addCapability(3).addCapability(4).addCapability(5).addCapability(7).addCapability(8).addCapability(33).addCapability(9).addCapability(29).addCapability(10).addCapability(13).addCapability(28).addCapability(12).addCapability(23).addCapability(34).addCapability(35).addEnterpriseId(1).addEnterpriseId(2).addEnterpriseId(3).addEnterpriseId(4).addEnterpriseId(5).setNetworkSpecifier(new TelephonyNetworkSpecifier.Builder().setSubscriptionId(i).build()).build();
    }

    /* loaded from: classes.dex */
    private class InternalHandler extends Handler {
        InternalHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                TelephonyNetworkFactory.this.onActivePhoneSwitch();
            } else if (i == 2) {
                TelephonyNetworkFactory.this.onSubIdChange();
            } else if (i == 3) {
                TelephonyNetworkFactory.this.onNeedNetworkFor(message);
            } else if (i != 4) {
            } else {
                TelephonyNetworkFactory.this.onReleaseNetworkFor(message);
            }
        }
    }

    private int getTransportTypeFromNetworkRequest(TelephonyNetworkRequest telephonyNetworkRequest) {
        int apnTypeNetworkCapability = telephonyNetworkRequest.getApnTypeNetworkCapability();
        if (apnTypeNetworkCapability >= 0) {
            return this.mAccessNetworksManager.getPreferredTransportByNetworkCapability(apnTypeNetworkCapability);
        }
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onActivePhoneSwitch() {
        logl("onActivePhoneSwitch");
        for (Map.Entry<TelephonyNetworkRequest, Integer> entry : this.mNetworkRequests.entrySet()) {
            TelephonyNetworkRequest key = entry.getKey();
            boolean z = entry.getValue().intValue() != -1;
            boolean shouldApplyNetworkRequest = this.mPhoneSwitcher.shouldApplyNetworkRequest(key, this.mPhone.getPhoneId());
            int action = getAction(z, shouldApplyNetworkRequest);
            if (action != 0) {
                StringBuilder sb = new StringBuilder();
                sb.append("onActivePhoneSwitch: ");
                sb.append(action == 1 ? "Requesting" : "Releasing");
                sb.append(" network request ");
                sb.append(key);
                logl(sb.toString());
                int transportTypeFromNetworkRequest = getTransportTypeFromNetworkRequest(key);
                if (action == 1) {
                    NetworkRequestsStats.addNetworkRequest(key.getNativeNetworkRequest(), this.mSubscriptionId);
                    this.mPhone.getDataNetworkController().addNetworkRequest(key);
                } else if (action == 2) {
                    this.mPhone.getDataNetworkController().removeNetworkRequest(key);
                }
                this.mNetworkRequests.put(key, Integer.valueOf(shouldApplyNetworkRequest ? transportTypeFromNetworkRequest : -1));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSubIdChange() {
        int subscriptionId = SubscriptionManager.getSubscriptionId(this.mPhone.getPhoneId());
        if (this.mSubscriptionId != subscriptionId) {
            logl("onSubIdChange " + this.mSubscriptionId + "->" + subscriptionId);
            this.mSubscriptionId = subscriptionId;
            setCapabilityFilter(makeNetworkFilter(subscriptionId));
        }
    }

    @Override // com.android.internal.telephony.NetworkFactory
    public void needNetworkFor(NetworkRequest networkRequest) {
        Message obtainMessage = this.mInternalHandler.obtainMessage(3);
        obtainMessage.obj = networkRequest;
        obtainMessage.sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNeedNetworkFor(Message message) {
        TelephonyNetworkRequest telephonyNetworkRequest = new TelephonyNetworkRequest((NetworkRequest) message.obj, this.mPhone);
        boolean shouldApplyNetworkRequest = this.mPhoneSwitcher.shouldApplyNetworkRequest(telephonyNetworkRequest, this.mPhone.getPhoneId());
        this.mNetworkRequests.put(telephonyNetworkRequest, Integer.valueOf(shouldApplyNetworkRequest ? getTransportTypeFromNetworkRequest(telephonyNetworkRequest) : -1));
        logl("onNeedNetworkFor " + telephonyNetworkRequest + " shouldApply " + shouldApplyNetworkRequest);
        if (shouldApplyNetworkRequest) {
            NetworkRequestsStats.addNetworkRequest(telephonyNetworkRequest.getNativeNetworkRequest(), this.mSubscriptionId);
            this.mPhone.getDataNetworkController().addNetworkRequest(telephonyNetworkRequest);
        }
    }

    @Override // com.android.internal.telephony.NetworkFactory
    public void releaseNetworkFor(NetworkRequest networkRequest) {
        Message obtainMessage = this.mInternalHandler.obtainMessage(4);
        obtainMessage.obj = networkRequest;
        obtainMessage.sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onReleaseNetworkFor(Message message) {
        TelephonyNetworkRequest telephonyNetworkRequest = new TelephonyNetworkRequest((NetworkRequest) message.obj, this.mPhone);
        boolean z = this.mNetworkRequests.get(telephonyNetworkRequest).intValue() != -1;
        this.mNetworkRequests.remove(telephonyNetworkRequest);
        logl("onReleaseNetworkFor " + telephonyNetworkRequest + " applied " + z);
        if (z) {
            this.mPhone.getDataNetworkController().removeNetworkRequest(telephonyNetworkRequest);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.NetworkFactory
    public void log(String str) {
        Rlog.d(this.LOG_TAG, str);
    }

    protected void logl(String str) {
        log(str);
        this.mLocalLog.log(str);
    }

    @Override // com.android.internal.telephony.NetworkFactory
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        indentingPrintWriter.println("TelephonyNetworkFactory-" + this.mPhone.getPhoneId());
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.println("Network Requests:");
        indentingPrintWriter.increaseIndent();
        for (Map.Entry<TelephonyNetworkRequest, Integer> entry : this.mNetworkRequests.entrySet()) {
            int intValue = entry.getValue().intValue();
            StringBuilder sb = new StringBuilder();
            sb.append(entry.getKey());
            sb.append(intValue != -1 ? " applied on " + intValue : " not applied");
            indentingPrintWriter.println(sb.toString());
        }
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.print("Local logs:");
        indentingPrintWriter.increaseIndent();
        this.mLocalLog.dump(fileDescriptor, indentingPrintWriter, strArr);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.decreaseIndent();
    }
}
