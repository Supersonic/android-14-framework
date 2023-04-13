package com.android.server.timedetector;

import android.app.ActivityManager;
import android.app.time.ExternalTimeSuggestion;
import android.app.time.ITimeDetectorListener;
import android.app.time.TimeCapabilitiesAndConfig;
import android.app.time.TimeConfiguration;
import android.app.time.TimeState;
import android.app.time.UnixEpochTime;
import android.app.timedetector.ITimeDetectorService;
import android.app.timedetector.ManualTimeSuggestion;
import android.app.timedetector.TelephonyTimeSuggestion;
import android.content.Context;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelableException;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ShellCallback;
import android.util.ArrayMap;
import android.util.IndentingPrintWriter;
import android.util.NtpTrustedTime;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.DumpUtils;
import com.android.server.FgThread;
import com.android.server.SystemService;
import com.android.server.location.gnss.TimeDetectorNetworkTimeHelper;
import com.android.server.timezonedetector.CallerIdentityInjector;
import com.android.server.timezonedetector.CurrentUserIdentityInjector;
import com.android.server.timezonedetector.StateChangeListener;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.time.DateTimeException;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class TimeDetectorService extends ITimeDetectorService.Stub implements IBinder.DeathRecipient {
    public final CallerIdentityInjector mCallerIdentityInjector;
    public final Context mContext;
    public final Handler mHandler;
    @GuardedBy({"mListeners"})
    public final ArrayMap<IBinder, ITimeDetectorListener> mListeners = new ArrayMap<>();
    public final NtpTrustedTime mNtpTrustedTime;
    public final ServiceConfigAccessor mServiceConfigAccessor;
    public final TimeDetectorStrategy mTimeDetectorStrategy;

    /* loaded from: classes2.dex */
    public static class Lifecycle extends SystemService {
        public Lifecycle(Context context) {
            super(context);
        }

        @Override // com.android.server.SystemService
        public void onStart() {
            Context context = getContext();
            Handler handler = FgThread.getHandler();
            ServiceConfigAccessor serviceConfigAccessorImpl = ServiceConfigAccessorImpl.getInstance(context);
            TimeDetectorStrategy create = TimeDetectorStrategyImpl.create(context, handler, serviceConfigAccessorImpl);
            publishLocalService(TimeDetectorInternal.class, new TimeDetectorInternalImpl(context, handler, CurrentUserIdentityInjector.REAL, serviceConfigAccessorImpl, create));
            publishBinderService("time_detector", new TimeDetectorService(context, handler, CallerIdentityInjector.REAL, serviceConfigAccessorImpl, create, NtpTrustedTime.getInstance(context)));
        }
    }

    @VisibleForTesting
    public TimeDetectorService(Context context, Handler handler, CallerIdentityInjector callerIdentityInjector, ServiceConfigAccessor serviceConfigAccessor, TimeDetectorStrategy timeDetectorStrategy, NtpTrustedTime ntpTrustedTime) {
        Objects.requireNonNull(context);
        this.mContext = context;
        Objects.requireNonNull(handler);
        this.mHandler = handler;
        Objects.requireNonNull(callerIdentityInjector);
        this.mCallerIdentityInjector = callerIdentityInjector;
        Objects.requireNonNull(serviceConfigAccessor);
        ServiceConfigAccessor serviceConfigAccessor2 = serviceConfigAccessor;
        this.mServiceConfigAccessor = serviceConfigAccessor2;
        Objects.requireNonNull(timeDetectorStrategy);
        this.mTimeDetectorStrategy = timeDetectorStrategy;
        Objects.requireNonNull(ntpTrustedTime);
        this.mNtpTrustedTime = ntpTrustedTime;
        serviceConfigAccessor2.addConfigurationInternalChangeListener(new StateChangeListener() { // from class: com.android.server.timedetector.TimeDetectorService$$ExternalSyntheticLambda0
            @Override // com.android.server.timezonedetector.StateChangeListener
            public final void onChange() {
                TimeDetectorService.this.lambda$new$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        this.mHandler.post(new Runnable() { // from class: com.android.server.timedetector.TimeDetectorService$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                TimeDetectorService.this.handleConfigurationInternalChangedOnHandlerThread();
            }
        });
    }

    public TimeCapabilitiesAndConfig getCapabilitiesAndConfig() {
        return getTimeCapabilitiesAndConfig(this.mCallerIdentityInjector.getCallingUserId());
    }

    public final TimeCapabilitiesAndConfig getTimeCapabilitiesAndConfig(int i) {
        enforceManageTimeDetectorPermission();
        long clearCallingIdentity = this.mCallerIdentityInjector.clearCallingIdentity();
        try {
            return this.mServiceConfigAccessor.getConfigurationInternal(i).createCapabilitiesAndConfig(false);
        } finally {
            this.mCallerIdentityInjector.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean updateConfiguration(TimeConfiguration timeConfiguration) {
        return updateConfiguration(this.mCallerIdentityInjector.getCallingUserId(), timeConfiguration);
    }

    public boolean updateConfiguration(int i, TimeConfiguration timeConfiguration) {
        int handleIncomingUser = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, false, false, "updateConfiguration", null);
        enforceManageTimeDetectorPermission();
        Objects.requireNonNull(timeConfiguration);
        long clearCallingIdentity = this.mCallerIdentityInjector.clearCallingIdentity();
        try {
            return this.mServiceConfigAccessor.updateConfiguration(handleIncomingUser, timeConfiguration, false);
        } finally {
            this.mCallerIdentityInjector.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void addListener(ITimeDetectorListener iTimeDetectorListener) {
        enforceManageTimeDetectorPermission();
        Objects.requireNonNull(iTimeDetectorListener);
        synchronized (this.mListeners) {
            IBinder asBinder = iTimeDetectorListener.asBinder();
            if (this.mListeners.containsKey(asBinder)) {
                return;
            }
            try {
                asBinder.linkToDeath(this, 0);
                this.mListeners.put(asBinder, iTimeDetectorListener);
            } catch (RemoteException e) {
                Slog.e("time_detector", "Unable to linkToDeath() for listener=" + iTimeDetectorListener, e);
            }
        }
    }

    public void removeListener(ITimeDetectorListener iTimeDetectorListener) {
        enforceManageTimeDetectorPermission();
        Objects.requireNonNull(iTimeDetectorListener);
        synchronized (this.mListeners) {
            IBinder asBinder = iTimeDetectorListener.asBinder();
            boolean z = false;
            if (this.mListeners.remove(asBinder) != null) {
                asBinder.unlinkToDeath(this, 0);
                z = true;
            }
            if (!z) {
                Slog.w("time_detector", "Client asked to remove listener=" + iTimeDetectorListener + ", but no listeners were removed. mListeners=" + this.mListeners);
            }
        }
    }

    @Override // android.os.IBinder.DeathRecipient
    public void binderDied() {
        Slog.wtf("time_detector", "binderDied() called unexpectedly.");
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
                Slog.w("time_detector", "Notified of binder death for who=" + iBinder + ", but did not remove any listeners. mListeners=" + this.mListeners);
            }
        }
    }

    public final void handleConfigurationInternalChangedOnHandlerThread() {
        synchronized (this.mListeners) {
            int size = this.mListeners.size();
            for (int i = 0; i < size; i++) {
                ITimeDetectorListener valueAt = this.mListeners.valueAt(i);
                try {
                    valueAt.onChange();
                } catch (RemoteException e) {
                    Slog.w("time_detector", "Unable to notify listener=" + valueAt, e);
                }
            }
        }
    }

    public TimeState getTimeState() {
        enforceManageTimeDetectorPermission();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mTimeDetectorStrategy.getTimeState();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void setTimeState(TimeState timeState) {
        enforceManageTimeDetectorPermission();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mTimeDetectorStrategy.setTimeState(timeState);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean confirmTime(UnixEpochTime unixEpochTime) {
        enforceManageTimeDetectorPermission();
        Objects.requireNonNull(unixEpochTime);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mTimeDetectorStrategy.confirmTime(unixEpochTime);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean setManualTime(ManualTimeSuggestion manualTimeSuggestion) {
        enforceManageTimeDetectorPermission();
        Objects.requireNonNull(manualTimeSuggestion);
        int callingUserId = this.mCallerIdentityInjector.getCallingUserId();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mTimeDetectorStrategy.suggestManualTime(callingUserId, manualTimeSuggestion, false);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void suggestTelephonyTime(final TelephonyTimeSuggestion telephonyTimeSuggestion) {
        enforceSuggestTelephonyTimePermission();
        Objects.requireNonNull(telephonyTimeSuggestion);
        this.mHandler.post(new Runnable() { // from class: com.android.server.timedetector.TimeDetectorService$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                TimeDetectorService.this.lambda$suggestTelephonyTime$1(telephonyTimeSuggestion);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$suggestTelephonyTime$1(TelephonyTimeSuggestion telephonyTimeSuggestion) {
        this.mTimeDetectorStrategy.suggestTelephonyTime(telephonyTimeSuggestion);
    }

    public boolean suggestManualTime(ManualTimeSuggestion manualTimeSuggestion) {
        enforceSuggestManualTimePermission();
        Objects.requireNonNull(manualTimeSuggestion);
        int callingUserId = this.mCallerIdentityInjector.getCallingUserId();
        long clearCallingIdentity = this.mCallerIdentityInjector.clearCallingIdentity();
        try {
            return this.mTimeDetectorStrategy.suggestManualTime(callingUserId, manualTimeSuggestion, false);
        } finally {
            this.mCallerIdentityInjector.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void suggestNetworkTime(final NetworkTimeSuggestion networkTimeSuggestion) {
        enforceSuggestNetworkTimePermission();
        Objects.requireNonNull(networkTimeSuggestion);
        this.mHandler.post(new Runnable() { // from class: com.android.server.timedetector.TimeDetectorService$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                TimeDetectorService.this.lambda$suggestNetworkTime$2(networkTimeSuggestion);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$suggestNetworkTime$2(NetworkTimeSuggestion networkTimeSuggestion) {
        this.mTimeDetectorStrategy.suggestNetworkTime(networkTimeSuggestion);
    }

    public void clearLatestNetworkTime() {
        enforceSuggestNetworkTimePermission();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mTimeDetectorStrategy.clearLatestNetworkSuggestion();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public UnixEpochTime latestNetworkTime() {
        NetworkTimeSuggestion networkTimeSuggestion;
        if (TimeDetectorNetworkTimeHelper.isInUse()) {
            networkTimeSuggestion = this.mTimeDetectorStrategy.getLatestNetworkSuggestion();
        } else {
            NtpTrustedTime.TimeResult cachedTimeResult = this.mNtpTrustedTime.getCachedTimeResult();
            networkTimeSuggestion = cachedTimeResult != null ? new NetworkTimeSuggestion(new UnixEpochTime(cachedTimeResult.getElapsedRealtimeMillis(), cachedTimeResult.getTimeMillis()), cachedTimeResult.getUncertaintyMillis()) : null;
        }
        if (networkTimeSuggestion == null) {
            throw new ParcelableException(new DateTimeException("Missing network time fix"));
        }
        return networkTimeSuggestion.getUnixEpochTime();
    }

    public NetworkTimeSuggestion getLatestNetworkSuggestion() {
        return this.mTimeDetectorStrategy.getLatestNetworkSuggestion();
    }

    public void suggestGnssTime(final GnssTimeSuggestion gnssTimeSuggestion) {
        enforceSuggestGnssTimePermission();
        Objects.requireNonNull(gnssTimeSuggestion);
        this.mHandler.post(new Runnable() { // from class: com.android.server.timedetector.TimeDetectorService$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                TimeDetectorService.this.lambda$suggestGnssTime$3(gnssTimeSuggestion);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$suggestGnssTime$3(GnssTimeSuggestion gnssTimeSuggestion) {
        this.mTimeDetectorStrategy.suggestGnssTime(gnssTimeSuggestion);
    }

    public void suggestExternalTime(final ExternalTimeSuggestion externalTimeSuggestion) {
        enforceSuggestExternalTimePermission();
        Objects.requireNonNull(externalTimeSuggestion);
        this.mHandler.post(new Runnable() { // from class: com.android.server.timedetector.TimeDetectorService$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                TimeDetectorService.this.lambda$suggestExternalTime$4(externalTimeSuggestion);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$suggestExternalTime$4(ExternalTimeSuggestion externalTimeSuggestion) {
        this.mTimeDetectorStrategy.suggestExternalTime(externalTimeSuggestion);
    }

    public void setNetworkTimeForSystemClockForTests(UnixEpochTime unixEpochTime, int i) {
        enforceSuggestNetworkTimePermission();
        if (TimeDetectorNetworkTimeHelper.isInUse()) {
            NetworkTimeSuggestion networkTimeSuggestion = new NetworkTimeSuggestion(unixEpochTime, i);
            networkTimeSuggestion.addDebugInfo("Injected for tests");
            this.mTimeDetectorStrategy.suggestNetworkTime(networkTimeSuggestion);
            return;
        }
        this.mNtpTrustedTime.setCachedTimeResult(new NtpTrustedTime.TimeResult(unixEpochTime.getUnixEpochTimeMillis(), unixEpochTime.getElapsedRealtimeMillis(), i, InetSocketAddress.createUnresolved("time.set.for.tests", 123)));
    }

    public void clearNetworkTimeForSystemClockForTests() {
        enforceSuggestNetworkTimePermission();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if (TimeDetectorNetworkTimeHelper.isInUse()) {
                this.mTimeDetectorStrategy.clearLatestNetworkSuggestion();
            } else {
                this.mNtpTrustedTime.clearCachedTimeResult();
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (DumpUtils.checkDumpPermission(this.mContext, "time_detector", printWriter)) {
            IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter);
            this.mTimeDetectorStrategy.dump(indentingPrintWriter, strArr);
            indentingPrintWriter.flush();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
        new TimeDetectorShellCommand(this).exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
    }

    public final void enforceSuggestTelephonyTimePermission() {
        this.mContext.enforceCallingPermission("android.permission.SUGGEST_TELEPHONY_TIME_AND_ZONE", "suggest telephony time and time zone");
    }

    public final void enforceSuggestManualTimePermission() {
        this.mContext.enforceCallingPermission("android.permission.SUGGEST_MANUAL_TIME_AND_ZONE", "suggest manual time and time zone");
    }

    public final void enforceSuggestNetworkTimePermission() {
        this.mContext.enforceCallingPermission("android.permission.SET_TIME", "suggest network time");
    }

    public final void enforceSuggestGnssTimePermission() {
        this.mContext.enforceCallingPermission("android.permission.SET_TIME", "suggest gnss time");
    }

    public final void enforceSuggestExternalTimePermission() {
        this.mContext.enforceCallingPermission("android.permission.SUGGEST_EXTERNAL_TIME", "suggest time from external source");
    }

    public final void enforceManageTimeDetectorPermission() {
        this.mContext.enforceCallingPermission("android.permission.MANAGE_TIME_AND_ZONE_DETECTION", "manage time and time zone detection");
    }
}
