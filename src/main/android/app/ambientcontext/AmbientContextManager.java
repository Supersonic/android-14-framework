package android.app.ambientcontext;

import android.annotation.SystemApi;
import android.app.PendingIntent;
import android.app.ambientcontext.IAmbientContextObserver;
import android.content.Context;
import android.content.Intent;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.RemoteCallback;
import android.p008os.RemoteException;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
@SystemApi
/* loaded from: classes.dex */
public final class AmbientContextManager {
    public static final String EXTRA_AMBIENT_CONTEXT_EVENTS = "android.app.ambientcontext.extra.AMBIENT_CONTEXT_EVENTS";
    public static final int STATUS_ACCESS_DENIED = 5;
    public static final int STATUS_MICROPHONE_DISABLED = 4;
    public static final int STATUS_NOT_SUPPORTED = 2;
    public static final String STATUS_RESPONSE_BUNDLE_KEY = "android.app.ambientcontext.AmbientContextStatusBundleKey";
    public static final int STATUS_SERVICE_UNAVAILABLE = 3;
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_UNKNOWN = 0;
    private final Context mContext;
    private final IAmbientContextManager mService;

    /* loaded from: classes.dex */
    public @interface StatusCode {
    }

    public static List<AmbientContextEvent> getEventsFromIntent(Intent intent) {
        if (intent.hasExtra(EXTRA_AMBIENT_CONTEXT_EVENTS)) {
            return intent.getParcelableArrayListExtra(EXTRA_AMBIENT_CONTEXT_EVENTS, AmbientContextEvent.class);
        }
        return new ArrayList();
    }

    public AmbientContextManager(Context context, IAmbientContextManager service) {
        this.mContext = context;
        this.mService = service;
    }

    public void queryAmbientContextServiceStatus(Set<Integer> eventTypes, final Executor executor, final Consumer<Integer> consumer) {
        try {
            RemoteCallback callback = new RemoteCallback(new RemoteCallback.OnResultListener() { // from class: android.app.ambientcontext.AmbientContextManager$$ExternalSyntheticLambda1
                @Override // android.p008os.RemoteCallback.OnResultListener
                public final void onResult(Bundle bundle) {
                    AmbientContextManager.lambda$queryAmbientContextServiceStatus$1(executor, consumer, bundle);
                }
            });
            this.mService.queryServiceStatus(integerSetToIntArray(eventTypes), this.mContext.getOpPackageName(), callback);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$queryAmbientContextServiceStatus$1(Executor executor, final Consumer consumer, Bundle result) {
        final int status = result.getInt(STATUS_RESPONSE_BUNDLE_KEY);
        long identity = Binder.clearCallingIdentity();
        try {
            executor.execute(new Runnable() { // from class: android.app.ambientcontext.AmbientContextManager$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    consumer.accept(Integer.valueOf(status));
                }
            });
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    public void startConsentActivity(Set<Integer> eventTypes) {
        try {
            this.mService.startConsentActivity(integerSetToIntArray(eventTypes), this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    private static int[] integerSetToIntArray(Set<Integer> integerSet) {
        int[] intArray = new int[integerSet.size()];
        int i = 0;
        for (Integer type : integerSet) {
            intArray[i] = type.intValue();
            i++;
        }
        return intArray;
    }

    public void registerObserver(AmbientContextEventRequest request, PendingIntent resultPendingIntent, final Executor executor, final Consumer<Integer> statusConsumer) {
        Preconditions.checkArgument(!resultPendingIntent.isImmutable());
        try {
            RemoteCallback callback = new RemoteCallback(new RemoteCallback.OnResultListener() { // from class: android.app.ambientcontext.AmbientContextManager$$ExternalSyntheticLambda3
                @Override // android.p008os.RemoteCallback.OnResultListener
                public final void onResult(Bundle bundle) {
                    AmbientContextManager.lambda$registerObserver$3(executor, statusConsumer, bundle);
                }
            });
            this.mService.registerObserver(request, resultPendingIntent, callback);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$registerObserver$3(Executor executor, final Consumer statusConsumer, Bundle result) {
        final int statusCode = result.getInt(STATUS_RESPONSE_BUNDLE_KEY);
        long identity = Binder.clearCallingIdentity();
        try {
            executor.execute(new Runnable() { // from class: android.app.ambientcontext.AmbientContextManager$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    statusConsumer.accept(Integer.valueOf(statusCode));
                }
            });
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    /* renamed from: android.app.ambientcontext.AmbientContextManager$1  reason: invalid class name */
    /* loaded from: classes.dex */
    class ambientcontextIAmbientContextObserver$StubC04311 extends IAmbientContextObserver.Stub {
        final /* synthetic */ AmbientContextCallback val$ambientContextCallback;
        final /* synthetic */ Executor val$executor;

        ambientcontextIAmbientContextObserver$StubC04311(Executor executor, AmbientContextCallback ambientContextCallback) {
            this.val$executor = executor;
            this.val$ambientContextCallback = ambientContextCallback;
        }

        @Override // android.app.ambientcontext.IAmbientContextObserver
        public void onEvents(final List<AmbientContextEvent> events) throws RemoteException {
            long identity = Binder.clearCallingIdentity();
            try {
                Executor executor = this.val$executor;
                final AmbientContextCallback ambientContextCallback = this.val$ambientContextCallback;
                executor.execute(new Runnable() { // from class: android.app.ambientcontext.AmbientContextManager$1$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        AmbientContextCallback.this.onEvents(events);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }

        @Override // android.app.ambientcontext.IAmbientContextObserver
        public void onRegistrationComplete(final int statusCode) throws RemoteException {
            long identity = Binder.clearCallingIdentity();
            try {
                Executor executor = this.val$executor;
                final AmbientContextCallback ambientContextCallback = this.val$ambientContextCallback;
                executor.execute(new Runnable() { // from class: android.app.ambientcontext.AmbientContextManager$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        AmbientContextCallback.this.onRegistrationComplete(statusCode);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }
    }

    public void registerObserver(AmbientContextEventRequest request, Executor executor, AmbientContextCallback ambientContextCallback) {
        try {
            IAmbientContextObserver observer = new ambientcontextIAmbientContextObserver$StubC04311(executor, ambientContextCallback);
            this.mService.registerObserverWithCallback(request, this.mContext.getPackageName(), observer);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void unregisterObserver() {
        try {
            this.mService.unregisterObserver(this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
