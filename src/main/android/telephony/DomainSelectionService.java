package android.telephony;

import android.app.PendingIntent$$ExternalSyntheticLambda1;
import android.app.Service;
import android.app.admin.PreferentialNetworkServiceConfig$$ExternalSyntheticLambda2;
import android.content.Intent;
import android.p008os.Build;
import android.p008os.CancellationSignal;
import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.RemoteException;
import android.telephony.DomainSelectionService;
import android.telephony.ims.ImsReasonInfo;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.telephony.IDomainSelectionServiceController;
import com.android.internal.telephony.IDomainSelector;
import com.android.internal.telephony.ITransportSelectorCallback;
import com.android.internal.telephony.ITransportSelectorResultCallback;
import com.android.internal.telephony.IWwanSelectorCallback;
import com.android.internal.telephony.IWwanSelectorResultCallback;
import com.android.internal.telephony.util.TelephonyUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
/* loaded from: classes3.dex */
public class DomainSelectionService extends Service {
    private static final String LOG_TAG = "DomainSelectionService";
    public static final int SCAN_TYPE_FULL_SERVICE = 2;
    public static final int SCAN_TYPE_LIMITED_SERVICE = 1;
    public static final int SCAN_TYPE_NO_PREFERENCE = 0;
    public static final int SELECTOR_TYPE_CALLING = 1;
    public static final int SELECTOR_TYPE_SMS = 2;
    public static final int SELECTOR_TYPE_UT = 3;
    public static final String SERVICE_INTERFACE = "android.telephony.DomainSelectionService";
    private Executor mExecutor;
    private final Object mExecutorLock = new Object();
    private final IBinder mDomainSelectionServiceController = new BinderC28941();

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface EmergencyScanType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface SelectorType {
    }

