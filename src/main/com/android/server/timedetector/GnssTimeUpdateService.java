package com.android.server.timedetector;

import android.annotation.RequiresPermission;
import android.app.AlarmManager;
import android.app.time.UnixEpochTime;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationManagerInternal;
import android.location.LocationRequest;
import android.location.LocationTime;
import android.os.Binder;
import android.os.Handler;
import android.os.ResultReceiver;
import android.os.ShellCallback;
import android.os.SystemClock;
import android.util.LocalLog;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.DumpUtils;
import com.android.server.FgThread;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Executor;
/* loaded from: classes2.dex */
public final class GnssTimeUpdateService extends Binder {

    /* renamed from: D */
    public static final boolean f1156D = Log.isLoggable("GnssTimeUpdateService", 3);
    public static final Duration GNSS_TIME_UPDATE_ALARM_INTERVAL = Duration.ofHours(4);
    @GuardedBy({"mLock"})
    public AlarmManager.OnAlarmListener mAlarmListener;
    public final AlarmManager mAlarmManager;
    public final Context mContext;
    public volatile UnixEpochTime mLastSuggestedGnssTime;
    @GuardedBy({"mLock"})
    public LocationListener mLocationListener;
    public final LocationManager mLocationManager;
    public final LocationManagerInternal mLocationManagerInternal;
    public final TimeDetectorInternal mTimeDetectorInternal;
    public final LocalLog mLocalLog = new LocalLog(10, false);
    public final Executor mExecutor = FgThread.getExecutor();
    public final Handler mHandler = FgThread.getHandler();
    public final Object mLock = new Object();

    /* loaded from: classes2.dex */
    public static class Lifecycle extends SystemService {
        public GnssTimeUpdateService mService;

        public Lifecycle(Context context) {
            super(context);
        }

        @Override // com.android.server.SystemService
        public void onStart() {
            Context createAttributionContext = getContext().createAttributionContext("GnssTimeUpdateService");
            GnssTimeUpdateService gnssTimeUpdateService = new GnssTimeUpdateService(createAttributionContext, (AlarmManager) createAttributionContext.getSystemService(AlarmManager.class), (LocationManager) createAttributionContext.getSystemService(LocationManager.class), (LocationManagerInternal) LocalServices.getService(LocationManagerInternal.class), (TimeDetectorInternal) LocalServices.getService(TimeDetectorInternal.class));
            this.mService = gnssTimeUpdateService;
            publishBinderService("gnss_time_update_service", gnssTimeUpdateService);
        }

        @Override // com.android.server.SystemService
        public void onBootPhase(int i) {
            if (i == 600) {
                this.mService.startGnssListeningInternal();
            }
        }
    }

    @VisibleForTesting
    public GnssTimeUpdateService(Context context, AlarmManager alarmManager, LocationManager locationManager, LocationManagerInternal locationManagerInternal, TimeDetectorInternal timeDetectorInternal) {
        Objects.requireNonNull(context);
        this.mContext = context;
        Objects.requireNonNull(alarmManager);
        this.mAlarmManager = alarmManager;
        Objects.requireNonNull(locationManager);
        this.mLocationManager = locationManager;
        Objects.requireNonNull(locationManagerInternal);
        this.mLocationManagerInternal = locationManagerInternal;
        Objects.requireNonNull(timeDetectorInternal);
        this.mTimeDetectorInternal = timeDetectorInternal;
    }

