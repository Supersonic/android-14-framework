package com.android.internal.telephony.data;

import android.net.KeepalivePacketData;
import android.net.NetworkAgent;
import android.net.NetworkAgentConfig;
import android.net.NetworkCapabilities;
import android.net.NetworkProvider;
import android.net.NetworkScore;
import android.net.QosFilter;
import android.net.Uri;
import android.os.Looper;
import android.util.ArraySet;
import com.android.internal.telephony.AndroidUtilIndentingPrintWriter;
import com.android.internal.telephony.GbaManager;
import com.android.internal.telephony.LocalLog;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.data.TelephonyNetworkAgent;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class TelephonyNetworkAgent extends NetworkAgent {
    private boolean mAbandoned;
    private final DataNetwork mDataNetwork;
    private final int mId;
    private final LocalLog mLocalLog;
    private final String mLogTag;
    private final NetworkAgentConfig mNetworkAgentConfig;
    private final Set<TelephonyNetworkAgentCallback> mTelephonyNetworkAgentCallbacks;

    /* loaded from: classes.dex */
    public static abstract class TelephonyNetworkAgentCallback extends DataCallback {
        public void onQosCallbackRegistered(int i, QosFilter qosFilter) {
        }

        public void onQosCallbackUnregistered(int i) {
        }

        public void onStartSocketKeepalive(int i, Duration duration, KeepalivePacketData keepalivePacketData) {
        }

        public void onStopSocketKeepalive(int i) {
        }

        public void onValidationStatus(int i, Uri uri) {
        }

        public TelephonyNetworkAgentCallback(Executor executor) {
            super(executor);
        }
    }

    public TelephonyNetworkAgent(Phone phone, Looper looper, DataNetwork dataNetwork, NetworkScore networkScore, NetworkAgentConfig networkAgentConfig, NetworkProvider networkProvider, TelephonyNetworkAgentCallback telephonyNetworkAgentCallback) {
        super(phone.getContext(), looper, "TelephonyNetworkAgent", new NetworkCapabilities.Builder(dataNetwork.getNetworkCapabilities()).addCapability(21).build(), dataNetwork.getLinkProperties(), networkScore, networkAgentConfig, networkProvider);
        this.mLocalLog = new LocalLog(128);
        this.mAbandoned = false;
        ArraySet arraySet = new ArraySet();
        this.mTelephonyNetworkAgentCallbacks = arraySet;
        register();
        this.mDataNetwork = dataNetwork;
        this.mNetworkAgentConfig = networkAgentConfig;
        arraySet.add(telephonyNetworkAgentCallback);
        int netId = getNetwork().getNetId();
        this.mId = netId;
        this.mLogTag = "TNA-" + netId;
        log("TelephonyNetworkAgent created, nc=" + new NetworkCapabilities.Builder(dataNetwork.getNetworkCapabilities()).addCapability(21).build() + ", score=" + networkScore);
    }

    public void onNetworkUnwanted() {
        if (this.mAbandoned) {
            log("The agent is already abandoned. Ignored onNetworkUnwanted.");
        } else {
            this.mDataNetwork.lambda$tearDownWhenConditionMet$9(1);
        }
    }

    public int getId() {
        return this.mId;
    }

    public void onValidationStatus(final int i, final Uri uri) {
        if (this.mAbandoned) {
            log("The agent is already abandoned. Ignored onValidationStatus.");
        } else {
            this.mTelephonyNetworkAgentCallbacks.forEach(new Consumer() { // from class: com.android.internal.telephony.data.TelephonyNetworkAgent$$ExternalSyntheticLambda3
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    TelephonyNetworkAgent.lambda$onValidationStatus$1(i, uri, (TelephonyNetworkAgent.TelephonyNetworkAgentCallback) obj);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onValidationStatus$1(final int i, final Uri uri, final TelephonyNetworkAgentCallback telephonyNetworkAgentCallback) {
        telephonyNetworkAgentCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.TelephonyNetworkAgent$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                TelephonyNetworkAgent.TelephonyNetworkAgentCallback.this.onValidationStatus(i, uri);
            }
        });
    }

    public void onBandwidthUpdateRequested() {
        loge("onBandwidthUpdateRequested: RIL.pullLceData is not supported anymore.");
    }

    public void onStartSocketKeepalive(final int i, final Duration duration, final KeepalivePacketData keepalivePacketData) {
        if (this.mAbandoned) {
            log("The agent is already abandoned. Ignored onStartSocketKeepalive.");
        } else {
            this.mTelephonyNetworkAgentCallbacks.forEach(new Consumer() { // from class: com.android.internal.telephony.data.TelephonyNetworkAgent$$ExternalSyntheticLambda2
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    TelephonyNetworkAgent.lambda$onStartSocketKeepalive$3(i, duration, keepalivePacketData, (TelephonyNetworkAgent.TelephonyNetworkAgentCallback) obj);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onStartSocketKeepalive$3(final int i, final Duration duration, final KeepalivePacketData keepalivePacketData, final TelephonyNetworkAgentCallback telephonyNetworkAgentCallback) {
        telephonyNetworkAgentCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.TelephonyNetworkAgent$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                TelephonyNetworkAgent.TelephonyNetworkAgentCallback.this.onStartSocketKeepalive(i, duration, keepalivePacketData);
            }
        });
    }

    public void onStopSocketKeepalive(final int i) {
        if (this.mAbandoned) {
            log("The agent is already abandoned. Ignored onStopSocketKeepalive.");
        } else {
            this.mTelephonyNetworkAgentCallbacks.forEach(new Consumer() { // from class: com.android.internal.telephony.data.TelephonyNetworkAgent$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    TelephonyNetworkAgent.lambda$onStopSocketKeepalive$5(i, (TelephonyNetworkAgent.TelephonyNetworkAgentCallback) obj);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onStopSocketKeepalive$5(final int i, final TelephonyNetworkAgentCallback telephonyNetworkAgentCallback) {
        telephonyNetworkAgentCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.TelephonyNetworkAgent$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                TelephonyNetworkAgent.TelephonyNetworkAgentCallback.this.onStopSocketKeepalive(i);
            }
        });
    }

    public void onQosCallbackRegistered(final int i, final QosFilter qosFilter) {
        if (this.mAbandoned) {
            log("The agent is already abandoned. Ignored onQosCallbackRegistered.");
        } else {
            this.mTelephonyNetworkAgentCallbacks.forEach(new Consumer() { // from class: com.android.internal.telephony.data.TelephonyNetworkAgent$$ExternalSyntheticLambda4
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    TelephonyNetworkAgent.lambda$onQosCallbackRegistered$7(i, qosFilter, (TelephonyNetworkAgent.TelephonyNetworkAgentCallback) obj);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onQosCallbackRegistered$7(final int i, final QosFilter qosFilter, final TelephonyNetworkAgentCallback telephonyNetworkAgentCallback) {
        telephonyNetworkAgentCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.TelephonyNetworkAgent$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                TelephonyNetworkAgent.TelephonyNetworkAgentCallback.this.onQosCallbackRegistered(i, qosFilter);
            }
        });
    }

    public void onQosCallbackUnregistered(final int i) {
        if (this.mAbandoned) {
            log("The agent is already abandoned. Ignored onQosCallbackUnregistered.");
        } else {
            this.mTelephonyNetworkAgentCallbacks.forEach(new Consumer() { // from class: com.android.internal.telephony.data.TelephonyNetworkAgent$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    TelephonyNetworkAgent.lambda$onQosCallbackUnregistered$9(i, (TelephonyNetworkAgent.TelephonyNetworkAgentCallback) obj);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onQosCallbackUnregistered$9(final int i, final TelephonyNetworkAgentCallback telephonyNetworkAgentCallback) {
        telephonyNetworkAgentCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.TelephonyNetworkAgent$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                TelephonyNetworkAgent.TelephonyNetworkAgentCallback.this.onQosCallbackUnregistered(i);
            }
        });
    }

    public void abandon() {
        this.mAbandoned = true;
        unregisterAfterReplacement((int) GbaManager.REQUEST_TIMEOUT_MS);
    }

    public void registerCallback(TelephonyNetworkAgentCallback telephonyNetworkAgentCallback) {
        this.mTelephonyNetworkAgentCallbacks.add(telephonyNetworkAgentCallback);
    }

    public void unregisterCallback(TelephonyNetworkAgentCallback telephonyNetworkAgentCallback) {
        this.mTelephonyNetworkAgentCallbacks.remove(telephonyNetworkAgentCallback);
    }

    protected void log(String str) {
        Rlog.d(this.mLogTag, str);
    }

    private void loge(String str) {
        Rlog.e(this.mLogTag, str);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        AndroidUtilIndentingPrintWriter androidUtilIndentingPrintWriter = new AndroidUtilIndentingPrintWriter(printWriter, "  ");
        androidUtilIndentingPrintWriter.println(this.mLogTag + ":");
        androidUtilIndentingPrintWriter.increaseIndent();
        androidUtilIndentingPrintWriter.println("Local logs:");
        androidUtilIndentingPrintWriter.increaseIndent();
        this.mLocalLog.dump(fileDescriptor, androidUtilIndentingPrintWriter, strArr);
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.decreaseIndent();
    }
}
