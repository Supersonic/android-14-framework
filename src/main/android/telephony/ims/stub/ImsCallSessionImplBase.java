package android.telephony.ims.stub;

import android.annotation.SystemApi;
import android.app.PendingIntent$$ExternalSyntheticLambda1;
import android.media.p007tv.interactive.TvInteractiveAppService;
import android.p008os.Message;
import android.p008os.RemoteException;
import android.telephony.ims.ImsCallProfile;
import android.telephony.ims.ImsCallSessionListener;
import android.telephony.ims.ImsStreamMediaProfile;
import android.telephony.ims.ImsVideoCallProvider;
import android.telephony.ims.RtpHeaderExtension;
import android.telephony.ims.aidl.IImsCallSessionListener;
import android.telephony.ims.stub.ImsCallSessionImplBase;
import android.util.ArraySet;
import android.util.Log;
import com.android.ims.internal.IImsCallSession;
import com.android.ims.internal.IImsVideoCallProvider;
import com.android.internal.telephony.util.TelephonyUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
@SystemApi
/* loaded from: classes3.dex */
public class ImsCallSessionImplBase implements AutoCloseable {
    private static final String LOG_TAG = "ImsCallSessionImplBase";
    public static final int MEDIA_STREAM_DIRECTION_DOWNLINK = 2;
    public static final int MEDIA_STREAM_DIRECTION_UPLINK = 1;
    public static final int MEDIA_STREAM_TYPE_AUDIO = 1;
    public static final int MEDIA_STREAM_TYPE_VIDEO = 2;
    public static final int USSD_MODE_NOTIFY = 0;
    public static final int USSD_MODE_REQUEST = 1;
    private Executor mExecutor = new PendingIntent$$ExternalSyntheticLambda1();
    private IImsCallSession mServiceImpl = new BinderC33011();

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface MediaStreamDirection {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface MediaStreamType {
    }

    /* loaded from: classes3.dex */
    public static class State {
        public static final int ESTABLISHED = 4;
        public static final int ESTABLISHING = 3;
        public static final int IDLE = 0;
        public static final int INITIATED = 1;
        public static final int INVALID = -1;
        public static final int NEGOTIATING = 2;
        public static final int REESTABLISHING = 6;
        public static final int RENEGOTIATING = 5;
        public static final int TERMINATED = 8;
        public static final int TERMINATING = 7;

        public static String toString(int state) {
            switch (state) {
                case 0:
                    return "IDLE";
                case 1:
                    return "INITIATED";
                case 2:
                    return "NEGOTIATING";
                case 3:
                    return "ESTABLISHING";
                case 4:
                    return "ESTABLISHED";
                case 5:
                    return "RENEGOTIATING";
                case 6:
                    return "REESTABLISHING";
                case 7:
                    return "TERMINATING";
                case 8:
                    return "TERMINATED";
                default:
                    return "UNKNOWN";
            }
        }

        private State() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.telephony.ims.stub.ImsCallSessionImplBase$1 */
    /* loaded from: classes3.dex */
    public class BinderC33011 extends IImsCallSession.Stub {
        BinderC33011() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$close$0() {
            ImsCallSessionImplBase.this.close();
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void close() {
            executeMethodAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda34
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSessionImplBase.BinderC33011.this.lambda$close$0();
                }
            }, "close");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ String lambda$getCallId$1() {
            return ImsCallSessionImplBase.this.getCallId();
        }

        @Override // com.android.ims.internal.IImsCallSession
        public String getCallId() {
            return (String) executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda4
                @Override // java.util.function.Supplier
                public final Object get() {
                    String lambda$getCallId$1;
                    lambda$getCallId$1 = ImsCallSessionImplBase.BinderC33011.this.lambda$getCallId$1();
                    return lambda$getCallId$1;
                }
            }, "getCallId");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ ImsCallProfile lambda$getCallProfile$2() {
            return ImsCallSessionImplBase.this.getCallProfile();
        }

        @Override // com.android.ims.internal.IImsCallSession
        public ImsCallProfile getCallProfile() {
            return (ImsCallProfile) executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda37
                @Override // java.util.function.Supplier
                public final Object get() {
                    ImsCallProfile lambda$getCallProfile$2;
                    lambda$getCallProfile$2 = ImsCallSessionImplBase.BinderC33011.this.lambda$getCallProfile$2();
                    return lambda$getCallProfile$2;
                }
            }, "getCallProfile");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ ImsCallProfile lambda$getLocalCallProfile$3() {
            return ImsCallSessionImplBase.this.getLocalCallProfile();
        }

