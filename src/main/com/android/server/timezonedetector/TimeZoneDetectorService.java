package com.android.server.timezonedetector;

import android.app.ActivityManager;
import android.app.time.ITimeZoneDetectorListener;
import android.app.time.TimeZoneCapabilitiesAndConfig;
import android.app.time.TimeZoneConfiguration;
import android.app.time.TimeZoneState;
import android.app.timezonedetector.ITimeZoneDetectorService;
import android.app.timezonedetector.ManualTimeZoneSuggestion;
import android.app.timezonedetector.TelephonyTimeZoneSuggestion;
import android.content.Context;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ShellCallback;
import android.util.ArrayMap;
import android.util.IndentingPrintWriter;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.DumpUtils;
import com.android.server.FgThread;
import com.android.server.SystemService;
import com.android.server.timezonedetector.DeviceActivityMonitor;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class TimeZoneDetectorService extends ITimeZoneDetectorService.Stub implements IBinder.DeathRecipient {
    public final CallerIdentityInjector mCallerIdentityInjector;
    public final Context mContext;
    public final Handler mHandler;
    public final TimeZoneDetectorStrategy mTimeZoneDetectorStrategy;
    @GuardedBy({"mListeners"})
    public final ArrayMap<IBinder, ITimeZoneDetectorListener> mListeners = new ArrayMap<>();
    @GuardedBy({"mDumpables"})
    public final List<Dumpable> mDumpables = new ArrayList();

    /* loaded from: classes2.dex */
    public static final class Lifecycle extends SystemService {
        public Lifecycle(Context context) {
            super(context);
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r5v1, types: [com.android.server.timezonedetector.TimeZoneDetectorService, android.os.IBinder] */
        @Override // com.android.server.SystemService
        public void onStart() {
            Context context = getContext();
            Handler handler = FgThread.getHandler();
            final TimeZoneDetectorStrategyImpl create = TimeZoneDetectorStrategyImpl.create(handler, ServiceConfigAccessorImpl.getInstance(context));
            DeviceActivityMonitor create2 = DeviceActivityMonitorImpl.create(context, handler);
            create2.addListener(new DeviceActivityMonitor.Listener() { // from class: com.android.server.timezonedetector.TimeZoneDetectorService.Lifecycle.1
                @Override // com.android.server.timezonedetector.DeviceActivityMonitor.Listener
                public void onFlightComplete() {
                    create.enableTelephonyTimeZoneFallback("onFlightComplete()");
                }
            });
            publishLocalService(TimeZoneDetectorInternal.class, new TimeZoneDetectorInternalImpl(context, handler, CurrentUserIdentityInjector.REAL, create));
            ?? timeZoneDetectorService = new TimeZoneDetectorService(context, handler, CallerIdentityInjector.REAL, create);
            timeZoneDetectorService.addDumpable(create2);
            publishBinderService("time_zone_detector", timeZoneDetectorService);
        }
    }

    @VisibleForTesting
    public TimeZoneDetectorService(Context context, Handler handler, CallerIdentityInjector callerIdentityInjector, TimeZoneDetectorStrategy timeZoneDetectorStrategy) {
        Objects.requireNonNull(context);
        this.mContext = context;
        Objects.requireNonNull(handler);
        this.mHandler = handler;
        Objects.requireNonNull(callerIdentityInjector);
        this.mCallerIdentityInjector = callerIdentityInjector;
        Objects.requireNonNull(timeZoneDetectorStrategy);
        TimeZoneDetectorStrategy timeZoneDetectorStrategy2 = timeZoneDetectorStrategy;
        this.mTimeZoneDetectorStrategy = timeZoneDetectorStrategy2;
        timeZoneDetectorStrategy2.addChangeListener(new StateChangeListener() { // from class: com.android.server.timezonedetector.TimeZoneDetectorService$$ExternalSyntheticLambda0
            @Override // com.android.server.timezonedetector.StateChangeListener
            public final void onChange() {
                TimeZoneDetectorService.this.lambda$new$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        this.mHandler.post(new Runnable() { // from class: com.android.server.timezonedetector.TimeZoneDetectorService$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                TimeZoneDetectorService.this.handleChangeOnHandlerThread();
            }
        });
    }

    public TimeZoneCapabilitiesAndConfig getCapabilitiesAndConfig() {
        return getCapabilitiesAndConfig(this.mCallerIdentityInjector.getCallingUserId());
    }

    public TimeZoneCapabilitiesAndConfig getCapabilitiesAndConfig(int i) {
        enforceManageTimeZoneDetectorPermission();
        int handleIncomingUser = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, false, false, "getCapabilitiesAndConfig", null);
        long clearCallingIdentity = this.mCallerIdentityInjector.clearCallingIdentity();
        try {
            return this.mTimeZoneDetectorStrategy.getCapabilitiesAndConfig(handleIncomingUser, false);
        } finally {
            this.mCallerIdentityInjector.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean updateConfiguration(TimeZoneConfiguration timeZoneConfiguration) {
        return updateConfiguration(this.mCallerIdentityInjector.getCallingUserId(), timeZoneConfiguration);
    }

    public boolean updateConfiguration(int i, TimeZoneConfiguration timeZoneConfiguration) {
        int handleIncomingUser = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, false, false, "updateConfiguration", null);
        enforceManageTimeZoneDetectorPermission();
        Objects.requireNonNull(timeZoneConfiguration);
        long clearCallingIdentity = this.mCallerIdentityInjector.clearCallingIdentity();
        try {
            return this.mTimeZoneDetectorStrategy.updateConfiguration(handleIncomingUser, timeZoneConfiguration, false);
        } finally {
            this.mCallerIdentityInjector.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void addListener(ITimeZoneDetectorListener iTimeZoneDetectorListener) {
        enforceManageTimeZoneDetectorPermission();
        Objects.requireNonNull(iTimeZoneDetectorListener);
        synchronized (this.mListeners) {
            IBinder asBinder = iTimeZoneDetectorListener.asBinder();
            if (this.mListeners.containsKey(asBinder)) {
                return;
            }
            try {
                asBinder.linkToDeath(this, 0);
                this.mListeners.put(asBinder, iTimeZoneDetectorListener);
            } catch (RemoteException e) {
                Slog.e("time_zone_detector", "Unable to linkToDeath() for listener=" + iTimeZoneDetectorListener, e);
            }
        }
    }

    public void removeListener(ITimeZoneDetectorListener iTimeZoneDetectorListener) {
        enforceManageTimeZoneDetectorPermission();
        Objects.requireNonNull(iTimeZoneDetectorListener);
        synchronized (this.mListeners) {
            IBinder asBinder = iTimeZoneDetectorListener.asBinder();
            boolean z = false;
            if (this.mListeners.remove(asBinder) != null) {
                asBinder.unlinkToDeath(this, 0);
                z = true;
            }
            if (!z) {
                Slog.w("time_zone_detector", "Client asked to remove listener=" + iTimeZoneDetectorListener + ", but no listeners were removed. mListeners=" + this.mListeners);
            }
        }
    }

    @Override // android.os.IBinder.DeathRecipient
    public void binderDied() {
        Slog.wtf("time_zone_detector", "binderDied() called unexpectedly.");
    }

    public void binderDied(IBinder iBinder) {
        synchronized (this.mListeners) {
            boolean z = true;
            int size = this.mListeners.size() - 1;
            while (true) {
                if (size < 0) {
                    z = false;
                    break;
                } else if (this.mListeners.keyAt(size).equals(iBinder)) {
                    this.mListeners.removeAt(size);
                    break;
                } else {
                    size--;
                }
            }
            if (!z) {
                Slog.w("time_zone_detector", "Notified of binder death for who=" + iBinder + ", but did not remove any listeners. mListeners=" + this.mListeners);
            }
        }
    }

    public void handleChangeOnHandlerThread() {
        synchronized (this.mListeners) {
            int size = this.mListeners.size();
            for (int i = 0; i < size; i++) {
                ITimeZoneDetectorListener valueAt = this.mListeners.valueAt(i);
                try {
                    valueAt.onChange();
                } catch (RemoteException e) {
                    Slog.w("time_zone_detector", "Unable to notify listener=" + valueAt, e);
                }
            }
        }
    }

    public void handleLocationAlgorithmEvent(final LocationAlgorithmEvent locationAlgorithmEvent) {
        enforceSuggestGeolocationTimeZonePermission();
        Objects.requireNonNull(locationAlgorithmEvent);
        this.mHandler.post(new Runnable() { // from class: com.android.server.timezonedetector.TimeZoneDetectorService$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                TimeZoneDetectorService.this.lambda$handleLocationAlgorithmEvent$1(locationAlgorithmEvent);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleLocationAlgorithmEvent$1(LocationAlgorithmEvent locationAlgorithmEvent) {
        this.mTimeZoneDetectorStrategy.handleLocationAlgorithmEvent(locationAlgorithmEvent);
    }

    public TimeZoneState getTimeZoneState() {
        enforceManageTimeZoneDetectorPermission();
        long clearCallingIdentity = this.mCallerIdentityInjector.clearCallingIdentity();
        try {
            return this.mTimeZoneDetectorStrategy.getTimeZoneState();
        } finally {
            this.mCallerIdentityInjector.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void setTimeZoneState(TimeZoneState timeZoneState) {
        enforceManageTimeZoneDetectorPermission();
        long clearCallingIdentity = this.mCallerIdentityInjector.clearCallingIdentity();
        try {
            this.mTimeZoneDetectorStrategy.setTimeZoneState(timeZoneState);
        } finally {
            this.mCallerIdentityInjector.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean confirmTimeZone(String str) {
        enforceManageTimeZoneDetectorPermission();
        long clearCallingIdentity = this.mCallerIdentityInjector.clearCallingIdentity();
        try {
            return this.mTimeZoneDetectorStrategy.confirmTimeZone(str);
        } finally {
            this.mCallerIdentityInjector.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean setManualTimeZone(ManualTimeZoneSuggestion manualTimeZoneSuggestion) {
        enforceManageTimeZoneDetectorPermission();
        int callingUserId = this.mCallerIdentityInjector.getCallingUserId();
        long clearCallingIdentity = this.mCallerIdentityInjector.clearCallingIdentity();
        try {
            return this.mTimeZoneDetectorStrategy.suggestManualTimeZone(callingUserId, manualTimeZoneSuggestion, false);
        } finally {
            this.mCallerIdentityInjector.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean suggestManualTimeZone(ManualTimeZoneSuggestion manualTimeZoneSuggestion) {
        enforceSuggestManualTimeZonePermission();
        Objects.requireNonNull(manualTimeZoneSuggestion);
        int callingUserId = this.mCallerIdentityInjector.getCallingUserId();
        long clearCallingIdentity = this.mCallerIdentityInjector.clearCallingIdentity();
        try {
            return this.mTimeZoneDetectorStrategy.suggestManualTimeZone(callingUserId, manualTimeZoneSuggestion, false);
        } finally {
            this.mCallerIdentityInjector.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void suggestTelephonyTimeZone(final TelephonyTimeZoneSuggestion telephonyTimeZoneSuggestion) {
        enforceSuggestTelephonyTimeZonePermission();
        Objects.requireNonNull(telephonyTimeZoneSuggestion);
        this.mHandler.post(new Runnable() { // from class: com.android.server.timezonedetector.TimeZoneDetectorService$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                TimeZoneDetectorService.this.lambda$suggestTelephonyTimeZone$2(telephonyTimeZoneSuggestion);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$suggestTelephonyTimeZone$2(TelephonyTimeZoneSuggestion telephonyTimeZoneSuggestion) {
        this.mTimeZoneDetectorStrategy.suggestTelephonyTimeZone(telephonyTimeZoneSuggestion);
    }

    public boolean isTelephonyTimeZoneDetectionSupported() {
        enforceManageTimeZoneDetectorPermission();
        return this.mTimeZoneDetectorStrategy.isTelephonyTimeZoneDetectionSupported();
    }

    public boolean isGeoTimeZoneDetectionSupported() {
        enforceManageTimeZoneDetectorPermission();
        return this.mTimeZoneDetectorStrategy.isGeoTimeZoneDetectionSupported();
    }

    public void enableTelephonyFallback(String str) {
        enforceManageTimeZoneDetectorPermission();
        this.mTimeZoneDetectorStrategy.enableTelephonyTimeZoneFallback(str);
    }

    public void addDumpable(Dumpable dumpable) {
        synchronized (this.mDumpables) {
            this.mDumpables.add(dumpable);
        }
    }

    public MetricsTimeZoneDetectorState generateMetricsState() {
        enforceManageTimeZoneDetectorPermission();
        return this.mTimeZoneDetectorStrategy.generateMetricsState();
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (DumpUtils.checkDumpPermission(this.mContext, "time_zone_detector", printWriter)) {
            IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter);
            this.mTimeZoneDetectorStrategy.dump(indentingPrintWriter, strArr);
            synchronized (this.mDumpables) {
                for (Dumpable dumpable : this.mDumpables) {
                    dumpable.dump(indentingPrintWriter, strArr);
                }
            }
            indentingPrintWriter.flush();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
        new TimeZoneDetectorShellCommand(this).exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
    }

    public final void enforceManageTimeZoneDetectorPermission() {
        this.mContext.enforceCallingPermission("android.permission.MANAGE_TIME_AND_ZONE_DETECTION", "manage time and time zone detection");
    }

    public final void enforceSuggestGeolocationTimeZonePermission() {
        this.mContext.enforceCallingPermission("android.permission.SET_TIME_ZONE", "suggest geolocation time zone");
    }

    public final void enforceSuggestTelephonyTimeZonePermission() {
        this.mContext.enforceCallingPermission("android.permission.SUGGEST_TELEPHONY_TIME_AND_ZONE", "suggest telephony time and time zone");
    }

    public final void enforceSuggestManualTimeZonePermission() {
        this.mContext.enforceCallingPermission("android.permission.SUGGEST_MANUAL_TIME_AND_ZONE", "suggest manual time and time zone");
    }
}
