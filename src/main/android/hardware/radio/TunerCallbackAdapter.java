package android.hardware.radio;

import android.hardware.radio.ITunerCallback;
import android.hardware.radio.ProgramList;
import android.hardware.radio.RadioManager;
import android.hardware.radio.RadioTuner;
import android.p008os.Handler;
import android.p008os.Looper;
import android.util.Log;
import java.util.List;
import java.util.Map;
import java.util.Objects;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class TunerCallbackAdapter extends ITunerCallback.Stub {
    private static final String TAG = "BroadcastRadio.TunerCallbackAdapter";
    private final RadioTuner.Callback mCallback;
    RadioManager.ProgramInfo mCurrentProgramInfo;
    private boolean mDelayedCompleteCallback;
    private final Handler mHandler;
    List<RadioManager.ProgramInfo> mLastCompleteList;
    ProgramList mProgramList;
    private final Object mLock = new Object();
    boolean mIsAntennaConnected = true;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TunerCallbackAdapter(RadioTuner.Callback callback, Handler handler) {
        this.mCallback = (RadioTuner.Callback) Objects.requireNonNull(callback, "Callback cannot be null");
        if (handler == null) {
            this.mHandler = new Handler(Looper.getMainLooper());
        } else {
            this.mHandler = handler;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void close() {
        synchronized (this.mLock) {
            ProgramList programList = this.mProgramList;
            if (programList != null) {
                programList.close();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setProgramListObserver(final ProgramList programList, final ProgramList.OnCloseListener closeListener) {
        Objects.requireNonNull(closeListener, "CloseListener cannot be null");
        synchronized (this.mLock) {
            if (this.mProgramList != null) {
                Log.m104w(TAG, "Previous program list observer wasn't properly closed, closing it...");
                this.mProgramList.close();
            }
            this.mProgramList = programList;
            if (programList == null) {
                return;
            }
            programList.setOnCloseListener(new ProgramList.OnCloseListener() { // from class: android.hardware.radio.TunerCallbackAdapter$$ExternalSyntheticLambda10
                @Override // android.hardware.radio.ProgramList.OnCloseListener
                public final void onClose() {
                    TunerCallbackAdapter.this.lambda$setProgramListObserver$0(programList, closeListener);
                }
            });
            programList.addOnCompleteListener(new ProgramList.OnCompleteListener() { // from class: android.hardware.radio.TunerCallbackAdapter$$ExternalSyntheticLambda11
                @Override // android.hardware.radio.ProgramList.OnCompleteListener
                public final void onComplete() {
                    TunerCallbackAdapter.this.lambda$setProgramListObserver$1(programList);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setProgramListObserver$0(ProgramList programList, ProgramList.OnCloseListener closeListener) {
        synchronized (this.mLock) {
            if (this.mProgramList != programList) {
                return;
            }
            this.mProgramList = null;
            this.mLastCompleteList = null;
            closeListener.onClose();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setProgramListObserver$1(ProgramList programList) {
        synchronized (this.mLock) {
            if (this.mProgramList != programList) {
                return;
            }
            this.mLastCompleteList = programList.toList();
            if (this.mDelayedCompleteCallback) {
                Log.m112d(TAG, "Sending delayed onBackgroundScanComplete callback");
                sendBackgroundScanCompleteLocked();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<RadioManager.ProgramInfo> getLastCompleteList() {
        List<RadioManager.ProgramInfo> list;
        synchronized (this.mLock) {
            list = this.mLastCompleteList;
        }
        return list;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clearLastCompleteList() {
        synchronized (this.mLock) {
            this.mLastCompleteList = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RadioManager.ProgramInfo getCurrentProgramInformation() {
        RadioManager.ProgramInfo programInfo;
        synchronized (this.mLock) {
            programInfo = this.mCurrentProgramInfo;
        }
        return programInfo;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isAntennaConnected() {
        boolean isConnected;
        synchronized (this.mLock) {
            isConnected = this.mIsAntennaConnected;
        }
        return isConnected;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onError$2(int status) {
        this.mCallback.onError(status);
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onError(final int status) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.TunerCallbackAdapter$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                TunerCallbackAdapter.this.lambda$onError$2(status);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onTuneFailed$3(int status, ProgramSelector selector) {
        this.mCallback.onTuneFailed(status, selector);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // android.hardware.radio.ITunerCallback
    public void onTuneFailed(final int status, final ProgramSelector selector) {
        final int errorCode;
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.TunerCallbackAdapter$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                TunerCallbackAdapter.this.lambda$onTuneFailed$3(status, selector);
            }
        });
        switch (status) {
            case Integer.MIN_VALUE:
            case -38:
            case -22:
            case -19:
                Log.m108i(TAG, "Got an error with no mapping to the legacy API (" + status + "), doing a best-effort conversion to ERROR_SCAN_TIMEOUT");
                errorCode = 3;
                break;
            case -32:
            case -1:
                errorCode = 1;
                break;
            default:
                errorCode = 3;
                break;
        }
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.TunerCallbackAdapter$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                TunerCallbackAdapter.this.lambda$onTuneFailed$4(errorCode);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onTuneFailed$4(int errorCode) {
        this.mCallback.onError(errorCode);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onConfigurationChanged$5(RadioManager.BandConfig config) {
        this.mCallback.onConfigurationChanged(config);
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onConfigurationChanged(final RadioManager.BandConfig config) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.TunerCallbackAdapter$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                TunerCallbackAdapter.this.lambda$onConfigurationChanged$5(config);
            }
        });
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onCurrentProgramInfoChanged(final RadioManager.ProgramInfo info) {
        if (info == null) {
            Log.m110e(TAG, "ProgramInfo must not be null");
            return;
        }
        synchronized (this.mLock) {
            this.mCurrentProgramInfo = info;
        }
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.TunerCallbackAdapter$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                TunerCallbackAdapter.this.lambda$onCurrentProgramInfoChanged$6(info);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCurrentProgramInfoChanged$6(RadioManager.ProgramInfo info) {
        this.mCallback.onProgramInfoChanged(info);
        RadioMetadata metadata = info.getMetadata();
        if (metadata != null) {
            this.mCallback.onMetadataChanged(metadata);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onTrafficAnnouncement$7(boolean active) {
        this.mCallback.onTrafficAnnouncement(active);
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onTrafficAnnouncement(final boolean active) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.TunerCallbackAdapter$$ExternalSyntheticLambda14
            @Override // java.lang.Runnable
            public final void run() {
                TunerCallbackAdapter.this.lambda$onTrafficAnnouncement$7(active);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onEmergencyAnnouncement$8(boolean active) {
        this.mCallback.onEmergencyAnnouncement(active);
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onEmergencyAnnouncement(final boolean active) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.TunerCallbackAdapter$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                TunerCallbackAdapter.this.lambda$onEmergencyAnnouncement$8(active);
            }
        });
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onAntennaState(final boolean connected) {
        synchronized (this.mLock) {
            this.mIsAntennaConnected = connected;
        }
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.TunerCallbackAdapter$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                TunerCallbackAdapter.this.lambda$onAntennaState$9(connected);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onAntennaState$9(boolean connected) {
        this.mCallback.onAntennaState(connected);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBackgroundScanAvailabilityChange$10(boolean isAvailable) {
        this.mCallback.onBackgroundScanAvailabilityChange(isAvailable);
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onBackgroundScanAvailabilityChange(final boolean isAvailable) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.TunerCallbackAdapter$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                TunerCallbackAdapter.this.lambda$onBackgroundScanAvailabilityChange$10(isAvailable);
            }
        });
    }

    private void sendBackgroundScanCompleteLocked() {
        this.mDelayedCompleteCallback = false;
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.TunerCallbackAdapter$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                TunerCallbackAdapter.this.lambda$sendBackgroundScanCompleteLocked$11();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$sendBackgroundScanCompleteLocked$11() {
        this.mCallback.onBackgroundScanComplete();
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onBackgroundScanComplete() {
        synchronized (this.mLock) {
            if (this.mLastCompleteList == null) {
                Log.m108i(TAG, "Got onBackgroundScanComplete callback, but the program list didn't get through yet. Delaying it...");
                this.mDelayedCompleteCallback = true;
                return;
            }
            sendBackgroundScanCompleteLocked();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onProgramListChanged$12() {
        this.mCallback.onProgramListChanged();
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onProgramListChanged() {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.TunerCallbackAdapter$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                TunerCallbackAdapter.this.lambda$onProgramListChanged$12();
            }
        });
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onProgramListUpdated(final ProgramList.Chunk chunk) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.TunerCallbackAdapter$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                TunerCallbackAdapter.this.lambda$onProgramListUpdated$13(chunk);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onProgramListUpdated$13(ProgramList.Chunk chunk) {
        synchronized (this.mLock) {
            ProgramList programList = this.mProgramList;
            if (programList == null) {
                return;
            }
            programList.apply((ProgramList.Chunk) Objects.requireNonNull(chunk, "Chunk cannot be null"));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onConfigFlagUpdated$14(int flag, boolean value) {
        this.mCallback.onConfigFlagUpdated(flag, value);
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onConfigFlagUpdated(final int flag, final boolean value) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.TunerCallbackAdapter$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                TunerCallbackAdapter.this.lambda$onConfigFlagUpdated$14(flag, value);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onParametersUpdated$15(Map parameters) {
        this.mCallback.onParametersUpdated(parameters);
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onParametersUpdated(final Map<String, String> parameters) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.TunerCallbackAdapter$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                TunerCallbackAdapter.this.lambda$onParametersUpdated$15(parameters);
            }
        });
    }
}
