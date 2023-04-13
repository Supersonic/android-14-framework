package android.telecom;

import android.annotation.SystemApi;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.RemoteException;
import android.telecom.Call;
import com.android.internal.p028os.SomeArgs;
import com.android.internal.telecom.ICallScreeningAdapter;
import com.android.internal.telecom.ICallScreeningService;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* loaded from: classes3.dex */
public abstract class CallScreeningService extends Service {
    private static final int MSG_SCREEN_CALL = 1;
    public static final String SERVICE_INTERFACE = "android.telecom.CallScreeningService";
    private ICallScreeningAdapter mCallScreeningAdapter;
    private final Handler mHandler = new Handler(Looper.getMainLooper()) { // from class: android.telecom.CallScreeningService.1
        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    SomeArgs args = (SomeArgs) msg.obj;
                    try {
                        try {
                            CallScreeningService.this.mCallScreeningAdapter = (ICallScreeningAdapter) args.arg1;
                            Call.Details callDetails = Call.Details.createFromParcelableCall((ParcelableCall) args.arg2);
                            CallScreeningService.this.onScreenCall(callDetails);
                            if (callDetails.getCallDirection() == 1) {
                                CallScreeningService.this.mCallScreeningAdapter.onScreeningResponse(callDetails.getTelecomCallId(), new ComponentName(CallScreeningService.this.getPackageName(), getClass().getName()), null);
                            }
                        } catch (RemoteException e) {
                            Log.m131w(this, "Exception when screening call: " + e, new Object[0]);
                        }
                        return;
                    } finally {
                        args.recycle();
                    }
                default:
                    return;
            }
        }
    };

    public abstract void onScreenCall(Call.Details details);

    /* loaded from: classes3.dex */
    private final class CallScreeningBinder extends ICallScreeningService.Stub {
        private CallScreeningBinder() {
        }

        @Override // com.android.internal.telecom.ICallScreeningService
        public void screenCall(ICallScreeningAdapter adapter, ParcelableCall call) {
            Log.m133v(this, "screenCall", new Object[0]);
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = adapter;
            args.arg2 = call;
            CallScreeningService.this.mHandler.obtainMessage(1, args).sendToTarget();
        }
    }

    /* loaded from: classes3.dex */
    public static class ParcelableCallResponse implements Parcelable {
        public static final Parcelable.Creator<ParcelableCallResponse> CREATOR = new Parcelable.Creator<ParcelableCallResponse>() { // from class: android.telecom.CallScreeningService.ParcelableCallResponse.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public ParcelableCallResponse createFromParcel(Parcel in) {
                return new ParcelableCallResponse(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public ParcelableCallResponse[] newArray(int size) {
                return new ParcelableCallResponse[size];
            }
        };
        private final int mCallComposerAttachmentsToShow;
        private final boolean mShouldDisallowCall;
        private final boolean mShouldRejectCall;
        private final boolean mShouldScreenCallViaAudioProcessing;
        private final boolean mShouldSilenceCall;
        private final boolean mShouldSkipCallLog;
        private final boolean mShouldSkipNotification;

        private ParcelableCallResponse(boolean shouldDisallowCall, boolean shouldRejectCall, boolean shouldSilenceCall, boolean shouldSkipCallLog, boolean shouldSkipNotification, boolean shouldScreenCallViaAudioProcessing, int callComposerAttachmentsToShow) {
            this.mShouldDisallowCall = shouldDisallowCall;
            this.mShouldRejectCall = shouldRejectCall;
            this.mShouldSilenceCall = shouldSilenceCall;
            this.mShouldSkipCallLog = shouldSkipCallLog;
            this.mShouldSkipNotification = shouldSkipNotification;
            this.mShouldScreenCallViaAudioProcessing = shouldScreenCallViaAudioProcessing;
            this.mCallComposerAttachmentsToShow = callComposerAttachmentsToShow;
        }

        protected ParcelableCallResponse(Parcel in) {
            this.mShouldDisallowCall = in.readBoolean();
            this.mShouldRejectCall = in.readBoolean();
            this.mShouldSilenceCall = in.readBoolean();
            this.mShouldSkipCallLog = in.readBoolean();
            this.mShouldSkipNotification = in.readBoolean();
            this.mShouldScreenCallViaAudioProcessing = in.readBoolean();
            this.mCallComposerAttachmentsToShow = in.readInt();
        }

        public CallResponse toCallResponse() {
            return new CallResponse.Builder().setDisallowCall(this.mShouldDisallowCall).setRejectCall(this.mShouldRejectCall).setSilenceCall(this.mShouldSilenceCall).setSkipCallLog(this.mShouldSkipCallLog).setSkipNotification(this.mShouldSkipNotification).setShouldScreenCallViaAudioProcessing(this.mShouldScreenCallViaAudioProcessing).setCallComposerAttachmentsToShow(this.mCallComposerAttachmentsToShow).build();
        }

        public boolean shouldDisallowCall() {
            return this.mShouldDisallowCall;
        }

        public boolean shouldRejectCall() {
            return this.mShouldRejectCall;
        }

        public boolean shouldSilenceCall() {
            return this.mShouldSilenceCall;
        }

        public boolean shouldSkipCallLog() {
            return this.mShouldSkipCallLog;
        }

        public boolean shouldSkipNotification() {
            return this.mShouldSkipNotification;
        }

        public boolean shouldScreenCallViaAudioProcessing() {
            return this.mShouldScreenCallViaAudioProcessing;
        }

        public int getCallComposerAttachmentsToShow() {
            return this.mCallComposerAttachmentsToShow;
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeBoolean(this.mShouldDisallowCall);
            dest.writeBoolean(this.mShouldRejectCall);
            dest.writeBoolean(this.mShouldSilenceCall);
            dest.writeBoolean(this.mShouldSkipCallLog);
            dest.writeBoolean(this.mShouldSkipNotification);
            dest.writeBoolean(this.mShouldScreenCallViaAudioProcessing);
            dest.writeInt(this.mCallComposerAttachmentsToShow);
        }
    }

    /* loaded from: classes3.dex */
    public static class CallResponse {
        public static final int CALL_COMPOSER_ATTACHMENT_LOCATION = 2;
        public static final int CALL_COMPOSER_ATTACHMENT_PICTURE = 1;
        public static final int CALL_COMPOSER_ATTACHMENT_PRIORITY = 8;
        public static final int CALL_COMPOSER_ATTACHMENT_SUBJECT = 4;
        private static final int NUM_CALL_COMPOSER_ATTACHMENT_TYPES = 4;
        private final int mCallComposerAttachmentsToShow;
        private final boolean mShouldDisallowCall;
        private final boolean mShouldRejectCall;
        private final boolean mShouldScreenCallViaAudioProcessing;
        private final boolean mShouldSilenceCall;
        private final boolean mShouldSkipCallLog;
        private final boolean mShouldSkipNotification;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes3.dex */
        public @interface CallComposerAttachmentType {
        }

        private CallResponse(boolean shouldDisallowCall, boolean shouldRejectCall, boolean shouldSilenceCall, boolean shouldSkipCallLog, boolean shouldSkipNotification, boolean shouldScreenCallViaAudioProcessing, int callComposerAttachmentsToShow) {
            if (!shouldDisallowCall && (shouldRejectCall || shouldSkipCallLog || shouldSkipNotification)) {
                throw new IllegalStateException("Invalid response state for allowed call.");
            }
            if (shouldDisallowCall && shouldScreenCallViaAudioProcessing) {
                throw new IllegalStateException("Invalid response state for allowed call.");
            }
            this.mShouldDisallowCall = shouldDisallowCall;
            this.mShouldRejectCall = shouldRejectCall;
            this.mShouldSkipCallLog = shouldSkipCallLog;
            this.mShouldSkipNotification = shouldSkipNotification;
            this.mShouldSilenceCall = shouldSilenceCall;
            this.mShouldScreenCallViaAudioProcessing = shouldScreenCallViaAudioProcessing;
            this.mCallComposerAttachmentsToShow = callComposerAttachmentsToShow;
        }

        public boolean getDisallowCall() {
            return this.mShouldDisallowCall;
        }

        public boolean getRejectCall() {
            return this.mShouldRejectCall;
        }

        public boolean getSilenceCall() {
            return this.mShouldSilenceCall;
        }

        public boolean getSkipCallLog() {
            return this.mShouldSkipCallLog;
        }

        public boolean getSkipNotification() {
            return this.mShouldSkipNotification;
        }

        public boolean getShouldScreenCallViaAudioProcessing() {
            return this.mShouldScreenCallViaAudioProcessing;
        }

        public int getCallComposerAttachmentsToShow() {
            return this.mCallComposerAttachmentsToShow;
        }

        public ParcelableCallResponse toParcelable() {
            return new ParcelableCallResponse(this.mShouldDisallowCall, this.mShouldRejectCall, this.mShouldSilenceCall, this.mShouldSkipCallLog, this.mShouldSkipNotification, this.mShouldScreenCallViaAudioProcessing, this.mCallComposerAttachmentsToShow);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            CallResponse that = (CallResponse) o;
            if (this.mShouldDisallowCall == that.mShouldDisallowCall && this.mShouldRejectCall == that.mShouldRejectCall && this.mShouldSilenceCall == that.mShouldSilenceCall && this.mShouldSkipCallLog == that.mShouldSkipCallLog && this.mShouldSkipNotification == that.mShouldSkipNotification && this.mShouldScreenCallViaAudioProcessing == that.mShouldScreenCallViaAudioProcessing && this.mCallComposerAttachmentsToShow == that.mCallComposerAttachmentsToShow) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(Boolean.valueOf(this.mShouldDisallowCall), Boolean.valueOf(this.mShouldRejectCall), Boolean.valueOf(this.mShouldSilenceCall), Boolean.valueOf(this.mShouldSkipCallLog), Boolean.valueOf(this.mShouldSkipNotification), Boolean.valueOf(this.mShouldScreenCallViaAudioProcessing), Integer.valueOf(this.mCallComposerAttachmentsToShow));
        }

        /* loaded from: classes3.dex */
        public static class Builder {
            private int mCallComposerAttachmentsToShow = -1;
            private boolean mShouldDisallowCall;
            private boolean mShouldRejectCall;
            private boolean mShouldScreenCallViaAudioProcessing;
            private boolean mShouldSilenceCall;
            private boolean mShouldSkipCallLog;
            private boolean mShouldSkipNotification;

            public Builder setDisallowCall(boolean shouldDisallowCall) {
                this.mShouldDisallowCall = shouldDisallowCall;
                return this;
            }

            public Builder setRejectCall(boolean shouldRejectCall) {
                this.mShouldRejectCall = shouldRejectCall;
                return this;
            }

            public Builder setSilenceCall(boolean shouldSilenceCall) {
                this.mShouldSilenceCall = shouldSilenceCall;
                return this;
            }

            public Builder setSkipCallLog(boolean shouldSkipCallLog) {
                this.mShouldSkipCallLog = shouldSkipCallLog;
                return this;
            }

            public Builder setSkipNotification(boolean shouldSkipNotification) {
                this.mShouldSkipNotification = shouldSkipNotification;
                return this;
            }

            @SystemApi
            public Builder setShouldScreenCallViaAudioProcessing(boolean shouldScreenCallViaAudioProcessing) {
                this.mShouldScreenCallViaAudioProcessing = shouldScreenCallViaAudioProcessing;
                return this;
            }

            public Builder setCallComposerAttachmentsToShow(int callComposerAttachmentsToShow) {
                if (callComposerAttachmentsToShow < 0) {
                    return this;
                }
                if ((callComposerAttachmentsToShow & 16) != 0) {
                    throw new IllegalArgumentException("Attachment types must match the ones defined in CallResponse");
                }
                this.mCallComposerAttachmentsToShow = callComposerAttachmentsToShow;
                return this;
            }

            public CallResponse build() {
                return new CallResponse(this.mShouldDisallowCall, this.mShouldRejectCall, this.mShouldSilenceCall, this.mShouldSkipCallLog, this.mShouldSkipNotification, this.mShouldScreenCallViaAudioProcessing, this.mCallComposerAttachmentsToShow);
            }
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        Log.m133v(this, "onBind", new Object[0]);
        return new CallScreeningBinder();
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        Log.m133v(this, "onUnbind", new Object[0]);
        return false;
    }

    public final void respondToCall(Call.Details callDetails, CallResponse response) {
        try {
            this.mCallScreeningAdapter.onScreeningResponse(callDetails.getTelecomCallId(), new ComponentName(getPackageName(), getClass().getName()), response.toParcelable());
        } catch (RemoteException e) {
            Log.m137e(this, e, "Got remote exception when returning response", new Object[0]);
        }
    }
}
