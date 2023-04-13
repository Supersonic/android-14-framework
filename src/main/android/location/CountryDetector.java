package android.location;

import android.annotation.SystemApi;
import android.location.CountryDetector;
import android.location.ICountryListener;
import android.p008os.Handler;
import android.p008os.HandlerExecutor;
import android.p008os.Looper;
import android.p008os.RemoteException;
import android.util.Log;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
@SystemApi(client = SystemApi.Client.PRIVILEGED_APPS)
/* loaded from: classes2.dex */
public class CountryDetector {
    private static final String TAG = "CountryDetector";
    private final HashMap<Consumer<Country>, ListenerTransport> mListeners = new HashMap<>();
    private final ICountryDetector mService;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class ListenerTransport extends ICountryListener.Stub {
        private final Executor mExecutor;
        private final Consumer<Country> mListener;

        ListenerTransport(Consumer<Country> consumer, Executor executor) {
            this.mListener = consumer;
            this.mExecutor = executor;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCountryDetected$0(Country country) {
            this.mListener.accept(country);
        }

        @Override // android.location.ICountryListener
        public void onCountryDetected(final Country country) {
            this.mExecutor.execute(new Runnable() { // from class: android.location.CountryDetector$ListenerTransport$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    CountryDetector.ListenerTransport.this.lambda$onCountryDetected$0(country);
                }
            });
        }
    }

    public CountryDetector(ICountryDetector service) {
        this.mService = service;
    }

    public Country detectCountry() {
        try {
            return this.mService.detectCountry();
        } catch (RemoteException e) {
            Log.m109e(TAG, "detectCountry: RemoteException", e);
            return null;
        }
    }

    @Deprecated
    public void addCountryListener(CountryListener listener, Looper looper) {
        Handler handler = looper != null ? new Handler(looper) : new Handler();
        registerCountryDetectorCallback(new HandlerExecutor(handler), listener);
    }

    @Deprecated
    public void removeCountryListener(CountryListener listener) {
        unregisterCountryDetectorCallback(listener);
    }

    public void registerCountryDetectorCallback(Executor executor, Consumer<Country> consumer) {
        synchronized (this.mListeners) {
            unregisterCountryDetectorCallback(consumer);
            ListenerTransport transport = new ListenerTransport(consumer, executor);
            try {
                this.mService.addCountryListener(transport);
                this.mListeners.put(consumer, transport);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void unregisterCountryDetectorCallback(Consumer<Country> consumer) {
        synchronized (this.mListeners) {
            ListenerTransport transport = this.mListeners.remove(consumer);
            if (transport != null) {
                try {
                    this.mService.removeCountryListener(transport);
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            }
        }
    }
}
