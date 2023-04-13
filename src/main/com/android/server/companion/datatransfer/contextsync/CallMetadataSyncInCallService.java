package com.android.server.companion.datatransfer.contextsync;

import android.telecom.Call;
import android.telecom.InCallService;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.companion.CompanionDeviceConfig;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class CallMetadataSyncInCallService extends InCallService {
    @VisibleForTesting
    boolean mShouldSync;
    @VisibleForTesting
    final Map<Call, CrossDeviceCall> mCurrentCalls = new HashMap();
    public final Call.Callback mTelecomCallback = new Call.Callback() { // from class: com.android.server.companion.datatransfer.contextsync.CallMetadataSyncInCallService.1
        @Override // android.telecom.Call.Callback
        public void onDetailsChanged(Call call, Call.Details details) {
            CallMetadataSyncInCallService.this.mCurrentCalls.get(call).updateCallDetails(details);
        }
    };
    public final CallMetadataSyncCallback mCallMetadataSyncCallback = new CallMetadataSyncCallback() { // from class: com.android.server.companion.datatransfer.contextsync.CallMetadataSyncInCallService.2
    };

    public static /* synthetic */ Call lambda$initializeCalls$0(Call call) {
        return call;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        initializeCalls();
    }

    public final void initializeCalls() {
        if (CompanionDeviceConfig.isEnabled("enable_context_sync_telecom") && this.mShouldSync) {
            this.mCurrentCalls.putAll((Map) getCalls().stream().collect(Collectors.toMap(new Function() { // from class: com.android.server.companion.datatransfer.contextsync.CallMetadataSyncInCallService$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Call lambda$initializeCalls$0;
                    lambda$initializeCalls$0 = CallMetadataSyncInCallService.lambda$initializeCalls$0((Call) obj);
                    return lambda$initializeCalls$0;
                }
            }, new Function() { // from class: com.android.server.companion.datatransfer.contextsync.CallMetadataSyncInCallService$$ExternalSyntheticLambda1
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    CrossDeviceCall lambda$initializeCalls$1;
                    lambda$initializeCalls$1 = CallMetadataSyncInCallService.this.lambda$initializeCalls$1((Call) obj);
                    return lambda$initializeCalls$1;
                }
            })));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ CrossDeviceCall lambda$initializeCalls$1(Call call) {
        return new CrossDeviceCall(getPackageManager(), call, getCallAudioState());
    }

    @VisibleForTesting
    public CrossDeviceCall getCallForId(long j, Collection<CrossDeviceCall> collection) {
        if (j == -1) {
            return null;
        }
        for (CrossDeviceCall crossDeviceCall : collection) {
            if (crossDeviceCall.getId() == j) {
                return crossDeviceCall;
            }
        }
        return null;
    }

    @Override // android.telecom.InCallService
    public void onCallAdded(Call call) {
        if (CompanionDeviceConfig.isEnabled("enable_context_sync_telecom") && this.mShouldSync) {
            this.mCurrentCalls.put(call, new CrossDeviceCall(getPackageManager(), call, getCallAudioState()));
        }
    }

    @Override // android.telecom.InCallService
    public void onCallRemoved(Call call) {
        if (CompanionDeviceConfig.isEnabled("enable_context_sync_telecom") && this.mShouldSync) {
            this.mCurrentCalls.remove(call);
        }
    }

    public void onMuteStateChanged(final boolean z) {
        if (CompanionDeviceConfig.isEnabled("enable_context_sync_telecom") && this.mShouldSync) {
            this.mCurrentCalls.values().forEach(new Consumer() { // from class: com.android.server.companion.datatransfer.contextsync.CallMetadataSyncInCallService$$ExternalSyntheticLambda2
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((CrossDeviceCall) obj).updateMuted(z);
                }
            });
        }
    }

    @Override // android.telecom.InCallService
    public void onSilenceRinger() {
        if (CompanionDeviceConfig.isEnabled("enable_context_sync_telecom") && this.mShouldSync) {
            this.mCurrentCalls.values().forEach(new Consumer() { // from class: com.android.server.companion.datatransfer.contextsync.CallMetadataSyncInCallService$$ExternalSyntheticLambda3
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((CrossDeviceCall) obj).updateSilencedIfRinging();
                }
            });
        }
    }
}