    @RequiresPermission("android.permission.SET_TIME")
    public boolean startGnssListening() {
        this.mContext.enforceCallingPermission("android.permission.SET_TIME", "Start GNSS listening");
        this.mLocalLog.log("startGnssListening() called");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return startGnssListeningInternal();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @VisibleForTesting
    public boolean startGnssListeningInternal() {
        if (!this.mLocationManager.hasProvider("gps")) {
            logError("GPS provider does not exist on this device");
            return false;
        }
        synchronized (this.mLock) {
            if (this.mLocationListener != null) {
                logDebug("Already listening for GNSS updates");
                return true;
            }
            AlarmManager.OnAlarmListener onAlarmListener = this.mAlarmListener;
            if (onAlarmListener != null) {
                this.mAlarmManager.cancel(onAlarmListener);
                this.mAlarmListener = null;
            }
            startGnssListeningLocked();
            return true;
        }
    }

    @GuardedBy({"mLock"})
    public final void startGnssListeningLocked() {
        logDebug("startGnssListeningLocked()");
        this.mLocationListener = new LocationListener() { // from class: com.android.server.timedetector.GnssTimeUpdateService$$ExternalSyntheticLambda0
            @Override // android.location.LocationListener
            public final void onLocationChanged(Location location) {
                GnssTimeUpdateService.this.lambda$startGnssListeningLocked$0(location);
            }
        };
        this.mLocationManager.requestLocationUpdates("gps", new LocationRequest.Builder(Long.MAX_VALUE).setMinUpdateIntervalMillis(0L).build(), this.mExecutor, this.mLocationListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startGnssListeningLocked$0(Location location) {
        handleLocationAvailable();
    }

    public final void handleLocationAvailable() {
        logDebug("handleLocationAvailable()");
        LocationTime gnssTimeMillis = this.mLocationManagerInternal.getGnssTimeMillis();
        if (gnssTimeMillis != null) {
            String str = "Passive location time received: " + gnssTimeMillis;
            logDebug(str);
            this.mLocalLog.log(str);
            suggestGnssTime(gnssTimeMillis);
        } else {
            logDebug("getGnssTimeMillis() returned null");
        }
        synchronized (this.mLock) {
            LocationListener locationListener = this.mLocationListener;
            if (locationListener == null) {
                logWarning("mLocationListener unexpectedly null");
            } else {
                this.mLocationManager.removeUpdates(locationListener);
                this.mLocationListener = null;
            }
            if (this.mAlarmListener != null) {
                logWarning("mAlarmListener was unexpectedly non-null");
                this.mAlarmManager.cancel(this.mAlarmListener);
            }
            AlarmManager.OnAlarmListener onAlarmListener = new AlarmManager.OnAlarmListener() { // from class: com.android.server.timedetector.GnssTimeUpdateService$$ExternalSyntheticLambda1
                @Override // android.app.AlarmManager.OnAlarmListener
                public final void onAlarm() {
                    GnssTimeUpdateService.this.handleAlarmFired();
                }
            };
            this.mAlarmListener = onAlarmListener;
            this.mAlarmManager.set(2, SystemClock.elapsedRealtime() + GNSS_TIME_UPDATE_ALARM_INTERVAL.toMillis(), "GnssTimeUpdateService", onAlarmListener, this.mHandler);
        }
    }

    public final void handleAlarmFired() {
        logDebug("handleAlarmFired()");
        synchronized (this.mLock) {
            this.mAlarmListener = null;
            startGnssListeningLocked();
        }
    }

    public final void suggestGnssTime(LocationTime locationTime) {
        logDebug("suggestGnssTime()");
        UnixEpochTime unixEpochTime = new UnixEpochTime(locationTime.getElapsedRealtimeNanos() / 1000000, locationTime.getUnixEpochTimeMillis());
        this.mLastSuggestedGnssTime = unixEpochTime;
        this.mTimeDetectorInternal.suggestGnssTime(new GnssTimeSuggestion(unixEpochTime));
    }

    @Override // android.os.Binder
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (DumpUtils.checkDumpPermission(this.mContext, "GnssTimeUpdateService", printWriter)) {
            printWriter.println("mLastSuggestedGnssTime: " + this.mLastSuggestedGnssTime);
            synchronized (this.mLock) {
                printWriter.print("state: ");
                if (this.mLocationListener != null) {
                    printWriter.println("time updates enabled");
                } else {
                    printWriter.println("alarm enabled");
                }
            }
            printWriter.println("Log:");
            this.mLocalLog.dump(printWriter);
        }
    }

    public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
        new GnssTimeUpdateServiceShellCommand(this).exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
    }

    public final void logError(String str) {
        Log.e("GnssTimeUpdateService", str);
        this.mLocalLog.log(str);
    }

    public final void logWarning(String str) {
        Log.w("GnssTimeUpdateService", str);
        this.mLocalLog.log(str);
    }

    public final void logDebug(String str) {
        if (f1156D) {
            Log.d("GnssTimeUpdateService", str);
        }
    }
}
