package com.android.internal.telephony.domainselection;

import android.os.AsyncResult;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.telephony.DomainSelectionService;
import android.telephony.DomainSelector;
import android.telephony.EmergencyRegResult;
import android.telephony.TransportSelectorCallback;
import android.telephony.WwanSelectorCallback;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.infra.AndroidFuture;
import com.android.internal.telephony.LocalLog;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.RILUtils$$ExternalSyntheticLambda6;
import com.android.internal.telephony.domainselection.DomainSelectionConnection;
import com.android.internal.telephony.util.TelephonyUtils;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class DomainSelectionConnection {
    private static final boolean DBG = TelephonyUtils.IS_DEBUGGABLE;
    protected static final int EVENT_EMERGENCY_NETWORK_SCAN_RESULT = 1;
    protected static final int EVENT_QUALIFIED_NETWORKS_CHANGED = 2;
    private final DomainSelectionController mController;
    private DomainSelector mDomainSelector;
    protected DomainSelectionConnectionHandler mHandler;
    private final boolean mIsEmergency;
    private boolean mIsWaitingForScanResult;
    private Looper mLooper;
    protected Phone mPhone;
    private boolean mRegisteredRegistrant;
    private Consumer<EmergencyRegResult> mResultCallback;
    private DomainSelectionService.SelectionAttributes mSelectionAttributes;
    private int mSelectorType;
    private Executor mWwanSelectedExecutor;
    private WwanSelectorCallback mWwanSelectorCallback;
    protected String mTag = "DomainSelectionConnection";
    private final LocalLog mLocalLog = new LocalLog(30);
    private final TransportSelectorCallback mTransportSelectorCallback = new TransportSelectorCallbackWrapper();
    private AndroidFuture<Integer> mOnComplete = new AndroidFuture<>();

    /* loaded from: classes.dex */
    public interface DomainSelectionConnectionCallback {
        void onSelectionTerminated(int i);
    }

    public void onCreated() {
    }

    public void onSelectionTerminated(int i) {
    }

    public void onWlanSelected() {
    }

    public void onWwanSelected() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class TransportSelectorCallbackWrapper implements TransportSelectorCallback {
        private TransportSelectorCallbackWrapper() {
        }

        public void onCreated(DomainSelector domainSelector) {
            DomainSelectionConnection.this.mDomainSelector = domainSelector;
            DomainSelectionConnection.this.onCreated();
        }

        public void onWlanSelected(boolean z) {
            DomainSelectionConnection.this.onWlanSelected(z);
        }

        public WwanSelectorCallback onWwanSelected() {
            if (DomainSelectionConnection.this.mWwanSelectorCallback == null) {
                DomainSelectionConnection domainSelectionConnection = DomainSelectionConnection.this;
                domainSelectionConnection.mWwanSelectorCallback = new WwanSelectorCallbackWrapper();
            }
            DomainSelectionConnection.this.onWwanSelected();
            return DomainSelectionConnection.this.mWwanSelectorCallback;
        }

        public void onWwanSelected(final Consumer<WwanSelectorCallback> consumer) {
            if (DomainSelectionConnection.this.mWwanSelectorCallback == null) {
                DomainSelectionConnection domainSelectionConnection = DomainSelectionConnection.this;
                domainSelectionConnection.mWwanSelectorCallback = new WwanSelectorCallbackWrapper();
            }
            if (DomainSelectionConnection.this.mWwanSelectedExecutor == null) {
                DomainSelectionConnection.this.mWwanSelectedExecutor = Executors.newSingleThreadExecutor();
            }
            DomainSelectionConnection.this.mWwanSelectedExecutor.execute(new Runnable() { // from class: com.android.internal.telephony.domainselection.DomainSelectionConnection$TransportSelectorCallbackWrapper$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    DomainSelectionConnection.TransportSelectorCallbackWrapper.this.lambda$onWwanSelected$0(consumer);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onWwanSelected$0(Consumer consumer) {
            DomainSelectionConnection.this.onWwanSelected();
            consumer.accept(DomainSelectionConnection.this.mWwanSelectorCallback);
        }

        public void onSelectionTerminated(int i) {
            DomainSelectionConnection.this.onSelectionTerminated(i);
            DomainSelectionConnection.this.dispose();
        }
    }

    /* loaded from: classes.dex */
    private final class WwanSelectorCallbackWrapper implements WwanSelectorCallback, CancellationSignal.OnCancelListener {
        private WwanSelectorCallbackWrapper() {
        }

        public void onRequestEmergencyNetworkScan(List<Integer> list, int i, CancellationSignal cancellationSignal, Consumer<EmergencyRegResult> consumer) {
            if (cancellationSignal != null) {
                cancellationSignal.setOnCancelListener(this);
            }
            DomainSelectionConnection.this.mResultCallback = consumer;
            DomainSelectionConnection.this.initHandler();
            DomainSelectionConnection.this.onRequestEmergencyNetworkScan(list.stream().mapToInt(new RILUtils$$ExternalSyntheticLambda6()).toArray(), i);
        }

        public void onDomainSelected(int i, boolean z) {
            DomainSelectionConnection.this.onDomainSelected(i, z);
        }

        @Override // android.os.CancellationSignal.OnCancelListener
        public void onCancel() {
            DomainSelectionConnection.this.onCancel();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public final class DomainSelectionConnectionHandler extends Handler {
        DomainSelectionConnectionHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i != 1) {
                if (i == 2) {
                    DomainSelectionConnection.this.onQualifiedNetworksChanged();
                    return;
                }
                DomainSelectionConnection domainSelectionConnection = DomainSelectionConnection.this;
                domainSelectionConnection.loge("handleMessage unexpected msg=" + message.what);
                return;
            }
            DomainSelectionConnection.this.mIsWaitingForScanResult = false;
            if (DomainSelectionConnection.this.mResultCallback == null) {
                return;
            }
            final EmergencyRegResult emergencyRegResult = (EmergencyRegResult) ((AsyncResult) message.obj).result;
            if (DomainSelectionConnection.DBG) {
                DomainSelectionConnection domainSelectionConnection2 = DomainSelectionConnection.this;
                domainSelectionConnection2.logd("EVENT_EMERGENCY_NETWORK_SCAN_RESULT result=" + emergencyRegResult);
            }
            CompletableFuture.runAsync(new Runnable() { // from class: com.android.internal.telephony.domainselection.DomainSelectionConnection$DomainSelectionConnectionHandler$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    DomainSelectionConnection.DomainSelectionConnectionHandler.this.lambda$handleMessage$0(emergencyRegResult);
                }
            }, DomainSelectionConnection.this.mController.getDomainSelectionServiceExecutor()).join();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$handleMessage$0(EmergencyRegResult emergencyRegResult) {
            DomainSelectionConnection.this.mResultCallback.accept(emergencyRegResult);
        }
    }

    public DomainSelectionConnection(Phone phone, int i, boolean z, DomainSelectionController domainSelectionController) {
        this.mController = domainSelectionController;
        this.mPhone = phone;
        this.mSelectorType = i;
        this.mIsEmergency = z;
    }

    public DomainSelectionService.SelectionAttributes getSelectionAttributes() {
        return this.mSelectionAttributes;
    }

    @VisibleForTesting
    public TransportSelectorCallback getTransportSelectorCallback() {
        return this.mTransportSelectorCallback;
    }

    public CompletableFuture<Integer> getCompletableFuture() {
        return this.mOnComplete;
    }

    public Phone getPhone() {
        return this.mPhone;
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PROTECTED)
    public void selectDomain(DomainSelectionService.SelectionAttributes selectionAttributes) {
        this.mSelectionAttributes = selectionAttributes;
        this.mController.selectDomain(selectionAttributes, getTransportSelectorCallback());
    }

    public void onWlanSelected(boolean z) {
        onWlanSelected();
    }

    public void onRequestEmergencyNetworkScan(int[] iArr, int i) {
        if (!this.mRegisteredRegistrant) {
            this.mPhone.registerForEmergencyNetworkScan(this.mHandler, 1, null);
            this.mRegisteredRegistrant = true;
        }
        this.mIsWaitingForScanResult = true;
        this.mPhone.triggerEmergencyNetworkScan(iArr, i, null);
    }

    public void onDomainSelected(int i) {
        getCompletableFuture().complete(Integer.valueOf(i));
    }

    public void onDomainSelected(int i, boolean z) {
        onDomainSelected(i);
    }

    public void onCancel() {
        onCancel(false);
    }

    private void onCancel(boolean z) {
        if (this.mIsWaitingForScanResult) {
            this.mIsWaitingForScanResult = false;
            this.mPhone.cancelEmergencyNetworkScan(z, null);
        }
    }

    public void cancelSelection() {
        DomainSelector domainSelector = this.mDomainSelector;
        if (domainSelector == null) {
            return;
        }
        domainSelector.cancelSelection();
        dispose();
    }

    public CompletableFuture<Integer> reselectDomain(DomainSelectionService.SelectionAttributes selectionAttributes) {
        this.mSelectionAttributes = selectionAttributes;
        if (this.mDomainSelector == null) {
            return null;
        }
        this.mOnComplete = new AndroidFuture<>();
        this.mDomainSelector.reselectDomain(selectionAttributes);
        return this.mOnComplete;
    }

    public void finishSelection() {
        DomainSelector domainSelector = this.mDomainSelector;
        if (domainSelector == null) {
            return;
        }
        domainSelector.finishSelection();
        dispose();
    }

    public void onServiceDisconnected() {
        dispose();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispose() {
        if (this.mRegisteredRegistrant) {
            this.mPhone.unregisterForEmergencyNetworkScan(this.mHandler);
            this.mRegisteredRegistrant = false;
        }
        onCancel(true);
        this.mController.removeConnection(this);
        Looper looper = this.mLooper;
        if (looper != null) {
            looper.quitSafely();
        }
        this.mLooper = null;
        this.mHandler = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void initHandler() {
        if (this.mLooper == null) {
            HandlerThread handlerThread = new HandlerThread(this.mTag);
            handlerThread.start();
            this.mLooper = handlerThread.getLooper();
        }
        if (this.mHandler == null) {
            this.mHandler = new DomainSelectionConnectionHandler(this.mLooper);
        }
    }

    protected void onQualifiedNetworksChanged() {
        if (this.mIsEmergency && this.mSelectorType == 1) {
            throw new IllegalStateException("DomainSelectionConnection for emergency calls should override onQualifiedNetworksChanged()");
        }
    }

    public void dump(PrintWriter printWriter) {
        this.mLocalLog.dump(printWriter);
    }

    protected void logd(String str) {
        Log.d(this.mTag, str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void logi(String str) {
        Log.i(this.mTag, str);
        this.mLocalLog.log(str);
    }

    protected void loge(String str) {
        Log.e(this.mTag, str);
        this.mLocalLog.log(str);
    }
}