        @Override // com.android.ims.internal.IImsCallSession
        public ImsCallProfile getLocalCallProfile() {
            return (ImsCallProfile) executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda12
                @Override // java.util.function.Supplier
                public final Object get() {
                    ImsCallProfile lambda$getLocalCallProfile$3;
                    lambda$getLocalCallProfile$3 = ImsCallSessionImplBase.BinderC33011.this.lambda$getLocalCallProfile$3();
                    return lambda$getLocalCallProfile$3;
                }
            }, "getLocalCallProfile");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ ImsCallProfile lambda$getRemoteCallProfile$4() {
            return ImsCallSessionImplBase.this.getRemoteCallProfile();
        }

        @Override // com.android.ims.internal.IImsCallSession
        public ImsCallProfile getRemoteCallProfile() {
            return (ImsCallProfile) executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda30
                @Override // java.util.function.Supplier
                public final Object get() {
                    ImsCallProfile lambda$getRemoteCallProfile$4;
                    lambda$getRemoteCallProfile$4 = ImsCallSessionImplBase.BinderC33011.this.lambda$getRemoteCallProfile$4();
                    return lambda$getRemoteCallProfile$4;
                }
            }, "getRemoteCallProfile");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ String lambda$getProperty$5(String name) {
            return ImsCallSessionImplBase.this.getProperty(name);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public String getProperty(final String name) {
            return (String) executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda19
                @Override // java.util.function.Supplier
                public final Object get() {
                    String lambda$getProperty$5;
                    lambda$getProperty$5 = ImsCallSessionImplBase.BinderC33011.this.lambda$getProperty$5(name);
                    return lambda$getProperty$5;
                }
            }, "getProperty");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ Integer lambda$getState$6() {
            return Integer.valueOf(ImsCallSessionImplBase.this.getState());
        }

        @Override // com.android.ims.internal.IImsCallSession
        public int getState() {
            return ((Integer) executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda5
                @Override // java.util.function.Supplier
                public final Object get() {
                    Integer lambda$getState$6;
                    lambda$getState$6 = ImsCallSessionImplBase.BinderC33011.this.lambda$getState$6();
                    return lambda$getState$6;
                }
            }, "getState")).intValue();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ Boolean lambda$isInCall$7() {
            return Boolean.valueOf(ImsCallSessionImplBase.this.isInCall());
        }

        @Override // com.android.ims.internal.IImsCallSession
        public boolean isInCall() {
            return ((Boolean) executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda29
                @Override // java.util.function.Supplier
                public final Object get() {
                    Boolean lambda$isInCall$7;
                    lambda$isInCall$7 = ImsCallSessionImplBase.BinderC33011.this.lambda$isInCall$7();
                    return lambda$isInCall$7;
                }
            }, "isInCall")).booleanValue();
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void setListener(IImsCallSessionListener listener) {
            final ImsCallSessionListener iCSL = new ImsCallSessionListener(listener);
            iCSL.setDefaultExecutor(ImsCallSessionImplBase.this.mExecutor);
            executeMethodAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda36
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSessionImplBase.BinderC33011.this.lambda$setListener$8(iCSL);
                }
            }, "setListener");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$setListener$8(ImsCallSessionListener iCSL) {
            ImsCallSessionImplBase.this.setListener(iCSL);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$setMute$9(boolean muted) {
            ImsCallSessionImplBase.this.setMute(muted);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void setMute(final boolean muted) {
            executeMethodAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda22
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSessionImplBase.BinderC33011.this.lambda$setMute$9(muted);
                }
            }, "setMute");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$start$10(String callee, ImsCallProfile profile) {
            ImsCallSessionImplBase.this.start(callee, profile);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void start(final String callee, final ImsCallProfile profile) {
            executeMethodAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSessionImplBase.BinderC33011.this.lambda$start$10(callee, profile);
                }
            }, "start");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$startConference$11(String[] participants, ImsCallProfile profile) {
            ImsCallSessionImplBase.this.startConference(participants, profile);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void startConference(final String[] participants, final ImsCallProfile profile) throws RemoteException {
            executeMethodAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda32
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSessionImplBase.BinderC33011.this.lambda$startConference$11(participants, profile);
                }
            }, "startConference");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$accept$12(int callType, ImsStreamMediaProfile profile) {
            ImsCallSessionImplBase.this.accept(callType, profile);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void accept(final int callType, final ImsStreamMediaProfile profile) {
            executeMethodAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSessionImplBase.BinderC33011.this.lambda$accept$12(callType, profile);
                }
            }, "accept");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$deflect$13(String deflectNumber) {
            ImsCallSessionImplBase.this.deflect(deflectNumber);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void deflect(final String deflectNumber) {
            executeMethodAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda33
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSessionImplBase.BinderC33011.this.lambda$deflect$13(deflectNumber);
                }
            }, "deflect");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$reject$14(int reason) {
            ImsCallSessionImplBase.this.reject(reason);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void reject(final int reason) {
            executeMethodAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda26
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSessionImplBase.BinderC33011.this.lambda$reject$14(reason);
                }
            }, "reject");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$transfer$15(String number, boolean isConfirmationRequired) {
            ImsCallSessionImplBase.this.transfer(number, isConfirmationRequired);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void transfer(final String number, final boolean isConfirmationRequired) {
            executeMethodAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda16
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSessionImplBase.BinderC33011.this.lambda$transfer$15(number, isConfirmationRequired);
                }
            }, "transfer");
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void consultativeTransfer(final IImsCallSession transferToSession) {
            executeMethodAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda13
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSessionImplBase.BinderC33011.this.lambda$consultativeTransfer$16(transferToSession);
                }
            }, "consultativeTransfer");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$consultativeTransfer$16(IImsCallSession transferToSession) {
            ImsCallSessionImplBase otherSession = new ImsCallSessionImplBase();
            otherSession.setServiceImpl(transferToSession);
            ImsCallSessionImplBase.this.transfer(otherSession);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$terminate$17(int reason) {
            ImsCallSessionImplBase.this.terminate(reason);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void terminate(final int reason) {
            executeMethodAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda14
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSessionImplBase.BinderC33011.this.lambda$terminate$17(reason);
                }
            }, "terminate");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$hold$18(ImsStreamMediaProfile profile) {
            ImsCallSessionImplBase.this.hold(profile);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void hold(final ImsStreamMediaProfile profile) {
            executeMethodAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda11
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSessionImplBase.BinderC33011.this.lambda$hold$18(profile);
                }
            }, "hold");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$resume$19(ImsStreamMediaProfile profile) {
            ImsCallSessionImplBase.this.resume(profile);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void resume(final ImsStreamMediaProfile profile) {
            executeMethodAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda17
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSessionImplBase.BinderC33011.this.lambda$resume$19(profile);
                }
            }, TvInteractiveAppService.TIME_SHIFT_COMMAND_TYPE_RESUME);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$merge$20() {
            ImsCallSessionImplBase.this.merge();
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void merge() {
            executeMethodAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda10
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSessionImplBase.BinderC33011.this.lambda$merge$20();
                }
            }, "merge");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$update$21(int callType, ImsStreamMediaProfile profile) {
            ImsCallSessionImplBase.this.update(callType, profile);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void update(final int callType, final ImsStreamMediaProfile profile) {
            executeMethodAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda35
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSessionImplBase.BinderC33011.this.lambda$update$21(callType, profile);
                }
            }, "update");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$extendToConference$22(String[] participants) {
            ImsCallSessionImplBase.this.extendToConference(participants);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void extendToConference(final String[] participants) {
            executeMethodAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda15
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSessionImplBase.BinderC33011.this.lambda$extendToConference$22(participants);
                }
            }, "extendToConference");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$inviteParticipants$23(String[] participants) {
            ImsCallSessionImplBase.this.inviteParticipants(participants);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void inviteParticipants(final String[] participants) {
            executeMethodAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda27
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSessionImplBase.BinderC33011.this.lambda$inviteParticipants$23(participants);
                }
            }, "inviteParticipants");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$removeParticipants$24(String[] participants) {
            ImsCallSessionImplBase.this.removeParticipants(participants);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void removeParticipants(final String[] participants) {
            executeMethodAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSessionImplBase.BinderC33011.this.lambda$removeParticipants$24(participants);
                }
            }, "removeParticipants");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$sendDtmf$25(char c, Message result) {
            ImsCallSessionImplBase.this.sendDtmf(c, result);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void sendDtmf(final char c, final Message result) {
            executeMethodAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda23
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSessionImplBase.BinderC33011.this.lambda$sendDtmf$25(c, result);
                }
            }, "sendDtmf");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$startDtmf$26(char c) {
            ImsCallSessionImplBase.this.startDtmf(c);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void startDtmf(final char c) {
            executeMethodAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda20
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSessionImplBase.BinderC33011.this.lambda$startDtmf$26(c);
                }
            }, "startDtmf");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$stopDtmf$27() {
            ImsCallSessionImplBase.this.stopDtmf();
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void stopDtmf() {
            executeMethodAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSessionImplBase.BinderC33011.this.lambda$stopDtmf$27();
                }
            }, "stopDtmf");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$sendUssd$28(String ussdMessage) {
            ImsCallSessionImplBase.this.sendUssd(ussdMessage);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void sendUssd(final String ussdMessage) {
            executeMethodAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda28
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSessionImplBase.BinderC33011.this.lambda$sendUssd$28(ussdMessage);
                }
            }, "sendUssd");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ IImsVideoCallProvider lambda$getVideoCallProvider$29() {
            return ImsCallSessionImplBase.this.getVideoCallProvider();
        }

        @Override // com.android.ims.internal.IImsCallSession
        public IImsVideoCallProvider getVideoCallProvider() {
            return (IImsVideoCallProvider) executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda24
                @Override // java.util.function.Supplier
                public final Object get() {
                    IImsVideoCallProvider lambda$getVideoCallProvider$29;
                    lambda$getVideoCallProvider$29 = ImsCallSessionImplBase.BinderC33011.this.lambda$getVideoCallProvider$29();
                    return lambda$getVideoCallProvider$29;
                }
            }, "getVideoCallProvider");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ Boolean lambda$isMultiparty$30() {
            return Boolean.valueOf(ImsCallSessionImplBase.this.isMultiparty());
        }

        @Override // com.android.ims.internal.IImsCallSession
        public boolean isMultiparty() {
            return ((Boolean) executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda1
                @Override // java.util.function.Supplier
                public final Object get() {
                    Boolean lambda$isMultiparty$30;
                    lambda$isMultiparty$30 = ImsCallSessionImplBase.BinderC33011.this.lambda$isMultiparty$30();
                    return lambda$isMultiparty$30;
                }
            }, "isMultiparty")).booleanValue();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$sendRttModifyRequest$31(ImsCallProfile toProfile) {
            ImsCallSessionImplBase.this.sendRttModifyRequest(toProfile);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void sendRttModifyRequest(final ImsCallProfile toProfile) {
            executeMethodAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda18
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSessionImplBase.BinderC33011.this.lambda$sendRttModifyRequest$31(toProfile);
                }
            }, "sendRttModifyRequest");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$sendRttModifyResponse$32(boolean status) {
            ImsCallSessionImplBase.this.sendRttModifyResponse(status);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void sendRttModifyResponse(final boolean status) {
            executeMethodAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda31
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSessionImplBase.BinderC33011.this.lambda$sendRttModifyResponse$32(status);
                }
            }, "sendRttModifyResponse");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$sendRttMessage$33(String rttMessage) {
            ImsCallSessionImplBase.this.sendRttMessage(rttMessage);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void sendRttMessage(final String rttMessage) {
            executeMethodAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda25
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSessionImplBase.BinderC33011.this.lambda$sendRttMessage$33(rttMessage);
                }
            }, "sendRttMessage");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$sendRtpHeaderExtensions$34(List extensions) {
            ImsCallSessionImplBase.this.sendRtpHeaderExtensions(new ArraySet(extensions));
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void sendRtpHeaderExtensions(final List<RtpHeaderExtension> extensions) {
            executeMethodAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda21
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSessionImplBase.BinderC33011.this.lambda$sendRtpHeaderExtensions$34(extensions);
                }
            }, "sendRtpHeaderExtensions");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionNotifyAnbr$35(int mediaType, int direction, int bitsPerSecond) {
            ImsCallSessionImplBase.this.callSessionNotifyAnbr(mediaType, direction, bitsPerSecond);
        }

        @Override // com.android.ims.internal.IImsCallSession
        public void callSessionNotifyAnbr(final int mediaType, final int direction, final int bitsPerSecond) {
            executeMethodAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSessionImplBase.BinderC33011.this.lambda$callSessionNotifyAnbr$35(mediaType, direction, bitsPerSecond);
                }
            }, "callSessionNotifyAnbr");
        }

        private void executeMethodAsync(final Runnable r, String errorLogName) {
            try {
                CompletableFuture.runAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        TelephonyUtils.runWithCleanCallingIdentity(r);
                    }
                }, ImsCallSessionImplBase.this.mExecutor).join();
            } catch (CancellationException | CompletionException e) {
                Log.m104w(ImsCallSessionImplBase.LOG_TAG, "ImsCallSessionImplBase Binder - " + errorLogName + " exception: " + e.getMessage());
            }
        }

        private <T> T executeMethodAsyncForResult(final Supplier<T> r, String errorLogName) {
            CompletableFuture<T> future = CompletableFuture.supplyAsync(new Supplier() { // from class: android.telephony.ims.stub.ImsCallSessionImplBase$1$$ExternalSyntheticLambda0
                @Override // java.util.function.Supplier
                public final Object get() {
                    Object runWithCleanCallingIdentity;
                    runWithCleanCallingIdentity = TelephonyUtils.runWithCleanCallingIdentity(r);
                    return runWithCleanCallingIdentity;
                }
            }, ImsCallSessionImplBase.this.mExecutor);
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                Log.m104w(ImsCallSessionImplBase.LOG_TAG, "ImsCallSessionImplBase Binder - " + errorLogName + " exception: " + e.getMessage());
                return null;
            }
        }
    }

    public final void setListener(IImsCallSessionListener listener) throws RemoteException {
        setListener(new ImsCallSessionListener(listener));
    }

    @Deprecated
    public void setListener(ImsCallSessionListener listener) {
    }

    @Override // java.lang.AutoCloseable
    public void close() {
    }

    public String getCallId() {
        return null;
    }

    public ImsCallProfile getCallProfile() {
        return null;
    }

    public ImsCallProfile getLocalCallProfile() {
        return null;
    }

    public ImsCallProfile getRemoteCallProfile() {
        return null;
    }

    public String getProperty(String name) {
        return null;
    }

    public int getState() {
        return -1;
    }

    public boolean isInCall() {
        return false;
    }

    public void setMute(boolean muted) {
    }

    public void start(String callee, ImsCallProfile profile) {
    }

    public void startConference(String[] participants, ImsCallProfile profile) {
    }

    public void accept(int callType, ImsStreamMediaProfile profile) {
    }

    public void deflect(String deflectNumber) {
    }

    public void reject(int reason) {
    }

    public void transfer(String number, boolean isConfirmationRequired) {
    }

    public void transfer(ImsCallSessionImplBase otherSession) {
    }

    public void terminate(int reason) {
    }

    public void hold(ImsStreamMediaProfile profile) {
    }

    public void resume(ImsStreamMediaProfile profile) {
    }

    public void merge() {
    }

    public void update(int callType, ImsStreamMediaProfile profile) {
    }

    public void extendToConference(String[] participants) {
    }

    public void inviteParticipants(String[] participants) {
    }

    public void removeParticipants(String[] participants) {
    }

    public void sendDtmf(char c, Message result) {
    }

    public void startDtmf(char c) {
    }

    public void stopDtmf() {
    }

    public void sendUssd(String ussdMessage) {
    }

    public IImsVideoCallProvider getVideoCallProvider() {
        ImsVideoCallProvider provider = getImsVideoCallProvider();
        if (provider != null) {
            return provider.getInterface();
        }
        return null;
    }

    public ImsVideoCallProvider getImsVideoCallProvider() {
        return null;
    }

    public boolean isMultiparty() {
        return false;
    }

    public void sendRttModifyRequest(ImsCallProfile toProfile) {
    }

    public void sendRttModifyResponse(boolean status) {
    }

    public void sendRttMessage(String rttMessage) {
    }

    public void sendRtpHeaderExtensions(Set<RtpHeaderExtension> rtpHeaderExtensions) {
    }

    public void callSessionNotifyAnbr(int mediaType, int direction, int bitsPerSecond) {
        Log.m108i(LOG_TAG, "ImsCallSessionImplBase callSessionNotifyAnbr - mediaType: " + mediaType);
    }

    public IImsCallSession getServiceImpl() {
        return this.mServiceImpl;
    }

    public void setServiceImpl(IImsCallSession serviceImpl) {
        this.mServiceImpl = serviceImpl;
    }

    public final void setDefaultExecutor(Executor executor) {
        this.mExecutor = executor;
    }
}
