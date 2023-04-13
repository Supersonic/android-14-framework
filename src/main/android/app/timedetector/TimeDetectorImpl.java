package android.app.timedetector;

import android.app.timedetector.ITimeDetectorService;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
/* loaded from: classes.dex */
public final class TimeDetectorImpl implements TimeDetector {
    private static final boolean DEBUG = false;
    private static final String TAG = "timedetector.TimeDetector";
    private final ITimeDetectorService mITimeDetectorService = ITimeDetectorService.Stub.asInterface(ServiceManager.getServiceOrThrow("time_detector"));

    @Override // android.app.timedetector.TimeDetector
    public void suggestTelephonyTime(TelephonyTimeSuggestion timeSuggestion) {
        try {
            this.mITimeDetectorService.suggestTelephonyTime(timeSuggestion);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @Override // android.app.timedetector.TimeDetector
    public boolean suggestManualTime(ManualTimeSuggestion timeSuggestion) {
        try {
            return this.mITimeDetectorService.suggestManualTime(timeSuggestion);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
