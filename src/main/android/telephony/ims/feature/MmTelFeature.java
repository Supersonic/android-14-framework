package android.telephony.ims.feature;

import android.annotation.SystemApi;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.Message;
import android.p008os.RemoteException;
import android.p008os.ServiceSpecificException;
import android.telephony.ims.ImsCallProfile;
import android.telephony.ims.ImsCallSessionListener;
import android.telephony.ims.ImsReasonInfo;
import android.telephony.ims.MediaQualityStatus;
import android.telephony.ims.MediaThreshold;
import android.telephony.ims.RtpHeaderExtensionType;
import android.telephony.ims.SrvccCall;
import android.telephony.ims.aidl.IImsCallSessionListener;
import android.telephony.ims.aidl.IImsCapabilityCallback;
import android.telephony.ims.aidl.IImsMmTelFeature;
import android.telephony.ims.aidl.IImsMmTelListener;
import android.telephony.ims.aidl.IImsSmsListener;
import android.telephony.ims.aidl.IImsTrafficSessionCallback;
import android.telephony.ims.aidl.ISrvccStartedCallback;
import android.telephony.ims.feature.ImsFeature;
import android.telephony.ims.feature.MmTelFeature;
import android.telephony.ims.stub.ImsCallSessionImplBase;
import android.telephony.ims.stub.ImsEcbmImplBase;
import android.telephony.ims.stub.ImsMultiEndpointImplBase;
import android.telephony.ims.stub.ImsSmsImplBase;
import android.telephony.ims.stub.ImsUtImplBase;
import android.util.ArraySet;
import android.util.Log;
import com.android.ims.internal.IImsCallSession;
import com.android.ims.internal.IImsEcbm;
import com.android.ims.internal.IImsMultiEndpoint;
import com.android.ims.internal.IImsUt;
import com.android.internal.telephony.util.TelephonyUtils;
import com.android.internal.util.FunctionalUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;
/* loaded from: classes3.dex */
public class MmTelFeature extends ImsFeature {
    @SystemApi
    public static final int AUDIO_HANDLER_ANDROID = 0;
    @SystemApi
    public static final int AUDIO_HANDLER_BASEBAND = 1;
    public static final int EPS_FALLBACK_REASON_INVALID = -1;
    public static final int EPS_FALLBACK_REASON_NO_NETWORK_RESPONSE = 2;
    public static final int EPS_FALLBACK_REASON_NO_NETWORK_TRIGGER = 1;
    @SystemApi
    public static final String EXTRA_IS_UNKNOWN_CALL = "android.telephony.ims.feature.extra.IS_UNKNOWN_CALL";
    @SystemApi
    public static final String EXTRA_IS_USSD = "android.telephony.ims.feature.extra.IS_USSD";
    public static final int IMS_TRAFFIC_DIRECTION_INCOMING = 0;
    public static final int IMS_TRAFFIC_DIRECTION_OUTGOING = 1;
    public static final int IMS_TRAFFIC_TYPE_EMERGENCY = 0;
    public static final int IMS_TRAFFIC_TYPE_EMERGENCY_SMS = 1;
    public static final int IMS_TRAFFIC_TYPE_NONE = -1;
    public static final int IMS_TRAFFIC_TYPE_REGISTRATION = 5;
    public static final int IMS_TRAFFIC_TYPE_SMS = 4;
    public static final int IMS_TRAFFIC_TYPE_UT_XCAP = 6;
    public static final int IMS_TRAFFIC_TYPE_VIDEO = 3;
    public static final int IMS_TRAFFIC_TYPE_VOICE = 2;
    private static final String LOG_TAG = "MmTelFeature";
    @SystemApi
    public static final int PROCESS_CALL_CSFB = 1;
    @SystemApi
    public static final int PROCESS_CALL_IMS = 0;
    private Executor mExecutor;
    private IImsMmTelListener mListener;
    private ImsSmsImplBase mSmsImpl;
    private HashMap<ImsTrafficSessionCallback, ImsTrafficSessionCallbackWrapper> mTrafficCallbacks = new HashMap<>();
    private final IImsMmTelFeature mImsMMTelBinder = new BinderC32961();

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface EpsFallbackReason {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ImsAudioHandler {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ImsTrafficDirection {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ImsTrafficType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ProcessCallResult {
    }

    @SystemApi
    public MmTelFeature() {
    }

    @SystemApi
    public MmTelFeature(Executor executor) {
        this.mExecutor = executor;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.telephony.ims.feature.MmTelFeature$1 */
    /* loaded from: classes3.dex */
    public class BinderC32961 extends IImsMmTelFeature.Stub {
        BinderC32961() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$setListener$0(IImsMmTelListener l) {
            MmTelFeature.this.setListener(l);
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void setListener(final IImsMmTelListener l) {
            executeMethodAsyncNoException(new Runnable() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda25
                @Override // java.lang.Runnable
                public final void run() {
                    MmTelFeature.BinderC32961.this.lambda$setListener$0(l);
                }
            }, "setListener");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ Integer lambda$getFeatureState$1() {
            return Integer.valueOf(MmTelFeature.this.getFeatureState());
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public int getFeatureState() throws RemoteException {
            return ((Integer) executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda22
                @Override // java.util.function.Supplier
                public final Object get() {
                    Integer lambda$getFeatureState$1;
                    lambda$getFeatureState$1 = MmTelFeature.BinderC32961.this.lambda$getFeatureState$1();
                    return lambda$getFeatureState$1;
                }
            }, "getFeatureState")).intValue();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ ImsCallProfile lambda$createCallProfile$2(int callSessionType, int callType) {
            return MmTelFeature.this.createCallProfile(callSessionType, callType);
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public ImsCallProfile createCallProfile(final int callSessionType, final int callType) throws RemoteException {
            return (ImsCallProfile) executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda33
                @Override // java.util.function.Supplier
                public final Object get() {
                    ImsCallProfile lambda$createCallProfile$2;
                    lambda$createCallProfile$2 = MmTelFeature.BinderC32961.this.lambda$createCallProfile$2(callSessionType, callType);
                    return lambda$createCallProfile$2;
                }
            }, "createCallProfile");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$changeOfferedRtpHeaderExtensionTypes$3(List types) {
            MmTelFeature.this.changeOfferedRtpHeaderExtensionTypes(new ArraySet(types));
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void changeOfferedRtpHeaderExtensionTypes(final List<RtpHeaderExtensionType> types) throws RemoteException {
            executeMethodAsync(new Runnable() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda34
                @Override // java.lang.Runnable
                public final void run() {
                    MmTelFeature.BinderC32961.this.lambda$changeOfferedRtpHeaderExtensionTypes$3(types);
                }
            }, "changeOfferedRtpHeaderExtensionTypes");
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public IImsCallSession createCallSession(final ImsCallProfile profile) throws RemoteException {
            final AtomicReference<RemoteException> exceptionRef = new AtomicReference<>();
            IImsCallSession result = (IImsCallSession) executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda7
                @Override // java.util.function.Supplier
                public final Object get() {
                    IImsCallSession lambda$createCallSession$4;
                    lambda$createCallSession$4 = MmTelFeature.BinderC32961.this.lambda$createCallSession$4(profile, exceptionRef);
                    return lambda$createCallSession$4;
                }
            }, "createCallSession");
            if (exceptionRef.get() != null) {
                throw exceptionRef.get();
            }
            return result;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ IImsCallSession lambda$createCallSession$4(ImsCallProfile profile, AtomicReference exceptionRef) {
            try {
                return MmTelFeature.this.createCallSessionInterface(profile);
            } catch (RemoteException e) {
                exceptionRef.set(e);
                return null;
            }
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public int shouldProcessCall(final String[] numbers) {
            Integer result = (Integer) executeMethodAsyncForResultNoException(new Supplier() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda9
                @Override // java.util.function.Supplier
                public final Object get() {
                    Integer lambda$shouldProcessCall$5;
                    lambda$shouldProcessCall$5 = MmTelFeature.BinderC32961.this.lambda$shouldProcessCall$5(numbers);
                    return lambda$shouldProcessCall$5;
                }
            }, "shouldProcessCall");
            if (result != null) {
                return result.intValue();
            }
            return 1;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ Integer lambda$shouldProcessCall$5(String[] numbers) {
            return Integer.valueOf(MmTelFeature.this.shouldProcessCall(numbers));
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public IImsUt getUtInterface() throws RemoteException {
            final AtomicReference<RemoteException> exceptionRef = new AtomicReference<>();
            IImsUt result = (IImsUt) executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda29
                @Override // java.util.function.Supplier
                public final Object get() {
                    IImsUt lambda$getUtInterface$6;
                    lambda$getUtInterface$6 = MmTelFeature.BinderC32961.this.lambda$getUtInterface$6(exceptionRef);
                    return lambda$getUtInterface$6;
                }
            }, "getUtInterface");
            if (exceptionRef.get() != null) {
                throw exceptionRef.get();
            }
            return result;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ IImsUt lambda$getUtInterface$6(AtomicReference exceptionRef) {
            try {
                return MmTelFeature.this.getUtInterface();
            } catch (RemoteException e) {
                exceptionRef.set(e);
                return null;
            }
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public IImsEcbm getEcbmInterface() throws RemoteException {
            final AtomicReference<RemoteException> exceptionRef = new AtomicReference<>();
            IImsEcbm result = (IImsEcbm) executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda23
                @Override // java.util.function.Supplier
                public final Object get() {
                    IImsEcbm lambda$getEcbmInterface$7;
                    lambda$getEcbmInterface$7 = MmTelFeature.BinderC32961.this.lambda$getEcbmInterface$7(exceptionRef);
                    return lambda$getEcbmInterface$7;
                }
            }, "getEcbmInterface");
            if (exceptionRef.get() != null) {
                throw exceptionRef.get();
            }
            return result;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ IImsEcbm lambda$getEcbmInterface$7(AtomicReference exceptionRef) {
            try {
                return MmTelFeature.this.getEcbmInterface();
            } catch (RemoteException e) {
                exceptionRef.set(e);
                return null;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$setUiTtyMode$8(int uiTtyMode, Message onCompleteMessage) {
            MmTelFeature.this.setUiTtyMode(uiTtyMode, onCompleteMessage);
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void setUiTtyMode(final int uiTtyMode, final Message onCompleteMessage) throws RemoteException {
            executeMethodAsync(new Runnable() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    MmTelFeature.BinderC32961.this.lambda$setUiTtyMode$8(uiTtyMode, onCompleteMessage);
                }
            }, "setUiTtyMode");
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public IImsMultiEndpoint getMultiEndpointInterface() throws RemoteException {
            final AtomicReference<RemoteException> exceptionRef = new AtomicReference<>();
            IImsMultiEndpoint result = (IImsMultiEndpoint) executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda31
                @Override // java.util.function.Supplier
                public final Object get() {
                    IImsMultiEndpoint lambda$getMultiEndpointInterface$9;
                    lambda$getMultiEndpointInterface$9 = MmTelFeature.BinderC32961.this.lambda$getMultiEndpointInterface$9(exceptionRef);
                    return lambda$getMultiEndpointInterface$9;
                }
            }, "getMultiEndpointInterface");
            if (exceptionRef.get() != null) {
                throw exceptionRef.get();
            }
            return result;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ IImsMultiEndpoint lambda$getMultiEndpointInterface$9(AtomicReference exceptionRef) {
            try {
                return MmTelFeature.this.getMultiEndpointInterface();
            } catch (RemoteException e) {
                exceptionRef.set(e);
                return null;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ Integer lambda$queryCapabilityStatus$10() {
            return Integer.valueOf(MmTelFeature.this.queryCapabilityStatus().mCapabilities);
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public int queryCapabilityStatus() {
            Integer result = (Integer) executeMethodAsyncForResultNoException(new Supplier() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda36
                @Override // java.util.function.Supplier
                public final Object get() {
                    Integer lambda$queryCapabilityStatus$10;
                    lambda$queryCapabilityStatus$10 = MmTelFeature.BinderC32961.this.lambda$queryCapabilityStatus$10();
                    return lambda$queryCapabilityStatus$10;
                }
            }, "queryCapabilityStatus");
            if (result != null) {
                return result.intValue();
            }
            return 0;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$addCapabilityCallback$11(IImsCapabilityCallback c) {
            MmTelFeature.this.addCapabilityCallback(c);
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void addCapabilityCallback(final IImsCapabilityCallback c) {
            executeMethodAsyncNoException(new Runnable() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda28
                @Override // java.lang.Runnable
                public final void run() {
                    MmTelFeature.BinderC32961.this.lambda$addCapabilityCallback$11(c);
                }
            }, "addCapabilityCallback");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$removeCapabilityCallback$12(IImsCapabilityCallback c) {
            MmTelFeature.this.removeCapabilityCallback(c);
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void removeCapabilityCallback(final IImsCapabilityCallback c) {
            executeMethodAsyncNoException(new Runnable() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda11
                @Override // java.lang.Runnable
                public final void run() {
                    MmTelFeature.BinderC32961.this.lambda$removeCapabilityCallback$12(c);
                }
            }, "removeCapabilityCallback");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$changeCapabilitiesConfiguration$13(CapabilityChangeRequest request, IImsCapabilityCallback c) {
            MmTelFeature.this.requestChangeEnabledCapabilities(request, c);
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void changeCapabilitiesConfiguration(final CapabilityChangeRequest request, final IImsCapabilityCallback c) {
            executeMethodAsyncNoException(new Runnable() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda10
                @Override // java.lang.Runnable
                public final void run() {
                    MmTelFeature.BinderC32961.this.lambda$changeCapabilitiesConfiguration$13(request, c);
                }
            }, "changeCapabilitiesConfiguration");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$queryCapabilityConfiguration$14(int capability, int radioTech, IImsCapabilityCallback c) {
            MmTelFeature.this.queryCapabilityConfigurationInternal(capability, radioTech, c);
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void queryCapabilityConfiguration(final int capability, final int radioTech, final IImsCapabilityCallback c) {
            executeMethodAsyncNoException(new Runnable() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda21
                @Override // java.lang.Runnable
                public final void run() {
                    MmTelFeature.BinderC32961.this.lambda$queryCapabilityConfiguration$14(capability, radioTech, c);
                }
            }, "queryCapabilityConfiguration");
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void setMediaQualityThreshold(final int sessionType, final MediaThreshold mediaThreshold) {
            if (mediaThreshold != null) {
                executeMethodAsyncNoException(new Runnable() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda18
                    @Override // java.lang.Runnable
                    public final void run() {
                        MmTelFeature.BinderC32961.this.lambda$setMediaQualityThreshold$15(sessionType, mediaThreshold);
                    }
                }, "setMediaQualityThreshold");
            } else {
                executeMethodAsyncNoException(new Runnable() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda19
                    @Override // java.lang.Runnable
                    public final void run() {
                        MmTelFeature.BinderC32961.this.lambda$setMediaQualityThreshold$16(sessionType);
                    }
                }, "clearMediaQualityThreshold");
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$setMediaQualityThreshold$15(int sessionType, MediaThreshold mediaThreshold) {
            MmTelFeature.this.setMediaThreshold(sessionType, mediaThreshold);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$setMediaQualityThreshold$16(int sessionType) {
            MmTelFeature.this.clearMediaThreshold(sessionType);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ MediaQualityStatus lambda$queryMediaQualityStatus$17(int sessionType) {
            return MmTelFeature.this.queryMediaQualityStatus(sessionType);
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public MediaQualityStatus queryMediaQualityStatus(final int sessionType) throws RemoteException {
            return (MediaQualityStatus) executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda8
                @Override // java.util.function.Supplier
                public final Object get() {
                    MediaQualityStatus lambda$queryMediaQualityStatus$17;
                    lambda$queryMediaQualityStatus$17 = MmTelFeature.BinderC32961.this.lambda$queryMediaQualityStatus$17(sessionType);
                    return lambda$queryMediaQualityStatus$17;
                }
            }, "queryMediaQualityStatus");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$setSmsListener$18(IImsSmsListener l) {
            MmTelFeature.this.setSmsListener(l);
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void setSmsListener(final IImsSmsListener l) {
            executeMethodAsyncNoException(new Runnable() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda26
                @Override // java.lang.Runnable
                public final void run() {
                    MmTelFeature.BinderC32961.this.lambda$setSmsListener$18(l);
                }
            }, "setSmsListener", MmTelFeature.this.getImsSmsImpl().getExecutor());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$sendSms$19(int token, int messageRef, String format, String smsc, boolean retry, byte[] pdu) {
            MmTelFeature.this.sendSms(token, messageRef, format, smsc, retry, pdu);
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void sendSms(final int token, final int messageRef, final String format, final String smsc, final boolean retry, final byte[] pdu) {
            executeMethodAsyncNoException(new Runnable() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    MmTelFeature.BinderC32961.this.lambda$sendSms$19(token, messageRef, format, smsc, retry, pdu);
                }
            }, "sendSms", MmTelFeature.this.getImsSmsImpl().getExecutor());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onMemoryAvailable$20(int token) {
            MmTelFeature.this.onMemoryAvailable(token);
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void onMemoryAvailable(final int token) {
            executeMethodAsyncNoException(new Runnable() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    MmTelFeature.BinderC32961.this.lambda$onMemoryAvailable$20(token);
                }
            }, "onMemoryAvailable", MmTelFeature.this.getImsSmsImpl().getExecutor());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$acknowledgeSms$21(int token, int messageRef, int result) {
            MmTelFeature.this.acknowledgeSms(token, messageRef, result);
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void acknowledgeSms(final int token, final int messageRef, final int result) {
            executeMethodAsyncNoException(new Runnable() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda32
                @Override // java.lang.Runnable
                public final void run() {
                    MmTelFeature.BinderC32961.this.lambda$acknowledgeSms$21(token, messageRef, result);
                }
            }, "acknowledgeSms", MmTelFeature.this.getImsSmsImpl().getExecutor());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$acknowledgeSmsWithPdu$22(int token, int messageRef, int result, byte[] pdu) {
            MmTelFeature.this.acknowledgeSms(token, messageRef, result, pdu);
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void acknowledgeSmsWithPdu(final int token, final int messageRef, final int result, final byte[] pdu) {
            executeMethodAsyncNoException(new Runnable() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda27
                @Override // java.lang.Runnable
                public final void run() {
                    MmTelFeature.BinderC32961.this.lambda$acknowledgeSmsWithPdu$22(token, messageRef, result, pdu);
                }
            }, "acknowledgeSms", MmTelFeature.this.getImsSmsImpl().getExecutor());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$acknowledgeSmsReport$23(int token, int messageRef, int result) {
            MmTelFeature.this.acknowledgeSmsReport(token, messageRef, result);
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void acknowledgeSmsReport(final int token, final int messageRef, final int result) {
            executeMethodAsyncNoException(new Runnable() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    MmTelFeature.BinderC32961.this.lambda$acknowledgeSmsReport$23(token, messageRef, result);
                }
            }, "acknowledgeSmsReport", MmTelFeature.this.getImsSmsImpl().getExecutor());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ String lambda$getSmsFormat$24() {
            return MmTelFeature.this.getSmsFormat();
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public String getSmsFormat() {
            return (String) executeMethodAsyncForResultNoException(new Supplier() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda14
                @Override // java.util.function.Supplier
                public final Object get() {
                    String lambda$getSmsFormat$24;
                    lambda$getSmsFormat$24 = MmTelFeature.BinderC32961.this.lambda$getSmsFormat$24();
                    return lambda$getSmsFormat$24;
                }
            }, "getSmsFormat", MmTelFeature.this.getImsSmsImpl().getExecutor());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSmsReady$25() {
            MmTelFeature.this.onSmsReady();
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void onSmsReady() {
            executeMethodAsyncNoException(new Runnable() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    MmTelFeature.BinderC32961.this.lambda$onSmsReady$25();
                }
            }, "onSmsReady", MmTelFeature.this.getImsSmsImpl().getExecutor());
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void notifySrvccStarted(final ISrvccStartedCallback cb) {
            executeMethodAsyncNoException(new Runnable() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda15
                @Override // java.lang.Runnable
                public final void run() {
                    MmTelFeature.BinderC32961.this.lambda$notifySrvccStarted$27(cb);
                }
            }, "notifySrvccStarted");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$notifySrvccStarted$27(final ISrvccStartedCallback cb) {
            MmTelFeature.this.notifySrvccStarted(new Consumer() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda30
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    MmTelFeature.BinderC32961.lambda$notifySrvccStarted$26(ISrvccStartedCallback.this, (List) obj);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$notifySrvccStarted$26(ISrvccStartedCallback cb, List profiles) {
            try {
                cb.onSrvccCallNotified(profiles);
            } catch (Exception e) {
                Log.m110e(MmTelFeature.LOG_TAG, "onSrvccCallNotified e=" + e);
            }
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void notifySrvccCompleted() {
            executeMethodAsyncNoException(new Runnable() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda13
                @Override // java.lang.Runnable
                public final void run() {
                    MmTelFeature.BinderC32961.this.lambda$notifySrvccCompleted$28();
                }
            }, "notifySrvccCompleted");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$notifySrvccCompleted$28() {
            MmTelFeature.this.notifySrvccCompleted();
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void notifySrvccFailed() {
            executeMethodAsyncNoException(new Runnable() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda20
                @Override // java.lang.Runnable
                public final void run() {
                    MmTelFeature.BinderC32961.this.lambda$notifySrvccFailed$29();
                }
            }, "notifySrvccFailed");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$notifySrvccFailed$29() {
            MmTelFeature.this.notifySrvccFailed();
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void notifySrvccCanceled() {
            executeMethodAsyncNoException(new Runnable() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda16
                @Override // java.lang.Runnable
                public final void run() {
                    MmTelFeature.BinderC32961.this.lambda$notifySrvccCanceled$30();
                }
            }, "notifySrvccCanceled");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$notifySrvccCanceled$30() {
            MmTelFeature.this.notifySrvccCanceled();
        }

        @Override // android.telephony.ims.aidl.IImsMmTelFeature
        public void setTerminalBasedCallWaitingStatus(boolean enabled) throws RemoteException {
            synchronized (MmTelFeature.this.mLock) {
                try {
                    try {
                        MmTelFeature.this.setTerminalBasedCallWaitingStatus(enabled);
                    } catch (ServiceSpecificException se) {
                        throw new ServiceSpecificException(se.errorCode, se.getMessage());
                    } catch (Exception e) {
                        throw new RemoteException(e.getMessage());
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        private void executeMethodAsync(final Runnable r, String errorLogName) throws RemoteException {
            try {
                CompletableFuture.runAsync(new Runnable() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        TelephonyUtils.runWithCleanCallingIdentity(r);
                    }
                }, MmTelFeature.this.mExecutor).join();
            } catch (CancellationException | CompletionException e) {
                Log.m104w(MmTelFeature.LOG_TAG, "MmTelFeature Binder - " + errorLogName + " exception: " + e.getMessage());
                throw new RemoteException(e.getMessage());
            }
        }

        private void executeMethodAsyncNoException(final Runnable r, String errorLogName) {
            try {
                CompletableFuture.runAsync(new Runnable() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda24
                    @Override // java.lang.Runnable
                    public final void run() {
                        TelephonyUtils.runWithCleanCallingIdentity(r);
                    }
                }, MmTelFeature.this.mExecutor).join();
            } catch (CancellationException | CompletionException e) {
                Log.m104w(MmTelFeature.LOG_TAG, "MmTelFeature Binder - " + errorLogName + " exception: " + e.getMessage());
            }
        }

        private void executeMethodAsyncNoException(final Runnable r, String errorLogName, Executor executor) {
            try {
                CompletableFuture.runAsync(new Runnable() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda35
                    @Override // java.lang.Runnable
                    public final void run() {
                        TelephonyUtils.runWithCleanCallingIdentity(r);
                    }
                }, executor).join();
            } catch (CancellationException | CompletionException e) {
                Log.m104w(MmTelFeature.LOG_TAG, "MmTelFeature Binder - " + errorLogName + " exception: " + e.getMessage());
            }
        }

        private <T> T executeMethodAsyncForResult(final Supplier<T> r, String errorLogName) throws RemoteException {
            CompletableFuture<T> future = CompletableFuture.supplyAsync(new Supplier() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda3
                @Override // java.util.function.Supplier
                public final Object get() {
                    Object runWithCleanCallingIdentity;
                    runWithCleanCallingIdentity = TelephonyUtils.runWithCleanCallingIdentity(r);
                    return runWithCleanCallingIdentity;
                }
            }, MmTelFeature.this.mExecutor);
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                Log.m104w(MmTelFeature.LOG_TAG, "MmTelFeature Binder - " + errorLogName + " exception: " + e.getMessage());
                throw new RemoteException(e.getMessage());
            }
        }

        private <T> T executeMethodAsyncForResultNoException(final Supplier<T> r, String errorLogName) {
            CompletableFuture<T> future = CompletableFuture.supplyAsync(new Supplier() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda0
                @Override // java.util.function.Supplier
                public final Object get() {
                    Object runWithCleanCallingIdentity;
                    runWithCleanCallingIdentity = TelephonyUtils.runWithCleanCallingIdentity(r);
                    return runWithCleanCallingIdentity;
                }
            }, MmTelFeature.this.mExecutor);
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                Log.m104w(MmTelFeature.LOG_TAG, "MmTelFeature Binder - " + errorLogName + " exception: " + e.getMessage());
                return null;
            }
        }

        private <T> T executeMethodAsyncForResultNoException(final Supplier<T> r, String errorLogName, Executor executor) {
            CompletableFuture<T> future = CompletableFuture.supplyAsync(new Supplier() { // from class: android.telephony.ims.feature.MmTelFeature$1$$ExternalSyntheticLambda17
                @Override // java.util.function.Supplier
                public final Object get() {
                    Object runWithCleanCallingIdentity;
                    runWithCleanCallingIdentity = TelephonyUtils.runWithCleanCallingIdentity(r);
                    return runWithCleanCallingIdentity;
                }
            }, executor);
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                Log.m104w(MmTelFeature.LOG_TAG, "MmTelFeature Binder - " + errorLogName + " exception: " + e.getMessage());
                return null;
            }
        }
    }

    /* loaded from: classes3.dex */
    public static class MmTelCapabilities extends ImsFeature.Capabilities {
        public static final int CAPABILITY_TYPE_CALL_COMPOSER = 16;
        public static final int CAPABILITY_TYPE_MAX = 17;
        public static final int CAPABILITY_TYPE_NONE = 0;
        public static final int CAPABILITY_TYPE_SMS = 8;
        public static final int CAPABILITY_TYPE_UT = 4;
        public static final int CAPABILITY_TYPE_VIDEO = 2;
        public static final int CAPABILITY_TYPE_VOICE = 1;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes3.dex */
        public @interface MmTelCapability {
        }

        @SystemApi
        public MmTelCapabilities() {
        }

        @SystemApi
        @Deprecated
        public MmTelCapabilities(ImsFeature.Capabilities c) {
            this.mCapabilities = c.mCapabilities;
        }

        @SystemApi
        public MmTelCapabilities(int capabilities) {
            super(capabilities);
        }

        @Override // android.telephony.ims.feature.ImsFeature.Capabilities
        @SystemApi
        public final void addCapabilities(int capabilities) {
            super.addCapabilities(capabilities);
        }

        @Override // android.telephony.ims.feature.ImsFeature.Capabilities
        @SystemApi
        public final void removeCapabilities(int capability) {
            super.removeCapabilities(capability);
        }

        @Override // android.telephony.ims.feature.ImsFeature.Capabilities
        public final boolean isCapable(int capabilities) {
            return super.isCapable(capabilities);
        }

        @Override // android.telephony.ims.feature.ImsFeature.Capabilities
        public String toString() {
            return "MmTel Capabilities - [Voice: " + isCapable(1) + " Video: " + isCapable(2) + " UT: " + isCapable(4) + " SMS: " + isCapable(8) + " CALL_COMPOSER: " + isCapable(16) + NavigationBarInflaterView.SIZE_MOD_END;
        }
    }

    /* loaded from: classes3.dex */
    public static class Listener extends IImsMmTelListener.Stub {
        @Override // android.telephony.ims.aidl.IImsMmTelListener
        public IImsCallSessionListener onIncomingCall(IImsCallSession c, String callId, Bundle extras) {
            return null;
        }

        @Override // android.telephony.ims.aidl.IImsMmTelListener
        public void onRejectedCall(ImsCallProfile callProfile, ImsReasonInfo reason) {
        }

        @Override // android.telephony.ims.aidl.IImsMmTelListener
        public void onVoiceMessageCountUpdate(int count) {
        }

        @Override // android.telephony.ims.aidl.IImsMmTelListener
        public void onAudioModeIsVoipChanged(int imsAudioHandler) {
        }

        @Override // android.telephony.ims.aidl.IImsMmTelListener
        public void onTriggerEpsFallback(int reason) {
        }

        @Override // android.telephony.ims.aidl.IImsMmTelListener
        public void onStartImsTrafficSession(int token, int trafficType, int accessNetworkType, int trafficDirection, IImsTrafficSessionCallback callback) {
        }

        @Override // android.telephony.ims.aidl.IImsMmTelListener
        public void onModifyImsTrafficSession(int token, int accessNetworkType) {
        }

        @Override // android.telephony.ims.aidl.IImsMmTelListener
        public void onStopImsTrafficSession(int token) {
        }

        @Override // android.telephony.ims.aidl.IImsMmTelListener
        public void onMediaQualityStatusChanged(MediaQualityStatus status) {
        }
    }

    /* loaded from: classes3.dex */
    public static class ImsTrafficSessionCallbackWrapper {
        public static final int INVALID_TOKEN = -1;
        private static final int MAX_TOKEN = 65536;
        private static final AtomicInteger sTokenGenerator = new AtomicInteger();
        private IImsTrafficSessionCallbackStub mCallback;
        private ImsTrafficSessionCallback mImsTrafficSessionCallback;
        private int mToken;

        private ImsTrafficSessionCallbackWrapper(ImsTrafficSessionCallback callback) {
            this.mCallback = null;
            this.mToken = -1;
            this.mImsTrafficSessionCallback = callback;
        }

        final void update(Executor executor) {
            if (executor == null) {
                throw new IllegalArgumentException("ImsTrafficSessionCallback Executor must be non-null");
            }
            IImsTrafficSessionCallbackStub iImsTrafficSessionCallbackStub = this.mCallback;
            if (iImsTrafficSessionCallbackStub == null) {
                this.mCallback = new IImsTrafficSessionCallbackStub(this.mImsTrafficSessionCallback, executor);
                this.mToken = generateToken();
                return;
            }
            iImsTrafficSessionCallbackStub.update(executor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class IImsTrafficSessionCallbackStub extends IImsTrafficSessionCallback.Stub {
            private Executor mExecutor;
            private WeakReference<ImsTrafficSessionCallback> mImsTrafficSessionCallbackWeakRef;

            IImsTrafficSessionCallbackStub(ImsTrafficSessionCallback imsTrafficCallback, Executor executor) {
                this.mImsTrafficSessionCallbackWeakRef = new WeakReference<>(imsTrafficCallback);
                this.mExecutor = executor;
            }

            void update(Executor executor) {
                this.mExecutor = executor;
            }

            @Override // android.telephony.ims.aidl.IImsTrafficSessionCallback
            public void onReady() {
                final ImsTrafficSessionCallback callback = this.mImsTrafficSessionCallbackWeakRef.get();
                if (callback == null) {
                    return;
                }
                Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.ims.feature.MmTelFeature$ImsTrafficSessionCallbackWrapper$IImsTrafficSessionCallbackStub$$ExternalSyntheticLambda2
                    @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                    public final void runOrThrow() {
                        MmTelFeature.ImsTrafficSessionCallbackWrapper.IImsTrafficSessionCallbackStub.this.lambda$onReady$1(callback);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onReady$1(final ImsTrafficSessionCallback callback) throws Exception {
                this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.feature.MmTelFeature$ImsTrafficSessionCallbackWrapper$IImsTrafficSessionCallbackStub$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        ImsTrafficSessionCallback.this.onReady();
                    }
                });
            }

            @Override // android.telephony.ims.aidl.IImsTrafficSessionCallback
            public void onError(final ConnectionFailureInfo info) {
                final ImsTrafficSessionCallback callback = this.mImsTrafficSessionCallbackWeakRef.get();
                if (callback == null) {
                    return;
                }
                Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.telephony.ims.feature.MmTelFeature$ImsTrafficSessionCallbackWrapper$IImsTrafficSessionCallbackStub$$ExternalSyntheticLambda0
                    @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                    public final void runOrThrow() {
                        MmTelFeature.ImsTrafficSessionCallbackWrapper.IImsTrafficSessionCallbackStub.this.lambda$onError$3(callback, info);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onError$3(final ImsTrafficSessionCallback callback, final ConnectionFailureInfo info) throws Exception {
                this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.feature.MmTelFeature$ImsTrafficSessionCallbackWrapper$IImsTrafficSessionCallbackStub$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        ImsTrafficSessionCallback.this.onError(info);
                    }
                });
            }
        }

        final IImsTrafficSessionCallbackStub getCallbackBinder() {
            return this.mCallback;
        }

        final int getToken() {
            return this.mToken;
        }

        final void reset() {
            this.mCallback = null;
            this.mToken = -1;
        }

        private static int generateToken() {
            AtomicInteger atomicInteger = sTokenGenerator;
            int token = atomicInteger.incrementAndGet();
            if (token == 65536) {
                atomicInteger.set(0);
            }
            return token;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setListener(IImsMmTelListener listener) {
        synchronized (this.mLock) {
            this.mListener = listener;
            if (listener != null) {
                onFeatureReady();
            }
        }
    }

    private IImsMmTelListener getListener() {
        IImsMmTelListener iImsMmTelListener;
        synchronized (this.mLock) {
            iImsMmTelListener = this.mListener;
        }
        return iImsMmTelListener;
    }

    @Override // android.telephony.ims.feature.ImsFeature
    @SystemApi
    public final MmTelCapabilities queryCapabilityStatus() {
        return new MmTelCapabilities(super.queryCapabilityStatus());
    }

    @SystemApi
    public final void notifyCapabilitiesStatusChanged(MmTelCapabilities c) {
        if (c == null) {
            throw new IllegalArgumentException("MmTelCapabilities must be non-null!");
        }
        super.notifyCapabilitiesStatusChanged((ImsFeature.Capabilities) c);
    }

    @SystemApi
    public final void notifyMediaQualityStatusChanged(MediaQualityStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("MediaQualityStatus must be non-null!");
        }
        Log.m108i(LOG_TAG, "notifyMediaQualityStatusChanged " + status);
        IImsMmTelListener listener = getListener();
        if (listener == null) {
            throw new IllegalStateException("Session is not available.");
        }
        try {
            listener.onMediaQualityStatusChanged(status);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @SystemApi
    @Deprecated
    public final void notifyIncomingCall(ImsCallSessionImplBase c, Bundle extras) {
        if (c == null || extras == null) {
            throw new IllegalArgumentException("ImsCallSessionImplBase and Bundle can not be null.");
        }
        IImsMmTelListener listener = getListener();
        if (listener == null) {
            throw new IllegalStateException("Session is not available.");
        }
        try {
            c.setDefaultExecutor(this.mExecutor);
            listener.onIncomingCall(c.getServiceImpl(), null, extras);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @SystemApi
    public final ImsCallSessionListener notifyIncomingCall(ImsCallSessionImplBase c, String callId, Bundle extras) {
        if (c == null || callId == null || extras == null) {
            throw new IllegalArgumentException("ImsCallSessionImplBase, callId, and Bundle can not be null.");
        }
        IImsMmTelListener listener = getListener();
        if (listener == null) {
            throw new IllegalStateException("Session is not available.");
        }
        try {
            c.setDefaultExecutor(this.mExecutor);
            IImsCallSessionListener isl = listener.onIncomingCall(c.getServiceImpl(), callId, extras);
            if (isl != null) {
                ImsCallSessionListener iCSL = new ImsCallSessionListener(isl);
                iCSL.setDefaultExecutor(this.mExecutor);
                return iCSL;
            }
            return null;
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @SystemApi
    public final void notifyRejectedCall(ImsCallProfile callProfile, ImsReasonInfo reason) {
        if (callProfile == null || reason == null) {
            throw new IllegalArgumentException("ImsCallProfile and ImsReasonInfo must not be null.");
        }
        IImsMmTelListener listener = getListener();
        if (listener == null) {
            throw new IllegalStateException("Session is not available.");
        }
        try {
            listener.onRejectedCall(callProfile, reason);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public final void notifyIncomingCallSession(IImsCallSession c, Bundle extras) {
        IImsMmTelListener listener = getListener();
        if (listener == null) {
            throw new IllegalStateException("Session is not available.");
        }
        try {
            listener.onIncomingCall(c, null, extras);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @SystemApi
    public final void notifyVoiceMessageCountUpdate(int count) {
        IImsMmTelListener listener = getListener();
        if (listener == null) {
            throw new IllegalStateException("Session is not available.");
        }
        try {
            listener.onVoiceMessageCountUpdate(count);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @SystemApi
    public final void setCallAudioHandler(int imsAudioHandler) {
        IImsMmTelListener listener = getListener();
        if (listener == null) {
            throw new IllegalStateException("Session is not available.");
        }
        try {
            listener.onAudioModeIsVoipChanged(imsAudioHandler);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public final void triggerEpsFallback(int reason) {
        IImsMmTelListener listener = getListener();
        if (listener == null) {
            throw new IllegalStateException("Session is not available.");
        }
        try {
            listener.onTriggerEpsFallback(reason);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public final void startImsTrafficSession(int trafficType, int accessNetworkType, int trafficDirection, Executor executor, ImsTrafficSessionCallback callback) {
        ImsTrafficSessionCallbackWrapper callbackWrapper;
        IImsMmTelListener listener = getListener();
        if (listener == null) {
            throw new IllegalStateException("Session is not available.");
        }
        ImsTrafficSessionCallbackWrapper callbackWrapper2 = this.mTrafficCallbacks.get(callback);
        if (callbackWrapper2 != null) {
            callbackWrapper = callbackWrapper2;
        } else {
            ImsTrafficSessionCallbackWrapper callbackWrapper3 = new ImsTrafficSessionCallbackWrapper(callback);
            this.mTrafficCallbacks.put(callback, callbackWrapper3);
            callbackWrapper = callbackWrapper3;
        }
        try {
            callbackWrapper.update(executor);
            listener.onStartImsTrafficSession(callbackWrapper.getToken(), trafficType, accessNetworkType, trafficDirection, callbackWrapper.getCallbackBinder());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public final void modifyImsTrafficSession(int accessNetworkType, ImsTrafficSessionCallback callback) {
        IImsMmTelListener listener = getListener();
        if (listener == null) {
            throw new IllegalStateException("Session is not available.");
        }
        ImsTrafficSessionCallbackWrapper callbackWrapper = this.mTrafficCallbacks.get(callback);
        if (callbackWrapper == null) {
            throw new IllegalStateException("Unknown ImsTrafficSessionCallback instance.");
        }
        try {
            listener.onModifyImsTrafficSession(callbackWrapper.getToken(), accessNetworkType);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public final void stopImsTrafficSession(ImsTrafficSessionCallback callback) {
        IImsMmTelListener listener = getListener();
        if (listener == null) {
            throw new IllegalStateException("Session is not available.");
        }
        ImsTrafficSessionCallbackWrapper callbackWrapper = this.mTrafficCallbacks.get(callback);
        if (callbackWrapper == null) {
            throw new IllegalStateException("Unknown ImsTrafficSessionCallback instance.");
        }
        try {
            listener.onStopImsTrafficSession(callbackWrapper.getToken());
            callbackWrapper.reset();
            this.mTrafficCallbacks.remove(callback);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override // android.telephony.ims.feature.ImsFeature
    @SystemApi
    public boolean queryCapabilityConfiguration(int capability, int radioTech) {
        return false;
    }

    @Override // android.telephony.ims.feature.ImsFeature
    @SystemApi
    public void changeEnabledCapabilities(CapabilityChangeRequest request, ImsFeature.CapabilityCallbackProxy c) {
    }

    @SystemApi
    public void setMediaThreshold(int mediaSessionType, MediaThreshold mediaThreshold) {
        Log.m112d(LOG_TAG, "setMediaThreshold is not supported." + mediaThreshold);
    }

    @SystemApi
    public void clearMediaThreshold(int mediaSessionType) {
        Log.m112d(LOG_TAG, "clearMediaThreshold is not supported." + mediaSessionType);
    }

    @SystemApi
    public MediaQualityStatus queryMediaQualityStatus(int mediaSessionType) {
        Log.m112d(LOG_TAG, "queryMediaQualityStatus is not supported." + mediaSessionType);
        return null;
    }

    @SystemApi
    public ImsCallProfile createCallProfile(int callSessionType, int callType) {
        return null;
    }

    @SystemApi
    public void changeOfferedRtpHeaderExtensionTypes(Set<RtpHeaderExtensionType> extensionTypes) {
    }

    public IImsCallSession createCallSessionInterface(ImsCallProfile profile) throws RemoteException {
        ImsCallSessionImplBase s = createCallSession(profile);
        if (s != null) {
            s.setDefaultExecutor(this.mExecutor);
            return s.getServiceImpl();
        }
        return null;
    }

    @SystemApi
    public ImsCallSessionImplBase createCallSession(ImsCallProfile profile) {
        return null;
    }

    @SystemApi
    public int shouldProcessCall(String[] numbers) {
        return 0;
    }

    protected IImsUt getUtInterface() throws RemoteException {
        ImsUtImplBase utImpl = getUt();
        if (utImpl != null) {
            utImpl.setDefaultExecutor(this.mExecutor);
            return utImpl.getInterface();
        }
        return null;
    }

    protected IImsEcbm getEcbmInterface() throws RemoteException {
        ImsEcbmImplBase ecbmImpl = getEcbm();
        if (ecbmImpl != null) {
            ecbmImpl.setDefaultExecutor(this.mExecutor);
            return ecbmImpl.getImsEcbm();
        }
        return null;
    }

    public IImsMultiEndpoint getMultiEndpointInterface() throws RemoteException {
        ImsMultiEndpointImplBase multiendpointImpl = getMultiEndpoint();
        if (multiendpointImpl != null) {
            multiendpointImpl.setDefaultExecutor(this.mExecutor);
            return multiendpointImpl.getIImsMultiEndpoint();
        }
        return null;
    }

    public ImsSmsImplBase getImsSmsImpl() {
        ImsSmsImplBase imsSmsImplBase;
        synchronized (this.mLock) {
            if (this.mSmsImpl == null) {
                ImsSmsImplBase smsImplementation = getSmsImplementation();
                this.mSmsImpl = smsImplementation;
                smsImplementation.setDefaultExecutor(this.mExecutor);
            }
            imsSmsImplBase = this.mSmsImpl;
        }
        return imsSmsImplBase;
    }

    @SystemApi
    public ImsUtImplBase getUt() {
        return new ImsUtImplBase();
    }

    @SystemApi
    public ImsEcbmImplBase getEcbm() {
        return new ImsEcbmImplBase();
    }

    @SystemApi
    public ImsMultiEndpointImplBase getMultiEndpoint() {
        return new ImsMultiEndpointImplBase();
    }

    @SystemApi
    public void setUiTtyMode(int mode, Message onCompleteMessage) {
    }

    @SystemApi
    public void setTerminalBasedCallWaitingStatus(boolean enabled) {
        throw new ServiceSpecificException(2, "Not implemented on device.");
    }

    @SystemApi
    public void notifySrvccStarted(Consumer<List<SrvccCall>> consumer) {
    }

    @SystemApi
    public void notifySrvccCompleted() {
    }

    @SystemApi
    public void notifySrvccFailed() {
    }

    @SystemApi
    public void notifySrvccCanceled() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSmsListener(IImsSmsListener listener) {
        getImsSmsImpl().registerSmsListener(listener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendSms(int token, int messageRef, String format, String smsc, boolean isRetry, byte[] pdu) {
        getImsSmsImpl().sendSms(token, messageRef, format, smsc, isRetry, pdu);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onMemoryAvailable(int token) {
        getImsSmsImpl().onMemoryAvailable(token);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void acknowledgeSms(int token, int messageRef, int result) {
        getImsSmsImpl().acknowledgeSms(token, messageRef, result);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void acknowledgeSms(int token, int messageRef, int result, byte[] pdu) {
        getImsSmsImpl().acknowledgeSms(token, messageRef, result, pdu);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void acknowledgeSmsReport(int token, int messageRef, int result) {
        getImsSmsImpl().acknowledgeSmsReport(token, messageRef, result);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSmsReady() {
        getImsSmsImpl().onReady();
    }

    @SystemApi
    public ImsSmsImplBase getSmsImplementation() {
        return new ImsSmsImplBase();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getSmsFormat() {
        return getImsSmsImpl().getSmsFormat();
    }

    @Override // android.telephony.ims.feature.ImsFeature
    @SystemApi
    public void onFeatureRemoved() {
    }

    @Override // android.telephony.ims.feature.ImsFeature
    @SystemApi
    public void onFeatureReady() {
    }

    @Override // android.telephony.ims.feature.ImsFeature
    public final IImsMmTelFeature getBinder() {
        return this.mImsMMTelBinder;
    }

    public final void setDefaultExecutor(Executor executor) {
        if (this.mExecutor == null) {
            this.mExecutor = executor;
        }
    }
}