    /* loaded from: classes3.dex */
    public static final class SelectionAttributes implements Parcelable {
        public static final Parcelable.Creator<SelectionAttributes> CREATOR = new Parcelable.Creator<SelectionAttributes>() { // from class: android.telephony.DomainSelectionService.SelectionAttributes.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public SelectionAttributes createFromParcel(Parcel in) {
                return new SelectionAttributes(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public SelectionAttributes[] newArray(int size) {
                return new SelectionAttributes[size];
            }
        };
        private static final String TAG = "SelectionAttributes";
        private String mCallId;
        private int mCause;
        private EmergencyRegResult mEmergencyRegResult;
        private ImsReasonInfo mImsReasonInfo;
        private boolean mIsEmergency;
        private boolean mIsExitedFromAirplaneMode;
        private boolean mIsVideoCall;
        private String mNumber;
        private int mSelectorType;
        private int mSlotId;
        private int mSubId;

        private SelectionAttributes(int slotId, int subId, String callId, String number, int selectorType, boolean video, boolean emergency, boolean exited, ImsReasonInfo imsReasonInfo, int cause, EmergencyRegResult regResult) {
            this.mSlotId = slotId;
            this.mSubId = subId;
            this.mCallId = callId;
            this.mNumber = number;
            this.mSelectorType = selectorType;
            this.mIsVideoCall = video;
            this.mIsEmergency = emergency;
            this.mIsExitedFromAirplaneMode = exited;
            this.mImsReasonInfo = imsReasonInfo;
            this.mCause = cause;
            this.mEmergencyRegResult = regResult;
        }

        public SelectionAttributes(SelectionAttributes s) {
            this.mSlotId = s.mSlotId;
            this.mSubId = s.mSubId;
            this.mCallId = s.mCallId;
            this.mNumber = s.mNumber;
            this.mSelectorType = s.mSelectorType;
            this.mIsEmergency = s.mIsEmergency;
            this.mIsExitedFromAirplaneMode = s.mIsExitedFromAirplaneMode;
            this.mImsReasonInfo = s.mImsReasonInfo;
            this.mCause = s.mCause;
            this.mEmergencyRegResult = s.mEmergencyRegResult;
        }

        private SelectionAttributes(Parcel in) {
            readFromParcel(in);
        }

        public int getSlotId() {
            return this.mSlotId;
        }

        public int getSubId() {
            return this.mSubId;
        }

        public String getCallId() {
            return this.mCallId;
        }

        public String getNumber() {
            return this.mNumber;
        }

        public int getSelectorType() {
            return this.mSelectorType;
        }

        public boolean isVideoCall() {
            return this.mIsVideoCall;
        }

        public boolean isEmergency() {
            return this.mIsEmergency;
        }

        public boolean isExitedFromAirplaneMode() {
            return this.mIsExitedFromAirplaneMode;
        }

        public ImsReasonInfo getPsDisconnectCause() {
            return this.mImsReasonInfo;
        }

        public int getCsDisconnectCause() {
            return this.mCause;
        }

        public EmergencyRegResult getEmergencyRegResult() {
            return this.mEmergencyRegResult;
        }

        public String toString() {
            return "{ slotId=" + this.mSlotId + ", subId=" + this.mSubId + ", callId=" + this.mCallId + ", number=" + (Build.IS_DEBUGGABLE ? this.mNumber : "***") + ", type=" + this.mSelectorType + ", videoCall=" + this.mIsVideoCall + ", emergency=" + this.mIsEmergency + ", airplaneMode=" + this.mIsExitedFromAirplaneMode + ", reasonInfo=" + this.mImsReasonInfo + ", cause=" + this.mCause + ", regResult=" + this.mEmergencyRegResult + " }";
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            SelectionAttributes that = (SelectionAttributes) o;
            if (this.mSlotId == that.mSlotId && this.mSubId == that.mSubId && TextUtils.equals(this.mCallId, that.mCallId) && TextUtils.equals(this.mNumber, that.mNumber) && this.mSelectorType == that.mSelectorType && this.mIsVideoCall == that.mIsVideoCall && this.mIsEmergency == that.mIsEmergency && this.mIsExitedFromAirplaneMode == that.mIsExitedFromAirplaneMode && equalsHandlesNulls(this.mImsReasonInfo, that.mImsReasonInfo) && this.mCause == that.mCause && equalsHandlesNulls(this.mEmergencyRegResult, that.mEmergencyRegResult)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.mCallId, this.mNumber, this.mImsReasonInfo, Boolean.valueOf(this.mIsVideoCall), Boolean.valueOf(this.mIsEmergency), Boolean.valueOf(this.mIsExitedFromAirplaneMode), this.mEmergencyRegResult, Integer.valueOf(this.mSlotId), Integer.valueOf(this.mSubId), Integer.valueOf(this.mSelectorType), Integer.valueOf(this.mCause));
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(this.mSlotId);
            out.writeInt(this.mSubId);
            out.writeString8(this.mCallId);
            out.writeString8(this.mNumber);
            out.writeInt(this.mSelectorType);
            out.writeBoolean(this.mIsVideoCall);
            out.writeBoolean(this.mIsEmergency);
            out.writeBoolean(this.mIsExitedFromAirplaneMode);
            out.writeParcelable(this.mImsReasonInfo, 0);
            out.writeInt(this.mCause);
            out.writeParcelable(this.mEmergencyRegResult, 0);
        }

        private void readFromParcel(Parcel in) {
            this.mSlotId = in.readInt();
            this.mSubId = in.readInt();
            this.mCallId = in.readString8();
            this.mNumber = in.readString8();
            this.mSelectorType = in.readInt();
            this.mIsVideoCall = in.readBoolean();
            this.mIsEmergency = in.readBoolean();
            this.mIsExitedFromAirplaneMode = in.readBoolean();
            this.mImsReasonInfo = (ImsReasonInfo) in.readParcelable(ImsReasonInfo.class.getClassLoader(), ImsReasonInfo.class);
            this.mCause = in.readInt();
            this.mEmergencyRegResult = (EmergencyRegResult) in.readParcelable(EmergencyRegResult.class.getClassLoader(), EmergencyRegResult.class);
        }

        private static boolean equalsHandlesNulls(Object a, Object b) {
            return a == null ? b == null : a.equals(b);
        }

        /* loaded from: classes3.dex */
        public static final class Builder {
            private String mCallId;
            private int mCause;
            private EmergencyRegResult mEmergencyRegResult;
            private ImsReasonInfo mImsReasonInfo;
            private boolean mIsEmergency;
            private boolean mIsExitedFromAirplaneMode;
            private boolean mIsVideoCall;
            private String mNumber;
            private final int mSelectorType;
            private final int mSlotId;
            private final int mSubId;

            public Builder(int slotId, int subId, int selectorType) {
                this.mSlotId = slotId;
                this.mSubId = subId;
                this.mSelectorType = selectorType;
            }

            public Builder setCallId(String callId) {
                this.mCallId = callId;
                return this;
            }

            public Builder setNumber(String number) {
                this.mNumber = number;
                return this;
            }

            public Builder setVideoCall(boolean video) {
                this.mIsVideoCall = video;
                return this;
            }

            public Builder setEmergency(boolean emergency) {
                this.mIsEmergency = emergency;
                return this;
            }

            public Builder setExitedFromAirplaneMode(boolean exited) {
                this.mIsExitedFromAirplaneMode = exited;
                return this;
            }

            public Builder setPsDisconnectCause(ImsReasonInfo info) {
                this.mImsReasonInfo = info;
                return this;
            }

            public Builder setCsDisconnectCause(int cause) {
                this.mCause = cause;
                return this;
            }

            public Builder setEmergencyRegResult(EmergencyRegResult regResult) {
                this.mEmergencyRegResult = regResult;
                return this;
            }

            public SelectionAttributes build() {
                return new SelectionAttributes(this.mSlotId, this.mSubId, this.mCallId, this.mNumber, this.mSelectorType, this.mIsVideoCall, this.mIsEmergency, this.mIsExitedFromAirplaneMode, this.mImsReasonInfo, this.mCause, this.mEmergencyRegResult);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public final class TransportSelectorCallbackWrapper implements TransportSelectorCallback {
        private static final String TAG = "TransportSelectorCallbackWrapper";
        private final ITransportSelectorCallback mCallback;
        private final Executor mExecutor;
        private ITransportSelectorResultCallbackAdapter mResultCallback;
        private DomainSelectorWrapper mSelectorWrapper;

        TransportSelectorCallbackWrapper(ITransportSelectorCallback cb, Executor executor) {
            this.mCallback = cb;
            this.mExecutor = executor;
        }

        @Override // android.telephony.TransportSelectorCallback
        public void onCreated(DomainSelector selector) {
            try {
                DomainSelectorWrapper domainSelectorWrapper = new DomainSelectorWrapper(selector, this.mExecutor);
                this.mSelectorWrapper = domainSelectorWrapper;
                this.mCallback.onCreated(domainSelectorWrapper.getCallbackBinder());
            } catch (Exception e) {
                com.android.telephony.Rlog.m8e(TAG, "onCreated e=" + e);
            }
        }

        @Override // android.telephony.TransportSelectorCallback
        public void onWlanSelected(boolean useEmergencyPdn) {
            try {
                this.mCallback.onWlanSelected(useEmergencyPdn);
            } catch (Exception e) {
                com.android.telephony.Rlog.m8e(TAG, "onWlanSelected e=" + e);
            }
        }

        @Override // android.telephony.TransportSelectorCallback
        public WwanSelectorCallback onWwanSelected() {
            try {
                IWwanSelectorCallback cb = this.mCallback.onWwanSelected();
                WwanSelectorCallback callback = new WwanSelectorCallbackWrapper(cb, this.mExecutor);
                return callback;
            } catch (Exception e) {
                com.android.telephony.Rlog.m8e(TAG, "onWwanSelected e=" + e);
                return null;
            }
        }

        @Override // android.telephony.TransportSelectorCallback
        public void onWwanSelected(final Consumer<WwanSelectorCallback> consumer) {
            try {
                ITransportSelectorResultCallbackAdapter iTransportSelectorResultCallbackAdapter = new ITransportSelectorResultCallbackAdapter(consumer, this.mExecutor);
                this.mResultCallback = iTransportSelectorResultCallbackAdapter;
                this.mCallback.onWwanSelectedAsync(iTransportSelectorResultCallbackAdapter);
            } catch (Exception e) {
                com.android.telephony.Rlog.m8e(TAG, "onWwanSelected e=" + e);
                DomainSelectionService.this.executeMethodAsyncNoException(this.mExecutor, new Runnable() { // from class: android.telephony.DomainSelectionService$TransportSelectorCallbackWrapper$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        consumer.accept(null);
                    }
                }, TAG, "onWwanSelectedAsync-Exception");
            }
        }

        @Override // android.telephony.TransportSelectorCallback
        public void onSelectionTerminated(int cause) {
            try {
                this.mCallback.onSelectionTerminated(cause);
                this.mSelectorWrapper = null;
            } catch (Exception e) {
                com.android.telephony.Rlog.m8e(TAG, "onSelectionTerminated e=" + e);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public class ITransportSelectorResultCallbackAdapter extends ITransportSelectorResultCallback.Stub {
            private final Consumer<WwanSelectorCallback> mConsumer;
            private final Executor mExecutor;

            ITransportSelectorResultCallbackAdapter(Consumer<WwanSelectorCallback> consumer, Executor executor) {
                this.mConsumer = consumer;
                this.mExecutor = executor;
            }

            @Override // com.android.internal.telephony.ITransportSelectorResultCallback
            public void onCompleted(IWwanSelectorCallback cb) {
                if (this.mConsumer == null) {
                    return;
                }
                final WwanSelectorCallback callback = new WwanSelectorCallbackWrapper(cb, this.mExecutor);
                DomainSelectionService.this.executeMethodAsyncNoException(this.mExecutor, new Runnable() { // from class: android.telephony.DomainSelectionService$TransportSelectorCallbackWrapper$ITransportSelectorResultCallbackAdapter$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        DomainSelectionService.TransportSelectorCallbackWrapper.ITransportSelectorResultCallbackAdapter.this.lambda$onCompleted$0(callback);
                    }
                }, TransportSelectorCallbackWrapper.TAG, "onWwanSelectedAsync-Completed");
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onCompleted$0(WwanSelectorCallback callback) {
                this.mConsumer.accept(callback);
            }
        }
    }

    /* loaded from: classes3.dex */
    private final class DomainSelectorWrapper {
        private static final String TAG = "DomainSelectorWrapper";
        private IDomainSelector mCallbackBinder;

        DomainSelectorWrapper(DomainSelector cb, Executor executor) {
            this.mCallbackBinder = new IDomainSelectorAdapter(cb, executor);
        }

        /* loaded from: classes3.dex */
        private class IDomainSelectorAdapter extends IDomainSelector.Stub {
            private final WeakReference<DomainSelector> mDomainSelectorWeakRef;
            private final Executor mExecutor;

            IDomainSelectorAdapter(DomainSelector domainSelector, Executor executor) {
                this.mDomainSelectorWeakRef = new WeakReference<>(domainSelector);
                this.mExecutor = executor;
            }

            @Override // com.android.internal.telephony.IDomainSelector
            public void cancelSelection() {
                final DomainSelector domainSelector = this.mDomainSelectorWeakRef.get();
                if (domainSelector == null) {
                    return;
                }
                DomainSelectionService.this.executeMethodAsyncNoException(this.mExecutor, new Runnable() { // from class: android.telephony.DomainSelectionService$DomainSelectorWrapper$IDomainSelectorAdapter$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        DomainSelector.this.cancelSelection();
                    }
                }, DomainSelectorWrapper.TAG, "cancelSelection");
            }

            @Override // com.android.internal.telephony.IDomainSelector
            public void reselectDomain(final SelectionAttributes attr) {
                final DomainSelector domainSelector = this.mDomainSelectorWeakRef.get();
                if (domainSelector == null) {
                    return;
                }
                DomainSelectionService.this.executeMethodAsyncNoException(this.mExecutor, new Runnable() { // from class: android.telephony.DomainSelectionService$DomainSelectorWrapper$IDomainSelectorAdapter$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        DomainSelector.this.reselectDomain(attr);
                    }
                }, DomainSelectorWrapper.TAG, "reselectDomain");
            }

            @Override // com.android.internal.telephony.IDomainSelector
            public void finishSelection() {
                final DomainSelector domainSelector = this.mDomainSelectorWeakRef.get();
                if (domainSelector == null) {
                    return;
                }
                DomainSelectionService.this.executeMethodAsyncNoException(this.mExecutor, new Runnable() { // from class: android.telephony.DomainSelectionService$DomainSelectorWrapper$IDomainSelectorAdapter$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        DomainSelector.this.finishSelection();
                    }
                }, DomainSelectorWrapper.TAG, "finishSelection");
            }
        }

        public IDomainSelector getCallbackBinder() {
            return this.mCallbackBinder;
        }
    }

    /* loaded from: classes3.dex */
    private final class WwanSelectorCallbackWrapper implements WwanSelectorCallback, CancellationSignal.OnCancelListener {
        private static final String TAG = "WwanSelectorCallbackWrapper";
        private final IWwanSelectorCallback mCallback;
        private final Executor mExecutor;
        private IWwanSelectorResultCallbackAdapter mResultCallback;

        WwanSelectorCallbackWrapper(IWwanSelectorCallback cb, Executor executor) {
            this.mCallback = cb;
            this.mExecutor = executor;
        }

        @Override // android.p008os.CancellationSignal.OnCancelListener
        public void onCancel() {
            try {
                this.mCallback.onCancel();
            } catch (Exception e) {
                com.android.telephony.Rlog.m8e(TAG, "onCancel e=" + e);
            }
        }

        @Override // android.telephony.WwanSelectorCallback
        public void onRequestEmergencyNetworkScan(List<Integer> preferredNetworks, int scanType, CancellationSignal signal, Consumer<EmergencyRegResult> consumer) {
            if (signal != null) {
                try {
                    signal.setOnCancelListener(this);
                } catch (Exception e) {
                    com.android.telephony.Rlog.m8e(TAG, "onRequestEmergencyNetworkScan e=" + e);
                    return;
                }
            }
            this.mResultCallback = new IWwanSelectorResultCallbackAdapter(consumer, this.mExecutor);
            this.mCallback.onRequestEmergencyNetworkScan(preferredNetworks.stream().mapToInt(new PreferentialNetworkServiceConfig$$ExternalSyntheticLambda2()).toArray(), scanType, this.mResultCallback);
        }

        @Override // android.telephony.WwanSelectorCallback
        public void onDomainSelected(int domain, boolean useEmergencyPdn) {
            try {
                this.mCallback.onDomainSelected(domain, useEmergencyPdn);
            } catch (Exception e) {
                com.android.telephony.Rlog.m8e(TAG, "onDomainSelected e=" + e);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public class IWwanSelectorResultCallbackAdapter extends IWwanSelectorResultCallback.Stub {
            private final Consumer<EmergencyRegResult> mConsumer;
            private final Executor mExecutor;

            IWwanSelectorResultCallbackAdapter(Consumer<EmergencyRegResult> consumer, Executor executor) {
                this.mConsumer = consumer;
                this.mExecutor = executor;
            }

            @Override // com.android.internal.telephony.IWwanSelectorResultCallback
            public void onComplete(final EmergencyRegResult result) {
                if (this.mConsumer == null) {
                    return;
                }
                DomainSelectionService.this.executeMethodAsyncNoException(this.mExecutor, new Runnable() { // from class: android.telephony.DomainSelectionService$WwanSelectorCallbackWrapper$IWwanSelectorResultCallbackAdapter$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        DomainSelectionService.WwanSelectorCallbackWrapper.IWwanSelectorResultCallbackAdapter.this.lambda$onComplete$0(result);
                    }
                }, WwanSelectorCallbackWrapper.TAG, "onScanComplete");
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onComplete$0(EmergencyRegResult result) {
                this.mConsumer.accept(result);
            }
        }
    }

    public void onDomainSelection(SelectionAttributes attr, TransportSelectorCallback callback) {
    }

    public void onServiceStateUpdated(int slotId, int subId, ServiceState serviceState) {
    }

    public void onBarringInfoUpdated(int slotId, int subId, BarringInfo info) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.telephony.DomainSelectionService$1 */
    /* loaded from: classes3.dex */
    public class BinderC28941 extends IDomainSelectionServiceController.Stub {
        BinderC28941() {
        }

        @Override // com.android.internal.telephony.IDomainSelectionServiceController
        public void selectDomain(final SelectionAttributes attr, final ITransportSelectorCallback callback) throws RemoteException {
            DomainSelectionService.executeMethodAsync(DomainSelectionService.this.getCachedExecutor(), new Runnable() { // from class: android.telephony.DomainSelectionService$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    DomainSelectionService.BinderC28941.this.lambda$selectDomain$0(attr, callback);
                }
            }, DomainSelectionService.LOG_TAG, "onDomainSelection");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$selectDomain$0(SelectionAttributes attr, ITransportSelectorCallback callback) {
            DomainSelectionService domainSelectionService = DomainSelectionService.this;
            domainSelectionService.onDomainSelection(attr, new TransportSelectorCallbackWrapper(callback, domainSelectionService.getCachedExecutor()));
        }

        @Override // com.android.internal.telephony.IDomainSelectionServiceController
        public void updateServiceState(final int slotId, final int subId, final ServiceState serviceState) {
            DomainSelectionService domainSelectionService = DomainSelectionService.this;
            domainSelectionService.executeMethodAsyncNoException(domainSelectionService.getCachedExecutor(), new Runnable() { // from class: android.telephony.DomainSelectionService$1$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    DomainSelectionService.BinderC28941.this.lambda$updateServiceState$1(slotId, subId, serviceState);
                }
            }, DomainSelectionService.LOG_TAG, "onServiceStateUpdated");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$updateServiceState$1(int slotId, int subId, ServiceState serviceState) {
            DomainSelectionService.this.onServiceStateUpdated(slotId, subId, serviceState);
        }

        @Override // com.android.internal.telephony.IDomainSelectionServiceController
        public void updateBarringInfo(final int slotId, final int subId, final BarringInfo info) {
            DomainSelectionService domainSelectionService = DomainSelectionService.this;
            domainSelectionService.executeMethodAsyncNoException(domainSelectionService.getCachedExecutor(), new Runnable() { // from class: android.telephony.DomainSelectionService$1$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    DomainSelectionService.BinderC28941.this.lambda$updateBarringInfo$2(slotId, subId, info);
                }
            }, DomainSelectionService.LOG_TAG, "onBarringInfoUpdated");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$updateBarringInfo$2(int slotId, int subId, BarringInfo info) {
            DomainSelectionService.this.onBarringInfoUpdated(slotId, subId, info);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void executeMethodAsync(Executor executor, final Runnable r, String tag, String errorLogName) throws RemoteException {
        try {
            CompletableFuture.runAsync(new Runnable() { // from class: android.telephony.DomainSelectionService$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    TelephonyUtils.runWithCleanCallingIdentity(r);
                }
            }, executor).join();
        } catch (CancellationException | CompletionException e) {
            com.android.telephony.Rlog.m2w(tag, "Binder - " + errorLogName + " exception: " + e.getMessage());
            throw new RemoteException(e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void executeMethodAsyncNoException(Executor executor, final Runnable r, String tag, String errorLogName) {
        try {
            CompletableFuture.runAsync(new Runnable() { // from class: android.telephony.DomainSelectionService$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    TelephonyUtils.runWithCleanCallingIdentity(r);
                }
            }, executor).join();
        } catch (CancellationException | CompletionException e) {
            com.android.telephony.Rlog.m2w(tag, "Binder - " + errorLogName + " exception: " + e.getMessage());
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        if (SERVICE_INTERFACE.equals(intent.getAction())) {
            Log.m108i(LOG_TAG, "DomainSelectionService Bound.");
            return this.mDomainSelectionServiceController;
        }
        return null;
    }

    public Executor getExecutor() {
        return new PendingIntent$$ExternalSyntheticLambda1();
    }

    public Executor getCachedExecutor() {
        Executor e;
        synchronized (this.mExecutorLock) {
            if (this.mExecutor == null) {
                Executor e2 = getExecutor();
                this.mExecutor = e2 != null ? e2 : new PendingIntent$$ExternalSyntheticLambda1();
            }
            e = this.mExecutor;
        }
        return e;
    }

    public static String getDomainName(int domain) {
        return NetworkRegistrationInfo.domainToString(domain);
    }
}
