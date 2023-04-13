package android.app.time;

import android.annotation.SystemApi;
import android.app.time.ITimeZoneDetectorListener;
import android.app.time.TimeManager;
import android.app.timedetector.ITimeDetectorService;
import android.app.timedetector.ManualTimeSuggestion;
import android.app.timezonedetector.ITimeZoneDetectorService;
import android.app.timezonedetector.ManualTimeZoneSuggestion;
import android.p008os.Process;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.util.ArrayMap;
import java.util.Objects;
import java.util.concurrent.Executor;
@SystemApi
/* loaded from: classes.dex */
public final class TimeManager {
    private static final boolean DEBUG = false;
    private static final String TAG = "time.TimeManager";
    private ArrayMap<TimeZoneDetectorListener, TimeZoneDetectorListener> mTimeZoneDetectorListeners;
    private ITimeZoneDetectorListener mTimeZoneDetectorReceiver;
    private final Object mLock = new Object();
    private final ITimeZoneDetectorService mITimeZoneDetectorService = ITimeZoneDetectorService.Stub.asInterface(ServiceManager.getServiceOrThrow("time_zone_detector"));
    private final ITimeDetectorService mITimeDetectorService = ITimeDetectorService.Stub.asInterface(ServiceManager.getServiceOrThrow("time_detector"));

    @FunctionalInterface
    /* loaded from: classes.dex */
    public interface TimeZoneDetectorListener {
        void onChange();
    }

    public TimeZoneCapabilitiesAndConfig getTimeZoneCapabilitiesAndConfig() {
        try {
            return this.mITimeZoneDetectorService.getCapabilitiesAndConfig();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public TimeCapabilitiesAndConfig getTimeCapabilitiesAndConfig() {
        try {
            return this.mITimeDetectorService.getCapabilitiesAndConfig();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean updateTimeConfiguration(TimeConfiguration configuration) {
        try {
            return this.mITimeDetectorService.updateConfiguration(configuration);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean updateTimeZoneConfiguration(TimeZoneConfiguration configuration) {
        try {
            return this.mITimeZoneDetectorService.updateConfiguration(configuration);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void addTimeZoneDetectorListener(final Executor executor, final TimeZoneDetectorListener listener) {
        synchronized (this.mLock) {
            ArrayMap<TimeZoneDetectorListener, TimeZoneDetectorListener> arrayMap = this.mTimeZoneDetectorListeners;
            if (arrayMap == null) {
                this.mTimeZoneDetectorListeners = new ArrayMap<>();
            } else if (arrayMap.containsKey(listener)) {
                return;
            }
            if (this.mTimeZoneDetectorReceiver == null) {
                ITimeZoneDetectorListener iListener = new ITimeZoneDetectorListener.Stub() { // from class: android.app.time.TimeManager.1
                    @Override // android.app.time.ITimeZoneDetectorListener
                    public void onChange() {
                        TimeManager.this.notifyTimeZoneDetectorListeners();
                    }
                };
                this.mTimeZoneDetectorReceiver = iListener;
                try {
                    this.mITimeZoneDetectorService.addListener(iListener);
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            }
            this.mTimeZoneDetectorListeners.put(listener, new TimeZoneDetectorListener() { // from class: android.app.time.TimeManager$$ExternalSyntheticLambda0
                @Override // android.app.time.TimeManager.TimeZoneDetectorListener
                public final void onChange() {
                    TimeManager.lambda$addTimeZoneDetectorListener$0(executor, listener);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$addTimeZoneDetectorListener$0(Executor executor, final TimeZoneDetectorListener listener) {
        Objects.requireNonNull(listener);
        executor.execute(new Runnable() { // from class: android.app.time.TimeManager$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                TimeManager.TimeZoneDetectorListener.this.onChange();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyTimeZoneDetectorListeners() {
        synchronized (this.mLock) {
            ArrayMap<TimeZoneDetectorListener, TimeZoneDetectorListener> arrayMap = this.mTimeZoneDetectorListeners;
            if (arrayMap != null && !arrayMap.isEmpty()) {
                ArrayMap<TimeZoneDetectorListener, TimeZoneDetectorListener> timeZoneDetectorListeners = new ArrayMap<>(this.mTimeZoneDetectorListeners);
                int size = timeZoneDetectorListeners.size();
                for (int i = 0; i < size; i++) {
                    timeZoneDetectorListeners.valueAt(i).onChange();
                }
            }
        }
    }

    public void removeTimeZoneDetectorListener(TimeZoneDetectorListener listener) {
        synchronized (this.mLock) {
            ArrayMap<TimeZoneDetectorListener, TimeZoneDetectorListener> arrayMap = this.mTimeZoneDetectorListeners;
            if (arrayMap != null && !arrayMap.isEmpty()) {
                this.mTimeZoneDetectorListeners.remove(listener);
                if (this.mTimeZoneDetectorListeners.isEmpty()) {
                    try {
                        this.mITimeZoneDetectorService.removeListener(this.mTimeZoneDetectorReceiver);
                        this.mTimeZoneDetectorReceiver = null;
                    } catch (RemoteException e) {
                        throw e.rethrowFromSystemServer();
                    }
                }
            }
        }
    }

    public void suggestExternalTime(ExternalTimeSuggestion timeSuggestion) {
        try {
            this.mITimeDetectorService.suggestExternalTime(timeSuggestion);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public TimeState getTimeState() {
        try {
            return this.mITimeDetectorService.getTimeState();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean confirmTime(UnixEpochTime unixEpochTime) {
        try {
            return this.mITimeDetectorService.confirmTime(unixEpochTime);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean setManualTime(UnixEpochTime unixEpochTime) {
        try {
            ManualTimeSuggestion manualTimeSuggestion = new ManualTimeSuggestion(unixEpochTime);
            manualTimeSuggestion.addDebugInfo("TimeManager.setTime()");
            manualTimeSuggestion.addDebugInfo("UID: " + Process.myUid());
            manualTimeSuggestion.addDebugInfo("UserHandle: " + Process.myUserHandle());
            manualTimeSuggestion.addDebugInfo("Process: " + Process.myProcessName());
            return this.mITimeDetectorService.setManualTime(manualTimeSuggestion);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public TimeZoneState getTimeZoneState() {
        try {
            return this.mITimeZoneDetectorService.getTimeZoneState();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean confirmTimeZone(String timeZoneId) {
        try {
            return this.mITimeZoneDetectorService.confirmTimeZone(timeZoneId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean setManualTimeZone(String timeZoneId) {
        try {
            ManualTimeZoneSuggestion manualTimeZoneSuggestion = new ManualTimeZoneSuggestion(timeZoneId);
            manualTimeZoneSuggestion.addDebugInfo("TimeManager.setManualTimeZone()");
            manualTimeZoneSuggestion.addDebugInfo("UID: " + Process.myUid());
            manualTimeZoneSuggestion.addDebugInfo("Process: " + Process.myProcessName());
            return this.mITimeZoneDetectorService.setManualTimeZone(manualTimeZoneSuggestion);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
