package android.service.timezone;

import android.annotation.SystemApi;
import android.app.Service;
import android.content.Intent;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.p008os.SystemClock;
import android.service.timezone.ITimeZoneProvider;
import android.service.timezone.TimeZoneProviderService;
import android.util.Log;
import com.android.internal.p028os.BackgroundThread;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public abstract class TimeZoneProviderService extends Service {
    public static final String PRIMARY_LOCATION_TIME_ZONE_PROVIDER_SERVICE_INTERFACE = "android.service.timezone.PrimaryLocationTimeZoneProviderService";
    public static final String SECONDARY_LOCATION_TIME_ZONE_PROVIDER_SERVICE_INTERFACE = "android.service.timezone.SecondaryLocationTimeZoneProviderService";
    private static final String TAG = "TimeZoneProviderService";
    public static final String TEST_COMMAND_RESULT_ERROR_KEY = "ERROR";
    public static final String TEST_COMMAND_RESULT_SUCCESS_KEY = "SUCCESS";
    private long mEventFilteringAgeThresholdMillis;
    private TimeZoneProviderEvent mLastEventSent;
    private ITimeZoneProviderManager mManager;
    private final TimeZoneProviderServiceWrapper mWrapper = new TimeZoneProviderServiceWrapper();
    private final Object mLock = new Object();
    private final Handler mHandler = BackgroundThread.getHandler();

    public abstract void onStartUpdates(long j);

    public abstract void onStopUpdates();

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        return this.mWrapper;
    }

    public final void reportSuggestion(TimeZoneProviderSuggestion suggestion) {
        reportSuggestionInternal(suggestion, null);
    }

    public final void reportSuggestion(TimeZoneProviderSuggestion suggestion, TimeZoneProviderStatus providerStatus) {
        Objects.requireNonNull(providerStatus);
        reportSuggestionInternal(suggestion, providerStatus);
    }

    private void reportSuggestionInternal(final TimeZoneProviderSuggestion suggestion, final TimeZoneProviderStatus providerStatus) {
        Objects.requireNonNull(suggestion);
        this.mHandler.post(new Runnable() { // from class: android.service.timezone.TimeZoneProviderService$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                TimeZoneProviderService.this.lambda$reportSuggestionInternal$0(suggestion, providerStatus);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$reportSuggestionInternal$0(TimeZoneProviderSuggestion suggestion, TimeZoneProviderStatus providerStatus) {
        synchronized (this.mLock) {
            ITimeZoneProviderManager manager = this.mManager;
            if (manager != null) {
                try {
                    TimeZoneProviderEvent thisEvent = TimeZoneProviderEvent.createSuggestionEvent(SystemClock.elapsedRealtime(), suggestion, providerStatus);
                    if (shouldSendEvent(thisEvent)) {
                        manager.onTimeZoneProviderEvent(thisEvent);
                        this.mLastEventSent = thisEvent;
                    }
                } catch (RemoteException | RuntimeException e) {
                    Log.m102w(TAG, e);
                }
            }
        }
    }

    public final void reportUncertain() {
        reportUncertainInternal(null);
    }

    public final void reportUncertain(TimeZoneProviderStatus providerStatus) {
        Objects.requireNonNull(providerStatus);
        reportUncertainInternal(providerStatus);
    }

    private void reportUncertainInternal(final TimeZoneProviderStatus providerStatus) {
        this.mHandler.post(new Runnable() { // from class: android.service.timezone.TimeZoneProviderService$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                TimeZoneProviderService.this.lambda$reportUncertainInternal$1(providerStatus);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$reportUncertainInternal$1(TimeZoneProviderStatus providerStatus) {
        synchronized (this.mLock) {
            ITimeZoneProviderManager manager = this.mManager;
            if (manager != null) {
                try {
                    TimeZoneProviderEvent thisEvent = TimeZoneProviderEvent.createUncertainEvent(SystemClock.elapsedRealtime(), providerStatus);
                    if (shouldSendEvent(thisEvent)) {
                        manager.onTimeZoneProviderEvent(thisEvent);
                        this.mLastEventSent = thisEvent;
                    }
                } catch (RemoteException | RuntimeException e) {
                    Log.m102w(TAG, e);
                }
            }
        }
    }

    public final void reportPermanentFailure(final Throwable cause) {
        Objects.requireNonNull(cause);
        this.mHandler.post(new Runnable() { // from class: android.service.timezone.TimeZoneProviderService$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                TimeZoneProviderService.this.lambda$reportPermanentFailure$2(cause);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$reportPermanentFailure$2(Throwable cause) {
        synchronized (this.mLock) {
            ITimeZoneProviderManager manager = this.mManager;
            if (manager != null) {
                try {
                    String causeString = cause.getMessage();
                    TimeZoneProviderEvent thisEvent = TimeZoneProviderEvent.createPermanentFailureEvent(SystemClock.elapsedRealtime(), causeString);
                    if (shouldSendEvent(thisEvent)) {
                        manager.onTimeZoneProviderEvent(thisEvent);
                        this.mLastEventSent = thisEvent;
                    }
                } catch (RemoteException | RuntimeException e) {
                    Log.m102w(TAG, e);
                }
            }
        }
    }

    private boolean shouldSendEvent(TimeZoneProviderEvent newEvent) {
        if (newEvent.isEquivalentTo(this.mLastEventSent)) {
            long timeSinceLastEventMillis = newEvent.getCreationElapsedMillis() - this.mLastEventSent.getCreationElapsedMillis();
            return timeSinceLastEventMillis > this.mEventFilteringAgeThresholdMillis;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onStartUpdatesInternal(ITimeZoneProviderManager manager, long initializationTimeoutMillis, long eventFilteringAgeThresholdMillis) {
        synchronized (this.mLock) {
            this.mManager = manager;
            this.mEventFilteringAgeThresholdMillis = eventFilteringAgeThresholdMillis;
            this.mLastEventSent = null;
            onStartUpdates(initializationTimeoutMillis);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onStopUpdatesInternal() {
        synchronized (this.mLock) {
            onStopUpdates();
            this.mManager = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Service
    public void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        synchronized (this.mLock) {
            writer.append((CharSequence) ("mLastEventSent=" + this.mLastEventSent));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class TimeZoneProviderServiceWrapper extends ITimeZoneProvider.Stub {
        private TimeZoneProviderServiceWrapper() {
        }

        @Override // android.service.timezone.ITimeZoneProvider
        public void startUpdates(final ITimeZoneProviderManager manager, final long initializationTimeoutMillis, final long eventFilteringAgeThresholdMillis) {
            Objects.requireNonNull(manager);
            TimeZoneProviderService.this.mHandler.post(new Runnable() { // from class: android.service.timezone.TimeZoneProviderService$TimeZoneProviderServiceWrapper$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    TimeZoneProviderService.TimeZoneProviderServiceWrapper.this.lambda$startUpdates$0(manager, initializationTimeoutMillis, eventFilteringAgeThresholdMillis);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$startUpdates$0(ITimeZoneProviderManager manager, long initializationTimeoutMillis, long eventFilteringAgeThresholdMillis) {
            TimeZoneProviderService.this.onStartUpdatesInternal(manager, initializationTimeoutMillis, eventFilteringAgeThresholdMillis);
        }

        @Override // android.service.timezone.ITimeZoneProvider
        public void stopUpdates() {
            Handler handler = TimeZoneProviderService.this.mHandler;
            final TimeZoneProviderService timeZoneProviderService = TimeZoneProviderService.this;
            handler.post(new Runnable() { // from class: android.service.timezone.TimeZoneProviderService$TimeZoneProviderServiceWrapper$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    TimeZoneProviderService.this.onStopUpdatesInternal();
                }
            });
        }
    }
}
