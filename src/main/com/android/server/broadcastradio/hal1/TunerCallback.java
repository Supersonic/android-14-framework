package com.android.server.broadcastradio.hal1;

import android.hardware.radio.ITunerCallback;
import android.hardware.radio.ProgramList;
import android.hardware.radio.ProgramSelector;
import android.hardware.radio.RadioManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class TunerCallback implements ITunerCallback {
    public static final String TAG = "BcRadio1Srv.TunerCallback";
    public final ITunerCallback mClientCallback;
    public final long mNativeContext;
    public final Tuner mTuner;
    public final AtomicReference<ProgramList.Filter> mProgramListFilter = new AtomicReference<>();
    public boolean mInitialConfigurationDone = false;

    /* loaded from: classes.dex */
    public interface RunnableThrowingRemoteException {
        void run() throws RemoteException;
    }

    private native void nativeDetach(long j);

    private native void nativeFinalize(long j);

    private native long nativeInit(Tuner tuner, int i);

    public TunerCallback(Tuner tuner, ITunerCallback iTunerCallback, int i) {
        this.mTuner = tuner;
        this.mClientCallback = iTunerCallback;
        this.mNativeContext = nativeInit(tuner, i);
    }

    public void finalize() throws Throwable {
        nativeFinalize(this.mNativeContext);
        super.finalize();
    }

    public void detach() {
        nativeDetach(this.mNativeContext);
    }

    public final void dispatch(RunnableThrowingRemoteException runnableThrowingRemoteException) {
        try {
            runnableThrowingRemoteException.run();
        } catch (RemoteException e) {
            Slog.e(TAG, "client died", e);
        }
    }

    public final void handleHwFailure() {
        onError(0);
        this.mTuner.close();
    }

    public void startProgramListUpdates(ProgramList.Filter filter) {
        if (filter == null) {
            filter = new ProgramList.Filter();
        }
        this.mProgramListFilter.set(filter);
        sendProgramListUpdate();
    }

    public void stopProgramListUpdates() {
        this.mProgramListFilter.set(null);
    }

    public boolean isInitialConfigurationDone() {
        return this.mInitialConfigurationDone;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onError$0(int i) throws RemoteException {
        this.mClientCallback.onError(i);
    }

    public void onError(final int i) {
        dispatch(new RunnableThrowingRemoteException() { // from class: com.android.server.broadcastradio.hal1.TunerCallback$$ExternalSyntheticLambda3
            @Override // com.android.server.broadcastradio.hal1.TunerCallback.RunnableThrowingRemoteException
            public final void run() {
                TunerCallback.this.lambda$onError$0(i);
            }
        });
    }

    public void onTuneFailed(int i, ProgramSelector programSelector) {
        Slog.e(TAG, "Not applicable for HAL 1.x");
    }

    public void onConfigurationChanged(final RadioManager.BandConfig bandConfig) {
        this.mInitialConfigurationDone = true;
        dispatch(new RunnableThrowingRemoteException() { // from class: com.android.server.broadcastradio.hal1.TunerCallback$$ExternalSyntheticLambda1
            @Override // com.android.server.broadcastradio.hal1.TunerCallback.RunnableThrowingRemoteException
            public final void run() {
                TunerCallback.this.lambda$onConfigurationChanged$1(bandConfig);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onConfigurationChanged$1(RadioManager.BandConfig bandConfig) throws RemoteException {
        this.mClientCallback.onConfigurationChanged(bandConfig);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCurrentProgramInfoChanged$2(RadioManager.ProgramInfo programInfo) throws RemoteException {
        this.mClientCallback.onCurrentProgramInfoChanged(programInfo);
    }

    public void onCurrentProgramInfoChanged(final RadioManager.ProgramInfo programInfo) {
        dispatch(new RunnableThrowingRemoteException() { // from class: com.android.server.broadcastradio.hal1.TunerCallback$$ExternalSyntheticLambda2
            @Override // com.android.server.broadcastradio.hal1.TunerCallback.RunnableThrowingRemoteException
            public final void run() {
                TunerCallback.this.lambda$onCurrentProgramInfoChanged$2(programInfo);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onTrafficAnnouncement$3(boolean z) throws RemoteException {
        this.mClientCallback.onTrafficAnnouncement(z);
    }

    public void onTrafficAnnouncement(final boolean z) {
        dispatch(new RunnableThrowingRemoteException() { // from class: com.android.server.broadcastradio.hal1.TunerCallback$$ExternalSyntheticLambda9
            @Override // com.android.server.broadcastradio.hal1.TunerCallback.RunnableThrowingRemoteException
            public final void run() {
                TunerCallback.this.lambda$onTrafficAnnouncement$3(z);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onEmergencyAnnouncement$4(boolean z) throws RemoteException {
        this.mClientCallback.onEmergencyAnnouncement(z);
    }

    public void onEmergencyAnnouncement(final boolean z) {
        dispatch(new RunnableThrowingRemoteException() { // from class: com.android.server.broadcastradio.hal1.TunerCallback$$ExternalSyntheticLambda10
            @Override // com.android.server.broadcastradio.hal1.TunerCallback.RunnableThrowingRemoteException
            public final void run() {
                TunerCallback.this.lambda$onEmergencyAnnouncement$4(z);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onAntennaState$5(boolean z) throws RemoteException {
        this.mClientCallback.onAntennaState(z);
    }

    public void onAntennaState(final boolean z) {
        dispatch(new RunnableThrowingRemoteException() { // from class: com.android.server.broadcastradio.hal1.TunerCallback$$ExternalSyntheticLambda6
            @Override // com.android.server.broadcastradio.hal1.TunerCallback.RunnableThrowingRemoteException
            public final void run() {
                TunerCallback.this.lambda$onAntennaState$5(z);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBackgroundScanAvailabilityChange$6(boolean z) throws RemoteException {
        this.mClientCallback.onBackgroundScanAvailabilityChange(z);
    }

    public void onBackgroundScanAvailabilityChange(final boolean z) {
        dispatch(new RunnableThrowingRemoteException() { // from class: com.android.server.broadcastradio.hal1.TunerCallback$$ExternalSyntheticLambda8
            @Override // com.android.server.broadcastradio.hal1.TunerCallback.RunnableThrowingRemoteException
            public final void run() {
                TunerCallback.this.lambda$onBackgroundScanAvailabilityChange$6(z);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBackgroundScanComplete$7() throws RemoteException {
        this.mClientCallback.onBackgroundScanComplete();
    }

    public void onBackgroundScanComplete() {
        dispatch(new RunnableThrowingRemoteException() { // from class: com.android.server.broadcastradio.hal1.TunerCallback$$ExternalSyntheticLambda4
            @Override // com.android.server.broadcastradio.hal1.TunerCallback.RunnableThrowingRemoteException
            public final void run() {
                TunerCallback.this.lambda$onBackgroundScanComplete$7();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onProgramListChanged$8() throws RemoteException {
        this.mClientCallback.onProgramListChanged();
    }

    public void onProgramListChanged() {
        dispatch(new RunnableThrowingRemoteException() { // from class: com.android.server.broadcastradio.hal1.TunerCallback$$ExternalSyntheticLambda5
            @Override // com.android.server.broadcastradio.hal1.TunerCallback.RunnableThrowingRemoteException
            public final void run() {
                TunerCallback.this.lambda$onProgramListChanged$8();
            }
        });
        sendProgramListUpdate();
    }

    public final void sendProgramListUpdate() {
        ProgramList.Filter filter = this.mProgramListFilter.get();
        if (filter == null) {
            return;
        }
        try {
            final ProgramList.Chunk chunk = new ProgramList.Chunk(true, true, (Set) this.mTuner.getProgramList(filter.getVendorFilter()).stream().collect(Collectors.toSet()), (Set) null);
            dispatch(new RunnableThrowingRemoteException() { // from class: com.android.server.broadcastradio.hal1.TunerCallback$$ExternalSyntheticLambda0
                @Override // com.android.server.broadcastradio.hal1.TunerCallback.RunnableThrowingRemoteException
                public final void run() {
                    TunerCallback.this.lambda$sendProgramListUpdate$9(chunk);
                }
            });
        } catch (IllegalStateException unused) {
            Slog.d(TAG, "Program list not ready yet");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$sendProgramListUpdate$9(ProgramList.Chunk chunk) throws RemoteException {
        this.mClientCallback.onProgramListUpdated(chunk);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onProgramListUpdated$10(ProgramList.Chunk chunk) throws RemoteException {
        this.mClientCallback.onProgramListUpdated(chunk);
    }

    public void onProgramListUpdated(final ProgramList.Chunk chunk) {
        dispatch(new RunnableThrowingRemoteException() { // from class: com.android.server.broadcastradio.hal1.TunerCallback$$ExternalSyntheticLambda7
            @Override // com.android.server.broadcastradio.hal1.TunerCallback.RunnableThrowingRemoteException
            public final void run() {
                TunerCallback.this.lambda$onProgramListUpdated$10(chunk);
            }
        });
    }

    public void onConfigFlagUpdated(int i, boolean z) {
        Slog.w(TAG, "Not applicable for HAL 1.x");
    }

    public void onParametersUpdated(Map<String, String> map) {
        Slog.w(TAG, "Not applicable for HAL 1.x");
    }

    public IBinder asBinder() {
        throw new RuntimeException("Not a binder");
    }
}
